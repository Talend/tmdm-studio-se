// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.workbench.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.xsd.XSDComplexTypeContent;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDParticleContent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaContent;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTerm;
import org.eclipse.xsd.XSDTypeDefinition;

import com.amalto.workbench.dialogs.datamodel.IXPathSelectionFilter;

/**
 * @author sbliu
 *
 */
public class SortFieldSelectionFilter implements IXPathSelectionFilter {

    public FilterResult check(Object obj) {
        if (obj instanceof XSDParticle) {
            if (!isFirstLevelChild((XSDParticle) obj)) {
                return FilterResult.DISABLE;
            }
            XSDParticle particle = (XSDParticle) obj;
            int maxOccurs = particle.getMaxOccurs();
            if (maxOccurs > 1 || maxOccurs == -1) {
                return FilterResult.DISABLE;
            }

            XSDTerm term = particle.getTerm();
            if (term instanceof XSDElementDeclaration) {
                XSDElementDeclaration element = ((XSDElementDeclaration) term);
                XSDTypeDefinition type = element.getType();
                if (type instanceof XSDSimpleTypeDefinition) {
                    return FilterResult.ENABLE;
                }
            }
        }
        return FilterResult.DISABLE;
    }

    private boolean isFirstLevelChild(XSDParticle particle) {
        XSDSchema schema = (XSDSchema) particle.getRootContainer();

        EList<XSDSchemaContent> contents = schema.getContents();
        for (XSDSchemaContent content : contents) {
            if (content instanceof XSDElementDeclaration) {
                XSDElementDeclaration concept = (XSDElementDeclaration) content;
                XSDComplexTypeDefinition ctypeDef = (XSDComplexTypeDefinition) concept.getTypeDefinition();
                List<XSDComplexTypeDefinition> ctypes = new ArrayList<XSDComplexTypeDefinition>();
                ctypes.add(ctypeDef);
                while (ctypeDef.getBaseTypeDefinition() != ((XSDComplexTypeDefinition) ctypeDef.getBaseTypeDefinition())
                        .getBaseTypeDefinition()) {
                    ctypes.add((XSDComplexTypeDefinition) ctypeDef.getBaseTypeDefinition());
                    ctypeDef = (XSDComplexTypeDefinition) ctypeDef.getBaseTypeDefinition();
                }

                for (XSDComplexTypeDefinition ctype : ctypes) {
                    XSDComplexTypeContent ctypeContent = ctype.getContent();
                    if (ctypeContent instanceof XSDParticle) {
                        XSDParticle typeParticle = (XSDParticle) ctypeContent;
                        XSDParticleContent particleContent = typeParticle.getContent();
                        if (particleContent instanceof XSDModelGroup) {
                            XSDModelGroup particleGroup = (XSDModelGroup) particleContent;
                            if (particleGroup.getContents().contains(particle)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

}
