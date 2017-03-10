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
package org.talend.mdm.repository.ui.actions;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.command.ICommand;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.model.mdmmetadata.MDMServerDef;
import org.talend.mdm.repository.ui.dialogs.lock.LockedDirtyObjectDialog;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;
import org.talend.mdm.workbench.serverexplorer.ui.dialogs.SelectServerDefDialog;

import com.amalto.workbench.exadapter.ExAdapterManager;
import com.amalto.workbench.service.MissingJarService;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class DeployToAction extends AbstractDeployAction {

    IDeployActionExAdapter<DeployToAction> exAdapter;

    public DeployToAction() {
        super(Messages.DeployToAction_deployTo);
        initExAdapter();
    }

    public DeployToAction(String label) {
        super(label);
        initExAdapter();
    }

    private void initExAdapter() {
        exAdapter = ExAdapterManager.getAdapter(this, IDeployActionExAdapter.class);
    }

    @Override
    protected void doRun() {
        boolean checkMissingJar = MissingJarService.getInstance().checkMissingJar(true);
        if (!checkMissingJar) {
            return;
        }

        List<IRepositoryViewObject> viewObjs = getSelectedRepositoryViewObject();

        viewObjs = doCheckDependency(viewObjs);
        if (viewObjs == null) {
            return;
        }

        SelectServerDefDialog dialog = getSelectServerDefDialog(viewObjs);

        if (dialog.open() == IDialogConstants.OK_ID) {
            // save editors
            if (!doBeforeDeploy(viewObjs)) {
                return;
            }
            // deploy
            MDMServerDef serverDef = dialog.getSelectedServerDef();

            IStatus status = deploy(serverDef, viewObjs, ICommand.CMD_MODIFY);
            if (status.getSeverity() != IStatus.CANCEL) {
                updateChangedStatus(status);
                if (status.isMultiStatus()) {
                    showDeployStatus(status);
                }
                doPostDeploy(status);
            }
        }

    }

    protected List<IRepositoryViewObject> doCheckDependency(List<IRepositoryViewObject> viewObjs) {
        if (exAdapter != null) {
            viewObjs = exAdapter.showDependencyConfigDialog(viewObjs);
            if (viewObjs == null) {
                return null;
            }
            // TO add match rule object
            viewObjs = getSelectedRepositoryViewObject(viewObjs);
            filterMatchRuleObjs(viewObjs);
            return viewObjs;
        }
        return viewObjs;
    }

    protected void doPostDeploy(IStatus status) {
        updateLastServer(status, new NullProgressMonitor());
    }

    protected boolean doBeforeDeploy(List<IRepositoryViewObject> viewObjs) {
        LockedDirtyObjectDialog lockDirtyDialog = new LockedDirtyObjectDialog(getShell(),
                Messages.AbstractDeployAction_promptToSaveEditors, viewObjs);
        if (lockDirtyDialog.needShowDialog() && lockDirtyDialog.open() == IDialogConstants.CANCEL_ID) {
            return false;
        }
        lockDirtyDialog.saveDirtyObjects();
        return true;
    }

    private SelectServerDefDialog getSelectServerDefDialog(List<IRepositoryViewObject> viewObjs) {
        SelectServerDefDialog dialog = new SelectServerDefDialog(getShell());

        initializeSelection(viewObjs, dialog);
        return dialog;
    }

    /**
     * set the default selection in SelectServerDefDialog
     * 
     * @param viewObjs current selected view objects
     * @param dialog
     */
    private void initializeSelection(List<IRepositoryViewObject> viewObjs, SelectServerDefDialog dialog) {
        MDMServerDef defServer = null;
        for (IRepositoryViewObject viewObject : viewObjs) {
            MDMServerDef tmpServer = RepositoryResourceUtil.getLastServerDef(viewObject);
            if (defServer == null) {
                defServer = tmpServer;
            } else if (tmpServer != null && !RepositoryResourceUtil.isSameMDMServerDef(defServer, tmpServer)) {
                defServer = null;
                break;
            }
        }

        dialog.create();
        dialog.setSelectServer(defServer);
    }
}
