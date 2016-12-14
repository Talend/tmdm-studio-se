// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.rcp.branding.tombundle;

import org.talend.core.branding.DefaultBrandingConfiguration;

public class TOMBrandingConfiguration extends DefaultBrandingConfiguration {

    @Override
    public String getInitialWindowPerspectiveId() {
        return "org.talend.mdm.perspective"; //$NON-NLS-1$
    }
}
