// ============================================================================
//
// Copyright (C) 2006-2021 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.repository.ui.editors;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.command.AbstractCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.IDocumentProviderExtension;
import org.eclipse.ui.texteditor.IDocumentProviderExtension3;
import org.talend.commons.exception.LoginException;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.mdm.repository.core.command.CommandManager;
import org.talend.mdm.repository.core.command.ICommand;
import org.talend.mdm.repository.core.service.DeployService;
import org.talend.mdm.repository.core.service.IModelValidationService;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.model.mdmmetadata.MDMServerDef;
import org.talend.mdm.repository.ui.actions.IPostOpenAction;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;
import org.talend.repository.RepositoryWorkUnit;

import com.amalto.workbench.dialogs.AddBrowseItemsWizard;
import com.amalto.workbench.editors.DataModelMainPage;
import com.amalto.workbench.editors.xsdeditor.XSDEditor;
import com.amalto.workbench.editors.xsdeditor.XSDSelectionListener;
import com.amalto.workbench.exadapter.ExAdapterManager;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.utils.CompositeViewersSelectionProvider;
import com.amalto.workbench.utils.Util;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class XSDEditor2 extends XSDEditor implements ISvnGitHistory, IPostOpenAction {

    public static final String EDITOR_ID = "org.talend.mdm.repository.ui.editors.XSDEditor2"; //$NON-NLS-1$

    static Logger log = Logger.getLogger(XObjectEditor2.class);

    private DataModelMainPage2 dMainPage;

    private CTabFolder folder;

    private IXSDEditor2ExAdapter exAdapter;

    public XSDEditor2() {
        exAdapter = ExAdapterManager.getAdapter(this, IXSDEditor2ExAdapter.class);
    }

    @Override
    protected void createPages() {

        super.createPages();
        XSDEditorInput2 editorInput = (XSDEditorInput2) getEditorInput();
        TreeObject treeObject = editorInput.getTreeObject();
        IRepositoryViewEditorInput xobjectEditorinput = new XObjectEditorInput2(editorInput.getViewObject(), treeObject,
                treeObject.getDisplayName());
        xobjectEditorinput.setReadOnly(editorInput.isReadOnly());
        IFile xsdFile = null;
        try {
            xsdFile = getXSDFile(treeObject);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        dMainPage = new DataModelMainPage2(treeObject);
        dMainPage.addPropertyListener(this);
        try {
            MODEL_PAGE_INDEX = addPage(dMainPage, xobjectEditorinput);
        } catch (PartInitException e) {
            log.error(e.getMessage(), e);
        }
        // Add ER Editor
        if (isEE()) {
            exAdapter.addPage(xsdFile, editorInput.isReadOnly());
        }

        // add repository view object in selectionprovider
        IRepositoryViewObject repositoryViewObj = editorInput.getViewObject();// ContainerCacheService.get(editorInput.getInputItem().getProperty());
        CompositeViewersSelectionProvider selectionProvider = (CompositeViewersSelectionProvider) dMainPage
                .getSelectionProvider();
        selectionProvider.setRepositoryViewObj(repositoryViewObj);
        //

        // add XSDSelectionListener
        XSDSelectionListener xsdListener = new XSDSelectionListener(this, dMainPage);
        dMainPage.getTypesViewer().addSelectionChangedListener(xsdListener);
        dMainPage.getElementsViewer().addSelectionChangedListener(xsdListener);
        //

        setXObject(treeObject);
        //
        folder = (CTabFolder) dMainPage.getMainControl().getParent();
        folder.getItem(MODEL_PAGE_INDEX).setText(treeObject.getDisplayName() + " " + Util.getRevision(treeObject));//$NON-NLS-1$

        folder.getItem(SOURCE_PAGE_INDEX).setText(Messages.XSDEditor2_schemaSource);
        // default use
        activePage(xsdFile);

    }

    private boolean needValidate = false;

    @Override
    public String getPartName() {
        IEditorInput input = getEditorInput();
        if (input != null && input instanceof FileEditorInput) {
            return ((FileEditorInput) input).getName();
        } else {
            return super.getPartName();
        }

    }

    @Override
    public String getContributorId() {
        if (exAdapter != null) {
            String contributorId = exAdapter.getContributorId();
            if (contributorId != null) {
                return contributorId;
            }
        }
        return super.getContributorId();
    }

    @Override
    public void propertyChanged(Object source, int propId) {

        super.propertyChanged(source, propId);
        if (propId == PROP_DIRTY) {
            if (isDirty()) {
                needValidate = true;

            } else if (needValidate) {
                needValidate = false;
                IRepositoryViewObject viewObject = getCurrentViewObject();
                validateModel(viewObject);

            }
        }
    }

    private void activePage(IFile xsdFile) {
        validateXsdSourceEditor();
        if (hasXSDErrors()) {
            setActivePage(SOURCE_PAGE_INDEX);
            preActivePageIndex = SOURCE_PAGE_INDEX;
            return;
        }
        setActivePage(MODEL_PAGE_INDEX);
        preActivePageIndex = SOURCE_PAGE_INDEX;
    }

    @Override
    protected IFile getXSDFile(TreeObject xobject) throws Exception {
        return ((IFileEditorInput) getEditorInput()).getFile();
    }

    @Override
    public boolean isLocalInput() {
        return true;
    }

    @Override
    public boolean isReadOnly() {
        IRepositoryViewEditorInput editorInput = (IRepositoryViewEditorInput) this.getEditorInput();
        return editorInput.isReadOnly();
    }

    @Override
    public void setFocus() {
        super.setFocus();
        if (folder != null) {
            folder.setFocus();
        }
    }

    private IRepositoryViewObject getCurrentViewObject() {
        return ((IRepositoryViewEditorInput) getEditorInput()).getViewObject();
    }

    @Override
    public void doSave(final IProgressMonitor monitor) {
        RepositoryWorkUnit<Object> repositoryWorkUnit = new RepositoryWorkUnit<Object>("", this) { //$NON-NLS-1$

            @Override
            protected void run() throws LoginException, PersistenceException {
                innerSave(monitor);
            }
        };
        repositoryWorkUnit.setAvoidUnloadResources(true);
        CoreRuntimePlugin.getInstance().getProxyRepositoryFactory().executeRepositoryWorkUnit(repositoryWorkUnit);
    }

    private void doSave(Item item, IProgressMonitor monitor) {
        super.doSave(monitor);

        if (isEE()) {
            exAdapter.doSave(item, dMainPage, monitor);
        }
    }

    private void innerSave(final IProgressMonitor monitor) {
        IRepositoryViewObject viewObject = getCurrentViewObject();
        final Item item = viewObject.getProperty().getItem();
        int activePage = getActivePage();
        //
        final TransactionalEditingDomain editingDomain = (TransactionalEditingDomain) getAdapter(TransactionalEditingDomain.class);
        if (editingDomain != null && (activePage != MODEL_PAGE_INDEX && activePage != SOURCE_PAGE_INDEX)) {

            editingDomain.getCommandStack().execute(new AbstractCommand() {

                @Override
                public boolean canExecute() {
                    return true;
                }

                @Override
                public void redo() {
                    // do nothing
                }

                @Override
                public void execute() {
                    doSave(item, monitor);
                }
            });

        } else {
            doSave(item, monitor);
        }

        DeployService deployService = DeployService.getInstance();
        if (deployService.isAutoDeploy()) {
            deployService.autoDeploy(getSite().getShell(), viewObject);
        } else {
            MDMServerDef lastServerDef = RepositoryResourceUtil.getLastServerDef(item);
            if (lastServerDef != null) {
                CommandManager.getInstance().pushCommand(ICommand.CMD_MODIFY, viewObject);
            }
        }
    }

    private void validateModel(final IRepositoryViewObject viewObject) {
        Display.getDefault().asyncExec(new Runnable() {

            @Override
            public void run() {
                IModelValidationService service = GlobalServiceRegister.getDefault().getService(
                        IModelValidationService.class);
                List<IRepositoryViewObject> viewObjs = new ArrayList<IRepositoryViewObject>();
                viewObjs.add(viewObject);
                service.validate(viewObjs, IModelValidationService.VALIDATE_AFTER_SAVE, false);

            }
        });

    }

    @Override
    public DataModelMainPage getdMainPage() {
        return dMainPage;
    }

    @Override
    public Object getAdapter(Class type) {

        if (type == IGotoMarker.class) {

            return dMainPage;
        } else if (type == XSDEditorInput2.class) {
            return getEditorInput();
        }
        if (type == MultiPageEditorPart.class) {
            return this;
        }
        if (type == AddBrowseItemsWizard.class) {
            return dMainPage.getAdapter(AddBrowseItemsWizard.class);
        }
        if (isEE()) {
            Object adapter = exAdapter.getAdapter(type);
            if (adapter != null) {
                return adapter;
            }
        }

        return super.getAdapter(type);
    }

    public void updateTabPageLabel(final int index, final String label) {
        Display.getDefault().asyncExec(new Runnable() {

            @Override
            public void run() {
                setPageText(index, label);
            }
        });

    }

    @Override
    public void init(IEditorSite site, IEditorInput editorInput) throws PartInitException {

        super.init(site, editorInput);

        if (isEE()) {
            exAdapter.init(editorInput);
        }
    }

    public void fireDirtyPropChange() {
        getdMainPage().firePropertyChange();
    }

    private boolean isEE() {
        return exAdapter != null;
    }

    @Override
    public void dispose() {
        if (isEE()) {
            exAdapter.dispose();
        }
        super.dispose();
    }

    @Override
    public boolean isDirty() {
        boolean mapInfoDirty = false;
        if (isEE()) {
            mapInfoDirty = exAdapter.isDirty();
        }
        return super.isDirty() || mapInfoDirty;
    }

    @Override
    protected void doPageChanged(int newPageIndex, int lastPageIndex) {
        if (newPageIndex == SOURCE_PAGE_INDEX) {
            handleSourceActivation();
        }
        super.doPageChanged(newPageIndex, lastPageIndex);
        if (isEE()) {
            exAdapter.doPageChanged(newPageIndex, lastPageIndex);
        }
    }

    // Just avoid popup querying reload dialog
    void handleSourceActivation() {
        if (hasXSDErrors()) {
            return;
        }
        final IDocumentProvider provider = getTextEditor().getDocumentProvider();
        if (provider == null) {
            return;
        }

        final IEditorInput input = getEditorInput();
        if (provider instanceof IDocumentProviderExtension3) {

            if (provider instanceof IDocumentProviderExtension) {
                IDocumentProviderExtension extension = (IDocumentProviderExtension) provider;
                try {
                    extension.synchronize(input);
                } catch (CoreException e) {
                    log.error(e.getMessage(), e);
                }
            }

        }
    }

    @Override
    public void doPostOpen() {
        if (exAdapter != null) {
            exAdapter.doPostOpen();
        }
    }

}
