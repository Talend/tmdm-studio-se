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
package org.talend.mdm.repository.ui.dialogs;

import org.eclipse.swt.widgets.Shell;
import org.talend.mdm.repository.ui.widgets.SchematronExpressBuilderR;

import com.amalto.workbench.dialogs.ValidationRuleExcpressDialog;
import com.amalto.workbench.models.TreeParent;
import com.amalto.workbench.widgets.SchematronExpressBuilder;


/**
 * DOC achen  class global comment. Detailled comment
 */
public class ValidationRuleExcpressDialog2 extends ValidationRuleExcpressDialog {

    /**
     * DOC achen ValidationRuleExcpressDialog2 constructor comment.
     *
     * @param parentShell
     * @param treeParent
     * @param title
     * @param value
     * @param conceptName
     * @param isAbsoluteXPath
     * @param isSchematron
     */
    public ValidationRuleExcpressDialog2(Shell parentShell, TreeParent treeParent, String title, String value,
            String conceptName, boolean isAbsoluteXPath, boolean isSchematron) {
        super(parentShell, treeParent, title, value, conceptName, isAbsoluteXPath, isSchematron);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.amalto.workbench.dialogs.ValidationRuleExcpressDialog#getBuilder()
     */
    @Override
    protected SchematronExpressBuilder getBuilder() {

        return new SchematronExpressBuilderR(composite, value, conceptName, isAbsoluteXPath, isSchematron);
    }
}
