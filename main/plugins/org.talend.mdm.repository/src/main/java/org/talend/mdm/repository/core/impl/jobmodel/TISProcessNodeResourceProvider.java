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
package org.talend.mdm.repository.core.impl.jobmodel;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ProcessItem;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.mdm.repository.core.impl.AbstractRepositoryNodeResourceProvider;
import org.talend.mdm.repository.model.mdmproperties.ContainerItem;

/**
 * DOC hbhong class global comment. Detailled comment <br/>
 *
 */
public class TISProcessNodeResourceProvider extends AbstractRepositoryNodeResourceProvider {

    public ERepositoryObjectType getRepositoryObjectType(Item item) {
        if (item.eClass().getInstanceClass() == ProcessItem.class || item instanceof ContainerItem) {
            return ERepositoryObjectType.PROCESS;
        }
        return null;
    }

    public Resource create(IProject project, Item item, int classifierID, IPath path) throws PersistenceException {
        // ERepositoryObjectType repositoryType = getRepositoryObjectType(item);
        // if (repositoryType != null) {
        // Resource itemResource = createCommonItemResource(project, item, repositoryType, path);
        // EList<EObject> contents = itemResource.getContents();
        // contents.add(((WSJobModelItem) item).getWsJobModelItem());
        // return itemResource;
        // }
        return null;
    }

    @Override
    public Resource save(Item item) throws PersistenceException {
        // if (item instanceof WSJobModelItem) {
        // Resource resource = xmiResourceManager.getItemResource(item);
        // resource.getContents().clear();
        // resource.getContents().add(((WSJobModelItem) item).getWsJobModelItem());
        // Resource eResource = ((WSJobModelItem) item).getWsJobModelItem().eResource();
        // return resource;
        // }
        return null;
    }

    public Item createNewItem(ERepositoryObjectType type) {
        // return MdmpropertiesFactory.eINSTANCE.createWSJobModelItem();
        return null;
    }

    public boolean canHandleRepObjType(ERepositoryObjectType type) {
        return type == ERepositoryObjectType.PROCESS;
    }

}
