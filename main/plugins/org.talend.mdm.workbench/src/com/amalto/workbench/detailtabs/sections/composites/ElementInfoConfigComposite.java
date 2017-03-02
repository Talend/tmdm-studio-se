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
package com.amalto.workbench.detailtabs.sections.composites;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDParticle;

import com.amalto.workbench.detailtabs.sections.BasePropertySection;
import com.amalto.workbench.detailtabs.sections.handlers.RefreshPropertySheetTitleHandler;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.utils.IConstants;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.utils.XSDAnnotationsStructure;

public class ElementInfoConfigComposite extends Composite {

    private Combo comboReference;

    private Text txtName;

    private XSDParticle curXSDParticle;

    private Group occurrenceGroup;

    private Spinner spinMax;

    private Spinner spinMin;

    private BasePropertySection section;

    private Button autoExpandBtn;

    public ElementInfoConfigComposite(Composite parent, int style, BasePropertySection section, XSDParticle curXSDParticle) {
        this(parent, style);
        this.section = section;
        this.curXSDParticle = curXSDParticle;

    }

    public ElementInfoConfigComposite(Composite parent, int style) {
        super(parent, style);
        createControls();

    }

    public void setXSDParticle(XSDParticle curXSDParticle) {
        this.curXSDParticle = curXSDParticle;

        initUIContents();

        refresh();
    }

    public String getElementName() {
        return txtName.getText().trim();
    }

    public String getElementReference() {
        return comboReference.getText().trim();
    }

    public boolean hasElementReference() {
        return !("".equals(getElementReference()));//$NON-NLS-1$
    }

    public int getMinCardinality() {
        return spinMin.getSelection();
    }

    public int getMaxCardinality() {
        return spinMax.getSelection();
    }

    public XSDParticle getElement() {
        return curXSDParticle;
    }

    public boolean isAutoExpand() {
        if (autoExpandBtn != null) {
            return autoExpandBtn.getSelection();
        }
        return false;
    }

    public void refresh() {

        refreshCardinalityArea();

        refreshNameArea();

        refreshPK();
    }

    private void refreshCardinalityArea() {
        if (occurrenceGroup == null) {
            return;
        }
        if (autoExpandBtn != null) {
            boolean isComplex = !Util.isSimpleTypedParticle(curXSDParticle);
            autoExpandBtn.setVisible(isComplex);
        }
        // }
        // if (!hasElementReference() &&
        // Util.isSimpleTypedParticle(curXSDParticle)) {
        // spinMin.setSelection(1);
        // spinMax.setSelection(1);
        // }

        // enableOccurrenceGroup(!Util.isSimpleTypedParticle(curXSDParticle) ||
        // hasElementReference());
    }

    private void enableOccurrenceGroup(boolean isEnabled) {
        spinMin.setEnabled(isEnabled);
        spinMax.setEnabled(isEnabled);
    }

    private void refreshNameArea() {

        if (hasElementReference()) {
            txtName.setText("");//$NON-NLS-1$
        }

        txtName.setEditable(!hasElementReference());
    }

    private void createControls() {
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        setLayout(gridLayout);

        autoExpandBtn = new Button(this, SWT.CHECK);
        autoExpandBtn.setText(Messages.ElementInfoConfigComposite_AutoExpand);
        autoExpandBtn.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));

        final Label lblName = new Label(this, SWT.NONE);
        lblName.setText(Messages.ElementInfoConfigComposite_Name);

        txtName = new Text(this, SWT.BORDER);
        final GridData gd_txtName = new GridData(SWT.FILL, SWT.CENTER, true, false);
        txtName.setLayoutData(gd_txtName);

        final Label lblReference = new Label(this, SWT.NONE);
        lblReference.setText("Reference");//$NON-NLS-1$

        comboReference = new Combo(this, SWT.READ_ONLY);
        final GridData gd_comboReference = new GridData(SWT.FILL, SWT.CENTER, true, false);
        comboReference.setLayoutData(gd_comboReference);

        occurrenceGroup = new Group(this, SWT.NONE);
        final GridData gd_occurrenceGroup = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
        occurrenceGroup.setLayoutData(gd_occurrenceGroup);
        occurrenceGroup.setText(Messages.ElementInfoConfigComposite_Occurrence);
        final GridLayout gridLayout_2 = new GridLayout();
        gridLayout_2.numColumns = 3;
        occurrenceGroup.setLayout(gridLayout_2);

        final Label lblMinOcur = new Label(occurrenceGroup, SWT.NONE);
        final GridData gd_lblMinOcur = new GridData(SWT.CENTER, SWT.CENTER, false, false);
        lblMinOcur.setLayoutData(gd_lblMinOcur);
        lblMinOcur.setText("Min");//$NON-NLS-1$

        new Label(occurrenceGroup, SWT.NONE);

        final Label lblMaxOcur = new Label(occurrenceGroup, SWT.NONE);
        final GridData gd_lblMaxOcur = new GridData(SWT.CENTER, SWT.CENTER, false, false);
        lblMaxOcur.setLayoutData(gd_lblMaxOcur);
        lblMaxOcur.setText("Max");//$NON-NLS-1$

        spinMin = new Spinner(occurrenceGroup, SWT.BORDER);
        spinMin.setSelection(1);
        spinMin.setMinimum(0);
        spinMin.setMaximum(Integer.MAX_VALUE);

        final Label label = new Label(occurrenceGroup, SWT.NONE);
        label.setText("----->");//$NON-NLS-1$

        spinMax = new Spinner(occurrenceGroup, SWT.BORDER);
        spinMax.setLayoutData(new GridData());
        spinMax.setMaximum(Integer.MAX_VALUE);
        spinMax.setMinimum(-1);
        spinMax.setSelection(1);

        initUIContents();

        initUIListeners();

        refresh();
    }

    private void refreshPK() {
        if (curXSDParticle != null) {
            this.section.getTreeObject();
            XSDElementDeclaration decl = (XSDElementDeclaration) curXSDParticle.getContent();
            List<Object> keyInfo = Util.getKeyInfo(decl);
            boolean isPK = keyInfo != null && keyInfo.size() > 0;
            comboReference.setEnabled(!isPK);
            spinMin.setEnabled(!isPK);
            spinMax.setEnabled(!isPK);
        }
    }

    private void initUIContents() {

        String name = Util.getParticleName(curXSDParticle);
        removeTextListener();
        txtName.setText(name);
        if (name != null) {
            int length = name.length();
            if (length >= caretOffset) {
                txtName.setSelection(caretOffset, caretOffset);
            } else {
                txtName.setSelection(length, length);
            }
        }
        addTextListener();
        comboReference.setItems(getAllReferences());
        comboReference.setText(Util.getParticleReferenceName(curXSDParticle));

        if (curXSDParticle != null) {
            spinMin.setSelection(curXSDParticle.getMinOccurs());
            spinMax.setSelection(curXSDParticle.getMaxOccurs());
            if (autoExpandBtn != null) {
                XSDAnnotationsStructure struct = new XSDAnnotationsStructure(curXSDParticle);
                String auto = struct.getAutoExpand();
                if (auto != null) {
                    autoExpandBtn.setSelection(Boolean.valueOf(auto));
                } else {
                    autoExpandBtn.setSelection(false);
                }
            }
        }
    }

    private void initUIListeners() {

        initUIListenerForComboReference();
        initTextListener();
        initSpinListener();
        initButtonListener();
    }

    private void initButtonListener() {
        if (autoExpandBtn != null) {
            autoExpandBtn.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    if (section != null) {
                        section.autoCommit();
                    }
                }
            });
        }
    }

    private void initSpinListener() {
        spinMin.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                if (section != null && curXSDParticle.getMinOccurs() != spinMin.getSelection()) {
                    section.autoCommit();

                    refreshPropertySheetTitle();
                }
            }
        });
        spinMax.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                if (section != null && curXSDParticle.getMaxOccurs() != spinMax.getSelection()) {
                    section.autoCommit();

                    refreshPropertySheetTitle();
                }
            }
        });
    }

    private int caretOffset;

    private ModifyListener textListener;

    private void initTextListener() {

        textListener = new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                caretOffset = txtName.getCaretPosition();
                if (section != null && !txtName.getText().equals(Util.getParticleName(curXSDParticle))) {
                    section.autoCommit();

                    refreshPropertySheetTitle();
                }
            }
        };
    }

    private void addTextListener() {
        if (textListener != null) {
            txtName.addModifyListener(textListener);
        }
    }

    public void removeTextListener() {
        if (textListener != null) {
            txtName.removeModifyListener(textListener);
        }
    }

    private void initUIListenerForComboReference() {

        comboReference.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseDown(MouseEvent e) {
                comboReference.setItems(getAllReferences());
            }

        });

        comboReference.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {

                refresh();
                if (section != null && !Util.getParticleReferenceName(curXSDParticle).equals(comboReference.getText())) {
                    section.autoCommit();

                    refreshPropertySheetTitle();
                }
            }

        });
    }

    private String[] getAllReferences() {

        ArrayList<String> elementDeclarations = new ArrayList<String>();
        elementDeclarations.add("");//$NON-NLS-1$

        if (curXSDParticle == null) {
            return elementDeclarations.toArray(new String[0]);
        }
        if (curXSDParticle.getSchema() == null) {
            return new String[0];
        }

        XSDConcreteComponent entity = getParentElement(curXSDParticle);
        for (XSDElementDeclaration eachXSDEleDeclaration : curXSDParticle.getSchema().getElementDeclarations()) {
            if (eachXSDEleDeclaration.getTargetNamespace() != null
                    && eachXSDEleDeclaration.getTargetNamespace().equals(IConstants.DEFAULT_NAME_SPACE)) {
                continue;
            }

            if (!eachXSDEleDeclaration.equals(entity)) {
                elementDeclarations
                        .add(eachXSDEleDeclaration.getQName()
                                + (eachXSDEleDeclaration.getTargetNamespace() != null ? " : " + eachXSDEleDeclaration.getTargetNamespace()//$NON-NLS-1$
                                        : ""));//$NON-NLS-1$
            }
        }

        return elementDeclarations.toArray(new String[0]);
    }

    private XSDConcreteComponent getParentElement(XSDParticle xSDParticle) {
        XSDConcreteComponent rootContainer = xSDParticle.getRootContainer();
        XSDConcreteComponent container = xSDParticle.getContainer();
        while (!container.getContainer().equals(rootContainer)) {
            container = container.getContainer();
        }

        return container;
    }

    private void refreshPropertySheetTitle() {
        RefreshPropertySheetTitleHandler.refreshPropertySheetTitle(section, curXSDParticle);
    }
}
