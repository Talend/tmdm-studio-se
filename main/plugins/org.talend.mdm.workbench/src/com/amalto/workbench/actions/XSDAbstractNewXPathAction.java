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

import org.eclipse.emf.common.util.EList;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDIdentityConstraintDefinition;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.impl.XSDParticleImpl;

import com.amalto.workbench.editors.DataModelMainPage;


/**
 * created by liusongbo on 2013-5-23
 */
public abstract class XSDAbstractNewXPathAction extends UndoAction {

    public XSDAbstractNewXPathAction(DataModelMainPage page) {
        super(page);
    }

    protected void updateElementForAddedfield(XSDIdentityConstraintDefinition icd, String fieldName) {
        if (icd == null || fieldName == null)
            return;

        XSDElementDeclaration entity = (XSDElementDeclaration) icd.getContainer();

        XSDComplexTypeDefinition ctype = (XSDComplexTypeDefinition) entity.getTypeDefinition();
        if (ctype.getContent() instanceof XSDParticle) {
            XSDParticleImpl particle = (XSDParticleImpl) ctype.getContent();
            if (particle.getTerm() instanceof XSDModelGroup) {
                XSDModelGroup group = (XSDModelGroup) particle.getTerm();
                EList<XSDParticle> particles = group.getParticles();
                for (XSDParticle part : particles) {
                    if (part.getTerm() instanceof XSDElementDeclaration) {
                        XSDElementDeclaration el = (XSDElementDeclaration) part.getTerm();
                        if (el.getTypeDefinition() instanceof XSDSimpleTypeDefinition) {
                            if (fieldName.equals(el.getName())) {
                                part.setMinOccurs(1);
                                part.setMaxOccurs(1);
                                break;
                            }
                        }
                    }
                }
            }
        }

        entity.updateElement();
    }

}
