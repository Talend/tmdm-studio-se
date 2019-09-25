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
package com.amalto.workbench.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDComponent;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDTerm;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.util.XSDSchemaBuildingTools;
import org.talend.mdm.commmon.util.core.EUUIDCustomType;
import org.talend.mdm.commmon.util.core.ICoreConstants;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.models.IAnnotationConst;

public class XSDAnnotationsStructure {

    private static final String CATEGORY_FIELD = "field";

    private static final String CATEGORY_NAME = "name";

    private static final String SOURCE = "source";

    private static final String APPINFO = "appinfo";

    private static Log log = LogFactory.getLog(XSDAnnotationsStructure.class);

    boolean hasChanged = false;

    XSDAnnotation annotation;

    XSDElementDeclaration declaration;

    XSDSchema schema;

    XSDComponent componet;

    private Integer particleMaxOccurs = null;

    private XSDComplexTypeDefinition complexTypeDef;

    /**
     * "Clever" Constructor that finds or creates annotations of an XSDComponent object
     *
     * @param component
     */
    public XSDAnnotationsStructure(XSDComponent component) {
        inputChanged(component);
    }

    protected void inputChanged(Object component) {
        if (component instanceof XSDAnnotation) {
            annotation = (XSDAnnotation) component;
            if (annotation.getContainer() instanceof XSDElementDeclaration) {
                declaration = (XSDElementDeclaration) annotation.getContainer();
                XSDConcreteComponent elementContainer = declaration.getContainer();
                if (elementContainer != null && elementContainer instanceof XSDParticle) {
                    particleMaxOccurs = ((XSDParticle) elementContainer).getMaxOccurs();
                }
            } else if (annotation.getContainer() instanceof XSDComplexTypeDefinition) {
                complexTypeDef = (XSDComplexTypeDefinition) annotation.getContainer();
            }
        }

        if (component instanceof XSDElementDeclaration) {
            declaration = (XSDElementDeclaration) component;
            if (declaration.getAnnotation() == null) {
                XSDFactory factory = XSDSchemaBuildingTools.getXSDFactory();
                annotation = factory.createXSDAnnotation();
            } else {
                annotation = declaration.getAnnotation();
            }
        }
        if (component instanceof XSDComplexTypeDefinition) {
            complexTypeDef = (XSDComplexTypeDefinition) component;
            componet = complexTypeDef;
            if (complexTypeDef.getAnnotation() == null) {
                XSDFactory factory = XSDSchemaBuildingTools.getXSDFactory();
                annotation = factory.createXSDAnnotation();
            } else {
                annotation = complexTypeDef.getAnnotation();
            }
        }
        if (component instanceof XSDModelGroup) {
            XSDModelGroup group = (XSDModelGroup) component;
            if (group.getContainer().getContainer() instanceof XSDComplexTypeDefinition) {
                complexTypeDef = (XSDComplexTypeDefinition) group.getContainer().getContainer();
                if (complexTypeDef.getAnnotation() == null) {
                    XSDFactory factory = XSDSchemaBuildingTools.getXSDFactory();
                    annotation = factory.createXSDAnnotation();
                } else {
                    annotation = complexTypeDef.getAnnotation();
                }
            }
            if (group.getContainer() instanceof XSDParticle) {
                particleMaxOccurs = ((XSDParticle) group.getContainer()).getMaxOccurs();
            }
        }

        if (component instanceof XSDParticle) {
            XSDTerm term = ((XSDParticle) component).getTerm();
            if (term instanceof XSDElementDeclaration) {
                declaration = (XSDElementDeclaration) term;
                if (declaration.getAnnotation() == null) {
                    XSDFactory factory = XSDSchemaBuildingTools.getXSDFactory();
                    annotation = factory.createXSDAnnotation();
                } else {
                    annotation = declaration.getAnnotation();
                }
            }
            particleMaxOccurs = ((XSDParticle) component).getMaxOccurs();
        }
    }

    public Integer getParticleMaxOccurs() {
        return particleMaxOccurs;
    }

    /****************************************************************************
     * DOCUMENTATION
     ****************************************************************************/

    private Element getDocumentationElement() {
        Element documentation = null;
        ArrayList<Element> list = new ArrayList<Element>();
        list.addAll(annotation.getUserInformation());
        for (Element ann : list) {
            String name = ann.getLocalName();
            if ("documentation".equals(name.toLowerCase())) {//$NON-NLS-1$
                documentation = ann;
                break;
            }
        }
        return documentation;
    }

    public String getDocumentation() {
        Element appInfo = getDocumentationElement();
        if (appInfo == null) {
            return null;
        }
        return appInfo.getFirstChild().getNodeValue();
    }

    private boolean removeDocumentation() {
        boolean somethingRemoved = false;
        Element documentation = null;
        do {
            documentation = getDocumentationElement();
            if (documentation != null) {
                annotation.getElement().removeChild(documentation);
                annotation.getApplicationInformation().remove(documentation); // yes we need to do that too....
                somethingRemoved = true;
            }
        } while (documentation != null);
        return somethingRemoved;
    }

    public boolean setDocumentation(String value) {
        if (value == null) {
            boolean wasRemoved = removeDocumentation();
            hasChanged = hasChanged | wasRemoved;
            return wasRemoved;
        }

        Element documentation = getDocumentationElement();
        if (documentation == null) {
            return addDocumentation(value);
        }

        // change existing one if
        // first make sure the annotation is not brain new and is attached
        if (declaration.getAnnotation() == null) {
            declaration.setAnnotation(annotation);
        }
        // then update the documentation
        String existingText = documentation.getFirstChild().getNodeValue();
        if (!value.equals(existingText)) {
            documentation.getFirstChild().setNodeValue(value);
            hasChanged = true;
            return true;
        }
        return false;
    }

    private boolean addDocumentation(String value) {
        if (declaration.getAnnotation() == null) {
            declaration.setAnnotation(annotation);
        }
        Element appinfo = annotation.createUserInformation("documentation");//$NON-NLS-1$
        Node text = appinfo.getOwnerDocument().createTextNode(value);
        appinfo.appendChild(text);
        annotation.getElement().appendChild(appinfo);
        hasChanged = true;
        return true;
    }

    /****************************************************************************
     * LABELS
     ****************************************************************************/

    public boolean removeLabel(String countryCode) {
        boolean somethingChanged = removeAppInfos(IAnnotationConst.KEY_PREFIX_LABEL + countryCode.toUpperCase());
        hasChanged = hasChanged | somethingChanged;
        return somethingChanged;
    }

    public boolean removeAllLabels() {
        boolean somethingChanged = removeAppInfos(IAnnotationConst.KEY_REGEX_LABEL);
        hasChanged = hasChanged | somethingChanged;
        return somethingChanged;
    }

    public boolean setLabel(String countryCode, String label) {
        if (countryCode == null) {
            return false;
        }
        boolean somethingChanged = setAppInfo(IAnnotationConst.KEY_PREFIX_LABEL + countryCode.toUpperCase(), label, true);
        hasChanged = hasChanged | somethingChanged;
        return somethingChanged;
    }

    public boolean setLabels(Map<String, String> labels) {
        boolean somethingChanged = false;
        Set<String> isoCodes = labels.keySet();
        for (String code : isoCodes) {
            String label = labels.get(code);
            somethingChanged = somethingChanged | setLabel(code, label);
        }
        hasChanged = hasChanged | somethingChanged;
        return somethingChanged;
    }

    public Map<String, String> getLabels() {
        Map<String, String> labels = new LinkedHashMap<String, String>();
        Map<String, String> appInfos = getAppInfos(IAnnotationConst.KEY_REGEX_LABEL);
        Set<String> keys = appInfos.keySet();
        for (String key : keys) {
            labels.put(key.substring(8).toLowerCase(), appInfos.get(key));
        }
        return labels;
    }

    /****************************************************************************
     * DESCRIPTIONS
     ****************************************************************************/

    public boolean removeDescription(String countryCode) {
        boolean somethingChanged = removeAppInfos(IAnnotationConst.KEY_PREFIX_DESCRIPTION + countryCode.toUpperCase());
        hasChanged = hasChanged | somethingChanged;
        return somethingChanged;
    }

    public boolean removeAllDescriptions() {
        boolean somethingChanged = removeAppInfos(IAnnotationConst.KEY_REGEX_DESCRIPTION);
        hasChanged = hasChanged | somethingChanged;
        return somethingChanged;
    }

    public boolean setDescription(String countryCode, String description) {
        if (countryCode == null) {
            return false;
        }
        boolean somethingChanged = setAppInfo(IAnnotationConst.KEY_PREFIX_DESCRIPTION + countryCode.toUpperCase(), description,
                true);
        hasChanged = hasChanged | somethingChanged;
        return somethingChanged;
    }

    public boolean setDescriptions(Map<String, String> descriptions) {
        boolean somethingChanged = false;
        Set<String> isoCodes = descriptions.keySet();
        for (String code : isoCodes) {
            String description = descriptions.get(code);
            somethingChanged = somethingChanged | setDescription(code, description);
        }
        hasChanged = hasChanged | somethingChanged;
        return somethingChanged;
    }

    public Map<String, String> getDescriptions() {
        Map<String, String> descriptions = new LinkedHashMap<String, String>();
        Map<String, String> appInfos = getAppInfos(IAnnotationConst.KEY_REGEX_DESCRIPTION);
        Set<String> keys = appInfos.keySet();
        for (String key : keys) {
            descriptions.put(key.substring(14).toLowerCase(), appInfos.get(key));
        }
        return descriptions;
    }

    /****************************************************************************
     * FK Filter
     ****************************************************************************/

    public boolean setFKFilter(String xPath) {
        boolean somethingChanged = setAppInfo(IAnnotationConst.KEY_FOREIGN_KEY_FILTER, xPath, true);
        hasChanged = hasChanged | somethingChanged;
        return somethingChanged;
    }

    public String getFKFilter() {
        return getAppInfoValue(IAnnotationConst.KEY_FOREIGN_KEY_FILTER);
    }

    /****************************************************************************
     * Auto Expand
     *
     * @throws Exception
     ****************************************************************************/

    public boolean setAutoExpand(String value) throws Exception {
        if (!(declaration.getTypeDefinition() instanceof XSDComplexTypeDefinition)) {
            return false;
        }

        XSDSchema xsd = schema != null ? schema : declaration.getSchema();
        String auto = IAnnotationConst.KEY_AUTO_EXPAND;
        String xsdString = Util.nodeToString(xsd.getDocument().getDocumentElement());
        ArrayList<Object> objs = Util.getAllComplexTypeChildren(declaration);
        for (Object obj : objs) {
            if (obj instanceof XSDElementDeclaration || obj instanceof XSDParticle) {
                boolean isImported = false;

                if (obj instanceof XSDParticle) {
                    XSDParticle particle = (XSDParticle) obj;
                    if (particle.getTerm() instanceof XSDElementDeclaration) {
                        XSDElementDeclaration decl = (XSDElementDeclaration) particle.getTerm();
                        if (Util.IsAImporedElement(decl, xsdString)) {
                            XSDTypeDefinition typeDef = decl.getTypeDefinition();
                            if (Util.IsAImporedElement(typeDef, xsdString)) {
                                isImported = true;
                            }
                        }
                    }
                } else if (obj instanceof XSDElementDeclaration) {
                    XSDElementDeclaration decl = (XSDElementDeclaration) obj;
                    if (Util.IsAImporedElement(decl, xsdString)) {
                        isImported = true;
                    }
                }

                if (!isImported) {
                    XSDAnnotationsStructure annotion = new XSDAnnotationsStructure((XSDComponent) obj);
                    annotion.setAppInfo(auto, value, true);
                }
            }
        }
        return true;
    }

    public String getAutoExpand() {
        return getAppInfoValue(IAnnotationConst.KEY_AUTO_EXPAND);
    }

    /****************************************************************************
     * FOREIGN KEY
     ****************************************************************************/

    public boolean setForeignKey(String xPath) {
        boolean somethingChanged = setAppInfo(IAnnotationConst.KEY_FOREIGN_KEY, xPath, true);
        hasChanged = hasChanged | somethingChanged;
        return somethingChanged;
    }

    public String getForeignKey() {
        return getAppInfoValue(IAnnotationConst.KEY_FOREIGN_KEY);
    }

    public boolean setForeignKeyNotSep(Boolean sep) {
        boolean somethingChanged = setAppInfo(IAnnotationConst.KEY_FOREIGN_KEY_NOT_SEP, sep == null ? null : sep.toString(),
                true);
        hasChanged = hasChanged | somethingChanged;
        return somethingChanged;
    }

    public String getForeignKeyNotSep() {
        return getAppInfoValue(IAnnotationConst.KEY_FOREIGN_KEY_NOT_SEP);
    }

    /****************************************************************************
     * Visible Rule
     ****************************************************************************/

    public boolean setVisibleRule(String xPath) {
        boolean somethingChanged = setAppInfo(IAnnotationConst.KEY_VISIBLE_RULE, xPath, true);
        hasChanged = hasChanged | somethingChanged;
        return somethingChanged;
    }

    public String getVisibleRule() {
        return getAppInfoValue(IAnnotationConst.KEY_VISIBLE_RULE);
    }

    /****************************************************************************
     * Default Value Rule
     ****************************************************************************/

    public boolean setDefaultValueRule(String xPath) {
        boolean somethingChanged = setAppInfo(IAnnotationConst.KEY_DEFAULT_VALUE, xPath, true);
        hasChanged = hasChanged | somethingChanged;
        return somethingChanged;
    }

    public String getDefaultValueRule() {
        return getAppInfoValue(IAnnotationConst.KEY_DEFAULT_VALUE);
    }

    // PRIMARY KEY INFOS
    public boolean setPrimaryKeyInfos(List<String> xPaths) {
        removeAppInfos(IAnnotationConst.KEY_PK_INFO);
        for (String xPath : xPaths) {
            addAppInfo(IAnnotationConst.KEY_PK_INFO, xPath);
        }
        hasChanged = true;
        return true;
    }

    /****************************************************************************
     * FOREIGN KEY INFOS
     ****************************************************************************/
    public boolean setForeignKeyInfos(List<String> xPaths) {
        boolean somethingChanged = removeAppInfos(IAnnotationConst.KEY_FOREIGN_KEY_INFO);
        boolean added = false;
        for (String xPath : xPaths) {
            addAppInfo(IAnnotationConst.KEY_FOREIGN_KEY_INFO, xPath);
            added = true;
        }
        hasChanged |= somethingChanged | added;
        return true;
    }

    public boolean setFormatForeignKeyInfo(String formatedForeignKeyinfo) {
        boolean somethingChanged = removeAppInfos(IAnnotationConst.KEY_FOREIGN_KEY_INFO_FORMAT);
        boolean added = false;
        if (formatedForeignKeyinfo != null && !formatedForeignKeyinfo.isEmpty()) {
            added = addAppInfo(IAnnotationConst.KEY_FOREIGN_KEY_INFO_FORMAT, formatedForeignKeyinfo);
        }

        hasChanged |= somethingChanged | added;
        return true;
    }

    public String getFormatForeignKeyInfo() {
        Map<String, String> appInfos = getAppInfos(IAnnotationConst.KEY_FOREIGN_KEY_INFO_FORMAT);

        Collection<String> values = appInfos.values();
        if (values.size() > 0) {
            return values.iterator().next();
        }

        return "";
    }

    public boolean setRetrieveFKinfos(Boolean retrieveFKinfos) {
        boolean somethingChanged = removeAppInfos(IAnnotationConst.KEY_RETRIEVE_FK_INFOS);
        if (retrieveFKinfos != null) {
            hasChanged = addAppInfo(IAnnotationConst.KEY_RETRIEVE_FK_INFOS, retrieveFKinfos.toString());
            return true;
        }
        hasChanged |= somethingChanged;
        return true;
    }

    public boolean getRetrieveFKinfos() {
        Map<String, String> appInfos = getAppInfos(IAnnotationConst.KEY_RETRIEVE_FK_INFOS);
        Set<String> keys = appInfos.keySet();
        for (String key : keys) {
            return "true".equals(appInfos.get(key));//$NON-NLS-1$
        }
        // Starting from 5.0 onwards, always resolve FKInfos by default
        if (log.isDebugEnabled()) {
            log.debug(Messages.XSDAnnotationsStructure_DebugInfo);
        }
        return true;
    }

    public boolean setForeignKeyInfo(int num, String xPath) {
        TreeMap<String, String> infos = getForeignKeyInfos();
        infos.put(IAnnotationConst.KEY_PREFIX_FOREIGN_KEY_INFO + num, xPath);
        return setForeignKeyInfos(new ArrayList<String>(infos.values()));
    }

    public TreeMap<String, String> getForeignKeyInfos() {
        TreeMap<String, String> foreignKeyInfos = new TreeMap<String, String>();
        Map<String, String> appInfos = getAppInfos(IAnnotationConst.KEY_FOREIGN_KEY_INFO);
        Set<String> keys = appInfos.keySet();
        for (String key : keys) {
            foreignKeyInfos.put(key, appInfos.get(key));
        }
        return foreignKeyInfos;
    }

    /****************************************************************************
     * WRITE ACCESS
     *
     * @throws XtentisException
     ****************************************************************************/
    public boolean setAccessRole(Collection<String> roles, boolean recursive, IStructuredContentProvider provider, String access)
            throws Exception {
        Map<String, List<String>> roleMap = Collections.singletonMap(access, new ArrayList(roles));
        return setAccessRole(roleMap, recursive, provider);
    }

    public boolean setAccessRole(Map<String, List<String>> roleMap, boolean recursive, IStructuredContentProvider provider)
            throws Exception {
        XSDSchema xsd = schema != null ? schema : declaration.getSchema();

        String xsdString = Util.nodeToString(xsd.getDocument().getDocumentElement());
        if (recursive) {
            ArrayList<Object> objList = new ArrayList<Object>();
            XSDComponent component = declaration;
            if (declaration == null) {
                component = this.componet;
            }
            Object[] objs = Util.getAllObject(component, objList, provider);

            while (objs.length > 0) {
                Object[] objCpys = objs;
                for (Object obj : objCpys) {

                    if (obj instanceof XSDElementDeclaration || obj instanceof XSDParticle) {
                        boolean isImported = false;

                        if (obj instanceof XSDParticle) {
                            XSDParticle particle = (XSDParticle) obj;
                            if (particle.getTerm() instanceof XSDElementDeclaration) {
                                XSDElementDeclaration decl = (XSDElementDeclaration) particle.getTerm();
                                if (Util.IsAImporedElement(decl, xsdString)) {
                                    XSDTypeDefinition typeDef = decl.getTypeDefinition();
                                    if (Util.IsAImporedElement(typeDef, xsdString)) {
                                        isImported = true;
                                    }
                                }
                            }
                        } else if (obj instanceof XSDElementDeclaration) {
                            XSDElementDeclaration decl = (XSDElementDeclaration) obj;
                            if (Util.IsAImporedElement(decl, xsdString)) {
                                isImported = true;
                            }
                        }
                        if (!isImported) {
                            XSDAnnotationsStructure annotion = new XSDAnnotationsStructure((XSDComponent) obj);
                            // see 7993, if UUID/AUTO_INCREMENT ,should not add write access
                            if (obj instanceof XSDParticle) {
                                XSDParticle o = (XSDParticle) obj;
                                // String name=Util.getFirstTextNode(o.getElement(), "@name");
                                String type = Util.getFirstTextNode(o.getElement(), "@type");//$NON-NLS-1$
                                if (EUUIDCustomType.AUTO_INCREMENT.equals(type) || EUUIDCustomType.UUID.equals(type)) {
                                    objList.remove(obj);
                                    objs = objList.toArray();
                                    continue;
                                }
                            }

                            for (String access : roleMap.keySet()) {
                                Collection<String> values = roleMap.get(access);
                                annotion.removeAppInfos(access); // X_Write
                                for (String value : values) {
                                    annotion.addAppInfo(access, value);
                                }
                            }
                        }
                    }
                    objList.remove(obj);
                    objs = objList.toArray();
                }
            }

            for (String access : roleMap.keySet()) {
                List<String> values = roleMap.get(access);
                setAccessRole(values, access);
            }

        } else {
            if (Util.IsAImporedElement(declaration, xsdString)) {
                return false;
            }
            for (String access : roleMap.keySet()) {
                List<String> values = roleMap.get(access);
                setAccessRole(values, access);
            }
        }
        return true;
    }

    private boolean setAccessRole(List<String> roles, String access) {
        removeAppInfos(access); // X_Write X_Hide
        for (String role : roles) {
            addAppInfo(access, role);
        }

        hasChanged = true;
        return true;
    }

    public Map<String, String> getWriteAccesses() {
        return getAppInfos(IAnnotationConst.KEY_WRITE);
    }

    public Map<String, String> getNoAdd() {
        return getAppInfos(IAnnotationConst.KEY_NO_ADD);
    }

    public Map<String, String> getNoRemove() {
        return getAppInfos(IAnnotationConst.KEY_NO_REMOVE);
    }

    /****************************************************************************
     * X_Deny_PhysicalDelete
     ****************************************************************************/

    public Map<String, String> getDenyPhysicalDelete() {
        return getAppInfos(IAnnotationConst.KEY_NO_PHYSICAL_DELETE);
    }

    /****************************************************************************
     * X_Deny_Create
     ****************************************************************************/
    public Map<String, String> getDenyCreate() {
        return getAppInfos(IAnnotationConst.KEY_NO_CREATE);
    }

    /****************************************************************************
     * X_Deny_LogicalDelete
     ****************************************************************************/

    public Map<String, String> getDenyLogicalDelete() {
        return getAppInfos(IAnnotationConst.KEY_NO_LOGIC_DELETE);
    }

    public Map<String, String> getLookupFields() {
        return getAppInfos(IAnnotationConst.KEY_LOOKUP_FIELD);
    }

    public Map<String, String> getPrimaryKeyInfos() {
        return getAppInfos(IAnnotationConst.KEY_PK_INFO);
    }

    /****************************************************************************
     * HIDDEN ACCESSES
     ****************************************************************************/

    public boolean setHiddenAccess(int num, String role) {
        Map<String, String> infos = getHiddenAccesses();
        infos.put(IAnnotationConst.KEY_PREFIX_NO_ACESS + num, role);
        return setForeignKeyInfos(new ArrayList<String>(infos.values()));
    }

    public Map<String, String> getHiddenAccesses() {
        return getAppInfos(IAnnotationConst.KEY_NO_ACESS);
    }

    /****************************************************************************
     * SOURCE SYSTEM
     ****************************************************************************/

    public boolean setSourceSystem(String sourceSystem) {
        boolean somethingChanged = setAppInfo(IAnnotationConst.KEY_SOURCE_SYSTEM, sourceSystem, true);
        hasChanged = hasChanged | somethingChanged;
        return somethingChanged;
    }

    public String getSourceSystem() {
        return getAppInfoValue(IAnnotationConst.KEY_SOURCE_SYSTEM);
    }

    /****************************************************************************
     * TARGET SYSTEMS
     ****************************************************************************/
    public boolean setTargetSystems(List<String> systems) {
        removeAppInfos(IAnnotationConst.KEY_TARGET_SYSTEM);
        for (String role : systems) {
            addAppInfo(IAnnotationConst.KEY_TARGET_SYSTEM, role);
        }
        hasChanged = true;
        return true;
    }

    public boolean setTargetSystem(int num, String system) {
        Map<String, String> infos = getTargetSystems();
        infos.put("X_TargetSystem_" + num, system);//$NON-NLS-1$
        return setForeignKeyInfos(new ArrayList<String>(infos.values()));
    }

    public Map<String, String> getTargetSystems() {
        return getAppInfos(IAnnotationConst.KEY_TARGET_SYSTEM);
    }

    /*************************************************************
     * Schematron Rule
     *************************************************************/
    public boolean setSchematrons(Collection<String> systems) {
        removeAppInfos(ICoreConstants.X_Schematron);
        for (String role : systems) {
            addAppInfo(ICoreConstants.X_Schematron, role);
        }
        hasChanged = true;
        return true;
    }

    public boolean setSchematron(int num, String system) {
        Map<String, String> infos = getSchematrons();
        infos.put(ICoreConstants.X_Schematron + "_" + num, system);//$NON-NLS-1$
        setSchematrons(infos.values());
        // return setForeignKeyInfos(new ArrayList(infos.values()));
        return true;
    }

    public void addSchematron(String pattern) {
        Map<String, String> infos = getSchematrons();
        infos.put(ICoreConstants.X_Schematron + "_" + (infos.size() + 1), pattern);//$NON-NLS-1$
        setSchematrons(infos.values());
    }

    public void removeSchematron(String pattern) {
        Map<String, String> infos = getSchematrons();
        for (Entry<String, String> entry : infos.entrySet()) {
            if (pattern.equals(entry.getValue())) {
                infos.remove(entry.getKey());
                break;
            }
        }
        setSchematrons(infos.values());
    }

    public Map<String, String> getSchematrons() {
        Map<String, String> targetSystems = new TreeMap<String, String>();
        Map<String, String> appInfos = getAppInfos(ICoreConstants.X_Schematron);
        Set<String> keys = appInfos.keySet();
        for (String key : keys) {
            String v = appInfos.get(key);
            if (v == null || v.trim().length() == 0) {
                continue;
            }
            targetSystems.put(key, appInfos.get(key));
        }
        return targetSystems;
    }

    /*************************************************************
     * Workflow
     *************************************************************/
    public boolean setWorkflows(Collection<String> systems) {
        removeAppInfos(ICoreConstants.X_Workflow);
        for (String role : systems) {
            addAppInfo(ICoreConstants.X_Workflow, role);
        }
        hasChanged = true;
        return true;
    }

    public boolean setWorkflow(int num, String system) {
        Map<String, String> infos = getSchematrons();
        infos.put(ICoreConstants.X_Workflow + "_" + num, system);//$NON-NLS-1$
        setSchematrons(infos.values());
        return true;
    }

    public void addWorkflow(String pattern) {
        Map<String, String> infos = getSchematrons();
        infos.put(ICoreConstants.X_Workflow + "_" + (infos.size() + 1), pattern);//$NON-NLS-1$
        setSchematrons(infos.values());
    }

    public void removeWorkflow(String pattern) {
        Map<String, String> infos = getSchematrons();
        for (Entry<String, String> entry : infos.entrySet()) {
            if (pattern.equals(entry.getValue())) {
                infos.remove(entry.getKey());
                break;
            }
        }
        setSchematrons(infos.values());
    }

    public Map<String, String> getWorkflows() {
        Map<String, String> targetSystems = new TreeMap<String, String>();
        Map<String, String> appInfos = getAppInfos(ICoreConstants.X_Workflow);
        Set<String> keys = appInfos.keySet();
        for (String key : keys) {
            String v = appInfos.get(key);
            if (v == null || v.trim().length() == 0) {
                continue;
            }
            targetSystems.put(key, appInfos.get(key));
        }
        return targetSystems;
    }

    /**
     * return all existed category names, it is simple wrapper for getCategoryFromElement() method
     * 
     * @return return a Set object included all category names
     */
    public Set<String> getCategoryNamesFromEntity() {
        Set<String> categoryNames = new HashSet<>();

        for (Element element : annotation.getApplicationInformation()) {
            Map<String, Map<String, String>> category = getCategoryFromElement(element);
            categoryNames.addAll(category.keySet());
        }
        return categoryNames;
    }

    /**
     * Return filed-> category map for current entity.
     * 
     * @return filed-> category map
     */
    public Map<String, String> getFieldCategoryMap() {
        Map<String, String> fieldMap = new HashMap<>();

        for (Element element : annotation.getApplicationInformation()) {
            String name = element.getLocalName();
            if (APPINFO.equals(name.toLowerCase())) {
                name = element.getAttribute(SOURCE);
                if (name.equals(IAnnotationConst.KEY_CATEGORY)) {
                    NodeList childNodes = element.getChildNodes();
                    List<String> fields = new ArrayList<>();
                    String categoryName = null;
                    for (int i = 0; i < childNodes.getLength(); i++) {
                        Node node = childNodes.item(i);
                        String localName = node.getLocalName();
                        if (localName != null) {
                            Node subChild = node.getFirstChild();
                            if (subChild != null) {
                                String nodeValue = subChild.getNodeValue();
                                if (nodeValue != null) {
                                    if (localName.equalsIgnoreCase(CATEGORY_NAME)) {
                                        categoryName = nodeValue;
                                    } else if (localName.equalsIgnoreCase(CATEGORY_FIELD) && nodeValue != null) {
                                        fields.add(nodeValue);
                                    }
                                }
                            }
                        }
                    }
                    if (categoryName != null) {
                        for (String field : fields) {
                            fieldMap.put(field, categoryName);
                        }
                    }
                }
            }
        }
        return fieldMap;
    }

    /**
     * Set fields for category
     * 
     * @param fieldMap field->category map
     * @return return true when field is updated successfully.
     */
    public boolean setCategoryFields(Map<String, String> fieldMap) {
        if (fieldMap == null) {
            return false;
        }
        // convert the map from field->category to category-List(fields)
        Map<String, List<String>> categoryFieldsMap = new LinkedHashMap<>();
        for (String field : fieldMap.keySet()) {
            String category = fieldMap.get(field);
            if (!categoryFieldsMap.containsKey(category)) {
                categoryFieldsMap.put(category, new ArrayList<String>());
            }
            categoryFieldsMap.get(category).add(field);
        }
        for (Element element : annotation.getApplicationInformation()) {
            String name = element.getLocalName();
            if (APPINFO.equals(name.toLowerCase())) {
                name = element.getAttribute(SOURCE);
                if (name.equals(IAnnotationConst.KEY_CATEGORY)) {
                    NodeList childNodes = element.getChildNodes();
                    List<Node> fieldNodes = new ArrayList<>();
                    String categoryName = null;
                    for (int i = 0; i < childNodes.getLength(); i++) {
                        Node node = childNodes.item(i);
                        String localName = node.getLocalName();
                        if (localName != null) {
                            Node subChild = node.getFirstChild();
                            if (subChild != null) {
                                String nodeValue = subChild.getNodeValue();
                                if (nodeValue != null) {
                                    if (localName.equalsIgnoreCase(CATEGORY_NAME)) {
                                        categoryName = nodeValue;
                                    } else if (localName.equalsIgnoreCase(CATEGORY_FIELD) && nodeValue != null) {
                                        fieldNodes.add(node);
                                    }
                                }
                            }
                        }
                    }
                    if (categoryName != null) {
                        for (Node fieldNode : fieldNodes) {
                            element.removeChild(fieldNode);
                        }
                        List<String> fields = categoryFieldsMap.get(categoryName);
                        if (fields != null) {
                            for (String field : fields) {
                                Element fieldElement = element.getOwnerDocument().createElementNS(null, CATEGORY_FIELD);
                                Node fieldValue = element.getOwnerDocument().createTextNode(field);
                                fieldElement.appendChild(fieldValue);
                                element.appendChild(fieldElement);
                                hasChanged = true;
                            }
                        }
                    }
                }
            }
        }

        if (hasChanged) {
            annotation.updateElement();
            return true;
        }
        return false;

    }

    /**
     * Query all categories definitions
     * 
     * @param element annotation element
     * @return this method return a map,( key=> category name, value => multi-language labels,such as<en:categorylabel>
     * )
     */
    public Map<String, Map<String, String>> getCategoryFromElement(Element element) {
        Map<String, Map<String, String>> categories = new LinkedHashMap<>();
        String name = element.getLocalName();
        if (APPINFO.equals(name.toLowerCase())) {
            name = element.getAttribute(SOURCE);
            if (name.equals(IAnnotationConst.KEY_CATEGORY)) {
                NodeList childNodes = element.getChildNodes();
                Map<String, String> labels = new LinkedHashMap<>();
                String categoryName = null;
                for (int i = 0; i < childNodes.getLength(); i++) {
                    Node node = childNodes.item(i);
                    String localName = node.getLocalName();
                    if (localName != null) {
                        Node subChild = node.getFirstChild();
                        if (subChild != null) {
                            String nodeValue = subChild.getNodeValue();
                            if (nodeValue != null) {
                                if (localName.equalsIgnoreCase(CATEGORY_NAME)) {
                                    categoryName = nodeValue;
                                } else if (localName.startsWith("label_")) {
                                    String lang = localName.substring(6);
                                    labels.put(lang, nodeValue);
                                }
                            }
                        }
                    }
                }
                if (categoryName != null) {
                    categories.put(categoryName, labels);
                }
            }
        }
        return categories;
    }

    /**
     * Create or replace a category with multi-language
     * 
     * @param oldNode when oldNode is null then create a new category, else replace category of oldNode with new one.
     * category
     * @param categoryName
     * @param labels multi-language labels
     * @return return True when create/replace successfully
     */
    public boolean setCategory4Entity(Element oldNode, String categoryName, Map<String, String> labels) {
        if (categoryName == null) {
            return false;
        }
        if (declaration != null) {
            if (declaration.getAnnotation() == null) {
                declaration.setAnnotation(annotation);
            }
        }

        if (oldNode == null) {
            Element appinfo = annotation.createApplicationInformation(IAnnotationConst.KEY_CATEGORY);
            Element element = appinfo.getOwnerDocument().createElementNS(null, CATEGORY_NAME);
            Node nameValue = appinfo.getOwnerDocument().createTextNode(categoryName);
            element.appendChild(nameValue);
            appinfo.appendChild(element);
            if (labels != null) {
                for (String lang : labels.keySet()) {
                    String labelValue = labels.get(lang);
                    Element langElement = appinfo.getOwnerDocument().createElementNS(null, "label_" + lang);
                    Node labelNode = appinfo.getOwnerDocument().createTextNode(labelValue);
                    langElement.appendChild(labelNode);
                    appinfo.appendChild(langElement);
                }
            }
            annotation.getElement().appendChild(appinfo);
        } else {
            NodeList childNodes = oldNode.getChildNodes();
            List<Node> labelNodes = new ArrayList<>();

            for (int i = 0; i < childNodes.getLength(); i++) {
                Node node = childNodes.item(i);
                String localName = node.getLocalName();
                if (localName != null) {
                    Node subChild = node.getFirstChild();
                    if (subChild != null) {
                        String nodeValue = subChild.getNodeValue();
                        if (nodeValue != null) {
                            if (localName.equalsIgnoreCase(CATEGORY_NAME) && !categoryName.equals(nodeValue)) {
                                subChild.setNodeValue(categoryName);
                            } else if (localName.startsWith("label_")) {
                                labelNodes.add(node);
                            }
                        }
                    }
                }
            }

            for (Node fieldNode : labelNodes) {
                oldNode.removeChild(fieldNode);
            }
            if (labels != null) {
                for (String lang : labels.keySet()) {
                    String labelValue = labels.get(lang);
                    Element langElement = oldNode.getOwnerDocument().createElementNS(null, "label_" + lang);
                    Node labelNode = oldNode.getOwnerDocument().createTextNode(labelValue);
                    langElement.appendChild(labelNode);
                    oldNode.appendChild(langElement);
                }
            }
        }
        hasChanged = true;
        return true;
    }

    /**
     * Remove the category element definition from annotation.
     * 
     * @param categoryElement
     * @return true when it run successful.
     */
    public boolean removeCategory(Element categoryElement) {
        if (removeAnnotation(categoryElement)) {
            hasChanged = true;
            return true;
        }
        return false;
    }

    /*************************************************************
     * Multilingual facet error messages
     *************************************************************/
    /**
     * author: fliu, set Multilingual facet error messages attached to facets in the schema please refer to bug 0009157
     */
    public boolean setFactMessage(Map<String, String> facts) {
        Iterator<String> isos = Util.iso2lang.keySet().iterator();
        while (isos.hasNext()) {
            String lang = isos.next();
            removeAppInfos(IAnnotationConst.KEY_PREFIX_FACET + lang.toUpperCase());
        }

        Iterator<String> isoIter = facts.keySet().iterator();
        while (isoIter.hasNext()) {
            String iso = isoIter.next();
            removeAppInfos(IAnnotationConst.KEY_PREFIX_FACET + iso.toUpperCase());
            addAppInfo(IAnnotationConst.KEY_PREFIX_FACET + iso.toUpperCase(), facts.get(iso));
        }

        hasChanged = true;
        return true;
    }

    /**
     * author: fliu, get Multilingual facet error messages attached to facets in the schema please refer to bug 0009157
     */
    public Map<String, String> getFactMessage() {
        LinkedHashMap<String, String> descriptionsMap = new LinkedHashMap<String, String>();
        Iterator<String> isoIter = Util.iso2lang.keySet().iterator();
        while (isoIter.hasNext()) {
            String iso = isoIter.next().toUpperCase();
            String prefix = IAnnotationConst.KEY_PREFIX_FACET + iso;
            String infoValue = getAppInfoValue(prefix);
            if (infoValue != null) {
                descriptionsMap.put(iso.toLowerCase(), infoValue);
            }
        }

        return descriptionsMap;
    }

    /**
     * @author ymli set the formats; fix the bug:0013463
     * @param fomats
     * @return
     */
    public boolean setDisplayFormat(Map<String, String> fomats) {
        Iterator<String> isos = Util.iso2lang.keySet().iterator();
        while (isos.hasNext()) {
            String lang = isos.next();
            removeAppInfos(IAnnotationConst.KEY_PREFIX_DISPLAY_FORMAT + lang.toUpperCase());
        }

        Iterator<String> isoIter = fomats.keySet().iterator();
        while (isoIter.hasNext()) {
            String iso = isoIter.next();
            removeAppInfos(IAnnotationConst.KEY_PREFIX_DISPLAY_FORMAT + iso.toUpperCase());
            addAppInfo(IAnnotationConst.KEY_PREFIX_DISPLAY_FORMAT + iso.toUpperCase(), fomats.get(iso));
        }

        hasChanged = true;
        return true;
    }

    /**
     * @author ymli get the formats; fix the bug:0013463
     * @param fomats
     * @return
     */
    public Map<String, String> getDisplayFormat() {
        LinkedHashMap<String, String> descriptionsMap = new LinkedHashMap<String, String>();
        Iterator<String> isoIter = Util.iso2lang.keySet().iterator();
        while (isoIter.hasNext()) {
            String iso = isoIter.next().toUpperCase();
            String prefix = IAnnotationConst.KEY_PREFIX_DISPLAY_FORMAT + iso;
            String infoValue = getAppInfoValue(prefix);
            if (infoValue != null) {
                descriptionsMap.put(iso.toLowerCase(), infoValue);
            }
        }

        return descriptionsMap;
    }

    /****************************************************************************
     * FKIntegerity and FKIntegrity_Override
     ****************************************************************************/

    public boolean setFKIntegrity(Boolean integrity) {
        boolean somethingChanged = setAppInfo(IAnnotationConst.KEY_FOREIGN_KEY_INTEGRITY,
                integrity == null ? null : integrity.toString(), true);
        hasChanged = hasChanged | somethingChanged;
        return somethingChanged;
    }

    public boolean setFKIntegrityOverride(Boolean integrityOverride) {
        boolean somethingChanged = setAppInfo(IAnnotationConst.KEY_FOREIGN_KEY_INTEGRITY_OVERRIDE,
                integrityOverride == null ? null : integrityOverride.toString(), true);
        hasChanged = hasChanged | somethingChanged;
        return somethingChanged;
    }

    /****************************************************************************
     * APPINFO UTILITIES
     ****************************************************************************/

    private Element getAppInfo(String regex) {
        Element appInfo = null;
        ArrayList<Element> list = new ArrayList<Element>();
        // list.addAll(annotation.getUserInformation());
        if (annotation == null) {
            return null;
        }
        list.addAll(annotation.getApplicationInformation());
        for (Element ann : list) {
            String name = ann.getLocalName();
            if (APPINFO.equals(name.toLowerCase())) {
                name = ann.getAttribute(SOURCE);
                if (name.matches(regex)) {
                    appInfo = ann;
                    break;
                }
            }
        }
        return appInfo;
    }

    private String getAppInfoValue(String regex) {
        Element appInfo = getAppInfo(regex);
        if (appInfo == null) {
            return null;
        }
        return appInfo.getFirstChild().getNodeValue();
    }

    private Map<String, String> getAppInfos(String regex) {
        LinkedHashMap<String, String> appInfos = new LinkedHashMap<String, String>();
        ArrayList<Element> list = new ArrayList<Element>();
        // list.addAll(annotation.getUserInformation());
        list.addAll(annotation.getApplicationInformation());
        int i = 0;
        for (Element ann : list) {
            String name = ann.getLocalName();
            if (APPINFO.equals(name.toLowerCase())) {
                name = ann.getAttribute(SOURCE);
                if (name.equals(regex)) {
                    appInfos.put(name + "_" + i, ann.getFirstChild().getNodeValue());//$NON-NLS-1$
                    i++;
                } else if (name.matches(regex)) {
                    appInfos.put(name, ann.getFirstChild().getNodeValue());
                }
            }
        }
        return appInfos;
    }

    private boolean removeAppInfos(String regex) {
        boolean somethingRemoved = false;
        Element appInfo = null;
        do {
            appInfo = getAppInfo(regex);
            if (appInfo != null) {
                annotation.getElement().removeChild(appInfo);
                annotation.getApplicationInformation().remove(appInfo); // yes we need to do that too....
                somethingRemoved = true;
            }
        } while (appInfo != null);
        return somethingRemoved;
    }

    private boolean removeAnnotation(Node node) {
        if (node != null) {
            annotation.getElement().removeChild(node);
            return annotation.getApplicationInformation().remove(node); // yes we need to do that too....
        }
        return false;
    }

    private boolean setAppInfo(String type, String value, boolean overwrite) {
        if (value == null || value.length() == 0) {
            boolean wasRemoved = removeAppInfos(type);
            hasChanged = hasChanged | wasRemoved;
            return wasRemoved;
        }

        if (!overwrite) {
            return addAppInfo(type, value);
        }

        Element ann = getAppInfo(type);
        if (ann == null) {
            return addAppInfo(type, value);
        }

        // change existing one if
        // first make sure the annotation is not brain new and is attached
        if (declaration.getAnnotation() == null) {
            declaration.setAnnotation(annotation);
        }
        // then create the appinfo
        String existingText = ann.getFirstChild().getNodeValue();
        if (!value.equals(existingText)) {
            ann.getFirstChild().setNodeValue(value);
            hasChanged = true;
            return true;
        }
        return false;
    }

    private boolean addAppInfo(String type, String value) {
        // if (declaration == null) {
        // return true;
        // }
        if (declaration != null) {
            if (declaration.getAnnotation() == null) {
                declaration.setAnnotation(annotation);
            }
        } else if (complexTypeDef != null && type.startsWith(IAnnotationConst.KEY_LABEL)) {
            if (complexTypeDef.getAnnotation() == null) {
                complexTypeDef.setAnnotation(annotation);
            }
        } else {
            return true;
        }

        Element appinfo = annotation.createApplicationInformation(type);
        Node text = appinfo.getOwnerDocument().createTextNode(value);
        appinfo.appendChild(text);
        annotation.getElement().appendChild(appinfo);
        hasChanged = true;
        return true;
    }

    public boolean hasChanged() {
        ArrayList<Element> list = new ArrayList<Element>();
        list.addAll(annotation.getApplicationInformation());
        list.addAll(annotation.getUserInformation());

        if (componet != null) {
            hasChanged = true;
        }
        if (list.size() == 0) {
            // remove the annotation from the declaration
            if (declaration != null && declaration.getAnnotation() != null) {
                declaration.getElement().removeChild(annotation.getElement());
                declaration.setAnnotation(null);
                hasChanged = true;
            }
        } else {
            // attach the annotation to the declaration
            if (declaration != null && declaration.getAnnotation() == null) {
                declaration.setAnnotation(annotation);
                hasChanged = true;
            }
        }
        return hasChanged;
    }

    /****************************************************************************
     * BEAN
     ****************************************************************************/

    public XSDAnnotation getAnnotation() {
        return annotation;
    }

    public void setAnnotation(XSDAnnotation annotation) {
        this.annotation = annotation;
    }

    public XSDElementDeclaration getDeclaration() {
        return declaration;
    }

    public void setDeclaration(XSDElementDeclaration declaration) {
        this.declaration = declaration;
    }

    public void setXSDSchema(XSDSchema schma) {
        schema = schma;
    }

}
