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
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
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
        layout.numColumns = 3;
        createDialogContent(composite);
        return composite;
    }

    protected void createDialogContent(Composite composite) {
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
        labelText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
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
        addLabelButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
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

        final String LANGUAGE = Messages.AnnotationLanguageLabelsDialog_Language;
        final String LABEL = Messages.AnnotationLanguageLabelsDialog_Label;
        descriptionsViewer = new TableViewer(composite,
                SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
        descriptionsViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
        ((GridData) descriptionsViewer.getControl().getLayoutData()).heightHint = 100;
        // Set up the underlying table
        Table table = descriptionsViewer.getTable();
        new TableColumn(table, SWT.LEFT).setText(LANGUAGE);
        new TableColumn(table, SWT.CENTER).setText(LABEL);
        table.getColumn(0).setWidth(150);
        table.getColumn(1).setWidth(150);
        for (int i = 2, n = table.getColumnCount(); i < n; i++) {
            table.getColumn(i).pack();
        }
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
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
                deleteItem();
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
        // set the label provider
        descriptionsViewer.setLabelProvider(new ITableLabelProvider() {

            @Override
            public boolean isLabelProperty(Object element, String property) {
                return false;
            }

            @Override
            public void dispose() {
            }

            @Override
            public void addListener(ILabelProviderListener listener) {
            }

            @Override
            public void removeListener(ILabelProviderListener listener) {
            }

            @Override
            public String getColumnText(Object element, int columnIndex) {
                DescriptionLine line = (DescriptionLine) element;
                switch (columnIndex) {
                case 0:
                    return line.getLanguage();
                case 1:
                    return line.getLabel();
                }
                return "";//$NON-NLS-1$
            }

            @Override
            public Image getColumnImage(Object element, int columnIndex) {
                return null;
            }
        });
        // Set the column properties
        descriptionsViewer.setColumnProperties(new String[] { LANGUAGE, LABEL });
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
                if ((e.stateMask == 0) && (e.character == SWT.DEL) && (descriptionsViewer.getSelection() != null)) {
                    deleteItem();
                }
            }
        });

        descriptionsViewer.setInput(descriptionsMap);
        descriptionsViewer.refresh();

        labelText.setFocus();
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
}
