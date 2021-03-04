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
package com.amalto.workbench.widgets.xmleditor;

import org.eclipse.swt.widgets.Composite;

import com.amalto.workbench.widgets.xmleditor.pagecontent.ExtensibleXMLEditorPageContent;

public abstract class ExtensibleXMLContentEditorPage<T extends ExtensibleXMLEditorPageContent> extends
        ExtensibleContentEditorPage {

    protected T pageContentObject;

    public ExtensibleXMLContentEditorPage(Composite parent, int style) {
        super(parent, style);

        pageContentObject = initPageContentObject();
    }

    public T getPageContentObject() {
        return pageContentObject;
    }

    @Override
    public void refresh() {

        if (pageContentObject.toXMLExpression().equals(getContent().getContent()))
            return;

        pageContentObject.init(getContent().getContent());

        doRefreshByPageContentObject();
    }

    @Override
    public void saveContentFromUI() throws ExtensibleContentEditorPageSaveException {
        getContent().setContent(pageContentObject.toXMLExpression());
    }

    protected abstract T initPageContentObject();

    protected abstract void doRefreshByPageContentObject();
}
