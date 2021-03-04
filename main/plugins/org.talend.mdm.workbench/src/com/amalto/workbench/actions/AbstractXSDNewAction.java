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
package com.amalto.workbench.actions;

import org.eclipse.jface.action.Action;
import org.talend.commons.exception.LoginException;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.repository.RepositoryWorkUnit;

/**
 * created by liusongbo on Sep 26, 2014
 */
public abstract class AbstractXSDNewAction extends Action {

    @Override
    public void run() {
        runRWU();
    }

    protected void runRWU() {
        RepositoryWorkUnit<Object> repositoryWorkUnit = new RepositoryWorkUnit<Object>("", this) { //$NON-NLS-1$

            @Override
            protected void run() throws LoginException, PersistenceException {
                doRun();
            }
        };
        repositoryWorkUnit.setAvoidUnloadResources(true);
        CoreRuntimePlugin.getInstance().getProxyRepositoryFactory().executeRepositoryWorkUnit(repositoryWorkUnit);
    }

    protected abstract void doRun();
}
