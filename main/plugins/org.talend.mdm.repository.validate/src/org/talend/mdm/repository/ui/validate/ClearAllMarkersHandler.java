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
package org.talend.mdm.repository.ui.validate;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.views.markers.MarkerViewHandler;
import org.talend.core.model.general.Project;
import org.talend.mdm.repository.core.marker.ValidateMarkerUtil;
import org.talend.repository.ProjectManager;

/**
 * created by HHB on 2013-1-31 Detailled comment
 *
 */
public class ClearAllMarkersHandler extends MarkerViewHandler {

    static Logger log = Logger.getLogger(ClearAllMarkersHandler.class);

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
     */
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        Project talendProject = ProjectManager.getInstance().getCurrentProject();
        if (talendProject != null) {
            IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(talendProject.getTechnicalLabel());
            try {
                for (String marker : ValidateMarkerUtil.getAllToRemoveMarkers()) {
                    project.deleteMarkers(marker, true, IResource.DEPTH_INFINITE);
                }
            } catch (CoreException e) {
                log.error(e.getMessage(), e);
            }
        }
        return null;
    }
}
