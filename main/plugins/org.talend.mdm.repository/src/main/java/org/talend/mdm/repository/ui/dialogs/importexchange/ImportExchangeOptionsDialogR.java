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
package org.talend.mdm.repository.ui.dialogs.importexchange;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.amalto.workbench.dialogs.ImportExchangeOptionsDialog;


public class ImportExchangeOptionsDialogR extends ImportExchangeOptionsDialog {

    public ImportExchangeOptionsDialogR(Shell parentShell, FormToolkit toolkit, boolean importOption,
            StringBuffer zipFileRepository) {
        super(parentShell, toolkit, importOption, zipFileRepository);
    }

	@Override
	public void fillInTable() {
		if (chooseExport()) {
			clearTable();
		} else {
			super.fillInTable();
		}
	}
}
