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

import org.eclipse.swt.widgets.Shell;

import com.amalto.workbench.dialogs.ValidationRuleExcpressDialog;
import com.amalto.workbench.models.TreeParent;
import com.amalto.workbench.widgets.SchematronExpressBuilder;


public class ValidationRuleExcpressDialogR extends ValidationRuleExcpressDialog {

    public ValidationRuleExcpressDialogR(Shell parentShell, TreeParent treeParent, String title, String value, String conceptName) {
        super(parentShell, treeParent, title, value, conceptName);
    }

    public ValidationRuleExcpressDialogR(Shell parentShell, TreeParent treeParent, String title, String value,
            String conceptName, boolean isAbsoluteXPath, boolean isSchematron) {
        super(parentShell, treeParent, title, value, conceptName, isAbsoluteXPath, isSchematron);
    }

    @Override
    protected SchematronExpressBuilder getBuilder() {
        return new SchematronExpressBuilderR(composite, value, conceptName, isAbsoluteXPath, isSchematron);
    }

}
