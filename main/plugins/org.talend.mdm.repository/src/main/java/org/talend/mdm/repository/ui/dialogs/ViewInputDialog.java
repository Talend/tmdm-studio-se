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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPartSite;
import org.talend.mdm.repository.core.impl.view.IViewNodeConstDef;
import org.talend.mdm.repository.i18n.Messages;

import com.amalto.workbench.dialogs.XpathSelectDialog;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.models.TreeParent;

public class ViewInputDialog extends Dialog implements SelectionListener, IViewNodeConstDef {

    private final String seprator3 = "#";//$NON-NLS-1$

    private final String blankText = "";//$NON-NLS-1$

    /**
     * The title of the dialog.
     */
    private String title;

    /**
     * The input value; the empty string by default.
     */
    protected String value = "";//$NON-NLS-1$

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
    protected Text entityText;

    private TreeParent treeParent;

    protected IWorkbenchPartSite site;

    protected XpathSelectDialog dlg;

    private Composite composite;

    boolean isBtnShow = true;

    private Button webFilterRadioBtn;

    private Button searchFilterRadioBtn;

    private Label lblNewLabel;

    private Label lblFilterName;

    private Text filterText1;

    private Composite bottom1;

    private Composite bottom2;

    private int parentType;

    private String filterName;

    private Text filterText2;

    private Text errorMessageText1;

    private Text errorMessageText2;

    public ViewInputDialog(IWorkbenchPartSite site, TreeParent treeParent, Shell parentShell, String dialogTitle,
            String dialogMessage, IInputValidator validator, boolean isTransfor, int type) {
        super(parentShell);
        setShellStyle(getShellStyle() | SWT.WRAP | SWT.RESIZE);
        this.site = site;
        this.title = dialogTitle;
        this.treeParent = treeParent;
        this.parentType = type;
        this.validator = validator;

        value = blankText;
    }

    public void setBtnShow(boolean isBtnShow) {
        this.isBtnShow = isBtnShow;
    }

    @Override
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            if (parentType == TYPE_WEBFILTER) {
                value = entityText.getText().trim();
                filterName = filterText1.getText().trim();
                if (filterName.equals(Messages.ViewInputDialog_Default))
                    filterName = blankText;
                if (value.isEmpty()) {
                    MessageDialog.openError(getShell(), Messages.Warning, Messages.ViewInputDialog_NameCannotbeEmpty);
                    return;
                }
            }
            if (parentType == TYPE_SEARCHFILTER) {
                value = filterText2.getText().trim();
                if (value.isEmpty()) {
                    MessageDialog.openError(getShell(), Messages.Warning, Messages.ViewInputDialog_NameCannotbeEmpty);
                    return;
                }
            }
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
        layout.numColumns = 3;

        webFilterRadioBtn = new Button(composite, SWT.RADIO);
        webFilterRadioBtn.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1));
        webFilterRadioBtn.setText(Messages.ViewInputDialog_webFilterRadioBtnText);

        searchFilterRadioBtn = new Button(composite, SWT.RADIO);
        searchFilterRadioBtn.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1));
        searchFilterRadioBtn.setText(Messages.ViewInputDialog_searchFilterRadioBtnText);

        lblNewLabel = new Label(composite, SWT.WRAP);
        GridData gd_lblNewLabel = new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1);
        gd_lblNewLabel.widthHint = 423;
        lblNewLabel.setLayoutData(gd_lblNewLabel);

        final Composite botComposite = new Composite(composite, SWT.NONE);
        final StackLayout stackLayout = new StackLayout();
        botComposite.setLayout(stackLayout);
        botComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));

        bottom1 = getBottom1(botComposite);
        bottom2 = getBottom2(botComposite);

        if (parentType == TYPE_VIEW) {
            webFilterRadioBtn.setSelection(true);
            searchFilterRadioBtn.setSelection(false);
            lblNewLabel.setText(Messages.ViewInputDialog_lblNewLabel_text1);
            stackLayout.topControl = bottom1;
            parentType = TYPE_WEBFILTER;
        } else if (parentType == TYPE_WEBFILTER) {
            webFilterRadioBtn.setSelection(true);
            searchFilterRadioBtn.setSelection(false);
            searchFilterRadioBtn.setEnabled(false);
            lblNewLabel.setText(Messages.ViewInputDialog_lblNewLabel_text1);
            stackLayout.topControl = bottom1;
        } else {
            webFilterRadioBtn.setSelection(false);
            searchFilterRadioBtn.setSelection(true);
            webFilterRadioBtn.setEnabled(false);
            lblNewLabel.setText(Messages.ViewInputDialog_lblNewLabel_text2);
            stackLayout.topControl = bottom2;
        }

        webFilterRadioBtn.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                lblNewLabel.setText(Messages.ViewInputDialog_lblNewLabel_text1);
                stackLayout.topControl = bottom1;
                botComposite.layout();
                parentType = TYPE_WEBFILTER;
            }
        });

        searchFilterRadioBtn.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                lblNewLabel.setText(Messages.ViewInputDialog_lblNewLabel_text2);
                stackLayout.topControl = bottom2;
                botComposite.layout();
                parentType = TYPE_SEARCHFILTER;
            }
        });

        new Label(composite, SWT.NONE);
        new Label(composite, SWT.NONE);
        new Label(composite, SWT.NONE);

        return composite;
    }

    private Composite getBottom1(Composite botComposite) {
        Composite panel1 = new Composite(botComposite, SWT.NONE);
        GridLayout layout = new GridLayout(3, false);
        panel1.setLayout(layout);

        Label label2 = new Label(panel1, SWT.NONE);
        label2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        label2.setText(Messages.ViewInputDialog_Entity);
        entityText = new Text(panel1, getInputTextStyle() | SWT.WRAP);
        GridData layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        // layoutData.widthHint = 350;
        entityText.setLayoutData(layoutData);

        openDLG = new Button(panel1, SWT.NONE);
        openDLG.setImage(ImageCache.getCreatedImage(EImage.DOTS_BUTTON.getPath()));
        openDLG.addSelectionListener(this);
        openDLG.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        openDLG.setVisible(isBtnShow);
        openDLG.setToolTipText(Messages.ViewInputDialog_SelectOneEntity);

        lblFilterName = new Label(panel1, SWT.NONE);
        lblFilterName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblFilterName.setText(Messages.ViewInputDialog_lblFilterName_text);

        filterText1 = new Text(panel1, SWT.BORDER);
        GridData layoutData2 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        layoutData2.widthHint = 330;
        filterText1.setLayoutData(layoutData2);
        filterText1.setText(Messages.ViewInputDialog_Default);
        filterText1.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_GRAY));
        new Label(panel1, SWT.NONE);

        final Label internalLabel = new Label(panel1, SWT.NONE | SWT.WRAP);
        internalLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1));
        internalLabel.setText(Messages.bind(Messages.ViewInputDialog_InternalNameX, getInternalName()));
        internalLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));

        errorMessageText1 = new Text(panel1, 72);
        errorMessageText1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1));
        errorMessageText1.setBackground(Display.getCurrent().getSystemColor(22));
        errorMessageText1.setText(Messages.Common_nameCanNotBeEmpty);

        filterText1.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                updateoOkButtonForWebType();

                internalLabel.setText(Messages.bind(Messages.ViewInputDialog_InternalNameX, getInternalName()));
                filterText1.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
            }
        });

        filterText1.addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent e) {
                if (filterText1.getText().trim().equals(Messages.ViewInputDialog_Default)) {
                    filterText1.setText(blankText);
                    filterText1.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (filterText1.getText().trim().isEmpty()) {
                    filterText1.setText(Messages.ViewInputDialog_Default);
                    filterText1.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_GRAY));
                }
            }
        });

        entityText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                updateoOkButtonForWebType();

                internalLabel.setText(Messages.bind(Messages.ViewInputDialog_InternalNameX, getInternalName()));
            }

        });
        return panel1;
    }

    private void updateoOkButtonForWebType() {
        if (validator != null) {
            String entityName = entityText.getText().trim();
            String prefix1 = PREFIX_VIEW_UPPER;
            if (entityName.isEmpty()) {
                prefix1 = blankText;
            }

            String filter1 = filterText1.getText().trim();
            String suffix1 = seprator3 + filter1;
            if (filter1.isEmpty() || filter1.equals(Messages.ViewInputDialog_Default)) {
                suffix1 = blankText;
            }

            String validMsg = validator.isValid(prefix1 + entityName + suffix1);

            if (validMsg == null) {
                errorMessageText1.setText(blankText);
                okButton.setEnabled(true);
            } else {
                errorMessageText1.setText(validMsg);
                okButton.setEnabled(false);
            }
        }
    }

    private String getInternalName() {
        StringBuffer internalBuffer = new StringBuffer();
        internalBuffer.append(PREFIX_VIEW_UPPER);
        internalBuffer.append(entityText.getText().trim());

        String filterStr = filterText1.getText().trim();
        if (!filterStr.isEmpty() && !filterStr.equalsIgnoreCase(Messages.ViewInputDialog_Default)) {
            internalBuffer.append(seprator3);
            internalBuffer.append(filterStr);
        }

        return internalBuffer.toString();
    }

    private Composite getBottom2(Composite botComposite) {
        Composite panel2 = new Composite(botComposite, SWT.NONE);
        GridLayout layout = new GridLayout(3, false);
        panel2.setLayout(layout);

        Label label2 = new Label(panel2, SWT.NONE);
        label2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        label2.setText(Messages.ViewInputDialog_Name);
        filterText2 = new Text(panel2, getInputTextStyle() | SWT.WRAP);
        GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gd_text.widthHint = 420;
        filterText2.setLayoutData(gd_text);
        new Label(panel2, SWT.NONE);

        final Label internalLabel = new Label(panel2, SWT.NONE | SWT.WRAP);
        internalLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1));
        internalLabel.setText(Messages.ViewInputDialog_InternalName);
        internalLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));

        errorMessageText2 = new Text(panel2, 72);
        errorMessageText2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1));
        errorMessageText2.setBackground(Display.getCurrent().getSystemColor(22));
        errorMessageText2.setText(Messages.Common_nameCanNotBeEmpty);

        filterText2.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                if (validator != null) {
                    String validMsg = validator.isValid(filterText2.getText().trim());
                    if (validMsg == null) {
                        errorMessageText2.setText(blankText);
                        okButton.setEnabled(true);
                    } else {
                        errorMessageText2.setText(validMsg);
                        okButton.setEnabled(false);
                    }
                }

                internalLabel.setText(Messages.bind(Messages.ViewInputDialog_InternalNameX, filterText2.getText().trim()));
            }
        });

        return panel2;
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

    public String getEntityName() {
        return value;
    }

    public String getFilterName() {
        return filterName;
    }

    protected int getInputTextStyle() {
        return SWT.SINGLE | SWT.BORDER;
    }

    public void widgetDefaultSelected(SelectionEvent e) {

    }

    public void widgetSelected(SelectionEvent e) {
        dlg = new XpathSelectDialog(composite.getShell(), treeParent, Messages.ViewInputDialog_SelectOneEntity, site, false, null);
        dlg.setBlockOnOpen(true);
        dlg.open();

        if (dlg.getReturnCode() == Window.OK) {
            entityText.setText(value + dlg.getEntityName());
            dlg.close();
        }
    }

}
