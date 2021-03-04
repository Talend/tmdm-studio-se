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
package com.amalto.workbench.detailtabs.sections.model.complextype;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDCompositor;
import org.eclipse.xsd.XSDDerivationMethod;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDParticle;

import com.amalto.workbench.detailtabs.sections.handlers.CommitHandler;
import com.amalto.workbench.detailtabs.sections.handlers.ComplexTypeWrapperCommitHandler;
import com.amalto.workbench.detailtabs.sections.model.ISubmittable;
import com.amalto.workbench.editors.DataModelMainPage;
import com.amalto.workbench.editors.xsdeditor.XSDEditor;
import com.amalto.workbench.utils.Util;

public class ComplexTypeWrapper implements ISubmittable {

    private String newTypeName;

    private XSDComplexTypeDefinition newExtends;

    private XSDCompositor newGroupType;

    private XSDComplexTypeDefinition curXSDComplexType;

    private boolean isAbstract;

    public ComplexTypeWrapper(XSDComplexTypeDefinition curXSDComplexType, String newTypeName,
            XSDComplexTypeDefinition newExtends, XSDCompositor newGroupType, boolean isAbstract) {

        this.curXSDComplexType = curXSDComplexType;
        this.newTypeName = newTypeName;
        this.newExtends = newExtends;
        this.newGroupType = newGroupType;
        this.isAbstract = isAbstract;
    }

    public boolean changeAbstract() {
        curXSDComplexType.setAbstract(isAbstract);
        curXSDComplexType.updateElement();
        return true;
    }

    public boolean changeExtends() {

        if ((newExtends == null && curXSDComplexType.getBaseType() == null)
                || (newExtends != null && newExtends.equals(curXSDComplexType.getBaseType())))
            return false;

        curXSDComplexType.setDerivationMethod(isDefaultExtends() ? null : XSDDerivationMethod.EXTENSION_LITERAL);
        curXSDComplexType.setBaseTypeDefinition(newExtends);
        curXSDComplexType.updateElement();

        return true;
    }

    public boolean changeGroupType() {

        XSDParticle groupParticle = (XSDParticle) curXSDComplexType.getContent();
        XSDModelGroup group = (XSDModelGroup) groupParticle.getContent();
        if (group.getCompositor().equals(newGroupType))
            return false;

        group.setCompositor(newGroupType);
        group.updateElement();

        return true;
    }

    public boolean changeTypeName() {

        if (curXSDComplexType.getName().equals(newTypeName))
            return false;

        curXSDComplexType.setName(newTypeName);
        curXSDComplexType.updateElement();

        IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        IEditorPart activeEditor = activePage.getActiveEditor();

        if (activeEditor instanceof XSDEditor) {
            XSDEditor editor = (XSDEditor) activeEditor;
            DataModelMainPage page = editor.getdMainPage();
            IStructuredContentProvider provider = (IStructuredContentProvider) page.getSchemaContentProvider();
            Util.updateReferenceToXSDTypeDefinition(page.getSite(), curXSDComplexType, provider);
        }

        return true;
    }

    public CommitHandler<?> createCommitHandler() {
        return new ComplexTypeWrapperCommitHandler(this);
    }

    public XSDComplexTypeDefinition getCurComplexType() {
        return curXSDComplexType;
    }

    public XSDComplexTypeDefinition getNewExtends() {
        return newExtends;
    }

    public XSDCompositor getNewGroupType() {
        return newGroupType;
    }

    public String getNewTypeName() {
        return newTypeName;
    }

    /**
     * Getter for isAbstract.
     *
     * @return the isAbstract
     */
    public boolean isAbstract() {
        return this.isAbstract;
    }

    public boolean isDefaultExtends() {

        return curXSDComplexType.getSchema()
                .resolveComplexTypeDefinition(curXSDComplexType.getSchema().getSchemaForSchemaNamespace(), "anyType")//$NON-NLS-1$
                .equals(newExtends);

    }
}
