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
package org.talend.mdm.repository.ui.editors;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;

import com.amalto.workbench.editors.XObjectBrowser;
import com.amalto.workbench.exadapter.IExAdapter;
import com.amalto.workbench.models.TreeObject;


/**
 * created by liusongbo on 2014-6-6
 */
public interface IXObjectBrowser2ExAdapter extends IExAdapter<XObjectBrowser2> {

    public boolean addPageForXObject(XObjectBrowser editor, IEditorInput editorInput, TreeObject xobject)
            throws PartInitException;

    public String getPageText();

    public Image getPageImage();

    public boolean isStagingDBExist();

}
