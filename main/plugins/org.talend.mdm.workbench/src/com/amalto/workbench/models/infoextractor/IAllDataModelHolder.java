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
package com.amalto.workbench.models.infoextractor;

import org.eclipse.xsd.XSDSchema;

public interface IAllDataModelHolder {

    public String[] getAllDataModelNames();

    public boolean hasDataModel();

    public XSDSchema getDataModel(String dataModelName);

    public String getDefaultDataModel();

    public String getDefaultEntity();
}
