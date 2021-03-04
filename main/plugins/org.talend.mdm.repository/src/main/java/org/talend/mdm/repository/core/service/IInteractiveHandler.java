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

import java.util.List;

import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.command.deploy.AbstractDeployCommand;
import org.talend.mdm.repository.model.mdmserverobject.MDMServerObject;

import com.amalto.workbench.utils.XtentisException;
import com.amalto.workbench.webservices.TMDMService;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public interface IInteractiveHandler {

    ERepositoryObjectType getRepositoryObjectType();

    String getLabel();

    Object convert(Item item, MDMServerObject serverObj);

    boolean deploy(AbstractDeployCommand cmd) throws XtentisException;

    boolean remove(AbstractDeployCommand cmd) throws XtentisException;

    boolean doDeployWSObject(TMDMService service, Object wsObj) throws XtentisException;

    void assertPropertyIsInited(Item item);

    List<IRepositoryViewObject> getAssociatedObjects(IRepositoryViewObject obj);

    boolean isShownInResultDialog(IRepositoryViewObject viewObj);

}
