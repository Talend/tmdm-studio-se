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
package org.talend.mdm.repository.ui.editors;

import org.eclipse.ui.IEditorInput;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.IRepositoryViewObject;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public interface IRepositoryViewEditorInput extends IEditorInput {

    public String getEditorId();

    public Item getInputItem();

    public IRepositoryViewObject getViewObject();

    public boolean isReadOnly();

    public void setReadOnly(boolean readOnly);

    public String getVersion();

    public void setVersion(String version);
}
