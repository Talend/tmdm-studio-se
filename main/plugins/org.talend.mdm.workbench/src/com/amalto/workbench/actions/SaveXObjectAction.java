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
import org.eclipse.swt.widgets.Event;
import org.talend.mdm.commmon.util.core.CommonUtil;

import com.amalto.workbench.dialogs.ErrorExceptionDialog;
import com.amalto.workbench.dialogs.RoleAssignmentDialog;
import com.amalto.workbench.editors.XObjectEditor;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.models.IXObjectModelListener;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.models.TreeParent;
import com.amalto.workbench.providers.XObjectEditorInput;
import com.amalto.workbench.utils.IConstants;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.webservices.WSDataCluster;
import com.amalto.workbench.webservices.WSDataModel;
import com.amalto.workbench.webservices.WSMenu;
import com.amalto.workbench.webservices.WSPutDataCluster;
import com.amalto.workbench.webservices.WSPutDataModel;
import com.amalto.workbench.webservices.WSPutMenu;
import com.amalto.workbench.webservices.WSPutRole;
import com.amalto.workbench.webservices.WSPutRoutingRule;
import com.amalto.workbench.webservices.WSPutStoredProcedure;
import com.amalto.workbench.webservices.WSPutSynchronizationPlan;
import com.amalto.workbench.webservices.WSPutTransformerV2;
import com.amalto.workbench.webservices.WSPutUniverse;
import com.amalto.workbench.webservices.WSPutView;
import com.amalto.workbench.webservices.WSRole;
import com.amalto.workbench.webservices.WSRoutingRule;
import com.amalto.workbench.webservices.WSStoredProcedure;
import com.amalto.workbench.webservices.WSSynchronizationPlan;
import com.amalto.workbench.webservices.WSTransformerV2;
import com.amalto.workbench.webservices.WSUniverse;
import com.amalto.workbench.webservices.WSView;
import com.amalto.workbench.webservices.XtentisPort;

public class SaveXObjectAction extends Action {

    private static Log log = LogFactory.getLog(SaveXObjectAction.class);

    protected XObjectEditor editor = null;

    // private TreeObject initialXObject = null;
    int state = -1; // 0: success 1:failed

    public SaveXObjectAction(XObjectEditor editor) {
        super();
        this.editor = editor;
        // this.initialXObject = initialXObject;
        setImageDescriptor(ImageCache.getImage(EImage.SAVE_EDIT.getPath()));
    }

    public void run() {
        try {

            TreeObject xobject = (TreeObject) ((XObjectEditorInput) editor.getEditorInput()).getModel();
            Object newWsObject = xobject.getWsObject();

            if (!xobject.isXObject())
                return;

            // Access to server and get port
            XtentisPort port = Util.getPort(new URL(xobject.getEndpointAddress()), xobject.getUniverse(), xobject.getUsername(),
                    xobject.getPassword());

            switch (xobject.getType()) {

            case TreeObject.DATA_MODEL:
                port.putDataModel(new WSPutDataModel((WSDataModel) newWsObject));
                RoleAssignmentDialog.doSave(port, ((WSDataModel) newWsObject).getName(), "Data Model");//$NON-NLS-1$ 
                break;
            case TreeObject.VIEW:
                port.putView(new WSPutView((WSView) newWsObject));
                break;
            case TreeObject.DATA_CLUSTER:
                port.putDataCluster(new WSPutDataCluster((WSDataCluster) newWsObject));
                RoleAssignmentDialog.doSave(port, ((WSDataCluster) newWsObject).getName(), "Data Cluster");//$NON-NLS-1$ 
                break;
            case TreeObject.STORED_PROCEDURE:
                port.putStoredProcedure(new WSPutStoredProcedure((WSStoredProcedure) newWsObject));
                break;
            case TreeObject.ROLE:
                port.putRole(new WSPutRole((WSRole) newWsObject));
                break;
            case TreeObject.ROUTING_RULE:
                port.putRoutingRule(new WSPutRoutingRule((WSRoutingRule) newWsObject));
                break;
            case TreeObject.TRANSFORMER:
                port.putTransformerV2(new WSPutTransformerV2((WSTransformerV2) newWsObject));
                break;
            case TreeObject.MENU:
                port.putMenu(new WSPutMenu((WSMenu) newWsObject));
                break;
            case TreeObject.UNIVERSE:
                port.putUniverse(new WSPutUniverse((WSUniverse) newWsObject));
                break;

            case TreeObject.SYNCHRONIZATIONPLAN:
                port.putSynchronizationPlan(new WSPutSynchronizationPlan((WSSynchronizationPlan) newWsObject));
                break;
            case TreeObject.SERVICE_CONFIGURATION:
                break;
            default:
                MessageDialog.openError(this.editor.getSite().getShell(), Messages._Error,
                        Messages.bind(Messages.SaveXObjectAction_ErrorMsg, IConstants.TALEND, xobject.getType()));
                return;
            }// switch

            // notify listeners that the data has been persisted
            if (xobject.getParent() == null) {
                // add the item to the tree
                if (xobject.getType() != TreeObject.DOCUMENT) {
                    TreeParent folder = xobject.findServerFolder(xobject.getType());
                    if (folder != null)
                        folder.addChild(xobject);
                }
                xobject.fireEvent(IXObjectModelListener.SAVE, xobject.getParent(), xobject);
                // new object notify the server root that it needs a refresh (actually not needed for this but a good
                // time to do it)
                // xobject.getServerRoot().fireEvent(IXObjectModelListener.NEED_REFRESH, null, xobject.getServerRoot());
            } else {
                // existing object saved
                xobject.fireEvent(IXObjectModelListener.SAVE, xobject.getParent(), xobject);
            }
            state = 0;

        } catch (Exception e) {

            log.error(e.getMessage(), e);
            if(!Util.handleConnectionException(this.editor, e, null)){
                ErrorExceptionDialog.openError(this.editor.getSite().getShell(), Messages.ErrorTitle2,
                    CommonUtil.getErrMsgFromException(e));
            }
            state = 1;
        }
    }

    public int getState() {
        return state;
    }

    public void runWithEvent(Event event) {
        super.runWithEvent(event);
    }

    /***************************************
     * Uploading of the document and save
     * 
     ***************************************/

}
