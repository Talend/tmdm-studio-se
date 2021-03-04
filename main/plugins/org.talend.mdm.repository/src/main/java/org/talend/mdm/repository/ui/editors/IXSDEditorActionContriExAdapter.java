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

import org.eclipse.ui.IEditorPart;

import com.amalto.workbench.exadapter.IExAdapter;

/**
 * created by HHB on 2015-9-1 Detailled comment
 *
 */
public interface IXSDEditorActionContriExAdapter extends IExAdapter<XSDEditorActionContributor> {

    void setActivePage(IEditorPart activeEditor);
}
