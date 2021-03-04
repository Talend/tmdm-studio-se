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
package org.talend.mdm.repository.core.migrate.impl;

import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.mdm.repository.core.IServerObjectRepositoryType;
import org.talend.mdm.repository.core.impl.transformerV2.ITransformerV2NodeConsDef;
import org.talend.mdm.repository.core.migrate.AbstractMigrateObjectPathRule;
import org.talend.mdm.repository.model.mdmproperties.MDMServerObjectItem;
import org.talend.mdm.repository.utils.RepositoryTransformUtil;

public class ProcessMigrateObjectPathRule extends AbstractMigrateObjectPathRule implements ITransformerV2NodeConsDef {

    public ERepositoryObjectType getRepositoryObjectType() {

        return IServerObjectRepositoryType.TYPE_TRANSFORMERV2;
    }

    String[] folderNames = new String[] { PATH_BEFORESAVE, PATH_BEFOREDEL, PATH_ENTITYACTION, PATH_WELCOMEACTION, PATH_SMARTVIEW,
            PATH_OTHER };

    public String[] getAllNewFolderNames() {

        return folderNames;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.talend.mdm.repository.core.migrate.IMigrateObjectPathRule#routeObject(org.talend.core.model.properties.Item)
     */
    public String routeObject(Item item) {
        if (item instanceof MDMServerObjectItem) {
            String processName = ((MDMServerObjectItem) item).getMDMServerObject().getName();
            return RepositoryTransformUtil.getInstance().getProcessPath(processName, false);
        }

        return null;
    }
}
