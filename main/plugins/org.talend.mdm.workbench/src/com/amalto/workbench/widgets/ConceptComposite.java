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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDCompositor;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.impl.XSDModelGroupImpl;
import org.eclipse.xsd.impl.XSDParticleImpl;

import com.amalto.workbench.i18n.Messages;

/**
 * this class is meant to encapsulate all widgets rendering concept it can output a composite to populate concept form
 *
 * @author fliu
 *
 */
public class ConceptComposite {

    private CCombo typeNameCombo = null;

    private Button sequenceButton = null;

    private Button choiceButton = null;

    private Button allButton = null;

    private Label typeNameLabel = null;

    private Label messageLabel = null;

    private Composite container = null;

    private Button btnAbstract;

    private CCombo superTypeNameCombo;

    private Map<String, XSDComplexTypeDefinition> typeMap;

    public ConceptComposite(Composite parent, boolean encloseTextField, final List<XSDComplexTypeDefinition> types,
            final boolean newComplex) {

        GridLayout layout = (GridLayout) parent.getLayout();
        layout.numColumns = 2;
        // layout.verticalSpacing = 10;
        typeNameLabel = new Label(parent, SWT.NONE);
        typeNameLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
        typeNameLabel.setText(newComplex == false ? Messages.ConceptComposite_TypeName1 : Messages.ConceptComposite_TypeName2);
        // init types
        initInputTypes(types);

        typeNameCombo = new CCombo(parent, SWT.SINGLE | SWT.BORDER);
        typeNameCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 2, 1));
        typeNameCombo.setItems(getInitExistedTypes());
        typeNameCombo.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                updateComponents();
            };
        });

        typeNameCombo.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                String text = typeNameCombo.getText().trim();
                if (text.isEmpty()) {
                    setAll();
                }
                updateComponents();
            }
        });

        Group radioGroup = new Group(parent, SWT.SHADOW_NONE);
        radioGroup.setText(encloseTextField ? "" : "Sub-Elements Group");//$NON-NLS-1$//$NON-NLS-2$

        radioGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 2, 1));
        radioGroup.setLayout(new GridLayout(1, false));

        allButton = new Button(radioGroup, SWT.RADIO);
        allButton.setText("All");//$NON-NLS-1$
        allButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));

        sequenceButton = new Button(radioGroup, SWT.RADIO);
        sequenceButton.setText("Sequence");//$NON-NLS-1$
        sequenceButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));

        choiceButton = new Button(radioGroup, SWT.RADIO);
        choiceButton.setText("Choice");//$NON-NLS-1$
        choiceButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));

        allButton.setSelection(true);

        messageLabel = new Label(parent, SWT.NONE);
        messageLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
        messageLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
        Label label = new Label(parent, 0);
        label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
        label.setText(Messages.ConceptComposite_Label);

        superTypeNameCombo = new CCombo(parent, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
        superTypeNameCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 2, 1));
        fillSuperTypeNameText();

        superTypeNameCombo.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                updateComponents();
            };
        });
        btnAbstract = new Button(parent, SWT.CHECK);
        btnAbstract.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 2, 1));
        btnAbstract.setText(Messages.ConceptComposite_Abstract);
        container = parent;
        if (encloseTextField) {
            typeNameLabel.setParent(radioGroup);
            typeNameCombo.setParent(radioGroup);
            label.setParent(radioGroup);
            superTypeNameCombo.setParent(radioGroup);
            btnAbstract.setParent(radioGroup);
        }
        updateComponents();
    }

    private void updateComponents() {
        String typeName = typeNameCombo.getText().trim();
        XSDComplexTypeDefinition type = getTypeByName(typeName);
        if (type != null) {
            setAbstract(type.isAbstract());
            XSDParticleImpl partCnt = (XSDParticleImpl) type.getContent();
            updateElementGroup(type);
            setSuperName(getSuperTypeName(type));
            btnAbstract.setEnabled(false);
        } else {

            // for anonymous type
            if (typeName.length() == 0) {
                enableRadioButtons(false);
                superTypeNameCombo.select(0);
                setAbstract(false);
                btnAbstract.setEnabled(false);
                superTypeNameCombo.setEnabled(false);
            } else {
                superTypeNameCombo.setEnabled(true);
                // new custom type
                String superTypeName = superTypeNameCombo.getText().trim();
                XSDComplexTypeDefinition superType = getTypeByName(superTypeName);
                if (superType != null) {
                    updateElementGroup(superType);
                    btnAbstract.setEnabled(true);
                } else if (superTypeName.length() == 0) {
                    enableRadioButtons(true);
                    btnAbstract.setEnabled(true);
                }
            }
        }
    }

    private void initInputTypes(List<XSDComplexTypeDefinition> types) {
        typeMap = new LinkedHashMap<String, XSDComplexTypeDefinition>();
        for (XSDComplexTypeDefinition type : types) {
            if (type.getTargetNamespace() == null) {
                String typeName = type.getName();
                typeMap.put(typeName, type);
            }
        }
    }

    private XSDComplexTypeDefinition getTypeByName(String name) {
        if (typeMap != null && name != null) {
            for (String typeName : typeMap.keySet()) {
                if (typeName.equalsIgnoreCase(name)) {
                    return typeMap.get(typeName);
                }
            }
        }
        return null;
    }

    private void enableRadioButtons(boolean enable) {
        choiceButton.setEnabled(enable);
        allButton.setEnabled(enable);
        sequenceButton.setEnabled(enable);
    }

    private String[] getExistedTypes() {
        if (typeMap != null) {
            return typeMap.keySet().toArray(new String[0]);
        }
        return new String[0];
    }

    private String[] getInitExistedTypes() {
        String[] existedTypes = getExistedTypes();
        String[] initTypes = new String[existedTypes.length + 1];
        initTypes[0] = ""; //$NON-NLS-1$
        System.arraycopy(existedTypes, 0, initTypes, 1, existedTypes.length);
        return initTypes;
    }

    private void fillSuperTypeNameText() {
        superTypeNameCombo.removeAll();
        superTypeNameCombo.setItems(getInitExistedTypes());
    }

    private void updateElementGroup(XSDComplexTypeDefinition type) {
        if (type != null) {
            XSDParticleImpl partCnt = (XSDParticleImpl) type.getContent();
            if (partCnt != null) {
                XSDModelGroupImpl mdlGrp = (XSDModelGroupImpl) partCnt.getTerm();
                XSDCompositor typeComposite = mdlGrp.getCompositor();
                if (typeComposite.equals(XSDCompositor.SEQUENCE_LITERAL)) {
                    setSequence();
                } else if (typeComposite.equals(XSDCompositor.ALL_LITERAL)) {
                    setAll();
                } else if (typeComposite.equals(XSDCompositor.CHOICE_LITERAL)) {
                    setChoice();
                }
                enableRadioButtons(false);
            }
        }
    }

    private String getSuperTypeName(XSDComplexTypeDefinition type) {
        XSDTypeDefinition baseType = type.getBaseTypeDefinition();
        if (baseType == null) {
            return ""; //$NON-NLS-1$
        }
        String superTypeName = baseType.getName();
        if (superTypeName == null || "anyType".equalsIgnoreCase(superTypeName)) { //$NON-NLS-1$
            return ""; //$NON-NLS-1$
        }
        return superTypeName;
    }

    public String getSuperName() {
        return superTypeNameCombo.getText();
    }

    public void setSuperName(String superName) {
        superTypeNameCombo.setText(superName);
        superTypeNameCombo.setEnabled(false);
    }

    public String getText() {
        return typeNameCombo.getText();
    }

    public void setText(String text) {
        typeNameCombo.setText(text);
    }

    public void setFocus() {
        typeNameCombo.setFocus();
    }

    public void setMessage(String msg) {
        messageLabel.setText(msg);
    }

    public boolean isSequence() {
        return sequenceButton.getSelection();
    }

    public void setSequence() {
        choiceButton.setSelection(false);
        allButton.setSelection(false);
        sequenceButton.setSelection(true);
    }

    public boolean isChoice() {
        return choiceButton.getSelection();
    }

    public boolean isAbstract() {
        return btnAbstract.getSelection();
    }

    public void setAbstract(boolean isAbstract) {
        btnAbstract.setSelection(isAbstract);
    }

    public void setChoice() {
        choiceButton.setSelection(true);
        allButton.setSelection(false);
        sequenceButton.setSelection(false);
    }

    public boolean isAll() {
        return allButton.getSelection();
    }

    public void setAll() {
        choiceButton.setSelection(false);
        allButton.setSelection(true);
        sequenceButton.setSelection(false);
    }

    public Composite getComposite() {
        return container;
    }

    public CCombo getTypeCombo() {
        return typeNameCombo;
    }

    public void setSelectAllWidgets(boolean selected) {
        typeNameLabel.setEnabled(selected);
        typeNameCombo.setEnabled(selected);
        btnAbstract.setEnabled(selected);
        superTypeNameCombo.setEnabled(selected);
        enableRadioButtons(selected);
        if (selected) {
            updateComponents();
        }

    }
}
