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
package com.amalto.workbench.task;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.talend.login.AbstractLoginTask;

import com.amalto.workbench.service.MissingJarService;

/**
 * created by HHB on 2013-11-01 Detailled comment
 *
 */
public class CheckMissingJarLoginTask extends AbstractLoginTask implements IRunnableWithProgress {

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
     */
    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        MissingJarService.getInstance().initialCheck();
    }

}
