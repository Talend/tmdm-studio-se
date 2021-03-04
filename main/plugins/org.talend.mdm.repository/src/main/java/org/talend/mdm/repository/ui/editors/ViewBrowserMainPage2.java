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
package org.talend.mdm.repository.ui.editors;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.ui.forms.editor.FormEditor;
import org.talend.mdm.repository.core.service.RepositoryWebServiceAdapter;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.model.mdmmetadata.MDMServerDef;

import com.amalto.workbench.editors.ViewBrowserMainPage;
import com.amalto.workbench.exadapter.ExAdapterManager;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.utils.XtentisException;
import com.amalto.workbench.webservices.TMDMService;
import com.amalto.workbench.webservices.WSDataClusterPK;
import com.amalto.workbench.webservices.WSViewPK;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class ViewBrowserMainPage2 extends ViewBrowserMainPage {

    private static Logger log = Logger.getLogger(ViewBrowserMainPage2.class);

    private IViewBrowserMainPage2Exadapter exAdapter;

    /**
     * DOC hbhong ViewBrowserMainPage2 constructor comment.
     *
     * @param editor
     */
    public ViewBrowserMainPage2(FormEditor editor) {
        super(editor);
        initAdapter();
    }

    private void initAdapter() {
        exAdapter = ExAdapterManager.getAdapter(this, IViewBrowserMainPage2Exadapter.class);
    }

    @Override
    protected List<WSDataClusterPK> getDataClusterPKs() throws MalformedURLException, XtentisException {
        MDMServerDef d = getServerDef();
        return Util.getAllDataClusterPKs(new URL(d.getProtocol() + d.getHost() + ":" + d.getPort() //$NON-NLS-1$
                + d.getPath()), d.getUser(), d.getPasswd());
    }

    private MDMServerDef getServerDef() {
        ViewBrowserInput input = (ViewBrowserInput) getEditorInput();
        return input.getServerDef();
    }

    @Override
    protected WSViewPK getViewPK() {

        return new WSViewPK((String) getXObject().getWsKey());
    }

    @Override
    protected TMDMService getMDMService() {
        try {
            MDMServerDef serverDef = getServerDef();
            return RepositoryWebServiceAdapter.getMDMService(serverDef);
        } catch (XtentisException e) {
            log.error(e.getMessage(), e);

        }
        return null;
    }

    @Override
    protected String[] getClusterTypes() {
        String[] clusterTypes = new String[] { Messages.ViewBrowserMainPage2_Master };
        if (exAdapter != null) {
            String dataClusterType = exAdapter.getDataClusterType(dataClusterCombo.getText(), getServerDef());
            if (dataClusterType != null) {
                String[] clusters = new String[clusterTypes.length + 1];
                System.arraycopy(clusterTypes, 0, clusters, 0, clusterTypes.length);
                clusters[clusterTypes.length] = dataClusterType;
                clusterTypes = clusters;
            }
        }

        return clusterTypes;
    }

    @Override
    protected String getPkAddition() {

        if (exAdapter != null) {
            String pkAddition = exAdapter.getPkAddition(clusterTypeCombo.getText());
            return pkAddition;
        }

        return super.getPkAddition();
    }

}
