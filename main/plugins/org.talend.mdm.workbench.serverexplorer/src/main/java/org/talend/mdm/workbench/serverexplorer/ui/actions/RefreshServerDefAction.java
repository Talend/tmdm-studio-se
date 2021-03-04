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
package org.talend.mdm.workbench.serverexplorer.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.talend.mdm.workbench.serverexplorer.ui.views.ServerExplorer;

public class RefreshServerDefAction implements IViewActionDelegate {

    private ServerExplorer serverExplorer;

    public void run(IAction action) {
        if (serverExplorer != null) {
            serverExplorer.refreshServerDefs();
        }
    }

    public void selectionChanged(IAction action, ISelection selection) {
    }

    public void init(IViewPart view) {

        if (view instanceof ServerExplorer) {
            serverExplorer = (ServerExplorer) view;
        }
    }

}
