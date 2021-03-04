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
package org.talend.mdm.repository.ui.actions.recyclebin;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.PlatformUI;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.i18n.Messages;

import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class RemovePhysicallyFromRepositoryAction extends AbstractRemoveCommandStackAction {

    /**
     * DOC hbhong RemoveFromRepositoryAction constructor comment.
     *
     * @param text
     */
    public RemovePhysicallyFromRepositoryAction() {
        super(Messages.RemovePhysicallyFromRepositoryAction_title);
        setImageDescriptor(ImageCache.getImage(EImage.DELETE_OBJ.getPath()));
    }

    @Override
    public String getGroupName() {
        return GROUP_EDIT;
    }

    @Override
    protected void doRun() {

        final IWorkspaceRunnable op = new IWorkspaceRunnable() {

            public void run(IProgressMonitor monitor) throws CoreException {
                int size = getSelectedObject().size();
                if (size > 0) {
                    if (!MessageDialog.openConfirm(getShell(), Messages.RemoveFromRepositoryAction_Title, Messages.bind(
                            Messages.RemoveFromRepositoryAction_confirm, size,
                            size > 1 ? Messages.RemoveFromRepositoryAction_instances
                                    : Messages.RemoveFromRepositoryAction_instance))) {
                        return;
                    }

                }
                List<IRepositoryViewObject> viewObjs = new ArrayList<IRepositoryViewObject>();
                for (Object obj : getSelectedObject()) {
                    if (obj instanceof IRepositoryViewObject) {
                        IRepositoryViewObject viewObj = (IRepositoryViewObject) obj;

                        viewObjs.add(viewObj);
                    }
                }
                removeViewObjects(viewObjs);
            }

        };
        //
        IRunnableWithProgress iRunnableWithProgress = new IRunnableWithProgress() {

            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                IWorkspace workspace = ResourcesPlugin.getWorkspace();
                try {
                    ISchedulingRule schedulingRule = workspace.getRoot();

                    workspace.run(op, schedulingRule, IWorkspace.AVOID_UPDATE, monitor);
                } catch (CoreException e) {
                    throw new InvocationTargetException(e);
                }

            }
        };

        try {
            PlatformUI.getWorkbench().getProgressService().run(false, false, iRunnableWithProgress);

        } catch (Exception e) {
            ExceptionHandler.process(e);
        }

    }

}
