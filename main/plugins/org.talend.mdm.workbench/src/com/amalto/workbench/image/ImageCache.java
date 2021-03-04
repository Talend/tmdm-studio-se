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
package com.amalto.workbench.image;

import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Image;

import com.amalto.workbench.MDMWorbenchPlugin;

public class ImageCache {

    static org.eclipse.jface.resource.ImageRegistry registry = null;
    static {
        registry = JFaceResources.getImageRegistry();
        Map<String, EImage> map = EImage.getAlllEimages();
        Iterator<EImage> it = map.values().iterator();
        while (it.hasNext()) {
            EImage image = it.next();
            registry.put(image.getPath(), MDMWorbenchPlugin.getImageDescriptor(image.getPath()));
        }
    }

    public static ImageDescriptor getImage(String path) {
        if (null != registry.getDescriptor(path))
            return registry.getDescriptor(path);
        else {
            return registry.getDescriptor("icons/talend-picto-small.gif");//$NON-NLS-1$
        }
    }

    public static Image getCreatedImage(String path) {
        Image img = registry.get(path);
        if (null != img)
            return img;
        else {
            registry.put(path, MDMWorbenchPlugin.getImageDescriptor(path));
            return registry.get(path);
            // return registry.get("icons/appli_16x16.gif");
        }
    }
}
