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
package org.talend.mdm.repository.ui.navigator;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.models.FolderRepositoryObject;


/**
 * DOC hbhong  class global comment. Detailled comment
 */
public class CommonMDMRepositoryContentProvider implements ITreeContentProvider {

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }

    public void dispose() {
    }

    public Object[] getElements(Object inputElement) {
        return getChildren(inputElement);
    }

    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof IRepositoryViewObject[]) {
            return (IRepositoryViewObject[]) parentElement;
        } else if (parentElement instanceof FolderRepositoryObject) {
            return ((FolderRepositoryObject) parentElement).getChildren().toArray();
        }
        return new Object[0];
    }

    public Object getParent(Object element) {
        return null;
    }

    public boolean hasChildren(Object element) {
        return getChildren(element).length > 0;
    }
}
