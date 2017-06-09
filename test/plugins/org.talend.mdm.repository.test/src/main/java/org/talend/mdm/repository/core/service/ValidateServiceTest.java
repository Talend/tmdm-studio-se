package org.talend.mdm.repository.core.service;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ItemState;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.mdm.repository.core.IServerObjectRepositoryType;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;
import org.talend.repository.model.IProxyRepositoryFactory;

import com.amalto.workbench.service.IValidateService;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ RepositoryResourceUtil.class, CoreRuntimePlugin.class })
@PowerMockIgnore({ "org.eclipse.core.runtime.*" })
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

            PowerMockito.mockStatic(RepositoryResourceUtil.class);
            PowerMockito.when(RepositoryResourceUtil.findViewObjectByName(any(ERepositoryObjectType.class), anyString()))
                    .thenCallRealMethod();
            PowerMockito.when(RepositoryResourceUtil.findAllViewObjectsWithDeleted(any(ERepositoryObjectType.class)))
                    .thenCallRealMethod();
            PowerMockito.when(
                    RepositoryResourceUtil.findAllViewObjects(any(ERepositoryObjectType.class), anyBoolean(), anyBoolean()))
                    .thenCallRealMethod();

            IRepositoryViewObject mockViewObj = PowerMockito.mock(IRepositoryViewObject.class);
            Property mockProperty = PowerMockito.mock(Property.class);
            PowerMockito.when(mockProperty.getLabel()).thenReturn(name);
            Item mockItem = PowerMockito.mock(Item.class);
            ItemState mockState = PowerMockito.mock(ItemState.class);
            PowerMockito.when(mockState.isDeleted()).thenReturn(true);// ////
            PowerMockito.when(mockItem.getState()).thenReturn(mockState);
            PowerMockito.when(mockProperty.getItem()).thenReturn(mockItem);
            PowerMockito.when(mockViewObj.getProperty()).thenReturn(mockProperty);
            IProxyRepositoryFactory mockRepositoryFactory = PowerMockito.mock(IProxyRepositoryFactory.class);
            List<IRepositoryViewObject> viewObjs = new ArrayList<IRepositoryViewObject>();
            viewObjs.add(mockViewObj);
            PowerMockito.when(mockRepositoryFactory.getAll(any(ERepositoryObjectType.class), anyBoolean())).thenReturn(viewObjs);
            PowerMockito.when(RepositoryResourceUtil.assertViewObject(any(IRepositoryViewObject.class))).thenReturn(mockViewObj);
            PowerMockito.when(RepositoryResourceUtil.class, "getCacheViewObject", any(Property.class), //$NON-NLS-1$
                    any(IRepositoryViewObject.class)).thenReturn(mockViewObj);

            PowerMockito.mockStatic(CoreRuntimePlugin.class);
            CoreRuntimePlugin mockCoreRuntimePlugin = PowerMockito.mock(CoreRuntimePlugin.class);
            PowerMockito.when(CoreRuntimePlugin.getInstance()).thenReturn(mockCoreRuntimePlugin);
            PowerMockito.when(mockCoreRuntimePlugin.getProxyRepositoryFactory()).thenReturn(mockRepositoryFactory);

            // check
            int validateResult = validateService.validateObjectExistence(objectType, name);
            assertEquals(IValidateService.STATUS_DELETED, validateResult);

            //
            PowerMockito.when(mockState.isDeleted()).thenReturn(false);// ////
            validateResult = validateService.validateObjectExistence(objectType, name);
            assertEquals(IValidateService.STATUS_EXISTED, validateResult);

            //
            PowerMockito.when(RepositoryResourceUtil.class, "getCacheViewObject", any(Property.class), //$NON-NLS-1$
                    any(IRepositoryViewObject.class)).thenReturn(PowerMockito.mock(IRepositoryViewObject.class));
            validateResult = validateService.validateObjectExistence(objectType, name);
            assertEquals(IValidateService.STATUS_OK, validateResult);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
