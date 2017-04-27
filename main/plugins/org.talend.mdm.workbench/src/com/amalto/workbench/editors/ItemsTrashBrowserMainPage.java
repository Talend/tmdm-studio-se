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
package com.amalto.workbench.editors;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.talend.mdm.webservice.TMDMService;
import org.talend.mdm.webservice.WSDataClusterPK;
import org.talend.mdm.webservice.WSDroppedItem;
import org.talend.mdm.webservice.WSDroppedItemPK;
import org.talend.mdm.webservice.WSFindAllDroppedItemsPKs;
import org.talend.mdm.webservice.WSItemPK;
import org.talend.mdm.webservice.WSLoadDroppedItem;
import org.talend.mdm.webservice.WSRecoverDroppedItem;
import org.talend.mdm.webservice.WSRemoveDroppedItem;

import com.amalto.workbench.dialogs.DOMViewDialog;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.models.IXObjectModelListener;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.providers.XObjectBrowserInput;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.widgets.WidgetFactory;

public class ItemsTrashBrowserMainPage extends AMainPage implements IXObjectModelListener {

    private static Log log = LogFactory.getLog(ItemsTrashBrowserMainPage.class);

    protected static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");//$NON-NLS-1$

    protected Text searchText;

    protected TableViewer resultsViewer;

    protected boolean[] ascending = { true, false, false, false, false };

    public ItemsTrashBrowserMainPage(FormEditor editor) {
        super(editor, ItemsTrashBrowserMainPage.class.getName(), Messages.ItemsTrashBrowserMainPage_0
                + ((XObjectBrowserInput) editor.getEditorInput()).getName());
        // listen to events
        ((XObjectBrowserInput) editor.getEditorInput()).addListener(this);
    }

    @Override
    protected void createCharacteristicsContent(FormToolkit toolkit, Composite charComposite) {
        // Everything is implemented in createFormContent
    }

    @Override
    protected void createFormContent(IManagedForm managedForm) {

        try {

            // sets the title
            managedForm.getForm().setText(this.getTitle());

            // get the toolkit
            FormToolkit toolkit = managedForm.getToolkit();

            // get the body
            Composite composite = managedForm.getForm().getBody();
            composite.setLayout(new GridLayout(9, false));

            /***
             * Search Text
             */
            Label descriptionLabel = toolkit.createLabel(composite, Messages.ItemsTrashBrowserMainPage_1, SWT.NULL);
            descriptionLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
            searchText = toolkit.createText(composite, "*", SWT.BORDER);//$NON-NLS-1$
            searchText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
            // searchText.addModifyListener(this);
            searchText.addKeyListener(new KeyListener() {

                public void keyPressed(KeyEvent e) {
                }

                public void keyReleased(KeyEvent e) {
                    if ((e.stateMask == 0) && (e.character == SWT.CR)) {
                        ItemsTrashBrowserMainPage.this.resultsViewer.setInput(getResults(true));
                        ItemsTrashBrowserMainPage.this.resultsViewer.getTable().setFocus();
                    }
                }// keyReleased
            }// keyListener
                    );

            // search
            Button bSearch = toolkit.createButton(composite, "", SWT.CENTER);//$NON-NLS-1$
            bSearch.setImage(ImageCache.getCreatedImage(EImage.BROWSE.getPath()));
            bSearch.setToolTipText(Messages.ItemsTrashBrowserMainPage_2);
            bSearch.setLayoutData(new GridData(SWT.NONE, SWT.CENTER, false, false, 1, 1));
            bSearch.addListener(SWT.Selection, new Listener() {

                public void handleEvent(Event event) {
                    ItemsTrashBrowserMainPage.this.resultsViewer.setInput(getResults(true));
                };
            });

            /***
             * Create Table
             */
            final Table table = createTable(composite);

            resultsViewer = new TableViewer(table);

            resultsViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 9, 1));
            ((GridData) resultsViewer.getControl().getLayoutData()).heightHint = 500;
            resultsViewer.setContentProvider(new ArrayContentProvider());
            resultsViewer.setLabelProvider(new ClusterTableLabelProvider());
            resultsViewer.addDoubleClickListener(new IDoubleClickListener() {

                public void doubleClick(DoubleClickEvent event) {
                    resultsViewer.setSelection(event.getSelection());
                    try {
                        new DisplayDroppedItemAction(ItemsTrashBrowserMainPage.this.getSite().getShell(), resultsViewer).run();
                    } catch (Exception e) {
                        MessageDialog.openError(ItemsTrashBrowserMainPage.this.getSite().getShell(), Messages._Error,
                                Messages.ItemsTrashBrowserMainPage_4 + e.getClass().getName()
                                        + Messages.ItemsTrashBrowserMainPage_5 + e.getLocalizedMessage());
                    }
                }
            });

            hookKeyboard();

            hookContextMenu();

            managedForm.reflow(true); // nothng will show on the form if not called

            searchText.setFocus();
            // adapt body add mouse/focus listener for child
            WidgetFactory factory = WidgetFactory.getWidgetFactory();
            factory.adapt(managedForm.getForm().getBody());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }// createFormContent

    /**
     * Create the Table
     */
    private Table createTable(Composite parent) {
        int style = SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION;

        final Table table = new Table(parent, style);

        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalSpan = 5;
        table.setLayoutData(gridData);

        table.setLinesVisible(true);
        table.setHeaderVisible(true);

        // 1st column
        final TableColumn column = new TableColumn(table, SWT.LEFT, 0);
        column.setText(Messages.ItemsTrashBrowserMainPage_6);
        column.setWidth(150);
        column.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
                ascending[0] = !ascending[0];
                ItemsTrashBrowserMainPage.this.resultsViewer.setSorter(new TableSorter(0, ascending[0]));
            }

            public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
                ascending[0] = !ascending[0];
                table.setSortColumn(column);
                ItemsTrashBrowserMainPage.this.resultsViewer.setSorter(new TableSorter(0, ascending[0]));
                if (ascending[0]) {

                    table.setSortDirection(SWT.UP);
                } else {
                    table.setSortDirection(SWT.DOWN);
                }
            }
        });

        // 2nd column
        final TableColumn column1 = new TableColumn(table, SWT.LEFT, 1);
        column1.setText(Messages.ItemsTrashBrowserMainPage_7);
        column1.setWidth(150);
        // Add listener to column so tasks are sorted by description when clicked
        column1.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
                ascending[1] = !ascending[1];
                ItemsTrashBrowserMainPage.this.resultsViewer.setSorter(new TableSorter(1, ascending[1]));
            }

            public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
                ascending[1] = !ascending[1];
                table.setSortColumn(column1);
                ItemsTrashBrowserMainPage.this.resultsViewer.setSorter(new TableSorter(1, ascending[1]));
                if (ascending[1]) {

                    table.setSortDirection(SWT.UP);
                } else {
                    table.setSortDirection(SWT.DOWN);
                }
            }
        });

        // 3rd column
        final TableColumn column2 = new TableColumn(table, SWT.LEFT, 2);
        column2.setText(Messages.ItemsTrashBrowserMainPage_8);
        column2.setWidth(150);
        // Add listener to column so tasks are sorted by description when clicked
        column2.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
                ascending[2] = !ascending[2];
                ItemsTrashBrowserMainPage.this.resultsViewer.setSorter(new TableSorter(2, ascending[2]));
            }

            public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
                ascending[2] = !ascending[2];
                table.setSortColumn(column2);
                ItemsTrashBrowserMainPage.this.resultsViewer.setSorter(new TableSorter(2, ascending[2]));
                if (ascending[2]) {
                    table.setSortDirection(SWT.UP);
                } else {

                    table.setSortDirection(SWT.DOWN);
                }
            }
        });

        // 4th column
        final TableColumn column3 = new TableColumn(table, SWT.LEFT, 3);
        column3.setText(Messages.ItemsTrashBrowserMainPage_9);
        column3.setWidth(150);
        // Add listener to column so tasks are sorted by description when clicked
        column3.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
                table.setSortDirection(SWT.UP);
                ItemsTrashBrowserMainPage.this.resultsViewer.setSorter(new TableSorter(3, ascending[3]));
            }

            public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
                ascending[3] = !ascending[3];
                table.setSortColumn(column3);
                ItemsTrashBrowserMainPage.this.resultsViewer.setSorter(new TableSorter(3, ascending[3]));
                if (ascending[3]) {
                    table.setSortDirection(SWT.UP);
                } else {
                    table.setSortDirection(SWT.DOWN);
                }
            }
        });

        // 5th column
        final TableColumn column4 = new TableColumn(table, SWT.LEFT, 4);
        column4.setText(Messages.ItemsTrashBrowserMainPage_10);
        column4.setWidth(150);
        // Add listener to column so tasks are sorted by description when clicked
        column4.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
                ascending[4] = !ascending[4];
                ItemsTrashBrowserMainPage.this.resultsViewer.setSorter(new TableSorter(4, ascending[4]));
            }

            public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
                ascending[4] = !ascending[4];
                table.setSortColumn(column4);
                ItemsTrashBrowserMainPage.this.resultsViewer.setSorter(new TableSorter(4, ascending[4]));
                if (ascending[4]) {

                    table.setSortDirection(SWT.UP);
                } else {
                    table.setSortDirection(SWT.DOWN);
                }
            }
        });

        return table;
    }

    @Override
    protected void createActions() {

    }

    /*********************************
     * IXObjectModelListener interface
     *********************************/
    public void handleEvent(int type, TreeObject parent, TreeObject child) {
        refreshData();
    }

    @Override
    protected void refreshData() {
        try {

            searchText.setFocus();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            MessageDialog.openError(this.getSite().getShell(), Messages.ItemsTrashBrowserMainPage_11,
                    Messages.ItemsTrashBrowserMainPage_12 + e.getLocalizedMessage());
        }
    }

    @Override
    protected void commit() {
        try {

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            MessageDialog.openError(this.getSite().getShell(), Messages.ItemsTrashBrowserMainPage_13,
                    Messages.ItemsTrashBrowserMainPage_14 + e.getLocalizedMessage());
        }
    }

    private void hookKeyboard() {
        resultsViewer.getControl().addKeyListener(new KeyListener() {

            public void keyPressed(KeyEvent e) {
            }

            public void keyReleased(KeyEvent e) {
                if (e.keyCode == SWT.DEL) {
                    new RemoveAction(ItemsTrashBrowserMainPage.this.getSite().getShell(),
                            ItemsTrashBrowserMainPage.this.resultsViewer).run();
                }
            }
        });
    }

    private void hookContextMenu() {
        MenuManager menuMgr = new MenuManager();
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {

            public void menuAboutToShow(IMenuManager manager) {
                // ItemsTrashBrowserMainPage.this.fillContextMenu(manager);
                manager.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));

                manager.appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS, new RestoreAction(ItemsTrashBrowserMainPage.this
                        .getSite().getShell(), ItemsTrashBrowserMainPage.this.resultsViewer));

                manager.appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS, new RemoveAction(ItemsTrashBrowserMainPage.this
                        .getSite().getShell(), ItemsTrashBrowserMainPage.this.resultsViewer));

            }
        });
        Menu menu = menuMgr.createContextMenu(resultsViewer.getControl());
        resultsViewer.getControl().setMenu(menu);
        getSite().registerContextMenu(menuMgr, resultsViewer);
    }

    protected void fillContextMenu(IMenuManager manager) {

        return;

    }

    protected LineItem[] getResults(boolean showResultInfo) {

        Cursor waitCursor = null;

        try {

            Display display = getEditor().getSite().getPage().getWorkbenchWindow().getWorkbench().getDisplay();
            waitCursor = new Cursor(display, SWT.CURSOR_WAIT);
            this.getSite().getShell().setCursor(waitCursor);

            TMDMService service = Util.getMDMService(getXObject());

            String search = searchText.getText();

            List<WSDroppedItemPK> results = null;
            if (search != null && search.length() > 0) {
                results = service.findAllDroppedItemsPKs(new WSFindAllDroppedItemsPKs(search)).getWsDroppedItemPK();
            }

            if ((results == null) || (results.isEmpty())) {
                if (showResultInfo) {
                    MessageDialog.openInformation(this.getSite().getShell(), Messages.ItemsTrashBrowserMainPage_15,
                            Messages.ItemsTrashBrowserMainPage_16);
                    return new LineItem[0];
                }
            } else {
                LineItem[] res = new LineItem[results.size()];
                for (int i = 0; i < results.size(); i++) {
                    WSDroppedItemPK wsDroppedItemPK = results.get(i);
                    WSItemPK refWSItemPK = wsDroppedItemPK.getWsItemPK();

                    // if(revison==null||revison.equals(""))revison="head";

                    res[i] = new LineItem(refWSItemPK.getWsDataClusterPK().getPk(), refWSItemPK.getConceptName(), refWSItemPK
                            .getIds().toArray(new String[0]), null, wsDroppedItemPK.getPartPath());
                }
                return res;
            }

            return new LineItem[0];
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if ((e.getLocalizedMessage() != null) && e.getLocalizedMessage().contains("10000")) {
                MessageDialog.openError(this.getSite().getShell(), Messages.ItemsTrashBrowserMainPage_17,
                        Messages.ItemsTrashBrowserMainPage_18);
            } else {
                if (!Util.handleConnectionException(this, e, null)) {
                    MessageDialog.openError(this.getSite().getShell(), Messages.ItemsTrashBrowserMainPage_19,
                            e.getLocalizedMessage());
                }
            }
            return null;
        } finally {
            try {
                this.getSite().getShell().setCursor(null);
                waitCursor.dispose();
            } catch (Exception e) {
            }
        }

    }

    /***************************************************************
     * Display Dropped Item Action
     * 
     ***************************************************************/
    class DisplayDroppedItemAction extends Action {

        protected Shell shell = null;

        protected Viewer viewer;

        public DisplayDroppedItemAction(Shell shell, Viewer viewer) {
            super();
            this.shell = shell;
            this.viewer = viewer;
            setImageDescriptor(ImageCache.getImage("icons/edit_obj.gif"));//$NON-NLS-1$
            setText(Messages.ItemsTrashBrowserMainPage_20);
            setToolTipText(Messages.ItemsTrashBrowserMainPage_21);
        }

        @Override
        public void run() {
            try {
                super.run();

                IStructuredSelection selection = ((IStructuredSelection) viewer.getSelection());
                LineItem li = (LineItem) selection.getFirstElement();

                WSDroppedItem wsDroppedItem = Util.getMDMService(getXObject()).loadDroppedItem(
                        new WSLoadDroppedItem(new WSDroppedItemPK(li.getPartPath(), new WSItemPK(li.getConcept(), Arrays
                                .asList(li.getIds()), new WSDataClusterPK(li.getDataCluster())))

                        ));

                // mask
                String projection = wsDroppedItem.getProjection();
                Pattern pLoad = Pattern.compile(".*?(<c>.*?</t>).*?(<p>(.*)</p>|<p/>).*", Pattern.DOTALL);//$NON-NLS-1$
                Matcher m = pLoad.matcher(projection);
                if (m.matches()) {
                    if (m.group(2) == null || m.group(2).equals("<p/>")) {//$NON-NLS-1$
                        projection = "";//$NON-NLS-1$
                    } else {
                        projection = m.group(3);
                    }
                }

                String userName = wsDroppedItem.getInsertionUserName() == null ? "undefine" : wsDroppedItem//$NON-NLS-1$
                        .getInsertionUserName();
                String droppedTime = wsDroppedItem.getInsertionTime() == null ? "undefine" : sdf.format(wsDroppedItem//$NON-NLS-1$
                        .getInsertionTime());
                String desc = Messages.ItemsTrashBrowserMainPage_22 + userName + Messages.ItemsTrashBrowserMainPage_23
                        + droppedTime;

                final DOMViewDialog d = new DOMViewDialog(ItemsTrashBrowserMainPage.this.getSite().getShell(),
                        Util.parse(projection), false, null, DOMViewDialog.TREE_VIEWER, null, desc);

                d.addListener(new Listener() {

                    public void handleEvent(Event event) {
                        if (event.button == DOMViewDialog.BUTTON_CLOSE) {
                            d.close();
                        }// if

                    }// handleEvent
                });

                d.setBlockOnOpen(true);
                d.open();

            } catch (Exception e) {
                log.error(e.getMessage(), e);
                MessageDialog.openError(shell, Messages._Error, Messages.ItemsTrashBrowserMainPage_25 + e.getLocalizedMessage());
            }
        }

        @Override
        public void runWithEvent(Event event) {
            super.runWithEvent(event);
        }

    }

    /***************************************************************
     * Restore Dropped Item Action
     * 
     ***************************************************************/
    class RestoreAction extends Action {

        protected Shell shell = null;

        protected Viewer viewer;

        public RestoreAction(Shell shell, Viewer viewer) {
            super();
            this.shell = shell;
            this.viewer = viewer;
            setImageDescriptor(ImageCache.getImage("icons/add_obj.gif"));//$NON-NLS-1$
            setText(Messages.ItemsTrashBrowserMainPage_26);
            setToolTipText(Messages.ItemsTrashBrowserMainPage_26);
        }

        @Override
        public void run() {
            try {
                super.run();

                IStructuredSelection selection = ((IStructuredSelection) viewer.getSelection());
                List<?> list = selection.toList();
                if (list != null && list.size() > 0) {
                    TMDMService service = Util.getMDMService(getXObject());
                    for (Object obj : list) {
                        LineItem li = (LineItem) obj;

                        WSDroppedItemPK wsDroppedItemPK = new WSDroppedItemPK(li.getPartPath(), new WSItemPK(li.getConcept(),
                                Arrays.asList(li.getIds()), new WSDataClusterPK(li.getDataCluster())));

                        service.recoverDroppedItem(new WSRecoverDroppedItem(wsDroppedItemPK));
                    }

                    // refresh the search
                    ItemsTrashBrowserMainPage.this.resultsViewer.setInput(getResults(false));
                }

            } catch (Exception e) {
                if (!Util.handleConnectionException(shell, e, null)) {
                    MessageDialog.openError(shell, Messages._Error,
                            Messages.ItemsTrashBrowserMainPage_29 + e.getLocalizedMessage());
                }
            }
        }

        @Override
        public void runWithEvent(Event event) {
            super.runWithEvent(event);
        }

    }

    /***************************************************************
     * Delete Dropped Item Action
     * 
     ***************************************************************/
    class RemoveAction extends Action {

        protected Shell shell = null;

        protected Viewer viewer;

        public RemoveAction(Shell shell, Viewer viewer) {
            super();
            this.shell = shell;
            this.viewer = viewer;
            setImageDescriptor(ImageCache.getImage("icons/delete_obj.gif"));//$NON-NLS-1$
            setText(Messages.ItemsTrashBrowserMainPage_30);
            setToolTipText(Messages.ItemsTrashBrowserMainPage_30);
        }

        @Override
        public void run() {
            try {
                super.run();

                // retrieve the item
                IStructuredSelection selection = ((IStructuredSelection) viewer.getSelection());

                List<?> list = selection.toList();
                if (list != null && list.size() > 0) {
                    if (!MessageDialog.openConfirm(this.shell, Messages.ItemsTrashBrowserMainPage_32,
                            Messages.ItemsTrashBrowserMainPage_33)) {
                        return;
                    }

                    TMDMService service = Util.getMDMService(getXObject());
                    for (Object obj : list) {
                        LineItem li = (LineItem) obj;
                        WSDroppedItemPK wsDroppedItemPK = new WSDroppedItemPK(li.getPartPath(), new WSItemPK(li.getConcept(),
                                Arrays.asList(li.getIds()), new WSDataClusterPK(li.getDataCluster())));
                        // run
                        service.removeDroppedItem(new WSRemoveDroppedItem(wsDroppedItemPK));
                    }

                    // refresh the search
                    ItemsTrashBrowserMainPage.this.resultsViewer.setInput(getResults(false));
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                if (!Util.handleConnectionException(shell, e, null)) {
                    MessageDialog.openError(shell, Messages._Error,
                            Messages.ItemsTrashBrowserMainPage_35 + e.getLocalizedMessage());
                }
            }
        }

        @Override
        public void runWithEvent(Event event) {
            super.runWithEvent(event);
        }

    }

    /***************************************************************
     * Table Label Provider
     * 
     ***************************************************************/
    class ClusterTableLabelProvider implements ITableLabelProvider {

        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

        public String getColumnText(Object element, int columnIndex) {
            LineItem li = (LineItem) element;
            switch (columnIndex) {
            case 0:
                return li.getDataCluster();
            case 1:
                return li.getConcept();
            case 2:
                return Util.joinStrings(li.getIds(), ".");//$NON-NLS-1$
            case 3:
                return li.getRevision();
            case 4:
                return li.getPartPath();
            default:
                return "???????";//$NON-NLS-1$
            }
        }

        public void addListener(ILabelProviderListener listener) {
        }

        public void dispose() {
        }

        public boolean isLabelProperty(Object element, String property) {
            return false;
        }

        public void removeListener(ILabelProviderListener listener) {
        }

    }

    /***************************************************************
     * Table Sorter
     * 
     ***************************************************************/
    class TableSorter extends ViewerSorter {

        int column = 0;

        boolean asc = true;

        public TableSorter(int column, boolean ascending) {
            super();
            this.column = column;
            this.asc = ascending;
        }

        @Override
        public int compare(Viewer viewer, Object e1, Object e2) {
            LineItem li1 = (LineItem) e1;
            LineItem li2 = (LineItem) e2;

            int res = 0;

            switch (column) {

            case 0:
                res = li1.getDataCluster().compareToIgnoreCase(li2.getDataCluster());
                break;
            case 1:
                res = li1.getConcept().compareToIgnoreCase(li2.getConcept());
                break;
            case 2:
                res = Util.joinStrings(li1.getIds(), ".").compareToIgnoreCase(Util.joinStrings(li2.getIds(), "."));//$NON-NLS-1$//$NON-NLS-2$
                break;
            case 3:
                res = li1.getRevision().compareToIgnoreCase(li2.getRevision());
                break;
            case 4:
                res = li1.getPartPath().compareToIgnoreCase(li2.getPartPath());
                break;
            default:
                res = 0;
            }
            if (asc) {
                return res;
            } else {
                return -res;
            }
        }

    }

    /***************************************************************
     * A Line Item Bean
     * 
     ***************************************************************/
    class LineItem {

        private String dataCluster;

        private String concept;

        private String[] ids;

        private String revision;

        private String partPath;

        public LineItem(String dataCluster, String concept, String[] ids, String revision, String partPath) {
            super();
            this.concept = concept;
            this.dataCluster = dataCluster;
            this.ids = ids;
            this.partPath = partPath;
            this.revision = revision;
        }

        public String getDataCluster() {
            return dataCluster;
        }

        public void setDataCluster(String dataCluster) {
            this.dataCluster = dataCluster;
        }

        public String getConcept() {
            return concept;
        }

        public void setConcept(String concept) {
            this.concept = concept;
        }

        public String[] getIds() {
            return ids;
        }

        public void setIds(String[] ids) {
            this.ids = ids;
        }

        public String getRevision() {
            return revision;
        }

        public void setRevision(String revision) {
            this.revision = revision;
        }

        public String getPartPath() {
            return partPath;
        }

        public void setPartPath(String partPath) {
            this.partPath = partPath;
        }

    }

}
