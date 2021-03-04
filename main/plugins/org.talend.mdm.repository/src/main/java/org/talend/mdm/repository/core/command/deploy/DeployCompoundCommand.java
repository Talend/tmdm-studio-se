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
package org.talend.mdm.repository.core.command.deploy;

import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.command.CommandManager;
import org.talend.mdm.repository.core.command.CompoundCommand;
import org.talend.mdm.repository.core.command.ICommand;
import org.talend.mdm.repository.core.service.IInteractiveHandler;
import org.talend.mdm.repository.core.service.InteractiveService;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.model.mdmmetadata.MDMServerDef;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class DeployCompoundCommand extends CompoundCommand {

    private AbstractDeployCommand deployCommand;

    private MDMServerDef serverDef;

    public MDMServerDef getServerDef() {
        return this.serverDef;
    }

    public DeployCompoundCommand(AbstractDeployCommand deployCommand) {
        this.deployCommand = deployCommand;
    }

    @Override
    public IRepositoryViewObject getViewObject() {
        return deployCommand.getViewObject();
    }

    protected boolean breakUpModifyCommand() {
        if (objName.equals(lastName))
            return false;
        breakUpRenameCommand();
        return true;
    }

    public int getToRunPhase() {
        return ICommand.PHASE_DEPLOY;
    }
    @Override
    protected String getOKStatusMsg() {
        ERepositoryObjectType type = getViewObjectType();
        String objectName = deployCommand.getObjLastName();
        IInteractiveHandler handler = InteractiveService.findHandler(type);
        if (handler != null) {
            String typeLabel = handler.getLabel();
            return Messages.bind(Messages._OkStatusMsg, typeLabel, objectName);
        }
        return null;
    }

    protected ERepositoryObjectType getViewObjectType() {
        return deployCommand.getViewObjectType();
    }

    @Override
    public void init() {
        commandId = deployCommand.getCommandId();
        lastName = deployCommand.getObjLastName();
        objName = deployCommand.getObjName();
        this.serverDef = deployCommand.getServerDef();
    }

    protected void breakUpRenameCommand() {
        CommandManager manager = CommandManager.getInstance();
        // delete
        AbstractDeployCommand deleteCmd = (AbstractDeployCommand) manager.getNewCommand(CMD_DELETE);
        deleteCmd.init(commandId, getObjName());
        deleteCmd.updateViewObject(getViewObject());
        deleteCmd.setServerDef(serverDef);
        // modify
        AbstractDeployCommand modifyCmd = (AbstractDeployCommand) manager.getNewCommand(CMD_MODIFY);
        modifyCmd.init(commandId, getObjLastName());
        modifyCmd.updateViewObject(getViewObject());
        modifyCmd.setServerDef(serverDef);
        //
        subCmds.add(deleteCmd);
        subCmds.add(modifyCmd);
    }

}
