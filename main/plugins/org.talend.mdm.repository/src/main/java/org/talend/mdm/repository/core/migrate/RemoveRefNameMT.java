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
package org.talend.mdm.repository.core.migrate;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.talend.commons.exception.ExceptionHandler;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.migration.AbstractItemMigrationTask;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.core.model.properties.ReferenceFileItem;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.mdm.repository.core.IServerObjectRepositoryType;

/**
 * created by HHB on 2015-7-17 Detailled comment
 *
 */
public class RemoveRefNameMT extends AbstractItemMigrationTask {

    protected static final ProxyRepositoryFactory REPO_FACTORY = ProxyRepositoryFactory.getInstance();

    public Date getOrder() {
        GregorianCalendar gc = new GregorianCalendar(2015, 7, 18, 0, 0, 0);
        return gc.getTime();
    }

    @Override
    public List<ERepositoryObjectType> getTypes() {

        List<ERepositoryObjectType> types = new ArrayList<ERepositoryObjectType>();
        types.add(IServerObjectRepositoryType.TYPE_CUSTOM_FORM);
        types.add(IServerObjectRepositoryType.TYPE_DATAMODEL);
        types.add(IServerObjectRepositoryType.TYPE_RESOURCE);
        types.add(IServerObjectRepositoryType.TYPE_CUSTOM_FORM);
        return types;
    }

    @Override
    public ExecutionResult execute(Item item) {
        Property property = item.getProperty();
        if (property != null) {
            boolean needSave = false;
            for (Object itemRefObj : property.getItem().getReferenceResources()) {
                ReferenceFileItem refItem = (ReferenceFileItem) itemRefObj;
                if (refItem.getName() != null) {
                    needSave = true;
                    refItem.setName(null);
                }
            }
            if (needSave) {
                try {
                    saveItem(item);
                    return ExecutionResult.SUCCESS_NO_ALERT;
                } catch (PersistenceException e) {
                    ExceptionHandler.process(e);
                    return ExecutionResult.FAILURE;
                }
            }
        }

        return ExecutionResult.NOTHING_TO_DO;
    }

    private void saveItem(Item item) throws PersistenceException {
        REPO_FACTORY.save(item, true);
    }
}
