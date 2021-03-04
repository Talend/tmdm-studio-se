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
package org.talend.mdm.repository.ui.actions;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.ws.WebServiceException;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.command.CommandManager;
import org.talend.mdm.repository.core.command.deploy.AbstractDeployCommand;
import org.talend.mdm.repository.core.command.param.ICommandParameter;
import org.talend.mdm.repository.core.service.ConsistencyService.ConsistencyCheckResult;
import org.talend.mdm.repository.core.service.ContainerCacheService;
import org.talend.mdm.repository.core.service.DeployService;
import org.talend.mdm.repository.core.service.IModelValidationService;
import org.talend.mdm.repository.core.service.IModelValidationService.IModelValidateResult;
import org.talend.mdm.repository.core.service.ModelImpactAnalyseService;
import org.talend.mdm.repository.core.service.ModelImpactAnalyseService.ImpactOperation;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.model.mdmmetadata.MDMServerDef;
import org.talend.mdm.repository.plugin.RepositoryPlugin;
import org.talend.mdm.repository.ui.dialogs.deploy.DeployAllDialog;
import org.talend.mdm.repository.ui.dialogs.lock.LockedDirtyObjectDialog;
import org.talend.mdm.repository.utils.EclipseResourceManager;

import com.amalto.workbench.service.MissingJarService;
import com.amalto.workbench.utils.XtentisException;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class DeployAllAction extends AbstractDeployAction {

    private static Logger log = Logger.getLogger(DeployAllAction.class);

    private final boolean isDeployAll;

    private static final ImageDescriptor DEPLOY_IMG = EclipseResourceManager.getImageDescriptor(RepositoryPlugin.PLUGIN_ID,
            "/icons/server_export.png"); //$NON-NLS-1$

    private List<IRepositoryViewObject> deployViewObject;

    public DeployAllAction(boolean isDeployAll) {
        super(Messages.DeployAllAction_label);
        this.isDeployAll = isDeployAll;
        setImageDescriptor(DEPLOY_IMG);
    }

    @Override
    protected void _doRun() {
        boolean checkMissingJar = MissingJarService.getInstance().checkMissingJar(true);
        if (!checkMissingJar) {
            return;
        }
        if (isDeployAll) {
            runWithType(null);
        } else {
            List<Object> selectedObject = getSelectedObject();
            if (!selectedObject.isEmpty()) {
                Object object = selectedObject.get(0);
                if (object instanceof IRepositoryViewObject) {
                    ERepositoryObjectType type = ((IRepositoryViewObject) object).getRepositoryObjectType();
                    runWithType(type);
                }
            }
        }

    }

    public void runWithType(ERepositoryObjectType type) {
        DeployAllDialog dialog = new DeployAllDialog(getShell(), type);
        if (dialog.open() == IDialogConstants.OK_ID) {
            List<AbstractDeployCommand> selectededCommands = dialog.getSelectedCommands();
            if (selectededCommands.size() >= 0) {
                DeployService deployService = DeployService.getInstance();
                deployViewObject = deployService.getDeployViewObject(selectededCommands);

                // validate object
                IModelValidateResult validateResult = deployService.validateModel(deployViewObject);
                int selectedButton = validateResult.getSelectedButton();
                if (selectedButton == IModelValidationService.BUTTON_CANCEL) {
                    return;
                }
                List<IRepositoryViewObject> validObjects = validateResult.getValidObjects(selectedButton);
                List<IRepositoryViewObject> invalidObjects = validateResult.getInvalidObjects(selectedButton);
                //
                MDMServerDef serverDef = dialog.getServerDef();
                try {
                    // consistency check
                    ConsistencyCheckResult consistencyCheckResult = deployService.checkConsistency(serverDef, validObjects,
                            selectededCommands);
                    if (consistencyCheckResult.isCanceled()) {
                        return;
                    } else {
                        validObjects = consistencyCheckResult.getToDeployObjects();
                    }
                    deployService.removeInvalidCommands(invalidObjects, selectededCommands);
                    deployService.removeInvalidCommands(consistencyCheckResult.getToSkipObjects(), selectededCommands);
                    // save editors
                    LockedDirtyObjectDialog lockDirtyDialog = new LockedDirtyObjectDialog(getShell(),
                            Messages.AbstractDeployAction_promptToSaveEditors, validObjects);
                    if (lockDirtyDialog.needShowDialog() && lockDirtyDialog.open() == IDialogConstants.CANCEL_ID) {
                        return;
                    }
                    lockDirtyDialog.saveDirtyObjects();
                    // insert impact dialog
                    List<AbstractDeployCommand> canceledCommandAfterImpactAnalysis = new LinkedList<AbstractDeployCommand>(
                            selectededCommands);
                    try {
                        Map<IRepositoryViewObject, ImpactOperation> analyzeModelImpact = ModelImpactAnalyseService
                                .analyzeCommandImpact(serverDef, selectededCommands);
                        Map<IRepositoryViewObject, ICommandParameter> paramMap = null;
                        if (analyzeModelImpact != null) {
                            ModelImpactAnalyseService.shrinkDeployCommands(analyzeModelImpact, selectededCommands);
                            paramMap = ModelImpactAnalyseService.convertToParameters(analyzeModelImpact);
                            CommandManager.getInstance().attachParameterToCommand(selectededCommands, paramMap);
                        }
                        canceledCommandAfterImpactAnalysis.removeAll(selectededCommands);
                    } catch (InterruptedException ex) {
                        return;
                    }
                    IStatus status = deployService.runCommands(selectededCommands, serverDef);
                    // update consistency value
                    try {
                        deployService.updateServerConsistencyStatus(serverDef, status);
                    } catch (XtentisException e) {
                        log.error(e.getMessage(), e);
                    } catch (WebServiceException e) {
                        log.error(e.getMessage(), e);
                    }
                    // add canceled object to status
                    deployService.generateValidationFailedDeployStatus(status, invalidObjects);
                    deployService.generateConsistencyCancelDeployStatus(status,
                            consistencyCheckResult.getToSkipObjects().toArray(new IRepositoryViewObject[0]));
                    for (AbstractDeployCommand cmd : canceledCommandAfterImpactAnalysis) {
                        deployService.generateConsistencyCancelDeployStatus(status, cmd.getViewObject());
                    }
                    //
                    updateChangedStatus(status);
                    if (status.isMultiStatus()) {
                        showDeployStatus(status);
                    }
                    updateLastServer(status, new NullProgressMonitor());
                } catch (Exception e) {
                    String url = serverDef.getProtocol() + serverDef.getHost() + ":" + serverDef.getPort() //$NON-NLS-1$
                            + serverDef.getPath();
                    String title = Messages.bind(Messages.Server_cannot_connected, url);
                    MessageDialog.openError(getShell(), title, Messages.AbstractDataClusterAction_ConnectFailed);
                }
            }
        }
    }

    @Override
    protected void refreshDeployedViewObjects() {
        if (deployViewObject.size() > 0) {
            for (IRepositoryViewObject viewObj : deployViewObject) {
                IRepositoryViewObject iRepositoryViewObject = ContainerCacheService.get(viewObj.getProperty());
                commonViewer.refresh(iRepositoryViewObject);
            }
        }
    }

    protected void refreshParent(Object object) {
        if (object instanceof IRepositoryViewObject) {
            IRepositoryViewObject parent = ContainerCacheService.getParent((IRepositoryViewObject) object);
            if (parent != null) {
                commonViewer.refresh(parent);
            }
        }
    }
}
