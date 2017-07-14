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
package com.amalto.workbench.models.sync;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDTerm;
import org.w3c.dom.Element;

/**
 * created by hbhong on Jul 12, 2017 Detailled comment
 *
 */
public class AnnotationSyncProcessor {

    private static final String SOURCE = "source"; //$NON-NLS-1$

    private static final String APPINFO = "appinfo"; //$NON-NLS-1$

    public static XSDAnnotation getAnnotation(XSDConcreteComponent component) {

        if (component instanceof XSDElementDeclaration) {
            XSDElementDeclaration declaration = (XSDElementDeclaration) component;
            return declaration.getAnnotation();

        }
        if (component instanceof XSDComplexTypeDefinition) {
            XSDComplexTypeDefinition complexTypeDef = (XSDComplexTypeDefinition) component;
            return complexTypeDef.getAnnotation();

        }
        if (component instanceof XSDModelGroup) {
            XSDModelGroup group = (XSDModelGroup) component;
            if (group.getContainer().getContainer() instanceof XSDComplexTypeDefinition) {
                XSDComplexTypeDefinition complexTypeDef = (XSDComplexTypeDefinition) group.getContainer().getContainer();
                return complexTypeDef.getAnnotation();

            }
        }

        if (component instanceof XSDParticle) {
            XSDTerm term = ((XSDParticle) component).getTerm();
            if (term instanceof XSDElementDeclaration) {
                XSDElementDeclaration declaration = (XSDElementDeclaration) term;
                return declaration.getAnnotation();
            }
        }
        return null;
    }

    public static List<Element> getAppInfos(XSDAnnotation xsdAnnotation, String regex) {
        List<Element> annotations = new ArrayList<Element>();
        if (xsdAnnotation != null) {
            EList<Element> elements = xsdAnnotation.getApplicationInformation();
            if (elements != null) {
                for (Element element : elements) {
                    String name = element.getLocalName();
                    if (APPINFO.equals(name.toLowerCase())) {
                        String key = element.getAttribute(SOURCE);
                        if (key.matches(regex)) {
                            annotations.add(element);
                        }
                    }
                }
            }
        }
        return annotations;
    }

    public static Element getAppInfo(XSDAnnotation xsdAnnotation, String key) {
        if (xsdAnnotation != null) {
            EList<Element> elements = xsdAnnotation.getApplicationInformation();
            if (elements != null) {

                for (Element element : elements) {
                    String name = element.getLocalName();
                    if (APPINFO.equals(name.toLowerCase())) {
                        if (element.getAttribute(SOURCE).equals(key)) {

                            return element;
                        }
                    }
                }
            }
        }
        return null;
    }

    public static void updateAppInfo(Element element, String value) {
        if (element != null) {
            element.getFirstChild().setNodeValue(value);
        }
    }
}
