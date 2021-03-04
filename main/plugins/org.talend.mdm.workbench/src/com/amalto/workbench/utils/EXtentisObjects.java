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
package com.amalto.workbench.utils;

import java.util.HashMap;
import java.util.Map;

import com.amalto.workbench.models.TreeObject;

/**
 * The XtentisObjects
 *
 * @author aiming
 *
 */
public enum EXtentisObjects {
    DataCluster("Data Container", "Data Cluster", TreeObject.DATA_CLUSTER, false), //$NON-NLS-1$//$NON-NLS-2$
    DataMODEL("Data Model", "Data Model", TreeObject.DATA_MODEL, true), //$NON-NLS-1$//$NON-NLS-2$
    DataMODELRESOURCE("Data Model", "Data Model Resource", TreeObject.DATA_MODEL_RESOURCE, true), //$NON-NLS-1$//$NON-NLS-2$
    DataMODELTYPESRESOURCE("Data Model Type", "Data Model Type Resource", TreeObject.DATA_MODEL_TYPES_RESOURCE, true), //$NON-NLS-1$//$NON-NLS-2$
    CUSTOMTYPESRESOURCE("Custom Type", "Custom Type Resource", TreeObject.CUSTOM_TYPES_RESOURCE), //$NON-NLS-1$//$NON-NLS-2$
    PICTURESRESOURCE("Pictures", "Pictures Resource", TreeObject.PICTURES_RESOURCE), //$NON-NLS-1$//$NON-NLS-2$
    Role("Role", "Role", TreeObject.ROLE, true), //$NON-NLS-1$//$NON-NLS-2$
    RoutingRule("Trigger", "Routing Rule", TreeObject.ROUTING_RULE, true), //$NON-NLS-1$//$NON-NLS-2$
    StoredProcedure("Stored Procedure", "Stored Procedure", TreeObject.STORED_PROCEDURE, true), //$NON-NLS-1$//$NON-NLS-2$
    Transformer("Process", "Transformer V2", TreeObject.TRANSFORMER, true), //$NON-NLS-1$//$NON-NLS-2$
    TransformerPlugin("Process Plugin", "Transformer Plugin V2", TreeObject.TRANSFORMER_PLUGIN), //$NON-NLS-1$//$NON-NLS-2$
    View("View", "View", TreeObject.VIEW, true), //$NON-NLS-1$//$NON-NLS-2$
    Menu("Menu", "Menu", TreeObject.MENU, true), //$NON-NLS-1$//$NON-NLS-2$
    SubscriptionEngine("Event Manager", "Subscription Engine", TreeObject.SUBSCRIPTION_ENGINE), //$NON-NLS-1$//$NON-NLS-2$
    ServiceConfiguration("Service Configuration", "Service Configuration", TreeObject.SERVICE_CONFIGURATION), //$NON-NLS-1$//$NON-NLS-2$
    Workflow("Workflow", "Workflow Processes", TreeObject.WORKFLOW), //$NON-NLS-1$//$NON-NLS-2$
    JobRegistry("Job Repository", "Job Repository", TreeObject.JOB_REGISTRY), //$NON-NLS-1$//$NON-NLS-2$
    Resources("Resources", "Resources", TreeObject.RESOURCES), //$NON-NLS-1$//$NON-NLS-2$
    EventManagement("Event Management", "Event Management", TreeObject.EVENT_MANAGEMENT), //$NON-NLS-1$//$NON-NLS-2$
    CustomType("Custom Type", "Custom Type", TreeObject.CUSTOM_TYPE);//$NON-NLS-1$//$NON-NLS-2$

    private String displayName;

    private String name;

    private int type;

    /**
     * check this object is need revision
     */
    private boolean isRevision = false;

    public boolean isRevision() {
        return isRevision;
    }

    public void setRevision(boolean isRevision) {
        this.isRevision = isRevision;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    EXtentisObjects(String displayName, String name, int type) {
        this.name = name;
        this.displayName = displayName;
        this.type = type;
    }

    EXtentisObjects(String displayName, String name, int type, boolean isRevision) {
        this.name = name;
        this.displayName = displayName;
        this.type = type;
        this.isRevision = isRevision;
    }

    // key is the type
    public static Map<String, EXtentisObjects> getXtentisObjexts() {

        Map<String, EXtentisObjects> map = new HashMap<String, EXtentisObjects>();
        for (int i = 0; i < values().length; i++) {
            map.put(String.valueOf(values()[i].getType()), values()[i]);
        }
        return map;
    }

    public static String getXtentisObjectName(String displayname) {
        for (int i = 0; i < values().length; i++) {
            if (values()[i].displayName.equals(displayname)) {
                return values()[i].name;
            }
        }
        return null;
    }

    public static String getXtentisObjectDisplayName(String name) {
        for (int i = 0; i < values().length; i++) {
            if (values()[i].name.equals(name)) {
                return values()[i].displayName;
            }
        }
        return null;
    }
}
