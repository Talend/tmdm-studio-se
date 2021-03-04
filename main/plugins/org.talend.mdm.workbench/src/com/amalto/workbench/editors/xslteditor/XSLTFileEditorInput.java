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
package com.amalto.workbench.editors.xslteditor;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.part.FileEditorInput;

/**
 * created by liusongbo on 2013-1-17
 *
 */
public class XSLTFileEditorInput extends FileEditorInput {

    public PageRefresher refresher;

    public boolean isTempFile = false;

    public XSLTFileEditorInput(IFile file) {
        super(file);
    }

    public XSLTFileEditorInput(IFile file, PageRefresher refresher, boolean isTempFile) {
        super(file);
        this.refresher = refresher;
        this.isTempFile = isTempFile;
    }
}
