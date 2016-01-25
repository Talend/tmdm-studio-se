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
package com.amalto.workbench.utils;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

import com.amalto.workbench.availablemodel.AvailableModelUtil;
import com.amalto.workbench.availablemodel.IAvailableModel;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.models.TreeParent;
import com.amalto.workbench.views.ServerView;
import com.amalto.workbench.webservices.WSBoolean;
import com.amalto.workbench.webservices.WSDataCluster;
import com.amalto.workbench.webservices.WSDataClusterPK;
import com.amalto.workbench.webservices.WSDataModel;
import com.amalto.workbench.webservices.WSDataModelPK;
import com.amalto.workbench.webservices.WSDeleteDataCluster;
import com.amalto.workbench.webservices.WSDeleteDataModel;
import com.amalto.workbench.webservices.WSDeleteMenu;
import com.amalto.workbench.webservices.WSDeleteRoutingRule;
import com.amalto.workbench.webservices.WSDeleteStoredProcedure;
import com.amalto.workbench.webservices.WSDeleteTransformerV2;
import com.amalto.workbench.webservices.WSDeleteView;
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
import com.amalto.workbench.webservices.WSGetCurrentUniverse;
import com.amalto.workbench.webservices.WSGetDataCluster;
import com.amalto.workbench.webservices.WSGetDataModel;
import com.amalto.workbench.webservices.WSGetMenu;
import com.amalto.workbench.webservices.WSGetRole;
import com.amalto.workbench.webservices.WSGetRolePKs;
import com.amalto.workbench.webservices.WSGetRoutingRule;
import com.amalto.workbench.webservices.WSGetStoredProcedure;
import com.amalto.workbench.webservices.WSGetSynchronizationPlan;
import com.amalto.workbench.webservices.WSGetTransformerV2;
import com.amalto.workbench.webservices.WSGetUniverse;
import com.amalto.workbench.webservices.WSGetView;
import com.amalto.workbench.webservices.WSMenu;
import com.amalto.workbench.webservices.WSMenuPK;
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
import com.amalto.workbench.webservices.WSRolePK;
import com.amalto.workbench.webservices.WSRoleSpecification;
import com.amalto.workbench.webservices.WSRoleSpecificationInstance;
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
import com.amalto.workbench.webservices.WSUniverseXtentisObjectsRevisionIDs;
import com.amalto.workbench.webservices.WSView;
import com.amalto.workbench.webservices.WSViewPK;
import com.amalto.workbench.webservices.XtentisPort;

public class TreeObjectUtil {

    private static Log log = LogFactory.getLog(TreeObjectUtil.class);

    public static boolean renameTreeOjects(TreeObject object, ServerView view) throws Exception {

        TreeParent parent = object instanceof TreeParent ? (TreeParent) object : object.getParent();
        final XtentisPort destPort = Util.getPort(new URL(object.getEndpointAddress()), object.getUniverse(),
                object.getUsername(), object.getPassword());

        // is an XObject necessarily
        // TreeObject object = iter.next();
        switch (object.getType()) {
        case TreeObject.DATA_MODEL: {
            WSDataModelPK key = (WSDataModelPK) object.getWsKey();
            WSDataModelPK newKey = new WSDataModelPK(key.getPk());
            if (destPort.existsDataModel(new WSExistsDataModel((WSDataModelPK) object.getWsKey())).is_true()) {
                String newName = getNewName(view.getSite().getShell(), key.getPk(), destPort, object.getType());
                if (newName == null)
                    return false;
                newKey = new WSDataModelPK(newName);
            }
            // fetch the copied model
            XtentisPort originalPort = Util.getPort(new URL(object.getEndpointAddress()), object.getUniverse(),
                    object.getUsername(), object.getPassword());
            WSDataModel originalDataModel = originalPort.getDataModel(new WSGetDataModel(key));
            WSDataModel newDataModel = new WSDataModel(newKey.getPk(), originalDataModel.getDescription(),
                    originalDataModel.getXsdSchema());
            // write the new model
            destPort.putDataModel(new WSPutDataModel(newDataModel));
            TreeObject newObj = new TreeObject(newKey.getPk(), parent != null ? parent.getServerRoot() : object.getServerRoot(),
                    object.getType(), newKey, null);
            newTreeObject(newObj, parent);

        }
            break;
        case TreeObject.VIEW: {
            WSViewPK key = (WSViewPK) object.getWsKey();
            WSViewPK newKey = new WSViewPK(key.getPk());
            if (destPort.existsView(new WSExistsView((WSViewPK) object.getWsKey())).is_true()) {
                String newName = getNewName(view.getSite().getShell(), key.getPk(), destPort, object.getType());
                if (newName == null)
                    return false;
                newKey = new WSViewPK(newName);
            }
            // fetch the copied model
            XtentisPort originalPort = Util.getPort(new URL(object.getEndpointAddress()), object.getUniverse(),
                    object.getUsername(), object.getPassword());
            WSView originalView = originalPort.getView(new WSGetView(key));
            WSView newView = new WSView(newKey.getPk(), originalView.getDescription(),
                    originalView.getViewableBusinessElements(), originalView.getWhereConditions(),
                    originalView.getSearchableBusinessElements(), null, new WSBoolean(false));
            // write the new model
            destPort.putView(new WSPutView(newView));
            TreeObject newObj = new TreeObject(newKey.getPk(), parent != null ? parent.getServerRoot() : object.getServerRoot(),
                    object.getType(), newKey, null);
            newTreeObject(newObj, parent);

        }
            break;
        case TreeObject.DATA_CLUSTER: {
            WSDataClusterPK key = (WSDataClusterPK) object.getWsKey();
            WSDataClusterPK newKey = new WSDataClusterPK(key.getPk());
            if (destPort.existsDataCluster(new WSExistsDataCluster((WSDataClusterPK) object.getWsKey())).is_true()) {
                String newName = getNewName(view.getSite().getShell(), key.getPk(), destPort, object.getType());
                if (newName == null)
                    return false;
                newKey = new WSDataClusterPK(newName);
            }
            // fetch the copied model
            XtentisPort originalPort = Util.getPort(new URL(object.getEndpointAddress()), object.getUniverse(),
                    object.getUsername(), object.getPassword());
            WSDataCluster originalDataCluster = originalPort.getDataCluster(new WSGetDataCluster(key));
            WSDataCluster newDataCluster = new WSDataCluster(newKey.getPk(), originalDataCluster.getDescription(),
                    originalDataCluster.getVocabulary());
            // write the new model
            destPort.putDataCluster(new WSPutDataCluster(newDataCluster));
            TreeObject newObj = new TreeObject(newKey.getPk(), parent != null ? parent.getServerRoot() : object.getServerRoot(),
                    object.getType(), newKey, null);
            newTreeObject(newObj, parent);

        }
            break;
        case TreeObject.STORED_PROCEDURE: {
            WSStoredProcedurePK key = (WSStoredProcedurePK) object.getWsKey();
            WSStoredProcedurePK newKey = new WSStoredProcedurePK(key.getPk());
            if (destPort.existsStoredProcedure(new WSExistsStoredProcedure((WSStoredProcedurePK) object.getWsKey())).is_true()) {
                String newName = getNewName(view.getSite().getShell(), key.getPk(), destPort, object.getType());
                if (newName == null)
                    return false;
                newKey = new WSStoredProcedurePK(newName);
            }
            // fetch the copied model
            XtentisPort originalPort = Util.getPort(new URL(object.getEndpointAddress()), object.getUniverse(),
                    object.getUsername(), object.getPassword());
            WSStoredProcedure originalStoredProcedure = originalPort.getStoredProcedure(new WSGetStoredProcedure(key));
            WSStoredProcedure newStoredProcedure = new WSStoredProcedure(newKey.getPk(),
                    originalStoredProcedure.getDescription(), originalStoredProcedure.getProcedure(),
                    originalStoredProcedure.getRefreshCache());
            // write the new model
            destPort.putStoredProcedure(new WSPutStoredProcedure(newStoredProcedure));
            TreeObject newObj = new TreeObject(newKey.getPk(), parent != null ? parent.getServerRoot() : object.getServerRoot(),
                    object.getType(), newKey, null);
            newTreeObject(newObj, parent);

        }
            break;
        case TreeObject.ROLE: {
            WSRolePK key = (WSRolePK) object.getWsKey();
            WSRolePK newKey = new WSRolePK(key.getPk());
            if (destPort.existsRole(new WSExistsRole((WSRolePK) object.getWsKey())).is_true()) {
                String newName = getNewName(view.getSite().getShell(), key.getPk(), destPort, object.getType());
                if (newName == null)
                    return false;
                newKey = new WSRolePK(newName);
            }
            // fetch the copied model
            XtentisPort originalPort = Util.getPort(new URL(object.getEndpointAddress()), object.getUniverse(),
                    object.getUsername(), object.getPassword());
            WSRole originalRole = originalPort.getRole(new WSGetRole(key));
            WSRole newRole = new WSRole(newKey.getPk(), originalRole.getDescription(), originalRole.getSpecification());
            // write the new model
            destPort.putRole(new WSPutRole(newRole));
            TreeObject newObj = new TreeObject(newKey.getPk(), parent != null ? parent.getServerRoot() : object.getServerRoot(),
                    object.getType(), newKey, null);
            newTreeObject(newObj, parent);

        }
            break;
        case TreeObject.ROUTING_RULE: {
            WSRoutingRulePK key = (WSRoutingRulePK) object.getWsKey();
            WSRoutingRulePK newKey = new WSRoutingRulePK(key.getPk());
            if (destPort.existsRoutingRule(new WSExistsRoutingRule((WSRoutingRulePK) object.getWsKey())).is_true()) {
                String newName = getNewName(view.getSite().getShell(), key.getPk(), destPort, object.getType());
                if (newName == null)
                    return false;
                newKey = new WSRoutingRulePK(newName);
            }
            // fetch the copied model
            XtentisPort originalPort = Util.getPort(new URL(object.getEndpointAddress()), object.getUniverse(),
                    object.getUsername(), object.getPassword());
            WSRoutingRule originalRoutingRule = originalPort.getRoutingRule(new WSGetRoutingRule(key));
            WSRoutingRule newRoutingRule = new WSRoutingRule(newKey.getPk(), originalRoutingRule.getDescription(),
                    originalRoutingRule.isSynchronous(), originalRoutingRule.getConcept(), originalRoutingRule.getServiceJNDI(),
                    originalRoutingRule.getParameters(), originalRoutingRule.getWsRoutingRuleExpressions(),
                    originalRoutingRule.getCondition(), originalRoutingRule.getDeactive());
            // write the new model
            destPort.putRoutingRule(new WSPutRoutingRule(newRoutingRule));
            TreeObject newObj = new TreeObject(newKey.getPk(), parent != null ? parent.getServerRoot() : object.getServerRoot(),
                    object.getType(), newKey, null);
            newTreeObject(newObj, parent);

        }
            break;
        case TreeObject.TRANSFORMER: {
            WSTransformerV2PK key = (WSTransformerV2PK) object.getWsKey();
            WSTransformerV2PK newKey = new WSTransformerV2PK(key.getPk());
            if (destPort.existsTransformerV2(new WSExistsTransformerV2((WSTransformerV2PK) object.getWsKey())).is_true()) {
                String newName = getNewName(view.getSite().getShell(), key.getPk(), destPort, object.getType());
                if (newName == null)
                    return false;
                newKey = new WSTransformerV2PK(newName);
            }
            // fetch the copied model
            XtentisPort originalPort = Util.getPort(new URL(object.getEndpointAddress()), object.getUniverse(),
                    object.getUsername(), object.getPassword());
            WSTransformerV2 originalTransformer = originalPort.getTransformerV2(new WSGetTransformerV2(key));
            WSTransformerV2 newTransformer = new WSTransformerV2(newKey.getPk(), originalTransformer.getDescription(),
                    originalTransformer.getProcessSteps());
            // write the new model
            destPort.putTransformerV2(new WSPutTransformerV2(newTransformer));
            TreeObject newObj = new TreeObject(newKey.getPk(), parent != null ? parent.getServerRoot() : object.getServerRoot(),
                    object.getType(), newKey, null);
            newTreeObject(newObj, parent);

        }
            break;
        case TreeObject.MENU: {
            WSMenuPK key = (WSMenuPK) object.getWsKey();
            WSMenuPK newKey = new WSMenuPK(key.getPk());
            if (destPort.existsMenu(new WSExistsMenu((WSMenuPK) object.getWsKey())).is_true()) {
                String newName = getNewName(view.getSite().getShell(), key.getPk(), destPort, object.getType());
                if (newName == null)
                    return false;
                newKey = new WSMenuPK(newName);
            }
            // fetch the copied model
            XtentisPort originalPort = Util.getPort(new URL(object.getEndpointAddress()), object.getUniverse(),
                    object.getUsername(), object.getPassword());
            WSMenu originalMenu = originalPort.getMenu(new WSGetMenu(key));
            WSMenu newMenu = new WSMenu(newKey.getPk(), originalMenu.getDescription(), originalMenu.getMenuEntries());
            // write the new model
            destPort.putMenu(new WSPutMenu(newMenu));
            TreeObject newObj = new TreeObject(newKey.getPk(), parent != null ? parent.getServerRoot() : object.getServerRoot(),
                    object.getType(), newKey, null);
            newTreeObject(newObj, parent);
        }
            break;

        case TreeObject.UNIVERSE: {
            WSUniversePK key = (WSUniversePK) object.getWsKey();
            WSUniversePK newKey = new WSUniversePK(key.getPk());
            if (destPort.existsUniverse(new WSExistsUniverse((WSUniversePK) object.getWsKey())).is_true()) {
                String newName = getNewName(view.getSite().getShell(), key.getPk(), destPort, object.getType());
                if (newName == null)
                    return false;
                newKey = new WSUniversePK(newName);
            }
            // fetch the copied model
            XtentisPort originalPort = Util.getPort(new URL(object.getEndpointAddress()), object.getUniverse(),
                    object.getUsername(), object.getPassword());
            WSUniverse originalUniverse = originalPort.getUniverse(new WSGetUniverse(key));
            WSUniverse newUniverse = new WSUniverse(newKey.getPk(), originalUniverse.getDescription(),
                    originalUniverse.getXtentisObjectsRevisionIDs(), originalUniverse.getDefaultItemsRevisionID(),
                    originalUniverse.getItemsRevisionIDs());
            // write the new model
            destPort.putUniverse(new WSPutUniverse(newUniverse));
            TreeObject newObj = new TreeObject(newKey.getPk(), parent != null ? parent.getServerRoot() : object.getServerRoot(),
                    object.getType(), newKey, null);
            newTreeObject(newObj, parent);

        }
            break;

        case TreeObject.SYNCHRONIZATIONPLAN: {
            WSSynchronizationPlanPK key = (WSSynchronizationPlanPK) object.getWsKey();
            WSSynchronizationPlanPK newKey = new WSSynchronizationPlanPK(key.getPk());
            if (destPort.existsSynchronizationPlan(new WSExistsSynchronizationPlan((WSSynchronizationPlanPK) object.getWsKey()))
                    .is_true()) {
                String newName = getNewName(view.getSite().getShell(), key.getPk(), destPort, object.getType());
                if (newName == null)
                    return false;
                newKey = new WSSynchronizationPlanPK(newName);
            }
            // fetch the copied model
            XtentisPort originalPort = Util.getPort(new URL(object.getEndpointAddress()), object.getUniverse(),
                    object.getUsername(), object.getPassword());
            WSSynchronizationPlan originalSynchronizationPlan = originalPort.getSynchronizationPlan(new WSGetSynchronizationPlan(
                    key));
            WSSynchronizationPlan newSynchronizationPlan = new WSSynchronizationPlan(newKey.getPk(),
                    originalSynchronizationPlan.getDescription(), originalSynchronizationPlan.getRemoteSystemName(),
                    originalSynchronizationPlan.getRemoteSystemURL(), originalSynchronizationPlan.getRemoteSystemUsername(),
                    originalSynchronizationPlan.getRemoteSystemPassword(), originalSynchronizationPlan.getTisURL(),
                    originalSynchronizationPlan.getTisUsername(), originalSynchronizationPlan.getTisPassword(),
                    originalSynchronizationPlan.getTisParameters(),
                    originalSynchronizationPlan.getXtentisObjectsSynchronizations(),
                    originalSynchronizationPlan.getItemsSynchronizations());
            // write the new model
            destPort.putSynchronizationPlan(new WSPutSynchronizationPlan(newSynchronizationPlan));
            TreeObject newObj = new TreeObject(newKey.getPk(), parent != null ? parent.getServerRoot() : object.getServerRoot(),
                    object.getType(), newKey, null);
            newTreeObject(newObj, parent);

        }
            break;

        default:

        }// switch
        return true;

    }

    private static String getNewName(Shell shell, String displayName, final XtentisPort destPort, final int type) {
        InputDialog id;
        id = new InputDialog(shell, Messages.TreeObjectUtil_Rename, Messages.TreeObjectUtil_PleaseEnterNewName, displayName, new IInputValidator() {

            public String isValid(String newText) {
                if ((newText == null) || "".equals(newText))//$NON-NLS-1$
                    return Messages.TreeObjectUtil_NameCannotbeEmpty;

                if (Pattern.compile("^\\s+\\w+\\s*").matcher(newText).matches()//$NON-NLS-1$
                        || newText.trim().replaceAll("\\s", "").length() != newText.trim().length())//$NON-NLS-1$//$NON-NLS-2$
                    return Messages.TreeObjectUtil_NameCannotContainEmpty;

                newText = newText.trim();
                try {
                    if ((type == TreeObject.DATA_MODEL && destPort.existsDataModel(
                            new WSExistsDataModel(new WSDataModelPK(newText))).is_true())
                            || (type == TreeObject.VIEW && destPort.existsView(new WSExistsView(new WSViewPK(newText))).is_true())
                            || (type == TreeObject.DATA_CLUSTER && destPort.existsDataCluster(
                                    new WSExistsDataCluster(new WSDataClusterPK(newText))).is_true())
                            || (type == TreeObject.STORED_PROCEDURE && destPort.existsStoredProcedure(
                                    new WSExistsStoredProcedure(new WSStoredProcedurePK(newText))).is_true())
                            || (type == TreeObject.ROUTING_RULE && destPort.existsRoutingRule(
                                    new WSExistsRoutingRule(new WSRoutingRulePK(newText))).is_true())
                            || (type == TreeObject.TRANSFORMER && destPort.existsTransformerV2(
                                    new WSExistsTransformerV2(new WSTransformerV2PK(newText))).is_true())
                            || (type == TreeObject.MENU && destPort.existsMenu(new WSExistsMenu(new WSMenuPK(newText))).is_true()))
                        return Messages.TreeObjectUtil_NameAlreadyExist;
                    if (Util.IsEnterPrise()
                            && ((type == TreeObject.ROLE && destPort.existsRole(new WSExistsRole(new WSRolePK(newText)))
                                    .is_true())
                                    || (type == TreeObject.UNIVERSE && destPort.existsUniverse(
                                            new WSExistsUniverse(new WSUniversePK(newText))).is_true()) || (type == TreeObject.SYNCHRONIZATIONPLAN && destPort
                                    .existsSynchronizationPlan(
                                            new WSExistsSynchronizationPlan(new WSSynchronizationPlanPK(newText))).is_true())))
                        return Messages.TreeObjectUtil_NameAlreadyExist;
                } catch (RemoteException e) {
                    log.error(e.getMessage(), e);
                }
                return null;
            };
        });
        id.setBlockOnOpen(true);
        if (id.open() == Window.OK)
            return id.getValue().trim();
        else
            return null;

    }

    public static void deleteTreeObject(TreeObject xobject, ServerView view) throws Exception {

        // Access to server and get port
        XtentisPort port = Util.getPort(new URL(xobject.getEndpointAddress()), xobject.getUniverse(), xobject.getUsername(),
                xobject.getPassword());
        // available models
        java.util.List<IAvailableModel> availablemodels = AvailableModelUtil.getAvailableModels();
        for (IAvailableModel model : availablemodels) {
            model.deleteTreeObject(port, xobject);
        }
        String displayName = xobject.getDisplayName();
        switch (xobject.getType()) {
        // replace the object types with the name which is got from the EXtentisObjects.
        case TreeObject.DATA_MODEL:
            port.deleteDataModel(new WSDeleteDataModel((WSDataModelPK) xobject.getWsKey()));
            deleteSpecificationFromAttachedRole(port, displayName, EXtentisObjects.DataMODEL.getName());
            break;
        case TreeObject.VIEW:
            port.deleteView(new WSDeleteView((WSViewPK) xobject.getWsKey()));
            deleteSpecificationFromAttachedRole(port, displayName, EXtentisObjects.View.getName());
            break;
        case TreeObject.DATA_CLUSTER:
            port.deleteDataCluster(new WSDeleteDataCluster((WSDataClusterPK) xobject.getWsKey()));
            deleteSpecificationFromAttachedRole(port, displayName, EXtentisObjects.DataCluster.getName());
            break;
        case TreeObject.STORED_PROCEDURE:
            port.deleteStoredProcedure(new WSDeleteStoredProcedure((WSStoredProcedurePK) xobject.getWsKey()));
            break;
        case TreeObject.ROUTING_RULE:
            port.deleteRoutingRule(new WSDeleteRoutingRule((WSRoutingRulePK) xobject.getWsKey()));
            deleteSpecificationFromAttachedRole(port, displayName, EXtentisObjects.RoutingRule.getName());
            break;
        case TreeObject.TRANSFORMER:
            port.deleteTransformerV2(new WSDeleteTransformerV2((WSTransformerV2PK) xobject.getWsKey()));
            deleteSpecificationFromAttachedRole(port, displayName, EXtentisObjects.Transformer.getName());
            break;
        case TreeObject.MENU:
            port.deleteMenu(new WSDeleteMenu((WSMenuPK) xobject.getWsKey()));
            deleteSpecificationFromAttachedRole(port, displayName, EXtentisObjects.Menu.getName());
            break;
        case TreeObject.CATEGORY_FOLDER:
            // do nothing over here
            break;
        case TreeObject.ROLE:
            deleteSpecificationFromAttachedRole(port, displayName, EXtentisObjects.Role.getName());
            break;
        case TreeObject.SYNCHRONIZATIONPLAN:
            deleteSpecificationFromAttachedRole(port, displayName, EXtentisObjects.SynchronizationPlan.getName());
            break;
        case TreeObject.UNIVERSE:
            deleteSpecificationFromAttachedRole(port, displayName, EXtentisObjects.Universe.getName());
            break;
        default:
            // MessageDialog.openError(view.getSite().getShell(), "Error",
            // "Unknown "+IConstants.TALEND+" Object Type: "+xobject.getType());
            return;
        }// switch

        if (xobject.getParent() != null) {
            LocalTreeObjectRepository.getInstance().switchOffSynchronize();
            xobject.getParent().removeChild(xobject);
            LocalTreeObjectRepository.getInstance().swithOnSynchronize();
        }
        view.getViewer().refresh();
    }

    public static void deleteSpecificationFromAttachedRole(XtentisPort port, String displayName, String objectType)
            throws RemoteException {
        if (Util.IsEnterPrise()) {
            String revision = retrieveRevisionID(port, objectType);
            WSRolePK[] pks = port.getRolePKs(new WSGetRolePKs(".*")).getWsRolePK();//$NON-NLS-1$
            if (pks == null)
                return;
            for (WSRolePK pk : pks) {
                WSGetRole getRole = new WSGetRole();
                getRole.setWsRolePK(new WSRolePK(pk.getPk()));
                WSRole role = port.getRole(getRole);
                for (WSRoleSpecification spec : role.getSpecification()) {
                    if (spec.getObjectType().equals(objectType)) {
                        WSRoleSpecificationInstance[] specInstance = spec.getInstance();
                        List<WSRoleSpecificationInstance> newSpecInstanceLst = new ArrayList<WSRoleSpecificationInstance>();
                        for (WSRoleSpecificationInstance specIns : specInstance) {
                            if (!specIns.getInstanceName().equals(displayName)) {
                                newSpecInstanceLst.add(specIns);
                            }
                        }
                        if (newSpecInstanceLst.size() < specInstance.length) {
                            String revisionForRole = retrieveRevisionID(port, "Role");//$NON-NLS-1$
                            if (revisionForRole == null || revision == null || !revisionForRole.equals(revision))
                                break;
                            spec.setInstance(newSpecInstanceLst.toArray(new WSRoleSpecificationInstance[] {}));
                            WSPutRole putRole = new WSPutRole();
                            putRole.setWsRole(role);
                            port.putRole(putRole);
                            break;
                        }
                    }
                }
            }

        }
    }

    /**
     * add by fliu, please see bug 0009262
     * 
     * @param port
     * @param xtentisName
     * @return
     * @throws RemoteException
     */
    private static String retrieveRevisionID(XtentisPort port, String xtentisName) throws RemoteException {
        WSUniverse wUuniverse = port.getCurrentUniverse(new WSGetCurrentUniverse());
        WSUniverseXtentisObjectsRevisionIDs[] ids = wUuniverse.getXtentisObjectsRevisionIDs();
        for (WSUniverseXtentisObjectsRevisionIDs id : ids) {
            if (id.getXtentisObjectName().equals(xtentisName) && Util.IsEnterPrise()) {
                return id.getRevisionID().replaceAll("\\[", "").replaceAll("\\]", "");//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$
            }
        }
        if (wUuniverse.getName().equals("[HEAD]"))//$NON-NLS-1$
            return wUuniverse.getName();
        return null;
    }

    private static void newTreeObject(TreeObject newObj, TreeParent parent) {
        if (parent != null)
            parent.addChild(newObj);
        // else
        // {
        // if (selected instanceof TreeParent)
        // {
        // if (((TreeParent)selected).findObject(newObj.getType(), newObj.getDisplayName()) == null)
        // {
        // ((TreeParent)selected).addChild(newObj);
        // }
        // }
        // else
        // {
        // if(selected.getParent().findObject(newObj.getType(), newObj.getDisplayName()) == null)
        // {
        // selected.getParent().addChild(newObj);
        // }
        // }
        // }
    }
}
