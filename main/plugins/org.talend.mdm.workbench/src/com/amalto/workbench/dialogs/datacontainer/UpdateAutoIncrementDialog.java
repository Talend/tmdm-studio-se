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
package com.amalto.workbench.dialogs.datacontainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.models.KeyValue;
import com.amalto.workbench.models.Line;

public class UpdateAutoIncrementDialog extends Dialog {

    private Map<String, String> results = new HashMap<String, String>();

    private Map<String, String> entityValues;

    private static final String DEFAULT_VALUE = "0"; //$NON-NLS-1$

    private VerifyListener verifyListeneer;

    private static int resetBtnId = 4;
    private static int resetAllBtnId = 8;

    private TableViewer resultsViewer;

    private IContentProvider contentProvider;

    private Button resetBtn;

    public UpdateAutoIncrementDialog(Shell parentShell, Map<String, String> entityValues) {
        super(parentShell);
        setShellStyle(getShellStyle() | SWT.RESIZE);
        this.entityValues = entityValues;
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(Messages.UpdateAutoIncrementDialog_ManageAutoIncrement);
    }

    @Override
    protected void initializeBounds() {
        super.initializeBounds();
        getShell().setSize(550, 450);
        Point location = getInitialLocation(getShell().getSize());
        getShell().setLocation(location.x, location.y);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite mainComp = (Composite) super.createDialogArea(parent);
        mainComp.setLayout(new GridLayout());
        createTable(mainComp);

        return mainComp;
    }

    private void createTable(Composite mainComp) {
        int style = SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.FULL_SELECTION;
        resultsViewer = new TableViewer(mainComp, style);
        resultsViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        resultsViewer.getTable().setHeaderVisible(true);
        resultsViewer.getTable().setLinesVisible(true);
        resultsViewer.setContentProvider(getContentProvider());

        TableViewerColumn column = new TableViewerColumn(resultsViewer, SWT.NONE);
        column.getColumn().setText(Messages.UpdateAutoIncrementDialog_entity);
        column.getColumn().setResizable(true);
        column.getColumn().setWidth(300);
        column.setLabelProvider(new CustomedLabelProvider(0));
        column.setEditingSupport(null);

        column = new TableViewerColumn(resultsViewer, SWT.NONE);
        column.getColumn().setText(Messages.UpdateAutoIncrementDialog_value);
        column.getColumn().setResizable(true);
        column.getColumn().setWidth(100);
        column.setLabelProvider(new CustomedLabelProvider(1));
        column.setEditingSupport(new EditingSupport(resultsViewer) {

            @Override
            protected CellEditor getCellEditor(Object element) {
                return new VerificableTextCellEditor(resultsViewer.getTable());
            }

            @Override
            protected boolean canEdit(Object element) {
                return true;
            }

            @Override
            protected Object getValue(Object element) {
                Line line = (Line) element;
                return line.keyValues.get(1).value;
            }

            @Override
            protected void setValue(Object element, Object value) {
                Line line = (Line) element;
                line.keyValues.get(1).value = value.toString();
                resultsViewer.refresh();
            }

        });

        resultsViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            public void selectionChanged(SelectionChangedEvent event) {
                IStructuredSelection selection = (IStructuredSelection) resultsViewer.getSelection();
                resetBtn.setEnabled(!selection.isEmpty());
            }
        });

        List<Line> lines = getInput();

        resultsViewer.setInput(lines);
    }

    private List<Line> getInput() {
        List<Line> lines = new ArrayList<Line>();
        Iterator<String> iterator = entityValues.keySet().iterator();
        while (iterator.hasNext()) {
            String entity = iterator.next();
            String value = entityValues.get(entity);

            List<KeyValue> keyvalues = new ArrayList<KeyValue>();
            keyvalues.add(new KeyValue("Entity", entity)); //$NON-NLS-1$
            keyvalues.add(new KeyValue("Value", value)); //$NON-NLS-1$
            Line line = new Line(keyvalues);
            lines.add(line);
        }

        return lines;
    }

    private IContentProvider getContentProvider() {
        if (contentProvider == null) {
            contentProvider = new IStructuredContentProvider() {

                public Object[] getElements(Object inputElement) {
                    @SuppressWarnings("unchecked")
                    List<Line> lines = (List<Line>) inputElement;
                    return lines.toArray();
                }

                public void dispose() {
                }

                public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
                }
            };
        }
        return contentProvider;
    }

    class CustomedLabelProvider extends ColumnLabelProvider {

        private int index;

        public CustomedLabelProvider(int index) {
            this.index = index;
        }

        @Override
        public String getText(Object element) {
            Line line = (Line) element;
            return line.keyValues.get(index).value;
        }
    }

    class VerificableTextCellEditor extends TextCellEditor {

        public VerificableTextCellEditor(Table table) {
            super(table);
        }

        @Override
        protected Control createControl(Composite parent) {
            Control control = super.createControl(parent);
            text.addVerifyListener(getVerifyListener());

            return control;
        }

        @Override
        protected Object doGetValue() {
            return super.doGetValue();
        }
    }

    private VerifyListener getVerifyListener() {
        if (verifyListeneer == null) {
            verifyListeneer = new VerifyListener() {

                public void verifyText(VerifyEvent e) {
                    Text text = (Text) e.getSource();
                    String msg = null;

                    String inputStr = e.text;
                    boolean matches = true;
                    String digitRegex = "[0-9]*"; //$NON-NLS-1$
                    matches = Pattern.matches(digitRegex, inputStr);
                    if (!matches) {
                        msg = Messages.UpdateAutoIncrementDialog_inputInvalid;
                    } else {
                        if (e.start == 0) {
                            if ((inputStr.startsWith(DEFAULT_VALUE) && inputStr.length() > 1)
                                    || (inputStr.equals(DEFAULT_VALUE) && !isFullSelected(e))) {
                                matches = false;
                                msg = Messages.UpdateAutoIncrementDialog_zeroAtBeginning;
                            }
                        } else {
                            if (text.getText().startsWith(DEFAULT_VALUE)) {
                                matches = false;
                                msg = Messages.UpdateAutoIncrementDialog_zeroAtBeginning;
                            }
                        }
                    }


                    if (!matches) {
                        MessageDialog.openError(getShell(), Messages._Error, msg);
                        e.doit = false;
                        return;
                    }

                }

                private boolean isFullSelected(VerifyEvent e) {
                    Text text = (Text) e.getSource();
                    String textContent = text.getText();
                    if (textContent.length() == text.getSelectionCount()) {
                        return true;
                    }

                    return false;
                }
            };

        }
        return verifyListeneer;
    }

    @Override
    protected void buttonPressed(int buttonId) {
        if (resetAllBtnId == buttonId) {
            List<Line> lines = (List<Line>) resultsViewer.getInput();
            for (Line line : lines) {
                line.keyValues.get(1).value = DEFAULT_VALUE;
            }
            resultsViewer.refresh();
        }

        if (resetBtnId == buttonId) {
            IStructuredSelection selection = (IStructuredSelection) resultsViewer.getSelection();
            for (Object obj : selection.toList()) {
                Line line = (Line) obj;
                line.keyValues.get(1).value = DEFAULT_VALUE;
            }
            resultsViewer.refresh();
        }
        super.buttonPressed(buttonId);
    }

    @Override
    protected void okPressed() {
        deactivateCellEditors();
        save();
        removeNotChanged();
        super.okPressed();
    }

    private void deactivateCellEditors() {
        CellEditor[] cellEditors = resultsViewer.getCellEditors();
        if (cellEditors != null) {
            for (CellEditor cellEditor : cellEditors) {
                cellEditor.deactivate();
            }
        }
    }

    private void save() {
        List<Line> lines = (List<Line>) resultsViewer.getInput();
        for (Line line : lines) {
            String key = line.keyValues.get(0).value;
            String value = line.keyValues.get(1).value;
            results.put(key, value);
        }
    }

    private void removeNotChanged() {
        Iterator<String> iterator = entityValues.keySet().iterator();
        while (iterator.hasNext()) {
            String entity = iterator.next();
            if (entityValues.get(entity).equals(results.get(entity))) {
                results.remove(entity);
            }
        }
    }

    @Override
    protected Control createButtonBar(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).extendedMargins(5, -3, 5, 5).equalWidth(false).applyTo(composite);

        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).span(2, 1).indent(0, 0).applyTo(composite);
        composite.setFont(parent.getFont());
        createButtonsForButtonBar(composite);
        return composite;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        resetBtn = createButton(parent, resetBtnId, Messages.UpdateAutoIncrementDialog_Reset, false);
        resetBtn.setEnabled(false);
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).applyTo(resetBtn);

        Button resetAllBtn = createButton(parent, resetAllBtnId, Messages.UpdateAutoIncrementDialog_resetAll, false);
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).applyTo(resetAllBtn);

        Composite rightArea = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(0).equalWidth(true).applyTo(rightArea);
        GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).grab(true, false).applyTo(rightArea);

        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    /**
     * entity to AutoIncrement value map
     */
    public Map<String, String> getResults() {
        return results;
    }
}
