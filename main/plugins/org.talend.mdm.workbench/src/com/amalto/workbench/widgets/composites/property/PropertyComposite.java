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
package com.amalto.workbench.widgets.composites.property;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

import com.amalto.workbench.detailtabs.sections.BasePropertySection;
import com.amalto.workbench.providers.ColumnTextExtractor;
import com.amalto.workbench.providers.CommonTableLabelProvider;
import com.amalto.workbench.providers.ListContentProvider;

public class PropertyComposite extends Composite {

    private TreeViewer tvProperty;

    private PropertyModifier propModifier;

    private List<IPropertySource<?>> propertySources = new ArrayList<IPropertySource<?>>();

    @SuppressWarnings("unchecked")
    public PropertyComposite(Composite parent, int style, String title, String label, String propNameColLabel,
            String propValueColLabel,BasePropertySection section) {
        super(parent, style);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.verticalSpacing = 0;
        gridLayout.horizontalSpacing = 0;
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        gridLayout.numColumns = 2;
        setLayout(gridLayout);

        final Label lblTitle = new Label(this, SWT.NONE);
        final GridData gd_lblTitle = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
        lblTitle.setLayoutData(gd_lblTitle);
        lblTitle.setText((title == null ? "" : title));//$NON-NLS-1$
        hideEmptyLabel(lblTitle);

        final Label lblLabel = new Label(this, SWT.NONE);
        lblLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
        lblLabel.setText((label == null ? "" : label));//$NON-NLS-1$
        hideEmptyLabel(lblLabel);

        tvProperty = new TreeViewer(this, SWT.FULL_SELECTION | SWT.BORDER);
        propModifier = new PropertyModifier(tvProperty);
        propModifier.setSection(section);
        tvProperty.setContentProvider(new ListContentProvider());
        tvProperty.setLabelProvider(new CommonTableLabelProvider<IPropertySource<?>>(new ColumnTextExtractor[] {
                new ColumnTextExtractor<IPropertySource<?>>("getPropertyName"),//$NON-NLS-1$
                new ColumnTextExtractor<IPropertySource<?>>("getPropertyValueLabel") }));//$NON-NLS-1$
        tvProperty.setSorter(new PropertySourceSorter());
        tvProperty.setCellEditors(new CellEditor[2]);
        tvProperty.setColumnProperties(PropertyModifier.COLPROPS);
        tvProperty.setCellModifier(propModifier);
        tvProperty.setInput(propertySources);

        Tree tree = tvProperty.getTree();
        tree.setLinesVisible(true);
        tree.setHeaderVisible(true);
        tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final TreeColumn colName = new TreeColumn(tree, SWT.NONE);
        colName.setWidth(150);
        colName.setText((propNameColLabel == null ? "name" : propNameColLabel));//$NON-NLS-1$

        final TreeColumn colValue = new TreeColumn(tree, SWT.NONE);
        colValue.setWidth(330);
        colValue.setText((propValueColLabel == null ? "value" : propValueColLabel));//$NON-NLS-1$
    }

    public void setPropertySources(IPropertySource<?>[] propSources) {

        propertySources.clear();

        for (IPropertySource<?> eachPropSource : propSources)
            propertySources.add(eachPropSource);

        tvProperty.refresh();
    }

    public IPropertySource<?>[] getProperySources() {
        return propertySources.toArray(new IPropertySource[0]);
    }

    public void addPropertySource(IPropertySource<?> propSource) {

        if (propSource == null || propertySources.contains(propSource))
            return;

        propertySources.add(propSource);
        tvProperty.refresh();
    }

    public void removePropertySource(IPropertySource<?> propSource) {
        propertySources.remove(propSource);
        tvProperty.refresh();
    }

    public void clearPropertySource() {
        propertySources.clear();
        tvProperty.refresh();
    }

    public TreeViewer getPropertyViewer() {
        return tvProperty;
    }

    private void hideEmptyLabel(Label label) {

        if (!label.getText().trim().equals(""))//$NON-NLS-1$
            return;

        ((GridData) label.getLayoutData()).exclude = true;
        label.setVisible(false);
    }

    public void setEditable(boolean isEditable) {
        tvProperty.setCellModifier(isEditable ? propModifier : null);
    }
}
