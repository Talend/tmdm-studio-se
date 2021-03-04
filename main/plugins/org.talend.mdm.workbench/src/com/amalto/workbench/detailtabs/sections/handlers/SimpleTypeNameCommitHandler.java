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
import com.amalto.workbench.detailtabs.sections.model.simpletype.SimpleTypeWrapper;
import com.amalto.workbench.i18n.Messages;

public class SimpleTypeNameCommitHandler extends CommitHandler<SimpleTypeWrapper> {

    public SimpleTypeNameCommitHandler(SimpleTypeWrapper submittable) {
        super(submittable);
    }

    @Override
    protected void validateCommit() throws CommitValidationException {
        if (getCommitedObj().getNewTypeName().replaceAll("\\s", "").length() != getCommitedObj().getNewTypeName().length()) { //$NON-NLS-1$ //$NON-NLS-2$
            throw new CommitValidationException(Messages.SimpleTypeNameCommitHandler_TypeNameCannotContainEmpty);
        }
    }

    @Override
    protected boolean doSubmit() throws CommitException {
        return getCommitedObj().changeTypeName();
    }

}
