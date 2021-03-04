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
package com.amalto.workbench.widgets.celleditor;

import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

public abstract class EditableDialogCellEditor extends DialogCellEditor {

    protected Text txtEdit;

    protected EditableDialogCellEditor(Composite parent) {
        super(parent);
    }

    @Override
    protected Control createContents(Composite cell) {

        txtEdit = new Text(cell, SWT.NONE);
        txtEdit.setFont(cell.getFont());
        txtEdit.setBackground(cell.getBackground());

        txtEdit.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.keyCode == 13) {
                    doChange();
                }
            }
        });

        cell.getParent().addFocusListener(getFocusListener());

        return txtEdit;
    }

    private FocusListener getFocusListener() {
        return new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent e) {
                doChange();
            }
        };
    }

    protected void doChange() {
        if (validate()) {
            markDirty();
            doSetValue(txtEdit.getText().trim());
            fireApplyEditorValue();
        } else {
            restoreValue();
        }
    }

    protected void restoreValue() {
        // do nothing
    }

    protected boolean validate() {
        return true;
    }

    @Override
    protected void doSetFocus() {

        if (txtEdit != null) {
            txtEdit.selectAll();
            txtEdit.setFocus();
        }

    }

    @Override
    protected void updateContents(Object value) {
        if (txtEdit == null) {
            return;
        }

        activate();

        String text = "";//$NON-NLS-1$
        if (value != null) {
            text = value.toString();
        }
        txtEdit.setText(text);

    }

}
