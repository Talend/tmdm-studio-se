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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.amalto.workbench.editors.AMainPageV2;
import com.amalto.workbench.i18n.Messages;

public class ConditionWidget {

    private Text conditionText;

    private AMainPageV2 page;

    public ConditionWidget(Composite parent, FormToolkit toolkit, AMainPageV2 page) {
        this.page = page;
        Group conditionComposite = new Group(parent, SWT.NONE);
        conditionComposite.setBackground(parent.getBackground());
        conditionComposite.setText(Messages.ConditionWidget_Conditions);
        conditionComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        conditionComposite.setLayout(new GridLayout(3, false));

        conditionText = toolkit.createText(conditionComposite, "", SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);//$NON-NLS-1$
        conditionText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 3));

        Composite conditionBtnComposite = toolkit.createComposite(conditionComposite, SWT.NULL);
        conditionBtnComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, true, 1, 1));
        conditionBtnComposite.setLayout(new GridLayout(5, false));
        ButtonListenr listener = new ButtonListenr();
        Button btnLeft = toolkit.createButton(conditionBtnComposite, "(", SWT.PUSH);//$NON-NLS-1$
        btnLeft.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, true, 1, 1));
        btnLeft.setData("(");//$NON-NLS-1$
        btnLeft.addSelectionListener(listener);

        Button btnRight = toolkit.createButton(conditionBtnComposite, ")", SWT.PUSH);//$NON-NLS-1$
        btnRight.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, true, 1, 1));
        btnRight.setData(")");//$NON-NLS-1$
        btnRight.addSelectionListener(listener);

        Button btnAnd = toolkit.createButton(conditionBtnComposite, "And", SWT.PUSH);//$NON-NLS-1$
        btnAnd.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, true, 1, 1));
        btnAnd.setData("&&");//$NON-NLS-1$
        btnAnd.addSelectionListener(listener);

        Button btnOr = toolkit.createButton(conditionBtnComposite, "Or", SWT.PUSH);//$NON-NLS-1$
        btnOr.setData("||");//$NON-NLS-1$
        btnOr.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, true, 1, 1));
        btnOr.addSelectionListener(listener);

        Button btnNot = toolkit.createButton(conditionBtnComposite, "Not", SWT.PUSH);//$NON-NLS-1$
        btnNot.setData("!");//$NON-NLS-1$
        btnNot.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, true, 1, 1));
        btnNot.addSelectionListener(listener);

    }

    class ButtonListenr implements SelectionListener {

        public void widgetDefaultSelected(SelectionEvent e) {


        }

        public void widgetSelected(SelectionEvent e) {

            if (e.widget instanceof Button) {
                Button btn = (Button) e.widget;
                String condition = conditionText.getText() + " " + btn.getText();//$NON-NLS-1$
                conditionText.setText(condition);
                conditionText.setFocus();
                page.markDirty();
            }
        }
    }

    public Text getConditionText() {
        return conditionText;
    }

    public void setConditionText(Text conditionText) {
        this.conditionText = conditionText;
    }

}
