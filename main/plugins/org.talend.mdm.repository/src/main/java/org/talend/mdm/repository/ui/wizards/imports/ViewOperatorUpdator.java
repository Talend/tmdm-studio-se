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
package org.talend.mdm.repository.ui.wizards.imports;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.talend.core.model.properties.Item;
import org.talend.mdm.repository.model.mdmproperties.WSViewItem;
import org.talend.mdm.repository.model.mdmserverobject.WSViewE;
import org.talend.mdm.repository.model.mdmserverobject.WSWhereConditionE;
import org.talend.mdm.repository.model.mdmserverobject.WSWhereOperatorE;

import com.amalto.workbench.webservices.WSWhereOperator;


/**
 * created by liusongbo on Apr 14, 2016
 *
 */
public class ViewOperatorUpdator implements IOperatorUpdator {

    private List<String> operators = null;

    public boolean updateConditionOperator(Item item) {
        boolean modified = false;
        List<String> opers = getOperators();
        
        if (item != null && item instanceof WSViewItem) {

            WSViewItem viewItem = (WSViewItem) item;
            WSViewE wsView = viewItem.getWsView();
            EList<WSWhereConditionE> whereConditions = wsView.getWhereConditions();
            if (whereConditions != null) {
                for (WSWhereConditionE whereConditionE : whereConditions) {
                    WSWhereOperatorE operator = whereConditionE.getOperator();
                    if (!opers.contains(operator.getValue())) {
                        operator.setValue(WSWhereOperator.CONTAINS.name());

                        modified = true;
                    }
                }
            }
        }

        return modified;
    }

    protected List<String> getOperators() {
        if (operators == null) {
            operators = new ArrayList<String>();
            WSWhereOperator[] values = WSWhereOperator.values();
            for (WSWhereOperator op : values) {
                operators.add(op.name());
            }
        }

        return operators;
    }

}
