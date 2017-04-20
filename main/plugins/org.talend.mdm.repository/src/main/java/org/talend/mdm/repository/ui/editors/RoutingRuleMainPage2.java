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
package org.talend.mdm.repository.ui.editors;

import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.talend.mdm.repository.core.service.RepositoryWebServiceAdapter;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.ui.dialogs.xpath.XpathSelectDialog2;
import org.talend.mdm.repository.ui.widgets.TisTableViewerR;
import org.talend.mdm.repository.ui.widgets.xmleditor.infoholder.RepositoryExternalInfoHolder;

import com.amalto.workbench.dialogs.XpathSelectDialog;
import com.amalto.workbench.editors.RoutingRuleMainPage;
import com.amalto.workbench.utils.XtentisException;
import com.amalto.workbench.webservices.WSServiceGetDocument;
import com.amalto.workbench.widgets.TisTableViewer;
import com.amalto.workbench.widgets.xmleditor.infoholder.ExternalInfoHolder;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class RoutingRuleMainPage2 extends RoutingRuleMainPage {

    private static Log log = LogFactory.getLog(RoutingRuleMainPage2.class);

    /**
     * DOC hbhong RoutingRuleMainPage2 constructor comment.
     * 
     * @param editor
     */
    public RoutingRuleMainPage2(FormEditor editor) {
        super(editor);
    }

    @Override
    protected void initServiceNameCombo() throws XtentisException {
        for (String name : RepositoryWebServiceAdapter.getServiceNames()) {
            serviceNameCombo.add(name);
        }
    }

    @Override
    protected void initExternalInfoHolder() {

        ExternalInfoHolder<?> allProcessNamesHolder = RepositoryExternalInfoHolder.getAllProcessesNamesHolder(null);
        ExternalInfoHolder<?> allJobInfosHolder = RepositoryExternalInfoHolder.getAllJobInfosHolder(null);
        ExternalInfoHolder<?> allVarCandidatesHolder = ExternalInfoHolder.getTriggerAllCallJobVarsCandidatesHolder();
        ExternalInfoHolder<?> mdmServerInfoHolder = RepositoryExternalInfoHolder.getAllMDMServerInfoHolder2(null);
        ExternalInfoHolder<?> workflowInfoHolder = RepositoryExternalInfoHolder.getAllWorkflowInfoHolder(null);
        ExternalInfoHolder<?> allDataModelHolderProxy = RepositoryExternalInfoHolder.getAllDataModelInfoHolderProxy(null);

        initExternalInfoHolderForEachType("callprocess", new ExternalInfoHolder<?>[] { allProcessNamesHolder });//$NON-NLS-1$
        initExternalInfoHolderForEachType("callJob", new ExternalInfoHolder<?>[] { allJobInfosHolder, mdmServerInfoHolder,//$NON-NLS-1$
                allVarCandidatesHolder });
        initExternalInfoHolderForEachType("workflow", new ExternalInfoHolder<?>[] { workflowInfoHolder, //$NON-NLS-1$
                allDataModelHolderProxy });

        initExternalInfoHolderForEachType("workflowcontext", new ExternalInfoHolder<?>[] { workflowInfoHolder, //$NON-NLS-1$
                allDataModelHolderProxy });

        //

    }

    @Override
    protected TisTableViewer getNewTisTableViewer(FormToolkit toolkit, Composite routingExpressionsGroup) {
        return new TisTableViewerR(Arrays.asList(conditionsColumns), toolkit, routingExpressionsGroup);
    }

    @Override
    protected void createCharacteristicsContent(FormToolkit toolkit, Composite charComposite) {
        super.createCharacteristicsContent(toolkit, charComposite);
    }

    @Override
    protected XpathSelectDialog getNewXpathDlg() {
        return new XpathSelectDialog2(getSite().getShell(), Messages.RoutingRuleMainPage2_selectEntity, getSite(), false,
                dataModelName);
    }

    @Override
    protected WSServiceGetDocument getServiceDocument(String jndiName) {
        return RepositoryWebServiceAdapter.getServiceDocument(jndiName);
    }

    @Override
    public void markDirtyWithoutCommit() {
        IEditorInput editorInput = getEditorInput();
        boolean isReadOnly = false;
        if (editorInput instanceof IRepositoryViewEditorInput) {
            isReadOnly = ((IRepositoryViewEditorInput) editorInput).isReadOnly();
        }
        if (!isReadOnly) {
            super.markDirtyWithoutCommit();
        }
    }

}
