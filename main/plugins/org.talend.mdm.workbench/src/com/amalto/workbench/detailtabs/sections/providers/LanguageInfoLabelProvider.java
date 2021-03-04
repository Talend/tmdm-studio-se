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
package com.amalto.workbench.detailtabs.sections.providers;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import com.amalto.workbench.detailtabs.sections.model.annotationinfo.langinfo.LanguageInfo;

public class LanguageInfoLabelProvider implements ITableLabelProvider {

    private static final int COLINDEX_LANG = 0;

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

    public String getColumnText(Object element, int columnIndex) {

        if (element instanceof LanguageInfo) {
            return getColumnTextByIndex((LanguageInfo) element, columnIndex);
        }

        return "";//$NON-NLS-1$
    }

    private String getColumnTextByIndex(LanguageInfo element, int columnIndex) {

        if (columnIndex == COLINDEX_LANG)
            return element.getLanguage();

        return element.getLabel();
    }

}
