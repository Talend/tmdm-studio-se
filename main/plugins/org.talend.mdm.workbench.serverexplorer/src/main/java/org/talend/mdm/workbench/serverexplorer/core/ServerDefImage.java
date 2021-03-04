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
package org.talend.mdm.workbench.serverexplorer.core;

import org.talend.commons.ui.runtime.image.IImage;

public enum ServerDefImage implements IImage {
    BEAN_ICON("/icons/server.png");//$NON-NLS-1$

    private String path;

    ServerDefImage(String path) {
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
    public Class<ServerDefImage> getLocation() {
        return ServerDefImage.class;
    }
}
