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
package org.talend.mdm.repository.ui.wizards.process;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPartSite;
import org.talend.mdm.repository.core.IServerObjectRepositoryType;
import org.talend.mdm.repository.core.impl.transformerV2.ITransformerV2NodeConsDef;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.ui.dialogs.xpath.XpathSelectDialog2;
import org.talend.mdm.repository.ui.wizards.process.composite.BeforeProcessTypeComposite;
import org.talend.mdm.repository.ui.wizards.process.composite.OtherTypeComposite;
import org.talend.mdm.repository.ui.wizards.process.composite.RunnableTypeComposite;
import org.talend.mdm.repository.ui.wizards.process.composite.SmartviewProcessTypeComposite;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;
import org.talend.mdm.repository.utils.ValidateUtil;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class InputProcessNamePage extends WizardPage implements ITransformerV2NodeConsDef {

    public static final String PAGE_ID = "org.talend.mdm.repository.ui.wizards.process.InputProcessNamePage"; //$NON-NLS-1$

    private final IWorkbenchPartSite site;

    private String processName;

    private String processDesc;

    public String getProcessName() {
        return this.processName;
    }

    public String getProcessDesc() {
        return this.processDesc;
    }

    public int getProcessType() {
        return this.processType;
    }

    private int processType;

    private int defaultSelectedType;

    /**
     * Create the wizard.
     *
     * @param selectType
     */
    public InputProcessNamePage(IWorkbenchPartSite site, int selectType) {
        super(PAGE_ID);
        this.site = site;
        this.defaultSelectedType = selectType;
        setTitle(Messages.NewProcessWizard_title);
        setDescription(Messages.InputProcessNamePage_description);
    }

    protected SelectionListener selectionListener = new SelectionAdapter() {

        @Override
        public void widgetSelected(SelectionEvent e) {
            setEntityLabelEnable();
            updateProcessNameLabel();
            getWizard().getContainer().updateButtons();
        }
    };

    private IProcessTypeComposite curProcessTypeComposite;

    IProcessTypeComposite beforeProcessComposite;

    IProcessTypeComposite runnableProcessComposite;

    IProcessTypeComposite otherProcessComposite;

    IProcessTypeComposite smartviewProcessComposite;

    private Text nameText;

    private Label processNameLabel;

    private Button selectEntityBun;

    private Label inputLabel;

    private Composite typeComposite;

    private Composite container;

    final Shell another = new Shell();

    private Label optionNameLabel;

    private Text optionNameText;

    private Label padLabel;

    private Group grpEnterProcessName;

    private Label internalNameLabel;

    private Label pad2Label;

    /**
     * Create contents of the wizard.
     *
     * @param parent
     */
    public void createControl(Composite parent) {
        container = new Composite(parent, SWT.NULL);

        setControl(container);
        container.setLayout(new GridLayout(1, false));
        typeComposite = new Composite(container, SWT.NONE);
        typeComposite.setLayout(new GridLayout(1, false));
        typeComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        curProcessTypeComposite = getProcessTypeComposite(NewProcessWizard.BEFORE_TYPE, TYPE_PROCESS);
        ((Composite) curProcessTypeComposite).setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        grpEnterProcessName = new Group(container, SWT.NONE);
        grpEnterProcessName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        grpEnterProcessName.setText(Messages.InputProcessNamePage_enterName);
        grpEnterProcessName.setLayout(new GridLayout(3, false));

        inputLabel = new Label(grpEnterProcessName, SWT.NONE | SWT.WRAP);
        inputLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        inputLabel.setText(Messages.InputProcessNamePage_entityName);

        nameText = new Text(grpEnterProcessName, SWT.BORDER);
        nameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        selectEntityBun = new Button(grpEnterProcessName, SWT.NONE);
        selectEntityBun.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                XpathSelectDialog2 dlg = new XpathSelectDialog2(getShell(), Messages.InputProcessNamePage_selectEntity, site,
                        false, null);
                dlg.setBlockOnOpen(true);
                dlg.open();

                if (dlg.getReturnCode() == IDialogConstants.OK_ID) {
                    nameText.setText(dlg.getEntityName());
                    dlg.close();
                }
            }
        });
        selectEntityBun.setText(Messages.InputProcessNamePage_select);

        // option part
        optionNameLabel = new Label(another, SWT.NONE);
        optionNameLabel.setText(Messages.InputProcessNamePage_OptionalName);

        optionNameText = new Text(another, SWT.BORDER);
        optionNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        padLabel = new Label(another, SWT.NONE);

        optionNameLabel.setParent(grpEnterProcessName);
        optionNameText.setParent(grpEnterProcessName);
        padLabel.setParent(grpEnterProcessName);

        Color grayColor = Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);
        // bottom part
        internalNameLabel = new Label(grpEnterProcessName, SWT.NONE);
        internalNameLabel.setForeground(grayColor);
        internalNameLabel.setText(Messages.InputProcessNamePage_processName);

        processNameLabel = new Label(grpEnterProcessName, SWT.NONE);
        processNameLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        processNameLabel.setForeground(grayColor);
        pad2Label = new Label(grpEnterProcessName, SWT.NONE);

        updateProcessTypeComposite(getSelectedType(), defaultSelectedType);

        nameText.addModifyListener(getModifyListener());
        optionNameText.addModifyListener(getModifyListener());
    }

    public ModifyListener getModifyListener() {
        if (modifyListener == null) {
            modifyListener = new ModifyListener() {

                public void modifyText(ModifyEvent e) {
                    updateProcessNameLabel();
                    getWizard().getContainer().updateButtons();
                }
            };
        }

        return modifyListener;
    }

    private int getSelectedType() {
        int selectedType = -1;
        switch (defaultSelectedType) {
        case TYPE_BEFOREDEL:
        case TYPE_BEFORESAVE:
            selectedType = NewProcessWizard.BEFORE_TYPE;
            break;
        case TYPE_ENTITYACTION:
        case TYPE_WELCOMEACTION:
            selectedType = NewProcessWizard.RUNNABLE_TYPE;
            break;
        case TYPE_SMARTVIEW:
            selectedType = NewProcessWizard.TYPE_SMARTVIEW;
            break;
        case TYPE_OTHER:
            selectedType = NewProcessWizard.TYPE_OTHER;
            break;
        default:
            break;
        }

        return selectedType;
    }

    private void updateOptionPart() {
        if (curProcessTypeComposite != null) {
            int type = curProcessTypeComposite.getCurrentProcessType();
            if (type == TYPE_ENTITYACTION || type == TYPE_WELCOMEACTION || type == TYPE_SMARTVIEW) {
                setOptionPartVisible(true);
                optionNameText.setText(""); //$NON-NLS-1$
            } else {
                setOptionPartVisible(false);
            }

            if (type == TYPE_OTHER) {
                inputLabel.setText(Messages.InputProcessNamePage_inputName);
            } else {
                inputLabel.setText(Messages.InputProcessNamePage_entityName);
            }
        }
    }

    private boolean optionPartVisible = false;

    private ModifyListener modifyListener;

    private void setOptionPartVisible(boolean visible) {
        optionPartVisible = visible;
        if (visible) {
            internalNameLabel.setParent(another);
            processNameLabel.setParent(another);
            pad2Label.setParent(another);

            optionNameLabel.setParent(grpEnterProcessName);
            optionNameText.setParent(grpEnterProcessName);
            padLabel.setParent(grpEnterProcessName);

            internalNameLabel.setParent(grpEnterProcessName);
            processNameLabel.setParent(grpEnterProcessName);
            pad2Label.setParent(grpEnterProcessName);

            grpEnterProcessName.layout();
            container.layout();
        } else {
            optionNameLabel.setParent(another);
            optionNameText.setParent(another);
            padLabel.setParent(another);
            grpEnterProcessName.layout();
            container.layout();
        }
    }

    private IProcessTypeComposite getProcessTypeComposite(int processType, int defaultProcessType) {
        if (typeComposite == null) {
            return null;
        }
        switch (processType) {
        case NewProcessWizard.BEFORE_TYPE:
            if (beforeProcessComposite == null) {
                beforeProcessComposite = new BeforeProcessTypeComposite(typeComposite, defaultProcessType, selectionListener);
            } else {
                ((BeforeProcessTypeComposite) beforeProcessComposite).updateBtnState(defaultProcessType);
            }

            return beforeProcessComposite;
        case NewProcessWizard.RUNNABLE_TYPE:
            if (runnableProcessComposite == null) {
                runnableProcessComposite = new RunnableTypeComposite(typeComposite, defaultProcessType, selectionListener);
            } else {
                ((RunnableTypeComposite) runnableProcessComposite).updateBtnState(defaultProcessType);
            }
            return runnableProcessComposite;

        case TYPE_OTHER:
            if (otherProcessComposite == null) {
                otherProcessComposite = new OtherTypeComposite();
            }
            return otherProcessComposite;
        case TYPE_SMARTVIEW:
            if (smartviewProcessComposite == null) {
                smartviewProcessComposite = new SmartviewProcessTypeComposite();
            }
            return smartviewProcessComposite;
        }

        return null;
    }

    public void updateProcessTypeComposite(int type, int defaultProcessType) {
        IProcessTypeComposite newComposite = getProcessTypeComposite(type, defaultProcessType);

        if (newComposite != null && newComposite != curProcessTypeComposite) {
            if (curProcessTypeComposite instanceof Composite) {
                GridData gridData = (GridData) ((Composite) curProcessTypeComposite).getLayoutData();
                gridData.exclude = true;
                ((Composite) curProcessTypeComposite).setVisible(false);
            }
            if (newComposite instanceof Composite) {
                GridData gridData = (GridData) ((Composite) newComposite).getLayoutData();
                if (gridData == null) {
                    gridData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
                    ((Composite) newComposite).setLayoutData(gridData);
                }
                gridData.exclude = false;
                ((Composite) newComposite).setVisible(true);
            }

            curProcessTypeComposite = newComposite;
            typeComposite.layout();
            container.layout();
        }

        updateOptionPart();
        if (optionPartVisible) {
            setEntityLabelEnable();
        }
        updateProcessNameLabel();
    }

    private void setEntityLabelEnable() {
        boolean enabled = true;
        int type = curProcessTypeComposite.getCurrentProcessType();
        if (type == TYPE_ENTITYACTION) {
            enabled = true;
        } else if (type == TYPE_WELCOMEACTION) {
            enabled = false;
            nameText.setText("");//$NON-NLS-1$
        }
        inputLabel.setEnabled(enabled);
        nameText.setEnabled(enabled);
        selectEntityBun.setEnabled(enabled);
    }

    private boolean validateProcessName() {
        if (processNameLabel == null) {
            return false;
        }
        String name = processNameLabel.getText().trim();
        String errorMsg = null;
        boolean result = false;
        boolean nameEnabled = nameText.isEnabled();
        if (nameEnabled && nameText.getText().trim().length() == 0) {
            errorMsg = Messages.Common_nameCanNotBeEmpty;
        } else if (optionPartVisible) {
            if (!nameEnabled && optionNameText.getText().trim().length() == 0) {
                errorMsg = Messages.Common_nameCanNotBeEmpty;
            }

            if (name.startsWith(ITransformerV2NodeConsDef.PREFIX_SMARTVIEW_UPPER)) {
                if (!ValidateUtil.matchSmartViewRegex(name)) {
                    errorMsg = Messages.Common_nameInvalid;
                }
            }
        } else if (!ValidateUtil.matchViewProcessRegex(name)) {
            errorMsg = Messages.Common_nameInvalid;
        }
        if (RepositoryResourceUtil.isExistByName(IServerObjectRepositoryType.TYPE_TRANSFORMERV2, name)) {
            errorMsg = Messages.Common_nameIsUsed;
        }
        if (errorMsg != null) {
            setErrorMessage(errorMsg);
            result = false;
        } else {
            setErrorMessage(null);

            processName = name;
            processType = curProcessTypeComposite.getCurrentProcessType();
            processDesc = curProcessTypeComposite.getDesc();
            result = true;
        }
        return result;
    }

    private void updateProcessNameLabel() {
        if (curProcessTypeComposite == null || selectEntityBun == null) {
            return;
        }
        String prefix = curProcessTypeComposite.getProcessPrefix();
        int type = curProcessTypeComposite.getCurrentProcessType();
        if (type == TYPE_WELCOMEACTION) {
            prefix += optionNameText.getText();
        } else if (type == TYPE_ENTITYACTION) {
            prefix += nameText.getText();
            if (!optionNameText.getText().isEmpty()) {
                prefix += "#" + optionNameText.getText(); //$NON-NLS-1$
            }
        } else if (type == TYPE_SMARTVIEW) {
            prefix += nameText.getText();
            if (!optionNameText.getText().isEmpty()) {
                prefix += "#" + optionNameText.getText(); //$NON-NLS-1$
            }
        } else {
            prefix += nameText.getText();
        }

        processNameLabel.setText(prefix);
        // update button
        selectEntityBun.setVisible(curProcessTypeComposite.needShowSelectEntityBun());
    }

    @Override
    public IWizardPage getNextPage() {
        if (curProcessTypeComposite != null) {
            processType = curProcessTypeComposite.getCurrentProcessType();
            processDesc = curProcessTypeComposite.getDesc();
            String pageId = curProcessTypeComposite.getConfigWizardPageId();
            if (pageId != null) {
                return getWizard().getPage(pageId);
            }
        }
        return null;
    }

    @Override
    public boolean isPageComplete() {
        return validateProcessName();
    }

}
