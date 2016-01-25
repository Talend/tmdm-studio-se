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
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IWorkbenchPage;

import com.amalto.workbench.editors.xsdeditor.XSDEditorUtil;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.providers.XObjectEditorInput;
import com.amalto.workbench.utils.IConstants;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.views.ServerView;
import com.amalto.workbench.webservices.WSDataCluster;
import com.amalto.workbench.webservices.WSDataClusterPK;
import com.amalto.workbench.webservices.WSDataModel;
import com.amalto.workbench.webservices.WSDataModelPK;
import com.amalto.workbench.webservices.WSGetDataCluster;
import com.amalto.workbench.webservices.WSGetDataModel;
import com.amalto.workbench.webservices.WSGetMenu;
import com.amalto.workbench.webservices.WSGetRole;
import com.amalto.workbench.webservices.WSGetRoutingRule;
import com.amalto.workbench.webservices.WSGetStoredProcedure;
import com.amalto.workbench.webservices.WSGetSynchronizationPlan;
import com.amalto.workbench.webservices.WSGetTransformerV2;
import com.amalto.workbench.webservices.WSGetUniverse;
import com.amalto.workbench.webservices.WSGetView;
import com.amalto.workbench.webservices.WSMenu;
import com.amalto.workbench.webservices.WSMenuPK;
import com.amalto.workbench.webservices.WSRole;
import com.amalto.workbench.webservices.WSRolePK;
import com.amalto.workbench.webservices.WSRoutingRule;
import com.amalto.workbench.webservices.WSRoutingRulePK;
import com.amalto.workbench.webservices.WSStoredProcedure;
import com.amalto.workbench.webservices.WSStoredProcedurePK;
import com.amalto.workbench.webservices.WSSynchronizationPlan;
import com.amalto.workbench.webservices.WSSynchronizationPlanPK;
import com.amalto.workbench.webservices.WSTransformerV2;
import com.amalto.workbench.webservices.WSTransformerV2PK;
import com.amalto.workbench.webservices.WSUniverse;
import com.amalto.workbench.webservices.WSUniversePK;
import com.amalto.workbench.webservices.WSView;
import com.amalto.workbench.webservices.WSViewPK;
import com.amalto.workbench.webservices.XtentisPort;

public class EditXObjectAction extends Action {

    private static Log log = LogFactory.getLog(EditXObjectAction.class);

    private ServerView view = null;

    private TreeObject xobject = null;

    private IWorkbenchPage page = null;

    public EditXObjectAction(TreeObject xobject, IWorkbenchPage page) {
        super();
        this.xobject = xobject;
        this.page = page;
        setDetails();
    }

    public EditXObjectAction(ServerView view) {
        super();
        this.view = view;
        setDetails();
    }

    private void setDetails() {
        setImageDescriptor(ImageCache.getImage("icons/edit.gif"));//$NON-NLS-1$
        setText(Messages.EditXObjectAction_Edit);
        setToolTipText(Messages.bind(Messages.EditXObjectAction_ActionTip, IConstants.TALEND));
    }

    public void run() {
        try {
            super.run();
            if (this.view != null) { // called from ServerView
                ISelection selection = view.getViewer().getSelection();
                xobject = (TreeObject) ((IStructuredSelection) selection).getFirstElement();
            }

            if (xobject == null || !xobject.isXObject())
                return;

            // Access to server and get port
            XtentisPort port = Util.getPort(new URL(xobject.getEndpointAddress()), xobject.getUniverse(), xobject.getUsername(),
                    xobject.getPassword());

            switch (xobject.getType()) {
            case TreeObject.DATA_MODEL:
                WSDataModel wsDataModel = port.getDataModel(new WSGetDataModel((WSDataModelPK) xobject.getWsKey()));
                xobject.setWsObject(wsDataModel);
                XSDEditorUtil.openDataModel(xobject, false);
                return;
            case TreeObject.VIEW:
                WSView wsView = port.getView(new WSGetView((WSViewPK) xobject.getWsKey()));
                xobject.setWsObject(wsView);
                break;
            case TreeObject.DATA_CLUSTER:
                WSDataCluster wsDataCluster = port.getDataCluster(new WSGetDataCluster((WSDataClusterPK) xobject.getWsKey()));
                xobject.setWsObject(wsDataCluster);
                break;
            case TreeObject.STORED_PROCEDURE:
                WSStoredProcedure wsStoredProcedure = port.getStoredProcedure(new WSGetStoredProcedure(
                        (WSStoredProcedurePK) xobject.getWsKey()));
                xobject.setWsObject(wsStoredProcedure);
                break;
            case TreeObject.ROLE:
                WSRole wsRole = port.getRole(new WSGetRole((WSRolePK) xobject.getWsKey()));
                xobject.setWsObject(wsRole);
                break;
            case TreeObject.ROUTING_RULE:
                WSRoutingRule wsRoutingRule = port.getRoutingRule(new WSGetRoutingRule((WSRoutingRulePK) xobject.getWsKey()));
                xobject.setWsObject(wsRoutingRule);
                break;
            case TreeObject.TRANSFORMER:
                WSTransformerV2 wsTranformer = port.getTransformerV2(new WSGetTransformerV2((WSTransformerV2PK) xobject
                        .getWsKey()));
                xobject.setWsObject(wsTranformer);
                break;
            case TreeObject.MENU:
                WSMenu wsMenu = port.getMenu(new WSGetMenu((WSMenuPK) xobject.getWsKey()));
                xobject.setWsObject(wsMenu);
                break;
            case TreeObject.UNIVERSE:
                WSUniverse wsUniverse = port.getUniverse(new WSGetUniverse((WSUniversePK) xobject.getWsKey()));
                xobject.setWsObject(wsUniverse);
                break;
            case TreeObject.SYNCHRONIZATIONPLAN:
                WSSynchronizationPlan wsSynchronizationPlan = port.getSynchronizationPlan(new WSGetSynchronizationPlan(
                        (WSSynchronizationPlanPK) xobject.getWsKey()));
                xobject.setWsObject(wsSynchronizationPlan);
                break;

            case TreeObject.JOB_REGISTRY:
                // System.out.println("JOB_REGISTRY "+ xobject.getDisplayName());
                break;
            case TreeObject.JOB:

                // System.out.println("JOB "+ xobject.getDisplayName()+" "+xobject.getWsKey());
                xobject.setWsObject(xobject.getDisplayName());
                break;

            case TreeObject.SERVICE_CONFIGURATION:
                break;

            case TreeObject.RESOURCES:
            case TreeObject.CUSTOM_TYPE:
            case TreeObject.DATA_MODEL_RESOURCE:
            case TreeObject.DATA_MODEL_TYPES_RESOURCE:
            case TreeObject.CUSTOM_TYPES_RESOURCE:
            case TreeObject.PICTURES_RESOURCE:
                break;
            default:
                MessageDialog.openError(view.getSite().getShell(), Messages._Error, Messages.bind(Messages.EditXObjectAction_ErrorMsg1, IConstants.TALEND, xobject.getType()));
                return;
            }// switch

            if (page == null)
                this.page = view.getSite().getWorkbenchWindow().getActivePage();

            this.page.openEditor(new XObjectEditorInput(xobject, xobject.getDisplayName()),
                    "com.amalto.workbench.editors.XObjectEditor"); //$NON-NLS-1$

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if(!Util.handleConnectionException(view.getSite().getShell(), e, Messages.EditXObjectAction_ErrorMsg2)){
            	MessageDialog.openError(view.getSite().getShell(), Messages._Error,
                        Messages.bind(Messages.EditXObjectAction_ErrorMsg2, e.getLocalizedMessage()));
            }
        }
    }

    public void runWithEvent(Event event) {
        super.runWithEvent(event);
    }

}
