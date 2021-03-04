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
package org.talend.mdm.repository.ui.actions.trigger;

import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.ui.IWorkbenchPartSite;
import org.talend.core.GlobalServiceRegister;
import org.talend.mdm.repository.core.AbstractRepositoryAction;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.model.mdmmetadata.MDMServerDef;
import org.talend.mdm.workbench.serverexplorer.ui.dialogs.SelectServerDefDialog;

import com.amalto.workbench.editors.DataClusterDialog;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.service.ILegendServerDefService;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.webservices.TMDMService;
import com.amalto.workbench.webservices.WSDataClusterPK;
import com.amalto.workbench.webservices.WSItemPK;
import com.amalto.workbench.webservices.WSRouteItemV2;
import com.amalto.workbench.webservices.WSRoutingRulePK;
import com.amalto.workbench.webservices.WSRoutingRulePKArray;


/**
 * created by liusongbo on Oct 15, 2014
 */
public class TestTriggerAction extends AbstractRepositoryAction implements SelectionListener {

    private static Logger log = Logger.getLogger(TestTriggerAction.class);
    private DataClusterDialog dialog;

    private TMDMService service;

    public TestTriggerAction() {
        super(Messages.TestTriggerAction_Test);
        setImageDescriptor(ImageCache.getImage(EImage.RUN_EXC.getPath()));
        setToolTipText(Messages.RoutingRuleMainPage2_run);
        setId("starttrigger"); //$NON-NLS-1$
    }

    @Override
    public String getGroupName() {
        return GROUP_EDIT;
    }

    @Override
    protected void doRun() {
        com.amalto.workbench.utils.MDMServerDef serverDef = getServerDef();
        if (serverDef == null) {
            return;
        }

        boolean canConnect = checkConnection(serverDef.getUrl(), serverDef.getUser(), serverDef.getPasswd());
        if (!canConnect) {
            MessageDialog.openError(getShell(), Messages.RoutingRuleMainPage2_CheckConnection,
                    Messages.RoutingRuleMainPage2_UnableToConnect);
            return;
        }

        try {
            service = Util.getMDMService(new URL(serverDef.getUrl()), serverDef.getUser(), serverDef.getPasswd());
            IWorkbenchPartSite site = commonViewer.getCommonNavigator().getSite();
            dialog = new DataClusterDialog(getShell(), new TreeObject(), site);
            dialog.setDefaultServerDef(serverDef);
            dialog.setOkLabel(Messages.TestTriggerAction_Test);
            dialog.setCancelLabel(Messages.TestTriggerAction_Close);
            dialog.setSelectionListener(this);
            dialog.open();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    protected void test() {
        try {
            String dataCluster = dialog.getDataContainer();
            String concept = dialog.getConcept();
            String[] recordIds = dialog.getRecordIds();
            if (recordIds == null || recordIds.length == 0) {
                MessageDialog.openError(getShell(), Messages._Error, Messages.RoutingRuleMainPage2_NoRecordSelected);
                return;
            }

            WSRoutingRulePKArray routeItemV2 = service.routeItemV2(new WSRouteItemV2(new WSItemPK(concept, Arrays
                    .asList(recordIds), new WSDataClusterPK(dataCluster))));

            if (routeItemV2 == null || routeItemV2.getWsRoutingRulePKs() == null || routeItemV2.getWsRoutingRulePKs().size() == 0) {
                MessageDialog.openInformation(getShell(), Messages.RoutingRuleMainPage2_Success,
                        Messages.RoutingRuleMainPage2_noTriggerExecuted);
                return;
            }

            List<WSRoutingRulePK> wsRoutingRulePKs = routeItemV2.getWsRoutingRulePKs();
            StringBuilder builder = new StringBuilder(wsRoutingRulePKs.get(0).getPk());
            for (int i = 1; i < wsRoutingRulePKs.size(); i++) {
                builder.append("," + wsRoutingRulePKs.get(i).getPk()); //$NON-NLS-1$
            }

            MessageDialog.openInformation(getShell(), Messages.RoutingRuleMainPage2_Success,
                    Messages.bind(Messages.RoutingRuleMainPage2_ExecuteTriggerSuccess, builder.toString()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            MessageDialog.openError(getShell(), Messages._Error, Messages.RoutingRuleMainPage2_ErrorTestTrigger);
        }
    }

    private com.amalto.workbench.utils.MDMServerDef getServerDef() {
        MDMServerDef lastServerDef = openServerDialog(null);
        if (lastServerDef == null) {
            return null;
        }

        return transform(lastServerDef);
    }

    public MDMServerDef openServerDialog(MDMServerDef serverObject) {
        SelectServerDefDialog dlg = new SelectServerDefDialog(getShell());
        dlg.create();
        dlg.setSelectServer(serverObject);
        if (dlg.open() == IDialogConstants.OK_ID) {
            MDMServerDef serverDef = dlg.getSelectedServerDef();

            return serverDef;
        }
        return null;
    }

    private com.amalto.workbench.utils.MDMServerDef transform(MDMServerDef serverDef) {
        com.amalto.workbench.utils.MDMServerDef mdmServerDef = new com.amalto.workbench.utils.MDMServerDef();
        mdmServerDef.setProtocol(serverDef.getProtocol());
        mdmServerDef.setHost(serverDef.getHost());
        mdmServerDef.setPort(serverDef.getPort());
        mdmServerDef.setName(serverDef.getName());
        mdmServerDef.setPasswd(serverDef.getPasswd());
        mdmServerDef.setPath(serverDef.getPath());
        mdmServerDef.setUser(serverDef.getUser());

        return mdmServerDef;
    }

    private boolean checkConnection(String endpointaddress, String username, String password) {
        ILegendServerDefService serverDefService = (ILegendServerDefService) GlobalServiceRegister.getDefault().getService(
                ILegendServerDefService.class);
        return serverDefService.checkServerDefConnection(endpointaddress, username, password);
    }

    public void widgetSelected(SelectionEvent e) {
        Button btn = (Button) e.widget;
        if (((Integer) btn.getData()).intValue() == IDialogConstants.OK_ID) {
            test();
        }
    }

    public void widgetDefaultSelected(SelectionEvent e) {
    }

}
