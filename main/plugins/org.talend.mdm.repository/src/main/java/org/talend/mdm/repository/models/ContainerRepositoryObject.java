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
package org.talend.mdm.repository.models;

import java.util.LinkedList;
import java.util.List;

import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.model.repository.RepositoryObject;


/**
 * DOC hbhong  class global comment. Detailled comment
 */
class ContainerRepositoryObject extends RepositoryObject {

    public ContainerRepositoryObject(Property prop) {
        super(prop);
    }

    private List<IRepositoryViewObject> children;

    @Override
    public List<IRepositoryViewObject> getChildren() {
        if (children == null) {
            children = new LinkedList<IRepositoryViewObject>();
        }
        return children;
    }

}
