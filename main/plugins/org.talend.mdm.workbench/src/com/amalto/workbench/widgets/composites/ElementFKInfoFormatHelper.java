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
package com.amalto.workbench.widgets.composites;

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
 * created by liusongbo on Sep 9, 2015
 *
 */
public class ElementFKInfoFormatHelper {

    private static final int VERTICAL_RULER_WIDTH = 10;

    public static IOverviewRuler createOverviewRuler() {
        IOverviewRuler ruler = new OverviewRuler(getAnnotationAccess(), VERTICAL_RULER_WIDTH, getSharedColors());

        Iterator<?> e = getAnnotationPreferences().getAnnotationPreferences().iterator();
        while (e.hasNext()) {
            AnnotationPreference preference = (AnnotationPreference) e.next();
            if (preference.contributesToHeader()) {
                ruler.addHeaderAnnotationType(preference.getAnnotationType());
            }
        }
        return ruler;
    }

    static IAnnotationAccess getAnnotationAccess() {
        return new DefaultMarkerAnnotationAccess();
    }

    static ISharedTextColors getSharedColors() {
        return EditorsUI.getSharedTextColors();
    }

    private static MarkerAnnotationPreferences fAnnotationPreferences;

    private static MarkerAnnotationPreferences getAnnotationPreferences() {
        if (fAnnotationPreferences == null) {
            fAnnotationPreferences = new MarkerAnnotationPreferences();
            // force init
            fAnnotationPreferences.getAnnotationPreferences();
        }
        return fAnnotationPreferences;
    }

    public static IVerticalRuler createVerticalRuler() {
        VerticalRuler verticalRuler = new VerticalRuler(VERTICAL_RULER_WIDTH, getAnnotationAccess());
        return verticalRuler;
    }
}
