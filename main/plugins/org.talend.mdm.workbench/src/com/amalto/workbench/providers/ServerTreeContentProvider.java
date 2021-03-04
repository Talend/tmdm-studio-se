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
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IWorkbenchPartSite;

import com.amalto.workbench.models.IXObjectModelListener;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.models.TreeParent;


public class ServerTreeContentProvider implements IStructuredContentProvider, ITreeContentProvider {

    private TreeParent invisibleRoot;

    IWorkbenchPartSite site = null;

    public ServerTreeContentProvider(IWorkbenchPartSite site, TreeParent invisibleRoot) {
        this.site = site;
        this.invisibleRoot = invisibleRoot;
    }

    public void inputChanged(Viewer v, Object oldInput, Object newInput) {
    }

    public void dispose() {
    }

    public void setRoot(TreeParent root) {
        invisibleRoot = root;
    }

    public Object[] getElements(Object parent) {
        if (parent.equals(site)) {
            // if (invisibleRoot==null) return viewer.
            return getChildren(invisibleRoot);
        }
        return getChildren(parent);
    }

    public Object getParent(Object child) {
        if (child instanceof TreeObject) {
            return ((TreeObject) child).getParent();
        }
        return null;
    }

    public Object[] getChildren(Object parent) {
        if (parent instanceof TreeParent) {
            return ((TreeParent) parent).getChildren();
        }
        return new Object[0];
    }

    public boolean hasChildren(Object parent) {
        if (parent instanceof TreeParent)
            return ((TreeParent) parent).hasChildren();
        return false;
    }

    public void addListener(IXObjectModelListener listener) {
        invisibleRoot.addListener(listener);
    }

    public void removeListener(IXObjectModelListener listener) {
        invisibleRoot.removeListener(listener);
    }

    public TreeParent getInvisibleRoot() {
        return invisibleRoot;
    }

}
