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
package org.talend.mdm.repository.ui.editors;

import javax.xml.ws.WebServiceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.ui.forms.editor.FormEditor;
import org.talend.mdm.repository.core.service.RepositoryWebServiceAdapter;
import org.talend.mdm.repository.model.mdmproperties.MDMServerObjectItem;
import org.talend.mdm.repository.model.mdmserverobject.MDMServerObject;
import org.talend.mdm.repository.model.mdmserverobject.WSServiceConfigurationE;
import org.talend.mdm.repository.model.mdmserverobject.WSServicePutConfigurationE;

import com.amalto.workbench.editors.ServiceConfigrationMainPage;
import com.amalto.workbench.webservices.TMDMService;
import com.amalto.workbench.webservices.WSCheckServiceConfigRequest;
import com.amalto.workbench.webservices.WSCheckServiceConfigResponse;
import com.amalto.workbench.webservices.WSServiceGetDocument;

/**
 * DOC jsxie class global comment. Detailled comment
 */
/**
 * created by HHB on 2013-12-20 this class is not used, the super class ServiceConfigurationMainPage instead of this
 * class
 *
 */
@Deprecated
public class MDMServiceConfigrationMainPage extends ServiceConfigrationMainPage {

    private static Log log = LogFactory.getLog(MDMServiceConfigrationMainPage.class);

    public MDMServiceConfigrationMainPage(FormEditor editor) {
        super(editor);
    }

    @Override
    protected WSServiceGetDocument getServiceDocument(String jndiName) {
        return RepositoryWebServiceAdapter.getServiceDocument(jndiName);
    }

    protected String[] getComboList() {
        String[] comblist = RepositoryWebServiceAdapter.getComboListForServiceConfig();
        return comblist;
    }

    @Override
    protected TMDMService getService() {
        return RepositoryWebServiceAdapter.getMDMService(getSite().getShell());
    }

    @Override
    protected void setForConfigureContent(String serviceName) {

        try {
            if (serviceName != null && !"".equals(serviceName)) {//$NON-NLS-1$
                document = getServiceDocument(serviceName.trim());

                XObjectEditorInput2 editorInput = (XObjectEditorInput2) getEditorInput();

                MDMServerObjectItem serverObjectItem = (MDMServerObjectItem) editorInput.getInputItem();
                MDMServerObject serverObject = serverObjectItem.getMDMServerObject();

                String configContent = null;
                if (serverObject instanceof WSServiceConfigurationE) {
                    WSServiceConfigurationE object = (WSServiceConfigurationE) serverObject;
                    for (WSServicePutConfigurationE config : object.getServicePutConfigurations()) {
                        if (config.getJndiName().equals(serviceName.trim())) {
                            configContent = config.getConfiguration();
                        }

                    }

                }
                serviceConfigurationsText.setText(configContent);
                errorLabel.setText("");//$NON-NLS-1$
            }
        } catch (WebServiceException e1) {
            log.error(e1.getMessage(), e1);
        }

    }

    @Override
    protected void saveChanges() {

        // XObjectEditorInput2 editorInput = (XObjectEditorInput2) getEditorInput();
        //
        // TreeObject xobject = (TreeObject) editorInput.getModel();
        // WSServiceConfiguration object = (WSServiceConfiguration) xobject.getWsObject();
        //
        // for (WSServicePutConfiguration config : object.getServicePutConfigurations()) {
        // if (config.getJndiName().equals(serviceNameCombo.getText())) {
        // config.setConfiguration(serviceConfigurationsText.getText());
        // }
        // }
        //
        // doSaveChanges();
    }

    @Override
    protected void setForServiceNameCombo() {
        for (String item : getComboList()) {
            serviceNameCombo.add(item);
        }
    }

    @Override
    protected void doSaveSVNChanges() {

    }

    @Override
    protected String getContentsCheckResult() {

        if (serviceNameCombo.getText().trim().length() == 0) {
            return CHECKMSG_NOSELECTION;
        }

        WSCheckServiceConfigResponse result;
        service = getService();
        if (service == null) {
            return null;
        }
        result = service.checkServiceConfiguration(new WSCheckServiceConfigRequest(serviceConfigurationsText.getText(),
                serviceNameCombo.getText().trim()));

        if (result.isCheckResult()) {
            return CHECKMSG_SUCCESSFULCONN;
        } else {
            return CHECKMSG_ERRORCONN;
        }

    }

    @Override
    protected String getDoc() {

        WSServiceGetDocument document = null;
        try {
            document = getServiceDocument(serviceNameCombo.getText().trim());
        } catch (WebServiceException e) {
            log.error(e.getMessage(), e);
        }

        String doc = document.getDocument();
        return doc;

    }

    @Override
    protected String getDesc() {

        WSServiceGetDocument document = null;
        try {
            document = getServiceDocument(serviceNameCombo.getText().trim());
        } catch (WebServiceException e) {
            log.error(e.getMessage(), e);
        }
        String desc = document.getDescription();
        return desc;

    }

}
