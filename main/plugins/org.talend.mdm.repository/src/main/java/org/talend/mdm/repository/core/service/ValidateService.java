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
package org.talend.mdm.repository.core.service;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;

import com.amalto.workbench.service.IValidateService;

/**
 * created by HHB on 2012-12-6 Detailled comment
 * 
 */
public class ValidateService implements IValidateService {

    /*
     * (non-Javadoc)
     * 
     * @see com.amalto.workbench.service.IValidateService#validateObjectExistence(int, java.lang.String)
     */
    public int validateObjectExistence(int type, String name) {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        ERepositoryObjectType objectType = RepositoryQueryService.getRepositoryObjectType(type);
        return validateObjectExistence(objectType, name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.amalto.workbench.service.IValidateService#validateObjectExistence(org.talend.core.model.repository.
     * ERepositoryObjectType, java.lang.String)
     */
    public int validateObjectExistence(ERepositoryObjectType objectType, String name) {
        if (objectType != null && name != null) {
            IRepositoryViewObject viewObject = RepositoryResourceUtil.findViewObjectByName(objectType, name);
            if (viewObject != null) {
                Property property = viewObject.getProperty();
                if (property != null) {
                    Item item = property.getItem();
                    if (item != null) {
                        if (item.getState().isDeleted()) {
                            return STATUS_DELETED;
                        } else {
                            return STATUS_EXISTED;
                        }
                    }
                }
            }
        } else {
            throw new IllegalArgumentException("Wrong treeObject type"); //$NON-NLS-1$
        }
        return STATUS_OK;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.amalto.workbench.service.IValidateService#validateAndAlertObjectExistence(int, java.lang.String)
     */
    public boolean validateAndAlertObjectExistence(int type, String name) {
        int result = validateObjectExistence(type, name);
        ERepositoryObjectType objectType = RepositoryQueryService.getRepositoryObjectType(type);
        return doValidateAndAlertObjectExistence(result, name, objectType, null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.amalto.workbench.service.IValidateService#validateAndAlertObjectExistence(org.talend.core.model.repository
     * .ERepositoryObjectType, java.lang.String)
     */
    public boolean validateAndAlertObjectExistence(ERepositoryObjectType type, String name, String typeName) {
        int result = validateObjectExistence(type, name);
        return doValidateAndAlertObjectExistence(result, name, type, typeName);
    }

    private boolean doValidateAndAlertObjectExistence(int result, String name, ERepositoryObjectType objectType, String typeName) {
        if (result == STATUS_OK) {
            return true;
        } else {
            String message = null;
            if (typeName == null) {
                typeName = objectType.getLabel();
            }
            if (result == STATUS_EXISTED) {
                message = Messages.bind(Messages.ValidateService_ObjectExist, name, typeName);
            } else if (result == STATUS_DELETED) {
                message = Messages.bind(Messages.ValidateService_DeletedObjectExist, name, typeName);
            }
            return MessageDialog.openConfirm(getShell(), Messages.Confirm_Overwrite, message);
        }
    }

    private Shell getShell() {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window != null) {
            return window.getShell();
        }
        return null;
    }
}
