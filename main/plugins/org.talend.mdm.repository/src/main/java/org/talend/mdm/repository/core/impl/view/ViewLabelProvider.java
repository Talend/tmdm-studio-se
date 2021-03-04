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
package org.talend.mdm.repository.core.impl.view;

import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.graphics.Image;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.mdm.repository.core.impl.AbstractLabelProvider;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.model.mdmproperties.ContainerItem;
import org.talend.mdm.repository.model.mdmproperties.WSViewItem;
import org.talend.mdm.repository.plugin.RepositoryPlugin;
import org.talend.mdm.repository.utils.EclipseResourceManager;
import org.talend.mdm.repository.utils.RepositoryTransformUtil;

/**
 * DOC hbhong class global comment. Detailled comment <br/>
 *
 */
public class ViewLabelProvider extends AbstractLabelProvider {

    private static final Image IMG = EclipseResourceManager.getImage(RepositoryPlugin.PLUGIN_ID, "icons/view.png"); //$NON-NLS-1$;

    public String getCategoryLabel(ERepositoryObjectType type) {
        return "View"; //$NON-NLS-1$
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
                if (item instanceof WSViewItem) {
                    img = IMG;
                }
            }
        }
        return img;
    }

    @Override
    protected String getConainerItemText(Item item) {
        if (item.getState().getPath().equals(IPath.SEPARATOR + IViewNodeConstDef.PATH_SEARCHFILTER)) {
            return Messages.ViewLabelProvider_SearchfilterNodeName;
        } else if (item.getState().getPath().equals(IPath.SEPARATOR + IViewNodeConstDef.PATH_WEBFILTER)) {
            return Messages.ViewLabelProvider_WebfilterNodeName;
        }

        return super.getConainerItemText(item);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.talend.mdm.repository.core.impl.AbstractLabelProvider#getText(org.talend.core.model.properties.Item)
     */
    @Override
    protected String getText(Item item) {
        String text = super.getText(item);
        String itemText = RepositoryTransformUtil.getInstance().transformToSilyViewName(text, false);
        return itemText;
    }

    @Override
    protected boolean isSystemServerObjectItem(Object element) {
        Item item = getItem(element);
        if (item instanceof ContainerItem) {
            String path = item.getState().getPath();
            if (path.equals(IPath.SEPARATOR + IViewNodeConstDef.PATH_WEBFILTER)
                    || path.equals(IPath.SEPARATOR + IViewNodeConstDef.PATH_SEARCHFILTER)) {
                return true;
            }
        }

        return false;
    }

}
