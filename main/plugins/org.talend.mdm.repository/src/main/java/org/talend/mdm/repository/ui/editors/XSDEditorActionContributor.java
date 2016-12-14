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
package org.talend.mdm.repository.ui.editors;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;

import com.amalto.workbench.exadapter.ExAdapterManager;

public class XSDEditorActionContributor extends MultiPageEditorActionBarContributor {

    private IXSDEditorActionContriExAdapter exAdapter;

    public XSDEditorActionContributor() {
        exAdapter = ExAdapterManager.getAdapter(this, IXSDEditorActionContriExAdapter.class);
    }

    @Override
    public void setActivePage(IEditorPart activeEditor) {
        if (exAdapter != null) {
            exAdapter.setActivePage(activeEditor);
        }
    }

    @Override
    public void contributeToMenu(IMenuManager menuManager) {
        // TODO Auto-generated method stub
        super.contributeToMenu(menuManager);
    }

}
