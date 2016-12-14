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
package org.talend.mdm.repository.core.impl.workspace;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.mdm.repository.core.impl.AbstractRepositoryNodeResourceProvider;
import org.talend.mdm.repository.model.mdmproperties.WorkspaceRootItem;

/**
 * DOC hbhong class global comment. Detailled comment <br/>
 * 
 */
public class WSRootResourceProvider extends AbstractRepositoryNodeResourceProvider {

    public ERepositoryObjectType getRepositoryObjectType(Item item) {

        return null;
    }

    public Resource create(IProject project, Item item, int classifierID, IPath path) throws PersistenceException {

        return null;
    }

    public Resource save(Item item) throws PersistenceException {

        return null;
    }

    public Item createNewItem(ERepositoryObjectType type) {
        return null;
    }

    public boolean canHandleRepObjType(ERepositoryObjectType type) {
        return false;
    }

    @Override
    public boolean canHandleItem(Item item) {
        return item instanceof WorkspaceRootItem;
    }

}
