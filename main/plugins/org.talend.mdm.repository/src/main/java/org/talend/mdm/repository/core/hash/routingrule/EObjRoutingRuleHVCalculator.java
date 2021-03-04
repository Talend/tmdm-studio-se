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
package org.talend.mdm.repository.core.hash.routingrule;

import org.talend.mdm.commmon.util.hash.ArrayCollectionHashValueCalculator;
import org.talend.mdm.commmon.util.hash.IHashValueCalculator;
import org.talend.mdm.commmon.util.hash.impl.RoutingRuleHVCalculator;
import org.talend.mdm.repository.model.mdmserverobject.WSRoutingRuleE;
import org.talend.mdm.repository.model.mdmserverobject.WSRoutingRuleExpressionE;
import org.talend.mdm.repository.model.mdmserverobject.WSRoutingRuleOperatorE;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class EObjRoutingRuleHVCalculator extends RoutingRuleHVCalculator {

    /**
     * 1- name; 2- description; 3- synchronous; 4- concept; 5- serviceJNDI; 6- parameters; 7- condition; 8- deactive; 9-
     * WSRoutingRuleExpressionE
     */
    @Override
    public Object[] getPropertys(Object obj) {
        if (obj == null || !(obj instanceof WSRoutingRuleE)) {
            throw new IllegalArgumentException();
        }
        WSRoutingRuleE rule = (WSRoutingRuleE) obj;
        return new Object[] { rule.getName(), rule.getDescription(), rule.isSynchronous(), rule.getConcept(),
                rule.getServiceJNDI(), rule.getParameters(), rule.getCondition(), rule.isDeactive(),
                rule.getWsRoutingRuleExpressions() };
    }

    class EobjRoutinRuleExpressionHVCalculator extends RoutingRuleExpressionHVCalculator {

        // 1-name 2-xpath 3-value 4-wsOperator
        @Override
        public Object[] getPropertys(Object obj) {
            if (obj == null || !(obj instanceof WSRoutingRuleExpressionE)) {
                throw new IllegalArgumentException();
            }
            WSRoutingRuleExpressionE expression = (WSRoutingRuleExpressionE) obj;
            return new Object[] { expression.getName(), expression.getXpath(), expression.getValue(), expression.getWsOperator() };
        }
    };

    IHashValueCalculator routinRuleArrayExpressionHVCalculator;

    @Override
    protected IHashValueCalculator getRoutinRuleExpressionHVCalculator() {
        if (routinRuleArrayExpressionHVCalculator == null) {

            routinRuleArrayExpressionHVCalculator = new ArrayCollectionHashValueCalculator(
                    new EobjRoutinRuleExpressionHVCalculator());
        }
        return routinRuleArrayExpressionHVCalculator;
    }

    protected IHashValueCalculator routingRuleOperatorHVCalculator;

    class EobjRoutingRuleOperatorHVCalculator extends RoutingRuleOperatorHVCalculator {

        // 1 value
        @Override
        public Object[] getPropertys(Object obj) {
            if (obj == null || !(obj instanceof WSRoutingRuleOperatorE)) {
                throw new IllegalArgumentException();
            }
            WSRoutingRuleOperatorE operator = (WSRoutingRuleOperatorE) obj;
            return new Object[] { operator.getValue() };
        }
    };

    @Override
    protected IHashValueCalculator getRoutingRuleOperatorHVCalculator() {
        if (routingRuleOperatorHVCalculator == null) {
            routingRuleOperatorHVCalculator = new EobjRoutingRuleOperatorHVCalculator();
        }
        return routingRuleOperatorHVCalculator;
    }
}
