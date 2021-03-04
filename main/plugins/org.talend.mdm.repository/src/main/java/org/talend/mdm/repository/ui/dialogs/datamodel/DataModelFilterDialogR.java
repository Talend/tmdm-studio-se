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
package org.talend.mdm.repository.ui.dialogs.datamodel;

import java.util.List;

import org.eclipse.swt.widgets.Shell;
import org.talend.mdm.repository.core.service.RepositoryQueryService;

import com.amalto.workbench.dialogs.DataModelFilterDialog;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.utils.DataModelFilter;
import com.amalto.workbench.utils.SchemaElementNameFilterDes;


/**
 * DOC achen  class global comment. Detailled comment
 */
public class DataModelFilterDialogR extends DataModelFilterDialog {

    /**
     * DOC achen DataModelFilterDialogR constructor comment.
     *
     * @param parentShell
     * @param xObject
     * @param dataModelFilter
     * @param nameFilterDes
     */
    public DataModelFilterDialogR(Shell parentShell, TreeObject xObject, DataModelFilter dataModelFilter,
            SchemaElementNameFilterDes nameFilterDes) {
        super(parentShell, xObject, dataModelFilter, nameFilterDes);

    }

    /*
     * (non-Javadoc)
     *
     * @see com.amalto.workbench.dialogs.DataModelFilterDialog#getRoles()
     */
    @Override
    protected List<String> getRoles() {
        return RepositoryQueryService.findAllRoleNames();
    }

}
