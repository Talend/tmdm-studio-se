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
import java.util.Iterator;
import java.util.List;

import org.eclipse.ui.IMemento;
import org.eclipse.ui.navigator.IMementoAware;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class CommandStack implements IMementoAware {

    CommandManager manager = CommandManager.getInstance();

    List<ICommand> commands = new ArrayList<ICommand>();

    private String cmdId;

    private ICommand validCommand;

    public ICommand getValidDeployCommand() {
        return this.validCommand;
    }

    public String getCommandId() {
        return this.cmdId;
    }

    public boolean pushCommand(ICommand cmd) {
        return pushCommand(cmd, true);
    }

    public List<ICommand> getCommands(int phase) {
        List<ICommand> cmds = new ArrayList<ICommand>();
        for (ICommand cmd : commands) {
            if (cmd.getToRunPhase() == phase) {
                cmds.add(cmd);
            }
        }
        return cmds;
    }

    public boolean pushCommand(ICommand cmd, boolean updateValidCmd) {
        if (commands.isEmpty()) {
            if (cmd.getCommandType() == ICommand.CMD_RESTORE) {
                return false;
            }
            commands.add(cmd);
            cmdId = cmd.getCommandId();
            if (updateValidCmd) {
                calValidDeployCommand();
            }
            return true;
        }
        String commandId = cmd.getCommandId();
        if (commandId != null && commandId.equals(cmdId)) {
            if (cmd.getCommandType() == ICommand.CMD_MODIFY) {
                ICommand lastCommand = getLastCommand(commands);
                if (lastCommand != null && lastCommand.getCommandType() == ICommand.CMD_MODIFY) {
                    // do nothing skip repeat command
                } else {
                    commands.add(cmd);
                }
            } else {
                commands.add(cmd);
            }

            if (updateValidCmd) {
                calValidDeployCommand();
            }
            return true;
        }

        return false;
    }

    @Override
    public void restoreState(IMemento aMemento) {
        IMemento[] children = aMemento.getChildren(ICommand.MEM_TYPE_COMMAND);
        if (children != null) {
            commands.clear();
            for (IMemento childMem : children) {
                ICommand cmd = manager.restoreCommand(childMem);
                if (cmd != null) {
                    pushCommand(cmd, false);
                }
            }
            calValidDeployCommand();
        }

    }

    @Override
    public void saveState(IMemento aMemento) {
        for (ICommand cmd : commands) {
            IMemento commandMem = aMemento.createChild(ICommand.MEM_TYPE_COMMAND);
            cmd.saveState(commandMem);
        }

    }

    private List<ICommand> shrinkDeployCommandStack() {
        List<ICommand> result = new ArrayList<ICommand>();
        int lastAddPos = -1;
        for (ICommand cmd : commands) {
            if (cmd.getToRunPhase() == ICommand.PHASE_DEPLOY) {
                int type = cmd.getCommandType();
                if (type != ICommand.CMD_RESTORE) {
                    result.add(cmd);
                    if (type == ICommand.CMD_DELETE) {
                        lastAddPos = result.size() - 1;
                    }
                } else {
                    if (lastAddPos >= 0 && lastAddPos < result.size()) {
                        result.remove(lastAddPos);
                    }
                }
            }
        }
        return result;
    }

    private void calValidDeployCommand() {
        List<ICommand> shrinked = shrinkDeployCommandStack();
        ICommand first = getFirstCommand(shrinked);
        if (first == null) {
            validCommand = manager.getDefaultNOPCommand();
            return;
        }
        ICommand last = getLastCommand(shrinked);
        int firstType = first.getCommandType();
        int lastType = last.getCommandType();

        if (firstType == ICommand.CMD_ADD) {
            // new or nop
            if (lastType == ICommand.CMD_DELETE) {
                // nop
                validCommand = manager.getDefaultNOPCommand();
            } else {
                // new
                validCommand = copyCommand(first, first, last);
            }
        } else {
            // rename
            if (first == last && firstType == ICommand.CMD_RENAME) {
                validCommand = copyCommand(first, first, last);
            } else {
                // delete
                if (lastType == ICommand.CMD_DELETE) {
                    validCommand = copyCommand(last, first, last);
                } else {
                    // modify
                    ICommand modifyCmd = manager.getNewCommand(first.getViewObject(), ICommand.CMD_MODIFY);
                    modifyCmd.setCommandId(getCommandId());
                    validCommand = copyCommand(modifyCmd, first, last);
                }
            }

        }

    }

    private ICommand copyCommand(ICommand cmd, ICommand first, ICommand last) {
        ICommand copyCommand = manager.copyCommand(cmd, new String[] { first.getObjName(), last.getObjLastName() });
        // patch for match rule mapinfo object
        if (first == last) {
            copyCommand.updateViewObject(first.getViewObject());
        }
        return copyCommand;
    }

    private ICommand getFirstCommand(List<ICommand> cmds) {
        if (!cmds.isEmpty()) {
            return cmds.get(0);
        }
        return null;
    }

    private ICommand getLastCommand(List<ICommand> cmds) {
        if (!cmds.isEmpty()) {
            return cmds.get(cmds.size() - 1);
        }
        return null;
    }

    public String getObjectFirstName() {
        ICommand firstCommand = getFirstCommand(commands);
        if (firstCommand != null) {
            return firstCommand.getObjName();
        }
        return null;
    }

    public String getObjectLastName() {
        ICommand lastCommand = getLastCommand(commands);
        if (lastCommand != null) {
            return lastCommand.getObjLastName();
        }
        return null;
    }

    public void removeCommandsByPhase(int phase) {
        for (Iterator<ICommand> il = commands.iterator(); il.hasNext();) {
            ICommand cmd = il.next();
            if (cmd.getToRunPhase() == phase) {
                il.remove();
            }
        }
    }

    public boolean isEmpty() {
        return commands.isEmpty();
    }
}
