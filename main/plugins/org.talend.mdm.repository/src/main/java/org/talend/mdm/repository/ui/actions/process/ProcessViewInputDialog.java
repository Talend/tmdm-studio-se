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
package org.talend.mdm.repository.ui.actions.process;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
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
import org.eclipse.ui.IWorkbenchPartSite;
import org.talend.mdm.repository.i18n.Messages;

import com.amalto.workbench.dialogs.XpathSelectDialog;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.models.TreeParent;

/**
 * @deprecated replace by ViewInputDialog2, can be deleted.
 */
public class ProcessViewInputDialog extends Dialog implements SelectionListener {

    /**
     * The title of the dialog.
     */
    private String title;

    /**
     * The message to display, or <code>null</code> if none.
     */
    private String message;

    /**
     * The input value; the empty string by default.
     */
    private String value = "";//$NON-NLS-1$

    /**
     * The input validator, or <code>null</code> if none.
     */
    private IInputValidator validator;

    /**
     * Ok button widget.
     */
    private Button okButton;

    private Button openDLG;

    /**
     * Input text widget.
     */
    private Text text;

    /**
     * Error message label widget.
     */
    private Text errorMessageText;

    /**
     * Error message string.
     */
    private String errorMessage = "";//$NON-NLS-1$

    private Label label;

    private Button transformeButton;

    private Button smartViewButton;

    private Button beforeSavingButton;

    private Button beforeDeletingButton;

    private Button runnableProcessButton;

    private Button standaloneProcessButton;

    private TreeParent treeParent;

    private IWorkbenchPartSite site;

    private XpathSelectDialog dlg;

    private Composite composite;

    private boolean isTransfor = false;

    private static String Smart_view = "Smart_view_";//$NON-NLS-1$

    private static String beforeSaving = "beforeSaving_";//$NON-NLS-1$

    private static String beforeDeleting = "beforeDeleting_";//$NON-NLS-1$

    private static String runnableProcess = "Runnable_";//$NON-NLS-1$

    private static String standaloneProcess = "Runnable#";//$NON-NLS-1$

    boolean isBtnShow = true;

    public ProcessViewInputDialog(IWorkbenchPartSite site, TreeParent treeParent, Shell parentShell, String dialogTitle,
            String dialogMessage, String initialValue, IInputValidator validator, boolean isTransfor) {
        super(parentShell);
        setShellStyle(getShellStyle() | SWT.RESIZE | SWT.WRAP);
        this.site = site;
        this.title = dialogTitle;
        message = dialogMessage;
        this.site = site;
        this.treeParent = treeParent;
        this.isTransfor = isTransfor;
        if (initialValue == null) {
            value = "";//$NON-NLS-1$
        } else {
            value = initialValue;
        }
        this.validator = validator;
    }// ViewInputDialog(

    public void setBtnShow(boolean isBtnShow) {
        this.isBtnShow = isBtnShow;
    }

    @Override
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            value = text.getText();
        } else {
            value = null;
        }
        super.buttonPressed(buttonId);
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        if (title != null) {
            shell.setText(title);
        }
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {

        // create OK and Cancel buttons by default
        okButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
        // do this here because setting the text will set enablement on the ok
        // button
        okButton.setEnabled(false);
    }

    @Override
    protected Control createDialogArea(final Composite parent) {
        setShellStyle(SWT.WRAP);
        // create composite
        composite = (Composite) super.createDialogArea(parent);
        GridLayout layout = (GridLayout) composite.getLayout();
        layout.makeColumnsEqualWidth = false;
        layout.numColumns = 2;
        label = new Label(composite, SWT.NONE | SWT.WRAP);
        // create message
        if (message != null) {
            // label = new Label(composite, SWT.NONE);
            label.setText(message);
            label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
            label.setFont(parent.getFont());
        }

        text = new Text(composite, getInputTextStyle() | SWT.WRAP);
        text.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));

        text.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                validateInput();
            }
        });
        // xpathwidget = new XpathWidget(site,"...",treeParent,null, composite, null,false,true);
        // XpathSelectDialog(Shell parentShell,TreeParent parent,String title,IWorkbenchPartSite site,boolean
        // isMulti,String dataModelName)

        openDLG = new Button(composite, SWT.NONE);
        openDLG.setImage(ImageCache.getCreatedImage(EImage.DOTS_BUTTON.getPath()));
        openDLG.addSelectionListener(this);
        openDLG.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        openDLG.setVisible(isBtnShow);
        openDLG.setToolTipText(Messages.ProcessViewXX_SelectOneEntity);

        errorMessageText = new Text(composite, SWT.READ_ONLY | SWT.WRAP);
        errorMessageText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 2, 1));
        errorMessageText.setBackground(errorMessageText.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
        // Set the error message text
        setErrorMessage(errorMessage);
        if (isTransfor) {
            Group radioGroup = new Group(parent, SWT.SHADOW_NONE);
            radioGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));
            radioGroup.setLayout(new GridLayout(1, false));
            radioGroup.setText(Messages.ProcessViewXX_SelectOneType);

            transformeButton = new Button(radioGroup, SWT.RADIO);
            transformeButton.setText(Messages.ProcessViewXX_CreateNormalProcess);
            transformeButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
            text.setText("");//$NON-NLS-1$
            transformeButton.addSelectionListener(new SelectionListener() {

                public void widgetDefaultSelected(SelectionEvent e) {

                }

                public void widgetSelected(SelectionEvent e) {
                    text.setText("");//$NON-NLS-1$
                    label.setText(message);
                    openDLG.setVisible(false);
                    parent.layout(true);
                    value = "";//$NON-NLS-1$
                }

            });
            transformeButton.setSelection(true);
            smartViewButton = new Button(radioGroup, SWT.RADIO);
            smartViewButton.setText(Messages.ProcessViewXX_CreateSmartProcess);
            smartViewButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));

            // smartViewButton.setSelection(true);
            smartViewButton.addSelectionListener(new SelectionListener() {

                public void widgetDefaultSelected(SelectionEvent e) {

                }

                public void widgetSelected(SelectionEvent e) {
                    text.setText(Smart_view);
                    label.setText(Messages.ProcessViewXX_LabelText);
                    openDLG.setVisible(true);
                    value = Smart_view;
                }

            });

            beforeSavingButton = new Button(radioGroup, SWT.RADIO);
            beforeSavingButton.setText(Messages.ProcessViewXX_CreateBeforeProcess);
            beforeSavingButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
            beforeSavingButton.addSelectionListener(new SelectionListener() {

                public void widgetDefaultSelected(SelectionEvent e) {
                }

                public void widgetSelected(SelectionEvent e) {
                    text.setText(beforeSaving);
                    label.setText(Messages.ProcessViewXX_LabelEnterName);
                    openDLG.setVisible(true);
                    value = beforeSaving;
                }

            });

            beforeDeletingButton = new Button(radioGroup, SWT.RADIO);
            beforeDeletingButton.setText(Messages.ProcessViewXX_CreateBeforeDelProcess);
            beforeDeletingButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
            beforeDeletingButton.addSelectionListener(new SelectionListener() {

                public void widgetDefaultSelected(SelectionEvent e) {
                }

                public void widgetSelected(SelectionEvent e) {
                    text.setText(beforeDeleting);
                    label.setText(Messages.ProcessViewXX_LabelEnterNameDel);
                    openDLG.setVisible(true);
                    value = beforeDeleting;
                }

            });

            runnableProcessButton = new Button(radioGroup, SWT.RADIO);
            runnableProcessButton.setText(Messages.ProcessViewXX_CreateRunnableProcess);
            runnableProcessButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
            runnableProcessButton.addSelectionListener(new SelectionListener() {

                public void widgetDefaultSelected(SelectionEvent e) {
                }

                public void widgetSelected(SelectionEvent e) {
                    text.setText(runnableProcess);
                    label.setText(Messages.ProcessViewXX_LabelEnterNameRunnable);
                    openDLG.setVisible(true);
                    value = runnableProcess;
                }

            });

            standaloneProcessButton = new Button(radioGroup, SWT.RADIO);
            standaloneProcessButton.setText(Messages.ProcessViewXX_CreateStandloneProcess);
            standaloneProcessButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
            standaloneProcessButton.addSelectionListener(new SelectionListener() {

                public void widgetDefaultSelected(SelectionEvent e) {
                }

                public void widgetSelected(SelectionEvent e) {
                    text.setText(standaloneProcess);
                    label.setText(Messages.ProcessViewXX_LabelEnterNameFlowRunnable);
                    openDLG.setVisible(true);
                    value = standaloneProcess;
                }

            });
        } else {
            if (value != null) {
                text.setText(value);
            }
        }

        applyDialogFont(composite);
        return composite;
    }

    protected Label getErrorMessageLabel() {
        return null;
    }

    protected Button getOkButton() {
        return okButton;
    }

    protected IInputValidator getValidator() {
        return validator;
    }

    public String getValue() {
        return value;
    }

    protected void validateInput() {
        String errorMessage = null;
        if (validator != null) {
            errorMessage = validator.isValid(text.getText());
        }
        setErrorMessage(errorMessage);
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        if (errorMessageText != null && !errorMessageText.isDisposed()) {
            errorMessageText.setText(errorMessage == null ? " \n " : errorMessage);//$NON-NLS-1$
            boolean hasError = errorMessage != null && (StringConverter.removeWhiteSpaces(errorMessage)).length() > 0;
            errorMessageText.setEnabled(hasError);
            errorMessageText.setVisible(hasError);
            errorMessageText.getParent().update();
            Control button = getButton(IDialogConstants.OK_ID);
            if (button != null) {
                button.setEnabled(errorMessage == null);
            }
        }
    }

    protected int getInputTextStyle() {
        return SWT.SINGLE | SWT.BORDER;
    }

    public void widgetDefaultSelected(SelectionEvent e) {

    }



     public void widgetSelected(SelectionEvent e) {
        dlg = new XpathSelectDialog(composite.getShell(), treeParent, Messages.ProcessViewXX_SelectOneEntity, site, false, null);
        dlg.setBlockOnOpen(true);
        dlg.open();

        if (dlg.getReturnCode() == Window.OK) {
            text.setText(value + dlg.getEntityName());
            dlg.close();
        }
    }

}
