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
package org.talend.mdm.repository.core.command;

import org.talend.core.model.repository.IRepositoryViewObject;

import com.amalto.workbench.exadapter.IExAdapter;

/**
 * created by HHB on 2017-3-22 Detailled comment
 *
 */
public interface ICommandManagerExAdapter extends IExAdapter<CommandManager> {

    ICommand getNewCommand(IRepositoryViewObject viewObj, int type);

    ICommand copyCommand(ICommand cmd, Object param);
}
