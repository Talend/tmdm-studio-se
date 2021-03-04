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
package org.talend.mdm.repository.ui.editors;

import java.util.Arrays;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbenchPartSite;
import org.talend.mdm.repository.ui.widgets.UserSecurityComboBoxDialogCellEditor;

import com.amalto.workbench.editors.AMainPageV2;
import com.amalto.workbench.models.KeyValue;
import com.amalto.workbench.models.Line;
import com.amalto.workbench.widgets.ComplexTableViewerColumn;


/**
 * created by liusongbo on 2014-3-25
 */
public class UserSecurityCellModifier implements ICellModifier {

    private UserSecurityComboBoxDialogCellEditor variableCellEditor;

    private TableViewer viewer;

    private AMainPageV2 page;

    private ComplexTableViewerColumn[] conditionColumns;

    public UserSecurityCellModifier(IWorkbenchPartSite site, AMainPageV2 page, TableViewer tv,
            ComplexTableViewerColumn[] conditionsColumns) {
        this.page = page;
        this.viewer = tv;
        this.conditionColumns = conditionsColumns;

        variableCellEditor = new UserSecurityComboBoxDialogCellEditor(tv.getTable(), site);
    }

    public void modify(Object element, String property, Object value) {
        if (value instanceof Integer) {
            if (Integer.valueOf(value.toString()) == -1) {
                return;
            }
        }

        Line line = null;
        if (element instanceof TableItem) {
            TableItem item = (TableItem) element;
            line = (Line) item.getData();
        } else {
            line = (Line) element;
        }

        int columnIndex = Arrays.asList(viewer.getColumnProperties()).indexOf(property);
        if (columnIndex >= 0 && columnIndex < viewer.getColumnProperties().length) {
            if (isAColumnWithCombo(columnIndex)) {
                String[] attrs = conditionColumns[columnIndex].getComboValues();
                value = attrs[Integer.parseInt(value.toString())];
            }
            KeyValue kv = line.keyValues.get(columnIndex);
            boolean noChange = kv.value.equals(value.toString());
            kv.value = value.toString();
            viewer.refresh();
            if (!noChange && page != null) {
                page.markDirty();
            }
        }
    }

    private boolean isAColumnWithCombo(int columnIdx) {
        return conditionColumns[columnIdx].isCombo();
    }

    public Object getValue(Object element, String property) {
        int columnIndex = Arrays.asList(viewer.getColumnProperties()).indexOf(property);
        Line line = (Line) element;
        if (isAColumnWithCombo(columnIndex)) {
            String value = line.keyValues.get(columnIndex).value;
            String[] attrs = conditionColumns[columnIndex].getComboValues();
            return Arrays.asList(attrs).indexOf(value);
        }

        for (KeyValue keyvalue : line.keyValues) {
            if (property.equals(keyvalue.key)) {
                if (keyvalue.value == null) {
                    return conditionColumns[columnIndex].getNillDisplay();
                }

                return keyvalue.value;
            }
        }
        return null;
    }

    public boolean canModify(Object element, String property) {
        int columnIndex = Arrays.asList(viewer.getColumnProperties()).indexOf(property);
        if (columnIndex == 2) {
            viewer.getCellEditors()[2] = variableCellEditor;
        }

        return true;
    }

}
