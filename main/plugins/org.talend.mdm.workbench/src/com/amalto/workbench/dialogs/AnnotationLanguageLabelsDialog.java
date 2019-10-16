// ============================================================================
//
// Copyright (C) 2006-2019 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.workbench.dialogs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.utils.Util;

public class AnnotationLanguageLabelsDialog extends Dialog {

    protected Text labelText;

    protected Combo languagesCombo;

    protected TableViewer descriptionsViewer;

    protected Map<String, String> descriptionsMap = new LinkedHashMap<String, String>();

    private SelectionListener caller = null;

    private String title = "";//$NON-NLS-1$

    private LanguageComparator comparator;

    private static final String LANGUAGE = Messages.AnnotationLanguageLabelsDialog_Language;

    private static final String LABEL = Messages.AnnotationLanguageLabelsDialog_Label;

    /**
     * @param parentShell
     */
    public AnnotationLanguageLabelsDialog(Map<String, String> descriptionsMap, SelectionListener caller, Shell parentShell,
            String title) {
        super(parentShell);
        if (descriptionsMap != null) {
            this.descriptionsMap = descriptionsMap;
        }
        this.caller = caller;
        this.title = title;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        // Should not really be here but well,....
        parent.getShell().setText(this.title);
        Composite composite = (Composite) super.createDialogArea(parent);
        GridLayout layout = (GridLayout) composite.getLayout();
        layout.numColumns = 4;
        createDialogContent(composite);
        return composite;
    }

    protected void createDialogContent(Composite composite) {
        Label langLabel = new Label(composite, SWT.NONE);
        langLabel.setText(Messages.AnnotationLanguageLabelsDialog_LanguageLabel);
        langLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, true, 1, 1));
        languagesCombo = new Combo(composite, SWT.READ_ONLY | SWT.DROP_DOWN | SWT.SINGLE);
        languagesCombo.setLayoutData(new GridData(SWT.BEGINNING, SWT.NONE, false, false, 1, 1));

        Set<String> languages = Util.lang2iso.keySet();
        for (Object element : languages) {
            String language = (String) element;
            languagesCombo.add(language);
        }
        languagesCombo.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
            }
        });
        languagesCombo.select(0);
        labelText = new Text(composite, SWT.BORDER | SWT.SINGLE);
        labelText.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false, 1, 1));
        ((GridData) labelText.getLayoutData()).minimumWidth = 150;
        labelText.addKeyListener(new KeyListener() {

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if ((e.stateMask == 0) && (e.character == SWT.CR)) {
                    String isoCode = Util.lang2iso.get(languagesCombo.getText());
                    descriptionsMap.put(isoCode, labelText.getText());
                    descriptionsViewer.refresh();
                }
            }
        });

        Button addLabelButton = new Button(composite, SWT.PUSH);
        addLabelButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
        addLabelButton.setImage(ImageCache.getCreatedImage(EImage.ADD_OBJ.getPath()));
        addLabelButton.setToolTipText(Messages.AnnotationLanguageLabelsDialog_Add);
        addLabelButton.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
            };

            @Override
            public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
                String code = Util.lang2iso.get(languagesCombo.getText());
                descriptionsMap.put(code, labelText.getText());
                descriptionsViewer.refresh();
            };
        });

        descriptionsViewer = new TableViewer(composite,
                SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
        descriptionsViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
        ((GridData) descriptionsViewer.getControl().getLayoutData()).heightHint = 100;
        // Set up the underlying table
        Table table = descriptionsViewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        // Set the sorter for the table
        comparator = new LanguageComparator();
        descriptionsViewer.setComparator(comparator);
        Button delLabelButton = new Button(composite, SWT.PUSH);
        delLabelButton.setLayoutData(new GridData(SWT.NONE, SWT.NONE, false, false, 1, 1));
        delLabelButton.setImage(ImageCache.getCreatedImage(EImage.DELETE_OBJ.getPath()));
        delLabelButton.setToolTipText(Messages.AnnotationLanguageLabelsDialog_Del);
        delLabelButton.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
            };

            @Override
            public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
                ISelection selection = descriptionsViewer.getSelection();
                if (selection != null && !selection.isEmpty()) {
                    deleteItem();
                }
            };
        });
        // Create the cell editors --> We actually discard those later: not natural for an user
        CellEditor[] editors = new CellEditor[2];
        editors[0] = new ComboBoxCellEditor(table, Util.lang2iso.keySet().toArray(new String[] {}), SWT.READ_ONLY);
        editors[1] = new TextCellEditor(table);
        descriptionsViewer.setCellEditors(editors);
        // set the content provider
        descriptionsViewer.setContentProvider(new IStructuredContentProvider() {

            @Override
            public void dispose() {
            }

            @Override
            public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            }

            @Override
            public Object[] getElements(Object inputElement) {
                LinkedHashMap<String, String> descs = (LinkedHashMap<String, String>) inputElement;
                Set<String> languages = descs.keySet();
                ArrayList<DescriptionLine> lines = new ArrayList<DescriptionLine>();
                for (Object element : languages) {
                    String language = ((String) element);
                    DescriptionLine line = new DescriptionLine(Util.iso2lang.get(language), descs.get(language));
                    lines.add(line);
                }
                // we return an instance line made of a Sring and a boolean
                return lines.toArray(new DescriptionLine[lines.size()]);
            }
        });
        // Set the column properties
        descriptionsViewer.setColumnProperties(new String[] { LANGUAGE, LABEL });
        createColumns();
        // set the Cell Modifier
        descriptionsViewer.setCellModifier(new ICellModifier() {

            @Override
            public boolean canModify(Object element, String property) {
                return true;
            }

            @Override
            public void modify(Object element, String property, Object value) {
                TableItem item = (TableItem) element;
                int columnIndex = Arrays.asList(descriptionsViewer.getColumnProperties()).indexOf(property);
                DescriptionLine line = (DescriptionLine) item.getData();
                if (columnIndex == 0) {
                    String[] attrs = Util.lang2iso.keySet().toArray(new String[] {});
                    int orgIndx = Arrays.asList(attrs).indexOf(line.getLanguage());
                    if (orgIndx != Integer.parseInt(value.toString())) {
                        String newLang = attrs[Integer.parseInt(value.toString())];
                        if (descriptionsMap.containsKey(Util.lang2iso.get(newLang))) {
                            MessageDialog.openInformation(null, Messages.Warnning,
                                    Messages.AnnotationLanguageLabelsDialog_InforContent);
                            return;
                        }
                        descriptionsMap.remove(Util.lang2iso.get(line.getLanguage()));
                        line.setLanguage(newLang);
                        descriptionsMap.put(Util.lang2iso.get(newLang), line.getLabel());
                    }
                } else // column label
                {
                    line.setLabel(value.toString());
                    descriptionsMap.put(Util.lang2iso.get(line.getLanguage()), line.getLabel());
                }

                descriptionsViewer.update(line, null);
            }

            @Override
            public Object getValue(Object element, String property) {
                int columnIndex = Arrays.asList(descriptionsViewer.getColumnProperties()).indexOf(property);
                DescriptionLine line = (DescriptionLine) element;
                if (columnIndex == 0) {
                    String[] attrs = Util.lang2iso.keySet().toArray(new String[] {});
                    return Arrays.asList(attrs).indexOf(line.getLanguage());
                } else {
                    if (LANGUAGE.equals(property)) {
                        return line.getLanguage();
                    }
                    if (LABEL.equals(property)) {
                        return line.getLabel();
                    }
                }

                return null;
            }
        });

        // Listen for changes in the selection of the viewer to display additional parameters
        descriptionsViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
            }
        });

        // display for Delete Key events to delete an instance
        descriptionsViewer.getTable().addKeyListener(new KeyListener() {

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                ISelection selection = descriptionsViewer.getSelection();
                if ((e.stateMask == 0) && (e.character == SWT.DEL) && (selection != null && !selection.isEmpty())) {
                    deleteItem();
                }
            }
        });

        descriptionsViewer.setInput(descriptionsMap);
        descriptionsViewer.refresh();

        labelText.setFocus();
    }

    // This will create the columns for the table
    private void createColumns() {
        // First column is for the language
        TableViewerColumn col = createTableViewerColumn(LANGUAGE, 150, 0);
        col.setLabelProvider(new ColumnLabelProvider() {

            @Override
            public String getText(Object element) {
                DescriptionLine line = (DescriptionLine) element;
                return line.getLanguage();
            }
        });

        // Second column is for label
        col = createTableViewerColumn(LABEL, 150, 1);
        col.setLabelProvider(new ColumnLabelProvider() {

            @Override
            public String getText(Object element) {
                DescriptionLine line = (DescriptionLine) element;
                return line.getLabel();
            }
        });
    }

    private TableViewerColumn createTableViewerColumn(String title, int bound, final int colNumber) {
        final TableViewerColumn viewerColumn = new TableViewerColumn(descriptionsViewer, SWT.NONE);
        final TableColumn column = viewerColumn.getColumn();
        column.setText(title);
        column.setWidth(bound);
        column.setResizable(true);
        column.setMoveable(true);
        column.addSelectionListener(getSelectionAdapter(column, colNumber));
        return viewerColumn;
    }

    private SelectionAdapter getSelectionAdapter(final TableColumn column, final int index) {
        SelectionAdapter selectionAdapter = new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                comparator.setColumn(index);
                int dir = comparator.getDirection();
                descriptionsViewer.getTable().setSortDirection(dir);
                descriptionsViewer.getTable().setSortColumn(column);
                descriptionsViewer.refresh();
            }
        };
        return selectionAdapter;
    }

    private void deleteItem() {
        DescriptionLine line = (DescriptionLine) ((IStructuredSelection) descriptionsViewer.getSelection()).getFirstElement();
        String isoCode = Util.lang2iso.get(line.getLanguage());
        LinkedHashMap<String, String> descs = (LinkedHashMap<String, String>) descriptionsViewer.getInput();
        descs.remove(isoCode);
        descriptionsViewer.refresh();
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);
        if (caller != null) {
            getButton(IDialogConstants.OK_ID).addSelectionListener(this.caller);
            getButton(IDialogConstants.CANCEL_ID).addSelectionListener(this.caller);
        }
    }

    @Override
    protected void okPressed() {
        setReturnCode(OK);
        getButton(IDialogConstants.OK_ID).setData("dialog", AnnotationLanguageLabelsDialog.this);//$NON-NLS-1$
        // no close let Action Handler handle it
        if (caller == null) {
            close();
        }
    }

    @Override
    protected void cancelPressed() {
        setReturnCode(CANCEL);
        getButton(IDialogConstants.CANCEL_ID).setData("dialog", AnnotationLanguageLabelsDialog.this);//$NON-NLS-1$
        // no close let Action Handler handle it
        if (caller == null) {
            close();
        }
    }

    /**************************************************************************************************
     * Public getters read by caller
     ***************************************************************************************************/

    /*
     * public TableViewer getDescriptionsTableViewer() { return descriptionsViewer; }
     */

    public Map<String, String> getDescriptionsMap() {
        return descriptionsMap;
    }

    /**************************************************************************************************
     * A table viewer line
     ***************************************************************************************************/
    class DescriptionLine {

        private String language;

        private String label;

        public DescriptionLine(String language, String label) {
            super();
            this.language = language;
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }
    }

    public class LanguageComparator extends ViewerComparator {

        private int propertyIndex;

        private static final int DESCENDING = 1;

        private int direction = DESCENDING;

        public LanguageComparator() {
            this.propertyIndex = 0;
            direction = DESCENDING;
        }

        public int getDirection() {
            return direction == 1 ? SWT.DOWN : SWT.UP;
        }

        public void setColumn(int column) {
            if (column == this.propertyIndex) {
                // Same column as last sort; toggle the direction
                direction = 1 - direction;
            } else {
                // New column; do an ascending sort
                this.propertyIndex = column;
                direction = DESCENDING;
            }
        }

        @Override
        public int compare(Viewer viewer, Object e1, Object e2) {
            DescriptionLine p1 = (DescriptionLine) e1;
            DescriptionLine p2 = (DescriptionLine) e2;
            int rc = 0;
            switch (propertyIndex) {
            case 0:
                String lang1 = p1.getLanguage() == null ? "" : p1.getLanguage().toLowerCase();
                String lang2 = p2.getLanguage() == null ? "" : p2.getLanguage().toLowerCase();
                rc = lang1.compareTo(lang2);
                break;
            case 1:
                String label1 = p1.getLabel() == null ? "" : p1.getLabel().toLowerCase();
                String label2 = p2.getLabel() == null ? "" : p2.getLabel().toLowerCase();
                rc = label1.compareTo(label2);
                break;
            default:
                rc = 0;
            }
            // If descending order, flip the direction
            if (direction == DESCENDING) {
                rc = -rc;
            }
            return rc;
        }
    }
}
