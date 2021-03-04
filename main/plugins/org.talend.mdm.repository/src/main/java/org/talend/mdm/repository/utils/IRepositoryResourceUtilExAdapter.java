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
package org.talend.mdm.repository.utils;

import org.eclipse.ui.IEditorReference;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.IRepositoryViewObject;

import com.amalto.workbench.exadapter.IExAdapter;

/**
 * created by HHB on 2014-1-13 Detailled comment
 *
 */
public interface IRepositoryResourceUtilExAdapter extends IExAdapter<RepositoryResourceUtil> {

    IEditorReference getOpenedWFEditor(IRepositoryViewObject viewObj, IEditorReference ref);

    /**
     * This method is only defined for importing workflow.<br><br>
     * It relys on implementation of IProcessService to get config file name with file extension, which config file name is stored during workflow migration.<br>
     *
     * <b>Runtime Exception</b> will be thrown when IProcessService implementation is not registered.<br>
     * <b>Null</b> will be returned when there is no config file recorded during migration.
     */
    String getWorkflowConfigFilename(Item item);
}
