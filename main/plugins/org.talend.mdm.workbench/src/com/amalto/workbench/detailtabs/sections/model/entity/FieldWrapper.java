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

import org.eclipse.xsd.XSDXPathDefinition;

public class FieldWrapper {

    private XSDXPathDefinition sourceFiled;

    private String xpath = "";//$NON-NLS-1$

    public FieldWrapper(String xpath) {
        this.xpath = xpath;
    }

    public FieldWrapper(XSDXPathDefinition sourceFiled) {

        if (sourceFiled != null) {
            this.sourceFiled = sourceFiled;
            this.xpath = sourceFiled.getValue();
        }
    }

    public String getXPath() {
        return xpath;
    }

    public void setXPath(String xpath) {

        if (xpath == null || "".equals(xpath.trim()))//$NON-NLS-1$
            return;

        this.xpath = xpath;
    }

    public XSDXPathDefinition getSourceField() {
        return sourceFiled;
    }

    public boolean isUserCreated() {
        return getSourceField() == null;
    }
}
