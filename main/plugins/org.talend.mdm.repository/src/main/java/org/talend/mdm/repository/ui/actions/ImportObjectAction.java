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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.talend.mdm.repository.core.AbstractRepositoryAction;
import org.talend.mdm.repository.core.bridge.AbstractBridgeRepositoryAction;
import org.talend.mdm.repository.ui.wizards.imports.MDMImportItemsWizard;
import org.talend.repository.items.importexport.ui.actions.ImportItemsAction;
import org.talend.repository.items.importexport.ui.wizard.imports.ImportItemsWizard;
import org.talend.repository.ui.actions.AContextualAction;

/**
 * @author cgpiao
 *
 */
public class ImportObjectAction {

    private static AContextualAction importAction;

    private static Log log = LogFactory.getLog(ImportObjectAction.class);

    public static AbstractRepositoryAction createImportAction() {
        AContextualAction action = new ImportItemsAction() {

            @Override
            protected ImportItemsWizard getNewImportItemsWizard() {

                return new MDMImportItemsWizard();
            }

        };
        if (null != action) {
            return new BridgeImportItemsAction(action);
        }
        return null;
    }

    static class BridgeImportItemsAction extends AbstractBridgeRepositoryAction {

        private AContextualAction action;

        public BridgeImportItemsAction(AContextualAction action) {
            super(action);
            this.action = action;
        }

        @Override
        public String getGroupName() {
            return GROUP_EXPORT;
        }

        @Override
        protected void doRun() {
            ISelection selection = getSelectionProvider().getSelection();
            if (action instanceof IWorkbenchWindowActionDelegate) {
                IWorkbenchWindowActionDelegate actionDelegate = (IWorkbenchWindowActionDelegate) action;
                actionDelegate.selectionChanged(null, selection);
            }
            super.doRun();
            getCommonViewer().refresh();
        }
    }

}
