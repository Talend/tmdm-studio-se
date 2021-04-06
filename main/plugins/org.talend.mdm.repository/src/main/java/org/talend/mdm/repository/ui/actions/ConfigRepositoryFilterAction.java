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
package org.talend.mdm.repository.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.talend.mdm.repository.ui.dialogs.filter.RepositoryViewFilterDialog;
import org.talend.repository.token.RepositoryActionLogger;

public class ConfigRepositoryFilterAction implements IViewActionDelegate {

    private IViewPart view;

    public void run(IAction action) {
        RepositoryActionLogger.logAction(getClass().getName());
    	
        RepositoryViewFilterDialog dialog = new RepositoryViewFilterDialog(view.getSite().getShell());
        dialog.open();
    }

    public void selectionChanged(IAction action, ISelection selection) {
    }

    public void init(IViewPart view) {
        this.view = view;
    }

}
