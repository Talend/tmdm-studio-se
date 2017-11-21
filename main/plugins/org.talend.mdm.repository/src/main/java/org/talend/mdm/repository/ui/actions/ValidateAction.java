// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
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

import java.util.LinkedList;
import java.util.List;

import org.talend.core.GlobalServiceRegister;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.AbstractRepositoryAction;
import org.talend.mdm.repository.core.service.IModelValidationService;
import org.talend.mdm.repository.i18n.Messages;

/**
 * created by HHB on 2013-1-24 Detailled comment
 * 
 */
public class ValidateAction extends AbstractRepositoryAction {

    /**
     * DOC HHB ValidateDataModelAction constructor comment.
     * 
     * @param text
     */
    public ValidateAction() {
        super(Messages.Validate_label);
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

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.mdm.repository.core.AbstractRepositoryAction#doRun()
     */
    @Override
    protected void doRun() {
        List<IRepositoryViewObject> viewObjs = new LinkedList<IRepositoryViewObject>();
        for (Object obj : getSelectedObject()) {
            if (obj instanceof IRepositoryViewObject) {
                viewObjs.add((IRepositoryViewObject) obj);
            }
        }
        IModelValidationService service = (IModelValidationService) GlobalServiceRegister.getDefault().getService(IModelValidationService.class);

        service.validate(viewObjs, IModelValidationService.VALIDATE_IMMEDIATE, false);

    }
}
