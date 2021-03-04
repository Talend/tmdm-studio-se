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

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.amalto.workbench.dialogs.ValidationRuleExcpressDialog;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.models.TreeParent;

public class ValidationRuleWidget {

    Composite parent;

    FormToolkit toolkit = WidgetFactory.getWidgetFactory();

    private Composite composite;

    private Button button;

    protected Text text;

    protected ValidationRuleExcpressDialog dlg;

    protected String conceptName;

    private GridData textGD;

    TableColumn column;

    // Modified by hbhong,to fix bug 21784|Add a TreeParent parameter to constructor
    protected final TreeParent treeParent;

    public ValidationRuleWidget(Composite parent, TreeParent treeParent,String conceptName) {
        this.parent = parent;
        this.treeParent = treeParent;
        this.conceptName = conceptName;
        create();
    }
    // The ending| bug:21784
    private void create() {
        composite = toolkit.createComposite(parent, SWT.NO_FOCUS);
        GridLayout layout = new GridLayout(2, false);
        layout.marginWidth = 0;
        layout.marginLeft = 0;
        layout.marginTop = 0;
        layout.marginHeight = 0;
        layout.marginBottom = 0;
        composite.setLayout(layout);

        text = toolkit.createText(composite, "", SWT.BORDER | SWT.MULTI | SWT.LEFT);//$NON-NLS-1$
        textGD = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        text.setLayoutData(textGD);
        button = toolkit.createButton(composite, "", SWT.PUSH);//$NON-NLS-1$
        button.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));

        button.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                Shell shell = Display.getDefault().getActiveShell();

                createValidationRuleExcpressDialog(shell);

                dlg.create();
                dlg.getShell().setMaximized(false);
                dlg.setBlockOnOpen(true);
                int ret = dlg.open();
                if (ret == Window.OK) {
                    text.setText(dlg.getExpression());
                }
            }
        });
        button.setImage(ImageCache.getCreatedImage(EImage.DOTS_BUTTON.getPath()));
        button.setToolTipText(Messages.BuildValidationRuleExpression);
    }

    protected void createValidationRuleExcpressDialog(Shell shell) {
        dlg = new ValidationRuleExcpressDialog(shell, treeParent, Messages.BuildValidationRuleExpression, text.getText(),
                conceptName, false, true);

    }
    public Composite getComposite() {
        return composite;
    }

    public Text getTextWidget() {
        return text;
    }

    public String getText() {
        return text.getText();
    }

    public void setText(String txt) {
        text.setText(txt);
    }

    public TableColumn getColumn() {
        return column;
    }

    public void setColumn(TableColumn column) {
        this.column = column;
        resetWidth();
    }

    public void resetWidth() {
        if (column != null) {
            textGD.widthHint = column.getWidth() - 35;
        }
    }
}
