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
package org.talend.mdm.repository.core.validate.i18n;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    private static final String BUNDLE_NAME = "org.talend.mdm.repository.core.validate.i18n.messages"; //$NON-NLS-1$

    // //////////////////////////////////////////////////////////////////////////
    //
    // Constructor
    //
    // //////////////////////////////////////////////////////////////////////////
    private Messages() {
        // do not instantiate
    }

    public static String bind(String message, String... bindings) {
        return NLS.bind(message, bindings);
    }

    // //////////////////////////////////////////////////////////////////////////
    //
    // Class initialization
    //
    // //////////////////////////////////////////////////////////////////////////
    static {
        // load message values from bundle file
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    public static String FKInfoElementVisitor_MK_NOT_EXIST;

    public static String FKInfoElementVisitor_MK_NOT_TYPE_STRING;

    public static String MDMValidationRunner_promptToSaveEditors;

    public static String ModelNameMarkerGroup_modelName;

    public static String ModelNameMarkerGroup_Uncategorized;

    public static String PKInfoElementVisitor_MK_FIELD_NOT_DISPLAY_TYPE;

    public static String PKInfoElementVisitor_MK_FIELD_NOT_LIST;

    public static String PKInfoElementVisitor_MK_NOT_EXIST;

    public static String PKInfoElementVisitor_MK_NOT_IN_SAME_ENTITY;

    public static String ValidationPreferencePage_Before_Deplying_Text;

    public static String ValidationPreferencePage_Before_Deplying_Title;

    public static String ValidationPreferencePage_CancelButton_Text;

    public static String ValidationPreferencePage_DeplyGroup_Text;

    public static String ValidationPreferencePage_SavingButton_Text;

    public static String ValidationPreferencePage_SavingDialog_Title;

    public static String ValidationPreferencePage_SkipErrorsAndWarningsButton_Text;

    public static String ValidationPreferencePage_SkipErrorsButton_Text;

    public static String ValidationPrefPage_Button_Text;

    public static String ValidationPrefPage_RightClick_Title;

    public static String ValidationResultDialog_Message;

    public static String ValidationResultDialog_NotShowThis;

    public static String ValidationResultDialog_skipAllErrs;

    public static String ValidationResultDialog_skipAllErrWarnings;

    public static String ValidationResultDialog_Title;

    public static String ViewValidator_EmptyNullOperatorValue_error;

    public static String ViewValidator_error;

}
