// ============================================================================
//
// Copyright (C) 2006-2019 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.workbench.providers;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ITreePathLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.ViewerLabel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDAttributeGroupDefinition;
import org.eclipse.xsd.XSDAttributeUse;
import org.eclipse.xsd.XSDAttributeUseCategory;
import org.eclipse.xsd.XSDComplexTypeContent;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDCompositor;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDFacet;
import org.eclipse.xsd.XSDIdentityConstraintCategory;
import org.eclipse.xsd.XSDIdentityConstraintDefinition;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDParticleContent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTerm;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.XSDVariety;
import org.eclipse.xsd.XSDWildcard;
import org.eclipse.xsd.XSDXPathDefinition;
import org.eclipse.xsd.XSDXPathVariety;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;

import com.amalto.workbench.dialogs.datamodel.IXPathSelectionFilter;
import com.amalto.workbench.dialogs.datamodel.IXPathSelectionFilter.FilterResult;
import com.amalto.workbench.exadapter.ExAdapterManager;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.utils.FontUtils;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.utils.XSDUtil;

public class XSDTreeLabelProvider extends LabelProvider implements ITreePathLabelProvider, IColorProvider, IFontProvider {

    private static final Log LOG = LogFactory.getLog(XSDTreeLabelProvider.class);

    private final IXPathSelectionFilter filter;

    private static final Color COLOR_GREY = getColor(SWT.COLOR_DARK_GRAY);

    private static final Color COLOR_BLACK = getColor(SWT.COLOR_BLACK);

    private static FontData defaultFontData = JFaceResources.getDefaultFont().getFontData()[0];

    public static final Font FONT_BOLD = FontUtils.getBoldFont(new Font(Display.getDefault(), defaultFontData));

    private IXSDTreeLabelPoviderExAdapter exAdapter;

    private static Color getColor(int systemColorID) {
        Display display = Display.getCurrent();

        return display.getSystemColor(systemColorID);
    }

    public XSDTreeLabelProvider() {
        this(null);
    }

    public XSDTreeLabelProvider(IXPathSelectionFilter filter) {
        this.filter = filter;
        exAdapter = ExAdapterManager.getAdapter(this, IXSDTreeLabelPoviderExAdapter.class);
    }

    @Override
    public String getText(Object obj) {
        if (obj instanceof XSDElementDeclaration) {
            String name = ((XSDElementDeclaration) obj).getName();
            if (((XSDElementDeclaration) obj).isAbstract()) {
                name += Messages.XSDTreeLabelProvider_0;
            }
            String tail = ((XSDElementDeclaration) obj).getTargetNamespace() != null ? " : "//$NON-NLS-1$
                    + ((XSDElementDeclaration) obj).getTargetNamespace() : "";//$NON-NLS-1$
            return name + tail;
        }

        if (obj instanceof XSDParticle) {
            XSDParticle xsdParticle = (XSDParticle) obj;
            XSDParticleContent content = xsdParticle.getContent();
            XSDTerm xsdTerm = xsdParticle.getTerm();
            String name = "";//$NON-NLS-1$
            if (content instanceof XSDElementDeclaration) {
                XSDElementDeclaration decl = (XSDElementDeclaration) content;
                name += (decl.getName() == null ? "" : decl.getName());//$NON-NLS-1$
                if (decl.getTypeDefinition() == null) {
                    name += " [" + ((XSDElementDeclaration) xsdTerm).getName() + "]";//$NON-NLS-1$//$NON-NLS-2$
                }
            } else if (content instanceof XSDModelGroup) {
                // log.info("SHOULD NOT HAPPEN????");
                if (xsdParticle.getContainer() instanceof XSDComplexTypeDefinition) {
                    String ctdName = ((XSDComplexTypeDefinition) xsdParticle.getContainer()).getName();
                    name = (ctdName != null ? ctdName : "");//$NON-NLS-1$
                }

            } else {
                name = "[Any]";//$NON-NLS-1$
            }
            if (!((xsdParticle.getMinOccurs() == 1) && (xsdParticle.getMaxOccurs() == 1))) {
                name += "  [";//$NON-NLS-1$
                name += xsdParticle.getMinOccurs();
                name += "...";//$NON-NLS-1$
                name += (xsdParticle.getMaxOccurs() == -1) ? "many" : "" + xsdParticle.getMaxOccurs();//$NON-NLS-1$//$NON-NLS-2$
                name += "]";//$NON-NLS-1$
            }
            return name;
        }

        if (obj instanceof XSDSimpleTypeDefinition) {
            return getSimpleTypeDefinition((XSDSimpleTypeDefinition) obj);
        }

        if (obj instanceof XSDModelGroup) {
            // return the name of the complex type definition
            XSDParticle particle = (XSDParticle) (((XSDModelGroup) obj).getContainer());
            XSDComplexTypeDefinition complexTypeDefinition = (XSDComplexTypeDefinition) particle.getContainer();
            String name = complexTypeDefinition.getName();
            if (name == null) {
                name = "anonymous type ";//$NON-NLS-1$
            }
            // return the occurrence
            if (!((particle.getMinOccurs() == 1) && (particle.getMaxOccurs() == 1))) {
                name += "  [";//$NON-NLS-1$
                name += particle.getMinOccurs();
                name += "...";//$NON-NLS-1$
                name += (particle.getMaxOccurs() == -1) ? "many" : "" + particle.getMaxOccurs();//$NON-NLS-1$//$NON-NLS-2$
                name += "]";//$NON-NLS-1$
            }
            // get extend type
            XSDTypeDefinition extendType = complexTypeDefinition.getBaseTypeDefinition();
            String extendTypeName = ""; //$NON-NLS-1$
            if (extendType != null && extendType != complexTypeDefinition && !"anyType".equals(extendType.getName())) { //$NON-NLS-1$
                extendTypeName = ":" + extendType.getName(); //$NON-NLS-1$
            }
            XSDSchema schema = particle.getSchema();
            String tail = ""; //$NON-NLS-1$
            if (schema != null && schema.getTargetNamespace() != null) {
                tail = " : "//$NON-NLS-1$
                        + schema.getTargetNamespace();
            }
            return name + tail + extendTypeName;

        }

        if (obj instanceof XSDFacet) {
            return ((XSDFacet) obj).getFacetName() + ": " + ((XSDFacet) obj).getLexicalValue();//$NON-NLS-1$
        }

        if (obj instanceof XSDIdentityConstraintDefinition) {
            return ((XSDIdentityConstraintDefinition) obj).getName();
        }

        if (obj instanceof XSDXPathDefinition) {
            XSDXPathDefinition xpath = (XSDXPathDefinition) obj;
            return xpath.getValue();
        }

        if (obj instanceof XSDAttributeGroupDefinition) {
            XSDAttributeGroupDefinition attributeGroupDefinition = (XSDAttributeGroupDefinition) obj;
            String name = (attributeGroupDefinition.getName() == null ? "" : attributeGroupDefinition.getName());//$NON-NLS-1$
            if (attributeGroupDefinition.getContents().size() == 0) {
                name += " [" + attributeGroupDefinition.getResolvedAttributeGroupDefinition().getName() + "]";//$NON-NLS-1$//$NON-NLS-2$
            }
            return name;
        }

        if (obj instanceof XSDAttributeUse) {
            XSDAttributeUse attributeUse = (XSDAttributeUse) obj;
            String name = attributeUse.getAttributeDeclaration().getName();
            if (name == null) {
                name = " [" + attributeUse.getAttributeDeclaration().getResolvedAttributeDeclaration().getName() + "]";//$NON-NLS-1$//$NON-NLS-2$
            }
            return name;
        }

        if (obj instanceof XSDAttributeDeclaration) {
            XSDAttributeDeclaration attributeDec = (XSDAttributeDeclaration) obj;
            String name = attributeDec.getName();
            if (name == null) {
                name = attributeDec.getAliasName();
                if (name == null) {
                    name = " [null]"; //$NON-NLS-1$
                }
            }
            return name;
        }

        if (obj instanceof XSDAnnotation) {
            // XSDAnnotation annotation = (XSDAnnotation) obj;
            return "Annotations";//$NON-NLS-1$
        }

        if (obj instanceof Element) {
            try {
                Element e = (Element) obj;
                if (e.getLocalName().equals("documentation")) {//$NON-NLS-1$
                    return "Documentation: " + e.getChildNodes().item(0).getNodeValue();//$NON-NLS-1$
                } else if (e.getLocalName().equals("appinfo")) {//$NON-NLS-1$
                    String source = e.getAttribute("source");//$NON-NLS-1$
                    if (source != null) {
                        if (source.startsWith("X_Label_")) {//$NON-NLS-1$
                            return Messages.bind(Messages.XSDTreeLabelProvider_1,
                                    Util.iso2lang.get(source.substring(8).toLowerCase()),
                                    e.getChildNodes().item(0).getNodeValue());
                        } else if (source.equals("X_ForeignKey")) {//$NON-NLS-1$
                            return Messages.bind(Messages.XSDTreeLabelProvider_2, e.getChildNodes().item(0).getNodeValue());
                        } else if (source.equals("X_ForeignKey_NotSep")) {//$NON-NLS-1$
                            Boolean v = Boolean.valueOf(e.getChildNodes().item(0).getNodeValue());
                            return Messages.bind(Messages.XSDTreeLabelProvider_3, Messages.SimpleXpathInputDialog_sepFkTabPanel,
                                    v);
                        } else if (source.equals("X_Visible_Rule")) {//$NON-NLS-1$
                            return Messages.bind(Messages.XSDTreeLabelProvider_4, e.getChildNodes().item(0).getNodeValue());
                        } else if (source.equals("X_Default_Value_Rule")) {//$NON-NLS-1$
                            return Messages.bind(Messages.XSDTreeLabelProvider_5, e.getChildNodes().item(0).getNodeValue());
                        } else if (source.equals("X_ForeignKeyInfo")) {//$NON-NLS-1$
                            return Messages.bind(Messages.XSDTreeLabelProvider_6, e.getChildNodes().item(0).getNodeValue());
                        } else if (source.equals("X_ForeignKeyInfoFormat")) {//$NON-NLS-1$
                            return Messages.bind(Messages.XSDTreeLabelProvider_20, e.getChildNodes().item(0).getNodeValue());
                        } else if (source.equals("X_PrimaryKeyInfo")) {//$NON-NLS-1$
                            return Messages.bind(Messages.XSDTreeLabelProvider_7, e.getChildNodes().item(0).getNodeValue());
                        } else if (source.equals("X_SourceSystem")) {//$NON-NLS-1$
                            return Messages.bind(Messages.XSDTreeLabelProvider_8, e.getChildNodes().item(0).getNodeValue());
                        } else if (source.equals("X_TargetSystem")) {//$NON-NLS-1$
                            return Messages.bind(Messages.XSDTreeLabelProvider_9, e.getChildNodes().item(0).getNodeValue());
                        } else if (source.startsWith("X_Description_")) {//$NON-NLS-1$
                            return Messages.bind(Messages.XSDTreeLabelProvider_10,
                                    Util.iso2lang.get(source.substring(14).toLowerCase()),
                                    e.getChildNodes().item(0).getNodeValue());
                        } else if (source.equals("X_Write")) {//$NON-NLS-1$
                            return Messages.bind(Messages.XSDTreeLabelProvider_11, e.getChildNodes().item(0).getNodeValue());
                        } else if (source.equals("X_No_Add")) {//$NON-NLS-1$
                            return Messages.bind(Messages.XSDTreeLabelProvider_29, e.getChildNodes().item(0).getNodeValue());
                        } else if (source.equals("X_No_Remove")) {//$NON-NLS-1$
                            return Messages.bind(Messages.XSDTreeLabelProvider_30, e.getChildNodes().item(0).getNodeValue());
                        } else if (source.equals("X_Deny_Create")) {//$NON-NLS-1$
                            return Messages.bind(Messages.XSDTreeLabelProvider_12, e.getChildNodes().item(0).getNodeValue());
                        } else if (source.equals("X_Deny_LogicalDelete")) {//$NON-NLS-1$
                            return Messages.bind(Messages.XSDTreeLabelProvider_13, e.getChildNodes().item(0).getNodeValue());
                        } else if (source.equals("X_Deny_PhysicalDelete")) {//$NON-NLS-1$
                            return Messages.bind(Messages.XSDTreeLabelProvider_14, e.getChildNodes().item(0).getNodeValue());
                        } else if (source.equals("X_Lookup_Field")) {//$NON-NLS-1$
                            return Messages.bind(Messages.XSDTreeLabelProvider_15, e.getChildNodes().item(0).getNodeValue());
                        } else if (source.equals("X_Workflow")) {//$NON-NLS-1$
                            return Messages.bind(Messages.XSDTreeLabelProvider_16, e.getChildNodes().item(0).getNodeValue());
                        } else if (source.equals("X_Hide")) {//$NON-NLS-1$
                            return Messages.bind(Messages.XSDTreeLabelProvider_17, e.getChildNodes().item(0).getNodeValue());
                            // add by ymli; bugId 0009157
                        } else if (source.equals("X_AutoExpand")) {//$NON-NLS-1$
                            return Messages.bind(Messages.XSDTreeLabelProvider_18, e.getChildNodes().item(0).getNodeValue());
                        } else if (source.startsWith("X_Facet")) {//$NON-NLS-1$
                            return Messages.bind(Messages.XSDTreeLabelProvider_19, source.substring(2, 7), source.substring(8),
                                    e.getChildNodes().item(0).getNodeValue());
                            // made schematron show:Schematron: schematron
                        } else if (source.startsWith("X_Display_Format_")) {//$NON-NLS-1$
                            return source + ": " + e.getChildNodes().item(0).getNodeValue();//$NON-NLS-1$
                        } else if (source.equals("X_Schematron")) {//$NON-NLS-1$

                            String pattern = (String) e.getFirstChild().getUserData("pattern_name");//$NON-NLS-1$
                            if (pattern == null) {
                                Element el = Util.parse(e.getChildNodes().item(0).getNodeValue()).getDocumentElement();
                                if (el.getAttributes().getNamedItem("name") != null) { //$NON-NLS-1$
                                    pattern = el.getAttributes().getNamedItem("name").getTextContent();//$NON-NLS-1$
                                }
                            }
                            return Messages.bind(Messages.XSDTreeLabelProvider_21,
                                    (pattern == null ? Messages.XSDTreeLabelProvider_22 : pattern));// e.getChildNodes().item(0).getNodeValue();
                            // end
                        } else if (source.equals("X_Retrieve_FKinfos")) {//$NON-NLS-1$
                            return Messages.bind(Messages.XSDTreeLabelProvider_23, e.getChildNodes().item(0).getNodeValue());
                        } else if (source.equals("X_FKIntegrity")) {//$NON-NLS-1$
                            return Messages.bind(Messages.XSDTreeLabelProvider_24, e.getChildNodes().item(0).getNodeValue());
                        } else if (source.equals("X_FKIntegrity_Override")) {//$NON-NLS-1$
                            return Messages.bind(Messages.XSDTreeLabelProvider_25, e.getChildNodes().item(0).getNodeValue());
                        } else if (source.equals("X_ForeignKey_Filter")) {//$NON-NLS-1$
                            String nodeValue = e.getChildNodes().item(0).getNodeValue();
                            if (nodeValue.startsWith("$CFFP:")) {//$NON-NLS-1$
                                nodeValue = StringEscapeUtils.unescapeXml(nodeValue).substring(6);
                            }
                            return Messages.bind(Messages.XSDTreeLabelProvider_26, nodeValue);
                        } else {
                            if (exAdapter != null) {
                                return exAdapter.getText4SourceElement((Element) obj);
                            } else {
                                return source + ": " + Util.nodeToString((Element) obj);//$NON-NLS-1$
                            }
                        }
                    } else {
                        return Util.nodeToString((Element) obj);
                    }
                } else {
                    return Util.nodeToString((Element) obj);
                }
            } catch (Exception e) {
                LOG.error("Fail in parsing XSD object:", e);
            }
        }

        if (obj == null) {
            return "NULL";//$NON-NLS-1$
        }
        return "?? " + obj.getClass().getName() + " : " + obj.toString();//$NON-NLS-1$//$NON-NLS-2$

    }

    @Override
    public Image getImage(Object obj) {

        if (obj instanceof XSDElementDeclaration) {
            // top declaration
            XSDElementDeclaration decl = (XSDElementDeclaration) obj;
            // check if concept or "just" element
            boolean isConcept = false;
            EList l = decl.getIdentityConstraintDefinitions();
            for (Iterator iter = l.iterator(); iter.hasNext();) {
                XSDIdentityConstraintDefinition icd = (XSDIdentityConstraintDefinition) iter.next();
                if (icd.getIdentityConstraintCategory().equals(XSDIdentityConstraintCategory.UNIQUE_LITERAL)) {
                    isConcept = true;
                    break;
                }
            }
            // display approprite image
            if (isConcept) {
                return ImageCache.getCreatedImage(EImage.CONCEPT.getPath());
            } else {
                return ImageCache.getCreatedImage(EImage.ELEMENT_ONLY.getPath());
                /*
                 * if (decl.getTypeDefinition() instanceof XSDComplexTypeDefinition) return
                 * PlatformUI.getWorkbench().getSharedImages().getCreatedImage(ISharedImages.IMG_OBJ_FOLDER); else
                 * return ImageCache.getCreatedImage( "icons/elements_obj_+.gif");
                 */
            }
        }

        if (obj instanceof XSDParticle) {
            XSDParticle xsdParticle = (XSDParticle) obj;

            XSDTerm xsdTerm = xsdParticle.getTerm();
            XSDParticleContent content = xsdParticle.getContent();
            if (content instanceof XSDElementDeclaration) {
                // get Type of Parent Group
                List<Object> realKeyInfos = Util.getRealKeyInfos(entity, xsdParticle);
                if (realKeyInfos != null && realKeyInfos.size() > 0) {
                    return ImageCache.getCreatedImage(EImage.PRIMARYKEY.getPath());
                }
                if (XSDUtil.hasFKInfo((XSDElementDeclaration) content)) {
                    return ImageCache.getCreatedImage(EImage.FK_OBJ.getPath());
                }

                XSDConcreteComponent xsdConcreteComponent = xsdParticle.getContainer();
                if (xsdConcreteComponent instanceof XSDModelGroup) {
                    return ImageCache.getCreatedImage(EImage.SCHEMAELEMENT.getPath());
                }
                /*
                 * if(((XSDElementDeclaration) xsdTerm).getAnonymousTypeDefinition() instanceof
                 * XSDComplexTypeDefinition) return ImageCache.getCreatedImage( EImage.COMPLEXTYPE.getPath()); else
                 * return ImageCache.getCreatedImage( EImage.SIMPLETYPE.getPath());
                 */

            } else if (xsdTerm instanceof XSDModelGroup) {
                int type = ((XSDModelGroup) xsdTerm).getCompositor().getValue();
                switch (type) {
                case XSDCompositor.ALL:
                    return ImageCache.getCreatedImage(EImage.COMPLEX_ALL.getPath());
                case XSDCompositor.CHOICE:
                    return ImageCache.getCreatedImage(EImage.COMPLEX_CHOICE.getPath());
                case XSDCompositor.SEQUENCE:
                    return ImageCache.getCreatedImage(EImage.COMPLEX_SEQUENCE.getPath());
                }
            } else if (xsdTerm instanceof XSDWildcard) {
                return ImageCache.getCreatedImage("icons/wildcard.gif");//$NON-NLS-1$
            } else {
                LOG.info(Messages.bind(Messages.XSDTreeLabelProvider_27, xsdTerm.getClass().getName()));
                return ImageCache.getCreatedImage("icons/error.gif");//$NON-NLS-1$
            }
        }

        if (obj instanceof XSDSimpleTypeDefinition) {
            return ImageCache.getCreatedImage(EImage.SIMPLETYPE.getPath());
        }

        if (obj instanceof XSDComplexTypeDefinition) {
            XSDComplexTypeDefinition ctd = (XSDComplexTypeDefinition) obj;
            XSDComplexTypeContent ctc = ctd.getContent();
            if (ctc instanceof XSDParticle) {
                if (((XSDParticle) ctc).getTerm() instanceof XSDModelGroup) {
                    int type = ((XSDModelGroup) ((XSDParticle) ctc).getTerm()).getCompositor().getValue();
                    switch (type) {
                    case XSDCompositor.ALL:
                        return ImageCache.getCreatedImage(EImage.COMPLEX_ALL.getPath());
                    case XSDCompositor.CHOICE:
                        return ImageCache.getCreatedImage(EImage.COMPLEX_CHOICE.getPath());
                    case XSDCompositor.SEQUENCE:
                        return ImageCache.getCreatedImage(EImage.COMPLEX_SEQUENCE.getPath());
                    }
                }
            } else {
                // simple Type!!!
                LOG.info(Messages.bind(Messages.XSDTreeLabelProvider_28, ctc.getClass().getName()));
                return ImageCache.getCreatedImage("icons/error.gif");//$NON-NLS-1$
            }
        }

        if (obj instanceof XSDModelGroup) {
            int type = ((XSDModelGroup) obj).getCompositor().getValue();
            switch (type) {
            case XSDCompositor.ALL:
                return ImageCache.getCreatedImage(EImage.COMPLEX_ALL.getPath());
            case XSDCompositor.CHOICE:
                return ImageCache.getCreatedImage(EImage.COMPLEX_CHOICE.getPath());
            case XSDCompositor.SEQUENCE:
                return ImageCache.getCreatedImage(EImage.COMPLEX_SEQUENCE.getPath());
            }
        }

        if (obj instanceof XSDFacet) {
            return ImageCache.getCreatedImage(EImage.FACET.getPath());
        }

        if (obj instanceof XSDIdentityConstraintDefinition) {
            XSDIdentityConstraintDefinition identity = (XSDIdentityConstraintDefinition) obj;
            if (identity.getIdentityConstraintCategory().equals(XSDIdentityConstraintCategory.UNIQUE_LITERAL)) {
                return ImageCache.getCreatedImage(EImage.KEYS.getPath());
            }
            return ImageCache.getCreatedImage(EImage.PRIMARYKEY.getPath());
        }

        if (obj instanceof XSDXPathDefinition) {
            XSDXPathDefinition xpath = (XSDXPathDefinition) obj;
            if (xpath.getVariety().equals(XSDXPathVariety.FIELD_LITERAL)) {
                return ImageCache.getCreatedImage("icons/field.gif");//$NON-NLS-1$
            }
            return ImageCache.getCreatedImage("icons/selector.gif");//$NON-NLS-1$
        }

        if (obj instanceof XSDAttributeGroupDefinition) {
            return ImageCache.getCreatedImage("icons/attribute_group.gif");//$NON-NLS-1$
        }

        if (obj instanceof XSDAttributeUse) {
            XSDAttributeUse att = (XSDAttributeUse) obj;
            if ("xmlns".equals(att.getAttributeDeclaration().getTargetNamespace())) {//$NON-NLS-1$
                return ImageCache.getCreatedImage(EImage.ANNOTATION.getPath());
            }
            if (att.getUse().equals(XSDAttributeUseCategory.REQUIRED_LITERAL)) {
                return ImageCache.getCreatedImage("icons/attribute_mandatory.gif");//$NON-NLS-1$
            } else {
                return ImageCache.getCreatedImage("icons/attribute.gif");//$NON-NLS-1$
            }
        }

        if (obj instanceof XSDAttributeDeclaration) {
            XSDAttributeDeclaration attributeDec = (XSDAttributeDeclaration) obj;
            if ("xmlns".equals(attributeDec.getTargetNamespace())) //$NON-NLS-1$
            {
                return ImageCache.getCreatedImage(EImage.ANNOTATION.getPath());
            } else {
                return ImageCache.getCreatedImage("icons/attribute.gif");//$NON-NLS-1$
            }
        }

        if (obj instanceof XSDAnnotation) {
            return ImageCache.getCreatedImage(EImage.ANNOTATION.getPath());
        }

        if (obj instanceof Element) {
            try {
                Element e = (Element) obj;
                if (e.getLocalName().equals("documentation")) {//$NON-NLS-1$
                    return ImageCache.getCreatedImage(EImage.DOCUMENTATION.getPath());
                } else if (e.getLocalName().equals("appinfo")) {//$NON-NLS-1$
                    String source = e.getAttribute("source");//$NON-NLS-1$
                    if (source != null) {
                        if (source.startsWith("X_Label_")) {//$NON-NLS-1$
                            return ImageCache.getCreatedImage(EImage.LABEL.getPath());
                        } else if (source.equals("X_ForeignKey")) {//$NON-NLS-1$
                            return ImageCache.getCreatedImage(EImage.FK_OBJ.getPath());
                        } else if (source.equals("X_Visible_Rule")) {//$NON-NLS-1$
                            return ImageCache.getCreatedImage(EImage.ROUTINE.getPath());
                        } else if (source.equals("X_Default_Value_Rule")) {//$NON-NLS-1$
                            return ImageCache.getCreatedImage(EImage.ROUTINE.getPath());
                        } else if (source.equals("X_ForeignKeyInfo")) {//$NON-NLS-1$
                            return ImageCache.getCreatedImage(EImage.KEYINFO.getPath());
                        } else if (source.equals("X_ForeignKeyInfoFormat")) {//$NON-NLS-1$
                            return ImageCache.getCreatedImage(EImage.KEYINFO.getPath());
                            // fix bug 0013194 by rhou.
                        } else if (source.equals("X_FKIntegrity")) {//$NON-NLS-1$
                            return ImageCache.getCreatedImage(EImage.KEYINFO.getPath());
                        } else if (source.equals("X_FKIntegrity_Override")) {//$NON-NLS-1$
                            return ImageCache.getCreatedImage(EImage.KEYINFO.getPath());
                        } else if (source.equals("X_Retrieve_FKinfos")) {//$NON-NLS-1$
                            return ImageCache.getCreatedImage(EImage.KEYINFO.getPath());
                        } else if (source.equals("X_SourceSystem")) {//$NON-NLS-1$
                            return ImageCache.getCreatedImage(EImage.SOURCESYSTEM.getPath());
                        } else if (source.equals("X_TargetSystem")) {//$NON-NLS-1$
                            return ImageCache.getCreatedImage(EImage.TARGETSYSTEM.getPath());
                        } else if (source.startsWith("X_Description_")) {//$NON-NLS-1$
                            return ImageCache.getCreatedImage(EImage.DOCUMENTATION.getPath());
                        } else if (source.equals("X_Write") || source.equals("X_No_Add") || source.equals("X_No_Remove")) {//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                            return ImageCache.getCreatedImage(EImage.SECURITYANNOTATION.getPath());
                        } else if (source.equals("X_Deny_Create")) {//$NON-NLS-1$
                            return ImageCache.getCreatedImage(EImage.SECURITYANNOTATION.getPath());
                        } else if (source.equals("X_Deny_LogicalDelete")) {//$NON-NLS-1$
                            return ImageCache.getCreatedImage(EImage.SECURITYANNOTATION.getPath());
                        } else if (source.equals("X_Deny_PhysicalDelete")) {//$NON-NLS-1$
                            return ImageCache.getCreatedImage(EImage.SECURITYANNOTATION.getPath());
                        } else if (source.equals("X_Lookup_Field")) {//$NON-NLS-1$
                            return ImageCache.getCreatedImage(EImage.BROWSE.getPath());
                        } else if (source.equals("X_Hide")) {//$NON-NLS-1$
                            return ImageCache.getCreatedImage(EImage.SECURITYANNOTATION.getPath());
                        } else if (source.equals("X_Schematron")) {//$NON-NLS-1$
                            return ImageCache.getCreatedImage(EImage.ROUTINE.getPath());
                        } else if (source.equals("X_Workflow")) {//$NON-NLS-1$
                            return ImageCache.getCreatedImage(EImage.WORKFLOW_PROCESS.getPath());
                        } else if (source.equals("X_AutoExpand")) {//$NON-NLS-1$
                            return ImageCache.getCreatedImage(EImage.DEFAULT.getPath());
                        }
                        if (source.equals("X_ForeignKey_Filter")) {//$NON-NLS-1$
                            return ImageCache.getCreatedImage(EImage.FILTER_PS.getPath());
                        } else if (source.startsWith("X_Display_Format_")) {//$NON-NLS-1$
                            return ImageCache.getCreatedImage(EImage.THIN_MIN_VIEW.getPath());
                        } else {
                            return ImageCache.getCreatedImage(EImage.DOCUMENTATION.getPath());
                        }
                    } else {
                        return ImageCache.getCreatedImage(EImage.DOCUMENTATION.getPath());
                    }
                } else {
                    return ImageCache.getCreatedImage(EImage.DOCUMENTATION.getPath());
                }
            } catch (Exception e) {
                LOG.error("Fail in parsing XSD component:", e);
            }
        }

        return ImageCache.getCreatedImage("icons/small_warn.gif");//$NON-NLS-1$
        // return PlatformUI.getWorkbench().getSharedImages().getCreatedImage(ISharedImages.IMG_OBJ_ELEMENT);
    }

    /**
     * Print a simple type definition for the document.
     *
     * @param xsdSimpleTypeDefinition a simple type definition in the schema for schema.
     */
    public String getSimpleTypeDefinition(XSDSimpleTypeDefinition xsdSimpleTypeDefinition) {
        String s = "";//$NON-NLS-1$
        if (xsdSimpleTypeDefinition == null) {
        } else if (xsdSimpleTypeDefinition.getEffectiveEnumerationFacet() != null) {
            /*
             * List value = xsdSimpleTypeDefinition.getEffectiveEnumerationFacet().getValue(); if (value.size() > 1) {
             * s+= "("; } for (Iterator enumerators = value.iterator(); enumerators.hasNext();) { String enumerator =
             * enumerators.next().toString(); s+= enumerator; if (enumerators.hasNext()) { s+= " | ;"; } } if
             * (value.size() > 1) { s+= ")";
             *
             * }
             */
            s += xsdSimpleTypeDefinition.getName() != null ? xsdSimpleTypeDefinition.getName()
                    : xsdSimpleTypeDefinition.getBaseTypeDefinition().getName();
        } else if (xsdSimpleTypeDefinition.getElement() != null
                && xsdSimpleTypeDefinition.getElement().hasAttribute(XSDConstants.ID_ATTRIBUTE)) {
            s += xsdSimpleTypeDefinition.getName();
        } else if ((XSDVariety.UNION_LITERAL == xsdSimpleTypeDefinition.getVariety())
                || (XSDVariety.LIST_LITERAL == xsdSimpleTypeDefinition.getVariety())) {
            s += "(";//$NON-NLS-1$
            for (Iterator members = xsdSimpleTypeDefinition.getMemberTypeDefinitions().iterator(); members.hasNext();) {
                XSDSimpleTypeDefinition memberTypeDefinition = (XSDSimpleTypeDefinition) members.next();
                s += getSimpleTypeDefinition(memberTypeDefinition);
                if (members.hasNext()) {
                    s += " | ";//$NON-NLS-1$
                }
            }
            s += ")";//$NON-NLS-1$
            if (xsdSimpleTypeDefinition.getMemberTypeDefinitions().isEmpty()) {
                s = xsdSimpleTypeDefinition.getVariety() + "";//$NON-NLS-1$
            }
        } else if ((XSDVariety.UNION_LITERAL == xsdSimpleTypeDefinition.getVariety())
                | (XSDVariety.LIST_LITERAL == xsdSimpleTypeDefinition.getVariety())) {
            s += "List of ";//$NON-NLS-1$
            s += getSimpleTypeDefinition(xsdSimpleTypeDefinition.getItemTypeDefinition());
        } else if (xsdSimpleTypeDefinition.getName() != null) {
            s += xsdSimpleTypeDefinition.getName();
        } else if (xsdSimpleTypeDefinition.getEffectivePatternFacet() != null) {
            // s+= xsdSimpleTypeDefinition.getEffectivePatternFacet().getLexicalValue());
            s += "a restricted xpath expression";//$NON-NLS-1$
        } else {
            s += "";//$NON-NLS-1$
        }
        String tail = xsdSimpleTypeDefinition.getTargetNamespace() == null ? "" : xsdSimpleTypeDefinition.getTargetNamespace();//$NON-NLS-1$
        if (tail.equals(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001) || tail.equals(XSDConstants.SCHEMA_FOR_SCHEMA_URI_1999)) {
            tail = "";//$NON-NLS-1$
        } else if (!tail.equals("")) { //$NON-NLS-1$
            tail = " : " + tail;//$NON-NLS-1$
        }
        return s + tail;
    }

    private XSDElementDeclaration entity;

    @Override
    public void updateLabel(ViewerLabel label, TreePath elementPath) {
        if (elementPath.getFirstSegment() instanceof XSDElementDeclaration) {
            entity = (XSDElementDeclaration) elementPath.getFirstSegment();
        }

        Object lastSegment = elementPath.getLastSegment();
        label.setText(getText(lastSegment));
        label.setImage(getImage(lastSegment));
    }

    @Override
    public Font getFont(Object element) {
        if (filter != null) {
            if (filter.check(element) == FilterResult.ENABLE) {
                return FONT_BOLD;
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.viewers.IColorProvider#getForeground(java.lang.Object)
     */
    @Override
    public Color getForeground(Object element) {
        if (filter != null) {

            switch (filter.check(element)) {
            case ENABLE:
                return COLOR_BLACK;
            case DISABLE:
            case IGNORE:
                return COLOR_GREY;
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.viewers.IColorProvider#getBackground(java.lang.Object)
     */
    @Override
    public Color getBackground(Object element) {
        return null;
    }

}
