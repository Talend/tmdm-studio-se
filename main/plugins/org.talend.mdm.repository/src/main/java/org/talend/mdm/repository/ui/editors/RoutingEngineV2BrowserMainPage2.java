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

import org.apache.log4j.Logger;
import org.eclipse.ui.forms.editor.FormEditor;
import org.talend.mdm.repository.core.service.RepositoryWebServiceAdapter;
import org.talend.mdm.repository.model.mdmmetadata.MDMServerDef;

import com.amalto.workbench.editors.RoutingEngineV2BrowserMainPage;
import com.amalto.workbench.utils.XtentisException;
import com.amalto.workbench.webservices.TMDMService;

public class RoutingEngineV2BrowserMainPage2 extends RoutingEngineV2BrowserMainPage {

    private static Logger log = Logger.getLogger(RoutingEngineV2BrowserMainPage2.class);

    public RoutingEngineV2BrowserMainPage2(FormEditor editor) {
        super(editor);
    }

    private MDMServerDef getServerDef() {
        XObjectBrowserInput2 input = (XObjectBrowserInput2) getEditorInput();
        return input.getServerDef();
    }

    @Override
    protected TMDMService getMDMService() throws XtentisException {
        MDMServerDef serverDef = getServerDef();
        return RepositoryWebServiceAdapter.getMDMService(serverDef);
    }
}
