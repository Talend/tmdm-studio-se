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
package org.talend.mdm.repository.core.impl.datamodel;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.mdm.repository.core.IServerObjectRepositoryType;
import org.talend.mdm.repository.core.impl.AbstractRepositoryNodeResourceProvider;
import org.talend.mdm.repository.model.mdmproperties.ContainerItem;
import org.talend.mdm.repository.model.mdmproperties.MdmpropertiesFactory;
import org.talend.mdm.repository.model.mdmproperties.WSDataModelItem;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;

/**
 * DOC hbhong class global comment. Detailled comment <br/>
 *
 */
public class DataModelNodeResourceProvider extends AbstractRepositoryNodeResourceProvider {

    Logger log = Logger.getLogger(DataModelNodeResourceProvider.class);

    public ERepositoryObjectType getRepositoryObjectType(Item item) {
        if (item instanceof WSDataModelItem || item instanceof ContainerItem) {
            return IServerObjectRepositoryType.TYPE_DATAMODEL;
        }
        return null;
    }

    public Resource create(IProject project, Item item, int classifierID, IPath path) throws PersistenceException {
        ERepositoryObjectType repositoryType = getRepositoryObjectType(item);
        if (repositoryType != null) {
            Resource itemResource = createCommonItemResource(project, item, repositoryType, path);
            EList<EObject> contents = itemResource.getContents();
            contents.add(((WSDataModelItem) item).getWsDataModel());
            return itemResource;
        }
        return null;
    }

    @Override
    public Resource save(Item item) throws PersistenceException {
        if (item instanceof WSDataModelItem) {
            Resource resource = xmiResourceManager.getItemResource(item);
            resource.getContents().clear();
            resource.getContents().add(((WSDataModelItem) item).getWsDataModel());
            return resource;
        }
        return null;
    }

    public Item createNewItem(ERepositoryObjectType type) {
        return MdmpropertiesFactory.eINSTANCE.createWSDataModelItem();
    }

    public boolean canHandleRepObjType(ERepositoryObjectType type) {
        return type == IServerObjectRepositoryType.TYPE_DATAMODEL;
    }

    @Override
    public boolean needSaveReferenceFile() {
        return true;
    }

    @Override
    public void handleReferenceFile(Item item) {
        IFile xsdFile = RepositoryResourceUtil.findReferenceFile(IServerObjectRepositoryType.TYPE_DATAMODEL, item, "xsd"); //$NON-NLS-1$
        IFile mapInfoFile = RepositoryResourceUtil.findReferenceFile(IServerObjectRepositoryType.TYPE_DATAMODEL, item, "mapinfo"); //$NON-NLS-1$
        IFile erFile = RepositoryResourceUtil.findReferenceFile(IServerObjectRepositoryType.TYPE_DATAMODEL, item, "er"); //$NON-NLS-1$
        try {

            createOrUpdateFile(item, xsdFile);
            linkReferenceFile(item, xsdFile);
            if (mapInfoFile.getLocation().toFile().exists()) {
                linkReferenceFile(item, mapInfoFile);
            }
            if (erFile.getLocation().toFile().exists()) {
                linkReferenceFile(item, erFile);
            }

        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
        } catch (CoreException e) {
            log.error(e.getMessage(), e);
        }

    }

    protected IFile createOrUpdateFile(Item item, IFile file) throws UnsupportedEncodingException, CoreException {

        String resource = ((WSDataModelItem) item).getWsDataModel().getXsdSchema();
        if (resource != null) {
            byte[] content = resource.getBytes("utf-8"); //$NON-NLS-1$
            if (!file.exists()) {
                file.create(new ByteArrayInputStream(content), IFile.FORCE, new NullProgressMonitor());
            } else {
                file.setContents(new ByteArrayInputStream(content), IFile.FORCE, new NullProgressMonitor());
            }
        }
        return file;
    }
}
