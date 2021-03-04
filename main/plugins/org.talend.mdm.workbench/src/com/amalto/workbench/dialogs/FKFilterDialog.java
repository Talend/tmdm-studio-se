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
package com.amalto.workbench.dialogs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.amalto.workbench.editors.DataModelMainPage;
import com.amalto.workbench.models.Line;
import com.amalto.workbench.models.TreeParent;
import com.amalto.workbench.utils.FKFilterParser;
import com.amalto.workbench.utils.IConstants;
import com.amalto.workbench.widgets.ComplexTableViewerColumn;
import com.amalto.workbench.widgets.ICellEditor;
import com.amalto.workbench.widgets.TisTableViewer;
import com.amalto.workbench.widgets.WidgetFactory;
import com.amalto.workbench.widgets.celleditor.DefaultXPathValidator;
import com.amalto.workbench.widgets.celleditor.IXPathValidator;

public class FKFilterDialog extends Dialog {

    String title;

    private TisTableViewer viewer;

    DataModelMainPage page;

    String filter;

    private ComplexTableViewerColumn[] columns;

    String conceptName;

    protected String dataModelName;

    private Composite dialogAreaComposite;

    private boolean lock;

    public FKFilterDialog(Shell parentShell, String title, String filter, DataModelMainPage page, String conceptName) {
        this(parentShell,title,filter,page,conceptName,false);
    }

    public FKFilterDialog(Shell parentShell, String title, String filter, DataModelMainPage page, String conceptName,boolean lock) {
        super(parentShell);
        this.filter = filter;
        this.page = page;
        this.title = title;
        this.conceptName = conceptName;
        this.lock = lock;
    }

    public boolean isLock() {
		return lock;
	}

	public void setLock(boolean lock) {
		this.lock = lock;
	}

	@Override
    protected Control createDialogArea(Composite parent) {
        parent.getShell().setText(this.title);
        dialogAreaComposite = (Composite) super.createDialogArea(parent);
        dialogAreaComposite.setLayout(new GridLayout(2, false));

        columns = new ComplexTableViewerColumn[] {
                new ComplexTableViewerColumn("XPath", false, "newXPath", "newXPath", "", ComplexTableViewerColumn.XPATH_STYLE,//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$
                        new String[] {}, 0),
                new ComplexTableViewerColumn("Operator", false, "", "", "", ComplexTableViewerColumn.COMBO_STYLE,//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$
                        IConstants.VIEW_CONDITION_OPERATORS, 0),
                new ComplexTableViewerColumn("Value", false, "", "", "", ComplexTableViewerColumn.XPATH_STYLE, new String[] {}, 0),//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$
                new ComplexTableViewerColumn("Predicate", true, "", "", "", ComplexTableViewerColumn.COMBO_STYLE,//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$
                        IConstants.PREDICATES, 0), };
        columns[0].setColumnWidth(200);
        columns[1].setColumnWidth(140);
        columns[2].setColumnWidth(200);
        columns[3].setColumnWidth(140);
        viewer = getNewTisTableViewer(Arrays.asList(columns), WidgetFactory.getWidgetFactory(), dialogAreaComposite);
        viewer.setModelLock(lock);
        viewer.setXpath(true);
        viewer.setDatamodelName(dataModelName);

        Map<ComplexTableViewerColumn, IXPathValidator> validators = new HashMap<ComplexTableViewerColumn, IXPathValidator>();
        validators.put(columns[0], new DefaultXPathValidator());// set to "XPath" column the xpath validator
        viewer.setValidators(validators);

        // viewer.setMainPage(page);//TODO
        // viewer.setConceptName(conceptName);
        // viewer.setContext(true);

        // Modified by hbhong,to fix bug 21784
        TreeParent treeParent = (TreeParent) page.getAdapter(TreeParent.class);
        viewer.setTreeParent(treeParent);
        // The ending| bug:21784
        viewer.create();
        viewer.setHeight(140);
        viewer.setWidth(680);
        viewer.getMainComposite().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 3));

        parent.getShell().addDisposeListener(new DisposeListener() {

            public void widgetDisposed(DisposeEvent e) {
                XpathSelectDialog.setContext(null);
            }
        });

        // init data
        parseRules();

        return dialogAreaComposite;
    }

    protected TisTableViewer getNewTisTableViewer(List<ComplexTableViewerColumn> columns, FormToolkit toolkit, Composite parent) {
        return new TisTableViewer(columns, toolkit, parent);
    }

    @Override
    protected boolean isResizable() {
        return true;
    }

    private void parseRules() {
        List<Line> lines = new ArrayList<Line>();

        String[] keyNames = getKeyNames();
        FKFilterParser.parseFilter(filter, lines, keyNames);

        viewer.getViewer().setInput(lines);
    }

    private String[] getKeyNames() {
        String[] keyNames = new String[columns.length];
        for (int i = 0; i < columns.length; i++) {
            keyNames[i] = columns[i].getName();
        }

        return keyNames;
    }

    @Override
    protected void okPressed() {
        XpathSelectDialog.setContext(null);
        deactiveAllCellEditors();
        resetFilter();
        super.okPressed();
    }

    private void deactiveAllCellEditors() {
        CellEditor[] editors = viewer.getViewer().getCellEditors();
        for (CellEditor editor : editors) {
            if (editor instanceof ICellEditor) {
                ((ICellEditor) editor).deactive();
            }
        }
    }

    @Override
    protected void cancelPressed() {
        super.cancelPressed();
        XpathSelectDialog.setContext(null);
    }

    private String resetFilter() {
        List<Line> lines = new ArrayList<Line>();
        TableItem[] items = viewer.getViewer().getTable().getItems();
        if (items.length > 0) {
            for (TableItem item : items) {
                Line line = (Line) item.getData();
                lines.add(line);
            }
        }
        filter = FKFilterParser.getDeParseredFilter(lines);

        return filter;
    }

    @Override
    protected Point getInitialSize() {
        return new Point(700, 400);
    }

    public String getFilter() {
        return filter;
    }

    public void setDataModel(String dataModelName) {
        this.dataModelName = dataModelName;
    }

}
