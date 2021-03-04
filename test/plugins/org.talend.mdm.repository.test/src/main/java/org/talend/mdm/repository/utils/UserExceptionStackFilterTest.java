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


/**
 * created by liusongbo on 2012-10-17
 *
 */
public class UserExceptionStackFilterTest {

    @Test
    public void testFilterExceptionMsg() {
        String errorMsg = "Unable to physically create the data cluster Test: javax.ejb.TransactionRolledbackLocalException: Could not create storage 'Test' with data model 'Test'. \r\n" //$NON-NLS-1$
                + " [Caused by]: Could not create storage 'Test' with data model 'Test'.\r\n" //$NON-NLS-1$
                + " [Caused by]: Could not create storage 'Test' with data model 'Test'.\r\n" //$NON-NLS-1$
                + " [Caused by]: java.lang.RuntimeException: java.lang.RuntimeException: Could not process field 'AgencyFK' in type 'AgentType'.\r\n" //$NON-NLS-1$
                + " [Caused by]: java.lang.RuntimeException: Could not process field 'AgencyFK' in type 'AgentType'.\r\n" //$NON-NLS-1$
                + " [Caused by]: Could not process field 'AgencyFK' in type 'AgentType'.\r\n" //$NON-NLS-1$
                + " [Caused by]: Field 'AgencyFK' in type 'AgentType': foreign key info is invalid.\r\n" //$NON-NLS-1$
                + " [Caused by]: Type 'Agency' does not exist."; //$NON-NLS-1$

        String[] filteredMsgs = UserExceptionStackFilter.filterExceptionMsg(errorMsg);
        assertEquals(5, filteredMsgs.length);
        String str1 = "Unable to physically create the data cluster Test: Could not create storage 'Test' with data model 'Test'."; //$NON-NLS-1$
        assertEquals(str1, filteredMsgs[0]);

        String str2 = "Could not create storage 'Test' with data model 'Test'."; //$NON-NLS-1$
        assertEquals(str2, filteredMsgs[1]);

        String str3 = "Could not process field 'AgencyFK' in type 'AgentType'."; //$NON-NLS-1$
        assertEquals(str3, filteredMsgs[2]);

        String str4 = "Field 'AgencyFK' in type 'AgentType': foreign key info is invalid."; //$NON-NLS-1$
        assertEquals(str4, filteredMsgs[3]);

        String str5 = "Type 'Agency' does not exist."; //$NON-NLS-1$
        assertEquals(str5, filteredMsgs[4]);

    }

}
