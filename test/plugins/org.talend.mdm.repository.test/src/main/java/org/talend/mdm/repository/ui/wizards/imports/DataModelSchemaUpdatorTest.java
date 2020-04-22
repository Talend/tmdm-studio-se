// ============================================================================
//
// Copyright (C) 2006-2019 Talend Inc. - www.talend.com
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
import org.talend.core.model.properties.PropertiesFactory;
import org.talend.core.model.properties.Property;
import org.talend.core.model.properties.ReferenceFileItem;
import org.talend.mdm.repository.model.mdmproperties.MdmpropertiesFactory;
import org.talend.mdm.repository.model.mdmproperties.WSDataModelItem;
import org.talend.mdm.repository.model.mdmserverobject.MdmserverobjectFactory;
import org.talend.mdm.repository.model.mdmserverobject.WSDataModelE;

@SuppressWarnings("nls")
public class DataModelSchemaUpdatorTest {

    private static final Logger LOG = Logger.getLogger(DataModelSchemaUpdatorTest.class);

    @Test
    public void testUpdateDataModel() {
        String filename = "UpdateReport.xsd";
        File file = new File("temp/" + filename);
        try {
            byte[] fbyteArray = IOUtils.toByteArray(new FileInputStream(file));

            WSDataModelItem wsdataModelItem = MdmpropertiesFactory.eINSTANCE.createWSDataModelItem();
            Property property = PropertiesFactory.eINSTANCE.createProperty();
            property.setLabel("notUpdateReport");
            wsdataModelItem.setProperty(property);
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

            DataModelSchemaUpdator dataModelSchemaUpdator = Mockito.spy(new DataModelSchemaUpdator());
            Mockito.doNothing().when(dataModelSchemaUpdator).saveItem(Mockito.any(Item.class), Mockito.anyBoolean());

            boolean updated = dataModelSchemaUpdator.updateDatamodel(wsdataModelItem);
            assertFalse(updated);

            property.setLabel("UpdateReport");
            updated = dataModelSchemaUpdator.updateDatamodel(wsdataModelItem);
            assertTrue(updated);
            String xsdSchema = wsdataModelItem.getWsDataModel().getXsdSchema();
            assertNotNull(xsdSchema);
            assertTrue(xsdSchema.contains(
                    "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"PrimaryKeyInfo\" nillable=\"true\" type=\"xsd:string\"/>"));
            assertTrue(xsdSchema.contains(
                    "<xsd:element maxOccurs=\"1\" minOccurs=\"1\" name=\"UUID\" nillable=\"false\" type=\"xsd:string\"/>"));
            Mockito.verify(dataModelSchemaUpdator).saveItem(Mockito.any(Item.class), Mockito.anyBoolean());
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

}
