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
package com.amalto.workbench.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

import com.amalto.workbench.i18n.Messages;

public class JobProcesssOptionsDialog extends Dialog {

	private String title;

	private Button btnIntegrated;

	private Button btnContext;

	private Button btnEmbedded;

	private Button btnWebService;

	private Group grpRecord;

	private Group grpExecution;

	private Parameter parameter = Parameter.INTEGRATED;

	private Execution execution = Execution.EMBEDDED;

	public static enum Parameter {
		INTEGRATED,
		CONTEXT_VARIABLE
	}

	public static enum Execution {
		EMBEDDED,
		WEB_SERVICE
	}

	public JobProcesssOptionsDialog(Shell parentShell, Execution execution) {
		super(parentShell);
		this.execution = execution;
	}

	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);

		grpRecord = new Group(composite, SWT.NONE);
		grpRecord.setLayout(new GridLayout(1, false));
		grpRecord.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				true, 1, 1));
		grpRecord
				.setText(Messages.JobProcesssOptionsDialog_GroupText);

		btnIntegrated = new Button(grpRecord, SWT.RADIO);
		btnIntegrated.setSize(78, 24);
		btnIntegrated.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				parameter = Parameter.INTEGRATED;
			}
		});
		btnIntegrated.setText(Messages.JobProcesssOptionsDialog_Integrated);

		btnContext = new Button(grpRecord, SWT.RADIO);
		btnContext.setSize(278, 24);
		btnContext
				.setText(Messages.JobProcesssOptionsDialog_BtnText);
		btnContext.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				parameter = Parameter.CONTEXT_VARIABLE;
			}
		});

		grpExecution = new Group(composite, SWT.NONE);
		grpExecution.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				true, true, 1, 1));
		grpExecution.setText(Messages.JobProcesssOptionsDialog_GrpGroupText);
		grpExecution.setLayout(new GridLayout(1, false));
		btnEmbedded = new Button(grpExecution, SWT.RADIO);
		btnEmbedded.setSize(191, 24);

		btnEmbedded.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				execution = Execution.EMBEDDED;
				doUpdate();
			}
		});
		btnEmbedded.setText(Messages.JobProcesssOptionsDialog_EmbeddedBtnText);

		btnWebService = new Button(grpExecution, SWT.RADIO);
		btnWebService.setSize(217, 24);
		btnWebService.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				execution = Execution.WEB_SERVICE;
				doUpdate();
			}
		});
		btnWebService.setText(Messages.JobProcesssOptionsDialog_WebserviceBtnText);

		// Initial value display
		doUpdate();

		return composite;
	}

	private void doUpdate() {
		if(execution == Execution.WEB_SERVICE) {
			btnIntegrated.setEnabled(false);
            btnEmbedded.setEnabled(false);
			btnContext.setSelection(true);
            btnWebService.setEnabled(true);
			// When web service is selected, only allowed value for execution is "context"
			parameter = Parameter.CONTEXT_VARIABLE;
		}

		if(execution == Execution.EMBEDDED) {
			btnIntegrated.setEnabled(true);
            btnEmbedded.setEnabled(true);
            btnWebService.setEnabled(false);
            btnContext.setEnabled(false);
		}

		btnIntegrated.setSelection(parameter == Parameter.INTEGRATED);
		btnContext.setSelection(parameter == Parameter.CONTEXT_VARIABLE);

		btnEmbedded.setSelection(execution == Execution.EMBEDDED);
		btnWebService.setSelection(execution == Execution.WEB_SERVICE);
	}

	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		if (title != null) {
			shell.setText(title);
		}
	}

	protected void createButtonsForButtonBar(Composite parent) {
		// create Generate and Cancel buttons by default
		Button button = createButton(parent, IDialogConstants.OK_ID,
				IDialogConstants.OK_LABEL, true);
		button.setText(Messages.JobProcesssOptionsDialog_Generate);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	public Parameter getParameter() {
		return parameter;
	}

	public Execution getExecution() {
		return execution;
	}

}
