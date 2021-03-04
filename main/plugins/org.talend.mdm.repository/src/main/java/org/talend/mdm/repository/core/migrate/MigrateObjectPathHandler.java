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

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.runtime.model.emf.EmfHelper;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ItemState;
import org.talend.core.model.properties.Property;
import org.talend.core.repository.model.ProxyRepositoryFactory;

/**
 * DOC HHB class global comment. Detailled comment
 */
public class MigrateObjectPathHandler {

    static Logger log = Logger.getLogger(MigrateObjectPathHandler.class);

    private final IMigrateObjectPathRule rule;

    public MigrateObjectPathHandler(IMigrateObjectPathRule rule) {
        this.rule = rule;
    }

    public IMigrateObjectPathRule getRule() {
        return this.rule;
    }

    public void migrate(IFolder parentFolder) {

        List<IResource> toMoveResources = new LinkedList<IResource>();
        iterateFolder(parentFolder, toMoveResources);
        assertFolder(parentFolder);
        move(parentFolder, toMoveResources);
        removeFolder(toMoveResources);

    }

    private void removeFolder(List<IResource> toMoveResources) {
        for (ListIterator<IResource> il = toMoveResources.listIterator(toMoveResources.size()); il.hasPrevious();) {
            IResource resource = il.previous();
            if (resource instanceof IFolder && !isSVNFolder(resource)) {
                IFolder folder = (IFolder) resource;
                try {
                    if (folder != null && folder.exists()) {
                        folder.delete(true, new NullProgressMonitor());
                    }
                } catch (CoreException e) {
                    log.error(e.getMessage(), e);
                }

            }
        }
    }

    private void move(IFolder parentFolder, List<IResource> toMoveResources) {
        for (IResource res : toMoveResources) {
            if (res instanceof IFolder) {
                createFolder(parentFolder, res);
            } else {
                moveFile(parentFolder, res);
            }
        }

    }

    private void moveFile(IFolder parentFolder, IResource resource) {
        if (resource instanceof IFile) {
            IFile propFile = (IFile) resource;
            Property property = null;
            try {
                property = rule.loadProperty(propFile);
                Item item = property.getItem();
                ItemState state = item.getState();
                String path = state.getPath();
                if (path.length() > 0 && !path.startsWith(String.valueOf(IPath.SEPARATOR))) {
                    path = IPath.SEPARATOR + path;
                }
                String prefix = rule.routeObject(item);
                if (prefix != null) {
                    path = IPath.SEPARATOR + prefix + path;
                }
                state.setPath(path);

                EmfHelper.saveResource(item.eResource());
                String itemName = propFile.getName().replace(".properties", ".item"); //$NON-NLS-1$ //$NON-NLS-2$
                IFile itemFile = ((IFolder) propFile.getParent()).getFile(itemName);
                if (itemFile.exists()) {
                    itemFile.move(getMoveTargetPath(parentFolder, prefix, itemFile), true, false, new NullProgressMonitor());
                    propFile.move(getMoveTargetPath(parentFolder, prefix, propFile), true, false, new NullProgressMonitor());
                }

            } catch (RuntimeException e) {
                ExceptionHandler.process(e);
            } catch (CoreException e) {
                ExceptionHandler.process(e);
            } catch (PersistenceException e) {
                ExceptionHandler.process(e);
            }
        }

    }

    private void createFolder(IFolder parentFolder, IResource res) {
        IFolder[] moveTargetFolders = getMoveTargetFolders(parentFolder, res);
        if (moveTargetFolders != null) {
            for (IFolder folder : moveTargetFolders) {
                try {
                    folder.create(true, true, null);
                } catch (CoreException e) {
                    ExceptionHandler.process(e);
                }
            }
        }
    }

    private IPath getMoveTargetPath(IFolder parent, String prefix, IResource viewObj) {
        IPath parentPath = parent.getFullPath();
        int segmentCount = parentPath.segmentCount();
        IPath relativePath = viewObj.getFullPath().removeFirstSegments(segmentCount);
        return parentPath.append(prefix).append(relativePath);
    }

    private IFolder[] getMoveTargetFolders(IFolder parent, IResource viewObj) {
        IPath parentPath = parent.getFullPath();
        int segmentCount = parentPath.segmentCount();
        String[] paths = rule.routeFolderObject(viewObj);
        if (paths == null) {
            return null;
        }
        IFolder[] returnFolders = new IFolder[paths.length];
        for (int i = 0; i < paths.length; i++) {
            String path = paths[i] + IPath.SEPARATOR + viewObj.getFullPath().removeFirstSegments(segmentCount).toString();
            returnFolders[i] = parent.getFolder(path);
        }
        return returnFolders;
    }

    private void iterateFolder(IResource parentRes, List<IResource> toMoveResourceObjects) {

        if (parentRes instanceof IFolder) {
            try {
                for (IResource resource : ((IFolder) parentRes).members()) {
                    if (resource instanceof IFolder) {
                        if (!rule.isToMigrateFolder(parentRes, resource) && !isSVNFolder(resource)) {
                            handleResourceObject(resource, toMoveResourceObjects);
                        }
                    } else {
                        handleResourceObject(resource, toMoveResourceObjects);
                    }
                }
            } catch (CoreException e) {
                ExceptionHandler.process(e);
            }
        }

    }

    private boolean isSVNFolder(IResource resource) {
        if (resource instanceof IFolder) {
            return resource.getName().equalsIgnoreCase(".svn"); //$NON-NLS-1$
        }
        return false;
    }

    public void assertFolder(IFolder parentFolder) {
        for (String name : rule.getAllNewFolderNames()) {
            IFolder folder = parentFolder.getFolder(name);
            if (!folder.exists()) {
                try {
                    ProxyRepositoryFactory.getInstance().createFolder(rule.getRepositoryObjectType(), Path.EMPTY, name);
                } catch (PersistenceException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    private void handleResourceObject(IResource resource, List<IResource> toMoveResourceObjects) {

        if (resource instanceof IFolder) {
            toMoveResourceObjects.add(resource);
            try {
                for (IResource childResource : ((IFolder) resource).members()) {
                    handleResourceObject(childResource, toMoveResourceObjects);
                }
            } catch (CoreException e) {
                ExceptionHandler.process(e);
            }
        } else {
            if (rule.isPropertyFile(resource)) {
                toMoveResourceObjects.add(resource);
            }
        }

    }

}
