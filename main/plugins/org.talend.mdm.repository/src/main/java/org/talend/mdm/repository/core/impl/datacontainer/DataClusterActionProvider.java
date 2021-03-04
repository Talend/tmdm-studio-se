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
package org.talend.mdm.repository.core.impl.datacontainer;

import java.util.List;

import org.eclipse.ui.navigator.CommonViewer;
import org.talend.core.model.properties.FolderType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.AbstractRepositoryAction;
import org.talend.mdm.repository.core.impl.RepositoryNodeActionProviderAdapter;
import org.talend.mdm.repository.model.mdmproperties.MDMServerObjectItem;
import org.talend.mdm.repository.ui.actions.datacontainer.ExportDataClusterAction;
import org.talend.mdm.repository.ui.actions.datacontainer.ImportDataClusterAction;
import org.talend.mdm.repository.ui.actions.datacontainer.NewDataContainerAction;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;

/**
 * DOC hbhong class global comment. Detailled comment <br/>
 *
 */
public class DataClusterActionProvider extends RepositoryNodeActionProviderAdapter {

    AbstractRepositoryAction addAction;

    AbstractRepositoryAction exportDataClusterAction;

    AbstractRepositoryAction importDataClusterAction;

    @Override
    public void initCommonViewer(CommonViewer commonViewer) {
        super.initCommonViewer(commonViewer);

        addAction = initRepositoryAction(new NewDataContainerAction(), commonViewer);
        exportDataClusterAction = initRepositoryAction(new ExportDataClusterAction(), commonViewer);
        importDataClusterAction = initRepositoryAction(new ImportDataClusterAction(), commonViewer);
    }

    @Override
    public List<AbstractRepositoryAction> getActions(IRepositoryViewObject viewObj) {
        List<AbstractRepositoryAction> actions = super.getActions(viewObj);
        if (RepositoryResourceUtil.hasContainerItem(viewObj, FolderType.SYSTEM_FOLDER_LITERAL, FolderType.FOLDER_LITERAL)) {
            addAction(actions, addAction, viewObj);
        }

        if (viewObj.getProperty().getItem() instanceof MDMServerObjectItem) {
            actions.remove(openVersionAction);
            addAction(actions, renameAction, viewObj);
            addAction(actions, deployToAction, viewObj);

            addAction(actions, deployToLastServerAction, viewObj);
            addAction(actions, deployAnotherToAction, viewObj);
            addAction(actions, undeployAction, viewObj);
            addAction(actions, exportDataClusterAction, viewObj);
            addAction(actions, importDataClusterAction, viewObj);
        }
        addAction(actions, deployAllAction, viewObj);

        return actions;
    }

}
