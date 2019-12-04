// ============================================================================
//
// Copyright (C) 2006-2019 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package com.amalto.workbench.widgets.celleditor;

import static org.junit.Assert.*;

import org.junit.Test;


public class DefaultXPathValidatorTest {

    @Test
    public void testValidate() {
        DefaultXPathValidator validator = new DefaultXPathValidator();

        String xpath = null;
        boolean validate = validator.validate(xpath);
        assertFalse(validate);

        xpath = "";// empty
        validate = validator.validate(xpath);
        assertTrue(validate);

        xpath = ".";// dot
        validate = validator.validate(xpath);
        assertTrue(validate);

        xpath = "abc";// alphabetic
        validate = validator.validate(xpath);
        assertTrue(validate);

        xpath = "1234567890";// digital
        validate = validator.validate(xpath);
        assertTrue(validate);

        xpath = "/";// slash
        validate = validator.validate(xpath);
        assertTrue(validate);

        xpath = "@";//
        validate = validator.validate(xpath);
        assertTrue(validate);

        xpath = ":";//
        validate = validator.validate(xpath);
        assertTrue(validate);

        ////////////////////////////////

        String[] xpaths = { "~", "`", "!", "#", "$", "%", "^", "&", "*", "(", ")", "-", "_", "+", "=", "[", "]", "{", "}", "|",
                "\\", ";", "'", "\"", "<", ">", ",", "?", " ", "    " };// the last two are empty string and tab string
        for (String path : xpaths) {
            validate = validator.validate(path);
            assertFalse(validate);
        }
    }

}
