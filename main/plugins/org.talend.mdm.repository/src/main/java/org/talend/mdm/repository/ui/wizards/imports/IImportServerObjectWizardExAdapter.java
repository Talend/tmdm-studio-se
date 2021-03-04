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

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.talend.mdm.repository.model.mdmmetadata.MDMServerDef;
import org.talend.mdm.repository.model.mdmserverobject.MDMServerObject;

import com.amalto.workbench.exadapter.IExAdapter;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.models.TreeParent;

/**
 * created by HHB on 2014-1-7 Detailled comment
 *
 */
public interface IImportServerObjectWizardExAdapter extends IExAdapter<ImportServerObjectWizard> {

    void retrieverCustomForms(MDMServerDef serverDef, TreeParent parent, IProgressMonitor monitor);

    void updateRelations(List<String> importedIds);

    String getWorkflowgTreeObjectName(TreeObject treeObject);

    String getWorkflowgTreeObjectVersion(TreeObject treeObject);

    MDMServerObject handleWorkflowObject(TreeObject treeObj);

}
