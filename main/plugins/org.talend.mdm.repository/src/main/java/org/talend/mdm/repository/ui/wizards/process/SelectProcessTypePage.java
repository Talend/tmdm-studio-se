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

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.talend.mdm.repository.core.impl.transformerV2.ITransformerV2NodeConsDef;
import org.talend.mdm.repository.i18n.Messages;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class SelectProcessTypePage extends WizardPage implements ITransformerV2NodeConsDef {

    public static final String PAGE_ID = "org.talend.mdm.repository.ui.wizards.process.SelectProcessTypePage"; //$NON-NLS-1$

    private Button btnTypeBefore;

    private Button btnTypeRunnable;

    private Button btnSmartView;

    private Button btnTypeOther;

    private Text descriptionTxt;

    private int currentSelectedType = NewProcessWizard.BEFORE_TYPE;

    private int type;

    /**
     * Create the wizard.
     *
     * @param type
     */
    public SelectProcessTypePage(int type) {
        super(PAGE_ID);
        this.type = type;
        setTitle(Messages.NewProcessWizard_title);
        setDescription(Messages.SelectProcessTypePage_description);
    }

    /**
     * Create contents of the wizard.
     *
     * @param parent
     */
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);

        setControl(container);
        container.setLayout(new GridLayout(1, false));

        Group typeGroup = new Group(container, SWT.NONE);
        typeGroup.setText(Messages.SelectProcessTypePage_selectType);
        typeGroup.setLayout(new GridLayout(1, false));
        typeGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

        btnTypeBefore = new Button(typeGroup, SWT.RADIO);
        btnTypeBefore.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                currentSelectedType = NewProcessWizard.BEFORE_TYPE;
                updateAll();
            }
        });
        // btnTypeBefore.setSelection(true);
        btnTypeBefore.setText(Messages.SelectProcessTypePage_createBeforeProcess);
        btnTypeRunnable = new Button(typeGroup, SWT.RADIO);
        btnTypeRunnable.setText(Messages.SelectProcessTypePage_createRunnableProcess);
        btnTypeRunnable.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                currentSelectedType = NewProcessWizard.RUNNABLE_TYPE;
                updateAll();
            }
        });
        btnSmartView = new Button(typeGroup, SWT.RADIO);
        btnSmartView.setText(Messages.SelectProcessTypePage_createSmartViewProcess);
        btnSmartView.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                currentSelectedType = TYPE_SMARTVIEW;
                updateAll();
            }
        });

        btnTypeOther = new Button(typeGroup, SWT.RADIO);
        btnTypeOther.setText(Messages.SelectProcessTypePage_createOtherProcess);
        btnTypeOther.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                currentSelectedType = TYPE_OTHER;
                updateAll();
            }
        });
        // description
        Group descriptionGroup = new Group(container, SWT.NONE);
        descriptionGroup.setText(Messages.SelectProcessTypePage_descText);
        FillLayout fl_descriptionGroup = new FillLayout(SWT.HORIZONTAL);
        fl_descriptionGroup.marginWidth = 5;
        fl_descriptionGroup.marginHeight = 5;
        descriptionGroup.setLayout(fl_descriptionGroup);
        descriptionGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        descriptionTxt = new Text(descriptionGroup, SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);

        //
        updateBtnState();

        // init
        updateAll();
    }

    private void updateBtnState() {
        switch (type) {
        case TYPE_BEFORESAVE:
            defaultProcessType = TYPE_BEFORESAVE;
            setBtnEnabled(btnTypeBefore);
            break;
        case TYPE_BEFOREDEL:
            defaultProcessType = TYPE_BEFOREDEL;
            setBtnEnabled(btnTypeBefore);
            break;
        case TYPE_ENTITYACTION:
            defaultProcessType = TYPE_ENTITYACTION;
            setBtnEnabled(btnTypeRunnable);
            currentSelectedType = NewProcessWizard.RUNNABLE_TYPE;
            break;
        case TYPE_WELCOMEACTION:
            defaultProcessType = TYPE_WELCOMEACTION;
            setBtnEnabled(btnTypeRunnable);
            currentSelectedType = NewProcessWizard.RUNNABLE_TYPE;
            break;
        case TYPE_SMARTVIEW:
            defaultProcessType = TYPE_SMARTVIEW;
            setBtnEnabled(btnSmartView);
            currentSelectedType = TYPE_SMARTVIEW;
            break;
        case TYPE_OTHER:
            defaultProcessType = TYPE_OTHER;
            setBtnEnabled(btnTypeOther);
            currentSelectedType = TYPE_OTHER;
            break;
        default:
            btnTypeBefore.setSelection(true);
            break;
        }
    }

    private void setBtnEnabled(Button btn) {
        btnTypeBefore.setEnabled(false);
        btnTypeRunnable.setEnabled(false);
        btnSmartView.setEnabled(false);
        btnTypeOther.setEnabled(false);

        btn.setEnabled(true);
        btn.setSelection(true);
    }

    private String inputName = null;

    public String getInputName() {
        return this.inputName;
    }

    private void updateAll() {
        updateProcessTypeDescription();
        updateProcessNamePage();
    }

    private int defaultProcessType = 0;

    private void updateProcessNamePage() {
        InputProcessNamePage page = (InputProcessNamePage) getWizard().getPage(InputProcessNamePage.PAGE_ID);
        page.updateProcessTypeComposite(currentSelectedType, defaultProcessType);
    }

    private void updateProcessTypeDescription() {

        String desc = getProcessDescription(currentSelectedType);
        descriptionTxt.setText(desc);
    }

    private String getProcessDescription(int type) {
        String desc = ""; //$NON-NLS-1$
        switch (currentSelectedType) {
        case NewProcessWizard.BEFORE_TYPE:
            desc = Messages.SelectProcessTypePage_beforeProcessDesc;
            break;

        case NewProcessWizard.RUNNABLE_TYPE:
            desc = Messages.SelectProcessTypePage_runnableProcessDesc;
            break;

        case TYPE_OTHER:
            desc = Messages.SelectProcessTypePage_otherProcessDesc;
            break;
        case TYPE_SMARTVIEW:
            desc = Messages.SelectProcessTypePage_smartviewProcessDesc;
            break;
        }
        return desc;
    }
}
