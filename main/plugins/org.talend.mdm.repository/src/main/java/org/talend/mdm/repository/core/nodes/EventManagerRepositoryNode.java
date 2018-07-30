// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.repository.core.nodes;

import org.talend.mdm.repository.core.IServerObjectOrdinal;
import org.talend.mdm.repository.core.ServerObjectImage;

/**
 * DOC hbhong class global comment. Detailled comment <br/>
 * 
 */
public class EventManagerRepositoryNode extends RepositoryNodeAdapter {

    public EventManagerRepositoryNode() {
        super(ServerObjectImage.EVENTMANAGER_ICON, IServerObjectOrdinal.EVENTMANAGER);
    }
}
