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

import java.util.Map;

import org.talend.mdm.repository.model.mdmmetadata.MDMServerDef;
import org.talend.mdm.workbench.serverexplorer.plugin.MDMServerExplorerPlugin;

/**
 * created by liusongbo on 2013-10-16
 */
public class MDMServerLogConsoleFactory extends MDMServerConsoleFactory {

    @Override
    protected Map<String, MDMServerMessageConsole> getServerToConsole() {
        return MDMServerExplorerPlugin.getDefault().getServerToConsole();
    }

    @Override
    protected MDMServerMessageConsole createMDMServerMessageConsole(MDMServerDef serverDef) {
        return new MDMServerLogMessageConsole(serverDef);
    }
}
