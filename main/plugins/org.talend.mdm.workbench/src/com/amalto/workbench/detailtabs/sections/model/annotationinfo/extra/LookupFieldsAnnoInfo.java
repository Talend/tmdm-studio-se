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
package com.amalto.workbench.detailtabs.sections.model.annotationinfo.extra;

import org.eclipse.xsd.XSDComponent;

import com.amalto.workbench.detailtabs.sections.handlers.CommitHandler;
import com.amalto.workbench.detailtabs.sections.handlers.LookupFieldsCommitHandler;
import com.amalto.workbench.detailtabs.sections.model.annotationinfo.ListContentsAnnotationInfo;

public class LookupFieldsAnnoInfo extends ListContentsAnnotationInfo {

    public LookupFieldsAnnoInfo(XSDComponent sourceComponent, String[] infos) {
        super(sourceComponent, infos);
    }

    public CommitHandler<LookupFieldsAnnoInfo> createCommitHandler() {
        return new LookupFieldsCommitHandler(this);
    }

}
