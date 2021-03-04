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
package org.talend.mdm.repository.core.datacontent;

import java.util.LinkedList;
import java.util.List;

/**
 * created by HHB on 2012-10-8 Detailled comment
 *
 */
public class DataProcessRule {

    List<DataEntityUnit> entityUnits = new LinkedList<DataEntityUnit>();

    /**
     * Getter for entityUnits.
     *
     * @return the entityUnits
     */
    public List<DataEntityUnit> getEntityUnits() {
        if (entityUnits == null) {
            entityUnits = new LinkedList<DataEntityUnit>();
        }
        return this.entityUnits;
    }

    /**
     * Sets the entityUnits.
     *
     * @param entityUnits the entityUnits to set
     */
    public void setEntityUnits(List<DataEntityUnit> entityUnits) {
        this.entityUnits = entityUnits;
    }

    public List<DataEntityUnit> returnSelectedEntityUnits() {
        List<DataEntityUnit> selectedUnits = new LinkedList<DataEntityUnit>();
        for (DataEntityUnit unit : getEntityUnits()) {
            if (unit.isSelected()) {
                selectedUnits.add(unit);
            }
        }
        return selectedUnits;
    }

    public void addNewEnityUnit(String entityName) {
        DataEntityUnit entityUnit = new DataEntityUnit(entityName);
        getEntityUnits().add(entityUnit);
    }

    public void addNewEnityUnit(DataEntityUnit entityUnit) {
        getEntityUnits().add(entityUnit);
    }
}
