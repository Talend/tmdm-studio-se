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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.ERepositoryObjectType;

/**
 * DOC hbhong :for save/create/delete the repository resource <br/>
 *
 */
public interface IRepositoryNodeResourceProvider {

    ERepositoryObjectType getRepositoryObjectType(Item item);

    Resource create(IProject project, Item item, int classifierID, IPath path) throws PersistenceException;

    Resource save(Item item) throws PersistenceException;

    Item createNewItem(ERepositoryObjectType type);

    boolean canHandleRepObjType(ERepositoryObjectType type);

    boolean canHandleItem(Item item);

    boolean needSaveReferenceFile();

    void handleReferenceFile(Item item);

    void postDelete(String name, String... versions);

}
