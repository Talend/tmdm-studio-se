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
package com.amalto.workbench.dialogs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.w3c.dom.Element;

import com.amalto.workbench.MDMWorbenchPlugin;
import com.amalto.workbench.editors.DataModelMainPage;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.models.IAnnotationConst;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.widgets.composites.ElementFKInfoConfiguration;
import com.amalto.workbench.widgets.composites.ElementFKInfoFormatHelper;
import com.amalto.workbench.widgets.composites.ElementFKInfoFormatViewer;

public class AnnotationOrderedListsDialog extends Dialog implements IPropertyChangeListener {

    private static Log log = LogFactory.getLog(AnnotationOrderedListsDialog.class);

    protected Control textControl;

    protected Button checkBox;

    protected TableViewer viewer;

    private List<String> roles;

    protected List<String> xPaths = new ArrayList<String>();

    private SelectionListener caller = null;

    private String title = "";//$NON-NLS-1$

    private String[] columnNames = null;

    private boolean recursive = true;

    private boolean lock;

    private boolean retrieveFKinfos = true;

    public boolean isRetrieveFKinfos() {
        return retrieveFKinfos;
    }

    public void setRetrieveFKinfos(boolean retrieveFKinfos) {
        this.retrieveFKinfos = retrieveFKinfos;
    }

    private DataModelMainPage parentPage = null;

    private TreeObject xObject = null;

    private int actionType;

    private String dataModelName;

    public static final int AnnotationForeignKeyInfo_ActionType = 1 << 0;

    public static final int AnnotationHidden_ActionType = 1 << 1;

    public static final int AnnotationTargetSystems_ActionType = 1 << 2;

    public static final int AnnotationWrite_ActionType = 1 << 3;

    public static final int AnnotationSchematron_ActionType = 1 << 4;

    public static final int AnnotationLookupField_ActionType = 1 << 5;

    public static final int AnnotationPrimaKeyInfo_ActionType = 1 << 6;

    private String formatFKInfo;

    private ElementFKInfoFormatViewer formatEditor;

    private IAnnotationOrderedListsDialogExtender extender;

    /**
     * @param parentShell
     */
    public AnnotationOrderedListsDialog(List<String> xPaths, SelectionListener caller, Shell parentShell, String title,
            String columnName, DataModelMainPage parentPage, int actionType, String dataModelName) {
        this(caller, parentShell, title, parentPage, actionType, dataModelName, null);
        this.columnNames = new String[] { columnName };
        this.xPaths = xPaths;
    }

    public AnnotationOrderedListsDialog(SelectionListener caller, Shell parentShell, String title, DataModelMainPage parentPage,
            int actionType, String dataModelName, IAnnotationOrderedListsDialogExtender extender) {
        super(parentShell);
        this.extender = extender;
        setShellStyle(this.getShellStyle() | SWT.RESIZE);
        this.caller = caller;
        this.title = title;
        this.parentPage = parentPage;
        this.xObject = parentPage.getXObject();
        this.actionType = actionType;
        this.dataModelName = dataModelName;
    }

    private List<String> getConceptElements() {
        DataModelMainPage page = parentPage;
        IStructuredSelection selection = (IStructuredSelection) page.getTreeViewer().getSelection();
        List<String> childNames = new ArrayList<String>();
        XSDElementDeclaration decl = null;
        if (selection.getFirstElement() instanceof XSDElementDeclaration) {
            decl = (XSDElementDeclaration) selection.getFirstElement();
            // childNames = Util.getChildElementNames(decl.getElement());
        } else if (selection.getFirstElement() instanceof Element) {
            TreePath tPath = ((TreeSelection) selection).getPaths()[0];
            XSDComponent xSDCom = null;
            for (int i = 0; i < tPath.getSegmentCount(); i++) {
                if (tPath.getSegment(i) instanceof XSDAnnotation) {
                    xSDCom = (XSDAnnotation) (tPath.getSegment(i));
                    break;
                }
            }
            decl = (XSDElementDeclaration) xSDCom.getContainer();
        }
        try {
            childNames = Util.getChildElementNames(decl.getName(), decl);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return childNames;
    }

    protected List<String> getAllRolesStr() {
        return Util.getChildren(this.xObject.getServerRoot(), TreeObject.ROLE);
    }

    @Override
    protected Control createDialogArea(Composite parent) {

        // Should not really be here but well,....

        parent.getShell().setText(this.title);

        Composite composite = (Composite) super.createDialogArea(parent);

        GridLayout layout = (GridLayout) composite.getLayout();
        layout.numColumns = 3;
        layout.makeColumnsEqualWidth = false;

        if (actionType == AnnotationWrite_ActionType || actionType == AnnotationHidden_ActionType) {
            textControl = new CCombo(composite, SWT.BORDER | SWT.READ_ONLY);
            roles = getAllRolesStr();
            ((CCombo) textControl).setItems(roles.toArray(new String[roles.size()]));

        } else if (actionType == AnnotationLookupField_ActionType || actionType == AnnotationPrimaKeyInfo_ActionType) {
            textControl = new CCombo(composite, SWT.BORDER | SWT.READ_ONLY);

            roles = getConceptElements();
            ((CCombo) textControl).setItems(roles.toArray(new String[roles.size()]));

        } else {
            if (actionType == AnnotationOrderedListsDialog.AnnotationSchematron_ActionType) {
                textControl = new Text(composite, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
            } else {
                textControl = new Text(composite, SWT.BORDER | SWT.SINGLE);
            }
        }

        if (actionType == AnnotationOrderedListsDialog.AnnotationForeignKeyInfo_ActionType) {
            textControl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        } else {
            if (actionType == AnnotationOrderedListsDialog.AnnotationSchematron_ActionType) {
                textControl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 7));
            } else {
                textControl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
            }
        }

        ((GridData) textControl.getLayoutData()).minimumWidth = 400;

        textControl.addKeyListener(new KeyListener() {

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if ((e.stateMask == 0) && (e.character == SWT.CR)) {
                    addXPath(-1, AnnotationOrderedListsDialog.getControlText(textControl));
                    viewer.refresh();
                    fireXPathsChanges();
                }
            }
        });

        if (actionType == AnnotationOrderedListsDialog.AnnotationForeignKeyInfo_ActionType) {
            Button xpathButton = new Button(composite, SWT.PUSH | SWT.CENTER);
            xpathButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
            xpathButton.setText("...");//$NON-NLS-1$
            xpathButton.setToolTipText(Messages.AnnotationOrderedListsDialog_SelectXpath);
            xpathButton.addSelectionListener(new SelectionListener() {

                @Override
                public void widgetDefaultSelected(SelectionEvent e) {

                }

                @Override
                public void widgetSelected(SelectionEvent e) {
                    XpathSelectDialog dlg = getNewXpathSelectDialog(parentPage, dataModelName);

                    dlg.setLock(lock);

                    dlg.setBlockOnOpen(true);

                    dlg.open();

                    if (dlg.getReturnCode() == Window.OK) {
                        ((Text) textControl).setText(dlg.getXpath());
                        dlg.close();
                    }

                }

            });

        }

        Button addLabelButton = new Button(composite, SWT.PUSH);
        addLabelButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
        addLabelButton.setImage(ImageCache.getCreatedImage(EImage.ADD_OBJ.getPath()));
        addLabelButton.setToolTipText(Messages.AnnotationOrderedListsDialog_Add);
        addLabelButton.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
            };

            @Override
            public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
                boolean exist = false;
                for (String string : getXPaths()) {
                    if (string.equals(getControlText(textControl))) {
                        exist = true;
                    }
                }
                if (!exist && getControlText(textControl) != null && getControlText(textControl) != "") {
                    addXPath(-1, getControlText(textControl));
                }
                viewer.refresh();
                fireXPathsChanges();
            };
        });

        viewer = new TableViewer(composite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
        viewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
        ((GridData) viewer.getControl().getLayoutData()).heightHint = 100;
        // Set up the underlying table
        Table table = viewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        if (extender != null) {
            String[] roleNames = roles.toArray(new String[0]);
            extender.configTableViewer(viewer, roleNames);
            viewer.addSelectionChangedListener(extender.getViewerSelectionChangedListener(viewer, textControl));
        } else {
            // Set the column properties
            viewer.setColumnProperties(this.columnNames);
            for (int i = 0; i < this.columnNames.length; i++) {
                String columnName = columnNames[i];
                TableColumn column = new TableColumn(table, SWT.CENTER);
                column.setText(columnName);
                table.getColumn(i).pack();
            }
            table.getColumn(0).setWidth(500);
            // default
            // Create the cell editors --> We actually discard those later: not natural for an user
            CellEditor[] editors = new CellEditor[1];
            if (actionType == AnnotationOrderedListsDialog.AnnotationWrite_ActionType
                    || actionType == AnnotationOrderedListsDialog.AnnotationHidden_ActionType
                    || actionType == AnnotationLookupField_ActionType || actionType == AnnotationPrimaKeyInfo_ActionType) {
                editors[0] = new ComboBoxCellEditor(table, roles.toArray(new String[] {}), SWT.READ_ONLY);
            } else {
                editors[0] = new TextCellEditor(table);
            }

            viewer.setCellEditors(editors);

            // set the content provider
            viewer.setContentProvider(new IStructuredContentProvider() {

                @Override
                public void dispose() {
                }

                @Override
                public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
                }

                @Override
                public Object[] getElements(Object inputElement) {
                    @SuppressWarnings("unchecked")
                    List<String> xPaths = (List<String>) inputElement;
                    List<DescriptionLine> lines = new ArrayList<DescriptionLine>();
                    for (String xPath : xPaths) {
                        DescriptionLine line = new DescriptionLine(xPath);
                        lines.add(line);
                    }
                    // we return an instance line made of a Sring and a boolean
                    return lines.toArray(new DescriptionLine[lines.size()]);
                }
            });

            // set the label provider
            viewer.setLabelProvider(new ITableLabelProvider() {

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
                        return line.getLabel();
                    }
                    return "";//$NON-NLS-1$
                }

                @Override
                public Image getColumnImage(Object element, int columnIndex) {
                    return null;
                }
            });

            // set the Cell Modifier
            viewer.setCellModifier(new ICellModifier() {

                @Override
                public boolean canModify(Object element, String property) {
                    return true;
                }

                @Override
                public void modify(Object element, String property, Object value) {

                    TableItem item = (TableItem) element;
                    DescriptionLine line = (DescriptionLine) item.getData();
                    String orgValue = line.getLabel();
                    if (actionType != AnnotationWrite_ActionType && actionType != AnnotationHidden_ActionType
                            && actionType != AnnotationLookupField_ActionType
                            && actionType != AnnotationPrimaKeyInfo_ActionType) {
                        int targetPos = getXPaths().indexOf(value.toString());
                        if (targetPos < 0) {
                            line.setLabel(value.toString());
                            int index = getXPaths().indexOf(orgValue);
                            removeXPath(index);
                            addXPath(index, value.toString());
                            viewer.update(line, null);
                        } else if (targetPos >= 0 && !value.toString().equals(orgValue)) {
                            MessageDialog.openInformation(null, Messages.Warning,
                                    Messages.AnnotationOrderedListsDialog_ValueAlreadyExists);
                        }
                    } else {
                        String[] attrs = roles.toArray(new String[] {});
                        int index = Integer.parseInt(value.toString());
                        if (index == -1) {
                            return;
                        }
                        value = attrs[index];
                        int pos = getXPaths().indexOf(value.toString());
                        if (pos >= 0 && !(orgValue.equals(value))) {
                            MessageDialog.openInformation(null, Messages.Warning, Messages.AnnotationOrderedListsDialog_);
                            return;
                        } else if (pos < 0) {
                            index = getXPaths().indexOf(orgValue);
                            line.setLabel(value.toString());
                            removeXPath(index);
                            addXPath(index, value.toString());
                            viewer.update(line, null);
                        }
                    }

                    fireXPathsChanges();
                }

                @Override
                public Object getValue(Object element, String property) {
                    DescriptionLine line = (DescriptionLine) element;
                    String value = line.getLabel();

                    if (actionType == AnnotationWrite_ActionType || actionType == AnnotationHidden_ActionType
                            || actionType == AnnotationLookupField_ActionType
                            || actionType == AnnotationPrimaKeyInfo_ActionType) {
                        String[] attrs = roles.toArray(new String[] {});
                        return Arrays.asList(attrs).indexOf(value);
                    } else {
                        return value;
                    }

                }
            });
            // Listen for changes in the selection of the viewer to display additional parameters
            viewer.addSelectionChangedListener(new ISelectionChangedListener() {

                @Override
                public void selectionChanged(SelectionChangedEvent event) {
                    DescriptionLine line = (DescriptionLine) ((IStructuredSelection) viewer.getSelection()).getFirstElement();
                    if (line != null) {
                        if (textControl instanceof CCombo) {
                            ((CCombo) textControl).setText(line.getLabel());
                        }
                        if (textControl instanceof Text) {
                            ((Text) textControl).setText(line.getLabel());
                        }
                    }
                }
            });
        }

        // display for Delete Key events to delete an instance
        viewer.getTable().addKeyListener(new KeyListener() {

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                // System.out.println("Table keyReleased() ");
                if ((e.stateMask == 0) && (e.character == SWT.DEL) && (viewer.getSelection() != null)) {
                    DescriptionLine line = (DescriptionLine) ((IStructuredSelection) viewer.getSelection()).getFirstElement();
                    @SuppressWarnings("unchecked")
                    ArrayList<String> xPaths = (ArrayList<String>) viewer.getInput();
                    xPaths.remove(line.getLabel());
                    viewer.refresh();
                }
            }
        });

        viewer.setInput(extender != null ? extender.getInput() : getXPaths());
        viewer.refresh();

        Composite rightButtonsComposite = new Composite(composite, SWT.NULL);
        rightButtonsComposite.setLayout(new GridLayout(1, true));
        rightButtonsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));

        Button upButton = new Button(rightButtonsComposite, SWT.PUSH | SWT.CENTER);
        upButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
        upButton.setImage(ImageCache.getCreatedImage(EImage.PREV_NAV.getPath()));
        upButton.setToolTipText(Messages.AnnotationOrderedListsDialog_MoveUpTheItem);
        upButton.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
            };

            @Override
            public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
                String selected = getLineLabel(((IStructuredSelection) viewer.getSelection()).getFirstElement());
                if (selected == null) {
                    return;
                }
                int i = 0;
                for (String xPath : getXPaths()) {
                    if (xPath.equals(selected)) {
                        if (i > 0) {
                            switchXPath(i, i - 1);
                            viewer.refresh();
                            viewer.getTable().setSelection(i - 1);
                            viewer.getTable().showSelection();
                            fireXPathsChanges();
                        }
                        return;
                    }
                    i++;
                }
            };
        });
        Button downButton = new Button(rightButtonsComposite, SWT.PUSH | SWT.CENTER);
        downButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
        downButton.setImage(ImageCache.getCreatedImage(EImage.NEXT_NAV.getPath()));
        downButton.setToolTipText(Messages.AnnotationOrderedListsDialog_MoveDownTheItem);
        downButton.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
            };

            @Override
            public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
                String selected = getLineLabel(((IStructuredSelection) viewer.getSelection()).getFirstElement());
                if (selected == null) {
                    return;
                }
                int i = 0;
                for (String xPath : getXPaths()) {
                    if (xPath.equals(selected)) {
                        if (i < getXPaths().size() - 1) {
                            switchXPath(i, i + 1);
                            viewer.refresh();
                            viewer.getTable().setSelection(i + 1);
                            viewer.getTable().showSelection();
                            fireXPathsChanges();
                        }
                        return;
                    }
                    i++;
                }
            };
        });
        Button delButton = new Button(rightButtonsComposite, SWT.PUSH | SWT.CENTER);
        delButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
        delButton.setImage(ImageCache.getCreatedImage(EImage.DELETE_OBJ.getPath()));
        delButton.setToolTipText(Messages.AnnotationOrderedListsDialog_DelTheItem);

        delButton.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
            };

            @Override
            public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
                String selected = getLineLabel(((IStructuredSelection) viewer.getSelection()).getFirstElement());
                if (selected == null) {
                    return;
                }
                int index = getXPaths().indexOf(selected);
                removeXPath(index);
                viewer.refresh();
                fireXPathsChanges();

            };
        });

        textControl.setFocus();
        if (actionType != AnnotationOrderedListsDialog.AnnotationForeignKeyInfo_ActionType
                && actionType != AnnotationOrderedListsDialog.AnnotationTargetSystems_ActionType
                && actionType != AnnotationOrderedListsDialog.AnnotationSchematron_ActionType
                && actionType != AnnotationOrderedListsDialog.AnnotationLookupField_ActionType
                && actionType != AnnotationOrderedListsDialog.AnnotationPrimaKeyInfo_ActionType) {
            checkBox = new Button(composite, SWT.CHECK);
            checkBox.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true, 2, 1));
            checkBox.addSelectionListener(new SelectionListener() {

                @Override
                public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
                };

                @Override
                public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
                    recursive = checkBox.getSelection();
                };
            });
            checkBox.setSelection(recursive);
            checkBox.setText(Messages.AnnotationOrderedListsDialog_SetRoleRecursively);
        }

        if (actionType == AnnotationForeignKeyInfo_ActionType) {
            createFKInfoFormatComp(composite);
            addDoubleClickListener();
        }

        return composite;
    }

    private void createFKInfoFormatComp(Composite parent) {
        initializeDefaultPreferences();

        Group formatGroup = new Group(parent, SWT.NONE);
        GridData glayoutData = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
        formatGroup.setLayoutData(glayoutData);
        GridLayout layout = new GridLayout();
        layout.marginLeft = 0;
        layout.marginRight = 0;
        layout.marginTop = 0;
        layout.marginBottom = 0;
        formatGroup.setLayout(layout);
        formatGroup.setText(Messages.FormatFKInfoComp_format);

        IVerticalRuler verticalRuler = ElementFKInfoFormatHelper.createVerticalRuler();
        IOverviewRuler overviewRuler = ElementFKInfoFormatHelper.createOverviewRuler();

        formatEditor = new ElementFKInfoFormatViewer(formatGroup, verticalRuler, overviewRuler, true,
                SWT.V_SCROLL | SWT.H_SCROLL);
        formatEditor.addPropertyChangeListener(this);
        formatEditor.configure(new ElementFKInfoConfiguration());
        formatEditor.initilize();

        GridData layoutData = new GridData(GridData.FILL_BOTH);
        layoutData.heightHint = 150;
        formatEditor.getControl().setLayoutData(layoutData);
        formatEditor.setFkinfos(getXPaths());
        formatEditor.setFormatFKInfo(formatFKInfo);
    }

    private void fireXPathsChanges() {
        if (actionType == AnnotationForeignKeyInfo_ActionType) {
            Set<Annotation> updatedAnnotations = formatEditor.setFkinfos(getXPaths());
            updateOKBtnState(updatedAnnotations.size() == 0);
        }
    }

    private void addDoubleClickListener() {
        viewer.addDoubleClickListener(new IDoubleClickListener() {

            @Override
            public void doubleClick(DoubleClickEvent event) {
                IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
                DescriptionLine line = (DescriptionLine) selection.getFirstElement();
                formatEditor.getTextWidget().insert(line.getLabel());
            }
        });
    }

    public static final String PREF_COLOR_DEFAULT = "colorDefault"; //$NON-NLS-1$

    public static final String PREF_COLOR_STRING = "colorString"; //$NON-NLS-1$

    public static final String PREF_COLOR_KEYWORD = "colorKeyword"; //$NON-NLS-1$

    public void initializeDefaultPreferences() {
        IPreferenceStore store = MDMWorbenchPlugin.getDefault().getPreferenceStore();

        store.setDefault(PREF_COLOR_DEFAULT, StringConverter.asString(new RGB(0, 128, 0)));
        store.setDefault(PREF_COLOR_STRING, StringConverter.asString(new RGB(0, 0, 255)));
        store.setDefault(PREF_COLOR_KEYWORD, StringConverter.asString(new RGB(0, 0, 128)));
    }

    private void updateOKBtnState(boolean btnEnabled) {
        if (getButton(IDialogConstants.OK_ID) != null) {
            getButton(IDialogConstants.OK_ID).setEnabled(btnEnabled);
        }
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);

        getButton(IDialogConstants.OK_ID).addSelectionListener(this.caller);
        getButton(IDialogConstants.CANCEL_ID).addSelectionListener(this.caller);
    }

    protected XpathSelectDialog getNewXpathSelectDialog(DataModelMainPage parentPage, String dataModelName) {
        return new XpathSelectDialog(parentPage.getSite().getShell(), xObject.getParent(),
                Messages.AnnotationOrderedListsDialog_SelectXPathXX, parentPage.getSite(), false, dataModelName);
    }

    @Override
    protected void okPressed() {
        if (formatEditor != null) {
            formatFKInfo = formatEditor.getFormatFKInfo();
        }

        setReturnCode(OK);
        getButton(IDialogConstants.OK_ID).setData("dialog", AnnotationOrderedListsDialog.this);//$NON-NLS-1$
        // no close let Action Handler handle it
    }

    @Override
    protected void cancelPressed() {
        setReturnCode(CANCEL);
        getButton(IDialogConstants.CANCEL_ID).setData("dialog", AnnotationOrderedListsDialog.this);//$NON-NLS-1$
        // no close let Action Handler handle it
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        Object newValue = event.getNewValue();
        if (newValue instanceof Boolean) {
            updateOKBtnState(((Boolean) newValue).booleanValue());
        }
    }

    /**************************************************************************************************
     * Public getters read by caller
     ***************************************************************************************************/
    public List<String> getXPaths() {
        if (extender != null) {
            return extender.getXPaths();
        }
        return xPaths;
    }

    public Map<String, List<String>> getXPathMap() {
        if (extender != null) {
            return extender.getXPathMap();
        }
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        map.put(IAnnotationConst.KEY_WRITE, getXPaths());
        return map;
    }

    protected String getLineLabel(Object value) {
        if (value == null) {
            return null;
        }
        if (extender != null) {
            return extender.getLineLabel(value);
        }
        return ((DescriptionLine) value).getLabel();
    }

    protected void addXPath(int index, String xpath) {
        if (extender != null) {
            extender.addXPath(index, xpath);
        }
        if (index < 0) {
            xPaths.add(xpath);
        } else {
            xPaths.add(index, xpath);
        }

    }

    protected void removeXPath(int index) {
        if (extender != null) {
            extender.removeXPath(index);
        } else {
            xPaths.remove(index);
        }
    }

    protected void switchXPath(int beforeIndex, int afterIndex) {
        if (extender != null) {
            extender.switchXPath(beforeIndex, afterIndex);
        } else {
            String path = getXPaths().get(beforeIndex);
            removeXPath(beforeIndex);
            addXPath(afterIndex, path);
        }
    }

    public boolean getRecursive() {
        return recursive;
    }

    public String getFormatFKInfo() {
        return formatFKInfo;
    }

    public void setFormatFKInfo(String formatedFkInfo) {
        this.formatFKInfo = formatedFkInfo;
    }

    private static String getControlText(Control textControl) {
        if (textControl instanceof CCombo) {
            return ((CCombo) textControl).getText();
        } else if (textControl instanceof Text) {
            return ((Text) textControl).getText();
        } else {
            return "";//$NON-NLS-1$
        }

    }

    public boolean isLock() {
        return lock;
    }

    public void setLock(boolean lock) {
        this.lock = lock;
    }

    /**************************************************************************************************
     * A table viewer line
     ***************************************************************************************************/
    class DescriptionLine {

        private String label;

        public DescriptionLine(String label) {
            super();
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }
    }
}
