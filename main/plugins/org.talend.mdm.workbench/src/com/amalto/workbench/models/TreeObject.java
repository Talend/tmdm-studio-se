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
package com.amalto.workbench.models;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IAdaptable;

import com.amalto.workbench.utils.EXtentisObjects;
import com.amalto.workbench.utils.LocalTreeObjectRepository;
import com.amalto.workbench.utils.UserInfo;

public class TreeObject implements IAdaptable, Comparable<TreeObject> {

    /**
     *
     */
    private static final String DEFAULT_PORT = "8180"; //$NON-NLS-1$

    public static final String UNKNOWN = "Unknown";//$NON-NLS-1$

    public final static int _ROOT_ = -1;

    public final static int _SERVER_ = 0;

    public final static int INBOUND_ADAPTOR = 1;

    public final static int INBOUND_PLUGIN = 2;

    public final static int DESTINATION = 3;

    public final static int OUTBOUND_ADAPTOR = 4;

    public final static int OUTBOUND_PLUGIN = 5;

    public final static int DOCUMENT = 6;

    public final static int ITEM = 7;

    public final static int VIEW = 8;

    public final static int DATA_MODEL = 9;

    public final static int SOURCE = 10;

    public final static int DATA_CLUSTER = 11;

    public final static int STORED_PROCEDURE = 12;

    public final static int ROLE = 13;

    public final static int ROUTING_RULE = 14;

    public final static int SUBSCRIPTION_ENGINE = 15;

    public final static int TRANSFORMER = 16;

    public final static int MENU = 17;

    public final static int TRANSFORMER_PLUGIN = 20;

    public final static int CATEGORY_FOLDER = 21;

    public final static int BUILT_IN_CATEGORY_FOLDER = 35;

    public final static int SERVICE_CONFIGURATION = 22;

    public final static int RESOURCES = 23;

    public final static int DATA_MODEL_RESOURCE = 24;

    public final static int DATA_MODEL_TYPES_RESOURCE = 25;

    public final static int CUSTOM_TYPES_RESOURCE = 26;

    public final static int PICTURES_RESOURCE = 27;

    public final static int CUSTOM_TYPE = 28;

    public final static int WORKFLOW = 29;

    public final static int WORKFLOW_PROCESS = 30;

    public final static int JOB_REGISTRY = 31;

    public final static int JOB = 32;

    public final static int EVENT_MANAGEMENT = 33;

    public final static int TIS_JOB = 34;

    public final static int _INVISIBLE = 99; // for first login use

    public final static int _ACTION_ = 100;

    public final static int _WIZARD_ = 101;

    public final static int DATA_CLUSTER_CONTENTS = 23;

    public final static int CUSTOM_FORM = 36;

    // export folder name
    public final static String DATACONTAINER = "datacontainer";//$NON-NLS-1$

    public final static String DATACONTAINER_COTENTS = "datacontainerContents";//$NON-NLS-1$

    public final static String DATAMODEL_ = "datamodel";//$NON-NLS-1$

    public final static String DATAMODELTYPES_ = "datamodelTypes";//$NON-NLS-1$

    public final static String PICTURES_ = "pictures";//$NON-NLS-1$

    public final static String MENU_ = "menu";//$NON-NLS-1$

    public final static String ROLE_ = "role";//$NON-NLS-1$

    public final static String ROUTINGRULE_ = "routingrule";//$NON-NLS-1$

    public final static String STOREDPROCEDURE_ = "storedprocedure";//$NON-NLS-1$

    public final static String TRANSFORMER_ = "transformer";//$NON-NLS-1$

    public final static String VIEW_ = "view";//$NON-NLS-1$

    public final static String DATAMODEL_URI = "/pubcomponent/secure/dataModels/";//$NON-NLS-1$

    public final static String DATAMODELTYPES_URI = "/pubcomponent/secure/dataModelsTypes/";//$NON-NLS-1$

    public final static String CUSTOM_TYPES_URI = "/pubcomponent/customTypesSets/";//$NON-NLS-1$

    public final static String PICTURES_URI = "/pubcomponent/pictures/";//$NON-NLS-1$

    public final static String BARFILE_URI = "/talendmdm/services/pubcomponent/barFile/";//$NON-NLS-1$

    public final static String BARFILE_PATH = "/workflow/";//$NON-NLS-1$

    private String name;

    private String displayName;

    private TreeParent parent;

    private TreeParent serverRoot;

    private Object wsKey;

    private Object wsObject;

    private int type;

    private boolean isXObject;

    private Object[] additionalInfo;

    private UserInfo user;

    protected ArrayList<IXObjectModelListener> listeners = null; // at root level only

    private String[] items; // export items

    public String[] getItems() {
        return items;
    }

    public void setItems(String[] items) {
        this.items = items;
    }

    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public TreeObject() {
    }

    /**
     * @param displayName
     * @param serverRoot
     * @param type
     * @param wsKey
     * @param wsObject
     */
    public TreeObject(String name, TreeParent serverRoot, int type, Object wsKey, Object wsObject) {
        this(name, serverRoot, type, wsKey, wsObject, null);
    }

    public TreeObject(String name, TreeParent serverRoot, int type, Object wsKey, Object wsObject, Object[] additionalInfo) {
        super();
        this.name = name;
        this.displayName = name;
        this.serverRoot = serverRoot;
        this.type = type;
        this.wsKey = wsKey;
        this.wsObject = wsObject;
        this.isXObject = true;
        this.additionalInfo = additionalInfo;

        addListener(LocalTreeObjectRepository.getInstance());
    }

    protected void setParent(TreeParent parent) {
        this.parent = parent;
    }

    public TreeParent getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return getDisplayName();
    }

    public Object getAdapter(Class key) {
        return null;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public TreeParent getServerRoot() {
        if (this.serverRoot == null) {
            if (this.type == TreeObject._SERVER_) {
                return (TreeParent) this; // we are the server root
            } else if (this.type == TreeObject._ROOT_) {
                if (((TreeParent) this).getChildren().length > 0) {
                    return (TreeParent) ((TreeParent) this).getChildren()[0]; // we are the root
                }
            }
        }
        return serverRoot;
    }

    public void setServerRoot(TreeParent serverRoot) {
        this.serverRoot = serverRoot;
    }

    public Object getWsObject() {
        return wsObject;
    }

    public void setWsObject(Object wsObject) {
        this.wsObject = wsObject;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        if (name == null) {
            // We shouldn't be forced to set the name too. Unfortunately due to a wrong design, most of the code is
            // based on the displayName of a TreeObject instead of its name. Below is a workaround to ensure name
            // is always set.
            this.name = displayName;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getWsKey() {
        return wsKey;
    }

    public void setWsKey(Object wsKey) {
        this.wsKey = wsKey;
    }

    public boolean isXObject() {
        return isXObject;
    }

    public void setXObject(boolean isXObject) {
        this.isXObject = isXObject;
    }

    public static String getParentPath(TreeObject obj) {
        if (obj == null || obj.getParent() == null) {
            return ""; //$NON-NLS-1$
        }
        if (obj.getParent().equals(obj.getServerRoot())) {
            return obj.getParent().getName() + "/" + obj.getName(); //$NON-NLS-1$
        } else {
            return getParentPath(obj.getParent()) + "/" + obj.getName(); //$NON-NLS-1$
        }
    }

    public String getPath() {
        String path = getParentPath(this.getParent());
        int pos = path.indexOf('/');
        if (pos != -1) {
            path = path.substring(pos + 1);
        }
        pos = path.indexOf('/');
        if (pos != -1) {
            return path.substring(pos + 1);
        }
        return ""; //$NON-NLS-1$
    }

    public void addListener(IXObjectModelListener listener) {
        if (listeners == null) {
            listeners = new ArrayList<IXObjectModelListener>();
            listeners.add(listener);
        } else {
            if (!listeners.contains(listener)) {
                listeners.add(listener);
            }
        }
    }

    public void removeListener(IXObjectModelListener listener) {
        if (listeners == null) {
            return;
        }
        if (listeners.contains(listener)) {
            listeners.remove(listener);
        }
    }

    public synchronized void fireEvent(int eventType, TreeObject objectParent, TreeObject child) {
        TreeObject current = this;
        do {
            if (current.listeners != null) {
                for (IXObjectModelListener listener : current.listeners) {
                    listener.handleEvent(eventType, objectParent, child);
                }
            }
            current = current.getParent();
        } while ((current != null));
    }

    public Object[] getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(Object[] additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public TreeParent findServerFolder(int objectType) {
        if (this.getServerRoot() == null) {
            return null;
        }

        TreeObject[] children = this.getServerRoot().getChildren();
        for (TreeObject element : children) {
            if (element instanceof TreeParent) {
                if (element.getType() == objectType) {
                    return (TreeParent) element;
                }
            }
        }
        if (objectType == TRANSFORMER || objectType == ROUTING_RULE) {
            TreeParent parent = findServerFolder(EVENT_MANAGEMENT);
            children = parent.getChildren();
            for (TreeObject obj : children) {
                if (obj.getType() == objectType) {
                    return (TreeParent) obj;
                }
            }
        }
        return null;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    private String username;

    private String password;

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        if (getServerRoot() != null) {
            return getServerRoot().getUser().getUsername();
        }
        return username;
    }

    public String getPassword() {
        if (getServerRoot() != null) {
            return getServerRoot().getUser().getPassword();
        }
        return password;
    }

    public String getEndpointAddress() {
        if (getServerRoot() != null) {
            return getServerRoot().getWsKey().toString();
        }
        return url;
    }

    public String getEndpointIpAddress() {

        String portAddress = getEndpointAddress();

        if (portAddress != null) {
            int endPos = portAddress.lastIndexOf(":");//$NON-NLS-1$
            int cutPos = portAddress.indexOf("/", endPos); //$NON-NLS-1$
            if (cutPos != -1) {
                return portAddress.substring(0, cutPos);
            }
        }

        return portAddress;
    }

    public String getEndpointHost() {

        String portAddress = getEndpointAddress();

        if (portAddress != null) {
            int startPos = portAddress.indexOf("://");//$NON-NLS-1$
            int endPos = portAddress.lastIndexOf(":");//$NON-NLS-1$
            if (endPos != -1 && startPos != -1) {
                return portAddress.substring(startPos + 3, endPos);
            }
        }

        return portAddress;
    }

    public String getEndpointPort() {

        String portAddress = getEndpointAddress();
        if (portAddress == null) {
            return DEFAULT_PORT;
        }
        Pattern p = Pattern.compile(":(\\d+?)/");//$NON-NLS-1$
        Matcher m = p.matcher(portAddress);
        if (m.find()) {
            return m.group(1);
        } else {
            return DEFAULT_PORT;
        }

    }

    public static String getTypeName(int type) {
        EXtentisObjects obj = EXtentisObjects.getXtentisObjexts().get(String.valueOf(type));
        if (obj != null) {
            return obj.getName();
        } else {
            return UNKNOWN;
        }
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof TreeObject) {
            TreeObject o = (TreeObject) obj;
            if (o.getParent() == null || getParent() == null) {
                return o.getName().equals(getName()) && getType() == o.getType();
            } else {

                if (o.getName().equals(getName()) && getType() == o.getType()) {
                    if (o.getParent() != null && getParent() != null) {
                        if (o.getParent().getName().equals(getParent().getName())) {
                            return true;
                        }
                    }
                }
            }

        }
        return false;
    }

    public int compareTo(TreeObject o) {
        if (o == this) {
            return 0;
        }

        if ((this instanceof TreeParent) && !(o instanceof TreeParent)) {
            return -1;
        } else if (!(this instanceof TreeParent) && (o instanceof TreeParent)) {
            return 1;
        } else if (!(this instanceof TreeParent) && !(o instanceof TreeParent)) {
            return this.getDisplayName().compareTo(o.getDisplayName());
        } else {
            TreeParent pThis = (TreeParent) this;
            TreeParent pThat = (TreeParent) o;
            return pThis.getDisplayName().compareTo(pThat.getDisplayName());
        }
    }

    @Override
    public int hashCode() {
        return this.displayName.hashCode() + this.type * 1000;

    }

}
