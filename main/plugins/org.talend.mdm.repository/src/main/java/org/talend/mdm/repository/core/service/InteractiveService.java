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
package org.talend.mdm.repository.core.service;

import java.util.LinkedList;
import java.util.List;

import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.mdm.repository.core.service.interactive.DataContainerInteractiveHandler;
import org.talend.mdm.repository.core.service.interactive.DataModelInteractiveHandler;
import org.talend.mdm.repository.core.service.interactive.JobInteractiveHandler;
import org.talend.mdm.repository.core.service.interactive.MenuInteractiveHandler;
import org.talend.mdm.repository.core.service.interactive.RoutingRuleInteractiveHandler;
import org.talend.mdm.repository.core.service.interactive.ServiceConfigurationInteractiveHandler;
import org.talend.mdm.repository.core.service.interactive.StoredProcedureInteractiveHandler;
import org.talend.mdm.repository.core.service.interactive.TransformerInteractiveHandler;
import org.talend.mdm.repository.core.service.interactive.ViewInteractiveHandler;

import com.amalto.workbench.exadapter.ExAdapterManager;
import com.amalto.workbench.models.TreeObject;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class InteractiveService {

    private static List<IInteractiveHandler> handlers = new LinkedList<IInteractiveHandler>();

    private static IInteractiveServiceExAdapter exAdapter;

    public static void registerHandler(IInteractiveHandler handler) {
        handlers.add(handler);
    }

    private InteractiveService() {

    }

    static {
        exAdapter = ExAdapterManager.getAdapter(new InteractiveService(), IInteractiveServiceExAdapter.class);
    }

    public static IInteractiveHandler findHandler(ERepositoryObjectType type) {
        if (type != null) {
            synchronized (InteractiveService.class) {
                if (handlers.isEmpty() || handlers.size() == 2) {
                    initHandler();
                }
            }
            for (IInteractiveHandler handler : handlers) {
                if (handler.getRepositoryObjectType().equals(type)) {
                    return handler;
                }
            }
        }
        return null;
    }

    public static IInteractiveHandler findHandlerForTreeObject(TreeObject treeObject) {

        switch (treeObject.getType()) {
        case TreeObject.DATA_MODEL:
            return new DataModelInteractiveHandler();
        case TreeObject.DATA_CLUSTER:
            return new DataContainerInteractiveHandler();
        case TreeObject.MENU:
            return new MenuInteractiveHandler();
        case TreeObject.ROUTING_RULE:
            return new RoutingRuleInteractiveHandler();
        case TreeObject.SERVICE_CONFIGURATION:
            return new ServiceConfigurationInteractiveHandler();
        case TreeObject.STORED_PROCEDURE:
            return new StoredProcedureInteractiveHandler();
        case TreeObject.TRANSFORMER:
            return new TransformerInteractiveHandler();
        case TreeObject.VIEW:
            return new ViewInteractiveHandler();
        default:
            if (exAdapter != null) {
                return exAdapter.findHandlerForTreeObject(treeObject);
            }

        }
        return null;

    }

    public static void initHandler() {

        registerHandler(new DataContainerInteractiveHandler());
        registerHandler(new DataModelInteractiveHandler());
        registerHandler(new MenuInteractiveHandler());
        registerHandler(new JobInteractiveHandler());
        registerHandler(new RoutingRuleInteractiveHandler());
        registerHandler(new ServiceConfigurationInteractiveHandler());
        registerHandler(new StoredProcedureInteractiveHandler());
        registerHandler(new TransformerInteractiveHandler());
        registerHandler(new ViewInteractiveHandler());

        if (exAdapter != null) {
            exAdapter.initHandler();
        }
    }
}
