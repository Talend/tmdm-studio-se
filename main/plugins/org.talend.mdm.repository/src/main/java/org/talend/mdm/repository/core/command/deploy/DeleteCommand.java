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
import org.talend.mdm.repository.core.service.DeployService.DeployStatus;
import org.talend.mdm.repository.core.service.IInteractiveHandler;
import org.talend.mdm.repository.core.service.InteractiveService;
import org.talend.mdm.repository.i18n.Messages;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class DeleteCommand extends AbstractDeployCommand {

    public DeleteCommand() {
    }

    @Override
    public int getCommandType() {
        return ICommand.CMD_DELETE;
    }

    @Override
    public IStatus execute(Object receiver, IProgressMonitor monitor) {
        IInteractiveHandler handler = InteractiveService.findHandler(getViewObjectType());
        String objectName = getObjLastName();
        if (handler != null) {
            String typeLabel = handler.getLabel();
            monitor.subTask(Messages.bind(Messages._SubTaskTitle, typeLabel));
            try {
                handler.remove(this);
                return DeployStatus.getOKStatus(this, Messages.bind(Messages._OkDeleteStatusMsg, typeLabel, objectName));

            } catch (Exception e) {
                return getDetailErrorMsg(Messages._ErrorStatusMsg1, typeLabel, objectName, e);
            }
        } else {
            return DeployStatus.getErrorStatus(null, Messages.bind(Messages._ErrorStatusMsg2, objectName));
        }
    }
}
