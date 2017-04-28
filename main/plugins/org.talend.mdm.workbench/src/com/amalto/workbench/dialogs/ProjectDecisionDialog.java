// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.workbench.dialogs;

import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.webservices.TMDMService;
import com.amalto.workbench.webservices.WSDataClusterPK;
import com.amalto.workbench.webservices.WSDataModelPK;
import com.amalto.workbench.webservices.WSRegexDataClusterPKs;
import com.amalto.workbench.webservices.WSRegexDataModelPKs;

public class ProjectDecisionDialog extends Dialog {

    protected Combo dataClustersCombo;

    protected Combo dataModelsCombo;

    protected Button overwriteButton;

    private static Pattern dp = Pattern.compile("PROJECT\\s*\\((.*?),(.*?)[,(.*?)]?\\)\\s*", Pattern.DOTALL//$NON-NLS-1$
            | Pattern.CASE_INSENSITIVE);

    private TreeObject transformerObject = null;

    private String decision = null;

    private SelectionListener caller = null;

    private String title = "";//$NON-NLS-1$

    /**
     * @param parentShell
     */
    public ProjectDecisionDialog(TreeObject transformerObject, String decision, Shell parentShell, String title,
            SelectionListener caller) {
        super(parentShell);
        this.transformerObject = transformerObject;
        this.decision = decision;
        this.caller = caller;
        this.title = title;
    }

    @Override
    protected Control createDialogArea(Composite parent) {

        // Should not really be here but well,....
        parent.getShell().setText(this.title);

        Composite composite = (Composite) super.createDialogArea(parent);

        GridLayout layout = (GridLayout) composite.getLayout();
        layout.numColumns = 2;
        // layout.verticalSpacing = 10;

        try {

            // process the decision
            String dataClusterName = "";//$NON-NLS-1$
            String dataModelName = "";//$NON-NLS-1$
            boolean doOverwrite = true;
            Matcher m = dp.matcher(decision);
            if (m.matches()) {
                dataClusterName = m.group(1);
                dataModelName = m.group(2);
                if (m.groupCount() >= 4) {
                    doOverwrite = (!"false".equals(m.group(3)));//$NON-NLS-1$
                }
            }

            TMDMService port = Util.getMDMService(new URL(transformerObject.getEndpointAddress()),
                    transformerObject.getUsername(), transformerObject.getPassword());

            // Grab the available Clusters
            List<WSDataClusterPK> dcpks = port.getDataClusterPKs(new WSRegexDataClusterPKs(".*")).getWsDataClusterPKs();//$NON-NLS-1$

            Label dataClustersLabel = new Label(composite, SWT.NULL);
            dataClustersLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
            dataClustersLabel.setText(Messages.ProjectDecisionDialog_DataContainers);

            dataClustersCombo = new Combo(composite, SWT.DROP_DOWN);
            dataClustersCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
            int dataClusterSelect = -1;
            if (dcpks != null) {
                int i = 0;
                for (WSDataClusterPK pk : dcpks) {
                    dataClustersCombo.add(pk.getPk());
                    if (pk.getPk().equals(dataClusterName)) {
                        dataClusterSelect = i;
                    }
                    i++;
                }
            }
            dataClustersCombo.select(dataClusterSelect);

            // Grab the available Models
            List<WSDataModelPK> dmpks = port.getDataModelPKs(new WSRegexDataModelPKs(".*")).getWsDataModelPKs();//$NON-NLS-1$

            Label dataModelsLabel = new Label(composite, SWT.NULL);
            dataModelsLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
            dataModelsLabel.setText(Messages.ProjectDecisionDialog_DataModels);

            dataModelsCombo = new Combo(composite, SWT.DROP_DOWN);
            dataModelsCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
            int dataModelSelect = -1;
            if (dmpks != null) {
                int i = 0;
                for (WSDataModelPK pk : dmpks) {
                    dataModelsCombo.add(pk.getPk());
                    if (pk.getPk().equals(dataModelName)) {
                        dataModelSelect = i;
                    }
                    i++;
                }
            }
            dataModelsCombo.select(dataModelSelect);

            // Overwrite
            Label overwriteLabel = new Label(composite, SWT.NULL);
            overwriteLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
            overwriteLabel.setText(Messages.ProjectDecisionDialog_Overwrite);

            overwriteButton = new Button(composite, SWT.CHECK);
            overwriteButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
            overwriteButton.setSelection(doOverwrite);

        } catch (Exception e) {
            if (!Util.handleConnectionException(this.getShell(), e, null)) {
                MessageDialog.openError(ProjectDecisionDialog.this.getShell(), Messages.ProjectDecisionDialog_ErrorTitle,
                        Messages.bind(Messages.ProjectDecisionDialog_ErrorMsg, e.getMessage()));
            }
        }

        return composite;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);
        getButton(IDialogConstants.OK_ID).addSelectionListener(this.caller);
        getButton(IDialogConstants.CANCEL_ID).addSelectionListener(this.caller);
    }

    @Override
    protected void okPressed() {
        setReturnCode(OK);
        getButton(IDialogConstants.OK_ID).setData("dialog", ProjectDecisionDialog.this);//$NON-NLS-1$
        // no close let Action Handler handle it
    }

    @Override
    protected void cancelPressed() {
        setReturnCode(CANCEL);
        getButton(IDialogConstants.CANCEL_ID).setData("dialog", ProjectDecisionDialog.this);//$NON-NLS-1$
        // no close let Action Handler handle it
    }

    /**************************************************************************************************
     * Public getters read by caller
     ***************************************************************************************************/

    public String getDataClusterName() {
        if (dataClustersCombo.getSelectionIndex() == -1) {
            return null;
        }
        return dataClustersCombo.getItem(dataClustersCombo.getSelectionIndex());
    }

    public String getDataModelName() {
        if (dataModelsCombo.getSelectionIndex() == -1) {
            return null;
        }
        return dataModelsCombo.getItem(dataModelsCombo.getSelectionIndex());
    }

    public boolean doOverwrite() {
        return overwriteButton.getSelection();
    }

    public String getDecision() {
        return "PROJECT(" + getDataClusterName() + "," + getDataModelName() + "," + (doOverwrite() ? "true" : "false") + ")";//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$//$NON-NLS-5$//$NON-NLS-6$
    }

}
