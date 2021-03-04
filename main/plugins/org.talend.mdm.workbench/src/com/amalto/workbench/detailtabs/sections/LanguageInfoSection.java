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
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.xsd.XSDComponent;

import com.amalto.workbench.detailtabs.sections.composites.LanguageInfoComposite;
import com.amalto.workbench.detailtabs.sections.model.annotationinfo.langinfo.LanguageInfo;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.utils.XSDAnnotationsStructure;

public abstract class LanguageInfoSection extends XSDComponentSection {

    protected List<LanguageInfo> langInfos = new ArrayList<LanguageInfo>();

    protected LanguageInfoComposite compLabel;

    @Override
    public void refresh() {
        compLabel.setLanguageInfos(langInfos.toArray(new LanguageInfo[0]));
        updateSectionEnabled();
    }

    public LanguageInfo[] getLangInfos() {
        return compLabel.getLanguageInfos();
    }

    protected abstract Map<String, String> getLang2Info(XSDAnnotationsStructure xsdAnnoStruct);

    protected abstract String getSectionTitle();

    @Override
    protected void initUIContents(XSDComponent editedObj) {
        super.initUIContents(editedObj);

        langInfos.clear();

        XSDAnnotationsStructure xsdAnnoStruct = new XSDAnnotationsStructure(curXSDComponent);

        for (Entry<String, String> eachLang2Label : getLang2Info(xsdAnnoStruct).entrySet()) {
            langInfos.add(new LanguageInfo(Util.iso2lang.get(eachLang2Label.getKey()), eachLang2Label.getKey(), eachLang2Label
                    .getValue()));
        }

    }

    @Override
    protected void createControlsInSection(Composite compSectionClient) {
        compLabel = new LanguageInfoComposite(compSectionClient, SWT.NONE,this);
    }
}
