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
package com.amalto.workbench.detailtabs.sections.handlers;

import java.util.Map;

import com.amalto.workbench.detailtabs.sections.model.annotationinfo.langinfo.DescriptionInfoCollection;
import com.amalto.workbench.detailtabs.sections.model.annotationinfo.langinfo.LanguageInfo;
import com.amalto.workbench.utils.XSDAnnotationsStructure;

public class DescriptionCommitHandler extends LanguageInfoCommitHandler<DescriptionInfoCollection> {

    public DescriptionCommitHandler(DescriptionInfoCollection submittedLangInfos) {
        super(submittedLangInfos);
    }

    @Override
    protected Map<String, String> getOriginalLang2Info() {
        return getXSDAnnotationStruct().getDescriptions();
    }

    @Override
    protected void addLangInfo(XSDAnnotationsStructure xsdAnnoStruct, LanguageInfo langInfo) {
        xsdAnnoStruct.setDescription(langInfo.getLanguageISOCode(), langInfo.getLabel());
    }

    @Override
    protected void removeLangInfo(XSDAnnotationsStructure xsdAnnoStruct, String langcode) {
        xsdAnnoStruct.removeDescription(langcode);
    }

    @Override
    protected void updateLangInfo(XSDAnnotationsStructure xsdAnnoStruct, LanguageInfo langInfo) {
        xsdAnnoStruct.setDescription(langInfo.getLanguageISOCode(), langInfo.getLabel());
    }
}
