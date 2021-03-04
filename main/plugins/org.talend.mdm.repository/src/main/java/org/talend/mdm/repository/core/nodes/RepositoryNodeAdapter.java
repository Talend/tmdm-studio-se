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
package org.talend.mdm.repository.core.nodes;

import org.talend.commons.ui.runtime.image.IImage;
import org.talend.commons.ui.runtime.repository.IExtendRepositoryNode;
import org.talend.repository.model.RepositoryNode;

/**
 * DOC hbhong class global comment. Detailled comment <br/>
 *
 */
public class RepositoryNodeAdapter implements IExtendRepositoryNode {

    private final IImage image;

    private final int ordinal;

    protected final static RepositoryNode[] EMPTY_NODE_ARRAY = new RepositoryNode[0];

    public RepositoryNodeAdapter(IImage image, int ordinal) {
        this.image = image;
        this.ordinal = ordinal;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.talend.core.repository.IExtendRepositoryNode#getNodeImage()
     */

    public IImage getNodeImage() {
        return image;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.talend.core.repository.IExtendRepositoryNode#getOrdinal()
     */

    public int getOrdinal() {
        return ordinal;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.talend.core.repository.IExtendRepositoryNode#getChildren()
     */

    public Object[] getChildren() {
        return EMPTY_NODE_ARRAY;
    }

}
