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
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.intro.IIntroSite;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.workbench.serverexplorer.ui.views.ServerExplorer;
import org.talend.mdm.workbench.serverexplorer.ui.views.ServerExplorer.AddServerDefAction;

public class AddServerDefLinkAction extends AbstractShowViewAction {

    private static Logger log = Logger.getLogger(AddServerDefLinkAction.class);

    protected void doRun(IIntroSite site, Properties params) {
        try {
            ServerExplorer serverExplorer = showServerExplorer();

            AddServerDefAction addServerDefAction = serverExplorer.getAddServerDefAction();
            addServerDefAction.run();
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
        return Messages.AddServerDefLinkAction_authorityMessage;
    }
}
