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

import javax.xml.ws.WebServiceException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.mdm.repository.core.command.AbstractCommand;
import org.talend.mdm.repository.core.command.ICommand;
import org.talend.mdm.repository.core.command.param.ICommandParameter;
import org.talend.mdm.repository.core.service.DeployService.DeployStatus;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.model.mdmmetadata.MDMServerDef;
import org.talend.mdm.repository.plugin.RepositoryPlugin;
import org.talend.mdm.repository.utils.UserExceptionStackFilter;

import com.amalto.workbench.utils.Util;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public abstract class AbstractDeployCommand extends AbstractCommand {

    private ICommandParameter parameter;

    protected MDMServerDef serverDef;

    public MDMServerDef getServerDef() {
        return this.serverDef;
    }

    private IStatus deployStatus;

    /**
     * Getter for deployStatus.
     *
     * @return the deployStatus
     */
    public IStatus getDeployStatus() {
        return this.deployStatus;
    }

    /**
     * Sets the deployStatus.
     *
     * @param deployStatus the deployStatus to set
     */
    public void setDeployStatus(IStatus deployStatus) {
        this.deployStatus = deployStatus;
    }

    public void setServerDef(MDMServerDef serverDef) {
        this.serverDef = serverDef;
    }

    protected ERepositoryObjectType getViewObjectType() {
        return viewObject.getRepositoryObjectType();
    }

    @Override
    public int getToRunPhase() {
        return ICommand.PHASE_DEPLOY;
    }

    protected IStatus getDetailErrorMsg(String bindMsg, String typeLabel, String objectName, Exception e) {
        Throwable cause = null;

        if (e instanceof WebServiceException) {
            cause = Util.analyseWebServiceException((WebServiceException) e);
        } else {
            cause = e.getCause();
        }

        IStatus status = null;
        if (cause == null) {
            status = buildErrorStatus(bindMsg, typeLabel, objectName, e);
        } else {
            status = DeployStatus.getErrorStatus(this, Messages.bind(bindMsg, typeLabel, objectName, e.getMessage()), e);
        }

        return status;
    }

    private IStatus buildErrorStatus(String bindMsg, String typeLabel, String objectName, Exception e) {
        String msg = e.getMessage();

        String[] exceptionMsgs = UserExceptionStackFilter.filterExceptionMsg(msg);

        IStatus status = null;
        if (exceptionMsgs.length == 0) {
            String errorMsg = msg != null && !msg.trim().isEmpty() ? msg : Messages.bind(Messages.Deploy_fail_text, typeLabel,
                    objectName);
            status = DeployStatus.getErrorStatus(this, errorMsg, null);
        } else if (exceptionMsgs.length == 1) {
            status = DeployStatus.getErrorStatus(this, Messages.bind(bindMsg, typeLabel, objectName, exceptionMsgs[0]), e);
        } else {
            status = new MultiStatus(RepositoryPlugin.PLUGIN_ID, Status.ERROR, exceptionMsgs[0], null);
            for (int i = 1; i < exceptionMsgs.length; i++) {
                DeployStatus errorStatus = DeployStatus.getErrorStatus(this, exceptionMsgs[i]);
                ((MultiStatus) status).add(errorStatus);
            }
        }

        return status;
    }

    public ICommandParameter getParameter() {
        return this.parameter;
    }

    public void setParameter(ICommandParameter parameter) {
        this.parameter = parameter;
    }

}
