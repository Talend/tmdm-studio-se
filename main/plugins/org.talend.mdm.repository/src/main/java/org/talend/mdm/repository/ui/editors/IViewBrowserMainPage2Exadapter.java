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
package org.talend.mdm.repository.ui.editors;

import org.talend.mdm.repository.model.mdmmetadata.MDMServerDef;

import com.amalto.workbench.exadapter.IExAdapter;


/**
 * created by liusongbo on Nov 11, 2014
 */
public interface IViewBrowserMainPage2Exadapter extends IExAdapter<ViewBrowserMainPage2> {

    public String getDataClusterType(String dataCluster, MDMServerDef mdmServerDef);

    public String getPkAddition(String clusterType);

}
