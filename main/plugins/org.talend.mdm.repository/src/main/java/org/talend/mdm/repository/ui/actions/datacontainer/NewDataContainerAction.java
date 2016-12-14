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
package org.talend.mdm.repository.ui.actions.datacontainer;

import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ItemState;
import org.talend.core.model.properties.PropertiesFactory;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.model.mdmproperties.MdmpropertiesFactory;
import org.talend.mdm.repository.model.mdmproperties.WSDataClusterItem;
import org.talend.mdm.repository.model.mdmserverobject.MdmserverobjectFactory;
import org.talend.mdm.repository.model.mdmserverobject.WSDataClusterE;
import org.talend.mdm.repository.ui.actions.AbstractSimpleAddAction;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;

/**
 * DOC class global comment. Detailled comment <br/>
 * 
 */
public class NewDataContainerAction extends AbstractSimpleAddAction {

    public NewDataContainerAction() {
        super();
    }

    @Override
    protected String getDialogTitle() {
        return Messages.NewDataContainerAction_newDataContainer;
    }

    private WSDataClusterE newDataContainer(String key) {

        WSDataClusterE datacontainer = MdmserverobjectFactory.eINSTANCE.createWSDataClusterE();

        datacontainer.setName(key);
        datacontainer.setDescription(""); //$NON-NLS-1$
        datacontainer.setVocabulary("");//$NON-NLS-1$

        return datacontainer;
    }

    @Override
    protected Item createServerObject(String key) {

        WSDataClusterItem item = MdmpropertiesFactory.eINSTANCE.createWSDataClusterItem();

        ItemState itemState = PropertiesFactory.eINSTANCE.createItemState();
        item.setState(itemState);
        //
        WSDataClusterE datacontainer = newDataContainer(key);
        item.setWsDataCluster(datacontainer);

        if (parentItem != null) {
            item.getState().setPath(parentItem.getState().getPath());
            RepositoryResourceUtil.createItem(item, key);
        }
        return item;
    }

    @Override
    protected boolean runOpenActionAfterCreation(Item item) {
        return false;
    }

}
