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
package org.talend.mdm.repository.ui.wizards.imports;

import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.service.ConsistencyService;

import com.amalto.workbench.exadapter.IExAdapter;
import com.amalto.workbench.models.TreeObject;


/**
 * created by liusongbo on Apr 28, 2015
 */
public interface IConsistencyServiceExAdapter extends IExAdapter<ConsistencyService> {

    public String getObjectNameForDigest(TreeObject treeObj);

    public String getObjectNameForDigest(IRepositoryViewObject viewObj);
}
