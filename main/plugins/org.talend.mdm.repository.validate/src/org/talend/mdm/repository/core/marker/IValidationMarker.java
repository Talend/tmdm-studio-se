// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.repository.core.marker;

/**
 * created by HHB on 2013-1-31 Detailled comment
 * 
 */
public interface IValidationMarker {

    public static final String MARKER_MDM = "org.talend.mdm.problemmarker"; //$NON-NLS-1$

    public static final String MARKER_DATA_MODEL = "org.talend.mdm.error.datamodel.model"; //$NON-NLS-1$

    public static final String MARKER_XSD_ERR = "org.eclipse.xsd.diagnostic"; //$NON-NLS-1$
    
    public static final String MARKER_VIEW = "org.talend.mdm.viewmarker"; //$NON-NLS-1$
}
