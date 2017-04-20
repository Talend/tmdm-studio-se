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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import com.amalto.workbench.exadapter.ExAdapterManager;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.providers.XObjectEditorInput;
import com.amalto.workbench.utils.IConstants;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.webservices.TMDMService;
import com.amalto.workbench.webservices.WSDataCluster;
import com.amalto.workbench.webservices.WSDataClusterPK;
import com.amalto.workbench.webservices.WSDataModel;
import com.amalto.workbench.webservices.WSDataModelPK;
import com.amalto.workbench.webservices.WSGetDataCluster;
import com.amalto.workbench.webservices.WSGetDataModel;
import com.amalto.workbench.webservices.WSGetMenu;
import com.amalto.workbench.webservices.WSGetRoutingRule;
import com.amalto.workbench.webservices.WSGetStoredProcedure;
import com.amalto.workbench.webservices.WSGetTransformerV2;
import com.amalto.workbench.webservices.WSGetView;
import com.amalto.workbench.webservices.WSMenu;
import com.amalto.workbench.webservices.WSMenuPK;
import com.amalto.workbench.webservices.WSRoutingRule;
import com.amalto.workbench.webservices.WSRoutingRulePK;
import com.amalto.workbench.webservices.WSStoredProcedure;
import com.amalto.workbench.webservices.WSStoredProcedurePK;
import com.amalto.workbench.webservices.WSTransformerV2;
import com.amalto.workbench.webservices.WSTransformerV2PK;
import com.amalto.workbench.webservices.WSView;
import com.amalto.workbench.webservices.WSViewPK;

// This class should be not used after Ver 4.0
@Deprecated
public class EditXObjectAction extends Action {

    private static Log log = LogFactory.getLog(EditXObjectAction.class);

    private TreeObject xobject = null;

    private IWorkbenchPage page = null;

    private IEditXObjectActionExAdapter exAdapter;

    public EditXObjectAction(TreeObject xobject, IWorkbenchPage page) {
        super();
        this.xobject = xobject;
        this.page = page;
        setDetails();
        this.exAdapter = ExAdapterManager.getAdapter(this, IEditXObjectActionExAdapter.class);
    }

    private void setDetails() {
        setImageDescriptor(ImageCache.getImage("icons/edit.gif"));//$NON-NLS-1$
        setText(Messages.EditXObjectAction_Edit);
        setToolTipText(Messages.bind(Messages.EditXObjectAction_ActionTip, IConstants.TALEND));
    }

    @Override
    public void run() {
        try {
            super.run();

            if (xobject == null || !xobject.isXObject()) {
                return;
            }

            // Access to server and get port
            TMDMService service = Util.getMDMService(new URL(xobject.getEndpointAddress()), xobject.getUsername(),
                    xobject.getPassword());

            switch (xobject.getType()) {
            case TreeObject.DATA_MODEL:
                WSDataModel wsDataModel = service.getDataModel(new WSGetDataModel((WSDataModelPK) xobject.getWsKey()));
                xobject.setWsObject(wsDataModel);
                // XSDEditorUtil.openDataModel(xobject, false);
                return;
            case TreeObject.VIEW:
                WSView wsView = service.getView(new WSGetView((WSViewPK) xobject.getWsKey()));
                xobject.setWsObject(wsView);
                break;
            case TreeObject.DATA_CLUSTER:
                WSDataCluster wsDataCluster = service.getDataCluster(new WSGetDataCluster((WSDataClusterPK) xobject.getWsKey()));
                xobject.setWsObject(wsDataCluster);
                break;
            case TreeObject.STORED_PROCEDURE:
                WSStoredProcedure wsStoredProcedure = service.getStoredProcedure(new WSGetStoredProcedure(
                        (WSStoredProcedurePK) xobject.getWsKey()));
                xobject.setWsObject(wsStoredProcedure);
                break;

            case TreeObject.ROUTING_RULE:
                WSRoutingRule wsRoutingRule = service.getRoutingRule(new WSGetRoutingRule((WSRoutingRulePK) xobject.getWsKey()));
                xobject.setWsObject(wsRoutingRule);
                break;
            case TreeObject.TRANSFORMER:
                WSTransformerV2 wsTranformer = service.getTransformerV2(new WSGetTransformerV2((WSTransformerV2PK) xobject
                        .getWsKey()));
                xobject.setWsObject(wsTranformer);
                break;
            case TreeObject.MENU:
                WSMenu wsMenu = service.getMenu(new WSGetMenu((WSMenuPK) xobject.getWsKey()));
                xobject.setWsObject(wsMenu);
                break;
            case TreeObject.SERVICE_CONFIGURATION:
                break;
            case TreeObject.JOB_REGISTRY:
                // System.out.println("JOB_REGISTRY "+ xobject.getDisplayName());
                break;
            case TreeObject.JOB:

                // System.out.println("JOB "+ xobject.getDisplayName()+" "+xobject.getWsKey());
                xobject.setWsObject(xobject.getDisplayName());
                break;

            case TreeObject.ROLE:
                if (exAdapter != null) {
                    exAdapter.run(service, xobject);
                }
                break;

            case TreeObject.RESOURCES:
            case TreeObject.CUSTOM_TYPE:
            case TreeObject.DATA_MODEL_RESOURCE:
            case TreeObject.DATA_MODEL_TYPES_RESOURCE:
            case TreeObject.CUSTOM_TYPES_RESOURCE:
            case TreeObject.PICTURES_RESOURCE:
                break;
            default:
                MessageDialog.openError(getShell(), Messages._Error,
                        Messages.bind(Messages.EditXObjectAction_ErrorMsg1, IConstants.TALEND, xobject.getType()));
                return;
            }// switch

            if (page == null) {
                this.page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
            }

            this.page.openEditor(new XObjectEditorInput(xobject, xobject.getDisplayName()),
                    "com.amalto.workbench.editors.XObjectEditor"); //$NON-NLS-1$

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (!Util.handleConnectionException(getShell(), e, Messages.EditXObjectAction_ErrorMsg2)) {
                MessageDialog.openError(getShell(), Messages._Error,
                        Messages.bind(Messages.EditXObjectAction_ErrorMsg2, e.getLocalizedMessage()));
            }
        }
    }

    private Shell getShell() {
        return Display.getDefault().getActiveShell();
    }

    @Override
    public void runWithEvent(Event event) {
        super.runWithEvent(event);
    }

}
