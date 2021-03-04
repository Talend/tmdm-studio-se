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
package com.amalto.workbench.service;

import org.talend.core.IService;
import org.talend.core.model.repository.ERepositoryObjectType;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public interface IValidateService extends IService {

    public static final int STATUS_OK = 0;

    public static final int STATUS_EXISTED = 1;

    public static final int STATUS_DELETED = 2;

    /**
     * DOC HHB Comment method "validateObjectExistence".
     *
     * @param type type defined in TreeObject
     * @param name
     * @return STATUS_XXX, defined in current interface
     */
    public int validateObjectExistence(int type, String name);

    public int validateObjectExistence(ERepositoryObjectType type, String name);

    public boolean validateAndAlertObjectExistence(ERepositoryObjectType type, String name, String typeName);

    public boolean validateAndAlertObjectExistence(int type, String name);

}
