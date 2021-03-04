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
package org.talend.mdm.repository.ui.actions.job;

import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.bridge.AbstractBridgeRepositoryAction;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class OpenExistVersionProcessAction extends AbstractBridgeRepositoryAction {

    private static class OpenExistVersionProcessActionAdapter extends org.talend.designer.core.ui.action.OpenExistVersionProcessAction {

        public OpenExistVersionProcessActionAdapter() {
            super();
        }

        @Override
        public void refresh(Object obj) {
        }

    }

    /**
     * DOC hbhong EditProcessAction constructor comment.
     *
     * @param cAction
     */
    public OpenExistVersionProcessAction() {
        super(new OpenExistVersionProcessActionAdapter());
    }

    @Override
    public String getGroupName() {
        return GROUP_EDIT;
    }

    @Override
    public boolean isVisible(IRepositoryViewObject viewObj) {
        if (getSelectedObject().size() > 1) {
            return false;
        }

        return super.isVisible(viewObj);
    }
}
