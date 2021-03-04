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
package com.amalto.workbench.dialogs;

import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Control;

public interface IAnnotationOrderedListsDialogExtender<R> {

    ISelectionChangedListener getViewerSelectionChangedListener(TableViewer viewer, Control textControl);

    List<String> getXPaths();

    List<R> getInput();

    String getLineLabel(Object value);

    void addXPath(int index, String xpath);

    void removeXPath(int index);

    void switchXPath(int beforeIndex, int afterIndex);

    void configTableViewer(TableViewer viewer, String[] names);

    Map<String, List<String>> getXPathMap();

}
