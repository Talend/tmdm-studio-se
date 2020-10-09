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
package org.talend.mdm.repository.core.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.navigator.IMementoAware;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.mdm.repository.core.command.common.PushCmdCommand;
import org.talend.mdm.repository.core.command.common.UpdateLastServerCommand;
import org.talend.mdm.repository.core.command.deploy.AbstractDeployCommand;
import org.talend.mdm.repository.core.command.deploy.AddCommand;
import org.talend.mdm.repository.core.command.deploy.DeleteCommand;
import org.talend.mdm.repository.core.command.deploy.DeployCompoundCommand;
import org.talend.mdm.repository.core.command.deploy.ModifyCommand;
import org.talend.mdm.repository.core.command.deploy.RenameCommand;
import org.talend.mdm.repository.core.command.deploy.job.BatchDeployJobCommand;
import org.talend.mdm.repository.core.command.impl.NOPCommand;
import org.talend.mdm.repository.core.command.impl.RestoreCommand;
import org.talend.mdm.repository.core.command.param.ICommandParameter;
import org.talend.mdm.repository.model.mdmmetadata.MDMServerDef;
import org.talend.repository.model.IProxyRepositoryFactory;

import com.amalto.workbench.exadapter.ExAdapterManager;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class CommandManager implements IMementoAware {

    /**
     * 
     */

    private static CommandManager instance = new CommandManager();

    static Logger log = Logger.getLogger(CommandManager.class);

    public static CommandManager getInstance() {
        return instance;
    }

    IProxyRepositoryFactory factory = CoreRuntimePlugin.getInstance().getProxyRepositoryFactory();

    protected Map<String, CommandStack> map = new HashMap<String, CommandStack>();

    ICommandManagerExAdapter exAdapter;

    public CommandManager() {
        exAdapter = ExAdapterManager.getAdapter(this, ICommandManagerExAdapter.class);
    }

    public CommandStack findCommandStack(String id) {
        if (id == null) {
            return null;
        }
        return map.get(id);
    }

    public ICommand getNewCommand(IRepositoryViewObject viewObj, int type) {
        ICommand newCommand = null;
        if (exAdapter != null) {
            newCommand = exAdapter.getNewCommand(viewObj, type);
        }
        if (newCommand == null) {
            newCommand = getNewCommand(type);
        }
        return newCommand;
    }

    public ICommand getNewCommand(int type) {
        switch (type) {
        case ICommand.CMD_NOP:
            return new NOPCommand();
        case ICommand.CMD_ADD:
            return new AddCommand();
        case ICommand.CMD_DELETE:
            return new DeleteCommand();

        case ICommand.CMD_MODIFY:
            return new ModifyCommand();
        case ICommand.CMD_RENAME:
            return new RenameCommand();
        case ICommand.CMD_RESTORE:
            return new RestoreCommand();
        case ICommand.CMD_UPDATE_SERVER:
            return new UpdateLastServerCommand();
        case ICommand.CMD_PUSH_COMMAND:
            return new PushCmdCommand();
        }
        return null;
    }

    public ICommand copyCommand(ICommand cmd, Object param) {
        ICommand newCmd = null;
        if (exAdapter != null) {
            newCmd = exAdapter.copyCommand(cmd, param);
        }
        if (newCmd == null) {
            newCmd = getNewCommand(cmd.getCommandType());
        }
        if (param == null) {
            param = new String[] { cmd.getObjName(), cmd.getObjLastName() };
        }
        newCmd.init(cmd.getCommandId(), param);
        return newCmd;
    }

    public ICommand convertToDeployCompoundCommand(AbstractDeployCommand cmd) {
        CompoundCommand compoundCommand = new DeployCompoundCommand(cmd);
        compoundCommand.setCommandType(cmd.getCommandType());
        compoundCommand.init();
        if (compoundCommand.breakUpCommand()) {
            return compoundCommand;
        }
        return cmd;
    }

    public void fillViewObjectToCommand(ICommand cmd) {
        try {
            IRepositoryViewObject viewObject = cmd.getViewObject();
            if (viewObject == null) {
                String version = cmd.getVersion();
                if (version == null) {
                    viewObject = factory.getLastVersion(cmd.getCommandId());
                } else {
                    viewObject = factory.getSpecificVersion(cmd.getCommandId(), version, true);
                }
            }
            cmd.updateViewObject(viewObject);
        } catch (PersistenceException e) {
            log.error(e.getMessage(), e);
        }
    }

    ICommand nopCommand;

    public ICommand getDefaultNOPCommand() {
        if (nopCommand == null) {
            nopCommand = getNewCommand(ICommand.CMD_NOP);
        }
        return nopCommand;
    }

    public void pushCommand(int type, IRepositoryViewObject viewObj) {
        ICommand newCommand = getNewCommand(type);
        newCommand.init(viewObj);
        pushCommand(newCommand);
    }

    public void pushCommand(int type, String id, Object name) {
        ICommand newCommand = getNewCommand(type);
        newCommand.init(id, name);
        pushCommand(newCommand);
    }

    public void pushCommand(ICommand command) {
        CommandStack commandStack = map.get(command.getCommandId());
        if (commandStack == null) {
            commandStack = new CommandStack();
            if (commandStack.pushCommand(command)) {
                if (command.getCommandId() != null) {
                    map.put(command.getCommandId(), commandStack);
                }
            }
        } else {
            commandStack.pushCommand(command);
        }
    }

    public ICommand restoreCommand(IMemento mem) {
        int type = mem.getInteger(ICommand.PROP_TYPE);
        ICommand cmd = null;
        cmd = getNewCommand(type);
        if (cmd != null) {
            cmd.restoreState(mem);
        }
        return cmd;
    }

    public void removeCommandStack(String id) {
        if (id == null) {
            return;
        }
        map.remove(id);
    }

    public void removeCommandStack(String id, int phase) {
        if (id == null) {
            return;
        }
        CommandStack stack = map.get(id);
        if (stack != null) {
            stack.removeCommandsByPhase(phase);
            if (stack.isEmpty()) {
                map.remove(id);
            }
        }
    }

    private void removeCommandStack(List<ICommand> cmds, int phase) {
        if (cmds == null || cmds.isEmpty()) {
            return;
        }
        for (ICommand cmd : cmds) {
            removeCommandStack(cmd.getCommandId(), phase);
        }
    }

    public void removeCommandStack(ICommand cmd, int phase) {
        if (cmd == null) {
            return;
        }
        if (cmd instanceof BatchDeployJobCommand) {
            BatchDeployJobCommand jobCommand = (BatchDeployJobCommand) cmd;
            removeCommandStack(jobCommand.getSubCmds(), phase);
            removeCommandStack(jobCommand.getSubDeleteCmds(), phase);
        } else {
            removeCommandStack(cmd.getCommandId(), phase);
        }
    }

    public void removeAllCommandStack() {
        map.clear();
    }

    @Override
    public void restoreState(IMemento aMemento) {
        if (map.isEmpty() && aMemento != null) {
            IMemento cmdManagerMem = aMemento.getChild(ICommand.MDM_COMMANDS);
            if (cmdManagerMem != null) {
                IMemento[] stackMems = cmdManagerMem.getChildren(ICommand.MEM_TYPE_COMMAND_STACK);
                if (stackMems != null) {
                    for (IMemento stackMem : stackMems) {
                        CommandStack stack = new CommandStack();
                        stack.restoreState(stackMem);
                        if (stack.getCommandId() != null) {
                            map.put(stack.getCommandId(), stack);
                        }
                    }
                }

            }
        }
    }

    @Override
    public void saveState(IMemento aMemento) {
        IMemento cmdManagerMem = aMemento.createChild(ICommand.MDM_COMMANDS);
        for (CommandStack stack : map.values()) {
            try {
                if (factory.getLastVersion(stack.getCommandId()) != null) {
                    IMemento stackMem = cmdManagerMem.createChild(ICommand.MEM_TYPE_COMMAND_STACK);
                    stack.saveState(stackMem);
                }
            } catch (PersistenceException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    public List<AbstractDeployCommand> getAllDeployCommands() {
        List<AbstractDeployCommand> cmds = new ArrayList<AbstractDeployCommand>();
        for (CommandStack stack : map.values()) {
            ICommand validCommand = stack.getValidDeployCommand();
            if (validCommand != null) {
                if (validCommand instanceof AbstractDeployCommand) {
                    fillViewObjectToCommand(validCommand);
                    AbstractDeployCommand deployCommand = (AbstractDeployCommand) validCommand;
                    cmds.add(deployCommand);
                }
            }
        }
        return cmds;
    }

    public Map<String, List<ICommand>> getAllCommandsByPhase(int phase) {
        Map<String, List<ICommand>> cmdMap = new HashMap<String, List<ICommand>>();
        for (String id : map.keySet()) {
            CommandStack commandStack = map.get(id);
            List<ICommand> commands = commandStack.getCommands(phase);
            if (!commands.isEmpty()) {
                cmdMap.put(id, commands);
            }
        }
        return cmdMap;
    }

    public List<AbstractDeployCommand> getDeployCommands(List<IRepositoryViewObject> viewObjs, int defaultCmdType) {
        return getDeployCommands(viewObjs, defaultCmdType, null);
    }

    /**
     * DOC hbhong Comment method "getDeployCommands".
     * 
     * @param viewObjs
     * @param defaultCmdType if none then assign -1
     * @return
     */
    public List<AbstractDeployCommand> getDeployCommands(List<IRepositoryViewObject> viewObjs, int defaultCmdType,
            Map<IRepositoryViewObject, ICommandParameter> paramMap) {
        List<AbstractDeployCommand> cmds = new LinkedList<AbstractDeployCommand>();
        for (IRepositoryViewObject viewObj : viewObjs) {
            CommandStack stack = findCommandStack(viewObj.getId());
            if (stack == null) {
                stack = new CommandStack();
                ICommand cmd = getNewCommand(viewObj, defaultCmdType);
                cmd.init(viewObj);
                stack.pushCommand(cmd);
            }
            ICommand validCommand = stack.getValidDeployCommand();
            if (validCommand != null) {
                if (validCommand instanceof AbstractDeployCommand) {
                    fillViewObjectToCommand(validCommand);
                    AbstractDeployCommand deployCommand = (AbstractDeployCommand) validCommand;
                    if (paramMap != null) {
                        ICommandParameter param = paramMap.get(viewObj);
                        deployCommand.setParameter(param);
                    }
                    //initialize deploy status
                    deployCommand.setDeployStatus(null);
                    cmds.add(deployCommand);
                } else if (validCommand instanceof NOPCommand && defaultCmdType > 0) {
                    ICommand cmd = getNewCommand(defaultCmdType);
                    if (cmd instanceof AbstractDeployCommand) {
                        cmd.init(viewObj);
                        cmds.add((AbstractDeployCommand) cmd);
                    }
                }
            }
        }
        return cmds;
    }

    public void attachParameterToCommand(List<AbstractDeployCommand> commands,
            Map<IRepositoryViewObject, ICommandParameter> paramMap) {
        for (AbstractDeployCommand cmd : commands) {
            IRepositoryViewObject viewObject = cmd.getViewObject();
            if (viewObject != null) {
                ICommandParameter param = paramMap.get(viewObject);
                cmd.setParameter(param);
            }
        }
    }

    public List<AbstractDeployCommand> getDeployCommandsWithoutHistory(List<IRepositoryViewObject> viewObjs) {
        List<AbstractDeployCommand> cmds = new LinkedList<AbstractDeployCommand>();
        for (IRepositoryViewObject viewObj : viewObjs) {
            ICommand cmd = getNewCommand(viewObj, ICommand.CMD_MODIFY);
            cmd.setVersion(viewObj.getVersion());
            cmd.init(viewObj);
            if (cmd instanceof AbstractDeployCommand) {
                cmds.add((AbstractDeployCommand) cmd);
            }
        }
        return cmds;
    }

    public List<ICommand> convertToDeployCompundCommands(List<AbstractDeployCommand> validCommands, MDMServerDef serverDef) {
        List<ICommand> cmds = new ArrayList<ICommand>(validCommands.size());
        for (AbstractDeployCommand deployCommand : validCommands) {
            deployCommand.setServerDef(serverDef);
            ICommand newCmd = convertToDeployCompoundCommand(deployCommand);

            cmds.add(newCmd);
        }
        return cmds;
    }

    public void arrangeForJobCommands(List<ICommand> cmds) {
        BatchDeployJobCommand jobCommand = new BatchDeployJobCommand();

        for (Iterator<ICommand> il = cmds.iterator(); il.hasNext();) {
            ICommand cmd = il.next();
            IRepositoryViewObject viewObject = cmd.getViewObject();
            if (viewObject.getRepositoryObjectType() == ERepositoryObjectType.PROCESS) {
                if (cmd instanceof CompoundCommand) {
                    for (ICommand subCmd : ((CompoundCommand) cmd).getSubCommands()) {
                        int type = subCmd.getCommandType();
                        if (type == ICommand.CMD_DELETE) {
                            jobCommand.addDeleteCommand(subCmd);
                        } else if (type == ICommand.CMD_MODIFY) {
                            jobCommand.addCommand(subCmd);
                        }
                    }
                } else {
                    int type = cmd.getCommandType();
                    if (type == ICommand.CMD_DELETE) {
                        jobCommand.addDeleteCommand(cmd);
                    } else {
                        jobCommand.addCommand(cmd);
                    }
                }
                il.remove();
            }
        }
        if (!jobCommand.isEmpty()) {
            cmds.add(jobCommand);
        }
    }
}
