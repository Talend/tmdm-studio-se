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
package com.amalto.workbench.widgets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.amalto.workbench.dialogs.XpathSelectDialog;
import com.amalto.workbench.editors.AMainPageV2;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.models.KeyValue;
import com.amalto.workbench.models.Line;
import com.amalto.workbench.models.TreeParent;
import com.amalto.workbench.widgets.celleditor.IXPathValidator;

/**
 * This table viewer has: 1.input Texts, add button 2.normal tableviewer with up/down/delete button
 *
 * @author aiming
 *
 */
public class ComplexTableViewer {

    // ITableModifyListener
    ListenerList modifyList = new ListenerList();

    protected List<ComplexTableViewerColumn> columns;

    protected Map<ComplexTableViewerColumn, IXPathValidator> validators = new HashMap<ComplexTableViewerColumn, IXPathValidator>();

    protected Composite parent;

    protected FormToolkit toolkit;

    protected Composite mainComposite;

    protected Button addButton;

    protected TableViewer viewer;

    protected Button downButton;

    protected Button upButton;

    protected Button deleteButton;

    protected ComplexTableViewerColumn[] keyColumns;

    protected boolean editable = true;

    protected static String ERROR_ITEMALREADYEXISTS_CONTENT = Messages.ComplexTableViewer_AlreadyExists;

    protected static String ERROR_ITEMALREADYEXISTS_TITLE = Messages.Warning;

    // mainPage can be null
    protected AMainPageV2 mainPage;

    protected Table table;

    protected String conceptName;

    protected String datamodelName;

    protected boolean context;

    protected boolean modelLock;

    public boolean isModelLock() {
        return modelLock;
    }

    public void setModelLock(boolean modelLock) {
        this.modelLock = modelLock;
    }

    public boolean isContext() {
        return context;
    }

    public void setContext(boolean context) {
        this.context = context;
    }

    // Modified by hhb,to fix bug 21784

    TreeParent treeParent;

    public void setTreeParent(TreeParent treeParent) {
        this.treeParent = treeParent;
    }

    protected TreeParent getCurrentTreeParent() {
        if (treeParent != null) {
            return treeParent;
        }
        if (mainPage != null) {
            return (TreeParent) ((IAdaptable) mainPage).getAdapter(TreeParent.class);
        }
        return null;
    }

    // The ending| bug:21784
    List<XpathWidget> xpaths = new ArrayList<XpathWidget>();

    protected DescAnnotationComposite multiMsg;

    protected XpathSelectDialog xpathDialog;

    protected ValidationRuleWidget validationRule;

    protected Text text;

    public String getConceptName() {
        return conceptName;
    }

    public void setConceptName(String conceptName) {
        this.conceptName = conceptName;
        for (XpathWidget xpath : xpaths) {
            xpath.setConceptName(conceptName);
        }
        if (xpathDialog != null) {
            xpathDialog.setConceptName(conceptName);
        }
    }

    public String getDatamodelName() {
        return datamodelName;
    }

    public void setDatamodelName(String datamodelName) {
        this.datamodelName = datamodelName;
        if (xpathDialog != null) {
            xpathDialog.dataModelName = datamodelName;
        }
    }

    public AMainPageV2 getMainPage() {
        return mainPage;
    }

    public void setMainPage(AMainPageV2 mainPage) {
        this.mainPage = mainPage;
    }

    public void setKeyColumns(ComplexTableViewerColumn[] keyIndex) {
        keyColumns = keyIndex;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public Button getAddButton() {
        return addButton;
    }

    public TableViewer getViewer() {
        return viewer;
    }

    public Button getDownButton() {
        return downButton;
    }

    public Button getUpButton() {
        return upButton;
    }

    public Button getDeleteButton() {
        return deleteButton;
    }

    public void setColumns(List<ComplexTableViewerColumn> columns) {
        this.columns = columns;
    }

    public List<ComplexTableViewerColumn> getColumns() {
        return columns;
    }

    public ComplexTableViewerColumn getColumn(ComplexTableViewerColumn compareComplexTableViewerColumn) {
        for (Object element : columns) {
            ComplexTableViewerColumn complexTableViewerColumn = (ComplexTableViewerColumn) element;
            if (complexTableViewerColumn.equals(compareComplexTableViewerColumn)) {
                return complexTableViewerColumn;
            }
        }
        return null;

    }

    public void setValidators(Map<ComplexTableViewerColumn, IXPathValidator> validators) {
        this.validators = validators;
        if (validators == null) {
            this.validators = new HashMap<ComplexTableViewerColumn, IXPathValidator>();
        }
    }

    public ComplexTableViewer(List<ComplexTableViewerColumn> columns, FormToolkit toolkit, Composite parent) {
        this.columns = columns;
        this.parent = parent;
        this.toolkit = toolkit;
    }

    protected String[] getTextValues() {
        List<String> values = new ArrayList<String>();
        for (ComplexTableViewerColumn column : columns) {
            String text = "";//$NON-NLS-1$
            if (column.isCombo()) {
                text = ((CCombo) column.getControl()).getText();
            } else if (column.isText()) {
                text = ((Text) column.getControl()).getText();
            } else if (column.isXPATH()) {
                Control text1 = ((Composite) column.getControl()).getChildren()[0];
                if (text1 instanceof Text) {
                    text = ((Text) text1).getText();
                }
            }
            values.add(text);
        }
        return values.toArray(new String[values.size()]);
    }

    protected String[] getInitValues() {
        List<String> values = new ArrayList<String>();
        for (ComplexTableViewerColumn column : columns) {
            String text = column.getNillValue();
            if (column.isCombo() && column.getComboValues().length > 0) {
                text = column.getComboValues()[0];
            }
            values.add(text);
        }
        return values.toArray(new String[values.size()]);
    }

    protected void markDirty() {
        if (mainPage != null) {
            mainPage.markDirtyWithoutCommit();
        }
        // fire table modify listener
        for (Object obj : modifyList.getListeners()) {
            if (obj instanceof ITableModifyListener) {
                ((ITableModifyListener) obj).handleEvent((List<Line>) getViewer().getInput());
            }
        }
    }

    protected void createLabels() {
        int i = 0;
        for (ComplexTableViewerColumn column : columns) {
            Label label = toolkit.createLabel(mainComposite, column.getName(), SWT.NULL);
            if (i == columns.size() - 1) {
                label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));
            } else {
                label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
            }
            if (!column.isNillable()) {
                label.setText(label.getText() + "(*)");//$NON-NLS-1$
                label.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
            }
            if (column.isUnique()) {
                label.setText(label.getText() + "(U)");//$NON-NLS-1$
                label.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
            }
            i++;
        }
    }

    protected void createTexts() {

        for (ComplexTableViewerColumn column : columns) {
            if (column.isCombo()) {
                CCombo combo = new CCombo(mainComposite, SWT.BORDER | SWT.READ_ONLY);
                GridData gd = new GridData(SWT.NONE, SWT.TOP, false, false, 1, 1);
                if (column.getColumnWidth() > 0) {
                    gd.widthHint = column.getColumnWidth();
                }
                combo.setLayoutData(gd);
                combo.setItems(column.getComboValues());
                if (column.getDefaultValue() == null || column.getDefaultValue().length() == 0) {
                    combo.select(0);
                } else {
                    combo.setText(column.getDefaultValue());
                }
                if (column.isComboEditable()) {
                    combo.setEditable(true);
                    combo.setText("");//$NON-NLS-1$
                }
                column.setControl(combo);
            } else {
                creatTextPortion(mainComposite, column);
            }
        }

        createRightmostPortion(mainComposite);
    }

    protected void creatTextPortion(Composite parent, ComplexTableViewerColumn column) {
        int style = SWT.BORDER;
        if (column.getTextLines() > 1) {
            style = SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL;
        }
        text = toolkit.createText(parent, column.getDefaultValue(), style);
        GridData gdata = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        if (column.getTextLines() > 1) {
            gdata.heightHint = text.getLineHeight() * column.getTextLines();
        }
        text.setLayoutData(gdata);
        column.setControl(text);
        text.addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent e) {
                text.selectAll();
            }
        });
    }

    protected void createRightmostPortion(Composite parent) {
        addButton = toolkit.createButton(parent, "", SWT.PUSH | SWT.CENTER);//$NON-NLS-1$
        addButton.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, false, false, 1, 1));
        addButton.setImage(ImageCache.getCreatedImage(EImage.ADD_OBJ.getPath()));
        addButton.setToolTipText(Messages.ComplexTableViewer_Add);
        addButton.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
            };

            @SuppressWarnings("unchecked")
            public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {

                String uniqueVal = "";//$NON-NLS-1$
                String keyVal = "";//$NON-NLS-1$
                // Make sure texts are not nill (empty) where not authorized
                for (ComplexTableViewerColumn column : columns) {
                    String text = "";//$NON-NLS-1$
                    if (column.isCombo()) {
                        text = ((CCombo) column.getControl()).getText();
                    } else if (column.isText()) {
                        text = ((Text) column.getControl()).getText();
                    } else if (column.isXPATH()) {
                        Control text1 = ((Composite) column.getControl()).getChildren()[0];
                        if (text1 instanceof Text) {
                            text = ((Text) text1).getText();
                        }
                    }
                    if (text.length() == 0) {
                        if (column.isNillable()) {
                            text = column.getNillValue();
                            if (column.isCombo()) {
                                ((CCombo) column.getControl()).setText(text);
                            } else {
                                ((Text) column.getControl()).setText(text);
                            }
                        } else {
                            MessageDialog.openError(ComplexTableViewer.this.getViewer().getControl().getShell(),
                                    Messages.ComplexTableViewer_InputError,
                                    Messages.ComplexTableViewer_ErrorMsg + column.getName()
                                            + Messages.ComplexTableViewer_ErrorMsgA);
                            return;
                        }
                    }
                    if (keyColumns != null && Arrays.asList(keyColumns).indexOf(column) >= 0) {
                        keyVal += text;
                    }
                    uniqueVal += "." + text;//$NON-NLS-1$
                }

                // check uniqueness by concatenating all the values
                List<Line> list = (List<Line>) getViewer().getInput();
                for (Line line : list) {
                    String thisLineVal = "";//$NON-NLS-1$
                    for (KeyValue keyvalue : line.keyValues) {
                        thisLineVal += "." + keyvalue.value;//$NON-NLS-1$
                    }
                    if (thisLineVal.equals(uniqueVal) || (keyVal.length() > 0 && thisLineVal.indexOf(keyVal) >= 0)) {
                        MessageDialog.openInformation(null, ERROR_ITEMALREADYEXISTS_TITLE, ERROR_ITEMALREADYEXISTS_CONTENT);
                        return;
                    }
                }

                // Update the model
                Line line = new Line(columns.toArray(new ComplexTableViewerColumn[columns.size()]), getTextValues());
                list.add(line);
                // update the instances viewer
                markDirty();
                viewer.setSelection(null);
                viewer.refresh();
                viewer.getTable().select(viewer.getTable().getItemCount() - 1);
            };
        });
    }

    protected void add() {

    }

    protected void createTable() {
        table = new Table(mainComposite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
        viewer = new TableViewer(table);

        // table.setLayoutData(new GridData(GridData.FILL_BOTH));
        table.setToolTipText(Messages.ComplexTableViewer_TableTip);
        for (ComplexTableViewerColumn column : columns) {
            TableColumn tableColumn = new TableColumn(table, SWT.CENTER);
            tableColumn.setText(column.getName());
            if (!column.isNillable()) {
                tableColumn.setImage(ImageCache.getCreatedImage(EImage.WILDCARD.getPath()));
            }
            if (column.isUnique()) {
                tableColumn.setImage(ImageCache.getCreatedImage(EImage.UNIQUE.getPath()));
            }
            if (column.getColumnWidth() > 0) {
                tableColumn.setWidth(column.getColumnWidth());
            } else {
                tableColumn.setWidth(200);
                tableColumn.pack();
            }
        }

        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.addListener(SWT.MeasureItem, new Listener() {

            public void handleEvent(Event event) {
                event.height = event.gc.getFontMetrics().getHeight() + 5;
            }
        });

    }

    protected void createViewer() {
        createTable();
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true, columns.size(), 1);
        table.setLayoutData(gd);
        gd.heightHint = 80;
        // Up Down Delete button group
        Composite stepUpDownComposite = toolkit.createComposite(mainComposite, SWT.NONE);
        stepUpDownComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
        stepUpDownComposite.setLayout(new GridLayout(1, false));

        upButton = toolkit.createButton(stepUpDownComposite, "", SWT.PUSH | SWT.CENTER);//$NON-NLS-1$
        upButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
        upButton.setImage(ImageCache.getCreatedImage(EImage.PREV_NAV.getPath()));
        upButton.setToolTipText(Messages.ComplexTableViewer_UpBtnTip);
        upButton.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
            };

            @SuppressWarnings("unchecked")
            public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
                int index = viewer.getTable().getSelectionIndex();
                if (index > 0 && index < viewer.getTable().getItemCount()) {
                    // commit as we go
                    if (mainPage != null) {
                        mainPage.setComitting(true);
                    }
                    List<Line> items = (List<Line>) viewer.getInput();
                    Line line = items.get(index);
                    items.remove(index);
                    items.add(index - 1, line);

                    viewer.refresh();

                    if (mainPage != null) {
                        mainPage.setComitting(false);
                    }
                    markDirty();
                }
            };
        });
        downButton = toolkit.createButton(stepUpDownComposite, "", SWT.PUSH | SWT.CENTER);//$NON-NLS-1$
        downButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
        downButton.setImage(ImageCache.getCreatedImage(EImage.NEXT_NAV.getPath()));
        downButton.setToolTipText(Messages.ComplexTableViewer_DownBtnTip);
        downButton.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
            };

            @SuppressWarnings("unchecked")
            public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
                int index = viewer.getTable().getSelectionIndex();
                if (index >= 0 && index < viewer.getTable().getItemCount() - 1) {
                    // commit as we go
                    if (mainPage != null) {
                        mainPage.setComitting(true);
                    }
                    List<Line> items = (List<Line>) viewer.getInput();
                    Line line = items.get(index);
                    items.remove(index);
                    items.add(index + 1, line);
                    // viewer.setInput(items);
                    viewer.refresh();
                    // TODO
                    if (mainPage != null) {
                        mainPage.setComitting(false);
                    }
                    markDirty();
                }
            };
        });
        deleteButton = toolkit.createButton(stepUpDownComposite, "", SWT.PUSH | SWT.CENTER);//$NON-NLS-1$
        deleteButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
        deleteButton.setToolTipText(Messages.ComplexTableViewer_DelBtnLabel);
        deleteButton.setImage(ImageCache.getCreatedImage(EImage.DELETE_OBJ.getPath()));
        deleteButton.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
            };

            @SuppressWarnings("unchecked")
            public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
                int index = viewer.getTable().getSelectionIndex();
                if (index >= 0 && index < viewer.getTable().getItemCount()) {
                    List<Line> items = (List<Line>) viewer.getInput();
                    items.remove(index);
                    viewer.refresh();
                    int pos = index - 1;
                    if (pos >= 0) {
                        viewer.getTable().select(pos);
                    }
                    markDirty();
                }
            };
        });

        // Create the cell editors --> We actually discard those later: not natural for an user
        CellEditor[] editors = new CellEditor[columns.size()];
        for (int i = 0; i < columns.size(); i++) {
            if (columns.get(i).isText()) {
                editors[i] = new TextCellEditor(table);
            } else if (columns.get(i).isCombo()) {
                editors[i] = new ComboBoxCellEditor(table, columns.get(i).getComboValues(), SWT.READ_ONLY);
            } else if (columns.get(i).isXPATH()) {
                editors[i] = new XpathCellEditor(table, validators.get(columns.get(i)));
            } else if (columns.get(i).isMultiMessage()) {
                editors[i] = new MultiMessageEditor(table);
                multiMsg.setColumn(table.getColumn(i));
            } else if (columns.get(i).isValidationRule()) {
                editors[i] = new ValidationRuleEditor(table);
                validationRule.setColumn(table.getColumn(i));
            }
        }
        viewer.setCellEditors(editors);

        // set the content provider
        viewer.setContentProvider(new IStructuredContentProvider() {

            public void dispose() {
            }

            public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            }

            @SuppressWarnings("unchecked")
            public Object[] getElements(Object inputElement) {
                List<Line> lines = (List<Line>) inputElement;
                return lines.toArray(new Line[lines.size()]);
            }
        });

        // set the label provider
        viewer.setLabelProvider(new ITableLabelProvider() {

            public boolean isLabelProperty(Object element, String property) {
                return false;
            }

            public void dispose() {
            }

            public void addListener(ILabelProviderListener listener) {
            }

            public void removeListener(ILabelProviderListener listener) {
            }

            public String getColumnText(Object element, int columnIndex) {
                Line line = (Line) element;
                if (columnIndex >= 0 && columnIndex < columns.size()) {
                    for (KeyValue keyvalue : line.keyValues) {
                        if (keyvalue.key.equals(columns.get(columnIndex).getName())) {
                            String val = keyvalue.value;
                            if (columns.get(columnIndex).isNillable()) {
                                if (columns.get(columnIndex).getNillValue().equals(val)) {
                                    val = columns.get(columnIndex).getNillDisplay();
                                }
                            }
                            return val;
                        }
                    }
                }
                return "";//$NON-NLS-1$
            }

            public Image getColumnImage(Object element, int columnIndex) {
                return null;
            }
        });

        // Set the column properties
        ArrayList<String> columnNames = new ArrayList<String>();
        for (ComplexTableViewerColumn column : columns) {
            columnNames.add(column.getName());
        }
        viewer.setColumnProperties(columnNames.toArray(new String[columnNames.size()]));

        // set the Cell Modifier
        viewer.setCellModifier(new ICellModifier() {

            public boolean canModify(Object element, String property) {
                // if (INSTANCE_ACCESS.equals(property)) return true; Deactivated
                return editable;
            }

            @SuppressWarnings("unchecked")
            public void modify(Object element, String property, Object value) {
                if (value instanceof Integer) {
                    if (Integer.valueOf(value.toString()) == -1) {
                        return;
                    }
                }
                // modify the text and combo cell value
                TableItem item = (TableItem) element;
                Line line = (Line) item.getData();
                int columnIndex = Arrays.asList(viewer.getColumnProperties()).indexOf(property);
                if (!isForceTextCellEditor(columnIndex) && isAColumnWithCombo(columnIndex)) {
                    String[] attrs = columns.get(columnIndex).getComboValues();
                    value = attrs[Integer.parseInt(value.toString())];
                }
                KeyValue kv = line.keyValues.get(columnIndex);
                boolean noChange = kv.value.equals(value.toString());
                kv.value = value.toString();

                viewer.refresh();
                if (!noChange) {
                    markDirty();
                }
            }

            public Object getValue(Object element, String property) {
                int columnIndex = Arrays.asList(viewer.getColumnProperties()).indexOf(property);
                Line line = (Line) element;
                // add getting value from combo
                if (isAColumnWithCombo(columnIndex)) {
                    String value = line.keyValues.get(columnIndex).value;
                    String[] attrs = columns.get(columnIndex).getComboValues();
                    return Arrays.asList(attrs).indexOf(value);
                }
                for (KeyValue keyvalue : line.keyValues) {
                    if (property.equals(keyvalue.key)) {
                        if (keyvalue.value.equals("")) {//$NON-NLS-1$
                            return columns.get(columnIndex).getNillDisplay();
                        }
                        return keyvalue.value;
                    }
                }
                return null;
            }

            private boolean isAColumnWithCombo(int columnIdx) {
                return columns.get(columnIdx).isCombo();
            }

            private boolean isForceTextCellEditor(int columnIdx) {
                return columns.get(columnIdx).isText();
            }
        });

        viewer.addSelectionChangedListener(new ISelectionChangedListener() {

            public void selectionChanged(SelectionChangedEvent event) {

                Line line = (Line) ((IStructuredSelection) event.getSelection()).getFirstElement();

                for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
                    if (line == null) {
                        Control control = columns.get(columnIndex).getControl();
                        if (control instanceof Text) {
                            ((Text) control).setText("");//$NON-NLS-1$
                        }
                        if (control instanceof CCombo) {
                            ((CCombo) control).select(0);
                        }
                        if (control instanceof Combo) {
                            ((CCombo) control).select(0);
                        }
                    } else {
                        for (KeyValue keyvalue : line.keyValues) {
                            if (keyvalue.key.equals(columns.get(columnIndex).getName())) {
                                String val = keyvalue.value;
                                Control control = columns.get(columnIndex).getControl();
                                if (control instanceof Text) {
                                    ((Text) control).setText(val);
                                }
                                if (control instanceof CCombo) {
                                    ((CCombo) control).setText(val);
                                }
                                if (control instanceof Combo) {
                                    ((CCombo) control).setText(val);
                                }
                            }
                        }
                    }
                }
            }

        });

        // display for Delete Key events to delete an instance
        viewer.getTable().addKeyListener(new KeyListener() {

            public void keyPressed(KeyEvent e) {
            }

            @SuppressWarnings("unchecked")
            public void keyReleased(KeyEvent e) {
                if ((e.stateMask == 0) && (e.character == SWT.DEL) && (viewer.getSelection() != null)) {
                    Line line = (Line) ((IStructuredSelection) viewer.getSelection()).getFirstElement();
                    // update the underlying role and refresh the table
                    // update the underlying model
                    List<Line> items = (List<Line>) viewer.getInput();
                    items.remove(line);
                    // refresh
                    viewer.refresh();
                    // mark for update
                    markDirty();
                }
            }
        });
    }

    public void create() {
        mainComposite = toolkit.createComposite(parent);
        mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.RIGHT, true, true, 1, 1));
        mainComposite.setLayout(new GridLayout(columns.size() + 1, false));

        createLabels();
        createTexts();
        createViewer();
    }

    public void setHeight(int height) {
        GridData gd = (GridData) table.getLayoutData();
        gd.heightHint = height;
    }

    public void setWidth(int width) {
        GridData gd = (GridData) table.getLayoutData();
        gd.widthHint = width;
    }

    public Composite getMainComposite() {
        return mainComposite;
    }

    protected class ValidationRuleEditor extends CellEditor implements ICellEditor {

        public ValidationRuleEditor(Composite parent) {
            super(parent);
        }

        @Override
        protected Control createControl(Composite parent) {
            createValidationRuleWidget(parent);
            ((GridData) validationRule.getComposite().getChildren()[0].getLayoutData()).heightHint = 15;
            ((GridData) validationRule.getComposite().getChildren()[1].getLayoutData()).heightHint = 15;
            if (parent instanceof Table) {
                ((Table) parent).addMouseListener(new MouseListener() {

                    public void mouseDoubleClick(MouseEvent e) {
                    }

                    public void mouseDown(MouseEvent e) {
                    }

                    public void mouseUp(MouseEvent e) {
                        if (!isMouseInControl(e)) {
                            deactive();
                        }
                    }
                });
                validationRule.getTextWidget().addKeyListener(new KeyListener() {

                    public void keyPressed(KeyEvent e) {
                    }

                    public void keyReleased(KeyEvent e) {

                        if (e.character == SWT.CR) {
                            validationRule.setText(validationRule.getText().trim());
                            deactive();
                        }
                    }
                });
            }
            return validationRule.getComposite();
        }

        public void createValidationRuleWidget(Composite parent) {
            validationRule = new ValidationRuleWidget(parent, getCurrentTreeParent(), conceptName);
        }

        boolean isMouseInControl(MouseEvent e) {
            if (e.widget instanceof Table) {
                if (validationRule.getComposite().getBounds().contains(e.x, e.y)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        protected Object doGetValue() {
            XpathSelectDialog.setContext(null);
            return validationRule.getText();
        }

        @Override
        protected void doSetFocus() {
            validationRule.getTextWidget().setFocus();
        }

        public void deactive() {
            if (isActivated()) {
                super.focusLost();
            }
            XpathSelectDialog.setContext(null);
        }

        @Override
        protected void doSetValue(Object value) {
            validationRule.setText(value.toString());
            if (viewer.getTable().getSelection().length > 0) {
                Line line = (Line) viewer.getTable().getSelection()[0].getData();
                String context = line.keyValues.get(1).value;
                XpathSelectDialog.setContext("newXPath".equalsIgnoreCase(context) ? null : context);//$NON-NLS-1$

            }
        }

    }

    class MultiMessageEditor extends CellEditor implements ICellEditor {

        public MultiMessageEditor(Composite parent) {
            super(parent);
        }

        @Override
        protected Control createControl(Composite parent) {
            multiMsg = new DescAnnotationComposite(null, null, toolkit, parent, mainPage, true);

            ((GridData) multiMsg.getComposite().getChildren()[0].getLayoutData()).heightHint = 15;
            ((GridData) multiMsg.getComposite().getChildren()[1].getLayoutData()).heightHint = 15;
            if (parent instanceof Table) {
                ((Table) parent).addMouseListener(new MouseListener() {

                    public void mouseDoubleClick(MouseEvent e) {
                    }

                    public void mouseDown(MouseEvent e) {
                    }

                    public void mouseUp(MouseEvent e) {
                        if (!isMouseInControl(e)) {
                            deactive();
                        }
                    }
                });
                // add by ymli, fix bug 0009441
                multiMsg.getTextWidget().addKeyListener(new KeyListener() {

                    public void keyPressed(KeyEvent e) {
                    }

                    public void keyReleased(KeyEvent e) {

                        if (e.character == SWT.CR) {
                            multiMsg.setText(multiMsg.getText().trim());
                            deactive();
                        }
                    }
                });
            }
            return multiMsg.getComposite();
        }

        boolean isMouseInControl(MouseEvent e) {
            if (e.widget instanceof Table) {
                if (multiMsg.getComposite().getBounds().contains(e.x, e.y)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        protected Object doGetValue() {

            return multiMsg.getText();
        }

        @Override
        protected void doSetFocus() {

            multiMsg.getTextWidget().setFocus();
        }

        public void deactive() {
            if (isActivated()) {
                super.focusLost();
            }
        }

        @Override
        protected void doSetValue(Object value) {

            multiMsg.setText(value.toString().trim());
        }

    }

    protected XpathWidget getNewXpathWidget(Composite parent, CellEditor cellEditor) {
        if (treeParent == null) {
            treeParent = (TreeParent) ((IAdaptable) mainPage).getAdapter(TreeParent.class);
        }
        return new XpathWidget(parent, treeParent, false);
    }

    class XpathCellEditor extends CellEditor implements ICellEditor {

        private String oldPath = ""; //$NON-NLS-1$

        protected XpathWidget xpath;

        private IXPathValidator validator;

        public XpathWidget getXpath() {
            return xpath;
        }

        public XpathCellEditor(Composite parent, IXPathValidator ixPathValidator) {
            super(parent);
            this.validator = ixPathValidator;
        }

        @Override
        protected Control createControl(Composite parent) {

            xpath = getNewXpathWidget(parent);
            FocusAdapter afterFocusLost = new FocusAdapter() {

                @Override
                public void focusLost(FocusEvent e) {
                    XpathCellEditor.this.focusLost();
                }

            };
            xpath.setAfterFocusAction(afterFocusLost);
            // The ending| bug:21784
            xpath.setLock(modelLock);
            xpaths.add(xpath);
            xpath.setConceptName(conceptName);
            xpath.setDataModelName(getDatamodelName());
            ((GridData) xpath.getComposite().getChildren()[0].getLayoutData()).heightHint = 15;
            ((GridData) xpath.getComposite().getChildren()[1].getLayoutData()).heightHint = 15;
            if (parent instanceof Table) {
                FocusAdapter focusListener = new FocusAdapter() {

                    @Override
                    public void focusGained(FocusEvent e) {
                        oldPath = xpath.getText().trim();
                    }

                    @Override
                    public void focusLost(FocusEvent e) {
                        afterLostFocus();
                    }
                };

                xpath.addButtonFocusListener(focusListener);

                // add by ymli, fix bug 0009441
                xpath.getTextWidget().addKeyListener(new KeyListener() {

                    public void keyPressed(KeyEvent e) {

                    }

                    public void keyReleased(KeyEvent e) {

                        if (e.character == SWT.CR) {
                            deactive();
                        }
                    }

                });

            }

            return xpath.getComposite();
        }

        private void afterLostFocus() {
            Display.getDefault().asyncExec(new Runnable() {

                public void run() {
                    if ((!xpath.getTextWidget().isFocusControl()) && !xpath.getButton().isFocusControl()) {
                        XpathCellEditor.this.focusLost();
                    }
                }
            });

        }

        @Override
        protected Object doGetValue() {
            ComplexTableViewer.this.datamodelName = xpath.getDataModelName();
            if (context) {
                XpathSelectDialog.setContext(null);
            }

            restoreIfInvalid();
            return xpath.getText();
        }

        @Override
        protected void doSetFocus() {

            xpath.getTextWidget().setFocus();
        }

        public void deactive() {
            if (isActivated()) {
                focusLost();
            }
        }

        @Override
        protected void doSetValue(Object value) {
            xpath.setText(value.toString().trim());
        }

        @Override
        protected void focusLost() {
            restoreIfInvalid();
            super.focusLost();
        }

        private void restoreIfInvalid() {
            if (validator != null && !validator.validate(xpath.getText())) {
                xpath.setText(oldPath);
            }
        }
    }

    public void addTableModifyListener(ITableModifyListener listener) {
        modifyList.add(listener);
    }

    public void removeTableModifyListener(ITableModifyListener listener) {
        modifyList.remove(listener);
    }

    public void updateEditoes(CellEditor[] editors) {
        for (int i = 0; i < columns.size(); i++) {
            if (columns.get(i).isText()) {
                editors[i] = new TextCellEditor(table);
            } else if (columns.get(i).isCombo()) {
                editors[i] = new ComboBoxCellEditor(table, columns.get(i).getComboValues(), SWT.READ_ONLY);
            } else if (columns.get(i).isXPATH()) {
                editors[i] = new XpathCellEditor(table, validators.get(columns.get(i)));
            } else if (columns.get(i).isMultiMessage()) {
                editors[i] = new MultiMessageEditor(table);
                multiMsg.setColumn(table.getColumn(i));
            } else if (columns.get(i).isValidationRule()) {
                editors[i] = new ValidationRuleEditor(table);
                validationRule.setColumn(table.getColumn(i));
            }
        }

    }

    protected XpathWidget getNewXpathWidget(Composite parent) {
        if (treeParent == null) {
            treeParent = (TreeParent) ((IAdaptable) mainPage).getAdapter(TreeParent.class);
        }
        return new XpathWidget(parent, treeParent, false);
    }

}
