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
package org.talend.mdm.repository.ui.actions;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.bridge.AbstractBridgeRepositoryAction;
import org.talend.mdm.repository.core.command.CommandManager;
import org.talend.mdm.repository.core.command.CommandStack;
import org.talend.mdm.repository.core.command.ICommand;
import org.talend.mdm.repository.core.command.deploy.AbstractDeployCommand;
import org.talend.mdm.repository.core.service.DeployService;
import org.talend.mdm.repository.core.service.IInteractiveHandler;
import org.talend.mdm.repository.core.service.InteractiveService;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.model.mdmmetadata.MDMServerDef;
import org.talend.mdm.repository.plugin.RepositoryPlugin;
import org.talend.mdm.repository.ui.dialogs.deploy.DeployStatusDialog;
import org.talend.mdm.repository.utils.EclipseResourceManager;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;
import org.talend.mdm.workbench.serverexplorer.ui.dialogs.SelectServerDefDialog;

/**
 * created by changguopiao on 2013-7-18 Detailled comment
 *
 */
public class UndeployAction extends AbstractBridgeRepositoryAction {

    private static final ImageDescriptor DEPLOY_IMG = EclipseResourceManager.getImageDescriptor(RepositoryPlugin.PLUGIN_ID,
            "/icons/undeploy.png"); //$NON-NLS-1$

    /**
     * DOC changguopiao UnloadAndDeleteAction constructor comment.
     *
     * @param text
     */
    public UndeployAction() {
        super(Messages.UndeployAction);
    }

    public UndeployAction(String label) {
        super(label);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#getImageDescriptor()
     */
    @Override
    public ImageDescriptor getImageDescriptor() {
        return DEPLOY_IMG;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.mdm.repository.core.AbstractRepositoryAction#getGroupName()
     */
    @Override
    public String getGroupName() {
        return GROUP_DEPLOY;
    }

    @Override
    public boolean isVisible(IRepositoryViewObject viewObj) {
        if (getSelectedObject().size() > 0) {
            if (RepositoryResourceUtil.isSystemViewObject(viewObj.getProperty().getItem())) {
                return false;
            }
            return true;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.mdm.repository.core.bridge.AbstractBridgeRepositoryAction#doRun()
     */
    @Override
    protected void doRun() {
        DeployService deployService = DeployService.getInstance();
        CommandManager cmanager = CommandManager.getInstance();
        List<IRepositoryViewObject> viewObjs = getSelectedRepositoryViewObject();
        if (viewObjs.size() < 1) {
            return;
        }
        SelectServerDefDialog dialog = getServerDefDialog(viewObjs);

        if (dialog.open() == IDialogConstants.OK_ID) {
            MDMServerDef serverDef = dialog.getSelectedServerDef();
            List<AbstractDeployCommand> deleteCommands = new ArrayList<AbstractDeployCommand>(viewObjs.size());
            for (IRepositoryViewObject obj : viewObjs) {
                ICommand deleteCmd = cmanager.getNewCommand(obj, ICommand.CMD_DELETE);
                deleteCmd.init(obj);
                deleteCommands.add((AbstractDeployCommand) deleteCmd);
            }
            IStatus status = deployService.runCommands(deleteCommands, serverDef);
            IProgressMonitor monitor = new NullProgressMonitor();
            deployService.updateChangedStatus(status);
            if (status.isMultiStatus()) {
                new DeployStatusDialog(getShell(), status).open();
            }
            for (IRepositoryViewObject viewObj : viewObjs) {
                MDMServerDef lastServerDef = RepositoryResourceUtil.getLastServerDef(viewObj);
                String id = viewObj.getId();
                CommandStack stack = cmanager.findCommandStack(id);
                if (stack != null) {
                    if (RepositoryResourceUtil.isSameMDMServerDef(lastServerDef, serverDef)) {
                        List<ICommand> commands = stack.getCommands(ICommand.PHASE_RESTORE);
                        for (ICommand cmd : commands) {
                            cmd.execute(null, monitor);
                        }
                    }
                    cmanager.removeCommandStack(id, ICommand.PHASE_RESTORE);
                    commonViewer.refresh(viewObj);
                }
            }
        } else {
            return;
        }

    }

    protected List<IRepositoryViewObject> getSelectedRepositoryViewObject() {
        List<IRepositoryViewObject> viewObjs = new LinkedList<IRepositoryViewObject>();
        for (Object obj : getSelectedObject()) {
            if (obj instanceof IRepositoryViewObject) {
                IRepositoryViewObject viewObject = (IRepositoryViewObject) obj;
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
                viewObjs.add(viewObject);
            }
        }
        return viewObjs;
    }

    private SelectServerDefDialog getServerDefDialog(List<IRepositoryViewObject> viewObjs) {
        SelectServerDefDialog dialog = new SelectServerDefDialog(getShell());
        MDMServerDef defServer = null;
        for (IRepositoryViewObject viewObject : viewObjs) {
            MDMServerDef tmpServer = RepositoryResourceUtil.getLastServerDef(viewObject);
            if (defServer == null) {
                defServer = tmpServer;
            } else if (tmpServer != null && !RepositoryResourceUtil.isSameMDMServerDef(defServer, tmpServer)) {
                defServer = null;
                break;
            }
        }
        dialog.create();
        dialog.setSelectServer(defServer);
        return dialog;
    }
}
