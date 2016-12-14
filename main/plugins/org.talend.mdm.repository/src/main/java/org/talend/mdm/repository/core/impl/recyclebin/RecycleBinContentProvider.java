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
package org.talend.mdm.repository.core.impl.recyclebin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.utils.workbench.resources.ResourceUtils;
import org.talend.core.model.general.Project;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ItemState;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.model.repository.RepositoryViewObject;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.mdm.repository.core.IServerObjectRepositoryType;
import org.talend.mdm.repository.core.impl.AbstractContentProvider;
import org.talend.mdm.repository.core.service.ContainerCacheService;
import org.talend.mdm.repository.models.FolderRepositoryObject;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;
import org.talend.repository.ProjectManager;
import org.talend.repository.model.IProxyRepositoryFactory;

/**
 * DOC hbhong class global comment. Detailled comment <br/>
 * 
 */
public class RecycleBinContentProvider extends AbstractContentProvider {

    private static final String MDM_PREFIX = "MDM"; //$NON-NLS-1$

    private static final String JOB_PREFIX = "process"; //$NON-NLS-1$

    private static final String DQ_PREFIX = "TDQ_"; //$NON-NLS-1$

    private Map<ERepositoryObjectType, Map<String, FolderRepositoryObject>> containerMap = new HashMap<ERepositoryObjectType, Map<String, FolderRepositoryObject>>();

    private static final Pattern mdmPattern = Pattern.compile("(MDM/\\w*+)((/(\\w*))+)"); //$NON-NLS-1$

    private static final Pattern jobPattern = Pattern.compile("(process)((/(\\w*))+)"); //$NON-NLS-1$

    private static final Pattern matchRulePattern = Pattern.compile("(TDQ_Libraries/Rules/Match)((/(\\w*))+)"); //$NON-NLS-1$

    private static Logger log = Logger.getLogger(RecycleBinContentProvider.class);

    IProxyRepositoryFactory factory = CoreRuntimePlugin.getInstance().getProxyRepositoryFactory();

    private void addToMap(FolderRepositoryObject viewObj, String path) {
        ERepositoryObjectType repObjType = viewObj.getRepositoryObjectType();
        Map<String, FolderRepositoryObject> map = containerMap.get(repObjType);
        if (map == null) {
            map = new HashMap<String, FolderRepositoryObject>();
            containerMap.put(repObjType, map);
        }
        map.put(path, viewObj);
    }

    private void buildAllDeletedFolders(FolderRepositoryObject rootViewObj) {
        Project currentProject = ProjectManager.getInstance().getCurrentProject();
        List<String> paths = currentProject.getEmfProject().getDeletedFolders();
        String[] deletedFolderPaths = sortFolderPath(paths);
        containerMap.clear();
        rootViewObj.getChildren().clear();
        IProject fsProject = null;
        try {
            fsProject = ResourceUtils.getProject(currentProject);

        } catch (PersistenceException e) {
            log.error(e.getMessage(), e);
        }

        for (String path : deletedFolderPaths) {
            IFolder folder = fsProject.getFolder(path);
            if (folder != null && folder.getLocation().toFile().exists()) {
                Matcher matcher = null;
                if (path.startsWith(MDM_PREFIX)) {
                    matcher = mdmPattern.matcher(path);
                } else if (path.startsWith(JOB_PREFIX)) {
                    matcher = jobPattern.matcher(path);
                } else if (path.startsWith(DQ_PREFIX)) {
                    matcher = matchRulePattern.matcher(path);
                }
                if (matcher != null && matcher.find()) {
                    String parentFolder = matcher.group(1);
                    String itemPath = matcher.group(2);
                    String folderName = matcher.group(4);

                    ERepositoryObjectType type = RepositoryResourceUtil.getTypeByPath(parentFolder);
                    FolderRepositoryObject parentContainer = getParenContainer(rootViewObj, type, itemPath, true);
                    if (type != null && itemPath != null && folderName != null) {
                        FolderRepositoryObject viewObj = RepositoryResourceUtil.createDeletedFolderViewObject(type, itemPath,
                                folderName, parentContainer);
                        addToMap(viewObj, itemPath);
                    }
                }
            }
        }
    }

    private void buildAllDeletedObjects(FolderRepositoryObject rootViewObj) {
        for (ERepositoryObjectType type : IServerObjectRepositoryType.ALL_TYPES) {
            try {
                List<IRepositoryViewObject> viewObjs = factory.getAll(type, true);
                for (IRepositoryViewObject viewObj : viewObjs) {
                    Property property = viewObj.getProperty();
                    ItemState state = property.getItem().getState();
                    if (state.isDeleted()) {
                        String path = state.getPath();

                        String prefix = "/"; //$NON-NLS-1$
                        if (!path.startsWith(prefix)) {
                            path = prefix + path;
                        }

                        FolderRepositoryObject container = getParenContainer(rootViewObj, type, path, false);
                        // get from cache
                        IRepositoryViewObject cacheViewObj = ContainerCacheService.get(property);
                        if (cacheViewObj == null) {
                            cacheViewObj = new RepositoryViewObject(property);
                            ContainerCacheService.put(property, cacheViewObj);
                        }
                        container.getChildren().add(cacheViewObj);
                    }
                }
            } catch (PersistenceException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public Object[] getChildren(Object element) {
        IRepositoryViewObject viewObj = (IRepositoryViewObject) element;
        ERepositoryObjectType type = viewObj.getRepositoryObjectType();
        if (viewObj instanceof FolderRepositoryObject) {
            FolderRepositoryObject containerObj = (FolderRepositoryObject) viewObj;
            if (type == IServerObjectRepositoryType.TYPE_RECYCLE_BIN) {
                buildAllDeletedFolders(containerObj);
                buildAllDeletedObjects(containerObj);
            }
            return containerObj.getChildren().toArray();

        }
        return new Object[0];
    }

    private FolderRepositoryObject getParenContainer(FolderRepositoryObject rootItem, ERepositoryObjectType type,
            String currentPath, boolean isFolder) {
        String parentPath = isFolder ? getParentPath(currentPath) : currentPath;
        if (parentPath != null) {
            Map<String, FolderRepositoryObject> map = containerMap.get(type);
            if (map != null) {
                FolderRepositoryObject item = map.get(parentPath);
                if (item != null) {
                    return item;
                }
            }
        }
        return rootItem;
    }

    private String getParentPath(String path) {
        int index = path.lastIndexOf(IPath.SEPARATOR);
        if (index > 0) {
            return path.substring(0, index);
        }
        return null;
    }

    @Override
    protected List<IRepositoryViewObject> getViewObjFromSystemFolder(Item parentItem) {
        return null;
    }

    public Class<?> getWSObjectClass() {
        return null;
    }

    private String[] sortFolderPath(List folderPaths) {
        Iterator il = folderPaths.iterator();
        List<String> result = new ArrayList<String>(folderPaths.size());
        while (il.hasNext()) {
            String path = (String) il.next();
            if (path.startsWith(MDM_PREFIX) || path.startsWith(JOB_PREFIX) || path.startsWith(DQ_PREFIX)) {
                result.add(path);
            }
        }
        String[] objs = result.toArray(new String[0]);
        Arrays.sort(objs);
        return objs;
    }

}
