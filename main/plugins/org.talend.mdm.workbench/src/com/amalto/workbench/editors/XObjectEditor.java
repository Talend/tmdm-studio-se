// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.workbench.editors;

import java.util.ArrayList;
import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.CoolBarManager;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;

import com.amalto.workbench.availablemodel.AvailableModelUtil;
import com.amalto.workbench.availablemodel.IAvailableModel;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.models.IXObjectModelListener;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.providers.XObjectEditorInput;

public class XObjectEditor extends FormEditor implements IXObjectModelListener, IServerObjectEditorState {

    private static Log log = LogFactory.getLog(XObjectEditor.class);

    public ArrayList<IFormPage> formPages = new ArrayList<IFormPage>();

    private TreeObject initialXObject = null; // backup

    protected boolean saveInProgress = false;

    private com.amalto.workbench.editors.XObjectEditor.TdEditorToolBar toolBar;

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.forms.editor.FormEditor#addPages()
     */
    @Override
    protected void addPages() {

        updateTitle();

        TreeObject xobject = (TreeObject) ((XObjectEditorInput) this.getEditorInput()).getModel();

        // backup initial object
        this.initialXObject = new TreeObject(xobject.getDisplayName(), xobject.getServerRoot(), xobject.getType(),
                xobject.getWsKey(), xobject.getWsObject(), xobject.getAdditionalInfo());

        if (!xobject.isXObject()) {
            return;
        }

        // register model listener
        xobject.addListener(this);
        // available models
        java.util.List<IAvailableModel> availablemodels = AvailableModelUtil.getAvailableModels(isLocalInput());
        for (IAvailableModel model : availablemodels) {
            model.addPage(xobject, this);
        }

        addPageForXObject(xobject);
    }

    public void setName(String name) {
        setPartName(name);
    }

    /**
     * DOC hbhong Comment method "addPageForXObject".
     * 
     * @throws PartInitException
     */
    protected void addPageForXObject(TreeObject xobject) {
        try {
            switch (xobject.getType()) {
            case TreeObject.DATA_MODEL:
                // addPage(new DataModelMainPage(this));
                //
                // // addPage(new DataModelEditorPage(this));
                // WSDataModel wsObject = (WSDataModel) (xobject.getWsObject());
                // Document doc = new Document(Util.formatXsdSource(wsObject.getXsdSchema()));
                // xmlEditor = new XMLEditor(this, xobject);
                // addPage(xmlEditor, new XMLEditorInput(doc));
                // this.setPageText(1, "Schema");

                break;

            case TreeObject.INBOUND_PLUGIN:
                break;
            case TreeObject.OUTBOUND_PLUGIN:
                break;
            case TreeObject.VIEW:
                addPage(new ViewMainPage(this));
                break;
            case TreeObject.DATA_CLUSTER:
                addPage(new DataClusterMainPage(this));
                break;
            case TreeObject.STORED_PROCEDURE:
                addPage(new StoredProcedureMainPage(this));
                break;

            case TreeObject.MENU:
                addPage(new MenuMainPage(this));
                break;
            case TreeObject.SERVICE_CONFIGURATION:
                addPage(new ServiceConfigrationMainPage(this));
                break;
            /*
             * case TreeObject.RESOURCES: case TreeObject.DATA_MODEL_RESOURCE: case
             * TreeObject.DATA_MODEL_TYPES_RESOURCE: case TreeObject.CUSTOM_TYPES_RESOURCE: case
             * TreeObject.PICTURES_RESOURCE: addPage(new ResourceMainPage(this)); break;
             */
            case TreeObject.CUSTOM_TYPE:
                // addPage(new CustomTypeMainPage(this));
                break;
            case TreeObject.ROUTING_RULE:
                try {
                    addPage(new RoutingRuleMainPage(this));
                } catch (PartInitException e) {
                    log.error(e.getMessage(), e);
                }
                break;
            case TreeObject.TRANSFORMER:
                try {
                    addPage(new TransformerMainPage(this));
                } catch (PartInitException e) {
                    log.error(e.getMessage(), e);
                }
                break;
            case TreeObject.JOB:
                try {
                    addPage(new JobMainPage(this));
                } catch (PartInitException e) {
                    log.error(e.getMessage(), e);
                }
                break;
            default:
                // MessageDialog.openError(this.getSite().getShell(), "Error",
                // "Unknown "+IConstants.TALEND+" Object Type: "+xobject.getType());
                return;
            }// switch

        } catch (PartInitException e) {
            MessageDialog.openError(this.getSite().getShell(), Messages._Error,
                    Messages.bind(Messages.XObjectEditor_ErrorMsg, e.getLocalizedMessage()));
        }
    }

    @Override
    public int addPage(IFormPage page) throws PartInitException {
        formPages.add(page);
        return super.addPage(page);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void doSave(IProgressMonitor monitor) {

        this.saveInProgress = true;
        try {
            int numPages = formPages.size();
            monitor.beginTask(Messages.bind(Messages.XObjectEditor_Saving, this.getEditorInput().getName()), numPages + 1);
            for (int i = 0; i < numPages; i++) {
                if ((formPages.get(i)) instanceof AFormPage) {
                    if (!((AFormPage) (formPages.get(i))).beforeDoSave()) {
                        return;
                    }
                }
                (formPages.get(i)).doSave(monitor);
                monitor.worked(1);
                if (monitor.isCanceled()) {
                    this.saveInProgress = false;
                    return;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            monitor.done();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
     */
    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

    @Override
    public void doSaveAs() {
    }

    private void updateTitle() {
        IEditorInput input = this.getEditorInput();
        setPartName(input.getName());
        setContentDescription("");//$NON-NLS-1$
    }

    @Override
    public void dispose() {
        // save space
        TreeObject xobject = (TreeObject) ((XObjectEditorInput) this.getEditorInput()).getModel();
        if (xobject != null) {
            xobject.setWsObject(null);
            xobject.removeListener(this);
        }
        super.dispose();
        formPages.clear();
    }

    /**
     * Model Listener
     */
    public void handleEvent(int type, TreeObject parent, TreeObject child) {
    }

    public TreeObject getInitialXObject() {
        return initialXObject;
    }

    @Override
    protected void pageChange(int newPageIndex) {
        AFormPage page = (AFormPage) formPages.get(0);
        boolean isdirty = page.isDirty();
        super.pageChange(newPageIndex);

        linkDirty(page, isdirty);

    }

    private void linkDirty(Object page, boolean dirty) {
        if (dirty) {
            if (page instanceof DataModelMainPage) {

                if (findPage(DataModelMainPage.class.getName()) != null) {
                    DataModelMainPage mainPage = (DataModelMainPage) findPage(DataModelMainPage.class.getName());
                    mainPage.markDirtyWithoutCommit();
                }
            }
        }
    }

    @Override
    public Image getTitleImage() {
        TreeObject object = (TreeObject) ((XObjectEditorInput) this.getEditorInput()).getModel();

        if (object.getType() == TreeObject._SERVER_) {
            return ImageCache.getCreatedImage("icons/talend-picto-small.gif");//$NON-NLS-1$
        } else if (object.getType() == TreeObject.DATA_CLUSTER) {
            return ImageCache.getCreatedImage(EImage.DATA_CLUSTER_WHITE.getPath());
        } else if (object.getType() == TreeObject.DATA_MODEL) {
            return ImageCache.getCreatedImage(EImage.DATA_MODEL_WHITE.getPath());
        } else if (object.getType() == TreeObject.MENU) {
            return ImageCache.getCreatedImage(EImage.MENU_WHITE.getPath());
        } else if (object.getType() == TreeObject.TRANSFORMER) {
            return ImageCache.getCreatedImage(EImage.TRANSFORMER_WHITE.getPath());
        } else if (object.getType() == TreeObject.ROLE) {
            return ImageCache.getCreatedImage(EImage.ROLE_WHITE.getPath());
        } else if (object.getType() == TreeObject.STORED_PROCEDURE) {
            return ImageCache.getCreatedImage(EImage.STORED_PROCEDURE_WHITE.getPath());
        } else if (object.getType() == TreeObject.ROUTING_RULE) {
            return ImageCache.getCreatedImage(EImage.ROUTING_RULE_WHITE.getPath());
        } else if (object.getType() == TreeObject.VIEW) {
            return ImageCache.getCreatedImage(EImage.VIEW_WHITE.getPath());
        } else if (object.getType() == TreeObject.DOCUMENT) {
            return ImageCache.getCreatedImage(EImage.DOCUMENTS.getPath());
        } else if (object.getType() == TreeObject.SUBSCRIPTION_ENGINE) {
            return ImageCache.getCreatedImage(EImage.SUBSCRIPTION_ENGINE_WHITE.getPath());
        } else if (object.getType() == TreeObject.SERVICE_CONFIGURATION) {
            return ImageCache.getCreatedImage(EImage.SERVICE_CONFIGURATION_WHITE.getPath());
        } else if (object.getType() == TreeObject.RESOURCES || object.getType() == TreeObject.DATA_MODEL_RESOURCE
                || object.getType() == TreeObject.DATA_MODEL_TYPES_RESOURCE
                || object.getType() == TreeObject.CUSTOM_TYPES_RESOURCE || object.getType() == TreeObject.PICTURES_RESOURCE) {
            return ImageCache.getCreatedImage(EImage.RESOURCES_WHITE.getPath());
        } else if (object.getType() == TreeObject.JOB) {
            return ImageCache.getCreatedImage(EImage.JOB.getPath());
        }

        return ImageCache.getCreatedImage("icons/error.gif");//$NON-NLS-1$
    }

    @Override
    public int getCurrentPage() {

        return super.getCurrentPage();
    }

    @Override
    protected Composite createPageContainer(Composite parent) {
        GridLayout gridLayout = new GridLayout();
        gridLayout.verticalSpacing = 0;
        gridLayout.numColumns = 1;
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        parent.setLayout(gridLayout);

        Composite barComp = new Composite(parent, SWT.NONE);
        GridData gdData = new GridData(GridData.FILL_HORIZONTAL);
        barComp.setLayoutData(gdData);
        barComp.setLayout(new FormLayout());

        createToolbar(barComp);

        Composite mainParent = new Composite(parent, SWT.NONE);
        GridData gdData1 = new GridData(GridData.FILL_BOTH);
        gdData1.grabExcessVerticalSpace = true;
        mainParent.setLayoutData(gdData1);
        return super.createPageContainer(mainParent);
    }

    protected void createToolbar(final Composite parent) {

        toolBar = new TdEditorToolBar(parent);

        FormData data = new FormData();
        data.top = new FormAttachment(0, 0);
        data.left = new FormAttachment(0, 0);
        data.right = new FormAttachment(100, 0);
        toolBar.getToolbarControl().setLayoutData(data);

        toolBar.addResizeListener(new ControlListener() {

            public void controlMoved(ControlEvent e) {
            }

            public void controlResized(ControlEvent e) {
                parent.getParent().layout(true);
                parent.layout(true);
            }
        });
    }

    /**
     * DOC bzhou Comment method "getToolBar".
     * 
     * @return
     */
    public TdEditorToolBar getToolBar() {
        return toolBar;
    }

    public class TdEditorToolBar {

        private CoolBar coolBar = null;

        private CoolBarManager coolBarMgr;

        private ToolBarManager defaultToolBarMgr;

        private LinkedList<Action> actions = new LinkedList<Action>();

        public TdEditorToolBar(Composite parent) {

            // create coolbar

            coolBar = new CoolBar(parent, SWT.FLAT);
            coolBarMgr = new CoolBarManager(coolBar);

            FormData fdata = new FormData();
            fdata.top = new FormAttachment(0, 0);
            fdata.left = new FormAttachment(0, 0);
            fdata.right = new FormAttachment(100, 0);
            coolBar.setLayoutData(fdata);

            // initialize default actions
            defaultToolBarMgr = new ToolBarManager(SWT.FLAT);

            actions.add(new RefreshSectionAction());

            for (Action action : actions) {
                defaultToolBarMgr.add(action);
            }

            // add all toolbars to parent coolbar
            coolBarMgr.add(new ToolBarContributionItem(defaultToolBarMgr));
            coolBarMgr.update(true);
        }

        public void addResizeListener(ControlListener listener) {
            coolBar.addControlListener(listener);
        }

        public CoolBar getToolbarControl() {
            return coolBar;
        }

        public void addActions(Action... actions) {
            assert actions != null;

            if (coolBarMgr != null) {
                for (Action action : actions) {
                    defaultToolBarMgr.add(action);
                }

                // coolBarMgr.add(new ToolBarContributionItem(defaultToolBarMgr));
                defaultToolBarMgr.update(true);
                coolBarMgr.update(true);
            }
        }

        /**
         * 
         * DOC mzhao TdEditorToolBar class global comment. Detailled comment
         */
        private class RefreshSectionAction extends Action {

            public RefreshSectionAction() {
                super("Refresh"); //$NON-NLS-1$
                setToolTipText(Messages.XObjectEditor_Refresh);
                this.setImageDescriptor(ImageCache.getImage(EImage.REFRESH.getPath()));
            }

            @Override
            public void run() {
                IFormPage page = formPages.get(getCurrentPage());
                if (page != null && page instanceof AFormPage) {
                    ((AFormPage) page).refreshPage();
                }
            }
        }
    }

    public IFormPage getPage(int index) {
        return formPages.get(index);
    }

    //

    public boolean isReadOnly() {
        return false;
    }

    public boolean isLocalInput() {
        return false;
    }

}
