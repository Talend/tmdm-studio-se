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
package org.talend.mdm.workbench.serverexplorer.ui.actions;

import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.model.mdmmetadata.MDMServerDef;
import org.talend.mdm.repository.model.mdmproperties.MDMServerDefItem;
import org.talend.mdm.workbench.serverexplorer.console.MDMServerLogConsoleFactory;
import org.talend.mdm.workbench.serverexplorer.i18n.Messages;
import org.talend.repository.token.RepositoryActionLogger;

public class ShowServerConsoleAction extends AbstractServerDefAction {

    public ShowServerConsoleAction() {
        setText(Messages.ServerExplorer_ViewLogAction_Text);
    }

    @Override
    public void run() {
        RepositoryActionLogger.logAction(getClass().getName());
    	
        IRepositoryViewObject viewObject = getSelectedViewObject();
        if (viewObject != null) {
            MDMServerDefItem serverDefItem = getMDMItem(viewObject);
            MDMServerDef selectedServerDef = serverDefItem.getServerDef();
            if (selectedServerDef != null) {
                showMDMServerConsole(selectedServerDef);
            }
        }
    }

    protected void showMDMServerConsole(MDMServerDef selectedServerDef) {
        new MDMServerLogConsoleFactory().showMDMServerConsole(selectedServerDef.getDecryptedServerDef());
    }
}
