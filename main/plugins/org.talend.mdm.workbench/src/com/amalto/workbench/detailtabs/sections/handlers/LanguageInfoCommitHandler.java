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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.amalto.workbench.detailtabs.exception.CommitValidationException;
import com.amalto.workbench.detailtabs.sections.model.annotationinfo.langinfo.LanguageInfo;
import com.amalto.workbench.detailtabs.sections.model.annotationinfo.langinfo.LanguageInfoCollection;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.utils.XSDAnnotationsStructure;

abstract class LanguageInfoCommitHandler<T extends LanguageInfoCollection> extends AnnotationInfoCommitHandler<T> {

    public LanguageInfoCommitHandler(T submittedLangInfos) {
        super(submittedLangInfos);
    }

    @Override
    protected void validateCommit() throws CommitValidationException {

        for (LanguageInfo eachLanguage : getCommitedObj().getLanguageInfos()) {
            validateEachLanguageInfo(eachLanguage);
        }

    }

    private void validateEachLanguageInfo(LanguageInfo validatedLanguageInfo) throws CommitValidationException {
        if ("".equals(validatedLanguageInfo.getLabel().trim())) {//$NON-NLS-1$
            throw new CommitValidationException(Messages.bind(Messages.LanguageInfoCommitHandler_ExceptionInfo, validatedLanguageInfo.getLanguage()));
        }

    }

    @Override
    protected String[] getNeedRemovedObjs() {

        Set<String> results = new HashSet<String>();

        Map<String, LanguageInfo> curLangCode2LangInfo = getCommitedObj().getLangCode2LangInfo();

        for (Entry<String, String> eachOriginalLangCode2Info : getOriginalLang2Info().entrySet()) {

            if (curLangCode2LangInfo.containsKey(eachOriginalLangCode2Info.getKey()))
                continue;

            results.add(eachOriginalLangCode2Info.getKey());
        }

        return results.toArray(new String[0]);

    }

    @Override
    protected LanguageInfo[] getNeedUpdatedObjs() {

        List<LanguageInfo> results = new ArrayList<LanguageInfo>();

        Map<String, LanguageInfo> curLangCode2LangInfo = getCommitedObj().getLangCode2LangInfo();

        for (Entry<String, String> eachOriginalLangCode2Info : getOriginalLang2Info().entrySet()) {

            if (!curLangCode2LangInfo.containsKey(eachOriginalLangCode2Info.getKey()))
                continue;

            if (curLangCode2LangInfo.get(eachOriginalLangCode2Info.getKey()).getLabel()
                    .equals(eachOriginalLangCode2Info.getValue()))
                continue;

            results.add(curLangCode2LangInfo.get(eachOriginalLangCode2Info.getKey()));
        }

        return results.toArray(new LanguageInfo[0]);

    }

    @Override
    protected LanguageInfo[] getNeedAddedObjs() {
        List<LanguageInfo> results = new ArrayList<LanguageInfo>();

        Map<String, String> originalLangCode2LangInfo = getOriginalLang2Info();

        for (LanguageInfo eachCurLangInfo : getCommitedObj().getLanguageInfos()) {

            if (originalLangCode2LangInfo.containsKey(eachCurLangInfo.getLanguageISOCode()))
                continue;

            results.add(eachCurLangInfo);
        }

        return results.toArray(new LanguageInfo[0]);
    }

    @Override
    protected void doRemoveEachObj(XSDAnnotationsStructure xsdAnnoStruct, Object eachNeedRemovedObj) {

        if (!(eachNeedRemovedObj instanceof String))
            return;

        removeLangInfo(xsdAnnoStruct, (String) eachNeedRemovedObj);
    }

    @Override
    protected void doUpdateEachObj(XSDAnnotationsStructure xsdAnnoStruct, Object eachNeedUpdatedObj) {

        if (!(eachNeedUpdatedObj instanceof LanguageInfo))
            return;

        updateLangInfo(xsdAnnoStruct, (LanguageInfo) eachNeedUpdatedObj);
    }

    @Override
    protected void doAddEachObj(XSDAnnotationsStructure xsdAnnoStruct, Object eachNeedAddedObj) {

        if (!(eachNeedAddedObj instanceof LanguageInfo))
            return;

        addLangInfo(xsdAnnoStruct, (LanguageInfo) eachNeedAddedObj);
    }

    protected abstract Map<String, String> getOriginalLang2Info();

    protected abstract void removeLangInfo(XSDAnnotationsStructure xsdAnnoStruct, String langcode);

    protected abstract void updateLangInfo(XSDAnnotationsStructure xsdAnnoStruct, LanguageInfo langInfo);

    protected abstract void addLangInfo(XSDAnnotationsStructure xsdAnnoStruct, LanguageInfo langInfo);
}
