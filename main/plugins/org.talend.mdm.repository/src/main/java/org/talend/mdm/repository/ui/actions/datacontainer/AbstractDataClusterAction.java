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
package org.talend.mdm.repository.ui.actions.datacontainer;

import org.apache.log4j.Logger;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.AbstractRepositoryAction;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public abstract class AbstractDataClusterAction extends AbstractRepositoryAction {

    protected static Logger log = Logger.getLogger(AbstractDataClusterAction.class);



    /**
     * DOC hbhong AbstractDataClusterAction constructor comment.
     *
     * @param text
     */
    protected AbstractDataClusterAction(String text) {
        super(text);
    }





    public String getGroupName() {
        return GROUP_EXPORT;
    }

    @Override
    public boolean isVisible(IRepositoryViewObject viewObj) {
        return getSelectedObject().size() == 1;
    }


}
