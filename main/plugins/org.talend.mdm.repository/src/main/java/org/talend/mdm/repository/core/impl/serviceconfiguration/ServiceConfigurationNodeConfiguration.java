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
package org.talend.mdm.repository.core.impl.serviceconfiguration;

import org.talend.mdm.repository.core.impl.RepositoryNodeConfigurationAdapter;

/**
 * DOC jsxie class global comment. Detailled comment <br/>
 *
 */
public class ServiceConfigurationNodeConfiguration extends RepositoryNodeConfigurationAdapter {

    public ServiceConfigurationNodeConfiguration() {
        setResourceProvider(new ServiceConfigurationNodeResourceProvider());

        setActionProvider(new ServiceConfigurationActionProvider());
    }

    @Override
    protected void initLabelProvider() {
        setLabelProvider(new ServiceConfigurationLabelProvider());
    }

    @Override
    protected void initContentProvider() {
        setContentProvider(new ServiceConfigurationContentProvider());
    }
}
