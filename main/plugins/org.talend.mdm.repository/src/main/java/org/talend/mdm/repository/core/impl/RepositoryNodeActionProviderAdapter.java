// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.repository.core.impl;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.navigator.CommonViewer;
import org.talend.commons.CommonsPlugin;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.model.properties.FolderItem;
import org.talend.core.model.properties.FolderType;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.mdm.repository.core.AbstractRepositoryAction;
import org.talend.mdm.repository.core.IRepositoryNodeActionProvider;
import org.talend.mdm.repository.core.IRepositoryViewGlobalActionHandler;
import org.talend.mdm.repository.core.IServerObjectRepositoryType;
import org.talend.mdm.repository.core.service.IModelValidationService;
import org.talend.mdm.repository.extension.ActionProviderManager;
import org.talend.mdm.repository.extension.RepositoryNodeConfigurationManager;
import org.talend.mdm.repository.model.mdmproperties.ContainerItem;
import org.talend.mdm.repository.model.mdmproperties.MDMItem;
import org.talend.mdm.repository.model.mdmproperties.MDMServerObjectItem;
import org.talend.mdm.repository.model.mdmproperties.WorkspaceRootItem;
import org.talend.mdm.repository.model.mdmserverobject.MDMServerObject;
import org.talend.mdm.repository.ui.actions.CreateFolderAction;
import org.talend.mdm.repository.ui.actions.DebugDigestValueAction;
import org.talend.mdm.repository.ui.actions.DeployAllAction;
import org.talend.mdm.repository.ui.actions.DeployAnotherVersionAction;
import org.talend.mdm.repository.ui.actions.DeployToAction;
import org.talend.mdm.repository.ui.actions.DeployToLastServerAction;
import org.talend.mdm.repository.ui.actions.DuplicateAction;
import org.talend.mdm.repository.ui.actions.ExportObjectAction;
import org.talend.mdm.repository.ui.actions.ImportObjectAction;
import org.talend.mdm.repository.ui.actions.ImportServerObjectAction;
import org.talend.mdm.repository.ui.actions.MDMEditPropertyAction;
import org.talend.mdm.repository.ui.actions.MDMOpenExistVersionProcessAction;
import org.talend.mdm.repository.ui.actions.RemoveFromRepositoryAction;
import org.talend.mdm.repository.ui.actions.RenameObjectAction;
import org.talend.mdm.repository.ui.actions.UndeployAction;
import org.talend.mdm.repository.ui.actions.ValidateAction;
import org.talend.mdm.repository.ui.actions.process.MDMEventManagerAction;
import org.talend.mdm.repository.ui.editors.IRepositoryViewEditorInput;
import org.talend.mdm.repository.ui.editors.XObjectBrowserInput2;
import org.talend.mdm.repository.ui.editors.XObjectEditorInput2;
import org.talend.repository.ProjectManager;

import com.amalto.workbench.exadapter.ExAdapterManager;
import com.amalto.workbench.models.TreeObject;

/**
 * DOC hbhong class global comment. Detailled comment <br/>
 * 
 */
public class RepositoryNodeActionProviderAdapter implements IRepositoryNodeActionProvider {

    private static boolean enableDebug = CommonsPlugin.isDebugMode();

    public static AbstractRepositoryAction createFolderAction;

    protected static AbstractRepositoryAction removeFromRepositoryAction;

    static AbstractRepositoryAction duplicateAction;

    protected static AbstractRepositoryAction exportObjectAction;

    protected static AbstractRepositoryAction importObjectAction;

    protected static AbstractRepositoryAction deployToAction;

    protected static AbstractRepositoryAction deployAnotherToAction;

    protected static AbstractRepositoryAction deployToLastServerAction;

    protected static AbstractRepositoryAction undeployAction;

    protected static AbstractRepositoryAction deployAllAction;

    protected static AbstractRepositoryAction renameAction;

    protected AbstractRepositoryAction refreshAction;

    protected AbstractRepositoryAction copyAction;

    protected AbstractRepositoryAction importServerObjectAction;

    protected AbstractRepositoryAction mdmEditPropertyAction;

    protected AbstractRepositoryAction pasteAction;

    protected AbstractRepositoryAction emAction;

    protected AbstractRepositoryAction openVersionAction;

    protected IRepositoryViewGlobalActionHandler globalActionHandler;

    private IStructuredSelection selection;

    protected AbstractRepositoryAction validateAction;

    protected AbstractRepositoryAction debugDigestValueAction;

    IRepositoryNodeActionProviderAdapterExAdapter exAdapter = null;

    public RepositoryNodeActionProviderAdapter() {
        exAdapter = ExAdapterManager.getAdapter(this, IRepositoryNodeActionProviderAdapterExAdapter.class);

    }

    @Override
    public void initCommonViewer(CommonViewer commonViewer) {
        importObjectAction = initRepositoryAction(ImportObjectAction.createImportAction(), commonViewer);

        exportObjectAction = initRepositoryAction(new ExportObjectAction(), commonViewer);
        createFolderAction = initRepositoryAction(new CreateFolderAction(), commonViewer);
        removeFromRepositoryAction = initRepositoryAction(new RemoveFromRepositoryAction(), commonViewer);
        renameAction = initRepositoryAction(new RenameObjectAction(), commonViewer);
        duplicateAction = initRepositoryAction(new DuplicateAction(), commonViewer);
        deployToAction = initRepositoryAction(new DeployToAction(), commonViewer);
        deployAnotherToAction = initRepositoryAction(new DeployAnotherVersionAction(), commonViewer);
        deployToLastServerAction = initRepositoryAction(new DeployToLastServerAction(), commonViewer);
        undeployAction = initRepositoryAction(new UndeployAction(), commonViewer);
        deployAllAction = initRepositoryAction(new DeployAllAction(false), commonViewer);
        emAction = initRepositoryAction(new MDMEventManagerAction(), commonViewer);
        importServerObjectAction = initRepositoryAction(new ImportServerObjectAction(), commonViewer);
        mdmEditPropertyAction = initRepositoryAction(new MDMEditPropertyAction(), commonViewer);
        openVersionAction = initRepositoryAction(new MDMOpenExistVersionProcessAction(), commonViewer);
        // for debug digestValue
        debugDigestValueAction = initRepositoryAction(new DebugDigestValueAction(), commonViewer);
        //
        if (hasValidateService()) {
            validateAction = initRepositoryAction(new ValidateAction(), commonViewer);
        }
        //
        refreshAction = globalActionHandler.getGlobalAction(IRepositoryViewGlobalActionHandler.REFRESH);
        copyAction = globalActionHandler.getGlobalAction(IRepositoryViewGlobalActionHandler.COPY);
        if (exAdapter != null) {
            exAdapter.initCommonViewer(commonViewer);
        }

        pasteAction = globalActionHandler.getGlobalAction(IRepositoryViewGlobalActionHandler.PASTE);
        // action provider
        for (IRepositoryNodeActionProvider provider : getExtendActionProviders()) {
            provider.initCommonViewer(commonViewer);
        }
        // recycle
        RepositoryNodeConfigurationManager.getRecycleBinNodeConfiguration().getActionProvider().initCommonViewer(commonViewer);
    }

    public AbstractRepositoryAction initRepositoryAction(AbstractRepositoryAction action, CommonViewer commonViewer) {
        action.initCommonViewer(commonViewer);
        return action;
    }

    private boolean hasValidateService() {
        IModelValidationService service = (IModelValidationService) GlobalServiceRegister.getDefault().getService(
                IModelValidationService.class);
        return service != null;

    }

    @Override
    public List<AbstractRepositoryAction> getActions(IRepositoryViewObject viewObj) {
        List<AbstractRepositoryAction> actions = new LinkedList<AbstractRepositoryAction>();
        //
        Item item = viewObj.getProperty().getItem();
        if (item instanceof MDMItem) {
            if (item instanceof ContainerItem) {
                FolderType type = ((FolderItem) item).getType();
                switch (type.getValue()) {
                case FolderType.SYSTEM_FOLDER:
                    addAction(actions, createFolderAction, viewObj);
                    addAction(actions, pasteAction, viewObj);

                    actions.add(importServerObjectAction);
                    actions.add(importObjectAction);

                    break;
                case FolderType.STABLE_SYSTEM_FOLDER:

                    break;

                case FolderType.FOLDER:
                    addAction(actions, createFolderAction, viewObj);
                    if (!((ContainerItem) item).getRepObjType().equals(ERepositoryObjectType.PROCESS)) {
                        actions.add(removeFromRepositoryAction);
                    }
                    addAction(actions, pasteAction, viewObj);
                    break;
                }

            } else if (item instanceof MDMServerObjectItem) {
                addAction(actions, removeFromRepositoryAction, viewObj);
                addAction(actions, mdmEditPropertyAction, viewObj);
                addAction(actions, openVersionAction, viewObj);
                addAction(actions, copyAction, viewObj);
                addAction(actions, pasteAction, viewObj);
                addAction(actions, duplicateAction, viewObj);

                if (exAdapter != null) {
                    exAdapter.addActions(actions, viewObj);
                }

            } else if (item instanceof WorkspaceRootItem) { // fix bug TMDM-3168
                actions.add(importObjectAction);
            }
        }
        if (enableDebug) {
            // for debug digestValue
            addAction(actions, debugDigestValueAction, viewObj);
        }
        //
        actions.add(refreshAction);
        actions.add(exportObjectAction);
        // action provider
        for (IRepositoryNodeActionProvider provider : getExtendActionProviders()) {
            List<AbstractRepositoryAction> providerActions = provider.getActions(viewObj);
            if (providerActions != null) {
                actions.addAll(providerActions);
            }
        }

        if (viewObj.getRepositoryObjectType() != null
                && IServerObjectRepositoryType.TYPE_EVENTMANAGER.equals(viewObj.getRepositoryObjectType())) {
            actions.add(emAction);
        }

        //
        return actions;
    }

    protected void addAction(List<AbstractRepositoryAction> actions, AbstractRepositoryAction action,
            IRepositoryViewObject viewObj) {
        if (action != null) {
            action.selectionChanged(selection);
            if (action.isVisible(viewObj)) {
                actions.add(action);
            }
        }
    }

    /**
     * The child class can override this method, current implement is adapt to most of MDMServerobject
     */
    @Override
    public IRepositoryViewEditorInput getOpenEditorInput(IRepositoryViewObject viewObj) {
        Item item = viewObj.getProperty().getItem();
        MDMServerObject serverObject = ((MDMServerObjectItem) item).getMDMServerObject();
        if (serverObject.getType() == TreeObject.DATA_CLUSTER) {// Data Cluster
            return new XObjectBrowserInput2(viewObj);
        }
        return new XObjectEditorInput2(viewObj);
    }

    @Override
    public AbstractRepositoryAction getOpenAction(IRepositoryViewObject viewObj) {
        return null;
    }

    @Override
    public void setRepositoryViewGlobalActionHandler(IRepositoryViewGlobalActionHandler handler) {
        this.globalActionHandler = handler;
    }

    @Override
    public void updateSelection(IStructuredSelection selection) {
        this.selection = selection;
        for (IRepositoryNodeActionProvider provider : getExtendActionProviders()) {
            provider.updateSelection(selection);
        }
    }

    private static final String SVN_ACTION_PROVIDER_ID = "mdm.svn"; //$NON-NLS-1$

    private static final String GIT_ACTION_PROVIDER_ID = "mdm.git"; //$NON-NLS-1$

    IRepositoryNodeActionProvider[] extendActionProviders;

    private IRepositoryNodeActionProvider[] getExtendActionProviders() {
        if (extendActionProviders == null) {
            boolean isLocalProj = ProjectManager.getInstance().getCurrentProject().isLocal();
            boolean isOffline = ProxyRepositoryFactory.getInstance().getRepositoryContext().isOffline();
            if (!isLocalProj && !isOffline) {
                IRepositoryNodeActionProvider svnProvider = ActionProviderManager.getActionProvider(SVN_ACTION_PROVIDER_ID);
                IRepositoryNodeActionProvider gitProvider = ActionProviderManager.getActionProvider(GIT_ACTION_PROVIDER_ID);
                if (svnProvider != null && gitProvider != null) {
                    extendActionProviders = new IRepositoryNodeActionProvider[] { svnProvider, gitProvider };
                } else if (svnProvider != null) {
                    extendActionProviders = new IRepositoryNodeActionProvider[] { svnProvider };
                } else if (gitProvider != null) {
                    extendActionProviders = new IRepositoryNodeActionProvider[] { gitProvider };
                }
            }

            if (extendActionProviders == null) {
                extendActionProviders = new IRepositoryNodeActionProvider[0];
            }
        }
        return extendActionProviders;
    }
}
