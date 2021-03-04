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
package com.amalto.workbench.widgets.xmlviewer;

import java.util.Iterator;

import org.eclipse.jface.text.source.IAnnotationAccess;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.ISharedTextColors;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.OverviewRuler;
import org.eclipse.jface.text.source.VerticalRuler;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.texteditor.AnnotationPreference;
import org.eclipse.ui.texteditor.DefaultMarkerAnnotationAccess;
import org.eclipse.ui.texteditor.MarkerAnnotationPreferences;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class XMLSourceViewerHelper {

    private MarkerAnnotationPreferences fAnnotationPreferences;

    private static XMLSourceViewerHelper instance = new XMLSourceViewerHelper();

    private static final int VERTICAL_RULER_WIDTH = 10;

    private IAnnotationAccess fAnnotationAccess;

    private XMLSourceViewerHelper() {
    }

    public static XMLSourceViewerHelper getInstance() {
        return instance;
    }

    public IVerticalRuler createVerticalRuler() {
        IVerticalRuler ruler = new VerticalRuler(VERTICAL_RULER_WIDTH, getAnnotationAccess());
        return ruler;
    }

    protected IAnnotationAccess createAnnotationAccess() {
        return new DefaultMarkerAnnotationAccess();
    }

    public IAnnotationAccess getAnnotationAccess() {
        if (fAnnotationAccess == null) {
            fAnnotationAccess = createAnnotationAccess();
        }
        return fAnnotationAccess;
    }

    private MarkerAnnotationPreferences getAnnotationPreferences() {
        if (fAnnotationPreferences == null) {
            fAnnotationPreferences = new MarkerAnnotationPreferences();
            // force init
            fAnnotationPreferences.getAnnotationPreferences();
        }
        return fAnnotationPreferences;
    }

    public IOverviewRuler createOverviewRuler() {
        IOverviewRuler ruler = new OverviewRuler(getAnnotationAccess(), VERTICAL_RULER_WIDTH, getSharedColors());

        Iterator e = getAnnotationPreferences().getAnnotationPreferences().iterator();
        while (e.hasNext()) {
            AnnotationPreference preference = (AnnotationPreference) e.next();
            if (preference.contributesToHeader()) {
                ruler.addHeaderAnnotationType(preference.getAnnotationType());
            }
        }
        return ruler;
    }

    public ISharedTextColors getSharedColors() {
        return EditorsUI.getSharedTextColors();
    }
}
