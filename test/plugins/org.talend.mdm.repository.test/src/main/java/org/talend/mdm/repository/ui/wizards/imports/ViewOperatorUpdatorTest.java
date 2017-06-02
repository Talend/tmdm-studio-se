package org.talend.mdm.repository.ui.wizards.imports;

import static org.junit.Assert.*;

import org.junit.Test;
import org.talend.mdm.repository.model.mdmproperties.MdmpropertiesFactory;
import org.talend.mdm.repository.model.mdmproperties.WSViewItem;
import org.talend.mdm.repository.model.mdmserverobject.MdmserverobjectFactory;
import org.talend.mdm.repository.model.mdmserverobject.WSViewE;
import org.talend.mdm.repository.model.mdmserverobject.WSWhereConditionE;
import org.talend.mdm.repository.model.mdmserverobject.WSWhereOperatorE;

import com.amalto.workbench.webservices.WSWhereOperator;

public class ViewOperatorUpdatorTest {

    @Test
    public void test() {
        WSViewItem wsViewItem = MdmpropertiesFactory.eINSTANCE.createWSViewItem();
        WSViewE wsViewE = MdmserverobjectFactory.eINSTANCE.createWSViewE();
        wsViewItem.setWsView(wsViewE);

        String customAppend = "_Customed"; //$NON-NLS-1$
        String customContianOperator = WSWhereOperator.CONTAINS.name() + customAppend;

        WSWhereConditionE conditionA = MdmserverobjectFactory.eINSTANCE.createWSWhereConditionE();
        wsViewE.getWhereConditions().add(conditionA);
        WSWhereOperatorE operatorA = MdmserverobjectFactory.eINSTANCE.createWSWhereOperatorE();
        operatorA.setValue(customContianOperator);
        conditionA.setOperator(operatorA);

        WSWhereConditionE conditionB = MdmserverobjectFactory.eINSTANCE.createWSWhereConditionE();
        wsViewE.getWhereConditions().add(conditionB);
        WSWhereOperatorE operatorB = MdmserverobjectFactory.eINSTANCE.createWSWhereOperatorE();
        operatorB.setValue(WSWhereOperator.JOIN.name());
        conditionB.setOperator(operatorB);

        boolean updated = new ViewOperatorUpdator().updateConditionOperator(wsViewItem);
        assertTrue(updated);
        assertEquals(WSWhereOperator.CONTAINS.name(), operatorA.getValue());
        assertEquals(WSWhereOperator.JOIN.name(), operatorB.getValue());
    }

}
