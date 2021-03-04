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
package org.talend.mdm.repository.core.bridge;

import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.service.ContainerCacheService;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;
import org.talend.repository.model.RepositoryNode;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class MDMRepositoryNode extends RepositoryNode {

    /**
     * DOC hbhong MDMRepositoryNode constructor comment.
     *
     * @param object
     * @param parent
     * @param type
     */
    public MDMRepositoryNode(IRepositoryViewObject object, RepositoryNode parent, ENodeType type) {
        super(object, parent, type);
    }

    @Override
    public RepositoryNode getParent() {
        if (super.getParent() == null) {
            IRepositoryViewObject parentViewObj = ContainerCacheService.getParent(getObject());
            if (parentViewObj != null) {
                RepositoryNode parentNode = RepositoryResourceUtil.convertToNode(parentViewObj);
                setParent(parentNode);
            }
        }
        return super.getParent();
    }

}
