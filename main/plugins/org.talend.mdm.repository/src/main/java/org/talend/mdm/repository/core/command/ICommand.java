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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.navigator.IMementoAware;
import org.talend.core.model.repository.IRepositoryViewObject;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public interface ICommand extends IMementoAware {

    public static final String PROP_ID = "id"; //$NON-NLS-1$

    public static final String PROP_TYPE = "type"; //$NON-NLS-1$

    public static final String PROP_OBJ_NAME = "objName"; //$NON-NLS-1$

    public static final String PROP_LAST_OBJ_NAME = "lastObjName"; //$NON-NLS-1$

    public static final String PROP_PHASE = "runPhase"; //$NON-NLS-1$

    public static final int CMD_NOP = 0;

    public static final int CMD_ADD = 1;

    public static final int CMD_MODIFY = 2;

    public static final int CMD_RENAME = 4;

    public static final int CMD_DELETE = 8;

    public static final int CMD_RESTORE = 16;

    public static final int CMD_UPDATE_SERVER = 32;

    public static final int CMD_PUSH_COMMAND = 1024;

    //
    public static final int PHASE_DEPLOY = 1;

    public static final int PHASE_AFTER_DEPLOY = 2;

    public static final int PHASE_RESTORE = 4;

    public static final String MEM_TYPE_COMMAND = "Command"; //$NON-NLS-1$

    public static final String MEM_TYPE_COMMAND_STACK = "CommandStack"; //$NON-NLS-1$

    public static final String MDM_COMMANDS = "MDM.COMMANDS"; //$NON-NLS-1$

    //
    int getCommandType();

    void setCommandId(String id);

    String getCommandId();

    String getObjName();

    String getObjLastName();

    int getToRunPhase();

    void setToRunPhase(int phase);

    IRepositoryViewObject getViewObject();

    void init(IRepositoryViewObject viewObj);

    void init(String id, Object param);

    void init();

    IStatus execute(Object params, IProgressMonitor monitor);

    boolean canExecute(Object params);

    void updateViewObject(IRepositoryViewObject viewObj);

    String getVersion();

    void setVersion(String version);
}
