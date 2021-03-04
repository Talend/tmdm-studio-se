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
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;

public abstract class EditableComboBoxDialogCellEditor extends CellEditor {

    private String[] items;

    private Composite editor;

    private CCombo comboBox;

    private Control contents;

    private Button button;

    private FocusListener buttonFocusListener;

    private ModifyListener modifyListener;

    private Object value = null;

    /** */
    /**
     * Internal class for laying out the dialog.
     */
    private class DialogCellLayout extends Layout {

        @Override
        public void layout(Composite editor, boolean force) {
            Rectangle bounds = editor.getClientArea();
            Point size = button.computeSize(SWT.DEFAULT, SWT.DEFAULT, force);
            if (contents != null) {
                contents.setBounds(0, 0, bounds.width - size.x, bounds.height);
            }
            button.setBounds(bounds.width - size.x, 0, size.x, bounds.height);
        }

        @Override
        public Point computeSize(Composite editor, int wHint, int hHint, boolean force) {
            if (wHint != SWT.DEFAULT && hHint != SWT.DEFAULT) {
                return new Point(wHint, hHint);
            }
            Point contentsSize = contents.computeSize(SWT.DEFAULT, SWT.DEFAULT, force);
            Point buttonSize = button.computeSize(SWT.DEFAULT, SWT.DEFAULT, force);
            // Just return the button width to ensure the button is not clipped
            // if the label is long.
            // The label will just use whatever extra width there is
            Point result = new Point(buttonSize.x, Math.max(contentsSize.y, buttonSize.y));
            return result;
        }
    }

    // Combo default style
    private static final int defaultStyle = SWT.NONE;

    protected FocusAdapter comboFocusListener;

    public EditableComboBoxDialogCellEditor() {
        setStyle(defaultStyle);
    }

    public EditableComboBoxDialogCellEditor(Composite parent, String[] items) {
        this(parent, items, defaultStyle);
    }

    public EditableComboBoxDialogCellEditor(Composite parent, String[] items, int style) {
        super(parent, style);
        setItems(items);
    }

    protected Button createButton(Composite parent) {
        Button result = new Button(parent, SWT.DOWN);
        result.setText(""); //$NON-NLS-1$
        return result;
    }

    @Override
    protected Control createControl(Composite parent) {
        Font font = parent.getFont();
        Color bg = parent.getBackground();

        editor = new Composite(parent, getStyle());
        editor.setFont(font);
        editor.setBackground(bg);
        editor.setLayout(new DialogCellLayout());

        contents = createContents(editor);
        updateContents(value);

        button = createButton(editor);
        button.setFont(font);

        button.addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.character == '\u001b') { // Escape
                    fireCancelEditor();
                }
            }
        });
        button.addFocusListener(getButtonFocusListener());
        button.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                button.removeFocusListener(getButtonFocusListener());
                comboBox.removeFocusListener(getComboFocusListener());
                Object newValue = openDialogBox(editor);
                button.addFocusListener(getButtonFocusListener());
                comboBox.addFocusListener(getComboFocusListener());

                if (newValue != null) {
                    boolean newValidState = isCorrect(newValue);
                    if (newValidState) {
                        markDirty();
                        doSetValue(newValue);
                    } else {
                        setErrorMessage(MessageFormat.format(getErrorMessage(), new Object[] { newValue.toString() }));
                    }
                }
                focusLost();
            }
        });

        setValueValid(true);

        return editor;
    }

    protected Control createContents(Composite cell) {
        comboBox = new CCombo(cell, getStyle());
        comboBox.setFont(cell.getFont());
        populateComboBoxItems();

        comboBox.addKeyListener(getComboKeyListener());

        comboBox.addModifyListener(getComboModifyListener());

        comboBox.addSelectionListener(getComboSelectionListener());

        comboBox.addTraverseListener(getTraverseListener());

        comboBox.addFocusListener(getComboFocusListener());

        return comboBox;
    }

    protected KeyAdapter getComboKeyListener() {
        return new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                keyReleaseOccured(e);
            }
        };
    }

    private ModifyListener getComboModifyListener() {
        if (modifyListener == null) {
            modifyListener = new ModifyListener() {

                public void modifyText(ModifyEvent e) {
                    editOccured(e);
                }
            };
        }

        return modifyListener;
    }

    protected SelectionListener getComboSelectionListener() {
        return new SelectionAdapter() {

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {
                applyEditorValueAndDeactivate();
            }

            @Override
            public void widgetSelected(SelectionEvent event) {
                value = comboBox.getText();
            }
        };
    }

    protected TraverseListener getTraverseListener() {
        return new TraverseListener() {

            public void keyTraversed(TraverseEvent e) {
                if (e.detail == SWT.TRAVERSE_ESCAPE || e.detail == SWT.TRAVERSE_RETURN) {
                    e.doit = false;
                }
            }
        };
    }

    protected FocusListener getComboFocusListener() {
        if (comboFocusListener == null) {
            this.comboFocusListener = new FocusAdapter() {

                @Override
                public void focusLost(FocusEvent e) {
                    if ((!comboBox.isFocusControl()) && !button.isFocusControl()) {
                        EditableComboBoxDialogCellEditor.this.focusLost();
                    }
                }
            };
        }
        return comboFocusListener;
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

    protected void applyEditorValueAndDeactivate() {
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
        if (keyEvent.character == '\r') { // Return key
            if (comboBox != null && !comboBox.isDisposed()) {
                fireCancelEditor();
            }
        } else if (keyEvent.character == '\t') { // tab key
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

    private FocusListener getButtonFocusListener() {
        if (buttonFocusListener == null) {
            buttonFocusListener = new FocusAdapter() {

                @Override
                public void focusLost(FocusEvent e) {
                    EditableComboBoxDialogCellEditor.this.focusLost();
                }
            };
        }

        return buttonFocusListener;
    }

    private void updateContents(Object value) {
        Assert.isTrue(comboBox != null);

        if (value != null && value instanceof String) {
            comboBox.removeModifyListener(getComboModifyListener());
            comboBox.setText((String) value);
            comboBox.addModifyListener(getComboModifyListener());
        }
    }

    public String[] getItems() {
        return items;
    }

    public void setItems(String[] items) {
        Assert.isNotNull(items);
        this.items = items;
        populateComboBoxItems();
    }

    public CCombo getComboBox() {
        return comboBox;
    }

    public Button getButton() {
        return button;
    }

    protected abstract Object openDialogBox(Control cellEditorWindow);

}
