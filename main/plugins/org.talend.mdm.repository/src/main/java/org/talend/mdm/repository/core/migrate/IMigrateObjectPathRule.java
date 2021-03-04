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

import org.eclipse.core.resources.IResource;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;

/**
 * DOC HHB class global comment. Detailled comment
 */
public interface IMigrateObjectPathRule {

    public ERepositoryObjectType getRepositoryObjectType();

    public String[] getAllNewFolderNames();

    public String routeObject(Item item);

    public boolean isPropertyFile(IResource resource);

    public Property loadProperty(IResource resource);

    public String[] routeFolderObject(IResource resource);

    public boolean isToMigrateFolder(IResource parentRes, IResource resource);
}
