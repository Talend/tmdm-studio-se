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

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.ServerException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.ws.WebServiceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
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
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.FieldMetadata;
import org.talend.mdm.commmon.metadata.MetadataRepository;
import org.talend.mdm.commmon.metadata.SimpleTypeFieldMetadata;
import org.talend.mdm.commmon.metadata.TypeMetadata;
import org.talend.mdm.commmon.util.core.EUUIDCustomType;

import com.amalto.workbench.availablemodel.AvailableModelUtil;
import com.amalto.workbench.availablemodel.IAvailableModel;
import com.amalto.workbench.compare.CompareHeadInfo;
import com.amalto.workbench.compare.CompareManager;
import com.amalto.workbench.dialogs.DOMViewDialog;
import com.amalto.workbench.dialogs.datacontainer.AutoIncrementHelper;
import com.amalto.workbench.dialogs.datacontainer.DataContainerDOMViewDialog;
import com.amalto.workbench.dialogs.datacontainer.UpdateAutoIncrementDialog;
import com.amalto.workbench.editors.dialog.ConfirmFireEventMessageDialog;
import com.amalto.workbench.editors.dialog.ConfirmFireEventWithInputDialog;
import com.amalto.workbench.exadapter.ExAdapterManager;
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
import com.amalto.workbench.utils.XtentisException;
import com.amalto.workbench.webservices.TMDMService;
import com.amalto.workbench.webservices.WSBoolean;
import com.amalto.workbench.webservices.WSDataClusterPK;
import com.amalto.workbench.webservices.WSDataModel;
import com.amalto.workbench.webservices.WSDataModelPK;
import com.amalto.workbench.webservices.WSDeleteItemWithReport;
import com.amalto.workbench.webservices.WSExistsItem;
import com.amalto.workbench.webservices.WSGetConceptsInDataCluster;
import com.amalto.workbench.webservices.WSGetDataModel;
import com.amalto.workbench.webservices.WSGetItem;
import com.amalto.workbench.webservices.WSIsItemModifiedByOther;
import com.amalto.workbench.webservices.WSItem;
import com.amalto.workbench.webservices.WSItemPK;
import com.amalto.workbench.webservices.WSPutItem;
import com.amalto.workbench.webservices.WSPutItemWithReport;
import com.amalto.workbench.webservices.WSRegexDataModelPKs;
import com.amalto.workbench.webservices.WSRouteItemV2;
import com.amalto.workbench.webservices.WSStringArray;

public class DataClusterBrowserMainPage extends AMainPage implements IXObjectModelListener {

    private static final Log log = LogFactory.getLog(DataClusterBrowserMainPage.class);

    protected static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");//$NON-NLS-1$

    protected TableViewer resultsViewer;

    protected ListViewer wcListViewer;

    private DataClusterComposite clusterComp;

    protected IDataClusterBrowserMainPageExAdapter exAdapter;

    private boolean isMaster = true;

    public DataClusterBrowserMainPage(FormEditor editor) {
        super(editor, DataClusterBrowserMainPage.class.getName(), Messages.bind(
                Messages.DataClusterBrowserMainPage_masterDataContainerBrowserTitle,
                ((XObjectBrowserInput) editor.getEditorInput()).getName()));
        // listen to events
        ((XObjectBrowserInput) editor.getEditorInput()).addListener(this);
        initAdapter();
    }

    private void initAdapter() {
        exAdapter = ExAdapterManager.getAdapter(this, IDataClusterBrowserMainPageExAdapter.class);
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
            clusterComp = new DataClusterComposite(composite, SWT.NONE, getXObject(), isMaster(), this, getSite());
            clusterComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            resultsViewer = clusterComp.getResultsViewer();
            hookDoubleClick();
            hookKeyboard();
            hookContextMenu();
            hookToolBarItem();
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

    protected void hookDoubleClick() {
        resultsViewer.addDoubleClickListener(new IDoubleClickListener() {

            public void doubleClick(DoubleClickEvent event) {
                resultsViewer.setSelection(event.getSelection());
                try {
                    editItem();
                } catch (Exception e) {
                    MessageDialog.openError(getShell(), Messages._Error, Messages.bind(Messages.DataClusterBrowserMainPage_10, e
                            .getClass().getName(), e.getLocalizedMessage()));
                }
            }
        });
    }

    protected void editItem() {
        new EditItemAction(getShell(), resultsViewer).run();
    }

    protected void hookKeyboard() {
        resultsViewer.getControl().addKeyListener(new KeyListener() {

            public void keyPressed(KeyEvent e) {
            }

            public void keyReleased(KeyEvent e) {
                if (e.keyCode == SWT.DEL) {
                    new PhysicalDeleteItemsAction(getShell(), resultsViewer).run();
                }
            }
        });
    }

    protected void hookContextMenu() {
        MenuManager menuMgr = new MenuManager();
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {

            public void menuAboutToShow(IMenuManager manager) {
                // ViewBrowserMainPage.this.fillContextMenu(manager);
                manager.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
                manager.appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS, new NewItemAction(getShell(), resultsViewer));
                IStructuredSelection selection = ((IStructuredSelection) resultsViewer.getSelection());
                if (exAdapter != null) {
                    exAdapter.menuAboutToShow(manager, IWorkbenchActionConstants.MB_ADDITIONS, resultsViewer, selection,
                            getShell(), getXObject());
                }
                if (selection.size() == 1) {
                    manager.appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS, new EditItemAction(getShell(), resultsViewer));
                }
                if (selection.size() > 0) {
                    manager.appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS, new PhysicalDeleteItemsAction(getShell(),
                            resultsViewer));
                    manager.appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS, new LogicalDeleteItemsAction(getShell(),
                            resultsViewer));
                    manager.appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS,
                            new SubmitItemsAction(getShell(), resultsViewer));
                }
                // compare item with each other
                if (selection.size() == 2) {
                    manager.appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS, new CompareItemWithEachOtherAction(getShell(),
                            resultsViewer));
                }
                // available models
                if (selection.size() == 1) {
                    java.util.List<IAvailableModel> availablemodels = AvailableModelUtil.getAvailableModels();
                    for (IAvailableModel model : availablemodels) {
                        model.menuAboutToShow(manager, DataClusterBrowserMainPage.this);
                    }
                }
            }
        });
        Menu menu = menuMgr.createContextMenu(resultsViewer.getControl());
        resultsViewer.getControl().setMenu(menu);
        getSite().registerContextMenu(menuMgr, resultsViewer);
    }

    protected void hookToolBarItem() {
        FormEditor editor = getEditor();
        if (editor instanceof XObjectBrowser) {
            XObjectBrowser xobjectEditor = (XObjectBrowser) editor;
            // remove refresh action
            xobjectEditor.getToolBar().getToolBarManager().removeAll();
            //
            ActionContributionItem manageAutoIncrementItem = new ActionContributionItem(new ManageAutoIncrementAction());
            manageAutoIncrementItem.setMode(ActionContributionItem.MODE_FORCE_TEXT);
            xobjectEditor.getToolBar().addActions(manageAutoIncrementItem);
        }
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

    protected DataContainerDOMViewDialog getDomViewDialog(final TMDMService service, final WSItem wsItem, String xml,
            ArrayList<String> dataModels) throws Exception {

        DataContainerDOMViewDialog d = new DataContainerDOMViewDialog(getShell(), service, Util.parse(xml), dataModels,
                DOMViewDialog.TREE_VIEWER, wsItem.getDataModelName(), isMaster());
        return d;
    }

    /*
     * Empty string for 'Master' type data cluster,'#STAGING' for 'staging' type data cluster
     */
    protected String getPkAddition() {
        return ""; //$NON-NLS-1$
    }

    protected Shell getShell() {
        return getSite().getShell();
    }

    public boolean isMaster() {
        return isMaster;
    }

    public void setMaster(boolean isMaster) {
        this.isMaster = isMaster;
    }

    class ManageAutoIncrementAction extends Action {

        public ManageAutoIncrementAction() {
            setImageDescriptor(ImageCache.getImage(EImage.ADD_OBJ.getPath()));
            setText(Messages.DataClusterBrowserMainPage_ManageAutoIncrement);
        }

        @Override
        public void run() {
            UpdateAutoIncrementKeyCommand updateCommand = new UpdateAutoIncrementKeyCommand(getShell(), resultsViewer);
            updateCommand.execute();
        }
    }

    class UpdateAutoIncrementKeyCommand {

        protected Shell shell = null;

        protected Viewer viewer;

        private MetadataRepository repository;

        private String schema;

        public UpdateAutoIncrementKeyCommand(Shell shell, Viewer viewer) {
            this.shell = shell;
            this.viewer = viewer;
        }

        public void execute() {
            try {
                TreeObject xObject = getXObject();
                String dataContainer = getDataContainer(xObject);

                final TMDMService service = Util.getMDMService(xObject);
                List<String> entityToRevisions = getAllEntityInDataContainer(service, dataContainer);
                String conent = getAutoIncrementRecord(service);
                Map<String, String> entityToAutoIncrementValues = getEntityAutoIncrementValues(conent, dataContainer,
                        entityToRevisions);
                if (entityToAutoIncrementValues == null || entityToAutoIncrementValues.isEmpty()) {
                    MessageDialog.openInformation(shell, Messages.Warnning,
                            Messages.DataClusterBrowserMainPage_noAutoIncrementToManage);
                    return;
                }
                UpdateAutoIncrementDialog dialog = new UpdateAutoIncrementDialog(shell, entityToAutoIncrementValues);
                if (dialog.open() == IDialogConstants.OK_ID) {
                    String updatedContent = updateAutoIncrement(dataContainer, conent, entityToRevisions, dialog.getResults());

                    saveAutoIncrement(service, updatedContent);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                MessageDialog.openError(getSite().getShell(), Messages._Error, e.getLocalizedMessage());
            }
        }

        private String getDataContainer(TreeObject xObject) {
            String dataContainer = xObject.getName();
            FormEditor editor = getEditor();
            if (editor instanceof XObjectBrowser) {
                XObjectBrowser xobjectEditor = (XObjectBrowser) editor;
                IFormPage activeMainPage = xobjectEditor.getActivePageInstance();
                if (activeMainPage instanceof DataClusterBrowserMainPage) {
                    DataClusterBrowserMainPage mainPage = (DataClusterBrowserMainPage) activeMainPage;
                    String pkAddition = mainPage.getPkAddition();
                    dataContainer = dataContainer + pkAddition;
                }
            }
            return dataContainer;
        }

        private String getAutoIncrementRecord(final TMDMService service) {
            String record = emptyRecord();

            WSBoolean existsItem = service.existsItem(new WSExistsItem(new WSItemPK("AutoIncrement", //$NON-NLS-1$
                    Arrays.asList(new String[] { "AutoIncrement" }), new WSDataClusterPK("CONF")))); //$NON-NLS-1$ //$NON-NLS-2$
            if (existsItem.isTrue()) {

                WSItem wsItem = service.getItem(new WSGetItem(new WSItemPK("AutoIncrement", //$NON-NLS-1$
                        Arrays.asList(new String[] { "AutoIncrement" }), new WSDataClusterPK("CONF")))); //$NON-NLS-1$
                if (wsItem != null) {
                    record = wsItem.getContent();
                }
            }
            return record;
        }

        private Map<String, String> getEntityAutoIncrementValues(String content, String dataContainer,
                List<String> entityToRevisions) {
            Iterator<String> iterator = entityToRevisions.iterator();
            Map<String, String> entityToKeys = new HashMap<String, String>();
            while (iterator.hasNext()) {
                String entity = iterator.next();
                String fieldName = getAutoIncrementKeyFieldNames(entity);
                if (fieldName != null) {

                    String key = formKey(dataContainer, entity, fieldName);
                    entityToKeys.put(entity, key);
                }
            }
            Map<String, String> entityValues = null;
            try {
                entityValues = AutoIncrementHelper.getCurrentValue(content, entityToKeys);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            return entityValues;
        }

        private String updateAutoIncrement(String cluster, String content, List<String> entityToRevisions,
                Map<String, String> results) throws Exception {
            Map<String, String> keyvalues = new HashMap<String, String>();
            for (String concept : results.keySet()) {

                String fieldName = getAutoIncrementKeyFieldNames(concept);

                String key = formKey(cluster, concept, fieldName);
                String value = results.get(concept);
                if (value.isEmpty()) {
                    value = "0"; //$NON-NLS-1$
                }
                keyvalues.put(key, value);
            }
            String updatedValue = AutoIncrementHelper.updateValue(content, keyvalues);
            return updatedValue;
        }

        private String formKey(String cluster, String concept, String fieldName) {
            return cluster + "." + concept + "." + fieldName; //$NON-NLS-1$//$NON-NLS-2$
        }

        private List<String> getAllEntityInDataContainer(final TMDMService service, String dataContainer) {
            List<String> entityToRevisions = new ArrayList<String>();
            WSStringArray conceptArray = service.getConceptsInDataCluster(new WSGetConceptsInDataCluster(new WSDataClusterPK(
                    dataContainer)));
            if (conceptArray != null) {
                List<String> concepts = conceptArray.getStrings();
                for (String concept : concepts) {
                    entityToRevisions.add(concept);
                }
            }
            return entityToRevisions;
        }

        private String getAutoIncrementKeyFieldNames(String concept) {
            String fieldName = null;
            ComplexTypeMetadata complexType = getRepository().getComplexType(concept);
            if (complexType != null) {
                Collection<FieldMetadata> keyFields = complexType.getKeyFields();
                for (FieldMetadata fieldMeta : keyFields) {
                    SimpleTypeFieldMetadata simpleFieldMeta = (SimpleTypeFieldMetadata) fieldMeta;
                    TypeMetadata type = simpleFieldMeta.getType();
                    if (type.getName().equals(EUUIDCustomType.AUTO_INCREMENT.getName())) {
                        fieldName = simpleFieldMeta.getName();
                    }
                }
            }
            return fieldName;
        }

        private void saveAutoIncrement(TMDMService service, String content) {
            String pk = "CONF"; //$NON-NLS-1$

            WSPutItem putItem = new WSPutItem(false, new WSDataClusterPK(pk), new WSDataModelPK(pk), content);
            service.putItem(putItem);
        }

        private MetadataRepository getRepository() {
            if (getSchema() == null) {
                throw new IllegalStateException("Schema has not been set"); //$NON-NLS-1$
            }
            if (repository == null) {
                repository = new MetadataRepository();
                try {
                    repository.load(new ByteArrayInputStream(getSchema().getBytes("UTF-8"))); //$NON-NLS-1$
                } catch (UnsupportedEncodingException e) {
                    // ignore it
                }
            }
            return repository;
        }

        private String getSchema() {
            if (schema == null) {
                TreeObject xObject = getXObject();
                String dataContainer = xObject.getName();

                TMDMService service = null;
                try {

                    service = Util.getMDMService(xObject);
                } catch (XtentisException e) {
                }

                if (service != null) {
                    WSDataModel dataModel = service.getDataModel(new WSGetDataModel(new WSDataModelPK(dataContainer)));
                    schema = dataModel.getXsdSchema();
                }
            }
            return schema;
        }

        private String emptyRecord() {

            String emptyRecord = "<AutoIncrement>\n <id>AutoIncrement</id>\n</AutoIncrement>\n"; //$NON-NLS-1$
            return emptyRecord;
        }
    }

    public class EditItemAction extends Action {

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
                final TMDMService service = Util.getMDMService(getXObject());
                IStructuredSelection selection = ((IStructuredSelection) viewer.getSelection());
                LineItem li = (LineItem) selection.getFirstElement();
                if (li == null) {
                    return;
                }

                String pk = ((WSDataClusterPK) getXObject().getWsKey()).getPk();
                final WSDataClusterPK dataClusterPk = new WSDataClusterPK(pk + getPkAddition());
                final WSItem wsItem = service.getItem(new WSGetItem(new WSItemPK(li.getConcept().trim(), Arrays.asList(li
                        .getIds()), dataClusterPk)));
                String xml = Util.formatXsdSource(wsItem.getContent());

                List<WSDataModelPK> dmPKs = service.getDataModelPKs(new WSRegexDataModelPKs("*")).getWsDataModelPKs();//$NON-NLS-1$
                ArrayList<String> dataModels = new ArrayList<String>();
                if (dmPKs != null) {

                    for (WSDataModelPK mpk : dmPKs) {
                        if (!"XMLSCHEMA---".equals(mpk.getPk())) { //$NON-NLS-1$
                            dataModels.add(mpk.getPk());
                        }
                    }
                }

                final DataContainerDOMViewDialog d = getDomViewDialog(service, wsItem, xml, dataModels);
                d.addListener(new Listener() {

                    public void handleEvent(Event event) {
                        if (event.button == DOMViewDialog.BUTTON_SAVE) {
                            // attempt to save
                            try {
                                // check the item is modified by others?

                                boolean isModified = service.isItemModifiedByOther(new WSIsItemModifiedByOther(wsItem)).isTrue();
                                WSPutItem putItem = new WSPutItem(false, dataClusterPk, "".equals(d //$NON-NLS-1$
                                        .getDataModelName()) ? null : new WSDataModelPK(d.getDataModelName()), d.getXML());
                                WSPutItemWithReport itemWithReport = new WSPutItemWithReport(d.isBeforeVerification(),
                                        "genericUI", putItem);//$NON-NLS-1$
                                if (isModified) {
                                    if (MessageDialog.openConfirm(shell, Messages.DataClusterBrowserMainPage_31,
                                            Messages.DataClusterBrowserMainPage_32)) {
                                        if (d.isTriggerProcess()) {

                                            service.putItemWithReport(itemWithReport);
                                        } else {

                                            service.putItem(putItem);
                                        }
                                    }
                                } else {
                                    if (d.isTriggerProcess()) {

                                        service.putItemWithReport(itemWithReport);
                                    } else {

                                        service.putItem(putItem);
                                    }
                                }
                            } catch (Exception e) {
                                log.error(e.getMessage(), e);
                                if (!Util.handleConnectionException(shell, e, Messages.DataClusterBrowserMainPage_33)) {
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
                    MessageDialog.openWarning(shell, Messages.Warning, Messages.DataClusterBrowserMainPage_46);
                    return;
                }
                @SuppressWarnings("unchecked")
                List<LineItem> liList = selection.toList();
                LineItem leftLineItem = liList.get(0);
                LineItem rightLineItem = liList.get(1);

                String pk = ((WSDataClusterPK) getXObject().getWsKey()).getPk();
                WSDataClusterPK dataClusterPk = new WSDataClusterPK(pk + getPkAddition());
                // left

                WSItemPK leftWSItemPK = new WSItemPK(leftLineItem.getConcept().trim(), Arrays.asList(leftLineItem.getIds()),
                        dataClusterPk);
                WSItem leftWSItem = Util.getMDMService(getXObject()).getItem(new WSGetItem(leftWSItemPK));
                String leftItemXmlContent = leftWSItem.getContent();
                // right

                WSItemPK rightWSItemPK = new WSItemPK(rightLineItem.getConcept().trim(), Arrays.asList(rightLineItem.getIds()),
                        dataClusterPk);
                WSItem rightWSItem = Util.getMDMService(getXObject()).getItem(new WSGetItem(rightWSItemPK));
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
                ConfirmFireEventWithInputDialog id = new ConfirmFireEventWithInputDialog(this.shell,
                        Messages.DataClusterBrowserMainPage_64, Messages.bind(Messages.DataClusterBrowserMainPage_65,
                                lineItems.size()), Messages.DataClusterBrowserMainPage_67, new IInputValidator() {

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
                        id.isFireEvent(), id.isInvokeBeforeProcess(), id.getSource(), this.shell);
                // run
                new ProgressMonitorDialog(this.shell).run(false, // fork
                        true, // cancelable
                        diwp);
                // refresh the search
                resultsViewer.setInput(getResults(false));
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

            private boolean fireEvent;

            private boolean invokeBeforeProcess;

            private String source;

            public LogicalDeleteItemsWithProgress(TreeObject object, Collection<LineItem> lineItems, String partPath,
                    boolean fireEvent, boolean invokeBeforeProcess, String source, Shell shell) {
                super();
                this.xObject = object;
                this.lineItems = lineItems;
                this.partPath = partPath;
                this.fireEvent = fireEvent;
                this.invokeBeforeProcess = invokeBeforeProcess;
                this.source = source;
                this.parentShell = shell;
            }

            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                try {
                    monitor.beginTask(Messages.DataClusterBrowserMainPage_71, lineItems.size());

                    TMDMService service = Util.getMDMService(xObject);
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

                        WSItemPK itempk = new WSItemPK(lineItem.getConcept(), Arrays.asList(lineItem.getIds()),
                                (WSDataClusterPK) xObject.getWsKey());
                        if (source.isEmpty()) {
                            source = "genericUI"; //$NON-NLS-1$
                        }

                        service.deleteItemWithReport(new WSDeleteItemWithReport(invokeBeforeProcess,
                                "LOGIC_DELETE", false, fireEvent, source, partPath, getXObject().getUsername(), itempk));//$NON-NLS-1$
                        // port.dropItem(new WSDropItem(new WSItemPK((WSDataClusterPK) xObject.getWSKey(),
                        // lineItem.getConcept(),
                        // lineItem.getIds()), partPath));
                        monitor.worked(1);
                    }// for
                    monitor.done();
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    if (!Util.handleConnectionException(shell, e, null)) {
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
                ConfirmFireEventMessageDialog confirmDlg = ConfirmFireEventMessageDialog.createConfirmDialog(shell,
                        Messages.DataClusterBrowserMainPage_64,
                        Messages.bind(Messages.DataClusterBrowserMainPage_85, lineItems.size()));
                if (confirmDlg.open() != Dialog.OK) {
                    return;
                }
                // Instantiate the Monitor with actual deletes
                PhysicalDeleteItemsWithProgress diwp = new PhysicalDeleteItemsWithProgress(getXObject(), lineItems,
                        confirmDlg.isFireEvent(), confirmDlg.isInvokeBeforeProcess(), confirmDlg.getSource(), this.shell);
                // run
                new ProgressMonitorDialog(this.shell).run(false, // fork
                        true, // cancelable
                        diwp);
                // refresh the search
                resultsViewer.setInput(getResults(false));
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

            private boolean fireEvent;

            private boolean invokeBeforeProcess;

            private String source;

            public PhysicalDeleteItemsWithProgress(TreeObject object, Collection<LineItem> lineItems, boolean fireEvent,
                    boolean invokeBeforeProcess, String source, Shell shell) {
                super();
                this.xObject = object;
                this.lineItems = lineItems;
                this.fireEvent = fireEvent;
                this.invokeBeforeProcess = invokeBeforeProcess;
                this.source = source;
                this.parentShell = shell;
            }

            private List<LineItem> orderItems(TMDMService service) {
                WSDataClusterPK pk = (WSDataClusterPK) getXObject().getWsKey();
                WSGetConceptsInDataCluster param = new WSGetConceptsInDataCluster(pk);
                List<LineItem> orderItems = new LinkedList<LineItem>();
                try {

                    WSStringArray concepts = service.getConceptsInDataCluster(param);
                    if (concepts == null || concepts.getStrings() == null || concepts.getStrings().isEmpty()) {
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
                } catch (WebServiceException e) {
                    log.error(e.getMessage(), e);
                }
                return orderItems;
            }

            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                try {
                    monitor.beginTask(Messages.DataClusterBrowserMainPage_89, lineItems.size());

                    TMDMService service = Util.getMDMService(getXObject());
                    List<LineItem> orderItems = orderItems(service);
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

                        WSItemPK itempk = new WSItemPK(lineItem.getConcept(), Arrays.asList(lineItem.getIds()),
                                (WSDataClusterPK) getXObject().getWsKey());
                        if (source.isEmpty()) {
                            source = "genericUI"; //$NON-NLS-1$
                        }

                        service.deleteItemWithReport(new WSDeleteItemWithReport(invokeBeforeProcess,
                                "PHYSICAL_DELETE", false, fireEvent, source, null, getXObject().getUsername(), itempk));//$NON-NLS-1$
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
                        if (!Util.handleConnectionException(shell, e, null)) {
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
    public class NewItemAction extends Action {

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

                TMDMService service = Util.getMDMService(getXObject());
                String dataClusterPk = ((WSDataClusterPK) getXObject().getWsKey()).getPk();
                boolean created = NewItemHandler.getNewInstance().createItemRecord(service, shell,
                        new WSDataClusterPK(dataClusterPk + getPkAddition()), isMaster());
                if (created) {
                    doSearch();
                }
            } catch (XtentisException e) {
                log.error(e.getMessage(), e);
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

                TMDMService service = null;
                try {

                    service = Util.getMDMService(getXObject());
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    MessageDialog.openError(shell, Messages.DataClusterBrowserMainPage_118,
                            Messages.bind(Messages.DataClusterBrowserMainPage_119, e.getLocalizedMessage()));
                }// try

                if (service != null) {
                    int i = 0;
                    for (LineItem lineItem : lineItems) {

                        String itemID = ((WSDataClusterPK) getXObject().getWsKey()).getPk() + "." + lineItem.getConcept() + "." //$NON-NLS-1$ //$NON-NLS-2$
                                + Util.joinStrings(lineItem.getIds(), Messages.DataClusterBrowserMainPage_120);
                        monitor.subTask(Messages.bind(Messages.DataClusterBrowserMainPage_121, (i++), itemID));
                        if (monitor.isCanceled()) {
                            MessageDialog.openWarning(this.parentShell, Messages.DataClusterBrowserMainPage_123,
                                    Messages.bind(Messages.DataClusterBrowserMainPage_124, i));
                            return;
                        }

                        try {
                            service.routeItemV2(new WSRouteItemV2(new WSItemPK(lineItem.getConcept(), Arrays.asList(lineItem
                                    .getIds()), (WSDataClusterPK) getXObject().getWsKey())));
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                            if (!Util.handleConnectionException(shell, e, null)) {
                                MessageDialog.openError(shell, Messages.DataClusterBrowserMainPage_127,
                                        Messages.bind(Messages.DataClusterBrowserMainPage_128, itemID));
                            }
                        }// try
                        monitor.worked(1);
                    }// for
                }
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

}