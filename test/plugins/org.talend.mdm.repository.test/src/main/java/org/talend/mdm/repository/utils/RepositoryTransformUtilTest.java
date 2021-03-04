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
package org.talend.mdm.repository.utils;

import static org.junit.Assert.*;

import org.junit.Test;
import org.talend.mdm.repository.core.impl.transformerV2.ITransformerV2NodeConsDef;
import org.talend.mdm.repository.core.impl.view.IViewNodeConstDef;
import org.talend.mdm.repository.i18n.Messages;


public class RepositoryTransformUtilTest {

    @Test
    public void testTransformToSilyViewName() {
        assertNull(RepositoryTransformUtil.getInstance().transformToSilyViewName(null, false));
        assertNull(RepositoryTransformUtil.getInstance().transformToSilyViewName(null, true));
        assertEquals("", RepositoryTransformUtil.getInstance().transformToSilyViewName("", false)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("", RepositoryTransformUtil.getInstance().transformToSilyViewName("", true)); //$NON-NLS-1$ //$NON-NLS-2$

        String viewName = "Browse_items_Product#Stores"; //$NON-NLS-1$
        String transformedSilyViewName = RepositoryTransformUtil.getInstance().transformToSilyViewName(viewName, false);
        assertEquals("Product (Stores)", transformedSilyViewName); //$NON-NLS-1$
        transformedSilyViewName = RepositoryTransformUtil.getInstance().transformToSilyViewName(viewName, true);
        assertEquals("Product (Stores) [Web Filter]", transformedSilyViewName); //$NON-NLS-1$

        viewName = "Browse_items_ProductFamily"; //$NON-NLS-1$
        transformedSilyViewName = RepositoryTransformUtil.getInstance().transformToSilyViewName(viewName, false);
        assertEquals("ProductFamily", transformedSilyViewName); //$NON-NLS-1$
        transformedSilyViewName = RepositoryTransformUtil.getInstance().transformToSilyViewName(viewName, true);
        assertEquals("ProductFamily [Web Filter]", transformedSilyViewName); //$NON-NLS-1$

        viewName = "Product_family"; //$NON-NLS-1$
        transformedSilyViewName = RepositoryTransformUtil.getInstance().transformToSilyViewName(viewName, false);
        assertEquals("Product_family", transformedSilyViewName); //$NON-NLS-1$
        transformedSilyViewName = RepositoryTransformUtil.getInstance().transformToSilyViewName(viewName, true);
        assertEquals("Product_family [Search Filter]", transformedSilyViewName); //$NON-NLS-1$

        viewName = "Product_family#ssa"; //$NON-NLS-1$
        transformedSilyViewName = RepositoryTransformUtil.getInstance().transformToSilyViewName(viewName, false);
        assertEquals("Product_family#ssa", transformedSilyViewName); //$NON-NLS-1$
        transformedSilyViewName = RepositoryTransformUtil.getInstance().transformToSilyViewName(viewName, true);
        assertEquals("Product_family#ssa [Search Filter]", transformedSilyViewName); //$NON-NLS-1$

    }

    @Test
    public void testTransformToSilyProcessName() {
        assertNull(RepositoryTransformUtil.getInstance().transformToSilyProcessName(null, false));
        assertNull(RepositoryTransformUtil.getInstance().transformToSilyProcessName(null, true));
        assertEquals(" ", RepositoryTransformUtil.getInstance().transformToSilyProcessName(" ", false));
        assertEquals(" ", RepositoryTransformUtil.getInstance().transformToSilyProcessName(" ", true));

        String processName = "beforeSaving_Product"; //$NON-NLS-1$
        String transformedSilyViewName = RepositoryTransformUtil.getInstance().transformToSilyProcessName(processName, false);
        assertEquals("Product", transformedSilyViewName); //$NON-NLS-1$
        transformedSilyViewName = RepositoryTransformUtil.getInstance().transformToSilyProcessName(processName, true);
        assertEquals("Product [" + Messages.TransformerV2XX_BeforeSaving + "]", transformedSilyViewName); //$NON-NLS-1$

        processName = "beforeDeleting_Product"; //$NON-NLS-1$
        transformedSilyViewName = RepositoryTransformUtil.getInstance().transformToSilyProcessName(processName, false);
        assertEquals("Product", transformedSilyViewName); //$NON-NLS-1$
        transformedSilyViewName = RepositoryTransformUtil.getInstance().transformToSilyProcessName(processName, true);
        assertEquals("Product [" + Messages.TransformerV2XX_BeforeDeleting + "]", transformedSilyViewName); //$NON-NLS-1$

        processName = "Runnable_Product"; //$NON-NLS-1$
        transformedSilyViewName = RepositoryTransformUtil.getInstance().transformToSilyProcessName(processName, false);
        assertEquals("Product", transformedSilyViewName); //$NON-NLS-1$
        transformedSilyViewName = RepositoryTransformUtil.getInstance().transformToSilyProcessName(processName, true);
        assertEquals("Product [" + Messages.TransformerV2XX_EntityAction + "]", transformedSilyViewName); //$NON-NLS-1$

        processName = "Runnable#Product"; //$NON-NLS-1$
        transformedSilyViewName = RepositoryTransformUtil.getInstance().transformToSilyProcessName(processName, false);
        assertEquals("Product", transformedSilyViewName); //$NON-NLS-1$
        transformedSilyViewName = RepositoryTransformUtil.getInstance().transformToSilyProcessName(processName, true);
        assertEquals("Product [" + Messages.TransformerV2XX_WelcomeAction + "]", transformedSilyViewName); //$NON-NLS-1$

        processName = "Runnable$Product"; //$NON-NLS-1$
        transformedSilyViewName = RepositoryTransformUtil.getInstance().transformToSilyProcessName(processName, false);
        assertEquals("Product", transformedSilyViewName); //$NON-NLS-1$
        transformedSilyViewName = RepositoryTransformUtil.getInstance().transformToSilyProcessName(processName, true);
        assertEquals("Product [" + Messages.TransformerV2XX_WelcomeAction + "]", transformedSilyViewName); //$NON-NLS-1$

        processName = "Smart_view_Product"; //$NON-NLS-1$
        transformedSilyViewName = RepositoryTransformUtil.getInstance().transformToSilyProcessName(processName, false);
        assertEquals("Product", transformedSilyViewName); //$NON-NLS-1$
        transformedSilyViewName = RepositoryTransformUtil.getInstance().transformToSilyProcessName(processName, true);
        assertEquals("Product [" + Messages.TransformerV2XX_SmartView + "]", transformedSilyViewName); //$NON-NLS-1$

        processName = "Process_Product"; //$NON-NLS-1$
        transformedSilyViewName = RepositoryTransformUtil.getInstance().transformToSilyProcessName(processName, false);
        assertEquals("Process_Product", transformedSilyViewName); //$NON-NLS-1$
        transformedSilyViewName = RepositoryTransformUtil.getInstance().transformToSilyProcessName(processName, true);
        assertEquals("Process_Product [" + Messages.TransformerV2XX_Other + "]", transformedSilyViewName); //$NON-NLS-1$

    }

    @Test
    public void testGetProcessType() {
        assertEquals(-1, RepositoryTransformUtil.getInstance().getProcessType(null));
        assertEquals(-1, RepositoryTransformUtil.getInstance().getProcessType(" ")); //$NON-NLS-1$

        assertEquals(ITransformerV2NodeConsDef.TYPE_BEFORESAVE,
                RepositoryTransformUtil.getInstance().getProcessType("beforeSaving_Product")); //$NON-NLS-1$
        assertEquals(ITransformerV2NodeConsDef.TYPE_BEFOREDEL,
                RepositoryTransformUtil.getInstance().getProcessType("beforeDeleting_Product")); //$NON-NLS-1$
        assertEquals(ITransformerV2NodeConsDef.TYPE_ENTITYACTION,
                RepositoryTransformUtil.getInstance().getProcessType("Runnable_Product")); //$NON-NLS-1$
        assertEquals(ITransformerV2NodeConsDef.TYPE_WELCOMEACTION,
                RepositoryTransformUtil.getInstance().getProcessType("Runnable#Product")); //$NON-NLS-1$
        assertEquals(ITransformerV2NodeConsDef.TYPE_WELCOMEACTION,
                RepositoryTransformUtil.getInstance().getProcessType("Runnable$Product")); //$NON-NLS-1$
        assertEquals(ITransformerV2NodeConsDef.TYPE_SMARTVIEW,
                RepositoryTransformUtil.getInstance().getProcessType("Smart_view_Product")); //$NON-NLS-1$
        assertEquals(ITransformerV2NodeConsDef.TYPE_OTHER, RepositoryTransformUtil.getInstance()
                .getProcessType("Process_Product")); //$NON-NLS-1$
    }

    @Test
    public void testGetProcessPath() {
        assertNull(RepositoryTransformUtil.getInstance().getProcessPath(null, false));
        assertNull(RepositoryTransformUtil.getInstance().getProcessPath(null, true));

        assertEquals(ITransformerV2NodeConsDef.PATH_BEFORESAVE,
                RepositoryTransformUtil.getInstance().getProcessPath("beforeSaving_Product", false)); //$NON-NLS-1$ //$NON-NLS-2$
        String seprator = "/"; //$NON-NLS-1$
        assertEquals(
                seprator + ITransformerV2NodeConsDef.PATH_BEFORESAVE, RepositoryTransformUtil.getInstance().getProcessPath("beforeSaving_Product", true)); //$NON-NLS-1$ //$NON-NLS-2$

        assertEquals(ITransformerV2NodeConsDef.PATH_BEFOREDEL,
                RepositoryTransformUtil.getInstance().getProcessPath("beforeDeleting_Product", false)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(
                seprator + ITransformerV2NodeConsDef.PATH_BEFOREDEL, RepositoryTransformUtil.getInstance().getProcessPath("beforeDeleting_Product", true)); //$NON-NLS-1$ //$NON-NLS-2$

        assertEquals(ITransformerV2NodeConsDef.PATH_ENTITYACTION,
                RepositoryTransformUtil.getInstance().getProcessPath("Runnable_Product", false)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(
                seprator + ITransformerV2NodeConsDef.PATH_ENTITYACTION, RepositoryTransformUtil.getInstance().getProcessPath("Runnable_Product", true)); //$NON-NLS-1$ //$NON-NLS-2$

        assertEquals(ITransformerV2NodeConsDef.PATH_WELCOMEACTION,
                RepositoryTransformUtil.getInstance().getProcessPath("Runnable#Product", false)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(
                seprator + ITransformerV2NodeConsDef.PATH_WELCOMEACTION, RepositoryTransformUtil.getInstance().getProcessPath("Runnable#Product", true)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(ITransformerV2NodeConsDef.PATH_WELCOMEACTION,
                RepositoryTransformUtil.getInstance().getProcessPath("Runnable$Product", false)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(
                seprator + ITransformerV2NodeConsDef.PATH_WELCOMEACTION, RepositoryTransformUtil.getInstance().getProcessPath("Runnable$Product", true)); //$NON-NLS-1$ //$NON-NLS-2$

        assertEquals(ITransformerV2NodeConsDef.PATH_SMARTVIEW,
                RepositoryTransformUtil.getInstance().getProcessPath("Smart_view_Product", false)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(
                seprator + ITransformerV2NodeConsDef.PATH_SMARTVIEW, RepositoryTransformUtil.getInstance().getProcessPath("Smart_view_Product", true)); //$NON-NLS-1$ //$NON-NLS-2$

        assertEquals(ITransformerV2NodeConsDef.PATH_OTHER,
                RepositoryTransformUtil.getInstance().getProcessPath("Process_Product", false)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(
                seprator + ITransformerV2NodeConsDef.PATH_OTHER, RepositoryTransformUtil.getInstance().getProcessPath("Process_Product", true)); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testGetViewType() {
        assertEquals(-1, RepositoryTransformUtil.getInstance().getViewType(null));
        assertEquals(-1, RepositoryTransformUtil.getInstance().getViewType(" "));

        assertEquals(IViewNodeConstDef.TYPE_WEBFILTER, RepositoryTransformUtil.getInstance().getViewType("Browse_items_Product"));
        assertEquals(IViewNodeConstDef.TYPE_WEBFILTER,
                RepositoryTransformUtil.getInstance().getViewType("Browse_items_Product#Stores"));
        assertEquals(IViewNodeConstDef.TYPE_SEARCHFILTER, RepositoryTransformUtil.getInstance().getViewType("Product_family"));
        assertEquals(IViewNodeConstDef.TYPE_SEARCHFILTER, RepositoryTransformUtil.getInstance().getViewType("Product_family#ssa"));
    }

    @Test
    public void testGetViewPath() {
        assertNull(RepositoryTransformUtil.getInstance().getViewPath(null));

        assertEquals(IViewNodeConstDef.PATH_WEBFILTER, RepositoryTransformUtil.getInstance().getViewPath("Browse_items_Product"));
        assertEquals(IViewNodeConstDef.PATH_SEARCHFILTER, RepositoryTransformUtil.getInstance().getViewPath("Product_family"));
    }

}
