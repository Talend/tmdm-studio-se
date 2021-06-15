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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDTerm;
import org.eclipse.xsd.util.XSDSchemaBuildingTools;
import org.w3c.dom.Element;

import com.amalto.workbench.dialogs.BusinessElementInputDialog;
import com.amalto.workbench.editors.DataModelMainPage;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.utils.IConstants;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.utils.XSDAnnotationsStructure;

public class XSDNewParticleFromTypeAction extends UndoAction implements SelectionListener {

    private static Log log = LogFactory.getLog(XSDNewParticleFromTypeAction.class);

    private BusinessElementInputDialog dialog = null;

    private XSDModelGroup group = null;

    private String elementName;

    private int minOccurs;

    private int maxOccurs;

    private XSDComplexTypeDefinition ctd;

    private String simpleTypeName;

    private String refName;

    public XSDNewParticleFromTypeAction(DataModelMainPage page) {
        super(page);
        this.simpleTypeName = "string";//$NON-NLS-1$
        setImageDescriptor(ImageCache.getImage(EImage.ADD_OBJ.getPath()));
        setText(Messages._AddStringElement);
        setToolTipText(Messages._AddABusinessElementTop);
    }

    public XSDNewParticleFromTypeAction(DataModelMainPage page, String simpleType) {
        super(page);
        this.simpleTypeName = simpleType;
        setText(Messages.bind(Messages._AddTypeElement, simpleType));
        setToolTipText(Messages._AddFromTypeFirstPos);
    }

    @Override
    public IStatus doAction() {
        try {
            IStructuredSelection selection = (IStructuredSelection) page.getTreeViewer().getSelection();
            if (selection.getFirstElement() instanceof XSDComplexTypeDefinition) {
                ctd = (XSDComplexTypeDefinition) selection.getFirstElement();
                if (!(ctd.getContent() instanceof XSDParticle)) {
                    return Status.CANCEL_STATUS;
                }
                if (!(((XSDParticle) ctd.getContent()).getTerm() instanceof XSDModelGroup)) {
                    return Status.CANCEL_STATUS;
                }
                ;
                group = (XSDModelGroup) ((XSDParticle) ctd.getContent()).getTerm();
            } else if (selection.getFirstElement() instanceof XSDParticle) {
                group = (XSDModelGroup) ((XSDParticle) selection.getFirstElement()).getTerm();
            } else if (selection.getFirstElement() instanceof XSDModelGroup) {
                group = (XSDModelGroup) selection.getFirstElement();
            } else {
                log.info(Messages.bind(Messages._UnkownSection, selection.getFirstElement().getClass().getName(), selection
                        .getFirstElement().toString()));
                return Status.CANCEL_STATUS;
            }

            EList<XSDElementDeclaration> eDecls = schema.getElementDeclarations();
            List<String> elementDeclarations = new LinkedList<String>();
            for (XSDElementDeclaration xsdElementDeclaration : eDecls) {
                XSDElementDeclaration d = xsdElementDeclaration;
                if (d.getTargetNamespace() != null && d.getTargetNamespace().equals(IConstants.DEFAULT_NAME_SPACE)) {
                    continue;
                }
                elementDeclarations.add(d.getQName() + (d.getTargetNamespace() != null ? " : " + d.getTargetNamespace() : ""));//$NON-NLS-1$//$NON-NLS-2$
            }
            elementDeclarations.add("");//$NON-NLS-1$

            dialog = new BusinessElementInputDialog(this, page.getSite().getShell(), Messages._AddANewBusinessElement,
                    "", "", elementDeclarations, 0, 1, true, false);//$NON-NLS-1$//$NON-NLS-2$;
            dialog.setBlockOnOpen(true);
            int ret = dialog.open();
            if (ret == Dialog.CANCEL) {
                return Status.CANCEL_STATUS;
            }

            XSDFactory factory = XSDSchemaBuildingTools.getXSDFactory();

            XSDElementDeclaration decl = factory.createXSDElementDeclaration();
            decl.setName(this.elementName);
            // decl.setTypeDefinition(schema.resolveSimpleTypeDefinition(schema.getSchemaForSchemaNamespace(),
            // simpleTypeName));
            if (!refName.equals("")) {//$NON-NLS-1$
                XSDElementDeclaration ref = Util.findReference(refName, schema);
                if (ref != null) {
                    decl.setResolvedElementDeclaration(ref);
                }
            } else {
                decl.setTypeDefinition(schema.resolveSimpleTypeDefinition(schema.getSchemaForSchemaNamespace(), simpleTypeName));
            }

            XSDParticle particle = factory.createXSDParticle();
            particle.setContent(decl);
            particle.setMinOccurs(this.minOccurs);
            particle.setMaxOccurs(this.maxOccurs);

            group.getContents().add(group.getContents().size(), particle);
            group.updateElement();

            if (Util.changeElementTypeToSequence(decl, maxOccurs) == Status.CANCEL_STATUS) {
                return Status.CANCEL_STATUS;
            }
            // fix 0010248. add annotion from parent

            if (dialog.isInherit()) {
                XSDTerm totm = particle.getTerm();
                XSDElementDeclaration concept = null;
                Object obj = Util.getParent(particle);
                if (obj instanceof XSDElementDeclaration) {
                    concept = (XSDElementDeclaration) obj;
                } else {
                    concept = (XSDElementDeclaration) particle.getContent();
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

            page.refresh();
            page.getTreeViewer().setSelection(new StructuredSelection(particle), true);
            page.markDirty();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            MessageDialog.openError(page.getSite().getShell(), Messages._Error,
                    Messages.bind(Messages._ErrorCreatBusinessElement, e.getLocalizedMessage()));
            return Status.CANCEL_STATUS;
        }
        return Status.OK_STATUS;
    }

    @Override
    public void runWithEvent(Event event) {
        super.runWithEvent(event);
    }

    public void addAnnotion(XSDAnnotationsStructure struc, XSDAnnotation xsdannotationparent) {
        Map<String, List<String>> infor = new HashMap<String, List<String>>();
        infor = cloneXSDAnnotation(xsdannotationparent);
        Set<String> keys = infor.keySet();
        for (int i = 0; i < infor.size(); i++) {
            ArrayList<String> lists = (ArrayList<String>) infor.get(keys.toArray()[i]);
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
                    String type = oldElem.getAttributes().getNamedItem("source").getNodeValue();//$NON-NLS-1$
                    // X_Write,X_Hide
                    if (type.equals("X_Write") || type.equals("X_Hide")) {//$NON-NLS-1$//$NON-NLS-2$
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
                    Messages.bind(Messages._ErrorPasteEntity, e.getLocalizedMessage()));
        }
        return infor;
    }

    /********************************
     * Listener to input dialog
     */
    public void widgetDefaultSelected(SelectionEvent e) {
    }

    public void widgetSelected(SelectionEvent e) {
        if (dialog.getReturnCode() == -1) {
            return; // there was a validation error
        }
        elementName = dialog.getElementName();
        refName = dialog.getRefName();
        minOccurs = dialog.getMinOccurs();
        maxOccurs = dialog.getMaxOccurs();

        // check that this element does not already exist
        // get position of the selected particle in the container
        for (XSDParticle xsdParticle : group.getContents()) {
            XSDParticle p = xsdParticle;
            if (p.getTerm() instanceof XSDElementDeclaration) {
                XSDElementDeclaration thisDecl = (XSDElementDeclaration) p.getTerm();
                if (thisDecl.getName().equals(elementName)) {
                    MessageDialog.openError(page.getSite().getShell(), Messages._Error,
                            Messages.bind(Messages._TheBusinessElement, elementName));
                    return;
                }
            }
        }// for

        dialog.close();
    }

}
