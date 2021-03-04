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
package org.talend.mdm.repository.core.service;

import org.talend.mdm.repository.core.impl.workspace.WSRootLabelProvider;

import com.amalto.workbench.exadapter.IExAdapter;


/**
 * created by liusongbo on Nov 10, 2015
 *
 */
public interface IWSRootLabelProviderExAdapter extends IExAdapter<WSRootLabelProvider> {

    public String getText(Object element);
}
