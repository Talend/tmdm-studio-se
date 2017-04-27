// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.repository.ui.widgets.xmleditor.infoholder;

import java.util.List;

import org.apache.log4j.Logger;
import org.talend.mdm.repository.model.mdmmetadata.MDMServerDef;
import org.talend.mdm.webservice.TMDMService;
import org.talend.mdm.webservice.WSMDMConfig;
import org.talend.mdm.workbench.serverexplorer.core.ServerDefService;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class RepositoryMDMServerInfoHolder extends RepositoryExternalInfoHolder<WSMDMConfig[]> {

    static Logger log = Logger.getLogger(RepositoryMDMServerInfoHolder.class);

    public RepositoryMDMServerInfoHolder(TMDMService service) {

    }

    @Override
    public WSMDMConfig[] getExternalInfo() {
        List<MDMServerDef> allServerDefs = ServerDefService.getAllServerDefs(true);
        WSMDMConfig[] configs = new WSMDMConfig[allServerDefs.size()];
        int i = 0;
        for (MDMServerDef def : allServerDefs) {
            configs[i] = new WSMDMConfig();
            configs[i].setServerName(def.getHost());
            configs[i].setServerPort(def.getPort());
            configs[i].setUserName(def.getUser());
            configs[i].setPassword(def.getPasswd());
            configs[i].setXdbID(def.getName());
            i++;
        }
        return configs;
    }

    @Override
    public String getId() {
        return INFOID_ALLMDMSERVERINFO;
    }

}
