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
package com.amalto.workbench.widgets;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.talend.mdm.commmon.util.core.EUUIDCustomType;

import com.amalto.workbench.i18n.Messages;

/**
 * this class is meant to encapsulate all widgets rendering element it can output composite to populate element form
 *
 * @author Developer
 *
 */
public class ElementComposite {

    private CCombo typeCombo = null;

    private Button customButton = null;

    private Button builtInButton = null;

    private Label serverLabel = null;

    private Label tipLabel = null;

    private Composite container = null;

    public ElementComposite(Composite parent, final List customTypes, final List builtInTypes, String defaultTypeName,
            boolean encloseTextField) {
        GridLayout layout = (GridLayout) parent.getLayout();
        layout.numColumns = 2;
        layout.makeColumnsEqualWidth = false;

        // layout.verticalSpacing = 10;

        Group radioGroup = new Group(parent, SWT.SHADOW_NONE);
        radioGroup.setText(encloseTextField ? "" : "Simple Types");//$NON-NLS-1$//$NON-NLS-2$
        radioGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 2, 1));
        radioGroup.setLayout(new GridLayout(1, false));

        customButton = new Button(radioGroup, SWT.RADIO);
        customButton.setText("Custom");//$NON-NLS-1$
        customButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
        builtInButton = new Button(radioGroup, SWT.RADIO);
        builtInButton.setText("Built In");//$NON-NLS-1$
        builtInButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));

        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
        tipLabel = new Label(parent, SWT.NONE);
        // tipLabel.setText("Leave blank for anonymous");
        tipLabel.setLayoutData(gd);
        gd.widthHint = 250;

        if (encloseTextField) {
            Composite cr = new Composite(radioGroup, SWT.NONE);
            cr.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true, 2, 1));
            cr.setLayout(new GridLayout(2, false));
            serverLabel = new Label(cr, SWT.NONE);
            serverLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
            serverLabel.setText(Messages.ElementComposite_Type);
            typeCombo = new CCombo(cr, SWT.BORDER);
            typeCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        } else {
            serverLabel = new Label(parent, SWT.NONE);
            serverLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
            serverLabel.setText(Messages.ElementComposite_Type);

            typeCombo = new CCombo(parent, SWT.BORDER);
            typeCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        }

        typeCombo.setEditable(false);

        customButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                if (customButton.getSelection()) {
                    typeCombo.removeAll();
                    addCustomTypes(customTypes);

                    typeCombo.setEditable(true);
                    tipLabel.setText(Messages.ElementComposite_LeaveBlankForAnonymous);
                }
            }
        });

        builtInButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                if (builtInButton.getSelection()) {
                    typeCombo.removeAll();

                    addBuildinTypes(builtInTypes);
                    typeCombo.select(0);
                    typeCombo.setEditable(false);
                    tipLabel.setText("");//$NON-NLS-1$
                }
            }
        });

        addTypes(customTypes, builtInTypes, defaultTypeName);

        container = parent;
    }

    private void addTypes(final List customTypes, final List builtInTypes, String defaultTypeName) {
        if (customTypes == null || builtInTypes == null) {
            throw new IllegalArgumentException();
        }

        boolean isCustomType = customTypes.indexOf(defaultTypeName) != -1;

        if (isCustomType) {
            addCustomTypes(customTypes);
        } else {
            addBuildinTypes(builtInTypes);
        }
        customButton.setSelection(isCustomType);
        builtInButton.setSelection(!isCustomType);
        typeCombo.setEditable(isCustomType);

        if (defaultTypeName == null || defaultTypeName.trim().isEmpty()) {
            typeCombo.select(0);
        } else {
            typeCombo.setText(defaultTypeName);
        }
    }

    private void addCustomTypes(final List customTypes) {
        Set<String> uuidTypes = new HashSet<String>();
        // add uuid type aiming
        for (EUUIDCustomType current : EUUIDCustomType.values()) {
            String name = current.getName();
            uuidTypes.add(name);
            typeCombo.add(name);
        }

        for (Object current : customTypes) {
            String name = String.valueOf(current);
            if (!uuidTypes.contains(name)) {
                typeCombo.add(name);
            }
        }
    }

    private void addBuildinTypes(final List builtInTypes) {
        for (Iterator iter = builtInTypes.iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            typeCombo.add(name);
        }
    }

    public void addModifyListener(ModifyListener listener) {
        typeCombo.addModifyListener(listener);
    }

    public boolean isBuiltIn() {
        return builtInButton.getSelection();
    }

    public String getText() {
        return typeCombo.getText();
    }

    public void setFocus() {
        typeCombo.setFocus();
    }

    public CCombo getTypeCombo() {
        return typeCombo;
    }

    public Composite getComposite() {
        return container;
    }

    public void setSelectAllWidgets(boolean selected) {
        customButton.setEnabled(selected);
        builtInButton.setEnabled(selected);
        typeCombo.setEnabled(selected);
        serverLabel.setEnabled(selected);
    }
}
