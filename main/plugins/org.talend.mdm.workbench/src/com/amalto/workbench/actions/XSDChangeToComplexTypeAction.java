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
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDCompositor;
import org.eclipse.xsd.XSDDerivationMethod;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDIdentityConstraintCategory;
import org.eclipse.xsd.XSDIdentityConstraintDefinition;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTerm;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.XSDXPathDefinition;
import org.eclipse.xsd.XSDXPathVariety;
import org.eclipse.xsd.impl.XSDParticleImpl;
import org.eclipse.xsd.util.XSDSchemaBuildingTools;

import com.amalto.workbench.dialogs.ComplexTypeInputDialog;
import com.amalto.workbench.editors.DataModelMainPage;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.utils.Util;

public class XSDChangeToComplexTypeAction extends UndoAction implements SelectionListener {

    private Log log = LogFactory.getLog(XSDChangeToComplexTypeAction.class);

    protected boolean isConcept = false;

    protected XSDElementDeclaration decl = null;

    protected ComplexTypeInputDialog dialog = null;

    XSDElementDeclaration declNew = null;

    String typeName = null;

    String superTypeName;

    boolean isAbstract;

    boolean isChoice = false;

    boolean isAll = false;

    boolean showDlg = true;

    private boolean isXSDModelGroup = false;

    public XSDChangeToComplexTypeAction(DataModelMainPage page, boolean isXSDModelGroup) {
        super(page);
        this.isXSDModelGroup = isXSDModelGroup;
        setImageDescriptor(ImageCache.getImage(EImage.COMPLEX_ALL.getPath()));
        if (isXSDModelGroup) {
            setText(Messages.XSDChangeToCXX_Text1);
        } else {
            setText(Messages.XSDChangeToCXX_Text2);
        }
        setToolTipText(Messages.XSDChangeToCXX_ActionTip);
        setDescription(getToolTipText());
    }

    public XSDChangeToComplexTypeAction(DataModelMainPage page, XSDElementDeclaration dec, String name, boolean choice,
            boolean all, boolean isAbstract, String superTypeName) {

        this(page, false);
        this.superTypeName = superTypeName;
        this.isAbstract = isAbstract;
        declNew = dec;
        showDlg = false;
        typeName = name;
        isChoice = choice;
        isAll = all;
    }

    @Override
    public IStatus doAction() {

        try {
            IStructuredSelection selection = (IStructuredSelection) page.getTreeViewer().getSelection();
            isConcept = false;
            TreePath tPath = null;
            if (((TreeSelection) selection).getPaths().length > 0) {
                tPath = ((TreeSelection) selection).getPaths()[0];
            }
            // fliu
            // add declNew to support convert action invoked from new concept/new element menu, in this case
            // declNew is the new created one not the selected one in tree vew
            if (declNew != null) {
                decl = declNew;
                checkConcept();
            } else if (selection.getFirstElement() instanceof XSDModelGroup) {
                for (int i = 0; i < tPath.getSegmentCount(); i++) {
                    if (tPath.getSegment(i) instanceof XSDElementDeclaration) {
                        decl = (XSDElementDeclaration) tPath.getSegment(i);
                    } else if (tPath.getSegment(i) instanceof XSDParticle) {
                        decl = (XSDElementDeclaration) ((XSDParticle) tPath.getSegment(i)).getTerm();
                    }
                }
                checkConcept();
            } else if (selection.getFirstElement() instanceof XSDElementDeclaration) {
                decl = (XSDElementDeclaration) selection.getFirstElement();
                // check if concept or "just" element
                checkConcept();

            } else if (selection.getFirstElement() instanceof XSDParticle) {
                // if it's a particle,it should change the element of its
                // content
                decl = (XSDElementDeclaration) ((XSDParticle) selection.getFirstElement()).getContent();
            } else {
                // if(selection.getFirstElement() instanceof XSDParticle )
                if (selection.getFirstElement() != null) {
                    // a sub element
                    decl = (XSDElementDeclaration) ((XSDParticle) selection.getFirstElement()).getTerm();
                }
            }

            // /save current Type Definition
            // XSDTypeDefinition current = decl.getTypeDefinition();
            List<XSDComplexTypeDefinition> types = Util.getComplexTypes(decl.getSchema());
            if (showDlg) {
                if (decl.getTypeDefinition() instanceof XSDComplexTypeDefinition) {
                    boolean confirm = MessageDialog.openConfirm(page.getSite().getShell(), Messages.Warning,
                            Messages.XSDChangeToCXX_ChangeToAnotherTypeWarning);
                    if (!confirm) {
                        return Status.CANCEL_STATUS;
                    }
                }

                if (tPath != null) {
                    for (int i = 0; i < tPath.getSegmentCount(); i++) {
                        if (tPath.getSegment(i) instanceof XSDElementDeclaration) {
                            XSDTypeDefinition type = (((XSDElementDeclaration) tPath.getSegment(i)).getTypeDefinition());
                            if (!type.equals(decl.getTypeDefinition())) {
                                types.remove(type);
                            }
                        }
                        if (tPath.getSegment(i) instanceof XSDParticle) {
                            XSDTypeDefinition type = ((XSDElementDeclaration) (((XSDParticle) tPath.getSegment(i)).getTerm()))
                                    .getTypeDefinition();
                            if (!type.equals(decl.getTypeDefinition())) {
                                types.remove(type);
                            }
                        }
                    }
                }
                dialog = new ComplexTypeInputDialog(this, page.getSite().getShell(), "", schema, decl.getTypeDefinition(), types,//$NON-NLS-1$
                        isXSDModelGroup);

                dialog.setBlockOnOpen(true);
                int ret = dialog.open();
                if (ret == Dialog.CANCEL) {
                    return Status.CANCEL_STATUS;
                }
            }

            if (!showDlg && !validateType()) {
                return Status.CANCEL_STATUS;
            }

            XSDFactory factory = XSDSchemaBuildingTools.getXSDFactory();
            boolean anonymous = (typeName == null) || ("".equals(typeName));//$NON-NLS-1$
            boolean alreadyExists = false;

            XSDComplexTypeDefinition complexType = null;
            // the sub element created if needed
            XSDParticle subParticle = null;
            XSDParticle groupParticle = null;
            XSDElementDeclaration subElement = null;

            // check if already exist
            // add by ymli; fix the bug:0012278;
            XSDElementDeclaration parent = null;
            Object pObject = Util.getParent(decl);
            if (pObject instanceof XSDElementDeclaration) {
                parent = (XSDElementDeclaration) pObject;
            }

            if (!anonymous) {
                List<XSDComplexTypeDefinition> list = Util.getComplexTypes(schema);
                if (typeName.lastIndexOf(" : ") != -1) {//$NON-NLS-1$
                    typeName = typeName.substring(0, typeName.lastIndexOf(" : "));//$NON-NLS-1$
                }
                for (XSDComplexTypeDefinition td : list) {
                    if ((td.getName().equals(typeName))) {
                        alreadyExists = true;
                        complexType = td;
                        break;
                    }
                }

            } else {
                XSDComplexTypeDefinition declComplexType = null;
                if (parent != null && decl.getTypeDefinition() instanceof XSDComplexTypeDefinition) {
                    declComplexType = (XSDComplexTypeDefinition) decl.getTypeDefinition();
                }
                if (declComplexType != null && declComplexType.getSchema() != null && declComplexType.getName() == null) {
                    alreadyExists = true;
                }
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
                        return Status.CANCEL_STATUS;
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
                if (declNew != null) {
                    // crate a new entity
                    if (declNew.getName() != null) {
                        subElement.setName(declNew.getName() + "Id");//$NON-NLS-1$
                    }
                } else {
                    // create a complex element
                    subElement.setName("subelement");//$NON-NLS-1$
                }
                subElement.setTypeDefinition(schema.resolveSimpleTypeDefinition(schema.getSchemaForSchemaNamespace(), "string"));//$NON-NLS-1$

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
                if (!anonymous) {
                    XSDTypeDefinition superType = null;
                    for (XSDTypeDefinition type : types) {
                        if (type.getName().equals(superTypeName)) {
                            superType = type;
                            break;
                        }
                    }
                    complexType.setName(typeName);
                    if (superType != null) {
                        complexType.setDerivationMethod(XSDDerivationMethod.EXTENSION_LITERAL);
                        complexType.setBaseTypeDefinition(superType);
                        updateCompositorType(superType, group);
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

            if (isConcept) {
                buildUniqueKey(factory, decl, complexType, anonymous, alreadyExists);
            }// if isConcept

            decl.updateElement();
            schema.update();
            page.refresh();

            declNew = null;
            page.markDirty();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            MessageDialog.openError(page.getSite().getShell(), Messages._Error,
                    Messages.bind(Messages.XSDChangeToCXX_ErrorMsg1, e.getLocalizedMessage()));
            return Status.CANCEL_STATUS;
        }

        return Status.OK_STATUS;
    }

    private void buildUniqueKey(XSDFactory factory, XSDElementDeclaration declaration, XSDComplexTypeDefinition complexType,
            boolean anonymous, boolean alreadyExists) {
        if (factory == null || declaration == null || complexType == null) {
            return;
        }

        // remove exisiting unique key(s)
        removeExistUniqueKey(declaration);

        // add new unique key with first element declaration name
        if (anonymous || !alreadyExists) {
            createUniqueKey(factory, declaration, complexType);
        }
    }

    private void removeExistUniqueKey(XSDElementDeclaration declaration) {
        List<XSDIdentityConstraintDefinition> keys = new ArrayList<XSDIdentityConstraintDefinition>();
        EList<XSDIdentityConstraintDefinition> list = declaration.getIdentityConstraintDefinitions();
        for (XSDIdentityConstraintDefinition icd : list) {
            if (icd.getIdentityConstraintCategory().equals(XSDIdentityConstraintCategory.UNIQUE_LITERAL)) {
                keys.add(icd);
            }
        }
        declaration.getIdentityConstraintDefinitions().removeAll(keys);
    }

    private void createUniqueKey(XSDFactory factory, XSDElementDeclaration declaration, XSDComplexTypeDefinition complexType) {
        List<String> fields = getPKFields(complexType);
        if (!fields.isEmpty()) {
            XSDIdentityConstraintDefinition uniqueKey = factory.createXSDIdentityConstraintDefinition();
            uniqueKey.setIdentityConstraintCategory(XSDIdentityConstraintCategory.UNIQUE_LITERAL);
            uniqueKey.setName(declaration.getName());
            XSDXPathDefinition selector = factory.createXSDXPathDefinition();
            selector.setVariety(XSDXPathVariety.SELECTOR_LITERAL);
            selector.setValue(".");//$NON-NLS-1$
            uniqueKey.setSelector(selector);
            for (String fieldName : fields) {
                XSDXPathDefinition field = factory.createXSDXPathDefinition();
                field.setVariety(XSDXPathVariety.FIELD_LITERAL);
                field.setValue(fieldName);
                uniqueKey.getFields().add(field);
            }
            declaration.getIdentityConstraintDefinitions().add(uniqueKey);
        }
    }

    /**
     * return all defined pk field names or return the first element's name
     */
    private List<String> getPKFields(XSDComplexTypeDefinition complexType) {
        List<String> fields = getUsablePKFields(complexType, getElementDecarations());

        if (fields.isEmpty()) {
            XSDElementDeclaration firstElement = getFirstElement((XSDComplexTypeDefinition) complexType.getRootType());
            if (firstElement != null) {
                fields.add(firstElement.getName());
            }
        }

        return fields;
    }

    private EList<XSDElementDeclaration> getElementDecarations() {
        return schema.getElementDeclarations();
    }

    private List<String> getUsablePKFields(XSDComplexTypeDefinition complexType, List<XSDElementDeclaration> elementDeclarations) {
        List<String> fields = new ArrayList<String>();

        List<String> definedPKs = new ArrayList<String>();

        XSDComplexTypeDefinition rootType = (XSDComplexTypeDefinition) complexType.getRootType();
        for (XSDElementDeclaration decla : elementDeclarations) {
            if (decla.getTypeDefinition() instanceof XSDComplexTypeDefinition) {
                XSDComplexTypeDefinition typeDefinition = (XSDComplexTypeDefinition) decla.getTypeDefinition();
                XSDTypeDefinition arootType = typeDefinition.getRootTypeDefinition();
                if (arootType == rootType) {
                    recordFields(decla, definedPKs);
                }
            }
        }

        if (definedPKs.size() > 0) {
            List<XSDComplexTypeDefinition> allSuperComplexTypes = Util.getAllSuperComplexTypes(complexType);
            for (int i = allSuperComplexTypes.size() - 1; i >= 0; i--) {
                XSDComplexTypeDefinition cTypeDef = allSuperComplexTypes.get(i);
                if (cTypeDef.getContent() instanceof XSDParticle) {
                    XSDParticleImpl particle = (XSDParticleImpl) cTypeDef.getContent();
                    if (particle.getTerm() instanceof XSDModelGroup) {
                        XSDModelGroup group = (XSDModelGroup) particle.getTerm();
                        EList<XSDParticle> particles = group.getParticles();
                        for (XSDParticle part : particles) {
                            if (part.getTerm() instanceof XSDElementDeclaration) {
                                XSDElementDeclaration xsdDecl = (XSDElementDeclaration) part.getTerm();
                                if (definedPKs.contains(xsdDecl.getName())) {
                                    fields.add(xsdDecl.getName());
                                }
                            }
                        }
                    }
                }
            }
        }

        return fields;
    }

    private void recordFields(XSDElementDeclaration decla, List<String> definedPKs) {
        EList<XSDIdentityConstraintDefinition> idConstraintDefs = decla.getIdentityConstraintDefinitions();
        if (idConstraintDefs != null) {
            for (XSDIdentityConstraintDefinition idCDef : idConstraintDefs) {
                if (idCDef.getIdentityConstraintCategory().equals(XSDIdentityConstraintCategory.UNIQUE_LITERAL)) {
                    EList<XSDXPathDefinition> xsdXPath = idCDef.getFields();
                    for (XSDXPathDefinition xpath : xsdXPath) {
                        if (!definedPKs.contains(xpath.getValue()) && xpath.getVariety() == XSDXPathVariety.FIELD_LITERAL
                                && !xpath.getValue().equals(".")) { //$NON-NLS-1$
                            definedPKs.add(xpath.getValue());
                        }
                    }
                }
            }
        }
    }

    private XSDElementDeclaration getFirstElement(XSDComplexTypeDefinition rootType) {
        if (rootType.getContent() instanceof XSDParticle) {
            if (((XSDParticle) rootType.getContent()).getTerm() instanceof XSDModelGroup) {
                XSDModelGroup group = (XSDModelGroup) ((XSDParticle) rootType.getContent()).getTerm();
                EList<XSDParticle> gpl = group.getContents();
                XSDElementDeclaration firstDecl = null;
                for (XSDParticle part : gpl) {
                    if (part.getTerm() instanceof XSDElementDeclaration) {
                        firstDecl = (XSDElementDeclaration) part.getTerm();
                        return firstDecl;
                    }
                }
            }
        }

        return null;
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

    private void checkConcept() {
        EList<XSDIdentityConstraintDefinition> l = decl.getIdentityConstraintDefinitions();
        for (XSDIdentityConstraintDefinition icd : l) {
            if (icd.getIdentityConstraintCategory().equals(XSDIdentityConstraintCategory.UNIQUE_LITERAL)) {
                isConcept = true;
                break;
            }
        }

    }

    @Override
    public void runWithEvent(Event event) {
        super.runWithEvent(event);
    }

    /********************************
     * Listener to input dialog
     */
    public void widgetDefaultSelected(SelectionEvent e) {
    }

    public void widgetSelected(SelectionEvent e) {
        if (dialog.getReturnCode() == -1) {
            return;
        }
        superTypeName = dialog.getSuperName();
        isAbstract = dialog.isAbstract();
        typeName = dialog.getTypeName();
        isChoice = dialog.isChoice();
        isAll = dialog.isAll();

        if (!validateType()) {
            return;
        }
        dialog.close();
    }

    private boolean validateType() {
        if (!"".equals(typeName)) {//$NON-NLS-1$
            EList<XSDTypeDefinition> list = schema.getTypeDefinitions();
            for (XSDTypeDefinition td : list) {
                if (td.getName().equals(typeName)) {
                    if (td instanceof XSDSimpleTypeDefinition) {
                        MessageDialog.openError(page.getSite().getShell(), Messages._Error,
                                Messages.bind(Messages.XSDChangeToCXX_ErrorMsg2, typeName));
                        return false;
                    }
                }
            }// for
        }

        return true;
    }
}
