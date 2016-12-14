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
package org.talend.mdm.repository.ui.wizards.imports.handlers;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EMap;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.core.model.properties.ReferenceFileItem;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.mdm.repository.core.command.CommandManager;
import org.talend.mdm.repository.core.command.ICommand;
import org.talend.mdm.repository.core.service.ContainerCacheService;
import org.talend.mdm.repository.core.service.ImportService;
import org.talend.mdm.repository.model.mdmproperties.MDMServerObjectItem;
import org.talend.mdm.repository.model.mdmserverobject.MDMServerObject;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;
import org.talend.repository.items.importexport.handlers.imports.ImportRepTypeHandler;
import org.talend.repository.items.importexport.handlers.model.ImportItem;
import org.talend.repository.items.importexport.manager.ResourcesManager;

public class CommonMdmImportHandler extends ImportRepTypeHandler {

    private ProxyRepositoryFactory factory = ProxyRepositoryFactory.getInstance();

    private static Logger log = Logger.getLogger(CommonMdmImportHandler.class);

    @Override
    protected void beforeCreatingItem(ImportItem importItem) {
        Property property = importItem.getProperty();
        if (property != null) {
            for (Object itemRefObj : property.getItem().getReferenceResources()) {
                ReferenceFileItem refItem = (ReferenceFileItem) itemRefObj;
                if (refItem.getName() != null) {
                    refItem.setName(null);
                }
            }
        }
    }

    @Override
    public void afterImportingItems(IProgressMonitor monitor, ResourcesManager resManager, ImportItem selectedImportItem) {

        try {
            String itemId = selectedImportItem.getItemId();
            IRepositoryViewObject object = factory.getSpecificVersion(itemId, selectedImportItem.getItemVersion(), true);
            if (null != object) {
                update(object, selectedImportItem);
            }
            // update imported id
            ImportService.importedId(itemId);
        } catch (PersistenceException e) {
            log.error(e.getMessage(), e);
        }

        super.afterImportingItems(monitor, resManager, selectedImportItem);
    }

    protected void update(IRepositoryViewObject object, ImportItem selectedImportItem) throws PersistenceException {
        Property property = object.getProperty();
        Item item = property.getItem();
        boolean needSave = false;
        if (item instanceof MDMServerObjectItem) {
            MDMServerObject serverObj = ((MDMServerObjectItem) item).getMDMServerObject();
            if (serverObj.getLastServerName() != null) {
                serverObj.setLastServerName(null);
                needSave = true;
            }
            if (serverObj.getLastServerDef() != null) {
                serverObj.setLastServerDef(null);
                needSave = true;
            }

        } else {
            EMap<?, ?> additionalProperties = property.getAdditionalProperties();
            if (additionalProperties != null) {
                additionalProperties.remove(RepositoryResourceUtil.PROP_LAST_SERVER_DEF);
                needSave = true;
            }
        }
        if (needSave) {
            factory.save(item, true);
        }
        if (selectedImportItem.isValid()) {
            String[] split = selectedImportItem.getLabel().split(" "); //$NON-NLS-1$
            String name = split.length > 0 ? split[0] : null;
            if (name != null) {
                CommandManager.getInstance().pushCommand(ICommand.CMD_ADD, selectedImportItem.getItemId(), name);
            }
        }
        // update cache
        ContainerCacheService.put(object);
    }
}
