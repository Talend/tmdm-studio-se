// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.engines.client.ui.wizards;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.ide.IDE;
import org.talend.core.model.properties.SpagoBiServer;
import org.talend.mdm.engines.client.Activator;
import org.talend.mdm.engines.client.i18n.Messages;

/**
 * Publish SpagoBI export wizard. <br/>
 * 
 * $Id: DeployOnMDMExportWizard.java 1 2007-04-27 11:30:00 cantoine
 * 
 */
public class DeployOnMDMExportWizard extends Wizard implements IExportWizard {

    private IStructuredSelection selection;

    private DeployOnMDMExportWizardPage mainPage;

    private SpagoBiServer mdmServer = null;

    /**
     * Creates a wizard for exporting workspace resources to a zip file.
     */
    public DeployOnMDMExportWizard() {
        IDialogSettings workbenchSettings = Activator.getDefault().getDialogSettings();
        IDialogSettings section = workbenchSettings.getSection("DeployOnMDMExportWizard"); //$NON-NLS-1$
        if (section == null) {
            section = workbenchSettings.addNewSection("DeployOnMDMExportWizard"); //$NON-NLS-1$
            section.put(JavaDeployOnMDMExportWizardPage.STORE_SHELL_LAUNCHER_ID, true);
            section.put(JavaDeployOnMDMExportWizardPage.STORE_SYSTEM_ROUTINE_ID, true);
            section.put(JavaDeployOnMDMExportWizardPage.STORE_USER_ROUTINE_ID, true);
            section.put(JavaDeployOnMDMExportWizardPage.STORE_MODEL_ID, true);
            section.put(JavaDeployOnMDMExportWizardPage.STORE_JOB_ID, true);
            section.put(JavaDeployOnMDMExportWizardPage.STORE_CONTEXT_ID, true);

        }
        setDialogSettings(section);
    }

    /*
     * (non-Javadoc) Method declared on IWizard.
     */
    @Override
    public void addPages() {
        super.addPages();

        mainPage = new JavaDeployOnMDMExportWizardPage(selection, mdmServer);
        addPage(mainPage);
    }

    /*
     * (non-Javadoc) Method declared on IWorkbenchWizard.
     */
    public void init(IWorkbench workbench, IStructuredSelection currentSelection) {
        this.selection = currentSelection;
        List selectedResources = IDE.computeSelectedResources(currentSelection);
        if (!selectedResources.isEmpty()) {
            this.selection = new StructuredSelection(selectedResources);
        }

        setWindowTitle(Messages.DeployOnMDMExportWizard_publishJob);
        setDefaultPageImageDescriptor(Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/exportzip_wiz.png"));//$NON-NLS-1$
        setNeedsProgressMonitor(true);
    }

    /*
     * (non-Javadoc) Method declared on IWizard.
     */
    @Override
    public boolean performFinish() {
        boolean finish = mainPage.finish();
        setMdmServer(mainPage.getMdmServer());

        return finish;
    }

    public SpagoBiServer getMdmServer() {
        return this.mdmServer;
    }

    public void setMdmServer(SpagoBiServer mdmServer) {
        this.mdmServer = mdmServer;
    }

    public RuntimeException getDeployException() {
        return mainPage.getDeployException();
    }

    public boolean isDeploySucceed() {
        return mainPage.isDeploySucceed();
    }
}
