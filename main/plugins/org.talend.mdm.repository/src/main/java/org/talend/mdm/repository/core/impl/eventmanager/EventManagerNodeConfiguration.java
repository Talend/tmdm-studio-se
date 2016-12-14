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
package org.talend.mdm.repository.core.impl.eventmanager;

import org.talend.mdm.repository.core.impl.RepositoryNodeConfigurationAdapter;

/**
 * DOC hbhong class global comment. Detailled comment <br/>
 * 
 */
public class EventManagerNodeConfiguration extends RepositoryNodeConfigurationAdapter {

    public EventManagerNodeConfiguration() {
        setResourceProvider(new EventManagerNodeResourceProvider());
        setActionProvider(new EventManagerActionProvider());
    }

    @Override
    protected void initLabelProvider() {
        setLabelProvider(new EventManagerLabelProvider());
    }

    @Override
    protected void initContentProvider() {
        setContentProvider(new EventManagerContentProvider());
    }

}
