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
package org.talend.mdm.repository.ui.wizards.imports;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.navigator.CommonViewer;
import org.talend.commons.exception.LoginException;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.utils.VersionUtils;
import org.talend.core.model.properties.ItemState;
import org.talend.core.model.properties.PropertiesFactory;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.mdm.repository.core.IRepositoryNodeConfiguration;
import org.talend.mdm.repository.core.IServerObjectRepositoryType;
import org.talend.mdm.repository.core.command.CommandManager;
import org.talend.mdm.repository.core.impl.transformerV2.ITransformerV2NodeConsDef;
import org.talend.mdm.repository.core.impl.view.IViewNodeConstDef;
import org.talend.mdm.repository.core.service.ImportService;
import org.talend.mdm.repository.core.service.RepositoryQueryService;
import org.talend.mdm.repository.extension.RepositoryNodeConfigurationManager;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.model.mdmmetadata.MDMServerDef;
import org.talend.mdm.repository.model.mdmproperties.MDMServerObjectItem;
import org.talend.mdm.repository.model.mdmserverobject.MDMServerObject;
import org.talend.mdm.repository.model.mdmserverobject.MdmserverobjectFactory;
import org.talend.mdm.repository.model.mdmserverobject.WSResourceE;
import org.talend.mdm.repository.ui.dialogs.lock.LockedObjectDialog;
import org.talend.mdm.repository.ui.navigator.MDMRepositoryView;
import org.talend.mdm.repository.ui.wizards.imports.viewer.TreeObjectCheckTreeViewer;
import org.talend.mdm.repository.utils.Bean2EObjUtil;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;
import org.talend.mdm.repository.utils.RepositoryTransformUtil;
import org.talend.mdm.workbench.serverexplorer.ui.dialogs.SelectServerDefDialog;
import org.talend.repository.RepositoryWorkUnit;
import org.talend.repository.model.IProxyRepositoryFactory;

import com.amalto.workbench.exadapter.ExAdapterManager;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.models.TreeParent;
import com.amalto.workbench.providers.XtentisServerObjectsRetriever;
import com.amalto.workbench.utils.HttpClientUtil;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.widgets.LabelCombo;
import com.amalto.workbench.widgets.WidgetFactory;

/**
 * DOC achen class global comment. Detailled comment
 */
public class ImportServerObjectWizard extends Wizard {

    private static final String UTF8 = "UTF-8"; //$NON-NLS-1$

    static Logger log = Logger.getLogger(ImportServerObjectWizard.class);

    private TreeObjectCheckTreeViewer treeViewer;

    private TreeObject serverRoot;

    private MDMServerDef serverDef;

    private LabelCombo comboVersion;

    private Text txtServer;

    WidgetFactory toolkit = WidgetFactory.getWidgetFactory();

    CommonViewer commonViewer;

    boolean isOverrideAll = true;

    private Button btnOverwrite;

    private Object[] selectedObjects;

    public ImportServerObjectWizard(CommonViewer commonViewer) {

        setNeedsProgressMonitor(true);
        this.commonViewer = commonViewer;
        exAdapter = ExAdapterManager.getAdapter(this, IImportServerObjectWizardExAdapter.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    @Override
    public boolean performFinish() {
        if (!showLockedObjDialog(selectedObjects)) {
            return false;
        }
        try {
            if (selectedObjects != null) {
                doImport();
            }
        } catch (InvocationTargetException e) {
            log.error(e);
            return false;
        } catch (InterruptedException e) {
            log.error(e);
            return false;
        }

        return true;
    }

    private void updateSelectedObjects() {
        selectedObjects = treeViewer.getCheckNodes();
    }

    private int isOveride(String name, String obTypeName) {

        final MessageDialog dialog = new MessageDialog(getShell(), Messages.Confirm_Overwrite, null, Messages.bind(
                Messages.Confirm_Overwrite_Info, obTypeName, name), MessageDialog.QUESTION, new String[] {
                IDialogConstants.YES_LABEL, IDialogConstants.YES_TO_ALL_LABEL, IDialogConstants.NO_LABEL,
                IDialogConstants.CANCEL_LABEL }, 0);
        dialog.open();
        int result = dialog.getReturnCode();
        if (result == 0) {
            return IDialogConstants.YES_ID;
        }
        if (result == 1) {
            return IDialogConstants.YES_TO_ALL_ID;
        }
        if (result == 2) {
            return IDialogConstants.NO_ID;
        }
        return IDialogConstants.CANCEL_ID;

    }

    Pattern picFileNamePattern = Pattern.compile("(.*?)-(.*)\\.(.*?)"); //$NON-NLS-1$

    Pattern picFileVersionPattern = Pattern.compile("(.*)_(\\d+\\.\\d+)"); //$NON-NLS-1$

    private IImportServerObjectWizardExAdapter exAdapter;

    /**
     * DOC hbhong Comment method "getFileInfo".
     * 
     * @param input
     * @return // dirName result[0] // fileQName result[1] // fileExtension result[2] // fileName result[3] // version
     * result[4]
     */
    private String[] getPicResourceFileInfo(String input) {
        Matcher m = picFileNamePattern.matcher(input);
        if (m.matches()) {
            String[] result = new String[5];
            // dirName
            result[0] = m.group(1);
            // fileQName
            result[1] = m.group(2);
            // fileExtension
            result[2] = m.group(3);
            // fileName
            result[3] = result[1];
            // version
            result[4] = VersionUtils.DEFAULT_VERSION;
            Matcher versionM = picFileVersionPattern.matcher(result[1]);
            if (versionM.matches()) {
                // fileName
                result[3] = versionM.group(1);
                // version
                result[4] = versionM.group(2);
            }
            return result;
        } else {
            return null;
        }

    }

    private MDMServerObject handleSpecialTreeObject(TreeObject treeObj) throws IOException {
        int type = treeObj.getType();
        if (type == TreeObject.PICTURES_RESOURCE) {
            return handlePictureResourceObject(treeObj);
        }
        if (type == TreeObject.WORKFLOW_PROCESS) {
            if (exAdapter != null) {
                return exAdapter.handleWorkflowObject(treeObj);
            }
        }
        return null;
    }

    /**
     * DOC hbhong Comment method "handlePictureResourceObject".
     * 
     * @param treeObj
     * @throws IOException
     */
    private WSResourceE handlePictureResourceObject(TreeObject treeObj) throws IOException {
        if (treeObj != null) {
            if (treeObj instanceof TreeParent) {
                return null;
            }
            String[] fileInfo = getPicResourceFileInfo(treeObj.getName());

            if (fileInfo != null) {
                String dirName = fileInfo[0];
                String fileQName = fileInfo[1];
                String fileExtension = fileInfo[2];
                String fileName = fileInfo[3];
                // encode the dirName and fileName
                String encodedDirName = URLEncoder.encode(dirName, UTF8);
                fileQName = URLEncoder.encode(fileQName, UTF8);
                WSResourceE resource = MdmserverobjectFactory.eINSTANCE.createWSResourceE();
                resource.setName(fileName);
                resource.setFileExtension(fileExtension);
                StringBuffer strBuf = new StringBuffer();
                strBuf.append(serverDef.getProtocol())
                        .append(serverDef.getHost())
                        .append(":").append(serverDef.getPort()) //$NON-NLS-1$ 
                        .append(Util.getContextPath(serverDef.getPath()))
                        .append("/services/imageserver/upload/").append(encodedDirName).append("/").append(fileQName).append(".").append(fileExtension); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                String url = strBuf.toString();
                byte[] bytes = HttpClientUtil.downloadFile(url, serverDef.getUser(), serverDef.getPasswd());
                resource.setFileContent(bytes);
                // add imageCatalog
                resource.setImageCatalog(dirName);

                treeObj.setName(fileName);
                return resource;
            }
        }
        return null;
    }

    private boolean showLockedObjDialog(Object[] objs) {
        if (objs == null) {
            return true;
        }
        List<IRepositoryViewObject> viewObjs = new LinkedList<IRepositoryViewObject>();
        boolean forceContinueResetOperation = false;
        Map<IRepositoryViewObject, TreeObject> objMap = new HashMap<IRepositoryViewObject, TreeObject>();
        for (Object obj : objs) {
            TreeObject treeObj = (TreeObject) obj;
            if (treeObj != null && treeObj instanceof TreeParent) {
                continue;
            }
            String treeObjName = treeObj.getName();

            ERepositoryObjectType type = RepositoryQueryService.getRepositoryObjectType(treeObj.getType());
            if (type != null && treeObjName != null) {
                String uniqueName = getUniqueName(treeObj, treeObjName);
                IRepositoryViewObject viewObject = RepositoryResourceUtil.findViewObjectByName(type, uniqueName);
                if (viewObject != null) {
                    viewObjs.add(viewObject);
                    objMap.put(viewObject, treeObj);
                } else {
                    // not exist in local
                    forceContinueResetOperation = true;
                }
            }
        }
        LockedObjectDialog lockDialog = new LockedObjectDialog(getShell(), Messages.ImportServerObjectWizard_lockedObjectMessage,
                Messages.ImportServerObjectWizard_cancelImportingObjectMessage, viewObjs, forceContinueResetOperation);
        if (lockDialog.needShowDialog()) {
            int open = lockDialog.open();

            if (open == IDialogConstants.OK_ID) {
                if (lockDialog.canContinueRestOperation()) {
                    selectedObjects = lockDialog.getUnlockedTreeObject(selectedObjects, objMap);
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    public List<String> doImport(Object[] objs, IProgressMonitor monitor) {
        monitor.beginTask(Messages.Import_Objects, objs.length);
        List<String> importedIds = new LinkedList<String>();
        ImportService.setImporting(true);

        List<Integer> types = new ArrayList<Integer>();
        types.add(TreeObject.CUSTOM_FORM);
        types.add(TreeObject.DATA_CLUSTER);
        types.add(TreeObject.DATA_MODEL);
        types.add(TreeObject.TRANSFORMER);
        types.add(TreeObject.ROUTING_RULE);
        types.add(TreeObject.MENU);
        types.add(TreeObject.ROLE);
        types.add(TreeObject.STORED_PROCEDURE);
        types.add(TreeObject.VIEW);
        types.add(TreeObject.WORKFLOW_PROCESS);
        IProxyRepositoryFactory factory = CoreRuntimePlugin.getInstance().getProxyRepositoryFactory();
        for (Object obj : objs) {
            try {
                TreeObject treeObj = (TreeObject) obj;
                monitor.subTask(treeObj.getDisplayName());
                String treeObjName = treeObj.getName();
                MDMServerObject eobj = handleSpecialTreeObject(treeObj);
                if (treeObj.getType() == TreeObject.WORKFLOW_PROCESS) {
                    continue;
                }
                if (eobj == null) {
                    if (!types.contains(treeObj.getType()) || treeObj.getWsObject() == null
                            || ("JCAAdapers".equals(treeObj.getName()) && treeObj.getType() == TreeObject.DATA_CLUSTER)) { //$NON-NLS-1$
                        continue;
                    }
                    eobj = (MDMServerObject) Bean2EObjUtil.getInstance().convertFromBean2EObj(treeObj.getWsObject(), null);
                }
                eobj.setLastServerName(serverDef.getName());

                ERepositoryObjectType type = RepositoryQueryService.getRepositoryObjectType(treeObj.getType());
                String uniqueName = getUniqueName(treeObj, treeObjName);
                MDMServerObjectItem item = RepositoryQueryService.findServerObjectItemByNameWithDeleted(type, uniqueName, true);

                if (item != null) {
                    if (!isOverrideAll) {
                        int result = isOveride(treeObj.getName(), TreeObject.getTypeName(treeObj.getType()));
                        if (result == IDialogConstants.CANCEL_ID) {
                            ImportService.setImporting(false);
                            return importedIds;
                        }
                        if (result == IDialogConstants.YES_TO_ALL_ID) {
                            isOverrideAll = true;
                        }
                        if (result == IDialogConstants.NO_ID) {
                            break;
                        }
                    }

                    if (!RepositoryResourceUtil.isLockedItem(item)) {
                        try {
                            factory.lock(item);
                        } catch (PersistenceException e1) {
                            log.error(e1.getMessage(), e1);
                        } catch (LoginException e1) {
                            log.error(e1.getMessage(), e1);
                        }

                        item.setMDMServerObject(eobj);
                        item.getState().setDeleted(false);
                        // save
                        RepositoryResourceUtil.saveItem(item, false);

                        try {
                            factory.unlock(item);
                        } catch (PersistenceException e) {
                            log.error(e.getMessage(), e);
                        } catch (LoginException e) {
                            log.error(e.getMessage(), e);
                        }
                        importedIds.add(item.getProperty().getId());
                    }

                    CommandManager.getInstance().removeCommandStack(item.getProperty().getId());
                } else {
                    IRepositoryNodeConfiguration config = RepositoryNodeConfigurationManager.getConfiguration(type);
                    item = (MDMServerObjectItem) config.getResourceProvider().createNewItem(type);
                    item.setMDMServerObject(eobj);
                    ItemState itemState = PropertiesFactory.eINSTANCE.createItemState();

                    itemState.setPath(caculatePath(treeObj));
                    handlePath(itemState, type);
                    item.setState(itemState);
                    String version = getVersion(treeObj);

                    if (RepositoryResourceUtil.createItem(item, uniqueName, version, null, false, false)) {
                        importedIds.add(item.getProperty().getId());
                    }

                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
            monitor.worked(1);
        }
        ImportService.setImporting(false);
        monitor.done();
        return importedIds;
    }

    /**
     * DOC jsxie Comment method "caculatePath". if it is process or trigger ,cut the path prefix.
     * 
     * @param treeObj
     * @return the tree object path.
     */
    private String caculatePath(TreeObject treeObj) {
        if (treeObj.getType() == TreeObject.TRANSFORMER) {
            return getProcessTypeObjectPath(treeObj);

        }

        if (treeObj.getType() == TreeObject.ROUTING_RULE) {
            if (treeObj.getPath().equals("Trigger")) { //$NON-NLS-1$
                return "";//$NON-NLS-1$
            } else {
                return treeObj.getPath().substring(8);
            }

        }

        if (treeObj.getType() == TreeObject.VIEW) {
            return getViewTypeObjectPath(treeObj);
        }

        return treeObj.getPath();

    }

    private String getProcessTypeObjectPath(TreeObject treeObj) {

        String path = treeObj.getPath();
        String processName = treeObj.getName();
        int processType = RepositoryTransformUtil.getInstance().getProcessType(processName);
        switch (processType) {
        case ITransformerV2NodeConsDef.TYPE_BEFORESAVE:
            if (path.equals(ITransformerV2NodeConsDef.PATH_PROCESS)) {
                return IPath.SEPARATOR + ITransformerV2NodeConsDef.PATH_BEFORESAVE;
            } else if (!path.equals(ITransformerV2NodeConsDef.PATH_PROCESS)
                    && !path.startsWith(IPath.SEPARATOR + ITransformerV2NodeConsDef.PATH_SMARTVIEW)) {
                return IPath.SEPARATOR + ITransformerV2NodeConsDef.PATH_BEFORESAVE + path.substring(8);
            }
            break;
        case ITransformerV2NodeConsDef.TYPE_BEFOREDEL:
            if (path.equals(ITransformerV2NodeConsDef.PATH_PROCESS)) {
                return IPath.SEPARATOR + ITransformerV2NodeConsDef.PATH_BEFOREDEL;
            } else if (!path.equals(ITransformerV2NodeConsDef.PATH_PROCESS)
                    && !path.startsWith(IPath.SEPARATOR + ITransformerV2NodeConsDef.PATH_BEFOREDEL)) {
                return IPath.SEPARATOR + ITransformerV2NodeConsDef.PATH_BEFOREDEL + path.substring(8);
            }
            break;
        case ITransformerV2NodeConsDef.TYPE_ENTITYACTION:
            if (path.equals(ITransformerV2NodeConsDef.PATH_PROCESS)) {
                return IPath.SEPARATOR + ITransformerV2NodeConsDef.PATH_ENTITYACTION;
            } else if (!path.startsWith(IPath.SEPARATOR + ITransformerV2NodeConsDef.PATH_ENTITYACTION)) {
                return IPath.SEPARATOR + ITransformerV2NodeConsDef.PATH_ENTITYACTION + path.substring(8);
            }
            break;
        case ITransformerV2NodeConsDef.TYPE_WELCOMEACTION:
            if (path.equals(ITransformerV2NodeConsDef.PATH_PROCESS)) {
                return IPath.SEPARATOR + ITransformerV2NodeConsDef.PATH_WELCOMEACTION;
            } else if (!path.startsWith(IPath.SEPARATOR + ITransformerV2NodeConsDef.PATH_WELCOMEACTION)) {
                return IPath.SEPARATOR + ITransformerV2NodeConsDef.PATH_WELCOMEACTION + path.substring(8);
            }
            break;
        case ITransformerV2NodeConsDef.TYPE_SMARTVIEW:
            if (path.equals(ITransformerV2NodeConsDef.PATH_PROCESS)) {
                return IPath.SEPARATOR + ITransformerV2NodeConsDef.PATH_SMARTVIEW;
            } else if (!path.startsWith(IPath.SEPARATOR + ITransformerV2NodeConsDef.PATH_SMARTVIEW)) {
                return IPath.SEPARATOR + ITransformerV2NodeConsDef.PATH_SMARTVIEW + path.substring(8);
            }
            break;
        case ITransformerV2NodeConsDef.TYPE_OTHER:

        default:
            if (path.equals(ITransformerV2NodeConsDef.PATH_PROCESS)) {
                return IPath.SEPARATOR + ITransformerV2NodeConsDef.PATH_OTHER;
            } else if (!path.startsWith(IPath.SEPARATOR + ITransformerV2NodeConsDef.PATH_OTHER)) {
                return IPath.SEPARATOR + ITransformerV2NodeConsDef.PATH_OTHER + path.substring(8);
            }
            break;
        }

        return path.substring(8);
    }

    private String getViewTypeObjectPath(TreeObject treeObj) {
        if (RepositoryTransformUtil.getInstance().getViewType(treeObj.getName()) == IViewNodeConstDef.TYPE_WEBFILTER) {
            if (!treeObj.getPath().startsWith(IPath.SEPARATOR + IViewNodeConstDef.PATH_WEBFILTER)) {
                String path = treeObj.getPath();
                if (path.length() > 8) {
                    path = IPath.SEPARATOR + path.substring(8);
                } else if (!path.isEmpty()) {
                    path = IPath.SEPARATOR + path;
                }
                return IPath.SEPARATOR + IViewNodeConstDef.PATH_WEBFILTER + path;
            }
        } else {
            if (!treeObj.getPath().startsWith(IPath.SEPARATOR + IViewNodeConstDef.PATH_SEARCHFILTER)) {
                String path = treeObj.getPath();
                if (path.length() > 8) {
                    path = path.substring(8);
                } else if (!path.isEmpty()) {
                    path = IPath.SEPARATOR + path;
                }
                return IPath.SEPARATOR + IViewNodeConstDef.PATH_SEARCHFILTER + path;
            }
        }

        return treeObj.getPath();
    }

    /**
     * DOC hbhong Comment method "getVersion".
     * 
     * @param treeObj
     * @return
     */
    private String getVersion(TreeObject treeObj) {
        int type = treeObj.getType();
        if (type == TreeObject.PICTURES_RESOURCE) {
            String[] fileInfo = getPicResourceFileInfo(treeObj.getName());
            if (fileInfo != null) {
                return fileInfo[4];
            }
        }
        if (type == TreeObject.WORKFLOW_PROCESS && exAdapter != null) {
            return exAdapter.getWorkflowgTreeObjectVersion(treeObj);
        }
        return VersionUtils.DEFAULT_VERSION;
    }

    /**
     * DOC hbhong Comment method "getUniqueName".
     * 
     * @param treeObj
     * @return
     */
    private String getUniqueName(TreeObject treeObj, String name) {
        int type = treeObj.getType();
        if (type == TreeObject.PICTURES_RESOURCE) {
            if (name == null) {
                name = treeObj.getName();
            }
            String[] fileInfo = getPicResourceFileInfo(name);
            if (fileInfo != null) {
                //return fileInfo[3] + "." + fileInfo[2]; //$NON-NLS-1$
                return fileInfo[3];
            }
        }
        if (type == TreeObject.WORKFLOW_PROCESS && exAdapter != null) {
            return exAdapter.getWorkflowgTreeObjectName(treeObj);
        }
        return treeObj.getName();
    }

    /**
     * DOC hbhong Comment method "handlePath".
     * 
     * @param itemState
     * @param type
     */
    private void handlePath(ItemState itemState, ERepositoryObjectType type) {
        if (type == IServerObjectRepositoryType.TYPE_RESOURCE) {
            itemState.setPath(""); //$NON-NLS-1$
        }

    }

    class ImportProcess implements IRunnableWithProgress {

        @Override
        public void run(final IProgressMonitor wizardMonitor) throws InvocationTargetException, InterruptedException {

            RepositoryWorkUnit<Object> repositoryWorkUnit = new RepositoryWorkUnit<Object>("", this) { //$NON-NLS-1$

                @Override
                protected void run() throws LoginException, PersistenceException {
                    List<String> importedIds = doImport(selectedObjects, wizardMonitor);
                    Display.getDefault().asyncExec(new Runnable() {

                        @Override
                        public void run() {
                            MDMRepositoryView.show().getCommonViewer().refresh();
                        }
                    });

                    if (exAdapter != null) {
                        exAdapter.updateRelations(importedIds);
                    }
                }
            };
            repositoryWorkUnit.setAvoidUnloadResources(true);
            CoreRuntimePlugin.getInstance().getProxyRepositoryFactory().executeRepositoryWorkUnit(repositoryWorkUnit);
        }
    }

    class RetriveProcess implements IRunnableWithProgress {

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
         */
        @Override
        public void run(IProgressMonitor m) throws InvocationTargetException, InterruptedException {

            String url = serverDef.getProtocol() + serverDef.getHost() + ":" + serverDef.getPort() //$NON-NLS-1$ 
                    + serverDef.getPath();
            final XtentisServerObjectsRetriever retriever = new XtentisServerObjectsRetriever(serverDef.getName(), url,
                    serverDef.getUser(), serverDef.getPasswd());

            retriever.setRetriveWSObject(true);
            retriever.run(m);
            serverRoot = retriever.getServerRoot();
            //
            if (exAdapter != null) {
                exAdapter.retrieverCustomForms(serverDef, (TreeParent) serverRoot, m);
            }
            // sort
            sortTreeObjs((TreeParent) serverRoot);
            //
            Display.getDefault().syncExec(new Runnable() {

                @Override
                public void run() {
                    try {

                        treeViewer.setRoot((TreeParent) serverRoot);

                        treeViewer.initInput(serverDef);

                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
            });
        }

        private void sortTreeObjs(TreeParent serverRoot) {
            Collections.sort(serverRoot.getChildrenList(), new Comparator() {

                @Override
                public int compare(Object o1, Object o2) {
                    String name1 = ((TreeObject) o1).getDisplayName();
                    String name2 = ((TreeObject) o2).getDisplayName();
                    return name1.compareTo(name2);
                }

            });
        }
    }

    @Override
    public void addPages() {
        addPage(new SelectItemsPage());
    }

    private void doImport() throws InvocationTargetException, InterruptedException {
        getContainer().run(true, false, new ImportProcess());
    }

    public void retriveServerRoot() {
        if (serverDef != null) {
            try {
                getContainer().run(true, false, new RetriveProcess());
            } catch (InvocationTargetException e) {
                if (!Util.handleConnectionException(getShell(), e.getTargetException(), null)) {
                    MessageDialog.openWarning(getShell(), Messages.Common_Warning,
                            Messages.AbstractDataClusterAction_ConnectFailed);
                }
                log.error(e);
            } catch (InterruptedException e) {
                MessageDialog.openWarning(getShell(), Messages.Common_Warning, Messages.AbstractDataClusterAction_ConnectFailed);
                log.error(e);
            }
        }
    }

    class SelectItemsPage extends WizardPage {

        protected SelectItemsPage() {
            super("SelectServerPage"); //$NON-NLS-1$
            setTitle(Messages.ImportServerObject);

            // Page isn't complete until an e-mail address has been added
            // setPageComplete(false);

        }

        public void checkCompleted() {
            // && (selectedObjects != null && selectedObjects.length > 0)
            if (txtServer.getText().length() > 0 && (selectedObjects != null && selectedObjects.length > 0)) {
                setPageComplete(true);
            } else {
                setPageComplete(false);
            }
        }

        @Override
        public void createControl(Composite parent) {
            Composite composite = new Composite(parent, SWT.BORDER);
            composite.setLayout(new GridLayout(4, false));
            composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
            setControl(composite);

            Group serverGroup = new Group(composite, SWT.NORMAL);
            serverGroup.setText(Messages.Select_Server);
            serverGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
            serverGroup.setLayout(new GridLayout(2, false));
            // serverGroup.setBackground(composite.getDisplay().getSystemColor(SWT.COLOR_WHITE));

            txtServer = new Text(serverGroup, SWT.BORDER);
            txtServer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
            Button btnSel = new Button(serverGroup, SWT.PUSH);
            btnSel.setText("..."); //$NON-NLS-1$
            btnSel.setToolTipText(Messages.Select_Server);
            txtServer.setEnabled(false);

            btnSel.addSelectionListener(new SelectionAdapter() {

                /*
                 * (non-Javadoc)
                 * 
                 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                 */
                @Override
                public void widgetSelected(SelectionEvent e) {
                    SelectServerDefDialog dlg = new SelectServerDefDialog(getShell());
                    if (dlg.open() == IDialogConstants.OK_ID) {
                        serverDef = dlg.getSelectedServerDef();
                        if (serverDef == null) {
                            return;
                        }
                        txtServer.setText(serverDef.getName());

                        retriveServerRoot();
                    }

                    treeViewer.refresh();
                    updateSelectedObjects();
                    checkCompleted();
                }
            });
            // create viewer
            treeViewer = new TreeObjectCheckTreeViewer((TreeParent) serverRoot);
            treeViewer.addButtonSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    updateSelectedObjects();
                    checkCompleted();
                }
            });
            Composite itemcom = treeViewer.createItemList(composite);
            treeViewer.getViewer().setInput(null);
            itemcom.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 5));
            treeViewer.setItemText(Messages.Select_Items_To_Imports);

            CheckboxTreeViewer checkboxViewer = (CheckboxTreeViewer) treeViewer.getViewer();
            checkboxViewer.addCheckStateListener(new ICheckStateListener() {

                @Override
                public void checkStateChanged(CheckStateChangedEvent checkstatechangedevent) {
                    updateSelectedObjects();
                    checkCompleted();
                }
            });

            treeViewer.getViewer().addFilter(new ViewerFilter() {

                @Override
                public boolean select(Viewer viewer, Object parentElement, Object element) {
                    if (element instanceof TreeObject) {
                        int type = ((TreeObject) element).getType();
                        if (type == 26 || type == 24 || type == 25) {
                            return false;
                        }
                    }
                    return true;
                }
            });
            final Button showTimeColumnBun = new Button(composite, SWT.CHECK);
            showTimeColumnBun.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));
            showTimeColumnBun.setText(Messages.ConsistencyConflict_showTimeStampColumn);
            showTimeColumnBun.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    treeViewer.showTimeStampColumns(showTimeColumnBun.getSelection());
                }
            });
            btnOverwrite = new Button(composite, SWT.CHECK);
            btnOverwrite.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    isOverrideAll = btnOverwrite.getSelection();

                    treeViewer.setOverWrite(isOverrideAll);
                    treeViewer.getViewer().refresh();
                }

            });
            btnOverwrite.setText(Messages.Overwrite_Exists_Items);
            btnOverwrite.setSelection(true);
            GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).hint(920, 600).applyTo(composite);
        }
    }
}
