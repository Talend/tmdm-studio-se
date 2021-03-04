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

import java.io.StringWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.exolab.castor.xml.Marshaller;

import com.amalto.workbench.dialogs.DOMViewDialog;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.webservices.WSRoutingOrderV2;

/***************************************************************
 * Edit Item Action
 *
 * @author bgrieder
 *
 ***************************************************************/
public class EditItemAction extends Action {

    private static Log log = LogFactory.getLog(EditItemAction.class);

    protected Shell shell = null;

    protected Viewer viewer;

    public EditItemAction(Shell shell, Viewer viewer) {
        super();
        this.shell = shell;
        this.viewer = viewer;
        setImageDescriptor(ImageCache.getImage("icons/edit_obj.gif"));//$NON-NLS-1$
        setText(Messages.RoutingEngineV2BrowserMainPage_EditItem);
        setToolTipText(Messages.RoutingEngineV2BrowserMainPage_ViewRoutingOrderDetails);
    }

    @Override
    public void run() {
        try {
            super.run();

            IStructuredSelection selection = ((IStructuredSelection) viewer.getSelection());

            if (selection.isEmpty()) {
                return;
            }

            WSRoutingOrderV2 routingOrder = (WSRoutingOrderV2) selection.getFirstElement();

            StringWriter sw = new StringWriter();
            Marshaller.marshal(routingOrder, sw);

            final DOMViewDialog d = new DOMViewDialog(shell, Util.parse(sw.toString()));
            d.addListener(new Listener() {

                public void handleEvent(Event event) {
                    d.close();
                }// handleEvent
            });

            d.setBlockOnOpen(true);
            d.open();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            MessageDialog.openError(shell, Messages._Error,
                    Messages.bind(Messages.RoutingEngineV2BrowserMainPage_ErrorToViewRoutingOrder, e.getLocalizedMessage()));
        }
    }

    @Override
    public void runWithEvent(Event event) {
        super.runWithEvent(event);
    }

}