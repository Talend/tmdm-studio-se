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
package org.talend.mdm.repository.core.migrate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.core.repository.utils.XmiResourceManager;

/**
 * DOC HHB class global comment. Detailled comment
 */
public abstract class AbstractMigrateObjectPathRule implements IMigrateObjectPathRule {

    Map<String, String[]> folderRouteCache = new HashMap<String, String[]>();

    private XmiResourceManager xmiResourceManager;

    public AbstractMigrateObjectPathRule() {
        xmiResourceManager = new XmiResourceManager();
    }

    public String[] routeFolderObject(IResource resource) {
        if (resource instanceof IFolder) {

            String path = resource.getFullPath().toString();
            String[] newFolders = folderRouteCache.get(path);
            if (newFolders != null)
                return newFolders;
            //
            Set<String> paths = new HashSet<String>();
            try {
                for (IResource childRes : ((IFolder) resource).members()) {
                    if (childRes instanceof IFolder) {
                        String[] folderResult = routeFolderObject(childRes);
                        if (folderResult != null) {
                            for (String folderName : folderResult) {
                                paths.add(folderName);
                            }
                        }
                    } else {
                        String objPath = routeObject(childRes);
                        if (objPath != null) {
                            paths.add(objPath);
                        }
                    }
                }
            } catch (CoreException e) {
                ExceptionHandler.process(e);
            }
            return paths.toArray(new String[0]);
        }
        return null;
    }

    private String routeObject(IResource resource) {
        IFile propFile = (IFile) resource;
        if (isPropertyFile(propFile)) {
            Property property = null;

            property = xmiResourceManager.loadProperty(propFile);
            Item item = property.getItem();
            return routeObject(item);
        }
        return null;
    }

    public boolean isPropertyFile(IResource resource) {
        if (resource instanceof IFile) {
            return xmiResourceManager.isPropertyFile((IFile) resource);
        }
        return false;
    }

    public Property loadProperty(IResource resource) {
        return xmiResourceManager.loadProperty(resource);
    }

    public boolean isToMigrateFolder(IResource parentRes, IResource resource) {
        if (resource instanceof IFolder) {
            int segmentCount = parentRes.getFullPath().segmentCount();
            IPath path = resource.getFullPath().removeFirstSegments(segmentCount);

            if (isNewFolder(path.toOSString()))
                return true;
        }
        return false;
    }

    protected boolean isNewFolder(String path) {
        if (path == null)
            throw new IllegalArgumentException();
        if (getAllNewFolderNames() != null) {
            for (String folderName : getAllNewFolderNames()) {
                if ((IPath.SEPARATOR + folderName).equalsIgnoreCase(path) || folderName.equalsIgnoreCase(path)) {
                    return true;
                }
            }
        }
        return false;
    }

}
