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
package com.amalto.workbench.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDCompositor;
import org.eclipse.xsd.XSDDerivationMethod;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTerm;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.util.XSDSchemaBuildingTools;
import org.w3c.dom.Element;

import com.amalto.workbench.dialogs.ComplexTypeInputDialogR;
import com.amalto.workbench.editors.DataModelMainPage;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.utils.XSDAnnotationsStructure;

public class XSDAddComplexTypeElementAction extends UndoAction {

    private static Log log = LogFactory.getLog(XSDAddComplexTypeElementAction.class);

    private final String defaultTypeName = "string"; //$NON-NLS-1$

    private XSDParticle selParticle = null;

    private XSDModelGroup modelGroup = null;

    private ComplexTypeInputDialogR dialogR;

    private String elementName;

    private int minOccurs;

    private int maxOccurs;

    private String superTypeName;

    private String typeName;

    private boolean isAbstract;

    private boolean isChoice;

    private boolean isAll;

    public XSDAddComplexTypeElementAction(DataModelMainPage page) {
        super(page);
        setText(Messages._AddCType);
    }

    @Override
    public IStatus doAction() {
        if (!init()) {
            return Status.CANCEL_STATUS;
        }

        if (openDialog() == Dialog.CANCEL) {
            return Status.CANCEL_STATUS;
        }

        updateElementFields();

        if (!changeElementTypeToSequence()) {
            return Status.CANCEL_STATUS;
        }

        if (!createComplexTypeParticle()) {
            return Status.CANCEL_STATUS;
        }

        return Status.OK_STATUS;
    }

    private boolean init() {
        IStructuredSelection selection = (IStructuredSelection) page.getTreeViewer().getSelection();

        if (selection.getFirstElement() instanceof XSDParticle) {
            selParticle = (XSDParticle) selection.getFirstElement();

            if (!(selParticle.getContainer() instanceof XSDModelGroup)) {
                return false;
            }

            modelGroup = (XSDModelGroup) selParticle.getContainer();
        } else {
            if (selection.getFirstElement() instanceof XSDComplexTypeDefinition) {
                XSDComplexTypeDefinition ctd = (XSDComplexTypeDefinition) selection.getFirstElement();
                if (!(ctd.getContent() instanceof XSDParticle)) {
                    return false;
                }
                if (!(((XSDParticle) ctd.getContent()).getTerm() instanceof XSDModelGroup)) {
                    return false;
                }
                ;
                modelGroup = (XSDModelGroup) ((XSDParticle) ctd.getContent()).getTerm();
            } else if (selection.getFirstElement() instanceof XSDParticle) {
                modelGroup = (XSDModelGroup) ((XSDParticle) selection.getFirstElement()).getTerm();
            } else if (selection.getFirstElement() instanceof XSDModelGroup) {
                modelGroup = (XSDModelGroup) selection.getFirstElement();
            } else {
                log.info(Messages.bind(Messages._UnkownSection, selection.getFirstElement().getClass().getName(), selection
                        .getFirstElement().toString()));
                return false;
            }
        }

        return true;
    }

    private int openDialog() {
        XSDSimpleTypeDefinition simpleTypeDefinition = schema.resolveSimpleTypeDefinition(schema.getSchemaForSchemaNamespace(),
                defaultTypeName);
        List<XSDComplexTypeDefinition> types = Util.getComplexTypes(schema);

        dialogR = new ComplexTypeInputDialogR(page.getSite().getShell(), Messages._AddCType, modelGroup, schema, types,
                simpleTypeDefinition, false, false);

        dialogR.setBlockOnOpen(true);
        int ret = dialogR.open();

        return ret;
    }

    private void updateElementFields() {
        elementName = dialogR.getElementName();
        minOccurs = dialogR.getMinOccurs();
        maxOccurs = dialogR.getMaxOccurs();

        superTypeName = dialogR.getSuperName();
        isAbstract = dialogR.isAbstract();
        typeName = dialogR.getTypeName();
        isChoice = dialogR.isChoice();
        isAll = dialogR.isAll();
    }

    private boolean changeElementTypeToSequence() {
        if (selParticle != null) {
            XSDElementDeclaration elem = (XSDElementDeclaration) selParticle.getContent();
            if (Util.changeElementTypeToSequence(elem, maxOccurs) == Status.CANCEL_STATUS) {
                return false;
            }
        }

        return true;
    }

    private boolean createComplexTypeParticle() {
        try {
            XSDParticle particle = createParticle();
            boolean flag = transformToComplexType(particle);
            if (!flag) {
                modelGroup.getContents().remove(particle);
                return false;
            }

            page.refresh();
            page.getTreeViewer().setSelection(new StructuredSelection(particle), true);
            page.markDirty();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            MessageDialog.openError(page.getSite().getShell(), Messages._Error,
                    Messages.bind(Messages._CreateCTypeError, e.getLocalizedMessage()));
            return false;
        }

        return true;
    }

    private XSDParticle createParticle() {

        XSDFactory factory = XSDSchemaBuildingTools.getXSDFactory();

        XSDElementDeclaration resultElementDeclaration = factory.createXSDElementDeclaration();
        resultElementDeclaration.setName(elementName);

        resultElementDeclaration.setTypeDefinition(schema.resolveSimpleTypeDefinition(schema.getSchemaForSchemaNamespace(),
                defaultTypeName));

        XSDParticle resultParticle = factory.createXSDParticle();
        resultParticle.setContent(resultElementDeclaration);
        resultParticle.setMinOccurs(this.minOccurs);
        XSDModelGroup group = modelGroup;
        if (maxOccurs > -1) {
            resultParticle.setMaxOccurs(this.maxOccurs);
            group.getContents().add(group.getContents().size(), resultParticle);
            group.updateElement();
        } else {
            resultParticle.setMaxOccurs(this.maxOccurs);
            group.getContents().add(group.getContents().size(), resultParticle);
            group.updateElement();
            if (resultParticle.getElement().getAttributeNode("maxOccurs") != null) { //$NON-NLS-1$
                resultParticle.getElement().getAttributeNode("maxOccurs").setNodeValue("unbounded");//$NON-NLS-1$//$NON-NLS-2$
            } else {
                resultParticle.getElement().setAttribute("maxOccurs", "unbounded");//$NON-NLS-1$//$NON-NLS-2$
            }
        }
        Util.changeElementTypeToSequence(resultElementDeclaration, maxOccurs);
        if (dialogR.isInherit()) {
            XSDTerm totm = resultParticle.getTerm();
            XSDElementDeclaration concept = null;

            Object parent = Util.getParent(resultParticle);
            if (parent instanceof XSDElementDeclaration) {
                concept = (XSDElementDeclaration) parent;
            } else {
                concept = (XSDElementDeclaration) resultParticle.getContent();
            }

            XSDAnnotation fromannotation = null;
            if (concept != null) {
                fromannotation = concept.getAnnotation();
            }
            if (fromannotation != null) {
                XSDAnnotationsStructure struc = new XSDAnnotationsStructure(totm);
                if (((XSDElementDeclaration) totm).getType() != null) {
                    addAnnotion(struc, fromannotation);
                }
            }

        }

        return resultParticle;
    }

    public void addAnnotion(XSDAnnotationsStructure struc, XSDAnnotation xsdannotationparent) {
        Map<String, List<String>> infor = new HashMap<String, List<String>>();
        infor = cloneXSDAnnotation(xsdannotationparent);
        Set<String> keys = infor.keySet();
        for (int i = 0; i < infor.size(); i++) {
            List<String> lists = infor.get(keys.toArray()[i]);
            try {
                struc.setAccessRole(lists, false, (IStructuredContentProvider) page.getTreeViewer().getContentProvider(),
                        (String) keys.toArray()[i]);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    public Map<String, List<String>> cloneXSDAnnotation(XSDAnnotation oldAnn) {
        Map<String, List<String>> infor = new HashMap<String, List<String>>();
        try {
            if (oldAnn != null) {
                for (int i = 0; i < oldAnn.getApplicationInformation().size(); i++) {
                    Element oldElem = oldAnn.getApplicationInformation().get(i);
                    String type = oldElem.getAttributes().getNamedItem("source").getNodeValue(); //$NON-NLS-1$
                    // X_Write,X_Hide
                    if (type.equals("X_Write") || type.equals("X_Hide")) { //$NON-NLS-1$ //$NON-NLS-2$
                        if (!infor.containsKey(type)) {
                            List<String> typeList = new ArrayList<String>();
                            typeList.add(oldElem.getFirstChild().getNodeValue());
                            infor.put(type, typeList);
                        } else {
                            (infor.get(type)).add(oldElem.getFirstChild().getNodeValue());
                        }
                    }
                }
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            MessageDialog.openError(this.page.getSite().getShell(), Messages._Error,
                    Messages.bind(Messages._PasteError, e.getLocalizedMessage()));
        }
        return infor;
    }

    private boolean transformToComplexType(XSDParticle particle) {
        XSDElementDeclaration decl = (XSDElementDeclaration) particle.getContent();

        List<XSDComplexTypeDefinition> types = Util.getComplexTypes(schema);

        XSDFactory factory = XSDSchemaBuildingTools.getXSDFactory();
        boolean anonymous = (typeName == null) || ("".equals(typeName));//$NON-NLS-1$
        boolean alreadyExists = false;

        XSDComplexTypeDefinition complexType = null;
        // the sub element created if needed
        XSDParticle subParticle = null;
        XSDParticle groupParticle = null;
        XSDElementDeclaration subElement = null;

        XSDElementDeclaration parent = null;
        Object pObject = Util.getParent(decl);
        if (pObject instanceof XSDElementDeclaration) {
            parent = (XSDElementDeclaration) pObject;
        }

        if (!anonymous) {
            if (typeName.lastIndexOf(" : ") != -1) {//$NON-NLS-1$
                typeName = typeName.substring(0, typeName.lastIndexOf(" : "));//$NON-NLS-1$
            }
            for (XSDComplexTypeDefinition td : types) {
                if ((td.getName().equals(typeName))) {
                    alreadyExists = true;
                    complexType = td;
                    break;
                }
            }

        } else {
            if (decl.getTypeDefinition() instanceof XSDSimpleTypeDefinition) {
                alreadyExists = false;
            }
        }

        if (alreadyExists) {
            XSDParticle partCnt = (XSDParticle) complexType.getContentType();
            partCnt.unsetMaxOccurs();
            partCnt.unsetMinOccurs();
            XSDTypeDefinition superType = null;
            for (XSDTypeDefinition type : types) {
                if (type.getName().equals(superTypeName)) {
                    superType = type;
                    break;
                }
            }

            if (superType != null) {
                XSDModelGroup mdlGrp = (XSDModelGroup) partCnt.getTerm();
                boolean status = updateCompositorType(superType, mdlGrp);
                if (!status) {
                    return false;
                }

                complexType.setDerivationMethod(XSDDerivationMethod.EXTENSION_LITERAL);
                complexType.setBaseTypeDefinition(superType);
            }
            if (isAbstract) {
                complexType.setAbstract(isAbstract);
            } else {
                complexType.unsetAbstract();
            }

            if (parent != null) {
                parent.updateElement();
            }
            if (complexType != null) {
                complexType.updateElement();
            }
        } else {// Create if does not exist

            // add an element declaration
            subElement = factory.createXSDElementDeclaration();
            subElement.setName("subelement");//$NON-NLS-1$
            subElement
                    .setTypeDefinition(schema.resolveSimpleTypeDefinition(schema.getSchemaForSchemaNamespace(), defaultTypeName));

            subParticle = factory.createXSDParticle();
            subParticle.unsetMaxOccurs();
            subParticle.unsetMinOccurs();
            subParticle.setContent(subElement);
            subParticle.updateElement();

            // create group
            XSDModelGroup group = factory.createXSDModelGroup();
            if (isChoice) {
                group.setCompositor(XSDCompositor.CHOICE_LITERAL);
            } else if (isAll) {
                group.setCompositor(XSDCompositor.ALL_LITERAL);
            } else {
                group.setCompositor(XSDCompositor.SEQUENCE_LITERAL);
            }
            group.getContents().add(0, subParticle);
            group.updateElement();

            // create the complex type
            complexType = factory.createXSDComplexTypeDefinition();
            // complexType.setDerivationMethod(XSDDerivationMethod.EXTENSION_LITERAL);
            if (!anonymous) {
                // if (true) {
                XSDTypeDefinition superType = null;
                for (XSDTypeDefinition type : types) {
                    if (type.getName().equals(superTypeName)) {
                        superType = type;
                        break;
                    }
                }
                complexType.setName(typeName);
                if (superType != null) {
                    boolean status = updateCompositorType(superType, group);
                    if (!status) {
                        return false;
                    }

                    complexType.setDerivationMethod(XSDDerivationMethod.EXTENSION_LITERAL);
                    complexType.setBaseTypeDefinition(superType);
                }
                if (isAbstract) {
                    complexType.setAbstract(isAbstract);
                } else {
                    complexType.unsetAbstract();
                }
                schema.getContents().add(complexType);
            }
            complexType.updateElement();

            // add the group
            groupParticle = factory.createXSDParticle();
            groupParticle.unsetMaxOccurs();
            groupParticle.unsetMinOccurs();
            groupParticle.setContent(group);
            groupParticle.updateElement();

            complexType.setContent(groupParticle);
            complexType.updateElement();
        }// end if NOT already exusts

        // set complex type to concept
        if (anonymous) {
            decl.setAnonymousTypeDefinition(complexType);
        } else {
            decl.setTypeDefinition(complexType);
        }

        decl.updateElement();
        schema.update();

        return true;
    }

    private boolean updateCompositorType(XSDTypeDefinition superType, XSDModelGroup currentGroup) {
        XSDParticle superTypeParticle = superType.getComplexType();
        XSDTerm term = superTypeParticle.getTerm();
        if (term instanceof XSDModelGroup) {
            XSDModelGroup group = (XSDModelGroup) term;
            if (group.getCompositor() == XSDCompositor.ALL_LITERAL || currentGroup.getCompositor() == XSDCompositor.ALL_LITERAL) {
                if (MessageDialog.openConfirm(null, Messages._ChangeToSequenceType, Messages._ComplexTypeToSequence)) {
                    group.setCompositor(XSDCompositor.SEQUENCE_LITERAL);
                    superTypeParticle.updateElement();
                    currentGroup.setCompositor(XSDCompositor.SEQUENCE_LITERAL);
                    currentGroup.updateElement();
                    return true;
                }
                return false;

            }
        }
        return true;
    }
}
