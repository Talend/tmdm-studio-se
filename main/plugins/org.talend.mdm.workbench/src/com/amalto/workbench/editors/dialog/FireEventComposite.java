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
package com.amalto.workbench.editors.dialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.amalto.workbench.i18n.Messages;


/**
 * created by liusongbo on Oct 20, 2014
 */
public class FireEventComposite extends Composite {

    private Button fireEvnBtn;

    private Text srcText;

    private Button invokeBtn;

    public FireEventComposite(Composite parent, int style) {
        super(parent, style);
        GridLayout layout = new GridLayout();
        setLayout(layout);
        create();
    }

    private void create() {
        Composite customArea = new Composite(this, SWT.NONE);
        GridLayout layout = new GridLayout(3, false);
        layout.marginWidth = 0;
        customArea.setLayout(layout);
        customArea.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

        fireEvnBtn = new Button(customArea, SWT.CHECK);
        fireEvnBtn.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));
        fireEvnBtn.setText(Messages.FireEventComposite_fireEvent);

        Label srcLabel = new Label(customArea, SWT.NONE);
        srcLabel.setText(Messages.FireEventComposite_sourceName);

        srcText = new Text(customArea, SWT.BORDER | SWT.SINGLE);
        GridData layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        srcText.setLayoutData(layoutData);
        srcText.setEnabled(false);

        invokeBtn = new Button(customArea, SWT.CHECK);
        invokeBtn.setText(Messages.FireEventComposite_invokeBeforeDelete);
        invokeBtn.setEnabled(false);

        addListener();
    }

    private void addListener() {
        fireEvnBtn.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                srcText.setText("genericUI"); //$NON-NLS-1$

                boolean isFire = fireEvnBtn.getSelection();
                if (!isFire) {
                    srcText.setText(""); //$NON-NLS-1$
                    invokeBtn.setSelection(false);
                }

                srcText.setEnabled(isFire);
                invokeBtn.setEnabled(isFire);
            }
        });

    }

    public boolean isFireEvent() {
        return fireEvnBtn.getSelection();
    }

    public boolean isInvokeBeforeProcess() {
        return invokeBtn.getSelection();
    }

    public String getSource() {
        return srcText.getText().trim();
    }

}
