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

import org.talend.mdm.repository.core.IRepositoryNodeContentProvider;
import org.talend.mdm.repository.models.WSRootRepositoryObject;

/**
 * DOC hbhong class global comment. Detailled comment <br/>
 * 
 */
public class WSRootContentProvider implements IRepositoryNodeContentProvider {

    public Class<?> getWSObjectClass() {
        return null;
    }

    public Object[] getChildren(Object element) {
        if (element instanceof WSRootRepositoryObject) {
            return ((WSRootRepositoryObject) element).getChildren().toArray();
        }
        return new Object[0];
    }

    public boolean isShownInRoot() {
        return true;
    }

}
