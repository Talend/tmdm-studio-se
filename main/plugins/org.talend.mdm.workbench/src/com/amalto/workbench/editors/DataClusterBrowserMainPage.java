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

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.rmi.ServerException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.talend.mdm.commmon.util.core.ICoreConstants;

import com.amalto.workbench.availablemodel.AvailableModelUtil;
import com.amalto.workbench.availablemodel.IAvailableModel;
import com.amalto.workbench.compare.CompareHeadInfo;
import com.amalto.workbench.compare.CompareManager;
import com.amalto.workbench.dialogs.DOMViewDialog;
import com.amalto.workbench.dialogs.datacontainer.DataContainerDOMViewDialog;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.models.IXObjectModelListener;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.models.TreeParent;
import com.amalto.workbench.providers.XObjectBrowserInput;
import com.amalto.workbench.providers.XtentisServerObjectsRetriever;
import com.amalto.workbench.utils.LineItem;
import com.amalto.workbench.utils.UserInfo;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.webservices.WSDataClusterPK;
import com.amalto.workbench.webservices.WSDataModelPK;
import com.amalto.workbench.webservices.WSDeleteItemWithReport;
import com.amalto.workbench.webservices.WSGetConceptsInDataCluster;
import com.amalto.workbench.webservices.WSGetItem;
import com.amalto.workbench.webservices.WSIsItemModifiedByOther;
import com.amalto.workbench.webservices.WSItem;
import com.amalto.workbench.webservices.WSItemPK;
import com.amalto.workbench.webservices.WSPutItem;
import com.amalto.workbench.webservices.WSPutItemWithReport;
import com.amalto.workbench.webservices.WSRegexDataModelPKs;
import com.amalto.workbench.webservices.WSRouteItemV2;
import com.amalto.workbench.webservices.WSString;
import com.amalto.workbench.webservices.WSStringArray;
import com.amalto.workbench.webservices.WSUpdateMetadataItem;
import com.amalto.workbench.webservices.WSVersioningGetItemContent;
import com.amalto.workbench.webservices.XtentisPort;

public class DataClusterBrowserMainPage extends AMainPage implements IXObjectModelListener {

    private static final Log log = LogFactory.getLog(DataClusterBrowserMainPage.class);

    protected static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");//$NON-NLS-1$

    protected TableViewer resultsViewer;

    protected ListViewer wcListViewer;

    private DataClusterComposite clusterComp;

    public DataClusterBrowserMainPage(FormEditor editor) {
        super(editor, DataClusterBrowserMainPage.class.getName(), Messages.bind(Messages.DataClusterBrowserMainPage_0,
                ((XObjectBrowserInput) editor.getEditorInput()).getName()));
        // listen to events
        ((XObjectBrowserInput) editor.getEditorInput()).addListener(this);
    }

    @Override
    protected void createFormContent(IManagedForm managedForm) {

        try {
            // sets the title
            managedForm.getForm().setText(this.getTitle());
            // get the body
            Composite composite = managedForm.getForm().getBody();
            // composite.setLayout(new GridLayout(9, false));
            composite.setLayout(new GridLayout());

            clusterComp = new DataClusterComposite(composite, SWT.NONE, this, getSite(), getXObject());
            clusterComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            resultsViewer = clusterComp.getResultsViewer();

            hookDoubleClick();

            hookKeyboard();

            hookContextMenu();

            managedForm.reflow(true); // nothng will show on the form if not called
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }// createFormContent

    @Override
    protected void createCharacteristicsContent(FormToolkit toolkit, Composite charComposite) {
        // Everything is implemented in createFormContent
    }

    public void doSearch() {
        clusterComp.doSearch();
    }

    @Override
    protected void refreshData() {
        clusterComp.refreshData();
    }

    protected LineItem[] getResults(boolean showResultInfo) {
        return clusterComp.getResults(showResultInfo);
    }

    @Override
    protected void commit() {
        try {

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            MessageDialog.openError(this.getSite().getShell(), Messages.DataClusterBrowserMainPage_18,
                    Messages.bind(Messages.DataClusterBrowserMainPage_19, e.getLocalizedMessage()));
        }
    }

    @Override
    protected void createActions() {
    }

    private void hookDoubleClick() {
        resultsViewer.addDoubleClickListener(new IDoubleClickListener() {

            public void doubleClick(DoubleClickEvent event) {
                resultsViewer.setSelection(event.getSelection());
                try {
                    new EditItemAction(DataClusterBrowserMainPage.this.getSite().getShell(), resultsViewer).run();
                } catch (Exception e) {
                    MessageDialog.openError(DataClusterBrowserMainPage.this.getSite().getShell(), Messages._Error, Messages.bind(
                            Messages.DataClusterBrowserMainPage_10, e.getClass().getName(), e.getLocalizedMessage()));
                }
            }
        });
    }

    private void hookKeyboard() {
        resultsViewer.getControl().addKeyListener(new KeyListener() {

            public void keyPressed(KeyEvent e) {
            }

            public void keyReleased(KeyEvent e) {
                if (e.keyCode == SWT.DEL) {
                    new PhysicalDeleteItemsAction(DataClusterBrowserMainPage.this.getSite().getShell(),
                            DataClusterBrowserMainPage.this.resultsViewer).run();
                }
            }
        });
    }

    private void hookContextMenu() {
        MenuManager menuMgr = new MenuManager();
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {

            public void menuAboutToShow(IMenuManager manager) {
                // ViewBrowserMainPage.this.fillContextMenu(manager);
                manager.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
                manager.appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS, new EditItemAction(DataClusterBrowserMainPage.this
                        .getSite().getShell(), DataClusterBrowserMainPage.this.resultsViewer));
                manager.appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS, new EditTaskIdAction(
                        DataClusterBrowserMainPage.this.getSite().getShell(), DataClusterBrowserMainPage.this.resultsViewer));
                manager.appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS, new PhysicalDeleteItemsAction(
                        DataClusterBrowserMainPage.this.getSite().getShell(), DataClusterBrowserMainPage.this.resultsViewer));
                manager.appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS, new LogicalDeleteItemsAction(
                        DataClusterBrowserMainPage.this.getSite().getShell(), DataClusterBrowserMainPage.this.resultsViewer));
                manager.appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS, new NewItemAction(DataClusterBrowserMainPage.this
                        .getSite().getShell(), DataClusterBrowserMainPage.this.resultsViewer));
                manager.appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS, new SubmitItemsAction(
                        DataClusterBrowserMainPage.this.getSite().getShell(), DataClusterBrowserMainPage.this.resultsViewer));
                // compare item with each other
                manager.appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS, new CompareItemWithEachOtherAction(getSite()
                        .getShell(), getResultsViewer()));
                // available models
                java.util.List<IAvailableModel> availablemodels = AvailableModelUtil.getAvailableModels();
                for (IAvailableModel model : availablemodels) {
                    model.menuAboutToShow(manager, DataClusterBrowserMainPage.this);
                }

            }
        });
        Menu menu = menuMgr.createContextMenu(resultsViewer.getControl());
        resultsViewer.getControl().setMenu(menu);
        getSite().registerContextMenu(menuMgr, resultsViewer);
    }

    protected void fillContextMenu(IMenuManager manager) {
    }

    public TableViewer getResultsViewer() {
        return resultsViewer;
    }

    /*********************************
     * IXObjectModelListener interface
     */
    public void handleEvent(int type, TreeObject parent, TreeObject child) {
        refreshData();
    }

    class EditItemAction extends Action {

        protected Shell shell = null;

        protected Viewer viewer;

        public EditItemAction(Shell shell, Viewer viewer) {
            super();
            this.shell = shell;
            this.viewer = viewer;
            setImageDescriptor(ImageCache.getImage("icons/edit_obj.gif"));//$NON-NLS-1$
            setText(Messages.DataClusterBrowserMainPage_29);
            setToolTipText(Messages.DataClusterBrowserMainPage_30);
        }

        @Override
        public void run() {
            try {
                super.run();
                final XtentisPort port = Util.getPort(getXObject());
                IStructuredSelection selection = ((IStructuredSelection) viewer.getSelection());
                LineItem li = (LineItem) selection.getFirstElement();
                if (li == null) {
                    return;
                }
                final WSItem wsItem = port.getItem(new WSGetItem(new WSItemPK((WSDataClusterPK) getXObject().getWsKey(), li
                        .getConcept().trim(), li.getIds())));
                String xml = Util.formatXsdSource(wsItem.getContent());

                WSDataModelPK[] dmPKs = port.getDataModelPKs(new WSRegexDataModelPKs("*")).getWsDataModelPKs();//$NON-NLS-1$
                ArrayList<String> dataModels = new ArrayList<String>();
                if (dmPKs != null) {
                    for (int i = 0; i < dmPKs.length; i++) {
                        if (!"XMLSCHEMA---".equals(dmPKs[i].getPk())) { //$NON-NLS-1$
                            dataModels.add(dmPKs[i].getPk());
                        }
                    }
                }

                final DataContainerDOMViewDialog d = new DataContainerDOMViewDialog(DataClusterBrowserMainPage.this.getSite()
                        .getShell(), port, Util.parse(xml), dataModels, DOMViewDialog.TREE_VIEWER, wsItem.getDataModelName());
                d.addListener(new Listener() {

                    public void handleEvent(Event event) {
                        if (event.button == DOMViewDialog.BUTTON_SAVE) {
                            // attempt to save
                            try {
                                // check the item is modified by others?
                                boolean isModified = port.isItemModifiedByOther(new WSIsItemModifiedByOther(wsItem)).is_true();
                                WSPutItem putItem = new WSPutItem((WSDataClusterPK) getXObject().getWsKey(), d.getXML(),
                                        "".equals(d //$NON-NLS-1$
                                                .getDataModelName()) ? null : new WSDataModelPK(d.getDataModelName()), false);
                                WSPutItemWithReport itemWithReport = new WSPutItemWithReport(putItem,
                                        "genericUI", d.isBeforeVerification());//$NON-NLS-1$
                                if (isModified) {
                                    if (MessageDialog.openConfirm(null, Messages.DataClusterBrowserMainPage_31,
                                            Messages.DataClusterBrowserMainPage_32)) {
                                        if (d.isTriggerProcess()) {
                                            Util.getPort(getXObject()).putItemWithReport(itemWithReport);
                                        } else {
                                            Util.getPort(getXObject()).putItem(putItem);
                                        }
                                    }
                                } else {
                                    if (d.isTriggerProcess()) {
                                        Util.getPort(getXObject()).putItemWithReport(itemWithReport);
                                    } else {
                                        Util.getPort(getXObject()).putItem(putItem);
                                    }
                                }
                            } catch (Exception e) {
                                log.error(e.getMessage(), e);
                                if(!Util.handleConnectionException(shell, e, Messages.DataClusterBrowserMainPage_33)){
                                	 MessageDialog.openError(shell, Messages.DataClusterBrowserMainPage_33,
                                             Messages.bind(Messages.DataClusterBrowserMainPage_34, e.getLocalizedMessage()));
                                }
                                return;
                            }
                        }// if
                        d.close();
                    }// handleEvent
                });

                d.setBlockOnOpen(true);
                d.open();

            } catch (Exception e) {
                log.error(e.getMessage(), e);
                MessageDialog.openError(shell, Messages._Error,
                        Messages.bind(Messages.DataClusterBrowserMainPage_36, e.getLocalizedMessage()));
            }
        }

        @Override
        public void runWithEvent(Event event) {
            super.runWithEvent(event);
        }

    }

    class EditTaskIdAction extends Action {

        protected Shell shell = null;

        protected Viewer viewer;

        public EditTaskIdAction(Shell shell, Viewer viewer) {
            super();
            this.shell = shell;
            this.viewer = viewer;
            setImageDescriptor(ImageCache.getImage("icons/default.gif"));//$NON-NLS-1$
            setText(Messages.DataClusterBrowserMainPage_37);
            setToolTipText(Messages.DataClusterBrowserMainPage_38);
        }

        @Override
        public void run() {
            try {
                super.run();
                final XtentisPort port = Util.getPort(getXObject());
                IStructuredSelection selection = ((IStructuredSelection) viewer.getSelection());
                LineItem li = (LineItem) selection.getFirstElement();

                InputDialog input = new InputDialog(DataClusterBrowserMainPage.this.getSite().getShell(),
                        Messages.DataClusterBrowserMainPage_39, Messages.DataClusterBrowserMainPage_40, li.getTaskId(), null);
                input.setBlockOnOpen(true);

                if (input.open() == Window.OK) {
                    input.getValue();
                    li.setTaskId(input.getValue());
                    // @temp yguo, save the taskid to db.
                    WSUpdateMetadataItem wsUpdateMetadataItem = new WSUpdateMetadataItem();
                    WSItemPK wsItemPK = new WSItemPK();
                    wsItemPK.setWsDataClusterPK((WSDataClusterPK) getXObject().getWsKey());
                    wsItemPK.setConceptName(li.getConcept());
                    wsItemPK.setIds(li.getIds());
                    wsUpdateMetadataItem.setWsItemPK(wsItemPK);
                    wsUpdateMetadataItem.setTaskId(input.getValue());
                    port.updateItemMetadata(wsUpdateMetadataItem);
                    // @temp yguo, refresh the data
                    viewer.refresh();
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
				if (!Util.handleConnectionException(shell, e, Messages.DataClusterBrowserMainPage_41)) {
					MessageDialog.openError(shell, Messages.DataClusterBrowserMainPage_41,
	                        Messages.bind(Messages.DataClusterBrowserMainPage_42, e.getLocalizedMessage()));
	                
				}
                return;
            }
        }

    }

    /***************************************************************
     * Compare item with each other
     ***************************************************************/
    public class CompareItemWithEachOtherAction extends Action {

        protected Shell shell = null;

        protected Viewer viewer;

        public CompareItemWithEachOtherAction(Shell shell, Viewer viewer) {
            super();
            this.shell = shell;
            this.viewer = viewer;
            setImageDescriptor(ImageCache.getImage(EImage.SYNCH.getPath()));
            setText(Messages.DataClusterBrowserMainPage_43);
            setToolTipText(Messages.DataClusterBrowserMainPage_44);
        }

        @Override
        public void run() {

            try {
                super.run();

                IStructuredSelection selection = ((IStructuredSelection) viewer.getSelection());
                int selectSize = selection.size();
                if (selectSize != 2) {
                    MessageDialog.openWarning(null, Messages.Warning, Messages.DataClusterBrowserMainPage_46);
                    return;
                }

                @SuppressWarnings("unchecked")
                List<LineItem> liList = selection.toList();

                LineItem leftLineItem = liList.get(0);
                LineItem rightLineItem = liList.get(1);

                // left
                WSItemPK leftWSItemPK = new WSItemPK((WSDataClusterPK) getXObject().getWsKey(), leftLineItem.getConcept().trim(),
                        leftLineItem.getIds());
                WSItem leftWSItem = Util.getPort(getXObject()).getItem(new WSGetItem(leftWSItemPK));
                String leftItemXmlContent = leftWSItem.getContent();

                // right
                WSItemPK rightWSItemPK = new WSItemPK((WSDataClusterPK) getXObject().getWsKey(), rightLineItem.getConcept()
                        .trim(), rightLineItem.getIds());
                WSItem rightWSItem = Util.getPort(getXObject()).getItem(new WSGetItem(rightWSItemPK));
                String rightItemXmlContent = rightWSItem.getContent();

                if (leftItemXmlContent != null && rightItemXmlContent != null) {
                    CompareHeadInfo compareHeadInfo = new CompareHeadInfo(getXObject());
                    compareHeadInfo.setItem(true);
                    compareHeadInfo.setDataModelName(leftWSItem.getDataModelName());
                    CompareManager.getInstance().compareTwoStream(leftItemXmlContent, rightItemXmlContent, true, compareHeadInfo,
                            leftWSItemPK.getConceptName() + "." + Util.joinStrings(leftWSItemPK.getIds(), "."),//$NON-NLS-1$//$NON-NLS-2$
                            rightWSItemPK.getConceptName() + "." + Util.joinStrings(rightWSItemPK.getIds(), "."), true, false);//$NON-NLS-1$//$NON-NLS-2$
                }

            } catch (Exception e) {
                log.error(e.getMessage(), e);
				if (!Util.handleConnectionException(shell, e, null)) {
                	MessageDialog.openError(shell, Messages._Error,
                            Messages.bind(Messages.DataClusterBrowserMainPage_48, e.getLocalizedMessage()));
                }
            }
        }
    }

    /***************************************************************
     * Compare item with svn TODO 1.object compare 2. item/object save
     ***************************************************************/
    public class CompareItemWithLatestRevisionAction extends Action {

        protected Shell shell = null;

        protected Viewer viewer;

        public CompareItemWithLatestRevisionAction(Shell shell, Viewer viewer) {
            super();
            this.shell = shell;
            this.viewer = viewer;
            setImageDescriptor(ImageCache.getImage(EImage.SYNCH.getPath()));
            setText(Messages.DataClusterBrowserMainPage_49);
            setToolTipText(Messages.DataClusterBrowserMainPage_50);
        }

        @Override
        public void run() {
            try {
                super.run();

                IStructuredSelection selection = ((IStructuredSelection) viewer.getSelection());
                int selectSize = selection.size();
                if (selectSize == 0) {
                    MessageDialog.openWarning(null, Messages.Warning, Messages.DataClusterBrowserMainPage_52);
                    return;
                } else if (selectSize > 1) {
                    MessageDialog.openWarning(null, Messages.Warning, Messages.DataClusterBrowserMainPage_54);
                    return;
                }
                LineItem li = (LineItem) selection.getFirstElement();

                WSItem wsItem = Util.getPort(getXObject()).getItem(
                        new WSGetItem(
                                new WSItemPK((WSDataClusterPK) getXObject().getWsKey(), li.getConcept().trim(), li.getIds())));

                String xml = wsItem.getContent();
                WSString svnContent = null;
                try {
                    svnContent = Util.getPort(getXObject()).versioningGetItemContent(
                            new WSVersioningGetItemContent(ICoreConstants.DEFAULT_SVN, new WSItemPK(wsItem.getWsDataClusterPK(),
                                    wsItem.getConceptName(), wsItem.getIds()), "-1"));//$NON-NLS-1$
                } catch (Exception e) {
                    MessageDialog.openWarning(null, Messages.Warning, e.getLocalizedMessage());
                    return;
                }

                String itemcontent = Util.getItemContent(svnContent.getValue());
                if (itemcontent != null) {
                    CompareHeadInfo compareHeadInfo = new CompareHeadInfo(getXObject());
                    compareHeadInfo.setItem(true);
                    compareHeadInfo.setDataModelName(wsItem.getDataModelName());
                    CompareManager.getInstance().compareTwoStream(xml, itemcontent, true, compareHeadInfo,
                            Messages.DataClusterBrowserMainPage_56, Messages.DataClusterBrowserMainPage_57, true, false);
                }

            } catch (Exception e) {
                log.error(e.getMessage(), e);
                if(!Util.handleConnectionException(shell, e, null)) {
                    MessageDialog.openError(shell, Messages._Error,
                        Messages.bind(Messages.DataClusterBrowserMainPage_59, e.getLocalizedMessage()));
                }
            }
        }
    }

    /***************************************************************
     * Delete Items Action
     * 
     * @author bgrieder
     * 
     ***************************************************************/

    class LogicalDeleteItemsAction extends Action {

        protected Shell shell = null;

        protected Viewer viewer;

        public LogicalDeleteItemsAction(Shell shell, Viewer viewer) {
            super();
            this.shell = shell;
            this.viewer = viewer;
            setImageDescriptor(ImageCache.getImage("icons/delete_obj.gif"));//$NON-NLS-1$

            IStructuredSelection selection = ((IStructuredSelection) viewer.getSelection());
            if (selection.size() == 1) {
                setText(Messages.DataClusterBrowserMainPage_60);
            } else {
                setText(Messages.bind(Messages.DataClusterBrowserMainPage_61, selection.size()));
            }

            setToolTipText(Messages.bind(Messages.DataClusterBrowserMainPage_62,
                    (selection.size() > 1 ? Messages.DataClusterBrowserMainPage_108 : Messages.DataClusterBrowserMainPage_63)));
        }

        @Override
        public void run() {
            try {
                super.run();

                // retrieve the list of items
                IStructuredSelection selection = ((IStructuredSelection) viewer.getSelection());
                @SuppressWarnings("unchecked")
                List<LineItem> lineItems = selection.toList();

                if (lineItems.size() == 0) {
                    return;
                }

                InputDialog id = new InputDialog(this.shell, Messages.DataClusterBrowserMainPage_64, Messages.bind(
                        Messages.DataClusterBrowserMainPage_65, lineItems.size()), Messages.DataClusterBrowserMainPage_67,
                        new IInputValidator() {

                            public String isValid(String newText) {
                                if ((newText == null) || !newText.matches("^\\/.*$")) { //$NON-NLS-1$
                                    return Messages.DataClusterBrowserMainPage_68;
                                }
                                return null;
                            };
                        });

                id.setBlockOnOpen(true);
                int ret = id.open();
                if (ret == Dialog.CANCEL) {
                    return;
                }

                // Instantiate the Monitor with actual deletes
                LogicalDeleteItemsWithProgress diwp = new LogicalDeleteItemsWithProgress(getXObject(), lineItems, id.getValue(),
                        this.shell);
                // run
                new ProgressMonitorDialog(this.shell).run(false, // fork
                        true, // cancelable
                        diwp);
                // refresh the search
                DataClusterBrowserMainPage.this.resultsViewer.setInput(getResults(false));

            } catch (Exception e) {
                log.error(e.getMessage(), e);
                MessageDialog.openError(shell, Messages._Error,
                        Messages.bind(Messages.DataClusterBrowserMainPage_70, e.getLocalizedMessage()));
            }
        }

        @Override
        public void runWithEvent(Event event) {
            super.runWithEvent(event);
        }

        // Progress Monitor that implements the actual delete
        class LogicalDeleteItemsWithProgress implements IRunnableWithProgress {

            TreeObject xObject;

            Collection<LineItem> lineItems;

            String partPath;

            Shell parentShell;

            public LogicalDeleteItemsWithProgress(TreeObject object, Collection<LineItem> lineItems, String partPath, Shell shell) {
                super();
                this.xObject = object;
                this.lineItems = lineItems;
                this.partPath = partPath;
                this.parentShell = shell;
            }

            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                try {
                    monitor.beginTask(Messages.DataClusterBrowserMainPage_71, lineItems.size());

                    XtentisPort port = Util.getPort(xObject);

                    int i = 0;
                    for (LineItem lineItem : lineItems) {
                        String itemID = ((WSDataClusterPK) xObject.getWsKey()).getPk() + "." + lineItem.getConcept() + "."//$NON-NLS-1$//$NON-NLS-2$
                                + Util.joinStrings(lineItem.getIds(), ".");//$NON-NLS-1$
                        monitor.subTask(Messages.bind(Messages.DataClusterBrowserMainPage_72, (i++), itemID));
                        if (monitor.isCanceled()) {
                            MessageDialog.openWarning(this.parentShell, Messages.DataClusterBrowserMainPage_74,
                                    Messages.bind(Messages.DataClusterBrowserMainPage_75, i));
                            return;
                        }
                        WSItemPK itempk = new WSItemPK((WSDataClusterPK) xObject.getWsKey(), lineItem.getConcept(),
                                lineItem.getIds());
                        port.deleteItemWithReport(new WSDeleteItemWithReport(itempk,
                                "genericUI", "LOGIC_DELETE", partPath, getXObject().getUsername(), false, true, false));//$NON-NLS-1$ //$NON-NLS-2$

                        // port.dropItem(new WSDropItem(new WSItemPK((WSDataClusterPK) xObject.getWsKey(),
                        // lineItem.getConcept(),
                        // lineItem.getIds()), partPath));
                        monitor.worked(1);
                    }// for

                    monitor.done();
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    if(!Util.handleConnectionException(shell, e, null)) {
                        MessageDialog.openError(shell, Messages.DataClusterBrowserMainPage_78,
                            Messages.bind(Messages.DataClusterBrowserMainPage_79, e.getLocalizedMessage()));
                    }
                }// try

            }// run
        }// class DeleteItemsWithProgress

    }// class DeletItemsAction

    class PhysicalDeleteItemsAction extends Action {

        protected Shell shell = null;

        protected Viewer viewer;

        public PhysicalDeleteItemsAction(Shell shell, Viewer viewer) {
            super();
            this.shell = shell;
            this.viewer = viewer;
            setImageDescriptor(ImageCache.getImage("icons/delete_obj.gif"));//$NON-NLS-1$
            IStructuredSelection selection = ((IStructuredSelection) viewer.getSelection());
            if (selection.size() == 1) {
                setText(Messages.DataClusterBrowserMainPage_80);
            } else {
                setText(Messages.bind(Messages.DataClusterBrowserMainPage_81, selection.size()));
            }

            setToolTipText(Messages.bind(Messages.DataClusterBrowserMainPage_82,
                    (selection.size() > 1 ? Messages.DataClusterBrowserMainPage_108 : Messages.DataClusterBrowserMainPage_83)));
        }

        @Override
        public void run() {
            try {
                super.run();

                // retrieve the list of items
                IStructuredSelection selection = ((IStructuredSelection) viewer.getSelection());
                @SuppressWarnings("unchecked")
                List<LineItem> lineItems = selection.toList();

                if (lineItems.size() == 0) {
                    return;
                }

                if (!MessageDialog.openConfirm(this.shell, Messages.DataClusterBrowserMainPage_64,
                        Messages.bind(Messages.DataClusterBrowserMainPage_85, lineItems.size()))) {
                    return;
                }

                // Instantiate the Monitor with actual deletes
                PhysicalDeleteItemsWithProgress diwp = new PhysicalDeleteItemsWithProgress(getXObject(), lineItems, this.shell);
                // run
                new ProgressMonitorDialog(this.shell).run(false, // fork
                        true, // cancelable
                        diwp);
                // refresh the search
                DataClusterBrowserMainPage.this.resultsViewer.setInput(getResults(false));

            } catch (Exception e) {
                log.error(e.getMessage(), e);
                MessageDialog.openError(shell, Messages._Error,
                        Messages.bind(Messages.DataClusterBrowserMainPage_88, e.getLocalizedMessage()));
            }
        }

        @Override
        public void runWithEvent(Event event) {
            super.runWithEvent(event);
        }

        // Progress Monitor that implements the actual delete
        class PhysicalDeleteItemsWithProgress implements IRunnableWithProgress {

            TreeObject xObject;

            Collection<LineItem> lineItems;

            Shell parentShell;

            public PhysicalDeleteItemsWithProgress(TreeObject object, Collection<LineItem> lineItems, Shell shell) {
                super();
                this.xObject = object;
                this.lineItems = lineItems;
                this.parentShell = shell;
            }

            private List<LineItem> orderItems(XtentisPort port) {

                WSDataClusterPK pk = (WSDataClusterPK) getXObject().getWsKey();
                WSGetConceptsInDataCluster param = new WSGetConceptsInDataCluster(pk);
                List<LineItem> orderItems = new LinkedList<LineItem>();
                try {
                    WSStringArray concepts = port.getConceptsInDataCluster(param);
                    if (concepts == null || concepts.getStrings() == null || concepts.getStrings().length == 0) {
                        orderItems.addAll(lineItems);
                    } else {
                        Map<String, List<LineItem>> orderMap = new LinkedHashMap<String, List<LineItem>>();
                        for (String concept : concepts.getStrings()) {
                            orderMap.put(concept, new LinkedList<LineItem>());
                        }
                        // order
                        List<LineItem> otherItems = new LinkedList<LineItem>();
                        for (LineItem lineItem : lineItems) {
                            String concept = lineItem.getConcept();
                            if (orderMap.containsKey(concept)) {
                                List<LineItem> items = orderMap.get(concept);
                                items.add(lineItem);
                            } else {
                                otherItems.add(lineItem);
                            }
                        }
                        // generate
                        for (List<LineItem> items : orderMap.values()) {
                            orderItems.addAll(0, items);
                        }
                        orderItems.addAll(otherItems);
                    }
                } catch (RemoteException e) {
                    log.error(e.getMessage(), e);
                }
                return orderItems;
            }

            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                try {
                    monitor.beginTask(Messages.DataClusterBrowserMainPage_89, lineItems.size());

                    XtentisPort port = Util.getPort(getXObject());
                    List<LineItem> orderItems = orderItems(port);
                    int i = 0;
                    for (LineItem lineItem : orderItems) {
                        String itemID = ((WSDataClusterPK) getXObject().getWsKey()).getPk() + "." + lineItem.getConcept() + "."//$NON-NLS-1$//$NON-NLS-2$
                                + Util.joinStrings(lineItem.getIds(), ".");//$NON-NLS-1$
                        monitor.subTask(Messages.bind(Messages.DataClusterBrowserMainPage_90, (i++), itemID));
                        if (monitor.isCanceled()) {
                            MessageDialog.openWarning(this.parentShell, Messages.DataClusterBrowserMainPage_92,
                                    Messages.bind(Messages.DataClusterBrowserMainPage_93, i));
                            return;
                        }
                        WSItemPK itempk = new WSItemPK((WSDataClusterPK) getXObject().getWsKey(), lineItem.getConcept(),
                                lineItem.getIds());
                        port.deleteItemWithReport(new WSDeleteItemWithReport(itempk,
                                "genericUI", "PHYSICAL_DELETE", null, getXObject().getUsername(), false, true, false));//$NON-NLS-1$ //$NON-NLS-2$
                        monitor.worked(1);
                    }// for

                    monitor.done();
                } catch (Exception e) {
                    String constraintMsg = getConstraintViolationExceptionMsg(e);
                    if (constraintMsg != null) {
                        MessageDialog.openError(shell, Messages.DataClusterBrowserMainPage_96,
                                Messages.bind(Messages.DataClusterBrowserMainPage_referedRecord, constraintMsg));
                    } else {
                        log.error(e.getMessage(), e);
                        if(!Util.handleConnectionException(shell, e, null)) {
                            MessageDialog.openError(shell, Messages.DataClusterBrowserMainPage_96,
                                Messages.bind(Messages.DataClusterBrowserMainPage_97, e.getLocalizedMessage()));
                        }
                    }
                }// try

            }// run

            private String getConstraintViolationExceptionMsg(Exception ex) {
                if (ex instanceof ServerException) {
                    String message = ex.getMessage().trim();
                    if (message.indexOf("org.hibernate.exception.ConstraintViolationException") > 0) { //$NON-NLS-1$
                        String prefix = ": com.amalto.core.util.XtentisException:"; //$NON-NLS-1$
                        int begin = message.indexOf(prefix);

                        if (begin > 0) {
                            return message.substring(0, begin);
                        }
                    }
                }
                return null;
            }
        }// class DeleteItemsWithProgress

    }// class DeletItemsAction

    /***************************************************************
     * New Item Action
     * 
     * @author bgrieder
     * 
     ***************************************************************/
    class NewItemAction extends Action {

        protected Shell shell = null;

        protected Viewer viewer;

        public NewItemAction(Shell shell, Viewer viewer) {
            super();
            this.shell = shell;
            this.viewer = viewer;
            setImageDescriptor(ImageCache.getImage("icons/add_obj.gif")); //$NON-NLS-1$
            setText(Messages.DataClusterBrowserMainPage_98);
            setToolTipText(Messages.DataClusterBrowserMainPage_99);
        }

        @Override
        public void run() {
            try {
                super.run();

                String xml = "<NewItem><NewElement></NewElement></NewItem>"; //$NON-NLS-1$

                WSDataModelPK[] dmPKs = Util.getPort(getXObject()).getDataModelPKs(new WSRegexDataModelPKs("*")) //$NON-NLS-1$
                        .getWsDataModelPKs();
                ArrayList<String> dataModels = new ArrayList<String>();
                if (dmPKs != null) {
                    for (int i = 0; i < dmPKs.length; i++) {
                        if (!"XMLSCHEMA---".equals(dmPKs[i].getPk())) { //$NON-NLS-1$
                            dataModels.add(dmPKs[i].getPk());
                        }
                    }
                }
                final XtentisPort port = Util.getPort(getXObject());
                final DataContainerDOMViewDialog d = new DataContainerDOMViewDialog(DataClusterBrowserMainPage.this.getSite()
                        .getShell(), port, Util.parse(xml), dataModels, DOMViewDialog.SOURCE_VIEWER, null);
                d.addListener(new Listener() {

                    public void handleEvent(Event event) {
                        if (event.button == DOMViewDialog.BUTTON_SAVE) {
                            // attempt to save
                            try {

                                WSPutItem putItem = new WSPutItem((WSDataClusterPK) getXObject().getWsKey(), d.getXML(),
                                        "".equals(d //$NON-NLS-1$
                                                .getDataModelName()) ? null : new WSDataModelPK(d.getDataModelName()), false);
                                WSPutItemWithReport item = new WSPutItemWithReport(putItem, "genericUI", d.isBeforeVerification());//$NON-NLS-1$
                                if (d.isTriggerProcess()) {
                                    port.putItemWithReport(item);
                                } else {
                                    port.putItem(putItem);
                                }
                                doSearch();
                            } catch (Exception e) {
                                log.error(e.getMessage(), e);
                                MessageDialog.openError(
                                        shell,
                                        Messages.DataClusterBrowserMainPage_100,
                                        Messages.bind(Messages.DataClusterBrowserMainPage_101,
                                                Util.formatErrorMessage(e.getLocalizedMessage())));
                                return;
                            }
                        }// if
                        d.close();
                    }// handleEvent
                });

                d.setBlockOnOpen(true);
                d.open();

            } catch (Exception e) {
                log.error(e.getMessage(), e);
                if(!Util.handleConnectionException(shell, e, null)) {
                    MessageDialog.openError(shell, Messages._Error,
                        Messages.bind(Messages.DataClusterBrowserMainPage_103, e.getLocalizedMessage()));
                }
            }
        }

        @Override
        public void runWithEvent(Event event) {
            super.runWithEvent(event);
        }

    }

    /***************************************************************
     * SubmitItems Action
     * 
     * @author bgrieder
     * 
     ***************************************************************/
    class SubmitItemsAction extends Action {

        protected Shell shell = null;

        protected Viewer viewer;

        public SubmitItemsAction(Shell shell, Viewer viewer) {
            super();
            this.shell = shell;
            this.viewer = viewer;
            setImageDescriptor(ImageCache.getImage("icons/execute.gif"));//$NON-NLS-1$
            IStructuredSelection selection = ((IStructuredSelection) viewer.getSelection());
            if (selection.size() == 1) {
                setText(Messages.DataClusterBrowserMainPage_104);
            } else {
                setText(Messages.bind(Messages.DataClusterBrowserMainPage_105, selection.size()));
            }
            setToolTipText(Messages.bind(Messages.DataClusterBrowserMainPage_107,
                    (selection.size() > 1 ? Messages.DataClusterBrowserMainPage_108 : "")));//$NON-NLS-1$
        }

        @Override
        public void run() {
            try {
                super.run();

                // retrieve the list of items
                IStructuredSelection selection = ((IStructuredSelection) viewer.getSelection());
                @SuppressWarnings("unchecked")
                List<LineItem> lineItems = selection.toList();

                if (lineItems.size() == 0) {
                    return;
                }

                if (!MessageDialog.openConfirm(this.shell, Messages.DataClusterBrowserMainPage_110, Messages.bind(
                        Messages.DataClusterBrowserMainPage_111, (lineItems.size() > 1 ? lineItems.size() + " " : "")))) { //$NON-NLS-1$ //$NON-NLS-2$
                    return;
                }

                // Instantiate the Monitor with actual deletes
                SubmitItemsWithProgress diwp = new SubmitItemsWithProgress(getXObject(), lineItems, this.shell);
                // run
                new ProgressMonitorDialog(this.shell).run(false, // fork
                        true, // cancelable
                        diwp);

            } catch (Exception e) {
                log.error(e.getMessage(), e);
                MessageDialog.openError(shell, Messages._Error,
                        Messages.bind(Messages.DataClusterBrowserMainPage_116, e.getLocalizedMessage()));
            }
        }

        @Override
        public void runWithEvent(Event event) {
            super.runWithEvent(event);
        }

        // Progress Monitor that implements the actual delete
        class SubmitItemsWithProgress implements IRunnableWithProgress {

            TreeObject xObject;

            Collection<LineItem> lineItems;

            Shell parentShell;

            public SubmitItemsWithProgress(TreeObject object, Collection<LineItem> lineItems, Shell shell) {
                super();
                this.xObject = object;
                this.lineItems = lineItems;
                this.parentShell = shell;
            }

            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                monitor.beginTask(Messages.DataClusterBrowserMainPage_117, lineItems.size());
                XtentisPort port = null;
                try {
                    port = Util.getPort(getXObject());
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    MessageDialog.openError(shell, Messages.DataClusterBrowserMainPage_118,
                            Messages.bind(Messages.DataClusterBrowserMainPage_119, e.getLocalizedMessage()));
                }// try

                int i = 0;
                for (LineItem lineItem : lineItems) {
                    String itemID = ((WSDataClusterPK) getXObject().getWsKey()).getPk() + "." + lineItem.getConcept() + "." //$NON-NLS-1$  //$NON-NLS-2$
                            + Util.joinStrings(lineItem.getIds(), Messages.DataClusterBrowserMainPage_120);
                    monitor.subTask(Messages.bind(Messages.DataClusterBrowserMainPage_121, (i++), itemID));
                    if (monitor.isCanceled()) {
                        MessageDialog.openWarning(this.parentShell, Messages.DataClusterBrowserMainPage_123,
                                Messages.bind(Messages.DataClusterBrowserMainPage_124, i));
                        return;
                    }
                    try {
                        port.routeItemV2(new WSRouteItemV2(new WSItemPK((WSDataClusterPK) getXObject().getWsKey(), lineItem
                                .getConcept(), lineItem.getIds())));
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        if(!Util.handleConnectionException(shell, e, null)) {
                            MessageDialog.openError(shell, Messages.DataClusterBrowserMainPage_127,
                                Messages.bind(Messages.DataClusterBrowserMainPage_128, itemID));
                        }
                    }// try
                    monitor.worked(1);
                }// for

                monitor.done();

            }// run
        }// class DeleteItemsWithProgress

    }// class DeletItemsAction

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

    // Modified by hbhong,to fix bug 21784
    @Override
    public Object getAdapter(Class adapter) {
        if (adapter == TreeParent.class) {
            TreeParent treeParent = Util.getServerTreeParent(getXObject());

            if (treeParent == null || treeParent.getChildren().length == 0) {
                TreeParent serverRoot = getRealTreeParent();
                if (serverRoot != null) {
                    treeParent = serverRoot;
                }
            }

            return treeParent;
        }
        return super.getAdapter(adapter);
    }

    // The ending| bug:21784

    private TreeParent getRealTreeParent() {
        TreeParent treeParent = null;
        TreeObject xObject = getXObject();
        if (xObject != null) {
            TreeParent serverRoot = xObject.getServerRoot();
            UserInfo user = serverRoot.getUser();

            String serverName = serverRoot.getName();
            String password = user.getPassword();
            String universe = user.getUniverse();
            String url = user.getServerUrl();
            String username = user.getUsername();

            final XtentisServerObjectsRetriever retriever = new XtentisServerObjectsRetriever(serverName, url, username,
                    password, universe, null);

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
}
