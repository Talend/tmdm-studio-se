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
package com.amalto.workbench.actions;

import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import com.amalto.workbench.editors.XObjectBrowser;
import com.amalto.workbench.editors.XObjectEditor;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.models.TreeParent;
import com.amalto.workbench.providers.XObjectBrowserInput;
import com.amalto.workbench.utils.IConstants;
import com.amalto.workbench.utils.LocalTreeObjectRepository;
import com.amalto.workbench.utils.MDMServerHelper;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.views.ServerView;
import com.amalto.workbench.webservices.WSLogout;

public class ServerLogoutAction extends Action {

    private static final Log log = LogFactory.getLog(ServerLogoutAction.class);

    private ServerView view;

    private boolean remove;

    public ServerLogoutAction(ServerView view, boolean remove) {
        this.view = view;
        this.remove = remove;
        if (remove) {
            setText(Messages.ServerLogoutAction_Text1);
            setToolTipText(Messages.bind(Messages.ServerLogoutAction_ActionTip1, IConstants.TALEND));
        } else {
            setText(Messages.ServerLogoutAction_Text2);
            setToolTipText(Messages.bind(Messages.ServerLogoutAction_ActionTip2, IConstants.TALEND));
        }
        setImageDescriptor(ImageCache.getImage(EImage.LOGOUT.getPath()));
        needConfirm=true;
    }
    
    private boolean needConfirm=true;
    //this constructor is for updating server location without confirm to delete
    public ServerLogoutAction(ServerView view) {
        this(view,true);
        needConfirm=false;
    }

    @Override
    public void run() {
        TreeParent serverRoot = (TreeParent) ((IStructuredSelection) view.getViewer().getSelection()).getFirstElement();

        if (remove
                && needConfirm
                && !MessageDialog.openConfirm(this.view.getSite().getShell(), Messages.ServerLogoutAction_DialogTitle,
                        Messages.bind(Messages.ServerLogoutAction_ConfirmInfo, serverRoot.getName())))
            return;

        final String universe = serverRoot.getUniverse();
        final String username = serverRoot.getUsername();
        final String password = serverRoot.getPassword();
        final String endpointAddress = serverRoot.getEndpointAddress();

        LocalTreeObjectRepository.getInstance().switchOffListening();
        LocalTreeObjectRepository.getInstance().setLazySaveStrategy(false, (TreeParent) serverRoot);
        // add by ymli; fix the bug:0011948:
        // All the tabs related to an MDM server connection should go away when loging out
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        int length = page.getEditors().length;
        String version = "";//$NON-NLS-1$
        String tabEndpointAddress = "";//$NON-NLS-1$
        String unserName = null;
        int j = 0;
        for (int i = 0; i < length; i++) {
            IEditorPart part = page.getEditors()[i - j];
            if (part instanceof XObjectBrowser) {
                version = ((TreeObject) ((XObjectBrowserInput) part.getEditorInput()).getModel()).getUniverse();
                tabEndpointAddress = ((TreeObject) ((XObjectBrowserInput) part.getEditorInput()).getModel()).getEndpointAddress();
                unserName = ((TreeObject) ((XObjectBrowserInput) part.getEditorInput()).getModel()).getUsername();
            } else if (part instanceof XObjectEditor) {
                version = ((XObjectEditor) part).getInitialXObject().getServerRoot().getUniverse();
                tabEndpointAddress = ((XObjectEditor) part).getInitialXObject().getServerRoot().getEndpointAddress();
                unserName = ((XObjectEditor) part).getInitialXObject().getServerRoot().getUsername();
            }
            if (serverRoot.getUniverse().equals(version) && endpointAddress.equals(tabEndpointAddress)
                    && serverRoot.getUsername().equals(unserName)) {
                page.closeEditor(part, false);
                j++;
            }
        }

        // attempt logout on the server side
        view.getViewer().getControl().getDisplay().syncExec(new Runnable() {

            public void run() {
                try {
                    Util.getPort(new URL(endpointAddress), universe, username, password).logout(new WSLogout());
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        });

        if (remove) {
            serverRoot.getParent().removeChild(serverRoot);
            MDMServerHelper.deleteServer(serverRoot.getName());
            serverRoot = null;
        } else {
            serverRoot.removeChildren();
            view.initServerTreeChildren(serverRoot);

        }

        view.getViewer().refresh();
        
        if (serverRoot != null)
            view.getViewer().collapseToLevel(serverRoot, AbstractTreeViewer.ALL_LEVELS);
        
        // add by jsxie to fix the bug 0020497
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(true);
        
        
    }
}
