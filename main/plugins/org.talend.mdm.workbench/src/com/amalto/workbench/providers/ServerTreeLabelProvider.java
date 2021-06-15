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
package com.amalto.workbench.providers;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.talend.core.GlobalServiceRegister;
import org.talend.mdm.commmon.util.webapp.XSystemObjects;

import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.image.OverlayImageProvider;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.service.bridge.ITransformService;
import com.amalto.workbench.utils.EXObjectStatus;
import com.amalto.workbench.utils.FontUtils;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.webservices.WSGetRoutingRule;
import com.amalto.workbench.webservices.WSRoutingRule;
import com.amalto.workbench.webservices.WSRoutingRulePK;
import com.amalto.workbench.webservices.WSTransformerV2;
import com.amalto.workbench.webservices.WSView;

public class ServerTreeLabelProvider extends ColumnLabelProvider implements IColorProvider, IFontProvider {

    private Font font = FontUtils.getBoldFont(Display.getCurrent().getSystemFont());

    private Color color = new Color(null, 150, 150, 150);

    @Override
    public String getText(Object obj) {
        String label = obj.toString();

        if (obj instanceof TreeObject) {
            label = filterName(label, (TreeObject) obj);
        }
        return label;
    }

    private String filterName(String label, TreeObject treeObject) {
        String transformedName = label;

        if (label == null || label.isEmpty()) {
            return transformedName;
        }

        Object wsObject = treeObject.getWsObject();
        ITransformService transformService = (ITransformService) GlobalServiceRegister.getDefault().getService(
                ITransformService.class);
        if (wsObject instanceof WSTransformerV2) {
            transformedName = transformService.transformToSilyProcessName(label, true);
        } else if (wsObject instanceof WSView) {
            transformedName = transformService.transformToSilyViewName(label, true);
        }

        return transformedName;
    }

    @Override
    public Image getImage(Object obj) {
        // if (obj instanceof TreeParent) {
        TreeObject object = (TreeObject) obj;
        if (object.getType() == TreeObject._SERVER_) {
            return ImageCache.getCreatedImage(EImage.DEFAULT.getPath());
        } else if (object.getType() == TreeObject.DATA_CLUSTER) {
            return ImageCache.getCreatedImage(EImage.DATA_CLUSTER.getPath());
        } else if (object.getType() == TreeObject.DATA_MODEL) {
            return ImageCache.getCreatedImage(EImage.DATA_MODEL.getPath());
        } else if (object.getType() == TreeObject.RESOURCES || object.getType() == TreeObject.DATA_MODEL_RESOURCE
                || object.getType() == TreeObject.DATA_MODEL_TYPES_RESOURCE
                || object.getType() == TreeObject.CUSTOM_TYPES_RESOURCE || object.getType() == TreeObject.PICTURES_RESOURCE) {
            return ImageCache.getCreatedImage(EImage.RESOURCES.getPath());
        } else if (object.getType() == TreeObject.MENU) {
            return ImageCache.getCreatedImage(EImage.MENU.getPath());
        } else if (object.getType() == TreeObject.TRANSFORMER) {
            return ImageCache.getCreatedImage(EImage.TRANSFORMER.getPath());
        } else if (object.getType() == TreeObject.ROLE) {
            return ImageCache.getCreatedImage(EImage.ROLE.getPath());
        } else if (object.getType() == TreeObject.STORED_PROCEDURE) {
            return ImageCache.getCreatedImage(EImage.STORED_PROCEDURE.getPath());
        } else if (object.getType() == TreeObject.ROUTING_RULE) {
            Image img = ImageCache.getCreatedImage(EImage.ROUTING_RULE.getPath());
            if (object.isXObject()) {
                WSRoutingRule ws = (WSRoutingRule) (object.getWsObject());
                try {
                    if (ws == null) {
                        ws = Util.getMDMService(object).getRoutingRule(
                                new WSGetRoutingRule(new WSRoutingRulePK(object.getDisplayName())));
                    }
                } catch (Exception e) {
                }

                if (ws != null && ws.isDeactive() != null && ws.isDeactive()) {
                    img = OverlayImageProvider.getImageWithStatus(img, EXObjectStatus.DEACTIVE);
                }
            }
            return img;
        } else if (object.getType() == TreeObject.VIEW) {
            return ImageCache.getCreatedImage(EImage.VIEW.getPath());
        } else if (object.getType() == TreeObject.DOCUMENT) {
            return ImageCache.getCreatedImage(EImage.DOCUMENTS.getPath());
        } else if (object.getType() == TreeObject.SUBSCRIPTION_ENGINE) {
            return ImageCache.getCreatedImage(EImage.SUBSCRIPTION_ENGINE.getPath());
        } else if (object.getType() == TreeObject.EVENT_MANAGEMENT) {
            return ImageCache.getCreatedImage(EImage.EVENTM_ANAGEMENT.getPath());
        } else if (object.getType() == TreeObject.JOB_REGISTRY || object.getType() == TreeObject.JOB
                || object.getType() == TreeObject.TIS_JOB) {
            return ImageCache.getCreatedImage(EImage.JOB.getPath());
        } else if (object.getType() == TreeObject.SERVICE_CONFIGURATION) {
            return ImageCache.getCreatedImage(EImage.SERVICE_CONFIGURATION.getPath());
        } else if (object.getType() == TreeObject.CATEGORY_FOLDER) {
            return ImageCache.getCreatedImage("icons/directory-close.png");//$NON-NLS-1$
        } else if (object.getType() == TreeObject.BUILT_IN_CATEGORY_FOLDER) {
            if (object.getDisplayName().equals("Deployed Jobs")) {
                return ImageCache.getCreatedImage("icons/folder_deployed-jobs.png");//$NON-NLS-1$
            }
            return ImageCache.getCreatedImage("icons/folder_source-jobs.png");//$NON-NLS-1$
        } else if (object.getType() == TreeObject._INVISIBLE) {
            return ImageCache.getCreatedImage(EImage.SANDGLASS.getPath());
        } else if (object.getType() == TreeObject.CUSTOM_FORM) {
            return ImageCache.getCreatedImage(EImage.CUSTOM_FORM.getPath());
        }

        return ImageCache.getCreatedImage("icons/phased_out.gif");//$NON-NLS-1$

    }

    @Override
    public Color getBackground(Object element) {
        return null;
    }

    @Override
    public Color getForeground(Object element) {
        TreeObject tb = (TreeObject) element;
        if (XSystemObjects.isExist(tb.getType(), tb.getDisplayName())) {
            return color;
        } else if (tb.getType() == TreeObject._SERVER_) {
            return null;
        } else {
            return null;
        }
    }

    @Override
    public Font getFont(Object element) {
        TreeObject tb = (TreeObject) element;
        if (XSystemObjects.isExist(tb.getType(), tb.getDisplayName())) {
            return font;
        }
        return null;
    }

    @Override
    public String getToolTipText(Object object) {
        if (object instanceof TreeObject) {
            if (((TreeObject) object).getType() == TreeObject._SERVER_) {
                return ((TreeObject) object).getEndpointAddress();
            }
        }
        return null;
    }

    @Override
    public Point getToolTipShift(Object object) {
        return new Point(5, 5);
    }

    @Override
    public void dispose() {
        color.dispose();
    }
}
