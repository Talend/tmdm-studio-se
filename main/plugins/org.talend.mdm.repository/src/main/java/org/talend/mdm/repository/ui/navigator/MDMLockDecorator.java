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

import org.apache.log4j.Logger;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.runtime.model.repository.ERepositoryStatus;
import org.talend.core.context.Context;
import org.talend.core.context.RepositoryContext;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.dataquality.properties.TDQMatchRuleItem;
import org.talend.mdm.repository.model.mdmproperties.MDMServerObjectItem;
import org.talend.mdm.repository.plugin.RepositoryPlugin;
import org.talend.mdm.repository.utils.EclipseResourceManager;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;
import org.talend.repository.model.IProxyRepositoryFactory;

import com.amalto.workbench.exadapter.ExAdapterManager;

/**
 * created by liusongbo on 2013-4-25
 */
public class MDMLockDecorator implements ILightweightLabelDecorator {

    private static Logger log = Logger.getLogger(MDMLockDecorator.class);

    IProxyRepositoryFactory factory = CoreRuntimePlugin.getInstance().getProxyRepositoryFactory();

    private boolean editableAsReadOnly;

    private IMDMLockDecoratorExAdapter exAdapter;

    public MDMLockDecorator() {
        isEditableAsReadOnly();

        exAdapter = ExAdapterManager.getAdapter(this, IMDMLockDecoratorExAdapter.class);
    }

    private static final ImageDescriptor IMG_G_LOCK = EclipseResourceManager.getImageDescriptor(RepositoryPlugin.PLUGIN_ID,
            "icons/locked_green_overlay.gif"); //$NON-NLS-1$

    public void decorate(Object element, IDecoration decoration) {
        Item item = RepositoryResourceUtil.getItemFromRepViewObj(element);
        if (item != null) {
            if (item instanceof MDMServerObjectItem || item instanceof TDQMatchRuleItem) {
                try {
                    if (!factory.isLocalConnectionProvider()) {
                        isEditableAsReadOnly();
                    }
                } catch (PersistenceException e) {
                    log.error(e.getMessage(), e);
                }
                decorateLockImage((IRepositoryViewObject) element, decoration);
            }
        }

    }

    private void decorateLockImage(IRepositoryViewObject viewObj, IDecoration decoration) {
        ERepositoryStatus status = factory.getStatus(viewObj.getProperty().getItem());
        if (editableAsReadOnly) {
            if (status == ERepositoryStatus.LOCK_BY_USER) {
                status = ERepositoryStatus.DEFAULT;
            }
        }

        if (exAdapter != null) {
            exAdapter.decorateLockImage(decoration, status);
        } else {
            if (status == ERepositoryStatus.LOCK_BY_USER) {
                decoration.addOverlay(IMG_G_LOCK, IDecoration.BOTTOM_LEFT);
            } else {
                if (status != ERepositoryStatus.LOCK_BY_OTHER) {
                    decoration.addOverlay(null, IDecoration.BOTTOM_LEFT);
                }
            }
        }
    }

    private void isEditableAsReadOnly() {
        Context ctx = CoreRuntimePlugin.getInstance().getContext();
        RepositoryContext rc = (RepositoryContext) ctx.getProperty(Context.REPOSITORY_CONTEXT_KEY);

        editableAsReadOnly = rc.isEditableAsReadOnly();
    }

    // //////////////////
    public void addListener(ILabelProviderListener listener) {
    }

    public void removeListener(ILabelProviderListener listener) {
    }

    public void dispose() {
    }

    public boolean isLabelProperty(Object element, String property) {
        return false;
    }
}
