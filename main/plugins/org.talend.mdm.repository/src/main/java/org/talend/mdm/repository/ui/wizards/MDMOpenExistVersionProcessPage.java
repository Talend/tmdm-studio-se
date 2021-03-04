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
package org.talend.mdm.repository.ui.wizards;

import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.designer.core.ui.wizards.OpenExistVersionProcessPage;
import org.talend.mdm.repository.i18n.Messages;


/**
 * DOC achen  class global comment. Detailled comment
 */
public class MDMOpenExistVersionProcessPage extends OpenExistVersionProcessPage {

    /**
     * DOC achen MDMOpenExistVersionProcessPage constructor comment.
     *
     * @param alreadyEditedByUser
     * @param processObject
     */
    protected MDMOpenExistVersionProcessPage(boolean alreadyEditedByUser, IRepositoryViewObject processObject) {
        super(alreadyEditedByUser, processObject);
        setTitle(Messages.MDMOpenExistVersionProcessPage_ChooseVersion);
        setMessage(Messages.MDMOpenExistVersionProcessPage_lockedByUser);
    }

    /* (non-Javadoc)
     * @see org.talend.designer.core.ui.wizards.OpenExistVersionProcessPage#isCreateNewVersionJob()
     */
    @Override
    public boolean isCreateNewVersionJob() {
    	if(versionText.getText().equals(getOriginVersion())){
    		return false;
    	}
    	return true;
    }
}
