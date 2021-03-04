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
package org.talend.mdm.repository.ui.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.talend.commons.utils.VersionUtils;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.service.DeployService;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.model.mdmmetadata.MDMServerDef;
import org.talend.mdm.repository.ui.dialogs.SelectVersionDialog;
import org.talend.mdm.repository.ui.dialogs.lock.LockedDirtyObjectDialog;
import org.talend.mdm.workbench.serverexplorer.ui.dialogs.SelectServerDefDialog;

import com.amalto.workbench.service.MissingJarService;

/**
 * DOC Administrator class global comment. Detailled comment
 */
public class DeployAnotherVersionAction extends AbstractDeployAction {

    public DeployAnotherVersionAction() {
        super(Messages.DeployAnotherVersionAction_deployAnother);
    }

    @Override
    protected void _doRun() {
        boolean checkMissingJar = MissingJarService.getInstance().checkMissingJar(true);
        if (!checkMissingJar) {
            return;
        }
        List<IRepositoryViewObject> viewObjs = getSelectedRepositoryViewObject();
        if (viewObjs.size() == 0) {
            return;
        }

        String originVersion = viewObjs.get(0).getProperty().getVersion();
        // open the version dialog
        SelectVersionDialog versionDialog = new SelectVersionDialog(getShell(), Messages.DeployAnotherVersionAction_selectAnother,
                viewObjs.get(0));
        versionDialog.create();
        if (versionDialog.open() == IDialogConstants.OK_ID) {
            if (versionDialog.getSelection() != null) {
                viewObjs = new ArrayList<IRepositoryViewObject>();
                IRepositoryViewObject modelviewObj = versionDialog.getSelection();
                viewObjs.add(modelviewObj);
                viewObjs.addAll(getAssociatedObjects(modelviewObj));
            }

            SelectServerDefDialog dialog = new SelectServerDefDialog(getShell());
            if (dialog.open() == IDialogConstants.OK_ID) {
                DeployService deployService = DeployService.getInstance();
                // save editors
                LockedDirtyObjectDialog lockDirtyDialog = new LockedDirtyObjectDialog(getShell(),
                        Messages.AbstractDeployAction_promptToSaveEditors, viewObjs);
                if (lockDirtyDialog.needShowDialog() && lockDirtyDialog.open() == IDialogConstants.CANCEL_ID) {
                    return;
                }
                lockDirtyDialog.saveDirtyObjects();
                // deploy
                MDMServerDef serverDef = dialog.getSelectedServerDef();
                if (doCheckServerConnection(serverDef)) {
                    IStatus status = deployService.deployAnotherVersion(serverDef, viewObjs);
                    if (status.isMultiStatus()) {
                        showDeployStatus(status);
                    }

                    if (isLatestVersion(viewObjs.get(0), originVersion)) {
                        updateChangedStatus(status);
                        updateLastServer(status, new NullProgressMonitor());
                    }
                }
            }
        }
    }

    private boolean isLatestVersion(IRepositoryViewObject viewObject, String originalVersion) {
        String openVersion = viewObject.getProperty().getVersion();

        return VersionUtils.compareTo(openVersion, originalVersion) >= 0;
    }

    @Override
    public boolean isVisible(IRepositoryViewObject viewObj) {
        return getSelectedObject().size() == 1;
    }

}
