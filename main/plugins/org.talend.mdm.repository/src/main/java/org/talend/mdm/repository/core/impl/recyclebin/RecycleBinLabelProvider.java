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
package org.talend.mdm.repository.core.impl.recyclebin;

import org.eclipse.swt.graphics.Image;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.IRepositoryNodeConfiguration;
import org.talend.mdm.repository.core.IRepositoryNodeLabelProvider;
import org.talend.mdm.repository.core.IServerObjectRepositoryType;
import org.talend.mdm.repository.core.impl.AbstractLabelProvider;
import org.talend.mdm.repository.extension.RepositoryNodeConfigurationManager;
import org.talend.mdm.repository.model.mdmproperties.MDMServerObjectItem;
import org.talend.mdm.repository.models.FolderRepositoryObject;
import org.talend.mdm.repository.plugin.RepositoryPlugin;
import org.talend.mdm.repository.utils.EclipseResourceManager;

/**
 * DOC hbhong class global comment. Detailled comment <br/>
 * 
 */
public class RecycleBinLabelProvider extends AbstractLabelProvider {

    private static final Image EMPTY_IMG = EclipseResourceManager.getImage(RepositoryPlugin.PLUGIN_ID, "icons/recycle-bin.png"); //$NON-NLS-1$;

    private static final Image FULL_IMG = EclipseResourceManager.getImage(RepositoryPlugin.PLUGIN_ID, "icons/recycle-bin.png"); //$NON-NLS-1$;

    public String getCategoryLabel(ERepositoryObjectType type) {
        return "Recycle bin"; //$NON-NLS-1$
    }

    @Override
    public Image getCategoryImage(Item item) {
        return null;
    }

    @Override
    public Image getImage(Object element) {
        if (element instanceof IRepositoryViewObject) {

            IRepositoryViewObject viewObj = (IRepositoryViewObject) element;
            ERepositoryObjectType type = viewObj.getRepositoryObjectType();
            if (type == IServerObjectRepositoryType.TYPE_RECYCLE_BIN) {
                if (viewObj instanceof FolderRepositoryObject) {
                    return ((FolderRepositoryObject) viewObj).getChildren().isEmpty() ? EMPTY_IMG : FULL_IMG;
                }
            }
            IRepositoryNodeConfiguration configuration = RepositoryNodeConfigurationManager.getConfiguration(type);
            if (configuration != null) {
                IRepositoryNodeLabelProvider labelProvider = configuration.getLabelProvider();
                return labelProvider.getImage(element);
            }
        }
        return null;
    }

    @Override
    protected String getServerObjectItemText(Item item) {
        MDMServerObjectItem mItem = (MDMServerObjectItem) item;
        return mItem.getMDMServerObject().getName();

    }

}
