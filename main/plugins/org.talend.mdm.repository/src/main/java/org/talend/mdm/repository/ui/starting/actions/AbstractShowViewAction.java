// ============================================================================
//
// Copyright (C) 2006-2019 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.repository.ui.starting.actions;

import java.util.Properties;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.intro.IIntroSite;
import org.eclipse.ui.intro.config.IIntroAction;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.ui.navigator.MDMRepositoryView;
import org.talend.mdm.workbench.serverexplorer.ui.views.ServerExplorer;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.token.RepositoryActionLogger;

public abstract class AbstractShowViewAction implements IIntroAction {

    public void run(IIntroSite site, Properties params) {
        RepositoryActionLogger.logAction(getClass().getName());
    	
        IProxyRepositoryFactory factory = ProxyRepositoryFactory.getInstance();
        if (factory.isUserReadOnlyOnCurrentProject()) {
            MessageDialog.openWarning(null, Messages.AbstractShowViewAction_authorityTitle, getReadOnlyMessage());
            return;
        }
        doRun(site, params);
    }

    /**
     * DOC HHB Comment method "doRun".
     *
     * @param site
     * @param params
     */
    protected abstract void doRun(IIntroSite site, Properties params);

    protected abstract String getReadOnlyMessage();

    protected ServerExplorer showServerExplorer() throws PartInitException {
        IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        ServerExplorer serverExplorer = (ServerExplorer) activePage.findView(ServerExplorer.ID);

        if (serverExplorer == null) {
            serverExplorer = (ServerExplorer) activePage.showView(ServerExplorer.ID);
        }

        activePage.activate(serverExplorer);

        serverExplorer.setFocus();
        return serverExplorer;
    }

    protected MDMRepositoryView showRepositoryView() throws PartInitException {
        IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        MDMRepositoryView findView = (MDMRepositoryView) activePage.findView(MDMRepositoryView.VIEW_ID);
        if (findView == null)
            findView = (MDMRepositoryView) activePage.showView(MDMRepositoryView.VIEW_ID);

        activePage.activate(findView);

        findView.setFocus();

        return findView;
    }

}
