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
package org.talend.mdm.repository.core.service.interactive;

import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.mdm.repository.core.IServerObjectRepositoryType;
import org.talend.mdm.repository.core.command.deploy.AbstractDeployCommand;
import org.talend.mdm.repository.i18n.Messages;

import com.amalto.workbench.utils.EXtentisObjects;
import com.amalto.workbench.utils.TreeObjectUtil;
import com.amalto.workbench.utils.XtentisException;
import com.amalto.workbench.webservices.TMDMService;
import com.amalto.workbench.webservices.WSDeleteRoutingRule;
import com.amalto.workbench.webservices.WSPutRoutingRule;
import com.amalto.workbench.webservices.WSRoutingRule;
import com.amalto.workbench.webservices.WSRoutingRulePK;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class RoutingRuleInteractiveHandler extends AbstractInteractiveHandler {

    public ERepositoryObjectType getRepositoryObjectType() {
        return IServerObjectRepositoryType.TYPE_ROUTINGRULE;
    }

    public String getLabel() {

        return Messages.RoutingRuleInteractiveHandler_label;
    }

    @Override
    public boolean doDeployWSObject(TMDMService service, Object wsObj) {
        if (wsObj != null) {
            service.putRoutingRule(new WSPutRoutingRule((WSRoutingRule) wsObj));
            return true;
        }
        return false;
    }

    @Override
    public boolean doRemove(TMDMService service, AbstractDeployCommand cmd) throws XtentisException {
        WSRoutingRulePK pk = new WSRoutingRulePK();
        String name = cmd.getObjName();
        pk.setPk(name);
        service.deleteRoutingRule(new WSDeleteRoutingRule(pk));
        TreeObjectUtil.deleteSpecificationFromAttachedRole(service, name, EXtentisObjects.RoutingRule.getName());
        return true;
    }
}
