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
package org.talend.mdm.repository.ui.navigator;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.model.mdmproperties.WSRoutingRuleItem;
import org.talend.mdm.repository.model.mdmserverobject.WSRoutingRuleE;
import org.talend.mdm.repository.plugin.RepositoryPlugin;
import org.talend.mdm.repository.utils.EclipseResourceManager;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;

public class TriggerDecorator implements ILightweightLabelDecorator {

    private static final ImageDescriptor IMG_TRIGGER_DEACTIVE = EclipseResourceManager.getImageDescriptor(
            RepositoryPlugin.PLUGIN_ID, "icons/waiting_ovr.gif"); //$NON-NLS-1$

    public void decorate(Object element, IDecoration decoration) {
        IRepositoryViewObject viewObj = (IRepositoryViewObject) element;
        Item item = RepositoryResourceUtil.getItemFromRepViewObj(viewObj);
        if (item != null && item instanceof WSRoutingRuleItem) {
            WSRoutingRuleE routing = ((WSRoutingRuleItem) item).getWsRoutingRule();
            if (routing.isDeactive()) {
                decoration.addOverlay(IMG_TRIGGER_DEACTIVE, IDecoration.BOTTOM_LEFT);
            }
        }
    }

    public void addListener(ILabelProviderListener listener) {
    }

    public void dispose() {
    }

    public boolean isLabelProperty(Object element, String property) {
        return false;
    }

    public void removeListener(ILabelProviderListener listener) {
    }
}
