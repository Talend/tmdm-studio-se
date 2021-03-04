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
package org.talend.mdm.repository.ui.navigator;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.talend.core.model.properties.Item;

import com.amalto.workbench.exadapter.IExAdapter;

/**
 * created by HHB on 2014-1-13 Detailled comment
 *
 */
public interface IMDMRepositoryViewExAdapter extends IExAdapter<MDMRepositoryView> {

    void registerWFEditorListener();

    void unregisterWFEditorListener();

    Item getItem(IWorkbenchPart part);

    void createBranchPartControl(Composite parent);

    void refreshBranchPartControl();
}
