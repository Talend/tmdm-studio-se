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
package org.talend.mdm.repository.ui.actions.trigger;

import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ItemState;
import org.talend.core.model.properties.PropertiesFactory;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.model.mdmproperties.MdmpropertiesFactory;
import org.talend.mdm.repository.model.mdmproperties.WSRoutingRuleItem;
import org.talend.mdm.repository.model.mdmserverobject.MdmserverobjectFactory;
import org.talend.mdm.repository.model.mdmserverobject.WSRoutingRuleE;
import org.talend.mdm.repository.ui.actions.AbstractSimpleAddAction;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;

/**
 * DOC class global comment. Detailled comment <br/>
 * 
 */
public class NewTriggerAction extends AbstractSimpleAddAction {

    public NewTriggerAction() {
        super();
    }

    @Override
    protected String getDialogTitle() {
        return Messages.NewTriggerAction_newTrigger;
    }

    private WSRoutingRuleE newRoutingRule(String key) {
        WSRoutingRuleE routingRule = MdmserverobjectFactory.eINSTANCE.createWSRoutingRuleE();

        routingRule.setName(key);
        routingRule.setDescription(""); //$NON-NLS-1$
        routingRule.setSynchronous(false);
        routingRule.setConcept("*"); //$NON-NLS-1$
        routingRule.setServiceJNDI(""); //$NON-NLS-1$
        routingRule.setParameters(""); //$NON-NLS-1$

        routingRule.setCondition(null);
        routingRule.setDeactive(false);

        return routingRule;
    }

    @Override
    protected Item createServerObject(String key) {

        WSRoutingRuleItem item = MdmpropertiesFactory.eINSTANCE.createWSRoutingRuleItem();

        ItemState itemState = PropertiesFactory.eINSTANCE.createItemState();
        item.setState(itemState);

        WSRoutingRuleE routingRule = newRoutingRule(key);
        item.setWsRoutingRule(routingRule);

        if (parentItem != null) {
            item.getState().setPath(parentItem.getState().getPath());
            RepositoryResourceUtil.createItem(item, key);
        }
        return item;
    }

}
