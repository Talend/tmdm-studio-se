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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.graphics.Image;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDAttributeGroupDefinition;
import org.eclipse.xsd.XSDAttributeUse;
import org.eclipse.xsd.XSDAttributeUseCategory;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDCompositor;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDFacet;
import org.eclipse.xsd.XSDIdentityConstraintCategory;
import org.eclipse.xsd.XSDIdentityConstraintDefinition;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDWildcard;
import org.eclipse.xsd.XSDXPathDefinition;
import org.eclipse.xsd.XSDXPathVariety;
import org.w3c.dom.Element;

import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.utils.Util;

public class SchemaItemImageCreator {

    private static Log log = LogFactory.getLog(SchemaItemImageCreator.class);

    private static SchemaItemImageCreator INSTANCE;

    protected SchemaItemImageCreator() {
    }

    public static synchronized SchemaItemImageCreator getInstance() {

        if (INSTANCE == null)
            INSTANCE = new SchemaItemImageCreator();

        return INSTANCE;
    }

    protected Image getImageForUnknown() {
        return ImageCache.getCreatedImage(EImage.SMALL_WARN.getPath());
    }

    protected Image getImageForXSDElementDeclaration(XSDElementDeclaration element) {

        for (XSDIdentityConstraintDefinition eachIDConsDef : element.getIdentityConstraintDefinitions()) {
            if (eachIDConsDef.getIdentityConstraintCategory().equals(XSDIdentityConstraintCategory.UNIQUE_LITERAL))
                return ImageCache.getCreatedImage(EImage.CONCEPT.getPath());
        }

        return ImageCache.getCreatedImage(EImage.ELEMENT_ONLY.getPath());
    }

    protected Image getImageForXSDParticle(XSDParticle element) {

        if (element.getTerm() instanceof XSDElementDeclaration)
            return getImageForXSDParticle_Term_XSDElementDeclaration(element, (XSDElementDeclaration) element.getTerm());

        if (element.getTerm() instanceof XSDModelGroup)
            return getImageForXSDParticle_Term_XSDModelGroup(element, (XSDModelGroup) element.getTerm());

        if (element.getTerm() instanceof XSDWildcard)
            return getImageForXSDParticle_Term_XSDWildcard();

        return getImageForXSDParticle_WrongStatus(element);
    }

    protected Image getImageForXSDParticle_Term_XSDElementDeclaration(XSDParticle element, XSDElementDeclaration term) {
        // get Type of Parent Group
        if (Util.getKeyInfo(term) != null && Util.getKeyInfo(term).size() > 0)
            return ImageCache.getCreatedImage(EImage.PRIMARYKEY.getPath());

        if (element.getContainer() instanceof XSDModelGroup)
            return ImageCache.getCreatedImage(EImage.SCHEMAELEMENT.getPath());

        return getImageForXSDParticle_WrongStatus(element);
    }

    protected Image getImageForXSDParticle_Term_XSDModelGroup(XSDParticle element, XSDModelGroup term) {

        int type = term.getCompositor().getValue();
        switch (type) {
        case XSDCompositor.ALL:
            return ImageCache.getCreatedImage(EImage.COMPLEX_ALL.getPath());
        case XSDCompositor.CHOICE:
            return ImageCache.getCreatedImage(EImage.COMPLEX_CHOICE.getPath());
        case XSDCompositor.SEQUENCE:
            return ImageCache.getCreatedImage(EImage.COMPLEX_SEQUENCE.getPath());
        default:
            return getImageForXSDParticle_WrongStatus(element);
        }

    }

    protected Image getImageForXSDParticle_Term_XSDWildcard() {
        return ImageCache.getCreatedImage(EImage.WILDCARD.getPath());
    }

    protected Image getImageForXSDParticle_WrongStatus(XSDParticle element) {
        log.info(Messages.bind(Messages.SchemaItemImageCreator_Loginfo, element.getTerm().getClass().getName()));
        return ImageCache.getCreatedImage(EImage.ERROR.getPath());
    }

    protected Image getImageForXSDSimpleTypeDefinition() {
        return ImageCache.getCreatedImage(EImage.SIMPLETYPE.getPath());
    }

    protected Image getImageForXSDComplexTypeDef(XSDComplexTypeDefinition element) {

        if (element.getContent() instanceof XSDParticle)
            return getImageForXSDComplexTypeDef_Cotent_XSDParticle((XSDParticle) element.getContent());

        return getImageForXSDComplexTypeDef_WrangStatus(element);
    }

    protected Image getImageForXSDComplexTypeDef_Cotent_XSDParticle(XSDParticle content) {

        if (content.getTerm() instanceof XSDModelGroup) {
            int type = ((XSDModelGroup) content.getTerm()).getCompositor().getValue();
            switch (type) {
            case XSDCompositor.ALL:
                return ImageCache.getCreatedImage(EImage.COMPLEX_ALL.getPath());
            case XSDCompositor.CHOICE:
                return ImageCache.getCreatedImage(EImage.COMPLEX_CHOICE.getPath());
            case XSDCompositor.SEQUENCE:
                return ImageCache.getCreatedImage(EImage.COMPLEX_SEQUENCE.getPath());
            }
        }

        return getImageForUnknown();
    }

    protected Image getImageForXSDComplexTypeDef_WrangStatus(XSDComplexTypeDefinition element) {
        log.info(Messages.bind(Messages.SchemaItemImageCreator_Loginfo1, element.getContent().getClass().getName()));
        return ImageCache.getCreatedImage(EImage.ERROR.getPath());
    }

    protected Image getImageForXSDModelGroup(XSDModelGroup element) {

        switch (element.getCompositor().getValue()) {
        case XSDCompositor.ALL:
            return ImageCache.getCreatedImage(EImage.COMPLEX_ALL.getPath());
        case XSDCompositor.CHOICE:
            return ImageCache.getCreatedImage(EImage.COMPLEX_CHOICE.getPath());
        case XSDCompositor.SEQUENCE:
            return ImageCache.getCreatedImage(EImage.COMPLEX_SEQUENCE.getPath());
        default:
            return getImageForUnknown();
        }
    }

    protected Image getImageForXSDFacet() {
        return ImageCache.getCreatedImage(EImage.FACET.getPath());
    }

    protected Image getImageForXSDIdentityConstraintDef(XSDIdentityConstraintDefinition element) {

        if (element.getIdentityConstraintCategory().equals(XSDIdentityConstraintCategory.UNIQUE_LITERAL))
            return ImageCache.getCreatedImage(EImage.KEYS.getPath());
        return ImageCache.getCreatedImage(EImage.PRIMARYKEY.getPath());
    }

    protected Image getImageForXSDXPathDefinition(XSDXPathDefinition element) {

        if (element.getVariety().equals(XSDXPathVariety.FIELD_LITERAL))
            return ImageCache.getCreatedImage(EImage.FIELD.getPath());
        return ImageCache.getCreatedImage(EImage.SELECTOR.getPath());
    }

    protected Image getImageForXSDAttributeGroupDef() {
        return ImageCache.getCreatedImage(EImage.ATTRIBUTE_GROUP.getPath());
    }

    protected Image getImageForXSDAttributeUse(XSDAttributeUse element) {

        if ("xmlns".equals(element.getAttributeDeclaration().getTargetNamespace()))//$NON-NLS-1$
            return ImageCache.getCreatedImage(EImage.ANNOTATION.getPath());

        if (XSDAttributeUseCategory.REQUIRED_LITERAL.equals(element.getUse()))
            return ImageCache.getCreatedImage(EImage.ATTRIBUTE_MANDATORY.getPath());

        return ImageCache.getCreatedImage(EImage.ATTRIBUTE.getPath());
    }

    protected Image getImageForXSDAnnotation() {
        return ImageCache.getCreatedImage(EImage.ANNOTATION.getPath());
    }

    protected Image getImageForElement(Element element) {

        try {
            if ("documentation".equals(element.getLocalName()))//$NON-NLS-1$
                return ImageCache.getCreatedImage(EImage.DOCUMENTATION.getPath());

            if ("appinfo".equals(element.getLocalName())) {//$NON-NLS-1$
                String source = element.getAttribute("source");//$NON-NLS-1$
                if (source != null) {
                    if (source.startsWith("X_Label_"))//$NON-NLS-1$
                        return ImageCache.getCreatedImage(EImage.LABEL.getPath());

                    if (source.equals("X_ForeignKey"))//$NON-NLS-1$
                        return ImageCache.getCreatedImage(EImage.PRIMARYKEY.getPath());

                    if (source.equals("X_ForeignKeyInfo"))//$NON-NLS-1$
                        return ImageCache.getCreatedImage(EImage.KEYINFO.getPath());

                    if (source.equals("X_Retrieve_FKinfos"))//$NON-NLS-1$
                        return ImageCache.getCreatedImage(EImage.KEYINFO.getPath());

                    if (source.equals("X_FKIntegrity"))//$NON-NLS-1$
                        return ImageCache.getCreatedImage(EImage.KEYINFO.getPath());

                    if (source.equals("X_FKIntegrity_Override"))//$NON-NLS-1$
                        return ImageCache.getCreatedImage(EImage.KEYINFO.getPath());

                    if (source.equals("X_SourceSystem"))//$NON-NLS-1$
                        return ImageCache.getCreatedImage(EImage.SOURCESYSTEM.getPath());

                    if (source.equals("X_TargetSystem"))//$NON-NLS-1$
                        return ImageCache.getCreatedImage(EImage.TARGETSYSTEM.getPath());

                    if (source.startsWith("X_Description_"))//$NON-NLS-1$
                        return ImageCache.getCreatedImage(EImage.DOCUMENTATION.getPath());

                    if (source.equals("X_Write"))//$NON-NLS-1$
                        return ImageCache.getCreatedImage(EImage.SECURITYANNOTATION.getPath());

                    if (source.equals("X_Lookup_Field"))//$NON-NLS-1$
                        return ImageCache.getCreatedImage(EImage.BROWSE.getPath());

                    if (source.equals("X_Hide"))//$NON-NLS-1$
                        return ImageCache.getCreatedImage(EImage.SECURITYANNOTATION.getPath());

                    if (source.equals("X_Schematron"))//$NON-NLS-1$
                        return ImageCache.getCreatedImage(EImage.ROUTINE.getPath());

                    if (source.equals("X_ForeignKey_Filter"))//$NON-NLS-1$
                        return ImageCache.getCreatedImage(EImage.FILTER_PS.getPath());

                    if (source.startsWith("X_Display_Format_"))//$NON-NLS-1$
                        return ImageCache.getCreatedImage(EImage.THIN_MIN_VIEW.getPath());
                }
            }

            return ImageCache.getCreatedImage(EImage.DOCUMENTATION.getPath());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return getImageForUnknown();
    }

    public Image getImage(Object obj) {

        if (obj instanceof XSDElementDeclaration)
            return getImageForXSDElementDeclaration((XSDElementDeclaration) obj);

        if (obj instanceof XSDParticle)
            return getImageForXSDParticle((XSDParticle) obj);

        if (obj instanceof XSDSimpleTypeDefinition)
            return getImageForXSDSimpleTypeDefinition();

        if (obj instanceof XSDComplexTypeDefinition)
            return getImageForXSDComplexTypeDef((XSDComplexTypeDefinition) obj);

        if (obj instanceof XSDModelGroup)
            return getImageForXSDModelGroup((XSDModelGroup) obj);

        if (obj instanceof XSDFacet)
            return getImageForXSDFacet();

        if (obj instanceof XSDIdentityConstraintDefinition)
            return getImageForXSDIdentityConstraintDef((XSDIdentityConstraintDefinition) obj);

        if (obj instanceof XSDXPathDefinition)
            return getImageForXSDXPathDefinition((XSDXPathDefinition) obj);

        if (obj instanceof XSDAttributeGroupDefinition)
            return getImageForXSDAttributeGroupDef();

        if (obj instanceof XSDAttributeUse)
            return getImageForXSDAttributeUse((XSDAttributeUse) obj);

        if (obj instanceof XSDAnnotation)
            return getImageForXSDAnnotation();

        if (obj instanceof Element)
            return getImageForElement((Element) obj);

        return getImageForUnknown();
    }

}
