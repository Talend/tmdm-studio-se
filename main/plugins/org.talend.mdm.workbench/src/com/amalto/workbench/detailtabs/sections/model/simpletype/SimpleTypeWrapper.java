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
package com.amalto.workbench.detailtabs.sections.model.simpletype;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;

import com.amalto.workbench.detailtabs.sections.handlers.CommitHandler;
import com.amalto.workbench.detailtabs.sections.handlers.SimpleTypeWrapperCommitHandler;
import com.amalto.workbench.detailtabs.sections.model.ISubmittable;
import com.amalto.workbench.editors.DataModelMainPage;
import com.amalto.workbench.editors.xsdeditor.XSDEditor;
import com.amalto.workbench.utils.Util;

public class SimpleTypeWrapper implements ISubmittable {

    private Map<String, Object> facetName2Value = new HashMap<String, Object>();

    private String newTypeName;

    private String newBaseTypeName;

    private XSDSimpleTypeDefinition newBaseType;

    private XSDSimpleTypeDefinition xsdSimpleType;

    public SimpleTypeWrapper(XSDSimpleTypeDefinition xsdSimpleType, String newTypeName, XSDSimpleTypeDefinition newBaseType,
            String newBaseTypeName, Map<String, Object> facetName2Value) {
        this.xsdSimpleType = xsdSimpleType;
        this.facetName2Value = facetName2Value;
        this.newTypeName = newTypeName;
        this.newBaseType = newBaseType;
        this.newBaseTypeName = newBaseTypeName;
    }

    public XSDSimpleTypeDefinition getXSDSimpleType() {
        return xsdSimpleType;
    }

    public String getOldTypeName() {
        return xsdSimpleType.getName();
    }

    public Object getFacetValue(String facetName) {
        return facetName2Value.get(facetName);
    }

    public String[] getFacetNames() {
        return facetName2Value.keySet().toArray(new String[0]);
    }

    public String getNewTypeName() {
        return newTypeName;
    }

    public String getNewBaseTypeName() {
        return newBaseTypeName;
    }

    public XSDSimpleTypeDefinition getNewBaseType() {
        return newBaseType;
    }

    public CommitHandler<SimpleTypeWrapper> createCommitHandler() {
        return new SimpleTypeWrapperCommitHandler(this);
    }

    public XSDSchema getSchema() {
        return xsdSimpleType.getSchema();
    }

    public boolean changeTypeName() {
        if (getNewTypeName().trim().equals(getOldTypeName()))
            return false;

        xsdSimpleType.setName(getNewTypeName().trim());
        xsdSimpleType.updateElement();

        IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        IEditorPart activeEditor = activePage.getActiveEditor();
        if(activeEditor instanceof XSDEditor) {
            XSDEditor editor = (XSDEditor) activeEditor;
            DataModelMainPage page = editor.getdMainPage();
            IStructuredContentProvider provider = (IStructuredContentProvider) page.getSchemaContentProvider();

            Util.updateReferenceToXSDTypeDefinition(page.getSite(), xsdSimpleType, provider);
        }

        return true;
    }

    public boolean isBaseTypeExists() {
        return newBaseTypeName.equals(newBaseType.getName());
    }
}
