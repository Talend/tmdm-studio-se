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

public class SimpleTypeDoubleFacetInfo extends SimpleTypeFacetInfo<Double> {

    public SimpleTypeDoubleFacetInfo(String propName, Double propValue) {
        super(propName, propValue);
    }

    @Override
    protected String getValueLabel(Double value) {
        if (value == null) {
            return "";
        }
        return value.toString();
    }

    @Override
    protected Double parseLabel(String label) {
        if (label.trim().isEmpty()) {
            return null;
        }

        Double result = null;

        try {
            result = Double.parseDouble(label);
        } catch (Exception e) {
            return this.propValue;
        }

        return result;
    }

}
