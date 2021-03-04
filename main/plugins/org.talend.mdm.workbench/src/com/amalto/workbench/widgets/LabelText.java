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
package com.amalto.workbench.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 *
 * @author aiming
 *
 */
public class LabelText {

    protected Label label;

    protected Text text;

    // WidgetFactory factory =WidgetFactory.getWidgetFactory();
    public LabelText(FormToolkit toolkit, Composite parent, final String labelName) {
        label = toolkit.createLabel(parent, labelName, SWT.NULL);
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, true, 1, 1));
        text = toolkit.createText(parent, "", SWT.BORDER);//$NON-NLS-1$
        text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        // text.addFocusListener(factory.focusListener);
        // Util.createCompDropTarget(text);
    }

    public Label getLabel() {
        return label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }

    public Text getText() {
        return text;
    }

    public void setText(String text) {
        this.text.setText(text);
    }

}
