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
package org.talend.mdm.repository.ui.actions.job;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.talend.mdm.repository.core.bridge.AbstractBridgeRepositoryAction;
import org.talend.repository.ui.wizards.exportjob.JobScriptsExportWizard;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class ExportJobScriptAction extends AbstractBridgeRepositoryAction {

    class ExportJobScriptActionAdapter extends org.talend.repository.ui.actions.ExportJobScriptAction {

        public ExportJobScriptActionAdapter() {
            super();
        }

        @Override
        protected void doRun() {

            JobScriptsExportWizard processWizard = new JobScriptsExportWizard();

            processWizard.setWindowTitle(EXPORTJOBSCRIPTS);
            IWorkbench workbench = getWorkbench();
            processWizard.init(workbench, (IStructuredSelection) this.getSelection());

            Shell activeShell = Display.getCurrent().getActiveShell();
            WizardDialog dialog = new WizardDialog(activeShell, processWizard);
            workbench.saveAllEditors(true);
            dialog.setPageSize(830, 450);
            dialog.open();
        }

    }

    /**
     * DOC hbhong EditProcessAction constructor comment.
     *
     * @param cAction
     */
    public ExportJobScriptAction() {
        super(""); //$NON-NLS-1$
        ExportJobScriptActionAdapter contextAction = new ExportJobScriptActionAdapter();
        setContextAction(contextAction);
    }

    private IWorkbench getWorkbench() {
        return commonViewer.getCommonNavigator().getSite().getWorkbenchWindow().getWorkbench();
    }

    @Override
    public String getGroupName() {
        return GROUP_EDIT;
    }

}
