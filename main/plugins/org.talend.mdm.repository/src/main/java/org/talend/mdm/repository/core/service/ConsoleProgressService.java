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
package org.talend.mdm.repository.core.service;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.progress.IProgressService;


/**
 * DOC hbhong  class global comment. Detailled comment
 */
public class ConsoleProgressService implements IProgressService {

    static ConsoleProgressService instance = new ConsoleProgressService();


    public static ConsoleProgressService getInstance() {
        return instance;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.progress.IProgressService#getLongOperationTime()
     */
    public int getLongOperationTime() {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.progress.IProgressService#registerIconForFamily(org.eclipse.jface.resource.ImageDescriptor, java.lang.Object)
     */
    public void registerIconForFamily(ImageDescriptor icon, Object family) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.progress.IProgressService#runInUI(org.eclipse.jface.operation.IRunnableContext, org.eclipse.jface.operation.IRunnableWithProgress, org.eclipse.core.runtime.jobs.ISchedulingRule)
     */
    public void runInUI(IRunnableContext context, IRunnableWithProgress runnable, ISchedulingRule rule)
            throws InvocationTargetException, InterruptedException {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.progress.IProgressService#getIconFor(org.eclipse.core.runtime.jobs.Job)
     */
    public Image getIconFor(Job job) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.progress.IProgressService#busyCursorWhile(org.eclipse.jface.operation.IRunnableWithProgress)
     */
    public void busyCursorWhile(IRunnableWithProgress runnable) throws InvocationTargetException, InterruptedException {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.progress.IProgressService#run(boolean, boolean, org.eclipse.jface.operation.IRunnableWithProgress)
     */
    public void run(boolean fork, boolean cancelable, IRunnableWithProgress runnable) throws InvocationTargetException,
            InterruptedException {
        runnable.run(new NullProgressMonitor());

    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.progress.IProgressService#showInDialog(org.eclipse.swt.widgets.Shell, org.eclipse.core.runtime.jobs.Job)
     */
    public void showInDialog(Shell shell, Job job) {
        // TODO Auto-generated method stub

    }

}
