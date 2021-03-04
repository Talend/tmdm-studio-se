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
package com.amalto.workbench.editors.xsdeditor;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.part.FileEditorInput;

import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;

public class XSDEditorInput extends FileEditorInput {

    IFile file;

    public XSDEditorInput(IFile file) {
        super(file);
        this.file = file;
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof XSDEditorInput)) {
            return false;
        }
        XSDEditorInput input = (XSDEditorInput) obj;
        boolean ret = input.file.getFullPath().toOSString().equals(file.getFullPath().toOSString());
        return ret;
    }

    @Override
    public ImageDescriptor getImageDescriptor() {
        return ImageCache.getImage(EImage.DATA_MODEL.getPath());
    }

    public void dispose() throws Throwable {
        finalize();
    }
}
