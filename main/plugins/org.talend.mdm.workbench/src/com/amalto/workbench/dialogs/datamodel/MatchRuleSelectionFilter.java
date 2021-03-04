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
package com.amalto.workbench.dialogs.datamodel;

import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTerm;
import org.eclipse.xsd.XSDTypeDefinition;

/**
 * created by HHB on 2013-8-14 Eligible elements are:
 *
 * simple type (extension or restriction ok), at any depth, for instance: Customer/Firstname, or Customer/Address/City
 * the selected element must be maxOccurs <= 1 no element in the path to that element must be maxOccurs > 1 e.g. in
 * Customer/Address/City, Address must be maxOccurs <= 1
 *
 *
 */
public class MatchRuleSelectionFilter implements IXPathSelectionFilter {

    public FilterResult check(Object obj) {
        if (obj instanceof XSDParticle) {
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

}
