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

import java.util.List;

import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.impl.AbstractContentProvider;
import org.talend.mdm.repository.model.mdmproperties.ContainerItem;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;

/**
 * DOC hbhong class global comment. Detailled comment <br/>
 *
 */
public class TISProcessContentProvider extends AbstractContentProvider {

    @Override
    protected List<IRepositoryViewObject> getViewObjFromSystemFolder(Item parentItem) {
        return RepositoryResourceUtil.findViewObjectsByType(ERepositoryObjectType.PROCESS, parentItem, -1);
    }

    protected List<IRepositoryViewObject> getViewObjFromFolder(ContainerItem containerItem) {
        return RepositoryResourceUtil.findViewObjects(containerItem.getRepObjType(), containerItem, true);
    }
    public Class<?> getWSObjectClass() {
        return null;
    }

}
