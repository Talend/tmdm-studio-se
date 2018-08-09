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
package org.talend.mdm.repository.ui.editors;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonViewer;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.mdm.repository.core.command.CommandManager;
import org.talend.mdm.repository.core.command.ICommand;
import org.talend.mdm.repository.core.service.DeployService;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.model.mdmmetadata.MDMServerDef;
import org.talend.mdm.repository.model.mdmproperties.MDMServerObjectItem;
import org.talend.mdm.repository.model.mdmserverobject.MDMServerObject;
import org.talend.mdm.repository.ui.navigator.MDMRepositoryView;
import org.talend.mdm.repository.utils.Bean2EObjUtil;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;
import org.talend.repository.model.IProxyRepositoryFactory;

import com.amalto.workbench.editors.AFormPage;
import com.amalto.workbench.editors.DataClusterMainPage;
import com.amalto.workbench.editors.JobMainPage;
import com.amalto.workbench.editors.MenuMainPage;
import com.amalto.workbench.editors.XObjectEditor;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.views.MDMPerspective;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class XObjectEditor2 extends XObjectEditor implements ISvnGitHistory {

    static Logger log = Logger.getLogger(XObjectEditor2.class);

    public static final String EDITOR_ID = "org.talend.mdm.repository.ui.editors.XObjectEditor2"; //$NON-NLS-1$

    @Override
    public void doSave(IProgressMonitor monitor) {

        if (monitor == null) {
            monitor = new NullProgressMonitor();
        }

        this.saveInProgress = true;
        try {
            int numPages = formPages.size();
            monitor.beginTask(Messages.bind(Messages.XObjectEditor2_saving, this.getEditorInput().getName()), numPages + 1);
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

            boolean saved = saveResourceToRepository();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            monitor.done();
        }
    }

    private boolean saveResourceToRepository() {
        XObjectEditorInput2 editorInput = (XObjectEditorInput2) this.getEditorInput();
        TreeObject xobject = (TreeObject) editorInput.getModel();

        IRepositoryViewObject viewObject = editorInput.getViewObject();
        IRepositoryViewObject refreshViewObject = RepositoryResourceUtil.assertViewObject(viewObject);
        if (viewObject != refreshViewObject) {
            editorInput.updateViewObject(refreshViewObject);
        }

        MDMServerObjectItem serverObjectItem = (MDMServerObjectItem) editorInput.getInputItem();
        MDMServerObject serverObject = serverObjectItem.getMDMServerObject();
        EObject eObj = Bean2EObjUtil.getInstance().convertFromBean2EObj(xobject.getWsObject(), serverObject);
        if (eObj != null) {
            IProxyRepositoryFactory factory = CoreRuntimePlugin.getInstance().getProxyRepositoryFactory();
            try {
                factory.save(serverObjectItem);
                // TODO should call the following,but the page in editor has many call to remote webService ,it will
                // search ServerRoot which cause a NPE
                // xobject.fireEvent(IXObjectModelListener.SAVE, xobject.getParent(), xobject);
                editorDirtyStateChanged();

                refreshDirtyCue();

                DeployService deployService = DeployService.getInstance();
                if (deployService.isAutoDeploy()) {
                    autoDeployProcess(deployService);
                } else {
                    MDMServerDef lastServerDef = RepositoryResourceUtil.getLastServerDef(serverObjectItem);
                    if (lastServerDef != null) {
                        CommandManager.getInstance().pushCommand(ICommand.CMD_MODIFY, editorInput.getViewObject());
                    }
                }
                return true;
            } catch (PersistenceException e) {
                log.error(e.getMessage(), e);
            }
        }
        return false;
    }

    public void autoDeployProcess(DeployService deployService) {
        if (deployService == null) {
            return;
        }

        IEditorInput input = getEditorInput();
        XObjectEditorInput2 theInput = null;
        if (input instanceof XObjectEditorInput2) {
            theInput = (XObjectEditorInput2) input;
        }
        IRepositoryViewObject viewObj = theInput.getViewObject();
        deployService.autoDeploy(getSite().getShell(), viewObj);
    }

    private void refreshDirtyCue() {
        IEditorInput input = getEditorInput();
        XObjectEditorInput2 theInput = null;
        if (input instanceof XObjectEditorInput2) {
            theInput = (XObjectEditorInput2) input;
        }
        IRepositoryViewObject viewObj = theInput.getViewObject();
        CommonViewer viewer = MDMRepositoryView.show().getCommonViewer();
        viewer.refresh(viewObj);

    }

    @Override
    protected void addPageForXObject(TreeObject xobject) {
        try {
            switch (xobject.getType()) {
            case TreeObject.DATA_MODEL:
                break;

            case TreeObject.INBOUND_PLUGIN:
                break;
            case TreeObject.OUTBOUND_PLUGIN:
                break;
            case TreeObject.VIEW:
                addPage(new ViewMainPage2(this));
                break;
            case TreeObject.DATA_CLUSTER:
                addPage(new DataClusterMainPage(this));
                break;
            case TreeObject.STORED_PROCEDURE:
                addPage(new StoredProcedureMainPage2(this));
                break;

            case TreeObject.MENU:
                addPage(new MenuMainPage(this));
                break;
            case TreeObject.SERVICE_CONFIGURATION:
                addPage(new MDMServiceConfigrationMainPage(this));
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
                addPage(new RoutingRuleMainPage2(this));
                break;
            case TreeObject.TRANSFORMER:
                addPage(new TransformerMainPage2(this));
                break;
            case TreeObject.JOB:
                addPage(new JobMainPage(this));
                break;

            default:

                return;
            }

        } catch (PartInitException e) {
            log.error(e.getMessage(), e);
            MessageDialog.openError(this.getSite().getShell(), Messages.Common_Error,
                    Messages.bind(Messages.XObjectEditor2_unableOpenEditor, e.getLocalizedMessage()));
        }
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

    /*
     * (non-Javadoc)
     * 
     * @see com.amalto.workbench.editors.XObjectEditor#addPages()
     */
    @Override
    protected void addPages() {
        super.addPages();
        try {
            refreshPropertyView();
        } catch (PartInitException e) {

        }
    }

    private void refreshPropertyView() throws PartInitException {

        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

        IViewPart propView = page.findView(MDMPerspective.VIEWID_PROPERTYVIEW);

        if (propView != null) {
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().hideView(propView);
        }

        page.showView(MDMPerspective.VIEWID_PROPERTYVIEW);
    }

}
