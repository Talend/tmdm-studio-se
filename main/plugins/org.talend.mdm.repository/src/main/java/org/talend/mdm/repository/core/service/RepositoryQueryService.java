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
package org.talend.mdm.repository.core.service;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.apache.log4j.Logger;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.IServerObjectRepositoryType;
import org.talend.mdm.repository.model.mdmproperties.MDMServerObjectItem;
import org.talend.mdm.repository.model.mdmserverobject.MDMServerObject;
import org.talend.mdm.repository.model.mdmserverobject.WSCustomFormE;
import org.talend.mdm.repository.model.mdmserverobject.WSDataModelE;
import org.talend.mdm.repository.model.mdmserverobject.WSRoleE;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;

import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.utils.IConstants;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class RepositoryQueryService {

    private static Logger log = Logger.getLogger(RepositoryQueryService.class);

    private static BidiMap typeMap = null;

    public static List<String> findAllDataModelNames() {
        return findAllServerObjectNames(IServerObjectRepositoryType.TYPE_DATAMODEL);
    }

    public static List<String> findAllDataContainerNames() {
        return findAllServerObjectNames(IServerObjectRepositoryType.TYPE_DATACLUSTER);
    }

    public static List<String> findAllRoleNames() {
        return findAllServerObjectNames(IServerObjectRepositoryType.TYPE_ROLE);
    }

    public static List<String> findAllProcessNames() {
        return findAllServerObjectNames(IServerObjectRepositoryType.TYPE_TRANSFORMERV2);
    }

    public static WSDataModelE findDataModelByName(String name) {
        return (WSDataModelE) findServerObjectByName(IServerObjectRepositoryType.TYPE_DATAMODEL, name);
    }

    public static WSRoleE findRoleByName(String name) {
        return (WSRoleE) findServerObjectByName(IServerObjectRepositoryType.TYPE_ROLE, name);
    }

    public static List<String> findAllMenuNames() {
        return findAllServerObjectNames(IServerObjectRepositoryType.TYPE_MENU);
    }

    public static List<String> findAllRoutingRuleNames() {
        return findAllServerObjectNames(IServerObjectRepositoryType.TYPE_ROUTINGRULE);
    }

    public static List<String> findAllWorkflowNames() {
        List<IRepositoryViewObject> viewObjects = RepositoryResourceUtil
                .findAllViewObjects(IServerObjectRepositoryType.TYPE_WORKFLOW);
        List<String> names = new LinkedList<String>();
        if (viewObjects != null) {
            for (IRepositoryViewObject viewObj : viewObjects) {
                Item item = viewObj.getProperty().getItem();
                if (item instanceof MDMServerObjectItem) {
                    MDMServerObjectItem serverItem = (MDMServerObjectItem) item;
                    names.add(serverItem.getMDMServerObject().getName() + "_" + item.getProperty().getVersion()); //$NON-NLS-1$
                }
                // names[i]=viewObj.getLabel();
            }
        }
        return names;
    }

    public static List<String> findAllStoredProcedureNames() {
        return findAllServerObjectNames(IServerObjectRepositoryType.TYPE_STOREPROCEDURE);
    }

    public static List<String> findAllViewNames() {
        return findAllServerObjectNames(IServerObjectRepositoryType.TYPE_VIEW);
    }

    public static List<String> findAllTransformerV2Names() {
        return findAllServerObjectNames(IServerObjectRepositoryType.TYPE_TRANSFORMERV2);
    }

    public static List<String> findAllCustomFormNames() {
        return findAllServerObjectNames(IServerObjectRepositoryType.TYPE_CUSTOM_FORM);
    }

    /**
     * this method is compatible with Util.getDataModel, but for Repository view
     *
     * @param obj
     * @param datamodel
     * @param conceptName
     * @return
     */
    public static List<String> getDataModel(String datamodel, String conceptName) {
        List<String> systemDataModelValues = findAllDataModelNames();

        // filter the datamodel according to conceptName
        List<String> avaiList = new LinkedList<String>();
        avaiList.addAll(systemDataModelValues);
        if (datamodel != null) {
            avaiList.clear();
            avaiList.add(datamodel);
        } else if (conceptName != null && !conceptName.contains("*")) {//$NON-NLS-1$
            for (String data : systemDataModelValues) {
                try {
                    WSDataModelE dm = findDataModelByName(data);
                    if (dm != null) {
                        String schema = dm.getXsdSchema();
                        Pattern p = Pattern.compile("<xsd:element(.*?)name=\"" + conceptName + "\"");//$NON-NLS-1$//$NON-NLS-2$
                        if (!p.matcher(schema).find()) {
                            avaiList.remove(data);
                        }
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        if (avaiList.size() == 0) {
            avaiList.addAll(systemDataModelValues);
        }
        return avaiList;
    }

    private static List<String> findAllServerObjectNames(ERepositoryObjectType type) {
        List<IRepositoryViewObject> viewObjects = RepositoryResourceUtil.findAllViewObjects(type);
        List<String> names = new LinkedList<String>();
        if (viewObjects != null) {
            for (IRepositoryViewObject viewObj : viewObjects) {
                Item item = viewObj.getProperty().getItem();
                if (item instanceof MDMServerObjectItem) {
                    String name = ((MDMServerObjectItem) item).getMDMServerObject().getName();
                    if (name != null) {
                        names.add(name);
                    } else {
                        names.add(viewObj.getLabel());
                    }
                }
                // names[i]=viewObj.getLabel();
            }
        }
        return names;
    }

    public static List<String> findAllCustomFormPks() {
        List<IRepositoryViewObject> viewObjects = RepositoryResourceUtil
                .findAllViewObjects(IServerObjectRepositoryType.TYPE_CUSTOM_FORM);
        List<String> names = new LinkedList<String>();
        if (viewObjects != null) {
            for (IRepositoryViewObject viewObj : viewObjects) {
                Item item = viewObj.getProperty().getItem();
                if (item instanceof MDMServerObjectItem) {
                    MDMServerObject sObject = ((MDMServerObjectItem) item).getMDMServerObject();
                    if (sObject instanceof WSCustomFormE) {
                        WSCustomFormE sForm = (WSCustomFormE) sObject;
                        String name = sForm.getDatamodel() + IConstants.ITEM_PK_SPLIT + sForm.getEntity()
                                + IConstants.ITEM_PK_SPLIT + sForm.getName();
                        if (name != null) {
                            names.add(name);
                        } else {
                            names.add(viewObj.getLabel());
                        }
                    }
                }
                // names[i]=viewObj.getLabel();
            }
        }
        return names;
    }

    public static MDMServerObject findServerObjectByName(ERepositoryObjectType type, String name) {
        List<IRepositoryViewObject> viewObjects = RepositoryResourceUtil.findAllViewObjects(type);
        if (viewObjects != null) {
            for (IRepositoryViewObject viewObj : viewObjects) {
                Item item = viewObj.getProperty().getItem();
                if (item instanceof MDMServerObjectItem) {
                    MDMServerObject serverObject = ((MDMServerObjectItem) item).getMDMServerObject();
                    if (serverObject.getName() != null && serverObject.getName().equals(name)) {
                        return serverObject;
                    } else if (viewObj.getLabel().equals(name)) {
                        return serverObject;
                    }
                }
            }
        }
        return null;
    }

    public static MDMServerObjectItem findServerObjectItemByNameWithDeleted(ERepositoryObjectType type, String name,
            boolean withDeleted) {
        List<IRepositoryViewObject> viewObjects = RepositoryResourceUtil.findAllViewObjects(type, true, withDeleted);
        if (viewObjects != null) {
            for (IRepositoryViewObject viewObj : viewObjects) {
                Item item = viewObj.getProperty().getItem();
                if (item instanceof MDMServerObjectItem) {
                    MDMServerObject serverObject = ((MDMServerObjectItem) item).getMDMServerObject();
                    if (name != null && name.equals(serverObject.getName())) {
                        return (MDMServerObjectItem) item;
                    }
                }
            }
        }
        return null;
    }

    public static MDMServerObjectItem findServerObjectItemByName(ERepositoryObjectType type, String name) {
        return findServerObjectItemByNameWithDeleted(type, name, false);
    }

    private static void initTypeMap() {
        if (typeMap == null) {
            typeMap = new DualHashBidiMap();
            typeMap.put(TreeObject.CUSTOM_FORM, IServerObjectRepositoryType.TYPE_CUSTOM_FORM);
            typeMap.put(TreeObject.DATA_MODEL, IServerObjectRepositoryType.TYPE_DATAMODEL);
            typeMap.put(TreeObject.DATA_CLUSTER, IServerObjectRepositoryType.TYPE_DATACLUSTER);
            typeMap.put(TreeObject.TRANSFORMER, IServerObjectRepositoryType.TYPE_TRANSFORMERV2);
            typeMap.put(TreeObject.MENU, IServerObjectRepositoryType.TYPE_MENU);
            typeMap.put(TreeObject.ROLE, IServerObjectRepositoryType.TYPE_ROLE);
            typeMap.put(TreeObject.ROUTING_RULE, IServerObjectRepositoryType.TYPE_ROUTINGRULE);
            typeMap.put(TreeObject.STORED_PROCEDURE, IServerObjectRepositoryType.TYPE_STOREPROCEDURE);
            typeMap.put(TreeObject.VIEW, IServerObjectRepositoryType.TYPE_VIEW);
            typeMap.put(TreeObject.PICTURES_RESOURCE, IServerObjectRepositoryType.TYPE_RESOURCE);
            typeMap.put(TreeObject.WORKFLOW_PROCESS, IServerObjectRepositoryType.TYPE_WORKFLOW);
        }
    }

    public static ERepositoryObjectType getRepositoryObjectType(int wsType) {
        initTypeMap();
        return (ERepositoryObjectType) typeMap.get(wsType);
    }

    public static int getWSType(ERepositoryObjectType type) {
        initTypeMap();
        Object key = typeMap.getKey(type);
        if (key != null) {
            return (Integer) key;
        }
        return -1;
    }
}
