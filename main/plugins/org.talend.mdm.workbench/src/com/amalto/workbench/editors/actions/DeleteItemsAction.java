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
package com.amalto.workbench.editors.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.talend.mdm.webservice.TMDMService;
import org.talend.mdm.webservice.WSDeleteRoutingOrderV2;
import org.talend.mdm.webservice.WSRoutingOrderV2;
import org.talend.mdm.webservice.WSRoutingOrderV2PK;

import com.amalto.workbench.editors.RoutingEngineV2BrowserMainPage;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.utils.Util;

/***************************************************************
 * Delete Items Action
 *
 * @author bgrieder
 *
 ***************************************************************/
public class DeleteItemsAction extends Action {

    private static Log log = LogFactory.getLog(DeleteItemsAction.class);

    protected Shell shell = null;

    protected Viewer viewer;

    private RoutingEngineV2BrowserMainPage routingEngineV2BrowserMainPage;

    public DeleteItemsAction(Shell shell, RoutingEngineV2BrowserMainPage routingEngineV2BrowserMainPage, Viewer viewer) {
        this.shell = shell;
        this.routingEngineV2BrowserMainPage = routingEngineV2BrowserMainPage;
        this.viewer = viewer;

        setImageDescriptor(ImageCache.getImage("icons/delete_obj.gif"));//$NON-NLS-1$
        IStructuredSelection selection = ((IStructuredSelection) viewer.getSelection());
        if (selection.size() == 1) {
            setText(Messages.RoutingEngineV2BrowserMainPage_DelSelectedItem);
        } else {
            setText(Messages.bind(Messages.RoutingEngineV2BrowserMainPage_DeleteThese, selection.size()));
        }
        setToolTipText("Delete the selected Routing Order" + (selection.size() > 1 ? "s" : TEXT));//$NON-NLS-1$//$NON-NLS-2$
    }

    @Override
    public void run() {
        try {
            super.run();

            // retrieve the list of items
            IStructuredSelection selection = ((IStructuredSelection) viewer.getSelection());
            List<WSRoutingOrderV2> lineItems = selection.toList();

            if (lineItems.size() == 0) {
                return;
            }

            if (!MessageDialog.openConfirm(this.shell, Messages.RoutingEngineV2BrowserMainPage_ConfirmDeletion,
                    Messages.bind(Messages.RoutingEngineV2BrowserMainPage_ErrorMsg2, lineItems.size()))) {
                return;
            }

            // Instantiate the Monitor with actual deletes
            DeleteItemsWithProgress diwp = new DeleteItemsWithProgress(
                    (TMDMService) routingEngineV2BrowserMainPage.getAdapter(TMDMService.class), lineItems, this.shell);
            // run
            new ProgressMonitorDialog(this.shell).run(false, // fork
                    true, // cancelable
                    diwp);
            // refresh the search
            routingEngineV2BrowserMainPage.doSearch();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            MessageDialog.openError(shell, Messages._Error,
                    Messages.bind(Messages.RoutingEngineV2BrowserMainPage_ErrorMsg3, e.getLocalizedMessage()));
        }
    }

    @Override
    public void runWithEvent(Event event) {
        super.runWithEvent(event);
    }

    class DeleteItemsWithProgress implements IRunnableWithProgress {

        Collection<WSRoutingOrderV2> lineItems;

        Shell parentShell;

        private TMDMService service;

        public DeleteItemsWithProgress(TMDMService mdmService, Collection<WSRoutingOrderV2> lineItems, Shell shell) {
            super();
            this.service = mdmService;
            this.lineItems = lineItems;
            this.parentShell = shell;
        }

        public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
            try {
                monitor.beginTask(Messages.RoutingEngineV2BrowserMainPage_DeletingItems, lineItems.size());

                if (service != null) {

                    int i = 0;
                    for (WSRoutingOrderV2 lineItem : lineItems) {
                        monitor.subTask(Messages.RoutingEngineV2BrowserMainPage_ProcessingItem + (i++));
                        if (monitor.isCanceled()) {
                            MessageDialog.openWarning(this.parentShell, Messages.RoutingEngineV2BrowserMainPage_UserCancelDel,
                                    Messages.RoutingEngineV2BrowserMainPage_WarningMsg + i
                                            + Messages.RoutingEngineV2BrowserMainPage_WarningMsgA
                                            + Messages.RoutingEngineV2BrowserMainPage_WarningMsgB);
                            return;
                        }
                        service.deleteRoutingOrderV2(new WSDeleteRoutingOrderV2(new WSRoutingOrderV2PK(lineItem.getName(),
                                lineItem.getStatus())));
                        monitor.worked(1);
                    }// for

                }
                monitor.done();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                if (!Util.handleConnectionException(shell, e, Messages.RoutingEngineV2BrowserMainPage_ErrorDel)) {
                    MessageDialog.openError(shell, Messages.RoutingEngineV2BrowserMainPage_ErrorDel,
                            Messages.RoutingEngineV2BrowserMainPage_WarningMsg1 + e.getLocalizedMessage());
                }
            }// try

        }// run
    }// class DeleteItemsWithProgress
}

