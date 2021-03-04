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
package com.amalto.workbench.export;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import com.amalto.workbench.exadapter.IExAdapter;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.webservices.TMDMService;

/**
 * created by HHB on 2014-1-3 Detailled comment
 *
 */
public interface IExportItemsWizardAdapter extends IExAdapter<ExportItemsWizard> {

    void doexport(TMDMService service, int treeObjType, List<TreeObject> exports, TreeObject obj, IProgressMonitor monitor);

}
