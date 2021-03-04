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
package org.talend.mdm.repository.core;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.navigator.CommonViewer;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.ui.editors.IRepositoryViewEditorInput;

/**
 * DOC hbhong class global comment. Detailled comment <br/>
 *
 */
public interface IRepositoryNodeActionProvider {

    public List<AbstractRepositoryAction> getActions(IRepositoryViewObject viewObj);

    public void initCommonViewer(CommonViewer commonViewer);

    public IRepositoryViewEditorInput getOpenEditorInput(IRepositoryViewObject viewObj);

    public AbstractRepositoryAction getOpenAction(IRepositoryViewObject viewObj);

    public void setRepositoryViewGlobalActionHandler(IRepositoryViewGlobalActionHandler handler);

    public void updateSelection(IStructuredSelection selection);
}
