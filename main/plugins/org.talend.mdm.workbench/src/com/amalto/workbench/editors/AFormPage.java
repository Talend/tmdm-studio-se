// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.workbench.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.models.IXObjectModelListener;
import com.amalto.workbench.models.TreeObject;

public abstract class AFormPage extends FormPage {

    private boolean refreshing = false;

    private boolean comitting = false;

    public AFormPage(FormEditor editor, String id, String title) {
        super(editor, id, title);
    }

    /**
     * Should be overriden
     */
    @Override
    protected void createFormContent(IManagedForm managedForm) {
        super.createFormContent(managedForm);
        managedForm.getForm().setText(this.getTitle());
        createActions();
        // add listener to execute System.gc when page is closed
    }

    protected void initReadOnly(ScrolledForm form) {
        Composite parent = form.getBody();
        FormEditor editor = getEditor();
        if (editor instanceof IServerObjectEditorState) {
            if (((IServerObjectEditorState) editor).isReadOnly()) {
                form.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
                form.setText(form.getText() + Messages.AFormPage_ReadOnly);
                if (parent != null) {
                    for (Control control : parent.getChildren()) {
                        control.setEnabled(false);
                    }
                }
            }
        }
    }

    /**
     * Called by The main Editor
     * 
     */
    public void refreshPage() {
        this.refreshing = true;
        if (!this.comitting) {
            refreshData();
        }
        this.refreshing = false;
    }

    @Override
    public void doSave(IProgressMonitor monitor) {
        commit();
        super.doSave(monitor);
    }

    /**
     * Refresh the form with the model content
     * 
     */
    protected abstract void refreshData();

    /**
     * Commit the form content to the model
     * 
     */
    protected abstract void commit();

    /**
     * Commit changes back to the model - must be called by the listeners
     * 
     * @param onSave
     */
    protected void commitChanges() {
        this.comitting = true;
        if (!this.refreshing) {
            Object obj = this.getEditor().getEditorInput().getAdapter(XObjectEditor.class);
            TreeObject xobject = (TreeObject) obj;
            commit();
            if (xobject.getParent() != null) { // e.g. not standalone but part
                // of model
                xobject.fireEvent(IXObjectModelListener.UPDATE, xobject.getParent(), xobject);
            }
        }
        this.comitting = false;
    }

    /**
     * @return the model manipulated by this Page
     */
    public TreeObject getXObject() {
        Object obj = this.getEditor().getEditorInput().getAdapter(XObjectEditor.class);
        return (TreeObject) obj;
    }

    /**
     * Called at time of the page creation
     * 
     */
    protected abstract void createActions();

    public boolean beforeDoSave() {
        return true;

    }

    public boolean isLocalInput() {
        FormEditor editor = getEditor();
        if (editor instanceof IServerObjectEditorState) {
            return ((IServerObjectEditorState) editor).isLocalInput();
        }
        return false;
    }
}
