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
package org.talend.mdm.repository.core.impl.recyclebin;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.ui.navigator.CommonViewer;
import org.talend.core.model.properties.FolderType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.AbstractRepositoryAction;
import org.talend.mdm.repository.core.impl.RepositoryNodeActionProviderAdapter;
import org.talend.mdm.repository.ui.actions.recyclebin.EmptyRecycleBinAction;
import org.talend.mdm.repository.ui.actions.recyclebin.RemovePhysicallyFromRepositoryAction;
import org.talend.mdm.repository.ui.actions.recyclebin.RestoreAction;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;

/**
 * DOC hbhong class global comment. Detailled comment <br/>
 *
 */
public class RecycleBinActionProvider extends RepositoryNodeActionProviderAdapter {

    AbstractRepositoryAction removeAction;

    AbstractRepositoryAction emptyRecycleBinAction;

    AbstractRepositoryAction restoreAction;

    @Override
    public void initCommonViewer(CommonViewer commonViewer) {
        removeAction = initRepositoryAction(new RemovePhysicallyFromRepositoryAction(), commonViewer);
        emptyRecycleBinAction = initRepositoryAction(new EmptyRecycleBinAction(), commonViewer);
        restoreAction = initRepositoryAction(new RestoreAction(), commonViewer);
    }

    @Override
    public List<AbstractRepositoryAction> getActions(IRepositoryViewObject viewObj) {
        List<AbstractRepositoryAction> actions = new LinkedList<AbstractRepositoryAction>();
        if (!RepositoryResourceUtil.hasContainerItem(viewObj, FolderType.SYSTEM_FOLDER_LITERAL)) {
            actions.add(removeAction);
            actions.add(restoreAction);
        } else {
            addAction(actions, emptyRecycleBinAction, viewObj);
        }
        return actions;
    }

}
