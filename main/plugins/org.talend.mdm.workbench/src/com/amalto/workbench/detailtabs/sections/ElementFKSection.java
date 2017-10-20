// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
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
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.xsd.XSDComponent;

import com.amalto.workbench.detailtabs.sections.composites.FKIntegrityComposite;
import com.amalto.workbench.detailtabs.sections.model.ISubmittable;
import com.amalto.workbench.detailtabs.sections.model.annotationinfo.relationship.ForeignKeyAnnoInfo;
import com.amalto.workbench.detailtabs.sections.util.FixDMNameBasePropertySectionDataModelExtractor;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.utils.XSDAnnotationsStructure;
import com.amalto.workbench.widgets.composites.SimpleXPathComposite;

public class ElementFKSection extends XSDComponentSection {

    private FixDMNameBasePropertySectionDataModelExtractor dataModelHolder;

    private SimpleXPathComposite compSimpleXPath;

    private String xpath = "";//$NON-NLS-1$

    private String fksep;

    private FKIntegrityComposite fkIntegrityConfig;

    @Override
    public void refresh() {
        compSimpleXPath.setXSDSchema(curXSDComponent.getSchema());
        compSimpleXPath.setXPath(xpath);
        if (fksep != null) {
            compSimpleXPath.setFKSep(Boolean.valueOf(fksep));
        }
        fkIntegrityConfig.setXSDComponent(curXSDComponent);
        updateSectionEnabled();
    }

    @Override
    protected void initUIContents(XSDComponent editedObj) {
        super.initUIContents(editedObj);

        xpath = new XSDAnnotationsStructure(curXSDComponent).getForeignKey();
        if (xpath == null) {
            xpath = "";//$NON-NLS-1$
        }
        fksep = new XSDAnnotationsStructure(curXSDComponent).getForeignKeyNotSep();
        dataModelHolder.setDefaultDataModel(getDataModelName());
        compSimpleXPath.setDefaultDataModelForSelect(getDataModelName());
        compSimpleXPath.setXSDSchema(curXSDComponent.getSchema());
        dataModelHolder.setDefaultEntity(getEntityName());
    }

    @Override
    protected ISubmittable getSubmittedObj() {
        return new ForeignKeyAnnoInfo(curXSDComponent, compSimpleXPath.getXPath(), compSimpleXPath.getFKSep());
    }

    @Override
    protected void createControlsInSection(Composite compSectionClient) {
        compSectionClient.setLayout(new GridLayout());
        dataModelHolder = new FixDMNameBasePropertySectionDataModelExtractor(this);
        compSimpleXPath = new SimpleXPathComposite(compSectionClient, SWT.NONE, SimpleXPathComposite.DEFAULTTITLE,
                dataModelHolder, dataModelHolder.getDefaultDataModel(), this, true);
        fkIntegrityConfig = new FKIntegrityComposite(compSectionClient, SWT.NONE, curXSDComponent, this); 
    }

    @Override
    protected String getSectionTitle() {
        return Messages.ElementFKSection_ForeignKey;
    }
}
