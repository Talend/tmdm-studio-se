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
import org.talend.mdm.repository.core.marker.IValidationMarker;
import org.talend.mdm.repository.core.validate.datamodel.model.IDataModelMarkerConst;
import org.talend.mdm.repository.core.validate.datamodel.validator.impl.DataModelChecker;

/**
 * created by HHB on 2013-4-7 Detailled comment
 *
 */
public class ModelMarkerHelper {

    protected static final String BLANK = ""; //$NON-NLS-1$

    static Logger log = Logger.getLogger(ModelMarkerHelper.class);

    public static String getModelName(IMarker marker) {
        if (marker == null) {
            return BLANK;
        }
        IResource resource = marker.getResource();
        try {
            String type = marker.getType();
            if (type.equals(IValidationMarker.MARKER_XSD_ERR)) {
                if (resource != null) {
                    String dataModelName = DataModelChecker.getDataModelName(resource.getName());
                    return dataModelName;
                }
            } else if (type.equals(IValidationMarker.MARKER_DATA_MODEL)) {
                String msg = marker.getAttribute(IDataModelMarkerConst.DATA_MODEL, BLANK);
                return msg;
            }
        } catch (CoreException e) {
            log.error(e.getMessage(), e);
        }
        return BLANK;
    }
}
