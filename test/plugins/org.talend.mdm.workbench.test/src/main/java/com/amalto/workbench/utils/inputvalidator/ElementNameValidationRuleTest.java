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

package com.amalto.workbench.utils.inputvalidator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.talend.mdm.commmon.metadata.FieldMetadata;
import org.talend.mdm.commmon.metadata.MetadataRepository;
import org.talend.mdm.commmon.metadata.TypeMetadata;
import org.talend.mdm.commmon.metadata.ValidationError;
import org.talend.mdm.commmon.metadata.ValidationHandler;
import org.w3c.dom.Element;

public class ElementNameValidationRuleTest {

    class SubMetadataRepository extends MetadataRepository {

        @Override
        public ValidationHandler getValidationHandler() {
            return super.getValidationHandler();
        }
    }

    @Test
    public void testPerformOfNameValidation() {
        SubMetadataRepository repository = new SubMetadataRepository();
        InputStream resourceAsStream = this.getClass().getResourceAsStream("/resources/ElementNameValidationDemo.xsd");
        TestValidationHandler validationHandler = new TestValidationHandler();
        repository.load(resourceAsStream, validationHandler);

        assertEquals(0, validationHandler.getWarningCount());
        assertEquals(8, validationHandler.getErrorCount());
        assertTrue(validationHandler.getMessages().contains(ValidationError.NAME_CONTAINS_INVALID_CHARACTER));
        assertTrue(validationHandler.getLineNumbers().containsAll(Arrays.asList(16, 66, 20, 21, 6, 22, 11, 30)));
    }

    private static class TestValidationHandler implements ValidationHandler {

        private int errorField;

        private int warningField;

        private int errorType;

        private int warningType;

        private Set<ValidationError> errors = new HashSet<ValidationError>();

        private Set<Integer> lineNumbers = new HashSet<Integer>();

        @Override
        public void fatal(FieldMetadata field, String message, Element element, Integer lineNumber, Integer columnNumber,
                ValidationError error) {
            errors.add(error);
        }

        @Override
        public void error(FieldMetadata field, String message, Element element, Integer lineNumber, Integer columnNumber,
                ValidationError error) {
            errors.add(error);
            boolean added = lineNumbers.add(lineNumber);
            if (added) {
                errorField++;
            }
        }

        @Override
        public void warning(FieldMetadata field, String message, Element element, Integer lineNumber, Integer columnNumber,
                ValidationError error) {
            errors.add(error);
            lineNumbers.add(lineNumber);
            warningField++;
        }

        @Override
        public void fatal(TypeMetadata type, String message, Element element, Integer lineNumber, Integer columnNumber,
                ValidationError error) {
            errors.add(error);
        }

        @Override
        public void error(TypeMetadata type, String message, Element element, Integer lineNumber, Integer columnNumber,
                ValidationError error) {
            errors.add(error);
            boolean added = lineNumbers.add(lineNumber);
            if (added) {
                errorType++;
            }
        }

        @Override
        public void warning(TypeMetadata type, String message, Element element, Integer lineNumber, Integer columnNumber,
                ValidationError error) {
            errors.add(error);
            lineNumbers.add(lineNumber);
            warningType++;
        }

        @Override
        public void end() {
        }

        @Override
        public int getErrorCount() {
            return errorField + errorType;
        }

        public int getWarningCount() {
            return warningField + warningType;
        }

        private Set<ValidationError> getMessages() {
            return errors;
        }

        private Set<Integer> getLineNumbers() {
            return lineNumbers;
        }
    }
}
