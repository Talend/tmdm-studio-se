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
package org.talend.mdm.repository.ui.widgets.xmleditor.infoholder;

import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.models.infoextractor.IAllDataModelHolder;
import com.amalto.workbench.utils.JobInfo;
import com.amalto.workbench.webservices.TMDMService;
import com.amalto.workbench.webservices.WSMDMConfig;
import com.amalto.workbench.webservices.WSTransformerV2;
import com.amalto.workbench.widgets.xmleditor.infoholder.ExternalInfoHolder;
import com.amalto.workbench.widgets.xmleditor.infoholder.ProcessAllCallJobVariableCandidatesHolder;
import com.amalto.workbench.widgets.xmleditor.util.WorkflowInfo;

/**
 * DOC hbhong class global comment. Detailled comment
 *
 * @param <T>
 */
public abstract class RepositoryExternalInfoHolder<T> extends ExternalInfoHolder<T> {

    public static ExternalInfoHolder<JobInfo[]> getAllJobInfosHolder(TMDMService service) {
        return (ExternalInfoHolder<JobInfo[]>) getEnternalInfoHolder("job");
    }

    public static ExternalInfoHolder<WSMDMConfig[]> getAllMDMServerInfoHolder2(TMDMService service) {
        return new RepositoryMDMServerInfoHolder(service);
    }

    public static ExternalInfoHolder<IAllDataModelHolder> getAllDataModelInfoHolderProxy(TreeObject treeNode) {
        return (ExternalInfoHolder<IAllDataModelHolder>) getEnternalInfoHolder("datamodel");
    }

    public static ExternalInfoHolder<WorkflowInfo[]> getAllWorkflowInfoHolder(TMDMService service) {
        return (ExternalInfoHolder<WorkflowInfo[]>) getEnternalInfoHolder("workflow");
    }

    public static ExternalInfoHolder<String[]> getProcessAllCallJobVarsCandidatesHolder(WSTransformerV2 service) {
        return new ProcessAllCallJobVariableCandidatesHolder(service);
    }

    public static ExternalInfoHolder<String[]> getTriggerAllCallJobVarsCandidatesHolder() {
        return (ExternalInfoHolder<String[]>) getEnternalInfoHolder("callJobVariableCandidates");
    }

    public static ExternalInfoHolder<String[]> getAllProcessesNamesHolder(TMDMService service) {
        return new RepositoryProcessesNamesHolder(service);
    }
}
