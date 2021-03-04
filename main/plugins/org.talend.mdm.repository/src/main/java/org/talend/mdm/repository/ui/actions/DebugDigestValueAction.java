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
package org.talend.mdm.repository.ui.actions;

import java.util.List;

import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.AbstractRepositoryAction;
import org.talend.mdm.repository.core.service.ConsistencyService;

/**
 * created by HHB on 2013-7-15 Just used for test DigestValueAction , only actved in debug mode
 *
 */
public class DebugDigestValueAction extends AbstractRepositoryAction {

    ConsistencyService service;

    public DebugDigestValueAction() {
        super("Debug digestValue"); //$NON-NLS-1$
        service = ConsistencyService.getInstance();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.talend.mdm.repository.core.AbstractRepositoryAction#getGroupName()
     */
    @Override
    public String getGroupName() {
        return GROUP_COMMON;
    }

    @Override
    protected void doRun() {
        List<Object> selectedObject = getSelectedObject();
        if (!selectedObject.isEmpty()) {
            IRepositoryViewObject viewObj = (IRepositoryViewObject) selectedObject.get(0);
            Item item = viewObj.getProperty().getItem();
            String digestValue = service.calculateDigestValue(item, viewObj.getRepositoryObjectType());
            System.out.println("DigestValue:(" + item.getProperty().getLabel() + ") = " + digestValue); //$NON-NLS-1$ //$NON-NLS-2$
        }

    }

}
