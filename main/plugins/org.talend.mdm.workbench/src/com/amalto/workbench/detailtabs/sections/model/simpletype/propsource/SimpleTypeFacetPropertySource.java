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
package com.amalto.workbench.detailtabs.sections.model.simpletype.propsource;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;

import com.amalto.workbench.detailtabs.sections.model.simpletype.SimpleTypeFacetInfo;
import com.amalto.workbench.providers.CellEditorValueExtractor;
import com.amalto.workbench.providers.CellEditorValueModifier;
import com.amalto.workbench.widgets.composites.property.IPropertySource;

public abstract class SimpleTypeFacetPropertySource<VALUETYPE, FACETINFOTYPE extends SimpleTypeFacetInfo<VALUETYPE>> implements
        IPropertySource<VALUETYPE> {

    protected FACETINFOTYPE facetInfo;

    protected Composite cellEditorParent;

    public SimpleTypeFacetPropertySource(Composite cellEditorParent, FACETINFOTYPE facetInfo) {
        this.cellEditorParent = cellEditorParent;
        this.facetInfo = facetInfo;
    }

    public String getPropertyName() {
        return facetInfo.getPropertyName();
    }

    public String getPropertyValueLabel() {
        return facetInfo.getPropertyValueLabel();
    }

    public VALUETYPE getPropertyValue() {
        return facetInfo.getPropertyValue();
    }

    public void setPropertyValueByLabel(String label) {
        facetInfo.setPropretyValueByLabel(label);
    }

    public abstract CellEditor getCellEditor();

    public abstract CellEditorValueExtractor<IPropertySource<VALUETYPE>> getCellEditorValueExtractor();

    public abstract CellEditorValueModifier<IPropertySource<VALUETYPE>> getCellEditorValueModifier();

}
