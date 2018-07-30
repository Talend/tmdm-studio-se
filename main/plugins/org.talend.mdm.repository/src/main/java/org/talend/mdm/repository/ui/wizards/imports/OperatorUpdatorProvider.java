// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.repository.ui.wizards.imports;

import java.util.HashMap;
import java.util.Map;

import org.talend.core.model.properties.Item;
import org.talend.mdm.repository.model.mdmproperties.impl.WSDataModelItemImpl;
import org.talend.mdm.repository.model.mdmproperties.impl.WSViewItemImpl;


/**
 * created by liusongbo on Apr 18, 2016
 *
 */
public class OperatorUpdatorProvider {

    private Map<Class, IOperatorUpdator> updators;

    private static OperatorUpdatorProvider instance = new OperatorUpdatorProvider();

    public static OperatorUpdatorProvider instance() {
        return instance;
    }

    public void updateOperator(Item item) {
        IOperatorUpdator updator = getUpdator(item.getClass());
        if (updator != null) {
            updator.updateConditionOperator(item);
        }
    }

    private IOperatorUpdator getUpdator(Class clazz) {
        if (updators == null) {
            updators = new HashMap<Class, IOperatorUpdator>();
        }

        IOperatorUpdator updator = updators.get(clazz);
        if (updator == null) {
            if (clazz == WSViewItemImpl.class) {
                updator = new ViewOperatorUpdator();
            }

            if (clazz == WSDataModelItemImpl.class) {
                updator = new DatamodelOperatorUpdator();
            }

            if (updator != null) {
                updators.put(clazz, updator);
            }
        }

        return updator;
    }

    public void registerUpdator(Class clazz, IOperatorUpdator updator) {
        if (updators == null) {
            updators = new HashMap<Class, IOperatorUpdator>();
        }

        updators.put(clazz, updator);
    }

}
