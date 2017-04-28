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
package com.amalto.workbench.editors.xmleditor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.editors.text.TextEditor;

import com.amalto.workbench.editors.DataModelMainPage;
import com.amalto.workbench.editors.XObjectEditor;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.webservices.WSDataModel;

public class XMLEditor extends TextEditor {

    TreeObject xobject;

    private ColorManager colorManager;

    XObjectEditor editor;

    int state = -1;

    private boolean modified = false;

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }

    public XMLEditor(XObjectEditor editor, TreeObject xobject) {
        super();
        this.xobject = xobject;
        this.editor = editor;
        colorManager = new ColorManager();
        setSourceViewerConfiguration(new XMLConfiguration(colorManager));
        setDocumentProvider(new XMLDocumentProvider());
    }

    @Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);
        // initializeKeyPress((SourceViewer)getSourceViewer());
    }

    /**
     * @deprecated
     * @param viewer
     */
    @Deprecated
    private void initializeKeyPress(final SourceViewer viewer) {
        StyledText styledText = viewer.getTextWidget();
        styledText.addKeyListener(new KeyListener() {

            public void keyPressed(KeyEvent e) {
            }

            public void keyReleased(KeyEvent e) {
                if (e.keyCode == 'x' && e.stateMask == SWT.CTRL) {
                    viewer.doOperation(ITextOperationTarget.CUT);
                } else if (e.keyCode == 'v' && e.stateMask == SWT.CTRL) {
                    viewer.doOperation(ITextOperationTarget.PASTE);
                }
            }
        });
    }

    @Override
    public void dispose() {
        colorManager.dispose();
        super.dispose();
    }

    @Override
    public boolean isEditable() {
        return true;
    }

    @Override
    public boolean isEditorInputModifiable() {

        return true;
    }

    @Override
    public boolean isSaveOnCloseNeeded() {

        return true;
    }

    @Override
    public void doSave(IProgressMonitor progressMonitor) {
        // WSDataModel wsObject = (WSDataModel) (xobject.getWSObject());
        IDocument doc = ((XMLEditorInput) this.getEditorInput()).getDocument();

        String schema = doc.get();

        DataModelMainPage page = (DataModelMainPage) editor.formPages.get(0);
        page.setXsdSchema(null);
        int ret = page.save(schema);
        if (ret != 0) {
            return;
        }
        setModified(true);

        super.doSave(progressMonitor);
    }

    public void refresh(TreeObject xobject) {
        state = -1;
        this.xobject = xobject;
        WSDataModel wsObject = (WSDataModel) (xobject.getWsObject());
        Document doc = new Document(Util.formatXsdSource(wsObject.getXsdSchema()));
        setInput(new XMLEditorInput(doc));
    }

    public void refresh() {
        state = -1;
        WSDataModel wsObject = (WSDataModel) (xobject.getWsObject());
        IDocument doc = ((XMLEditorInput) this.getEditorInput()).getDocument();
        wsObject.setXsdSchema(doc.get());
    }

    public void markDirty() {
        firePropertyChange(PROP_DIRTY);
    }

    @Override
    public boolean isDirty() {
        if (state == 1) {
            return true;
        }
        return super.isDirty();
    }

    public void setState(int state) {
        this.state = state;
    }

}
