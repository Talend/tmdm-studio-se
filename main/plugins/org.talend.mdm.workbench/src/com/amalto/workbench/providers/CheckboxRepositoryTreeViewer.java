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
package com.amalto.workbench.providers;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.dialogs.ContainerCheckedTreeViewer;

import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.models.TreeParent;

/**
 * DOC hcw class global comment. Detailled comment
 */
public class CheckboxRepositoryTreeViewer extends ContainerCheckedTreeViewer implements ITreeViewerListener {

    private Map<String, Boolean> expanded = new HashMap<String, Boolean>();

    public CheckboxRepositoryTreeViewer(Composite parent, int style) {
        super(parent, style);
    }

    private TreeObject getTreeObject(Item node) {
        Object data = node.getData();
        TreeObject repositoryNode = null;
        if (data instanceof TreeObject) {
            repositoryNode = (TreeObject) data;
        }
        return repositoryNode;
    }

    @Override
    protected boolean getExpanded(Item item) {
        TreeObject repositoryNode = getTreeObject(item);
        if (repositoryNode != null && repositoryNode.getDisplayName() != null) {
            Boolean result = expanded.get(repositoryNode.getDisplayName());
            if (result != null) {
                if (item instanceof TreeItem) {
                    TreeItem treeItem = (TreeItem) item;
                    treeItem.setExpanded(result);
                }
            }
        }
        return super.getExpanded(item);
    }

    @Override
    public void setExpandedState(Object elementOrTreePath, boolean expanded) {
        if (expanded) {
            internalExpand(elementOrTreePath);
        } else {
            internalCollapse(elementOrTreePath);
        }
        super.setExpandedState(elementOrTreePath, expanded);
    }

    public void treeCollapsed(TreeExpansionEvent event) {
        Object element = event.getElement();
        internalCollapse(element);
    }

    public void treeExpanded(TreeExpansionEvent event) {
        Object element = event.getElement();
        internalExpand(element);
    }

    private void internalCollapse(Object element) {
        if (element instanceof TreeObject) {
            TreeObject repositoryNode = (TreeObject) element;
            if (idIsValid(repositoryNode)) {
                expanded.put(repositoryNode.getDisplayName(), false);
            }
            if (repositoryNode instanceof TreeParent)
                emptyExpandedChildren((TreeParent) repositoryNode);
        }
    }

    private void internalExpand(Object element) {
        if (element instanceof TreeObject) {
            TreeObject repositoryNode = (TreeObject) element;
            if (idIsValid(repositoryNode)) {
                expanded.put(repositoryNode.getDisplayName(), true);
            }
        }
    }

    private void emptyExpandedChildren(TreeParent repositoryNode) {
        for (TreeObject children : repositoryNode.getChildren()) {

            if (children instanceof TreeParent)
                emptyExpandedChildren((TreeParent) children);
            else {
                if (idIsValid(children)) {
                    expanded.remove(children.getDisplayName());
                }
            }
        }
    }

    private boolean idIsValid(TreeObject repositoryNode) {
        // String id = repositoryNode.getId();
        // return id != null && !TreeObject.NO_ID.equals(id);
        return true;
    }

}
