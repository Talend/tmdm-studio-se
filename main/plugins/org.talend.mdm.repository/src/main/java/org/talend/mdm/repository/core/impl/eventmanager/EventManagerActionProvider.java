// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.repository.core.impl.eventmanager;

import java.util.List;

import org.eclipse.ui.navigator.CommonViewer;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.AbstractRepositoryAction;
import org.talend.mdm.repository.core.impl.RepositoryNodeActionProviderAdapter;

/**
 * DOC hbhong class global comment. Detailled comment <br/>
 * 
 */
public class EventManagerActionProvider extends RepositoryNodeActionProviderAdapter {

    AbstractRepositoryAction addAction;

    @Override
    public void initCommonViewer(CommonViewer commonViewer) {
        super.initCommonViewer(commonViewer);

    }

    @Override
    public List<AbstractRepositoryAction> getActions(IRepositoryViewObject viewObj) {
        List<AbstractRepositoryAction> actions = super.getActions(viewObj);
        actions.remove(createFolderAction);
        actions.add(deployAllAction);
        return actions;
    }

}
