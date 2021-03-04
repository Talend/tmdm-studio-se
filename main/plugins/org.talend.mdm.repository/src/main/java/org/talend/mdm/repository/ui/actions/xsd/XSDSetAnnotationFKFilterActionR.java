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
package org.talend.mdm.repository.ui.actions.xsd;

import org.eclipse.swt.widgets.Shell;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.ui.dialogs.xsd.FKFilterDialogR;

import com.amalto.workbench.actions.XSDSetAnnotationFKFilterAction;
import com.amalto.workbench.dialogs.FKFilterDialog;
import com.amalto.workbench.editors.DataModelMainPage;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class XSDSetAnnotationFKFilterActionR extends XSDSetAnnotationFKFilterAction {

    /**
     * DOC hbhong XSDSetAnnotationFKFilterActionR constructor comment.
     *
     * @param page
     * @param dataModelName
     */
    public XSDSetAnnotationFKFilterActionR(DataModelMainPage page, String dataModelName) {
        super(page, dataModelName);
    }

    @Override
    protected FKFilterDialog getNewFKFilterDialog(Shell shell, String filter, DataModelMainPage page, String conceptName) {
        return new FKFilterDialogR(shell, Messages.XSDSetAnnotationFKFilterActionR_title, filter, page, conceptName, true);
    }

}
