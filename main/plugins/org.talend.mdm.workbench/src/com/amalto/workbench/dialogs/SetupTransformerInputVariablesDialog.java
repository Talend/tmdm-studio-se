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
package com.amalto.workbench.dialogs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.talend.mdm.webservice.WSTransformerProcessStep;
import org.talend.mdm.webservice.WSTransformerV2;
import org.talend.mdm.webservice.WSTransformerVariablesMapping;

import com.amalto.workbench.editors.TransformerMainPage;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.models.Line;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.utils.EContentType;
import com.amalto.workbench.utils.EInputTemplate;
import com.amalto.workbench.widgets.ComplexTableViewer;
import com.amalto.workbench.widgets.ComplexTableViewerColumn;
import com.amalto.workbench.widgets.ComplexTableViewerR;

public class SetupTransformerInputVariablesDialog extends Dialog {

    WSTransformerV2 transformer;

    FormToolkit toolkit;

    TreeObject object;

    private ComplexTableViewer objectViewer;

    TransformerMainPage page;

    public SetupTransformerInputVariablesDialog(Shell parentShell, FormToolkit toolkit, TreeObject obj, TransformerMainPage page) {
        super(parentShell);
        this.toolkit = toolkit;
        object = obj;
        this.page = page;
        transformer = (WSTransformerV2) obj.getWsObject();
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Group comp = new Group(parent, SWT.NONE);
        comp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        ((GridLayout) parent.getLayout()).marginTop = 5;
        ((GridLayout) parent.getLayout()).marginLeft = 5;
        ((GridLayout) parent.getLayout()).marginRight = 5;
        ((GridLayout) parent.getLayout()).marginBottom = 5;
        parent.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        comp.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));

        comp.setLayout(new GridLayout(1, false));
        comp.setText(Messages.bind(Messages.SetupTransformerInputVariablesDialog_CompText, transformer.getName()));

        // Get the input variable names
        Set<String> inputVariables = new HashSet<String>();
        for (WSTransformerProcessStep step : transformer.getProcessSteps()) {
            for (WSTransformerVariablesMapping mapping : step.getInputMappings()) {
                inputVariables.add(mapping.getPipelineVariable() == null ? TransformerMainPage.DEFAULT_VAR : mapping
                        .getPipelineVariable());
            }
        }

        ComplexTableViewerColumn[] columns = new ComplexTableViewerColumn[] {
                new ComplexTableViewerColumn(Messages.SetupTransformerInputVariablesDialog_InputVariables, // name
                        true, // is Nillable ?
                        "_DEFAULT_", // Nill value//$NON-NLS-1$
                        "_DEFAULT_", // Nill display//$NON-NLS-1$
                        "", // Default Value//$NON-NLS-1$
                        ComplexTableViewerColumn.COMBO_STYLE, // is Combo ?
                        inputVariables.toArray(new String[inputVariables.size()]), // Combo Values
                        0 // Text Lines
                ), new ComplexTableViewerColumn(Messages.SetupTransformerInputVariablesDialog_ContentType, // name
                        false, // is Nillable ?
                        "", // Nill value//$NON-NLS-1$
                        "", // Nill display//$NON-NLS-1$
                        "text/xml", // Default Value//$NON-NLS-1$
                        ComplexTableViewerColumn.COMBO_STYLE, // is Combo ?
                        EContentType.allTypes().toArray(new String[EContentType.allTypes().size()]), // Combo Values
                        0 // Text Lines
                ), new ComplexTableViewerColumn(Messages.SetupTransformerInputVariablesDialog_Value, // name
                        true, // is Nillable ?
                        "", // Nill value//$NON-NLS-1$
                        "", // Nill display//$NON-NLS-1$
                        "", // Default Value//$NON-NLS-1$
                        ComplexTableViewerColumn.TEXT_STYLE, // is Combo ?
                        null, // Combo Values
                        10 // Text Lines
                ) };
        objectViewer = new ComplexTableViewerR(Arrays.asList(columns), toolkit, comp, page.getSite());
        objectViewer.create();

        Table table = objectViewer.getViewer().getTable();

        table.getColumns()[2].setWidth(500);

        // GridData gd=(GridData)objectViewer.getTxtLists().get(0).getLayoutData();
        // gd.minimumHeight=200;
        // gd.minimumWidth=300;

        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        // ((GridData)table.getLayoutData()).minimumHeight = 100;
        // ((GridData)table.getLayoutData()).minimumWidth = 600;
        if (page.getCacheList() != null) {
            objectViewer.getViewer().setInput(page.getCacheList());
        } else {
            List<Line> list = new ArrayList<Line>();
            objectViewer.getViewer().setInput(list);
        }
        final CCombo combo = (CCombo) objectViewer.getColumns().get(1).getControl();
        combo.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(SelectionEvent e) {

            }

            public void widgetSelected(SelectionEvent e) {

                if (combo.getText().equals(EInputTemplate.APPLICATION_ITEMPK.getName())) {
                    Text t = (Text) objectViewer.getColumns().get(2).getControl();
                    t.setText(EInputTemplate.APPLICATION_ITEMPK.getContent());
                }
            }

        });
        return comp;

    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);
        parent.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
    }

    private void processOk() {
        // objectViewer.add();
        java.util.List<Line> list = (java.util.List<Line>) objectViewer.getViewer().getInput();
        if (list.size() == 0) {
            return;
        }

        page.setCacheList(list);
        page.execute();

    }

    @Override
    protected void okPressed() {
        setReturnCode(OK);
        processOk();
        super.okPressed();
    }

    @Override
    protected void cancelPressed() {
        setReturnCode(CANCEL);
        super.cancelPressed();
    }
}
