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
package org.talend.mdm.repository.ui.wizards.imports;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.mockito.Mockito;
import org.talend.core.model.properties.ByteArray;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ItemState;
import org.talend.core.model.properties.PropertiesFactory;
import org.talend.core.model.properties.ReferenceFileItem;
import org.talend.mdm.repository.model.mdmproperties.MdmpropertiesFactory;
import org.talend.mdm.repository.model.mdmproperties.WSDataModelItem;
import org.talend.mdm.repository.model.mdmserverobject.MdmserverobjectFactory;
import org.talend.mdm.repository.model.mdmserverobject.WSDataModelE;

public class DataModelOperatorUpdatorTest {

    private static final Logger LOG = Logger.getLogger(DataModelOperatorUpdatorTest.class);

    @Test
    public void testUpdateConditionOperator() {
        String filename = "Product_0.1.xsd"; //$NON-NLS-1$
        File file = new File("temp/" + filename); //$NON-NLS-1$
        try {
            byte[] fbyteArray = IOUtils.toByteArray(new FileInputStream(file));

            WSDataModelItem wsdataModelItem = MdmpropertiesFactory.eINSTANCE.createWSDataModelItem();

            ReferenceFileItem xsdFileItem = PropertiesFactory.eINSTANCE.createReferenceFileItem();
            ByteArray byteArray = PropertiesFactory.eINSTANCE.createByteArray();
            byteArray = spy(byteArray);//
            when(byteArray.getInnerContent()).thenReturn(fbyteArray);//

            xsdFileItem.setContent(byteArray);
            xsdFileItem.setExtension("xsd");
            wsdataModelItem.getReferenceResources().add(xsdFileItem);

            WSDataModelE wsdataModelE = MdmserverobjectFactory.eINSTANCE.createWSDataModelE();
            wsdataModelE.setXsdSchema(null);
            wsdataModelItem.setWsDataModel(wsdataModelE);
            ItemState itemState = PropertiesFactory.eINSTANCE.createItemState();
            itemState.setPath("system");
            wsdataModelItem.setState(itemState);

            DataModelOperatorUpdator dataModelOperatorUpdator = Mockito.spy(new DataModelOperatorUpdator());
            Mockito.doNothing().when(dataModelOperatorUpdator).saveItem(Mockito.any(Item.class), Mockito.anyBoolean());

            boolean updated = dataModelOperatorUpdator.updateConditionOperator(wsdataModelItem);
            assertFalse(updated);

            itemState.setPath("");
            updated = dataModelOperatorUpdator.updateConditionOperator(wsdataModelItem);
            assertTrue(updated);
            String xsdSchema = wsdataModelItem.getWsDataModel().getXsdSchema();
            assertNotNull(xsdSchema);
            assertFalse(xsdSchema.contains("Strict Contains")); //$NON-NLS-1$
            assertFalse(xsdSchema.contains("Contains Text Of")); //$NON-NLS-1$
            assertTrue(xsdSchema.contains("Contains")); //$NON-NLS-1$
            Mockito.verify(dataModelOperatorUpdator).saveItem(Mockito.any(Item.class), Mockito.anyBoolean());
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

}
