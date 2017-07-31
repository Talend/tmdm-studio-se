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

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.IServerObjectRepositoryType;
import org.talend.mdm.repository.core.command.CommandManager;
import org.talend.mdm.repository.core.command.CommandStack;
import org.talend.mdm.repository.core.command.ICommand;
import org.talend.mdm.repository.core.service.ContainerCacheService;
import org.talend.mdm.repository.core.service.DeployService;
import org.talend.mdm.repository.core.service.RepositoryQueryService;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.model.mdmmetadata.MDMServerDef;
import org.talend.mdm.repository.ui.dialogs.xpath.XpathSelectDialog2;
import org.talend.mdm.repository.ui.navigator.MDMRepositoryView;
import org.talend.mdm.repository.ui.widgets.TisTableViewerR;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;

import com.amalto.workbench.dialogs.XpathSelectDialog;
import com.amalto.workbench.dialogs.datamodel.IXPathSelectionFilter;
import com.amalto.workbench.editors.ViewMainPage;
import com.amalto.workbench.utils.XtentisException;
import com.amalto.workbench.webservices.WSConceptKey;
import com.amalto.workbench.webservices.WSGetBusinessConceptKey;
import com.amalto.workbench.webservices.WSView;
import com.amalto.workbench.widgets.ComplexTableViewerColumn;
import com.amalto.workbench.widgets.TisTableViewer;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class ViewMainPage2 extends ViewMainPage {

    private static Logger log = Logger.getLogger(ViewMainPage2.class);

    /**
     * DOC hbhong ViewMainPage2 constructor comment.
     * 
     * @param editor
     */
    public ViewMainPage2(FormEditor editor) {
        super(editor);
    }

    @Override
    protected void createCharacteristicsContent(FormToolkit toolkit, Composite charComposite) {
        super.createCharacteristicsContent(toolkit, charComposite);
        TableViewer viewer = conditionViewer.getViewer();
        viewer.setCellModifier(new UserSecurityCellModifier(getSite(), this, viewer, conditionsColumns));
    }

    @Override
    protected void initProcessCombo() throws XtentisException {
        List<String> processNames = RepositoryQueryService.findAllProcessNames();
        cboProcessList.setItems(processNames.toArray(new String[0]));
    }

    @Override
    protected TisTableViewer getNewTisTableViewer(Composite parent, FormToolkit toolkit, List<ComplexTableViewerColumn> columns) {
        return new TisTableViewerR(columns, toolkit, parent);
    }

    @Override
    protected WSView getWsViewObject() {
        return (WSView) getXObject().getWsObject();
    }

    @Override
    protected List<String> getAvailableDataModel() {
        return RepositoryQueryService.getDataModel(null, concept);
    }

    @Override
    protected WSConceptKey getBusinessConceptKey(WSGetBusinessConceptKey businessConcepKey) throws XtentisException {
        return RepositoryResourceUtil.getBusinessConceptKey(businessConcepKey);
    }

    @Override
    protected XpathSelectDialog getXPathSelectionDialog(String title, String modelName, IXPathSelectionFilter filter) {
        XpathSelectDialog dlg = new XpathSelectDialog2(getEditorSite().getShell(), null, title, getSite(), false, modelName,
                false, filter);
        return dlg;
    }

    @Override
    protected void runTest() {
        if (getLastServerDef() == null) {
            MessageDialog.openWarning(null, Messages.Warning_text, Messages.RepositoryWebServiceAdapter_DeployFirst);
            return;
        }

        performSave();

        openTestView();
    }

    private void openTestView() {
        XObjectEditorInput2 input = (XObjectEditorInput2) getEditor().getEditorInput();
        Property prop = input.getInputItem().getProperty();
        IRepositoryViewObject viewObject = ContainerCacheService.get(prop);
        IWorkbenchPage page = getEditor().getEditorSite().getPage();

        ViewBrowserInput browserInput = new ViewBrowserInput(viewObject);
        browserInput.setServerDef(getLastServerDef());
        try {
            IEditorPart oldEditor = page.findEditor(browserInput);
            if (oldEditor != null) {
                page.closeEditor(oldEditor, false);
            }
            page.openEditor(browserInput, XObjectBrowser2.EDITOR_ID);
        } catch (PartInitException e) {
            log.error(e.getMessage(), e);
        }
    }

    private MDMServerDef getLastServerDef() {
        XObjectEditorInput2 input = (XObjectEditorInput2) getEditor().getEditorInput();
        Item item = input.getInputItem();
        MDMServerDef lastServerDef = RepositoryResourceUtil.getLastServerDef(item);

        return lastServerDef;
    }

    protected void performSave() {
        if (getEditor().isDirty()) {
            boolean isConfirmedToDeploy = openConfirmDialog();

            if (isConfirmedToDeploy) {
                getEditor().doSave(new NullProgressMonitor());

                deployAndRefresh();
            }
        } else if (isReferedViewObjModified()) {
            boolean isConfirmedToDeploy = openConfirmDialog();
            if (isConfirmedToDeploy) {
                deployAndRefresh();
            }
        }
    }

    private void deployAndRefresh() {
        DeployService deployService = DeployService.getInstance();
        if (!deployService.isAutoDeploy()) {
            autoDeployProcess(deployService);

            // refresh after deploy
            MDMRepositoryView view = MDMRepositoryView.show();
            if (view != null) {
                view.refreshRootNode(IServerObjectRepositoryType.TYPE_TRANSFORMERV2);
            }
        }
    }

    private void autoDeployProcess(DeployService deployService) {
        XObjectEditorInput2 theInput = (XObjectEditorInput2) getEditorInput();
        IRepositoryViewObject viewObj = theInput.getViewObject();
        deployService.autoDeploy(getSite().getShell(), viewObj);
    }

    private boolean openConfirmDialog() {
        String msg = null;
        if (getEditor().isDirty()) {
            msg = Messages.ViewMainPage_needSaveAndDeploy;
        } else {
            msg = Messages.ViewMainPage_needDeploy;
        }

        return MessageDialog.openConfirm(getSite().getShell(), Messages.ViewMainPage_testView, msg);
    }

    private boolean isReferedViewObjModified() {
        XObjectEditorInput2 editorInput2 = (XObjectEditorInput2) getEditor().getEditorInput();
        IRepositoryViewObject viewObj = editorInput2.getViewObject();
        CommandStack stack = CommandManager.getInstance().findCommandStack(viewObj.getId());
        if (stack != null) {
            ICommand command = stack.getValidDeployCommand();
            if (command.getCommandType() == ICommand.CMD_MODIFY) {
                return true;
            }
        }

        return false;
    }
}
