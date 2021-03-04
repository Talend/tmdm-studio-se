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
package com.amalto.workbench.detailtabs.sections.model.annotationinfo.relationship;

import java.util.Arrays;

import com.amalto.workbench.utils.IConstants;

public class ForeignKeyFilterAnnoInfoDefUnit {

    private String xpath = "";//$NON-NLS-1$

    private String operator = "";//$NON-NLS-1$

    private String value = "";//$NON-NLS-1$

    private String predicate = "";//$NON-NLS-1$

    public ForeignKeyFilterAnnoInfoDefUnit(String xpath, String operator, String value, String predicate) {

        if (xpath != null)
            this.xpath = xpath;

        if (Arrays.asList(IConstants.VIEW_CONDITION_OPERATORS).contains(operator))
            this.operator = operator;

        if (value != null)
            this.value = value;

        if (Arrays.asList(IConstants.PREDICATES).contains(predicate))
            this.predicate = predicate;
    }

    public String getXpath() {
        return xpath;
    }

    public void setXpath(String xpath) {
        if (xpath != null)
            this.xpath = xpath;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        if (Arrays.asList(IConstants.VIEW_CONDITION_OPERATORS).contains(operator))
            this.operator = operator;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        if (value != null)
            this.value = value;
    }

    public String getPredicate() {
        return predicate;
    }

    public void setPredicate(String predicate) {
        if (Arrays.asList(IConstants.PREDICATES).contains(predicate))
            this.predicate = predicate;
    }

    public String getNormalizeValue() {
        return normalizeValue(value);
    }

    private String normalizeValue(String value) {
        if (value != null && value.trim().length() > 0) {
            value = value.replaceAll("\"", "&quot;");//$NON-NLS-1$//$NON-NLS-2$
            value = value.replaceAll("'", "&quot;");//$NON-NLS-1$//$NON-NLS-2$
        }
        return value;
    }
}
