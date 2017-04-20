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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import javax.xml.ws.WebServiceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.amalto.workbench.dialogs.PluginDetailsDialog;
import com.amalto.workbench.exadapter.ExAdapterManager;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.providers.XObjectEditorInput;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.utils.XmlUtil;
import com.amalto.workbench.webservices.TMDMService;
import com.amalto.workbench.webservices.WSCheckServiceConfigRequest;
import com.amalto.workbench.webservices.WSCheckServiceConfigResponse;
import com.amalto.workbench.webservices.WSGetServicesList;
import com.amalto.workbench.webservices.WSServiceGetDocument;
import com.amalto.workbench.webservices.WSServicePutConfiguration;
import com.amalto.workbench.webservices.WSServicesList;
import com.amalto.workbench.webservices.WSServicesListItem;
import com.amalto.workbench.webservices.WSString;

public class ServiceConfigrationMainPage extends AMainPageV2 {

    private static Log log = LogFactory.getLog(ServiceConfigrationMainPage.class);

    protected Combo serviceNameCombo;

    protected Text serviceConfigurationsText;

    private Button defultParameterBtn;

    private Button checkButton;

    protected TMDMService service;

    protected WSServiceGetDocument document;

    protected WSServicePutConfiguration ws = new WSServicePutConfiguration();

    protected Text errorLabel;

    protected static final String CHECKMSG_NOSELECTION = ""; //$NON-NLS-1$

    protected static final String CHECKMSG_SUCCESSFULCONN = Messages.ServiceConfigrationMainPage_1;

    protected static final String CHECKMSG_ERRORCONN = Messages.ServiceConfigrationMainPage_2;

    private IServiceConfigrationMainPageExAdapter exAdapter;

    public ServiceConfigrationMainPage(FormEditor editor) {
        super(editor, ServiceConfigrationMainPage.class.getName(), ((XObjectEditorInput) editor.getEditorInput()).getName());
        this.exAdapter = ExAdapterManager.getAdapter(this, IServiceConfigrationMainPageExAdapter.class);
    }

    protected void setForConfigureContent(String serviceName) {

        if (serviceName != null && !"".equals(serviceName)) {//$NON-NLS-1$
            document = service.getServiceDocument(new WSString(serviceName.trim()));
            String documentConfigure = ServiceConfigrationMainPage.formartXml(document.getConfigure());
            serviceConfigurationsText.setText(documentConfigure);
            errorLabel.setText("");//$NON-NLS-1$
        }

    }

    protected void setForServiceNameCombo() {
        try {
            service = Util.getMDMService(getXObject());
            WSServicesList list = service.getServicesList(new WSGetServicesList(""));//$NON-NLS-1$
            List<WSServicesListItem> items = list.getItem();
            if (items != null) {
                String[] sortedList = new String[items.size()];
                for (int i = 0; i < items.size(); i++) {
                    sortedList[i] = items.get(i).getJndiName();
                }
                Arrays.sort(sortedList);
                for (String element : sortedList) {
                    WSServiceGetDocument doc = service.getServiceDocument(new WSString(element.trim()));
                    if (doc.getConfigureSchema() == null || doc.getConfigureSchema().length() == 0) {
                        continue;
                    }
                    serviceNameCombo.add(element);
                }
            }
        } catch (Exception e) {
            if (!Util.handleConnectionException(getSite().getShell(), e, Messages.EditXObjectAction_ErrorMsg2)) {
                MessageDialog.openError(getSite().getShell(), Messages._Error,
                        Messages.bind(Messages.EditXObjectAction_ErrorMsg2, e.getLocalizedMessage()));
            }
        }

    }

    protected String getDoc() {
        try {
            WSServiceGetDocument doc = getServiceDocument(serviceNameCombo.getText());
            return doc.getDefaultConfig();
        } catch (WebServiceException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    protected String getDesc() {
        try {
            WSServiceGetDocument doc = getServiceDocument(serviceNameCombo.getText());
            return doc.getDescription();
        } catch (WebServiceException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    protected WSServiceGetDocument getServiceDocument(String jndiName) {

        // return port.getServiceDocument(new WSString(serviceNameCombo.getText().trim()));
        return service.getServiceDocument(new WSString(jndiName.trim()));
    }

    @Override
    protected void createCharacteristicsContent(FormToolkit toolkit, Composite charSection) {

        // Routing Expressions
        Composite serviceGroup = this.getNewSectionComposite(Messages.ServiceConfigrationMainPage_3);
        serviceGroup.setLayout(new GridLayout(2, false));
        // Service Name
        Label serviceNameLabel = toolkit.createLabel(serviceGroup, Messages.ServiceConfigrationMainPage_4, SWT.NULL);
        serviceNameLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, true, 1, 1));

        Composite subPanel = toolkit.createComposite(serviceGroup);
        subPanel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));
        subPanel.setLayout(new GridLayout(2, false));
        serviceNameCombo = new Combo(subPanel, SWT.DROP_DOWN | SWT.SINGLE | SWT.READ_ONLY);
        serviceNameCombo.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));
        ((GridData) serviceNameCombo.getLayoutData()).widthHint = 300;
        serviceNameCombo.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                String serviceName = serviceNameCombo.getText();
                setRefreshing(true);
                setForConfigureContent(serviceName);
                setRefreshing(false);
                // markDirty();
            }
        });

        setForServiceNameCombo();
        // default parameters button
        defultParameterBtn = toolkit.createButton(subPanel, "", SWT.PUSH);//$NON-NLS-1$
        defultParameterBtn.setImage(ImageCache.getCreatedImage(EImage.HELP_CONTENTS.getPath()));
        defultParameterBtn.setToolTipText(Messages.ServiceConfigrationMainPage_5);
        defultParameterBtn.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
        defultParameterBtn.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
            };

            public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
                if (serviceNameCombo.getText().trim().length() == 0) {
                    return;
                }
                String doc = "";//$NON-NLS-1$
                String desc = "";//$NON-NLS-1$

                try {

                    desc = getDesc();
                    doc = getDoc();

                } catch (Exception e1) {
                    doc = "N/A";//$NON-NLS-1$
                } finally {
                    showUpDialog(desc, doc);
                }
            };

            private void showUpDialog(String desc, String doc) {

                final PluginDetailsDialog dialog = new PluginDetailsDialog(getSite().getShell(), desc, doc, null,
                        Messages.ServiceConfigrationMainPage_6);
                dialog.addListener(new Listener() {

                    public void handleEvent(Event event) {
                        dialog.close();
                    }
                });
                dialog.create();
                dialog.getShell().setText(serviceNameCombo.getText() + Messages.ServiceConfigrationMainPage_7);
                dialog.setBlockOnOpen(true);
                dialog.open();
            }

        });

        // Service Parameters
        Label serviceConfigurationsLabel = toolkit.createLabel(serviceGroup, Messages.ServiceConfigrationMainPage_8, SWT.NULL);
        serviceConfigurationsLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, true, 2, 1));
        serviceConfigurationsText = toolkit.createText(serviceGroup, "", SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);//$NON-NLS-1$

        serviceConfigurationsText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
        ((GridData) serviceConfigurationsText.getLayoutData()).widthHint = 200;
        ((GridData) serviceConfigurationsText.getLayoutData()).heightHint = 120;

        serviceConfigurationsText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                if (isRefreshing()) {
                    return;
                }
                markDirtyWithoutCommit();
            }
        });
        checkButton = toolkit.createButton(serviceGroup, Messages.ServiceConfigrationMainPage_9, SWT.NONE);
        checkButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {

                String msg = getContentsCheckResult();

                errorLabel.setForeground(CHECKMSG_SUCCESSFULCONN.equals(msg) ? errorLabel.getDisplay().getSystemColor(
                        SWT.COLOR_BLUE) : errorLabel.getDisplay().getSystemColor(SWT.COLOR_RED));
                errorLabel.setText(msg);
            }
        });
        errorLabel = new Text(serviceGroup, SWT.WRAP);
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2);
        gd.heightHint = 400;
        errorLabel.setLayoutData(gd);

    }

    public static String formartXml(String doc) {
        // format output
        if (doc != null && doc.length() > 0) {
            try {
                doc = XmlUtil.formatPretty(doc, "UTF-8");//$NON-NLS-1$
            } catch (DocumentException e) {
                log.error(e.getMessage(), e);
            }
        }
        return doc;
    }

    protected void doSaveSVNChanges() {
        if (exAdapter != null) {
            exAdapter.doSaveSVNChange(service, ws, serviceNameCombo.getText(), serviceConfigurationsText.getText());
        }

    }

    protected void saveChanges() {
        ws.setJndiName(serviceNameCombo.getText().contains("/") ? serviceNameCombo.getText() : "amalto/local/service/"//$NON-NLS-1$//$NON-NLS-2$
                + serviceNameCombo.getText());
        ws.setConfiguration(serviceConfigurationsText.getText());

        doSaveSVNChanges();
    }

    @Override
    protected void commit() {
    }

    @Override
    protected void createActions() {

    }

    @Override
    protected void refreshData() {

    }

    @Override
    public void doSave(IProgressMonitor monitor) {

        String msg = checkValidXML();

        if (msg != null) {
            MessageDialog.openError(getSite().getShell(), Messages._Error, msg);
            return;
        }

        saveChanges();
        super.doSave(monitor);
    }

    protected String getContentsCheckResult() {

        if (serviceNameCombo.getText().trim().length() == 0) {
            return CHECKMSG_NOSELECTION;
        }

        WSCheckServiceConfigResponse result;

        result = service.checkServiceConfiguration(new WSCheckServiceConfigRequest(serviceNameCombo.getText().trim(),
                serviceConfigurationsText.getText()));

        if (result.isCheckResult()) {
            return CHECKMSG_SUCCESSFULCONN;
        } else {
            return CHECKMSG_ERRORCONN;
        }

    }

    private String checkValidXML() {

        if (serviceConfigurationsText == null) {
            return null;
        }

        InputStream inStream = null;

        try {
            String xmlTxt = serviceConfigurationsText.getText();
            inStream = new ByteArrayInputStream(xmlTxt.getBytes());
            XmlUtil.parse(inStream);
        } catch (Exception e) {
            return e.getMessage();
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {

                }
            }
        }

        return null;

    }
}
