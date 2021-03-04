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
import java.util.List;

import com.amalto.workbench.detailtabs.exception.CommitException;
import com.amalto.workbench.detailtabs.exception.CommitValidationException;
import com.amalto.workbench.detailtabs.sections.model.ISubmittable;

public abstract class CompositeCommitHandler<T extends ISubmittable> extends CommitHandler<T> {

    protected List<CommitHandler<T>> childHandlers = new ArrayList<CommitHandler<T>>();

    public CompositeCommitHandler(T submittable) {
        super(submittable);

        for (CommitHandler<T> eachChild : createChildHandlers()) {

            if (eachChild == null || this.childHandlers.contains(eachChild))
                continue;

            this.childHandlers.add(eachChild);
        }
    }

    public void removeChildHandler(CommitHandler<T> handler) {
        childHandlers.remove(handler);
    }

    public void addChildHandler(CommitHandler<T> handler) {

        if (handler == null || this.childHandlers.contains(handler))
            return;

        this.childHandlers.add(handler);
    }

    @Override
    protected void validateCommit() throws CommitValidationException {

        for (CommitHandler<T> eachChild : childHandlers)
            eachChild.validateCommit();

    }

    @Override
    protected boolean doSubmit() throws CommitException {

        boolean result = false;

        for (CommitHandler<T> eachChild : childHandlers)
            result |= eachChild.doSubmit();

        return result;
    }

    protected abstract CommitHandler<T>[] createChildHandlers();
}
