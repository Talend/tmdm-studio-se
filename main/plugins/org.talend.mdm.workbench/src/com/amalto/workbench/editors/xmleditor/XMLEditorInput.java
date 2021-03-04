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
package com.amalto.workbench.editors.xmleditor;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.Document;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.amalto.workbench.i18n.Messages;

public class XMLEditorInput implements IEditorInput {

    Document document;

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public XMLEditorInput(Document document) {
        this.document = document;
    }

    public boolean exists() {

        return true;
    }

    public ImageDescriptor getImageDescriptor() {

        return null;
    }

    public String getName() {

        return Messages.XMLEditorInput_Document;
    }

    public IPersistableElement getPersistable() {

        return null;
    }

    public String getToolTipText() {

        return Messages.XMLEditorInput_Tooltip;
    }

    public Object getAdapter(Class adapter) {

        return null;
    }

}
