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
package com.amalto.workbench.detailtabs.sections.model.annotationinfo.relationship;

import org.eclipse.xsd.XSDComponent;

import com.amalto.workbench.detailtabs.sections.handlers.CommitHandler;
import com.amalto.workbench.detailtabs.sections.handlers.ElementForeighKeyCommitHandler;
import com.amalto.workbench.detailtabs.sections.model.annotationinfo.ListContentsAnnotationInfo;

public class ForeignKeyAnnoInfo extends ListContentsAnnotationInfo {

    public ForeignKeyAnnoInfo(XSDComponent sourceXSDComponent, String... xpath) {
        super(sourceXSDComponent, xpath);
    }

    public CommitHandler<ForeignKeyAnnoInfo> createCommitHandler() {
        return new ElementForeighKeyCommitHandler(this);
    }
}
