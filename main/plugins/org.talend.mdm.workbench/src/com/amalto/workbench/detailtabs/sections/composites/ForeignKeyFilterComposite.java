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
package com.amalto.workbench.detailtabs.sections.composites;

import java.util.ArrayList;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

import com.amalto.workbench.detailtabs.sections.BasePropertySection;
import com.amalto.workbench.detailtabs.sections.model.annotationinfo.relationship.ForeignKeyFilterAnnoInfo;
import com.amalto.workbench.detailtabs.sections.model.annotationinfo.relationship.ForeignKeyFilterAnnoInfoDefUnit;
import com.amalto.workbench.detailtabs.sections.providers.ForeignKeyFilterCellModifier;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.models.infoextractor.IAllDataModelHolder;
import com.amalto.workbench.providers.ColumnTextExtractor;
import com.amalto.workbench.providers.CommonTableLabelProvider;
import com.amalto.workbench.utils.IConstants;
import com.amalto.workbench.widgets.celleditor.XPathCellEditor;
import com.amalto.workbench.widgets.composites.ComplexAnnotaionInfoComposite;

public class ForeignKeyFilterComposite extends ComplexAnnotaionInfoComposite<ForeignKeyFilterAnnoInfoDefUnit> {
    private IAllDataModelHolder dataModelHolder;

    public ForeignKeyFilterComposite(Composite parent, int style, IAllDataModelHolder dataModelHolder,BasePropertySection section) {
    	super(parent,style,new Object[] { dataModelHolder },section);

    }
    public ForeignKeyFilterComposite(Composite parent, int style, IAllDataModelHolder dataModelHolder) {
        super(parent, style, new Object[] { dataModelHolder },null);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected ITableLabelProvider getLabelProviderForViewer() {
        return new CommonTableLabelProvider<ForeignKeyFilterAnnoInfoDefUnit>(new ColumnTextExtractor[] {
                new ColumnTextExtractor<ForeignKeyFilterAnnoInfoDefUnit>("getXpath"),//$NON-NLS-1$
                new ColumnTextExtractor<ForeignKeyFilterAnnoInfoDefUnit>("getOperator"),//$NON-NLS-1$
                new ColumnTextExtractor<ForeignKeyFilterAnnoInfoDefUnit>("getValue"),//$NON-NLS-1$
                new ColumnTextExtractor<ForeignKeyFilterAnnoInfoDefUnit>("getPredicate") });//$NON-NLS-1$
    }

    @Override
    protected void createExtentArea() {
    }

    @Override
    protected void fillColumnsInTree(Tree tree) {

        final TreeColumn colXPath = new TreeColumn(tree, SWT.NONE);
        colXPath.setWidth(200);
        colXPath.setText(Messages.ForeignKeyFilterComposite_XPath);
        colXPath.setImage(nillableColImage);

        final TreeColumn colOperator = new TreeColumn(tree, SWT.NONE);
        colOperator.setWidth(100);
        colOperator.setText(Messages.ForeignKeyFilterComposite_Operator);
        colOperator.setImage(nillableColImage);

        final TreeColumn colValue = new TreeColumn(tree, SWT.NONE);
        colValue.setWidth(200);
        colValue.setText(Messages.ForeignKeyFilterComposite_Value);
        colValue.setImage(nillableColImage);

        final TreeColumn colPredicate = new TreeColumn(tree, SWT.NONE);
        colPredicate.setWidth(100);
        colPredicate.setText(Messages.ForeignKeyFilterComposite_Predicate);

    }

    @Override
    protected ForeignKeyFilterAnnoInfoDefUnit createDefaultInfoObj() {
        return new ForeignKeyFilterAnnoInfoDefUnit("newXPath", IConstants.VIEW_CONDITION_OPERATORS[0], "",//$NON-NLS-1$//$NON-NLS-2$
                IConstants.PREDICATES[0]);
    }

    @Override
    protected boolean validateBeforePaste(ForeignKeyFilterAnnoInfoDefUnit copyedObj) {
        return true;
    }

    @Override
    protected ForeignKeyFilterAnnoInfoDefUnit cloneCopyedObj(ForeignKeyFilterAnnoInfoDefUnit copyedObj) {
        return new ForeignKeyFilterAnnoInfoDefUnit(copyedObj.getXpath(), copyedObj.getOperator(), copyedObj.getValue(),
                copyedObj.getPredicate());
    }

    @Override
    protected String[] getColumnProperties() {
        return ForeignKeyFilterCellModifier.COLPROPS;
    }

    @Override
    protected CellEditor[] getCellEditors() {
        return new CellEditor[] { new XPathCellEditor(tvInfos.getTree(), dataModelHolder),
                new ComboBoxCellEditor(tvInfos.getTree(), IConstants.VIEW_CONDITION_OPERATORS, SWT.READ_ONLY),
                new TextCellEditor(tvInfos.getTree()),
                new ComboBoxCellEditor(tvInfos.getTree(), IConstants.PREDICATES, SWT.READ_ONLY) };
    }

    @Override
    protected ICellModifier getCellModifier() {
        ForeignKeyFilterCellModifier modifier=new ForeignKeyFilterCellModifier(tvInfos);
        modifier.setSection(section);
        return modifier;
    }

    @Override
    protected void initParameters(Object[] parameters) {
        infos = new ArrayList<ForeignKeyFilterAnnoInfoDefUnit>();

        for (Object eachPara : parameters) {

            if (eachPara instanceof IAllDataModelHolder) {
                dataModelHolder = (IAllDataModelHolder) eachPara;
            }
        }
    }

    public String getFilterExpression() {
        return ForeignKeyFilterAnnoInfo.getFKFilterByFKFilterCfgInfos(infos.toArray(new ForeignKeyFilterAnnoInfoDefUnit[0]));
    }

    public void setFilter(String filterExpression) {
        setInfos(ForeignKeyFilterAnnoInfo.getFKFilterCfgInfos(filterExpression));
    }

    @Override
    protected void createTopExtentArea() {
    }
}
