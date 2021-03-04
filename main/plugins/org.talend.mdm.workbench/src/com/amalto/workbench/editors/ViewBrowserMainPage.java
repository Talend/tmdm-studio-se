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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.amalto.workbench.dialogs.DOMViewDialog;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.models.IXObjectModelListener;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.providers.XObjectBrowserInput;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.utils.XtentisException;
import com.amalto.workbench.webservices.TMDMService;
import com.amalto.workbench.webservices.WSDataClusterPK;
import com.amalto.workbench.webservices.WSGetView;
import com.amalto.workbench.webservices.WSQuickSearch;
import com.amalto.workbench.webservices.WSStringPredicate;
import com.amalto.workbench.webservices.WSView;
import com.amalto.workbench.webservices.WSViewPK;
import com.amalto.workbench.webservices.WSViewSearch;
import com.amalto.workbench.webservices.WSWhereAnd;
import com.amalto.workbench.webservices.WSWhereCondition;
import com.amalto.workbench.webservices.WSWhereItem;
import com.amalto.workbench.webservices.WSWhereOperator;

public class ViewBrowserMainPage extends AMainPage implements IXObjectModelListener {

    private static Log log = LogFactory.getLog(ViewBrowserMainPage.class);

    protected Combo dataClusterCombo;

    protected Text searchText;

    protected TableViewer resultsViewer;

    protected List viewableBEsList;

    protected List searchableBEsList;

    protected ListViewer wcListViewer;

    protected Label resultsLabel;

    protected Button matchAllWordsBtn;

    private Combo searchItemCombo;

    private final String FULL_TEXT = Messages.ViewBrowserMainPage_fullText;

    protected Combo clusterTypeCombo;

    public ViewBrowserMainPage(FormEditor editor) {
        super(editor, ViewBrowserMainPage.class.getName(), Messages.ViewBrowserMainPage_ViewBrowser
                + ((XObjectBrowserInput) editor.getEditorInput()).getName());
        // listen to events
        ((XObjectBrowserInput) editor.getEditorInput()).addListener(this);
    }

    @Override
    protected void createCharacteristicsContent(FormToolkit toolkit, Composite charComposite) {

        try {

            Label vbeLabel = toolkit.createLabel(charComposite, Messages.ViewBrowserMainPage_ViewableElements, SWT.NULL);
            vbeLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));

            Label sbeLabel = toolkit.createLabel(charComposite, Messages.ViewBrowserMainPage_SearchableElements, SWT.NULL);
            sbeLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));

            viewableBEsList = new List(charComposite, SWT.SINGLE | SWT.V_SCROLL | SWT.BORDER);
            viewableBEsList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
            ((GridData) viewableBEsList.getLayoutData()).heightHint = 100;

            searchableBEsList = new List(charComposite, SWT.SINGLE | SWT.V_SCROLL | SWT.BORDER);
            searchableBEsList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
            ((GridData) searchableBEsList.getLayoutData()).heightHint = 100;

            Label wcLabel = toolkit.createLabel(charComposite, Messages.ViewBrowserMainPage_Conditions, SWT.NULL);
            wcLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 2, 1));
            wcListViewer = new ListViewer(charComposite, SWT.BORDER);
            wcListViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
            ((GridData) wcListViewer.getControl().getLayoutData()).minimumHeight = 100;
            wcListViewer.setContentProvider(new IStructuredContentProvider() {

                @Override
                public void dispose() {
                }

                @Override
                public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
                }

                @Override
                public Object[] getElements(Object inputElement) {
                    return ((WSView) inputElement).getWhereConditions().toArray();
                }
            });
            wcListViewer.setLabelProvider(new ILabelProvider() {

                @Override
                public Image getImage(Object element) {
                    return null;
                }

                @Override
                public String getText(Object element) {
                    WSWhereCondition wc = (WSWhereCondition) element;
                    String text = wc.getLeftPath() + " ";//$NON-NLS-1$
                    if (wc.getOperator().equals(WSWhereOperator.CONTAINS)) {
                        text += "Contains";//$NON-NLS-1$
                    } else if (wc.getOperator().equals(WSWhereOperator.CONTAINS_SENTENCE)) {
                        text += "contains the sentence";//$NON-NLS-1$
                    } else if (wc.getOperator().equals(WSWhereOperator.EQUALS)) {
                        text += "=";//$NON-NLS-1$
                    } else if (wc.getOperator().equals(WSWhereOperator.GREATER_THAN)) {
                        text += ">";//$NON-NLS-1$
                    } else if (wc.getOperator().equals(WSWhereOperator.GREATER_THAN_OR_EQUAL)) {
                        text += ">=";//$NON-NLS-1$
                    } else if (wc.getOperator().equals(WSWhereOperator.JOIN)) {
                        text += "Joins With";//$NON-NLS-1$
                    } else if (wc.getOperator().equals(WSWhereOperator.LOWER_THAN)) {
                        text += "<";//$NON-NLS-1$
                    } else if (wc.getOperator().equals(WSWhereOperator.LOWER_THAN_OR_EQUAL)) {
                        text += "<=";//$NON-NLS-1$
                    } else if (wc.getOperator().equals(WSWhereOperator.NOT_EQUALS)) {
                        text += "!=";//$NON-NLS-1$
                    } else if (wc.getOperator().equals(WSWhereOperator.STARTSWITH)) {
                        text += "Starts With";//$NON-NLS-1$
                    } else if (wc.getOperator().equals(WSWhereOperator.EMPTY_NULL)) {
                        text += "Is Empty Or Null";//$NON-NLS-1$
                    } else if (wc.getOperator().equals(WSWhereOperator.FULLTEXTSEARCH)) {
                        text += "whole content contains";//$NON-NLS-1$
                    }
                    text += " ";//$NON-NLS-1$
                    if (!wc.getOperator().equals(WSWhereOperator.JOIN)) {
                        text += "\"";//$NON-NLS-1$
                    }
                    text += wc.getRightValueOrPath();
                    if (!wc.getOperator().equals(WSWhereOperator.JOIN)) {
                        text += "\"";//$NON-NLS-1$
                    }
                    text += " ";//$NON-NLS-1$
                    if (wc.getStringPredicate().equals(WSStringPredicate.AND)) {
                        text += "[and]";//$NON-NLS-1$
                    } else if (wc.getStringPredicate().equals(WSStringPredicate.NONE)) {
                        text += "";//$NON-NLS-1$
                    } else if (wc.getStringPredicate().equals(WSStringPredicate.OR)) {
                        text += "[or]";//$NON-NLS-1$
                    } else if (wc.getStringPredicate().equals(WSStringPredicate.NOT)) {
                        text += "[not]";//$NON-NLS-1$
                    }

                    return text;
                }

                @Override
                public void addListener(ILabelProviderListener listener) {
                }

                @Override
                public void dispose() {
                }

                @Override
                public boolean isLabelProperty(Object element, String property) {
                    return false;
                }

                @Override
                public void removeListener(ILabelProviderListener listener) {
                }
            });

            int columns = 6;
            Composite resultsGroup = this.getNewSectionComposite(Messages.ViewBrowserMainPage_SearchAndResults);
            resultsGroup.setLayout(new GridLayout(columns, false));

            Composite createComposite = toolkit.createComposite(resultsGroup);
            GridLayout layout = new GridLayout(3, false);
            layout.marginWidth = 0;
            createComposite.setLayout(layout);
            createComposite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));

            Label containerLabel = toolkit.createLabel(createComposite, Messages.ViewBrowserMainPage_Container, SWT.NULL);
            containerLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));

            dataClusterCombo = new Combo(createComposite, SWT.READ_ONLY | SWT.DROP_DOWN | SWT.SINGLE);
            dataClusterCombo.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
            ((GridData) dataClusterCombo.getLayoutData()).minimumWidth = 100;

            clusterTypeCombo = new Combo(createComposite, SWT.READ_ONLY | SWT.SINGLE);
            GridData typeLayout = new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1);
            typeLayout.horizontalIndent = 10;
            clusterTypeCombo.setLayoutData(typeLayout);

            Label searchOnLabel = toolkit.createLabel(resultsGroup, Messages.ViewBrowserMainPage_SearchOn, SWT.NULL);
            GridData layoutData = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
            layoutData.horizontalIndent = 20;
            searchOnLabel.setLayoutData(layoutData);

            searchItemCombo = new Combo(resultsGroup, SWT.READ_ONLY | SWT.DROP_DOWN | SWT.SINGLE);
            searchItemCombo.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
            ((GridData) searchItemCombo.getLayoutData()).minimumWidth = 100;
            searchItemCombo.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    if (FULL_TEXT.equals(searchItemCombo.getText())) {
                        matchAllWordsBtn.setEnabled(true);
                    } else {
                        matchAllWordsBtn.setSelection(false);
                        matchAllWordsBtn.setEnabled(false);
                    }
                }
            });

            searchText = toolkit.createText(resultsGroup, "", SWT.BORDER);//$NON-NLS-1$
            searchText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
            searchText.addKeyListener(new KeyListener() {

                @Override
                public void keyPressed(KeyEvent e) {
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    if ((e.stateMask == 0) && (e.character == SWT.CR)) {
                        ViewBrowserMainPage.this.resultsViewer.setInput(getResults());
                    }
                }// keyReleased
            }// keyListener
                    );

            Button bSearch = toolkit.createButton(resultsGroup, Messages.ViewBrowserMainPage_Search, SWT.CENTER);
            bSearch.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, true, 1, 1));
            bSearch.addListener(SWT.Selection, new Listener() {

                @Override
                public void handleEvent(Event event) {
                    ViewBrowserMainPage.this.resultsViewer.setInput(getResults());
                };
            });

            matchAllWordsBtn = toolkit.createButton(resultsGroup, Messages.ViewBrowserMainPage_MatchWholeSentence, SWT.CHECK);
            matchAllWordsBtn.setSelection(true);

            resultsLabel = toolkit.createLabel(resultsGroup, "", SWT.NULL); //$NON-NLS-1$
            GridData resultLayoutData = new GridData(SWT.LEFT, SWT.CENTER, false, false, columns - 1, 1);
            resultLayoutData.widthHint = 100;
            resultsLabel.setLayoutData(resultLayoutData);

            resultsViewer = new TableViewer(resultsGroup);
            resultsViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, columns, 1));
            ((GridData) resultsViewer.getControl().getLayoutData()).heightHint = 500;
            resultsViewer.setContentProvider(new ArrayContentProvider());
            resultsViewer.setLabelProvider(new XMLTableLabelProvider());
            resultsViewer.addDoubleClickListener(new IDoubleClickListener() {

                @Override
                public void doubleClick(DoubleClickEvent event) {
                    resultsViewer.setSelection(event.getSelection());
                    try {
                        new DOMViewAction(ViewBrowserMainPage.this.getSite().getShell(), resultsViewer).run();
                    } catch (Exception e) {
                        MessageDialog.openError(
                                ViewBrowserMainPage.this.getSite().getShell(),
                                Messages._Error,
                                Messages.bind(Messages.ViewBrowserMainPage_ErrorMsg, e.getClass().getName(),
                                        e.getLocalizedMessage()));
                    }
                }
            });

            hookContextMenu();

            addListener();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }// createCharacteristicsContent

    @Override
    protected void refreshData() {
        try {

            if (viewableBEsList.isDisposed() || searchableBEsList.isDisposed() || wcListViewer.getList().isDisposed()
                    || searchItemCombo.isDisposed() || dataClusterCombo.isDisposed()) {
                return;
            }

            WSView view = null;
            if (getXObject().getWsObject() == null) { // then fetch from server
                TMDMService port = getMDMService();
                view = port.getView(new WSGetView((WSViewPK) getXObject().getWsKey()));
                getXObject().setWsObject(view);
            } else { // it has been opened by an editor - use the object there
                view = (WSView) getXObject().getWsObject();
            }

            java.util.List<String> paths = view.getViewableBusinessElements();
            // Fill the vbe List
            viewableBEsList.removeAll();
            for (String path : paths) {
                viewableBEsList.add(path);
            }

            paths = view.getSearchableBusinessElements();
            searchableBEsList.removeAll();
            searchItemCombo.removeAll();
            if (paths != null) {
                for (String path : paths) {
                    searchableBEsList.add(path);
                    if (path.contains("/")) { //$NON-NLS-1$
                        searchItemCombo.add(path);
                    }
                }
            }
            searchItemCombo.add(FULL_TEXT);
            searchItemCombo.setText(FULL_TEXT);

            wcListViewer.setInput(view);
            wcListViewer.refresh();

            dataClusterCombo.removeAll();
            java.util.List<WSDataClusterPK> dataClusterPKs = getDataClusterPKs();
            if ((dataClusterPKs == null) || (dataClusterPKs.size() == 0)) {
                MessageDialog.openError(this.getSite().getShell(), Messages._Error, Messages.ViewBrowserMainPage_ErrorMsg1);
                return;
            }
            for (WSDataClusterPK pk : dataClusterPKs) {
                dataClusterCombo.add(pk.getPk());
            }
            dataClusterCombo.select(0);

            clusterTypeCombo.setItems(getClusterTypes());
            clusterTypeCombo.select(0);

            this.getManagedForm().reflow(true);

            searchText.setFocus();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (!Util.handleConnectionException(this.getSite().getShell(), e, Messages.ViewBrowserMainPage_ErrorTitle2)) {
                MessageDialog.openError(this.getSite().getShell(), Messages.ViewBrowserMainPage_ErrorTitle2,
                        Messages.bind(Messages.ViewBrowserMainPage_ErrorMsg2, e.getLocalizedMessage()));
            }
        }
    }

    protected String[] getClusterTypes() {
        return new String[0];
    }

    protected java.util.List<WSDataClusterPK> getDataClusterPKs() throws MalformedURLException, XtentisException {
        return Util.getAllDataClusterPKs(new URL(getXObject().getEndpointAddress()), getXObject().getUsername(), getXObject()
                .getPassword());
    }

    @Override
    protected void commit() {
        try {

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            MessageDialog.openError(this.getSite().getShell(), Messages.ViewBrowserMainPage_ErrorTitle3,
                    Messages.bind(Messages.ViewBrowserMainPage_ErrorMsg3, e.getLocalizedMessage()));
        }
    }

    @Override
    protected void createActions() {

    }

    private void hookContextMenu() {
        MenuManager menuMgr = new MenuManager();
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {

            @Override
            public void menuAboutToShow(IMenuManager manager) {
                manager.add(new DOMViewAction(ViewBrowserMainPage.this.getSite().getShell(),
                        ViewBrowserMainPage.this.resultsViewer));
            }
        });
        Menu menu = menuMgr.createContextMenu(resultsViewer.getControl());
        resultsViewer.getControl().setMenu(menu);
        getSite().registerContextMenu(menuMgr, resultsViewer);
    }

    private void addListener() {
        dataClusterCombo.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                clusterTypeCombo.setItems(getClusterTypes());
                clusterTypeCombo.select(0);
            }
        });
    }

    protected void fillContextMenu(IMenuManager manager) {
        return;
    }

    protected WSViewPK getViewPK() {
        return (WSViewPK) getXObject().getWsKey();
    }

    public String[] getResults() {

        Cursor waitCursor = null;

        try {

            Display display = getEditor().getSite().getPage().getWorkbenchWindow().getWorkbench().getDisplay();
            waitCursor = new Cursor(display, SWT.CURSOR_WAIT);
            this.getSite().getShell().setCursor(waitCursor);

            TMDMService service = getMDMService();
            java.util.List<String> results = null;
            int maxItem = 10;

            String search = "".equals(searchText.getText()) ? "*" : searchText.getText(); //$NON-NLS-1$ //$NON-NLS-2$
            WSDataClusterPK wsDataClusterPK = new WSDataClusterPK(dataClusterCombo.getText() + getPkAddition());
            if (FULL_TEXT.equals(searchItemCombo.getText())) {
                boolean matchAllWords = matchAllWordsBtn.getSelection();

                results = service.quickSearch(
                        new WSQuickSearch(null, matchAllWords, maxItem, null, search, 0, Integer.MAX_VALUE, wsDataClusterPK,
                                getViewPK())).getStrings();

            } else {
                WSView wsview = (WSView) wcListViewer.getInput();

                java.util.List<WSWhereCondition> array = wsview.getWhereConditions();
                java.util.List<WSWhereItem> conditions = new ArrayList<WSWhereItem>();
                for (WSWhereCondition condition : array) {
                    WSWhereItem item = new WSWhereItem(null, condition, null);
                    conditions.add(item);
                }

                WSWhereCondition condition = new WSWhereCondition(searchItemCombo.getText(), WSWhereOperator.CONTAINS, search,
                        true, WSStringPredicate.AND);
                WSWhereItem item = new WSWhereItem(null, condition, null);
                conditions.add(item);
                WSWhereAnd and = new WSWhereAnd(conditions);
                WSWhereItem wi = new WSWhereItem(and, null, null);

                results = service.viewSearch(
                        new WSViewSearch("ascending", maxItem, null, 0, -1, wi, wsDataClusterPK, getViewPK())).getStrings(); //$NON-NLS-1$
            }

            resultsLabel.setText(Messages.bind(Messages.ViewBrowserMainPage_Results, results.size() - 1));
            if (results.size() > 1) {
                return results.subList(1, results.size()).toArray(new String[0]);
            }

            return new String[0];
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if ((e.getLocalizedMessage() != null) && e.getLocalizedMessage().contains("10000")) {
                MessageDialog.openError(this.getSite().getShell(), Messages.ViewBrowserMainPage_ErrorTitle4,
                        Messages.ViewBrowserMainPage_ErrorMsg4);
            } else if (!Util.handleConnectionException(this.getSite().getShell(), e, Messages.ViewBrowserMainPage_ErrorTitle5)) {
                MessageDialog.openError(this.getSite().getShell(), Messages.ViewBrowserMainPage_ErrorTitle5,
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

    protected String getPkAddition() {
        return ""; //$NON-NLS-1$
    }

    class DOMViewAction extends Action {

        private Shell shell = null;

        private Viewer viewer;

        public DOMViewAction(Shell shell, Viewer viewer) {
            super();
            this.shell = shell;
            this.viewer = viewer;
            setImageDescriptor(ImageCache.getImage("icons/add_obj.gif"));//$NON-NLS-1$
            setText(Messages.ViewBrowserMainPage_ViewTree);
            setToolTipText(Messages.ViewBrowserMainPage_ViewAsDOMTree);
        }

        @Override
        public void run() {
            try {
                super.run();

                IStructuredSelection selection = ((IStructuredSelection) viewer.getSelection());
                String xml = (String) selection.getFirstElement();

                // clean up highlights
                xml = xml.replaceAll("\\s*__h", "").replaceAll("h__\\s*", "");//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$

                final DOMViewDialog d = new DOMViewDialog(ViewBrowserMainPage.this.getSite().getShell(), Util.parse(xml));
                d.addListener(new Listener() {

                    @Override
                    public void handleEvent(Event event) {
                        if (event.button == DOMViewDialog.BUTTON_CLOSE) {
                            d.close();
                        }
                    }
                });
                d.setBlockOnOpen(true);
                d.open();
                d.close();

            } catch (Exception e) {
                log.error(e.getMessage(), e);
                MessageDialog.openError(shell, Messages._Error,
                        Messages.bind(Messages.ViewBrowserMainPage_ErrorMsg5, e.getLocalizedMessage()));
            }
        }

        @Override
        public void runWithEvent(Event event) {
            super.runWithEvent(event);
        }

    }

    protected static Pattern highlightLeft = Pattern.compile("\\s*__h");//$NON-NLS-1$

    protected static Pattern highlightRight = Pattern.compile("h__\\s*");//$NON-NLS-1$

    protected static Pattern emptyTags = Pattern.compile("\\s*<(.*?)\\/>\\s*");//$NON-NLS-1$

    protected static Pattern openingTags = Pattern.compile("\\s*<([^\\/].*?[^\\/])>\\s*");//$NON-NLS-1$

    protected static Pattern closingTags = Pattern.compile("\\s*</(.*?)>\\s*");//$NON-NLS-1$

    class XMLTableLabelProvider implements ITableLabelProvider {

        @Override
        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

        @Override
        public String getColumnText(Object element, int columnIndex) {
            String xml = (String) element;
            xml = highlightLeft.matcher(xml).replaceAll("");//$NON-NLS-1$
            xml = highlightRight.matcher(xml).replaceAll("");//$NON-NLS-1$
            xml = emptyTags.matcher(xml).replaceAll("[$1]");//$NON-NLS-1$
            xml = openingTags.matcher(xml).replaceAll("[$1: ");//$NON-NLS-1$
            xml = closingTags.matcher(xml).replaceAll("]");//$NON-NLS-1$
            if (xml.length() >= 150) {
                return xml.substring(0, 150) + "...";//$NON-NLS-1$
            }
            return xml;
        }

        @Override
        public void addListener(ILabelProviderListener listener) {
        }

        @Override
        public void dispose() {
        }

        @Override
        public boolean isLabelProperty(Object element, String property) {
            return false;
        }

        @Override
        public void removeListener(ILabelProviderListener listener) {
        }

    }

    /*********************************
     * IXObjectModelListener interface
     */
    @Override
    public void handleEvent(int type, TreeObject parent, TreeObject child) {
        refreshData();
    }

}
