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
package org.talend.mdm.repository.ui.navigator;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ItemState;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.command.CommandManager;
import org.talend.mdm.repository.core.command.CommandStack;
import org.talend.mdm.repository.core.command.ICommand;
import org.talend.mdm.repository.model.mdmmetadata.MDMServerDef;
import org.talend.mdm.repository.model.mdmproperties.ContainerItem;
import org.talend.mdm.repository.model.mdmproperties.WSResourceItem;
import org.talend.mdm.repository.plugin.RepositoryPlugin;
import org.talend.mdm.repository.utils.EclipseResourceManager;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;

public class MDMServerDecorator implements ILightweightLabelDecorator {

    private static final ImageDescriptor IMG_SERVER = EclipseResourceManager.getImageDescriptor(RepositoryPlugin.PLUGIN_ID,
            "icons/run_co.gif"); //$NON-NLS-1$

    private static final ImageDescriptor IMG_MODIFY = EclipseResourceManager.getImageDescriptor(RepositoryPlugin.PLUGIN_ID,
            "icons/dirty_ov.gif"); //$NON-NLS-1$

    private static final ImageDescriptor IMG_NEW = EclipseResourceManager.getImageDescriptor(RepositoryPlugin.PLUGIN_ID,
            "icons/new_resource.gif"); //$NON-NLS-1$

    private static final ImageDescriptor IMG_DELETE = EclipseResourceManager.getImageDescriptor(RepositoryPlugin.PLUGIN_ID,
            "icons/deleted.gif"); //$NON-NLS-1$

    public void decorate(Object element, IDecoration decoration) {
        IRepositoryViewObject viewObj = (IRepositoryViewObject) element;
        Item item = RepositoryResourceUtil.getItemFromRepViewObj(viewObj);
        if (item != null) {
            if (isDeleted(viewObj.getRepositoryObjectType(), item)) {
                decorateRecycleBin(item, decoration);
            } else {
                decorateRepositoryObject(item, decoration);
            }
            decorateModifiedObject(viewObj, decoration);
        }
    }

    private void decorateModifiedObject(IRepositoryViewObject viewObj, IDecoration decoration) {
        CommandStack stack = CommandManager.getInstance().findCommandStack(viewObj.getId());
        if (stack != null) {
            ICommand command = stack.getValidDeployCommand();
            switch (command.getCommandType()) {
            case ICommand.CMD_ADD:
                decoration.addOverlay(IMG_NEW, IDecoration.BOTTOM_RIGHT);
                return;
            case ICommand.CMD_DELETE:
                decoration.addOverlay(IMG_DELETE, IDecoration.BOTTOM_RIGHT);
                return;
            case ICommand.CMD_MODIFY:
            case ICommand.CMD_RENAME:
                decoration.addPrefix("> "); //$NON-NLS-1$
                decoration.addOverlay(IMG_MODIFY, IDecoration.BOTTOM_RIGHT);
                return;

            }
        }
    }

    private boolean isDeleted(ERepositoryObjectType type, Item item) {
        if (item instanceof ContainerItem) {
            return RepositoryResourceUtil.isDeletedFolder(item, type);
        }
        return item.getState().isDeleted();
    }

    private void decorateRepositoryObject(Item item, IDecoration decoration) {
        if (item != null) {

            String version = item.getProperty().getVersion();
            if (item instanceof WSResourceItem) {
                // resource image show catalog
                WSResourceItem ritem = (WSResourceItem) item;
                String imageCatalog = ritem.getResource().getImageCatalog();
                if (imageCatalog != null) {
                    decoration.addSuffix(" " + imageCatalog); //$NON-NLS-1$
                }
            } else if (version != null) {
                decoration.addSuffix(" " + version); //$NON-NLS-1$
            }
            MDMServerDef serverDef = RepositoryResourceUtil.getLastServerDef(item);
            if (serverDef != null) {
                decoration.addOverlay(IMG_SERVER, IDecoration.TOP_RIGHT);
                decoration.addSuffix(" " + serverDef.getName()); //$NON-NLS-1$
            }
        }
    }

    private void decorateRecycleBin(Item item, IDecoration decoration) {
        ItemState state = item.getState();
        String path = state.getPath();
        if (item instanceof ContainerItem) {
            int index = path.lastIndexOf(IPath.SEPARATOR);
            if (index > 0) {
                path = path.substring(0, index);
            } else if (index == 0) {
                path = ""; //$NON-NLS-1$
            }
        }
        if (path.length() > 0) {
            decoration.addSuffix(" (" + path + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    public void addListener(ILabelProviderListener listener) {
    }

    public void dispose() {
    }

    public boolean isLabelProperty(Object element, String property) {
        return false;
    }

    public void removeListener(ILabelProviderListener listener) {
    }
}
