// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.workbench.serverexplorer.core;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.ws.WebServiceException;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Path;
import org.talend.commons.exception.LoginException;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.utils.VersionUtils;
import org.talend.core.context.RepositoryContext;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.PropertiesFactory;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.mdm.repository.model.mdmmetadata.MDMServerDef;
import org.talend.mdm.repository.model.mdmproperties.MDMServerDefItem;
import org.talend.mdm.repository.model.mdmproperties.MdmpropertiesFactory;
import org.talend.repository.model.IProxyRepositoryFactory;

import com.amalto.workbench.service.ILegendServerDefService;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.utils.XtentisException;
import com.amalto.workbench.webservices.TMDMService;
import com.amalto.workbench.webservices.WSPing;

/**
 * DOC hbhong class global comment. Detailled comment <br/>
 * 
 */
public class ServerDefService implements ILegendServerDefService {

    private static Logger log = Logger.getLogger(ServerDefService.class);

    private static Map<String, String> tmpPasswordCache = new HashMap<String, String>();

    public static void updateTempPassword(String id, String password) {
        if (id == null) {
            throw new IllegalArgumentException();
        }
        if (password != null && password.length() > 0) {
            tmpPasswordCache.put(id, password);
        } else {
            tmpPasswordCache.remove(id);
        }
    }

    public static String getTempPassword(String id) {
        if (id == null) {
            throw new IllegalArgumentException();
        }
        return tmpPasswordCache.get(id);
    }

    public static ERepositoryObjectType REPOSITORY_TYPE_SERVER_DEF = ERepositoryObjectType.valueOf(ERepositoryObjectType.class,
            "MDM.ServerDef"); //$NON-NLS-1$

    public static List<IRepositoryViewObject> getAllServerDefViewObjects() {
        return getAllServerDefViewObjects(false);
    }

    /**
     * Warning: the return result is a encrypted List
     * 
     * @return
     */
    public static List<IRepositoryViewObject> getAllServerDefViewObjects(boolean includeDisabledServer) {
        IProxyRepositoryFactory factory = CoreRuntimePlugin.getInstance().getProxyRepositoryFactory();
        try {
            List<IRepositoryViewObject> viewObjects = factory.getAll(REPOSITORY_TYPE_SERVER_DEF);
            if (viewObjects != null) {
                List<IRepositoryViewObject> serverDefViewObjs = new ArrayList<IRepositoryViewObject>();
                for (IRepositoryViewObject viewObj : viewObjects) {
                    Item item = viewObj.getProperty().getItem();
                    MDMServerDef serverDef = ((MDMServerDefItem) item).getServerDef();
                    if (serverDef != null) {
                        if (tmpPasswordCache.containsKey(viewObj.getId())) {
                            String tmpPasswd = tmpPasswordCache.get(viewObj.getId());
                            serverDef.setTempPasswd(tmpPasswd);
                        }
                    }
                    if (serverDef != null) {
                        if (includeDisabledServer || serverDef.isEnabled()) {
                            serverDefViewObjs.add(viewObj);
                        }
                    }
                }
                return serverDefViewObjs;
            }
            return null;

        } catch (PersistenceException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * Warning: the return result is a decrypted serverDef
     * 
     * @return
     */
    public static List<MDMServerDef> getAllServerDefs() {
        return getAllServerDefs(false);
    }

    public static List<MDMServerDef> getAllServerDefs(boolean includeDisabledServer) {
        List<IRepositoryViewObject> viewObjects = getAllServerDefViewObjects(includeDisabledServer);
        if (viewObjects != null) {
            List<MDMServerDef> serverDefs = new ArrayList<MDMServerDef>(viewObjects.size());
            for (IRepositoryViewObject viewObj : viewObjects) {
                Item item = viewObj.getProperty().getItem();
                MDMServerDef serverDef = ((MDMServerDefItem) item).getServerDef();
                if (serverDef != null && (includeDisabledServer || serverDef.isEnabled())) {
                    serverDefs.add(serverDef.getDecryptedServerDef());
                }
            }
            return serverDefs;
        }
        return null;
    }

    /**
     * DOC hbhong Comment method "deleteServerDef".
     * 
     * @param viewObject
     */
    public static boolean deleteServerDef(IRepositoryViewObject viewObject) {
        IProxyRepositoryFactory factory = CoreRuntimePlugin.getInstance().getProxyRepositoryFactory();

        if (viewObject != null) {
            try {
                factory.deleteObjectPhysical(viewObject);
                return true;
            } catch (PersistenceException e) {
                log.error(e.getMessage(), e);
            }
        }
        return false;

    }

    public static boolean isExistServerDefName(String name) {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        List<MDMServerDef> serverDefs = getAllServerDefs(true);
        if (serverDefs != null) {
            for (MDMServerDef def : serverDefs) {
                if (def.getName().equalsIgnoreCase(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 
     * @param name
     * @return a decrypted serverDef
     */
    public static MDMServerDef findServerDefByName(String name) {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        List<MDMServerDef> serverDefs = getAllServerDefs(true);
        if (serverDefs != null) {
            for (MDMServerDef def : serverDefs) {
                if (def.getName().equalsIgnoreCase(name)) {
                    return def;
                }
            }
        }
        return null;
    }

    public static boolean saveServeDef(MDMServerDefItem item) {
        IProxyRepositoryFactory factory = CoreRuntimePlugin.getInstance().getProxyRepositoryFactory();

        try {
            if (factory.isEditableAndLockIfPossible(item)) {
                String name = item.getServerDef().getName();
                item.getProperty().setLabel(name);
                factory.save(item, false);
                return true;
            }
        } catch (PersistenceException e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                factory.unlock(item);
            } catch (PersistenceException e) {
                log.error(e.getMessage(), e);
            } catch (LoginException e) {
                log.error(e.getMessage(), e);
            }
        }
        return false;
    }

    public static String createServerDef(MDMServerDef serverDef) {
        IProxyRepositoryFactory factory = CoreRuntimePlugin.getInstance().getProxyRepositoryFactory();
        RepositoryContext context = factory.getRepositoryContext();

        MDMServerDefItem item = MdmpropertiesFactory.eINSTANCE.createMDMServerDefItem();

        item.setServerDef(serverDef);
        Property prop = PropertiesFactory.eINSTANCE.createProperty();
        item.setProperty(prop);
        try {
            String nextId = factory.getNextId();
            Property property = item.getProperty();
            property.setId(nextId);
            property.setVersion(VersionUtils.DEFAULT_VERSION);
            property.setAuthor(context.getUser());
            property.setLabel(item.getServerDef().getName());
            factory.create(item, new Path("")); //$NON-NLS-1$
            return nextId;
        } catch (PersistenceException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * check connection
     * 
     * @param serverDef need a decrypted serverDef
     * @return
     * @throws Exception
     */
    public static void checkMDMConnection(MDMServerDef serverDef) throws MalformedURLException, XtentisException {
        checkMDMConnection(serverDef.getUrl(), serverDef.getUser(), serverDef.getPasswd());
    }

    public static void checkMDMConnection(String endpointaddress, String username, String password)
            throws MalformedURLException, XtentisException {
        try {
            TMDMService port = Util.getMDMService(new URL(endpointaddress), username, password);
            port.ping(new WSPing("ServerExplorer")); //$NON-NLS-1$
        } catch (WebServiceException e) {
            XtentisException xtentisException = Util.convertWebServiceException(e);
            if (xtentisException != null) {
                throw xtentisException;
            }
            log.error(e.getMessage(), e);
        }
    }

    public boolean checkServerDefConnection(String endpointaddress, String username, String password) {
        try {
            checkMDMConnection(endpointaddress, username, password);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.amalto.workbench.service.ILegendServerDefService#getLegendServerDefs()
     */
    public List<com.amalto.workbench.utils.MDMServerDef> getLegendServerDefs() {
        List<MDMServerDef> servers = ServerDefService.getAllServerDefs();
        List<com.amalto.workbench.utils.MDMServerDef> legendDefs = new LinkedList<com.amalto.workbench.utils.MDMServerDef>();

        if (servers != null) {
            for (MDMServerDef serverDef : servers) {
                String url = serverDef.getProtocol() + serverDef.getHost() + ":" + serverDef.getPort() //$NON-NLS-1$ 
                        + serverDef.getPath();
                com.amalto.workbench.utils.MDMServerDef legendDef = com.amalto.workbench.utils.MDMServerDef.parse(
                        serverDef.getProtocol(), url, serverDef.getUser(), serverDef.getPasswd(), serverDef.getName());
                legendDefs.add(legendDef);
            }

        }
        return legendDefs;
    }
}
