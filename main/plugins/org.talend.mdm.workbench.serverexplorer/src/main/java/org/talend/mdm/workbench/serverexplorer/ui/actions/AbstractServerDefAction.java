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
package org.talend.mdm.workbench.serverexplorer.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.model.mdmproperties.MDMServerDefItem;


/**
 * created by liusongbo on 2014-5-12
 */
public abstract class AbstractServerDefAction extends Action {

    private TreeViewer treeViewer;

    public void initSelectionProvider(TreeViewer viewer) {
        this.treeViewer = viewer;
    }

    public IRepositoryViewObject getSelectedViewObject() {
        if(treeViewer != null) {
            ISelection selection = treeViewer.getSelection();
            if (!selection.isEmpty()) {
                Object firstElement = ((TreeSelection) selection).getFirstElement();
                return (IRepositoryViewObject) firstElement;
            }
        }

        return null;
    }

    public MDMServerDefItem getMDMItem(IRepositoryViewObject viewObject) {
        if (viewObject != null) {
            return (MDMServerDefItem) (viewObject.getProperty().getItem());
        }
        return null;
    }
}
