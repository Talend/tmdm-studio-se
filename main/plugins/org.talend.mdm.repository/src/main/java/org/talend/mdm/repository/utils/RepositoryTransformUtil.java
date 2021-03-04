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
package org.talend.mdm.repository.utils;

import org.talend.mdm.repository.core.impl.transformerV2.ITransformerV2NodeConsDef;
import org.talend.mdm.repository.core.impl.view.IViewNodeConstDef;
import org.talend.mdm.repository.i18n.Messages;

import com.amalto.workbench.service.bridge.ITransformService;

/**
 * created by liusongbo on 2012-9-28 Detailled comment
 */
public class RepositoryTransformUtil implements ITransformService, ITransformerV2NodeConsDef, IViewNodeConstDef {

    private static final String SEPARATOR = "#"; //$NON-NLS-1$

    public static RepositoryTransformUtil instance;

    public static RepositoryTransformUtil getInstance() {
        if (instance == null) {
            instance = new RepositoryTransformUtil();
        }

        return instance;
    }

    public String transformToSilyViewName(final String internalName, boolean withDirName) {
        if (internalName == null || internalName.isEmpty()) {
            return internalName;
        }
        String filterName = null;
        String dirName = null;
        String prefix = null;

        String transformedName = internalName;
        int viewType = getViewType(internalName);

        if (viewType == TYPE_WEBFILTER) {
            prefix = PREFIX_VIEW_UPPER;
            filterName = transformedName;
            dirName = Messages.ViewLabelProvider_WebfilterNodeName;
        } else if (viewType == TYPE_SEARCHFILTER) {
            dirName = Messages.ViewLabelProvider_SearchfilterNodeName;
        }
        if (prefix != null) {
            transformedName = transformedName.substring(prefix.length());
        }
        if (filterName != null) {
            int index = transformedName.lastIndexOf(SEPARATOR);
            if (index > 0) {
                filterName = transformedName.substring(index + 1);
                transformedName = transformedName.substring(0, index);
                int spaceIndex = filterName.indexOf(" "); //$NON-NLS-1$
                if (spaceIndex > 0) {
                    transformedName += filterName.substring(spaceIndex);
                    filterName = filterName.substring(0, spaceIndex);
                }
            } else {
                filterName = null;
            }

        }

        return transformedName + getOptionalName(filterName) + (withDirName ? getDirectoryName(dirName) : ""); //$NON-NLS-1$
    }

    private String getOptionalName(String optionalName) {
        if (optionalName == null || optionalName.trim().length() == 0)
            return ""; //$NON-NLS-1$
        else
            return " (" + optionalName + ")"; //$NON-NLS-1$//$NON-NLS-2$
    }

    private String getDirectoryName(String name) {
        if (name == null || name.trim().length() == 0)
            return ""; //$NON-NLS-1$
        else
            return " [" + name + "]"; //$NON-NLS-1$ //$NON-NLS-2$
    }

    public String transformToSilyProcessName(final String internalName, boolean withDirName) {
        if (internalName == null || internalName.trim().length() == 0) {
            return internalName;
        }

        String prefix = null;
        String optionalName = null;
        String dirName = null;

        String transformedName = internalName;
        int type = getProcessType(internalName);
        switch (type) {
        case TYPE_BEFORESAVE:
            prefix = PREFIX_BEFORESAVE_UPPER;
            dirName = Messages.TransformerV2XX_BeforeSaving;
            break;
        case TYPE_BEFOREDEL:
            prefix = PREFIX_BEFOREDEL_UPPER;
            dirName = Messages.TransformerV2XX_BeforeDeleting;
            break;
        case TYPE_ENTITYACTION:
            prefix = PREFIX_RUNNABLE_UPPER;
            optionalName = transformedName;
            dirName = Messages.TransformerV2XX_EntityAction;
            break;
        case TYPE_WELCOMEACTION:
            prefix = PREFIX_STANDLONE_UPPER;
            dirName = Messages.TransformerV2XX_WelcomeAction;
            break;
        case TYPE_SMARTVIEW:
            prefix = PREFIX_SMARTVIEW_UPPER;
            optionalName = transformedName;
            dirName = Messages.TransformerV2XX_SmartView;
            break;
        case TYPE_OTHER:
            dirName = Messages.TransformerV2XX_Other;
            break;
        default:
            break;
        }

        if (prefix != null) {
            transformedName = transformedName.substring(prefix.length());
        }
        if (optionalName != null) {
            int index = transformedName.lastIndexOf(SEPARATOR);
            if (index > 0) {
                optionalName = transformedName.substring(index + 1);
                transformedName = transformedName.substring(0, index);
                int spaceIndex = optionalName.indexOf(" "); //$NON-NLS-1$
                if (spaceIndex > 0) {
                    transformedName += optionalName.substring(spaceIndex);
                    optionalName = optionalName.substring(0, spaceIndex);
                }
            } else {
                optionalName = null;
            }

        }

        return transformedName + getOptionalName(optionalName) + (withDirName ? getDirectoryName(dirName) : ""); //$NON-NLS-1$
    }

    public int getProcessType(String processName) {
        if (processName == null || processName.trim().length() == 0) {
            return -1;
        }
        if (processName.startsWith(PREFIX_BEFORESAVE_UPPER)) {
            return TYPE_BEFORESAVE;
        } else if (processName.startsWith(PREFIX_BEFOREDEL_UPPER)) {
            return TYPE_BEFOREDEL;
        } else if (processName.startsWith(PREFIX_RUNNABLE_UPPER)) {
            return TYPE_ENTITYACTION;
        } else if (processName.startsWith(PREFIX_STANDLONE_UPPER) || processName.startsWith(PREFIX_STANDLONE_UPPER_OLD)) {
            return TYPE_WELCOMEACTION;
        } else if (processName.startsWith(PREFIX_SMARTVIEW_UPPER)) {
            return TYPE_SMARTVIEW;
        } else {
            return TYPE_OTHER;
        }
    }

    public String getProcessPath(String processName, boolean withSeperator) {

        if (processName != null) {
            int processType = RepositoryTransformUtil.getInstance().getProcessType(processName);
            String prefix = withSeperator ? "/" : ""; //$NON-NLS-1$ //$NON-NLS-2$
            String path = ""; //$NON-NLS-1$
            switch (processType) {
            case ITransformerV2NodeConsDef.TYPE_BEFORESAVE:
                path = PATH_BEFORESAVE;
                break;
            case ITransformerV2NodeConsDef.TYPE_BEFOREDEL:
                path = PATH_BEFOREDEL;
                break;
            case ITransformerV2NodeConsDef.TYPE_ENTITYACTION:
                path = PATH_ENTITYACTION;
                break;
            case ITransformerV2NodeConsDef.TYPE_WELCOMEACTION:
                path = PATH_WELCOMEACTION;
                break;
            case ITransformerV2NodeConsDef.TYPE_SMARTVIEW:
                path = PATH_SMARTVIEW;
                break;
            case ITransformerV2NodeConsDef.TYPE_OTHER:
                path = PATH_OTHER;
                break;
            }
            return prefix + path;
        }
        return null;
    }

    public int getViewType(String viewName) {
        if (viewName == null || viewName.trim().length() == 0) {
            return -1;
        }
        if (viewName.startsWith(PREFIX_VIEW_UPPER)) {
            return TYPE_WEBFILTER;
        } else
            return TYPE_SEARCHFILTER;
    }

    public String getViewPath(String viewName) {
        if (viewName != null) {
            int viewType = getViewType(viewName);
            if (viewType == TYPE_WEBFILTER) {
                return PATH_WEBFILTER;
            } else
                return PATH_SEARCHFILTER;
        }
        return null;
    }
}
