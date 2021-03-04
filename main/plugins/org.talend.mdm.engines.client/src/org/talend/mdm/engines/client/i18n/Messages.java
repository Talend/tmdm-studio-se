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
package org.talend.mdm.engines.client.i18n;

import org.eclipse.osgi.util.NLS;

/**
 * Default implementation of MessageCore from org.talend.commons plug-in.<br/>
 *
 * $Id: Messages.java 3351 2007-05-04 12:14:00Z plegall $
 *
 */
public class Messages extends NLS {

    private static final String BUNDLE_NAME = "org.talend.mdm.engines.client.i18n.messages"; //$NON-NLS-1$

    // //////////////////////////////////////////////////////////////////////////
    //
    // Constructor
    //
    // //////////////////////////////////////////////////////////////////////////
    private Messages() {
        // do not instantiate
    }

    public static String bind(String message, String... bindings) {
        return NLS.bind(message, bindings);
    }

    // //////////////////////////////////////////////////////////////////////////
    //
    // Class initialization
    //
    // //////////////////////////////////////////////////////////////////////////
    static {
        // load message values from bundle file
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    public static String DeployOnMDMExportWizard_publishJob;

    public static String DeployOnMDMExportWizardPage_alreadyExistsError;

    public static String DeployOnMDMExportWizardPage_prepareDeployJob;

    public static String DeployOnMDMExportWizardPage_publishJob;

    public static String DeployOnMDMExportWizardPage_publishResourceError;

    public static String DeployOnMDMExportWizardPage_SpecifyServer_PublishJob;

    public static String DeployOnMDMExportWizardPage_checkConnection;

    public static String DeployOnMDMExportWizardPage_chooseResource;

    public static String DeployOnMDMExportWizardPage_DeployingJobObjects;

    public static String DeployOnMDMExportWizardPage_DeployJobOnServer;

    public static String DeployOnMDMExportWizardPage_jobLabel;

    public static String DeployOnMDMExportWizardPage_jobName;

    public static String DeployOnMDMExportWizardPage_jobDescription;

    public static String DeployOnMDMExportWizardPage_SpagoBI_Server;

    public static String DeployOnMDMExportWizardPage_spagoVisible;

    public static String DeployOnMDMExportWizardPage_publishJobSuccess;

    public static String DeployOnMDMExportWizardPage_UnableConnect;

    public static String DeployOnMDMExportWizardPage_ErrorDeployMsg;

    public static String DeployOnMDMExportWizardPage_exporting;

    public static String DeployOnMDMExportWizardPage_exportTitle;

    public static String DeployOnMDMExportWizardPage_exportType;

    public static String DeployOnMDMExportWizardPage_ExportType;

    public static String DeployOnMDMExportWizardPage_Generating;

    public static String DeployOnMDMExportWizardPage_hostedZip;

    public static String DeployOnMDMExportWizardPage_Pacakging;

    public static String DeployOnMDMExportWizardPage_jobCompileError;

    public static String DeployOnMDMExportWizardPage_mustBeFile;

    public static String MDMPreferencePage_technicalStatusLabel;

    public static String MDMServerEditor_engineColumnTitle;

    public static String MDMServerEditor_descriptionColumnTitle;

    public static String MDMServerEditor_hostColumnTitle;

    public static String MDMServerDialog_shellText_createNewServer;

    public static String MDMServerDialog_shellText_editServer;

    public static String MDMServerDialog_engineNameText;

    public static String MDMServerDialog_shortDescription;

    public static String MDMServerDialog_host;

    public static String MDMServerDialog_port;

    public static String MDMServerDialog_login;

    public static String MDMServerDialog_password;

    public static String MDMServerDialog_applicationContext;

    public static String MDMServerDialog_errorMessage_engineNameLetters;

    public static String MDMServerDialog_errorMessage_engineNameUsed;

    public static String JobScriptsExportWizardPage_contextPerlScripts;

    public static String MDMPreferencePage_spagoBiCheckButton;

    public static String DeployOnMDMExportWizardPage_Settings;

    public static String ZipExport_alreadyExistsError;

    public static String ZipExport_mustBeFile;

    public static String EditorManager_saveChangesQuestion;
    public static String Save_Resource;
}
