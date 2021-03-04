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
package org.talend.mdm.repository.ui.wizards.exports.viewers;

import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;

import com.amalto.workbench.providers.CheckboxRepositoryTreeViewer;

/**
 * DOC hywang class global comment. Detailled comment
 */
// HHB: can be remove after successfully test import and export item wizard
@Deprecated
public class CheckboxMDMRepositoryTreeViewer extends CheckboxRepositoryTreeViewer {

    public CheckboxMDMRepositoryTreeViewer(Composite parent, int style) {
        super(parent, style);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected boolean getExpanded(Item item) {
        return super.getExpanded(item);
    }

    @Override
    public void setExpandedState(Object elementOrTreePath, boolean expanded) {
        super.setExpandedState(elementOrTreePath, expanded);
    }

    public void treeCollapsed(TreeExpansionEvent event) {
        Object element = event.getElement();
    }

    public void treeExpanded(TreeExpansionEvent event) {
        Object element = event.getElement();
    }

}
