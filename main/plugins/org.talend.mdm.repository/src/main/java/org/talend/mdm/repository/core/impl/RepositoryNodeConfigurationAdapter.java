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
package org.talend.mdm.repository.core.impl;

import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.mdm.repository.core.IRepositoryNodeActionProvider;
import org.talend.mdm.repository.core.IRepositoryNodeConfiguration;
import org.talend.mdm.repository.core.IRepositoryNodeContentProvider;
import org.talend.mdm.repository.core.IRepositoryNodeLabelProvider;
import org.talend.mdm.repository.core.IRepositoryNodeResourceProvider;

/**
 * DOC hbhong class global comment. Detailled comment <br/>
 *
 */
public class RepositoryNodeConfigurationAdapter implements IRepositoryNodeConfiguration {

    IRepositoryNodeResourceProvider resourceProvider;

    IRepositoryNodeLabelProvider labelProvider;

    IRepositoryNodeContentProvider contentProvider;

    IRepositoryNodeActionProvider actionProvider;

    public IRepositoryNodeActionProvider getActionProvider() {
        return this.actionProvider;
    }

    public void setActionProvider(IRepositoryNodeActionProvider actionProvider) {
        this.actionProvider = actionProvider;
    }

    public IRepositoryNodeContentProvider getContentProvider() {
        if (contentProvider == null) {
            initContentProvider();
        }
        return this.contentProvider;
    }

    public void setContentProvider(IRepositoryNodeContentProvider contentProvider) {
        this.contentProvider = contentProvider;
    }

    public boolean canHandleRepObjType(ERepositoryObjectType type) {

        return resourceProvider.canHandleRepObjType(type);
    }

    public boolean canHandleItem(Item item) {
        return resourceProvider.canHandleItem(item);
    }

    public IRepositoryNodeLabelProvider getLabelProvider() {
        if (labelProvider == null) {
            initLabelProvider();
        }
        return this.labelProvider;
    }

    public void setLabelProvider(IRepositoryNodeLabelProvider labelProvider) {
        this.labelProvider = labelProvider;
    }

    public void setResourceProvider(IRepositoryNodeResourceProvider resourceProvider) {
        this.resourceProvider = resourceProvider;
    }

    public IRepositoryNodeResourceProvider getResourceProvider() {
        return resourceProvider;
    }

    protected void initLabelProvider() {

    }

    protected void initContentProvider() {

    }
}
