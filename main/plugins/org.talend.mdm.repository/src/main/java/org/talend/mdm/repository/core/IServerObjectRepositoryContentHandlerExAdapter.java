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

import org.talend.commons.ui.runtime.image.IImage;
import org.talend.core.model.repository.ERepositoryObjectType;

import com.amalto.workbench.exadapter.IExAdapter;

/**
 * created by HHB on 2014-3-24 Detailled comment
 *
 */
public interface IServerObjectRepositoryContentHandlerExAdapter extends IExAdapter<ServerObjectRepositoryContentHandler> {

    IImage getIcon(ERepositoryObjectType type);
}
