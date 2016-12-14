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
package org.talend.mdm.repository.core.impl;

import java.util.List;

import org.talend.core.model.properties.FolderType;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.IRepositoryNodeContentProvider;
import org.talend.mdm.repository.extension.RepositoryNodeConfigurationManager;
import org.talend.mdm.repository.model.mdmproperties.ContainerItem;
import org.talend.mdm.repository.models.FolderRepositoryObject;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;

/**
 * DOC hbhong class global comment. Detailled comment <br/>
 * 
 */
public abstract class AbstractContentProvider implements IRepositoryNodeContentProvider {

    public Object[] getChildren(Object element) {
        Item item = RepositoryResourceUtil.getItemFromRepViewObj(element);
        if (item != null && item instanceof ContainerItem) {

            ContainerItem containerItem = (ContainerItem) item;
            // recycle
            if (RepositoryResourceUtil.isDeletedFolder(item, ((IRepositoryViewObject) element).getRepositoryObjectType())) {
                AbstractContentProvider recycleContentProvider = (AbstractContentProvider) RepositoryNodeConfigurationManager
                        .getRecycleBinNodeConfiguration().getContentProvider();
                return recycleContentProvider.getChildren(element);
            }
            //
            FolderType containerType = containerItem.getType();
            List<IRepositoryViewObject> children = ((FolderRepositoryObject) element).getChildren();
            if (containerType == FolderType.SYSTEM_FOLDER_LITERAL) {
                List<IRepositoryViewObject> viewObjects = getViewObjFromSystemFolder(containerItem);
                if (viewObjects != null) {
                    children.clear();
                    children.addAll(viewObjects);
                    return viewObjects.toArray();
                }
            }
            if (containerType == FolderType.FOLDER_LITERAL || containerType == FolderType.STABLE_SYSTEM_FOLDER_LITERAL) {
                List<IRepositoryViewObject> viewObjects = getViewObjFromFolder(containerItem);
                if (viewObjects != null) {
                    children.clear();
                    children.addAll(viewObjects);
                    return viewObjects.toArray();
                }
            }
            // if (containerType == FolderType.STABLE_SYSTEM_FOLDER_LITERAL) {
            // List<IRepositoryViewObject> children = ((ContainerRepositoryObject) element).getChildren();
            // if (children != null) {
            // return children.toArray();
            // }
            // }
        }
        return new Object[0];
    }

    protected abstract List<IRepositoryViewObject> getViewObjFromSystemFolder(Item parentItem);

    protected List<IRepositoryViewObject> getViewObjFromFolder(ContainerItem containerItem) {
        return RepositoryResourceUtil.findViewObjects(containerItem.getRepObjType(), containerItem);
    }

    public boolean isShownInRoot() {
        return true;
    }

}
