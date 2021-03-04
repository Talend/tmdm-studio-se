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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDComplexTypeContent;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTerm;
import org.eclipse.xsd.XSDTypeDefinition;
import org.w3c.dom.Element;

import com.amalto.workbench.editors.DataModelMainPage;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.utils.WorkbenchClipboard;
import com.amalto.workbench.utils.XSDAnnotationsStructure;

public class XSDPasteConceptAction extends UndoAction {

    private static Log log = LogFactory.getLog(XSDPasteConceptAction.class);

    List<XSDElementDeclaration> conceptList = new ArrayList<XSDElementDeclaration>();

    Set<XSDTypeDefinition> copyTypeSet = new HashSet<XSDTypeDefinition>();

    Map<String, XSDTypeDefinition> typeList = new HashMap<String, XSDTypeDefinition>();

    String displayName = Messages.XSDPasteConceptAction_DisplayName;

    public XSDPasteConceptAction(DataModelMainPage page, String title) {
        super(page);

        displayName = title;
        setImageDescriptor(ImageCache.getImage(EImage.PASTE.getPath()));
        setText(displayName);
        setToolTipText(Messages.XSDPasteConceptAction_ActionTip);
    }

    @Override
    public IStatus doAction() {

        try {

            conceptList = WorkbenchClipboard.getWorkbenchClipboard().getConcepts();

            XSDFactory factory = XSDFactory.eINSTANCE;
            if (!conceptList.isEmpty()) {
                // List<String> concepts = new ArrayList<String>();
                int index = 0;
                for (Iterator<XSDElementDeclaration> it = conceptList.iterator(); it.hasNext();) {

                    if (conceptList.get(index).getSchema() != null) {
                        // concepts = Util.getConcepts(conceptList.get(index).getSchema());
                        typeList = Util.getTypeDefinition(conceptList.get(index).getSchema());
                    }
                    index++;
                    Object concept = it.next();

                    if (concept instanceof XSDElementDeclaration) {
                        // edit by ymli,fix the bug:0011523. let the element(simple or complex) can be pasted
                        // if (concepts.contains(((XSDElementDeclaration) concept).getName())) {
                        XSDElementDeclaration copy_concept = (XSDElementDeclaration) concept;

                        XSDElementDeclaration new_copy_concept = factory.createXSDElementDeclaration();


                        new_copy_concept = (XSDElementDeclaration) copy_concept.cloneConcreteComponent(true, false);
                        InputDialog id = new InputDialog(page.getSite().getShell(), Messages.XSDPasteConceptAction_CopyElement,
                                Messages.XSDPasteConceptAction_DialogTip, Messages.bind(Messages.XSDPasteConceptAction_CopyOf,
                                        copy_concept.getName()), new IInputValidator() {

                                    public String isValid(String newText) {
                                        if ((newText == null) || "".equals(newText)) {
                                            return Messages.XSDPasteConceptAction_NameCannNotbeEmpty;
                                        }
                                        EList<XSDElementDeclaration> list = schema.getElementDeclarations();
                                        for (XSDElementDeclaration d : list) {
                                            if (d.getName().equalsIgnoreCase(newText)) {
                                                return Messages.XSDPasteConceptAction_EntityAlreadyExists;
                                            }
                                        }
                                        return null;
                                    };
                                });

                        id.setBlockOnOpen(true);
                        int ret = id.open();
                        if (ret == Window.CANCEL) {
                            return Status.CANCEL_STATUS;
                        }

                        new_copy_concept.setName(id.getValue());
                        for (int i = 0; i < new_copy_concept.getIdentityConstraintDefinitions().size(); i++) {
                            String name = new_copy_concept.getIdentityConstraintDefinitions().get(i).getName()
                                    .replaceAll(copy_concept.getName(), new_copy_concept.getName());
                            new_copy_concept.getIdentityConstraintDefinitions().get(i).setName(name);
                        }

                        ///modified by xie to fix the bug 22077

                        if(new_copy_concept.getAnonymousTypeDefinition() == null) {

                           XSDComplexTypeDefinition copyType =(XSDComplexTypeDefinition) copy_concept.getTypeDefinition().cloneConcreteComponent(true, false);
                           String originalName =  copyType.getName();
                           String typeName = "Copy_of_" + originalName;    //$NON-NLS-1$
                           copyType.setName(typeName);
                           schema.getContents().add(copyType);
                           new_copy_concept.setTypeDefinition(copyType);

                        }

                        new_copy_concept.updateElement();
                        schema.getContents().add(new_copy_concept);
                        addAnnotationForXSDElementDeclaration(copy_concept, new_copy_concept);


                    }
                }
                Map<String, XSDTypeDefinition> typeDef = Util.getTypeDefinition(schema);
                for (XSDTypeDefinition type : copyTypeSet) {
                    if (typeDef.containsKey(type.getName())) {
                        continue;
                    }
                    XSDTypeDefinition typedefinitionClone = null;
                    if (type instanceof XSDComplexTypeDefinition) {
                        typedefinitionClone = factory.createXSDComplexTypeDefinition();
                        typedefinitionClone = (XSDComplexTypeDefinition) type.cloneConcreteComponent(true, false);
                        schema.getContents().add(typedefinitionClone);
                        addAnnotationForComplexType((XSDComplexTypeDefinition) type,
                                (XSDComplexTypeDefinition) typedefinitionClone);
                    } else if (type instanceof XSDSimpleTypeDefinition) {
                        schema.getContents().add((XSDSimpleTypeDefinition) type.cloneConcreteComponent(true, false));
                    }
                }
                schema.getElement();
                // WSDataModel wsObject = (WSDataModel)
                // (page.getXObject().getWsObject());
                // wsObject.getXsdSchema();//.setXsdSchema(Util.nodeToString(
                // schema.getDocument()));

                /*
                 * String schema1 = ((XSDTreeContentProvider) page.getViewer()
                 * .getContentProvider()).getXSDSchemaAsString(); wsObject.setXsdSchema(schema1); XMLEditor
                 * xmleditor=((XObjectEditor)page.getEditor()).getXmlEditor(); xmleditor.refresh(page.getXObject());
                 */

                page.markDirty();
                page.refresh();
                // page.refreshData();

                getOperationHistory();
                WorkbenchClipboard.getWorkbenchClipboard().conceptsReset();
                typeList.clear();

                return Status.OK_STATUS;
            } else if (WorkbenchClipboard.getWorkbenchClipboard().getParticles().size() > 0) {
                copyElements();
                WorkbenchClipboard.getWorkbenchClipboard().particlesReset();
                page.markDirty();
                page.refresh();
                // page.refreshData();

                getOperationHistory();
                return Status.OK_STATUS;
            } else {
                return Status.CANCEL_STATUS;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            MessageDialog.openError(page.getSite().getShell(), Messages._Error,
                    Messages.bind(Messages.XSDPasteConceptAction_ErrorMsg1, e.getLocalizedMessage()));

        }
        return Status.OK_STATUS;
    }

    public boolean checkInPasteType() {
        /*
         * if(WorkbenchClipboard.getWorkbenchClipboard().getConcepts().size()>1) this.displayName = "Paste Entities";
         */
        if (WorkbenchClipboard.getWorkbenchClipboard().getConcepts().size() <= 0
                && WorkbenchClipboard.getWorkbenchClipboard().getParticles().size() <= 0) {
            return false;
        // edit by ymli,fix the bug:0011523. let the element(simple or complex) can be pasted
        /*
         * conceptList = WorkbenchClipboard.getWorkbenchClipboard().getConcepts(); int t = 0; for(XSDElementDeclaration
         * ele:conceptList){ if(conceptList.get(t).getSchema()!=null){ List<String> concepts =
         * Util.getConcepts(conceptList.get(t).getSchema()); if(concepts.contains(ele.getName())) return true; } t++; }
         */
        } else {
            return true;
        }
    }

    public void addAnnotationForComplexType(XSDComplexTypeDefinition fromType, XSDComplexTypeDefinition toType) {
        XSDComplexTypeContent tocomplexType = toType.getContent();
        XSDComplexTypeContent fromcomplexType = fromType.getContent();
        if (fromcomplexType instanceof XSDParticle) {

            XSDParticle fromxsdParticle = (XSDParticle) fromcomplexType;
            XSDParticle toxsdParticle = (XSDParticle) tocomplexType;

            if (fromxsdParticle.getTerm() instanceof XSDModelGroup) {

                XSDModelGroup frommodelGroup = ((XSDModelGroup) fromxsdParticle.getTerm());
                XSDModelGroup tomodelGroup = ((XSDModelGroup) toxsdParticle.getTerm());

                EList<XSDParticle> fromlist = frommodelGroup.getContents();
                EList<XSDParticle> tolist = tomodelGroup.getContents();

                Iterator<XSDParticle> toIt = tolist.iterator();

                for (XSDParticle fromel : fromlist.toArray(new XSDParticle[fromlist.size()])) {
                    XSDParticle toel = toIt.next();
                    XSDTerm totm = toel.getTerm();
                    XSDTerm fromtm = fromel.getTerm();
                    if (fromtm instanceof XSDElementDeclaration) {
                        XSDAnnotation fromannotation = ((XSDElementDeclaration) fromtm).getAnnotation();
                        if (fromannotation != null) {
                            XSDAnnotationsStructure struc = new XSDAnnotationsStructure(totm);
                            addAnnotion(struc, fromannotation);
                            if (this.typeList.containsKey(((XSDElementDeclaration) totm).getType().getName())) {
                                this.copyTypeSet.add(this.typeList.get(((XSDElementDeclaration) totm).getType().getName()));
                            }
                            addAnnotationForXSDElementDeclaration((XSDElementDeclaration) fromtm, (XSDElementDeclaration) totm);

                        }
                    }
                }
            }

        }
    }

    public void addAnnotationForXSDElementDeclaration(XSDElementDeclaration fromElem, XSDElementDeclaration toElem) {
        if (fromElem.getAnnotation() != null) {
            // toElem.setAnnotation(cloneXSDAnnotation(toElem.getAnnotation(),fromElem.getAnnotation()));
            XSDAnnotationsStructure struc = new XSDAnnotationsStructure(toElem);
            addAnnotion(struc, fromElem.getAnnotation());
        }
        if (fromElem.getTypeDefinition() instanceof XSDComplexTypeDefinition) {
            XSDComplexTypeContent fromcomplexType = ((XSDComplexTypeDefinition) fromElem.getTypeDefinition()).getContent();
            // if the concept is a complex type itself. copy the complex type also,
            // in this situation,if not copy the complex type, the type change to simple type
            if (this.typeList.containsKey(toElem.getTypeDefinition().getName())) {
                this.copyTypeSet.add(this.typeList.get(toElem.getTypeDefinition().getName()));
            }

            if (toElem.getTypeDefinition() instanceof XSDComplexTypeDefinition) {
                XSDComplexTypeContent tocomplexType = ((XSDComplexTypeDefinition) toElem.getTypeDefinition()).getContent();
                if (fromcomplexType instanceof XSDParticle) {

                    XSDParticle fromxsdParticle = (XSDParticle) fromcomplexType;
                    XSDParticle toxsdParticle = (XSDParticle) tocomplexType;

                    if (fromxsdParticle.getTerm() instanceof XSDModelGroup) {

                        XSDModelGroup frommodelGroup = ((XSDModelGroup) fromxsdParticle.getTerm());
                        XSDModelGroup tomodelGroup = ((XSDModelGroup) toxsdParticle.getTerm());

                        EList<XSDParticle> fromlist = frommodelGroup.getContents();
                        EList<XSDParticle> tolist = tomodelGroup.getContents();

                        Iterator<XSDParticle> toIt = tolist.iterator();

                        for (XSDParticle fromel : fromlist.toArray(new XSDParticle[fromlist.size()])) {
                            XSDParticle toel = toIt.next();
                            XSDTerm totm = toel.getTerm();
                            XSDTerm fromtm = fromel.getTerm();
                            if (fromtm instanceof XSDElementDeclaration) {
                                XSDAnnotation fromannotation = ((XSDElementDeclaration) fromtm).getAnnotation();
                                if (fromannotation != null) {
                                    // ((XSDElementDeclaration)
                                    // totm).setAnnotation(cloneXSDAnnotation(((XSDElementDeclaration)
                                    // totm).getAnnotation(),fromannotation));
                                    XSDAnnotationsStructure struc = new XSDAnnotationsStructure(totm);
                                    if (((XSDElementDeclaration) totm).getType() != null) {
                                        addAnnotion(struc, fromannotation);
                                    } else {
                                        MessageDialog.openInformation(page.getSite().getShell(), Messages.Warning,
                                                Messages.bind(Messages.XSDPasteConceptAction_Information, fromElem.getName()));
                                        return;
                                    }

                                }
                                if (((XSDElementDeclaration) totm).getType() != null
                                        && this.typeList.containsKey(((XSDElementDeclaration) totm).getType().getName())) {
                                    this.copyTypeSet.add(this.typeList.get(((XSDElementDeclaration) totm).getType().getName()));
                                }
                                addAnnotationForXSDElementDeclaration((XSDElementDeclaration) fromtm,
                                        (XSDElementDeclaration) totm);
                            }
                        }
                    }
                }
            }
        } else {
            String simpleType = fromElem.getTypeDefinition().getName();
            if (this.typeList.containsKey(simpleType)) {
                this.copyTypeSet.add(fromElem.getTypeDefinition());
            }
        }
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
        XSDAnnotation xsdannotation = XSDFactory.eINSTANCE.createXSDAnnotation();
        Map<String, List<String>> infor = new HashMap<String, List<String>>();
        try {
            /*
             * Element oldAnnElem =oldAnn.getElement(); Element newAnnElem = (Element)oldAnnElem.cloneNode(true);
             * xsdannotation.setElement(newAnnElem);
             */

            /*
             * List<Element> listAppInfo = new ArrayList<Element>(); List<Element> listUserInfo = new
             * ArrayList<Element>(); List<Attr> listAttri = new ArrayList<Attr>();
             */
            if (oldAnn != null) {
                for (int i = 0; i < oldAnn.getApplicationInformation().size(); i++) {
                    Element oldElem = oldAnn.getApplicationInformation().get(i);
                    // System.out.println(oldElem.getAttributes().getNamedItem(
                    // "source").getNodeValue());
                    String type = oldElem.getAttributes().getNamedItem(Messages.XSDPasteConceptAction_Source).getNodeValue();
                    /*
                     * Element newElem = (Element) oldElem.cloneNode(true);
                     * listAppInfo.add(oldAnn.getApplicationInformation().get(i));
                     */
                    if (!infor.containsKey(type)) {
                        List<String> typeList = new ArrayList<String>();
                        typeList.add(oldElem.getFirstChild().getNodeValue());
                        infor.put(type, typeList);
                    } else {
                        infor.get(type).add(oldElem.getFirstChild().getNodeValue());
                    }
                }
                /*
                 * xsdannotation.getApplicationInformation().addAll(listAppInfo);
                 *
                 * for (int i = 0; i < oldAnn.getUserInformation().size(); i++) { Element oldElemUserInfo =
                 * oldAnn.getUserInformation() .get(i); Element newElemUserInfo = (Element) oldElemUserInfo
                 * .cloneNode(true); listUserInfo.add(newElemUserInfo);
                 *
                 * } xsdannotation.getUserInformation().addAll(listUserInfo);
                 *
                 * for (int i = 0; i < oldAnn.getAttributes().size(); i++) { Attr oldAttri =
                 * oldAnn.getAttributes().get(i); Attr newAtri = (Attr) oldAttri.cloneNode(true);
                 * listAttri.add(newAtri); } xsdannotation.getAttributes().addAll(listAttri);
                 */
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            MessageDialog.openError(this.page.getSite().getShell(), Messages._Error,
                    Messages.bind(Messages.XSDPasteConceptAction_ErrorMsg2, e.getLocalizedMessage()));
        }
        return infor;
    }

    // edit by ymli; fix the bug:0011523: pasty partcles to the element
    public void copyElements() {
        ArrayList<XSDParticle> particles = WorkbenchClipboard.getWorkbenchClipboard().getParticles();
        IStructuredSelection selection = (IStructuredSelection) page.getTreeViewer().getSelection();
        XSDComplexTypeContent content = null;
        XSDElementDeclaration element = null;
        if(selection.getFirstElement() instanceof XSDElementDeclaration){
        	element = (XSDElementDeclaration) selection.getFirstElement();
        	content = ((XSDComplexTypeDefinition) element.getTypeDefinition()).getContent();
        } else if (selection.getFirstElement() instanceof XSDComplexTypeDefinition) {
            content = ((XSDComplexTypeDefinition) selection.getFirstElement()).getContent();
        } else if (selection.getFirstElement() instanceof XSDModelGroup) {
            XSDComplexTypeDefinition complexType = (XSDComplexTypeDefinition) ((XSDModelGroup) selection.getFirstElement())
                    .getContainer().getContainer();
            content = complexType.getContent();
        } else {
            if (selection.getFirstElement() instanceof XSDParticle) {
                XSDParticle particle = (XSDParticle) selection.getFirstElement();
                XSDElementDeclaration declar = (XSDElementDeclaration) particle.getTerm();
                if (declar.getTypeDefinition() instanceof XSDComplexTypeDefinition) {
                    XSDComplexTypeDefinition typeDefinition = (XSDComplexTypeDefinition) declar.getTypeDefinition();
                    content = typeDefinition.getContent();
                }
            }
        }

        if (content instanceof XSDParticle) {
            XSDParticle partile = (XSDParticle) content;
            if (partile.getTerm() instanceof XSDModelGroup) {
                XSDModelGroup toGroup = ((XSDModelGroup) partile.getTerm());
                for (XSDParticle particle : particles) {
                    // if the is particle with the same name, donot copy it.
                    if (isExist(toGroup, particle)) {
                        boolean ifOverwrite = MessageDialog.openConfirm(this.page.getSite().getShell(), Messages.XSDPasteConceptAction_Confirm,
                                Messages.bind(Messages.XSDPasteConceptAction_ErrorMsg3, ((XSDElementDeclaration) particle.getTerm()).getName()));
                        if (ifOverwrite) {
                            reomveElement(toGroup, particle);
                        } else {
                            continue;
                        }
                    }

                    XSDParticle newParticle = (XSDParticle) particle.cloneConcreteComponent(true, false);
                    if (newParticle.getContent() instanceof XSDElementDeclaration
                            && Util.changeElementTypeToSequence(element, newParticle.getMaxOccurs()) == Status.CANCEL_STATUS) {
                        break;
                    }
                    toGroup.getContents().add(newParticle);
                    toGroup.updateElement();

                    if (newParticle.getContent() instanceof XSDElementDeclaration) {
                        if (((XSDElementDeclaration) newParticle.getContent()).getTypeDefinition() instanceof XSDComplexTypeDefinition) {
                            addAnnotationForComplexType(
                                    (XSDComplexTypeDefinition) ((XSDElementDeclaration) particle.getContent())
                                            .getTypeDefinition(),
                                    (XSDComplexTypeDefinition) ((XSDElementDeclaration) newParticle.getContent())
                                            .getTypeDefinition());
                        }

                        XSDAnnotationsStructure struc1 = new XSDAnnotationsStructure(newParticle.getTerm());
                        addAnnotion(struc1, ((XSDElementDeclaration) particle.getTerm()).getAnnotation());

                        Util.changeElementTypeToSequence((XSDElementDeclaration) newParticle.getContent(),
                                newParticle.getMaxOccurs());
                    }

                }
            }
        }
    }

    public boolean isExist(XSDModelGroup toGroup, XSDParticle particle) {
        for (XSDParticle paticleContent : toGroup.getContents()) {
            if (paticleContent.getTerm() instanceof XSDElementDeclaration) {
                String contentName = ((XSDElementDeclaration) paticleContent.getTerm()).getName();
                String copyParticleName = ((XSDElementDeclaration) particle.getTerm()).getName();
                if (contentName.equals(copyParticleName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void reomveElement(XSDModelGroup toGroup, XSDParticle particle) {
        for (XSDParticle paticleContent : toGroup.getContents()) {
            if (paticleContent.getTerm() instanceof XSDElementDeclaration) {
                String contentName = ((XSDElementDeclaration) paticleContent.getTerm()).getName();
                String copyParticleName = ((XSDElementDeclaration) particle.getTerm()).getName();
                if (contentName.equals(copyParticleName)) {
                    toGroup.getContents().remove(paticleContent);
                    toGroup.updateElement();
                    return;
                }
            }
        }
    }

}
