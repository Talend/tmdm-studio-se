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
package com.amalto.workbench.service;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.talend.core.model.general.ModuleNeeded;
import org.talend.core.model.general.ModuleNeeded.ELibraryInstallStatus;
import org.talend.librariesmanager.model.ModulesNeededProvider;
import org.talend.librariesmanager.ui.dialogs.ExternalModulesInstallDialogWithProgress;

import com.amalto.workbench.i18n.Messages;

/**
 * created by HHB on 2013-9-22 Detailled comment
 * 
 */
public class MissingJarService {

    private MissingJarService() {

    }

    private static MissingJarService instance = new MissingJarService();

    public static MissingJarService getInstance() {
        return instance;
    }

    private List<String> getMissingJarList() {
        List<ModuleNeeded> modulesNeeded = ModulesNeededProvider.getModulesNeeded("plugin:org.talend.mdm.workbench"); //$NON-NLS-1$
        List<String> jars = new LinkedList<String>();
        for (ModuleNeeded module : modulesNeeded) {
            if (module.getStatus() == ELibraryInstallStatus.NOT_INSTALLED) {
                jars.add(module.getModuleName());
            }
        }
        return jars.isEmpty() ? null : jars;
    }

    private boolean needRestart = false;

    private boolean isOkForFirstTime = true;

    public void initialCheck() {
        isOkForFirstTime = getMissingJarList() == null;
    }

    public boolean checkMissingJar(boolean showUI) {
        if (getMissingJarList() == null) {
            if (!isOkForFirstTime) {
                needRestart = true;
                isOkForFirstTime = true;
            }
            if (needRestart) {
                showRestartConfirmDialog();
                return false;
            } else {
                return true;
            }
        }

        if (showUI && !needRestart) {
            showMissingJarDialog();
            if (getMissingJarList() == null) {
                needRestart = true;
                showRestartConfirmDialog();
                return false;
            } else {
                showOperationCanceldDialog();
            }
        }
        return false;

    }

    private void showOperationCanceldDialog() {
        Display.getDefault().syncExec(new Runnable() {

            public void run() {
                Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

                MessageDialog.openWarning(shell, Messages.MissingJarService_InstallJarTitle,
                        Messages.MissingJarService_OperationAborted);
            }
        });
    }

    private void showRestartConfirmDialog() {
        Display.getDefault().syncExec(new Runnable() {

            public void run() {
                Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
                boolean restart = MessageDialog.openQuestion(shell, Messages.MissingJarService_InstallJarTitle,
                        Messages.MissingJarService_RestartStudioMsg);
                if (restart) {
                    PlatformUI.getWorkbench().restart();
                }
            }
        });

    }

    private void showMissingJarDialog() {
        Display.getDefault().syncExec(new Runnable() {

            public void run() {
                Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

                ExternalModulesInstallDialogWithProgress dialog = new ExternalModulesInstallDialogWithProgress(shell,
                        Messages.MissingJarDialog_title, Messages.MissingJarDialog_text, SWT.APPLICATION_MODAL);
                dialog.showDialog(true, getMissingJarList().toArray(new String[0]));
            }
        });
    }
}
