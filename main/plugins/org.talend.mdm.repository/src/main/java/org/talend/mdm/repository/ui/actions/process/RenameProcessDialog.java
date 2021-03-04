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
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
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
import org.talend.mdm.repository.core.impl.transformerV2.ITransformerV2NodeConsDef;
import org.talend.mdm.repository.i18n.Messages;

import com.amalto.workbench.dialogs.XpathSelectDialog;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;

public class RenameProcessDialog extends Dialog implements SelectionListener, ITransformerV2NodeConsDef {

    public static int typeAll = 1;

    public static int typeEntity = 2;

    public static int typeOption = 3;

    private final String seprator3 = "#";//$NON-NLS-1$

    private final String blankText = "";//$NON-NLS-1$

    private int processType;

    private String processPrefix;

    private IWorkbenchPartSite site;

    protected IInputValidator validator;

    private int uiType;

    private Button okBtn;

    private Text errorMessageText;

    private Text entityText1;

    private Text optionText1;

    private Text entityText2;

    private Text optionText3;

    private StackLayout stackLayout;

    private Composite container1;

    private Composite container2;

    private Composite container3;

    private String value;

    private Label internalLabel1;

    private Label internalLabel2;

    private Label internalLabel3;

    protected RenameProcessDialog(Shell shell, IWorkbenchPartSite site, int type, String processName, IInputValidator validator) {
        super(shell);
        this.site = site;
        this.processType = type;
        this.value = processName;
        this.validator = validator;

        setShellStyle(getShellStyle() | SWT.WRAP | SWT.RESIZE);
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(Messages.RenameViewDialog_Rename);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite mainC = (Composite) super.createDialogArea(parent);

        createMainPart(mainC);

        init();

        return mainC;
    }

    protected void createMainPart(Composite mainC) {
        final Composite botComposite = new Composite(mainC, SWT.NONE);
        stackLayout = new StackLayout();
        botComposite.setLayout(stackLayout);
        botComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

        switch (processType) {
        case TYPE_BEFOREDEL:
        case TYPE_BEFORESAVE:
            createTwo(botComposite);
            break;
        case TYPE_ENTITYACTION:
        case TYPE_SMARTVIEW:
            createOne(botComposite);
            break;
        case TYPE_WELCOMEACTION:
        case TYPE_OTHER:
            createThree(botComposite);
            break;
        default:
            break;
        }
    }

    private void createBottom(Composite container) {
        errorMessageText = new Text(container, 72);
        errorMessageText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1));
        errorMessageText.setBackground(getColor(22));
    }

    private void createOne(Composite botComposite) {
        container1 = new Composite(botComposite, SWT.NONE);
        GridLayout layout = new GridLayout(3, false);
        container1.setLayout(layout);

        Label label2 = new Label(container1, SWT.NONE);
        label2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        label2.setText(Messages.InputProcessNamePage_entityName);
        entityText1 = new Text(container1, SWT.SINGLE | SWT.BORDER | SWT.WRAP);
        GridData layoutData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        layoutData.widthHint = 330;
        entityText1.setLayoutData(layoutData);

        Button openDLG = new Button(container1, SWT.NONE);
        openDLG.setImage(ImageCache.getCreatedImage(EImage.DOTS_BUTTON.getPath()));
        openDLG.addSelectionListener(this);
        openDLG.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        openDLG.setToolTipText(Messages.ViewInputDialog_SelectOneEntity);

        Label lblFilterName = new Label(container1, SWT.NONE);
        lblFilterName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblFilterName.setText(Messages.InputProcessNamePage_OptionalName);

        optionText1 = new Text(container1, SWT.BORDER);
        GridData layoutData2 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        layoutData2.widthHint = 310;
        optionText1.setLayoutData(layoutData2);
        optionText1.setText(Messages.RenameViewDialog_DefaultFilter);

        new Label(container1, SWT.NONE);

        internalLabel1 = new Label(container1, SWT.NONE | SWT.WRAP);
        internalLabel1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1));
        internalLabel1.setText(Messages.InputProcessNamePage_processName);
        internalLabel1.setForeground(getColor(SWT.COLOR_GRAY));

        createBottom(container1);

        optionText1.addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent e) {
                if (optionText1.getText().trim().equals(Messages.ViewInputDialog_Default)) {
                    optionText1.setText(blankText);
                    optionText1.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (optionText1.getText().trim().isEmpty()) {
                    optionText1.setText(Messages.ViewInputDialog_Default);
                    optionText1.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_GRAY));
                }
            }
        });

        ModifyListener modifyListener = new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                if (!optionText1.getText().equals(Messages.RenameViewDialog_DefaultFilter))
                    optionText1.setForeground(getColor(SWT.COLOR_BLACK));

                String entityName = entityText1.getText().trim();
                String optionName = optionText1.getText().trim();
                if (optionName.equals(Messages.RenameViewDialog_DefaultFilter))
                    optionName = blankText;

                StringBuilder builder = new StringBuilder();
                builder.append(Messages.InputProcessNamePage_processName);
                builder.append(processPrefix);
                builder.append(entityName);
                if (!optionName.isEmpty()) {
                    builder.append(seprator3);
                    builder.append(optionName);
                }
                internalLabel1.setText(builder.toString());

                updateBtnState(entityName, optionName);
            }
        };
        entityText1.addModifyListener(modifyListener);
        optionText1.addModifyListener(modifyListener);
    }

    private void createTwo(Composite botComposite) {
        container2 = new Composite(botComposite, SWT.NONE);
        GridLayout layout = new GridLayout(3, false);
        container2.setLayout(layout);

        Label label2 = new Label(container2, SWT.NONE);
        label2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        label2.setText(Messages.InputProcessNamePage_entityName);
        entityText2 = new Text(container2, SWT.SINGLE | SWT.BORDER | SWT.WRAP);
        GridData layoutData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        layoutData.widthHint = 330;
        entityText2.setLayoutData(layoutData);

        Button openDLG = new Button(container2, SWT.NONE);
        openDLG.setImage(ImageCache.getCreatedImage(EImage.DOTS_BUTTON.getPath()));
        openDLG.addSelectionListener(this);
        openDLG.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        openDLG.setToolTipText(Messages.ViewInputDialog_SelectOneEntity);

        internalLabel2 = new Label(container2, SWT.NONE | SWT.WRAP);
        internalLabel2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1));
        internalLabel2.setText(Messages.InputProcessNamePage_processName);
        internalLabel2.setForeground(getColor(SWT.COLOR_GRAY));

        createBottom(container2);

        entityText2.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                String entityName = entityText2.getText().trim();
                internalLabel2.setText(Messages.InputProcessNamePage_processName + processPrefix + entityName);

                updateBtnState(entityName, null);
            }
        });
    }

    private void createThree(Composite botComposite) {
        container3 = new Composite(botComposite, SWT.NONE);
        GridLayout layout = new GridLayout(3, false);
        container3.setLayout(layout);

        Label lblFilterName = new Label(container3, SWT.NONE);
        lblFilterName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblFilterName.setText(Messages.InputProcessNamePage_inputName);

        optionText3 = new Text(container3, SWT.BORDER);
        GridData layoutData2 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        layoutData2.widthHint = 310;
        optionText3.setLayoutData(layoutData2);
        optionText3.setText(Messages.RenameViewDialog_DefaultFilter);
        new Label(container3, SWT.NONE);

        internalLabel3 = new Label(container3, SWT.NONE | SWT.WRAP);
        internalLabel3.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1));
        internalLabel3.setText(Messages.InputProcessNamePage_processName);
        internalLabel3.setForeground(getColor(SWT.COLOR_GRAY));

        createBottom(container3);

        optionText3.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                String name = optionText3.getText().trim();
                internalLabel3.setText(Messages.InputProcessNamePage_processName + processPrefix + name);

                updateBtnState(null, name);
            }
        });
    }

    private void init() {
        switch (processType) {
        case TYPE_BEFORESAVE:
            stackLayout.topControl = container2;
            uiType = typeEntity;
            processPrefix = ITransformerV2NodeConsDef.PREFIX_BEFORESAVE_UPPER;
            internalLabel2.setText(Messages.InputProcessNamePage_processName + value);
            value = value.substring(processPrefix.length());
            entityText2.setText(value);

            break;
        case TYPE_BEFOREDEL:
            stackLayout.topControl = container2;
            uiType = typeEntity;
            processPrefix = ITransformerV2NodeConsDef.PREFIX_BEFOREDEL_UPPER;
            internalLabel2.setText(Messages.InputProcessNamePage_processName + value);
            value = value.substring(processPrefix.length());
            entityText2.setText(value);

            break;
        case TYPE_ENTITYACTION:
            stackLayout.topControl = container1;
            uiType = typeAll;
            processPrefix = ITransformerV2NodeConsDef.PREFIX_RUNNABLE_UPPER;
            internalLabel1.setText(Messages.InputProcessNamePage_processName + value);
            value = value.substring(processPrefix.length());
            String[] split = value.split(seprator3);
            entityText1.setText(split[0]);
            if (split.length == 2)
                optionText1.setText(split[1]);
            else {
                optionText1.setForeground(getColor(SWT.COLOR_GRAY));
                optionText1.setText(Messages.ViewInputDialog_Default);
            }

            break;
        case TYPE_WELCOMEACTION:
            stackLayout.topControl = container3;
            uiType = typeOption;
            processPrefix = ITransformerV2NodeConsDef.PREFIX_STANDLONE_UPPER;
            internalLabel3.setText(Messages.InputProcessNamePage_processName + value);
            value = value.substring(processPrefix.length());
            optionText3.setText(value);

            break;
        case TYPE_SMARTVIEW:
            stackLayout.topControl = container1;
            uiType = typeAll;
            processPrefix = ITransformerV2NodeConsDef.PREFIX_SMARTVIEW_UPPER;
            internalLabel1.setText(Messages.InputProcessNamePage_processName + value);
            value = value.substring(processPrefix.length());
            String[] split2 = value.split(seprator3);
            entityText1.setText(split2[0]);
            if (split2.length == 2)
                optionText1.setText(split2[1]);
            else {
                optionText1.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_GRAY));
                optionText1.setText(Messages.ViewInputDialog_Default);
            }

            break;
        case TYPE_OTHER:
            stackLayout.topControl = container3;
            uiType = typeOption;
            processPrefix = blankText;
            internalLabel3.setText(Messages.InputProcessNamePage_processName + value);
            optionText3.setText(value);

            break;

        default:
            break;
        }

    }

    @Override
    protected void okPressed() {
        saveDatas();
        super.okPressed();
    }

    protected void saveDatas() {
        if (uiType == typeAll)
            value = internalLabel1.getText().substring(Messages.InputProcessNamePage_processName.length());
        else if (uiType == typeEntity)
            value = internalLabel2.getText().substring(Messages.InputProcessNamePage_processName.length());
        else {
            value = internalLabel3.getText().substring(Messages.InputProcessNamePage_processName.length());
        }
    }

    private void updateBtnState(String entityName, String optionName) {
        if (okBtn != null) {
            boolean isValid = validInput(entityName, optionName);
            okBtn.setEnabled(isValid);
        }
    }

    protected boolean validInput(String entityName, String optionName) {
        if (entityName != null && entityName.isEmpty()) {
            errorMessageText.setText(Messages.Common_nameCanNotBeEmpty);
            return false;
        }

        String suffix = blankText;
        if (optionName != null && !optionName.isEmpty())
            suffix = seprator3 + optionName;

        if (entityName != null && value.equals(entityName + suffix)) {
            errorMessageText.setText(blankText);
            return false;
        }

        String processName = null;
        if (entityName == null) {
            if (optionName.isEmpty()) {
                errorMessageText.setText(Messages.Common_nameCanNotBeEmpty);
                return false;
            }

            if (processType == TYPE_WELCOMEACTION)
                processName = processPrefix + optionName;
            else
                processName = optionName;

            if ((processPrefix + value).equals(processName))
                return false;
        } else {
            processName = processPrefix + entityName + suffix;
        }

        if (validator != null) {
            String validMsg = validator.isValid(processName);

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
        return new Point(500, 230);
    }

    public void widgetSelected(SelectionEvent e) {
        XpathSelectDialog dlg = new XpathSelectDialog(getShell(), null, Messages.ViewInputDialog_SelectOneEntity, site, false,
                null);
        dlg.setBlockOnOpen(true);
        dlg.open();

        if (dlg.getReturnCode() == Window.OK) {
            if (uiType == typeAll) {
                entityText1.setText(dlg.getEntityName());
            } else if (uiType == typeEntity) {
                entityText2.setText(dlg.getEntityName());
            }

            dlg.close();
        }
    }

    private Color getColor(int color) {
        return Display.getCurrent().getSystemColor(color);
    }

    public String getValue() {
        return value;
    }

    /**
     * Create contents of the button bar.
     *
     * @param parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        okBtn = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        okBtn.setEnabled(false);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    public void widgetDefaultSelected(SelectionEvent e) {
    }
}
