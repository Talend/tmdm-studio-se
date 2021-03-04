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
package com.amalto.workbench.utils;
import org.eclipse.xsd.XSDSchema;

import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.models.infoextractor.IAllDataModelHolder;


public class ServerTreeNodeAllDataModelHolder implements IAllDataModelHolder {

    private TreeObject treeNode;

    public ServerTreeNodeAllDataModelHolder(TreeObject treeNode) {
        this.treeNode = treeNode;
    }

    public String[] getAllDataModelNames() {
        return Util.getChildren(treeNode.getServerRoot(), TreeObject.DATA_MODEL).toArray(new String[0]);
    }

    public boolean hasDataModel() {
        return getAllDataModelNames().length > 0;
    }

    public XSDSchema getDataModel(String dataModelName) {
        return Util.getXSDSchema(treeNode, dataModelName);
    }

	public String getDefaultDataModel() {
		return null;
	}

	public String getDefaultEntity() {
		return null;
	}

}
