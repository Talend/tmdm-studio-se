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
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.amalto.workbench.detailtabs.sections.model.simpletype.SimpleTypeStringFacetInfo;
import com.amalto.workbench.providers.CellEditorValueExtractor;
import com.amalto.workbench.providers.CellEditorValueModifier;
import com.amalto.workbench.utils.XSDUtil;
import com.amalto.workbench.widgets.composites.property.IPropertySource;

public class SimpleTypeFacetDatePropertySource extends SimpleTypeFacetPropertySource<String, SimpleTypeStringFacetInfo> {

    private int validateKey;

    public SimpleTypeFacetDatePropertySource(Composite cellEditorParent, String propName, String value, int validateKey) {
        super(cellEditorParent, new SimpleTypeStringFacetInfo(propName, value));
        this.validateKey = validateKey;
    }

    @Override
    public CellEditor getCellEditor() {
        return new TextCellEditor(cellEditorParent, SWT.BORDER);
    }

    @Override
    public CellEditorValueExtractor<IPropertySource<String>> getCellEditorValueExtractor() {
        return new CellEditorValueExtractor<IPropertySource<String>>("getPropertyValueLabel");//$NON-NLS-1$
    }

    @Override
    public CellEditorValueModifier<IPropertySource<String>> getCellEditorValueModifier() {
        return new DateCellEditorValueModifier<IPropertySource<String>>(validateKey, "setPropertyValueByLabel", //$NON-NLS-1$
                "getPropertyValueLabel");//$NON-NLS-1$
    }

    class DateCellEditorValueModifier<T> extends CellEditorValueModifier<T> {

        private int validateKey;

        public DateCellEditorValueModifier(int validateKey, String infoSetMethod, String infoGetMethod) {
            super(infoSetMethod, infoGetMethod);
            this.validateKey = validateKey;
        }

        @Override
        protected Object translateNewValueToModelAcceptable(Object newValue) {
            String newInput = (String) super.translateNewValueToModelAcceptable(newValue);
            newInput = newInput.trim();
            boolean match = XSDUtil.isValidatedXSDDate(validateKey, newInput);
            return match ? newInput : null;
        }

    }
}
