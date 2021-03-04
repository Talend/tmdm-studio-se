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
package org.talend.mdm.repository.ui.navigator.sorter;

import java.util.HashMap;
import java.util.Map;

import org.talend.core.model.properties.FolderType;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.mdm.repository.core.IServerObjectRepositoryType;
import org.talend.mdm.repository.model.mdmproperties.ContainerItem;


public class SortOrderFactory {

    private static Map<String, ISortOrder> orders = new HashMap<String, ISortOrder>();

    private static ISortOrder getSortOrder(ERepositoryObjectType type) {
        if(orders.get(type.getType()) == null)
        {
            ISortOrder order = null;
            if(type == IServerObjectRepositoryType.TYPE_VIEW) {
                order = new ViewCategoryOrder();
                orders.put(type.getType(), order);
            } else if (type == IServerObjectRepositoryType.TYPE_TRANSFORMERV2) {
                order = new ProcessCategoryOrder();
                orders.put(type.getType(), order);
            }

        }

        return orders.get(type.getType());
    }

    public static int getOrder(ContainerItem item) {
        ISortOrder sortOrder = getSortOrder(item.getRepObjType());
        if(sortOrder != null) {
            return sortOrder.getOrder(item);
        }

        int typeValue = item.getType().getValue();
        return (typeValue == FolderType.STABLE_SYSTEM_FOLDER) ? -2 : -1;
    }
}
