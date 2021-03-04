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
package org.talend.mdm.repository.ui.actions.process;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchPartSite;
import org.talend.commons.exception.BusinessException;
import org.talend.commons.exception.LoginException;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.mdm.repository.core.AbstractRepositoryAction;
import org.talend.mdm.repository.core.command.CommandManager;
import org.talend.mdm.repository.core.command.ICommand;
import org.talend.mdm.repository.core.impl.transformerV2.ITransformerV2NodeConsDef;
import org.talend.mdm.repository.core.service.ContainerCacheService;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.model.mdmmetadata.MDMServerDef;
import org.talend.mdm.repository.model.mdmproperties.MDMServerObjectItem;
import org.talend.mdm.repository.model.mdmserverobject.MDMServerObject;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;
import org.talend.mdm.repository.utils.RepositoryTransformUtil;
import org.talend.mdm.repository.utils.ValidateUtil;
import org.talend.repository.model.IProxyRepositoryFactory;

import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;

public class RenameProcessAction extends AbstractRepositoryAction implements ITransformerV2NodeConsDef {

    private static Logger log = Logger.getLogger(RenameProcessAction.class);

    private String oldPrefix = null;

    protected IInputValidator validator;

    private boolean processTypeChanged = false;

    private ERepositoryObjectType etype;

    public RenameProcessAction() {
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
            renameProcessObject(viewObj);

            if (processTypeChanged) {
                moveToOtherTypeNode(parent, viewObj);
            }
        }

    }

    private void renameProcessObject(IRepositoryViewObject viewObj) {
        MDMServerObjectItem item = (MDMServerObjectItem) viewObj.getProperty().getItem();
        MDMServerObject serverObject = item.getMDMServerObject();

        int type = getType(item);
        if (serverObject != null) {
            String oldName = serverObject.getName();
            RenameProcessDialog dialog = getRenameDialog(getSite(), oldName, type, getInputValidator());
            int returnCode = dialog.open();

            if (returnCode == Window.OK) {
                IProxyRepositoryFactory factory = getFactory();
                String newName = dialog.getValue();

                transformPath(viewObj, newName);

                try {
                    if (factory.isEditableAndLockIfPossible(item)) {
                        serverObject.setName(newName);
                        viewObj.getProperty().setLabel(newName);
                        viewObj.getProperty().setDisplayName(newName);

                        factory.save(item, false);
                        MDMServerDef lastServerDef = RepositoryResourceUtil.getLastServerDef(item);
                        if (lastServerDef != null) {
                            CommandManager.getInstance().pushCommand(ICommand.CMD_RENAME, viewObj.getId(),
                                    new String[] { oldName, newName });
                        }
                    }

                    commonViewer.refresh(viewObj);
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
        }
    }

    private void waitSomeTime(IRepositoryViewObject viewObj) {
        boolean lockedViewObject = RepositoryResourceUtil.isLockedViewObject(viewObj);
        System.out.println(lockedViewObject);

        if (lockedViewObject) {
            try {
                Thread.sleep(300);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    public void moveToOtherTypeNode(IRepositoryViewObject parent, IRepositoryViewObject viewObj) {
        MDMServerObjectItem item = (MDMServerObjectItem) viewObj.getProperty().getItem();

        waitSomeTime(viewObj);

        IProxyRepositoryFactory factory = getFactory();
        try {
            if (factory.isEditableAndLockIfPossible(item)) {
                String path = item.getState().getPath();
                IPath ipath = new Path(path);
                factory.moveObject(viewObj, ipath);

                IRepositoryViewObject iRepositoryViewObject = ContainerCacheService.get(viewObj.getRepositoryObjectType(), path);
                commonViewer.refresh(parent);
                commonViewer.refresh(iRepositoryViewObject);
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

    private IWorkbenchPartSite getSite() {
        IWorkbenchPartSite site = commonViewer.getCommonNavigator().getSite();
        return site;
    }

    private IProxyRepositoryFactory getFactory() {
        IProxyRepositoryFactory factory = CoreRuntimePlugin.getInstance().getProxyRepositoryFactory();
        return factory;
    }

    private RenameProcessDialog getRenameDialog(IWorkbenchPartSite site, String processName, int type,
            IInputValidator inputValidator) {
        RenameProcessDialog renameDialog = new RenameProcessDialog(site.getShell(), site, type, processName, inputValidator);
        return renameDialog;
    }

    private int getType(Item item) {
        int type = 0;

        String path = item.getState().getPath();
        if (path.startsWith("/")) { //$NON-NLS-1$
            path = path.substring(1);
        }
        if (path.startsWith(PATH_BEFORESAVE)) {
            type = TYPE_BEFORESAVE;
            oldPrefix = PREFIX_BEFORESAVE_UPPER;
        } else if (path.startsWith(PATH_BEFOREDEL)) {
            type = TYPE_BEFOREDEL;
            oldPrefix = PREFIX_BEFOREDEL_UPPER;
        } else if (path.startsWith(PATH_ENTITYACTION)) {
            type = TYPE_ENTITYACTION;
            oldPrefix = PREFIX_RUNNABLE_UPPER;
        } else if (path.startsWith(PATH_WELCOMEACTION)) {
            type = TYPE_WELCOMEACTION;
            oldPrefix = PREFIX_STANDLONE_UPPER;
        } else if (path.startsWith(PATH_SMARTVIEW)) {
            type = TYPE_SMARTVIEW;
            oldPrefix = PREFIX_SMARTVIEW_UPPER;
        } else if (path.startsWith(PATH_OTHER)) {
            type = TYPE_OTHER;
            oldPrefix = ""; //$NON-NLS-1$
        }

        return type;
    }

    private void transformPath(IRepositoryViewObject viewObj, String newName) {
        String newPath = getNewPath(newName);
        if (newPath != null) {
            processTypeChanged = true;
            viewObj.getProperty().getItem().getState().setPath(newPath);
        }
    }

    private String getNewPath(String newName) {
        String path = null;

        if (oldPrefix.isEmpty()) {
            return RepositoryTransformUtil.getInstance().getProcessPath(newName, true);
        }

        return path;
    }

    private IInputValidator getInputValidator() {
        if (validator == null) {
            validator = new IInputValidator() {

                @Override
                public String isValid(String newText) {
                    if (newText == null || newText.trim().length() == 0) {
                        return Messages.Common_nameCanNotBeEmpty;
                    }

                    if (newText.startsWith(ITransformerV2NodeConsDef.PREFIX_SMARTVIEW_UPPER)) {
                        if (!ValidateUtil.matchSmartViewRegex(newText)) {
                            return Messages.Common_nameInvalid;
                        }
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

        return validator;
    }

    @Override
    public boolean isVisible(IRepositoryViewObject viewObj) {
        if (getSelectedObject().size() == 1) {
            return true;
        }
        return false;
    }
}
