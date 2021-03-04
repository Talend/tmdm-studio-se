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

import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.command.CommandManager;
import org.talend.mdm.repository.core.command.ICommand;
import org.talend.mdm.repository.core.service.DeployService.DeployStatus;
import org.talend.mdm.repository.model.mdmmetadata.MDMServerDef;

import com.amalto.workbench.service.IUpdateLastServerService;

/**
 * created by talend2 on 2012-11-20 Detailled comment
 *
 */
public class UpdateLastServerService implements IUpdateLastServerService {

    public void updateLastServerDefInfo(Object serverDef, IRepositoryViewObject ViewObject) {
        if (serverDef == null || ViewObject == null || !(serverDef instanceof MDMServerDef)) {
            return;
        }

        CommandManager cmdManager = CommandManager.getInstance();
        DeployService deployService = DeployService.getInstance();
        ICommand deployCmd = cmdManager.getNewCommand(ICommand.CMD_MODIFY);
        deployCmd.init(ViewObject);
        MultiStatus mStatus = new MultiStatus("PLUGIN_ID", Status.OK, "", null); //$NON-NLS-1$ //$NON-NLS-2$

        DeployStatus deployStatus = DeployStatus.getOKStatus(deployCmd, ""); //$NON-NLS-1$
        mStatus.add(deployStatus);
        cmdManager.removeCommandStack(deployCmd, ICommand.PHASE_DEPLOY);
        deployService.pushRestoreCommand(cmdManager, deployCmd, (MDMServerDef) serverDef);
        deployService.updateLastServer(mStatus, new NullProgressMonitor());
    }

}
