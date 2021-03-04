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
package com.amalto.workbench.widgets.celleditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.amalto.workbench.dialogs.ValidationRuleExcpressDialog;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.models.TreeParent;


public class ValidationRuleExpressionCellEditor extends EditableDialogCellEditor {

    protected String conceptName;
    // Modified by hbhong,to fix bug 21784|Add a TreeParent parameter to constructor
    protected   TreeParent treeParent;


    public void setTreeParent(TreeParent treeParent) {
        this.treeParent = treeParent;
    }

    public ValidationRuleExpressionCellEditor(Composite parent,TreeParent treeParent, String conceptName) {
        super(parent);
        this.treeParent = treeParent;
        this.conceptName = conceptName;
    }

    @Override
    protected Object openDialogBox(Control cellEditorWindow) {

        ValidationRuleExcpressDialog dialog = new ValidationRuleExcpressDialog(cellEditorWindow.getShell(),treeParent,
                Messages.ValidationRuleXX_DialogTitle, getValue() == null ? "" : getValue().toString(), conceptName, false, true); //$NON-NLS-1$

        if (dialog.open() != Window.OK)
            return null;

        return dialog.getExpression();
    }
    // The ending| bug:21784

    public void setConceptName(String conceptName) {
        this.conceptName = conceptName;
    }
}
