package org.talend.mdm.repository.ui.wizards.imports;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.talend.core.model.properties.ByteArray;
import org.talend.core.model.properties.PropertiesFactory;
import org.talend.core.model.properties.ReferenceFileItem;
import org.talend.mdm.repository.model.mdmproperties.MdmpropertiesFactory;
import org.talend.mdm.repository.model.mdmproperties.WSDataModelItem;
import org.talend.mdm.repository.model.mdmserverobject.MdmserverobjectFactory;
import org.talend.mdm.repository.model.mdmserverobject.WSDataModelE;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;

import com.amalto.workbench.exadapter.ExAdapterManager;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ RepositoryResourceUtil.class, ExAdapterManager.class })
@PowerMockIgnore({ "org.eclipse.core.runtime.*" })
public class DatamodelOperatorUpdatorTest {

    private static Logger log = Logger.getLogger(DatamodelOperatorUpdatorTest.class);

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

            PowerMockito.mockStatic(RepositoryResourceUtil.class);

            boolean updated = new DatamodelOperatorUpdator().updateConditionOperator(wsdataModelItem);
            String xsdSchema = wsdataModelItem.getWsDataModel().getXsdSchema();
            assertTrue(updated);
            assertNotNull(xsdSchema);
            assertFalse(xsdSchema.contains("Strict Contains")); //$NON-NLS-1$
            assertFalse(xsdSchema.contains("Contains Text Of")); //$NON-NLS-1$
            assertTrue(xsdSchema.contains("Contains")); //$NON-NLS-1$
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
