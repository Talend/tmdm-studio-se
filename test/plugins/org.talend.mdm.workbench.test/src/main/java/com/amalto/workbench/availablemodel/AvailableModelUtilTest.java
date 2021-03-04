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

package com.amalto.workbench.availablemodel;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;


public class AvailableModelUtilTest {

    @Test
    public void testGetAvailableModels() {
        List<IAvailableModel> availableModels = AvailableModelUtil.getAvailableModels();
        assertNotNull(availableModels);
        assertEquals(11, availableModels.size());
    }

    @Test
    public void testGetAvailableModelsBoolean() {
        List<IAvailableModel> availableModels = AvailableModelUtil.getAvailableModels(true);
        assertNotNull(availableModels);
        assertEquals(6, availableModels.size());

        availableModels = AvailableModelUtil.getAvailableModels(false);
        assertNotNull(availableModels);
        assertEquals(6, availableModels.size());
    }

}
