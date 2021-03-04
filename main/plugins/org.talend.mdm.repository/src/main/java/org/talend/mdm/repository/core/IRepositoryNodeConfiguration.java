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
package org.talend.mdm.repository.core;

import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.ERepositoryObjectType;

/**
 * DOC hbhong class global comment. Detailled comment <br/>
 *
 */
public interface IRepositoryNodeConfiguration {

    public boolean canHandleRepObjType(ERepositoryObjectType type);

    public boolean canHandleItem(Item item);

    // resource
    public void setResourceProvider(IRepositoryNodeResourceProvider resourceProvider);

    public IRepositoryNodeResourceProvider getResourceProvider();

    // label
    public IRepositoryNodeLabelProvider getLabelProvider();

    public void setLabelProvider(IRepositoryNodeLabelProvider labelProvider);

    // content
    public IRepositoryNodeContentProvider getContentProvider();

    public void setContentProvider(IRepositoryNodeContentProvider contentProvider);

    // action
    public IRepositoryNodeActionProvider getActionProvider();

    public void setActionProvider(IRepositoryNodeActionProvider actionProvider);

}
