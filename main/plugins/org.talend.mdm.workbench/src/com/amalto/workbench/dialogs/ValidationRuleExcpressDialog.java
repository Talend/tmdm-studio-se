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
package com.amalto.workbench.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.amalto.workbench.models.TreeParent;
import com.amalto.workbench.widgets.SchematronExpressBuilder;

public class ValidationRuleExcpressDialog extends Dialog {

    private String title;

    private SchematronExpressBuilder builder;

    protected String value;

    String express;

    protected String conceptName;

    protected boolean isAbsoluteXPath = false;
    // Modified by hbhong,to fix bug 21784, add a treeParent field to receive TreeParent object
    protected TreeParent treeParent;

    // schematron using XPathFunc.xml, others using StandardXPathFunc.xml
    protected boolean isSchematron;

    protected Composite composite;

    public ValidationRuleExcpressDialog(Shell parentShell, TreeParent treeParent,String title, String value, String conceptName) {
        this(parentShell, treeParent, title, value, conceptName, false, true);
    }

    public ValidationRuleExcpressDialog(Shell parentShell, TreeParent treeParent, String title, String value, String conceptName,
            boolean isAbsoluteXPath, boolean isSchematron) {
        super(parentShell);
        this.treeParent=treeParent;
        this.title = title;
        this.value = value;
        this.isSchematron = isSchematron;
        this.conceptName = conceptName;
        this.isAbsoluteXPath = isAbsoluteXPath;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        parent.getShell().setText(this.title);
        composite = (Composite) super.createDialogArea(parent);
        builder = getBuilder();
        builder.setTreeParent(treeParent);
        return composite;
    }
    // The ending| bug:21784
    @Override
    protected void okPressed() {
        express = builder.getText();
        super.okPressed();
    }

    public String getExpression() {
        return express;
    }

    @Override
    protected boolean isResizable() {
        return false;
    }

    protected SchematronExpressBuilder getBuilder() {
        return new SchematronExpressBuilder(composite, value, conceptName, isAbsoluteXPath, isSchematron);
    }
}
