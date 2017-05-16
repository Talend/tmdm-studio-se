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
package com.amalto.workbench.detailtabs.sections.handlers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDIdentityConstraintDefinition;
import org.eclipse.xsd.XSDXPathDefinition;
import org.eclipse.xsd.util.XSDConstants;
import org.eclipse.xsd.util.XSDSchemaBuildingTools;
import org.w3c.dom.Element;

import com.amalto.workbench.actions.IMatchRuleMapInfoOperationExAdapter;
import com.amalto.workbench.actions.IXSDElementOperationExAdapter;
import com.amalto.workbench.actions.XSDGetXPathAction;
import com.amalto.workbench.detailtabs.exception.CommitException;
import com.amalto.workbench.detailtabs.exception.CommitValidationException;
import com.amalto.workbench.detailtabs.sections.model.element.ElementWrapper;
import com.amalto.workbench.editors.DataModelMainPage;
import com.amalto.workbench.editors.xsdeditor.XSDEditor;
import com.amalto.workbench.exadapter.ExAdapterManager;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.utils.XSDAnnotationsStructure;

public class ElementWrapperCommitHandler extends CommitHandler<ElementWrapper> {

    private IXSDElementOperationExAdapter elementExAdapter = null;

    private IMatchRuleMapInfoOperationExAdapter mapinfoExAdapter = null;

    public ElementWrapperCommitHandler(ElementWrapper submittedObj) {
        super(submittedObj);
        this.elementExAdapter = ExAdapterManager.getAdapter(this, IXSDElementOperationExAdapter.class);
        this.mapinfoExAdapter = ExAdapterManager.getAdapter(this, IMatchRuleMapInfoOperationExAdapter.class);
    }

    @Override
    protected void validateCommit() throws CommitValidationException {

        validateElementNameAndReference();

        validateCardinality();

    }

    @Override
    protected boolean doSubmit() throws CommitException {

        try {
            String originalName = getCommitedObj().getSourceName();

            XSDElementDeclaration decl = getCommitedObj().getSourceXSDContent();

            XSDElementDeclaration ref = null;
            if (decl.isElementDeclarationReference()) {
                ref = decl.getResolvedElementDeclaration();
            }
            XSDAnnotationsStructure struct = new XSDAnnotationsStructure(getCommitedObj().getSourceElement());
            // remove first
            // struct.setAutoExpand(null);
            struct.setAutoExpand(String.valueOf(getCommitedObj().isAutoExpand()));

            // update validation rule
            Set<String> paths = new HashSet<String>();
            DataModelMainPage page = getPage();
            Util.collectElementPaths((IStructuredContentProvider) page.getElementsViewer().getContentProvider(), page.getSite(),
                    getCommitedObj().getSourceElement(), paths, null);

            XSDElementDeclaration newRef = Util.findReference(getCommitedObj().getNewReference(), getCommitedObj().getSchema());

            XSDIdentityConstraintDefinition identify = null;
            XSDXPathDefinition keyPath = null;
            List<Object> keyInfo = Util.getKeyInfo(getCommitedObj().getSourceXSDContent());
            if (keyInfo != null && keyInfo.size() > 0) {
                identify = (XSDIdentityConstraintDefinition) keyInfo.get(0);
                keyPath = (XSDXPathDefinition) keyInfo.get(1);
                identify.getFields().remove(keyPath);
            }

            getCommitedObj().getSourceXSDContent().setName(
                    "".equals(getCommitedObj().getNewName()) ? null : getCommitedObj().getNewName());//$NON-NLS-1$
            if (keyPath != null) {
                XSDFactory factory = XSDSchemaBuildingTools.getXSDFactory();
                XSDXPathDefinition field = factory.createXSDXPathDefinition();
                field.setVariety(keyPath.getVariety());
                field.setValue(getCommitedObj().getNewName());
                identify.getFields().add(field);
            }

            if (newRef != null) {
                decl.setResolvedElementDeclaration(newRef);
                decl.setTypeDefinition(null);
                Element elem = decl.getElement();
                if (elem.getAttributes().getNamedItem("type") != null) {
                    elem.getAttributes().removeNamedItem("type");//$NON-NLS-1$
                }
                decl.updateElement();
            } else if (ref != null) {

                XSDElementDeclaration sourceXSDContent = getCommitedObj().getSourceXSDContent();
                sourceXSDContent.setTypeDefinition(getCommitedObj().getSchema().getSchemaForSchema()
                        .resolveSimpleTypeDefinition(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001, "string"));//$NON-NLS-1$

                sourceXSDContent.setResolvedElementDeclaration(sourceXSDContent);
                // XSDFactory factory = XSDSchemaBuildingTools.getXSDFactory();
                // XSDElementDeclaration newD = (XSDElementDeclaration) factory.createXSDElementDeclaration();
                // newD.setName(getCommitedObj().getNewName());
                // newD.updateElement();
                // XSDSimpleTypeDefinition stringType = getCommitedObj().getSchema().getSchemaForSchema()
                // .resolveSimpleTypeDefinition(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001, "string");
                //
                // newD.setTypeDefinition(stringType);
                // if (getCommitedObj().getSourceElement().getContainer() instanceof XSDModelGroup) {
                // XSDModelGroup group = ((XSDModelGroup) getCommitedObj().getSourceElement().getContainer());
                // ((XSDModelGroup) getCommitedObj().getSourceElement().getContainer()).getContents().remove(
                // getCommitedObj().getSourceElement());
                // getCommitedObj().setSourceElement(factory.createXSDParticle());
                // getCommitedObj().getSourceElement().setContent(newD);
                // group.getContents().add(getCommitedObj().getSourceElement());
                // }
            }

            int newMaxOcur = getCommitedObj().getNewMaxOcur();
            if (Util.changeElementTypeToSequence(decl, newMaxOcur) == Status.CANCEL_STATUS) {
                return false;
            }

            int newMinOcur = getCommitedObj().getNewMinOcur();
            getCommitedObj().getSourceElement().setMinOccurs(newMinOcur);
            if (newMaxOcur == -1 || (newMaxOcur == 0 & newMinOcur == 0)) {
                if (!"unbounded".equals(getCommitedObj().getSourceElement().getElement().getAttribute("maxOccurs"))) {
                    getCommitedObj().getSourceElement().getElement().setAttribute("maxOccurs", "unbounded");//$NON-NLS-1$//$NON-NLS-2$
                }
            } else {
                getCommitedObj().getSourceElement().setMaxOccurs(newMaxOcur);
            }

            getCommitedObj().getSourceElement().updateElement();

            updateReference(originalName);

            if (elementExAdapter != null) {
                elementExAdapter.renameElement(getCommitedObj().getSchema(), paths, getCommitedObj().getNewName());
            }
            if (mapinfoExAdapter != null) {
                mapinfoExAdapter.renameElementMapinfo(paths, decl.getName());
            }
        } catch (Exception e) {
            throw new CommitException(e.getMessage(), e);
        }

        return true;
    }

    private DataModelMainPage getPage() {
        IEditorPart activeEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
        if (activeEditor instanceof XSDEditor) {
            XSDEditor xsdEditor = (XSDEditor) activeEditor;
            DataModelMainPage page = xsdEditor.getdMainPage();

            return page;
        }
        return null;
    }

    private String getModifiedXpath(DataModelMainPage page) {
        if (page != null) {
            XSDGetXPathAction getXpathAction = new XSDGetXPathAction(page);
            getXpathAction.doAction();
            String originalXpath = getXpathAction.getCopiedXpath();
            return originalXpath;
        }

        return null;
    }

    private void updateReference(String originalName) {
        DataModelMainPage page = getPage();
        String modifiedXpath = getModifiedXpath(page);
        if (modifiedXpath != null && !modifiedXpath.isEmpty()) {
            int lastIndex = modifiedXpath.lastIndexOf("/"); //$NON-NLS-1$

            String originalXpath = originalName;
            originalXpath = modifiedXpath.substring(0, lastIndex + 1) + originalXpath;
            IStructuredContentProvider provider = (IStructuredContentProvider) page.getTreeViewer().getContentProvider();
            Object[] allForeignKeyRelatedInfos = Util.getAllForeignKeyRelatedInfos(page.getSite(), new ArrayList<Object>(),
                    provider, new HashSet<Object>());
            Util.updateForeignKeyRelatedInfo(originalXpath, modifiedXpath, allForeignKeyRelatedInfos);

        }
    }

    private void validateCardinality() throws CommitValidationException {
        int newMinOcur = getCommitedObj().getNewMinOcur();
        if (newMinOcur < 0) {
            throw new CommitValidationException(Messages.ElementWrapperCommitHandler_MinValidExceptionInfo);
        }

        int newMaxOcur = getCommitedObj().getNewMaxOcur();
        if (newMaxOcur > -1 && newMaxOcur < newMinOcur) {
            throw new CommitValidationException(Messages.ElementWrapperCommitHandler_ManValidExceptionInfo);
        }
    }

    private void validateElementNameAndReference() throws CommitValidationException {

        if (((getCommitedObj().getNewName() == null) || ("".equals(getCommitedObj().getNewName())))//$NON-NLS-1$
                && ((getCommitedObj().getNewReference() == null) || "".equals(getCommitedObj().getNewReference()))) {
            throw new CommitValidationException(Messages.ElementWrapperCommitHandler_BusinessElementCannotbeEmpty);
        }

        if (getCommitedObj().getNewName().replaceAll("\\s", "").length() != getCommitedObj().getNewName().length()) {
            throw new CommitValidationException(Messages.ElementWrapperCommitHandler_BusinessElementCannotContainEmpty);
        }

        if (getCommitedObj().hasNewReference()
                && (Util.findReference(getCommitedObj().getNewReference(), getCommitedObj().getSchema()) == null)) {
            throw new CommitValidationException(Messages.ElementWrapperCommitHandler_ReferenceElementExceptionInfo
                    + getCommitedObj().getNewReference() + Messages.ElementWrapperCommitHandler_ReferenceElementExceptionInfoA);
        }

    }
}
