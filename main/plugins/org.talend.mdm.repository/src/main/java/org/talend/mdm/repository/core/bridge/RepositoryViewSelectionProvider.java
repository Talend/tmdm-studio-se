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
package org.talend.mdm.repository.core.bridge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.AbstractRepositoryAction;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class RepositoryViewSelectionProvider implements ISelectionProvider {

    private final AbstractRepositoryAction action;

    private Map<IRepositoryViewObject, RepositoryNode> allNodes = new HashMap<IRepositoryViewObject, RepositoryNode>();

    public RepositoryViewSelectionProvider(AbstractRepositoryAction action) {
        this.action = action;
    }

    public void addSelectionChangedListener(ISelectionChangedListener listener) {

    }

    public ISelection getSelection() {
        allNodes.clear();
        List<Object> selectedObjects = action.getSelectedObject();
        if (!selectedObjects.isEmpty()) {
            List<RepositoryNode> selectedNodes = new ArrayList<RepositoryNode>();
            for (Object object : selectedObjects) {
                if (object instanceof IRepositoryViewObject) {
                    IRepositoryViewObject viewObj = (IRepositoryViewObject) object;
                    if (allNodes.containsKey(viewObj))
                        selectedNodes.add(allNodes.get(viewObj));
                    else {
                        RepositoryNode node = RepositoryResourceUtil.convertToNode(viewObj);
                        if (node != null) {
                            selectedNodes.add(node);
                        }

                        addToAllNodes(node);
                    }
                }
            }
            return new StructuredSelection(selectedNodes);
        }
        return new StructuredSelection();
    }

    private void addToAllNodes(RepositoryNode... nodes) {
        if (nodes == null || nodes.length == 0)
            return;

        for (RepositoryNode node : nodes) {
            allNodes.put(node.getObject(), node);
        }

        for (RepositoryNode node : nodes) {
            List<IRepositoryNode> children = node.getChildren();
            if (children != null) {
                addToAllNodes(children.toArray(new RepositoryNode[0]));
            }
        }
    }

    public void removeSelectionChangedListener(ISelectionChangedListener listener) {

    }

    public void setSelection(ISelection selection) {

    }

}
