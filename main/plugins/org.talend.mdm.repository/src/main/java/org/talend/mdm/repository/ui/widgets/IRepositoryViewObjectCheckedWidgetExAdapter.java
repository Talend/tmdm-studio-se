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
package org.talend.mdm.repository.ui.widgets;

import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.talend.core.model.repository.IRepositoryViewObject;

import com.amalto.workbench.exadapter.IExAdapter;

/**
 * created by HHB on 2014-5-20 Detailled comment
 *
 */
public interface IRepositoryViewObjectCheckedWidgetExAdapter extends IExAdapter<RepositoryViewObjectCheckedWidget> {

    public void initSeverityControl(Composite parent);

    public void updateSeverityCount(List<IRepositoryViewObject> selectededViewObjs);
}
