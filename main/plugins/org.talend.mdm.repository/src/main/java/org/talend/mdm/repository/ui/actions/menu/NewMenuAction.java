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
package org.talend.mdm.repository.ui.actions.menu;

import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ItemState;
import org.talend.core.model.properties.PropertiesFactory;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.model.mdmproperties.MdmpropertiesFactory;
import org.talend.mdm.repository.model.mdmproperties.WSMenuItem;
import org.talend.mdm.repository.model.mdmserverobject.MdmserverobjectFactory;
import org.talend.mdm.repository.model.mdmserverobject.WSMenuE;
import org.talend.mdm.repository.model.mdmserverobject.WSMenuEntryE;
import org.talend.mdm.repository.model.mdmserverobject.WSMenuMenuEntriesDescriptionsE;
import org.talend.mdm.repository.ui.actions.AbstractSimpleAddAction;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;

/**
 * DOC hbhong class global comment. Detailled comment <br/>
 *
 */
public class NewMenuAction extends AbstractSimpleAddAction {

    /**
     * DOC hbhong AddMenu constructor comment.
     *
     * @param text
     */
    public NewMenuAction() {
        super();
    }

    @Override
    protected String getDialogTitle() {
        return Messages.NewMenuAction_newMenu;
    }

    private WSMenuE newBlankMenu(String key) {

        WSMenuMenuEntriesDescriptionsE descriptions = MdmserverobjectFactory.eINSTANCE.createWSMenuMenuEntriesDescriptionsE();
        descriptions.setLabel(key);
        descriptions.setLanguage("en"); //$NON-NLS-1$
        //

        WSMenuEntryE entry = MdmserverobjectFactory.eINSTANCE.createWSMenuEntryE();
        entry.getDescriptions().add(descriptions);
        entry.setId(key);
        //
        WSMenuE menu = MdmserverobjectFactory.eINSTANCE.createWSMenuE();
        menu.setName(key);
        menu.getMenuEntries().add(entry);
        //
        return menu;
    }

    @Override
    protected Item createServerObject(String key) {

        WSMenuItem item = MdmpropertiesFactory.eINSTANCE.createWSMenuItem();
        ItemState itemState = PropertiesFactory.eINSTANCE.createItemState();
        item.setState(itemState);
        //
        WSMenuE menu = newBlankMenu(key);
        item.setWsMenu(menu);

        if (parentItem != null) {
            item.getState().setPath(parentItem.getState().getPath());
            createItemAndSave(item, key);
        }
        return item;
    }

    protected void createItemAndSave(WSMenuItem item, String key) {
        RepositoryResourceUtil.createItem(item, key);
    }

}
