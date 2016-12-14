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
package com.amalto.workbench.widgets.xmleditor.util;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.Item;

import com.amalto.workbench.widgets.xmleditor.ExtensibleContentEditorPage;
import com.amalto.workbench.widgets.xmleditor.pagecontent.ExtensibleXMLEditorPageContent;

public abstract class ExtensiblePageContentTableCellModifier<MODIFIEDTYPE extends ExtensibleXMLEditorPageContent> implements
        ICellModifier {

    protected ExtensibleContentEditorPage parentPage;

    protected ExtensibleXMLEditorPageContent modifiedRootPageContent;

    public ExtensiblePageContentTableCellModifier(ExtensibleContentEditorPage parentPage,
            ExtensibleXMLEditorPageContent modifiedRootPageContent) {
        this.parentPage = parentPage;
        this.modifiedRootPageContent = modifiedRootPageContent;
    }

    public void modify(Object element, String property, Object value) {

        if (!doModify(element, property, value))
            return;

        parentPage.getContent().setContent(modifiedRootPageContent.toXMLExpression());
        parentPage.notifyOnXMLDocumentChanged();
    }

    @SuppressWarnings("unchecked")
    protected MODIFIEDTYPE getRealModifiedItem(Object selection) {

        if (!(selection instanceof Item))
            return null;

        try {
            return (MODIFIEDTYPE) ((Item) selection).getData();
        } catch (Exception e) {
            return null;
        }
    }

    protected abstract boolean doModify(Object element, String property, Object value);
}
