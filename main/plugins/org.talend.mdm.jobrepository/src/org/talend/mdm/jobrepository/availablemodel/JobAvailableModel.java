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
package org.talend.mdm.jobrepository.availablemodel;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IMenuManager;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ProcessItem;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.jobrepository.JobRepositoryUtil;
import org.talend.mdm.jobrepository.actions.DeployJobAction;
import org.talend.mdm.jobrepository.actions.OpenJobAction;
import org.talend.mdm.jobrepository.actions.RunTisJobAction;

import com.amalto.workbench.availablemodel.AbstractAvailableModel;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.models.TreeParent;
import com.amalto.workbench.utils.EXtentisObjects;
import com.amalto.workbench.webservices.XtentisPort;

/**
 * DOC achen class global comment. Detailled comment
 */
public class JobAvailableModel extends AbstractAvailableModel {

    private static final Log log = LogFactory.getLog(JobAvailableModel.class);

    @Override
    public void addTreeObjects(XtentisPort port, IProgressMonitor monitor, TreeParent serverRoot) {
        monitor.subTask("Loading Jobs");

        TreeParent jobs = serverRoot.findServerFolder(TreeObject.JOB_REGISTRY);
        if (jobs == null)
            jobs = new TreeParent(EXtentisObjects.JobRegistry.getDisplayName(), serverRoot, TreeObject.JOB_REGISTRY, null, null);

        // TIS JOB
        try {
            List<IRepositoryViewObject> listJobs = JobRepositoryUtil.getAllTISRepositoryJobs();
            if (listJobs != null && listJobs.size() > 0) {
                TreeParent tosJob = new TreeParent("Source Jobs", serverRoot, TreeObject.BUILT_IN_CATEGORY_FOLDER, null, null);
                for (IRepositoryViewObject o : listJobs) {
                    Item item = o.getProperty().getItem();
                    if (item instanceof ProcessItem) {
                        String name = o.getLabel() + "_" + o.getProperty().getVersion(); //$NON-NLS-1$
                        // add the category information for the jobs.
                        String path = ((ProcessItem) item).getState().getPath();
                        if (log.isDebugEnabled())
                            log.debug("name-->" + name + " path--->" + path); //$NON-NLS-1$ //$NON-NLS-2$
                        TreeObject obj = new TreeObject(name, serverRoot, TreeObject.TIS_JOB, o, null);
                        obj.setWsObject(new OpenJobAction());
                        if (path != null && path.length() != 0) {
                            String[] categories = path.split("/"); //$NON-NLS-1$
                            TreeParent parentFolder = null;
                            for (String folder : categories) {
                                if (parentFolder == null)
                                    parentFolder = tosJob;
                                parentFolder = addCategory(serverRoot, parentFolder, folder);
                            }
                            parentFolder.addChild(obj);
                        } else
                            tosJob.addChild(obj);
                    }
                }
                jobs.addChild(tosJob);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        serverRoot.addChild(jobs);
        monitor.worked(1);
    }

    private TreeParent addCategory(TreeParent serverRoot, TreeParent parentFolder, String folder) {
        TreeObject jobFolder = parentFolder.findObject(TreeObject.CATEGORY_FOLDER, folder);
        if (jobFolder == null)
            jobFolder = new TreeParent(folder, serverRoot, TreeObject.CATEGORY_FOLDER, null, null);
        parentFolder.addChild(jobFolder, false);
        return (TreeParent) jobFolder;
    }

    @Override
    public void fillContextMenu(TreeObject xobject, IMenuManager manager) {
        if (xobject.getType() == TreeObject.TIS_JOB) {
            manager.removeAll();
            manager.add(new DeployJobAction());
            manager.add(new OpenJobAction());
            manager.add(new RunTisJobAction());
        }
    }

}
