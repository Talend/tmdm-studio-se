// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.amalto.workbench.utils.JobInfo;
import com.amalto.workbench.webservices.TMDMService;
import com.amalto.workbench.webservices.WSMDMJob;
import com.amalto.workbench.webservices.WSMDMNULL;

public class AllJobInfoHolder extends ExternalInfoHolder<JobInfo[]> {

    private static Log log = LogFactory.getLog(AllJobInfoHolder.class);

    private TMDMService service;

    public AllJobInfoHolder(TMDMService service) {
        this.service = service;
    }

    @Override
    public JobInfo[] getExternalInfo() {

        List<JobInfo> results = new ArrayList<JobInfo>();

        try {
            List<WSMDMJob> jobPKs = service.getMDMJob(new WSMDMNULL()).getWsMDMJob();
            if (jobPKs == null) {
                return new JobInfo[0];
            }

            for (WSMDMJob eacJobPK : jobPKs) {
                results.add(new JobInfo(eacJobPK.getJobName(), eacJobPK.getJobVersion(), eacJobPK.getSuffix()));
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new JobInfo[0];
        }

        return results.toArray(new JobInfo[0]);
    }

    @Override
    public String getId() {
        return INFOID_ALLJOBINFOS;
    }

}
