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

public abstract class SimpleTypeFacetInfo<T> {

    protected String propName = "";//$NON-NLS-1$

    protected T propValue;

    protected SimpleTypeFacetInfo(String propName, T propValue) {
        this.propName = propName;
        this.propValue = propValue;
    }

    public String getPropertyName() {
        return propName;
    }

    public String getPropertyValueLabel() {
        return getValueLabel(propValue);
    }

    public T getPropertyValue() {
        return propValue;
    }

    public void setPropertyValue(T value) {

        if (value == null)
            return;

        propValue = value;
    }

    public void setPropretyValueByLabel(String label) {

        T parsedValue = parseLabel(label);

        propValue = parsedValue;

    }

    protected abstract String getValueLabel(T value);

    protected abstract T parseLabel(String label);

}
