// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
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

import javax.xml.ws.WebServiceException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.mdm.repository.core.service.DeployService.DeployStatus;
import org.talend.mdm.repository.core.service.IInteractiveHandler;
import org.talend.mdm.repository.core.service.InteractiveService;
import org.talend.mdm.repository.i18n.Messages;

import com.amalto.workbench.utils.XtentisException;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public abstract class DefaultDeployCommand extends AbstractDeployCommand {

    @Override
    public IStatus execute(Object params, IProgressMonitor monitor) {
        ERepositoryObjectType type = getViewObjectType();
        String objectName = getLabel();
        IInteractiveHandler handler = InteractiveService.findHandler(type);
        if (handler != null) {
            String typeLabel = handler.getLabel();
            monitor.subTask(Messages.bind(Messages.Deploy_text, typeLabel));
            try {
                boolean isOK = handler.deploy(this);
                if (getDeployStatus() != null) {
                    return getDeployStatus();
                }
                if (isOK) {
                    if (getCommandType() == CMD_MODIFY) {
                        return DeployStatus.getOKStatus(this,
                                Messages.bind(Messages.Deploy_successfully_text, typeLabel, objectName));
                    }
                    return DeployStatus
                            .getOKStatus(this, Messages.bind(Messages.Create_successfully_text, typeLabel, objectName));
                } else {
                    return DeployStatus.getErrorStatus(this, Messages.bind(Messages.Deploy_fail_text, typeLabel, objectName));
                }

            } catch (OperationCanceledException e) {
                return DeployStatus.getInfoStatus(this, Messages.bind(Messages.Deploy_cancel_text, typeLabel, objectName));
            } catch (WebServiceException e) {
                return getDetailErrorMsg(Messages.Deploy_fail_cause_text, typeLabel, objectName, e);
            } catch (XtentisException e) {
                return getDetailErrorMsg(Messages.Deploy_fail_cause_text, typeLabel, objectName, e);
            } catch (RuntimeException e) {
                return getDetailErrorMsg(Messages.Deploy_fail_cause_text, typeLabel, objectName, e);
            }
        } else {
            return DeployStatus.getErrorStatus(this, Messages.bind(Messages.Deploy_notSupport_text, objectName));
        }

    }

    /**
     * DOC hbhong Comment method "getLabel".
     * 
     * @return
     */
    protected String getLabel() {
        return getObjLastName();
    }

}
