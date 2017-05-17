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
package com.amalto.workbench.actions;

import java.util.Set;

import org.eclipse.xsd.XSDSchema;

import com.amalto.workbench.exadapter.IExAdapter;

/**
 * created by HHB on 2017-5-15 Detailled comment
 *
 */
public interface IXSDElementOperationExAdapter<T> extends IExAdapter<T> {

    void renameElement(XSDSchema schema, Set<String> paths, String newName);

    void renameEntityName(XSDSchema schema, String oldEntityName, String newEntityName);
}
