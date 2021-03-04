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

import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.forms.editor.FormEditor;
import org.talend.mdm.repository.core.service.RepositoryQueryService;
import org.talend.mdm.repository.core.service.RepositoryWebServiceAdapter;
import org.talend.mdm.repository.i18n.Messages;

import com.amalto.workbench.editors.StoredProcedureMainPage;
import com.amalto.workbench.webservices.TMDMService;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class StoredProcedureMainPage2 extends StoredProcedureMainPage {

    /**
     * DOC hbhong StoredProcedureMainPage2 constructor comment.
     *
     * @param editor
     */
    public StoredProcedureMainPage2(FormEditor editor) {
        super(editor);
    }

    @Override
    protected void initDataClusterCombo() {
        dataClusterCombo.removeAll();
        List<String> dataContainerNames = RepositoryQueryService.findAllDataContainerNames();
        if ((dataContainerNames.size() == 0) || ((dataContainerNames.size() == 1) && ("CACHE".equals(dataContainerNames.get(0))))) {//$NON-NLS-1$
            MessageDialog.openError(this.getSite().getShell(), Messages.Common_Error,
                    Messages.StoredProcedureMainPage2_noDataContainer);
            return;
        }
        dataClusterCombo.add("[ALL]");//$NON-NLS-1$

        for (String dataContainerName : dataContainerNames) {
            if (!"CACHE".equals(dataContainerName)) {
                dataClusterCombo.add(dataContainerName);
            }
        }
    }

    @Override
    protected TMDMService getMDMService() {
        return RepositoryWebServiceAdapter.getMDMService(getSite().getShell());
    }

}
