// ============================================================================
//
// Copyright (C) 2006-2019 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.repository.ui.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.intro.IIntroSite;
import org.eclipse.ui.intro.config.IIntroAction;
import org.eclipse.ui.progress.UIJob;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.runtime.model.repository.ERepositoryStatus;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.mdm.repository.core.AbstractRepositoryAction;
import org.talend.mdm.repository.core.IRepositoryNodeActionProvider;
import org.talend.mdm.repository.core.IRepositoryNodeConfiguration;
import org.talend.mdm.repository.core.IRepositoryViewGlobalActionHandler;
import org.talend.mdm.repository.core.IServerObjectRepositoryType;
import org.talend.mdm.repository.core.service.IMDMSVNProviderService;
import org.talend.mdm.repository.extension.RepositoryNodeConfigurationManager;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.model.mdmmetadata.MDMServerDef;
import org.talend.mdm.repository.model.mdmproperties.ContainerItem;
import org.talend.mdm.repository.model.mdmproperties.MDMServerObjectItem;
import org.talend.mdm.repository.model.mdmserverobject.MDMServerObject;
import org.talend.mdm.repository.models.WSRootRepositoryObject;
import org.talend.mdm.repository.ui.editors.IRepositoryViewEditorInput;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;
import org.talend.mdm.workbench.serverexplorer.ui.dialogs.SelectServerDefDialog;
import org.talend.repository.model.IProxyRepositoryFactory;

import com.amalto.workbench.exadapter.ExAdapterManager;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.models.TreeParent;
import com.amalto.workbench.providers.XObjectBrowserInput;
import com.amalto.workbench.providers.XObjectEditorInput;
import com.amalto.workbench.service.MissingJarService;
import com.amalto.workbench.utils.EXtentisObjects;
import com.amalto.workbench.utils.UserInfo;
import com.amalto.workbench.webservices.WSDataClusterPK;

/**
 * DOC hbhong class global comment. Detailled comment <br/>
 *
 */
public class OpenObjectAction extends AbstractRepositoryAction implements IIntroAction {

    private static final Logger LOG = Logger.getLogger(OpenObjectAction.class);

    private List<Object> selObjects;

    private IOpenObjectActionExAdapter exAdapter;

    public void setSelObjects(List<Object> selObjects) {
        this.selObjects = selObjects;
    }

    public OpenObjectAction() {
        super(Messages.OpenObjectAction_open);
        setId(IRepositoryViewGlobalActionHandler.OPEN);
        setActionDefinitionId(IRepositoryViewGlobalActionHandler.OPEN);
        exAdapter = ExAdapterManager.getAdapter(this, IOpenObjectActionExAdapter.class);
    }

    private IWorkbenchPage page = null;

    IProxyRepositoryFactory factory = CoreRuntimePlugin.getInstance().getProxyRepositoryFactory();

    private IMarker marker;

    private IEditorPart activeEditor;

    public IEditorPart getOpenedEditor() {
        return this.activeEditor;
    }

    @Override
    protected boolean isLocked() {

        List<Object> selectedObject = getSelectedObject();
        if (!selectedObject.isEmpty()) {
            Object object = selectedObject.get(0);

            if (object instanceof IRepositoryViewObject) {
                IRepositoryViewObject viewObj = (IRepositoryViewObject) object;
                ERepositoryStatus status = factory.getStatus(viewObj);
                if (status == ERepositoryStatus.LOCK_BY_USER) {
                    return !status.isEditable();
                } else if (status == ERepositoryStatus.LOCK_BY_OTHER) {
                    return false;
                }

            }
        }

        return false;

    }

    private void updateEditorInputVersionInfo(IRepositoryViewEditorInput editorInput, IRepositoryViewObject viewObject) {
        String version = viewObject.getVersion();
        try {
            if (!factory.isLocalConnectionProvider()) {
                if (GlobalServiceRegister.getDefault().isServiceRegistered(IMDMSVNProviderService.class)) {
                    IMDMSVNProviderService service = GlobalServiceRegister.getDefault()
                            .getService(IMDMSVNProviderService.class);
                    if (service != null && service.isProjectInSvnMode()) {
                        String revisionNumStr = service.getCurrentSVNRevision(viewObject);
                        if (revisionNumStr != null) {
                            revisionNumStr = ".r" + revisionNumStr; //$NON-NLS-1$
                            version += revisionNumStr;
                        }
                    }
                }
            }
        } catch (PersistenceException e) {
            LOG.error(e.getMessage(), e);
        }

        editorInput.setVersion(version);
    }

    public class OpenJob extends UIJob {

        boolean isOpened = false;

        public boolean isOpened() {
            return this.isOpened;
        }

        public OpenJob() {
            super("Open Object"); //$NON-NLS-1$
            setUser(true);
        }

        @Override
        public IStatus runInUIThread(IProgressMonitor monitor) {

            isOpened = doOpen();

            monitor.done();
            return Status.OK_STATUS;
        }

    }

    @Override
    protected void doRun() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                UIJob openJob = new OpenJob();
                if (exAdapter != null) {
                    if (toOpenDatamodel()) {
                        exAdapter.beforeRun(openJob);
                    }
                }
                openJob.schedule();
            }

            private boolean toOpenDatamodel() {
                boolean toOpenDatamodel = false;
                List<Object> selectedObject = getSelectedObject();
                if (!selectedObject.isEmpty()) {
                    Object object = selectedObject.get(0);

                    if (object instanceof IRepositoryViewObject) {
                        IRepositoryViewObject viewObj = (IRepositoryViewObject) object;
                        toOpenDatamodel = viewObj.getRepositoryObjectType() == IServerObjectRepositoryType.TYPE_DATAMODEL;
                    }
                }
                return toOpenDatamodel;
            }

        }).start();

    }

    public boolean doOpen() {

        List<Object> sels = getSelectedObject();
        if (selObjects != null) {
            sels = selObjects;
        }
        if (sels.isEmpty()) {
            return false;
        }
        Object obj = sels.get(0);
        if (obj instanceof IRepositoryViewObject) {
            if (obj instanceof WSRootRepositoryObject) {
                return false;
            }
            IRepositoryViewObject viewObject = (IRepositoryViewObject) obj;

            Item item = viewObject.getProperty().getItem();
            if (item instanceof ContainerItem) {
                if (viewObject.getRepositoryObjectType().equals(IServerObjectRepositoryType.TYPE_SERVICECONFIGURATION)) {// service
                    boolean checkMissingJar = MissingJarService.getInstance().checkMissingJar(true);
                    if (!checkMissingJar) {
                        return false;
                    }
                    // configuration
                    MDMServerDef serverDef = openServerDialog(null);
                    openServiceConfig(serverDef);
                } else {
                    getCommonViewer().expandToLevel(obj, 1);
                }

            } else {
                IEditorReference editorRef = RepositoryResourceUtil.isOpenedInEditor(viewObject);
                if (editorRef != null) {
                    if (page == null) {
                        page = getCommonViewer().getCommonNavigator().getSite().getWorkbenchWindow().getActivePage();
                    }
                    if (page != null) {
                        page.bringToTop(editorRef.getPart(false));
                    }
                    if (marker != null) {
                        IDE.gotoMarker(editorRef.getEditor(true), marker);
                    }
                    return false;
                } else {
                    openItem(viewObject);
                    return true;
                }
            }
        }
        return true;
    }

    private void openServiceConfig(MDMServerDef serverDef) {
        TreeParent serverRoot = getServerRoot(serverDef);

        if (!isNull(serverRoot)) {
            TreeObject xobject = new TreeObject(EXtentisObjects.ServiceConfiguration.getDisplayName(), serverRoot,
                    TreeObject.SERVICE_CONFIGURATION, null, null);
            try {
                if (page == null) {
                    this.page = getCommonViewer().getCommonNavigator().getSite().getWorkbenchWindow().getActivePage();
                }
                page.openEditor(new XObjectEditorInput(xobject, xobject.getDisplayName()),
                        "com.amalto.workbench.editors.XObjectEditor"); //$NON-NLS-1$
            } catch (PartInitException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    private void openItem(IRepositoryViewObject viewObject) {
        Item item = viewObject.getProperty().getItem();
        item = RepositoryResourceUtil.assertItem(item);
        IRepositoryNodeConfiguration configuration = RepositoryNodeConfigurationManager.getConfiguration(item);
        if (configuration != null) {
            try {
                IRepositoryNodeActionProvider actionProvider = configuration.getActionProvider();
                if (actionProvider != null) {
                    IRepositoryViewEditorInput editorInput = actionProvider.getOpenEditorInput(viewObject);
                    if (editorInput != null) {
                        if (page == null) {
                            this.page = getCommonViewer().getCommonNavigator().getSite().getWorkbenchWindow().getActivePage();
                        }
                        // do extra action
                        MDMServerObject serverObject = ((MDMServerObjectItem) item).getMDMServerObject();
                        if (!checkMissingJar(serverObject)) {
                            return;
                        }
                        boolean selected = doSelectServer(item, editorInput);
                        if (!selected) {
                            return;
                        }
                        try { // svn lock
                            ERepositoryStatus status = factory.getStatus(item);
                            boolean isEditable = factory.isEditableAndLockIfPossible(item);
                            if (isEditable) {
                                getCommonViewer().refresh(viewObject);
                            }
                            //
                            editorInput.setReadOnly(status == ERepositoryStatus.LOCK_BY_OTHER
                                    || status == ERepositoryStatus.READ_ONLY || !isEditable);

                            if (!editorInput.isReadOnly()) {
                                editorInput.setReadOnly(item.getState().isDeleted());
                            }
                            updateEditorInputVersionInfo(editorInput, viewObject);
                            activeEditor = this.page.openEditor(editorInput, editorInput.getEditorId());
                            if (marker != null) {
                                IDE.gotoMarker(activeEditor, marker);
                            }
                        } catch (PartInitException e) {
                            LOG.error(e.getMessage(), e);
                        }
                    } else {
                        AbstractRepositoryAction openAction = actionProvider.getOpenAction(viewObject);
                        if (openAction != null) {
                            openAction.selectionChanged(getStructuredSelection());
                            openAction.run();
                        }
                    }
                }
            } catch (UnsupportedOperationException ex) {
                // for svn/git mode, open a deleted object would throw this exception, just log it
                LOG.info("The Object is deleted from remote server");
            }
        }
    }

    @Override
    public IStructuredSelection getStructuredSelection() {
        if (selObjects != null) {
            return new StructuredSelection(selObjects);
        }
        return super.getStructuredSelection();
    }

    @Override
    protected boolean needValidateLockedObject() {
        return true;
    }

    @Override
    public String getGroupName() {
        // this action not be shown in context menu,so Nothing need to do in here
        return null;
    }

    public TreeParent getServerRoot(MDMServerDef serverDef) {
        if (isNull(serverDef)) {
            return null;
        }

        String serverName = serverDef.getName();
        String username = serverDef.getUser();
        String password = serverDef.getPasswd();
        String endpointaddress = serverDef.getProtocol() + serverDef.getHost() + ":" + serverDef.getPort() //$NON-NLS-1$
                + serverDef.getPath();
        TreeParent serverRoot = new TreeParent(serverName, null, TreeObject._SERVER_, endpointaddress, username
                + ":" + (password == null ? "" : password));//$NON-NLS-1$//$NON-NLS-2$
        UserInfo user = new UserInfo();
        user.setUsername(username);
        user.setPassword(password);
        user.setServerUrl(endpointaddress);

        serverRoot.setUser(user);
        return serverRoot;
    }

    private boolean checkMissingJar(MDMServerObject serverObject) {
        int type = serverObject.getType();
        switch (type) {
        case TreeObject.DATA_CLUSTER:
        case TreeObject.TRANSFORMER:
        case TreeObject.ROUTING_RULE:
            boolean checkMissingJar = MissingJarService.getInstance().checkMissingJar(true);
            if (!checkMissingJar) {
                return false;
            }
        }
        return true;
    }

    public boolean doSelectServer(Item item, IRepositoryViewEditorInput editorInput) {
        MDMServerObject serverObject = ((MDMServerObjectItem) item).getMDMServerObject();
        if (serverObject.getType() == TreeObject.DATA_CLUSTER) {// Data Cluster
            MDMServerDef lastServerDef = RepositoryResourceUtil.getLastServerDef(item);
            MDMServerDef serverDef = openServerDialog(lastServerDef);
            if (serverDef != null) {
                XObjectBrowserInput input = (XObjectBrowserInput) editorInput;
                TreeObject xobject = (TreeObject) input.getModel();
                if (xobject != null && xobject.getWsKey() != null) {
                    TreeParent serverRoot = getServerRoot(serverDef);
                    xobject.setWsKey(new WSDataClusterPK(xobject.getWsKey().toString()));
                    xobject.setServerRoot(serverRoot);
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    /**
     * return a decrypted server def
     *
     * @param serverObject
     * @return a decrypted server def
     */
    public MDMServerDef openServerDialog(MDMServerDef serverObject) {
        SelectServerDefDialog dlg = new SelectServerDefDialog(getShell());
        dlg.create();
        dlg.setSelectServer(serverObject);
        if (dlg.open() == IDialogConstants.OK_ID) {
            MDMServerDef serverDef = dlg.getSelectedServerDef();
            return serverDef;
        }
        return null;
    }

    private boolean isNull(Object obj) {
        return obj == null;
    }

    public void openMarker(IRepositoryViewObject viewObj, IMarker marker) {
        this.marker = marker;
        List<Object> selObjs = new ArrayList<Object>();
        selObjs.add(viewObj);
        setSelObjects(selObjs);
        run();
    }

    @Override
    public void run(IIntroSite site, Properties params) {
        PlatformUI.getWorkbench().getIntroManager().closeIntro(PlatformUI.getWorkbench().getIntroManager().getIntro());
        Object id = params.get("nodeId"); //$NON-NLS-1$
        if (id != null) {
            try {
                IRepositoryViewObject selectedObj = factory.getLastVersion((String) id);
                List selObjs = new ArrayList(1);
                selObjs.add(selectedObj);
                setSelObjects(selObjs);
                run();
            } catch (PersistenceException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

}
