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
package com.amalto.workbench.editors;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Shell;

import com.amalto.workbench.exadapter.IExAdapter;
import com.amalto.workbench.models.TreeObject;


/**
 * created by liusongbo on 2014-4-29
 */
public interface IDataClusterBrowserMainPageExAdapter extends IExAdapter<DataClusterBrowserMainPage> {

    public void menuAboutToShow(IMenuManager manager, String menuGroupName, TableViewer resultsViewer,
            IStructuredSelection selection, Shell shell, TreeObject treeObject);

}
