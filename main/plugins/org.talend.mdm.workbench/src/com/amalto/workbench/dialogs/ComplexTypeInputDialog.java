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
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDCompositor;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.impl.XSDModelGroupImpl;
import org.eclipse.xsd.impl.XSDParticleImpl;

import com.amalto.workbench.actions.XSDChangeToComplexTypeAction;
import com.amalto.workbench.actions.XSDNewComplexTypeDefinition;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.utils.XSDUtil;
import com.amalto.workbench.widgets.ConceptComposite;

public class ComplexTypeInputDialog extends Dialog implements ModifyListener {

    private SelectionListener caller = null;

    private String typeName = "";//$NON-NLS-1$

    private String superTypeName = "";//$NON-NLS-1$

    private boolean isAbstract;
    private boolean isAll;
    private boolean isChoice;
    private boolean isSequence;

    private ConceptComposite conceptPanel = null;

    List<XSDComplexTypeDefinition> types;

    protected XSDSchema xsdSchema;

    private boolean isXSDModelGroup = false;

    private XSDCompositor typeComposite;

    private XSDCompositor superTypeComposite;

    private String title;

    /**
     * @param parentShell
     * @param isXSDModelGroup
     */
    public ComplexTypeInputDialog(SelectionListener caller, Shell parentShell, String title, XSDSchema schema,
            XSDTypeDefinition typeDefinition, List<XSDComplexTypeDefinition> types, boolean isXSDModelGroup) {
        super(parentShell);
        this.title = title;
        if(title == null || title.equals("")) //$NON-NLS-1$
            this.title = Messages._ComplexTypeProp;

        this.caller = caller;
        this.types = types;
        this.isXSDModelGroup = isXSDModelGroup;
        xsdSchema = schema;
        if (typeDefinition != null) {
            if (typeDefinition instanceof XSDComplexTypeDefinition) {
                if (typeDefinition.getName() != null)
                    this.typeName = typeDefinition.getName();
                superTypeName = typeDefinition.getBaseType().getName();
                XSDComplexTypeDefinition complexType = (XSDComplexTypeDefinition) typeDefinition;
                XSDParticleImpl partCnt = (XSDParticleImpl) complexType.getContent();
                XSDModelGroupImpl mdlGrp = (XSDModelGroupImpl) partCnt.getTerm();
                typeComposite = mdlGrp.getCompositor();

                XSDComplexTypeDefinition superComplexType = (XSDComplexTypeDefinition) (typeDefinition.getBaseType());
                partCnt = (XSDParticleImpl) superComplexType.getContent();
                mdlGrp = (XSDModelGroupImpl) partCnt.getTerm();
                superTypeComposite = mdlGrp.getCompositor();

                isAbstract = ((XSDComplexTypeDefinition) typeDefinition).isAbstract();
            }
        }
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        // Should not really be here but well,....
        final Composite composite = (Composite) super.createDialogArea(parent);

        createTopPart(composite);
        // encapsulate all widgets into the ConceptComposite which can be applied to several cases
        parent.getShell().setText(title);

        if (caller instanceof XSDNewComplexTypeDefinition) {
            conceptPanel = new ConceptComposite(composite, false, types, true);
        } else {
            conceptPanel = new ConceptComposite(composite, false, types, false);
            conceptPanel.setText(typeName);
            if (superTypeName != null && !"anyType".equalsIgnoreCase(superTypeName))//$NON-NLS-1$
                conceptPanel.setSuperName(superTypeName);
            if (typeComposite != null) {
                if (typeComposite.equals(XSDCompositor.ALL_LITERAL))
                    conceptPanel.setAll();
                else if (typeComposite.equals(XSDCompositor.CHOICE_LITERAL))
                    conceptPanel.setChoice();
                if (typeComposite.equals(XSDCompositor.SEQUENCE_LITERAL))
                    conceptPanel.setSequence();
            }
            conceptPanel.setAbstract(isAbstract);
        }
        conceptPanel.getTypeCombo().addModifyListener(this);
        return composite;
    }

    protected void createTopPart(Composite parent) {
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);
        getButton(IDialogConstants.OK_ID).addSelectionListener(this.caller);
    }

    @Override
    protected void okPressed() {
        typeName = conceptPanel.getText();
        superTypeName = conceptPanel.getSuperName();
        isAbstract = conceptPanel.isAbstract();
        isAll = conceptPanel.isAll();
        isChoice = conceptPanel.isChoice();
        isSequence = conceptPanel.isSequence();

        if(superTypeName.equals(typeName) && (!superTypeName.equals(""))){//$NON-NLS-1$
            MessageDialog.openError(null, Messages._Error, Messages.typeCannotExtendsItsself);
            setReturnCode(CANCEL);
            return;
        }

        setReturnCode(OK);
//        super.okPressed();
        // no close let Action Handler handle it
    }

    public void modifyText(ModifyEvent e) {
        getButton(IDialogConstants.OK_ID).setEnabled(true);
        conceptPanel.setMessage("");//$NON-NLS-1$

        String type = conceptPanel.getText();

        if (Pattern.compile("^\\s+\\w+\\s*").matcher(type).matches()//$NON-NLS-1$
                || type.trim().replaceAll("\\s", "").length() != type.trim().length()) {//$NON-NLS-1$//$NON-NLS-2$
            conceptPanel.setMessage(Messages._NameWithEmptyCharacters);
            getButton(IDialogConstants.OK_ID).setEnabled(false);
            return;
        }
        type = type.trim();
        if (!XSDUtil.isValidatedXSDName(type)) {
            conceptPanel.setMessage(Messages.InvalidName_Message);
            getButton(IDialogConstants.OK_ID).setEnabled(false);
            return;
        }

        for (XSDTypeDefinition specType : xsdSchema.getTypeDefinitions()) {
            String typeToCompare = specType.getName();
            int delimiter = type.indexOf(" : ");//$NON-NLS-1$
            if (delimiter != -1) {
                type = type.substring(0, delimiter);
            }
            if (typeToCompare.equals(type)) {
                if (caller instanceof XSDNewComplexTypeDefinition) {
                    conceptPanel.setMessage(Messages._SameTypeNameExists);
                    getButton(IDialogConstants.OK_ID).setEnabled(false);
                } else if (caller instanceof XSDChangeToComplexTypeAction && specType instanceof XSDSimpleTypeDefinition) {
                    conceptPanel.setMessage(Messages._SameTypeNameExists);
                    getButton(IDialogConstants.OK_ID).setEnabled(false);
                }
                break;
            }
        }
    }

    public boolean isSequence() {
        return isSequence;
    }

    public boolean isChoice() {
        return isChoice;
    }

    public boolean isAll() {
        return isAll;
    }

    public boolean isAbstract() {
        return isAbstract;
    }

    public String getSuperName() {
        return superTypeName;
    }

    public String getTypeName() {
        return typeName;
    }

}
