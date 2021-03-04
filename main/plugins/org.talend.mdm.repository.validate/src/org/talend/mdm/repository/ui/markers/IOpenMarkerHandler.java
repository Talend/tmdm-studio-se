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
package org.talend.mdm.repository.ui.markers;

import org.eclipse.core.resources.IMarker;
import org.eclipse.ui.IWorkbenchPage;

/**
 * created by HHB on 2014-3-31 Detailled comment
 *
 */
public interface IOpenMarkerHandler {

    public boolean canOpen(IMarker marker);

    public void open(IWorkbenchPage page, IMarker marker, Object param);
}
