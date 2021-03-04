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
package org.talend.mdm.repository.core.command.common;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IMemento;
import org.talend.mdm.repository.core.command.AbstractCommand;
import org.talend.mdm.repository.core.command.CommandManager;
import org.talend.mdm.repository.core.command.ICommand;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class PushCmdCommand extends AbstractCommand {

    /*
     * (non-Javadoc)
     *
     * @see org.talend.mdm.repository.core.command.AbstractCommand#getCommandType()
     */
    @Override
    public int getCommandType() {
        return CMD_PUSH_COMMAND;
    }

    private int pushCmdType;

    public void setPushCmdType(int pushCmdType) {
        this.pushCmdType = pushCmdType;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.talend.mdm.repository.core.command.AbstractCommand#execute(java.lang.Object,
     * org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public IStatus execute(Object params, IProgressMonitor monitor) {
        ICommand exeCmd = CommandManager.getInstance().getNewCommand(pushCmdType);
        if (exeCmd != null) {
            CommandManager.getInstance().pushCommand(pushCmdType, getCommandId(), getObjLastName());
            return Status.OK_STATUS;
        }
        return Status.CANCEL_STATUS;
    }

    public static final String PROP_PUSH_TYPE = "pushCmdType"; //$NON-NLS-1$

    public void restoreState(IMemento aMemento) {

        super.restoreState(aMemento);
        Integer exeObj = aMemento.getInteger(PROP_PUSH_TYPE);
        if (exeObj != null) {
            pushCmdType = exeObj.intValue();
        }
    }

    public void saveState(IMemento aMemento) {
        super.saveState(aMemento);
        aMemento.putInteger(PROP_PUSH_TYPE, pushCmdType);
    }
}
