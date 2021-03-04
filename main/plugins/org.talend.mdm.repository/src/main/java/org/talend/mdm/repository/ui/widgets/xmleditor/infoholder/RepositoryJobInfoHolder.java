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

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.model.mdmmetadata.MDMServerDef;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;

import com.amalto.workbench.utils.JobInfo;

public class RepositoryJobInfoHolder extends RepositoryExternalInfoHolder<JobInfo[]> {

    static Logger log = Logger.getLogger(RepositoryJobInfoHolder.class);

    public RepositoryJobInfoHolder() {
    }

    @Override
    public JobInfo[] getExternalInfo() {

        List<JobInfo> results = new LinkedList<JobInfo>();
        List<IRepositoryViewObject> viewObjs = RepositoryResourceUtil.findAllViewObjects(ERepositoryObjectType.PROCESS);

        for (IRepositoryViewObject viewObj : viewObjs) {
            Property prop = viewObj.getProperty();
            String path = null;
            if (prop != null) {
                Item item = prop.getItem();
                if (item != null) {
                    path = item.getState().getPath();
                }
            }
            MDMServerDef serverDef = RepositoryResourceUtil.getLastServerDef(viewObj);
            String lastServerName = (serverDef != null) ? serverDef.getName() : null;
            // the suffix should be "war" or "zip", now use "" to replace
            JobInfo jobInfo = new JobInfo(prop.getLabel(), prop.getVersion(), "", path, lastServerName); //$NON-NLS-1$
            results.add(jobInfo);
        }

        return results.toArray(new JobInfo[0]);

    }

    @Override
    public String getId() {
        return INFOID_ALLJOBINFOS;
    }

}
