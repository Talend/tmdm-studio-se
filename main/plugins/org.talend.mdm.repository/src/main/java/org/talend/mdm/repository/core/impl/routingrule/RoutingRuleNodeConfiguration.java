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
package org.talend.mdm.repository.core.impl.routingrule;

import org.talend.mdm.repository.core.impl.RepositoryNodeConfigurationAdapter;

/**
 * DOC hbhong class global comment. Detailled comment <br/>
 *
 */
public class RoutingRuleNodeConfiguration extends RepositoryNodeConfigurationAdapter {

    public RoutingRuleNodeConfiguration() {
        setResourceProvider(new RoutingRuleNodeResourceProvider());

        setActionProvider(new RoutingRuleActionProvider());
    }

    @Override
    protected void initLabelProvider() {
        setLabelProvider(new RoutingRuleLabelProvider());
    }

    @Override
    protected void initContentProvider() {
        setContentProvider(new RoutingRuleContentProvider());
    }
}
