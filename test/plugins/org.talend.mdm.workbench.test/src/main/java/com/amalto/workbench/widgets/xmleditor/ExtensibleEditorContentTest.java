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
        String content = "<parameters>\r\n" + "  <processId>Product_Product</processId>\r\n"
                + "  <processVersion>1.0</processVersion>\r\n" + "  <username>admin</username>\r\n"
                + "  <password>talend</password>\r\n" + "  <useBuiltInVariable>false</useBuiltInVariable>\r\n"
                + "  <defaultDataModel/>\r\n" + "</parameters>";

        String[] expected = new String[] {
                "<parameters>\r\n" + "  <processId>Product_Product</processId>\r\n" + "  <processVersion>1.0</processVersion>\r\n"
                        + "  <username>admin</username>\r\n  ",
                "<password>talend</password>",
                "\r\n" + "  <useBuiltInVariable>false</useBuiltInVariable>\r\n" + "  <defaultDataModel/>\r\n" + "</parameters>" };

        try {
            ExtensibleEditorContent extensibleEditorContent = new ExtensibleEditorContent(null);
            Method declaredMethod = extensibleEditorContent.getClass().getDeclaredMethod("splitbyPasswordTag", String.class);
            declaredMethod.setAccessible(true);
            String[] splited = (String[]) declaredMethod.invoke(extensibleEditorContent, content);
            Assert.assertArrayEquals(expected, splited);
        } catch (Exception e) {
            fail();
        }
    }

}
