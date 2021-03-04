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
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.amalto.workbench.detailtabs.sections.model.simpletype.SimpleTypeIntegerFacetInfo;
import com.amalto.workbench.providers.CellEditorValueExtractor;
import com.amalto.workbench.providers.CellEditorValueModifier;
import com.amalto.workbench.widgets.celleditor.NumbericCellEditor;
import com.amalto.workbench.widgets.composites.property.IPropertySource;

public class SimpleTypeFacetIntegerPropertySource extends SimpleTypeFacetPropertySource<Integer, SimpleTypeIntegerFacetInfo> {

    public SimpleTypeFacetIntegerPropertySource(Composite cellEditorParent, String propName, Integer value) {
        super(cellEditorParent, new SimpleTypeIntegerFacetInfo(propName, value));
    }

    @Override
    public CellEditor getCellEditor() {
        return NumbericCellEditor.createPositiveNumberCellEditor(cellEditorParent, SWT.BORDER);
    }

    @Override
    public CellEditorValueExtractor<IPropertySource<Integer>> getCellEditorValueExtractor() {
        return new CellEditorValueExtractor<IPropertySource<Integer>>("getPropertyValueLabel");//$NON-NLS-1$
    }

    @Override
    public CellEditorValueModifier<IPropertySource<Integer>> getCellEditorValueModifier() {
        return new CellEditorValueModifier<IPropertySource<Integer>>("setPropertyValueByLabel", "getPropertyValueLabel");//$NON-NLS-1$//$NON-NLS-2$
    }

}
