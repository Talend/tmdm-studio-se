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
package com.amalto.workbench.compare;

import com.amalto.workbench.models.TreeObject;

public class CompareHeadInfo {

    private TreeObject xobject;

    private boolean isItem;

    private String dataModelName;

    public CompareHeadInfo(TreeObject xobject) {
        super();
        this.xobject = xobject;
    }

    public TreeObject getXobject() {
        return xobject;
    }

    public void setXobject(TreeObject xobject) {
        this.xobject = xobject;
    }

    public boolean isItem() {
        return isItem;
    }

    public void setItem(boolean isItem) {
        this.isItem = isItem;
    }

    public String getDataModelName() {
        return dataModelName;
    }

    public void setDataModelName(String dataModelName) {
        this.dataModelName = dataModelName;
    }

}
