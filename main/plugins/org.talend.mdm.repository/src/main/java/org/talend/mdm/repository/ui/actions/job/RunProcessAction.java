// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.repository.ui.actions.job;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.core.model.general.Project;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.designer.core.ui.action.RunProcess;
import org.talend.mdm.repository.core.bridge.AbstractBridgeRepositoryAction;
import org.talend.repository.ProjectManager;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class RunProcessAction extends AbstractBridgeRepositoryAction {

    static Logger log = Logger.getLogger(RunProcessAction.class);
    private static final String PROCESS_VIEW_ID = "org.talend.designer.runprocess.ui.views.processview"; //$NON-NLS-1$
    static class MDMRunProcess extends RunProcess {

        @Override
        protected RepositoryNode initCurrentRepositoryNode() {
            ISelection selection = getSelection();
            if (selection == null || selection.isEmpty() || !(selection instanceof IStructuredSelection)) {
                return null;
            }
            Object object = ((IStructuredSelection) selection).getFirstElement();
            if (object instanceof IRepositoryNode) {
                RepositoryNode node = (RepositoryNode) object;
                if (node == null || node.getObject() == null || !(node.getObject() instanceof RepositoryNode)) {
                    return node;
                }
                Property property = node.getObject().getProperty();
                Property updatedProperty = null;

                try {
                    IProxyRepositoryFactory proxyRepositoryFactory = CoreRuntimePlugin.getInstance().getProxyRepositoryFactory();
                    proxyRepositoryFactory.initialize();

                    updatedProperty = proxyRepositoryFactory.getLastVersion(
                            new Project(ProjectManager.getInstance().getProject(property.getItem())), property.getId())
                            .getProperty();
                } catch (PersistenceException e) {
                    ExceptionHandler.process(e);
                }

                return node;

            }
            return null;
        }

    }

    @Override
    protected void doRun() {
        activeProcessView();
        super.doRun();
    }
    private void activeProcessView() {
        IWorkbench workbench = PlatformUI.getWorkbench();
        if (workbench != null) {
            IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
            if (window != null) {
                IWorkbenchPage page = window.getActivePage();
                if (page != null) {
                    try {
                        page.showView(PROCESS_VIEW_ID);
                    } catch (PartInitException e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
        }
    }
    /**
     * DOC hbhong EditProcessAction constructor comment.
     * 
     * @param cAction
     */
    public RunProcessAction() {
        super(new MDMRunProcess());
    }

    @Override
    public String getGroupName() {
        return GROUP_EDIT;
    }

    @Override
    public boolean isVisible(IRepositoryViewObject viewObj) {
        if (getSelectedObject().size() > 1) {
            return false;
        }

        return super.isVisible(viewObj);
    }
}
