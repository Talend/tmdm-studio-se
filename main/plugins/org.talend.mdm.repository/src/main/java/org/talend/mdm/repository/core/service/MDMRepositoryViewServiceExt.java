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
package org.talend.mdm.repository.core.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.xsd.XSDSchema;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.model.mdmserverobject.WSDataModelE;
import org.talend.mdm.repository.ui.actions.job.EditProcessAction;
import org.talend.mdm.repository.ui.navigator.MDMRepositoryView;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;

import com.amalto.workbench.detailtabs.sections.IMDMRepositoryViewServiceExt;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.utils.Util;

public class MDMRepositoryViewServiceExt implements IMDMRepositoryViewServiceExt {

    private static Logger log = Logger.getLogger(MDMRepositoryViewServiceExt.class);

    public List<String> findAllRoleNames() {
        return RepositoryQueryService.findAllRoleNames();
    }

    public List<String> findAllWorkflowNames() {
        return RepositoryQueryService.findAllWorkflowNames();
    }

    public List<String> findAllDataModelNames() {
        return RepositoryQueryService.findAllDataModelNames();
    }

    public IWorkbenchPartSite getMDMRepositoryViewSite() {
        return MDMRepositoryView.show().getSite();
    }

    public XSDSchema getDataModelXsd(TreeObject pObject, String filter, String dataModelName) {

        WSDataModelE wsDataModel = RepositoryQueryService.findDataModelByName(dataModelName);
        XSDSchema xsd = null;
        if (wsDataModel != null) {
            try {

                String schema = wsDataModel.getXsdSchema();
                xsd = Util.createXsdSchema(schema, pObject);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return xsd;
    }

    public void openJob(String jobName) {
        List<IRepositoryViewObject> viewObjects = RepositoryResourceUtil.findAllViewObjects(ERepositoryObjectType.PROCESS, true,
                true);
        for (IRepositoryViewObject viewObj : viewObjects) {
            if (viewObj.getLabel().equals(jobName)) {
                EditProcessAction action = new EditProcessAction();
                action.selectionChanged(new StructuredSelection(viewObj));
                action.run();
                return;
            }
        }
        MessageDialog.openInformation(null, Messages.MDMRepositoryViewServiceExt_openJob,
                Messages.bind(Messages.MDMRepositoryViewServiceExt_cannotFindJob, jobName));

    }

    public List<String> getDataModel(String dataModel, String concept) {
        List<String> dataModels = RepositoryQueryService.getDataModel(dataModel, concept);

        return dataModels;
    }
}
