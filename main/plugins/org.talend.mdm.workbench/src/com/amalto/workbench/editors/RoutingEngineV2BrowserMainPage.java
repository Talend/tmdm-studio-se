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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.talend.mdm.webservice.TMDMService;
import org.talend.mdm.webservice.WSGetRoutingOrderV2ByCriteriaWithPaging;
import org.talend.mdm.webservice.WSGetServicesList;
import org.talend.mdm.webservice.WSRoutingEngineV2Action;
import org.talend.mdm.webservice.WSRoutingEngineV2ActionCode;
import org.talend.mdm.webservice.WSRoutingEngineV2Status;
import org.talend.mdm.webservice.WSRoutingOrderV2;
import org.talend.mdm.webservice.WSRoutingOrderV2SearchCriteriaWithPaging;
import org.talend.mdm.webservice.WSRoutingOrderV2Status;
import org.talend.mdm.webservice.WSServicesListItem;

import com.amalto.workbench.editors.actions.DeleteItemsAction;
import com.amalto.workbench.editors.actions.EditItemAction;
import com.amalto.workbench.editors.actions.ExecuteRoutingOrdersAction;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.models.IXObjectModelListener;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.providers.XObjectBrowserInput;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.utils.XtentisException;
import com.amalto.workbench.widgets.CalendarSelectWidget;
import com.amalto.workbench.widgets.IPagingListener;
import com.amalto.workbench.widgets.PageingToolBar;
import com.amalto.workbench.widgets.WidgetFactory;

public class RoutingEngineV2BrowserMainPage extends AMainPage implements IXObjectModelListener, IPagingListener {

    /**
     *
     */
    private static final String BLANK = ""; //$NON-NLS-1$

    private static Log log = LogFactory.getLog(RoutingEngineV2BrowserMainPage.class);

    protected static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");//$NON-NLS-1$

    protected Label statusLabel;

    protected Text fromText;

    protected Text toText;

    protected Combo statusCombo;

    protected Text idText;

    protected Combo serviceCombo;

    protected Text anyFieldText;

    protected Text documentTypeText;

    protected TableViewer resultsViewer;

    protected ListViewer wcListViewer;

    protected Button suspendButton;

    private PageingToolBar pageToolBar;

    protected boolean[] ascending = { true, false, false, false };

    private Button stopButton;

    private Button startButton;

    public RoutingEngineV2BrowserMainPage(FormEditor editor) {
        super(editor, RoutingEngineV2BrowserMainPage.class.getName(), ((XObjectBrowserInput) editor.getEditorInput()).getName());
        // listen to events
        ((XObjectBrowserInput) editor.getEditorInput()).addListener(this);
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
            composite.setLayout(new GridLayout(1, false));

            // Create a Router status holder
            Composite statusComposite = toolkit.createComposite(composite, SWT.NONE);
            statusComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
            statusComposite.setLayout(new GridLayout(5, false));

            // status
            Label descriptionLabel = toolkit.createLabel(statusComposite,
                    Messages.RoutingEngineV2BrowserMainPage_EventManagerStatus, SWT.NULL);
            descriptionLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, true, 1, 1));
            statusLabel = toolkit.createLabel(statusComposite, "                                           ", SWT.NULL);//$NON-NLS-1$
            statusLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, true, 1, 1));
            startButton = toolkit.createButton(statusComposite, BLANK, SWT.PUSH);
            startButton.setImage(ImageCache.getCreatedImage(EImage.RUN_EXC.getPath()));
            startButton.setToolTipText(Messages.RoutingEngineV2BrowserMainPage_Start);
            startButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, true, 1, 1));
            startButton.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    startSubscriptionEngine();
                    updateButtons();
                };
            });
            stopButton = toolkit.createButton(statusComposite, BLANK, SWT.PUSH);
            stopButton.setImage(ImageCache.getCreatedImage(EImage.STOP.getPath()));
            stopButton.setToolTipText(Messages.RoutingEngineV2BrowserMainPage_Stop);
            stopButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, true, 1, 1));
            stopButton.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    stopSubscriptionEngine();
                    updateButtons();
                };
            });
            suspendButton = toolkit.createButton(statusComposite, BLANK, SWT.PUSH);
            suspendButton.setImage(ImageCache.getCreatedImage(EImage.SUSPEND.getPath()));
            suspendButton.setToolTipText(Messages.RoutingEngineV2BrowserMainPage_Suspend);
            suspendButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, true, 1, 1));
            suspendButton.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    suspendSubscriptionEngine();
                    updateButtons();
                };
            });

            Composite separator = toolkit.createCompositeSeparator(composite);
            separator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
            ((GridData) separator.getLayoutData()).heightHint = 2;

            // first Line of routing Orders
            Composite firstLineComposite = toolkit.createComposite(composite);
            firstLineComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
            firstLineComposite.setLayout(new GridLayout(9, false));

            // Routing Orders Label
            Label routingOrdersLabel = toolkit.createLabel(firstLineComposite,
                    Messages.RoutingEngineV2BrowserMainPage_RoutingOrders, SWT.NULL);
            routingOrdersLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 9, 1));

            // from
            Label fromLabel = toolkit.createLabel(firstLineComposite, Messages.RoutingEngineV2BrowserMainPage_From, SWT.NULL);
            fromLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
            CalendarSelectWidget fromCalendar = new CalendarSelectWidget(toolkit, firstLineComposite, true);
            fromText = fromCalendar.getText();

            // to
            Label toLabel = toolkit.createLabel(firstLineComposite, Messages.RoutingEngineV2BrowserMainPage_To, SWT.NULL);
            toLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
            CalendarSelectWidget toCalendar = new CalendarSelectWidget(toolkit, firstLineComposite, false);
            toText = toCalendar.getText();

            Label statusLab = toolkit.createLabel(firstLineComposite, Messages.RoutingEngineV2BrowserMainPage_Status, SWT.NULL);
            statusLab.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
            statusCombo = new Combo(firstLineComposite, SWT.READ_ONLY | SWT.DROP_DOWN | SWT.MULTI);
            statusCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
            statusCombo.addKeyListener(new KeyAdapter() {

                @Override
                public void keyReleased(KeyEvent e) {
                    if ((e.stateMask == 0) && (e.character == SWT.CR)) {
                        doSearch();
                    }
                }// keyReleased
            }// keyListener
                    );
            statusCombo.add("FAILED");//$NON-NLS-1$
            statusCombo.add("COMPLETED");//$NON-NLS-1$
            statusCombo.select(0);

            // to
            Button bSearch = toolkit.createButton(firstLineComposite, BLANK, SWT.CENTER);
            bSearch.setImage(ImageCache.getCreatedImage(EImage.BROWSE.getPath()));
            bSearch.setToolTipText(Messages.RoutingEngineV2BrowserMainPage_Search);
            bSearch.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
            bSearch.addListener(SWT.Selection, new Listener() {

                public void handleEvent(Event event) {
                    pageToolBar.reset();
                    doSearch();
                };
            });

            // Second Line of routing Orders
            Composite searchLineComposite = toolkit.createComposite(composite);
            searchLineComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
            searchLineComposite.setLayout(new GridLayout(4, false));

            // document type
            Label documentTypeLabel = toolkit.createLabel(searchLineComposite,
                    Messages.RoutingEngineV2BrowserMainPage_DocumentType, SWT.NULL);
            documentTypeLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
            // DocumentType
            documentTypeText = toolkit.createText(searchLineComposite, BLANK, SWT.BORDER);
            documentTypeText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
            ((GridData) documentTypeText.getLayoutData()).widthHint = 120;
            documentTypeText.addKeyListener(new KeyAdapter() {

                @Override
                public void keyReleased(KeyEvent e) {
                    if ((e.stateMask == 0) && (e.character == SWT.CR)) {
                        doSearch();
                    }
                }// keyReleased
            }// keyListener
                    );
            // ID
            Label idLabel = toolkit.createLabel(searchLineComposite, Messages.RoutingEngineV2BrowserMainPage_ItemIDs, SWT.NULL);
            idLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
            // ID
            idText = toolkit.createText(searchLineComposite, BLANK, SWT.BORDER);
            idText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
            idText.addKeyListener(new KeyAdapter() {

                @Override
                public void keyReleased(KeyEvent e) {
                    if ((e.stateMask == 0) && (e.character == SWT.CR)) {
                        doSearch();
                    }
                }// keyReleased
            }// keyListener
            );

            // service
            Label serviceLabel = toolkit.createLabel(searchLineComposite, Messages.RoutingEngineV2BrowserMainPage_Service,
                    SWT.NULL);
            serviceLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
            // service
            serviceCombo = new Combo(searchLineComposite, SWT.READ_ONLY | SWT.DROP_DOWN | SWT.MULTI);
            serviceCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
            serviceCombo.addKeyListener(new KeyAdapter() {

                @Override
                public void keyReleased(KeyEvent e) {
                    if ((e.stateMask == 0) && (e.character == SWT.CR)) {
                        doSearch();
                    }
                }// keyReleased
            }// keyListener
                    );
            serviceCombo.add(BLANK);
            // WSServicesListItem[] servicesList = Util.getPort(getXObject()).getServicesList(new
            // WSGetServicesList("en")).getItem();
            List<WSServicesListItem> servicesList = getMDMService().getServicesList(new WSGetServicesList("en")).getItem(); //$NON-NLS-1$

            if ((servicesList != null) && (servicesList.size() > 0)) {
                String[] services = new String[servicesList.size()];
                for (int i = 0; i < servicesList.size(); i++) {
                    services[i] = servicesList.get(i).getJndiName().replaceFirst("amalto/local/service/", BLANK);//$NON-NLS-1$
                }
                Arrays.sort(services);
                for (String service : services) {
                    serviceCombo.add(service);
                }
            }
            serviceCombo.select(0);

            // any Field
            Label anyFieldLabel = toolkit.createLabel(searchLineComposite, Messages.RoutingEngineV2BrowserMainPage_AnyField,
                    SWT.NULL);
            anyFieldLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
            // anyField
            anyFieldText = toolkit.createText(searchLineComposite, BLANK, SWT.BORDER);
            anyFieldText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
            anyFieldText.addKeyListener(new KeyAdapter() {

                @Override
                public void keyReleased(KeyEvent e) {
                    if ((e.stateMask == 0) && (e.character == SWT.CR)) {
                        doSearch();
                    }
                }// keyReleased
            }// keyListener
                    );

            // pageToolBar
            pageToolBar = new PageingToolBar(composite);
            pageToolBar.getComposite().setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 9, 1));
            pageToolBar.getComposite().setVisible(false);
            pageToolBar.setPageingListener(this);

            final Table table = createTable(composite);

            resultsViewer = new TableViewer(table);

            resultsViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
            // ((GridData) resultsViewer.getControl().getLayoutData()).heightHint = 500;
            resultsViewer.setContentProvider(new ArrayContentProvider());
            resultsViewer.setLabelProvider(new ClusterTableLabelProvider());
            resultsViewer.addDoubleClickListener(new IDoubleClickListener() {

                public void doubleClick(DoubleClickEvent event) {
                    resultsViewer.setSelection(event.getSelection());
                    try {
                        new EditItemAction(RoutingEngineV2BrowserMainPage.this.getSite().getShell(), resultsViewer).run();
                    } catch (Exception e) {
                        MessageDialog.openError(
                                RoutingEngineV2BrowserMainPage.this.getSite().getShell(),
                                Messages._Error,
                                Messages.bind(Messages.RoutingEngineV2BrowserMainPage_ErrorMsg, e.getClass().getName(),
                                        e.getLocalizedMessage()));
                    }
                }
            });

            hookContextMenu();

            managedForm.reflow(true); // nothing will show on the form if not called

            // adapt body add mouse/focus listener for child
            WidgetFactory factory = WidgetFactory.getWidgetFactory();
            factory.adapt(managedForm.getForm().getBody());
            updateButtons();
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
        gridData.horizontalSpan = 3;
        table.setLayoutData(gridData);

        table.setLinesVisible(true);
        table.setHeaderVisible(true);

        // 1st column
        final TableColumn column0 = new TableColumn(table, SWT.LEFT, 0);
        column0.setText(Messages.RoutingEngineV2BrowserMainPage_Document);
        column0.setWidth(150);
        column0.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
                ascending[0] = !ascending[0];
                RoutingEngineV2BrowserMainPage.this.resultsViewer.setSorter(new TableSorter(0, ascending[0]));
            }

            public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
                ascending[0] = !ascending[0];
                RoutingEngineV2BrowserMainPage.this.resultsViewer.setSorter(new TableSorter(0, ascending[0]));
                table.setSortColumn(column0);
                if (ascending[0]) {
                    table.setSortDirection(SWT.UP);
                } else {
                    table.setSortDirection(SWT.DOWN);
                }

            }
        });

        // 2nd column
        final TableColumn column1 = new TableColumn(table, SWT.LEFT, 1);
        column1.setText(Messages.RoutingEngineV2BrowserMainPage_Date);
        column1.setWidth(150);
        // Add listener to column so tasks are sorted by description when clicked
        column1.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
                ascending[1] = !ascending[1];
                RoutingEngineV2BrowserMainPage.this.resultsViewer.setSorter(new TableSorter(1, ascending[1]));
            }

            public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
                ascending[1] = !ascending[1];
                RoutingEngineV2BrowserMainPage.this.resultsViewer.setSorter(new TableSorter(1, ascending[1]));
                table.setSortColumn(column1);
                if (ascending[1]) {
                    table.setSortDirection(SWT.UP);
                } else {
                    table.setSortDirection(SWT.DOWN);
                }

            }
        });

        // 3rd column
        final TableColumn column2 = new TableColumn(table, SWT.LEFT, 2);
        column2.setText(Messages.RoutingEngineV2BrowserMainPage_Service);
        column2.setWidth(100);
        // Add listener to column so tasks are sorted by description when clicked
        column2.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
                ascending[2] = !ascending[2];
                RoutingEngineV2BrowserMainPage.this.resultsViewer.setSorter(new TableSorter(2, ascending[2]));
            }

            public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
                ascending[2] = !ascending[2];
                RoutingEngineV2BrowserMainPage.this.resultsViewer.setSorter(new TableSorter(2, ascending[2]));
                table.setSortColumn(column2);
                if (ascending[2]) {
                    table.setSortDirection(SWT.UP);
                } else {
                    table.setSortDirection(SWT.DOWN);
                }

            }
        });

        // 4th column
        final TableColumn column3 = new TableColumn(table, SWT.LEFT, 3);
        column3.setText(Messages.RoutingEngineV2BrowserMainPage_Message);
        column3.setWidth(300);
        // Add listener to column so tasks are sorted by description when clicked
        column3.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
                ascending[3] = !ascending[3];
                RoutingEngineV2BrowserMainPage.this.resultsViewer.setSorter(new TableSorter(3, ascending[3]));
            }

            public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
                ascending[3] = !ascending[3];
                RoutingEngineV2BrowserMainPage.this.resultsViewer.setSorter(new TableSorter(3, ascending[3]));

                table.setSortColumn(column3);
                if (ascending[3]) {
                    table.setSortDirection(SWT.UP);
                } else {
                    table.setSortDirection(SWT.DOWN);
                }
            }
        });

        return table;
    }

    @Override
    protected void createCharacteristicsContent(FormToolkit toolkit, Composite charComposite) {
        // Everything is implemented in createFormContent
    }// createCharacteristicsContent

    @Override
    protected void refreshData() {
        try {
            WSRoutingEngineV2Status status = getServerRoutingStatus();
            statusLabel.setText(status.value());

            idText.setFocus();

        } catch (Exception e) {
            updateButtons();
            log.error(e.getMessage(), e);
            if (!Util.handleConnectionException(this.getSite().getShell(), e,
                    Messages.RoutingEngineV2BrowserMainPage_ErrorRefreshingPage)) {
                MessageDialog.openError(this.getSite().getShell(), Messages.RoutingEngineV2BrowserMainPage_ErrorRefreshingPage,
                        Messages.bind(Messages.RoutingEngineV2BrowserMainPage_ErrorRefreshingPageXX, e.getLocalizedMessage()));
            }
        }
    }

    protected WSRoutingEngineV2Status getServerRoutingStatus() throws XtentisException {
        TMDMService service = getMDMService();
        WSRoutingEngineV2Status status = service.routingEngineV2Action(new WSRoutingEngineV2Action(
                WSRoutingEngineV2ActionCode.STATUS));
        return status;
    }

    protected void updateButtons() {
        try {
            WSRoutingEngineV2Status status = getServerRoutingStatus();

            startButton.setEnabled(status != WSRoutingEngineV2Status.RUNNING);
            suspendButton.setEnabled(status != WSRoutingEngineV2Status.SUSPENDED);
            stopButton.setEnabled(status != WSRoutingEngineV2Status.STOPPED);
            statusLabel.setText(status.value());
        } catch (XtentisException e) {
            startButton.setEnabled(true);
            suspendButton.setEnabled(false);
            stopButton.setEnabled(false);
            statusLabel.setText("FAILED"); //$NON-NLS-1$
            log.debug(e.getMessage(), e);
        }
    }

    @Override
    protected void commit() {
        try {
            // Nothing to do
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            MessageDialog.openError(this.getSite().getShell(), Messages.RoutingEngineV2BrowserMainPage_ErrorCommitingPage,
                    Messages.RoutingEngineV2BrowserMainPage_ErrorCommitingPageXX + e.getLocalizedMessage());
        }
    }

    @Override
    protected void createActions() {

    }

    private void hookContextMenu() {
        MenuManager menuMgr = new MenuManager();
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {

            public void menuAboutToShow(IMenuManager manager) {
                manager.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
                manager.appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS, new EditItemAction(
                        RoutingEngineV2BrowserMainPage.this.getSite().getShell(),
                        RoutingEngineV2BrowserMainPage.this.resultsViewer));

                manager.appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS, new DeleteItemsAction(
                        RoutingEngineV2BrowserMainPage.this.getSite().getShell(), RoutingEngineV2BrowserMainPage.this,
                        RoutingEngineV2BrowserMainPage.this.resultsViewer));
                manager.appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS, new ExecuteRoutingOrdersAction(
                        RoutingEngineV2BrowserMainPage.this.getSite().getShell(), RoutingEngineV2BrowserMainPage.this,
                        RoutingEngineV2BrowserMainPage.this.resultsViewer, true));
                manager.appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS, new ExecuteRoutingOrdersAction(
                        RoutingEngineV2BrowserMainPage.this.getSite().getShell(), RoutingEngineV2BrowserMainPage.this,
                        RoutingEngineV2BrowserMainPage.this.resultsViewer, false // asynchronously
                        ));
            }
        });
        Menu menu = menuMgr.createContextMenu(resultsViewer.getControl());
        resultsViewer.getControl().setMenu(menu);
        getSite().registerContextMenu(menuMgr, resultsViewer);
    }

    protected void fillContextMenu(IMenuManager manager) {
        // IStructuredSelection selection=((IStructuredSelection)viewer.getSelection());

        return;
    }

    public void doSearch() {
        resultsViewer.setInput(getResults());
        resultsViewer.getTable().setFocus();
        pageToolBar.getComposite().setVisible(true);
        pageToolBar.getComposite().layout(true);
        pageToolBar.getComposite().getParent().layout(true);

        doSearchSort();//
    }

    /**
     * this method will be call when search action is activated or page is changed every time
     */
    private void doSearchSort() {
        Table table = resultsViewer.getTable();
        TableColumn sortColumn = table.getSortColumn();
        if (sortColumn != null) {
            List<TableColumn> columns = Arrays.asList(table.getColumns());
            int index = columns.indexOf(sortColumn);
            sort(index, sortColumn);
        }
    }

    private void sort(int index, TableColumn column) {
        resultsViewer.setSorter(new TableSorter(index, ascending[index]));
        Table table = resultsViewer.getTable();
        if (ascending[index]) {
            table.setSortColumn(column);
            table.setSortDirection(SWT.DOWN);
        } else {
            table.setSortColumn(column);
            table.setSortDirection(SWT.UP);
        }
    }

    protected WSRoutingOrderV2[] getResults() {

        Cursor waitCursor = null;

        try {

            Display display = getEditor().getSite().getPage().getWorkbenchWindow().getWorkbench().getDisplay();
            waitCursor = new Cursor(display, SWT.CURSOR_WAIT);
            this.getSite().getShell().setCursor(waitCursor);
            TMDMService service = getMDMService();
            long from = -1;
            long to = -1;
            Pattern pattern = Pattern.compile("^\\d{4}\\d{2}\\d{2} \\d{2}:\\d{2}:\\d{2}$");//$NON-NLS-1$

            if (!BLANK.equals(fromText.getText())) {
                String dateTimeText = fromText.getText().trim();
                Matcher matcher = pattern.matcher(dateTimeText);
                if (!matcher.matches()) {
                    MessageDialog.openWarning(this.getSite().getShell(), Messages.Warning,
                            Messages.RoutingEngineV2BrowserMainPage_FormatIllegal);
                    return new WSRoutingOrderV2[0];
                }
                try {
                    Date d = sdf.parse(fromText.getText());
                    from = d.getTime();
                } catch (ParseException pe) {
                }
            }

            if (!BLANK.equals(toText.getText())) {
                String dateTimeText = toText.getText().trim();
                Matcher matcher = pattern.matcher(dateTimeText);
                if (!matcher.matches()) {
                    MessageDialog.openWarning(this.getSite().getShell(), Messages.Warning,
                            Messages.RoutingEngineV2BrowserMainPage_FormatIllegal);
                    return new WSRoutingOrderV2[0];
                }
                try {
                    Date d = sdf.parse(toText.getText());
                    to = d.getTime();
                } catch (ParseException pe) {
                }
            }

            long timeCreatedMin = -1;
            long timeCreatedMax = -1;
            long timeScheduledMin = -1;
            long timeScheduledMax = -1;
            long timeLastRunStartedMin = -1;
            long timeLastRunStartedMax = -1;
            long timeLastRunCompletedMin = -1;
            long timeLastRunCompletedMax = -1;
            WSRoutingOrderV2Status status = null;

            String statusText = statusCombo.getItem(statusCombo.getSelectionIndex());
            if ("COMPLETED".equals(statusText)) {//$NON-NLS-1$
                timeLastRunCompletedMin = from;
                timeLastRunCompletedMax = to;
                status = WSRoutingOrderV2Status.COMPLETED;
            } else if ("FAILED".equals(statusText)) {//$NON-NLS-1$
                timeLastRunCompletedMin = from;
                timeLastRunCompletedMax = to;
                status = WSRoutingOrderV2Status.FAILED;
            } else {
                throw new XtentisException(Messages.RoutingEngineV2BrowserMainPage_ExceptionInfo + statusText
                        + Messages.RoutingEngineV2BrowserMainPage_ExceptionInfoA);
            }

            String serviceJNDI = serviceCombo.getItem(serviceCombo.getSelectionIndex());
            if (BLANK.equals(serviceJNDI)) {
                serviceJNDI = null;
            }

            int start = pageToolBar.getStart();
            int limit = pageToolBar.getLimit();
            List<WSRoutingOrderV2> wsRoutingOrder = service.getRoutingOrderV2ByCriteriaWithPaging(
                    new WSGetRoutingOrderV2ByCriteriaWithPaging(new WSRoutingOrderV2SearchCriteriaWithPaging("*" //$NON-NLS-1$
                            .equals(anyFieldText.getText())
                            || BLANK.equals(anyFieldText.getText()) ? null : anyFieldText.getText(),
                            "*".equals(documentTypeText.getText()) || BLANK.equals(documentTypeText.getText()) ? null //$NON-NLS-1$
                                    : documentTypeText.getText(),
                            "*".equals(idText.getText()) || BLANK.equals(idText.getText()) ? null //$NON-NLS-1$
                                    : idText.getText(), limit, null, null, serviceJNDI, null, start, status, timeCreatedMax,
                            timeCreatedMin, timeLastRunCompletedMax, timeLastRunCompletedMin, timeLastRunStartedMax,
                            timeLastRunStartedMin, timeScheduledMax, timeScheduledMin, true))).getWsRoutingOrder();

            if (wsRoutingOrder.size() == 1) {
                MessageDialog.openInformation(this.getSite().getShell(), Messages.RoutingEngineV2BrowserMainPage_Info,
                        Messages.RoutingEngineV2BrowserMainPage_SorryNoResult);
                return new WSRoutingOrderV2[0];
            }

            int totalSize = Integer.parseInt(wsRoutingOrder.get(0).getName());

            pageToolBar.setTotalsize(totalSize);
            pageToolBar.refreshUI();

            WSRoutingOrderV2[] resultOrderV2s = new WSRoutingOrderV2[wsRoutingOrder.size() - 1];
            System.arraycopy(wsRoutingOrder.toArray(new WSRoutingOrderV2[0]), 1, resultOrderV2s, 0, resultOrderV2s.length);

            return resultOrderV2s;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if ((e.getLocalizedMessage() != null) && e.getLocalizedMessage().contains("10000")) {
                MessageDialog.openError(this.getSite().getShell(), Messages.RoutingEngineV2BrowserMainPage_TooManyResults,
                        Messages.RoutingEngineV2BrowserMainPage_ErrorMsg1);
            } else if (!Util.handleConnectionException(this.getSite().getShell(), e, null)) {
                MessageDialog.openError(this.getSite().getShell(), Messages.ErrorTitle1, e.getLocalizedMessage());
            }
            return null;
        } finally {
            try {
                this.getSite().getShell().setCursor(null);
                if (waitCursor != null) {
                    waitCursor.dispose();
                }
            } catch (Exception e) {
                // do nothing
            }
        }

    }

    /*********************************
     * IXObjectModelListener interface
     */
    public void handleEvent(int type, TreeObject parent, TreeObject child) {
        refreshData();
    }

    /***************************************************************
     * Table Label Provider
     *
     * @author bgrieder
     *
     ***************************************************************/
    class ClusterTableLabelProvider implements ITableLabelProvider {

        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

        public String getColumnText(Object element, int columnIndex) {
            WSRoutingOrderV2 ro = (WSRoutingOrderV2) element;
            switch (columnIndex) {
            case 0:
                return ro.getWsItemPK().getConceptName() + "[" + Util.joinStrings(ro.getWsItemPK().getIds(), ".") + "]";//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
            case 1:
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(ro.getTimeLastRunCompleted());
                return sdf.format(cal.getTime());
            case 2:
                return ro.getServiceJNDI().replaceFirst("amalto/local/service/", BLANK);//$NON-NLS-1$
            case 3:
                return ro.getMessage();
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
     * @author bgrieder
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
            WSRoutingOrderV2 ro1 = (WSRoutingOrderV2) e1;
            WSRoutingOrderV2 ro2 = (WSRoutingOrderV2) e2;

            int res = 0;

            switch (column) {
            case 0: // id
                String d1 = ro1.getWsItemPK().getConceptName() + "[" + Util.joinStrings(ro1.getWsItemPK().getIds(), ".") + "]";//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
                String d2 = ro2.getWsItemPK().getConceptName() + "[" + Util.joinStrings(ro2.getWsItemPK().getIds(), ".") + "]";//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
                res = d1.compareToIgnoreCase(d2);
                break;
            case 1: // date
                if (WSRoutingOrderV2Status.ACTIVE.equals(ro1.getStatus())) {
                    res = (int) (ro1.getTimeCreated() - ro2.getTimeCreated());
                } else {
                    res = (int) (ro1.getTimeLastRunCompleted() - ro2.getTimeLastRunCompleted());
                }
                break;
            case 2: // service
                res = ro1.getServiceJNDI().compareToIgnoreCase(ro2.getServiceJNDI());
                break;
            case 3: // message
                res = ro1.getMessage().compareToIgnoreCase(ro2.getMessage());
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

    private void startSubscriptionEngine() {
        try {
            WSRoutingEngineV2Status status = getServerRoutingStatus();
            TMDMService service = getMDMService();
            if (status == WSRoutingEngineV2Status.STOPPED) {
                service.routingEngineV2Action(new WSRoutingEngineV2Action(WSRoutingEngineV2ActionCode.START));
            } else if (status == WSRoutingEngineV2Status.SUSPENDED) {
                service.routingEngineV2Action(new WSRoutingEngineV2Action(WSRoutingEngineV2ActionCode.RESUME));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (!Util.handleConnectionException(this.getSite().getShell(), e, null)) {
                MessageDialog.openError(this.getSite().getShell(), Messages._Error,
                        Messages.bind(Messages.RoutingEngineV2BrowserMainPage_ErrorMsg7, e.getLocalizedMessage()));
            }
        }
    }

    private void stopSubscriptionEngine() {
        try {
            TMDMService service = getMDMService();
            service.routingEngineV2Action(new WSRoutingEngineV2Action(WSRoutingEngineV2ActionCode.STOP));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (!Util.handleConnectionException(this.getSite().getShell(), e, null)) {
                MessageDialog.openError(this.getSite().getShell(), Messages._Error,
                        Messages.bind(Messages.RoutingEngineV2BrowserMainPage_ErrorMsg8, e.getLocalizedMessage()));
            }
        }
    }

    private void suspendSubscriptionEngine() {
        try {
            TMDMService service = getMDMService();
            service.routingEngineV2Action(new WSRoutingEngineV2Action(WSRoutingEngineV2ActionCode.SUSPEND));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (!Util.handleConnectionException(this.getSite().getShell(), e, null)) {
                MessageDialog.openError(this.getSite().getShell(), Messages._Error,
                        Messages.bind(Messages.RoutingEngineV2BrowserMainPage_ErrorMsg9, e.getLocalizedMessage()));
            }
        }
    }

    @Override
    public Object getAdapter(Class adapter) {
        if (adapter == TMDMService.class) {
            TMDMService mdmService = null;
            try {
                mdmService = getMDMService();
            } catch (XtentisException e) {
                log.error(e.getMessage(), e);
            }

            return mdmService;
        }

        return super.getAdapter(adapter);
    }
}
