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
package com.amalto.workbench.widgets.composites;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.amalto.workbench.detailtabs.sections.BasePropertySection;

public class TextboxListStringContentComposite extends ListStringContentsComposite {

    protected Text txtInput;

    protected String colTitle;

    public TextboxListStringContentComposite(Composite parent, int style, String colTitle,BasePropertySection section) {
        super(parent, style, new Object[] { colTitle },section);
    }

    protected TextboxListStringContentComposite(Composite parent, int style, Object[] initParas,BasePropertySection section) {
        super(parent, style, initParas,section);
    }

    @Override
    protected String getInfoColTitle() {
        return colTitle;
    }

    @Override
    protected void createExtentUIArea(Composite parent) {
    }

    @Override
    protected void createCandidateInfoUIArea(Composite parent) {
        txtInput = new Text(this, SWT.BORDER);
        txtInput.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    }

    @Override
    protected boolean hasCandidateInfo() {
        return !"".equals(txtInput.getText().trim());//$NON-NLS-1$
    }

    @Override
    protected String getCandidateInfo() {
        return txtInput.getText().trim();
    }

    @Override
    protected void initCandidateInfoUIArea() {
        txtInput.setText("");//$NON-NLS-1$
    }

    @Override
    protected void initParas(Object[] paras) {

        for (Object eachPara : paras) {

            if (eachPara instanceof String)
                this.colTitle = (String) eachPara;
        }

    }

}
