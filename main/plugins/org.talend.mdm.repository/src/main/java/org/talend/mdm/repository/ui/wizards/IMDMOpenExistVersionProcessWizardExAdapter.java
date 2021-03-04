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
package org.talend.mdm.repository.ui.wizards;

import org.talend.core.model.repository.IRepositoryViewObject;

import com.amalto.workbench.exadapter.IExAdapter;

/**
 * created by liusongbo on 2014-3-14
 */
public interface IMDMOpenExistVersionProcessWizardExAdapter extends IExAdapter<MDMOpenExistVersionProcessWizard> {

    public boolean canOpen(IRepositoryViewObject viewObject, String originalVersion);

    public boolean performFinish(boolean createNewVersion);
}
