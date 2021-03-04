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
package com.amalto.workbench.editors;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;

import com.amalto.workbench.exadapter.IExAdapter;


/**
 * created by liusongbo on 2014-4-29
 */
public interface IDataClusterCompositeExAdapter extends IExAdapter<DataClusterComposite> {

    public Composite createSecondPart(Composite composite);

    public void doSearch(TableViewer resultsViewer);
}
