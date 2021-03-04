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
package com.amalto.workbench.widgets.composites.property;
import org.eclipse.jface.viewers.CellEditor;

import com.amalto.workbench.providers.CellEditorValueExtractor;
import com.amalto.workbench.providers.CellEditorValueModifier;


public interface IPropertySource<T> {

    public String getPropertyName();

    public String getPropertyValueLabel();

    public void setPropertyValueByLabel(String label);

    public T getPropertyValue();

    public CellEditor getCellEditor();

    public CellEditorValueExtractor<IPropertySource<T>> getCellEditorValueExtractor();

    public CellEditorValueModifier<IPropertySource<T>> getCellEditorValueModifier();
}
