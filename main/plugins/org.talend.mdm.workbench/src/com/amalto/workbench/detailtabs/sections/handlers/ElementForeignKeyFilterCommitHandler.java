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

import com.amalto.workbench.detailtabs.exception.CommitException;
import com.amalto.workbench.detailtabs.exception.CommitValidationException;
import com.amalto.workbench.detailtabs.sections.model.annotationinfo.relationship.ForeignKeyFilterAnnoInfo;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.utils.XSDAnnotationsStructure;

public class ElementForeignKeyFilterCommitHandler extends ListContentsCommitHandler<ForeignKeyFilterAnnoInfo> {

    public ElementForeignKeyFilterCommitHandler(ForeignKeyFilterAnnoInfo submittedFKFilter) {
        super(submittedFKFilter);
    }

    @Override
    protected String getMsgHeader() {
        return Messages.ElementForeignKeyFilterCommitHandler_MsgHeader;
    }

    @Override
    protected String[] getOriginalAnnoInfos() {
        return new String[] { getXSDAnnotationStruct().getFKFilter() };
    }

    @Override
    protected void validateCommit() throws CommitValidationException {

    }

    @Override
    protected boolean doSubmit() throws CommitException {

        XSDAnnotationsStructure xsdAnnoStruct = getXSDAnnotationStruct();

        try {
            xsdAnnoStruct.setFKFilter(getCommitedObj().getValue());
            doUpdateFKAnnotationStructure(xsdAnnoStruct);
        } catch (Exception e) {
            throw new CommitException(e.getMessage(), e);
        }

        return true;
    }
}
