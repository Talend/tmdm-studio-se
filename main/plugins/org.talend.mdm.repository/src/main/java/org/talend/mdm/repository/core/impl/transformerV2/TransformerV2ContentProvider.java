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
package org.talend.mdm.repository.core.impl.transformerV2;

import java.util.List;

import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.IServerObjectRepositoryType;
import org.talend.mdm.repository.core.impl.AbstractContentProvider;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;

import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.webservices.WSTransformerV2;

/**
 * DOC hbhong class global comment. Detailled comment <br/>
 *
 */
public class TransformerV2ContentProvider extends AbstractContentProvider {

    @Override
    protected List<IRepositoryViewObject> getViewObjFromSystemFolder(Item parentItem) {
        return RepositoryResourceUtil.findViewObjectsByType(IServerObjectRepositoryType.TYPE_TRANSFORMERV2, parentItem,
                TreeObject.TRANSFORMER);
    }

    public Class getWSObjectClass() {
        return WSTransformerV2.class;
    }

    @Override
    public boolean isShownInRoot() {
        return false;
    }

}
