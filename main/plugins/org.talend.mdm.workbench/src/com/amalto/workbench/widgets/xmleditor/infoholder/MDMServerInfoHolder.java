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
package com.amalto.workbench.widgets.xmleditor.infoholder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.amalto.workbench.webservices.TMDMService;
import com.amalto.workbench.webservices.WSMDMConfig;

public class MDMServerInfoHolder extends ExternalInfoHolder<WSMDMConfig> {

    private static Log log = LogFactory.getLog(MDMServerInfoHolder.class);

    private TMDMService service;

    public MDMServerInfoHolder(TMDMService service) {
        this.service = service;
    }

    @Override
    public WSMDMConfig getExternalInfo() {
        return service.getMDMConfiguration();
    }

    @Override
    public String getId() {
        return INFOID_MDMSERVERINFO;
    }

}
