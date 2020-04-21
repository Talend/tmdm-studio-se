package org.talend.mdm.repository.core.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.mockito.Mockito;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ItemState;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.IServerObjectRepositoryType;

import com.amalto.workbench.service.IValidateService;

public class ValidateServiceTest {

    private static Logger log = Logger.getLogger(ValidateServiceTest.class);

    @Test
    public void testValidateObjectExistence() {
        String name = "Product"; //$NON-NLS-1$
        ERepositoryObjectType objectType = IServerObjectRepositoryType.TYPE_DATAMODEL;
        ValidateService validateService = new ValidateService();
        //
        try {
            validateService.validateObjectExistence(null, null);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }
        try {
            validateService.validateObjectExistence(null, name);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }
        try {
            validateService.validateObjectExistence(objectType, null);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        try {

            IRepositoryViewObject mockViewObj = Mockito.mock(IRepositoryViewObject.class);
            Property mockProperty = Mockito.mock(Property.class);
            Mockito.when(mockProperty.getLabel()).thenReturn(name);
            Item mockItem = Mockito.mock(Item.class);
            ItemState mockState = Mockito.mock(ItemState.class);
            Mockito.when(mockState.isDeleted()).thenReturn(true);// ////
            Mockito.when(mockItem.getState()).thenReturn(mockState);
            Mockito.when(mockProperty.getItem()).thenReturn(mockItem);
            Mockito.when(mockViewObj.getProperty()).thenReturn(mockProperty);


            validateService = mock(ValidateService.class);
            doCallRealMethod().when(validateService).validateObjectExistence(Mockito.eq(objectType), Mockito.eq(name));
            when(validateService.lookupViewObject(Mockito.eq(objectType), Mockito.eq(name))).thenReturn(mockViewObj);
            // check
            int validateResult = validateService.validateObjectExistence(objectType, name);
            assertEquals(IValidateService.STATUS_DELETED, validateResult);

            //
            Mockito.when(mockState.isDeleted()).thenReturn(false);// ////
            validateResult = validateService.validateObjectExistence(objectType, name);
            assertEquals(IValidateService.STATUS_EXISTED, validateResult);

            //
            Mockito.when(mockProperty.getItem()).thenReturn(null);
            validateResult = validateService.validateObjectExistence(objectType, name);
            assertEquals(IValidateService.STATUS_OK, validateResult);

            //
            Mockito.when(mockViewObj.getProperty()).thenReturn(mockProperty);
            validateResult = validateService.validateObjectExistence(objectType, name);
            assertEquals(IValidateService.STATUS_OK, validateResult);

            //
            when(validateService.lookupViewObject(Mockito.eq(objectType), Mockito.eq(name))).thenReturn(null);
            validateResult = validateService.validateObjectExistence(objectType, name);
            assertEquals(IValidateService.STATUS_OK, validateResult);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
