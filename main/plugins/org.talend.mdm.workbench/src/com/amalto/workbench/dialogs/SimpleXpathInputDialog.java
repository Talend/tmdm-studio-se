// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
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
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.amalto.workbench.dialogs.datamodel.IXPathSelectionFilter;
import com.amalto.workbench.dialogs.filter.SimpleXPathSelectionFilter;
import com.amalto.workbench.editors.DataModelMainPage;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;

public class SimpleXpathInputDialog extends Dialog {

    private DataModelMainPage parentPage;

    private String title = "";//$NON-NLS-1$

    private String dialogMessage = "";//$NON-NLS-1$

    private String initialValue = "";//$NON-NLS-1$

    private SelectionListener caller = null;

    private String xpath = "";//$NON-NLS-1$

    private boolean sepFk = false;

    private String dataModelName;

    private Button btnSep;
    
    private boolean lock;

    /**
     * @param parentShell
     */
    public SimpleXpathInputDialog(DataModelMainPage parentPage, String title, String dialogMessage, String initialValue,
            SelectionListener caller, String dataModelName) {
        super(parentPage.getSite().getShell());
        this.parentPage = parentPage;
        this.title = title;
        this.dialogMessage = dialogMessage;
        this.initialValue = initialValue;
        this.caller = caller;
        this.dataModelName = dataModelName;
    }

    public void setFkSep(boolean sepFk) {
        this.sepFk = sepFk;
    }
    @Override
    protected Control createDialogArea(Composite parent) {
        // Should not really be here but well,....
        parent.getShell().setText(this.title);

        Composite composite = (Composite) super.createDialogArea(parent);
        GridLayout layout = (GridLayout) composite.getLayout();
        layout.numColumns = 2;

        Label dialogMessageLbl = new Label(composite, SWT.NULL);
        dialogMessageLbl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
        dialogMessageLbl.setText(dialogMessage);

        final Text textControl = new Text(composite, SWT.BORDER | SWT.SINGLE);
        textControl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        ((GridData) textControl.getLayoutData()).minimumWidth = 300;
        textControl.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                SimpleXpathInputDialog.this.xpath = textControl.getText();
                btnSep.setEnabled(xpath != null && xpath.length() > 0);
            }
        });

        Button xpathButton = new Button(composite, SWT.PUSH | SWT.CENTER);
        xpathButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
        xpathButton.setImage(ImageCache.getCreatedImage(EImage.DOTS_BUTTON.getPath()));
        xpathButton.setToolTipText(Messages.SchematronExpressBuilder_selectXPath);
        xpathButton.addSelectionListener(new SelectionAdapter() {
            private IXPathSelectionFilter xpathSelectionFilter;

            @Override
            public void widgetSelected(SelectionEvent e) {
                XpathSelectDialog dlg = getNewXpathSelectDialog(parentPage, dataModelName);
                // new XpathSelectDialog(parentPage.getSite().getShell(), parentPage.getXObject()
                // .getParent(), "Select Xpath", parentPage.getSite(), false, dataModelName);
                dlg.setSelectionFilter(getXPathSelectionFilter());
                dlg.setLock(lock);
                dlg.setBlockOnOpen(true);
                dlg.open();

                if (dlg.getReturnCode() == Window.OK) {
                    textControl.setText(dlg.getXpath());
                    dlg.close();
                }

            }

            private IXPathSelectionFilter getXPathSelectionFilter() {
                if(xpathSelectionFilter == null) {
                    xpathSelectionFilter = new SimpleXPathSelectionFilter();
                }

                return xpathSelectionFilter;
            }

        });

        textControl.forceFocus();

        btnSep = new Button(composite, SWT.CHECK);
        btnSep.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        btnSep.setText(Messages.SimpleXpathInputDialog_sepFkTabPanel);
        btnSep.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                sepFk = btnSep.getSelection();
            }
        });

        // init value
        textControl.setText(initialValue == null ? "" : initialValue);//$NON-NLS-1$
        btnSep.setSelection(sepFk);
        return composite;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);
        getButton(IDialogConstants.OK_ID).addSelectionListener(this.caller);
        getButton(IDialogConstants.CANCEL_ID).addSelectionListener(this.caller);
    }

    protected XpathSelectDialog getNewXpathSelectDialog(DataModelMainPage parentPage, String dataModelName) {
        return new XpathSelectDialog(parentPage.getSite().getShell(), parentPage.getXObject().getParent(), Messages.SimpleXpathInputDialog_SelectXpath,
                parentPage.getSite(), false, dataModelName);
    }
    @Override
    protected void okPressed() {
        setReturnCode(OK);
        getButton(IDialogConstants.OK_ID).setData("dialog", SimpleXpathInputDialog.this);//$NON-NLS-1$
        // no close let Action Handler handle it
    }

    @Override
    protected void cancelPressed() {
        setReturnCode(CANCEL);
        getButton(IDialogConstants.CANCEL_ID).setData("dialog", SimpleXpathInputDialog.this);//$NON-NLS-1$
        // no close let Action Handler handle it
    }

    public String getXpath() {
        return xpath;
    }

    public boolean getSepFk() {
        return sepFk;
    }

	public boolean isLock() {
		return lock;
	}

	public void setLock(boolean lock) {
		this.lock = lock;
	}
}
