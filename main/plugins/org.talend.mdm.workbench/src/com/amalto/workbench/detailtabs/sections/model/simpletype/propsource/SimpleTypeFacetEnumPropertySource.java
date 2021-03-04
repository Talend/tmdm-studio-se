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

import org.eclipse.swt.widgets.Composite;

import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.utils.IConstants;

public class SimpleTypeFacetEnumPropertySource extends SimpleTypeFacetListStrPropertySource {

    public static final String DELIMITER = " ; ";//$NON-NLS-1$

    public SimpleTypeFacetEnumPropertySource(Composite cellEditorParent, String[] values) {
        super(cellEditorParent, IConstants.SIMPLETYPE_FACETNAME_ENUM, values, DELIMITER);
    }

    @Override
    protected String getCellEditorDialogInputAreaLabel() {
        return Messages.SimpleTypeFacetEnumPropertySource_Enumeration;
    }

    @Override
    protected String getCellEditorDialogTitle() {
        return Messages.SimpleTypeFacetEnumPropertySource_SimpleTypeEnumeration;
    }
}
