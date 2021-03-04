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
package org.talend.mdm.workbench.serverexplorer.console;

import org.talend.mdm.repository.model.mdmmetadata.MDMServerDef;
import org.talend.mdm.workbench.serverexplorer.i18n.Messages;
import org.talend.mdm.workbench.serverexplorer.plugin.MDMServerExplorerPlugin;

/**
 * created by liusonbo on 2013-10-17
 */
public class MDMServerLogMessageConsole extends MDMServerMessageConsole {

    public MDMServerLogMessageConsole(MDMServerDef serverDef) {
        super(serverDef);
    }

    @Override
    protected String getLogPath() {
        return "/services/logviewer/log"; //$NON-NLS-1$
    }

    @Override
    protected String getConsoleTitle() {
        MDMServerDef serverDef = getServerDef();
        if (serverDef != null && serverDef.getName() != null) {
            return Messages.bind(Messages.MDMServerMessageConsole_Name, serverDef.getName());
        }

        return ""; //$NON-NLS-1$
    }

    @Override
    protected void removeFromCache(String serverName) {
        if (serverName != null) {
            MDMServerExplorerPlugin.getDefault().getServerToConsole().remove(serverName);
        }
    }

    @Override
    protected String getLogFlag() {
        return Messages.MDMServerMessageConsole_mdmFlag;
    }

}
