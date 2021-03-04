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
package com.amalto.workbench.detailtabs.sections.providers;

import java.util.Arrays;

import org.eclipse.jface.viewers.Viewer;

import com.amalto.workbench.detailtabs.sections.model.annotationinfo.relationship.ForeignKeyFilterAnnoInfoDefUnit;
import com.amalto.workbench.providers.ArrayComboCellEditorIndexExtractor;
import com.amalto.workbench.providers.ArrayComboCellEditorValueModifier;
import com.amalto.workbench.providers.CellEditorValueExtractor;
import com.amalto.workbench.providers.CellEditorValueModifier;
import com.amalto.workbench.providers.CommonTableCellModifier;
import com.amalto.workbench.utils.IConstants;

public class ForeignKeyFilterCellModifier extends CommonTableCellModifier<ForeignKeyFilterAnnoInfoDefUnit> {

    public static final String COL_PROP_XPATH = "xpath";//$NON-NLS-1$

    public static final String COL_PROP_OPERATOR = "operator";//$NON-NLS-1$

    public static final String COL_PROP_VALUE = "value";//$NON-NLS-1$

    public static final String COL_PROP_PREDICATE = "predicate";//$NON-NLS-1$

    public static final String[] COLPROPS = new String[] { COL_PROP_XPATH, COL_PROP_OPERATOR, COL_PROP_VALUE, COL_PROP_PREDICATE };

    private Viewer viewer;

    public ForeignKeyFilterCellModifier(Viewer viewer) {
        this.viewer = viewer;

        prop2ValueIndexExtractor.put(COL_PROP_XPATH, new CellEditorValueExtractor<ForeignKeyFilterAnnoInfoDefUnit>("getXpath"));//$NON-NLS-1$
        prop2ValueIndexExtractor.put(COL_PROP_OPERATOR, new ArrayComboCellEditorIndexExtractor<ForeignKeyFilterAnnoInfoDefUnit>(
                IConstants.VIEW_CONDITION_OPERATORS, "getOperator"));//$NON-NLS-1$
        prop2ValueIndexExtractor.put(COL_PROP_VALUE, new CellEditorValueExtractor<ForeignKeyFilterAnnoInfoDefUnit>("getValue"));//$NON-NLS-1$
        prop2ValueIndexExtractor.put(COL_PROP_PREDICATE, new ArrayComboCellEditorIndexExtractor<ForeignKeyFilterAnnoInfoDefUnit>(
                IConstants.PREDICATES, "getPredicate"));//$NON-NLS-1$

        prop2ValueModifier.put(COL_PROP_XPATH, new CellEditorValueModifier<ForeignKeyFilterAnnoInfoDefUnit>("setXpath",//$NON-NLS-1$
                "getXpath"));//$NON-NLS-1$
        prop2ValueModifier.put(COL_PROP_OPERATOR, new ArrayComboCellEditorValueModifier<ForeignKeyFilterAnnoInfoDefUnit>(
                IConstants.VIEW_CONDITION_OPERATORS, "setOperator", "getOperator"));//$NON-NLS-1$//$NON-NLS-2$
        prop2ValueModifier.put(COL_PROP_VALUE, new CellEditorValueModifier<ForeignKeyFilterAnnoInfoDefUnit>("setValue",//$NON-NLS-1$
                "getValue"));//$NON-NLS-1$
        prop2ValueModifier.put(COL_PROP_PREDICATE, new ArrayComboCellEditorValueModifier<ForeignKeyFilterAnnoInfoDefUnit>(
                IConstants.PREDICATES, "setPredicate", "getPredicate"));//$NON-NLS-1$//$NON-NLS-2$
    }

    public boolean canModify(Object element, String property) {
        return (element instanceof ForeignKeyFilterAnnoInfoDefUnit) && Arrays.asList(COLPROPS).contains(property);
    }

    @Override
    protected Viewer getViewer() {
        return viewer;
    }
}
