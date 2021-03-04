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

import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;

/**
 * DOC hbhong class global comment. WorkspaceRoot object
 */
public class WSRootRepositoryObject extends ContainerRepositoryObject {

    /**
     * DOC hbhong RootRepositoryObject constructor comment.
     *
     * @param props
     */
    public WSRootRepositoryObject(Property prop) {
        super(prop);
    }

    @Override
    public ERepositoryObjectType getRepositoryObjectType() {
        return null;
    }

}
