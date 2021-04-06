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
package org.talend.mdm.repository.ui.actions;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.ui.navigator.CommonDropAdapterAssistant;
import org.eclipse.ui.navigator.INavigatorDnDService;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.AbstractRepositoryAction;
import org.talend.mdm.repository.core.IServerObjectRepositoryType;
import org.talend.mdm.repository.core.dnd.RepositoryDropAssistant;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.repository.token.RepositoryActionLogger;

import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class DuplicateAction extends AbstractRepositoryAction {

    private static Logger log = Logger.getLogger(DuplicateAction.class);
    /**
     * DOC hbhong DuplicateAction constructor comment.
     *
     * @param text
     */
    public DuplicateAction() {
        super(Messages.DuplicateAction_duplicate);
        setImageDescriptor(ImageCache.getImage(EImage.DUPLICATE.getPath()));
    }

    public String getGroupName() {
        return GROUP_COMMON;
    }

    @Override
    public void run() {
        IRepositoryViewObject viewObj = getSelectedViewObj();
        if (IServerObjectRepositoryType.TYPE_WORKFLOW == viewObj.getRepositoryObjectType()) {
            RepositoryActionLogger.logAction(getClass().getName());
        	
            final IWorkspaceRunnable op = new IWorkspaceRunnable() {

                @Override
                public void run(IProgressMonitor monitor) {
                    DuplicateAction.super.run();
                }
            };

            IWorkspace workspace = ResourcesPlugin.getWorkspace();
            try {
                ISchedulingRule scheduleRule = workspace.getRuleFactory().createRule(workspace.getRoot());
                workspace.run(op, scheduleRule, IWorkspace.AVOID_UPDATE, new NullProgressMonitor());
            } catch (CoreException e) {
                log.error(e.getMessage(), e);
            }

        } else {
            super.run();
        }
    }

    protected void doRun() {
        RepositoryDropAssistant dropAssistant = getDropAssistant();

        if (dropAssistant != null) {
            IRepositoryViewObject viewObj = getSelectedViewObj();
            boolean result = dropAssistant.copyViewObj(viewObj, viewObj);
            if (result) {
                refreshContainer(dropAssistant, viewObj);
            }
        }
    }

    private void refreshContainer(RepositoryDropAssistant dropAssistant, IRepositoryViewObject viewObject) {
        IRepositoryViewObject parentViewObject = dropAssistant.getParentRepositoryViewObject(viewObject);
        if (parentViewObject != null)
            commonViewer.refresh(parentViewObject);
    }

    private RepositoryDropAssistant getDropAssistant() {
        INavigatorDnDService dndService = commonViewer.getNavigatorContentService().getDnDService();
        CommonDropAdapterAssistant[] dropAssistants = dndService.findCommonDropAdapterAssistants(getSelectedViewObj(),
                getStructuredSelection());
        for (CommonDropAdapterAssistant assistant : dropAssistants) {
            if (assistant instanceof RepositoryDropAssistant) {
                return (RepositoryDropAssistant) assistant;
            }
        }
        return null;
    }

    private IRepositoryViewObject getSelectedViewObj() {
        return (IRepositoryViewObject) getSelectedObject().get(0);
    }

    public boolean isVisible(IRepositoryViewObject viewObj) {
        return getSelectedObject().size() == 1;
    }
}
