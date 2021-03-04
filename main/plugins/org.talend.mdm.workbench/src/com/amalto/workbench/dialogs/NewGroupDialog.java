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
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.amalto.workbench.i18n.Messages;

public class NewGroupDialog extends Dialog {

    protected Button sequenceButton = null;

    protected Button choiceButton = null;

    protected Button allButton = null;

    private SelectionListener caller = null;

    private int minOccurs = 1;

    private int maxOccurs = 1;

    private Text minOccursText;

    private Text maxOccursText;

    /**
     * @param parentShell
     */
    public NewGroupDialog(SelectionListener caller, Shell parentShell) {
        super(parentShell);
        this.caller = caller;
    }

    protected Control createDialogArea(Composite parent) {
        // Should not really be here but well,....
        parent.getShell().setText(Messages.NewGroupDialog_DialogTitle);

        final Composite composite = (Composite) super.createDialogArea(parent);

        GridLayout layout = (GridLayout) composite.getLayout();
        layout.numColumns = 2;
        // layout.verticalSpacing = 10;

        Group radioGroup = new Group(composite, SWT.SHADOW_NONE);
        radioGroup.setText(Messages.NewGroupDialog_SubElementGroup);
        radioGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 2, 1));
        radioGroup.setLayout(new GridLayout(1, false));
        allButton = new Button(radioGroup, SWT.RADIO);
        allButton.setText(Messages.NewGroupDialog_All);
        allButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
        allButton.setSelection(true);

        sequenceButton = new Button(radioGroup, SWT.RADIO);
        sequenceButton.setText(Messages.NewGroupDialog_Sequence);
        sequenceButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
        choiceButton = new Button(radioGroup, SWT.RADIO);
        choiceButton.setText(Messages.NewGroupDialog_Choice);
        choiceButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));

        Label minOccursLabel = new Label(composite, SWT.NONE);
        minOccursLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
        minOccursLabel.setText(Messages.NewGroupDialog_MinOccurrence);

        minOccursText = new Text(composite, SWT.NONE);
        minOccursText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        minOccursText.setDoubleClickEnabled(false);
        minOccursText.setText("" + getMinOccurs());//$NON-NLS-1$

        Label maxOccursLabel = new Label(composite, SWT.NONE);
        maxOccursLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
        maxOccursLabel.setText(Messages.NewGroupDialog_MaxOccurrence);

        maxOccursText = new Text(composite, SWT.NONE);
        maxOccursText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        maxOccursText.setText(getMaxOccurs() == -1 ? "" : "" + getMaxOccurs());//$NON-NLS-1$//$NON-NLS-2$

        return composite;
    }

    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);
        getButton(IDialogConstants.OK_ID).addSelectionListener(this.caller);
        /*
         * createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true); createButton(parent,
         * IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
         */
    }

    protected void okPressed() {
        if ("".equals(minOccursText.getText()) && "".equals(maxOccursText.getText())) {//$NON-NLS-1$//$NON-NLS-2$
            minOccurs = 1;
            maxOccurs = 1;
            return;
        }
        try {
            minOccurs = Integer.parseInt(minOccursText.getText());
        } catch (Exception e1) {
            MessageDialog.openError(this.getShell(), Messages._Error, Messages.NewGroupDialog_ErrorMsg);
            setReturnCode(-1);
            minOccursText.setFocus();
            return;
        }
        if (minOccurs < 0) {
            MessageDialog.openError(this.getShell(), Messages._Error, Messages.NewGroupDialog_ErrorMsg);
            setReturnCode(-1);
            minOccursText.setFocus();
            return;
        }

        if ("".equals(maxOccursText.getText())) {//$NON-NLS-1$
            maxOccurs = -1;
        } else {
            try {
                maxOccurs = Integer.parseInt(maxOccursText.getText());
            } catch (Exception e2) {
                MessageDialog.openError(this.getShell(), Messages._Error, Messages.NewGroupDialog_ErrorMsg1);
                setReturnCode(-1);
                maxOccursText.setFocus();
                return;
            }
            if ((maxOccurs < minOccurs) || (maxOccurs <= 0))
                maxOccurs = -1;
        }

        setReturnCode(OK);
        // no close let Action Handler handle it
    }

    public boolean isSequence() {
        return sequenceButton.getSelection();
    }

    public boolean isChoice() {
        return choiceButton.getSelection();
    }

    public boolean isAll() {
        return allButton.getSelection();
    }

    public int getMaxOccurs() {
        return maxOccurs;
    }

    public int getMinOccurs() {
        return minOccurs;
    }

}
