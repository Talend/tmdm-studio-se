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

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.talend.commons.exception.LoginException;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.mdm.repository.core.AbstractRepositoryAction;
import org.talend.mdm.repository.core.IRepositoryNodeConfiguration;
import org.talend.mdm.repository.core.IServerObjectRepositoryType;
import org.talend.mdm.repository.core.command.CommandManager;
import org.talend.mdm.repository.core.command.ICommand;
import org.talend.mdm.repository.core.service.ContainerCacheService;
import org.talend.mdm.repository.extension.RepositoryNodeConfigurationManager;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.model.mdmmetadata.MDMServerDef;
import org.talend.mdm.repository.model.mdmproperties.ContainerItem;
import org.talend.mdm.repository.model.mdmproperties.MDMServerObjectItem;
import org.talend.mdm.repository.model.mdmserverobject.MDMServerObject;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;
import org.talend.mdm.repository.utils.ValidateUtil;
import org.talend.repository.model.IProxyRepositoryFactory;

import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class RenameObjectAction extends AbstractRepositoryAction {

    static Logger log = Logger.getLogger(RenameObjectAction.class);

    IProxyRepositoryFactory factory = CoreRuntimePlugin.getInstance().getProxyRepositoryFactory();

    /**
     * DOC hbhong RemoveFromRepositoryAction constructor comment.
     * 
     * @param text
     */
    public RenameObjectAction() {
        super(""); //$NON-NLS-1$
        setImageDescriptor(ImageCache.getImage(EImage.RENAME.getPath()));
    }

    @Override
    public String getGroupName() {
        return GROUP_EDIT;
    }

    @Override
    public String getText() {
        return Messages.RenameObjectAction_rename;
    }

    @Override
    protected boolean needValidateLockedObject() {
        return true;
    }

    @Override
    protected void doRun() {
        Object obj = getSelectedObject().get(0);
        if (obj instanceof IRepositoryViewObject) {
            IRepositoryViewObject viewObj = (IRepositoryViewObject) obj;

            MDMServerObjectItem item = (MDMServerObjectItem) viewObj.getProperty().getItem();
            MDMServerObject serverObject = item.getMDMServerObject();
            IRepositoryNodeConfiguration configuration = RepositoryNodeConfigurationManager.getConfiguration(item);
            if (configuration != null) {
                ERepositoryObjectType type = configuration.getResourceProvider().getRepositoryObjectType(item);
                IRepositoryViewObject parentViewObj = ContainerCacheService.get(type, item.getState().getPath());

                try {
                    if (serverObject != null) {
                        String oldName = serverObject.getName();
                        String newName = showRenameDlg(type, (ContainerItem) parentViewObj.getProperty().getItem(), oldName);
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
                    }
                    commonViewer.refresh(obj);
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

    private String showRenameDlg(final ERepositoryObjectType type, final ContainerItem parentItem, final String originalName) {

        CustomInputDialog dlg = new CustomInputDialog(getShell(), Messages.RenameObjectAction_rename, Messages.Common_rename, originalName,
                null);
        dlg.setBlockOnOpen(true);
        dlg.create();
        dlg.setValidator(new IInputValidator() {

                    @Override
                    public String isValid(String newText) {
                        if (newText == null || newText.trim().length() == 0) {
                            return Messages.Common_nameCanNotBeEmpty;
                        }

                        if (type.equals(IServerObjectRepositoryType.TYPE_TRANSFORMERV2)
                                || type.equals(IServerObjectRepositoryType.TYPE_VIEW)) {
                            if (!ValidateUtil.matchViewProcessRegex(newText)) {
                                return Messages.Common_nameInvalid;
                            }
                        } else if (type.equals(IServerObjectRepositoryType.TYPE_ROLE)) {
                            if (!ValidateUtil.matchRoleRegex(newText)) {
                                return Messages.shouldNotBeSystemRoleName;
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
        if (dlg.open() == Window.CANCEL) {
            return null;
        }
        return dlg.getValue();

    }

    @Override
    public boolean isVisible(IRepositoryViewObject viewObj) {
        if (getSelectedObject().size() == 1) {
            if (RepositoryResourceUtil.isSystemViewObject(viewObj)) {
                return false;
            }
            return true;
        }
        return false;
    }

    class CustomInputDialog extends InputDialog {

        private IInputValidator validator;
        public CustomInputDialog(Shell parentShell, String dialogTitle, String dialogMessage, String initialValue,
                IInputValidator validator) {
            super(parentShell, dialogTitle, dialogMessage, initialValue, validator);
            this.validator = validator;
        }

        @Override
        protected void validateInput() {
            String errorMessage = null;
            if (getValidator() != null) {
                errorMessage = getValidator().isValid(getText().getText());
            }
            setErrorMessage(errorMessage);
        }

        public void setValidator(IInputValidator validator) {
            this.validator = validator;
            validateInput();
        }

        @Override
        protected IInputValidator getValidator() {
            return validator;
        }
    }
}
