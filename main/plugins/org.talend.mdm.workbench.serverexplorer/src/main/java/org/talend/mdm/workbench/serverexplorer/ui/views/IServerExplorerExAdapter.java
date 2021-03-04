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
package org.talend.mdm.workbench.serverexplorer.ui.views;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;

import com.amalto.workbench.exadapter.IExAdapter;


/**
 * created by liusongbo on 2014-5-12
 */
public interface IServerExplorerExAdapter extends IExAdapter<ServerExplorer> {

    public void initAction(List<Action> actions, TreeViewer treeViewer);
}
