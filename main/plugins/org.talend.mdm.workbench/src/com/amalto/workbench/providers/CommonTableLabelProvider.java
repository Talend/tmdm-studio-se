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
package com.amalto.workbench.providers;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

public class CommonTableLabelProvider<T> implements ITableLabelProvider {

    @SuppressWarnings("unchecked")
    private ColumnTextExtractor<T>[] colTxtExtractors = new ColumnTextExtractor[0];

    public CommonTableLabelProvider(ColumnTextExtractor<T>[] colTxtExtractors) {

        if (colTxtExtractors != null)
            this.colTxtExtractors = colTxtExtractors;
    }

    public void addListener(ILabelProviderListener listener) {

    }

    public void dispose() {
    }

    public boolean isLabelProperty(Object element, String property) {
        return false;
    }

    public void removeListener(ILabelProviderListener listener) {
    }

    public Image getColumnImage(Object element, int columnIndex) {
        return null;
    }

    @SuppressWarnings("unchecked")
    public String getColumnText(Object element, int columnIndex) {

        if (element == null || colTxtExtractors.length <= columnIndex)
            return "";//$NON-NLS-1$

        try {
            return colTxtExtractors[columnIndex].getColText((T) element);
        } catch (Exception e) {
            return "";//$NON-NLS-1$
        }
    }

}
