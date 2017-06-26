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
