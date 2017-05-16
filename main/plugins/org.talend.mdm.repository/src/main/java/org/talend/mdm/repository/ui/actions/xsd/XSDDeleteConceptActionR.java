// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.repository.ui.actions.xsd;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.xsd.XSDSchema;

import com.amalto.workbench.actions.IMatchRuleMapInfoOperationExAdapter;
import com.amalto.workbench.actions.XSDDeleteConceptAction;
import com.amalto.workbench.editors.DataModelMainPage;
import com.amalto.workbench.exadapter.ExAdapterManager;
import com.amalto.workbench.utils.Util;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class XSDDeleteConceptActionR extends XSDDeleteConceptAction {

    private IMatchRuleMapInfoOperationExAdapter mapinfoExAdapter = null;

    public XSDDeleteConceptActionR(DataModelMainPage page) {
        super(page);
        this.mapinfoExAdapter = ExAdapterManager.getAdapter(this, IMatchRuleMapInfoOperationExAdapter.class);
    }

    @Override
    protected boolean checkContainFK(String fkName) throws Exception {
        XSDSchema xsdSchema = page.getXSDSchema();
        Set<String> fks = new HashSet<String>();
        if (xsdSchema != null) {
            Util.getForeingKeyInSchema(fks, xsdSchema);
            return fks.contains(fkName);
        }
        return false;
    }

    @Override
    public IStatus doAction() {

        IStatus status = super.doAction();
        if (status.isOK()) {
            if (deletedEntityName != null && mapinfoExAdapter != null) {
                mapinfoExAdapter.deleteEntityMapInfo(deletedEntityName);
                deletedEntityName = null;
            }

        }

        return status;

    }

}
