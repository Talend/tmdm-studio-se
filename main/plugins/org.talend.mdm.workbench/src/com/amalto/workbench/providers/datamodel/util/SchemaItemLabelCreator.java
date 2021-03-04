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

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDAttributeGroupDefinition;
import org.eclipse.xsd.XSDAttributeUse;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDFacet;
import org.eclipse.xsd.XSDIdentityConstraintDefinition;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDVariety;
import org.eclipse.xsd.XSDXPathDefinition;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;

import com.amalto.workbench.utils.Util;

public class SchemaItemLabelCreator {

    private static Log log = LogFactory.getLog(SchemaItemLabelCreator.class);

    protected static SchemaItemLabelCreator INSTANCE;

    public static final String XSDELEMENTDECLARATION_SUFFIX_ABSTRACT = "   (abstract)";//$NON-NLS-1$

    public static final String XSDELEMENTDECLARATION_SEPARATOR_TARGETNAMESPACE = " : ";//$NON-NLS-1$

    public static final String XSDPARTICLE_MULTIPLICITY_MANY = "many";//$NON-NLS-1$

    public static final String XSDPARTICLE_MULTIPLICITY_STARTTAG = "[";//$NON-NLS-1$

    public static final String XSDPARTICLE_MULTIPLICITY_ENDTAG = "]";//$NON-NLS-1$

    public static final String XSDPARTICLE_MULTIPLICITY_SEPARATOR = "...";//$NON-NLS-1$

    public static final String XSDPARTICLE_PREFIX_DEFAULT = "[Any]";//$NON-NLS-1$

    public static final String XSDPARTICLE_SEPARATOR = " ";//$NON-NLS-1$

    public static final String XSDSIMPLETYPEDEF_SEPARATOR = " : ";//$NON-NLS-1$

    public static final String XSDANNOTATION_LABEL_DEFAULT = "Annotations";//$NON-NLS-1$

    public static final String XSDSIMPLETYPEDEF_PREFIX_NOTKNOWN = "";//$NON-NLS-1$

    public static final String XSDMODELGROUP_LABEL_NONNAME = "anonymous type ";//$NON-NLS-1$

    public static final String XSDCOMPLEXTYPEDEF_SEPARATOR = " : ";//$NON-NLS-1$

    protected SchemaItemLabelCreator() {
    }

    public synchronized static SchemaItemLabelCreator getInstance() {

        if (INSTANCE == null)
            INSTANCE = new SchemaItemLabelCreator();

        return INSTANCE;
    }

    public String getLabel(Object schemaElement) {

        if (schemaElement == null)
            return "NULL";//$NON-NLS-1$

        return getLabelForNotNullElement(schemaElement);

    }

    protected String getLabelForNotNullElement(Object element) {

        if (element instanceof XSDElementDeclaration)
            return getLabelForXSDElementDeclaration((XSDElementDeclaration) element);

        if (element instanceof XSDParticle)
            return getLabelForXSDParticle((XSDParticle) element);

        if (element instanceof XSDSimpleTypeDefinition)
            return getSimpleTypeDefinition((XSDSimpleTypeDefinition) element);

        if (element instanceof XSDComplexTypeDefinition)
            return getLabelForComplexTypeDef((XSDComplexTypeDefinition) element);

        if (element instanceof XSDModelGroup)
            return getLabelForXSDModelGroup((XSDModelGroup) element);

        if (element instanceof XSDFacet)
            return getLabelForXSDFacet((XSDFacet) element);

        if (element instanceof XSDIdentityConstraintDefinition)
            return getLabelForXSDIDConDef((XSDIdentityConstraintDefinition) element);

        if (element instanceof XSDXPathDefinition)
            return getLabelForXSDXPathDefinition((XSDXPathDefinition) element);

        if (element instanceof XSDAttributeGroupDefinition)
            return getLabelForXSDAttriGroupDef((XSDAttributeGroupDefinition) element);

        if (element instanceof XSDAttributeUse)
            return getLabelForXSDAttributeUse((XSDAttributeUse) element);

        if (element instanceof XSDAnnotation)
            return getLabelForAnnotation((XSDAnnotation) element);

        if (element instanceof Element)
            return getLableForElement((Element) element);

        return "?? " + element.getClass().getName() + " : " + element.toString();//$NON-NLS-1$//$NON-NLS-2$

    }

    protected String getLabelForXSDElementDeclaration(XSDElementDeclaration element) {

        StringBuilder label = new StringBuilder(element.getName());

        if (element.isAbstract())
            label.append(XSDELEMENTDECLARATION_SUFFIX_ABSTRACT);

        if (element.getTargetNamespace() != null)
            label.append(XSDELEMENTDECLARATION_SEPARATOR_TARGETNAMESPACE + element.getTargetNamespace());

        return label.toString();
    }

    protected String getLabelForXSDParticle(XSDParticle element) {

        if (element.getContent() instanceof XSDElementDeclaration)
            return getPrefixForXSDParticle_XSDElementDeclaration(element, (XSDElementDeclaration) element.getContent())
                    + XSDPARTICLE_SEPARATOR + getMulticityLabelForXSDParticle(element);

        if (element.getContent() instanceof XSDElementDeclaration) {
            return getPrefixForXSDParticle_XSDModelGroup(element) + XSDPARTICLE_SEPARATOR
                    + getMulticityLabelForXSDParticle(element);
        }

        return XSDPARTICLE_PREFIX_DEFAULT + XSDPARTICLE_SEPARATOR + getMulticityLabelForXSDParticle(element);

    }

    protected String getPrefixForXSDParticle_XSDElementDeclaration(XSDParticle element, XSDElementDeclaration content) {

        StringBuilder prefix = new StringBuilder();

        if (content.getName() != null)
            prefix.append(content.getName());

        if (content.getTypeDefinition() == null)
            prefix.append(" [" + ((XSDElementDeclaration) element.getTerm()).getName() + "]");//$NON-NLS-1$//$NON-NLS-2$

        return prefix.toString();
    }

    protected String getPrefixForXSDParticle_XSDModelGroup(XSDParticle element) {

        StringBuilder prefix = new StringBuilder();

        if (element.getContainer() instanceof XSDComplexTypeDefinition) {
            String ctdName = ((XSDComplexTypeDefinition) element.getContainer()).getName();
            if (ctdName != null)
                prefix.append(ctdName);
        }

        return prefix.toString();
    }

    protected String getMulticityLabelForXSDParticle(XSDParticle element) {

        StringBuilder multicityLabel = new StringBuilder();

        if (!((element.getMinOccurs() == 1) && (element.getMaxOccurs() == 1))) {
            multicityLabel.append(XSDPARTICLE_MULTIPLICITY_STARTTAG);
            multicityLabel.append(element.getMinOccurs());
            multicityLabel.append(XSDPARTICLE_MULTIPLICITY_SEPARATOR);

            if (element.getMaxOccurs() == -1)
                multicityLabel.append(XSDPARTICLE_MULTIPLICITY_MANY);
            else
                multicityLabel.append(element.getMaxOccurs());

            multicityLabel.append(XSDPARTICLE_MULTIPLICITY_ENDTAG);
        }

        return multicityLabel.toString();
    }

    protected String getSimpleTypeDefinition(XSDSimpleTypeDefinition element) {

        return getPrefixForXSDSimpleTypeDefinition(element) + getSuffixForXSDSimpleTypeDefinition(element);
    }

    protected String getPrefixForXSDSimpleTypeDefinition(XSDSimpleTypeDefinition element) {

        String s = "";//$NON-NLS-1$
        if (element == null) {
        } else if (element.getEffectiveEnumerationFacet() != null) {
            s += element.getName() != null ? element.getName() : element.getBaseTypeDefinition().getName();
        } else if (element.getElement() != null && element.getElement().hasAttribute(XSDConstants.ID_ATTRIBUTE)) {
            s += element.getName();
        } else if ((XSDVariety.UNION_LITERAL == element.getVariety()) || (XSDVariety.LIST_LITERAL == element.getVariety())) {
            s += "(";//$NON-NLS-1$
            for (Iterator<?> members = element.getMemberTypeDefinitions().iterator(); members.hasNext();) {
                XSDSimpleTypeDefinition memberTypeDefinition = (XSDSimpleTypeDefinition) members.next();
                s += getSimpleTypeDefinition(memberTypeDefinition);
                if (members.hasNext()) {
                    s += " | ";//$NON-NLS-1$
                }
            }
            s += ")";//$NON-NLS-1$
            if (element.getMemberTypeDefinitions().isEmpty()) {
                s = element.getVariety() + "";//$NON-NLS-1$
            }
        } else if ((XSDVariety.UNION_LITERAL == element.getVariety()) | (XSDVariety.LIST_LITERAL == element.getVariety())) {
            s += "List of ";//$NON-NLS-1$
            s += getSimpleTypeDefinition(element.getItemTypeDefinition());
        } else if (element.getName() != null) {
            s += element.getName();
        } else if (element.getEffectivePatternFacet() != null) {
            s += "a restricted xpath expression";//$NON-NLS-1$
        } else {
            s += XSDSIMPLETYPEDEF_PREFIX_NOTKNOWN;
        }

        return s;
    }

    protected String getSuffixForXSDSimpleTypeDefinition(XSDSimpleTypeDefinition element) {

        if (element.getTargetNamespace() == null || XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001.equals(element.getTargetNamespace())
                || XSDConstants.SCHEMA_FOR_SCHEMA_URI_1999.equals(element.getTargetNamespace()))
            return "";//$NON-NLS-1$

        return XSDSIMPLETYPEDEF_SEPARATOR + element.getTargetNamespace();

    }

    protected String getLabelForXSDModelGroup(XSDModelGroup element) {

        XSDParticle particle = (XSDParticle) (element.getContainer());
        String name = ((XSDComplexTypeDefinition) particle.getContainer()).getName();
        if (name == null)
            name = XSDMODELGROUP_LABEL_NONNAME;

        name += getMulticityLabelForXSDParticle(particle);

        String tail = particle.getSchema().getTargetNamespace() == null ? "" : " : " + particle.getSchema().getTargetNamespace();//$NON-NLS-1$//$NON-NLS-2$
        return name + tail;
    }

    protected String getLabelForXSDFacet(XSDFacet element) {
        return element.getFacetName() + ": " + element.getLexicalValue();//$NON-NLS-1$
    }

    protected String getLabelForXSDIDConDef(XSDIdentityConstraintDefinition element) {
        return element.getName();
    }

    protected String getLabelForXSDXPathDefinition(XSDXPathDefinition element) {
        return element.getValue();
    }

    protected String getLabelForXSDAttriGroupDef(XSDAttributeGroupDefinition element) {

        StringBuilder label = new StringBuilder();

        if (element.getName() != null)
            label.append(element.getName());

        if (element.getContents().size() == 0)
            label.append(" [" + element.getResolvedAttributeGroupDefinition().getName() + "]");//$NON-NLS-1$//$NON-NLS-2$

        return label.toString();

    }

    protected String getLabelForXSDAttributeUse(XSDAttributeUse element) {

        String name = element.getAttributeDeclaration().getName();
        if (name == null)
            name = " [" + element.getAttributeDeclaration().getResolvedAttributeDeclaration().getName() + "]";//$NON-NLS-1$//$NON-NLS-2$
        return name;
    }

    protected String getLabelForAnnotation(XSDAnnotation element) {
        return XSDANNOTATION_LABEL_DEFAULT;
    }

    protected String getLableForElement(Element element) {
        try {
            if (element.getLocalName().equals("documentation")) {//$NON-NLS-1$
                return "Documentation: " + element.getChildNodes().item(0).getNodeValue();//$NON-NLS-1$
            } else if (element.getLocalName().equals("appinfo")) {//$NON-NLS-1$
                String source = element.getAttribute("source");//$NON-NLS-1$
                if (source != null) {
                    if (source.startsWith("X_Label_")) {//$NON-NLS-1$
                        return Util.iso2lang.get(source.substring(8).toLowerCase()) + " Label: "//$NON-NLS-1$
                                + element.getChildNodes().item(0).getNodeValue();
                    } else if (source.equals("X_ForeignKey")) {//$NON-NLS-1$
                        return "Foreign Key:  " + element.getChildNodes().item(0).getNodeValue();//$NON-NLS-1$
                    } else if (source.equals("X_ForeignKeyInfo")) {//$NON-NLS-1$
                        return "Foreign Key Info:  " + element.getChildNodes().item(0).getNodeValue();//$NON-NLS-1$
                    } else if (source.equals("X_SourceSystem")) {//$NON-NLS-1$
                        return "Source System:  " + element.getChildNodes().item(0).getNodeValue();//$NON-NLS-1$
                    } else if (source.equals("X_TargetSystem")) {//$NON-NLS-1$
                        return "Target System(s):  " + element.getChildNodes().item(0).getNodeValue();//$NON-NLS-1$
                    } else if (source.startsWith("X_Description_")) {//$NON-NLS-1$
                        return Util.iso2lang.get(source.substring(14).toLowerCase()) + " Description: "//$NON-NLS-1$
                                + element.getChildNodes().item(0).getNodeValue();
                    } else if (source.equals("X_Write")) {//$NON-NLS-1$
                        return "Writable By : " + element.getChildNodes().item(0).getNodeValue();//$NON-NLS-1$
                    } else if (source.equals("X_Lookup_Field")) {//$NON-NLS-1$
                        return "Look Field : " + element.getChildNodes().item(0).getNodeValue();//$NON-NLS-1$
                    } else if (source.equals("X_Workflow")) {//$NON-NLS-1$
                        return "Workflow access : " + element.getChildNodes().item(0).getNodeValue();//$NON-NLS-1$
                    } else if (source.equals("X_Hide")) {//$NON-NLS-1$
                        return "No Access to : " + element.getChildNodes().item(0).getNodeValue();//$NON-NLS-1$

                    } else if (source.startsWith("X_Facet")) {//$NON-NLS-1$
                        return source.substring(2, 7) + "_Msg_" + source.substring(8) + ": "//$NON-NLS-1$//$NON-NLS-2$
                                + element.getChildNodes().item(0).getNodeValue();

                    } else if (source.startsWith("X_Display_Format_")) {//$NON-NLS-1$
                        return source + ": " + element.getChildNodes().item(0).getNodeValue();//$NON-NLS-1$
                    } else if (source.equals("X_Schematron")) {//$NON-NLS-1$

                        String pattern = (String) element.getFirstChild().getUserData("pattern_name");//$NON-NLS-1$
                        if (pattern == null) {
                            Element el = Util.parse(element.getChildNodes().item(0).getNodeValue()).getDocumentElement();
                            if (el.getAttributes().getNamedItem("name") != null)//$NON-NLS-1$
                                pattern = el.getAttributes().getNamedItem("name").getTextContent();//$NON-NLS-1$
                        }
                        return "Validation Rule: " + (pattern == null ? "" : pattern);//$NON-NLS-1$//$NON-NLS-2$
                    } else if (source.equals("X_Retrieve_FKinfos")) {//$NON-NLS-1$
                        return "Foreign Key resolution:  " + element.getChildNodes().item(0).getNodeValue();//$NON-NLS-1$
                    } else if (source.equals("X_FKIntegrity")) {//$NON-NLS-1$
                        return "Foreign Key integrity:  " + element.getChildNodes().item(0).getNodeValue(); //$NON-NLS-1$
                    } else if (source.equals("X_FKIntegrity_Override")) {//$NON-NLS-1$
                        return "Foreign Key integrity override:  " + element.getChildNodes().item(0).getNodeValue(); //$NON-NLS-1$
                    }
                    if (source.equals("X_ForeignKey_Filter")) {//$NON-NLS-1$
                        return "Foreign Key Filter:  " + element.getChildNodes().item(0).getNodeValue();//$NON-NLS-1$
                    } else {
                        return source + ": " + Util.nodeToString(element);//$NON-NLS-1$
                    }
                } else {
                    return Util.nodeToString(element);
                }
            } else {
                return Util.nodeToString(element);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return "?? " + element.getClass().getName() + " : " + element.toString();//$NON-NLS-1$//$NON-NLS-2$
    }

    protected String getLabelForComplexTypeDef(XSDComplexTypeDefinition element) {

        StringBuilder label = new StringBuilder(element.getName());

        if (element.getTargetNamespace() != null)
            label.append(XSDCOMPLEXTYPEDEF_SEPARATOR + element.getTargetNamespace());

        return label.toString();
    }
}
