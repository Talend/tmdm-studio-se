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
package org.talend.mdm.repository.ui.starting.actions;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.intro.IIntroSite;
import org.eclipse.ui.intro.config.IIntroAction;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.model.mdmmetadata.MDMServerDef;
import org.talend.mdm.workbench.serverexplorer.core.ServerDefService;
import org.talend.mdm.workbench.serverexplorer.ui.dialogs.SelectServerDefDialog;
import org.talend.repository.token.RepositoryActionLogger;

import com.amalto.workbench.utils.Util;

public class ConnectToWebUIAction implements IIntroAction {

    private static Logger log = Logger.getLogger(ConnectToWebUIAction.class);

    public void run(IIntroSite iintrosite, Properties properties) {
        RepositoryActionLogger.logAction(getClass().getName());
    	
        String url = getUrl();
        try {
            if (url != null) {
                open(url);
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

    }

    private void open(String url) throws IOException {
        Desktop desktop = Desktop.getDesktop();
        if (Desktop.isDesktopSupported() && desktop.isSupported(Desktop.Action.BROWSE)) {
            URI uri = URI.create(url);
            desktop.browse(uri);
        }
    }

    private SelectServerDefDialog getSelectServerDefDialog() {
        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        return new SelectServerDefDialog(shell);
    }

    private String getUrl() {
        String path = "/talendmdm";//$NON-NLS-1$
        String defaultHostPort = "http://localhost:8080"; //$NON-NLS-1$
        List<IRepositoryViewObject> viewObjects = ServerDefService.getAllServerDefViewObjects();
        if (viewObjects == null || viewObjects.size() == 0) {
            return defaultHostPort + path;
        }

        SelectServerDefDialog selServerDlg = getSelectServerDefDialog();
        if (selServerDlg.open() == IDialogConstants.OK_ID) {
            MDMServerDef serverDef = selServerDlg.getSelectedServerDef();
            return serverDef.getProtocol() + serverDef.getHost()
                    + ":" + serverDef.getPort() + Util.getContextPath(serverDef.getPath()); //$NON-NLS-1$
        }
        return null;
    }
}
