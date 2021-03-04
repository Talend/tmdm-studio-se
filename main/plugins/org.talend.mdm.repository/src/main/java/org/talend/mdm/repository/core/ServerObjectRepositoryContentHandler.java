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
package org.talend.mdm.repository.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.ui.runtime.image.IImage;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.AbstractRepositoryContentHandler;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.repository.utils.XmiResourceManager;
import org.talend.mdm.repository.extension.RepositoryNodeConfigurationManager;
import org.talend.mdm.repository.model.mdmproperties.MDMServerObjectItem;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;

import com.amalto.workbench.exadapter.ExAdapterManager;

public class ServerObjectRepositoryContentHandler extends AbstractRepositoryContentHandler implements IServerObjectRepositoryType {

    private XmiResourceManager xmiResourceManager = ProxyRepositoryFactory.getInstance().getRepositoryFactoryFromProvider()
            .getResourceManager();

    IServerObjectRepositoryContentHandlerExAdapter exAdapter = null;

    public ServerObjectRepositoryContentHandler() {
        exAdapter = ExAdapterManager.getAdapter(this, IServerObjectRepositoryContentHandlerExAdapter.class);
    }

    public ERepositoryObjectType getRepositoryObjectType(Item item) {

        IRepositoryNodeConfiguration configuration = RepositoryNodeConfigurationManager.getConfiguration(item);
        if (configuration != null) {
            return configuration.getResourceProvider().getRepositoryObjectType(item);
        }
        return null;
    }

    public Resource create(IProject project, Item item, int classifierID, IPath path) throws PersistenceException {
        IRepositoryNodeConfiguration configuration = RepositoryNodeConfigurationManager.getConfiguration(item);
        if (configuration != null) {
            Resource resource = configuration.getResourceProvider().create(project, item, classifierID, path);
            return resource;
        }
        return null;
    }

    public Resource save(Item item) throws PersistenceException {
        IRepositoryNodeConfiguration configuration = RepositoryNodeConfigurationManager.getConfiguration(item);
        item = RepositoryResourceUtil.assertItem(item);
        if (configuration != null) {
            return configuration.getResourceProvider().save(item);
        } else if (item instanceof MDMServerObjectItem) {
            return xmiResourceManager.getItemResource(item);
        }
        return null;
    }

    @Override
    public IImage getIcon(ERepositoryObjectType type) {
        if (type == TYPE_MENU) {
            return ServerObjectImage.MENU_ICON;
        }

        if (type == TYPE_DATACLUSTER) {
            return ServerObjectImage.DATACLUSTER_ICON;
        }
        if (type == TYPE_DATAMODEL) {
            return ServerObjectImage.DATAMODEL_ICON;
        }
        if (type == TYPE_EVENTMANAGER) {
            return ServerObjectImage.EVENTMANAGER_ICON;
        }

        if (type == TYPE_ROUTINGRULE) {
            return ServerObjectImage.ROUTINGRULE_ICON;
        }
        if (type == TYPE_SERVICECONFIGURATION) {
            return ServerObjectImage.SERVICECONFIGURATION_ICON;
        }
        if (type == TYPE_STOREPROCEDURE) {
            return ServerObjectImage.STOREPROCEDURE_ICON;
        }

        if (type == TYPE_TRANSFORMERV2) {
            return ServerObjectImage.TRANSFORMERV2_ICON;
        }

        if (type == TYPE_VIEW) {
            return ServerObjectImage.VIEW_ICON;
        }
        if (exAdapter != null) {
            return exAdapter.getIcon(type);
        }
        return null;
    }

    public Item createNewItem(ERepositoryObjectType type) {
        IRepositoryNodeConfiguration configuration = RepositoryNodeConfigurationManager.getConfiguration(type);
        if (configuration != null) {
            return configuration.getResourceProvider().createNewItem(type);
        }
        return null;
    }

    @Override
    public boolean isProcess(Item item) {
        return false;
    }

    public boolean isRepObjType(ERepositoryObjectType type) {
        IRepositoryNodeConfiguration configuration = RepositoryNodeConfigurationManager.getConfiguration(type);
        return configuration != null;

    }

}
