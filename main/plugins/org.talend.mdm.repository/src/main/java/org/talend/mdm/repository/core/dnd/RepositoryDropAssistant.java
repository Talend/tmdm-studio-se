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
package org.talend.mdm.repository.core.dnd;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.navigator.CommonDropAdapter;
import org.eclipse.ui.navigator.CommonDropAdapterAssistant;
import org.eclipse.ui.navigator.CommonNavigator;
import org.talend.commons.exception.BusinessException;
import org.talend.commons.exception.LoginException;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.properties.FolderType;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.mdm.repository.core.IServerObjectRepositoryType;
import org.talend.mdm.repository.core.command.CommandManager;
import org.talend.mdm.repository.core.command.ICommand;
import org.talend.mdm.repository.core.impl.transformerV2.ITransformerV2NodeConsDef;
import org.talend.mdm.repository.core.impl.view.IViewNodeConstDef;
import org.talend.mdm.repository.core.service.ContainerCacheService;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.model.mdmproperties.ContainerItem;
import org.talend.mdm.repository.model.mdmproperties.MDMServerObjectItem;
import org.talend.mdm.repository.model.mdmserverobject.MDMServerObject;
import org.talend.mdm.repository.models.FolderRepositoryObject;
import org.talend.mdm.repository.plugin.RepositoryPlugin;
import org.talend.mdm.repository.ui.actions.view.MDMEditViewProcessPropertyAction;
import org.talend.mdm.repository.ui.navigator.MDMRepositoryView;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;
import org.talend.mdm.repository.utils.ValidateUtil;
import org.talend.repository.model.IProxyRepositoryFactory;

import com.amalto.workbench.exadapter.ExAdapterManager;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class RepositoryDropAssistant extends CommonDropAdapterAssistant {

    private static Logger log = Logger.getLogger(RepositoryDropAssistant.class);

    private IRepositoryDropAssistantExAdapter exAdapter;

    public RepositoryDropAssistant() {
        this.exAdapter = ExAdapterManager.getAdapter(this, IRepositoryDropAssistantExAdapter.class);
    }

    @Override
    public IStatus validateDrop(Object target, int operation, TransferData transferType) {
        if (operation == DND.DROP_COPY || operation == DND.DROP_MOVE) {
            if (!(target instanceof IRepositoryViewObject)) {
                return Status.CANCEL_STATUS;
            }
            List<IRepositoryViewObject> dragViewObjs = getSelectedDragViewObj();
            IRepositoryViewObject dropViewObj = (IRepositoryViewObject) target;
            for (IRepositoryViewObject dragViewObj : dragViewObjs) {
                boolean valid = validate(operation, dragViewObj, dropViewObj);
                if (!valid) {
                    return Status.CANCEL_STATUS;
                }
            }

            return Status.OK_STATUS;
        }

        return Status.CANCEL_STATUS;
    }

    public boolean validate(int operation, IRepositoryViewObject dragViewObj, IRepositoryViewObject dropViewObj) {
        if (dragViewObj == null || dropViewObj == null) {
            return false;
        }
        //
        if (dragViewObj instanceof FolderRepositoryObject) {
            return false;
        }

        ERepositoryObjectType dragType = dragViewObj.getRepositoryObjectType();

        // can't support job object
        if (dragType == ERepositoryObjectType.PROCESS) {
            return false;
        }

        if (dropViewObj instanceof FolderRepositoryObject) {
            if (((ContainerItem) dropViewObj.getProperty().getItem()).getType().getValue() == FolderType.STABLE_SYSTEM_FOLDER) {
                return false;
            }
        } else {
            return false;
        }
        // can't move to self folder
        if (operation == DND.DROP_MOVE) {
            IRepositoryViewObject dragParent = getParentRepositoryViewObject(dragViewObj);
            if (dragParent.equals(dropViewObj)) {
                return false;
            }
        }
        // can't move/copy to different node folder
        ERepositoryObjectType dropType = dropViewObj.getRepositoryObjectType();

        if (dragType != null && !dragType.equals(dropType)) {
            return false;
        }

        if (dragType == IServerObjectRepositoryType.TYPE_VIEW) {
            boolean viewValid = validateForView(dragViewObj, dropViewObj);

            return viewValid;
        }

        if (dragType == IServerObjectRepositoryType.TYPE_TRANSFORMERV2) {
            boolean processValid = validateForProcess(dragViewObj, dropViewObj);

            return processValid;
        }

        return true;
    }

    private boolean validateForView(IRepositoryViewObject dragViewObj, IRepositoryViewObject dropViewObj) {
        String dropPath = dropViewObj.getProperty().getItem().getState().getPath().toLowerCase();
        String dragPath = dragViewObj.getProperty().getItem().getState().getPath().toLowerCase();

        if (dropPath.isEmpty()) {
            return false;
        }

        String webTypePrefix = IPath.SEPARATOR + IViewNodeConstDef.PATH_WEBFILTER;
        if (dragPath.startsWith(webTypePrefix) && !dropPath.startsWith(webTypePrefix)) {
            return false;
        }

        if (!dragPath.startsWith(webTypePrefix) && dropPath.startsWith(webTypePrefix)) {
            return false;
        }

        return true;
    }

    private boolean validateForProcess(IRepositoryViewObject dragViewObj, IRepositoryViewObject dropViewObj) {
        String dropPath = dropViewObj.getProperty().getItem().getState().getPath().toLowerCase();
        String dragPath = dragViewObj.getProperty().getItem().getState().getPath().toLowerCase();

        if (dropPath.isEmpty()) {
            return false;
        }

        String beforeSavingPrefix = IPath.SEPARATOR + ITransformerV2NodeConsDef.PATH_BEFORESAVE;
        String beforeDelPrefix = IPath.SEPARATOR + ITransformerV2NodeConsDef.PATH_BEFOREDEL;
        String entityActionPrefix = IPath.SEPARATOR + ITransformerV2NodeConsDef.PATH_ENTITYACTION;
        String welcomeActionPrefix = IPath.SEPARATOR + ITransformerV2NodeConsDef.PATH_WELCOMEACTION;
        String smartviewPrefix = IPath.SEPARATOR + ITransformerV2NodeConsDef.PATH_SMARTVIEW;
        String otherPrefix = IPath.SEPARATOR + ITransformerV2NodeConsDef.PATH_OTHER;

        if (dragPath.startsWith(beforeSavingPrefix) && !dropPath.startsWith(beforeSavingPrefix)) {
            return false;
        }
        if (dragPath.startsWith(beforeDelPrefix) && !dropPath.startsWith(beforeDelPrefix)) {
            return false;
        }
        if (dragPath.startsWith(entityActionPrefix) && !dropPath.startsWith(entityActionPrefix)) {
            return false;
        }
        if (dragPath.startsWith(welcomeActionPrefix) && !dropPath.startsWith(welcomeActionPrefix)) {
            return false;
        }
        if (dragPath.startsWith(smartviewPrefix) && !dropPath.startsWith(smartviewPrefix)) {
            return false;
        }
        if (dragPath.startsWith(otherPrefix) && !dropPath.startsWith(otherPrefix)) {
            return false;
        }

        return true;
    }

    private List<IRepositoryViewObject> getSelectedDragViewObj() {
        List<IRepositoryViewObject> selectedViewObjects = new ArrayList<IRepositoryViewObject>();

        ISelection selection = LocalSelectionTransfer.getTransfer().getSelection();
        if (selection instanceof IStructuredSelection) {
            List<Object> list = ((IStructuredSelection) selection).toList();
            for (Object obj : list) {
                if (obj instanceof IRepositoryViewObject) {
                    selectedViewObjects.add((IRepositoryViewObject) obj);
                }
            }
        }

        return selectedViewObjects;
    }

    @Override
    public IStatus handleDrop(CommonDropAdapter dropAdapter, DropTargetEvent dropTargetEvent, Object aTarget) {
        IRepositoryViewObject dropViewObj = (IRepositoryViewObject) aTarget;
        IRepositoryViewObject dropParent = getParentRepositoryViewObject(dropViewObj);

        List<IRepositoryViewObject> dragViewObjs = getSelectedDragViewObj();

        for (IRepositoryViewObject dragViewObj : dragViewObjs) {
            IRepositoryViewObject dragParent = getParentRepositoryViewObject(dragViewObj);
            int detail = dropTargetEvent.detail;
            if (detail == DND.DROP_COPY) {
                if (!copyViewObj(dragViewObj, dropViewObj)) {
                    return Status.CANCEL_STATUS;
                }

                refreshContainer(dropParent);
            } else if (detail == DND.DROP_MOVE) {
                if (!moveViewObj(dragViewObj, dropViewObj)) {
                    return Status.CANCEL_STATUS;
                }

                refreshContainer(dropParent);
                refreshContainer(dragParent);
            }
        }

        return Status.OK_STATUS;
    }

    /**
     * DOC hbhong Comment method "moveViewObj".
     * 
     * @param dragViewObj
     * @param dropViewObj
     * @return
     */
    private boolean moveViewObj(IRepositoryViewObject dragViewObj, IRepositoryViewObject dropViewObj) {
        if (dropViewObj != null && dragViewObj != null) {
            if (RepositoryResourceUtil.isLockedViewObject(dragViewObj)) {
                MessageDialog.openError(getShell(), Messages.AbstractRepositoryAction_lockedObjTitle,
                        Messages.RepositoryDropAssistant_stopMoveMsg);
                return false;
            }
            Property dropProp = dropViewObj.getProperty();
            String pathStr = dropProp.getItem().getState().getPath();
            IPath path = new Path(pathStr);
            IProxyRepositoryFactory factory = CoreRuntimePlugin.getInstance().getProxyRepositoryFactory();
            try {
                factory.moveObject(dragViewObj, path);
                return true;
            } catch (PersistenceException e) {
                log.error(e.getMessage(), e);
            } catch (BusinessException e) {
                log.error(e.getMessage(), e);
            }

        }
        return false;
    }

    public boolean copyViewObj(IRepositoryViewObject dragViewObj, IRepositoryViewObject dropViewObj) {
        if (dropViewObj != null && dragViewObj != null) {

            Property dragProp = dragViewObj.getProperty();
            Property dropProp = dropViewObj.getProperty();
            Item item = dragProp.getItem();
            String name;
            if (item instanceof MDMServerObjectItem) {
                MDMServerObject serverObj = ((MDMServerObjectItem) item).getMDMServerObject();
                name = serverObj.getName();
            } else {
                name = dragProp.getLabel();
            }
            // show dialog
            IRepositoryViewObject dragParentViewObj = getParentRepositoryViewObject(dragViewObj);
            ContainerItem dragParentItem = (ContainerItem) dragParentViewObj.getProperty().getItem();
            String newName = showPasteDlg(dragParentItem.getRepObjType(), dragParentItem, "Copy_" + name); //$NON-NLS-1$
            if (newName != null) {
                String pathStr = dropProp.getItem().getState().getPath();
                pathStr = rebuildPath(dragViewObj, name, newName, pathStr);
                IPath path = new Path(pathStr);
                ERepositoryObjectType type = dropViewObj.getRepositoryObjectType();
                if (type == IServerObjectRepositoryType.TYPE_WORKFLOW) {
                    if (exAdapter != null) {
                        return exAdapter.copyWorkflowViewObj(item, name, newName);
                    }
                } else {
                    IProxyRepositoryFactory factory = CoreRuntimePlugin.getInstance().getProxyRepositoryFactory();
                    Item copy = null;
                    try {
                        copy = factory.copy(item, path, true);
                        if (factory.isEditableAndLockIfPossible(copy)) {
                            if (copy instanceof MDMServerObjectItem) {
                                ((MDMServerObjectItem) copy).getMDMServerObject().setName(newName);
                                ((MDMServerObjectItem) copy).getMDMServerObject().setLastServerDef(null);
                                CommandManager.getInstance().pushCommand(ICommand.CMD_ADD, copy.getProperty().getId(), newName);
                            }
                            copy.getProperty().setLabel(newName);
                            copy.getProperty().setDisplayName(newName);
                            RepositoryResourceUtil.setLastServerDef(copy, null);
                            factory.save(copy);

                            MDMRepositoryView.show().refreshRootNode(type);
                            return true;
                        }
                    } catch (PersistenceException e) {
                        log.error(e.getMessage(), e);
                    } catch (BusinessException e) {
                        log.error(e.getMessage(), e);
                    } finally {

                        try {
                            factory.unlock(copy);
                        } catch (PersistenceException e) {
                            log.error(e.getMessage(), e);
                        } catch (LoginException e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                }
            }
        }
        return false;
    }

    private String rebuildPath(IRepositoryViewObject dragViewObj, String name, String newName, String pathStr) {
        String newPath = null;

        ERepositoryObjectType objType = dragViewObj.getRepositoryObjectType();
        if (objType == IServerObjectRepositoryType.TYPE_VIEW || objType == IServerObjectRepositoryType.TYPE_TRANSFORMERV2) {
            newPath = new MDMEditViewProcessPropertyAction().getNewPath(objType, newName, name);
        }

        if (newPath == null) {
            newPath = pathStr;
        }

        return newPath;
    }

    private String showPasteDlg(final ERepositoryObjectType type, final ContainerItem parentItem, String initLabel) {
        InputDialog dlg = new InputDialog(getShell(), Messages.RepositoryDropAssistant_pasteObject, Messages.Common_inputName,
                initLabel, new IInputValidator() {

                    @Override
                    public String isValid(String newText) {
                        if (newText == null || newText.trim().length() == 0) {
                            return Messages.Common_nameCanNotBeEmpty;
                        }

                        if (type.equals(IServerObjectRepositoryType.TYPE_TRANSFORMERV2)) {
                            if (newText.startsWith(ITransformerV2NodeConsDef.PREFIX_SMARTVIEW_UPPER)) {
                                if (!ValidateUtil.matchSmartViewRegex(newText)) {
                                    return Messages.Common_nameInvalid;
                                }
                            }

                            if (!ValidateUtil.matchViewProcessRegex(newText)) {
                                return Messages.Common_nameInvalid;
                            }
                        } else if (type.equals(IServerObjectRepositoryType.TYPE_VIEW)) {
                            if (!ValidateUtil.matchViewProcessRegex(newText)) {
                                return Messages.Common_nameInvalid;
                            }
                        } else if (type.equals(IServerObjectRepositoryType.TYPE_ROLE)) {
                            if (!ValidateUtil.matchRoleRegex(newText)) {
                                return Messages.Common_nameInvalid;
                            }
                        } else if (!ValidateUtil.matchCommonRegex(newText)) {
                            return Messages.Common_nameInvalid;
                        }
                        //
                        if (RepositoryResourceUtil.isExistByName(parentItem.getRepObjType(), newText.trim())) {
                            return Messages.Common_nameIsUsed;
                        }
                        return null;
                    };
                });
        dlg.setBlockOnOpen(true);
        if (dlg.open() == Window.CANCEL) {
            return null;
        }
        return dlg.getValue();

    }

    private void refreshContainer(final IRepositoryViewObject viewObj) {
        Display.getDefault().asyncExec(new Runnable() {

            @Override
            public void run() {
                if (viewObj != null) {
                    try {
                        IViewPart viewPart = RepositoryPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow()
                                .getActivePage().findView(getContentService().getViewerId());
                        if (viewPart != null && viewPart instanceof CommonNavigator) {
                            ((CommonNavigator) viewPart).getCommonViewer().refresh(viewObj);
                        }
                        Item item = viewObj.getProperty().getItem();
                        if (item instanceof ContainerItem) {
                            ERepositoryObjectType repObjType = ((ContainerItem) item).getRepObjType();
                            if (repObjType == IServerObjectRepositoryType.TYPE_TRANSFORMERV2) {
                                ContainerCacheService.refreshRepositoryRoot(IServerObjectRepositoryType.TYPE_EVENTMANAGER,
                                        ((CommonNavigator) viewPart).getCommonViewer());
                            }
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }

        });

    }

    public IRepositoryViewObject getParentRepositoryViewObject(IRepositoryViewObject dropViewObj) {
        Item dropItem = dropViewObj.getProperty().getItem();
        if (dropItem instanceof ContainerItem) {
            return dropViewObj;
        }
        return ContainerCacheService.getParent(dropViewObj);

    }
}
