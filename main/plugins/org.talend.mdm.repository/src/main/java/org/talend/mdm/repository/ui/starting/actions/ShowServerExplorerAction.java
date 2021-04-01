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

import org.apache.log4j.Logger;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.intro.IIntroSite;
import org.talend.repository.token.RepositoryActionLogger;

public class ShowServerExplorerAction extends AbstractShowViewAction {

    private static Logger log = Logger.getLogger(ShowServerExplorerAction.class);

    public void run(IIntroSite iintrosite, Properties properties) {
    	RepositoryActionLogger.logAction(getClass().getName());
    	
        try {
            showServerExplorer();
        } catch (PartInitException e) {
            log.error(e.getMessage(), e);
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see org.talend.mdm.repository.ui.starting.actions.AbstractShowViewAction#doRun(org.eclipse.ui.intro.IIntroSite,
     * java.util.Properties)
     */
    @Override
    protected void doRun(IIntroSite site, Properties params) {
        // do nothing even current user is readonly user ,still can run this function

    }

    /*
     * (non-Javadoc)
     *
     * @see org.talend.mdm.repository.ui.starting.actions.AbstractShowViewAction#getReadOnlyMessage()
     */
    @Override
    protected String getReadOnlyMessage() {
        return null;
    }
}
