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
package org.talend.mdm.repository.ui.dialogs.datacontent;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.talend.mdm.repository.core.datacontent.DataEntityUnit;
import org.talend.mdm.repository.core.datacontent.DataProcessRule;
import org.talend.mdm.repository.i18n.Messages;

import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;

/**
 * created by HHB on 2012-10-9 Detailled comment
 *
 */
public class DataProcessRuleDialog extends Dialog {

    private static final Image IMG_CONCEPT = ImageCache.getCreatedImage(EImage.CONCEPT.getPath());

    private class TableLabelProvider extends LabelProvider implements ITableLabelProvider {

        public Image getColumnImage(Object element, int columnIndex) {
            return IMG_CONCEPT;
        }

        public String getColumnText(Object element, int columnIndex) {
            if (element instanceof DataEntityUnit) {
                return ((DataEntityUnit) element).getEntityName();
            }
            return element.toString();
        }
    }

    private Button okButton;

    private final DataProcessRule rule;

    private Button selectAllBun;

    private Button upBun;

    private Table table;

    private CheckboxTableViewer tableViewer;

    private Label lblNewLabel;

    private Button downBun;

    /**
     * Create the dialog.
     *
     * @param parentShell
     */
    public DataProcessRuleDialog(Shell parentShell, DataProcessRule rule) {
        super(parentShell);
        this.rule = rule;
        setShellStyle(getShellStyle() | SWT.RESIZE);
    }

    /**
     * Create contents of the dialog.
     *
     * @param parent
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        GridLayout gridLayout = (GridLayout) container.getLayout();
        gridLayout.numColumns = 2;

        lblNewLabel = new Label(container, SWT.NONE);
        lblNewLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
        lblNewLabel.setText(Messages.DataProcessRuleDialog_title);

        tableViewer = CheckboxTableViewer.newCheckList(container, SWT.BORDER | SWT.FULL_SELECTION);
        table = tableViewer.getTable();
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3));

        upBun = new Button(container, SWT.NONE);
        upBun.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                moveUp();
            }
        });
        upBun.setImage(ImageCache.getCreatedImage(EImage.PREV_NAV.getPath()));
        upBun.setEnabled(false);
        downBun = new Button(container, SWT.NONE);
        downBun.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                moveDown();
            }
        });
        downBun.setImage(ImageCache.getCreatedImage(EImage.NEXT_NAV.getPath()));
        downBun.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        downBun.setEnabled(false);
        new Label(container, SWT.NONE);

        selectAllBun = new Button(container, SWT.CHECK);
        selectAllBun.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                selectAll(selectAllBun.getSelection());
                updateOKButton();
            }
        });
        selectAllBun.setText(Messages.DataProcessRuleDialog_selectAll);
        new Label(container, SWT.NONE);
        initTableViewer();
        updateSelectAllBun();
        return container;
    }

    private void updateSelectAllBun() {
        boolean selectAll = true;
        for (DataEntityUnit unit : rule.getEntityUnits()) {
            if (!unit.isSelected()) {
                selectAll = false;
                break;
            }
        }
        selectAllBun.setSelection(selectAll);

    }

    private void updateOKButton() {
        if (okButton != null) {
            okButton.setEnabled(rule.returnSelectedEntityUnits().size() > 0);
        }
    }

    /**
     * DOC HHB Comment method "moveUp".
     */
    protected void moveUp() {
        moveUnit(true);
    }

    protected void moveDown() {
        moveUnit(false);
    }

    private void moveUnit(boolean up) {
        DataEntityUnit curSelectedUnit = getCurSelectedUnit();
        if (curSelectedUnit == null) {
            return;
        }

        List<DataEntityUnit> units = rule.getEntityUnits();
        int index = units.indexOf(curSelectedUnit);
        int newIndex = -1;
        if (index >= 0) {
            if (up) {
                newIndex = index - 1;
            } else {
                newIndex = index + 1;
            }
            DataEntityUnit bakUnit = units.get(newIndex);
            units.set(newIndex, curSelectedUnit);
            units.set(index, bakUnit);
            tableViewer.refresh();
            enableButtons();
        }
    }

    private void selectAll(boolean selectAll) {
        for (DataEntityUnit unit : rule.getEntityUnits()) {
            unit.setSelected(selectAll);
        }
        tableViewer.refresh();
    }

    private void initTableViewer() {
        tableViewer.setLabelProvider(new TableLabelProvider());
        tableViewer.setContentProvider(new ArrayContentProvider());
        tableViewer.setCheckStateProvider(new ICheckStateProvider() {

            public boolean isGrayed(Object element) {
                return false;
            }

            public boolean isChecked(Object element) {
                if (element instanceof DataEntityUnit) {
                    return ((DataEntityUnit) element).isSelected();
                }
                return false;
            }
        });
        tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            public void selectionChanged(SelectionChangedEvent event) {
                enableButtons();

            }
        });
        tableViewer.addCheckStateListener(new ICheckStateListener() {

            public void checkStateChanged(CheckStateChangedEvent event) {
                DataEntityUnit unit = (DataEntityUnit) event.getElement();
                unit.setSelected(event.getChecked());
                updateSelectAllBun();
                updateOKButton();
            }
        });
        //
        tableViewer.setInput(rule.getEntityUnits());

    }

    private DataEntityUnit getCurSelectedUnit() {
        ISelection selection = tableViewer.getSelection();
        if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
            Object selectedObj = ((IStructuredSelection) selection).getFirstElement();
            return (DataEntityUnit) selectedObj;
        }
        return null;
    }

    private void enableButtons() {
        DataEntityUnit curSelectedUnit = getCurSelectedUnit();
        if (curSelectedUnit == null) {
            return;
        }
        List<DataEntityUnit> entityUnits = rule.getEntityUnits();
        upBun.setEnabled(entityUnits.get(0) != curSelectedUnit);
        downBun.setEnabled(entityUnits.get(entityUnits.size() - 1) != curSelectedUnit);
    }

    /**
     * Create contents of the button bar.
     *
     * @param parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        okButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    /**
     * Return the initial size of the dialog.
     */
    @Override
    protected Point getInitialSize() {
        return new Point(500, 300);
    }

}
