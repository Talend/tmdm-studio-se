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
package org.talend.mdm.repository.ui.actions;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorReference;
import org.talend.commons.exception.BusinessException;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.model.properties.FolderItem;
import org.talend.core.model.properties.FolderType;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.TDQItem;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.dataquality.record.linkage.ui.service.IMatchRuleChangeService;
import org.talend.mdm.repository.core.AbstractRepositoryAction;
import org.talend.mdm.repository.core.command.CommandManager;
import org.talend.mdm.repository.core.command.ICommand;
import org.talend.mdm.repository.core.service.ContainerCacheService;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.model.mdmproperties.ContainerItem;
import org.talend.mdm.repository.model.mdmproperties.MDMServerObjectItem;
import org.talend.mdm.repository.model.mdmserverobject.MDMServerObject;
import org.talend.mdm.repository.models.FolderRepositoryObject;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;
import org.talend.repository.ProjectManager;
import org.talend.repository.model.IProxyRepositoryFactory;

import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class RemoveFromRepositoryAction extends AbstractRepositoryAction {

    static Logger log = Logger.getLogger(RemoveFromRepositoryAction.class);

    IProxyRepositoryFactory factory = CoreRuntimePlugin.getInstance().getProxyRepositoryFactory();

    private List<Object> lockedObjs;

    private List<Object> removed = new LinkedList<Object>();

    /**
     * DOC hbhong RemoveFromRepositoryAction constructor comment.
     * 
     * @param text
     */
    public RemoveFromRepositoryAction() {
        super(""); //$NON-NLS-1$
        setImageDescriptor(ImageCache.getImage(EImage.DELETE_OBJ.getPath()));
    }

    @Override
    public String getText() {
        return Messages.RemoveFromRepositoryAction_removeFromRepository;
    }

    @Override
    public String getGroupName() {
        return GROUP_EDIT;
    }

    @Override
    protected boolean needValidateLockedObject() {
        return true;
    }

    @Override
    protected void doRun() {
        List<Object> selectedObject = getSelectedObject();
        int size = selectedObject.size();
        if (size > 0) {
            if (hasOpenedObject(selectedObject)) {
                warn();
                return;
            }
            if (!confirm(size)) {
                return;
            }

        }

        selectedObject.removeAll(lockedObjs);
        // handle delete event for DQ match rule object
        if (!handleDeleteEvent(selectedObject)) {
            return;
        }
        for (Object obj : selectedObject) {
            if (obj instanceof IRepositoryViewObject) {
                IRepositoryViewObject viewObj = (IRepositoryViewObject) obj;
                if (isServerObject(viewObj)) {
                    removeServerObject(viewObj);
                } else if (RepositoryResourceUtil.hasContainerItem(obj, FolderType.FOLDER_LITERAL)) {
                    removeFolderObject(viewObj);
                }

            }
        }

        try {
            factory.saveProject(ProjectManager.getInstance().getCurrentProject());
        } catch (PersistenceException e) {
            log.error(e.getMessage(), e);
        }

        commonViewer.refresh();
        removed.clear();

        if (lockedObjs.size() > 0) {
            MessageDialog.openError(getShell(), Messages.AbstractRepositoryAction_lockedObjTitle, getAlertMsg());
        }
    }

    private void warn() {
        MessageDialog
        .openWarning(
                getShell(),
                Messages.RemoveFromRepositoryAction_Title,
                Messages.RemoveFromRepositoryAction_CanNotDeleteFolder);
    }

    private boolean confirm(int size) {
        return MessageDialog.openConfirm(getShell(), Messages.RemoveFromRepositoryAction_Title, Messages.bind(
                Messages.RemoveFromRepositoryAction_confirm, size, size > 1 ? Messages.RemoveFromRepositoryAction_instances
                        : Messages.RemoveFromRepositoryAction_instance));
    }


    private boolean hasOpenedObject(List selectedObject) {
        if (selectedObject != null) {
            for (Object object : selectedObject) {
                if (object instanceof FolderRepositoryObject) {
                    boolean result = hasOpenedObject(((FolderRepositoryObject) object).getChildren());
                    if (result) {
                        return true;
                    }
                } else {
                    if (object instanceof IRepositoryViewObject
                            && !factory.getRepositoryContext().isEditableAsReadOnly()
                            && (!factory.isPotentiallyEditable((IRepositoryViewObject) object) || factory
                                    .isUserReadOnlyOnCurrentProject())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void collectSelectedMatchRuleObjs(Object obj, List<IRepositoryViewObject> matchRules) {
        IRepositoryViewObject viewObj = (IRepositoryViewObject) obj;
        if (viewObj instanceof FolderRepositoryObject) {
            for (IRepositoryViewObject child : ((FolderRepositoryObject) viewObj).getChildren()) {
                collectSelectedMatchRuleObjs(child, matchRules);
            }
        } else if (viewObj.getRepositoryObjectType() == ERepositoryObjectType.TDQ_RULES_MATCHER) {
            matchRules.add(viewObj);
        }
    }

    private boolean handleDeleteEvent(List<Object> selectedObject) {
        List<IRepositoryViewObject> matchRules = new LinkedList<IRepositoryViewObject>();
        for (Object object : selectedObject) {
            collectSelectedMatchRuleObjs(object, matchRules);
        }

        if (!matchRules.isEmpty()) {
            IMatchRuleChangeService changeService = getMatchRuleChangeService();
            if (changeService == null) {
                return true;
            } else {
                boolean isOk = changeService.objectChange(null, matchRules, null,
                        IMatchRuleChangeService.ChangeEvent.BEFORE_DELETE);
                return isOk;
            }
        }
        return true;
    }

    private IMatchRuleChangeService getMatchRuleChangeService() {
        if (GlobalServiceRegister.getDefault().isServiceRegistered(IMatchRuleChangeService.class)) {
            IMatchRuleChangeService service = (IMatchRuleChangeService) GlobalServiceRegister.getDefault().getService(
                    IMatchRuleChangeService.class);
            return service;
        }
        return null;
    }

    private boolean isServerObject(IRepositoryViewObject viewObj) {
        Item item = viewObj.getProperty().getItem();
        return item instanceof MDMServerObjectItem || item instanceof TDQItem;
    }

    private void removeServerObject(IRepositoryViewObject viewObj) {
        if (removed.contains(viewObj.getId())) {
            return;
        }

        removed.add(viewObj.getId());

        try {
            Item item = viewObj.getProperty().getItem();
            IEditorReference ref = RepositoryResourceUtil.isOpenedInEditor(viewObj);

            if (ref != null) {
                RepositoryResourceUtil.closeEditor(ref, true);
            }

            factory.deleteObjectLogical(viewObj);
            if (item instanceof MDMServerObjectItem) {
                MDMServerObject serverObj = ((MDMServerObjectItem) item).getMDMServerObject();
                CommandManager.getInstance().pushCommand(ICommand.CMD_DELETE, viewObj.getId(), serverObj.getName());
            }
        } catch (BusinessException e) {
            MessageDialog.openError(getShell(), Messages.Common_Error, e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void removeFolderObject(IRepositoryViewObject viewObj) {
        if (removed.contains(viewObj.getId())) {
            return;
        }

        for (IRepositoryViewObject childObj : viewObj.getChildren()) {
            if (childObj instanceof FolderRepositoryObject) {
                removeFolderObject(childObj);
            } else {
                removeServerObject(childObj);
            }
        }

        removed.add(viewObj.getId());
        //
        ContainerItem containerItem = (ContainerItem) viewObj.getProperty().getItem();
        String path = containerItem.getState().getPath();
        ERepositoryObjectType repObjType = containerItem.getRepObjType();

        ContainerCacheService.removeContainer(repObjType, path);

        FolderItem folderItem = factory.getFolderItem(ProjectManager.getInstance().getCurrentProject(), repObjType,
                new Path(path));
        folderItem.getState().setDeleted(true);

    }

    @Override
    protected boolean isLocked() {
        initLockedObjectArray();

        boolean unlocked = false;

        List<Object> selectedObject = getSelectedObject();
        if (selectedObject != null && !selectedObject.isEmpty()) {
            for (Object obj : selectedObject) {
                if (obj instanceof IRepositoryViewObject) {
                    boolean locked = RepositoryResourceUtil.isLockedAndEdited((IRepositoryViewObject) obj);
                    if (locked) {
                        lockedObjs.add(obj);
                        continue;
                    }

                    unlocked = true;
                }
            }
        }

        return !unlocked;
    }

    private void initLockedObjectArray() {
        if (lockedObjs == null) {
            lockedObjs = new ArrayList<Object>();
        }
        lockedObjs.clear();
    }

    protected String getAlertMsg() {
        return Messages.bind(Messages.RemoveFromRepositoryAction_AlterLockMsg, lockedObjs.size());
    }

    @Override
    public boolean isVisible(IRepositoryViewObject viewObj) {
        if (RepositoryResourceUtil.isSystemViewObject(viewObj)) {
            return false;
        }
        return true;
    }
}
