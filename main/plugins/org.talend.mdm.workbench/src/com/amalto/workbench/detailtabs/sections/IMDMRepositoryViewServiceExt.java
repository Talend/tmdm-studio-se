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
package com.amalto.workbench.detailtabs.sections;

import java.util.List;

import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.xsd.XSDSchema;

import com.amalto.workbench.models.TreeObject;

public interface IMDMRepositoryViewServiceExt {

    public List<String> findAllRoleNames();

    public List<String> findAllDataModelNames();

    public XSDSchema getDataModelXsd(TreeObject pObject, String filter, String dataModelName);

    public IWorkbenchPartSite getMDMRepositoryViewSite();

    public void openJob(String jobName);

    public List<String> getDataModel(String dataModel, String concept);
}
