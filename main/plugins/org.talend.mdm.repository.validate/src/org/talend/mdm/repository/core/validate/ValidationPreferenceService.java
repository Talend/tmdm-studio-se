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
package org.talend.mdm.repository.core.validate;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.PlatformUI;
import org.talend.mdm.repository.core.service.IModelValidationService;
import org.talend.mdm.repository.ui.preferences.IValidationPerferenceConstant;

/**
 * created by HHB on 2013-2-1 Detailled comment
 *
 */
public class ValidationPreferenceService implements IValidationPerferenceConstant {

    private static ValidationPreferenceService instance = new ValidationPreferenceService();

    /**
     * Getter for instance.
     *
     * @return the instance
     */
    public static ValidationPreferenceService getInstance() {
        return instance;
    }

    private ValidationPreferenceService() {
    }

    private IPreferenceStore preferenceStore;

    public boolean isShowDlgAfterImmediateChecking() {
        getPreferenceStore().setDefault(SHOW_RESULT_DIALOG_AFTER_IMMEDIATE, true);
        return getPreferenceStore().getBoolean(SHOW_RESULT_DIALOG_AFTER_IMMEDIATE);
    }

    public boolean isShowDlgBeforeDeploying() {
        getPreferenceStore().setDefault(SHOW_RESULT_DIALOG_BEFORE_DEPLOYING, true);
        return getPreferenceStore().getBoolean(SHOW_RESULT_DIALOG_BEFORE_DEPLOYING);
    }

    public void setShowDlgBeforeDeploying(boolean isShowing) {
        getPreferenceStore().setValue(SHOW_RESULT_DIALOG_BEFORE_DEPLOYING, isShowing);
    }

    public int getDeployActionWhenValidateFail() {
        getPreferenceStore().setDefault(DEPLOY_WAY_WHEN_VALIDATE_FAIL, IModelValidationService.BUTTON_SKIP_ERROR);
        return getPreferenceStore().getInt(DEPLOY_WAY_WHEN_VALIDATE_FAIL);
    }

    public void setDeployActionWhenValidateFail(int id) {
        getPreferenceStore().setValue(DEPLOY_WAY_WHEN_VALIDATE_FAIL, id);
    }

    public void setShowDlgAfterImmediateChecking(boolean isShowing) {
        getPreferenceStore().setValue(SHOW_RESULT_DIALOG_AFTER_IMMEDIATE, isShowing);
    }

    public boolean isShowDlgAfterSaving() {
        getPreferenceStore().setDefault(SHOW_RESULT_DIALOG_AFTER_SAVING, true);
        return getPreferenceStore().getBoolean(SHOW_RESULT_DIALOG_AFTER_SAVING);
    }

    public void setShowDlgAfterSaving(boolean isShowing) {
        getPreferenceStore().setValue(SHOW_RESULT_DIALOG_AFTER_SAVING, isShowing);
    }

    private IPreferenceStore getPreferenceStore() {
        if (preferenceStore == null) {
            preferenceStore = PlatformUI.getPreferenceStore();
        }
        return preferenceStore;
    }
}
