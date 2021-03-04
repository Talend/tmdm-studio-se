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
package org.talend.mdm.repository.ui.actions;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.model.repository.RepositoryObject;
import org.talend.mdm.repository.core.IRepositoryNodeActionProvider;
import org.talend.mdm.repository.core.IRepositoryNodeConfiguration;
import org.talend.mdm.repository.core.bridge.AbstractBridgeRepositoryAction;
import org.talend.mdm.repository.extension.RepositoryNodeConfigurationManager;
import org.talend.mdm.repository.ui.editors.IRepositoryViewEditorInput;
import org.talend.mdm.repository.ui.navigator.MDMRepositoryView;
import org.talend.mdm.repository.ui.wizards.MDMOpenExistVersionProcessWizard;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;


/**
 * DOC achen  class global comment. Detailled comment
 */
public class MDMOpenExistVersionProcessAction extends AbstractBridgeRepositoryAction {

    private static Logger log = Logger.getLogger(MDMOpenExistVersionProcessAction.class);

    private static class MDMOpenExistVersionProcessActionAdapter extends
            org.talend.designer.core.ui.action.OpenExistVersionProcessAction {

        @Override
        protected void doRun() {
            ISelection selection = getSelection();
            Object obj = ((IStructuredSelection) selection).getFirstElement();
            RepositoryNode node = (RepositoryNode) obj;

            RepositoryObject repositoryObj = new RepositoryObject(node.getObject().getProperty());
            repositoryObj.setRepositoryNode(node.getObject().getRepositoryNode());
            MDMOpenExistVersionProcessWizard wizard = new MDMOpenExistVersionProcessWizard(repositoryObj);
            WizardDialog dialog = new WizardDialog(Display.getCurrent().getActiveShell(), wizard);
            dialog.setPageSize(300, 250);
            if (dialog.open() == Dialog.OK) {
                if(wizard.getViewObj()!=null){
                    obj=wizard.getViewObj();
                }

                if (obj == node.getObject()) {
                    MDMRepositoryView.show().getCommonViewer().refresh(obj);
                } else {
                    MDMRepositoryView.show().getCommonViewer().refresh(node.getParent().getObject());
                }
            }
        }

        @Override
        protected IEditorPart getCorrespondingEditor(IRepositoryNode node) {
            IRepositoryViewObject viewObject = node.getObject();
            Item item = viewObject.getProperty().getItem();
            IRepositoryNodeConfiguration configuration = RepositoryNodeConfigurationManager.getConfiguration(item);
            if (configuration != null) {
                IRepositoryNodeActionProvider actionProvider = configuration.getActionProvider();
                if (actionProvider != null) {
                    IRepositoryViewEditorInput editorInput = actionProvider.getOpenEditorInput(viewObject);
                    if (editorInput != null) {

                        IWorkbenchPage page = MDMRepositoryView.show().getCommonViewer().getCommonNavigator().getSite().getWorkbenchWindow()
                                .getActivePage();
                        try {
                            return page.openEditor(editorInput, editorInput.getEditorId());
                        } catch (PartInitException e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                }
            }
            return null;
        }

    }

    /**
     * DOC achen MDMOpenExistVersionProcessAction constructor comment.
     *
     * @param cAction
     */
    public MDMOpenExistVersionProcessAction() {
        super(new MDMOpenExistVersionProcessActionAdapter());

    }

    /*
     * (non-Javadoc)
     *
     * @see org.talend.mdm.repository.core.AbstractRepositoryAction#getGroupName()
     */
    @Override
    public String getGroupName() {
        return GROUP_COMMON;
    }

    @Override
    public boolean isVisible(IRepositoryViewObject viewObj) {
        return getSelectedObject().size() == 1;
    }
}
