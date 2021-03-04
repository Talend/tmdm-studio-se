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
package org.talend.mdm.repository.core.validate.datamodel.validator.rule;

/**
 * created by HHB on 2013-1-11 Detailled comment
 *
 */
public interface IComponentValidationRule {

    public static final int SEV_ERROR = 2;

    public static final int SEV_WARNING = 4;

    // to mark the error is caused by UNKNOW
    public static final int MSG_GROUP_UNKNOW = 0;

    // to mark the error is caused by Entity
    public static final int MSG_GROUP_ENTITY = 1;

    // to mark the error is caused by Element
    public static final int MSG_GROUP_ELEMENT = 2;

    // to mark the error is caused by Type
    public static final int MSG_GROUP_TYPE = 4;

    public String[] getMessageKeys();

}
