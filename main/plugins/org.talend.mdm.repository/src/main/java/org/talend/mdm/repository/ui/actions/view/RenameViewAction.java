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
package org.talend.mdm.repository.ui.actions.view;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.ui.IWorkbenchPartSite;
import org.talend.commons.exception.BusinessException;
import org.talend.commons.exception.LoginException;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.mdm.repository.core.AbstractRepositoryAction;
import org.talend.mdm.repository.core.command.CommandManager;
import org.talend.mdm.repository.core.command.ICommand;
import org.talend.mdm.repository.core.impl.view.IViewNodeConstDef;
import org.talend.mdm.repository.core.service.ContainerCacheService;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.model.mdmmetadata.MDMServerDef;
import org.talend.mdm.repository.model.mdmproperties.MDMServerObjectItem;
import org.talend.mdm.repository.model.mdmserverobject.MDMServerObject;
import org.talend.mdm.repository.ui.actions.RenameObjectAction;
import org.talend.mdm.repository.ui.dialogs.RenameViewDialog;
import org.talend.mdm.repository.ui.dialogs.RenameViewDialog2;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;
import org.talend.mdm.repository.utils.RepositoryTransformUtil;
import org.talend.mdm.repository.utils.ValidateUtil;
import org.talend.repository.model.IProxyRepositoryFactory;

import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;

public class RenameViewAction extends AbstractRepositoryAction implements IViewNodeConstDef {

    private static Logger log = Logger.getLogger(RenameObjectAction.class);

    private IInputValidator inputValidator;

    private ERepositoryObjectType etype;

    private boolean viewTypeChanged = false;

    public RenameViewAction() {
        super(Messages.RenameObjectAction_rename);
        setImageDescriptor(ImageCache.getImage(EImage.RENAME.getPath()));
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
        Object selectObj = getSelectedObject().get(0);
        if (selectObj instanceof IRepositoryViewObject) {
            IRepositoryViewObject viewObj = (IRepositoryViewObject) selectObj;
            IRepositoryViewObject parent = ContainerCacheService.getParent(viewObj);
            etype = viewObj.getRepositoryObjectType();

            //
            renameViewObject(viewObj);

            if (viewTypeChanged) {
                moveToAnotherNode(parent, viewObj);
            }
        }
    }

    private void renameViewObject(IRepositoryViewObject viewObj) {
        MDMServerObjectItem item = (MDMServerObjectItem) viewObj.getProperty().getItem();
        MDMServerObject serverObject = item.getMDMServerObject();

        IProxyRepositoryFactory factory = getFactory();
        try {
            if (serverObject != null) {
                String oldName = serverObject.getName();
                RenameViewDialog dialog = getRenameDialog(oldName);

                int returnCode = dialog.open();
                if (returnCode == IDialogConstants.OK_ID) {
                    String newName = dialog.getValue();

                    if (RepositoryTransformUtil.getInstance().getViewType(oldName) == TYPE_WEBFILTER) {
                        newName = PREFIX_VIEW_UPPER + newName;
                    } else {
                        if (newName.startsWith(PREFIX_VIEW_UPPER)) {
                            viewTypeChanged = true;

                            item.getState().setPath(IPath.SEPARATOR + PATH_WEBFILTER);
                        }
                    }

                    if (newName != null && factory.isEditableAndLockIfPossible(item)) {
                        serverObject.setName(newName);
                        viewObj.getProperty().setLabel(newName);
                        viewObj.getProperty().setDisplayName(newName);

                        factory.save(viewObj.getProperty().getItem(), false);
                        MDMServerDef lastServerDef = RepositoryResourceUtil.getLastServerDef(viewObj);
                        if (lastServerDef != null) {
                            CommandManager.getInstance().pushCommand(ICommand.CMD_RENAME, viewObj.getId(),
                                    new String[] { oldName, newName });
                        }
                    }

                    commonViewer.refresh(viewObj);
                }
            }
        } catch (PersistenceException e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                factory.unlock(item);
            } catch (PersistenceException e) {
                log.error(e.getMessage(), e);
            } catch (LoginException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    public void moveToAnotherNode(IRepositoryViewObject parent, IRepositoryViewObject viewObj) {
        MDMServerObjectItem item = (MDMServerObjectItem) viewObj.getProperty().getItem();

        waitSomeTime(viewObj);

        IProxyRepositoryFactory factory = getFactory();
        try {
            if (factory.isEditableAndLockIfPossible(item)) {
                String path = item.getState().getPath();
                IPath ipath = new Path(path);
                factory.moveObject(viewObj, ipath);

                IRepositoryViewObject iRepositoryViewObject = ContainerCacheService.get(viewObj.getRepositoryObjectType(), path);
                commonViewer.refresh(iRepositoryViewObject);
                commonViewer.refresh(parent);
            }
        } catch (PersistenceException e) {
            log.error(e.getMessage(), e);
        } catch (BusinessException e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                factory.unlock(item);
            } catch (PersistenceException e) {
                log.error(e.getMessage(), e);
            } catch (LoginException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    private void waitSomeTime(IRepositoryViewObject viewObj) {
        boolean lockedViewObject = RepositoryResourceUtil.isLockedViewObject(viewObj);

        if (lockedViewObject) {
            try {
                Thread.sleep(300);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    private RenameViewDialog getRenameDialog(String oldName) {
        RenameViewDialog dialog = null;

        IWorkbenchPartSite site = commonViewer.getCommonNavigator().getSite();
        if (RepositoryTransformUtil.getInstance().getViewType(oldName) == TYPE_WEBFILTER) {
            dialog = new RenameViewDialog(getShell(), Messages.RenameObjectAction_rename, Messages.Common_rename,
                    oldName.substring(PREFIX_VIEW_UPPER.length()), getInputValidator(), site);
        } else {
            dialog = new RenameViewDialog2(getShell(), Messages.RenameObjectAction_rename, Messages.Common_rename, oldName,
                    getInputValidator(), site);
        }

        return dialog;
    }

    private IProxyRepositoryFactory getFactory() {
        IProxyRepositoryFactory factory = CoreRuntimePlugin.getInstance().getProxyRepositoryFactory();
        return factory;
    }

    private IInputValidator getInputValidator() {
        if (inputValidator == null) {
            inputValidator = new IInputValidator() {

                public String isValid(String newText) {
                    if (newText == null || newText.trim().length() == 0) {
                        return Messages.Common_nameCanNotBeEmpty;
                    }

                    if (!ValidateUtil.matchViewProcessRegex(newText)) {
                        return Messages.Common_nameInvalid;
                    }
                    //
                    if (RepositoryResourceUtil.isExistByName(etype, newText.trim())) {
                        return Messages.Common_nameIsUsed;
                    }
                    return null;
                };
            };
        }

        return inputValidator;
    }

    @Override
    public boolean isVisible(IRepositoryViewObject viewObj) {
        if (getSelectedObject().size() > 1) {
            return false;
        }

        return super.isVisible(viewObj);
    }
}
