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
package org.talend.mdm.repository.core;

import org.talend.commons.ui.runtime.image.IImage;

/**
 * DOC hbhong it works for the common repository view. <br/>
 *
 */
public enum ServerObjectImage implements IImage {
    MENU_ICON("/icons/menu.png"), //$NON-NLS-1$
    ROLE_ICON("/icons/role.png"), //$NON-NLS-1$
    VIEW_ICON("/icons/view.png"), //$NON-NLS-1$
    DATAMODEL_ICON("/icons/datamodel.png"), //$NON-NLS-1$
    DATACLUSTER_ICON("/icons/datacluster.png"), //$NON-NLS-1$
    STOREPROCEDURE_ICON("/icons/stored_procedure.png"), //$NON-NLS-1$
    WORKFLOW_ICON("/icons/workflow_process.png"), //$NON-NLS-1$
    TRANSFORMERV2_ICON("/icons/transformer.png"), //$NON-NLS-1$
    ROUTINGRULE_ICON("/icons/routing_rule.png"), //$NON-NLS-1$
    JOBMODEL_ICON("/icons/joblet_icon.png"), //$NON-NLS-1$
    RESOURCE_ICON("/icons/resources.png"), //$NON-NLS-1$
    SERVICECONFIGURATION_ICON("/icons/service_config.png"), //$NON-NLS-1$
    EVENTMANAGER_ICON("/icons/events_management.png"), //$NON-NLS-1$
    CUSTOMFORM_ICON("/icons/customform.png"), //$NON-NLS-1$
    RECYCLEBIN_ICON("/icons/recycle_bin_full.png");//$NON-NLS-1$

    private String path;

    ServerObjectImage(String path) {
        this.path = path;
    }

    /**
     * Getter for path.
     *
     * @return the path
     */
    public String getPath() {
        return this.path;
    }

    /**
     * Getter for clazz.
     *
     * @return the clazz
     */
    public Class getLocation() {
        return ServerObjectImage.class;
    }
}
