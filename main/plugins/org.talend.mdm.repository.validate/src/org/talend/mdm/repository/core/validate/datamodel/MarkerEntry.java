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
package org.talend.mdm.repository.core.validate.datamodel;

import org.eclipse.core.resources.IMarker;
import org.eclipse.ui.views.markers.MarkerItem;

/**
 * created by Huang Zhenlong on Jan 28, 2013 Detailled comment
 *
 */
public class MarkerEntry extends MarkerItem {

    private IMarker marker;

    public MarkerEntry(IMarker marker) {
        this.marker = marker;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.views.markers.MarkerItem#getAttributeValue(java.lang.String, boolean)
     */
    @Override
    public boolean getAttributeValue(String attribute, boolean defaultValue) {
        return marker.getAttribute(attribute, defaultValue);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.views.markers.MarkerItem#getAttributeValue(java.lang.String, int)
     */
    @Override
    public int getAttributeValue(String attribute, int defaultValue) {
        return marker.getAttribute(attribute, defaultValue);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.views.markers.MarkerItem#getAttributeValue(java.lang.String, java.lang.String)
     */
    @Override
    public String getAttributeValue(String attribute, String defaultValue) {
        return marker.getAttribute(attribute, defaultValue);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.views.markers.MarkerItem#getPath()
     */
    @Override
    public String getPath() {
        return marker.getResource().getFullPath().toOSString();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.views.markers.MarkerItem#getMarker()
     */
    @Override
    public IMarker getMarker() {
        return marker;
    }
}
