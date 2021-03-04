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
package org.talend.mdm.repository.ui.actions.recyclebin;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.general.Project;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ProcessItem;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.dataquality.properties.TDQMatchRuleItem;
import org.talend.mdm.repository.core.AbstractRepositoryAction;
import org.talend.mdm.repository.core.IRepositoryNodeConfiguration;
import org.talend.mdm.repository.core.IRepositoryNodeResourceProvider;
import org.talend.mdm.repository.core.IServerObjectRepositoryType;
import org.talend.mdm.repository.core.command.CommandManager;
import org.talend.mdm.repository.core.service.ContainerCacheService;
import org.talend.mdm.repository.core.service.IRemoveViewObjectService;
import org.talend.mdm.repository.extension.RepositoryNodeConfigurationManager;
import org.talend.mdm.repository.model.mdmproperties.ContainerItem;
import org.talend.mdm.repository.model.mdmproperties.MDMServerObjectItem;
import org.talend.mdm.repository.models.FolderRepositoryObject;
import org.talend.mdm.repository.ui.dialogs.recycle.WaitToDeployDialog;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;
import org.talend.mdm.repository.utils.ServiceUtil;
import org.talend.repository.ProjectManager;
import org.talend.repository.model.IProxyRepositoryFactory;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public abstract class AbstractRemoveCommandStackAction extends AbstractRepositoryAction {

    IProxyRepositoryFactory factory = CoreRuntimePlugin.getInstance().getProxyRepositoryFactory();

    private static final Logger LOG = Logger.getLogger(AbstractRemoveCommandStackAction.class);

    /**
     * DOC hbhong AbstractRemoveCommandStackAction constructor comment.
     *
     * @param text
     */
    protected AbstractRemoveCommandStackAction(String text) {
        super(text);
    }

    protected boolean canRemove(List<IRepositoryViewObject> viewObjs) {
        WaitToDeployDialog dialog = new WaitToDeployDialog(getShell(), viewObjs);
        if (dialog.needShowDialog()) {
            return dialog.open() == IDialogConstants.OK_ID;
        }
        return true;
    }

    protected void removeViewObjects(List<IRepositoryViewObject> viewObjs) {
        if (canRemove(viewObjs)) {
            for (IRepositoryViewObject viewObj : viewObjs) {
                RepositoryResourceUtil.closeEditor(viewObj, false);
                if (isServerObject(viewObj)) {
                    beforeRemoveServerObject(viewObj, viewObjs);
                    removeServerObject(viewObj);
                } else if (viewObj instanceof FolderRepositoryObject) {
                    removeFolderObject((FolderRepositoryObject) viewObj);
                }
            }
        }

        try {
            factory.saveProject(ProjectManager.getInstance().getCurrentProject());
        } catch (PersistenceException e) {
            LOG.error("Failed to save project after remove repository view ojbects.", e);
        }

        refreshRepositoryRoot(IServerObjectRepositoryType.TYPE_RECYCLE_BIN);
    }

    protected void beforeRemoveServerObject(IRepositoryViewObject viewObj, List<IRepositoryViewObject> allWillDeletedViewObjs) {
        IRemoveViewObjectService service = (IRemoveViewObjectService) ServiceUtil.getService(IRemoveViewObjectService.class);
        if (service != null) {
            service.beforeRemoveServerObject(viewObj, allWillDeletedViewObjs);
        }
    }

    private boolean isServerObject(IRepositoryViewObject viewObj) {
        Item item = viewObj.getProperty().getItem();
        return item instanceof MDMServerObjectItem || item instanceof ProcessItem || item instanceof TDQMatchRuleItem;
    }

    private void removeServerObject(IRepositoryViewObject viewObj) {
        try {
            List<String> versions = new ArrayList<>();
            List<IRepositoryViewObject> allVersionViewObjects = RepositoryResourceUtil.findAllVersionViewObjects(viewObj);
            for (IRepositoryViewObject _viewObj : allVersionViewObjects) {
                versions.add(_viewObj.getVersion());
            }

            ERepositoryObjectType type = viewObj.getRepositoryObjectType();
            String label = viewObj.getLabel();
            //
            String id = viewObj.getId();
            List<IRepositoryViewObject> viewObjs = new ArrayList<IRepositoryViewObject>();
            viewObjs.add(viewObj);

            ContainerCacheService.remove(id);
            factory.deleteObjectPhysical(viewObj);
            CommandManager.getInstance().removeCommandStack(id);
            //
            postRemove(type, label, versions.toArray(new String[0]));
        } catch (Exception e) {
            LOG.error("Failed to remove repository view object " + (viewObj == null ? null : viewObj.getLabel()), e);
        }
    }

    private void postRemove(ERepositoryObjectType type, String name, String[] versions) {
        IRepositoryNodeConfiguration configuration = RepositoryNodeConfigurationManager.getConfiguration(type);
        if (configuration != null) {
            IRepositoryNodeResourceProvider resourceProvider = configuration.getResourceProvider();
            if (resourceProvider != null) {
                resourceProvider.postDelete(name, versions);
            }
        }
    }

    private void removeFolderObject(FolderRepositoryObject viewObj) {
        Project project = ProjectManager.getInstance().getCurrentProject();
        ContainerItem containerItem = (ContainerItem) viewObj.getProperty().getItem();
        String path = containerItem.getState().getPath();
        ERepositoryObjectType repObjType = containerItem.getRepObjType();
        // ContainerCacheService.removeContainer(repObjType, path);
        for (IRepositoryViewObject childObj : viewObj.getChildren()) {
            if (childObj instanceof FolderRepositoryObject) {
                removeFolderObject((FolderRepositoryObject) childObj);
            } else {
                removeServerObject(childObj);
            }
        }
        try {
            factory.deleteFolder(project, repObjType, new Path(path), false);
        } catch (PersistenceException e) {
            LOG.error("Failed to delete folder " + path, e);
        }
    }
}
