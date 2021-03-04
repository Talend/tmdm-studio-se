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

import java.util.Collection;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.IRepositoryNodeConfiguration;
import org.talend.mdm.repository.core.IRepositoryNodeContentProvider;
import org.talend.mdm.repository.extension.RepositoryNodeConfigurationManager;

public class MDMRepositoryContentProvider implements ITreeContentProvider {

    public Object[] getChildren(Object element) {
        if (element instanceof Collection) {
            return ((Collection<?>) element).toArray();
        }
        if (element instanceof Object[]) {
            return (Object[]) element;
        }
        IRepositoryNodeContentProvider contentProvider = getContentProvider(element);
        if (contentProvider != null) {
            Object[] children = contentProvider.getChildren(element);
            if (children != null) {
                return children;
            }
        }

        return new Object[0];
    }

    public Object[] getElements(Object element) {
        return getChildren(element);
    }

    public void dispose() {
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }

    public Object getParent(Object element) {
        return null;
    }

    public boolean hasChildren(Object element) {
        return getChildren(element).length > 0;
    }

    private IRepositoryNodeContentProvider getContentProvider(Object element) {
        if (element instanceof IRepositoryViewObject) {
            Property property = ((IRepositoryViewObject) element).getProperty();
            if (property != null) {
                Item item = property.getItem();
                IRepositoryNodeConfiguration conf = RepositoryNodeConfigurationManager.getConfiguration(item);
                if (conf != null) {
                    return conf.getContentProvider();
                }
            }
        }
        return null;
    }

}
