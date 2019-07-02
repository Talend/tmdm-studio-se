// ============================================================================
//
// Copyright (C) 2006-2019 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.repository.core.service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.ws.WebServiceException;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.IServerObjectRepositoryType;
import org.talend.mdm.repository.core.command.CommandManager;
import org.talend.mdm.repository.core.command.CompoundCommand;
import org.talend.mdm.repository.core.command.ICommand;
import org.talend.mdm.repository.core.command.common.PushCmdCommand;
import org.talend.mdm.repository.core.command.common.UpdateLastServerCommand;
import org.talend.mdm.repository.core.command.deploy.AbstractDeployCommand;
import org.talend.mdm.repository.core.command.deploy.DeployCompoundCommand;
import org.talend.mdm.repository.core.command.deploy.job.BatchDeployJobCommand;
import org.talend.mdm.repository.core.command.param.ICommandParameter;
import org.talend.mdm.repository.core.service.ConsistencyService.ConsistencyCheckResult;
import org.talend.mdm.repository.core.service.IModelValidationService.IModelValidateResult;
import org.talend.mdm.repository.core.service.ModelImpactAnalyseService.ImpactOperation;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.model.mdmmetadata.MDMServerDef;
import org.talend.mdm.repository.plugin.RepositoryPlugin;
import org.talend.mdm.repository.ui.dialogs.deploy.DeployStatusDialog;
import org.talend.mdm.repository.ui.dialogs.message.MultiStatusDialog;
import org.talend.mdm.repository.ui.preferences.PreferenceConstants;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;
import org.talend.mdm.repository.utils.UIUtil;

import com.amalto.workbench.utils.XtentisException;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class DeployService {

    private static Logger log = Logger.getLogger(DeployService.class);

    public static class DeployCategoryStatus extends MultiStatus {

        public DeployCategoryStatus(String pluginId, int code, String message, Throwable exception) {
            super(pluginId, code, message, exception);
        }

    }

    public static class DeployStatus extends Status {

        ICommand command;

        public ICommand getCommand() {
            return this.command;
        }

        /**
         * DOC hbhong DeployStatus constructor comment.
         *
         * @param severity
         * @param pluginId
         * @param message
         * @param exception
         */
        DeployStatus(int severity, String message, Throwable exception, ICommand command) {
            super(severity, RepositoryPlugin.PLUGIN_ID, message, exception);
            this.command = command;
        }

        public static DeployStatus getOKStatus(ICommand command, String msg) {
            return new DeployStatus(Status.OK, msg, null, command);
        }

        public static DeployStatus getInfoStatus(ICommand command, String msg) {
            return new DeployStatus(Status.INFO, msg, null, command);
        }

        public static DeployStatus getErrorStatus(ICommand command, Throwable exception) {
            return new DeployStatus(Status.ERROR, exception.getMessage(), exception, command);
        }

        public static DeployStatus getErrorStatus(ICommand command, String errMsg) {
            return new DeployStatus(Status.ERROR, errMsg, null, command);
        }

        public static DeployStatus getErrorStatus(ICommand command, String errMsg, Throwable exception) {
            return new DeployStatus(Status.ERROR, errMsg, exception, command);
        }

    }

    private static DeployService instance = new DeployService();

    private DeployService() {
    }

    public static DeployService getInstance() {
        return instance;
    }

    class DeployProcess implements IRunnableWithProgress {

        private List<ICommand> commands;

        MultiStatus mStatus = new MultiStatus(RepositoryPlugin.PLUGIN_ID, Status.OK, "", null); //$NON-NLS-1$

        public MultiStatus getStatus() {
            return this.mStatus;
        }

        public DeployProcess(List<ICommand> commands) {
            this.commands = commands;
        }

        @Override
        public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
            if (commands != null) {
                monitor.beginTask(Messages.DeployService_processTitle, commands.size());
                for (ICommand cmd : commands) {
                    if (cmd.getToRunPhase() == ICommand.PHASE_DEPLOY) {
                        if (cmd.canExecute(commands)) {
                            IStatus status = cmd.execute(null, monitor);
                            if (status != null) {
                                if (status.isMultiStatus()) {
                                    mStatus.addAll(status);
                                } else {
                                    mStatus.add(status);
                                }
                            }
                        }
                    }
                    monitor.worked(1);
                }
                monitor.done();
            }
        }

    }

    public IStatus deploy(MDMServerDef serverDef, List<IRepositoryViewObject> viewObjs, int defaultCmdType, boolean removeLocked) {
        if (removeLocked) {
            removeLockedViewObj(viewObjs);
        }
        IModelValidateResult validateResult = validateModel(viewObjs);
        int selectedButton = validateResult.getSelectedButton();
        if (selectedButton == IModelValidationService.BUTTON_CANCEL) {
            return Status.CANCEL_STATUS;
        }
        List<IRepositoryViewObject> validObjects = validateResult.getValidObjects(selectedButton);
        List<IRepositoryViewObject> invalidObjects = validateResult.getInvalidObjects(selectedButton);
        try {
            // consistency check
            ConsistencyCheckResult consistencyCheckResult = ConsistencyService.getInstance().checkConsistency(serverDef,
                    validObjects);
            if (consistencyCheckResult == null || consistencyCheckResult.isCanceled()) {
                return Status.CANCEL_STATUS;
            }
            validObjects = consistencyCheckResult.getToDeployObjects();

            //
            CommandManager manager = CommandManager.getInstance();
            List<AbstractDeployCommand> commands = manager.getDeployCommands(validObjects, defaultCmdType);

            // insert impact dialog
            List<AbstractDeployCommand> canceledCommandAfterImpactAnalysis = new LinkedList<AbstractDeployCommand>(commands);
            if (UIUtil.isWorkInUI()) {
                try {
                    Map<IRepositoryViewObject, ImpactOperation> analyzeModelImpact = ModelImpactAnalyseService
                            .analyzeCommandImpact(serverDef, commands);
                    Map<IRepositoryViewObject, ICommandParameter> paramMap = null;
                    if (analyzeModelImpact != null) {
                        ModelImpactAnalyseService.shrinkDeployCommands(analyzeModelImpact, commands);
                        paramMap = ModelImpactAnalyseService.convertToParameters(analyzeModelImpact);
                        manager.attachParameterToCommand(commands, paramMap);
                    }

                    canceledCommandAfterImpactAnalysis.removeAll(commands);
                } catch (InterruptedException ex) {
                    return Status.CANCEL_STATUS;
                }
            }
            IStatus mainStatus = runCommands(commands, serverDef);
            // update consistency value
            try {
                updateServerConsistencyStatus(serverDef, mainStatus);
            } catch (XtentisException e) {
                log.error(e.getMessage(), e);
            } catch (WebServiceException e) {
                log.error(e.getMessage(), e);
            }
            //
            generateValidationFailedDeployStatus(mainStatus, invalidObjects);
            if (UIUtil.isWorkInUI()) {
                generateConsistencyCancelDeployStatus(mainStatus,
                        consistencyCheckResult.getToSkipObjects().toArray(new IRepositoryViewObject[0]));

                for (AbstractDeployCommand cmd : canceledCommandAfterImpactAnalysis) {
                    generateConsistencyCancelDeployStatus(mainStatus, cmd.getViewObject());
                }
            }
            return mainStatus;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            String url = serverDef.getProtocol() + serverDef.getHost() + ":" + serverDef.getPort() //$NON-NLS-1$
                    + serverDef.getPath();
            String title = Messages.bind(Messages.Server_cannot_connected, url);
            MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), title,
                    Messages.AbstractDataClusterAction_ConnectFailed);
            return Status.CANCEL_STATUS;
        }
    }

    public void updateServerConsistencyStatus(MDMServerDef serverDef, IStatus mainStatus) throws XtentisException,
            WebServiceException {
        if (!UIUtil.isWorkInUI()) {
            return;
        }
        if (mainStatus.isMultiStatus()) {
            Set<IRepositoryViewObject> viewObjs = new HashSet<IRepositoryViewObject>();
            for (IStatus childStatus : mainStatus.getChildren()) {
                DeployStatus deployStatus = null;
                if (childStatus instanceof DeployStatus) {
                    deployStatus = (DeployStatus) childStatus;
                    if (deployStatus.isOK()) {
                        AbstractDeployCommand command = (AbstractDeployCommand) deployStatus.getCommand();
                        if (command instanceof BatchDeployJobCommand) {
                            for (ICommand subCmd : ((BatchDeployJobCommand) command).getSubCmds()) {
                                if (subCmd instanceof AbstractDeployCommand) {
                                    IRepositoryViewObject viewObj = ((AbstractDeployCommand) subCmd).getViewObject();
                                    if (viewObj != null) {
                                        viewObjs.add(viewObj);
                                    }
                                }
                            }
                        } else {
                            if (command != null) {
                                IRepositoryViewObject viewObj = command.getViewObject();
                                if (viewObj != null) {
                                    viewObjs.add(viewObj);
                                }
                            }
                        }

                    }
                } else if (childStatus instanceof MultiStatus) {
                    updateServerConsistencyStatus(serverDef, childStatus);
                }

            }
            updateServerConsistencyStatus(serverDef, viewObjs);
        }
    }

    private void updateServerConsistencyStatus(MDMServerDef serverDef, Collection<IRepositoryViewObject> viewObjs)
            throws XtentisException, WebServiceException {
        ConsistencyService consistencyService = ConsistencyService.getInstance();
        for (IRepositoryViewObject viewObj : viewObjs) {
            consistencyService.updateDigestValue(serverDef, viewObj);
        }
    }

    public void generateValidationFailedDeployStatus(IStatus mainStatus, List<IRepositoryViewObject> cancelViewObjs) {
        for (IRepositoryViewObject viewObj : cancelViewObjs) {
            ICommand cancelCmd = CommandManager.getInstance().getNewCommand(ICommand.CMD_NOP);
            cancelCmd.updateViewObject(viewObj);
            DeployStatus cancelStatus = DeployStatus.getInfoStatus(cancelCmd,
                    Messages.bind(Messages.Deploy_cancel_text, viewObj.getLabel()));
            ((MultiStatus) mainStatus).add(cancelStatus);
        }

    }

    public void generateConsistencyCancelDeployStatus(IStatus mainStatus, IRepositoryViewObject... cancelViewObjs) {
        if (cancelViewObjs != null) {
            for (IRepositoryViewObject viewObj : cancelViewObjs) {
                ICommand cancelCmd = CommandManager.getInstance().getNewCommand(ICommand.CMD_NOP);
                cancelCmd.updateViewObject(viewObj);
                DeployStatus cancelStatus = DeployStatus.getInfoStatus(cancelCmd, Messages.DeployService_conflictCancelStatus
                        + viewObj.getLabel());
                ((MultiStatus) mainStatus).add(cancelStatus);
            }
        }
    }

    /**
     * work for updater server operation.
     *
     * @param serverDef
     * @param viewObjs
     * @param selectededCommands
     * @return
     * @throws XtentisException
     */
    public ConsistencyCheckResult checkConsistency(MDMServerDef serverDef, List<IRepositoryViewObject> viewObjs,
            List<AbstractDeployCommand> selectededCommands) throws XtentisException {

        Map<IRepositoryViewObject, Integer> viewObCmdOpjMap = new HashMap<IRepositoryViewObject, Integer>();
        for (AbstractDeployCommand cmd : selectededCommands) {
            IRepositoryViewObject viewObj = cmd.getViewObject();
            if (viewObj != null && viewObjs.contains(viewObj)) {
                viewObCmdOpjMap.put(viewObj, cmd.getCommandType());
            }
        }
        return ConsistencyService.getInstance().checkConsistency(serverDef, viewObCmdOpjMap);
    }

    public IModelValidateResult validateModel(List<IRepositoryViewObject> viewObjs) {
        IModelValidationService service = GlobalServiceRegister.getDefault().getService(
                IModelValidationService.class);

        IModelValidateResult validateResult = service.validate(viewObjs, IModelValidationService.VALIDATE_BEFORE_DEPLOY, false);
        return validateResult;
    }

    public IStatus deployAnotherVersion(MDMServerDef serverDef, List<IRepositoryViewObject> viewObjs) {
        CommandManager manager = CommandManager.getInstance();
        List<AbstractDeployCommand> commands = manager.getDeployCommandsWithoutHistory(viewObjs);
        try {
            // insert impact dialog
            Map<IRepositoryViewObject, ImpactOperation> analyzeModelImpact = ModelImpactAnalyseService.analyzeCommandImpact(
                    serverDef, commands);
            Map<IRepositoryViewObject, ICommandParameter> paramMap = null;
            if (analyzeModelImpact != null) {
                ModelImpactAnalyseService.shrinkDeployCommands(analyzeModelImpact, commands);
                paramMap = ModelImpactAnalyseService.convertToParameters(analyzeModelImpact);
                manager.attachParameterToCommand(commands, paramMap);
            }
        } catch (InterruptedException ex) {
            return Status.CANCEL_STATUS;
        }
        return runCommands(commands, serverDef);
    }

    public IStatus deploy(MDMServerDef serverDef, List<IRepositoryViewObject> viewObjs, int defaultCmdType) {
        return deploy(serverDef, viewObjs, defaultCmdType, false);
    }

    private void removeLockedViewObj(List<IRepositoryViewObject> viewObjs) {
        if (viewObjs != null) {
            for (java.util.Iterator<IRepositoryViewObject> il = viewObjs.iterator(); il.hasNext();) {
                if (RepositoryResourceUtil.isLockedAndEdited(il.next())) {
                    il.remove();
                }
            }
        }
    }

    public boolean isAutoDeploy() {
        return PlatformUI.getPreferenceStore().getBoolean(PreferenceConstants.P_AUTO_DEPLOY);
    }

    public List<IRepositoryViewObject> getAssociatedObjects(IRepositoryViewObject viewObject) {
        List<IRepositoryViewObject> viewObjs = new LinkedList<IRepositoryViewObject>();
        if (viewObject != null) {
            ERepositoryObjectType type = viewObject.getRepositoryObjectType();
            if (type != null) {
                IInteractiveHandler handler = InteractiveService.findHandler(type);
                if (handler != null) {
                    List<IRepositoryViewObject> associatedObjects = handler.getAssociatedObjects(viewObject);
                    if (associatedObjects != null) {
                        for (IRepositoryViewObject associatedObj : associatedObjects) {
                            viewObjs.add(associatedObj);
                        }
                    }
                }
            }
        }

        return viewObjs;
    }

    public void autoDeploy(Shell shell, IRepositoryViewObject viewObj) {
        if (shell == null || viewObj == null) {
            throw new IllegalArgumentException();
        }
        MDMServerDef serverDef = RepositoryResourceUtil.getLastServerDef(viewObj);
        if (serverDef != null) {

            if (!serverDef.isEnabled()) {
                MessageDialog.openWarning(shell, null, Messages.DeployService_CanNotDeployToDisabledServer);
                return;
            }
            List<IRepositoryViewObject> viewObjs = getAssociatedObjects(viewObj);
            viewObjs.add(0, viewObj);

            IStatus status = deploy(serverDef, viewObjs, ICommand.CMD_MODIFY, false);
            if (!status.isOK()) {
                return;
            }
            updateAutoStatus(status);
            if (status.isMultiStatus()) {
                showDeployStatus(shell, status);
            }
        } else {
            boolean warnUser = PlatformUI.getPreferenceStore().getBoolean(PreferenceConstants.P_WARN_USER_AUTO_DEPLOY);
            if (warnUser) {
                MessageDialog.openWarning(shell, Messages.Warning_text, Messages.NeverDeploy_text);
            }
        }
    }

    public List<IRepositoryViewObject> getDeployViewObject(List<AbstractDeployCommand> selectededCommands) {
        List<IRepositoryViewObject> viewObjs = new ArrayList<IRepositoryViewObject>(selectededCommands.size());
        for (AbstractDeployCommand command : selectededCommands) {
            IRepositoryViewObject viewObject = command.getViewObject();
            if (viewObject != null && !viewObjs.contains(viewObject)) {
                viewObjs.add(viewObject);
            }
        }
        return viewObjs;
    }

    public void removeInvalidCommands(List<IRepositoryViewObject> invalidObjects, List<AbstractDeployCommand> selectededCommands) {
        if (invalidObjects == null || invalidObjects.isEmpty()) {
            return;
        }
        for (Iterator<AbstractDeployCommand> il = selectededCommands.iterator(); il.hasNext();) {
            AbstractDeployCommand cmd = il.next();
            IRepositoryViewObject viewObject = cmd.getViewObject();
            if (viewObject != null && invalidObjects.contains(viewObject)) {
                il.remove();
            }
        }
    }

    protected void updateAutoStatus(IStatus status) {
        if (status.isMultiStatus()) {
            for (IStatus childStatus : status.getChildren()) {
                DeployStatus deployStatus = null;
                if (childStatus instanceof DeployStatus) {
                    deployStatus = (DeployStatus) childStatus;
                } else if (childStatus instanceof MultiStatus) {
                    deployStatus = (DeployStatus) ((MultiStatus) childStatus).getChildren()[0];
                }

                ICommand command = deployStatus.getCommand();
                CommandManager manager = CommandManager.getInstance();
                manager.removeCommandStack(command, ICommand.PHASE_DEPLOY);

            }
        }
    }

    private void showDeployStatus(Shell shell, IStatus status) {
        MultiStatusDialog dialog = new DeployStatusDialog(shell, status);
        dialog.open();
    }

    public IStatus runCommands(List<AbstractDeployCommand> commands, MDMServerDef serverDef) {
        filterInvalidCommands(commands);
        reorderCommandObjects(commands);

        CommandManager manager = CommandManager.getInstance();
        List<ICommand> compundCommands = manager.convertToDeployCompundCommands(commands, serverDef);
        manager.arrangeForJobCommands(compundCommands);
        //
        try {
            IProgressService progressService = null;
            if (PlatformUI.isWorkbenchRunning()) {
                progressService = PlatformUI.getWorkbench().getProgressService();
            } else {
                progressService = ConsoleProgressService.getInstance();
            }
            DeployProcess runnable = new DeployProcess(compundCommands);
            progressService.run(true, true, runnable);
            return runnable.getStatus();
        } catch (InvocationTargetException e) {
            log.error(e.getMessage(), e);
        } catch (InterruptedException e) {

        }
        return Status.CANCEL_STATUS;
    }

    private void filterInvalidCommands(List<AbstractDeployCommand> commands) {
        if (commands != null) {
            Iterator<AbstractDeployCommand> iterator = commands.iterator();
            while (iterator.hasNext()) {
                AbstractDeployCommand cmd = iterator.next();
                if (cmd.getViewObject() == null) {
                    iterator.remove();
                }
            }
        }
    }

    private void reorderCommandObjects(List<AbstractDeployCommand> commands) {
        commands.sort(new Comparator<AbstractDeployCommand>() {

            private int getTypeOrder(ERepositoryObjectType type) {
                if (type.equals(IServerObjectRepositoryType.TYPE_DATAMODEL)) {
                    return 1;
                }
                if (type.equals(IServerObjectRepositoryType.TYPE_DATACLUSTER)) {
                    return 2;
                }
                if (type.equals(IServerObjectRepositoryType.TYPE_MATCH_RULE_MAPINFO)) {
                    return 3;
                }
                if (type.equals(IServerObjectRepositoryType.TYPE_TDS)) {
                    return 4;
                }
                // others
                return 10;
            }

            @Override
            public int compare(AbstractDeployCommand o1, AbstractDeployCommand o2) {
                ERepositoryObjectType t1 = o1.getViewObject().getRepositoryObjectType();
                ERepositoryObjectType t2 = o2.getViewObject().getRepositoryObjectType();
                String label1 = o1.getViewObject().getLabel();
                String label2 = o2.getViewObject().getLabel();
                int typeOrder1 = getTypeOrder(t1);
                int typeOrder2 = getTypeOrder(t2);
                if (typeOrder1 == typeOrder2) {
                    return label1.compareTo(label2);
                } else {
                    return typeOrder1 - typeOrder2;
                }
            }

        });
    }

    private void pushRestoreCommand(CommandManager manager, List<ICommand> subCmds, MDMServerDef serverDef) {
        if (subCmds != null && subCmds.size() > 0) {
            for (ICommand command : subCmds) {
                pushRestoreCommand(manager, command, serverDef);
            }
        }
    }

    public void updateLastServer(IStatus status, IProgressMonitor monitor) {
        CommandManager manager = CommandManager.getInstance();
        Map<String, List<ICommand>> commandMap = manager.getAllCommandsByPhase(ICommand.PHASE_AFTER_DEPLOY);
        Set<String> ids = getValidUpdateIds(status);
        for (String id : commandMap.keySet()) {
            boolean canRun = false;
            if (ids == null) {
                canRun = true;
            } else {
                canRun = ids.contains(id);
            }
            if (canRun) {
                List<ICommand> cmds = commandMap.get(id);
                for (ICommand cmd : cmds) {
                    cmd.execute(null, monitor);
                }
                manager.removeCommandStack(id, ICommand.PHASE_AFTER_DEPLOY);
            }
        }
    }

    private Set<String> getValidUpdateIds(IStatus status) {
        if (status == null || !(status.isMultiStatus())) {
            return null;
        }
        Set<String> ids = new HashSet<String>();
        for (IStatus childStatus : status.getChildren()) {
            DeployStatus deployStatus = null;

            if (childStatus instanceof DeployStatus) {
                deployStatus = (DeployStatus) childStatus;
            } else if (childStatus instanceof MultiStatus) {
                if (((MultiStatus) childStatus).getChildren().length > 0) {
                    deployStatus = (DeployStatus) ((MultiStatus) childStatus).getChildren()[0];
                }
            }
            if (deployStatus != null && deployStatus.isOK()) {
                ICommand command = deployStatus.getCommand();
                if (command instanceof BatchDeployJobCommand) {
                    BatchDeployJobCommand deployJobCommand = (BatchDeployJobCommand) command;
                    for (ICommand subCmd : deployJobCommand.getSubCmds()) {
                        ids.add(subCmd.getCommandId());
                    }
                } else {
                    ids.add(command.getCommandId());
                }
            }
        }
        return ids;
    }

    public void updateChangedStatus(IStatus status) {
        updateChangedStatus(status, true);
    }

    public void updateChangedStatus(IStatus status, boolean isUpdateServer) {
        if (status.isMultiStatus()) {
            for (IStatus childStatus : status.getChildren()) {
                List<DeployStatus> deployStatuses = new ArrayList<DeployStatus>();
                if (childStatus instanceof DeployStatus) {
                    deployStatuses.add((DeployStatus) childStatus);
                } else if (childStatus instanceof MultiStatus) {
                    IStatus[] childrenStatus = ((MultiStatus) childStatus).getChildren();
                    if (childrenStatus.length > 0) {
                        for (IStatus cstatus : childrenStatus) {
                            if (cstatus instanceof DeployStatus) {
                                deployStatuses.add((DeployStatus) cstatus);
                                if (((DeployStatus) cstatus).getCommand() instanceof BatchDeployJobCommand) {
                                    break;
                                }
                            }
                        }
                    }
                }

                updateForStatus(isUpdateServer, deployStatuses);
            }
        }
    }

    private void updateForStatus(boolean isUpdateServer, List<DeployStatus> deployStatuses) {
        if (deployStatuses == null || deployStatuses.size() == 0) {
            return;
        }

        for (DeployStatus deployStatus : deployStatuses) {
            if (deployStatus != null && deployStatus.isOK()) {
                ICommand command = deployStatus.getCommand();
                if (command != null) {
                    CommandManager manager = CommandManager.getInstance();
                    manager.removeCommandStack(command, ICommand.PHASE_DEPLOY);
                    if (isUpdateServer) {
                        MDMServerDef serverDef = null;
                        if (command instanceof AbstractDeployCommand) {
                            serverDef = ((AbstractDeployCommand) command).getServerDef();
                        } else if (command instanceof DeployCompoundCommand) {
                            serverDef = ((DeployCompoundCommand) command).getServerDef();
                        }
                        if (command instanceof BatchDeployJobCommand) {
                            BatchDeployJobCommand deployJobCommand = (BatchDeployJobCommand) command;
                            pushRestoreCommand(manager, deployJobCommand.getSubCmds(), serverDef);
                            pushRestoreCommand(manager, deployJobCommand.getSubDeleteCmds(), serverDef);
                        } else {
                            // updateserver
                            pushRestoreCommand(manager, command, serverDef);
                        }
                    }
                }
            }
        }
    }

    public void pushRestoreCommand(CommandManager manager, ICommand command, MDMServerDef serverDef) {
        // updateserver
        if (command.getCommandType() != ICommand.CMD_DELETE) {
            Item item = command.getViewObject().getProperty().getItem();
            UpdateLastServerCommand updateLastServerCommand = new UpdateLastServerCommand(item, serverDef);
            updateLastServerCommand.setToRunPhase(ICommand.PHASE_AFTER_DEPLOY);
            updateLastServerCommand.init(command.getCommandId(), command.getObjLastName());
            manager.pushCommand(updateLastServerCommand);
        }
        //
        if (command.getCommandType() == ICommand.CMD_DELETE && !(command instanceof CompoundCommand)) {

            UpdateLastServerCommand emptyLastServerCommand = new UpdateLastServerCommand();
            PushCmdCommand pushCmd = new PushCmdCommand();
            pushCmd.setPushCmdType(ICommand.CMD_ADD);
            pushRestoreCommand(manager, command, emptyLastServerCommand);
            pushRestoreCommand(manager, command, pushCmd);
        }
    }

    private void pushRestoreCommand(CommandManager manager, ICommand command, ICommand cmd) {
        cmd.init(command.getCommandId(), command.getObjLastName());
        cmd.setToRunPhase(ICommand.PHASE_RESTORE);
        manager.pushCommand(cmd);
    }
}
