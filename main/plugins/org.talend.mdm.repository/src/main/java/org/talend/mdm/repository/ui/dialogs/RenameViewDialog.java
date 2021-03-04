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
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
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


public class RenameViewDialog extends Dialog implements SelectionListener {
    private final String seprator3 = "#";//$NON-NLS-1$
    private final String blankText = "";//$NON-NLS-1$

    private Text entityText;
    private Text filterText;
    protected String value;
    protected IInputValidator validator;
    private IWorkbenchPartSite site;
    protected Button okBtn;

    protected Text errorMessageText;

    /**
     * Create the dialog.
     * @param parentShell
     */
    public RenameViewDialog(Shell parentShell, String dialogTitle, String dialogMessage, String initialValue,
            IInputValidator validator, IWorkbenchPartSite site) {
        super(parentShell);
        this.value = initialValue;
        this.validator = validator;
        this.site = site;
        setShellStyle(getShellStyle() | SWT.WRAP);
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(Messages.RenameViewDialog_Rename);
    }
    /**
     * Create contents of the dialog.
     * @param parent
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        createMainPart(container);
        createBottom(container);

        return container;
    }

    protected void createMainPart(Composite container) {

        GridLayout gridLayout = (GridLayout) container.getLayout();
        gridLayout.numColumns = 3;

        Label label2 = new Label(container, SWT.NONE);
        label2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        label2.setText(Messages.RenameViewDialog_Entity);
        entityText = new Text(container, SWT.SINGLE | SWT.BORDER | SWT.WRAP);
        GridData layoutData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        layoutData.widthHint = 330;
        entityText.setLayoutData(layoutData);


        Button openDLG = new Button(container, SWT.NONE);
        openDLG.setImage(ImageCache.getCreatedImage(EImage.DOTS_BUTTON.getPath()));
        openDLG.addSelectionListener(this);
        openDLG.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        openDLG.setToolTipText(Messages.ViewInputDialog_SelectOneEntity);

        Label lblFilterName = new Label(container, SWT.NONE);
        lblFilterName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblFilterName.setText(Messages.ViewInputDialog_lblFilterName_text);

        filterText = new Text(container, SWT.BORDER);
        GridData layoutData2 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        layoutData2.widthHint = 310;
        filterText.setLayoutData(layoutData2);
        filterText.setText(Messages.RenameViewDialog_DefaultFilter);
        new Label(container, SWT.NONE);

        final Label internalLabel = new Label(container, SWT.NONE | SWT.WRAP);
        internalLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3,1));
        internalLabel.setText(Messages.RenameViewDialog_InternalName);
        internalLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));


        if(value != null) {
            String[] split = value.split(seprator3);
            entityText.setText(split[0]);
            if(split.length == 1)
                filterText.setText(blankText);
            else
                filterText.setText(split[1]);

            internalLabel.setText(getInternalName());
        }

        filterText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                updateOkButtonState();

                internalLabel.setText(getInternalName());
            }
        });

        entityText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                updateOkButtonState();

                internalLabel.setText(getInternalName());
            }
        });

    }

    private void createBottom(Composite container) {
        errorMessageText = new Text(container, 72);
        errorMessageText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3,1));
        errorMessageText.setBackground(Display.getCurrent().getSystemColor(22));
    }

    private String getInternalName() {
        String filterPart = blankText;

        String filterStr = filterText.getText().trim();
        if(!filterStr.isEmpty())
            filterPart = seprator3 + filterStr;

        String entityName = entityText.getText().trim();

        String internalName = Messages.bind(Messages.RenameViewDialog_InternalNameX, IViewNodeConstDef.PREFIX_VIEW_UPPER
                + entityName + filterPart);

        return internalName;
    }
    /**
     * Create contents of the button bar.
     * @param parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        okBtn = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        okBtn.setEnabled(false);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    @Override
    protected void okPressed() {
        saveDatas();
        super.okPressed();
    }

    protected void saveDatas() {
        value = entityText.getText().trim();
        String filterStr = filterText.getText().trim();
        if(!filterStr.isEmpty())
        {
            value += seprator3 + filterStr;
        }
    }

    private void updateOkButtonState() {
        if(okBtn != null) {
            boolean isValid = validInput();
            okBtn.setEnabled(isValid);
        }
    }

    protected boolean validInput() {
        String entityName = entityText.getText().trim();
        String filter = filterText.getText().trim();

        if(value.equals(entityName + seprator3 + filter))
            return false;

        if (validator != null) {
            String prefix = IViewNodeConstDef.PREFIX_VIEW_UPPER;
            if (entityName.isEmpty()) {
                errorMessageText.setText(Messages.Common_nameCanNotBeEmpty);
                return false;
            }

            String suffix = seprator3 + filter;
            if (filter.isEmpty())
                suffix = blankText;


            String validMsg = validator.isValid(prefix + entityName + suffix);

            if (validMsg != null) {
                errorMessageText.setText(validMsg);
                return false;
            }

            errorMessageText.setText(blankText);
        }

        return true;
    }
    /**
     * Return the initial size of the dialog.
     */
    @Override
    protected Point getInitialSize() {
        return new Point(478, 230);
    }

    public String getValue() {
        return value;
    }

    public void widgetSelected(SelectionEvent e) {
        XpathSelectDialog dlg = new XpathSelectDialog(getShell(), null, Messages.ViewInputDialog_SelectOneEntity, site, false, null);
        dlg.setBlockOnOpen(true);
        dlg.open();

        if (dlg.getReturnCode() == Window.OK) {
            entityText.setText(dlg.getEntityName());
            dlg.close();
        }
    }

    public void widgetDefaultSelected(SelectionEvent e) {
    }
}
