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
package org.talend.mdm.repository.ui.navigator;

import org.eclipse.jface.viewers.IDecoration;
import org.talend.commons.runtime.model.repository.ERepositoryStatus;

import com.amalto.workbench.exadapter.IExAdapter;


/**
 * created by liusongbo on Dec 7, 2015
 *
 */
public interface IMDMLockDecoratorExAdapter extends IExAdapter<MDMLockDecorator> {

    public void decorateLockImage(IDecoration decoration, ERepositoryStatus status);
}
