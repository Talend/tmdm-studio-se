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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import com.amalto.workbench.image.OverlayImage.EPosition;
import com.amalto.workbench.utils.EXObjectStatus;

public class OverlayImageProvider {

    public static Image getImageWithStatus(Image source, EXObjectStatus status) {
        ImageDescriptor statusOverlay;
        switch (status) {
        case DEACTIVE:
            statusOverlay = ImageCache.getImage(EImage.WAITING_OVR.getPath());
            break;
        default:
            statusOverlay = null;
            break;
        }
        if (statusOverlay == null)
            return source;

        OverlayImage overlayImage = new OverlayImage(source, statusOverlay, EPosition.BOTTOM_RIGHT);
        return ImageProvider.getImage(overlayImage);
    }

}
