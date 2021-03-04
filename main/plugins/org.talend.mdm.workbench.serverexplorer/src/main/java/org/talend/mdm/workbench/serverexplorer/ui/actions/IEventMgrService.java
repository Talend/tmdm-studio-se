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
package org.talend.mdm.workbench.serverexplorer.ui.actions;

import org.talend.core.IService;
import org.talend.mdm.repository.model.mdmmetadata.MDMServerDef;


public interface IEventMgrService extends IService {

    void run(MDMServerDef serverDef);

    void run();
}
