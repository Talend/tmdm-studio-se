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

import com.amalto.workbench.editors.RoutingEngineV2BrowserMainPage;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.webservices.TMDMService;
import com.amalto.workbench.webservices.WSExecuteRoutingOrderV2Asynchronously;
import com.amalto.workbench.webservices.WSExecuteRoutingOrderV2Synchronously;
import com.amalto.workbench.webservices.WSRoutingOrderV2;
import com.amalto.workbench.webservices.WSRoutingOrderV2PK;
import com.amalto.workbench.webservices.WSString;

public class ExecuteRoutingOrdersAction extends Action {

    private static Log log = LogFactory.getLog(ExecuteRoutingOrdersAction.class);

    protected Shell shell = null;

    protected Viewer viewer;

    protected boolean synchronously = true;

    private RoutingEngineV2BrowserMainPage routingEngineV2BrowserMainPage;

    public ExecuteRoutingOrdersAction(Shell shell, RoutingEngineV2BrowserMainPage routingEngineV2BrowserMainPage, Viewer viewer,
            boolean synchronously) {
        this.shell = shell;
        this.routingEngineV2BrowserMainPage = routingEngineV2BrowserMainPage;
        this.viewer = viewer;
        this.synchronously = synchronously;

        setImageDescriptor(ImageCache.getImage("icons/execute.gif"));//$NON-NLS-1$
        IStructuredSelection selection = ((IStructuredSelection) viewer.getSelection());
        if (selection.size() == 1) {
            setText(Messages.RoutingEngineV2BrowserMainPage_Text
                    + (synchronously ? Messages.RoutingEngineV2BrowserMainPage_Text2
                            : Messages.RoutingEngineV2BrowserMainPage_Text3) + Messages.RoutingEngineV2BrowserMainPage_TextA);
        } else {
            setText(Messages.RoutingEngineV2BrowserMainPage_Text1
                    + (synchronously ? Messages.RoutingEngineV2BrowserMainPage_Text2
                            : Messages.RoutingEngineV2BrowserMainPage_Text3) + Messages.RoutingEngineV2BrowserMainPage_Text1A
                    + selection.size() + Messages.RoutingEngineV2BrowserMainPage_Text1B);
        }
        setToolTipText(Messages.RoutingEngineV2BrowserMainPage_ActionTip
                + (synchronously ? Messages.RoutingEngineV2BrowserMainPage_Text2 : Messages.RoutingEngineV2BrowserMainPage_Text3)
                + Messages.RoutingEngineV2BrowserMainPage_ActionTipA + (selection.size() > 1 ? "s" : TEXT));//$NON-NLS-1$
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

            if (!MessageDialog.openConfirm(this.shell, Messages.RoutingEngineV2BrowserMainPage_ConfirmTitle,
                    Messages.RoutingEngineV2BrowserMainPage_ConfirmContent
                            + (synchronously ? Messages.RoutingEngineV2BrowserMainPage_Text2
                                    : Messages.RoutingEngineV2BrowserMainPage_Text3)
                            + Messages.RoutingEngineV2BrowserMainPage_ConfirmContentA + lineItems.size()
                            + Messages.RoutingEngineV2BrowserMainPage_B)) {
                return;
            }

            // Instantiate the Monitor with actual deletes
            ExecuteRoutingOrdersWithProgress diwp = new ExecuteRoutingOrdersWithProgress(
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
                    Messages.bind(Messages.RoutingEngineV2BrowserMainPage_ErrorMsg4, e.getLocalizedMessage()));
        }
    }

    @Override
    public void runWithEvent(Event event) {
        super.runWithEvent(event);
    }

    class ExecuteRoutingOrdersWithProgress implements IRunnableWithProgress {


        Collection<WSRoutingOrderV2> lineItems;

        Shell parentShell;

        private TMDMService service;

        public ExecuteRoutingOrdersWithProgress(TMDMService tmdmService, Collection<WSRoutingOrderV2> lineItems, Shell shell) {
            super();
            this.service = tmdmService;
            this.lineItems = lineItems;
            this.parentShell = shell;
        }

        public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

            monitor.beginTask(Messages.RoutingEngineV2BrowserMainPage_ExecutingRoutingOrders, lineItems.size());

            String results = TEXT;
            if (service != null) {
                for (WSRoutingOrderV2 lineItem : lineItems) {

                    monitor.subTask(Messages.RoutingEngineV2BrowserMainPage_ExecutingRoutingOrder + lineItem.getName());

                    if (monitor.isCanceled()) {
                        MessageDialog.openWarning(this.parentShell, Messages.RoutingEngineV2BrowserMainPage_WarningTitle,
                                Messages.RoutingEngineV2BrowserMainPage_WraningMsg + lineItem.getName()
                                        + Messages.RoutingEngineV2BrowserMainPage_WraningMsgA
                                        + Messages.RoutingEngineV2BrowserMainPage_WraningMsgB);
                        return;
                    }

                    try {
                        if (synchronously) {
                            WSString wsResult = service
                                    .executeRoutingOrderV2Synchronously(new WSExecuteRoutingOrderV2Synchronously(
                                            new WSRoutingOrderV2PK(lineItem.getName(), lineItem.getStatus())));
                            if (wsResult.getValue() != null) {
                                results += lineItem.getName() + ": " + wsResult.getValue(); //$NON-NLS-1$
                            }
                        } else {
                            service.executeRoutingOrderV2Asynchronously(new WSExecuteRoutingOrderV2Asynchronously(
                                    new WSRoutingOrderV2PK(lineItem.getName(), lineItem.getStatus())));
                        }
                        monitor.worked(1);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        if (!Util.handleConnectionException(shell, e, Messages.RoutingEngineV2BrowserMainPage_ErrorExecuting)) {
                            MessageDialog.openError(shell, Messages.RoutingEngineV2BrowserMainPage_ErrorExecuting,
                                    Messages.bind(Messages.RoutingEngineV2BrowserMainPage_ErrorMsg6, e.getLocalizedMessage()));
                        }
                    }// try

                }// for
            }

            monitor.done();
            MessageDialog.openInformation(shell, Messages.RoutingEngineV2BrowserMainPage_InfoTitle, lineItems.size()
                    + Messages.RoutingEngineV2BrowserMainPage_InfoContent + (TEXT.equals(results) ? TEXT : "\n" + results));//$NON-NLS-1$

        }// run
    }// class DeleteItemsWithProgress
}
