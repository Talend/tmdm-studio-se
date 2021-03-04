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
package com.amalto.workbench.detailtabs.sections.composites;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.amalto.workbench.detailtabs.sections.util.CommitBarListenerRegistry;
import com.amalto.workbench.i18n.Messages;

public class CommitBarComposite extends Composite {

	private Button btnSubmit;

	private Button btnReset;

	private List<CommitBarListener> listeners = new ArrayList<CommitBarListener>();

	public CommitBarComposite(Composite parent, int style) {
		super(parent, style);

		final GridLayout gridLayout = new GridLayout();
		gridLayout.horizontalSpacing = 10;
		gridLayout.makeColumnsEqualWidth = true;
		gridLayout.numColumns = 2;
		setLayout(gridLayout);

		setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		btnSubmit = new Button(this, SWT.NONE);
		final GridData gd_btnSubmit = new GridData(SWT.FILL, SWT.FILL, false,
				false);
		gd_btnSubmit.heightHint = 18;
		gd_btnSubmit.widthHint = 74;
		btnSubmit.setLayoutData(gd_btnSubmit);
		btnSubmit.setText(Messages.CommitBarComposite_Apply);

		btnReset = new Button(this, SWT.NONE);
		btnReset.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		btnReset.setText(Messages.CommitBarComposite_Reset);

		initUIListeners();
	}

	public void addCommitListener(CommitBarListener listener) {
		if (listener != null && !listeners.contains(listener)) {
			listeners.add(listener);
		}

	}

	public void removeCommitListener(CommitBarListener listener) {
		listeners.remove(listener);
	}

	public void removeAllCommitBarListeners() {
		listeners.clear();
	}

	public List<CommitBarListener> getCommitBarListeners() {
		return listeners;
	}

	public void fireSubmit() {
		for (CommitBarListener eachListener : listeners)
			eachListener.onSubmit();
	}

	public void fireSubmitAllTabs() {
		for (CommitBarListener eachListener : CommitBarListenerRegistry
				.getInstance().getAllRegistedListeners())
			eachListener.onSubmit();
	}

	private void fireReset() {
		for (CommitBarListener eachListener : listeners)
			eachListener.onReset();
	}

	private void initUIListeners() {

		btnSubmit.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				fireSubmit();
				//fireSubmitAllTabs();
			}
		});

		btnReset.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				fireReset();
			}
		});
	}

	public Button getBtnSubmit() {
		return btnSubmit;
	}

	public void setBtnSubmit(Button btnSubmit) {
		this.btnSubmit = btnSubmit;
	}

	public Button getBtnReset() {
		return btnReset;
	}

	public void setBtnReset(Button btnReset) {
		this.btnReset = btnReset;
	}

	public interface CommitBarListener {

		public void onReset();

		public void onSubmit();

	}
}
