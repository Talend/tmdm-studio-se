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
package com.amalto.workbench.export;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.exolab.castor.xml.Unmarshaller;
import org.talend.mdm.bulkload.client.BulkloadClient;
import org.talend.mdm.bulkload.client.BulkloadOptions;
import org.talend.mdm.bulkload.client.InputStreamMerger;
import org.talend.mdm.commmon.util.core.ICoreConstants;
import org.talend.mdm.commmon.util.workbench.ZipToFile;

import com.amalto.workbench.dialogs.ImportExchangeOptionsDialog;
import com.amalto.workbench.editors.XObjectBrowser;
import com.amalto.workbench.editors.XObjectEditor;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.models.TreeParent;
import com.amalto.workbench.providers.XObjectBrowserInput;
import com.amalto.workbench.providers.XObjectEditorInput;
import com.amalto.workbench.utils.EXtentisObjects;
import com.amalto.workbench.utils.LocalTreeObjectRepository;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.webservices.TMDMService;
import com.amalto.workbench.webservices.WSAutoIncrement;
import com.amalto.workbench.webservices.WSItem;
import com.amalto.workbench.widgets.FileSelectWidget;
import com.amalto.workbench.widgets.RepositoryCheckTreeViewer;
import com.amalto.workbench.widgets.WidgetFactory;

public class ImportItemsWizard extends Wizard {

    private static Log log = LogFactory.getLog(ImportItemsWizard.class);

    protected IStructuredSelection sel;

    protected RepositoryCheckTreeViewer treeViewer;

    protected String importFolder;

    protected StringBuffer zipFileRepository = new StringBuffer();;

    protected FileSelectWidget folder;

    private Button zipBtn;

    protected Button folderBtn;

    protected FileSelectWidget zip;

    // private List<TreeObject> objList =new ArrayList<TreeObject>();
    protected Hashtable<String, String[]> dataClusterContent = new Hashtable<String, String[]>();

    protected TreeParent serverRoot;

    private TMDMService service = null;

    protected Button btnOverwrite = null;

    protected boolean isOverride = false;

    public ImportItemsWizard(IStructuredSelection sel) {
        setNeedsProgressMonitor(true);
        this.sel = sel;
    }

    protected void refreshViewJob() {
        // new UIJob(Messages.ImportItemsWizard_0) {
        //
        // @Override
        // public IStatus runInUIThread(IProgressMonitor monitor) {
        // new ServerRefreshAction(view, serverRoot).run();
        // return Status.OK_STATUS;
        // }
        //
        // }.schedule();
    }

    protected Object[] getCheckedObjects() {
        return treeViewer.getCheckNodes();
    }

    @Override
    public boolean performFinish() {
        closeOpenEditors();
        final String zipFilePath = getZipFilePath();
        final boolean selectZip = zipBtn.getSelection();
        if (selectZip) {
            try {

                ZipToFile.unZipFile(zipFilePath, importFolder);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        if (folderBtn.getSelection()) {
            importFolder = folder.getText().getText();
        }

        final Object[] objs = getCheckedObjects();
        IRunnableWithProgress iRunnableWithProgress = new IRunnableWithProgress() {

            @Override
            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                try {
                    doImport(objs, monitor);
                    // run initMDM to call backend migration task
                    if (service != null) {
                        service.initMDM(null);
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                } finally {
                    refreshViewJob();
                    // modified by honghb ,fix bug 21552
                    if (selectZip && zipFilePath != null && new File(importFolder).exists()) {
                        ZipToFile.deleteDirectory(new File(importFolder));
                    }
                }
            }

        };
        try {
            new ProgressMonitorDialog(getShell()).run(true, true, iRunnableWithProgress);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return true;
    }

    private String getZipFilePath() {
        return zip.getText().getText();
    }

    private void closeOpenEditors() {
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        int length = page.getEditors().length;
        String tabEndpointAddress = Messages.ImportItemsWizard_3;
        String unserName = null;
        int j = 0;
        for (int i = 0; i < length; i++) {
            IEditorPart part = page.getEditors()[i - j];
            if (part instanceof XObjectBrowser) {
                TreeObject obj = (TreeObject) ((XObjectBrowserInput) part.getEditorInput()).getModel();
                if (obj != null) {
                    tabEndpointAddress = obj.getEndpointAddress();
                    unserName = obj.getUsername();
                }
            } else if (part instanceof XObjectEditor) {
                TreeObject obj = (TreeObject) ((XObjectEditorInput) part.getEditorInput()).getModel();
                if (obj != null) {
                    tabEndpointAddress = obj.getEndpointAddress();
                    unserName = obj.getUsername();
                }
            }
            if (serverRoot != null) {
                if (serverRoot.getEndpointAddress().equals(tabEndpointAddress)
                        && serverRoot.getUsername().equals(unserName)) {
                    if (part.isDirty() && isSaveModifiedEditor(part.getTitle())) {
                        part.doSave(new NullProgressMonitor());
                    }
                    page.closeEditor(part, false);
                    j++;
                }
            }
        }

    }

    private boolean isSaveModifiedEditor(String editorName) {
        final MessageDialog dialog = new MessageDialog(getShell(), Messages.ImportItemsWizard_4, null,
                Messages.ImportItemsWizard_5 + editorName + Messages.ImportItemsWizard_6, MessageDialog.QUESTION, new String[] {
                        IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL }, 0);
        dialog.open();
        if (dialog.getReturnCode() == 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean performCancel() {
        LocalTreeObjectRepository.getInstance().cancelMergeImportCategory(serverRoot);
        return super.performCancel();
    }

    private void checkUpExchangeImport(boolean check) {
        if (check) {
            if (!zipFileRepository.toString().equals("")) {//$NON-NLS-1$
                int pos = zipFileRepository.toString().lastIndexOf(File.separator);
                importFolder = zipFileRepository.toString().substring(0, pos);
            }
        } else {
            if (zipFileRepository.length() > 0) {
                zipFileRepository.delete(0, zipFileRepository.length());
            }
        }

    }

    protected void parse() {
        try {
            boolean importFromArchieve = zipBtn.getSelection();
            if (importFromArchieve) {
                importFolder = System.getProperty("java.io.tmpdir") + File.separator + "temp" + File.separator + "subfolder"//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
                        + System.currentTimeMillis();
            }
            if (folderBtn.getSelection()) {
                importFolder = folder.getText().getText();
            }
            getContainer().run(true, false, new ImportProcess(importFromArchieve, getZipFilePath()));
        } catch (InvocationTargetException e) {
            log.error(e.getMessage(), e);
        } catch (InterruptedException e) {
            log.debug(e.getMessage(), e);
        }
    }

    class ImportProcess implements IRunnableWithProgress {

        private final boolean importFromArchieve;

        private final String zipFilePath;

        public ImportProcess(boolean importFromArchieve, String zipFilePath) {
            this.importFromArchieve = importFromArchieve;
            this.zipFilePath = zipFilePath;

        }

        @Override
        public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
            parses(importFromArchieve, zipFilePath, monitor);
        }
    };

    protected void parses(boolean importFromArchieve, String zipFilePath, IProgressMonitor monitor) {
        // init var for progressMonitor
        int total = 500, zipCount = 200, readCount = 100;
        int step = 1, interval = 1;
        //
        monitor.beginTask(Messages.ImportItemsWizard_7, total);

        if (importFromArchieve) {
            checkUpExchangeImport(true);
            try {
                Util.unZipFile(zipFilePath, importFolder, zipCount, monitor);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        monitor.worked(zipCount);
        monitor.setTaskName(Messages.ImportItemsWizard_8);
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(new FileInputStream(importFolder + "/exportitems.xml"), "UTF-8");//$NON-NLS-1$//$NON-NLS-2$
            final Exports exports = (Exports) Unmarshaller.unmarshal(Exports.class, reader);
            String[] orgSchemas = exports.getSchemas();
            int idx = 0;

            if (orgSchemas != null) {
                for (String orgSchema : orgSchemas) {
                    String orgSchemaCpy = new String(orgSchema);
                    for (Map.Entry<String, String> pair : ICoreConstants.rolesConvert.oldRoleToNewRoleMap.entrySet()) {
                        orgSchemaCpy = orgSchemaCpy.replaceAll(pair.getKey().toString(), pair.getValue().toString());
                    }
                    if (!orgSchemaCpy.equals(orgSchema)) {
                        orgSchemas[idx] = orgSchemaCpy;
                    }
                    idx++;
                }
            }

            try {
                LocalTreeObjectRepository.getInstance().makeUpDocWithImportCategory(orgSchemas, serverRoot);
            } catch (Exception e) {
            }

            // import autoincrement
            if (exports.getAutoIncrement() != null) {
                try {
                    service.getAutoIncrement(new WSAutoIncrement(exports.getAutoIncrement()));
                } catch (Exception e) {
                }
            }
            // new server root
            final TreeParent reserverRoot = new TreeParent(serverRoot.getDisplayName(), null, TreeObject._SERVER_,
                    serverRoot.getWsKey(), serverRoot.getWsObject());
            reserverRoot.setUser(serverRoot.getUser());
            // serverRoot=reserverRoot;
            TreeParent clusters = new TreeParent(EXtentisObjects.DataCluster.getDisplayName(), reserverRoot,
                    TreeObject.DATA_CLUSTER, null, null);
            TreeParent models = new TreeParent(EXtentisObjects.DataMODEL.getDisplayName(), reserverRoot, TreeObject.DATA_MODEL,
                    null, null);
            TreeParent menus = new TreeParent(EXtentisObjects.Menu.getDisplayName(), reserverRoot, TreeObject.MENU, null, null);
            TreeParent roles = new TreeParent(EXtentisObjects.Role.getDisplayName(), reserverRoot, TreeObject.ROLE, null, null);
            TreeParent eventManager = new TreeParent(EXtentisObjects.EventManagement.getDisplayName(), reserverRoot,
                    TreeObject.EVENT_MANAGEMENT, null, null);
            TreeParent routingrules = new TreeParent(EXtentisObjects.RoutingRule.getDisplayName(), reserverRoot,
                    TreeObject.ROUTING_RULE, null, null);
            TreeParent storeprocedures = new TreeParent(EXtentisObjects.StoredProcedure.getDisplayName(), reserverRoot,
                    TreeObject.STORED_PROCEDURE, null, null);
            TreeParent transformers = new TreeParent(EXtentisObjects.Transformer.getDisplayName(), reserverRoot,
                    TreeObject.TRANSFORMER, null, null);
            TreeParent pictures = new TreeParent(EXtentisObjects.PICTURESRESOURCE.getDisplayName(), reserverRoot,
                    TreeObject.PICTURES_RESOURCE, null, null);
            // add by ymli
            TreeParent workflow = new TreeParent(EXtentisObjects.Workflow.getDisplayName(), reserverRoot, TreeObject.WORKFLOW,
                    null, null);
            TreeParent views = new TreeParent(EXtentisObjects.View.getDisplayName(), reserverRoot, TreeObject.VIEW, null, null);
            reserverRoot.addChild(clusters);
            reserverRoot.addChild(models);
            reserverRoot.addChild(menus);
            reserverRoot.addChild(roles);

            reserverRoot.addChild(storeprocedures);
            eventManager.addChild(transformers);
            eventManager.addChild(routingrules);
            reserverRoot.addChild(eventManager);
            reserverRoot.addChild(pictures);
            reserverRoot.addChild(workflow);
            reserverRoot.addChild(views);
            monitor.worked(readCount);
            // caculate step and interval
            float val = (total - zipCount - readCount) / exports.getItems().length;
            if (val > 0) {
                interval = 1;
                step = Math.round(val);
            } else {
                step = 1;
                interval = Math.round(exports.getItems().length / (total - zipCount - readCount) + 0.5f);
            }
            // System.out.println("count:" + exports.getItems().length + "\tinterval:" + interval + "\tstep:" + step);
            monitor.setTaskName(Messages.ImportItemsWizard_9);
            //
            int tmp = 1;
            for (TreeObject obj : exports.getItems()) {
                obj.setServerRoot(reserverRoot);
                switch (obj.getType()) {
                case TreeObject.DATA_CLUSTER:
                    clusters.addChild(obj);
                    break;
                case TreeObject.DATA_MODEL:
                    models.addChild(obj);
                    break;
                case TreeObject.MENU:
                    menus.addChild(obj);
                    break;
                case TreeObject.ROLE:
                    roles.addChild(obj);
                    break;
                case TreeObject.ROUTING_RULE:
                    routingrules.addChild(obj);
                    break;
                case TreeObject.STORED_PROCEDURE:
                    storeprocedures.addChild(obj);
                    break;
                case TreeObject.TRANSFORMER:
                    transformers.addChild(obj);
                    break;
                case TreeObject.PICTURES_RESOURCE:
                    pictures.addChild(obj);
                    break;
                case TreeObject.WORKFLOW_PROCESS:
                    workflow.addChild(obj);
                    break;
                case TreeObject.VIEW:
                    views.addChild(obj);
                    break;
                default:
                    if (obj.getItems() != null && obj.getItems().length > 0) {
                        for (int i = 0; i < obj.getItems().length; i++) {
                            if (obj.getItems()[i].split(Messages.ImportItemsWizard_10)[1] != null) {
                                dataClusterContent.put(obj.getItems()[i].split(Messages.ImportItemsWizard_11)[1], obj.getItems());
                            }
                        }
                    }
                }
                // update monitor
                if (interval == 1) {
                    monitor.worked(step);
                } else {
                    if (tmp >= interval) {
                        monitor.worked(step);
                        tmp = 1;
                    } else {
                        tmp++;
                    }
                }
            }
            Display.getDefault().syncExec(new Runnable() {

                @Override
                public void run() {
                    treeViewer.setRoot(reserverRoot);
                    treeViewer.getViewer().setInput(null);
                    treeViewer.setCheckItems(Arrays.asList(exports.getItems()));
                    GridData gd = (GridData) treeViewer.getViewer().getControl().getLayoutData();
                    gd.heightHint = 300;
                    treeViewer.getViewer().getControl().getParent().layout(true);
                    treeViewer.getViewer().getControl().getShell().layout(true);
                    try {
                        Object[] checkNodes = treeViewer.getCheckNodes();
                        if (checkNodes != null) {
                            LocalTreeObjectRepository.getInstance().setOriginalXobjectsToImport((TreeObject[]) checkNodes);
                        }
                    } catch (Exception e) {
                    }
                }
            });

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
            }
            monitor.done();
        }

    }

    private int isOveride(String name, String obTypeName) {

        final MessageDialog dialog = new MessageDialog(
                getShell(),
                Messages.ImportItemsWizard_12,
                null,
                Messages.ImportItemsWizard_13 + obTypeName + Messages.ImportItemsWizard_14 + name + Messages.ImportItemsWizard_15,
                MessageDialog.QUESTION, new String[] { IDialogConstants.YES_LABEL, IDialogConstants.YES_TO_ALL_LABEL,
                        IDialogConstants.NO_LABEL, IDialogConstants.CANCEL_LABEL }, 0);
        dialog.open();
        int result = dialog.getReturnCode();
        if (result == 0) {
            return IDialogConstants.YES_ID;
        }
        if (result == 1) {
            return IDialogConstants.YES_TO_ALL_ID;
        }
        if (result == 2) {
            return IDialogConstants.NO_ID;
        }
        return IDialogConstants.CANCEL_ID;

    }

    public void doImport(Object[] selectedObjs, IProgressMonitor monitor) {
        // empty
    }

    protected void importClusterContents(TreeObject item, TMDMService port, HashMap<String, String> picturePathMap)
            throws Exception {
        if (dataClusterContent.containsKey(item.getDisplayName())) {
            Reader reader = null;
            String[] paths = dataClusterContent.get(item.getDisplayName());
            Map<String, List<String>> conceptMap = new HashMap<String, List<String>>();
            for (String path : paths) {
                try {
                    reader = new InputStreamReader(new FileInputStream(importFolder + "/" + path), "UTF-8");//$NON-NLS-1$//$NON-NLS-2$
                    WSItem wsItem = (WSItem) Unmarshaller.unmarshal(WSItem.class, reader);
                    String key = wsItem.getWsDataClusterPK().getPk() + "##" + wsItem.getConceptName() + "##"//$NON-NLS-1$//$NON-NLS-2$
                            + wsItem.getDataModelName();
                    List<String> list = null;
                    list = conceptMap.get(key);
                    if (list == null) {
                        list = new ArrayList<String>();
                        conceptMap.put(key, list);
                    }
                    // ymli; fix 0016875: set the right path of picture when import pictures
                    String content = wsItem.getContent();
                    boolean isUpdate = false;
                    for (String picturekey : picturePathMap.keySet()) {
                        String fileName = picturekey.substring(picturekey.indexOf("-") + 1);//$NON-NLS-1$
                        if (content.contains(fileName)) {
                            String targetPicturePath = picturePathMap.get(picturekey);
                            content = updatePicturePath(content, picturekey, targetPicturePath);
                            isUpdate = true;
                            list.add(content);
                            conceptMap.put(key, list);
                        }

                    }
                    if (!isUpdate) {
                        list.add(content);
                    }

                } catch (Exception e1) {
                    log.error(e1.getMessage(), e1);
                } finally {
                    try {
                        if (reader != null) {
                            reader.close();
                        }
                    } catch (Exception e) {
                    }
                }
            }
            // store the items to db using bulkloadclient
            String url = item.getEndpointIpAddress() + "/datamanager/loadServlet";//$NON-NLS-1$
            for (Entry<String, List<String>> entry : conceptMap.entrySet()) {
                String[] keys = entry.getKey().split("##");//$NON-NLS-1$
                String cluster = keys[0];
                String concept = keys[1];
                String datamodel = keys[2];
                BulkloadClient bulkloadClient = new BulkloadClient(url, item.getUsername(), item.getPassword(), cluster,
                        concept, datamodel);
                bulkloadClient.setOptions(new BulkloadOptions(false, false, 500));

                StringBuffer sb = new StringBuffer();
                for (String xml : entry.getValue()) {
                    sb.append(xml).append("\n"); //$NON-NLS-1$
                }
                try {
                    InputStreamMerger manager = bulkloadClient.load();
                    InputStream bin = new ByteArrayInputStream(sb.toString().getBytes(Messages.ImportItemsWizard_29));
                    manager.push(bin);
                    // bulkloadClient.load(sb.toString());
                    manager.close();
                } catch (Exception e) {
                    // MessageDialog.openWarning(null, "Warning", "Importing  Entity: "+ concept+
                    // " in Data Container: "+cluster + " Error --> "+e.getLocalizedMessage());
                    throw new Exception(Messages.ImportItemsWizard_30 + concept + Messages.ImportItemsWizard_31 + cluster
                            + Messages.ImportItemsWizard_32 + e.getLocalizedMessage());
                }
            }
        }
    }

    @Override
    public void addPages() {
        addPage(new SelectItemsPage());
    }

    protected void createOverwriteBtn(Composite composite) {
    }

    class PageListener implements Listener {

        SelectItemsPage page;

        PageListener(SelectItemsPage page) {
            this.page = page;
        }

        @Override
        public void handleEvent(Event event) {
            page.checkCompleted();
            parse();
        }
    };

    public void checkCompleted() {

    }

    class SelectItemsPage extends WizardPage {

        protected SelectItemsPage() {
            super(Messages.ImportItemsWizard_33);
            setTitle(Messages.ImportItemsWizard_34);

            // Page isn't complete until an e-mail address has been added
            setPageComplete(false);

        }

        public void checkCompleted() {
            if (folderBtn.getSelection() && folder.getText().getText().length() > 0
                    && new File(folder.getText().getText()).exists()) {
                setPageComplete(true);
            }
            if (zipBtn.getSelection() && zip.getText().getText().length() > 0
                    && new File(zip.getText().getText()).getParentFile().exists()) {
                setPageComplete(true);
            }
        }

        @Override
        public void createControl(Composite parent) {
            Composite composite = new Composite(parent, SWT.BORDER);
            composite.setLayout(new GridLayout(4, false));
            composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
            setControl(composite);

            folderBtn = new Button(composite, SWT.RADIO);
            folderBtn.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
            folderBtn.setText(Messages.ImportItemsWizard_35);
            folder = new FileSelectWidget(composite, "", new String[] { "*.*" }, "", false);//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
            // folder.getCmp().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
            // false, 1, 1));
            folderBtn.addSelectionListener(new SelectionListener() {

                @Override
                public void widgetDefaultSelected(SelectionEvent e) {

                }

                @Override
                public void widgetSelected(SelectionEvent e) {
                    folder.setEnabled(folderBtn.getSelection());
                    checkCompleted();
                }
            });

            Composite padding = new Composite(composite, SWT.NONE);
            padding.setLayout(new GridLayout(1, false));
            padding.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

            zipBtn = new Button(composite, SWT.RADIO);
            zipBtn.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
            zipBtn.setText(Messages.ImportItemsWizard_36);
            zip = new FileSelectWidget(composite, "", new String[] { "*.zip", "*.tar" }, "", true);//$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
            // zip.getCmp().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
            // false, 1, 1));
            ((GridData) zip.getText().getLayoutData()).widthHint = 200;

            zip.getText().addListener(SWT.Modify, new PageListener(this));

            final Button exchangeBtn = new Button(composite, SWT.PUSH);
            exchangeBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, false, 1, 1));
            exchangeBtn.setText(Messages.ImportItemsWizard_37);
            exchangeBtn.setEnabled(false);
            exchangeBtn.addSelectionListener(new SelectionListener() {

                @Override
                public void widgetDefaultSelected(SelectionEvent e) {
                }

                @Override
                public void widgetSelected(SelectionEvent e) {
                    exchangeImport();
                }

            });

            zipBtn.addSelectionListener(new SelectionListener() {

                @Override
                public void widgetDefaultSelected(SelectionEvent e) {

                }

                @Override
                public void widgetSelected(SelectionEvent e) {
                    zip.setEnabled(zipBtn.getSelection());
                    exchangeBtn.setEnabled(zipBtn.getSelection());
                    checkUpExchangeImport(false);
                    checkCompleted();
                }
            });
            // zip.getButton().addListener(SWT.Selection, new PageListener(this));
            folder.getText().addListener(SWT.Modify, new PageListener(this));
            // folder.getButton().addListener(SWT.Selection, new PageListener(this));
            // create viewer
            createViewer();
            Composite itemcom = initItemTreeViewer(composite);

            itemcom.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 5));
            folder.setEnabled(folderBtn.getSelection());
            zip.setEnabled(zipBtn.getSelection());

            createOverwriteBtn(composite);

            GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).hint(920, 600).applyTo(composite);
        }

        protected void refreshTree() {

        }

    }

    protected void exchangeImport() {
        ImportExchangeOptionsDialog dlg = getExchangeOptionsDialog();
        dlg.setBlockOnOpen(true);
        if (dlg.open() == Window.OK) {
            zip.getText().setText(zipFileRepository.toString());
            parse();
            checkUpExchangeImport(false);
        }
    }

    protected Composite initItemTreeViewer(Composite composite) {
        Composite returnComposite = treeViewer.createItemList(composite);
        treeViewer.getViewer().setInput(null);
        treeViewer.setItemText(Messages.ImportItemsWizard_38);
        return returnComposite;
    }

    /**
     * @author ymli edit the path of picture fix 0016875
     * @param orignalPicturepath
     * @param targetPicturePath
     * @return
     */
    private static String updatePicturePath(String orignalPicturepath, String pictureName, String targetPicturePath) {
        String pictureName1 = pictureName.replace("-", "/");//$NON-NLS-1$//$NON-NLS-2$
        return orignalPicturepath.replaceAll(pictureName1, targetPicturePath.substring(targetPicturePath.indexOf("/") + 1));//$NON-NLS-1$
    }

    protected void createViewer() {
        treeViewer = new RepositoryCheckTreeViewer(sel);
    }

    protected ImportExchangeOptionsDialog getExchangeOptionsDialog() {
        FormToolkit toolkit = WidgetFactory.getWidgetFactory();
        ImportExchangeOptionsDialog dlg = new ImportExchangeOptionsDialog(zipBtn.getShell(), toolkit, true, zipFileRepository);
        dlg.create();
        dlg.fillInTable();

        return dlg;
    }
}
