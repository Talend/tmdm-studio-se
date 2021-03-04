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
package org.talend.mdm.repository.ui.starting.editor;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.talend.core.ui.branding.IBrandingService;


public class MDMStartingEditorInput implements IPathEditorInput {

    private IBrandingService service;

    public MDMStartingEditorInput(IBrandingService service) {
        this.service = service;
    }

    public boolean exists() {
        return true;
    }

    public ImageDescriptor getImageDescriptor() {
        return null;
    }

    public String getName() {
        if(service == null)
         {
            return "product name";//$NON-NLS-1$
        }
        return service.getProductName();
    }

    public IPersistableElement getPersistable() {
        return null;
    }

    public String getToolTipText() {
        if(service == null)
         {
            return "full product name";//$NON-NLS-1$
        }
        return service.getFullProductName();
    }

    public Object getAdapter(Class arg0) {
        return null;
    }

    public IPath getPath() {
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof MDMStartingEditorInput) {
            return true;
        }

        return false;
    }
}
