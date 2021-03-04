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
package com.amalto.workbench.providers.datamodel.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.graphics.Image;
import org.w3c.dom.Element;

import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;

public class TypeItemImageCreator extends SchemaItemImageCreator {

    private static Log log = LogFactory.getLog(TypeItemImageCreator.class);

    private static TypeItemImageCreator INSTANCE;

    protected TypeItemImageCreator() {
    }

    public static synchronized SchemaItemImageCreator getInstance() {

        if (INSTANCE == null)
            INSTANCE = new TypeItemImageCreator();

        return INSTANCE;
    }

    @Override
    protected Image getImageForUnknown() {
        return ImageCache.getCreatedImage(EImage.TREE_OBJECT.getPath());
    }

    @Override
    protected Image getImageForElement(Element element) {

        try {
            if ("documentation".equals(element.getLocalName()))//$NON-NLS-1$
                return ImageCache.getCreatedImage(EImage.DOCUMENTATION.getPath());

            if ("appinfo".equals(element.getLocalName())) {//$NON-NLS-1$
                String source = element.getAttribute("source");//$NON-NLS-1$
                if (source != null) {
                    if (source.startsWith("X_Label_"))//$NON-NLS-1$
                        return ImageCache.getCreatedImage(EImage.LABEL.getPath());

                    if (source.equals("X_ForeignKey"))//$NON-NLS-1$
                        return ImageCache.getCreatedImage(EImage.PRIMARYKEY.getPath());

                    if (source.equals("X_ForeignKeyInfo"))//$NON-NLS-1$
                        return ImageCache.getCreatedImage(EImage.KEYINFO.getPath());

                    if (source.equals("X_FKIntegrity"))//$NON-NLS-1$
                        return ImageCache.getCreatedImage(EImage.KEYINFO.getPath());

                    if (source.equals("X_FKIntegrity_Override"))//$NON-NLS-1$
                        return ImageCache.getCreatedImage(EImage.KEYINFO.getPath());

                    if (source.equals("X_SourceSystem"))//$NON-NLS-1$
                        return ImageCache.getCreatedImage(EImage.SOURCESYSTEM.getPath());

                    if (source.equals("X_TargetSystem"))//$NON-NLS-1$
                        return ImageCache.getCreatedImage(EImage.TARGETSYSTEM.getPath());

                    if (source.startsWith("X_Description_"))//$NON-NLS-1$
                        return ImageCache.getCreatedImage(EImage.DOCUMENTATION.getPath());

                    if (source.equals("X_Write"))//$NON-NLS-1$
                        return ImageCache.getCreatedImage(EImage.SECURITYANNOTATION.getPath());

                    if (source.equals("X_Hide"))//$NON-NLS-1$
                        return ImageCache.getCreatedImage(EImage.SECURITYANNOTATION.getPath());
                }
            }

            return ImageCache.getCreatedImage(EImage.DOCUMENTATION.getPath());
        } catch (Exception e) {

            log.error(e.getMessage(), e);
        }

        return getImageForUnknown();
    }
}
