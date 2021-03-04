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
package org.talend.mdm.repository.ui.preferences;

public interface PreferenceConstants {

    // auto deploy
    String P_AUTO_DEPLOY = "autoDeploy"; //$NON-NLS-1$

    String P_WARN_USER_AUTO_DEPLOY = "warnUserBeforeAutoDeploy"; //$NON-NLS-1$

    // switch perspective
    String P_AUTO_SWITCH_TO_DI = "autoSwitchToDI"; //$NON-NLS-1$

    String P_AUTO_SWITCH_TO_BONITA = "autoSwitchToBonita"; //$NON-NLS-1$

    String P_NOT_ASK_AUTO_SWITCH_TO_DI = "notAskAutoSwitchToDI"; //$NON-NLS-1$

    String P_NOT_ASK_AUTO_SWITCH_TO_BONITA = "notAskAutoSwitchToBonita"; //$NON-NLS-1$

    // Consistency Conflict
    String P_WARN_USER_HAS_CONFLICT = "warnUserWhenHasConflict"; //$NON-NLS-1$

    String P_CONFLICT_STRATEGY = "conflictStrategy"; //$NON-NLS-1$
}
