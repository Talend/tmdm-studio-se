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
package org.talend.mdm.repository.core.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.talend.mdm.repository.core.service.wsimpl.servicedoc.AbstractGetDocument;
import org.talend.mdm.repository.core.service.wsimpl.servicedoc.CallJobGetDocument;
import org.talend.mdm.repository.core.service.wsimpl.servicedoc.CallTransformerGetDocument;
import org.talend.mdm.repository.core.service.wsimpl.servicedoc.DumpToConsoleGetDocument;
import org.talend.mdm.repository.core.service.wsimpl.servicedoc.ItemDispatcherGetDocument;
import org.talend.mdm.repository.core.service.wsimpl.servicedoc.JdbcGetDocument;
import org.talend.mdm.repository.core.service.wsimpl.servicedoc.LoggingGetDocument;
import org.talend.mdm.repository.core.service.wsimpl.servicedoc.WorkflowContextGetDocument;
import org.talend.mdm.repository.core.service.wsimpl.servicedoc.WorkflowGetDocument;
import org.talend.mdm.repository.core.service.wsimpl.transformplugin.AbstractPluginDetail;
import org.talend.mdm.repository.core.service.wsimpl.transformplugin.CodeProjectPluginDetail;
import org.talend.mdm.repository.core.service.wsimpl.transformplugin.DumpAndGoPluginDetail;
import org.talend.mdm.repository.core.service.wsimpl.transformplugin.GroovyPluginDetail;
import org.talend.mdm.repository.core.service.wsimpl.transformplugin.RegexpPluginDetail;
import org.talend.mdm.repository.core.service.wsimpl.transformplugin.ReplacePluginDetail;
import org.talend.mdm.repository.core.service.wsimpl.transformplugin.RoutePluginDetail;
import org.talend.mdm.repository.core.service.wsimpl.transformplugin.TISCallJobPluginDetail;
import org.talend.mdm.repository.core.service.wsimpl.transformplugin.WorkflowContextTriggerPluginDetail;
import org.talend.mdm.repository.core.service.wsimpl.transformplugin.WorkflowTriggerPluginDetail;
import org.talend.mdm.repository.core.service.wsimpl.transformplugin.XPathPluginDetail;
import org.talend.mdm.repository.core.service.wsimpl.transformplugin.XSLTPluginDetail;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.model.mdmmetadata.MDMServerDef;
import org.talend.mdm.webservice.TMDMService;
import org.talend.mdm.webservice.WSServiceGetDocument;
import org.talend.mdm.webservice.WSTransformerPluginV2Details;
import org.talend.mdm.workbench.serverexplorer.ui.dialogs.SelectServerDefDialog;

import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.models.TreeParent;
import com.amalto.workbench.utils.UserInfo;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.utils.XtentisException;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class RepositoryWebServiceAdapter {

    private static Logger log = Logger.getLogger(RepositoryWebServiceAdapter.class);

    private static Map<String, AbstractPluginDetail> transformerPluginMap;

    private static Map<String, AbstractGetDocument> documentServiceMap;

    public static TMDMService getMDMService(MDMServerDef serverDef) throws XtentisException {
        return getMDMService(serverDef, true);
    }

    /**
     * Warning: the param ServerDef must a decrypted serverDef
     * 
     * @param serverDef
     * @return
     * @throws XtentisException
     */
    public static TMDMService getMDMService(MDMServerDef serverDef, boolean showMissingJarDialog) throws XtentisException {

        try {
            if (serverDef == null) {
                return null;
            }

            TMDMService service = Util.getMDMService(new URL(serverDef.getUrl()), serverDef.getUser(),
                    serverDef.getPasswd(), showMissingJarDialog);

            return service;
        } catch (MalformedURLException e) {
            throw new XtentisException(Messages.bind(Messages.RepositoryWebServiceAdapter_InvalidEndpointAddress,
                    serverDef.getUrl()));
        }
    }

    public static TMDMService getMDMService(Shell shell) {
        SelectServerDefDialog dialog = new SelectServerDefDialog(shell);

        try {
            if (dialog.open() == IDialogConstants.OK_ID) {
                MDMServerDef serverDef = dialog.getSelectedServerDef();
                return RepositoryWebServiceAdapter.getMDMService(serverDef);
            }
        } catch (XtentisException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public static TMDMService getMDMService(Shell shell, MDMServerDef lastserverDef) {
        if (lastserverDef == null) {
            MessageDialog.openWarning(null, Messages.Warning_text, Messages.RepositoryWebServiceAdapter_DeployFirst);
            return null;
        }
        SelectServerDefDialog dialog = new SelectServerDefDialog(shell);
        dialog.create();
        dialog.setSelectServer(lastserverDef);
        try {
            if (dialog.open() == IDialogConstants.OK_ID) {
                MDMServerDef serverDef = dialog.getSelectedServerDef();
                TMDMService port = RepositoryWebServiceAdapter.getMDMService(serverDef);
                return port;
            }
        } catch (XtentisException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public static WSTransformerPluginV2Details findTransformerPluginV2Detail(String jndiName) {
        if (jndiName == null) {
            return null;
        }
        initTransformerPluginDetails();
        return transformerPluginMap.get(jndiName);
    }

    public static Collection<AbstractPluginDetail> findAllTransformerPluginV2Details() {
        initTransformerPluginDetails();
        return transformerPluginMap.values();
    }

    public static WSServiceGetDocument getServiceDocument(String jndiName) {
        if (jndiName == null) {
            return null;
        }
        initGetDocumentServices();
        return documentServiceMap.get(jndiName);
    }

    private static void initGetDocumentServices() {
        if (documentServiceMap == null) {
            documentServiceMap = new LinkedHashMap<String, AbstractGetDocument>();
            String twoLettersLanguageCode = "en"; //$NON-NLS-1$
            addGetDoc(documentServiceMap, new CallJobGetDocument(twoLettersLanguageCode));
            addGetDoc(documentServiceMap, new CallTransformerGetDocument(twoLettersLanguageCode));
            addGetDoc(documentServiceMap, new DumpToConsoleGetDocument(twoLettersLanguageCode));
            addGetDoc(documentServiceMap, new ItemDispatcherGetDocument(twoLettersLanguageCode));
            addGetDoc(documentServiceMap, new JdbcGetDocument(twoLettersLanguageCode));
            addGetDoc(documentServiceMap, new LoggingGetDocument(twoLettersLanguageCode));
            addGetDoc(documentServiceMap, new WorkflowGetDocument(twoLettersLanguageCode));
            addGetDoc(documentServiceMap, new WorkflowContextGetDocument(twoLettersLanguageCode));
        }
    }

    private static void initTransformerPluginDetails() {
        if (transformerPluginMap == null) {
            transformerPluginMap = new LinkedHashMap<String, AbstractPluginDetail>();
            String twoLettersLanguageCode = "en"; //$NON-NLS-1$
            addDetail(transformerPluginMap, new CodeProjectPluginDetail(twoLettersLanguageCode));
            addDetail(transformerPluginMap, new DumpAndGoPluginDetail(twoLettersLanguageCode));
            addDetail(transformerPluginMap, new GroovyPluginDetail(twoLettersLanguageCode));
            addDetail(transformerPluginMap, new RegexpPluginDetail(twoLettersLanguageCode));
            addDetail(transformerPluginMap, new ReplacePluginDetail(twoLettersLanguageCode));
            addDetail(transformerPluginMap, new RoutePluginDetail(twoLettersLanguageCode));
            addDetail(transformerPluginMap, new TISCallJobPluginDetail(twoLettersLanguageCode));
            addDetail(transformerPluginMap, new WorkflowTriggerPluginDetail(twoLettersLanguageCode));
            addDetail(transformerPluginMap, new WorkflowContextTriggerPluginDetail(twoLettersLanguageCode));
            addDetail(transformerPluginMap, new XPathPluginDetail(twoLettersLanguageCode));
            addDetail(transformerPluginMap, new XSLTPluginDetail(twoLettersLanguageCode));
        }
    }

    public static String[] getTheObjectsForRole() {
        String[] objects = new String[] { "Routing Engine V2", "Service", "Routing Rule", //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$ 
                "Background Job", "Menu", "Transformer V2", "Stored Procedure", "View", //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ 
                "Routing Order V2 Failed", "Item", "Data Model", "Routing Order V2 Completed", //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$ //$NON-NLS-4$ 
                "Transformer Plugin V2", "Role", "Data Cluster", "Configuration Info", "Custom Layout" };//$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        return objects;

    }

    public static String[] getComboListForServiceConfig() {
        String[] comblist = new String[] { "smtp", "svn", "workflow" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        return comblist;

    }

    private static void addDetail(Map<String, AbstractPluginDetail> map, AbstractPluginDetail detail) {
        map.put(detail.getJNDIName(), detail);
    }

    private static void addGetDoc(Map<String, AbstractGetDocument> map, AbstractGetDocument doc) {
        map.put(doc.getJNDIName(), doc);
    }

    public static String[] getServiceNames() {
        initGetDocumentServices();
        return documentServiceMap.keySet().toArray(new String[0]);
    }

    public static void resetXObject(MDMServerDef serverDef, TreeObject xobject) {
        String serverName = serverDef.getHost();
        String username = serverDef.getUser();
        String password = serverDef.getPasswd();
        String endpointaddress = serverDef.getProtocol() + serverDef.getHost() + ":" + serverDef.getPort() //$NON-NLS-1$ 
                + serverDef.getPath();
        TreeParent serverRoot = new TreeParent(serverName, null, TreeObject._SERVER_, endpointaddress, username
                + ":" + (password == null ? "" : password));//$NON-NLS-1$//$NON-NLS-2$
        UserInfo user = new UserInfo();
        user.setUsername(username);
        user.setPassword(password);
        user.setServerUrl(endpointaddress);

        serverRoot.setUser(user);

        xobject.setServerRoot(serverRoot);

    }
}
