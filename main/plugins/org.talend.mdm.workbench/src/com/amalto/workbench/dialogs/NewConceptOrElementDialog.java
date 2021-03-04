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

import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;

import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.utils.XSDUtil;
import com.amalto.workbench.widgets.ConceptComposite;
import com.amalto.workbench.widgets.ElementComposite;

/**
 * this class is to represent the concept or element creation dialog
 *
 * @author fliu
 *
 */
public class NewConceptOrElementDialog extends Dialog implements ModifyListener, SelectionListener {

    private Text typeNameText = null;

    private Label infoLabel = null;

    private Button complexTypeBtn, simpleTypeBtn;

    protected List<String> customTypes = null;

    protected List<String> builtInTypes = null;

    private SelectionListener caller = null;

    private String typeName = "";//$NON-NLS-1$

    private String title = "";//$NON-NLS-1$

    private ConceptComposite conceptPanel = null;

    private ElementComposite elemPanel = null;

    private XSDSchema schema;

    public NewConceptOrElementDialog(SelectionListener caller, Shell parentShell, XSDSchema sa, String title,
            List<String> customTypes, List<String> builtInTypes) {
        super(parentShell);
        this.caller = caller;
        this.title = title;

        this.customTypes = customTypes;
        this.builtInTypes = builtInTypes;

        schema = sa;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        parent.getShell().setText(title);

        Composite composite = (Composite) super.createDialogArea(parent);
        GridLayout layout = (GridLayout) composite.getLayout();
        layout.numColumns = 2;
        Label typeNameLabel = new Label(composite, SWT.NONE);
        typeNameLabel.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true, 1, 1));
        typeNameLabel.setText(Messages.NewConceptOrElementDialog_Name);

        typeNameText = new Text(composite, SWT.SINGLE | SWT.BORDER);
        typeNameText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
        typeNameText.addModifyListener(this);
        typeNameText.setFocus();

        complexTypeBtn = new Button(composite, SWT.RADIO);
        complexTypeBtn.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true, 2, 1));
        complexTypeBtn.setText(Messages.NewConceptOrElementDialog_ComplexType);
        complexTypeBtn.setSelection(true);
        complexTypeBtn.addSelectionListener(this);

        conceptPanel = new ConceptComposite(composite, true, Util.getComplexTypes(schema), false);

        simpleTypeBtn = new Button(composite, SWT.RADIO);
        simpleTypeBtn.setText(Messages.NewConceptOrElementDialog_SimpleType);
        simpleTypeBtn.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true, 2, 1));
        simpleTypeBtn.addSelectionListener(this);

        elemPanel = new ElementComposite(conceptPanel.getComposite(), customTypes, builtInTypes, null, true);
        composite = elemPanel.getComposite();
        elemPanel.addModifyListener(this);

        infoLabel = new Label(composite, SWT.NONE);
        infoLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

        conceptPanel.getTypeCombo().addModifyListener(this);
        elemPanel.getTypeCombo().addModifyListener(this);

        maskTypeWidgets();

        return composite;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);
        getButton(IDialogConstants.OK_ID).setEnabled(false);
        getButton(IDialogConstants.OK_ID).addSelectionListener(this.caller);
    }

    @Override
    protected void okPressed() {
        boolean valid = true;
        typeName = typeNameText.getText().trim();

        getButton(IDialogConstants.OK_ID).setData("dialog", NewConceptOrElementDialog.this);//$NON-NLS-1$
        // no close let Action Handler handle it
    }

    public void modifyText(ModifyEvent e) {
//        if (e != null && e.widget == typeNameText) {
//            conceptPanel.setText(typeNameText.getText() + "Type");
//        }

        if (typeNameText.getText().trim().equals("")) {//$NON-NLS-1$
            infoLabel.setText(Messages.NewConceptOrElementDialog_ElementNamecannotbeEmpty);
            getButton(IDialogConstants.OK_ID).setEnabled(false);
            return;
        } else if (typeNameText.getText().replaceAll("\\s", "").length() != typeNameText.getText().length()) {//$NON-NLS-1$//$NON-NLS-2$
            infoLabel.setText(Messages.NewConceptOrElementDialog_NameCannotContainEmpty);
            getButton(IDialogConstants.OK_ID).setEnabled(false);
            return;
        } else if (!XSDUtil.isValidatedXSDName(typeNameText.getText().trim())) {
            infoLabel.setText(Messages.InvalidName_Message);
            getButton(IDialogConstants.OK_ID).setEnabled(false);
            return;
        } else if (simpleTypeBtn.getSelection()
                && (elemPanel.getText().trim().equals("") || elemPanel.getText().replaceAll("\\s", "").length() != elemPanel//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
                        .getText().length())) {
            infoLabel.setText(Messages.NewConceptOrElementDialog_ComboValueCannotbeEmptyOrXX);
            getButton(IDialogConstants.OK_ID).setEnabled(false);
            return;
        } else if (complexTypeBtn.getSelection() && !conceptPanel.getText().trim().equals("")//$NON-NLS-1$
                && conceptPanel.getText().replaceAll("\\s", "").length() != conceptPanel.getText().length()) {//$NON-NLS-1$//$NON-NLS-2$
            infoLabel.setText(Messages.NewConceptOrElementDialog_ComboValueCannotbeEmptyOrXX);
            getButton(IDialogConstants.OK_ID).setEnabled(false);
            return;
        } else {
            EList list = schema.getElementDeclarations();
            for (Iterator iter = list.iterator(); iter.hasNext();) {
                XSDElementDeclaration decl = (XSDElementDeclaration) iter.next();
                if (decl.getName().equalsIgnoreCase(typeNameText.getText())) {
                    infoLabel.setText(Messages.NewConceptOrElementDialog_ElementEntityAlreadyExists);
                    getButton(IDialogConstants.OK_ID).setEnabled(false);
                    return;
                }
            }

            if ((e != null && e.widget == conceptPanel.getTypeCombo()) || (e == null && conceptPanel.getTypeCombo().isEnabled())) {
                validateType(conceptPanel.getText(), false);
                return;
            } else if ((e != null && e.widget == elemPanel.getTypeCombo()) || (e == null && elemPanel.getTypeCombo().isEnabled())) {
                validateType(elemPanel.getText(), true);
                return;
            }
        }
        getButton(IDialogConstants.OK_ID).setEnabled(true);
        infoLabel.setText("");//$NON-NLS-1$
    }

    private void validateType(String typeName, boolean forConcept) {
        getButton(IDialogConstants.OK_ID).setEnabled(true);
        infoLabel.setText("");//$NON-NLS-1$

        for (XSDTypeDefinition specType : schema.getTypeDefinitions()) {
            if (forConcept && specType instanceof XSDSimpleTypeDefinition) {
                continue;
            } else if (!forConcept && specType instanceof XSDComplexTypeDefinition) {
                continue;
            }

            String typeToCompare = typeName;
            int delimiter = typeToCompare.indexOf(" : ");//$NON-NLS-1$
            if (delimiter != -1) {
                typeToCompare = typeToCompare.substring(0, delimiter);
            }
            if (typeToCompare.equals(specType.getName())) {
                infoLabel.setText(Messages.NewConceptOrElementDialog_SameTypeNameAlreadyExists);
                getButton(IDialogConstants.OK_ID).setEnabled(false);
                return;
            }
        }
    }

    private void maskTypeWidgets() {
        conceptPanel.setSelectAllWidgets(complexTypeBtn.getSelection());
        elemPanel.setSelectAllWidgets(!complexTypeBtn.getSelection());
    }

    public void widgetSelected(SelectionEvent e) {
        maskTypeWidgets();
        modifyText(null);
    }

    public void widgetDefaultSelected(SelectionEvent e) {

    }

    public String getTypeName() {
        return typeName;
    }

    public String getSuperTypeName() {
        return conceptPanel.getSuperName();
    }

    public boolean isChoice() {
        return conceptPanel.isChoice();
    }

    public boolean isAll() {
        return conceptPanel.isAll();
    }

    public String getComplexType() {
        return conceptPanel.getText();
    }

    public boolean isAbstract() {
        return conceptPanel.isAbstract();
    }

    public boolean isBuildIn() {
        return elemPanel.isBuiltIn();
    }

    public String getElementType() {
        return elemPanel.getText();
    }

    public boolean isComplexType() {
        return complexTypeBtn.getSelection();
    }

    public boolean isSimpleType() {
        return simpleTypeBtn.getSelection();
    }
}
