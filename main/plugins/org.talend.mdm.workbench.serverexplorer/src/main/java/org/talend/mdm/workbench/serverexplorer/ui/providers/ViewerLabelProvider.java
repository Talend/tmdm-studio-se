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
package org.talend.mdm.workbench.serverexplorer.ui.providers;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.model.mdmmetadata.MDMServerDef;
import org.talend.mdm.repository.model.mdmproperties.MDMServerDefItem;
import org.talend.mdm.workbench.serverexplorer.plugin.MDMServerExplorerPlugin;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class ViewerLabelProvider extends LabelProvider implements IColorProvider {

    private static final Color COLOR_GRAY = Display.getDefault().getSystemColor(SWT.COLOR_GRAY);

    static final Image IMG_SERVER_DEF = AbstractUIPlugin.imageDescriptorFromPlugin(MDMServerExplorerPlugin.PLUGIN_ID,
            "icons/server.png").createImage(); //$NON-NLS-1$

    @Override
    public Image getImage(Object element) {
        return IMG_SERVER_DEF;
    }

    @Override
    public String getText(Object element) {
        if (element instanceof IRepositoryViewObject) {
            MDMServerDefItem mdmItem = getMDMItem((IRepositoryViewObject) element);
            if (mdmItem != null) {
                MDMServerDef serverDef = mdmItem.getServerDef();
                return serverDef.getName() + " (" + serverDef.getHost() + ":" + serverDef.getPort() + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            }
        }
        return ""; //$NON-NLS-1$
    }

    private MDMServerDefItem getMDMItem(IRepositoryViewObject viewObject) {
        if (viewObject != null) {
            return (MDMServerDefItem) (viewObject.getProperty().getItem());
        }
        return null;
    }

    public Color getForeground(Object element) {
        if (element instanceof IRepositoryViewObject) {
            MDMServerDefItem mdmItem = getMDMItem((IRepositoryViewObject) element);
            if (mdmItem != null) {
                MDMServerDef serverDef = mdmItem.getServerDef();
                return serverDef.isEnabled() ? null : COLOR_GRAY;
            }
        }
        return null;
    }

    public Color getBackground(Object element) {
        return null;
    }

}
