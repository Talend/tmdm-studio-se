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
package com.amalto.workbench.widgets.xmleditor.infoholder;

import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.models.infoextractor.IAllDataModelHolder;
import com.amalto.workbench.utils.ServerTreeNodeAllDataModelHolder;

public class AllDataModelInfoHolderProxy extends ExternalInfoHolder<IAllDataModelHolder> {

    private IAllDataModelHolder dataModelHolder;

    public AllDataModelInfoHolderProxy(TreeObject treeNode) {
        dataModelHolder = new ServerTreeNodeAllDataModelHolder(treeNode);
    }

    @Override
    public IAllDataModelHolder getExternalInfo() {
        return dataModelHolder;
    }

    @Override
    public String getId() {
        return INFOID_ALLDATAMODELHOLDER;
    }

}
