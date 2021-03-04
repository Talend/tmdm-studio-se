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

import java.util.ArrayList;
import java.util.List;

public class XPathFunc {

    String category;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    List<KeyValue> funcs = new ArrayList<KeyValue>();

    public List<KeyValue> getFuncs() {
        return funcs;
    }

    public void setFuncs(List<KeyValue> funcs) {
        this.funcs = funcs;
    }

}
