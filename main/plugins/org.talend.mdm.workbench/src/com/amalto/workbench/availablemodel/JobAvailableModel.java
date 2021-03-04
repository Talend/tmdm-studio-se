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
package com.amalto.workbench.availablemodel;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.models.TreeParent;
import com.amalto.workbench.utils.EXtentisObjects;
import com.amalto.workbench.webservices.TMDMService;
import com.amalto.workbench.webservices.WSMDMJob;
import com.amalto.workbench.webservices.WSMDMNULL;

/**
 * DOC achen class global comment. Detailled comment
 */
public class JobAvailableModel extends AbstractAvailableModel {

    @Override
    public void addTreeObjects(TMDMService service, IProgressMonitor monitor, TreeParent serverRoot) {
        monitor.subTask(Messages.JobAvailableModel_LoadingJobs);
        // MDM Job

        TreeParent jobs = serverRoot.findServerFolder(TreeObject.JOB_REGISTRY);
        if (jobs == null) {
            jobs = new TreeParent(EXtentisObjects.JobRegistry.getDisplayName(), serverRoot, TreeObject.JOB_REGISTRY, null, null);
        }
        TreeParent deployedJob = new TreeParent(Messages.JobAvailableModel_DeployedJobs, serverRoot,
                TreeObject.BUILT_IN_CATEGORY_FOLDER, null, null);
        try {
            List<WSMDMJob> jobPKs = service.getMDMJob(new WSMDMNULL()).getWsMDMJob();
            if (jobPKs != null) {
                for (WSMDMJob jobPK : jobPKs) {
                    String name = jobPK.getJobName() + "_" + jobPK.getJobVersion() + jobPK.getSuffix();//$NON-NLS-1$
                    TreeObject obj = new TreeObject(name, serverRoot, TreeObject.JOB, name, null);
                    deployedJob.addChild(obj);
                }
            }
        } catch (Exception e) {
        }

        jobs.addChild(deployedJob);
        serverRoot.addChild(jobs);
        monitor.worked(1);
    }

}
