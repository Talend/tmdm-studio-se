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

import java.util.Collection;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class ListContentProvider implements ITreeContentProvider {

    // @Override
    public Object[] getChildren(Object parentElement) {
        return new Object[0];
    }

    // @Override
    public Object getParent(Object element) {
        return null;
    }

    // @Override
    public boolean hasChildren(Object element) {
        return false;
    }

    // @Override
    public Object[] getElements(Object inputElement) {

        if (inputElement instanceof Object[])
            return (Object[]) inputElement;

        if (inputElement instanceof Collection<?>)
            return ((Collection<?>) inputElement).toArray();

        return new Object[0];
    }

    // @Override
    public void dispose() {
    }

    // @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }

}
