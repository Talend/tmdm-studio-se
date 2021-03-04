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
package com.amalto.workbench.detailtabs.sections.util;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.xsd.XSDSchema;

import com.amalto.workbench.detailtabs.sections.IMDMRepositoryViewServiceExt;
import com.amalto.workbench.models.TreeObject;

public class MDMRepositoryViewExtensionService {

    private static Log log = LogFactory.getLog(MDMRepositoryViewExtensionService.class);

    private static final String PLUGIN = "org.talend.mdm.workbench"; //$NON-NLS-1$

    private static final String EXTENSION_POINT = "repositoryViewService"; //$NON-NLS-1$

    private static final String PROP_CLASS = "class"; //$NON-NLS-1$

    public static IMDMRepositoryViewServiceExt getRepositoryViewService() {
        IExtensionRegistry registry = Platform.getExtensionRegistry();
        IExtensionPoint extensionPoint = registry.getExtensionPoint(PLUGIN, EXTENSION_POINT);
        if (extensionPoint != null && extensionPoint.isValid()) {
            IExtension[] extensions = extensionPoint.getExtensions();
            for (IExtension s : extensions) {
                IConfigurationElement[] elements = s.getConfigurationElements();
                for (IConfigurationElement element : elements) {
                    if (element.getAttribute(PROP_CLASS) != null) {
                        try {
                            IMDMRepositoryViewServiceExt repositoryViewService = (IMDMRepositoryViewServiceExt) element
                                    .createExecutableExtension(PROP_CLASS);
                            return repositoryViewService;
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                            return null;
                        }
                    }
                }
            }
        }
        return null;
    }

    public static List<String> findAllRoleNames() {
        IMDMRepositoryViewServiceExt service = getRepositoryViewService();
        if (service != null) {
            return service.findAllRoleNames();
        } else {
            return null;
        }
    }

    public static List<String> findAllWorkflowNames() {
        IMDMRepositoryViewServiceExt service = getRepositoryViewService();
        if (service != null) {
            return service.findAllWorkflowNames();
        } else {
            return null;
        }

    }

    public static List<String> findAllDataModelNames() {
        IMDMRepositoryViewServiceExt service = getRepositoryViewService();
        if (service != null) {
            return service.findAllDataModelNames();
        } else {
            return null;
        }

    }

    public static XSDSchema getDataModelXsd(TreeObject pObject, String filter, String dataModelName) {
        IMDMRepositoryViewServiceExt service = getRepositoryViewService();
        if (service != null) {
            return service.getDataModelXsd(pObject, filter, dataModelName);
        } else {
            return null;
        }

    }

    public static IWorkbenchPartSite getMDMRepositoryViewSite() {
        IMDMRepositoryViewServiceExt service = getRepositoryViewService();
        if (service != null) {
            return service.getMDMRepositoryViewSite();
        } else {
            return null;
        }

    }

}
