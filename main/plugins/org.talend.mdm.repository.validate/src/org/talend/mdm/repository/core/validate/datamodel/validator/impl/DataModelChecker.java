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
package org.talend.mdm.repository.core.validate.datamodel.validator.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.map.MultiKeyMap;
import org.apache.log4j.Logger;
import org.talend.mdm.commmon.metadata.*;
import org.talend.mdm.commmon.metadata.validation.ValidationFactory;
import org.talend.mdm.commmon.metadata.validation.ValidationRule;
import org.talend.mdm.repository.core.validate.datamodel.IChecker;
import org.talend.mdm.repository.core.validate.datamodel.validator.ModelValidationMessage;
import org.talend.mdm.repository.core.validate.datamodel.validator.rule.IComponentValidationRule;
import org.w3c.dom.Element;

public class DataModelChecker implements IChecker<ModelValidationMessage> {

    static Logger log = Logger.getLogger(DataModelChecker.class);

    public static String getDataModelName(String fileName) {
        Pattern pattern = Pattern.compile("(\\w*?)_(\\d*?)\\.(\\d*?)\\.xsd"); //$NON-NLS-1$
        Matcher matcher = pattern.matcher(fileName);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return fileName;
    }

    @Override
    public List<ModelValidationMessage> toCheck(File file) {
        if (!file.exists()) {
            throw new IllegalArgumentException("File '" + file + "' does not exist.");
        }
        if (!file.canRead()) {
            throw new IllegalArgumentException("File '" + file + "' exists but cannot be read.");
        }
        ValidationHandlerAdapter validationHandler = new ValidationHandlerAdapter(getDataModelName(file.getName()));
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            MetadataRepository repository = new MetadataRepository();
            repository.load(inputStream, validationHandler);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Could not open file '" + file + "'.", e);
        } catch (RuntimeException e) {
            validationHandler.error(TypeMetadataAdapter.INSTANCE, e.getMessage(), null, -1, -1, ValidationError.UNCAUGHT_ERROR);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }

        }
        return validationHandler.getMessages();
    }

    private static class ValidationHandlerAdapter implements ValidationHandler {

        public static final String ANONYMOUS_TYPE_NAME = "<Anonymous>"; //$NON-NLS-1$

        private final String dataModelName;

        private final Map<ValidationError, MultiKeyMap> errors = new HashMap<ValidationError, MultiKeyMap>();

        private int errorCount;

        public ValidationHandlerAdapter(String dataModelName) {
            this.dataModelName = dataModelName;
        }

        private static String getTypeName(TypeMetadata type) {
            String name = type.getName();
            if (name.startsWith(MetadataRepository.ANONYMOUS_PREFIX)) {
                name = ANONYMOUS_TYPE_NAME;
            }
            return name;
        }

        private static int getValue(Integer integer, int defaultValue) {
            if (integer == null) {
                return defaultValue;
            } else {
                return integer;
            }
        }

        private static String getEntityName(FieldMetadata field) {
            try {
                ComplexTypeMetadata containingType = field.getContainingType().getEntity();
                String name = containingType.getName();
                if (name.startsWith(MetadataRepository.ANONYMOUS_PREFIX)) {
                    name = ANONYMOUS_TYPE_NAME;
                }
                return name;
            } catch (Exception e) {
                return ""; //$NON-NLS-1$
            }
        }

        @Override
        public void fatal(TypeMetadata type, String message, Element element, Integer lineNumber, Integer columnNumber,
                ValidationError error) {
            error(type, message, element, getValue(lineNumber, -1), getValue(columnNumber, -1), error);
        }

        @Override
        public void error(TypeMetadata type, String message, Element element, Integer lineNumber, Integer columnNumber,
                ValidationError error) {
            if (error != ValidationError.XML_SCHEMA) {
                int group = type.isInstantiable() ? IComponentValidationRule.MSG_GROUP_ENTITY
                        : IComponentValidationRule.MSG_GROUP_TYPE;
                ModelValidationMessage validationMessage = new ModelValidationMessage(IComponentValidationRule.SEV_ERROR,
                        message,
                        "key", // TODO
                        dataModelName, getValue(lineNumber, -1), getValue(columnNumber, -1), group, element, getTypeName(type),
                        getTypeName(type), getTypeName(type));
                addMessage(getValue(lineNumber, -1), getValue(columnNumber, -1), error, validationMessage);
                errorCount++;
            }
        }

        private void addMessage(int lineNumber, int columnNumber, ValidationError error, ModelValidationMessage validationMessage) {
            MultiKeyMap errorsByType = errors.get(error);
            if (errorsByType == null) {
                errorsByType = new MultiKeyMap();
                errors.put(error, errorsByType);
            }
            errorsByType.put(getValue(lineNumber, -1), getValue(columnNumber, -1), validationMessage);
        }

        @Override
        public void warning(TypeMetadata type, String message, Element element, Integer lineNumber, Integer columnNumber,
                ValidationError error) {
            int group = type.isInstantiable() ? IComponentValidationRule.MSG_GROUP_ENTITY
                    : IComponentValidationRule.MSG_GROUP_TYPE;
            ModelValidationMessage validationMessage = new ModelValidationMessage(IComponentValidationRule.SEV_WARNING,
                    message,
                    "key", // TODO
                    dataModelName, getValue(lineNumber, -1), getValue(columnNumber, -1), group, element, getTypeName(type),
                    getTypeName(type), getTypeName(type));
            addMessage(getValue(lineNumber, -1), getValue(columnNumber, -1), error, validationMessage);
        }

        @Override
        public void fatal(FieldMetadata field, String message, Element element, Integer lineNumber, Integer columnNumber,
                ValidationError error) {
            error(field, message, element, getValue(lineNumber, -1), getValue(columnNumber, -1), error);
        }

        @Override
        public void error(FieldMetadata field, String message, Element element, Integer lineNumber, Integer columnNumber,
                ValidationError error) {
            if (error != ValidationError.XML_SCHEMA) {
                int group;
                try {
                    ComplexTypeMetadata containingType = field.getContainingType().getEntity();
                    group = containingType.isInstantiable() ? IComponentValidationRule.MSG_GROUP_ENTITY
                            : IComponentValidationRule.MSG_GROUP_TYPE;
                } catch (Exception e) {
                    group = IComponentValidationRule.MSG_GROUP_ENTITY;
                }
                ModelValidationMessage validationMessage = new ModelValidationMessage(IComponentValidationRule.SEV_ERROR,
                        message,
                        "key", // TODO
                        dataModelName, getValue(lineNumber, -1), getValue(columnNumber, -1), group, element,
                        getEntityName(field), getEntityName(field), field.getPath());
                addMessage(getValue(lineNumber, -1), getValue(columnNumber, -1), error, validationMessage);
                errorCount++;
            }
        }

        @Override
        public void warning(FieldMetadata field, String message, Element element, Integer lineNumber, Integer columnNumber,
                ValidationError error) {
            int group = field.getContainingType().isInstantiable() ? IComponentValidationRule.MSG_GROUP_ENTITY
                    : IComponentValidationRule.MSG_GROUP_TYPE;
            ModelValidationMessage validationMessage = new ModelValidationMessage(IComponentValidationRule.SEV_WARNING,
                    message,
                    "key", // TODO
                    dataModelName, getValue(lineNumber, -1), getValue(columnNumber, -1), group, element, getEntityName(field),
                    getEntityName(field), field.getPath());
            addMessage(getValue(lineNumber, -1), getValue(columnNumber, -1), error, validationMessage);
        }

        public List<ModelValidationMessage> getMessages() {
            List<ModelValidationMessage> messages = new LinkedList<ModelValidationMessage>();
            for (Map.Entry<ValidationError, MultiKeyMap> error : errors.entrySet()) {
                messages.addAll(error.getValue().values());
            }
            return messages;
        }

        @Override
        public void end() {
        }

        @Override
        public int getErrorCount() {
            return errorCount;
        }
    }
}

class TypeMetadataAdapter implements TypeMetadata {

    final static TypeMetadataAdapter INSTANCE = new TypeMetadataAdapter();

    private TypeMetadataAdapter() {
    }

    @Override
    public <T> T accept(MetadataVisitor<T> visitor) {
        return null;
    }

    @Override
    public void setData(String key, Object data) {
    }

    @Override
    public <X> X getData(String key) {
        return null;
    }

    @Override
    public Collection<TypeMetadata> getSuperTypes() {
        return null;
    }

    @Override
    public void addSuperType(TypeMetadata superType) {
    }

    @Override
    public String getName() {
        return ""; //$NON-NLS-1$
    }

    @Override
    public void setName(String name) {
    }

    @Override
    public String getNamespace() {
        return null;
    }

    @Override
    public boolean isAssignableFrom(TypeMetadata type) {
        return false;
    }

    @Override
    public TypeMetadata copy() {
        return null;
    }

    @Override
    public TypeMetadata copyShallow() {
        return null;
    }

    @Override
    public TypeMetadata freeze() {
        return null;
    }

    @Override
    public boolean isInstantiable() {
        return true;
    }

    @Override
    public void setInstantiable(boolean isInstantiable) {
    }

    @Override
    public boolean isFrozen() {
        return false;
    }

    @Override
    public void validate(ValidationHandler handler) {
    }

    @Override
    public ValidationRule createValidationRule() {
        return ValidationFactory.getRule(this);
    }

}
