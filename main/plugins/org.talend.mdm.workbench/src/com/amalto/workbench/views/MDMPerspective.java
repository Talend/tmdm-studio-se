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
package com.amalto.workbench.views;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import com.amalto.workbench.availablemodel.AvailableModelUtil;

public class MDMPerspective implements IPerspectiveFactory {

    public static final String PERPECTIVE_ID = "org.talend.mdm.perspective";//$NON-NLS-1$

    public static final String VIEWID_PROPERTYVIEW = "org.eclipse.ui.views.PropertySheet";//$NON-NLS-1$

    public static final String VIEWID_OUTLINE = "org.eclipse.ui.views.ContentOutline";//$NON-NLS-1$

    static {
        // scan availabelmodel extention point
        AvailableModelUtil.getAvailableModels();
    }

    public void createInitialLayout(IPageLayout layout) {
        // from 5.0 m4 it will disappear from perspective
        // leftTopLayout
        //        IFolderLayout leftTopLayout = layout.createFolder("navigatorLayout", IPageLayout.LEFT, new Float(0.32),//$NON-NLS-1$
        // IPageLayout.ID_EDITOR_AREA);

        // leftTopLayout.addView(ServerView.VIEW_ID);

        // layout.addPlaceholder(VIEWID_PROPERTYVIEW, IPageLayout.BOTTOM, 0.7f, IPageLayout.ID_EDITOR_AREA);

        layout.addPlaceholder(VIEWID_OUTLINE, IPageLayout.RIGHT, 0.7f, IPageLayout.ID_EDITOR_AREA);

        layout.setEditorAreaVisible(true);

    }

}
