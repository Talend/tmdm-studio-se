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
package org.talend.mdm.repository.core.service.interactive;

import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.mdm.repository.core.IServerObjectRepositoryType;
import org.talend.mdm.repository.core.command.deploy.AbstractDeployCommand;
import org.talend.mdm.repository.i18n.Messages;

import com.amalto.workbench.utils.XtentisException;
import com.amalto.workbench.webservices.TMDMService;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class ServiceConfigurationInteractiveHandler extends AbstractInteractiveHandler {

    public ERepositoryObjectType getRepositoryObjectType() {
        return IServerObjectRepositoryType.TYPE_SERVICECONFIGURATION;
    }

    public String getLabel() {

        return Messages.ServiceConfigurationInteractiveHandler_label;
    }

    @Override
    public boolean doDeployWSObject(TMDMService service, Object wsObj) {
        // TODO will not invoke it
        // if (wsObj != null) {
        // WSServiceConfiguration configurations = (WSServiceConfiguration) wsObj;
        // for (WSServicePutConfiguration config : configurations.getServicePutConfigurations()) {
        //                config.setJndiName("amalto/local/service/" + config.getJndiName()); //$NON-NLS-1$
        // port.putServiceConfiguration(config);
        //                if (config.getJndiName().equalsIgnoreCase("amalto/local/service/svn")) { //$NON-NLS-1$
        // WSPutVersioningSystemConfiguration svnConfig;
        // try {
        // svnConfig = getDefaultSvn(config.getConfiguration());
        // port.putVersioningSystemConfiguration(svnConfig);
        // } catch (Exception e) {
        // throw new RemoteException(e.getMessage(), e);
        // }
        //
        // }
        // }
        // return true;
        // }
        return false;
    }

    //
    // private WSPutVersioningSystemConfiguration getDefaultSvn(String svnConfig) throws Exception {
    // Node e = Util.parse(svnConfig).getDocumentElement();
    //        String jndi = "amalto/local/service/svn"; //$NON-NLS-1$
    //
    //        String url = Util.getFirstTextNode(e, "./url");//$NON-NLS-1$
    //        String username = Util.getFirstTextNode(e, "./username");//$NON-NLS-1$
    //        String password = Util.getFirstTextNode(e, "./password");//$NON-NLS-1$
    //        String autocommit = Util.getFirstTextNode(e, "./autocommit");//$NON-NLS-1$
    // WSPutVersioningSystemConfiguration conf = new WSPutVersioningSystemConfiguration(new
    // WSVersioningSystemConfiguration(
    // ICoreConstants.DEFAULT_SVN, ICoreConstants.DEFAULT_SVN, url, username, password, autocommit, jndi));
    // return conf;
    // }

    @Override
    public boolean remove(AbstractDeployCommand cmd) throws XtentisException {
        // Not support by MDM Server
        return true;
    }

}
