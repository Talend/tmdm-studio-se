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
package com.amalto.workbench.service.bridge;

import org.talend.core.IService;

/**
 * created by liusongbo on 2012-9-28
 *
 */
public interface ITransformService extends IService {

    public String transformToSilyViewName(final String internalName, boolean withDirName);

    public String transformToSilyProcessName(final String internalName, boolean withDirName);

    public int getProcessType(String processName);

    public String getProcessPath(String processName, boolean withSeperator);

    public int getViewType(String viewName);

    public String getViewPath(String viewName);
}
