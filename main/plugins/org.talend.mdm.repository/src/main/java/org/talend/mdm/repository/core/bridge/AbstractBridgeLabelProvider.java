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

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Image;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.runtime.model.repository.ERepositoryStatus;
import org.talend.commons.ui.runtime.image.OverlayImageProvider;
import org.talend.core.context.Context;
import org.talend.core.context.RepositoryContext;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.model.repository.RepositoryViewObject;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.core.ui.images.CoreImageProvider;
import org.talend.mdm.repository.core.impl.AbstractLabelProvider;
import org.talend.mdm.repository.model.mdmproperties.ContainerItem;
import org.talend.repository.model.IProxyRepositoryFactory;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class AbstractBridgeLabelProvider extends AbstractLabelProvider {

    private static Logger log = Logger.getLogger(AbstractBridgeLabelProvider.class);

    private IProxyRepositoryFactory factory = CoreRuntimePlugin.getInstance().getProxyRepositoryFactory();

    private boolean editableAsReadOnly;

    public AbstractBridgeLabelProvider() {
        isEditableAsReadOnly();
    }

    private void isEditableAsReadOnly() {
        Context ctx = CoreRuntimePlugin.getInstance().getContext();
        RepositoryContext rc = (RepositoryContext) ctx.getProperty(Context.REPOSITORY_CONTEXT_KEY);
        this.editableAsReadOnly = rc.isEditableAsReadOnly();
    }

    public String getCategoryLabel(ERepositoryObjectType type) {
        return type.toString();
    }

    @Override
    protected String getServerObjectItemText(Item item) {
        return ""; //$NON-NLS-1$
    }

    @Override
    public Image getCategoryImage(Item item) {
        if (item instanceof ContainerItem) {
            ERepositoryObjectType repObjType = ((ContainerItem) item).getRepObjType();
            return CoreImageProvider.getImage(repObjType);
        }
        return null;
    }

    @Override
    public Image getImage(Object element) {
        Image img = super.getImage(element);
        if (img == null && element instanceof IRepositoryViewObject) {
            try {
                if (!factory.isLocalConnectionProvider()) {
                    isEditableAsReadOnly();
                }
            } catch (PersistenceException e) {
                log.error(e.getMessage(), e);
            }

            return getRepositoryImage((IRepositoryViewObject) element);
        }
        return img;
    }

    protected Image getRepositoryImage(IRepositoryViewObject object) {
        // Item item = property.getItem();
        ERepositoryObjectType itemType = object.getRepositoryObjectType();
        Image img = null;
        if (object instanceof RepositoryViewObject && ((RepositoryViewObject) object).getCustomImage() != null) {
            img = ((RepositoryViewObject) object).getCustomImage();
        } else {
            img = CoreImageProvider.getImage(itemType);
        }

        ERepositoryStatus repositoryStatus = object.getRepositoryStatus();
        // For offline mode,its item is locked by default but need show the default image.
        if (editableAsReadOnly) {
            if (repositoryStatus == ERepositoryStatus.LOCK_BY_USER) {
                repositoryStatus = ERepositoryStatus.DEFAULT;
            }
        }

        Image image = OverlayImageProvider.getImageWithStatus(img, repositoryStatus);

        ERepositoryStatus informationStatus = object.getInformationStatus();

        return OverlayImageProvider.getImageWithStatus(image, informationStatus);
    }
}
