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
package org.talend.mdm.repository.ui.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.talend.core.ui.context.view.Contexts;
import org.talend.mdm.repository.i18n.Messages;

/**
 * DOC HHB class global comment. Detailled comment
 */
public class SwitchPerspectiveDialog extends MessageDialog {

    private final String perspectiveId;

    private final String prefWarnUserSwitchId;

    private final String prefSwitchId;

    private Button checkBun;

    /**
     * DOC HHB SwitchPerspectiveDialog constructor comment.
     *
     * @param parentShell
     * @param dialogTitle
     * @param dialogTitleImage
     * @param dialogMessage
     * @param dialogImageType
     * @param dialogButtonLabels
     * @param defaultIndex
     */
    public SwitchPerspectiveDialog(Shell parentShell, String perspectiveName, String perspectiveId, String prefSwitchId,
            String prefWarnUserSwitchId) {
        super(parentShell, Messages.SwitchPerspectiveDialog_title, null, getDialogMessage(perspectiveName),
                MessageDialog.QUESTION, new String[] { IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL }, 0);
        this.perspectiveId = perspectiveId;
        this.prefSwitchId = prefSwitchId;
        this.prefWarnUserSwitchId = prefWarnUserSwitchId;
    }

    public void run() {
        askUser = needAskUser();
        boolean canSwitch = false;
        if (askUser) {
            int result = open();
            canSwitch = result == IDialogConstants.OK_ID;
        } else {
            canSwitch = canSwitch();
        }
        //
        if (!askUser) {
            saveUserConfig(canSwitch);
        }
        // run
        if (canSwitch) {
            doSwitch();
        }
    }

    @Override
    protected Control createCustomArea(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout());
        checkBun = new Button(composite, SWT.CHECK);
        checkBun.addSelectionListener(selectionListener);
        checkBun.setText(Messages.SwitchPerspectiveDialog_notAskAgain);
        checkBun.setSelection(!needAskUser());
        return composite;
    }

    private boolean askUser = true;

    private SelectionListener selectionListener = new SelectionAdapter() {

        @Override
        public void widgetSelected(SelectionEvent e) {
            Button button = (Button) e.widget;
            askUser = !button.getSelection();

        }
    };

    /**
     * DOC HHB Comment method "doSwitch".
     */
    private void doSwitch() {
        IPerspectiveDescriptor perspective = PlatformUI.getWorkbench().getPerspectiveRegistry()
                .findPerspectiveWithId(perspectiveId);
        if (perspective != null) {
            IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
            if (activePage != null) {
                activePage.setPerspective(perspective);
                Contexts.switchToCurContextsView();
            }
        }
    }

    private boolean canSwitch() {
        boolean canSwitch = PlatformUI.getPreferenceStore().getBoolean(prefSwitchId);
        return canSwitch;
    }

    private boolean needAskUser() {
        boolean needAskUser = !PlatformUI.getPreferenceStore().getBoolean(prefWarnUserSwitchId);
        return needAskUser;
    }

    private void saveUserConfig(boolean switchTo) {
        IPreferenceStore preferenceStore = PlatformUI.getPreferenceStore();
        preferenceStore.setValue(prefSwitchId, switchTo);
        preferenceStore.setValue(prefWarnUserSwitchId, !askUser);
    }

    /**
     * DOC HHB Comment method "getDialogMessage".
     *
     * @param perspectiveName
     * @return
     */
    private static String getDialogMessage(String perspectiveName) {
        return Messages.bind(Messages.SwitchPerspectiveDialog_message, perspectiveName);
    }

}
