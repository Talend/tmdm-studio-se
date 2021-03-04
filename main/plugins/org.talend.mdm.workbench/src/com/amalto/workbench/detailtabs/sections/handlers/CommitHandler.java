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
import com.amalto.workbench.detailtabs.sections.model.ISubmittable;

public abstract class CommitHandler<T extends ISubmittable> {

    protected T submittedObj;

    public CommitHandler(T submittedObj) {
        this.submittedObj = submittedObj;
    }

    public T getCommitedObj() {
        return submittedObj;
    }

    protected abstract void validateCommit() throws CommitValidationException;

    protected abstract boolean doSubmit() throws CommitException;

    /**
     * update the changes
     *
     * @return true means there are some changes committed, otherwise return false
     *
     * @throws CommitException
     * @throws CommitValidationException
     */
    public boolean submit() throws CommitException, CommitValidationException {

        validateCommit();

        return doSubmit();
    }
}
