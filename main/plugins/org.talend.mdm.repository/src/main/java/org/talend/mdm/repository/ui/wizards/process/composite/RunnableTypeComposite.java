// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
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

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.ui.wizards.process.ConfigRedirectURLPage;

import com.amalto.workbench.dialogs.AnnotationLanguageLabelsDialog;
import com.amalto.workbench.widgets.MultilingualDescParser;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class RunnableTypeComposite extends AbstractProcessTypeComposite {

    private Button runnableBun;

    private Button standaloneBun;

    private Text messageText;

    private Composite descContainer;

    private Label lblMessage;

    private Button btnNewButton;

    /**
     * DOC hbhong BeforeProcessTypeComposite constructor comment.
     * 
     * @param parent
     * @param defaultProcessType
     */
    public RunnableTypeComposite(Composite parent, int defaultProcessType, SelectionListener selectionListener) {
        super(parent, selectionListener);
        setLayout(new GridLayout(2, false));

        runnableBun = new Button(this, SWT.RADIO);
        runnableBun.addSelectionListener(selectionListener);
        runnableBun.setText(Messages.RunnableTypeComposite_createRunnableProcess);

        descContainer = new Composite(this, SWT.NONE);
        descContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        descContainer.setLayout(new GridLayout(3, false));
        lblMessage = new Label(descContainer, SWT.NONE);
        lblMessage.setText(Messages.SelectProcessTypePage_descText);

        btnNewButton = new Button(descContainer, SWT.NONE);
        btnNewButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                Map<String, String> descriptionsMap = new LinkedHashMap<String, String>();
                MultilingualDescParser.parseMultiLanguageString(messageText.getText().trim(), descriptionsMap);
                AnnotationLanguageLabelsDialog dialog = new AnnotationLanguageLabelsDialog(descriptionsMap, null, getShell(),
                        Messages.ConfigReturnMessagePage_setMessage);
                if (dialog.open() == IDialogConstants.OK_ID) {
                    StringBuffer output = new StringBuffer();
                    for (Map.Entry<String, String> m : dialog.getDescriptionsMap().entrySet()) {
                        output.append("[").append(m.getKey().toUpperCase()).append(":").append(m.getValue()).append("]");//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
                    }
                    messageText.setText(output.toString());
                }
            }
        });
        btnNewButton.setText(Messages.ConfigReturnMessagePage_selectMessage);

        messageText = new Text(descContainer, SWT.BORDER);
        messageText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                // getWizard().getContainer().updateButtons();
            }
        });
        messageText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        standaloneBun = new Button(this, SWT.RADIO);
        standaloneBun.setText(Messages.RunnableTypeComposite_createStandaloneProcess);
        standaloneBun.addSelectionListener(selectionListener);

        runnableBun.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                boolean selected = runnableBun.getSelection();
                enableRunnablePart(selected);
            }

        });

        updateBtnState(defaultProcessType);
    }

    public void updateBtnState(int defaultProcessType) {
        if (defaultProcessType == TYPE_ENTITYACTION) {
            runnableBun.setSelection(true);
            standaloneBun.setSelection(false);
            standaloneBun.setEnabled(false);
        } else if (defaultProcessType == TYPE_WELCOMEACTION) {
            standaloneBun.setSelection(true);
            runnableBun.setSelection(false);
            runnableBun.setEnabled(false);
            enableRunnablePart(false);
        } else {
            runnableBun.setSelection(true);
            standaloneBun.setSelection(false);
        }
    }

    private void enableRunnablePart(boolean selected) {
        descContainer.setEnabled(selected);
        lblMessage.setEnabled(selected);
        btnNewButton.setEnabled(selected);
        messageText.setEnabled(selected);
    }

    @Override
    public int getCurrentProcessType() {
        if (runnableBun.getSelection()) {
            return TYPE_ENTITYACTION;
        }
        if (standaloneBun.getSelection()) {
            return TYPE_WELCOMEACTION;
        }
        return -1;
    }

    @Override
    public String getDesc() {
        return messageText.getText();
    }

    @Override
    public boolean needShowSelectEntityBun() {
        return true;
    }

    @Override
    public String getProcessPrefix() {
        switch (getCurrentProcessType()) {
        case TYPE_ENTITYACTION:
            return "Runnable_"; //$NON-NLS-1$
        case TYPE_WELCOMEACTION:
            return "Runnable#"; //$NON-NLS-1$
        }
        return ""; //$NON-NLS-1$
    }

    @Override
    public String getConfigWizardPageId() {
        return ConfigRedirectURLPage.PAGE_ID;
    }

}
