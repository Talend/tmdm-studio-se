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
package org.talend.mdm.repository.ui.markers.datamodel;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IWorkbenchPage;
import org.talend.mdm.repository.core.marker.IValidationMarker;
import org.talend.mdm.repository.core.validate.datamodel.validator.impl.DataModelChecker;

/**
 * created by HHB on 2014-3-31 Detailled comment
 *
 */
public class OpenXSDMarkerHandler extends AbstractOpenDataModelMarkerHandler {

    static Logger log = Logger.getLogger(OpenXSDMarkerHandler.class);

    @Override
    public boolean canOpen(IMarker marker) {

        try {
            return marker.getType().equals(IValidationMarker.MARKER_XSD_ERR);
        } catch (CoreException e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public void open(IWorkbenchPage page, IMarker marker, Object param) {
        IResource resource = marker.getResource();
        if (resource != null) {
            String dataModelName = DataModelChecker.getDataModelName(resource.getName());
            openDataModel(dataModelName, marker);
        }
    }

}
