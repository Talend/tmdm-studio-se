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
package org.talend.mdm.repository.core.impl;

import org.apache.log4j.Logger;
import org.talend.core.model.properties.Item;

/**
 * created by HHB on 2013-10-18 Detailled comment
 *
 */
public abstract class AbstractRepositoryResourceChangeListener {

    static Logger log = Logger.getLogger(AbstractRepositoryResourceChangeListener.class);

    public AbstractRepositoryResourceChangeListener() {
    }

    protected abstract boolean isHandleProperty(String propertyName);

    protected abstract boolean isHandleItem(Item item);

    protected abstract void run(String propertyName, Item item) throws Exception;

}
