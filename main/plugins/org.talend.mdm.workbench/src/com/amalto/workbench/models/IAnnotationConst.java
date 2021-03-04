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
package com.amalto.workbench.models;

/**
 * created by HHB on 2017-7-12 Detailled comment
 *
 */
public interface IAnnotationConst {

    String KEY_WRITE = "X_Write"; //$NON-NLS-1$

    String KEY_NO_ACESS = "X_Hide"; //$NON-NLS-1$

    String KEY_NO_CREATE = "X_Deny_Create"; //$NON-NLS-1$

    String KEY_NO_LOGIC_DELETE = "X_Deny_LogicalDelete"; //$NON-NLS-1$

    String KEY_NO_PHYSICAL_DELETE = "X_Deny_PhysicalDelete"; //$NON-NLS-1$

    String KEY_NO_ADD = "X_No_Add"; //$NON-NLS-1$

    String KEY_NO_REMOVE = "X_No_Remove"; //$NON-NLS-1$

    String KEY_CATEGORY = "X_Category";

    //
    String KEY_PK_INFO = "X_PrimaryKeyInfo"; //$NON-NLS-1$

    String KEY_WORKFLOW = "X_Workflow"; //$NON-NLS-1$

    String KEY_AUTO_EXPAND = "X_AutoExpand"; //$NON-NLS-1$

    String KEY_LOOKUP_FIELD = "X_Lookup_Field"; //$NON-NLS-1$

    String KEY_VALIDATION_RULE = "X_Schematron"; //$NON-NLS-1$

    String KEY_VISIBLE_RULE = "X_Visible_Rule"; //$NON-NLS-1$

    String KEY_DEFAULT_VALUE = "X_Default_Value_Rule"; //$NON-NLS-1$

    String KEY_SOURCE_SYSTEM = "X_SourceSystem"; //$NON-NLS-1$

    String KEY_TARGET_SYSTEM = "X_TargetSystem"; //$NON-NLS-1$

    String KEY_LABEL = "X_Label"; //$NON-NLS-1$
    // foreign key
    String KEY_FOREIGN_KEY = "X_ForeignKey"; //$NON-NLS-1$

    String KEY_FOREIGN_KEY_INFO = "X_ForeignKeyInfo"; //$NON-NLS-1$

    String KEY_FOREIGN_KEY_FILTER = "X_ForeignKey_Filter"; //$NON-NLS-1$

    String KEY_FOREIGN_KEY_INTEGRITY_OVERRIDE = "X_FKIntegrity_Override"; //$NON-NLS-1$

    String KEY_FOREIGN_KEY_INTEGRITY = "X_FKIntegrity"; //$NON-NLS-1$

    String KEY_FOREIGN_KEY_NOT_SEP = "X_ForeignKey_NotSep"; //$NON-NLS-1$

    String KEY_FOREIGN_KEY_INFO_FORMAT = "X_ForeignKeyInfoFormat"; //$NON-NLS-1$

    String KEY_RETRIEVE_FK_INFOS = "X_Retrieve_FKinfos"; //$NON-NLS-1$

    // prefix
    String KEY_PREFIX_FACET = "X_Facet_"; //$NON-NLS-1$

    String KEY_PREFIX_LABEL = "X_Label_"; //$NON-NLS-1$

    String KEY_PREFIX_DESCRIPTION = "X_Description_"; //$NON-NLS-1$

    String KEY_PREFIX_DISPLAY_FORMAT = "X_Display_Format_"; //$NON-NLS-1$

    String KEY_PREFIX_FOREIGN_KEY_INFO = "X_ForeignKeyInfo_"; //$NON-NLS-1$

    String KEY_PREFIX_NO_ACESS = "X_Hide_"; //$NON-NLS-1$
    // regex

    String KEY_REGEX_LABEL = "X_Label_.*"; //$NON-NLS-1$

    String KEY_REGEX_DESCRIPTION = "X_Description_.*"; //$NON-NLS-1$
}
