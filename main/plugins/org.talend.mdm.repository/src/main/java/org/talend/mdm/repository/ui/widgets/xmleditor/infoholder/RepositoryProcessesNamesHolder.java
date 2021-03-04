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
package org.talend.mdm.repository.ui.widgets.xmleditor.infoholder;

import java.util.List;

import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.IServerObjectRepositoryType;
import org.talend.mdm.repository.model.mdmproperties.MDMServerObjectItem;
import org.talend.mdm.repository.model.mdmserverobject.MDMServerObject;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;

import com.amalto.workbench.webservices.TMDMService;

/**
 * DOC hbhong class global comment. Detailled comment
 */
class RepositoryProcessesNamesHolder extends RepositoryExternalInfoHolder<String[]> {

    private TMDMService service;

    public RepositoryProcessesNamesHolder(TMDMService service) {
        this.service = service;
    }

    @Override
    public String[] getExternalInfo() {

        List<IRepositoryViewObject> viewObjs = RepositoryResourceUtil
                .findAllViewObjects(IServerObjectRepositoryType.TYPE_TRANSFORMERV2);
        String[] names = new String[viewObjs.size()];
        int i = 0;
        for (IRepositoryViewObject viewObj : viewObjs) {
            Property prop = viewObj.getProperty();
            if (prop.getItem() instanceof MDMServerObjectItem) {
                MDMServerObject serverObject = ((MDMServerObjectItem) prop.getItem()).getMDMServerObject();
                if (serverObject != null) {
                    names[i] = serverObject.getName();
                }
            } else {
                names[i] = prop.getLabel();
            }
            i++;
        }

        return names;
    }

    @Override
    public String getId() {
        return INFOID_ALLPROCESSNAMES;
    }

}
