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
package com.amalto.workbench.detailtabs.sections;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.xsd.XSDSimpleTypeDefinition;

import com.amalto.workbench.detailtabs.sections.composites.SimpleTypeConfigComposite;
import com.amalto.workbench.detailtabs.sections.model.ISubmittable;
import com.amalto.workbench.detailtabs.sections.model.simpletype.SimpleTypeWrapper;
import com.amalto.workbench.i18n.Messages;

public class SimpleTypeMainSection extends CommitBarListenerSection<XSDSimpleTypeDefinition> {

    private SimpleTypeConfigComposite compSimpleTypeConfig;

    private XSDSimpleTypeDefinition xsdSimpleType;

    @Override
    protected ISubmittable getSubmittedObj() {

        return new SimpleTypeWrapper(xsdSimpleType, compSimpleTypeConfig.getSimpleTypeName(),
                compSimpleTypeConfig.getSelectedBaseType(), compSimpleTypeConfig.getSelectedBaseTypeName(),
                compSimpleTypeConfig.getPropertyName2Values());
    }

    @Override
    public void refresh() {
        super.refresh();
        updateSectionEnabled();
    }

    @Override
    public XSDSimpleTypeDefinition getEditedObj() {
        return xsdSimpleType;
    }

    @Override
    protected void initUIContents(XSDSimpleTypeDefinition editedObj) {

        xsdSimpleType = editedObj;

        compSimpleTypeConfig.setSimpleType(xsdSimpleType);

    }

    @Override
    protected String getSectionTitle() {
        return Messages.SimpleTypeMainSection_Main;
    }

    @Override
    protected void createControlsInSection(Composite compSectionClient) {
        compSimpleTypeConfig = new SimpleTypeConfigComposite(compSectionClient, SWT.NONE,this);
    }
}
