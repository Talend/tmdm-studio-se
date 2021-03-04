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
package org.talend.mdm.repository.core.impl.recyclebin;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.mdm.repository.core.IServerObjectRepositoryType;
import org.talend.mdm.repository.core.impl.AbstractRepositoryNodeResourceProvider;
import org.talend.mdm.repository.model.mdmproperties.ContainerItem;

/**
 * DOC hbhong class global comment. Detailled comment <br/>
 *
 */
public class RecycleBinNodeResourceProvider extends AbstractRepositoryNodeResourceProvider {

    public ERepositoryObjectType getRepositoryObjectType(Item item) {
        if (item instanceof ContainerItem) {
            return IServerObjectRepositoryType.TYPE_RECYCLE_BIN;
        }
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
        return type == IServerObjectRepositoryType.TYPE_RECYCLE_BIN;
    }

}
