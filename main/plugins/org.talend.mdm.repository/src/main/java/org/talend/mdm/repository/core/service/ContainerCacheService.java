// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.ui.navigator.CommonViewer;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.model.repository.RepositoryObject;
import org.talend.core.model.repository.RepositoryViewObject;
import org.talend.mdm.repository.model.mdmproperties.ContainerItem;
import org.talend.mdm.repository.models.FolderRepositoryObject;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class ContainerCacheService {

    /**
     * 
     */
    private static final String DIVIDE = "/"; //$NON-NLS-1$

    private static Map<ERepositoryObjectType, Map<String, IRepositoryViewObject>> containerMap = new HashMap<ERepositoryObjectType, Map<String, IRepositoryViewObject>>();

    private static Map<String, IRepositoryViewObject> viewObjMap = new HashMap<String, IRepositoryViewObject>();

    public static void clearCache() {
        viewObjMap.clear();
        containerMap.clear();
    }

    public static void putContainer(IRepositoryViewObject viewObj) {
        Item item = viewObj.getProperty().getItem();
        if (item instanceof ContainerItem) {
            ERepositoryObjectType repObjType = ((ContainerItem) item).getRepObjType();
            Map<String, IRepositoryViewObject> map = containerMap.get(repObjType);
            if (map == null) {
                map = new HashMap<String, IRepositoryViewObject>();
                containerMap.put(repObjType, map);
            }
            //
            String path = correctPath(item.getState().getPath());
            map.put(path, viewObj);
        }
    }

    public static void put(Property prop, IRepositoryViewObject viewObj) {
        if (prop == null || viewObj == null) {
            throw new IllegalArgumentException();
        }
        if (viewObj instanceof RepositoryObject) {
            viewObj = createRepositoryViewObject(viewObj);
        }
        viewObjMap.put(prop.getId(), viewObj);
    }

    public static IRepositoryViewObject put(IRepositoryViewObject viewObj) {
        if (viewObj == null) {
            throw new IllegalArgumentException();
        }
        if (viewObj instanceof RepositoryObject) {
            viewObj = createRepositoryViewObject(viewObj);
        }
        viewObjMap.put(viewObj.getId(), viewObj);
        return viewObj;
    }

    private static IRepositoryViewObject createRepositoryViewObject(IRepositoryViewObject viewObj) {
        return new RepositoryViewObject(viewObj.getProperty());
    }

    public static void put(Collection<IRepositoryViewObject> viewObjs) {
        for (IRepositoryViewObject viewObj : viewObjs) {
            put(viewObj);
        }
    }

    public static void remove(Property prop) {
        if (prop != null) {
            remove(prop.getId());
        }
    }

    public static void remove(String propId) {
        if (propId != null) {
            viewObjMap.remove(propId);
        }
    }

    public static void removeContainer(ERepositoryObjectType repObjType, String path) {
        Map<String, IRepositoryViewObject> map = containerMap.get(repObjType);
        if (map != null) {
            for (Iterator<String> il = map.keySet().iterator(); il.hasNext();) {
                String next = il.next();
                if (next.startsWith(path)) {
                    IRepositoryViewObject viewObj = map.get(next);
                    if (viewObj != null && viewObj instanceof FolderRepositoryObject) {
                        if (viewObj.getChildren() != null) {
                            for (IRepositoryViewObject child : viewObj.getChildren()) {
                                remove(child.getProperty());
                            }
                        }
                    }
                    il.remove();
                }
            }
        }
    }

    private static String correctPath(String path) {
        if (path != null && path.length() > 0) {
            if (!path.startsWith(DIVIDE)) {
                return DIVIDE + path;
            }
        }
        return path;
    }

    public static IRepositoryViewObject get(ERepositoryObjectType repObjType, String path) {
        Map<String, IRepositoryViewObject> map = containerMap.get(repObjType);
        if (map != null) {
            path = correctPath(path);
            return map.get(path);
        }
        return null;
    }

    public static IRepositoryViewObject get(Property prop) {
        if (prop == null) {
            throw new IllegalArgumentException();
        }
        return viewObjMap.get(prop.getId());
    }

    public static IRepositoryViewObject get(String propId) {
        if (propId == null) {
            throw new IllegalArgumentException();
        }
        return viewObjMap.get(propId);
    }

    public static IRepositoryViewObject getParent(IRepositoryViewObject obj) {
        ERepositoryObjectType type = obj.getRepositoryObjectType();

        Item item = obj.getProperty().getItem();
        String path = item.getState().getPath();
        if (item instanceof ContainerItem) {
            if (path.length() > 1) {
                int pos = path.lastIndexOf(DIVIDE);
                if (pos >= 0) {
                    path = path.substring(0, pos);
                }
            } else {
                return null;
            }
        }
        return get(type, path);

    }

    public static void refreshRepositoryRoot(ERepositoryObjectType type, CommonViewer viewer) {
        Object input = viewer.getInput();
        if (input != null && input instanceof IRepositoryViewObject[]) {
            for (IRepositoryViewObject viewObject : (IRepositoryViewObject[]) input) {
                if (refreshRepositoryContainer(viewObject, type, viewer)) {
                    return;
                }
                List<IRepositoryViewObject> children = viewObject.getChildren();
                if (children != null) {
                    for (IRepositoryViewObject child : children) {
                        if (refreshRepositoryContainer(child, type, viewer)) {
                            return;
                        }
                    }
                }
            }
        }
    }

    public static boolean refreshRepositoryContainer(IRepositoryViewObject viewObj, ERepositoryObjectType type,
            CommonViewer viewer) {
        if (viewObj instanceof FolderRepositoryObject) {
            FolderRepositoryObject containerRepositoryObject = (FolderRepositoryObject) viewObj;
            if (containerRepositoryObject.getRepositoryObjectType().equals(type)) {
                viewer.refresh(containerRepositoryObject);
                return true;
            }
        }
        return false;
    }

}
