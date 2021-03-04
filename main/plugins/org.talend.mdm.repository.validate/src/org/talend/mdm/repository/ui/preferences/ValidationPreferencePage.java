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
package org.talend.mdm.repository.ui.preferences;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.talend.mdm.repository.core.service.IModelValidationService;
import org.talend.mdm.repository.core.validate.ValidationPreferenceService;
import org.talend.mdm.repository.core.validate.i18n.Messages;

public class ValidationPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    private Button showDialogAfterImmediateButton;

    private Button showDialogAfterSavedButton;

    private Button showDialogBeforeDeployButton;

    private Button cancelButton;

    private Button skipErrorsButton;

    private Button skipErrorsAndWarningsButton;

    public ValidationPreferencePage() {
    }

    public ValidationPreferencePage(String title) {
        super(title);
    }

    public ValidationPreferencePage(String title, ImageDescriptor image) {
        super(title, image);
    }

    @Override
    public void init(IWorkbench workbench) {
        IPreferenceStore prefStore = PlatformUI.getPreferenceStore();
        setPreferenceStore(prefStore);
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        doCreateContent(composite);
        initControls();
        return composite;
    }


    protected void doCreateContent(Composite parent) {
        Composite immediateComposite = createExpandComposite(parent, Messages.ValidationPrefPage_RightClick_Title, true);
        showDialogAfterImmediateButton = new Button(immediateComposite, SWT.CHECK);
        showDialogAfterImmediateButton.setText(Messages.ValidationPrefPage_Button_Text);

        Composite savedComposite = createExpandComposite(parent, Messages.ValidationPreferencePage_SavingDialog_Title, true);
        showDialogAfterSavedButton = new Button(savedComposite, SWT.CHECK);
        showDialogAfterSavedButton.setText(Messages.ValidationPreferencePage_SavingButton_Text);

        Composite deployComposite = createExpandComposite(parent, Messages.ValidationPreferencePage_Before_Deplying_Title, true);
        GridLayoutFactory.fillDefaults().extendedMargins(10, 10, 0, 0).applyTo(deployComposite);
        showDialogBeforeDeployButton = new Button(deployComposite, SWT.CHECK);
        showDialogBeforeDeployButton.setText(Messages.ValidationPreferencePage_Before_Deplying_Text);
        final Group deployGroup = new Group(deployComposite, SWT.NONE);
        deployGroup.setText(Messages.ValidationPreferencePage_DeplyGroup_Text);
        deployGroup.setLayout(new GridLayout());
        deployGroup.setLayoutData(new GridData());
        cancelButton = new Button(deployGroup, SWT.RADIO);
        cancelButton.setText(Messages.ValidationPreferencePage_CancelButton_Text);
        skipErrorsButton = new Button(deployGroup, SWT.RADIO);
        skipErrorsButton.setText(Messages.ValidationPreferencePage_SkipErrorsButton_Text);
        skipErrorsAndWarningsButton = new Button(deployGroup, SWT.RADIO);
        skipErrorsAndWarningsButton.setText(Messages.ValidationPreferencePage_SkipErrorsAndWarningsButton_Text);
        showDialogBeforeDeployButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                updateDeplyWayButtons();
            }
        });
    }

    private Composite createExpandComposite(final Composite parent, String title, boolean needExpanded) {
        ExpandableComposite expComposite = new ExpandableComposite(parent, ExpandableComposite.TREE_NODE
                | ExpandableComposite.EXPANDED);
        expComposite.setLayout(new GridLayout());
        expComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        Composite composite = new Composite(expComposite, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginLeft = 10;
        layout.marginRight = 10;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        expComposite.setClient(composite);
        expComposite.setText(title);
        expComposite.setExpanded(needExpanded);
        expComposite.addExpansionListener(new ExpansionAdapter() {

            @Override
            public void expansionStateChanged(ExpansionEvent e) {
                parent.layout();
            }
        });
        return composite;
    }

    private void initControls() {
        showDialogAfterImmediateButton.setSelection(ValidationPreferenceService.getInstance()
                .isShowDlgAfterImmediateChecking());
        showDialogAfterSavedButton.setSelection(ValidationPreferenceService.getInstance().isShowDlgAfterSaving());
        showDialogBeforeDeployButton.setSelection(ValidationPreferenceService.getInstance().isShowDlgBeforeDeploying());

        int value = ValidationPreferenceService.getInstance().getDeployActionWhenValidateFail();
        switch (value) {
        case IModelValidationService.BUTTON_CANCEL:
            cancelButton.setSelection(true);
            break;
        case IModelValidationService.BUTTON_SKIP_ERROR:
            skipErrorsButton.setSelection(true);
            break;
        case IModelValidationService.BUTTON_SKIP_ERROR_WARNING:
            skipErrorsAndWarningsButton.setSelection(true);
            break;
        }
        updateDeplyWayButtons();
    }

    private void updateDeplyWayButtons() {
        boolean selected = showDialogBeforeDeployButton.getSelection();
        cancelButton.setEnabled(!selected);
        skipErrorsButton.setEnabled(!selected);
        skipErrorsAndWarningsButton.setEnabled(!selected);
    }

    @Override
    public boolean performOk() {
        ValidationPreferenceService.getInstance().setShowDlgAfterImmediateChecking(
                showDialogAfterImmediateButton.getSelection());
        ValidationPreferenceService.getInstance().setShowDlgAfterSaving(showDialogAfterSavedButton.getSelection());
        ValidationPreferenceService.getInstance().setShowDlgBeforeDeploying(showDialogBeforeDeployButton.getSelection());
        ValidationPreferenceService.getInstance().setDeployActionWhenValidateFail(getDeployWayValue());
        return super.performOk();
    }

    private int getDeployWayValue() {
        if (cancelButton.getSelection()) {
            return IModelValidationService.BUTTON_CANCEL;
        } else if (skipErrorsButton.getSelection()) {
            return IModelValidationService.BUTTON_SKIP_ERROR;
        } else if (skipErrorsAndWarningsButton.getSelection()) {
            return IModelValidationService.BUTTON_SKIP_ERROR_WARNING;
        }
        Assert.isTrue(false);
        return -1;
    }

    @Override
    protected void performDefaults() {
        initControls();
    }
}
