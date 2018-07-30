// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
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

import com.amalto.workbench.exadapter.IExAdapter;

/**
 * created by HHB on 2015-5-5 Detailled comment
 *
 */
public interface IMDMImportItemsWizardPageExAdapter extends IExAdapter<MDMImportItemsWizardPage> {

    void rebuildRelations(List<String> importedIds);
}
