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

import org.eclipse.core.resources.IMarker;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.IServerObjectRepositoryType;
import org.talend.mdm.repository.ui.actions.OpenObjectAction;
import org.talend.mdm.repository.ui.markers.IOpenMarkerHandler;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;

/**
 * created by HHB on 2014-3-31 Detailled comment
 *
 */
public abstract class AbstractOpenDataModelMarkerHandler implements IOpenMarkerHandler {

    protected void openDataModel(String modelName, IMarker marker) {
        IRepositoryViewObject viewObj = RepositoryResourceUtil.findViewObjectByName(IServerObjectRepositoryType.TYPE_DATAMODEL,
                modelName);
        OpenObjectAction openAction = new OpenObjectAction();

        openAction.openMarker(viewObj, marker);

    }

}
