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
package org.talend.mdm.repository.ui.dialogs.xsd;

import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.talend.mdm.repository.ui.widgets.TisTableViewerR;

import com.amalto.workbench.dialogs.FKFilterDialog;
import com.amalto.workbench.editors.DataModelMainPage;
import com.amalto.workbench.widgets.ComplexTableViewerColumn;
import com.amalto.workbench.widgets.TisTableViewer;


/**
 * DOC hbhong  class global comment. Detailled comment
 */
public class FKFilterDialogR extends FKFilterDialog {

    /**
     * DOC hbhong FKFilterDialogR constructor comment.
     *
     * @param parentShell
     * @param title
     * @param filter
     * @param page
     * @param conceptName
     */
    public FKFilterDialogR(Shell parentShell, String title, String filter, DataModelMainPage page, String conceptName) {
        super(parentShell, title, filter, page, conceptName);
    }

    @Override
    protected TisTableViewer getNewTisTableViewer(List<ComplexTableViewerColumn> columns, FormToolkit toolkit, Composite parent) {
        return new TisTableViewerR(columns, toolkit, parent);
    }

	public FKFilterDialogR(Shell parentShell, String title, String filter,
			DataModelMainPage page, String conceptName, boolean lock) {
		super(parentShell, title, filter, page, conceptName, lock);
	}

}
