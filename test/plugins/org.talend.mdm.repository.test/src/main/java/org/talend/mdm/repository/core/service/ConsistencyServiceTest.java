package org.talend.mdm.repository.core.service;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.PlatformUI;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ProcessItem;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.IServerObjectRepositoryType;
import org.talend.mdm.repository.core.service.ConsistencyService.ConsistencyCheckResult;
import org.talend.mdm.repository.core.service.ConsistencyService.DigestCalStrategyEnum;
import org.talend.mdm.repository.model.mdmmetadata.MDMServerDef;
import org.talend.mdm.repository.model.mdmmetadata.MdmmetadataFactory;
import org.talend.mdm.repository.model.mdmproperties.MDMServerObjectItem;
import org.talend.mdm.repository.model.mdmserverobject.MDMServerObject;
import org.talend.mdm.repository.ui.preferences.PreferenceConstants;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;
import org.talend.mdm.repository.utils.UIUtil;

import com.amalto.workbench.exadapter.ExAdapterManager;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.utils.PasswordUtil;
import com.amalto.workbench.webservices.TMDMService;
import com.amalto.workbench.webservices.WSDigest;
import com.amalto.workbench.webservices.WSDigestKey;
import com.amalto.workbench.webservices.WSLong;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ UIUtil.class, PlatformUI.class, ConsistencyService.class, RepositoryWebServiceAdapter.class,
        ERepositoryObjectType.class, RepositoryResourceUtil.class, ExAdapterManager.class, RepositoryQueryService.class })
@PowerMockIgnore({ "org.eclipse.core.runtime.*" })
public class ConsistencyServiceTest {

    private static Logger log = Logger.getLogger(ConsistencyServiceTest.class);

    @Test
    public void testCheckConsistency() {

        MDMServerDef serverDef = MdmmetadataFactory.eINSTANCE.createMDMServerDef();
        serverDef.setAlgorithm(PasswordUtil.ALGORITHM_COMMON_V2);
        String url = "http://localhost:8180/talendmdm/service/soap?wsdl"; //$NON-NLS-1$
        serverDef.parse(url);
        serverDef.setUrl(url);
        serverDef.setName("MyServer"); //$NON-NLS-1$
        serverDef.setUser("admin"); //$NON-NLS-1$
        serverDef.setPasswd("talend"); //$NON-NLS-1$

        Map<IRepositoryViewObject, Integer> viewObCmdOpjMap = new HashMap<IRepositoryViewObject, Integer>();
        IRepositoryViewObject mockViewObj = Mockito.mock(IRepositoryViewObject.class);
        int cmdType = 1;
        viewObCmdOpjMap.put(mockViewObj, cmdType);

        try {
            // isWorkInUI=false
            PowerMockito.mockStatic(UIUtil.class);
            when(UIUtil.class, "isWorkInUI").thenReturn(false); //$NON-NLS-1$
            ConsistencyService mockCService = PowerMockito.mock(ConsistencyService.class);
            PowerMockito.when(mockCService.checkConsistency(serverDef, viewObCmdOpjMap)).thenCallRealMethod();
            ConsistencyCheckResult checkResult1 = mockCService.checkConsistency(serverDef, viewObCmdOpjMap);
            assertEquals(1, checkResult1.getToDeployObjects().size());
            assertEquals(0, checkResult1.getToSkipObjects().size());
            assertEquals(mockViewObj, checkResult1.getToDeployObjects().get(0));

            // isWorkInUI=true
            when(UIUtil.class, "isWorkInUI").thenReturn(true); //$NON-NLS-1$

            // has no conflict
            PowerMockito.when(mockCService, "getConflictCount", anyMap()).thenReturn(0); //$NON-NLS-1$
            ConsistencyCheckResult checkResult2 = mockCService.checkConsistency(serverDef, viewObCmdOpjMap);
            assertEquals(1, checkResult2.getToDeployObjects().size());
            assertEquals(0, checkResult2.getToSkipObjects().size());
            assertEquals(mockViewObj, checkResult2.getToDeployObjects().get(0));

            // has (one) conflict
            PowerMockito.when(mockCService, "getConflictCount", anyMap()).thenReturn(1); //$NON-NLS-1$
            PowerMockito.when(mockCService.isWarnUserWhenConflict()).thenReturn(true);
            ConsistencyCheckResult mockResult1 = PowerMockito.mock(ConsistencyCheckResult.class);
            PowerMockito
                    .when(mockCService, "warnUserWhenConflict", eq(viewObCmdOpjMap), anyMap(), anyInt()).thenReturn(mockResult1); //$NON-NLS-1$

            ConsistencyCheckResult checkResult3 = mockCService.checkConsistency(serverDef, viewObCmdOpjMap);
            assertEquals(mockResult1, checkResult3);
            PowerMockito.verifyPrivate(mockCService, times(1)).invoke("correctCheckResult", mockResult1); //$NON-NLS-1$

            PowerMockito.when(mockCService.isWarnUserWhenConflict()).thenReturn(false);
            ConsistencyCheckResult mockResult2 = PowerMockito.mock(ConsistencyCheckResult.class);
            PowerMockito.when(mockCService, "getCheckResultByStrategy", anyInt(), anyMap(), eq(viewObCmdOpjMap)).thenReturn( //$NON-NLS-1$
                    mockResult2);

            ConsistencyCheckResult checkResult4 = mockCService.checkConsistency(serverDef, viewObCmdOpjMap);
            assertEquals(mockResult2, checkResult4);
            PowerMockito.verifyPrivate(mockCService, times(1)).invoke("correctCheckResult", mockResult2); //$NON-NLS-1$

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Test
    public void testCalculateDigestValue() {
        String digestFromObj = "digestFromObjectResource"; //$NON-NLS-1$
        String digestFromRefFile = "digestFromRefFile"; //$NON-NLS-1$
        String digestFromSortedObj = "digestFromSortedObject"; //$NON-NLS-1$

        try {
            ConsistencyService mockCService = PowerMockito.mock(ConsistencyService.class);
            PowerMockito.when(mockCService, "calculateDigestValueByObjectResource", any(Item.class)).thenReturn(digestFromObj); //$NON-NLS-1$
            PowerMockito
                    .when(mockCService, "calculateDigestValueByRefFile", any(Item.class), any(ERepositoryObjectType.class)).thenReturn(digestFromRefFile); //$NON-NLS-1$
            PowerMockito
                    .when(mockCService, "calculateDigestValueBySortedObject", any(Item.class), any(ERepositoryObjectType.class)).thenReturn(digestFromSortedObj); //$NON-NLS-1$
            PowerMockito.when(mockCService.calculateDigestValue(any(Item.class), any(ERepositoryObjectType.class)))
                    .thenCallRealMethod();

            //
            PowerMockito.when(mockCService, "getDigetValueCaculateStrategy", any(Item.class)).thenReturn( //$NON-NLS-1$
                    DigestCalStrategyEnum.OBJ_RESOURCE);
            String calculatedDigestValue = mockCService.calculateDigestValue(PowerMockito.mock(Item.class),
                    IServerObjectRepositoryType.TYPE_DATAMODEL);
            assertEquals(digestFromObj, calculatedDigestValue);

            //
            PowerMockito.when(mockCService, "getDigetValueCaculateStrategy", any(Item.class)).thenReturn( //$NON-NLS-1$
                    DigestCalStrategyEnum.REF_FILE);
            calculatedDigestValue = mockCService.calculateDigestValue(PowerMockito.mock(Item.class),
                    IServerObjectRepositoryType.TYPE_DATAMODEL);
            assertEquals(digestFromRefFile, calculatedDigestValue);

            //
            PowerMockito.when(mockCService, "getDigetValueCaculateStrategy", any(Item.class)).thenReturn( //$NON-NLS-1$
                    DigestCalStrategyEnum.SORTED_OBJ_RESOURCE);
            calculatedDigestValue = mockCService.calculateDigestValue(PowerMockito.mock(Item.class),
                    IServerObjectRepositoryType.TYPE_DATAMODEL);
            assertEquals(digestFromSortedObj, calculatedDigestValue);

            //
            PowerMockito.when(mockCService, "getDigetValueCaculateStrategy", any(Item.class)).thenReturn( //$NON-NLS-1$
                    DigestCalStrategyEnum.OTHER);
            calculatedDigestValue = mockCService.calculateDigestValue(PowerMockito.mock(Item.class),
                    IServerObjectRepositoryType.TYPE_DATAMODEL);
            assertNull(calculatedDigestValue);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Test
    public void testGetLocalDigestValue() {
        assertNull(ConsistencyService.getInstance().getLocalDigestValue(null));

        String localDigestValue = "local_digest_value_0123"; //$NON-NLS-1$
        MDMServerObject mockMDMServerObj = Mockito.mock(MDMServerObject.class);
        Mockito.when(mockMDMServerObj.getDigestValue()).thenReturn(localDigestValue);

        MDMServerObjectItem mockItem = Mockito.mock(MDMServerObjectItem.class);
        Mockito.when(mockItem.getMDMServerObject()).thenReturn(mockMDMServerObj);

        //
        assertEquals(localDigestValue, ConsistencyService.getInstance().getLocalDigestValue(mockItem));

        EMap mockProperties = Mockito.mock(EMap.class);
        Mockito.when(mockProperties.get(eq("digestValue"))).thenReturn(localDigestValue); //$NON-NLS-1$
        Property mockProperty = Mockito.mock(Property.class);
        Mockito.when(mockProperty.getAdditionalProperties()).thenReturn(mockProperties);
        ProcessItem mockProcessItem = Mockito.mock(ProcessItem.class);
        Mockito.when(mockProcessItem.getProperty()).thenReturn(mockProperty);

        //
        assertEquals(localDigestValue, ConsistencyService.getInstance().getLocalDigestValue(mockProcessItem));

    }

    @Test
    public void testGetCurrentDigestValue() {
        assertNull(ConsistencyService.getInstance().getCurrentDigestValue(null));

        String currentDigestValue = "current_digest_value_0123"; //$NON-NLS-1$
        MDMServerObject mockMDMServerObj = Mockito.mock(MDMServerObject.class);
        Mockito.when(mockMDMServerObj.getCurrentDigestValue()).thenReturn(currentDigestValue);

        MDMServerObjectItem mockItem = Mockito.mock(MDMServerObjectItem.class);
        Mockito.when(mockItem.getMDMServerObject()).thenReturn(mockMDMServerObj);

        //
        assertEquals(currentDigestValue, ConsistencyService.getInstance().getCurrentDigestValue(mockItem));

        EMap mockProperties = Mockito.mock(EMap.class);
        Mockito.when(mockProperties.get(eq("currentDigestValue"))).thenReturn(currentDigestValue); //$NON-NLS-1$
        Property mockProperty = Mockito.mock(Property.class);
        Mockito.when(mockProperty.getAdditionalProperties()).thenReturn(mockProperties);
        ProcessItem mockProcessItem = Mockito.mock(ProcessItem.class);
        Mockito.when(mockProcessItem.getProperty()).thenReturn(mockProperty);

        //
        assertEquals(currentDigestValue, ConsistencyService.getInstance().getCurrentDigestValue(mockProcessItem));
    }

    @Test
    public void testUpdateDigestValue() {
        PowerMockito.mockStatic(RepositoryResourceUtil.class);
        
        IRepositoryViewObject mockViewObj = PowerMockito.mock(IRepositoryViewObject.class);
        MDMServerObjectItem mockItem = PowerMockito.mock(MDMServerObjectItem.class);
        Property mockProperty = PowerMockito.mock(Property.class);
        PowerMockito.when(mockProperty.getItem()).thenReturn(mockItem);
        PowerMockito.when(mockItem.getProperty()).thenReturn(mockProperty);
        PowerMockito.when(mockViewObj.getProperty()).thenReturn(mockProperty);
        PowerMockito.when(RepositoryResourceUtil.assertItem(mockItem)).thenReturn(mockItem);
        PowerMockito.when(mockViewObj.getRepositoryObjectType()).thenReturn(IServerObjectRepositoryType.TYPE_DATAMODEL);

        try {
            TMDMService mockService = PowerMockito.mock(TMDMService.class);
            MDMServerDef mockServerDef = PowerMockito.mock(MDMServerDef.class);
            PowerMockito.mockStatic(RepositoryWebServiceAdapter.class);
            PowerMockito.when(RepositoryWebServiceAdapter.getMDMService(mockServerDef)).thenReturn(mockService);

            WSLong timeValue = new WSLong(11);
            PowerMockito.when(mockService.updateDigest(any(WSDigest.class))).thenReturn(timeValue);

            ConsistencyService mockCService = PowerMockito.mock(ConsistencyService.class);

            PowerMockito.doNothing().when(mockCService, "updateLocalDigestValue", mockViewObj); //$NON-NLS-1$
            String label = "Product"; //$NON-NLS-1$
            PowerMockito.when(mockCService, "getObjectName", mockViewObj).thenReturn(label); //$NON-NLS-1$
            PowerMockito.when(mockCService.getLocalDigestValue(mockItem)).thenReturn(label);
            PowerMockito
                    .when(mockCService, "updateDigestValue", any(MDMServerDef.class), any(IRepositoryViewObject.class)).thenCallRealMethod(); //$NON-NLS-1$
            
            mockCService.updateDigestValue(mockServerDef, mockViewObj);

            // check
            Mockito.verify(mockCService, times(1)).updateLocalDigestValue(mockViewObj);
            Mockito.verify(mockService, Mockito.times(1)).updateDigest(any(WSDigest.class));
            PowerMockito.verifyStatic();
            RepositoryResourceUtil.saveItem(mockItem);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Test
    public void testQueryServerDigestValue() {
        MDMServerDef serverDef = MdmmetadataFactory.eINSTANCE.createMDMServerDef();
        serverDef.setAlgorithm(PasswordUtil.ALGORITHM_COMMON_V2);
        String url = "http://localhost:8180/talendmdm/service/soap?wsdl"; //$NON-NLS-1$
        serverDef.parse(url);
        serverDef.setUrl(url);
        serverDef.setName("MyServer"); //$NON-NLS-1$
        serverDef.setUser("admin"); //$NON-NLS-1$
        serverDef.setPasswd("talend"); //$NON-NLS-1$

        String label = "Product"; //$NON-NLS-1$
        ERepositoryObjectType type = IServerObjectRepositoryType.TYPE_DATAMODEL;
        try {
            WSDigest mockDigest = PowerMockito.mock(WSDigest.class);

            TMDMService mockService = PowerMockito.mock(TMDMService.class);
            PowerMockito.when(mockService.getDigest(Mockito.any(WSDigestKey.class))).thenReturn(mockDigest);

            PowerMockito.mockStatic(RepositoryWebServiceAdapter.class);
            PowerMockito.when(RepositoryWebServiceAdapter.getMDMService(serverDef)).thenReturn(mockService);

            // IRepositoryViewObject
            IRepositoryViewObject mockViewObj = PowerMockito.mock(IRepositoryViewObject.class);
            PowerMockito.when(mockViewObj.getLabel()).thenReturn(label);

            PowerMockito.when(mockViewObj.getRepositoryObjectType()).thenReturn(type);
            Map<IRepositoryViewObject, WSDigest> queryServerDigestValue = ConsistencyService.getInstance().queryServerDigestValue(serverDef, Collections.singleton(mockViewObj));// /////////
            assertNotNull(queryServerDigestValue);
            assertTrue(queryServerDigestValue.size() == 1);
            assertEquals(mockViewObj, queryServerDigestValue.keySet().iterator().next());
            assertEquals(mockDigest, queryServerDigestValue.values().iterator().next());

            // TreeObject
            TreeObject mockTreeObj = PowerMockito.mock(TreeObject.class);
            PowerMockito.when(mockTreeObj.getDisplayName()).thenReturn(label);
            PowerMockito.when(mockTreeObj.getType()).thenReturn(1);

            PowerMockito.mockStatic(RepositoryQueryService.class);
            PowerMockito.when(RepositoryQueryService.getRepositoryObjectType(anyInt())).thenReturn(type);

            ConsistencyService mockCService = PowerMockito.mock(ConsistencyService.class);
            PowerMockito.when(mockCService, "getObjectName", any(TreeObject.class)).thenReturn(label); //$NON-NLS-1$
            PowerMockito.when(mockCService, "isSupportConsistency", any(TMDMService.class)).thenReturn(true); //$NON-NLS-1$
            PowerMockito.when(mockCService.queryServerDigestValue(any(MDMServerDef.class), anySetOf(TreeObject.class)))
                    .thenCallRealMethod();
            Map<TreeObject, WSDigest> serverDigestValue = mockCService.queryServerDigestValue(serverDef,
                    Collections.singleton(mockTreeObj));
            assertNotNull(serverDigestValue);
            assertTrue(serverDigestValue.size() >= 0);
            if (serverDigestValue.size() > 0) {
                assertEquals(mockTreeObj, serverDigestValue.keySet().iterator().next());
                assertEquals(mockDigest, serverDigestValue.values().iterator().next());
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Test
    public void testUpdateLocalDigestValue() {
        IRepositoryViewObject mockViewObj = Mockito.mock(IRepositoryViewObject.class);
        Item mockItem = Mockito.mock(Item.class);
        ERepositoryObjectType mockType = Mockito.mock(ERepositoryObjectType.class);
        Property mockProperty = Mockito.mock(Property.class);
        Mockito.when(mockProperty.getItem()).thenReturn(mockItem);
        Mockito.when(mockViewObj.getProperty()).thenReturn(mockProperty);
        Mockito.when(mockViewObj.getRepositoryObjectType()).thenReturn(mockType);

        ConsistencyService mockCService = Mockito.mock(ConsistencyService.class);
        String digestValue = "myDigestValue"; //$NON-NLS-1$
        try {
            Mockito.when(mockCService.calculateDigestValue(mockItem, mockType)).thenReturn(digestValue);
            when(mockCService, "updateLocalDigestValue", mockViewObj).thenCallRealMethod(); //$NON-NLS-1$
            mockCService.updateCurrentDigestValue(mockViewObj);

            Mockito.verify(mockCService, Mockito.atLeastOnce()).calculateDigestValue(mockItem, mockType);
            PowerMockito.verifyPrivate(mockCService, atLeastOnce()).invoke("updateLocalDigestValue", mockItem, digestValue); //$NON-NLS-1$
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Test
    public void testUpdateCurrentDigestValue() {
        IRepositoryViewObject mockViewObj = Mockito.mock(IRepositoryViewObject.class);
        Item mockItem = Mockito.mock(Item.class);
        ERepositoryObjectType mockType = Mockito.mock(ERepositoryObjectType.class);
        Property mockProperty = Mockito.mock(Property.class);
        Mockito.when(mockProperty.getItem()).thenReturn(mockItem);
        Mockito.when(mockViewObj.getProperty()).thenReturn(mockProperty);
        Mockito.when(mockViewObj.getRepositoryObjectType()).thenReturn(mockType);

        ConsistencyService mockCService = Mockito.mock(ConsistencyService.class);
        String digestValue = "myDigestValue"; //$NON-NLS-1$
        try {
            Mockito.when(mockCService.calculateDigestValue(mockItem, mockType)).thenReturn(digestValue);
            when(mockCService, "updateCurrentDigestValue", mockViewObj).thenCallRealMethod(); //$NON-NLS-1$
            mockCService.updateCurrentDigestValue(mockViewObj);

            Mockito.verify(mockCService, Mockito.atLeastOnce()).calculateDigestValue(mockItem, mockType);
            PowerMockito.verifyPrivate(mockCService, atLeastOnce()).invoke("updateCurrentDigestValue", mockItem, digestValue); //$NON-NLS-1$
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Test
    public void testIsWarnUserWhenConflict() {
        try {
            IPreferenceStore preferenceStore = Mockito.mock(IPreferenceStore.class);
            Mockito.when(preferenceStore.getBoolean(PreferenceConstants.P_WARN_USER_HAS_CONFLICT)).thenReturn(true);

            ConsistencyService mockCService = PowerMockito.mock(ConsistencyService.class);
            PowerMockito.doReturn(preferenceStore).when(mockCService, "getPreferenceStore"); //$NON-NLS-1$
            PowerMockito.when(mockCService.isWarnUserWhenConflict()).thenCallRealMethod();
            boolean warnUserWhenConflict = mockCService.isWarnUserWhenConflict();
            assertTrue(warnUserWhenConflict);

            Mockito.when(preferenceStore.getBoolean(PreferenceConstants.P_WARN_USER_HAS_CONFLICT)).thenReturn(false);
            warnUserWhenConflict = mockCService.isWarnUserWhenConflict();
            assertTrue(!warnUserWhenConflict);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }

    @Test
    public void testGetConflictStrategy() {
        try {
            IPreferenceStore preferenceStore = Mockito.mock(IPreferenceStore.class);
            Mockito.when(preferenceStore.getInt(PreferenceConstants.P_CONFLICT_STRATEGY)).thenReturn(1);

            ConsistencyService mockCService = PowerMockito.mock(ConsistencyService.class);
            PowerMockito.when(mockCService.isWarnUserWhenConflict()).thenCallRealMethod();
            PowerMockito.when(mockCService, "getPreferenceStore").thenReturn(preferenceStore); //$NON-NLS-1$
            PowerMockito.when(mockCService.getConflictStrategy()).thenCallRealMethod();
            int conflictStrategy = mockCService.getConflictStrategy();
            assertTrue(conflictStrategy == 1);

            Mockito.when(preferenceStore.getInt(PreferenceConstants.P_CONFLICT_STRATEGY)).thenReturn(2);
            conflictStrategy = mockCService.getConflictStrategy();
            assertTrue(conflictStrategy == 2);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
