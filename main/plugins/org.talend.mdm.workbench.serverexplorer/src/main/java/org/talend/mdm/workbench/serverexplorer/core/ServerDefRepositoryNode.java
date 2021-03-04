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
package org.talend.mdm.workbench.serverexplorer.core;

import org.talend.commons.ui.runtime.image.IImage;
import org.talend.commons.ui.runtime.repository.IExtendRepositoryNode;
import org.talend.repository.model.RepositoryNode;

/**
 * DOC hbhong class global comment. Detailled comment <br/>
 *
 */
public class ServerDefRepositoryNode implements IExtendRepositoryNode {

    public IImage getNodeImage() {
        return ServerDefImage.BEAN_ICON;
    }

    public int getOrdinal() {
        return 100;
    }

    public Object[] getChildren() {
        return new RepositoryNode[0];
    }
}
