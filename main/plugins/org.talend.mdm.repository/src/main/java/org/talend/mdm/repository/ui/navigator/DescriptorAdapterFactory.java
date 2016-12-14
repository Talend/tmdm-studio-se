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
package org.talend.mdm.repository.ui.navigator;

import org.eclipse.core.runtime.IAdapterFactory;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.mdm.repository.models.WSRootRepositoryObject;
import org.talend.repository.navigator.INavigatorDescriptor;

public class DescriptorAdapterFactory implements IAdapterFactory {

    @Override
    public Object getAdapter(Object adaptableObject, Class adapterType) {
        if (adapterType == INavigatorDescriptor.class) {
            if (adaptableObject instanceof WSRootRepositoryObject) {
                return new INavigatorDescriptor() {

                    @Override
                    public String getDescriptor() {
                        return ProxyRepositoryFactory.getInstance().getNavigatorViewDescription();
                    }
                };
            }// else return null
        }// else return null
        return null;
    }

    @Override
    public Class[] getAdapterList() {
        return new Class[] { INavigatorDescriptor.class };
    }

}