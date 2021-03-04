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
import com.amalto.workbench.detailtabs.sections.model.annotationinfo.extra.LookupFieldsAnnoInfo;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.utils.XSDAnnotationsStructure;

public class LookupFieldsCommitHandler extends ListContentsCommitHandler<LookupFieldsAnnoInfo> {

    private static final String ANNOTAG = "X_Lookup_Field";//$NON-NLS-1$

    public LookupFieldsCommitHandler(LookupFieldsAnnoInfo submittedInfo) {
        super(submittedInfo);
    }

    @Override
    protected String getMsgHeader() {
        return Messages.LookupFieldsCommitHandler_MsgHeader;
    }

    @Override
    protected String[] getOriginalAnnoInfos() {
        return getXSDAnnotationStruct().getLookupFields().values().toArray(new String[0]);
    }

    @Override
    protected boolean doSubmit() throws CommitException {

        XSDAnnotationsStructure xsdAnnoStruct = getXSDAnnotationStruct();
        try {
            return xsdAnnoStruct.setAccessRole(Arrays.asList(getCommitedObj().getInfos()), false, null, ANNOTAG);
        } catch (Exception e) {
            throw new CommitException(e.getMessage(), e);
        }
    }

}
