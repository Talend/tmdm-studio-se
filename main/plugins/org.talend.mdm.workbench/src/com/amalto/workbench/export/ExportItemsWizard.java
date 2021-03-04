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
package com.amalto.workbench.export;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.exolab.castor.xml.Marshaller;
import org.talend.mdm.commmon.util.workbench.ZipToFile;

import com.amalto.workbench.exadapter.ExAdapterManager;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.utils.HttpClientUtil;
import com.amalto.workbench.utils.LocalTreeObjectRepository;
import com.amalto.workbench.utils.ResourcesUtil;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.webservices.TMDMService;
import com.amalto.workbench.webservices.WSAutoIncrement;
import com.amalto.workbench.webservices.WSDataCluster;
import com.amalto.workbench.webservices.WSDataClusterPK;
import com.amalto.workbench.webservices.WSDataModel;
import com.amalto.workbench.webservices.WSDataModelPK;
import com.amalto.workbench.webservices.WSGetDataCluster;
import com.amalto.workbench.webservices.WSGetDataModel;
import com.amalto.workbench.webservices.WSGetItem;
import com.amalto.workbench.webservices.WSGetItemPKsByCriteria;
import com.amalto.workbench.webservices.WSGetMenu;
import com.amalto.workbench.webservices.WSGetRoutingRule;
import com.amalto.workbench.webservices.WSGetStoredProcedure;
import com.amalto.workbench.webservices.WSGetTransformerV2;
import com.amalto.workbench.webservices.WSGetView;
import com.amalto.workbench.webservices.WSItem;
import com.amalto.workbench.webservices.WSItemPKsByCriteriaResponseResults;
import com.amalto.workbench.webservices.WSMenu;
import com.amalto.workbench.webservices.WSMenuPK;
import com.amalto.workbench.webservices.WSRoutingRule;
import com.amalto.workbench.webservices.WSRoutingRulePK;
import com.amalto.workbench.webservices.WSStoredProcedure;
import com.amalto.workbench.webservices.WSStoredProcedurePK;
import com.amalto.workbench.webservices.WSTransformerV2;
import com.amalto.workbench.webservices.WSTransformerV2PK;
import com.amalto.workbench.webservices.WSView;
import com.amalto.workbench.webservices.WSViewPK;
import com.amalto.workbench.widgets.FileSelectWidget;
import com.amalto.workbench.widgets.RepositoryCheckTreeViewer;

public class ExportItemsWizard extends Wizard {

    private static Log log = LogFactory.getLog(ExportItemsWizard.class);

    protected IStructuredSelection sel;

    protected RepositoryCheckTreeViewer treeViewer;

    protected String exportFolder;

    protected FileSelectWidget folder;

    protected Button zipBtn;

    protected Button folderBtn;

    protected FileSelectWidget zip;

    protected String zipfile;

    public ExportItemsWizard(IStructuredSelection sel) {
        this.sel = sel;
    }

    protected Object[] getCheckedObjects() {
        return treeViewer.getCheckNodes();
    }

    @Override
    public boolean performFinish() {

        if (zipBtn.getSelection()) {
            // fix bug 0016873:clear the temp directory before zip the exported items.
            File tempFile = new File(System.getProperty("java.io.tmpdir") + "/temp/subfolder" + System.currentTimeMillis());//$NON-NLS-1$//$NON-NLS-2$
            if (tempFile.exists()) {
                File[] tempFiles = tempFile.listFiles();
                for (File file : tempFiles) {
                    file.delete();
                }
            }
            exportFolder = tempFile.getAbsolutePath();
            zipfile = zip.getText().getText();
            if (zipfile != null && new File(zipfile).exists()) {
                if (!MessageDialog.openConfirm(null, Messages.Warning,
                        Messages.bind(Messages.ExportItemsWizard_overridefile, zipfile))) {
                    return false;
                }
            }
        }
        if (folderBtn.getSelection()) {
            exportFolder = folder.getText().getText();
            if (exportFolder != null && new File(exportFolder).list().length > 0) {
                if (!MessageDialog.openConfirm(null, Messages.Warning,
                        Messages.bind(Messages.ExportItemsWizard_overridefolder, exportFolder))) {
                    return false;
                }
            }
        }

        final Object[] objs = getCheckedObjects();
        Job job = new Job(Messages.ExportItemsWizard_Export) {

            @Override
            public IStatus run(IProgressMonitor monitor) {
                try {
                    doexport(objs, monitor);
                    return Status.OK_STATUS;
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    return Status.CANCEL_STATUS;
                } finally {
                    if (zipfile != null && zipfile.length() > 0 && new File(exportFolder).exists()) {
                        try {
                            ZipToFile.zipFile(exportFolder, zipfile);
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }
                        ZipToFile.deleteDirectory(new File(exportFolder));
                    }
                }
            }
        };
        job.setPriority(Job.INTERACTIVE);
        job.schedule();
        return true;
    }

    protected void createViewer() {
        treeViewer = new RepositoryCheckTreeViewer(sel);
    }

    public void doexport(Object[] selectedObjs, IProgressMonitor monitor) {
        TreeObject[] objs = null;
        if (selectedObjs.length > 0 && selectedObjs[0] instanceof TreeObject) {
            objs = Arrays.asList(selectedObjs).toArray(new TreeObject[0]);
        }
        if (objs == null || objs.length == 0) {
            return;
        }
        monitor.beginTask(Messages.ExportItemsWizard_Export, IProgressMonitor.UNKNOWN);
        Exports eps = new Exports();
        List<TreeObject> exports = new ArrayList<TreeObject>();
        TMDMService service;
        try {
            service = Util.getMDMService(objs[0]);
            try {
                LocalTreeObjectRepository.getInstance().parseElementForOutput(objs);
            } catch (Exception e) {
            }
            for (TreeObject obj : objs) {

                StringWriter sw;
                ArrayList<String> items;
                String encodedID = null;
                switch (obj.getType()) {

                case TreeObject.DATA_CLUSTER:
                    monitor.subTask(Messages.ExportItemsWizard_2);

                    items = new ArrayList<String>();
                    // dataclusters

                    WSDataClusterPK pk = (WSDataClusterPK) obj.getWsKey();

                    try {
                        WSDataCluster cluster = service.getDataCluster(new WSGetDataCluster(pk));
                        // Marshal
                        sw = new StringWriter();
                        Marshaller.marshal(cluster, sw);
                        encodedID = URLEncoder.encode(cluster.getName(), "UTF-8");//$NON-NLS-1$
                        writeString(sw.toString(), TreeObject.DATACONTAINER + "/" + encodedID);//$NON-NLS-1$
                        items.add(TreeObject.DATACONTAINER + "/" + encodedID);//$NON-NLS-1$

                        obj.setItems(items.toArray(new String[items.size()]));
                        exports.add(obj);
                    } catch (Exception e) {
                    }
                    monitor.worked(1);
                    // datacluster contents

                    monitor.subTask(Messages.bind(Messages.ExportItemsWizard_3, pk.getPk()));

                    exportCluster(exports, pk, service);

                    monitor.worked(1);

                    break;

                case TreeObject.DATA_MODEL:
                    monitor.subTask(Messages.ExportItemsWizard_5);

                    items = new ArrayList<String>();
                    // datamodels
                    try {
                        WSDataModel model = service.getDataModel(new WSGetDataModel((WSDataModelPK) obj.getWsKey()));
                        sw = new StringWriter();
                        Marshaller.marshal(model, sw);
                        encodedID = URLEncoder.encode(model.getName(), "UTF-8");//$NON-NLS-1$
                        writeString(sw.toString(), TreeObject.DATAMODEL_ + "/" + encodedID);//$NON-NLS-1$
                        items.add(TreeObject.DATAMODEL_ + "/" + encodedID);//$NON-NLS-1$
                        obj.setItems(items.toArray(new String[items.size()]));
                        exports.add(obj);
                    } catch (Exception e) {
                    }
                    monitor.worked(1);
                    break;
                case TreeObject.MENU:
                    monitor.subTask(Messages.ExportItemsWizard_6);
                    // ExportItem exportItem=new ExportItem();

                    items = new ArrayList<String>();
                    // menu
                    try {
                        WSMenu menu = service.getMenu(new WSGetMenu((WSMenuPK) obj.getWsKey()));
                        // Marshal
                        sw = new StringWriter();
                        Marshaller.marshal(menu, sw);
                        encodedID = URLEncoder.encode(menu.getName(), "UTF-8");//$NON-NLS-1$
                        writeString(sw.toString(), TreeObject.MENU_ + "/" + encodedID);//$NON-NLS-1$
                        items.add(TreeObject.MENU_ + "/" + encodedID);//$NON-NLS-1$

                        obj.setItems(items.toArray(new String[items.size()]));
                        exports.add(obj);
                    } catch (Exception e) {
                    }
                    monitor.worked(1);
                    break;
                case TreeObject.PICTURES_RESOURCE:
                    monitor.subTask(Messages.ExportItemsWizard_7);
                    // ExportItem exportItem=new ExportItem();

                    items = new ArrayList<String>();
                    // picture
                    try {
                        String endpointIpAddress = obj.getEndpointIpAddress();
                        int index = endpointIpAddress.indexOf("/services/soap"); //$NON-NLS-1$
                        if (index != -1) {
                            endpointIpAddress = endpointIpAddress.substring(0, index);
                        }
                        String picUrl = endpointIpAddress
                                + ResourcesUtil.getResourcesMapFromURI(endpointIpAddress + TreeObject.PICTURES_URI, objs[0]).get(
                                        obj.getDisplayName());
                        // Marshal
                        sw = new StringWriter();
                        byte[] content = HttpClientUtil.getByteArrayContentByHttpget(picUrl);
                        Marshaller.marshal(content, sw);
                        encodedID = URLEncoder.encode(obj.getDisplayName(), "UTF-8");//$NON-NLS-1$
                        writeInputStream(new ByteArrayInputStream(content), TreeObject.PICTURES_ + "/" + encodedID); //$NON-NLS-1$
                        items.add(TreeObject.PICTURES_ + "/" + encodedID);//$NON-NLS-1$

                        obj.setItems(items.toArray(new String[items.size()]));
                        exports.add(obj);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                    monitor.worked(1);
                    break;

                case TreeObject.ROUTING_RULE:
                    monitor.subTask(Messages.ExportItemsWizard_10);
                    // ExportItem exportItem=new ExportItem();

                    items = new ArrayList<String>();
                    // routing rule
                    try {
                        WSRoutingRule RoutingRule = service
                                .getRoutingRule(new WSGetRoutingRule((WSRoutingRulePK) obj.getWsKey()));
                        // Marshal
                        sw = new StringWriter();
                        Marshaller.marshal(RoutingRule, sw);
                        encodedID = URLEncoder.encode(RoutingRule.getName(), "UTF-8");//$NON-NLS-1$
                        writeString(sw.toString(), TreeObject.ROUTINGRULE_ + "/" + encodedID);//$NON-NLS-1$
                        items.add(TreeObject.ROUTINGRULE_ + "/" + encodedID);//$NON-NLS-1$
                        obj.setItems(items.toArray(new String[items.size()]));
                        exports.add(obj);
                    } catch (Exception e) {
                    }
                    monitor.worked(1);
                    break;
                case TreeObject.STORED_PROCEDURE:
                    monitor.subTask(Messages.ExportItemsWizard_11);

                    items = new ArrayList<String>();
                    // stored procedure
                    try {
                        WSStoredProcedure StoredProcedure = service.getStoredProcedure(new WSGetStoredProcedure(
                                (WSStoredProcedurePK) obj.getWsKey()));
                        // Marshal
                        sw = new StringWriter();
                        Marshaller.marshal(StoredProcedure, sw);
                        encodedID = URLEncoder.encode(StoredProcedure.getName(), "UTF-8");//$NON-NLS-1$
                        writeString(sw.toString(), TreeObject.STOREDPROCEDURE_ + "/" + encodedID);//$NON-NLS-1$
                        items.add(TreeObject.STOREDPROCEDURE_ + "/" + encodedID);//$NON-NLS-1$

                        obj.setItems(items.toArray(new String[items.size()]));
                        exports.add(obj);
                    } catch (Exception e) {
                    }
                    monitor.worked(1);
                    break;

                case TreeObject.TRANSFORMER:
                    monitor.subTask(Messages.ExportItemsWizard_13);

                    items = new ArrayList<String>();
                    // transformer
                    // TODO:check the pk
                    try {
                        WSTransformerV2 transformer = service.getTransformerV2(new WSGetTransformerV2(new WSTransformerV2PK(
                                ((WSTransformerV2PK) obj.getWsKey()).getPk())));
                        // Marshal
                        sw = new StringWriter();
                        Marshaller.marshal(transformer, sw);
                        encodedID = URLEncoder.encode(transformer.getName(), "UTF-8");//$NON-NLS-1$
                        writeString(sw.toString(), TreeObject.TRANSFORMER_ + "/" + encodedID);//$NON-NLS-1$
                        items.add(TreeObject.TRANSFORMER_ + "/" + encodedID);//$NON-NLS-1$

                        obj.setItems(items.toArray(new String[items.size()]));
                        exports.add(obj);
                    } catch (Exception e) {
                    }
                    monitor.worked(1);
                    break;

                case TreeObject.VIEW:
                    monitor.subTask(Messages.ExportItemsWizard_15);

                    items = new ArrayList<String>();
                    // view
                    try {
                        WSView View = service.getView(new WSGetView((WSViewPK) obj.getWsKey()));
                        // Marshal
                        sw = new StringWriter();
                        Marshaller.marshal(View, sw);
                        encodedID = URLEncoder.encode(View.getName(), "UTF-8");//$NON-NLS-1$
                        writeString(sw.toString(), TreeObject.VIEW_ + "/" + encodedID);//$NON-NLS-1$
                        items.add(TreeObject.VIEW_ + "/" + encodedID);//$NON-NLS-1$

                        obj.setItems(items.toArray(new String[items.size()]));
                        exports.add(obj);
                    } catch (Exception e) {
                    }
                    monitor.worked(1);
                    break;

                default:
                    IExportItemsWizardAdapter exAdapter = ExAdapterManager.getAdapter(this, IExportItemsWizardAdapter.class);
                    if (exAdapter != null) {
                        exAdapter.doexport(service, obj.getType(), exports, obj, monitor);
                    }
                    break;
                }

            }
            // store the content xml
            eps.setItems(exports.toArray(new TreeObject[exports.size()]));
            eps.setSchemas(LocalTreeObjectRepository.getInstance().outPutSchemas());
            // export autoincrement
            try {
                WSAutoIncrement auto = service.getAutoIncrement(null);
                if (auto != null && auto.getAutoincrement() != null) {
                    eps.setAutoIncrement(auto.getAutoincrement());
                }
            } catch (Exception e) {
            }
            StringWriter sw = new StringWriter();
            try {
                Marshaller.marshal(eps, sw);
                writeString(sw.toString(), "exportitems.xml");//$NON-NLS-1$
            } catch (Exception e) {
            }
            monitor.done();
        } catch (Exception e) {

        }
    }

    // add by ymli;fix the bug:0012771
    public void writeInputStream(InputStream inputSteam, String filename) {
        try {
            File f = new File(exportFolder + "/" + filename);//$NON-NLS-1$
            if (!f.getParentFile().getParentFile().exists()) {
                f.getParentFile().getParentFile().mkdir();
            }
            if (!f.getParentFile().exists()) {
                f.getParentFile().mkdir();
            }

            FileOutputStream output = new FileOutputStream(f);

            IOUtils.copy(inputSteam, output);
            output.close();
            inputSteam.close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void writeString(String outputStr, String filename) {
        try {
            File f = new File(exportFolder + "/" + filename);//$NON-NLS-1$
            if (!f.getParentFile().getParentFile().exists()) {
                f.getParentFile().getParentFile().mkdir();
            }
            if (!f.getParentFile().exists()) {
                f.getParentFile().mkdir();
            }
            FileOutputStream fo = new FileOutputStream(f);
            IOUtils.write(outputStr, fo, "UTF-8");//$NON-NLS-1$
            fo.flush();
            fo.close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void addPages() {

        addPage(new SelectItemsPage());
    }

    class PageListener implements Listener {

        SelectItemsPage page;

        PageListener(SelectItemsPage page) {
            this.page = page;
        }

        public void handleEvent(Event event) {
            page.checkCompleted();
        }
    };

    public void checkCompleted() {

    }

    class SelectItemsPage extends WizardPage {

        protected SelectItemsPage() {
            super(Messages.ExportItemsWizard_17);
            setTitle(Messages.ExportItemsWizard_18);
            // setDescription("Please select items to export");

            // Page isn't complete until an e-mail address has been added
            setPageComplete(false);

        }

        public void checkCompleted() {
            String folderText = folder.getText().getText();
            if (folderBtn.getSelection() && folderText.length() > 0 && new File(folderText).exists()) {
                setPageComplete(true);
            }
            String zipText = zip.getText().getText();
            if (zipBtn.getSelection() && zipText.length() > 0) {
                File parentFolder = new File(zipText).getParentFile();
                if (parentFolder != null && parentFolder.exists()) {
                    setPageComplete(true);
                }
            }

        }

        public void createControl(Composite parent) {
            Composite composite = new Composite(parent, SWT.BORDER);
            composite.setLayout(new GridLayout(3, false));
            folderBtn = new Button(composite, SWT.RADIO);
            folderBtn.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 1, 1));
            folderBtn.setText(Messages.ExportItemsWizard_19);
            folder = new FileSelectWidget(composite, "", new String[] { "*.*" }, "", false, SWT.SAVE);//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
            folder.getCmp().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

            folderBtn.addSelectionListener(new SelectionListener() {

                public void widgetDefaultSelected(SelectionEvent e) {

                }

                public void widgetSelected(SelectionEvent e) {
                    folder.setEnabled(folderBtn.getSelection());
                    checkCompleted();
                }
            });

            zipBtn = new Button(composite, SWT.RADIO);
            zipBtn.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 1, 1));
            zipBtn.setText(Messages.ExportItemsWizard_20);
            zip = new FileSelectWidget(composite, "", new String[] { "*.zip" }, "", true, SWT.SAVE);//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
            zip.getCmp().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
            zipBtn.addSelectionListener(new SelectionListener() {

                public void widgetDefaultSelected(SelectionEvent e) {

                }

                public void widgetSelected(SelectionEvent e) {
                    zip.setEnabled(zipBtn.getSelection());
                    checkCompleted();
                }
            });
            zip.getText().addListener(SWT.Modify, new PageListener(this));
            zip.getButton().addListener(SWT.Selection, new PageListener(this));
            folder.getText().addListener(SWT.Modify, new PageListener(this));
            folder.getButton().addListener(SWT.Selection, new PageListener(this));
            // create viewer
            createViewer();
            Composite itemcom = initItemTreeViewer(composite);
            itemcom.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 0));

            folder.setEnabled(folderBtn.getSelection());
            zip.setEnabled(zipBtn.getSelection());
            setControl(composite);
        }

    }

    protected Composite initItemTreeViewer(Composite composite) {
        Composite returnComposite = treeViewer.createItemList(composite);
        treeViewer.setItemText(Messages.ExportItemsWizard_21);
        return returnComposite;
    }

    protected TreeObject exportCluster(List<TreeObject> exports, WSDataClusterPK pk, TMDMService service) {
        String encodedID = null;
        try {
            List<String> items1 = new ArrayList<String>();
            List<WSItemPKsByCriteriaResponseResults> results = service.getItemPKsByCriteria(
                    new WSGetItemPKsByCriteria(null, encodedID, (long) -1, encodedID, encodedID, Integer.MAX_VALUE, 0, (long) -1,
                            pk)).getResults();
            if (results == null) {
                return null;
            }
            for (WSItemPKsByCriteriaResponseResults item : results) {
                if (item.getWsItemPK().getIds() == null) {
                    continue;
                }
                WSItem wsitem = service.getItem(new WSGetItem(item.getWsItemPK()));

                // Marshal
                StringWriter sw1 = new StringWriter();
                Marshaller.marshal(wsitem, sw1);

                String uniqueId = pk.getPk() + "." + wsitem.getConceptName();//$NON-NLS-1$
                for (String id : wsitem.getIds()) {
                    uniqueId = uniqueId + "." + id;//$NON-NLS-1$
                }
                encodedID = URLEncoder.encode(uniqueId, "UTF-8");//$NON-NLS-1$
                writeString(sw1.toString(), TreeObject.DATACONTAINER_COTENTS + "/" + pk.getPk() + "/" + encodedID);//$NON-NLS-1$//$NON-NLS-2$
                items1.add(TreeObject.DATACONTAINER_COTENTS + "/" + pk.getPk() + "/" + encodedID);//$NON-NLS-1$//$NON-NLS-2$
            }
            TreeObject obj1 = new TreeObject(pk.getPk(), null, TreeObject.DATA_CLUSTER_CONTENTS, null, null);
            obj1.setItems(items1.toArray(new String[items1.size()]));
            exports.add(obj1);
            return obj1;
        } catch (Exception e) {
        }
        return null;
    }

    private Matcher filter(String name) {
        Pattern bracket = Pattern.compile("(.*?)(\\s*)\\[(\\w+)\\]");//$NON-NLS-1$
        Matcher matcher = bracket.matcher(name);
        return matcher;
    }

}
