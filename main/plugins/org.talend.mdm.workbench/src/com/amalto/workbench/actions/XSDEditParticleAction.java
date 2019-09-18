// ============================================================================
//
// Copyright (C) 2006-2019 Talend Inc. - www.talend.com
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDIdentityConstraintDefinition;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDXPathDefinition;
import org.eclipse.xsd.util.XSDConstants;
import org.eclipse.xsd.util.XSDSchemaBuildingTools;
import org.w3c.dom.Element;

import com.amalto.workbench.dialogs.BusinessElementInputDialog;
import com.amalto.workbench.editors.DataModelMainPage;
import com.amalto.workbench.exadapter.ExAdapterManager;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.providers.datamodel.SchemaTreeContentProvider;
import com.amalto.workbench.utils.IConstants;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.utils.XSDUtil;

public class XSDEditParticleAction extends UndoAction implements SelectionListener {

    private static Log log = LogFactory.getLog(XSDEditParticleAction.class);

    private BusinessElementInputDialog dialog = null;

    private XSDParticle selParticle = null;

    private String elementName;

    private String refName;

    private int minOccurs;

    private int maxOccurs;

    private String initEleName = ""; //$NON-NLS-1$

    private IXSDElementOperationExAdapter elementExAdapter = null;

    private IMatchRuleMapInfoOperationExAdapter mapinfoExAdapter = null;

    public XSDEditParticleAction(DataModelMainPage page) {
        super(page);
        setImageDescriptor(ImageCache.getImage(EImage.EDIT_OBJ.getPath()));
        setText(Messages.XSDEditParticleAction_EditElement);
        setToolTipText(Messages.XSDEditParticleAction_EditBusinessElement);
        this.elementExAdapter = ExAdapterManager.getAdapter(this, IXSDElementOperationExAdapter.class);
        this.mapinfoExAdapter = ExAdapterManager.getAdapter(this, IMatchRuleMapInfoOperationExAdapter.class);
    }

    @Override
    public IStatus doAction() {
        try {
            IStructuredSelection selection = (IStructuredSelection) page.getTreeViewer().getSelection();

            String originalXpath = getOriginalXpath();
            String entity = originalXpath.substring(0, originalXpath.indexOf("/")); //$NON-NLS-1$

            selParticle = (XSDParticle) selection.getFirstElement();

            if (!(selParticle.getTerm() instanceof XSDElementDeclaration)) {
                return Status.CANCEL_STATUS;
            }

            XSDElementDeclaration decl = (XSDElementDeclaration) selParticle.getContent();

            XSDElementDeclaration ref = null;
            if (decl.isElementDeclarationReference()) {
                // it is a ref
                ref = decl.getResolvedElementDeclaration();
            }

            EList eDecls = decl.getSchema().getElementDeclarations();

            ArrayList<String> elementDeclarations = new ArrayList<String>();
            for (Iterator iter = eDecls.iterator(); iter.hasNext();) {
                XSDElementDeclaration d = (XSDElementDeclaration) iter.next();
                if (d.getTargetNamespace() != null && d.getTargetNamespace().equals(IConstants.DEFAULT_NAME_SPACE)) {
                    continue;
                }

                if (!d.getQName().equals(entity)) {
                    elementDeclarations
                            .add(d.getQName() + (d.getTargetNamespace() != null ? " : " + d.getTargetNamespace() : "")); //$NON-NLS-1$ //$NON-NLS-2$
                }
            }
            elementDeclarations.add(""); //$NON-NLS-1$

            XSDIdentityConstraintDefinition identify = null;

            XSDXPathDefinition keyPath = null;
            List<Object> keyInfo = Util.getKeyInfo(decl);
            boolean isPK = false;
            if (keyInfo != null && keyInfo.size() > 0) {
                identify = (XSDIdentityConstraintDefinition) keyInfo.get(0);
                keyPath = (XSDXPathDefinition) keyInfo.get(1);
                isPK = true;
            }
            initEleName = decl.getName();
            dialog = new BusinessElementInputDialog(this, page.getSite().getShell(),
                    Messages.XSDEditParticleAction_InputDialogTitle, decl.getName(), ref == null ? null : ref.getQName(),
                    elementDeclarations, selParticle.getMinOccurs(), selParticle.getMaxOccurs(), false, isPK);
            dialog.setBlockOnOpen(true);
            int ret = dialog.open();
            if (ret == Window.CANCEL) {
                return Status.CANCEL_STATUS;
            }
            if (keyPath != null) {
                identify.getFields().remove(keyPath);
            }
            // find reference
            XSDElementDeclaration newRef = null;
            if (!"".equals(refName.trim())) { //$NON-NLS-1$
                newRef = Util.findReference(refName, schema);
                if (newRef == null) {
                    MessageDialog.openError(this.page.getSite().getShell(), Messages._Error,
                            Messages.bind(Messages.XSDEditParticleAction_ErrorMsg, refName));
                    return Status.CANCEL_STATUS;
                }
            }// ref

            // update validation rule
            Set<String> paths = new HashSet<String>();
            Util.collectElementPaths((IStructuredContentProvider) page.getElementsViewer().getContentProvider(), page.getSite(),
                    selParticle, paths, null);
            //

            decl.setName("".equals(this.elementName) ? null : this.elementName); //$NON-NLS-1$
            if (keyPath != null) {
                XSDFactory factory = XSDSchemaBuildingTools.getXSDFactory();
                XSDXPathDefinition field = factory.createXSDXPathDefinition();
                field.setVariety(keyPath.getVariety());
                field.setValue(elementName);
                identify.getFields().add(field);
            }

            if (newRef != null) {
                decl.setResolvedElementDeclaration(newRef);
                decl.setTypeDefinition(null);
                decl.setAnnotation(null);
                Element elem = decl.getElement();
                if (elem.getAttributes().getNamedItem("type") != null) {
                    elem.getAttributes().removeNamedItem("type");//$NON-NLS-1$
                }

                if (ref == null) {
                    // TODO remove from entity category annotation if exist
                    List<XSDElementDeclaration> conceptsOfField = XSDUtil.getConceptsOfField(selParticle);
                    for (XSDElementDeclaration concept : conceptsOfField) {
                        XSDUtil.syncEntityCategoryAnnotation(concept, selParticle, decl.getName(), null);
                        concept.updateElement();
                    }
                }
                decl.updateElement();
            } else {
                if (ref != null) {
                    XSDSimpleTypeDefinition stringType = ((SchemaTreeContentProvider) page.getTreeViewer().getContentProvider())
                            .getXsdSchema().getSchemaForSchema()
                            .resolveSimpleTypeDefinition(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001, "string"); //$NON-NLS-1$
                    decl.setResolvedElementDeclaration(decl);
                    selParticle.setTerm(decl);
                    decl.setTypeDefinition(stringType);
                    decl.updateElement();
                } else {
                    // TODO handle entity category annotation change if exist
                    List<XSDElementDeclaration> conceptsOfField = XSDUtil.getConceptsOfField(selParticle);
                    for (XSDElementDeclaration concept : conceptsOfField) {
                        XSDUtil.syncEntityCategoryAnnotation(concept, selParticle, initEleName, elementName);
                        concept.updateElement();
                    }
                }

                selParticle.updateElement();
            }

            if (Util.changeElementTypeToSequence(decl, maxOccurs) == Status.CANCEL_STATUS) {
                return Status.CANCEL_STATUS;
            }

            selParticle.setMinOccurs(this.minOccurs);
            if (maxOccurs > -1) {
                selParticle.setMaxOccurs(this.maxOccurs);
            } else {
                if (selParticle.getElement().getAttributeNode("maxOccurs") != null) {
                    selParticle.getElement().getAttributeNode("maxOccurs").setNodeValue("unbounded");//$NON-NLS-1$//$NON-NLS-2$
                } else {
                    selParticle.getElement().setAttribute("maxOccurs", "unbounded");//$NON-NLS-1$//$NON-NLS-2$
                }
            }

            selParticle.updateElement();

            updateReference(originalXpath);

            if (elementExAdapter != null) {
                elementExAdapter.renameElement(decl.getSchema(), paths, decl.getName());
            }
            if (mapinfoExAdapter != null) {
                mapinfoExAdapter.renameElementMapinfo(paths, decl.getName());
            }

            page.refresh();
            page.markDirty();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            MessageDialog.openError(page.getSite().getShell(), Messages._Error,
                    Messages.bind(Messages.XSDEditParticleAction_ErrorMsg1, e.getLocalizedMessage()));
            return Status.CANCEL_STATUS;
        }

        return Status.OK_STATUS;
    }

    private String getOriginalXpath() {
        XSDGetXPathAction getXpathAction = new XSDGetXPathAction(page);
        getXpathAction.doAction();
        String originalXpath = getXpathAction.getCopiedXpath();
        return originalXpath;
    }

    private void updateReference(String originalXpath) {
        XSDElementDeclaration decl = (XSDElementDeclaration) selParticle.getContent();
        int lastIndex = originalXpath.lastIndexOf("/"); //$NON-NLS-1$

        String newXpath = decl.getName();
        newXpath = originalXpath.substring(0, lastIndex + 1) + newXpath;
        IStructuredContentProvider provider = (IStructuredContentProvider) page.getTreeViewer().getContentProvider();
        Object[] allForeignKeyRelatedInfos = Util.getAllForeignKeyRelatedInfos(page.getSite(), new ArrayList<Object>(), provider,
                new HashSet<Object>());
        Util.updateForeignKeyRelatedInfo(originalXpath, newXpath, allForeignKeyRelatedInfos);
    }

    @Override
    public void runWithEvent(Event event) {
        super.runWithEvent(event);
    }

    /********************************
     * Listener to input dialog
     */
    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
    }

    @Override
    public void widgetSelected(SelectionEvent e) {
        if (dialog.getReturnCode() == -1) {
            return; // there was a validation error
        }
        elementName = dialog.getElementName();
        refName = dialog.getRefName();
        minOccurs = dialog.getMinOccurs();
        maxOccurs = dialog.getMaxOccurs();

        // check that this element does not already exist
        XSDModelGroup group = (XSDModelGroup) selParticle.getContainer();
        // get position of the selected particle in the container
        for (Object element : group.getContents()) {
            XSDParticle p = (XSDParticle) element;
            if (p.getTerm() instanceof XSDElementDeclaration) {
                XSDElementDeclaration thisDecl = (XSDElementDeclaration) p.getTerm();
                if (thisDecl.getName().equals(elementName) && initEleName != null && !initEleName.equalsIgnoreCase(elementName)) {
                    MessageDialog.openError(page.getSite().getShell(), Messages._Error,
                            Messages.bind(Messages.XSDEditParticleAction_ErrorMsg2, elementName));
                    return;
                }
            }
        }// for
        dialog.close();
    }

}
