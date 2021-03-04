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
package com.amalto.workbench.providers.datamodel.util;

import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDIdentityConstraintDefinition;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDTerm;

abstract class SchemaItemRoleSelectorOnNotAll implements SchemaItemRoleSelector {

    public boolean isSatisfiedItem(String role, Object parentElement, Object element) {

        if (parentElement instanceof XSDParticle)
            return justifyParentXSDParticle((XSDParticle) parentElement, element);

        if (parentElement instanceof XSDElementDeclaration)
            return justifyParentXSDElementDeclaration((XSDElementDeclaration) parentElement, element);

        if (parentElement instanceof XSDIdentityConstraintDefinition)
            return justifyParentIDConstraintDef((XSDIdentityConstraintDefinition) parentElement, element);

        if (parentElement instanceof XSDAnnotation)
            return justifyParentXSDAnnotation((XSDAnnotation) parentElement, element);

        if (element instanceof XSDElementDeclaration)
            return justifyRootXSDElementDeclaration((XSDElementDeclaration) element, role);

        if (element instanceof XSDParticle)
            return justifyParticleElement((XSDParticle) element, role);

        return true;
    }

    protected boolean justifyRootXSDElementDeclaration(XSDElementDeclaration element, String role) {
        return isRoleValid(role, element.getAnnotation());
    }

    protected boolean justifyParentXSDAnnotation(XSDAnnotation parentElement, Object element) {

        return !(parentElement.getUserInformation().contains(element) || parentElement.getApplicationInformation().contains(
                element));
    }

    protected boolean justifyParentIDConstraintDef(XSDIdentityConstraintDefinition parentElement, Object element) {

        return !(element.equals(parentElement.getSelector()) || parentElement.getFields().contains(element));
    }

    protected boolean justifyParentXSDElementDeclaration(XSDElementDeclaration parentElement, Object element) {

        return !(parentElement.getIdentityConstraintDefinitions().contains(element) || element.equals(parentElement
                .getAnnotation()));
    }

    protected boolean justifyParticleElement(XSDParticle element, String role) {

        if (element.getTerm() instanceof XSDElementDeclaration) {
            XSDAnnotation annotation = ((XSDElementDeclaration) element.getTerm()).getAnnotation();
            return isRoleValid(role, annotation);
        } else
            return false;

    }

    protected boolean justifyParentXSDParticle(XSDParticle parentElement, Object element) {

        XSDTerm term = ((XSDParticle) parentElement).getTerm();
        if (term instanceof XSDElementDeclaration)
            return !(element.equals(((XSDElementDeclaration) term).getAnnotation()));

        return true;
    }

    protected abstract boolean isRoleValid(String role, XSDAnnotation annotation);

}
