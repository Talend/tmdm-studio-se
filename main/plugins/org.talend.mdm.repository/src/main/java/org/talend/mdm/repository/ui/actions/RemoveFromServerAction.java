// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
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

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.command.CommandManager;
import org.talend.mdm.repository.core.command.ICommand;
import org.talend.mdm.repository.core.command.deploy.AbstractDeployCommand;
import org.talend.mdm.repository.core.service.DeployService;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.model.mdmmetadata.MDMServerDef;
import org.talend.mdm.repository.ui.dialogs.lock.LockedDirtyObjectDialog;
import org.talend.mdm.workbench.serverexplorer.ui.dialogs.SelectServerDefDialog;

/**
 * DOC achen class global comment. Detailled comment
 */
public class RemoveFromServerAction extends AbstractDeployAction {


    public RemoveFromServerAction() {
        super(Messages.RemoveFromServerAction_removeFromServer);
    }

    @Override
    protected void doRun() {
        SelectServerDefDialog dialog = new SelectServerDefDialog(getShell());
        if (dialog.open() == IDialogConstants.OK_ID) {
            // save editors
            LockedDirtyObjectDialog lockDirtyDialog = new LockedDirtyObjectDialog(getShell(),
                    Messages.AbstractDeployAction_promptToSaveEditors, getSelectedRepositoryViewObject());
            if (lockDirtyDialog.needShowDialog() && lockDirtyDialog.open() == IDialogConstants.CANCEL_ID) {
                return;
            }
            DeployService deployService = DeployService.getInstance();
            try {
                deployService.aboutToDeploy();
                lockDirtyDialog.saveDirtyObjects();
                // remove
                MDMServerDef serverDef = dialog.getSelectedServerDef();
                List<AbstractDeployCommand> commands = new LinkedList<AbstractDeployCommand>();
                CommandManager commandManager = CommandManager.getInstance();
                for (Object obj : getSelectedObject()) {
                    IRepositoryViewObject viewObj = (IRepositoryViewObject) obj;
                    ICommand deleteCommand = commandManager.getNewCommand(ICommand.CMD_DELETE);
                    deleteCommand.init(viewObj);
                    commands.add((AbstractDeployCommand) deleteCommand);
                }
                //
                IStatus status = DeployService.getInstance().runCommands(commands, serverDef);
                if (status.isMultiStatus()) {
                    showDeployStatus(status);
                }
            } finally {
                deployService.postDeploying();
            }
        }
    }
}
