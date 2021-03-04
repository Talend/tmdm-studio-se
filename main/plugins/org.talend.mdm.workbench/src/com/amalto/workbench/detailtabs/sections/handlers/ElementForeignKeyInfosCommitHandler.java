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

import java.util.Arrays;

import com.amalto.workbench.detailtabs.exception.CommitException;
import com.amalto.workbench.detailtabs.exception.CommitValidationException;
import com.amalto.workbench.detailtabs.sections.model.annotationinfo.relationship.ForeignKeyInfosAnnoInfo;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.utils.XSDAnnotationsStructure;

public class ElementForeignKeyInfosCommitHandler extends ListContentsCommitHandler<ForeignKeyInfosAnnoInfo> {

    public ElementForeignKeyInfosCommitHandler(ForeignKeyInfosAnnoInfo submittedFKInfosAnnoInfo) {
        super(submittedFKInfosAnnoInfo);
    }

    @Override
    protected String getMsgHeader() {
        return Messages.ElementForeignKeyInfosCommitHandler_MsgHeader;
    }

    @Override
    protected String[] getOriginalAnnoInfos() {
        return getXSDAnnotationStruct().getForeignKeyInfos().values().toArray(new String[0]);
    }

    @Override
    protected void validateCommit() throws CommitValidationException {
    }

    @Override
    protected boolean doSubmit() throws CommitException {

        XSDAnnotationsStructure xsdAnnoStruct = getXSDAnnotationStruct();

        try {
            xsdAnnoStruct.setForeignKeyInfos(Arrays.asList(getCommitedObj().getInfos()));
            xsdAnnoStruct.setFormatForeignKeyInfo(getCommitedObj().getFormatFkInfo());
            doUpdateFKAnnotationStructure(xsdAnnoStruct);
        } catch (Exception e) {
            throw new CommitException(e.getMessage(), e);
        }

        return true;
    }
}
