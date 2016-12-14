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
package com.amalto.workbench.widgets.xmleditor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.amalto.workbench.widgets.xmleditor.infoholder.ExternalInfoHolder;

public abstract class ExtensibleContentEditorPage extends Composite {

    protected ExtensibleEditorContent content;

    protected List<ExtensibleContentEditorPageListener> listeners = new ArrayList<ExtensibleContentEditorPageListener>();

    public ExtensibleContentEditorPage(Composite parent, int style) {
        super(parent, style);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        setLayout(gridLayout);

    }

    public void setContent(ExtensibleEditorContent content) {

        this.content = content;
        refresh();
    }

    public ExtensibleEditorContent getContent() {
        return content;
    }

    @Override
    public void dispose() {
        super.dispose();

        listeners.clear();
        listeners = null;
    }

    public void clearExternalResources() {
    }

    public void addExtensibleXMLEditorPageListener(ExtensibleContentEditorPageListener listener) {

        if (listeners.contains(listener))
            return;

        listeners.add(listener);
    }

    public void removeExtensibleXMLEditorPageListener(ExtensibleContentEditorPageListener listener) {
        listeners.remove(listener);
    }

    public void notifyOnXMLDocumentChanged() {

        for (ExtensibleContentEditorPageListener eachListener : getListeners())
            eachListener.onXMLDocumentChanged(this, getContent());

    }

    private ExtensibleContentEditorPageListener[] getListeners() {
        return listeners.toArray(new ExtensibleContentEditorPageListener[0]);
    }

    public abstract void reloadExternalInfo();

    public abstract void setExternalInfoHolder(ExternalInfoHolder<?> externalInfoHolder);

    public abstract void refresh();

    public abstract void saveContentFromUI() throws ExtensibleContentEditorPageSaveException;

}
