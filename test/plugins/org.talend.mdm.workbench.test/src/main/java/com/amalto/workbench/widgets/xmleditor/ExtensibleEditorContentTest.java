// ============================================================================
//
// Copyright (C) 2006-2020 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.workbench.widgets.xmleditor;

import static org.junit.Assert.fail;

import org.junit.Assert;
import org.junit.Test;

import com.amalto.workbench.widgets.xmleditor.ExtensibleEditorContent.PasswordTagUtil;


public class ExtensibleEditorContentTest {

    @Test
    public void testHidePassword() {
        // check cases:common password, masked password,empty password
        String[] passwordPart = { "<password>talend</password>", "<password>******</password>", "<password>*talend</password>",
                "<password></password>", "<password/>" };
        String[] expectedPasswordPart = { "<password>******</password>", "<password>******</password>",
                "<password>******</password>", "<password></password>", "<password/>" };

        String checkContent1 = "<parameters>\r\n  <processId>Product_Product</processId>\r\n  <processVersion>1.0</processVersion>\r\n  <username>admin</username>\r\n  ";

        String checkContent2 = "\r\n  <useBuiltInVariable>false</useBuiltInVariable>\r\n  <defaultDataModel/>\r\n</parameters>";

        for (int i = 0; i < passwordPart.length; i++) {
            String expected = checkContent1 + expectedPasswordPart[i] + checkContent2;
            String hidePassword = PasswordTagUtil.hidePassword(checkContent1 + passwordPart[i] + checkContent2);
            Assert.assertEquals(expected, hidePassword);
        }
    }

    @Test
    public void testIsPasswordHidden() {
        // check cases:common password, masked password,empty password
        String[] passwordPart = { "<password>talend</password>", "<password>******</password>", "<password>*talend</password>",
                "<password></password>", "<password/>" };
        boolean[] expected = { false, true, true, false, false };

        String checkContent1 = "<parameters>\r\n  <processId>Product_Product</processId>\r\n  <processVersion>1.0</processVersion>\r\n  <username>admin</username>\r\n  ";

        String checkContent2 = "\r\n  <useBuiltInVariable>false</useBuiltInVariable>\r\n  <defaultDataModel/>\r\n</parameters>";

        for (int i = 0; i < passwordPart.length; i++) {
            boolean hidePassword = PasswordTagUtil.isPasswordHidden(checkContent1 + passwordPart[i] + checkContent2);
            Assert.assertEquals(expected[i], hidePassword);
        }
    }

    @Test
    public void testIsContentChanged() {
        String[] newpasswordPart = { "<password>******</password>", "<password>*talend*</password>",
                "<password>*talend*</password>", "<password>*talend</password>", "<password>******</password>",
                "<password>******</password>", "<password>*talend*</password>" };
        String[] passwordPart = { "<password>******</password>", "<password>*talend*</password>", "<password>tal*end*</password>",
                "<password>******</password>", "<password>tal*end*</password>", "<password></password>",
                "<password></password>" };
        String pad = "<desc>abc</desc>";

        String checkContent1 = "<parameters>\r\n  <processId>Product_Product</processId>\r\n  <processVersion>1.0</processVersion>\r\n  <username>admin</username>\r\n  ";

        String checkContent2 = "\r\n  <useBuiltInVariable>false</useBuiltInVariable>\r\n  <defaultDataModel/>\r\n</parameters>";

        // only mask coded password changed
        for (int i = 0; i < newpasswordPart.length; i++) {
            boolean contentChanged = PasswordTagUtil.isContentChanged(checkContent1 + newpasswordPart[i] + checkContent2,
                    checkContent1 + passwordPart[i] + checkContent2);
            Assert.assertEquals(false, contentChanged);
        }

        newpasswordPart = new String[] { "<password>*talend</password>", "<password>******</password>" };
        passwordPart = new String[] { "<password/>", "<password/>" };
        for (int i = 0; i < newpasswordPart.length; i++) {
            boolean contentChanged = PasswordTagUtil.isContentChanged(checkContent1 + newpasswordPart[i] + checkContent2,
                    checkContent1 + passwordPart[i] + checkContent2);
            Assert.assertEquals(true, contentChanged);
        }

        // except mask code password changed , other content also changed
        for (int i = 0; i < newpasswordPart.length; i++) {
            boolean contentChanged1 = PasswordTagUtil.isContentChanged(checkContent1 + newpasswordPart[i] + checkContent2 + pad,
                    checkContent1 + passwordPart[i] + checkContent2);
            boolean contentChanged2 = PasswordTagUtil.isContentChanged(checkContent1 + newpasswordPart[i] + pad + checkContent2,
                    checkContent1 + passwordPart[i] + checkContent2);
            boolean contentChanged3 = PasswordTagUtil.isContentChanged(checkContent1 + pad + newpasswordPart[i] + checkContent2,
                    checkContent1 + passwordPart[i] + checkContent2);
            boolean contentChanged4 = PasswordTagUtil.isContentChanged(pad + checkContent1 + newpasswordPart[i] + checkContent2,
                    checkContent1 + passwordPart[i] + checkContent2);
            Assert.assertEquals(true, contentChanged1);
            Assert.assertEquals(true, contentChanged2);
            Assert.assertEquals(true, contentChanged3);
            Assert.assertEquals(true, contentChanged4);
        }
    }

    @Test
    public void testSplitbyPasswordTag() {
        // check cases:common password, masked password
        String[] passwordPart = { "<password>talend</password>", "<password>******</password>",
                "<password>*talend</password>", "<password></password>" };
        
        String checkContent1 = "<parameters>\r\n  <processId>Product_Product</processId>\r\n  <processVersion>1.0</processVersion>\r\n  <username>admin</username>\r\n  ";
                 
        String checkContent2 = "\r\n  <useBuiltInVariable>false</useBuiltInVariable>\r\n  <defaultDataModel/>\r\n</parameters>";

        String[] expected = new String[3];
        expected[0] = "<parameters>\r\n  <processId>Product_Product</processId>\r\n  <processVersion>1.0</processVersion>\r\n  <username>admin</username>\r\n  ";
        expected[2] = "\r\n  <useBuiltInVariable>false</useBuiltInVariable>\r\n  <defaultDataModel/>\r\n</parameters>";

        String pad = "<desc>abc</desc>";

        try {
            // check every single password
            for (int i = 0; i < passwordPart.length; i++) {
                String checkContent = checkContent1 + passwordPart[i] + checkContent2;
                String[] splited = PasswordTagUtil.splitByPasswordTag(checkContent);
                expected[1] = passwordPart[i];
                Assert.assertArrayEquals(expected, splited);
            }

            // check case: no password part
            String[] splited = PasswordTagUtil.splitByPasswordTag(checkContent1 + checkContent2);
            Assert.assertArrayEquals(null, splited);

            // check invalid content: multi password tag
            String checkContent = checkContent1 + passwordPart[0] + pad + passwordPart[1] + checkContent2;
            splited = PasswordTagUtil.splitByPasswordTag(checkContent);
            expected[1] = passwordPart[0] + pad + passwordPart[1];
            Assert.assertArrayEquals(expected, splited);

            checkContent = checkContent1 + passwordPart[0] + pad + passwordPart[3] + checkContent2;
            splited = PasswordTagUtil.splitByPasswordTag(checkContent);
            expected[1] = passwordPart[0] + pad + passwordPart[3];
            Assert.assertArrayEquals(expected, splited);

            checkContent = checkContent1 + passwordPart[1] + pad + passwordPart[3] + checkContent2;
            splited = PasswordTagUtil.splitByPasswordTag(checkContent);
            expected[1] = passwordPart[1] + pad + passwordPart[3];
            Assert.assertArrayEquals(expected, splited);

            // invalid password tag pair
            checkContent = checkContent1 + "<password>" + checkContent2;
            splited = PasswordTagUtil.splitByPasswordTag(checkContent);
            Assert.assertNull(splited);

            checkContent = checkContent1 + "</password>" + checkContent2;
            splited = PasswordTagUtil.splitByPasswordTag(checkContent);
            Assert.assertNull(splited);

        } catch (Exception e) {
            fail();
        }
    }

}
