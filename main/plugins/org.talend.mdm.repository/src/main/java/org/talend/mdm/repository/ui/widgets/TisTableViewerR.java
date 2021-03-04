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
package org.talend.mdm.repository.ui.widgets;

import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.ui.dialogs.xpath.XpathSelectDialog2;

import com.amalto.workbench.dialogs.XpathSelectDialog;
import com.amalto.workbench.widgets.ComplexTableViewerColumn;
import com.amalto.workbench.widgets.TisTableViewer;
import com.amalto.workbench.widgets.XpathWidget;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class TisTableViewerR extends TisTableViewer {

    private IWorkbenchPartSite site;

    public void setSite(IWorkbenchPartSite site) {
        this.site = site;
    }

    /**
     * DOC hbhong TisTableViewerR constructor comment.
     *
     * @param columns
     * @param toolkit
     * @param parent
     */
    public TisTableViewerR(List<ComplexTableViewerColumn> columns, FormToolkit toolkit, Composite parent) {
        super(columns, toolkit, parent);
    }

    @Override
    protected XpathSelectDialog getNewXpathDlgInstance() {
        if (site == null) {
            site = getMainPage().getSite();
        }
        return new XpathSelectDialog2(table.getShell(), Messages.SelectMultipleXPaths, site, true,
                getDatamodelName());
    }

    @Override
    protected XpathWidget getNewXpathWidget(Composite parent) {
        return new XpathWidgetR(parent, false);
    }

    @Override
    protected CellEditor createValidationRuleEditor() {
        return new ValidationRuleEditorR(table);
    }

    public class ValidationRuleEditorR extends ValidationRuleEditor {

        public ValidationRuleEditorR(Composite parent) {
            super(parent);
        }

        @Override
        public void createValidationRuleWidget(Composite parent) {
            validationRule = new ValidationRuleWidgetR(parent, getCurrentTreeParent(), conceptName);
        }
    }

}
