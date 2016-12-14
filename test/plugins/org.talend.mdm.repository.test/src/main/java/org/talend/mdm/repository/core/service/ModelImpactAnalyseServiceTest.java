// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.repository.core.service;

import org.junit.Assert;
import org.junit.Test;
import org.talend.mdm.repository.core.service.ModelImpactAnalyseService.Result;

/**
 * created by HHB on 2016-7-21 Detailled comment
 *
 */
public class ModelImpactAnalyseServiceTest {

    /**
     * Test method for
     * {@link org.talend.mdm.repository.core.service.ModelImpactAnalyseService#readResponseMessage(java.lang.String)}.
     */
    @Test
    public void testReadResponseMessage() {
        // returned msg when new datamodel is deploying
        String msg = ""; //$NON-NLS-1$
        Result result = ModelImpactAnalyseService.readResponseMessage(msg);
        Assert.assertNull(result);
        // test Impact analyse message parse
        msg = "<result><high><change><message>Element 'id2' was added.</message></change><change><message>Element 'id' was removed.</message></change></high><medium/><low><change><message>Element 'a2' was added.</message></change></low><entitiesToDrop><entity>EntityC</entity><entity>EntityA</entity></entitiesToDrop></result>"; //$NON-NLS-1$
        result = ModelImpactAnalyseService.readResponseMessage(msg);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getSeverities().size(), 3);
        Assert.assertEquals(result.getChanges().size(), 3);
        Assert.assertEquals(result.entitiesToDrop.entities.size(), 2);

    }

}
