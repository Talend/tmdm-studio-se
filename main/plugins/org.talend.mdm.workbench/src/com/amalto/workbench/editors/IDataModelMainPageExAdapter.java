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
package com.amalto.workbench.editors;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.talend.mdm.webservice.TMDMService;

import com.amalto.workbench.exadapter.IExAdapter;

/**
 * created by HHB on 2014-1-6 Detailled comment
 * 
 */
public interface IDataModelMainPageExAdapter extends IExAdapter<DataModelMainPage> {

    /**
     * DOC HHB Comment method "doSave".
     * 
     * @param port
     * @param name
     * @param dataModelText
     */
    void doSave(TMDMService service, String name, String dataModelText);

    ToolItem createFilterToolItem(ToolBar parentToolBar, final TreeViewer tTreeViewer);

    void createActions();

    void fillContextMenu(IMenuManager manager);

    void fillContextMenu(IMenuManager manager, Object[] selectedObjs);

    void setAnnotationActions(Object obj, IMenuManager manager);

    void setAnnotationActions2(Object obj, IMenuManager manager);

    void doubleClick(int elem);
}
