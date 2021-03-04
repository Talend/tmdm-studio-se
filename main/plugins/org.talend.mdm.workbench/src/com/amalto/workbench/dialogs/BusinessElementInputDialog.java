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

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.amalto.workbench.exadapter.ExAdapterManager;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.utils.XSDUtil;

public class BusinessElementInputDialog extends Dialog {

    private Text elementNameText = null;

    private Combo refCombo = null;

    private Text minOccursText = null;

    private Text maxOccursText = null;

    private Collection<String> elementDeclarations = null;

    private Label msgLabel = null;

    private String elementName = "";//$NON-NLS-1$

    private String refName = "";//$NON-NLS-1$

    private int minOccurs = 0;

    private int maxOccurs = 1;

    private boolean isNew = false;

    private SelectionListener caller = null;

    private String title = "";//$NON-NLS-1$

    // fix 0010248
    private boolean inherit = true;

    public boolean isInherit() {
        return inherit;
    }

    public void setInherit(boolean inherit) {
        this.inherit = inherit;
    }

    // fix TMDM-3726
    private boolean isPK = false;

    private IBusinessElementInputDialogExAdapter exAdapter;

    public boolean isPK() {
        return isPK;
    }

    public void setPK(boolean isPK) {
        this.isPK = isPK;
    }

    /**
     * @param parentShell
     */
    public BusinessElementInputDialog(SelectionListener caller, Shell parentShell, String title, boolean isNew) {
        this(caller, parentShell, title, "", "", new ArrayList<String>(), 0, 1, isNew, false);//$NON-NLS-1$//$NON-NLS-2$
    }

    /**
     * @param parentShell
     */
    public BusinessElementInputDialog(SelectionListener caller, Shell parentShell, String title, String elementName,
            String refName, Collection<String> decls, int minOccurs, int maxOccurs, boolean isNew, boolean isPK) {
        super(parentShell);
        this.caller = caller;
        this.title = title;
        this.elementName = elementName;
        this.refName = refName;
        this.elementDeclarations = decls;
        this.minOccurs = minOccurs;
        this.maxOccurs = maxOccurs;
        this.isNew = isNew;
        this.isPK = isPK;
        this.exAdapter = ExAdapterManager.getAdapter(this, IBusinessElementInputDialogExAdapter.class);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        // Should not really be here but well,....
        parent.getShell().setText(this.title);

        Composite composite = (Composite) super.createDialogArea(parent);

        GridLayout layout = (GridLayout) composite.getLayout();
        layout.numColumns = 2;
        // layout.verticalSpacing = 10;

        Label nameLabel = new Label(composite, SWT.NONE);
        nameLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
        nameLabel.setText(Messages.BusinessElementInputDialog_NameText);

        elementNameText = new Text(composite, SWT.BORDER);
        elementNameText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        elementNameText.setText(getElementName() == null ? "" : getElementName());//$NON-NLS-1$
        ((GridData) elementNameText.getLayoutData()).widthHint = 200;
        // elementNameText.setSize(100, 22);

        Label refLabel = new Label(composite, SWT.NONE);
        refLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
        refLabel.setText(Messages.BusinessElementInputDialog_RefText);

        refCombo = new Combo(composite, SWT.DROP_DOWN | SWT.SIMPLE | SWT.READ_ONLY);
        refCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        refCombo.setItems(elementDeclarations.toArray(new String[elementDeclarations.size()]));
        refCombo.setText(getRefName() == null ? "" : getRefName());//$NON-NLS-1$
        ((GridData) refCombo.getLayoutData()).widthHint = 200;

        if (refCombo.getText().length() > 0) {
            elementNameText.setText("");//$NON-NLS-1$
            elementNameText.setEditable(false);
        } else {
            elementNameText.setEditable(true);
            elementNameText.setText(getElementName() == null ? "" : getElementName());//$NON-NLS-1$
        }

        refCombo.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(SelectionEvent e) {

            }

            public void widgetSelected(SelectionEvent e) {
                if (refCombo.getText().length() > 0) {
                    elementNameText.setText("");//$NON-NLS-1$
                    elementNameText.setEditable(false);
                } else {
                    elementNameText.setEditable(true);
                    elementNameText.setText(getElementName() == null ? "" : getElementName());//$NON-NLS-1$
                }

            }
        });

        Label minLabel = new Label(composite, SWT.NONE);
        minLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
        minLabel.setText(Messages.BusinessElementInputDialog_MinText);

        minOccursText = new Text(composite, SWT.NONE);
        minOccursText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        minOccursText.setDoubleClickEnabled(false);
        minOccursText.setText("" + getMinOccurs());//$NON-NLS-1$

        Label maxLabel = new Label(composite, SWT.NONE);
        maxLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
        maxLabel.setText(Messages.BusinessElementInputDialog_MaxText);

        maxOccursText = new Text(composite, SWT.NONE);
        maxOccursText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        maxOccursText.setText(getMaxOccurs() == -1 ? "" : "" + getMaxOccurs());//$NON-NLS-1$//$NON-NLS-2$

        if (isNew && exAdapter != null) {
            exAdapter.crateDialogArea(composite);
        }
        // check pk can't edit Maximum/Minimum
        minOccursText.setEditable(!isPK);
        maxOccursText.setEditable(!isPK);
        refCombo.setEnabled(!isPK);

        msgLabel = new Label(composite, SWT.NONE);
        GridDataFactory.fillDefaults().span(2, 1).applyTo(msgLabel);
        msgLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));

        elementNameText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                String text = elementNameText.getText().trim();
                boolean isValid = XSDUtil.isValidatedXSDName(text);
                isValid = isValid || !refCombo.getText().trim().isEmpty();
                msgLabel.setText(isValid ? "" : Messages.InvalidName_Message); //$NON-NLS-1$
                msgLabel.getParent().layout();
                getButton(IDialogConstants.OK_ID).setEnabled(isValid);
            }
        });
        return composite;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);
        getButton(IDialogConstants.OK_ID).addSelectionListener(this.caller);
        /*
         * createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true); createButton(parent,
         * IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
         */
    }

    @Override
    protected void okPressed() {
        elementName = elementNameText.getText().trim();
        refName = refCombo.getText();
        if (((elementName == null) || ("".equals(elementName))) && ((refName == null) || "".equals(refName))) {//$NON-NLS-1$//$NON-NLS-2$
            MessageDialog.openError(this.getShell(), Messages._Error, Messages.BusinessElementInputDialog_NameCannotbeEmptyIfXX);
            setReturnCode(-1);
            elementNameText.setFocus();
            return;
        }

        if (elementName.replaceAll("\\s", "").length() != elementName.length()) {//$NON-NLS-1$//$NON-NLS-2$
            MessageDialog.openError(this.getShell(), Messages._Error, Messages.BusinessElementInputDialog_NameCannotContainEmpty);
            setReturnCode(-1);
            elementNameText.setFocus();
            return;
        }

        if ("".equals(minOccursText.getText()) && "".equals(maxOccursText.getText())) {//$NON-NLS-1$//$NON-NLS-2$
            minOccurs = 1;
            maxOccurs = 1;
            return;
        }
        try {
            minOccurs = Integer.parseInt(minOccursText.getText());
        } catch (Exception e1) {
            MessageDialog.openError(this.getShell(), Messages._Error, Messages.BusinessElementInputDialog_ErrorMsg);
            setReturnCode(-1);
            minOccursText.setFocus();
            return;
        }
        if (minOccurs < 0) {
            MessageDialog.openError(this.getShell(), Messages._Error, Messages.BusinessElementInputDialog_ErrorMsg);
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
                MessageDialog.openError(this.getShell(), Messages._Error, Messages.BusinessElementInputDialog_ErrorMsg1);
                setReturnCode(-1);
                maxOccursText.setFocus();
                return;
            }
            if ((maxOccurs < minOccurs) || (maxOccurs <= 0)) {
                maxOccurs = -1;
            }
        }

        setReturnCode(OK);
        // no close let Action Handler handle it
    }

    public String getElementName() {
        return elementName;
    }

    public int getMaxOccurs() {
        return maxOccurs;
    }

    public int getMinOccurs() {
        return minOccurs;
    }

    public String getRefName() {
        return refName;
    }

}
