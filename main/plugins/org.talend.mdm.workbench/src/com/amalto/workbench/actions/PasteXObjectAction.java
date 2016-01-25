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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Event;
import com.amalto.workbench.dialogs.MDMXSDSchemaEntryDialog;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.models.TreeParent;
import com.amalto.workbench.utils.IConstants;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.utils.WorkbenchClipboard;
import com.amalto.workbench.views.ServerView;
import com.amalto.workbench.webservices.WSBoolean;
import com.amalto.workbench.webservices.WSConceptRevisionMap;
import com.amalto.workbench.webservices.WSConceptRevisionMapMapEntry;
import com.amalto.workbench.webservices.WSDataCluster;
import com.amalto.workbench.webservices.WSDataClusterPK;
import com.amalto.workbench.webservices.WSDataModel;
import com.amalto.workbench.webservices.WSDataModelPK;
import com.amalto.workbench.webservices.WSExistsDataCluster;
import com.amalto.workbench.webservices.WSExistsDataModel;
import com.amalto.workbench.webservices.WSExistsMenu;
import com.amalto.workbench.webservices.WSExistsRole;
import com.amalto.workbench.webservices.WSExistsRoutingRule;
import com.amalto.workbench.webservices.WSExistsStoredProcedure;
import com.amalto.workbench.webservices.WSExistsSynchronizationPlan;
import com.amalto.workbench.webservices.WSExistsTransformerV2;
import com.amalto.workbench.webservices.WSExistsUniverse;
import com.amalto.workbench.webservices.WSExistsView;
import com.amalto.workbench.webservices.WSGetConceptsInDataClusterWithRevisions;
import com.amalto.workbench.webservices.WSGetDataCluster;
import com.amalto.workbench.webservices.WSGetDataModel;
import com.amalto.workbench.webservices.WSGetItem;
import com.amalto.workbench.webservices.WSGetItemPKsByCriteria;
import com.amalto.workbench.webservices.WSGetMenu;
import com.amalto.workbench.webservices.WSGetRole;
import com.amalto.workbench.webservices.WSGetRoutingRule;
import com.amalto.workbench.webservices.WSGetStoredProcedure;
import com.amalto.workbench.webservices.WSGetSynchronizationPlan;
import com.amalto.workbench.webservices.WSGetTransformerV2;
import com.amalto.workbench.webservices.WSGetUniverse;
import com.amalto.workbench.webservices.WSGetView;
import com.amalto.workbench.webservices.WSItem;
import com.amalto.workbench.webservices.WSItemPKsByCriteriaResponseResults;
import com.amalto.workbench.webservices.WSMenu;
import com.amalto.workbench.webservices.WSMenuPK;
import com.amalto.workbench.webservices.WSPutDataCluster;
import com.amalto.workbench.webservices.WSPutDataModel;
import com.amalto.workbench.webservices.WSPutItem;
import com.amalto.workbench.webservices.WSPutMenu;
import com.amalto.workbench.webservices.WSPutRole;
import com.amalto.workbench.webservices.WSPutRoutingRule;
import com.amalto.workbench.webservices.WSPutStoredProcedure;
import com.amalto.workbench.webservices.WSPutSynchronizationPlan;
import com.amalto.workbench.webservices.WSPutTransformerV2;
import com.amalto.workbench.webservices.WSPutUniverse;
import com.amalto.workbench.webservices.WSPutView;
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

public class PasteXObjectAction extends Action {

    private static Log log = LogFactory.getLog(PasteXObjectAction.class);

    private ServerView view = null;

    private XtentisPort destPort = null;

    private TreeParent parent = null;

    /*
     * public CopyXObjectAction(TreeObject xobject, IWorkbenchPage page) { super(); this.xobject = xobject; this.page =
     * page; setDetails(); }
     */

    public PasteXObjectAction(ServerView view) {
        super();
        this.view = view;
        setDetails();
    }

    private void setDetails() {
        setImageDescriptor(ImageCache.getImage(EImage.PASTE.getPath()));
        setText(Messages._Paste);
        setToolTipText(Messages.bind(Messages.PasteXObjectAction_ActionTip, IConstants.TALEND ));
    }

    public void setXtentisPort(TreeObject remoteTreeObject) {
        try {
            destPort = Util.getPort(new URL(remoteTreeObject.getEndpointAddress()), remoteTreeObject.getUniverse(),
                    remoteTreeObject.getUsername(), remoteTreeObject.getPassword());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void setParent(TreeParent p) {
        parent = p;
    }

    @Override
    public void run() {
        TreeObject selected = (TreeObject) ((IStructuredSelection) view.getViewer().getSelection()).getFirstElement();
        Map<TreeObject, Object> keyTrackMap = new HashMap<TreeObject, Object>();
        try {
            super.run();
            if (this.view == null) {
                MessageDialog.openError(view.getSite().getShell(), Messages.PasteXObjectAction_DialogTitle1,
                        Messages.PasteXObjectAction_ErrorMsg1);
                return;
            }

            if (destPort == null) {
                destPort = Util.getPort(new URL(selected.getEndpointAddress()), selected.getUniverse(), selected.getUsername(),
                        selected.getPassword());
            }

            ArrayList<TreeObject> list = WorkbenchClipboard.getWorkbenchClipboard().get();
            while (true) {
                for (Iterator<TreeObject> iter = list.iterator(); iter.hasNext();) {
                    // is an XObject necessarily
                    TreeObject xobject = iter.next();
                    Object latestValue = keyTrackMap.get(xobject);
                    switch (xobject.getType()) {
                    case TreeObject.DATA_MODEL: {
                        WSDataModelPK key = (WSDataModelPK) xobject.getWsKey();
                        WSDataModelPK newKey = latestValue != null ? (WSDataModelPK) latestValue : new WSDataModelPK(key.getPk());
                        if (destPort.existsDataModel(
                                new WSExistsDataModel(latestValue != null ? (WSDataModelPK) latestValue : (WSDataModelPK) xobject
                                        .getWsKey())).is_true()) {
                            InputDialog id = new InputDialog(
                                    view.getSite().getShell(),
                                    Messages.bind(Messages.PasteXObjectAction_DialogTitle2, key.getPk()),
                                    Messages.bind(Messages.PasteXObjectAction_MsgContent, (latestValue != null ? newKey.getPk() : key.getPk())),
                                    "CopyOf"//$NON-NLS-1$
                                            + (selected.getEndpointAddress().equals(xobject.getEndpointAddress()) ? "" : xobject//$NON-NLS-1$
                                                    .getEndpointAddress().split(":")[0] + " ") + key.getPk(),//$NON-NLS-1$//$NON-NLS-2$
                                    new IInputValidator() {

                                        public String isValid(String newText) {
                                            if ((newText == null) || "".equals(newText)) { //$NON-NLS-1$
                                                return Messages.PasteXObjectAction_NameCannotBeEmpty;
                                            }
                                            return null;
                                        };
                                    });
                            id.setBlockOnOpen(true);
                            if (id.open() == Window.CANCEL) {
                                list.remove(xobject);
                                iter = list.iterator();
                                break;
                            }
                            newKey = new WSDataModelPK(id.getValue());
                            keyTrackMap.put(xobject, newKey);
                            break;
                        }
                        // fetch the copied model
                        XtentisPort originalPort = Util.getPort(new URL(xobject.getEndpointAddress()), xobject.getUniverse(),
                                xobject.getUsername(), xobject.getPassword());
                        WSDataModel originalDataModel = originalPort.getDataModel(new WSGetDataModel(key));
                        WSDataModel newDataModel = new WSDataModel(newKey.getPk(), originalDataModel.getDescription(),
                                originalDataModel.getXsdSchema());
                        // write the new model
                        destPort.putDataModel(new WSPutDataModel(newDataModel));
                        TreeObject newObj = new TreeObject(newKey.getPk(), parent != null ? parent.getServerRoot()
                                : selected.getServerRoot(), xobject.getType(), newKey, null);
                        newTreeObject(newObj, selected);

                        list.remove(xobject);
                        iter = list.iterator();
                    }
                        break;
                    case TreeObject.VIEW: {
                        WSViewPK key = (WSViewPK) xobject.getWsKey();
                        WSViewPK newKey = latestValue != null ? (WSViewPK) latestValue : new WSViewPK(key.getPk());
                        if (destPort.existsView(
                                new WSExistsView(latestValue != null ? (WSViewPK) latestValue : (WSViewPK) xobject.getWsKey()))
                                .is_true()) {
                            InputDialog id = new InputDialog(
                                    view.getSite().getShell(),
                                    Messages.bind(Messages.PasteXObjectAction_DialogTitle2, key.getPk()),
                                    Messages.bind(Messages.PasteXObjectAction_MsgContent2, (latestValue != null ? newKey.getPk() : key.getPk())),
                                    "CopyOf"//$NON-NLS-1$
                                            + (selected.getEndpointAddress().equals(xobject.getEndpointAddress()) ? "" : xobject//$NON-NLS-1$
                                                    .getEndpointAddress().split(":")[0] + " ") + key.getPk(),//$NON-NLS-1$//$NON-NLS-2$
                                    new IInputValidator() {

                                        public String isValid(String newText) {
                                            if ((newText == null) || "".equals(newText)) { //$NON-NLS-1$
                                                return Messages.PasteXObjectAction_NameCannotBeEmpty;
                                            }
                                            return null;
                                        };
                                    });
                            id.setBlockOnOpen(true);
                            if (id.open() == Window.CANCEL) {
                                list.remove(xobject);
                                iter = list.iterator();
                                break;
                            }
                            newKey = new WSViewPK(id.getValue());
                            keyTrackMap.put(xobject, newKey);
                            break;
                        }
                        // fetch the copied model
                        XtentisPort originalPort = Util.getPort(new URL(xobject.getEndpointAddress()), xobject.getUniverse(),
                                xobject.getUsername(), xobject.getPassword());
                        WSView originalView = originalPort.getView(new WSGetView(key));
                        WSView newView = new WSView(newKey.getPk(), originalView.getDescription(),
                                originalView.getViewableBusinessElements(), originalView.getWhereConditions(),
                                originalView.getSearchableBusinessElements(), null, new WSBoolean(false)

                        );
                        // write the new model
                        destPort.putView(new WSPutView(newView));
                        TreeObject newObj = new TreeObject(newKey.getPk(), parent != null ? parent.getServerRoot()
                                : xobject.getServerRoot(), xobject.getType(), newKey, null);
                        newTreeObject(newObj, selected);

                        list.remove(xobject);
                        iter = list.iterator();
                    }
                        break;
                    case TreeObject.DATA_CLUSTER: {
                        WSDataClusterPK key = (WSDataClusterPK) xobject.getWsKey();
                        WSDataClusterPK newKey = latestValue != null ? (WSDataClusterPK) latestValue : new WSDataClusterPK(
                                key.getPk());
                        if (destPort.existsDataCluster(
                                new WSExistsDataCluster(latestValue != null ? (WSDataClusterPK) latestValue
                                        : (WSDataClusterPK) xobject.getWsKey())).is_true()) {
                            InputDialog id = new InputDialog(
                                    view.getSite().getShell(),
                                    Messages.bind(Messages.PasteXObjectAction_DialogTitle2, key.getPk()),
                                    Messages.bind(Messages.PasteXObjectAction_MsgContent3, (latestValue != null ? newKey.getPk() : key.getPk())),
                                    "CopyOf"//$NON-NLS-1$
                                            + (selected.getEndpointAddress().equals(xobject.getEndpointAddress()) ? "" : xobject//$NON-NLS-1$
                                                    .getEndpointAddress().split(":")[0] + " ") + key.getPk(),//$NON-NLS-1$//$NON-NLS-2$
                                    new IInputValidator() {

                                        public String isValid(String newText) {
                                            if ((newText == null) || "".equals(newText)) { //$NON-NLS-1$
                                                return Messages.PasteXObjectAction_NameCannotBeEmpty;
                                            }
                                            return null;
                                        };
                                    });
                            id.setBlockOnOpen(true);
                            if (id.open() == Window.CANCEL) {
                                list.remove(xobject);
                                iter = list.iterator();
                                break;
                            }
                            newKey = new WSDataClusterPK(id.getValue());
                            keyTrackMap.put(xobject, newKey);
                            break;
                        }
                        // fetch the copied model
                        XtentisPort originalPort = Util.getPort(new URL(xobject.getEndpointAddress()), xobject.getUniverse(),
                                xobject.getUsername(), xobject.getPassword());
                        WSDataCluster originalDataCluster = originalPort.getDataCluster(new WSGetDataCluster(key));
                        WSDataCluster newDataCluster = new WSDataCluster(newKey.getPk(), originalDataCluster.getDescription(),
                                originalDataCluster.getVocabulary());
                        // write the new model
                        destPort.putDataCluster(new WSPutDataCluster(newDataCluster));
                        TreeObject newObj = new TreeObject(newKey.getPk(), parent != null ? parent.getServerRoot()
                                : xobject.getServerRoot(), xobject.getType(), newKey, null);
                        newTreeObject(newObj, selected);
                        copyXObjectContent(((WSDataClusterPK) xobject.getWsKey()).getPk(), newKey.getPk(), newObj.getUniverse(),
                                TreeObject.DATA_CLUSTER);
                        list.remove(xobject);
                        iter = list.iterator();
                    }
                        break;
                    case TreeObject.STORED_PROCEDURE: {
                        WSStoredProcedurePK key = (WSStoredProcedurePK) xobject.getWsKey();
                        WSStoredProcedurePK newKey = latestValue != null ? (WSStoredProcedurePK) latestValue
                                : new WSStoredProcedurePK(key.getPk());
                        if (destPort.existsStoredProcedure(
                                new WSExistsStoredProcedure(latestValue != null ? (WSStoredProcedurePK) latestValue
                                        : (WSStoredProcedurePK) xobject.getWsKey())).is_true()) {
                            InputDialog id = new InputDialog(
                                    view.getSite().getShell(),
                                    Messages.bind(Messages.PasteXObjectAction_DialogTitle2, key.getPk()),
                                    Messages.bind(Messages.PasteXObjectAction_MsgContent4, (latestValue != null ? newKey.getPk() : key.getPk())),
                                    "CopyOf"//$NON-NLS-1$
                                            + (selected.getEndpointAddress().equals(xobject.getEndpointAddress()) ? "" : xobject//$NON-NLS-1$
                                                    .getEndpointAddress().split(":")[0] + " ") + key.getPk(),//$NON-NLS-1$//$NON-NLS-2$
                                    new IInputValidator() {

                                        public String isValid(String newText) {
                                            if ((newText == null) || "".equals(newText)) { //$NON-NLS-1$
                                                return Messages.PasteXObjectAction_NameCannotBeEmpty;
                                            }
                                            return null;
                                        };
                                    });
                            id.setBlockOnOpen(true);
                            if (id.open() == Window.CANCEL) {
                                list.remove(xobject);
                                iter = list.iterator();
                                break;
                            }
                            newKey = new WSStoredProcedurePK(id.getValue());
                            keyTrackMap.put(xobject, newKey);
                            break;
                        }
                        // fetch the copied model
                        XtentisPort originalPort = Util.getPort(new URL(xobject.getEndpointAddress()), xobject.getUniverse(),
                                xobject.getUsername(), xobject.getPassword());
                        WSStoredProcedure originalStoredProcedure = originalPort
                                .getStoredProcedure(new WSGetStoredProcedure(key));
                        WSStoredProcedure newStoredProcedure = new WSStoredProcedure(newKey.getPk(),
                                originalStoredProcedure.getDescription(), originalStoredProcedure.getProcedure(),
                                originalStoredProcedure.getRefreshCache());
                        // write the new model
                        destPort.putStoredProcedure(new WSPutStoredProcedure(newStoredProcedure));
                        TreeObject newObj = new TreeObject(newKey.getPk(), parent != null ? parent.getServerRoot()
                                : xobject.getServerRoot(), xobject.getType(), newKey, null);
                        newTreeObject(newObj, selected);

                        list.remove(xobject);
                        iter = list.iterator();
                    }
                        break;
                    case TreeObject.ROLE: {
                        WSRolePK key = (WSRolePK) xobject.getWsKey();
                        WSRolePK newKey = latestValue != null ? (WSRolePK) latestValue : new WSRolePK(key.getPk());
                        if (destPort.existsRole(
                                new WSExistsRole(latestValue != null ? (WSRolePK) latestValue : (WSRolePK) xobject.getWsKey()))
                                .is_true()) {
                            InputDialog id = new InputDialog(
                                    view.getSite().getShell(),
                                    Messages.bind(Messages.PasteXObjectAction_DialogTitle2, key.getPk()),
                                    Messages.bind(Messages.PasteXObjectAction_MsgContent5, (latestValue != null ? newKey.getPk() : key.getPk())),
                                    "CopyOf"//$NON-NLS-1$
                                            + (selected.getEndpointAddress().equals(xobject.getEndpointAddress()) ? "" : xobject//$NON-NLS-1$
                                                    .getEndpointAddress().split(":")[0] + " ") + key.getPk(),//$NON-NLS-1$//$NON-NLS-2$
                                    new IInputValidator() {

                                        public String isValid(String newText) {
                                            if ((newText == null) || "".equals(newText)) { //$NON-NLS-1$
                                                return Messages.PasteXObjectAction_NameCannotBeEmpty;
                                            }
                                            return null;
                                        };
                                    });
                            id.setBlockOnOpen(true);
                            if (id.open() == Window.CANCEL) {
                                list.remove(xobject);
                                iter = list.iterator();
                                break;
                            }
                            newKey = new WSRolePK(id.getValue());
                            keyTrackMap.put(xobject, newKey);
                            break;
                        }
                        // fetch the copied model
                        XtentisPort originalPort = Util.getPort(new URL(xobject.getEndpointAddress()), xobject.getUniverse(),
                                xobject.getUsername(), xobject.getPassword());
                        WSRole originalRole = originalPort.getRole(new WSGetRole(key));
                        WSRole newRole = new WSRole(newKey.getPk(), originalRole.getDescription(),
                                originalRole.getSpecification());
                        // write the new model
                        destPort.putRole(new WSPutRole(newRole));
                        TreeObject newObj = new TreeObject(newKey.getPk(), parent != null ? parent.getServerRoot()
                                : xobject.getServerRoot(), xobject.getType(), newKey, null);
                        newTreeObject(newObj, selected);

                        list.remove(xobject);
                        iter = list.iterator();
                    }
                        break;
                    case TreeObject.ROUTING_RULE: {
                        WSRoutingRulePK key = (WSRoutingRulePK) xobject.getWsKey();
                        WSRoutingRulePK newKey = latestValue != null ? (WSRoutingRulePK) latestValue : new WSRoutingRulePK(
                                key.getPk());
                        if (destPort.existsRoutingRule(
                                new WSExistsRoutingRule(latestValue != null ? (WSRoutingRulePK) latestValue
                                        : (WSRoutingRulePK) xobject.getWsKey())).is_true()) {
                            InputDialog id = new InputDialog(
                                    view.getSite().getShell(),
                                    Messages.bind(Messages.PasteXObjectAction_DialogTitle2, key.getPk()),
                                    Messages.bind(Messages.PasteXObjectAction_MsgContent6, (latestValue != null ? newKey.getPk() : key.getPk())),
                                    "CopyOf"//$NON-NLS-1$
                                            + (selected.getEndpointAddress().equals(xobject.getEndpointAddress()) ? "" : xobject//$NON-NLS-1$
                                                    .getEndpointAddress().split(":")[0] + " ") + key.getPk(),//$NON-NLS-1$//$NON-NLS-2$
                                    new IInputValidator() {

                                        public String isValid(String newText) {
                                            if ((newText == null) || "".equals(newText)) { //$NON-NLS-1$
                                                return Messages.PasteXObjectAction_NameCannotBeEmpty;
                                            }
                                            return null;
                                        };
                                    });
                            id.setBlockOnOpen(true);
                            if (id.open() == Window.CANCEL) {
                                list.remove(xobject);
                                iter = list.iterator();
                                break;
                            }
                            newKey = new WSRoutingRulePK(id.getValue());
                            keyTrackMap.put(xobject, newKey);
                            break;
                        }
                        // fetch the copied model
                        XtentisPort originalPort = Util.getPort(new URL(xobject.getEndpointAddress()), xobject.getUniverse(),
                                xobject.getUsername(), xobject.getPassword());
                        WSRoutingRule originalRoutingRule = originalPort.getRoutingRule(new WSGetRoutingRule(key));
                        WSRoutingRule newRoutingRule = new WSRoutingRule(newKey.getPk(), originalRoutingRule.getDescription(),
                                originalRoutingRule.isSynchronous(), originalRoutingRule.getConcept(),
                                originalRoutingRule.getServiceJNDI(), originalRoutingRule.getParameters(),
                                originalRoutingRule.getWsRoutingRuleExpressions(), originalRoutingRule.getCondition(),
                                originalRoutingRule.getDeactive());
                        // write the new model
                        destPort.putRoutingRule(new WSPutRoutingRule(newRoutingRule));
                        TreeObject newObj = new TreeObject(newKey.getPk(), parent != null ? parent.getServerRoot()
                                : xobject.getServerRoot(), xobject.getType(), newKey, null);
                        newTreeObject(newObj, selected);

                        list.remove(xobject);
                        iter = list.iterator();
                    }
                        break;
                    case TreeObject.TRANSFORMER: {
                        WSTransformerV2PK key = (WSTransformerV2PK) xobject.getWsKey();
                        WSTransformerV2PK newKey = latestValue != null ? (WSTransformerV2PK) latestValue : new WSTransformerV2PK(
                                key.getPk());
                        if (destPort.existsTransformerV2(
                                new WSExistsTransformerV2(latestValue != null ? (WSTransformerV2PK) latestValue
                                        : (WSTransformerV2PK) xobject.getWsKey())).is_true()) {
                            InputDialog id = new InputDialog(
                                    view.getSite().getShell(),
                                    Messages.bind(Messages.PasteXObjectAction_DialogTitle2, key.getPk()),
                                    Messages.bind(Messages.PasteXObjectAction_MsgContent7, (latestValue != null ? newKey.getPk() : key.getPk())),
                                    "CopyOf"//$NON-NLS-1$
                                            + (selected.getEndpointAddress().equals(xobject.getEndpointAddress()) ? "" : xobject//$NON-NLS-1$
                                                    .getEndpointAddress().split(":")[0] + " ") + key.getPk(),//$NON-NLS-1$//$NON-NLS-2$
                                    new IInputValidator() {

                                        public String isValid(String newText) {
                                            if ((newText == null) || "".equals(newText)) { //$NON-NLS-1$
                                                return Messages.PasteXObjectAction_NameCannotBeEmpty;
                                            }
                                            return null;
                                        };
                                    });
                            id.setBlockOnOpen(true);
                            if (id.open() == Window.CANCEL) {
                                list.remove(xobject);
                                iter = list.iterator();
                                break;
                            }
                            newKey = new WSTransformerV2PK(id.getValue());
                            keyTrackMap.put(xobject, newKey);
                            break;
                        }
                        // fetch the copied model
                        XtentisPort originalPort = Util.getPort(new URL(xobject.getEndpointAddress()), xobject.getUniverse(),
                                xobject.getUsername(), xobject.getPassword());
                        WSTransformerV2 originalTransformer = originalPort.getTransformerV2(new WSGetTransformerV2(key));
                        WSTransformerV2 newTransformer = new WSTransformerV2(newKey.getPk(),
                                originalTransformer.getDescription(), originalTransformer.getProcessSteps());
                        // write the new model
                        destPort.putTransformerV2(new WSPutTransformerV2(newTransformer));
                        TreeObject newObj = new TreeObject(newKey.getPk(), parent != null ? parent.getServerRoot()
                                : xobject.getServerRoot(), xobject.getType(), newKey, null);
                        newTreeObject(newObj, selected);

                        list.remove(xobject);
                        iter = list.iterator();
                    }
                        break;
                    case TreeObject.MENU: {
                        WSMenuPK key = (WSMenuPK) xobject.getWsKey();
                        WSMenuPK newKey = latestValue != null ? (WSMenuPK) latestValue : new WSMenuPK(key.getPk());
                        if (destPort.existsMenu(
                                new WSExistsMenu(latestValue != null ? (WSMenuPK) latestValue : (WSMenuPK) xobject.getWsKey()))
                                .is_true()) {
                            InputDialog id = new InputDialog(
                                    view.getSite().getShell(),
                                    Messages.bind(Messages.PasteXObjectAction_DialogTitle2, key.getPk()),
                                    Messages.bind(Messages.PasteXObjectAction_MsgContent8, (latestValue != null ? newKey.getPk() : key.getPk())),
                                    "CopyOf"//$NON-NLS-1$
                                            + (selected.getEndpointAddress().equals(xobject.getEndpointAddress()) ? "" : xobject//$NON-NLS-1$
                                                    .getEndpointAddress().split(":")[0] + " ") + key.getPk(),//$NON-NLS-1$//$NON-NLS-2$
                                    new IInputValidator() {

                                        public String isValid(String newText) {
                                            if ((newText == null) || "".equals(newText)) { //$NON-NLS-1$
                                                return Messages.PasteXObjectAction_NameCannotBeEmpty;
                                            }
                                            return null;
                                        };
                                    });
                            id.setBlockOnOpen(true);
                            if (id.open() == Window.CANCEL) {
                                list.remove(xobject);
                                iter = list.iterator();
                                break;
                            }
                            newKey = new WSMenuPK(id.getValue());
                            keyTrackMap.put(xobject, newKey);
                            break;
                        }
                        // fetch the copied model
                        XtentisPort originalPort = Util.getPort(new URL(xobject.getEndpointAddress()), xobject.getUniverse(),
                                xobject.getUsername(), xobject.getPassword());
                        WSMenu originalMenu = originalPort.getMenu(new WSGetMenu(key));
                        WSMenu newMenu = new WSMenu(newKey.getPk(), originalMenu.getDescription(), originalMenu.getMenuEntries());
                        // write the new model
                        destPort.putMenu(new WSPutMenu(newMenu));
                        TreeObject newObj = new TreeObject(newKey.getPk(), parent != null ? parent.getServerRoot()
                                : xobject.getServerRoot(), xobject.getType(), newKey, null);
                        newTreeObject(newObj, selected);

                        list.remove(xobject);
                        iter = list.iterator();
                    }
                        break;

                    case TreeObject.UNIVERSE: {
                        WSUniversePK key = (WSUniversePK) xobject.getWsKey();
                        WSUniversePK newKey = latestValue != null ? (WSUniversePK) latestValue : new WSUniversePK(key.getPk());
                        if (destPort.existsUniverse(
                                new WSExistsUniverse(latestValue != null ? (WSUniversePK) latestValue : (WSUniversePK) xobject
                                        .getWsKey())).is_true()) {
                            InputDialog id = new InputDialog(
                                    view.getSite().getShell(),
                                    Messages.bind(Messages.PasteXObjectAction_DialogTitle2, key.getPk()),
                                    Messages.bind(Messages.PasteXObjectAction_MsgContent9, (latestValue != null ? newKey.getPk() : key.getPk())),
                                    "CopyOf"//$NON-NLS-1$
                                            + (selected.getEndpointAddress().equals(xobject.getEndpointAddress()) ? "" : xobject//$NON-NLS-1$
                                                    .getEndpointAddress().split(":")[0] + " ") + key.getPk(),//$NON-NLS-1$//$NON-NLS-2$
                                    new IInputValidator() {

                                        public String isValid(String newText) {
                                            if ((newText == null) || "".equals(newText)) { //$NON-NLS-1$
                                                return Messages.PasteXObjectAction_NameCannotBeEmpty;
                                            }
                                            return null;
                                        };
                                    });
                            id.setBlockOnOpen(true);
                            if (id.open() == Window.CANCEL) {
                                list.remove(xobject);
                                iter = list.iterator();
                                break;
                            }
                            newKey = new WSUniversePK(id.getValue());
                            keyTrackMap.put(xobject, newKey);
                            break;
                        }
                        // fetch the copied model
                        XtentisPort originalPort = Util.getPort(new URL(xobject.getEndpointAddress()), xobject.getUniverse(),
                                xobject.getUsername(), xobject.getPassword());
                        WSUniverse originalUniverse = originalPort.getUniverse(new WSGetUniverse(key));
                        WSUniverse newUniverse = new WSUniverse(newKey.getPk(), originalUniverse.getDescription(),
                                originalUniverse.getXtentisObjectsRevisionIDs(), originalUniverse.getDefaultItemsRevisionID(),
                                originalUniverse.getItemsRevisionIDs());
                        // write the new model
                        destPort.putUniverse(new WSPutUniverse(newUniverse));
                        TreeObject newObj = new TreeObject(newKey.getPk(), parent != null ? parent.getServerRoot()
                                : xobject.getServerRoot(), xobject.getType(), newKey, null);
                        newTreeObject(newObj, selected);

                        list.remove(xobject);
                        iter = list.iterator();
                    }
                        break;

                    case TreeObject.SYNCHRONIZATIONPLAN: {
                        WSSynchronizationPlanPK key = (WSSynchronizationPlanPK) xobject.getWsKey();
                        WSSynchronizationPlanPK newKey = latestValue != null ? (WSSynchronizationPlanPK) latestValue
                                : new WSSynchronizationPlanPK(key.getPk());
                        if (destPort.existsSynchronizationPlan(
                                new WSExistsSynchronizationPlan(latestValue != null ? (WSSynchronizationPlanPK) latestValue
                                        : (WSSynchronizationPlanPK) xobject.getWsKey())).is_true()) {
                            InputDialog id = new InputDialog(
                                    view.getSite().getShell(),
                                    Messages.PasteXObjectAction_DialogTitle2 + key.getPk(),
                                    Messages.PasteXObjectAction_MsgContent10
                                            + (latestValue != null ? newKey.getPk() : key.getPk())
                                            + Messages.PasteXObjectAction_MsgContent10t,
                                    "CopyOf"//$NON-NLS-1$
                                            + (selected.getEndpointAddress().equals(xobject.getEndpointAddress()) ? "" : xobject//$NON-NLS-1$
                                                    .getEndpointAddress().split(":")[0] + " ") + key.getPk(),//$NON-NLS-1$//$NON-NLS-2$
                                    new IInputValidator() {

                                        public String isValid(String newText) {
                                            if ((newText == null) || "".equals(newText)) { //$NON-NLS-1$
                                                return Messages.PasteXObjectAction_NameCannotBeEmpty;
                                            }
                                            return null;
                                        };
                                    });
                            id.setBlockOnOpen(true);
                            if (id.open() == Window.CANCEL) {
                                list.remove(xobject);
                                iter = list.iterator();
                                break;
                            }
                            newKey = new WSSynchronizationPlanPK(id.getValue());
                            keyTrackMap.put(xobject, newKey);
                            break;
                        }
                        // fetch the copied model
                        XtentisPort originalPort = Util.getPort(new URL(xobject.getEndpointAddress()), xobject.getUniverse(),
                                xobject.getUsername(), xobject.getPassword());
                        WSSynchronizationPlan originalSynchronizationPlan = originalPort
                                .getSynchronizationPlan(new WSGetSynchronizationPlan(key));
                        WSSynchronizationPlan newSynchronizationPlan = new WSSynchronizationPlan(newKey.getPk(),
                                originalSynchronizationPlan.getDescription(), originalSynchronizationPlan.getRemoteSystemName(),
                                originalSynchronizationPlan.getRemoteSystemURL(),
                                originalSynchronizationPlan.getRemoteSystemUsername(),
                                originalSynchronizationPlan.getRemoteSystemPassword(), originalSynchronizationPlan.getTisURL(),
                                originalSynchronizationPlan.getTisUsername(), originalSynchronizationPlan.getTisPassword(),
                                originalSynchronizationPlan.getTisParameters(),
                                originalSynchronizationPlan.getXtentisObjectsSynchronizations(),
                                originalSynchronizationPlan.getItemsSynchronizations());
                        // write the new model
                        destPort.putSynchronizationPlan(new WSPutSynchronizationPlan(newSynchronizationPlan));
                        TreeObject newObj = new TreeObject(newKey.getPk(), parent != null ? parent.getServerRoot()
                                : xobject.getServerRoot(), xobject.getType(), newKey, null);
                        newTreeObject(newObj, selected);

                        list.remove(xobject);
                        iter = list.iterator();
                    }
                        break;

                    default:

                    }// switch

                }

                if (list.isEmpty()) {
                    break;
                }
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if(!Util.handleConnectionException(view, e, null)){
                MessageDialog.openError(view.getSite().getShell(), Messages._Error,
                    Messages.PasteXObjectAction_ErrorMsg + e.getLocalizedMessage());
            }
        } finally {
            keyTrackMap.clear();
            // refresh view
            // view.forceAllSiteToRefresh();
            destPort = null;
            parent = null;
        }
    }

    private void newTreeObject(TreeObject newObj, TreeObject selected) {
        if (parent != null) {
            parent.addChild(newObj);
        } else {
            if (selected instanceof TreeParent) {
                if (((TreeParent) selected).findObject(newObj.getType(), newObj.getDisplayName()) == null) {
                    ((TreeParent) selected).addChild(newObj);
                }
            } else {
                if (selected.getParent().findObject(newObj.getType(), newObj.getDisplayName()) == null) {
                    selected.getParent().addChild(newObj);
                }
            }
        }
    }

    private void copyXObjectContent(String oldXObjectPk, String newXObjectPk, String revisionID, int xobjectType) {
        switch (xobjectType) {
        case TreeObject.DATA_CLUSTER:
            try {
                WSItemPKsByCriteriaResponseResults[] results = destPort.getItemPKsByCriteria(
                        new WSGetItemPKsByCriteria(new WSDataClusterPK(oldXObjectPk), null, null, null, (long) -1, (long) -1, 0,
                                Integer.MAX_VALUE)).getResults();

                List<String> conceptList = new ArrayList<String>();
                WSConceptRevisionMap concepts = destPort
                        .getConceptsInDataClusterWithRevisions(new WSGetConceptsInDataClusterWithRevisions(new WSDataClusterPK(
                                oldXObjectPk), new WSUniversePK(revisionID)));
                if (concepts != null) {
                    WSConceptRevisionMapMapEntry[] wsConceptRevisionMapMapEntries = concepts.getMapEntry();

                    for (WSConceptRevisionMapMapEntry entry : wsConceptRevisionMapMapEntries) {
                        conceptList.add(entry.getConcept());
                    }
                }

                MDMXSDSchemaEntryDialog dlg = new MDMXSDSchemaEntryDialog(this.view.getSite().getShell(),
                        Messages.PasteXObjectAction_DialogTitle3);
                dlg.create();
                dlg.retrieveDataModels(conceptList, true);
                dlg.setOKButton(true);
                dlg.setBlockOnOpen(true);
                dlg.open();

                if (dlg.getReturnCode() == Window.OK) {
                    for (WSItemPKsByCriteriaResponseResults result : results) {
                        if (dlg.getMDMDataModelUrls().contains(result.getWsItemPK().getConceptName())) {
                        	WSItem item=destPort.getItem(new WSGetItem(result.getWsItemPK()));
                        	//item.setWsDataClusterPK(new WSDataClusterPK(newXObjectPk));
//                            WSSynchronizationGetItemXML getItemXML = new WSSynchronizationGetItemXML(revisionID,
//                                    result.getWsItemPK());
//                            WSString xmlForm = destPort.synchronizationGetItemXML(getItemXML);
//                            Document doc = Util.parse(xmlForm.getValue());
//                            NodeList clusterNameList = Util.getNodeList(doc, "/ii/c");//$NON-NLS-1$
//                            for (int i = 0; i < clusterNameList.getLength(); i++) {
//                                Node node = clusterNameList.item(i);
//                                node.setTextContent(newXObjectPk);
//                            }
//
//                            WSSynchronizationPutItemXML putItemXML = new WSSynchronizationPutItemXML(revisionID,
//                                    Util.nodeToString(doc));
//                            destPort.synchronizationPutItemXML(putItemXML);
                        	destPort.putItem(new WSPutItem(new WSDataClusterPK(newXObjectPk), item.getContent(), new WSDataModelPK(item.getDataModelName()), false));
                        }
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }

            break;
        default:
        }

    }

    @Override
    public void runWithEvent(Event event) {
        super.runWithEvent(event);
    }

}
