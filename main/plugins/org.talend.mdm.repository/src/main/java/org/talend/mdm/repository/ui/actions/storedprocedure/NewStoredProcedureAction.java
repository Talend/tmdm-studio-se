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
package org.talend.mdm.repository.ui.actions.storedprocedure;

import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ItemState;
import org.talend.core.model.properties.PropertiesFactory;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.model.mdmproperties.MdmpropertiesFactory;
import org.talend.mdm.repository.model.mdmproperties.WSStoredProcedureItem;
import org.talend.mdm.repository.model.mdmserverobject.MdmserverobjectFactory;
import org.talend.mdm.repository.model.mdmserverobject.WSStoredProcedureE;
import org.talend.mdm.repository.ui.actions.AbstractSimpleAddAction;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;

/**
 * DOC class global comment. Detailled comment <br/>
 * 
 */
public class NewStoredProcedureAction extends AbstractSimpleAddAction {

    public NewStoredProcedureAction() {
        super();
    }

    @Override
    protected String getDialogTitle() {
        return Messages.NewStoredProcedureAction_newStoredProcedure;
    }

    private WSStoredProcedureE newStoredProcedure(String key) {

        WSStoredProcedureE storedProcedure = MdmserverobjectFactory.eINSTANCE.createWSStoredProcedureE();

        storedProcedure.setName(key);
        storedProcedure.setDescription(""); //$NON-NLS-1$
        storedProcedure.setProcedure("");//$NON-NLS-1$
        storedProcedure.setRefreshCache(false);

        return storedProcedure;
    }

    @Override
    protected Item createServerObject(String key) {

        WSStoredProcedureItem item = MdmpropertiesFactory.eINSTANCE.createWSStoredProcedureItem();

        ItemState itemState = PropertiesFactory.eINSTANCE.createItemState();
        item.setState(itemState);

        WSStoredProcedureE storedProcedure = newStoredProcedure(key);
        item.setWsStoredProcedure(storedProcedure);

        if (parentItem != null) {
            item.getState().setPath(parentItem.getState().getPath());
            RepositoryResourceUtil.createItem(item, key);
        }
        return item;
    }

}
