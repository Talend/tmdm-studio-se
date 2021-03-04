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
package com.amalto.workbench.editors;

import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.rmi.ServerException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.ws.WebServiceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.amalto.workbench.dialogs.DataModelSelectDialog;
import com.amalto.workbench.exadapter.ExAdapterManager;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.models.TreeParent;
import com.amalto.workbench.providers.XtentisServerObjectsRetriever;
import com.amalto.workbench.utils.LineItem;
import com.amalto.workbench.utils.MDMServerDef;
import com.amalto.workbench.utils.UserInfo;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.utils.XtentisException;
import com.amalto.workbench.webservices.TMDMService;
import com.amalto.workbench.webservices.WSDataCluster;
import com.amalto.workbench.webservices.WSDataClusterPK;
import com.amalto.workbench.webservices.WSDataModel;
import com.amalto.workbench.webservices.WSDataModelPK;
import com.amalto.workbench.webservices.WSGetConceptsInDataCluster;
import com.amalto.workbench.webservices.WSGetDataCluster;
import com.amalto.workbench.webservices.WSGetDataModel;
import com.amalto.workbench.webservices.WSGetItemPKsByCriteria;
import com.amalto.workbench.webservices.WSGetItemPKsByFullCriteria;
import com.amalto.workbench.webservices.WSItemPKsByCriteriaResponseResults;
import com.amalto.workbench.webservices.WSStringArray;
import com.amalto.workbench.widgets.CalendarSelectWidget;
import com.amalto.workbench.widgets.IPagingListener;
import com.amalto.workbench.widgets.PageingToolBar;
import com.amalto.workbench.widgets.WidgetFactory;

/**
 * created by liusongbo on 2013-1-24
 */
public class DataClusterComposite extends Composite implements IPagingListener {

    private static final Log log = LogFactory.getLog(DataClusterComposite.class);

    protected static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");//$NON-NLS-1$

    private Text fromText;

    private Text toText;

    private Combo conceptCombo;

    private Text keyText;

    private Text searchText;

    private Button checkFTSearchButton;

    private PageingToolBar pageToolBar;

    private TableViewer resultsViewer;

    private boolean isMaster; // show master data or staging data

    // //
    protected boolean[] ascending = { true, false, false };

    private TreeObject model;

    private AFormPage page;

    private IWorkbenchPartSite site;

    private Map<MDMServerDef, TreeParent> serverMap = new HashMap<MDMServerDef, TreeParent>();

    private IDataClusterCompositeExAdapter exAdapter;

    public DataClusterComposite(Composite parent, int style, TreeObject model, boolean isMaster, IWorkbenchPartSite site) {
        super(parent, style);
        this.model = model;
        this.site = site;
        this.isMaster = isMaster;
        initAdapter();

        setLayout(new GridLayout());
        create(this);
    }

    public DataClusterComposite(Composite parent, int style, TreeObject model, boolean isMaster, AFormPage page,
            IWorkbenchPartSite site) {
        super(parent, style);
        this.page = page;
        this.site = site;
        this.model = model;
        this.isMaster = isMaster;
        initAdapter();

        setLayout(new GridLayout());
        create(this);
    }

    private void initAdapter() {
        exAdapter = ExAdapterManager.getAdapter(this, IDataClusterCompositeExAdapter.class);
    }

    private void create(Composite composite) {
        createFirstPart(composite);

        createSecondPart(composite);

        createPageToolbar(composite);

        createSearchResultPart(composite);
        // adapt body add mouse/focus listener for child
        FormToolkit toolkit = WidgetFactory.getWidgetFactory();// managedForm.getToolkit();
        toolkit.adapt(composite);
    }

    protected void createFirstPart(Composite composite) {
        FormToolkit toolkit = WidgetFactory.getWidgetFactory();
        // We do not implement IFormPart: we do not care about lifecycle management
        Composite compFirstLine = toolkit.createComposite(composite, SWT.NONE);
        compFirstLine.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
        compFirstLine.setLayout(new GridLayout(10, false));

        // from
        Label fromLabel = toolkit.createLabel(compFirstLine, Messages.DataClusterBrowserMainPage_1, SWT.NULL);
        fromLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

        CalendarSelectWidget fromCalendar = new CalendarSelectWidget(toolkit, compFirstLine, true);
        fromText = fromCalendar.getText();
        fromText.addKeyListener(keylistener);
        // to
        Label toLabel = toolkit.createLabel(compFirstLine, Messages.DataClusterBrowserMainPage_2, SWT.NULL);
        toLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

        CalendarSelectWidget toCalendar = new CalendarSelectWidget(toolkit, compFirstLine, false);
        toText = toCalendar.getText();
        toText.addKeyListener(keylistener);

        Label conceptLabel = toolkit.createLabel(compFirstLine, Messages.DataClusterBrowserMainPage_3, SWT.NULL);
        conceptLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        conceptCombo = new Combo(compFirstLine, SWT.READ_ONLY | SWT.DROP_DOWN | SWT.MULTI);
        conceptCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        // ((GridData) conceptCombo.getLayoutData()).widthHint = 180;
        conceptCombo.addKeyListener(keylistener);
        // refresh
        // search
        Button refreshBun = toolkit.createButton(compFirstLine, "", SWT.CENTER); //$NON-NLS-1$
        refreshBun.setImage(ImageCache.getCreatedImage(EImage.REFRESH.getPath()));
        refreshBun.setToolTipText(Messages.XObjectBrowser_Refresh);
        refreshBun.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        refreshBun.addListener(SWT.Selection, new Listener() {

            public void handleEvent(Event event) {
                refreshData();
            };
        });

        // search
        Button bSearch = toolkit.createButton(compFirstLine, "", SWT.CENTER); //$NON-NLS-1$
        bSearch.setImage(ImageCache.getCreatedImage(EImage.BROWSE.getPath()));
        bSearch.setToolTipText(Messages.DataClusterBrowserMainPage_4);
        bSearch.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        bSearch.addListener(SWT.Selection, new Listener() {

            public void handleEvent(Event event) {
                pageToolBar.reset();
                doSearch();
            };
        });
    }

    protected Composite createSecondPart(Composite composite) {
        FormToolkit toolkit = WidgetFactory.getWidgetFactory();

        Composite compSecondLine = toolkit.createComposite(composite, SWT.NONE);
        compSecondLine.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
        compSecondLine.setLayout(new GridLayout(9, false));

        createSearchPart(compSecondLine);

        if (exAdapter != null) {
            exAdapter.createSecondPart(compSecondLine);
        }

        return compSecondLine;
    }

    protected void createSearchPart(Composite compSecondLine) {
        FormToolkit toolkit = WidgetFactory.getWidgetFactory();
        Label keyLabel = toolkit.createLabel(compSecondLine, Messages.DataClusterBrowserMainPage_5, SWT.NULL);
        keyLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        keyText = toolkit.createText(compSecondLine, "", SWT.BORDER);//$NON-NLS-1$
        keyText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
        keyText.addKeyListener(keylistener);

        /***
         * Search Text
         */
        Label descriptionLabel = toolkit.createLabel(compSecondLine, Messages.DataClusterBrowserMainPage_6, SWT.NULL);
        descriptionLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        searchText = toolkit.createText(compSecondLine, "", SWT.BORDER);//$NON-NLS-1$
        searchText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
        // searchText.addModifyListener(this);
        searchText.addKeyListener(keylistener);
        searchText.setFocus();

        checkFTSearchButton = toolkit.createButton(compSecondLine, Messages.DataClusterBrowserMainPage_7, SWT.CHECK);
        checkFTSearchButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        checkFTSearchButton.setEnabled(isMaster);
    }

    protected void createPageToolbar(Composite composite) {
        pageToolBar = new PageingToolBar(composite);
        pageToolBar.getComposite().setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 9, 1));
        pageToolBar.getComposite().setVisible(false);
        pageToolBar.setPageingListener(this);
    }

    protected void createSearchResultPart(Composite composite) {
        int style = SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION;
        resultsViewer = new TableViewer(composite, style);
        initTable(resultsViewer.getTable());

        resultsViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        // ((GridData) resultsViewer.getControl().getLayoutData()).heightHint = DEFAULT_VIEWER_HEIGHT;
        resultsViewer.setContentProvider(new ArrayContentProvider());
        resultsViewer.setLabelProvider(new ClusterTableLabelProvider());
    }

    private KeyListener keylistener = new KeyAdapter() {

        @Override
        public void keyReleased(KeyEvent e) {
            if ((e.stateMask == 0) && (e.character == SWT.CR)) {
                resultsViewer.setInput(getResults(true));
                resultsViewer.getTable().setFocus();
                pageToolBar.getComposite().setVisible(true);
                pageToolBar.getComposite().layout(true);
                pageToolBar.getComposite().getParent().layout(true);
                readjustViewerHeight();
            }
        }// keyReleased
    };

    private void readjustViewerHeight() {
        resultsViewer.refresh();
    }

    public void doSearch() {
        if (exAdapter != null) {
            exAdapter.doSearch(resultsViewer);
        } else {
            if (resultsViewer.getTable().getColumnCount() == 4) {
                resultsViewer.getTable().getColumn(3).dispose();
            }
        }

        resultsViewer.setInput(getResults(true));
        resultsViewer.getTable().setFocus();
        pageToolBar.getComposite().setVisible(true);
        pageToolBar.getComposite().layout(true);
        pageToolBar.getComposite().getParent().layout(true);

        doSearchSort();//
        readjustViewerHeight();
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

    protected LineItem[] getResults(boolean showResultInfo) {

        Cursor waitCursor = null;

        try {

            waitCursor = new Cursor(Display.getCurrent(), SWT.CURSOR_WAIT);
            getSite().getShell().setCursor(waitCursor);

            TMDMService service = Util.getMDMService(getXObject());

            long from = -1;
            long to = -1;

            Pattern pattern = Pattern.compile("^\\d{4}\\d{2}\\d{2} \\d{2}:\\d{2}:\\d{2}$");//$NON-NLS-1$

            if (!"".equals(fromText.getText())) {//$NON-NLS-1$

                String dateTimeText = fromText.getText().trim();
                Matcher matcher = pattern.matcher(dateTimeText);
                if (!matcher.matches()) {
                    MessageDialog
                            .openWarning(this.getSite().getShell(), Messages.Warning, Messages.DataClusterBrowserMainPage_21);
                    return new LineItem[0];
                }

                try {
                    Date d = sdf.parse(fromText.getText());
                    from = d.getTime();
                } catch (ParseException pe) {
                }
            }

            if (!"".equals(toText.getText())) { //$NON-NLS-1$
                String dateTimeText = toText.getText().trim();
                Matcher matcher = pattern.matcher(dateTimeText);
                if (!matcher.matches()) {
                    MessageDialog
                            .openWarning(this.getSite().getShell(), Messages.Warning, Messages.DataClusterBrowserMainPage_23);
                    return new LineItem[0];
                }

                try {
                    Date d = sdf.parse(toText.getText());
                    to = d.getTime();
                } catch (ParseException pe) {
                }
            }

            String concept = conceptCombo.getText();
            if ("*".equals(concept) | "".equals(concept)) { //$NON-NLS-1$ //$NON-NLS-2$
                concept = null;
            }
            if (concept != null) {
                concept = concept.replaceAll("\\[.*\\]", "").trim();//$NON-NLS-1$//$NON-NLS-2$
            }
            String keys = keyText.getText();
            if ("*".equals(keys) | "".equals(keys)) { //$NON-NLS-1$ //$NON-NLS-2$
                keys = null;
            }

            boolean useFTSearch = isMaster ? checkFTSearchButton.getSelection() : false;
            String search = searchText.getText();
            if ("*".equals(search) | "".equals(search)) { //$NON-NLS-1$ //$NON-NLS-2$
                search = null;
            }

            int start = pageToolBar.getStart();
            int limit = pageToolBar.getLimit();
            // see 0015909
            String clusterName = URLEncoder.encode(getXObject().toString(), "utf-8");//$NON-NLS-1$
            WSDataClusterPK clusterPk = new WSDataClusterPK(clusterName + getPkAddition());

            // @temp yguo, get item with taskid or get taskid by specify wsitempk.
            List<WSItemPKsByCriteriaResponseResults> results = service.getItemPKsByFullCriteria(
                    new WSGetItemPKsByFullCriteria(useFTSearch, new WSGetItemPKsByCriteria(concept, search, from, null, keys,
                            limit, start, to, clusterPk))).getResults();

            if (showResultInfo && (results.size() == 1)) {
                MessageDialog.openInformation(this.getSite().getShell(), Messages.DataClusterBrowserMainPage_24,
                        Messages.DataClusterBrowserMainPage_25);
                return new LineItem[0];
            }
            if (results.size() == 1) {
                return new LineItem[0];
            }
            int totalSize = 0;
            List<LineItem> ress = new ArrayList<LineItem>();
            for (int i = 0; i < results.size(); i++) {
                WSItemPKsByCriteriaResponseResults result = results.get(i);
                if (i == 0) {
                    totalSize = Integer.parseInt(Util.parse(result.getWsItemPK().getConceptName()).getDocumentElement()
                            .getTextContent());

                    continue;
                }

                ress.add(new LineItem(result.getDate(), result.getWsItemPK().getConceptName(), result.getWsItemPK().getIds()
                        .toArray(new String[0]), result.getTaskId()));
            }
            pageToolBar.setTotalsize(totalSize);
            pageToolBar.refreshUI();
            return ress.toArray(new LineItem[ress.size()]);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if ((e.getLocalizedMessage() != null) && e.getLocalizedMessage().contains("10000")) { //$NON-NLS-1$
                MessageDialog.openError(this.getSite().getShell(), Messages.DataClusterBrowserMainPage_26,
                        Messages.DataClusterBrowserMainPage_27);
            } else if (!Util.handleConnectionException(this.getSite().getShell(), e, Messages.DataClusterBrowserMainPage_28)) {
                MessageDialog.openError(this.getSite().getShell(), Messages.DataClusterBrowserMainPage_28,
                        e.getLocalizedMessage());
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

    private long parseTime(String timeStr) {
        try {
            Date d = sdf.parse(timeStr);
            long time = d.getTime();
            return time;
        } catch (ParseException pe) {
            log.error(pe.getMessage(), pe);
        }

        return -1L;
    }

    private TreeObject getXObject() {
        return model;
    }

    /**
     * Create the Table
     */
    private void initTable(final Table table) {

        // final Table table = new Table(parent, style);
        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        // gridData.horizontalSpan = 3;
        gridData.heightHint = 200;
        table.setLayoutData(gridData);

        table.setLinesVisible(true);
        table.setHeaderVisible(true);

        // table.setSortDirection(0);
        // 1st column
        final TableColumn column = new TableColumn(table, SWT.LEFT, 0);
        // table.setSortColumn(column);
        // table.setSortDirection(SWT.UP);
        column.setText(Messages.DataClusterBrowserMainPage_13);
        column.setWidth(150);
        // column.setImage(getDefaultImage());//============
        column.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
                ascending[0] = !ascending[0];
                resultsViewer.setSorter(new TableSorter(0, ascending[0]));
            }

            public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
                ascending[0] = !ascending[0];
                resultsViewer.setSorter(new TableSorter(0, ascending[0]));
                if (ascending[0]) {
                    table.setSortColumn(column);
                    table.setSortDirection(SWT.DOWN);
                } else {
                    table.setSortColumn(column);
                    table.setSortDirection(SWT.UP);
                }
            }
        });

        // 2nd column
        final TableColumn column1 = new TableColumn(table, SWT.LEFT, 1);
        // table.setSortColumn(column1);
        column1.setText(Messages.DataClusterBrowserMainPage_14);
        column1.setWidth(150);
        // Add listener to column so tasks are sorted by description when clicked
        column1.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
                ascending[1] = !ascending[1];
                resultsViewer.setSorter(new TableSorter(1, ascending[1]));
            }

            public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
                ascending[1] = !ascending[1];
                resultsViewer.setSorter(new TableSorter(1, ascending[1]));
                if (ascending[1]) {
                    table.setSortColumn(column1);
                    table.setSortDirection(SWT.DOWN);
                } else {
                    table.setSortColumn(column1);
                    table.setSortDirection(SWT.UP);
                }

            }
        });

        // 3rd column
        final TableColumn column2 = new TableColumn(table, SWT.LEFT, 2);
        // table.setSortColumn(column2);
        column2.setText(Messages.DataClusterBrowserMainPage_15);
        column2.setWidth(150);
        // Add listener to column so tasks are sorted by description when clicked
        column2.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
                ascending[2] = !ascending[2];
                resultsViewer.setSorter(new TableSorter(2, ascending[2]));
            }

            public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
                ascending[2] = !ascending[2];
                resultsViewer.setSorter(new TableSorter(2, ascending[2]));
                if (ascending[2]) {
                    table.setSortColumn(column2);
                    table.setSortDirection(SWT.DOWN);
                } else {
                    table.setSortColumn(column2);
                    table.setSortDirection(SWT.UP);
                }

            }
        });

        // return table;
    }

    protected boolean refreshData() {
        try {
            if (conceptCombo.isDisposed()) {
                return false;
            }
            if (getXObject().getEndpointAddress() == null) {
                return false;
            }
            TMDMService service = Util.getMDMService(getXObject());

            WSDataCluster cluster = null;
            if (getXObject().getWsObject() == null) { // then fetch from server
                cluster = service.getDataCluster(new WSGetDataCluster((WSDataClusterPK) getXObject().getWsKey()));
                getXObject().setWsObject(cluster);
            } else { // it has been opened by an editor - use the object there
                // added for TMDM-3064
                // the following may throw ServerException to identify the data continer not exist on the server
                cluster = service.getDataCluster(new WSGetDataCluster(new WSDataClusterPK(getXObject().getName())));
                // if you could go to next line, that means the data container is on the server specified
                cluster = (WSDataCluster) getXObject().getWsObject();

            }

            // add by myli; fix the bug:0013077: if the data is too much, just get the entities from the model instead
            // of from the container.

            String clusterName = URLEncoder.encode(cluster.getName(), "utf-8");//$NON-NLS-1$

            //            WSString countStr = port.count(new WSCount(new WSDataClusterPK(cluster.getName()), "*", null, 100)); //$NON-NLS-1$
            // long count = Long.parseLong(countStr.getValue());

            WSStringArray conceptsInDataCluster = service.getConceptsInDataCluster(new WSGetConceptsInDataCluster(
                    new WSDataClusterPK(clusterName)));
            if (conceptsInDataCluster != null) {
                List<String> concepts = conceptsInDataCluster.getStrings();
                conceptCombo.removeAll();
                conceptCombo.add("*");//$NON-NLS-1$
                for (String concept : concepts) {
                    conceptCombo.add(concept);
                }
            } else {
                boolean selected = doSelectDataModelForEntityRecords(clusterName);
                if (!selected) {
                    return false;
                }
            }

            conceptCombo.select(0);
            searchText.setFocus();
        } catch (ServerException e) {
            log.error(e.getMessage(), e);
            MessageDialog.openError(getSite().getShell(), Messages._Error, Messages.DataClusterBrowser_dataContainerError);
            return false;
        } catch (WebServiceException e) {
            log.error(e.getMessage(), e);
            if (!Util.handleConnectionException(getSite().getShell(), e, null)) {
                MessageDialog.openError(getSite().getShell(), Messages._Error, Messages.DataClusterBrowser_connectionError);
            }
            return false;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            MessageDialog.openError(this.getSite().getShell(), Messages._Error,
                    Messages.bind(Messages.DataClusterBrowser_error, e.getLocalizedMessage()));
            return false;
        }

        return true;
    }

    /**
     * @param clusterName
     * @throws XtentisException
     * @throws Exception
     */
    private boolean doSelectDataModelForEntityRecords(String clusterName) throws XtentisException, Exception {
        // if (false) {
        // Modified by hbhong,to fix bug 21784|
        TreeParent treeParent = (TreeParent) getAdapter(TreeParent.class);

        DataModelSelectDialog dialog = new DataModelSelectDialog(getSite(), treeParent, clusterName);
        // The ending| bug:21784
        dialog.setBlockOnOpen(true);
        dialog.open();

        if (dialog.getReturnCode() == Window.OK) {
            List<String> allConcept = new ArrayList<String>();

            String[] xpaths = dialog.getXpath();
            for (String xpath : xpaths) {
                WSDataModel dm = Util.getMDMService(this.getXObject()).getDataModel(new WSGetDataModel(new WSDataModelPK(xpath)));
                if (dm == null) {
                    return false;
                }
                List<String> concepts = Util.getConcepts(Util.getXSDSchema(dm.getXsdSchema()));
                allConcept.addAll(concepts);
            }

            String[] concepts = allConcept.toArray(new String[0]);

            TreeObject object = null;
            TreeObject[] children = treeParent.getChildren();
            for (TreeObject element : children) {
                object = element;
                if (object.getType() == TreeObject.DATA_MODEL) {
                    break;
                }
            }
            String revision = "";//$NON-NLS-1$
            if (object != null) {
                // TMDM-2606: Don't expect data model to contain revision name (CE edition doesn't support
                // revisions).
                if (object.getDisplayName().contains(Messages.DataClusterBrowserMainPage_16)
                        && object.getDisplayName().contains(Messages.DataClusterBrowserMainPage_17)) {
                    revision = object.getDisplayName().substring(object.getDisplayName().indexOf("[") + 1,//$NON-NLS-1$
                            object.getDisplayName().indexOf("]"));//$NON-NLS-1$
                }
            }

            for (int i = 0; i < concepts.length; i++) {
                String concept = concepts[i];
                if (revision == null || revision.equals("")) { //$NON-NLS-1$
                    revision = "HEAD";//$NON-NLS-1$
                }
                concepts[i] = concept + " " + "[" + revision + "]";//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
            }
            conceptCombo.removeAll();
            conceptCombo.add("*");//$NON-NLS-1$
            for (String concept : concepts) {
                conceptCombo.add(concept);
            }
        } else {
            conceptCombo.add("*");//$NON-NLS-1$
        }

        return true;
    }

    private IWorkbenchPartSite getSite() {
        return site;
    }

    private Object getAdapter(Class<?> adapter) {
        if (page != null) {
            return page.getAdapter(adapter);
        }

        TreeParent treeParent = Util.getServerTreeParent(getXObject());

        if (treeParent == null || treeParent.getChildren().length == 0) {
            TreeParent serverRoot = getRealTreeParent();
            if (serverRoot != null) {
                treeParent = serverRoot;
            }
        }
        return treeParent;
    }

    private TreeParent getRealTreeParent() {
        TreeParent treeParent = null;
        TreeObject xObject = getXObject();
        if (xObject != null) {
            TreeParent serverRoot = xObject.getServerRoot();
            UserInfo user = serverRoot.getUser();

            String serverName = serverRoot.getName();
            String password = user.getPassword();
            String url = user.getServerUrl();
            String username = user.getUsername();

            final XtentisServerObjectsRetriever retriever = new XtentisServerObjectsRetriever(serverName, url, username, password);

            retriever.setRetriveWSObject(true);

            try {
                retriever.run(new NullProgressMonitor());
                treeParent = retriever.getServerRoot();// get the real server root as the treeParent
            } catch (InvocationTargetException e) {
                log.error(e.getMessage(), e);
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        }

        return treeParent;
    }

    public TableViewer getResultsViewer() {
        return resultsViewer;
    }

    private String getPkAddition() {
        if (isMaster) {
            return ""; //$NON-NLS-1$
        }

        return IDataClusterConstants.PK_ADDITION;
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
            LineItem li = (LineItem) element;
            switch (columnIndex) {
            case 0:
                return sdf.format(new Date(li.getTime()));
            case 1:
                return li.getConcept();
            case 2:
                return Util.joinStrings(li.getIds(), "."); //$NON-NLS-1$
            case 3:
                return li.getTaskId();
            default:
                return "???????"; //$NON-NLS-1$
            }
        }

        public void addListener(ILabelProviderListener listener) {//
        }

        public void dispose() {//
        }

        public boolean isLabelProperty(Object element, String property) {
            return false;
        }

        public void removeListener(ILabelProviderListener listener) {//
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
            LineItem li1 = (LineItem) e1;
            LineItem li2 = (LineItem) e2;

            int res = 0;

            switch (column) {
            case 0:
                res = (int) (li1.getTime() - li2.getTime());
                break;
            case 1:
                res = li1.getConcept().compareToIgnoreCase(li2.getConcept());
                break;
            case 2:
                res = Util
                        .joinStrings(li1.getIds(), ".").compareToIgnoreCase(Util.joinStrings(li2.getIds(), Messages.DataClusterBrowserMainPage_130)); //$NON-NLS-1$
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

    // /////////
    public void changeToServer(MDMServerDef serverDef) {
        conceptCombo.removeAll();

        model.setServerRoot(getServerRoot(serverDef));
        resultsViewer.setInput(new LineItem[0]);

        setEnabled(false);
    }

    public boolean changeToDataContainer(TreeObject dataContainer) {
        dataContainer.setServerRoot(model.getServerRoot());
        this.model = dataContainer;

        resultsViewer.setInput(new LineItem[0]);
        boolean refreshed = refreshData();
        setEnabled(refreshed);

        return refreshed;
    }

    public TreeObject getDataContainer() {
        return model;
    }

    private TreeParent getServerRoot(MDMServerDef serverDef) {
        if (serverDef == null) {
            return null;
        }

        if (serverMap.containsKey(serverDef)) {
            return serverMap.get(serverDef);
        }

        String serverName = serverDef.getName();
        String username = serverDef.getUser();
        String password = serverDef.getPasswd();
        String endpointaddress = serverDef.getUrl();
        TreeParent serverRoot = new TreeParent(serverName, null, TreeObject._SERVER_, endpointaddress, username
                + ":" + (password == null ? "" : password));//$NON-NLS-1$//$NON-NLS-2$
        UserInfo user = new UserInfo();
        user.setUsername(username);
        user.setPassword(password);
        user.setServerUrl(endpointaddress);

        serverRoot.setUser(user);

        serverMap.put(serverDef, serverRoot);

        return serverRoot;
    }
}
