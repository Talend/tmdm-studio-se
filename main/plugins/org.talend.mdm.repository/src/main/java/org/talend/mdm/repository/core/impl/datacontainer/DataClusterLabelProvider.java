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
package org.talend.mdm.repository.core.impl.datacontainer;

import org.eclipse.swt.graphics.Image;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.mdm.repository.core.impl.AbstractLabelProvider;
import org.talend.mdm.repository.model.mdmproperties.WSDataClusterItem;
import org.talend.mdm.repository.plugin.RepositoryPlugin;
import org.talend.mdm.repository.utils.EclipseResourceManager;

/**
 * DOC hbhong class global comment. Detailled comment <br/>
 * 
 */
public class DataClusterLabelProvider extends AbstractLabelProvider {

    private static final Image IMG = EclipseResourceManager.getImage(RepositoryPlugin.PLUGIN_ID, "icons/datacluster.png"); //$NON-NLS-1$;

    public String getCategoryLabel(ERepositoryObjectType type) {
        return "Data Container"; //$NON-NLS-1$
    }

    @Override
    public Image getCategoryImage(Item item) {
        return IMG;
    }

    @Override
    public Image getImage(Object element) {
        Image img = super.getImage(element);
        if (img == null) {
            Item item = getItem(element);
            if (item != null) {

                if (item instanceof WSDataClusterItem) {
                    img = IMG;
                }
            }
        }
        return img;
    }
}
