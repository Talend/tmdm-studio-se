// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.repository.core.service.interactive;

import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.mdm.repository.core.IServerObjectRepositoryType;
import org.talend.mdm.repository.core.command.deploy.AbstractDeployCommand;
import org.talend.mdm.repository.i18n.Messages;

import com.amalto.workbench.utils.EXtentisObjects;
import com.amalto.workbench.utils.TreeObjectUtil;
import com.amalto.workbench.utils.XtentisException;
import com.amalto.workbench.webservices.TMDMService;
import com.amalto.workbench.webservices.WSDeleteTransformerV2;
import com.amalto.workbench.webservices.WSPutTransformerV2;
import com.amalto.workbench.webservices.WSTransformerV2;
import com.amalto.workbench.webservices.WSTransformerV2PK;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class TransformerInteractiveHandler extends AbstractInteractiveHandler {

    public ERepositoryObjectType getRepositoryObjectType() {
        return IServerObjectRepositoryType.TYPE_TRANSFORMERV2;
    }

    public String getLabel() {

        return Messages.TransformerInteractiveHandler_label;
    }

    @Override
    public boolean doDeployWSObject(TMDMService service, Object wsObj) {
        if (wsObj != null) {
            service.putTransformerV2(new WSPutTransformerV2((WSTransformerV2) wsObj));
            return true;
        }
        return false;
    }

    @Override
    public boolean doRemove(TMDMService service, AbstractDeployCommand cmd) throws XtentisException {
        WSTransformerV2PK pk = new WSTransformerV2PK();
        String name = cmd.getObjName();
        pk.setPk(name);
        service.deleteTransformerV2(new WSDeleteTransformerV2(pk));
        TreeObjectUtil.deleteSpecificationFromAttachedRole(service, name, EXtentisObjects.Transformer.getName());
        return true;
    }
}
