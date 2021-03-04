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
package com.amalto.workbench.detailtabs.sections.model.simpletype;

public class SimpleTypeStringFacetInfo extends SimpleTypeFacetInfo<String> {

    public SimpleTypeStringFacetInfo(String propName, String propValue) {
        super(propName, propValue);
    }

    @Override
    protected String getValueLabel(String value) {
        return value;
    }

    @Override
    protected String parseLabel(String label) {
        return label;
    }

}
