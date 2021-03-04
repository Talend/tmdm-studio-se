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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.talend.mdm.repository.core.command.ICommand;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class RenameCommand extends AbstractDeployCommand {

    public RenameCommand() {
    }

    @Override
    public int getCommandType() {
        return ICommand.CMD_RENAME;
    }

    @Override
    public IStatus execute(Object receiver, IProgressMonitor monitor) {
        return null;
    }

}
