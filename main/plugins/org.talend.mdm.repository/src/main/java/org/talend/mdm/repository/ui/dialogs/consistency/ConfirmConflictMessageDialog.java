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
package org.talend.mdm.repository.ui.dialogs.consistency;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IconAndMessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.talend.mdm.repository.core.service.ConsistencyService;
import org.talend.mdm.repository.i18n.Messages;

/**
 * created by HHB on 2013-7-25 Detailled comment
 *
 */
public class ConfirmConflictMessageDialog extends IconAndMessageDialog {

    private Button defaultBun;

    private Button overrideBun;

    private Button skipBun;

    private Button rememberBun;

    public ConfirmConflictMessageDialog(Shell parentShell, int conflictCount) {
        super(parentShell);
        this.message = Messages.bind(Messages.ConfirmConflictMessageDialog_dialogMessage, conflictCount);
    }

    @Override
    protected Image getImage() {
        return getWarningImage();
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite dialogAreaComposite = (Composite) super.createDialogArea(parent);
        ((GridData) dialogAreaComposite.getLayoutData()).horizontalSpan = 2;
        createMessageArea(dialogAreaComposite);
        createStrategyGroup(dialogAreaComposite);
        return dialogAreaComposite;
    }

    @Override
    protected Control createMessageArea(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).applyTo(composite);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(composite);

        Control control = super.createMessageArea(composite);
        return control;
    }

    private void createStrategyGroup(Composite dialogAreaComposite) {
        Group grpConflictStr = new Group(dialogAreaComposite, SWT.NONE);
        grpConflictStr.setText(Messages.ConsistencyConflict_conflictStrategy);
        grpConflictStr.setLayout(new GridLayout(1, false));
        grpConflictStr.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        defaultBun = new Button(grpConflictStr, SWT.RADIO);
        defaultBun.setSelection(true);
        defaultBun.setText(Messages.ConsistencyConflict_defaultStrategyLabel);

        overrideBun = new Button(grpConflictStr, SWT.RADIO);
        overrideBun.setText(Messages.ConsistencyConflict_overrideStrategyLabel);

        skipBun = new Button(grpConflictStr, SWT.RADIO);
        skipBun.setText(Messages.ConsistencyConflict_skipStrategyLabel);
        rememberBun = new Button(dialogAreaComposite, SWT.CHECK);
        rememberBun.setText(Messages.ConfirmConflictMessageDialog_rememberChoice);
    }

    private int getSelectedStrategy() {
        if (defaultBun.getSelection()) {
            return ConsistencyService.CONFLICT_STRATEGY_DEFAULT;
        }
        if (overrideBun.getSelection()) {
            return ConsistencyService.CONFLICT_STRATEGY_OVERWRITE;
        }
        if (skipBun.getSelection()) {
            return ConsistencyService.CONFLICT_STRATEGY_SKIP_DIFFERENCE;
        }
        return ConsistencyService.CONFLICT_STRATEGY_DEFAULT;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.DETAILS_ID, Messages.ConfirmConflictMessageDialog_showConflicts, false);
        super.createButtonsForButtonBar(parent);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
     */
    @Override
    protected void buttonPressed(int buttonId) {
        ConsistencyService service = ConsistencyService.getInstance();
        if (buttonId == IDialogConstants.OK_ID) {
            selectedStrategy = getSelectedStrategy();
        } else if (buttonId == IDialogConstants.DETAILS_ID) {
            setReturnCode(IDialogConstants.DETAILS_ID);
            close();
            return;
        }
        if (!rememberBun.getSelection()) {
            service.setWarnUserWhenConflict(true);

        } else {
            service.setWarnUserWhenConflict(false);
            service.setConflictStrategy(selectedStrategy);
        }

        super.buttonPressed(buttonId);
    }

    int selectedStrategy = ConsistencyService.CONFLICT_STRATEGY_DEFAULT;

    public int getStrategy() {
        return selectedStrategy;
    }

}
