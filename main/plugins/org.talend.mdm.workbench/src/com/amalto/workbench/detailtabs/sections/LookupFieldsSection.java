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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.xsd.XSDComponent;

import com.amalto.workbench.detailtabs.sections.model.ISubmittable;
import com.amalto.workbench.detailtabs.sections.model.annotationinfo.extra.LookupFieldsAnnoInfo;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.models.infoextractor.XSDComponentChildElementsHolder;
import com.amalto.workbench.utils.XSDAnnotationsStructure;
import com.amalto.workbench.widgets.composites.SelectElementsOfEntityComposite;

public class LookupFieldsSection extends XSDComponentSection {

    private SelectElementsOfEntityComposite compElements;

    private List<String> lookupFields = new ArrayList<String>();

    @Override
    public void refresh() {
        compElements.setInfos(lookupFields.toArray(new String[0]));
        updateSectionEnabled();
    }

    @Override
    protected void initUIContents(XSDComponent editedObj) {
        super.initUIContents(editedObj);

        lookupFields.clear();

        XSDAnnotationsStructure annoStruct = new XSDAnnotationsStructure(curXSDComponent);

        for (String eachLookUpFields : annoStruct.getLookupFields().values())
            lookupFields.add(eachLookUpFields);

        compElements.setElementsHolder(new XSDComponentChildElementsHolder(curXSDComponent));

    }

    @Override
    protected ISubmittable getSubmittedObj() {
        return new LookupFieldsAnnoInfo(curXSDComponent, compElements.getInfos());
    }

    @Override
    protected String getSectionTitle() {
        return Messages.LookupFieldsSection_LookupFields;
    }

    @Override
    protected void createControlsInSection(Composite compSectionClient) {
        compElements = new SelectElementsOfEntityComposite(compSectionClient, SWT.NONE, Messages.LookupFieldsSection_LookupFields, null,this);
    }

}
