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
package org.talend.mdm.repository.core.service;

import java.util.List;

import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.command.CommandManager;
import org.talend.mdm.repository.core.command.ICommand;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;

import com.amalto.workbench.service.bridge.IRemoveCommandService;


public class RemoveCommandService implements IRemoveCommandService {

    public boolean removeDeployPhaseCommandOf(ERepositoryObjectType type, Object item) {
        List<IRepositoryViewObject> viewObjs = RepositoryResourceUtil.findAllViewObjects(type);
        for(IRepositoryViewObject viewObj:viewObjs) {
            Item innerItem = viewObj.getProperty().getItem();
            if(innerItem.equals(item)) {
                CommandManager.getInstance().removeCommandStack(viewObj.getId(), ICommand.PHASE_DEPLOY);
                break;
            }
        }
        return false;
    }

    public boolean removeDeployPhaseCommandOf(ERepositoryObjectType type, String itemName) {
        IRepositoryViewObject viewObj = RepositoryResourceUtil.findViewObjectByName(type, itemName);
        if(viewObj != null) {
            Item item = viewObj.getProperty().getItem();
            return removeDeployPhaseCommandOf(type, item);
        }

        return false;
    }

}
