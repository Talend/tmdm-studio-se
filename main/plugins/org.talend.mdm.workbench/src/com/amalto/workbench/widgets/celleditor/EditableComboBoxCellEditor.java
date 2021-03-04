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

import java.text.MessageFormat;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;

public class EditableComboBoxCellEditor extends CellEditor {

    private String[] items;

    private CCombo comboBox;

    private ModifyListener modifyListener;

    private Object value = null;

    private static final int defaultStyle = SWT.NONE;

    public EditableComboBoxCellEditor() {
        setStyle(defaultStyle);
    }

    public EditableComboBoxCellEditor(Composite parent, String[] items) {
        this(parent, items, defaultStyle);
    }

    public EditableComboBoxCellEditor(Composite parent, String[] items, int style) {
        super(parent, style);
        setItems(items);
    }

    public String[] getItems() {
        return items;
    }

    public void setItems(String[] items) {
        Assert.isNotNull(items);
        this.items = items;
        populateComboBoxItems();
    }

    protected Button createButton(Composite parent) {
        Button result = new Button(parent, SWT.DOWN);
        result.setText(""); //$NON-NLS-1$
        return result;
    }

    protected Control createContents(Composite cell) {

        return comboBox;
    }

    @Override
    protected Control createControl(Composite parent) {
        Composite cell = new Composite(parent, SWT.NONE);
        cell.setLayout(new ComboCellLayout());

        comboBox = new CCombo(cell, getStyle());
        comboBox.setFont(parent.getFont());
        populateComboBoxItems();

        comboBox.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                keyReleaseOccured(e);
            }
        });

        comboBox.addModifyListener(getModifyListener());

        comboBox.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {
                applyEditorValueAndDeactivate();
            }

            @Override
            public void widgetSelected(SelectionEvent event) {
                value = comboBox.getText();
            }
        });

        comboBox.addTraverseListener(new TraverseListener() {

            public void keyTraversed(TraverseEvent e) {
                if (e.detail == SWT.TRAVERSE_ESCAPE || e.detail == SWT.TRAVERSE_RETURN) {
                    e.doit = false;
                }
            }
        });

        comboBox.addFocusListener(new FocusAdapter() {

            @Override
            public void focusLost(FocusEvent e) {
                EditableComboBoxCellEditor.this.focusLost();
            }
        });

        updateContents(value);

        setValueValid(true);

        return cell;
    }

    @Override
    protected Object doGetValue() {
        return value;
    }

    @Override
    protected void doSetFocus() {
        if (comboBox != null) {
            comboBox.setFocus();
        }
    }

    @Override
    protected void doSetValue(Object value) {
        this.value = value;
        updateContents(value);
    }

    private void populateComboBoxItems() {
        if (comboBox != null && items != null) {
            comboBox.removeAll();
            for (int i = 0; i < items.length; i++) {
                comboBox.add(items[i], i);
            }
            comboBox.setText("");//$NON-NLS-1$
        }
    }

    void applyEditorValueAndDeactivate() {
        Object newValue = comboBox.getText();
        if (newValue != null && !newValue.equals(value.toString())) {
            boolean newValidState = isCorrect(newValue);
            if (newValidState) {
                markDirty();
                doSetValue(newValue);
            } else {
                setErrorMessage(MessageFormat.format(getErrorMessage(), new Object[] { newValue.toString() }));
            }
        }
        fireApplyEditorValue();
        deactivate();
    }

    @Override
    protected void focusLost() {
        if (isActivated()) {
            applyEditorValueAndDeactivate();
        }
    }

    @Override
    protected void keyReleaseOccured(KeyEvent keyEvent) {
        if (keyEvent.character == SWT.ESC) { // Return key
            if (comboBox != null && !comboBox.isDisposed()) {
                fireCancelEditor();
            }
        } else if (keyEvent.character == SWT.CR || keyEvent.character == SWT.TAB) { // tab key
            applyEditorValueAndDeactivate();
        }
    }

    protected void editOccured(ModifyEvent e) {
        String value = comboBox.getText();
        if (value == null) {
            value = "";//$NON-NLS-1$
        }
        Object typedValue = value;
        boolean oldValidState = isValueValid();
        boolean newValidState = isCorrect(typedValue);
        if (!newValidState) {
            // try to insert the current value into the error message.
            setErrorMessage(MessageFormat.format(getErrorMessage(), new Object[] { value }));
        }
        valueChanged(oldValidState, newValidState);
    }

    private ModifyListener getModifyListener() {
        if (modifyListener == null) {
            modifyListener = new ModifyListener() {

                public void modifyText(ModifyEvent e) {
                    editOccured(e);
                }
            };
        }
        return modifyListener;
    }

    private void updateContents(Object value) {
        Assert.isTrue(comboBox != null);

        if (value != null && value instanceof String) {
            comboBox.removeModifyListener(getModifyListener());
            comboBox.setText((String) value);
            comboBox.addModifyListener(getModifyListener());
        }
    }

    private class ComboCellLayout extends Layout {

        @Override
        public void layout(Composite editor, boolean force) {
            Rectangle bounds = editor.getClientArea();
            comboBox.setBounds(0, 0, bounds.width, bounds.height);
        }

        @Override
        public Point computeSize(Composite editor, int wHint, int hHint, boolean force) {
            if (wHint != SWT.DEFAULT && hHint != SWT.DEFAULT) {
                return new Point(wHint, hHint);
            }

            String text = comboBox.getText();
            comboBox.setText(""); //$NON-NLS-1$
            Point contentsSize = comboBox.computeSize(SWT.DEFAULT, SWT.DEFAULT, force);
            comboBox.setText(text);

            return contentsSize;
        }
    }
}
