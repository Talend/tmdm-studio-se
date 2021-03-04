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
package org.talend.mdm.repository.ui.actions.process;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.AbstractRepositoryAction;
import org.talend.mdm.repository.core.IServerObjectRepositoryType;
import org.talend.mdm.repository.core.service.ContainerCacheService;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.model.mdmmetadata.MDMServerDef;
import org.talend.mdm.repository.plugin.RepositoryPlugin;
import org.talend.mdm.repository.ui.editors.XObjectBrowser2;
import org.talend.mdm.repository.ui.editors.XObjectBrowserInput2;
import org.talend.mdm.repository.utils.EclipseResourceManager;
import org.talend.mdm.workbench.serverexplorer.core.ServerDefService;
import org.talend.mdm.workbench.serverexplorer.ui.actions.IEventMgrService;
import org.talend.mdm.workbench.serverexplorer.ui.dialogs.SelectServerDefDialog;

import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.service.MissingJarService;

public class MDMEventManagerAction extends AbstractRepositoryAction implements IEventMgrService {

    private static Log log = LogFactory.getLog(MDMEventManagerAction.class);

    private IWorkbenchPage page = null;

    private MDMServerDef mdmServerDef;

    private static final ImageDescriptor EM_IMG = EclipseResourceManager.getImageDescriptor(RepositoryPlugin.PLUGIN_ID,
            "/icons/sub_engine.png"); //$NON-NLS-1$

    public MDMEventManagerAction() {
        super(Messages.EventManager_text);
        setImageDescriptor(EM_IMG);
    }

    @Override
    protected void doRun() {
        boolean checkMissingJar = MissingJarService.getInstance().checkMissingJar(true);
        if (!checkMissingJar) {
            return;
        }
        MDMServerDef serverDef = getServerDef();
        if (serverDef != null) {
            try {
                ServerDefService.checkMDMConnection(serverDef);
            } catch (Exception e) {
                MessageDialog.openError(getShell(), Messages.MDMEventManagerAction_CheckConnection,
                        Messages.MDMEventManagerAction_connectFailed);
                return;
            }

            IRepositoryViewObject eventViewObj = getEventMangerViewObject();
            TreeObject treeObj = createModel();

            XObjectBrowserInput2 input = new XObjectBrowserInput2(eventViewObj, treeObj, Messages.EventManager_text);
            input.setServerDef(serverDef);
            if (page == null) {
                this.page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
            }
            try {
                this.page.openEditor(input, XObjectBrowser2.EDITOR_ID);
            } catch (PartInitException e) {
                log.error(e.getMessage(), e);
            }

            setServerDef(null);
        }
    }

    private MDMServerDef getServerDef() {
        if (mdmServerDef != null) {
            return mdmServerDef;
        }

        SelectServerDefDialog dlg = new SelectServerDefDialog(getShell());
        dlg.create();
        if (dlg.open() == IDialogConstants.OK_ID) {
            return dlg.getSelectedServerDef();
        }

        return null;
    }

    private IRepositoryViewObject getEventMangerViewObject() {
        IRepositoryViewObject eventViewObj = null;

        List<Object> selectedObject = getSelectedObject();
        if (selectedObject != null && selectedObject.size() > 0) {
            eventViewObj = (IRepositoryViewObject) selectedObject.get(0);
        } else {
            eventViewObj = ContainerCacheService.get(IServerObjectRepositoryType.TYPE_EVENTMANAGER, ""); //$NON-NLS-1$
        }

        return eventViewObj;
    }

    private TreeObject createModel() {
        TreeObject treeObj = new TreeObject("treeObject", null, TreeObject.SUBSCRIPTION_ENGINE, null, null); //$NON-NLS-1$
        return treeObj;
    }

    public void run(MDMServerDef serverDef) {
        setServerDef(serverDef);
        run();
    }

    private void setServerDef(MDMServerDef serverDef) {
        this.mdmServerDef = serverDef;
    }

    @Override
    public String getGroupName() {
        return GROUP_COMMON;
    }

}
