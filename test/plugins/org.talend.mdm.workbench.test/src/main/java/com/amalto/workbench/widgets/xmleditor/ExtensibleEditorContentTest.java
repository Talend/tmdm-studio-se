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

import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Test;


public class ExtensibleEditorContentTest {

    @Test
    public void testSplitbyPasswordTag() {
        // check cases:common password, masked password, empty password
        String[] checkPasswordPart = { "<password>talend</password>", "<password>******</password>",
                "<password>*talend</password>", "<password></password>", "<password/>" };
        String[] expectedPasswordPart = { "<password>talend</password>", "<password>******</password>",
                "<password>*talend</password>", "<password></password>", "<password/>" };
        
        String checkContent1 = "<parameters>\r\n  <processId>Product_Product</processId>\r\n  <processVersion>1.0</processVersion>\r\n  <username>admin</username>\r\n  ";
                 
        String checkContent2 = "\r\n  <useBuiltInVariable>false</useBuiltInVariable>\r\n  <defaultDataModel/>\r\n</parameters>";

        String[] expected = new String[3];
        expected[0] = "<parameters>\r\n  <processId>Product_Product</processId>\r\n  <processVersion>1.0</processVersion>\r\n  <username>admin</username>\r\n  ";
        expected[2] = "\r\n  <useBuiltInVariable>false</useBuiltInVariable>\r\n  <defaultDataModel/>\r\n</parameters>";

        String pad = "<desc>abc</desc>";

        try {
            ExtensibleEditorContent extensibleEditorContent = new ExtensibleEditorContent(null);
            Method declaredMethod = extensibleEditorContent.getClass().getDeclaredMethod("splitByPasswordTag", String.class);
            declaredMethod.setAccessible(true);

            // check every single password
            for (int i = 0; i < checkPasswordPart.length; i++) {
                String checkContent = checkContent1 + checkPasswordPart[i] + checkContent2;
                String[] splited = (String[]) declaredMethod.invoke(extensibleEditorContent, checkContent);
                expected[1] = expectedPasswordPart[i];
                Assert.assertArrayEquals(expected, splited);
            }

            // check case: no password part
            String[] splited = (String[]) declaredMethod.invoke(extensibleEditorContent, checkContent1 + checkContent2);
            Assert.assertArrayEquals(null, splited);

            // check invalid content: multi password tag
            String checkContent = checkContent1 + checkPasswordPart[0] + pad + checkPasswordPart[1] + checkContent2;
            splited = (String[]) declaredMethod.invoke(extensibleEditorContent, checkContent);
            expected[1] = expectedPasswordPart[0] + pad + expectedPasswordPart[1];
            Assert.assertArrayEquals(expected, splited);

            checkContent = checkContent1 + checkPasswordPart[0] + pad + checkPasswordPart[3] + checkContent2;
            splited = (String[]) declaredMethod.invoke(extensibleEditorContent, checkContent);
            expected[1] = expectedPasswordPart[0] + pad + expectedPasswordPart[3];
            Assert.assertArrayEquals(expected, splited);

            // empty password tag behind common password tag
            checkContent = checkContent1 + checkPasswordPart[0] + pad + checkPasswordPart[4] + checkContent2;
            splited = (String[]) declaredMethod.invoke(extensibleEditorContent, checkContent);
            expected[1] = expectedPasswordPart[0];
            Assert.assertArrayEquals(new String[] { expected[0], expected[1], pad + expectedPasswordPart[4] + expected[2] },
                    splited);

            // empty password tag before common password tag
            checkContent = checkContent1 + checkPasswordPart[4] + pad + checkPasswordPart[0] + checkContent2;
            splited = (String[]) declaredMethod.invoke(extensibleEditorContent, checkContent);
            expected[1] = expectedPasswordPart[0];
            Assert.assertArrayEquals(new String[] { expected[0] + checkPasswordPart[4] + pad, expected[1], expected[2] },
                    splited);

            checkContent = checkContent1 + checkPasswordPart[4] + pad + checkPasswordPart[3] + checkContent2;
            splited = (String[]) declaredMethod.invoke(extensibleEditorContent, checkContent);
            expected[1] = expectedPasswordPart[3];
            Assert.assertArrayEquals(new String[] { expected[0] + checkPasswordPart[4] + pad, expected[1], expected[2] },
                    splited);

            checkContent = checkContent1 + checkPasswordPart[4] + pad + checkPasswordPart[4] + checkContent2;
            splited = (String[]) declaredMethod.invoke(extensibleEditorContent, checkContent);
            expected[1] = checkPasswordPart[4] + pad + checkPasswordPart[4];
            Assert.assertArrayEquals(expected, splited);

        } catch (Exception e) {
            fail();
        }
    }

}
