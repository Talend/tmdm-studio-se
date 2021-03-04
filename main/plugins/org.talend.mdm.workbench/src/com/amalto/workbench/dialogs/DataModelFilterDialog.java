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

import java.util.List;

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
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.utils.DataModelFilter;
import com.amalto.workbench.utils.SchemaElementNameFilterDes;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.widgets.LabelCombo;
import com.amalto.workbench.widgets.WidgetFactory;

public class DataModelFilterDialog extends Dialog {

    FormToolkit toolkit = WidgetFactory.getWidgetFactory();

    TreeObject xObject;

    DataModelFilter dataModelFilter;

    private SchemaElementNameFilterDes nameFilterDes;

    private LabelCombo comboRole;

    private Button btnReadOnly;

    private Button btnWrite;

    private Button btnHidden;

    private Button btnAll;

    private Button btnEnableNameFilter;

    private Text txtNameFilterExpression;

    private Text txtNameFilterComment;

    private static final String LABEL_NAMEFILTER = Messages.DataModelFilterDialog_LabelNameFilter;

    private static final String LABEL_NAMEFILTERCOMMENT = Messages.DataModelFilterDialog_LabelNameFilterComment;

    public DataModelFilterDialog(Shell parentShell, TreeObject xObject, DataModelFilter dataModelFilter,
            SchemaElementNameFilterDes nameFilterDes) {
        super(parentShell);
        this.xObject = xObject;
        this.dataModelFilter = dataModelFilter;
        this.nameFilterDes = nameFilterDes;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        parent.getShell().setText(Messages.DataModelFilterDialog_DialogTitle);
        Composite composite = (Composite) super.createDialogArea(parent);
        composite.setBackground(composite.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        GridLayout layout = (GridLayout) composite.getLayout();
        layout.numColumns = 1;

        createSecurityFilterArea(composite);

        createNameFilterArea(composite);

        return composite;
    }

    private void createNameFilterArea(Composite compParent) {

        Group nameFilterGroup = new Group(compParent, SWT.NORMAL);
        nameFilterGroup.setText(Messages.DataModelFilterDialog_NameFilterSetting);
        nameFilterGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        nameFilterGroup.setLayout(new GridLayout());
        nameFilterGroup.setBackground(compParent.getDisplay().getSystemColor(SWT.COLOR_WHITE));

        btnEnableNameFilter = toolkit.createButton(nameFilterGroup, LABEL_NAMEFILTER, SWT.CHECK);
        btnEnableNameFilter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        btnEnableNameFilter.setSelection(nameFilterDes.isEnable());

        txtNameFilterExpression = toolkit.createText(nameFilterGroup, nameFilterDes.getSourceFilterExpression());
        txtNameFilterExpression.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        txtNameFilterComment = new Text(nameFilterGroup, SWT.NONE | SWT.READ_ONLY | SWT.WRAP | SWT.MULTI);
        txtNameFilterComment.setText(LABEL_NAMEFILTERCOMMENT);
        txtNameFilterComment.setEnabled(false);
        txtNameFilterComment.setBackground(compParent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        txtNameFilterComment.setForeground(compParent.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
        txtNameFilterComment.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));

        refreshNameFilterArea();

        addUIListenersInNameFilterArea();
    }

    protected List<String> getRoles() {
        return Util.getChildren(this.xObject.getServerRoot(), TreeObject.ROLE);
    }
    private void createSecurityFilterArea(Composite compParent) {
        Group secutityFilterGroup = new Group(compParent, SWT.NORMAL);
        secutityFilterGroup.setText(Messages.DataModelFilterDialog_SecurityFilterSetting);
        secutityFilterGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        secutityFilterGroup.setLayout(new GridLayout());
        secutityFilterGroup.setBackground(compParent.getDisplay().getSystemColor(SWT.COLOR_WHITE));

        comboRole = new LabelCombo(toolkit, secutityFilterGroup, Messages.DataModelFilterDialog_Role, SWT.BORDER, 2);
        comboRole.getCombo().setEditable(false);
        // List<String> roles=Util.getCachedXObjectsNameSet(this.xObject, TreeObject.ROLE);
        List<String> roles = getRoles();// Util.getChildren(this.xObject.getServerRoot(), TreeObject.ROLE);
        comboRole.getCombo().setItems(roles.toArray(new String[roles.size()]));
        if (dataModelFilter.getRole() == null || dataModelFilter.getRole().length() == 0) {
            comboRole.getCombo().select(0);
        } else {
            comboRole.getCombo().setText(dataModelFilter.getRole());
        }

        btnAll = toolkit.createButton(secutityFilterGroup, Messages.DataModelFilterDialog_All, SWT.RADIO);
        btnAll.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        btnAll.setSelection(dataModelFilter.isAll());

        btnReadOnly = toolkit.createButton(secutityFilterGroup, Messages.DataModelFilterDialog_ReadOnly, SWT.RADIO);
        btnReadOnly.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        btnReadOnly.setSelection(dataModelFilter.isReadOnly());

        btnWrite = toolkit.createButton(secutityFilterGroup, Messages.DataModelFilterDialog_WriteAccess, SWT.RADIO);
        btnWrite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        btnWrite.setSelection(dataModelFilter.isWriteAccess());

        btnHidden = toolkit.createButton(secutityFilterGroup, Messages.DataModelFilterDialog_NoAccess, SWT.RADIO);
        btnHidden.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        btnHidden.setSelection(dataModelFilter.isHiddenAccess());
    }

    private void refreshNameFilterArea() {
        txtNameFilterExpression.setEnabled(btnEnableNameFilter.getSelection());
    }

    private void addUIListenersInNameFilterArea() {

        btnEnableNameFilter.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                refreshNameFilterArea();
            }
        });
    }

    @Override
    protected void okPressed() {
        dataModelFilter.setRole(comboRole.getCombo().getText());
        dataModelFilter.setReadOnly(btnReadOnly.getSelection());
        dataModelFilter.setWriteAccess(btnWrite.getSelection());
        dataModelFilter.setHiddenAccess(btnHidden.getSelection());
        dataModelFilter.setAll(btnAll.getSelection());

        nameFilterDes.setEnable(btnEnableNameFilter.getSelection());
        nameFilterDes.setSourceFilterExpression(txtNameFilterExpression.getText().trim());

        super.okPressed();
    }

    @Override
    protected Control createButtonBar(Composite parent) {
        Composite composite = (Composite) super.createDialogArea(parent);
        composite.setBackground(composite.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        createButton(composite, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        createButton(composite, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
        return composite;
    }

}
