// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.workbench.dialogs.datacontainer;

import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.w3c.dom.Node;

import com.amalto.workbench.dialogs.DOMViewDialog;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.webservices.TMDMService;
import com.amalto.workbench.webservices.TMDMService;

/**
 * created by HHB on 2012-11-19 Detailled comment
 * 
 */
public class DataContainerDOMViewDialog extends DOMViewDialog {

    private Button triggerBtn;

    private Button beforeBtn;

    private boolean isMaster;

    /**
     * DOC HHB DataContainerDOMViewDialog constructor comment.
     * 
     * @param parentShell
     * @param service
     * @param node
     * @param editable
     * @param dataModelNames
     * @param firstTab
     * @param selectedDataModel
     */
    public DataContainerDOMViewDialog(Shell parentShell, TMDMService service, Node node, Collection<String> dataModelNames,
            int firstTab, String selectedDataModel, boolean isMaster) {
        super(parentShell, service, node, true, dataModelNames, firstTab, selectedDataModel);
        this.isMaster = isMaster;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.amalto.workbench.dialogs.DOMViewDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createDialogArea(Composite parent) {

        Composite composite = (Composite) super.createDialogArea(parent);
        if (isMaster) {
            triggerBtn = new Button(composite, SWT.CHECK);
            triggerBtn.setText(Messages.DOMViewDialog_TriggerBtnText);
            triggerBtn.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    beforeBtn.setEnabled(triggerBtn.getSelection());
                }
            });
            beforeBtn = new Button(composite, SWT.CHECK);
            beforeBtn.setText(Messages.DOMViewDialog_BeforeBtnText);
            beforeBtn.setEnabled(triggerBtn.getSelection());
        }
        return composite;
    }

    public boolean isTriggerProcess() {
        return triggerBtn == null ? false : triggerBtn.getSelection();
    }

    public boolean isBeforeVerification() {
        return beforeBtn == null ? false : beforeBtn.getSelection();
    }
}
