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
package org.talend.mdm.repository.core.hash;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.talend.mdm.commmon.util.hash.IHashValueCalculator;
import org.talend.mdm.repository.core.hash.routingrule.EObjRoutingRuleHVCalculator;
import org.talend.mdm.repository.core.hash.routingrule.WSRoutingRuleHVCalculator;
import org.talend.mdm.repository.model.mdmserverobject.MdmserverobjectFactory;
import org.talend.mdm.repository.model.mdmserverobject.WSRoutingRuleE;
import org.talend.mdm.repository.model.mdmserverobject.WSRoutingRuleExpressionE;
import org.talend.mdm.repository.model.mdmserverobject.WSRoutingRuleOperatorE;

import com.amalto.workbench.webservices.WSRoutingRule;
import com.amalto.workbench.webservices.WSRoutingRuleExpression;
import com.amalto.workbench.webservices.WSRoutingRuleOperator;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class RoutingRuleTest {

    private List<String> operators;

    @Before
    public void initOperators() {
        if (operators == null) {
            operators = new ArrayList<String>();
        }

        operators.clear();
        WSRoutingRuleOperator[] values = WSRoutingRuleOperator.values();
        for (WSRoutingRuleOperator operator : values) {
            operators.add(operator.name());
        }
    }

    private WSRoutingRule initWSObject() {
        WSRoutingRule rule = new WSRoutingRule();
        rule.setName("ruleNameA"); //$NON-NLS-1$
        rule.setConcept("concept"); //$NON-NLS-1$
        rule.setCondition("condition"); //$NON-NLS-1$
        rule.setDeactive(true);
        rule.setDescription("description"); //$NON-NLS-1$
        rule.setParameters("parameters"); //$NON-NLS-1$
        rule.setServiceJNDI("serviceJNDI"); //$NON-NLS-1$
        rule.setSynchronous(false);

        for (int i = 0; i < operators.size(); i++) {
            WSRoutingRuleExpression exp = new WSRoutingRuleExpression();
            exp.setName("expression" + i); //$NON-NLS-1$
            exp.setValue("value" + i); //$NON-NLS-1$
            exp.setXpath("xpath" + i); //$NON-NLS-1$
            WSRoutingRuleOperator o1 = WSRoutingRuleOperator.valueOf(operators.get(i));
            exp.setWsOperator(o1);

            rule.getWsRoutingRuleExpressions().add(exp);
        }

        return rule;
    }

    private WSRoutingRuleE initEObjRule() {
        WSRoutingRuleE rule = MdmserverobjectFactory.eINSTANCE.createWSRoutingRuleE();
        rule.setName("ruleNameA"); //$NON-NLS-1$
        rule.setConcept("concept"); //$NON-NLS-1$
        rule.setCondition("condition"); //$NON-NLS-1$
        rule.setDeactive(true);
        rule.setDescription("description"); //$NON-NLS-1$
        rule.setParameters("parameters"); //$NON-NLS-1$
        rule.setServiceJNDI("serviceJNDI"); //$NON-NLS-1$
        rule.setSynchronous(false);

        for (int i = 0; i < operators.size(); i++) {
            WSRoutingRuleExpressionE exp = MdmserverobjectFactory.eINSTANCE.createWSRoutingRuleExpressionE();
            exp.setName("expression" + i); //$NON-NLS-1$
            exp.setValue("value" + i); //$NON-NLS-1$
            exp.setXpath("xpath" + i); //$NON-NLS-1$
            //
            WSRoutingRuleOperatorE o1 = MdmserverobjectFactory.eINSTANCE.createWSRoutingRuleOperatorE();
            o1.setValue(operators.get(i));
            exp.setWsOperator(o1);

            rule.getWsRoutingRuleExpressions().add(exp);
        }

        return rule;
    }

    @Test
    public void testHash() {
        IHashValueCalculator wsCalculator = new WSRoutingRuleHVCalculator();
        IHashValueCalculator eobjCalculator = new EObjRoutingRuleHVCalculator();
        WSRoutingRule wsRule = initWSObject();
        WSRoutingRuleE eobjRule = initEObjRule();
        long wsHash = wsCalculator.calculateHash(wsRule);
        long eobjHash = eobjCalculator.calculateHash(eobjRule);
        assertEquals(wsHash, eobjHash);
    }

}
