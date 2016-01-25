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
package com.amalto.workbench.rcp;

import java.lang.management.ManagementFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.internal.ide.EditorAreaDropAdapter;

import com.amalto.workbench.MDMWorbenchPlugin;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.register.RegisterManagement;
import com.amalto.workbench.register.RegisterWizard;
import com.amalto.workbench.register.RegisterWizardDialog;
import com.amalto.workbench.service.GlobalServiceRegister;
import com.amalto.workbench.service.branding.IBrandingService;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

    private static Log log = LogFactory.getLog(ApplicationWorkbenchWindowAdvisor.class);

    public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        super(configurer);
    }

    public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
        return new ApplicationActionBarAdvisor(configurer);
    }

    public void preWindowOpen() {

        IWorkbenchWindowConfigurer configurer = getWindowConfigurer();

        configurer.setInitialSize(new Point(1200, 950));
        configurer.setShowCoolBar(true);
        configurer.setShowStatusLine(true);
        configurer.setShowProgressIndicator(true);
        configurer.setShowPerspectiveBar(true);
        configurer.configureEditorAreaDropListener(new EditorAreaDropAdapter(configurer.getWindow()));

        // configurer.setTitle("Talend MDM Studio (3.2.0M2)");
    }

    @Override
    public void postWindowOpen() {
        PerspectiveReviewUtil.regisitPerspectiveBarSelectListener();
        // Start Web Service Registration
        try {
            if (!RegisterManagement.isProductRegistered()) {
                RegisterWizard registerWizard = new RegisterWizard();
                Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
                WizardDialog dialog = new RegisterWizardDialog(shell, registerWizard);
                dialog.setTitle(Messages.RegisterWizard_windowTitle);
                if (dialog.open() == WizardDialog.OK) {

                    String projectLanguage = "java";//$NON-NLS-1$

                    // OS
                    String osName = System.getProperty("os.name"); //$NON-NLS-1$
                    String osVersion = System.getProperty("os.version"); //$NON-NLS-1$

                    // Java version
                    String javaVersion = System.getProperty("java.version"); //$NON-NLS-1$

                    // Java Memory
                    long totalMemory = Runtime.getRuntime().totalMemory();

                    // RAM
                    com.sun.management.OperatingSystemMXBean composantSystem = (com.sun.management.OperatingSystemMXBean) ManagementFactory
                            .getOperatingSystemMXBean();
                    Long memRAM = new Long(composantSystem.getTotalPhysicalMemorySize() / 1024);

                    // CPU
                    int nbProc = Runtime.getRuntime().availableProcessors();

                    RegisterManagement.register(registerWizard.getEmail(), registerWizard.getCountry(),
                            registerWizard.isProxyEnabled(), registerWizard.getProxyHost(), registerWizard.getProxyPort(),
                            MDMWorbenchPlugin.getDefault().getVersion(), projectLanguage, osName, osVersion, javaVersion,
                            totalMemory, memRAM, nbProc);
                } else {
                    RegisterManagement.decrementTry();
                }
            }
        } catch (Exception e) {

            log.error(e.getMessage(), e);
            // Do nothing : registration web service error is not a problem
        }
        IBrandingService service = (IBrandingService) GlobalServiceRegister.getDefault().getService(IBrandingService.class);
        getWindowConfigurer()
                .setTitle(getWindowConfigurer().getTitle() + service.getBrandingConfiguration().getAdditionalTitle());

    }

}
