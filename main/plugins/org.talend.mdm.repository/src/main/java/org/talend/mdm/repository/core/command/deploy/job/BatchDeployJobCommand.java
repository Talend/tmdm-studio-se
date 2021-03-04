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
package org.talend.mdm.repository.core.command.deploy.job;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.command.ICommand;
import org.talend.mdm.repository.core.command.deploy.AbstractDeployCommand;
import org.talend.mdm.repository.core.command.deploy.DefaultDeployCommand;
import org.talend.mdm.repository.core.service.DeployService.DeployStatus;
import org.talend.mdm.repository.core.service.IInteractiveHandler;
import org.talend.mdm.repository.core.service.InteractiveService;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.plugin.RepositoryPlugin;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class BatchDeployJobCommand extends DefaultDeployCommand {

    List<ICommand> subCmds = new ArrayList<ICommand>();

    public List<ICommand> getSubCmds() {
        return this.subCmds;
    }

    public List<ICommand> getSubDeleteCmds() {
        return this.subDeleteCmds;
    }

    List<ICommand> subDeleteCmds = new ArrayList<ICommand>();

    public void addCommand(ICommand cmd) {
        if (cmd instanceof AbstractDeployCommand && getServerDef() == null) {
            setServerDef(((AbstractDeployCommand) cmd).getServerDef());
        }
        subCmds.add(cmd);
    }

    public boolean isEmpty() {
        return subCmds.isEmpty() && subDeleteCmds.isEmpty();
    }

    public void addDeleteCommand(ICommand cmd) {
        if (cmd instanceof AbstractDeployCommand && getServerDef() == null) {
            setServerDef(((AbstractDeployCommand) cmd).getServerDef());
        }
        subDeleteCmds.add(cmd);
    }

    @Override
    protected ERepositoryObjectType getViewObjectType() {
        return ERepositoryObjectType.PROCESS;
    }

    public List<IRepositoryViewObject> getViewObjects() {
        List<IRepositoryViewObject> viewObjs = new LinkedList<IRepositoryViewObject>();
        for (ICommand cmd : subCmds) {
            if (cmd instanceof AbstractDeployCommand) {
                viewObjs.add(((AbstractDeployCommand) cmd).getViewObject());
            }
        }
        return viewObjs;
    }

    @Override
    public IStatus execute(Object params, IProgressMonitor monitor) {
        IStatus status = super.execute(params, monitor);
        //
        MultiStatus ms = new MultiStatus(RepositoryPlugin.PLUGIN_ID, status.getSeverity(),
                Messages.BatchDeployJobCommand_deployJob, null);
        IInteractiveHandler handler = InteractiveService.findHandler(getViewObjectType());
        String typeLabel = handler.getLabel();
        if (status.isOK()) {
            runDeleteCmds(params, monitor, ms);
        }

        collectDeployStatus(status, ms, typeLabel);
        return ms;

    }

    private void collectDeployStatus(IStatus dialogStatus, MultiStatus ms, String typeLabel) {
        for (ICommand cmd : subCmds) {
            String objectName = cmd.getObjLastName();
            int severity = dialogStatus.getSeverity();
            if (severity == IStatus.OK) {
                if (cmd.getCommandType() == CMD_MODIFY)
                    ms.add(DeployStatus.getOKStatus(this,
                            Messages.bind(Messages.BatchDeployJobCommand_successfullyUpdate, typeLabel, objectName)));
                else
                    ms.add(DeployStatus.getOKStatus(this,
                            Messages.bind(Messages.BatchDeployJobCommand_createSuccessfully, typeLabel, objectName)));
            } else if (severity == IStatus.INFO) {
                ms.add(DeployStatus.getInfoStatus(this,
                        Messages.bind(Messages.JobInteractiveHandler_skipToDeploy, getLabel(), objectName)));
            } else if (severity == IStatus.ERROR) {
                Throwable ex = dialogStatus.getException();
                String msg = null != ex ? ex.getMessage() : dialogStatus.getMessage();
                ms.add(DeployStatus.getErrorStatus(this, Messages.bind(Messages.Deploy_fail_text, objectName, msg)));
            }
        }
    }

    private void runDeleteCmds(Object params, IProgressMonitor monitor, MultiStatus ms) {
        for (ICommand cmd : subDeleteCmds) {
            if (cmd instanceof AbstractDeployCommand) {
                AbstractDeployCommand removeCmd = (AbstractDeployCommand) cmd;
                IStatus status = removeCmd.execute(params, monitor);
                ms.add(status);
            }
        }
    }

    @Override
    protected String getLabel() {
        return Messages.BatchDeployJobCommand_title;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.talend.mdm.repository.core.command.AbstractCommand#getCommandType()
     */
    @Override
    public int getCommandType() {
        return CMD_MODIFY;
    }

}
