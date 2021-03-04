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
package com.amalto.workbench.detailtabs.sections.model.entity;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.xsd.XSDIdentityConstraintCategory;
import org.eclipse.xsd.XSDIdentityConstraintDefinition;
import org.eclipse.xsd.XSDXPathDefinition;

public class KeyWrapper {

    private XSDIdentityConstraintDefinition sourceKey;

    private String name = "";//$NON-NLS-1$

    private String selector = "";//$NON-NLS-1$

    private XSDIdentityConstraintCategory type;

    private List<FieldWrapper> fields = new ArrayList<FieldWrapper>();

    public KeyWrapper(String name, String selector, XSDIdentityConstraintCategory type, FieldWrapper[] fields) {

        this.name = name;
        this.selector = selector;
        this.type = type;

        for (FieldWrapper eachFieldWrapper : fields)
            this.fields.add(eachFieldWrapper);
    }

    public KeyWrapper(XSDIdentityConstraintDefinition sourceKey) {
        this.sourceKey = sourceKey;

        if (sourceKey != null) {
            name = sourceKey.getName();
            selector = sourceKey.getSelector().getValue();
            type = sourceKey.getIdentityConstraintCategory();

            for (XSDXPathDefinition eachSourceFields : sourceKey.getFields())
                fields.add(new FieldWrapper(eachSourceFields));
        }
    }

    public XSDIdentityConstraintDefinition getSourceKey() {
        return sourceKey;
    }

    public XSDIdentityConstraintCategory getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSelector() {
        return selector;
    }

    public void setSelector(String selector) {
        this.selector = selector;
    }

    public void addField(FieldWrapper field) {
        fields.add(field);
    }

    public void removeField(FieldWrapper field) {
        fields.remove(field);
    }

    public FieldWrapper[] getFields() {
        return fields.toArray(new FieldWrapper[fields.size()]);
    }

    public boolean isUserCreated() {
        return getSourceKey() == null;
    }

    public boolean isUniqueKey() {
        return XSDIdentityConstraintCategory.UNIQUE_LITERAL.equals(getType());
    }
}
