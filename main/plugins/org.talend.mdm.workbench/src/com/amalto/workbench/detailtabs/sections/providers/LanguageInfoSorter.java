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

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import com.amalto.workbench.detailtabs.sections.model.annotationinfo.langinfo.LanguageInfo;

public class LanguageInfoSorter extends ViewerSorter {

    @Override
    public int compare(Viewer viewer, Object e1, Object e2) {

        if (e1 instanceof LanguageInfo && e2 instanceof LanguageInfo) {
            return ((LanguageInfo) e1).getLanguage().compareTo(((LanguageInfo) e2).getLanguage());
        }

        return super.compare(viewer, e1, e2);
    }

}
