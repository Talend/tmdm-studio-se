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

import com.amalto.workbench.detailtabs.sections.model.simpletype.SimpleTypeStringListFacetInfo;
import com.amalto.workbench.providers.CellEditorValueExtractor;
import com.amalto.workbench.providers.CellEditorValueModifier;
import com.amalto.workbench.widgets.celleditor.ListStringContentDialogCellEditor;
import com.amalto.workbench.widgets.composites.property.IPropertySource;

public abstract class SimpleTypeFacetListStrPropertySource extends
        SimpleTypeFacetPropertySource<String[], SimpleTypeStringListFacetInfo> {

    public SimpleTypeFacetListStrPropertySource(Composite cellEditorParent, String propName, String[] values, String delimiter) {
        super(cellEditorParent, new SimpleTypeStringListFacetInfo(propName, values, delimiter));
    }

    @Override
    public CellEditor getCellEditor() {
        return new ListStringContentDialogCellEditor(cellEditorParent, getCellEditorDialogTitle(),
                getCellEditorDialogInputAreaLabel(), facetInfo.getDelimiter());
    }

    @Override
    public CellEditorValueExtractor<IPropertySource<String[]>> getCellEditorValueExtractor() {
        return new CellEditorValueExtractor<IPropertySource<String[]>>("getPropertyValueLabel");//$NON-NLS-1$
    }

    @Override
    public CellEditorValueModifier<IPropertySource<String[]>> getCellEditorValueModifier() {
        return new CellEditorValueModifier<IPropertySource<String[]>>("setPropertyValueByLabel", "getPropertyValueLabel");//$NON-NLS-1$//$NON-NLS-2$
    }

    protected abstract String getCellEditorDialogTitle();

    protected abstract String getCellEditorDialogInputAreaLabel();
}
