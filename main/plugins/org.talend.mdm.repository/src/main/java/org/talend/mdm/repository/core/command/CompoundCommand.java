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
package org.talend.mdm.repository.core.command;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IMemento;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.plugin.RepositoryPlugin;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class CompoundCommand extends AbstractCommand {

    protected List<ICommand> subCmds = new ArrayList<ICommand>();

    public List<ICommand> getSubCommands() {
        return this.subCmds;
    }

    protected int commandType;

    public void setCommandType(int commandType) {
        this.commandType = commandType;
    }

    public void addCommand(ICommand cmd) {
        subCmds.add(cmd);
    }
    public void restoreState(IMemento aMemento) {
    }

    public void saveState(IMemento aMemento) {
    }

    public int getCommandType() {
        return commandType;
    }

    public IStatus execute(Object params, IProgressMonitor monitor) {
        MultiStatus ms = new MultiStatus(RepositoryPlugin.PLUGIN_ID, Status.OK, getOKStatusMsg(), null);
        for (ICommand cmd : subCmds) {
            IStatus status = cmd.execute(params, monitor);
            ms.add(status);
        }
        return ms;
    }

    protected String getOKStatusMsg() {
        return null;
    }

    @Override
    public void updateViewObject(IRepositoryViewObject viewObj) {
        for (ICommand cmd : subCmds) {
            cmd.updateViewObject(viewObj);
        }
    }

    public boolean breakUpCommand() {
        subCmds.clear();
        if (commandType == ICommand.CMD_RENAME) {
            breakUpRenameCommand();
            return true;
        } else if (commandType == ICommand.CMD_MODIFY) {
            return breakUpModifyCommand();
        }
        return false;
    }

    protected boolean breakUpModifyCommand() {
        if (objName.equals(lastName))
            return false;
        breakUpRenameCommand();
        return true;
    }

    protected void breakUpRenameCommand() {
        CommandManager manager = CommandManager.getInstance();
        // delete
        ICommand deleteCmd = manager.getNewCommand(CMD_DELETE);
        deleteCmd.init(commandId, getObjName());
        deleteCmd.updateViewObject(getViewObject());
        // modify
        ICommand modifyCmd = manager.getNewCommand(CMD_MODIFY);
        modifyCmd.init(commandId, getObjLastName());
        modifyCmd.updateViewObject(getViewObject());
        //
        subCmds.add(deleteCmd);
        subCmds.add(modifyCmd);
    }

}
