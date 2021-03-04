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
package org.talend.mdm.repository.ui.wizards.process.composite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.ui.wizards.process.ConfigReturnMessagePage;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class BeforeProcessTypeComposite extends AbstractProcessTypeComposite {

    private Button beforeSaveBun;

    private Button beforeDeleteBun;

    /**
     * DOC hbhong BeforeProcessTypeComposite constructor comment.
     *
     * @param parent
     */
    public BeforeProcessTypeComposite(Composite parent, int defaultProcessType, SelectionListener selectionListener) {
        super(parent, selectionListener);
        setLayout(new GridLayout(1, false));

        beforeSaveBun = new Button(this, SWT.RADIO);
        beforeSaveBun.addSelectionListener(selectionListener);
        beforeSaveBun.setText(Messages.BeforeProcessTypeComposite_createBeforeSavingProcess);

        beforeDeleteBun = new Button(this, SWT.RADIO);
        beforeDeleteBun.setText(Messages.BeforeProcessTypeComposite_createBeforeDeletingProcess);
        beforeDeleteBun.addSelectionListener(selectionListener);

        updateBtnState(defaultProcessType);
    }

    public void updateBtnState(int defaultProcessType) {
        if (defaultProcessType == TYPE_BEFORESAVE) {
            beforeSaveBun.setSelection(true);
            beforeDeleteBun.setSelection(false);
            beforeDeleteBun.setEnabled(false);
        } else if (defaultProcessType == TYPE_BEFOREDEL) {
            beforeDeleteBun.setSelection(true);
            beforeSaveBun.setSelection(false);
            beforeSaveBun.setEnabled(false);
        } else {
            beforeDeleteBun.setSelection(false);
            beforeSaveBun.setSelection(true);
        }
    }

    public int getCurrentProcessType() {
        if (beforeSaveBun.getSelection())
            return TYPE_BEFORESAVE;
        if (beforeDeleteBun.getSelection())
            return TYPE_BEFOREDEL;
        return -1;
    }

    public boolean needShowSelectEntityBun() {
        return true;
    }

    public String getProcessPrefix() {
        switch (getCurrentProcessType()) {
        case TYPE_BEFORESAVE:
            return "beforeSaving_"; //$NON-NLS-1$
        case TYPE_BEFOREDEL:
            return "beforeDeleting_"; //$NON-NLS-1$
        }
        return ""; //$NON-NLS-1$
    }

    public String getConfigWizardPageId() {
        return ConfigReturnMessagePage.PAGE_ID;
    }

    public String getDesc() {
        return null;
    }
}
