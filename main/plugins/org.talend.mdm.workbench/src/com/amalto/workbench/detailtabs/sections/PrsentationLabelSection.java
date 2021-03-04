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

import java.util.Map;

import com.amalto.workbench.detailtabs.sections.model.ISubmittable;
import com.amalto.workbench.detailtabs.sections.model.annotationinfo.langinfo.LanguageInfoCollection;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.utils.XSDAnnotationsStructure;

public class PrsentationLabelSection extends LanguageInfoSection {

    @Override
    protected Map<String, String> getLang2Info(XSDAnnotationsStructure xsdAnnoStruct) {
        return xsdAnnoStruct.getLabels();
    }

    @Override
    protected String getSectionTitle() {
        return Messages.PrsentationLabelSection_Labels;
    }

    @Override
    protected ISubmittable getSubmittedObj() {
        return LanguageInfoCollection.createLabelInfoCollection(getEditedObj(), getLangInfos());
    }
}
