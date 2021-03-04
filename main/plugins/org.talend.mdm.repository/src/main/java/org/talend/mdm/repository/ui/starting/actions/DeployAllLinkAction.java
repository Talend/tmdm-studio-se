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
package org.talend.mdm.repository.ui.starting.actions;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.intro.IIntroSite;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.ui.actions.DeployAllAction;
import org.talend.mdm.repository.ui.navigator.MDMRepositoryView;

public class DeployAllLinkAction extends AbstractShowViewAction {

    private static Logger log = Logger.getLogger(DeployAllLinkAction.class);

    protected void doRun(IIntroSite site, Properties params) {
        IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        MDMRepositoryView findView = (MDMRepositoryView) activePage.findView(MDMRepositoryView.VIEW_ID);
        try {
            if (findView == null)
                findView = (MDMRepositoryView) activePage.showView(MDMRepositoryView.VIEW_ID);
            findView.setFocus();

            DeployAllAction deployAllAction = findView.getDeployAllAction();
            deployAllAction.runWithType(null);

        } catch (PartInitException e) {
            log.error(e.getMessage(), e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.talend.mdm.repository.ui.starting.actions.AbstractShowViewAction#getReadOnlyMessage()
     */
    @Override
    protected String getReadOnlyMessage() {
        return Messages.DeployAllLinkAction_authorityMessage;
    }

}
