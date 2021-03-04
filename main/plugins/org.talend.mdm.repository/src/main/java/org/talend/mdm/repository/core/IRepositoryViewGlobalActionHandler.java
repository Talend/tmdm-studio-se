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
package org.talend.mdm.repository.core;

import org.eclipse.ui.navigator.ICommonActionConstants;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public interface IRepositoryViewGlobalActionHandler {

    public static final String REFRESH = "org.eclipse.ui.file.refresh"; //$NON-NLS-1$

    public static final String COPY = "com.talend.mdm.repository.copy"; //$NON-NLS-1$

    public static final String PASTE = "com.talend.mdm.repository.paste"; //$NON-NLS-1$

    public static final String OPEN = ICommonActionConstants.OPEN;

    public AbstractRepositoryAction getGlobalAction(String actionId);
}
