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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.amalto.workbench.detailtabs.exception.CommitException;
import com.amalto.workbench.detailtabs.sections.model.annotationinfo.langinfo.DisplayFormatInfoCollection;
import com.amalto.workbench.detailtabs.sections.model.annotationinfo.langinfo.LanguageInfo;
import com.amalto.workbench.utils.XSDAnnotationsStructure;

public class DisplayFormatCommitHandler extends LanguageInfoCommitHandler<DisplayFormatInfoCollection> {

    public DisplayFormatCommitHandler(DisplayFormatInfoCollection submittedLangInfos) {
        super(submittedLangInfos);
    }

    @Override
    protected Map<String, String> getOriginalLang2Info() {
        return getXSDAnnotationStruct().getDisplayFormat();
    }

    @Override
    protected boolean doSubmit() throws CommitException {

        XSDAnnotationsStructure xsdAnnoStruct = getXSDAnnotationStruct();

        Map<String, String> langCode2Value = new LinkedHashMap<String, String>();
        for (Entry<String, LanguageInfo> eachLangCode2LangInfo : getCommitedObj().getLangCode2LangInfo().entrySet()) {
            langCode2Value.put(eachLangCode2LangInfo.getKey(), eachLangCode2LangInfo.getValue().getLabel());
        }

        return xsdAnnoStruct.setDisplayFormat(langCode2Value);

    }

    @Override
    protected void removeLangInfo(XSDAnnotationsStructure xsdAnnoStruct, String langcode) {
    }

    @Override
    protected void updateLangInfo(XSDAnnotationsStructure xsdAnnoStruct, LanguageInfo langInfo) {
    }

    @Override
    protected void addLangInfo(XSDAnnotationsStructure xsdAnnoStruct, LanguageInfo langInfo) {
    }

}
