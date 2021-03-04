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
package com.amalto.workbench.editors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.models.IXObjectModelListener;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.providers.XObjectEditorInput;
import com.amalto.workbench.utils.HttpClientUtil;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.utils.XtentisException;

public class JobMainPage extends AMainPage implements IXObjectModelListener {

    private static Log log = LogFactory.getLog(JobMainPage.class);

    protected String jobName;

    protected Label statusLabel;

    public JobMainPage(FormEditor editor) {
        super(editor, JobMainPage.class.getName(), Messages.bind(
                Messages.JobMainPage_Job,
                ((XObjectEditorInput) editor.getEditorInput()).getName()
                        + Util.getRevision((TreeObject) ((XObjectEditorInput) editor.getEditorInput()).getModel())));
        jobName = ((XObjectEditorInput) editor.getEditorInput()).getName();
    }

    @Override
    protected void createCharacteristicsContent(FormToolkit toolkit, Composite charSection) {

    }

    @Override
    protected void createFormContent(IManagedForm managedForm) {

        try {
            managedForm.getForm().setText(this.getTitle());

            // get the toolkit
            FormToolkit toolkit = managedForm.getToolkit();

            // get the body
            Composite composite = managedForm.getForm().getBody();
            composite.setLayout(new GridLayout(1, false));

            // Create a Router status holder
            Composite statusComposite = toolkit.createComposite(composite);
            statusComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
            statusComposite.setLayout(new GridLayout(3, false));
            Label descriptionLabel = toolkit.createLabel(statusComposite, Messages.JobMainPage_ServiceStatus, SWT.NULL);
            descriptionLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
            statusLabel = toolkit.createLabel(statusComposite, "                                           ", SWT.NULL);//$NON-NLS-1$
            statusLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
            // start/stop/suspend/resume
            Button checkButton = toolkit.createButton(statusComposite, Messages.JobMainPage_Check, SWT.CENTER);
            checkButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
            checkButton.addSelectionListener(new SelectionListener() {

                public void widgetDefaultSelected(SelectionEvent e) {

                }

                public void widgetSelected(SelectionEvent e) {

                    checkServiceStatus();
                }

            });
            // Composite separator = toolkit.createCompositeSeparator(composite);
            // separator.setLayoutData(
            // new GridData(SWT.FILL,SWT.CENTER,false,false,1,1)
            // );
            // ((GridData)separator.getLayoutData()).heightHint = 2;
        } catch (Exception e) {
            log.error(e.getMessage(), e);

        }
    }

    public void checkServiceStatus() {
        try {//
            if (jobName.endsWith(".zip")) {//$NON-NLS-1$
                statusLabel.setText(Messages.JobMainPage_Ready);
                return;
            }
            String job = this.jobName.substring(0, this.jobName.lastIndexOf("_"));//$NON-NLS-1$
            String jobversion = this.jobName.substring(0, this.jobName.lastIndexOf("."));//$NON-NLS-1$
            String URLPath = "http://" + getXObject().getEndpointHost() + ":" + getXObject().getEndpointPort() + "/" + jobversion//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
                    + "/services/" + job;//$NON-NLS-1$
            HttpClientUtil.getStringContentByHttpget(URLPath);
            statusLabel.setText(Messages.JobMainPage_Ready);
        } catch (XtentisException ex) {
            // if the http response status is not 200
            statusLabel.setText(Messages.JobMainPage_Fail);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            MessageDialog.openError(this.getSite().getShell(), Messages._Error,
                    Messages.bind(Messages.JobMainPage_ErrorMsg, e.getLocalizedMessage()));
        }
    }

    @Override
    protected void commit() {

    }

    @Override
    protected void createActions() {

    }

    @Override
    protected void refreshData() {

    }

    public void handleEvent(int type, TreeObject parent, TreeObject child) {

        refreshData();
    }

}
