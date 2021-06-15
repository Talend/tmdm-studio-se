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
package org.talend.mdm.repository.ui.actions.importproject;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.general.Project;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.mdm.repository.core.IServerObjectRepositoryType;
import org.talend.mdm.repository.core.command.CommandManager;
import org.talend.mdm.repository.core.command.ICommand;
import org.talend.mdm.repository.model.mdmproperties.MDMServerObjectItem;
import org.talend.mdm.repository.model.mdmserverobject.MDMServerObject;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.ui.utils.IAfterImportProjectAction;

/**
 *
 * Mark the imported project nodes as 'new' status
 */
public class MarkNodeAsNewAction implements IAfterImportProjectAction {

    private static Logger log = Logger.getLogger(MarkNodeAsNewAction.class);

    public void run(Project project) {
        if (project == null) {
            return;
        }
        List<ERepositoryObjectType> types = new ArrayList<ERepositoryObjectType>();
        types.add(IServerObjectRepositoryType.TYPE_DATAMODEL);
        types.add(IServerObjectRepositoryType.TYPE_CUSTOM_FORM);
        types.add(IServerObjectRepositoryType.TYPE_DATACLUSTER);
        types.add(IServerObjectRepositoryType.TYPE_MENU);
        types.add(IServerObjectRepositoryType.TYPE_RESOURCE);
        types.add(IServerObjectRepositoryType.TYPE_ROLE);
        types.add(IServerObjectRepositoryType.TYPE_ROUTINGRULE);
        types.add(IServerObjectRepositoryType.TYPE_STOREPROCEDURE);
        types.add(IServerObjectRepositoryType.TYPE_TRANSFORMERV2);
        types.add(IServerObjectRepositoryType.TYPE_VIEW);
        types.add(ERepositoryObjectType.PROCESS);
        IProxyRepositoryFactory factory = CoreRuntimePlugin.getInstance().getProxyRepositoryFactory();
        for (ERepositoryObjectType type : types) {
            List<IRepositoryViewObject> objects;
            try {
                objects = factory.getAll(project, type);
            } catch (PersistenceException e1) {
                log.error(e1.getLocalizedMessage(), e1);
                return;
            }

            if (objects != null && objects.size() > 0) {
                for (IRepositoryViewObject object : objects) {

                    Property property = object.getProperty();
                    Item item = property.getItem();
                    boolean needRemoved = false;
                    MDMServerObject serverObj = null;
                    if (item instanceof MDMServerObjectItem) {

                        serverObj = ((MDMServerObjectItem) item).getMDMServerObject();
                        if (serverObj.getLastServerName() != null || serverObj.getLastServerDef() != null) {
                            needRemoved = true;
                        }
                    } else {
                        Object value = property.getAdditionalProperties().get(RepositoryResourceUtil.PROP_LAST_SERVER_DEF);
                        if (value != null) {
                            needRemoved = true;
                        }
                    }
                    if (needRemoved) {
                        RepositoryResourceUtil.setLastServerDef(item, null);
                        try {
                            factory.save(project, item);
                        } catch (PersistenceException e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                    String name = (serverObj == null || serverObj.getName() == null) ? item.getProperty().getLabel() : serverObj
                            .getName();
                    // flagged as new
                    CommandManager.getInstance().pushCommand(ICommand.CMD_ADD, item.getProperty().getId(), name);

                }
            }
        }
    }
}
