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
package org.talend.mdm.repository.ui.widgets;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.talend.mdm.repository.i18n.Messages;

import com.amalto.workbench.models.TreeParent;
import com.amalto.workbench.widgets.ValidationRuleWidget;



public class ValidationRuleWidgetR extends ValidationRuleWidget {

    public ValidationRuleWidgetR(Composite parent, TreeParent treeParent, String conceptName) {
        super(parent, treeParent, conceptName);
    }

    @Override
    protected void createValidationRuleExcpressDialog(Shell shell) {
        dlg = new ValidationRuleExcpressDialogR(shell, treeParent, Messages.BuildValidationRuleExpression, text.getText(),
                conceptName, false, true);
    }

}
