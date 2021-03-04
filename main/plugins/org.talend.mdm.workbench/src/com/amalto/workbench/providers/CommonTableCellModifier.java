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
package com.amalto.workbench.providers;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Item;

import com.amalto.workbench.detailtabs.sections.BasePropertySection;

public abstract class CommonTableCellModifier<T> implements ICellModifier {

    protected Map<String, CellEditorValueExtractor<T>> prop2ValueIndexExtractor = new HashMap<String, CellEditorValueExtractor<T>>();

    protected Map<String, CellEditorValueModifier<T>> prop2ValueModifier = new HashMap<String, CellEditorValueModifier<T>>();

    protected BasePropertySection section;

    public BasePropertySection getSection() {
		return section;
	}

	public void setSection(BasePropertySection section) {
		this.section = section;
	}

	public boolean canModify(Object element, String property) {
        return false;
    }

    @SuppressWarnings("unchecked")
    public Object getValue(Object element, String property) {

        if (element == null || !prop2ValueIndexExtractor.containsKey(property))
            return null;
        try {
            return prop2ValueIndexExtractor.get(property).getValue((T) element);
        } catch (Exception e) {
            return null;
        }
    }

    public void modify(Object element, String property, Object value) {

        if (!doModify(element, property, value))
            return;

        if (getViewer() != null)
            getViewer().refresh();
        if(section!=null)section.autoCommit();
    }

    private boolean doModify(Object element, String property, Object value) {

        if (element == null || !prop2ValueModifier.containsKey(property))
            return false;

        try {
            prop2ValueModifier.get(property).modify(getRealModifiedItem(element), value);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    @SuppressWarnings("unchecked")
    private T getRealModifiedItem(Object selection) {

        if (!(selection instanceof Item))
            return null;

        try {
            return (T) ((Item) selection).getData();
        } catch (Exception e) {
            return null;
        }
    }

    protected abstract Viewer getViewer();
}
