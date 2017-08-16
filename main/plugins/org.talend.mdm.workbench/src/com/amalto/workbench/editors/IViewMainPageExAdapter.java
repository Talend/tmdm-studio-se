// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.amalto.workbench.exadapter.IExAdapter;
import com.amalto.workbench.webservices.WSView;


/**
 * @author sbliu
 *
 */
public interface IViewMainPageExAdapter extends IExAdapter<ViewMainPage> {

    void createComposite(ViewMainPage viewMainPage, FormToolkit toolkit, Composite parent);

    void refreshData(WSView wsObject, String datamodel);

    void saveData(WSView wsObject);

}
