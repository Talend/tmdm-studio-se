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
import org.eclipse.xsd.XSDComplexTypeDefinition;

import com.amalto.workbench.detailtabs.sections.composites.ComplexTypeConfigComposite;
import com.amalto.workbench.detailtabs.sections.model.ISubmittable;
import com.amalto.workbench.detailtabs.sections.model.complextype.ComplexTypeWrapper;
import com.amalto.workbench.i18n.Messages;

public class ComplexTypeMainSection extends CommitBarListenerSection<XSDComplexTypeDefinition> {

    private ComplexTypeConfigComposite compComplexType;

    private XSDComplexTypeDefinition complexType;

    @Override
    protected ISubmittable getSubmittedObj() {
        return new ComplexTypeWrapper(complexType, compComplexType.getTypeName(), compComplexType.getExtends(),
                compComplexType.getGroupTypeCompositor(), compComplexType.isAbstract());
    }

    @Override
    public void refresh() {
        super.refresh();
        updateSectionEnabled();
    }

    @Override
    public XSDComplexTypeDefinition getEditedObj() {
        return complexType;
    }

    @Override
    protected void initUIContents(XSDComplexTypeDefinition editedObj) {

        this.complexType = editedObj;

        compComplexType.setComplexType(complexType);
    }

    @Override
    protected String getSectionTitle() {
        return Messages.ComplexTypeMainSection_Main;
    }

    @Override
    protected void createControlsInSection(Composite compSectionClient) {
        compComplexType = new ComplexTypeConfigComposite(compSectionClient, SWT.NONE, this, complexType);
    }

}
