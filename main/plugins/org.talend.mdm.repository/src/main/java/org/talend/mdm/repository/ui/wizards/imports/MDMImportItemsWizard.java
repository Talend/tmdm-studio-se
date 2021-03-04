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
package org.talend.mdm.repository.ui.wizards.imports;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.talend.repository.items.importexport.ui.wizard.imports.ImportItemsWizard;
import org.talend.repository.items.importexport.ui.wizard.imports.ImportItemsWizardPage;

/**
 * created by HHB on 2015-5-4 Detailled comment
 *
 */
public class MDMImportItemsWizard extends ImportItemsWizard {

    private ImportItemsWizardPage mainPage;

    private IWorkbench workbench;

    private IStructuredSelection selection;

    @Override
    public void init(IWorkbench w, IStructuredSelection s) {
        this.workbench = w;
        this.selection = s;
    }

    @Override
    public void addPages() {
        mainPage = new MDMImportItemsWizardPage(this.getWindowTitle(), this.selection);
        addPage(mainPage);
    }

    @Override
    public boolean performCancel() {
        return mainPage.performCancel();
    }

    @Override
    public boolean performFinish() {
        return mainPage.performFinish();
    }

}
