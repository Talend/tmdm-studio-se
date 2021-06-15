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
package com.amalto.workbench.widgets.xmleditor.infoholder;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;

import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.models.infoextractor.IAllDataModelHolder;
import com.amalto.workbench.utils.JobInfo;
import com.amalto.workbench.webservices.TMDMService;
import com.amalto.workbench.webservices.WSMDMConfig;
import com.amalto.workbench.webservices.WSTransformerV2;

public abstract class ExternalInfoHolder<T> {

    public static final String INFOID_ALLPROCESSNAMES = "all process names";//$NON-NLS-1$

    public static final String INFOID_ALLJOBINFOS = "all job infos";//$NON-NLS-1$

    public static final String INFOID_MDMSERVERINFO = "mdm server info";//$NON-NLS-1$

    public static final String INFOID_ALLDATAMODELHOLDER = "all data model holder";//$NON-NLS-1$

    public static final String INFOID_ALLCALLJOBVARIABLES = "all calljob vars";//$NON-NLS-1$

    public static final String INFOID_ALLMDMSERVERINFO = "all mdm server infos";//$NON-NLS-1$

    private static final Map<String, ExternalInfoHolder<?>> cache = new HashMap<String, ExternalInfoHolder<?>>();

    private static final String holderExtension = "org.talend.mdm.workbench.infoholder";

    protected static Logger log = Logger.getLogger(ExternalInfoHolder.class);

    private static ExternalInfoHolder<?> getHolderFromExtension(String type) {
        IExtensionPoint point = Platform.getExtensionRegistry().getExtensionPoint(holderExtension);
        if (null == point) {
            return null;
        }
        for (IExtension ext : point.getExtensions()) {
            IConfigurationElement[] configurationElements = ext.getConfigurationElements();
            for (IConfigurationElement ce : configurationElements) {
                String id = ce.getAttribute("type"); //$NON-NLS-1$
                if (type.equals(id)) {
                    try {
                        return (ExternalInfoHolder<?>) ce.createExecutableExtension("class");
                    } catch (CoreException e) {
                        log.error(e.getMessage());
                    }
                }
            }
        }
        return null;
    }

    public static ExternalInfoHolder<?> getEnternalInfoHolder(String type) {
        ExternalInfoHolder<?> holder = cache.get(type);
        if (null == holder) {
            holder = getHolderFromExtension(type);
            cache.put(type, holder);
        }
        return holder;
    }

    public static ExternalInfoHolder<String[]> getAllProcessesNamesHolder(TMDMService service) {
        return new AllProcessesNamesHolder(service);
    }

    public static ExternalInfoHolder<JobInfo[]> getAllJobInfosHolder(TMDMService service) {
        return new AllJobInfoHolder(service);
    }

    public static ExternalInfoHolder<WSMDMConfig> getAllMDMServerInfoHolder(TMDMService service) {
        return new MDMServerInfoHolder(service);
    }

    public static ExternalInfoHolder<IAllDataModelHolder> getAllDataModelInfoHolderProxy(TreeObject treeNode) {
        return new AllDataModelInfoHolderProxy(treeNode);
    }

    public static ExternalInfoHolder<String[]> getProcessAllCallJobVarsCandidatesHolder(WSTransformerV2 service) {
        return new ProcessAllCallJobVariableCandidatesHolder(service);
    }

    public static ExternalInfoHolder<String[]> getTriggerAllCallJobVarsCandidatesHolder() {
        return (ExternalInfoHolder<String[]>) getEnternalInfoHolder("callJobVariableCandidates");
    }

    public abstract T getExternalInfo();

    public abstract String getId();
}
