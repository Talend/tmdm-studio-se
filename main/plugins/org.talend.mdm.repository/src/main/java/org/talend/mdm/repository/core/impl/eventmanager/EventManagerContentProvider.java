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
package org.talend.mdm.repository.core.impl.eventmanager;

import java.util.LinkedList;
import java.util.List;

import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.IRepositoryNodeConfiguration;
import org.talend.mdm.repository.core.IServerObjectRepositoryType;
import org.talend.mdm.repository.core.impl.AbstractContentProvider;
import org.talend.mdm.repository.extension.RepositoryNodeConfigurationManager;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;

public class EventManagerContentProvider extends AbstractContentProvider {

    private List<IRepositoryViewObject> result;

    /**
     * DOC hbhong EventManagerContentProvider constructor comment.
     */
    public EventManagerContentProvider() {

    }

    @Override
    protected List<IRepositoryViewObject> getViewObjFromSystemFolder(Item parentItem) {
        if (result == null) {
            result = new LinkedList<IRepositoryViewObject>();
            IRepositoryNodeConfiguration processConf = RepositoryNodeConfigurationManager
                    .getConfiguration(IServerObjectRepositoryType.TYPE_TRANSFORMERV2);
            IRepositoryNodeConfiguration triggerConf = RepositoryNodeConfigurationManager
                    .getConfiguration(IServerObjectRepositoryType.TYPE_ROUTINGRULE);

            //
            addCategoryViewObject(result, processConf);
            addCategoryViewObject(result, triggerConf);
        }
        return result;
    }

    private void addCategoryViewObject(List<IRepositoryViewObject> result, IRepositoryNodeConfiguration conf) {
        if (conf != null) {
            IRepositoryViewObject categoryViewObject = RepositoryResourceUtil.getCategoryViewObject(conf);
            if (categoryViewObject != null) {
                result.add(categoryViewObject);
            }
        }
    }

    public Class getWSObjectClass() {
        return null;
    }

}
