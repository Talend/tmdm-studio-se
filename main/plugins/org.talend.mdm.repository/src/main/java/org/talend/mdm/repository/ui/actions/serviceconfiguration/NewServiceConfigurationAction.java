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
package org.talend.mdm.repository.ui.actions.serviceconfiguration;

import org.dom4j.DocumentException;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ItemState;
import org.talend.core.model.properties.PropertiesFactory;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.model.mdmproperties.MdmpropertiesFactory;
import org.talend.mdm.repository.model.mdmproperties.WSServiceConfigurationItem;
import org.talend.mdm.repository.model.mdmserverobject.MdmserverobjectFactory;
import org.talend.mdm.repository.model.mdmserverobject.WSServiceConfigurationE;
import org.talend.mdm.repository.model.mdmserverobject.WSServicePutConfigurationE;
import org.talend.mdm.repository.ui.actions.AbstractSimpleAddAction;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;

import com.amalto.workbench.utils.XmlUtil;

/**
 * DOC jsxie class global comment. Detailled comment <br/>
 *
 */
public class NewServiceConfigurationAction extends AbstractSimpleAddAction {

    public NewServiceConfigurationAction() {
        super();
    }

    @Override
    protected String getDialogTitle() {
        return Messages.NewServiceConfigurationAction_newServiceConfiguration;
    }

    private WSServiceConfigurationE newServiceConfiguration(String key) {

        WSServicePutConfigurationE putConfig1 = MdmserverobjectFactory.eINSTANCE.createWSServicePutConfigurationE();
        putConfig1.setJndiName("smtp"); //$NON-NLS-1$

        String configContent1 = "<configuration> " + "\n" + "<host>localhost</host>  " + "\n" + "<port>25</port>  " + "\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
                + "<username/>  " + "\n" + " <password/>  " + "\n" + " <from/>  " + "\n" + " <to/>  " + "\n" + " <permanentbcc/>" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
                + "\n" + " <process/>  " + "\n" + " <logfilename/> " + "\n" + " </configuration>";//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$

        putConfig1.setConfiguration(formatXml(configContent1));

        WSServicePutConfigurationE putConfig2 = MdmserverobjectFactory.eINSTANCE.createWSServicePutConfigurationE();
        putConfig2.setJndiName("svn"); //$NON-NLS-1$

        String configContent2 = "<svn-configuration><url>https://localhost:8443/svn/starkeylib</url><password>admin</password><autocommit>false</autocommit>  " + //$NON-NLS-1$
                " <username>admin</username>" + //$NON-NLS-1$
                "</svn-configuration>"; //$NON-NLS-1$

        putConfig2.setConfiguration(formatXml(configContent2));

        WSServicePutConfigurationE putConfig3 = MdmserverobjectFactory.eINSTANCE.createWSServicePutConfigurationE();
        putConfig3.setJndiName("workflow"); //$NON-NLS-1$

        String configContent3 = "  <workflow-configuration> <api-type>EJB2</api-type> </workflow-configuration>"; //$NON-NLS-1$

        putConfig3.setConfiguration(formatXml(configContent3));

        WSServiceConfigurationE serConfig = MdmserverobjectFactory.eINSTANCE.createWSServiceConfigurationE();
        serConfig.setName(key);

        serConfig.getServicePutConfigurations().add(putConfig1);
        serConfig.getServicePutConfigurations().add(putConfig2);
        serConfig.getServicePutConfigurations().add(putConfig3);

        return serConfig;
    }

    private String formatXml(String doc) {
        String formatXml = doc;
        if (formatXml != null && formatXml.length() > 0) {
            try {
                formatXml = XmlUtil.formatPretty(formatXml, "UTF-8");//$NON-NLS-1$
            } catch (DocumentException e) {
                log.error(e.getMessage(), e);
            }
        }
        return formatXml;
    }

    @Override
    protected Item createServerObject(String key) {

        WSServiceConfigurationItem item = MdmpropertiesFactory.eINSTANCE.createWSServiceConfigurationItem();
        ItemState itemState = PropertiesFactory.eINSTANCE.createItemState();
        item.setState(itemState);

        WSServiceConfigurationE serConfig = newServiceConfiguration(key);
        item.setWsServiceConfiguration(serConfig);

        if (parentItem != null) {
            item.getState().setPath(parentItem.getState().getPath());
            createItemAndSave(item, key);
        }
        return item;
    }

    protected void createItemAndSave(WSServiceConfigurationItem item, String key) {
        RepositoryResourceUtil.createItem(item, key);
    }

}
