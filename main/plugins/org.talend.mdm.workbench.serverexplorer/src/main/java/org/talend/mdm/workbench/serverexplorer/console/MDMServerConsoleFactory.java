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
package org.talend.mdm.workbench.serverexplorer.console;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleFactory;
import org.eclipse.ui.console.IConsoleManager;
import org.talend.mdm.repository.model.mdmmetadata.MDMServerDef;
import org.talend.mdm.workbench.serverexplorer.core.ServerDefService;
import org.talend.mdm.workbench.serverexplorer.i18n.Messages;
import org.talend.mdm.workbench.serverexplorer.ui.dialogs.SelectServerDefDialog;

/**
 *
 * created by Karelun Huang on Mar 20, 2013 Detailled comment
 *
 */
public abstract class MDMServerConsoleFactory implements IConsoleFactory {

    private static final Log log = LogFactory.getLog(MDMServerConsoleFactory.class);

    public void openConsole() {
        List<MDMServerDef> allServerDefs = ServerDefService.getAllServerDefs();
        if (allServerDefs == null || allServerDefs.isEmpty()) {
            MessageDialog.openInformation(null, Messages.ShowMDMServerConsole_InfoDialog_Title, Messages.ShowMDMServerConsole_InfoDialog_Message);
        } else if (allServerDefs.size() == 1) {
            MDMServerDef serverDef = allServerDefs.get(0);
            showMDMServerConsole(serverDef);
        } else {
            SelectServerDefDialog d = new SelectServerDefDialog(Display.getDefault().getActiveShell());
            d.create();
            d.setSelectServer(allServerDefs.get(0));
            if (d.open() == IDialogConstants.OK_ID) {
                MDMServerDef serverDef = d.getSelectedServerDef();
                showMDMServerConsole(serverDef);
            }
        }
    }

    public void showMDMServerConsole(MDMServerDef serverDef) {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null) {
            return;
        }
        IWorkbenchPage page = window.getActivePage();
        if (page == null) {
            return;
        }

        Map<String, MDMServerMessageConsole> serverToConsole = getServerToConsole();
        MDMServerMessageConsole mdmServerConsole = serverToConsole.get(serverDef.getName());
        if (mdmServerConsole == null) {
            mdmServerConsole = createMDMServerMessageConsole(serverDef);
            serverToConsole.put(serverDef.getName(), mdmServerConsole);
        }

        if (!containedMDMServerMessageConsole(mdmServerConsole)) {
            IConsoleManager consoleManager = ConsolePlugin.getDefault().getConsoleManager();
            consoleManager.addConsoles(new IConsole[] { mdmServerConsole });

            mdmServerConsole.activate();
        } else {
            mdmServerConsole.setServerDef(serverDef);
            mdmServerConsole.reload();
            ConsolePlugin.getDefault().getConsoleManager().showConsoleView(mdmServerConsole);
        }
        // boolean showed = showConsoleView(serverDef.getName());
        // if (showed) {
        // }
    }

    // private boolean showConsoleView(String serverName) {
    // IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    // if (window == null) {
    // return false;
    // }
    // IWorkbenchPage page = window.getActivePage();
    // if (page == null) {
    // return false;
    // }
    // try {
    // page.showView(IConsoleConstants.ID_CONSOLE_VIEW, "mdm_server_console", IWorkbenchPage.VIEW_ACTIVATE);
    // return true;
    // } catch (PartInitException e) {
    // log.error(e.getMessage(), e);
    // }
    // return false;
    // }

    private boolean containedMDMServerMessageConsole(IConsole mdmServerConsole) {
        IConsole[] consoles = ConsolePlugin.getDefault().getConsoleManager().getConsoles();
        if (consoles == null || consoles.length == 0) {
            return false;
        }
        for (IConsole console : consoles) {
            if (console == mdmServerConsole) {
                return true;
            }
        }
        return false;
    }

    protected abstract MDMServerMessageConsole createMDMServerMessageConsole(MDMServerDef serverDef);

    protected abstract Map<String, MDMServerMessageConsole> getServerToConsole();
}
