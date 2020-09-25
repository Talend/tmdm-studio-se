// ============================================================================
//
// Copyright (C) 2006-2020 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package org.talend.mdm.repository.core.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anySetOf;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.map.MultiKeyMap;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.BasicEMap;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.impl.EClassImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.internal.Workbench;
import org.junit.Test;
import org.mockito.Mockito;
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

import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.utils.PasswordUtil;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.webservices.TMDMService;
import com.amalto.workbench.webservices.WSDigest;
import com.amalto.workbench.webservices.WSDigestKey;
import com.amalto.workbench.webservices.WSLong;
public class ConsistencyServiceTest {

    private static Logger log = Logger.getLogger(ConsistencyServiceTest.class);

    @Test
    public void testCheckConsistency() throws Exception {
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

        Workbench instance = Workbench.getInstance();
        boolean running = instance.isRunning();
        Field runEventLoopField = null;

        try {
            // isWorkInUI=false
            runEventLoopField = instance.getClass().getDeclaredField("runEventLoop");
            runEventLoopField.setAccessible(true);
            runEventLoopField.set(instance, false);

            ConsistencyService mockCService = Mockito.mock(ConsistencyService.class);
            Mockito.when(mockCService.checkConsistency(serverDef, viewObCmdOpjMap)).thenCallRealMethod();
            ConsistencyCheckResult checkResult1 = mockCService.checkConsistency(serverDef, viewObCmdOpjMap);
            assertEquals(1, checkResult1.getToDeployObjects().size());
            assertEquals(0, checkResult1.getToSkipObjects().size());
            assertEquals(mockViewObj, checkResult1.getToDeployObjects().get(0));

            // isWorkInUI=true
            runEventLoopField.set(instance, true);

            // has no conflict
            Mockito.when(mockCService.getConflictCount(anyMap())).thenReturn(0); // $NON-NLS-1$
            ConsistencyCheckResult checkResult2 = mockCService.checkConsistency(serverDef, viewObCmdOpjMap);
            assertEquals(1, checkResult2.getToDeployObjects().size());
            assertEquals(0, checkResult2.getToSkipObjects().size());
            assertEquals(mockViewObj, checkResult2.getToDeployObjects().get(0));

            // has (one) conflict
            Mockito.when(mockCService.getConflictCount(anyMap())).thenReturn(1); // $NON-NLS-1$
            Mockito.when(mockCService.isWarnUserWhenConflict()).thenReturn(true);
            ConsistencyCheckResult mockResult1 = Mockito.mock(ConsistencyCheckResult.class);
            Mockito.when(mockCService.warnUserWhenConflict(eq(viewObCmdOpjMap), anyMap(), anyInt())).thenReturn(mockResult1); // $NON-NLS-1$

            ConsistencyCheckResult checkResult3 = mockCService.checkConsistency(serverDef, viewObCmdOpjMap);
            assertEquals(mockResult1, checkResult3);
            Mockito.verify(mockCService, times(1)).correctCheckResult(mockResult1);

            Mockito.when(mockCService.isWarnUserWhenConflict()).thenReturn(false);
            ConsistencyCheckResult mockResult2 = Mockito.mock(ConsistencyCheckResult.class);
            Mockito.when(mockCService.getCheckResultByStrategy(anyInt(), anyMap(), eq(viewObCmdOpjMap))).thenReturn(mockResult2);

            ConsistencyCheckResult checkResult4 = mockCService.checkConsistency(serverDef, viewObCmdOpjMap);
            assertEquals(mockResult2, checkResult4);
            Mockito.verify(mockCService, times(1)).correctCheckResult(mockResult2);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (runEventLoopField != null) {
                runEventLoopField.set(instance, running);
            }
        }
    }

    @Test
    public void testCalculateDigestValue() {
        String digestFromObj = "digestFromObjectResource"; //$NON-NLS-1$
        String digestFromRefFile = "digestFromRefFile"; //$NON-NLS-1$
        String digestFromSortedObj = "digestFromSortedObject"; //$NON-NLS-1$

        try {
            ConsistencyService mockCService = Mockito.mock(ConsistencyService.class);
            Mockito.when(mockCService.calculateDigestValueByObjectResource(any(Item.class))).thenReturn(digestFromObj);
            Mockito.when(mockCService.calculateDigestValueByRefFile(any(Item.class), any(ERepositoryObjectType.class)))
                    .thenReturn(digestFromRefFile);
            Mockito.when(mockCService.calculateDigestValueBySortedObject(any(Item.class), any(ERepositoryObjectType.class)))
                    .thenReturn(digestFromSortedObj);
            Mockito.when(mockCService.calculateDigestValue(any(Item.class), any(ERepositoryObjectType.class)))
                    .thenCallRealMethod();

            //
            Mockito.when(mockCService.getDigetValueCaculateStrategy(any(Item.class)))
                    .thenReturn(DigestCalStrategyEnum.OBJ_RESOURCE);
            String calculatedDigestValue = mockCService.calculateDigestValue(Mockito.mock(Item.class),
                    IServerObjectRepositoryType.TYPE_DATAMODEL);
            assertEquals(digestFromObj, calculatedDigestValue);

            //
            Mockito.when(mockCService.getDigetValueCaculateStrategy(any(Item.class))).thenReturn(
                    DigestCalStrategyEnum.REF_FILE);
            calculatedDigestValue = mockCService.calculateDigestValue(Mockito.mock(Item.class),
                    IServerObjectRepositoryType.TYPE_DATAMODEL);
            assertEquals(digestFromRefFile, calculatedDigestValue);

            //
            Mockito.when(mockCService.getDigetValueCaculateStrategy(any(Item.class))).thenReturn(
                    DigestCalStrategyEnum.SORTED_OBJ_RESOURCE);
            calculatedDigestValue = mockCService.calculateDigestValue(Mockito.mock(Item.class),
                    IServerObjectRepositoryType.TYPE_DATAMODEL);
            assertEquals(digestFromSortedObj, calculatedDigestValue);

            //
            Mockito.when(mockCService.getDigetValueCaculateStrategy(any(Item.class))).thenReturn(
                    DigestCalStrategyEnum.OTHER);
            calculatedDigestValue = mockCService.calculateDigestValue(Mockito.mock(Item.class),
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

        EMap mockProperties = new BasicEMap<>();
        mockProperties.put("digestValue", localDigestValue);
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

        EMap mockProperties = new BasicEMap<>();
        mockProperties.put("currentDigestValue", currentDigestValue); //$NON-NLS-1$
        Property mockProperty = Mockito.mock(Property.class);
        Mockito.when(mockProperty.getAdditionalProperties()).thenReturn(mockProperties);
        ProcessItem mockProcessItem = Mockito.mock(ProcessItem.class);
        Mockito.when(mockProcessItem.getProperty()).thenReturn(mockProperty);

        //
        assertEquals(currentDigestValue, ConsistencyService.getInstance().getCurrentDigestValue(mockProcessItem));
    }

    @Test
    public void testUpdateDigestValue() {
        String url = "http://localhost:8080/talendmdm/services?wsdl";
        String username = "username";
        String passwd = "password";

        IRepositoryViewObject mockViewObj = Mockito.mock(IRepositoryViewObject.class);
        MDMServerObjectItem mockItem = Mockito.mock(MDMServerObjectItem.class);
        URI uri = URI.createPlatformResourceURI("/localProject/MDM/datamodel/model_1.0.xsd", true);
        ResourceSet resourceSet = new ResourceSetImpl();
        Resource emfResource = resourceSet.createResource(uri);
        Resource spyResource = Mockito.spy(emfResource);
        class ItemEClass extends EClassImpl {

            public ItemEClass() {
            }
        }

        ItemEClass itemEClass = new ItemEClass();
        Mockito.when(mockItem.eClass()).thenReturn(itemEClass);
        Mockito.when(mockItem.eResource()).thenReturn(spyResource);
        Mockito.when(spyResource.getResourceSet()).thenReturn(resourceSet);

        Property mockProperty = Mockito.mock(Property.class);
        Mockito.when(mockProperty.getItem()).thenReturn(mockItem);
        Mockito.when(mockItem.getProperty()).thenReturn(mockProperty);
        Mockito.when(mockViewObj.getProperty()).thenReturn(mockProperty);
        Mockito.when(mockViewObj.getRepositoryObjectType()).thenReturn(IServerObjectRepositoryType.TYPE_DATAMODEL);

        try {
            Field cachedMDMServiceField = Util.class.getDeclaredField("cachedMDMService");
            cachedMDMServiceField.setAccessible(true);
            MultiKeyMap mkmap = (MultiKeyMap) cachedMDMServiceField.get(null);

            TMDMService mockService = Mockito.mock(TMDMService.class);
            MDMServerDef mockServerDef = Mockito.mock(MDMServerDef.class);
            Mockito.when(mockServerDef.getUrl()).thenReturn(url);
            Mockito.when(mockServerDef.getUser()).thenReturn(username);
            Mockito.when(mockServerDef.getPasswd()).thenReturn(passwd);
            mkmap.put(new URL(url), username, passwd, mockService);

            WSLong timeValue = new WSLong(11);
            Mockito.when(mockService.updateDigest(any(WSDigest.class))).thenReturn(timeValue);

            ConsistencyService mockCService = Mockito.mock(ConsistencyService.class);

            String label = "Product"; //$NON-NLS-1$
            Mockito.when(mockCService.getObjectName(mockViewObj)).thenReturn(label);
            Mockito.when(mockCService.getLocalDigestValue(mockItem)).thenReturn(label);
            Mockito.doCallRealMethod().when(mockCService).updateDigestValue(any(MDMServerDef.class),
                    any(IRepositoryViewObject.class));

            mockCService.updateDigestValue(mockServerDef, mockViewObj);

            mkmap.remove(new URL(url), username, passwd);
            // check
            Mockito.verify(mockCService, times(1)).updateLocalDigestValue(mockViewObj);
            Mockito.verify(mockCService, times(1)).updateLocalTimestamp(mockItem, timeValue.getValue());
            Mockito.verify(mockService, Mockito.times(1)).updateDigest(any(WSDigest.class));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Test
    public void testQueryServerDigestValue() {
        MDMServerDef serverDef = MdmmetadataFactory.eINSTANCE.createMDMServerDef();
        serverDef.setAlgorithm(PasswordUtil.ALGORITHM_COMMON_V2);
        String url = "http://localhost:8180/talendmdm/service/soap?wsdl"; //$NON-NLS-1$
        String username = "admin";
        String passwd = "talend";
        serverDef.parse(url);
        serverDef.setUrl(url);
        serverDef.setName("MyServer"); //$NON-NLS-1$
        serverDef.setUser(username); // $NON-NLS-1$
        serverDef.setPasswd(passwd); // $NON-NLS-1$

        String label = "Product"; //$NON-NLS-1$
        ERepositoryObjectType type = IServerObjectRepositoryType.TYPE_DATAMODEL;
        try {
            Field cachedMDMServiceField = Util.class.getDeclaredField("cachedMDMService");
            cachedMDMServiceField.setAccessible(true);
            MultiKeyMap mkmap = (MultiKeyMap) cachedMDMServiceField.get(null);

            TMDMService mockService = Mockito.mock(TMDMService.class);
            WSDigest mockDigest = Mockito.mock(WSDigest.class);
            Mockito.when(mockService.getDigest(Mockito.any(WSDigestKey.class))).thenReturn(mockDigest);
            mkmap.put(new URL(url), username, passwd, mockService);

            // IRepositoryViewObject
            IRepositoryViewObject mockViewObj = Mockito.mock(IRepositoryViewObject.class);
            Mockito.when(mockViewObj.getLabel()).thenReturn(label);

            Mockito.when(mockViewObj.getRepositoryObjectType()).thenReturn(type);
            Map<IRepositoryViewObject, WSDigest> queryServerDigestValue = ConsistencyService.getInstance().queryServerDigestValue(serverDef, Collections.singleton(mockViewObj));// /////////
            assertNotNull(queryServerDigestValue);
            assertTrue(queryServerDigestValue.size() == 1);
            assertEquals(mockViewObj, queryServerDigestValue.keySet().iterator().next());
            assertEquals(mockDigest, queryServerDigestValue.values().iterator().next());

            // TreeObject
            TreeObject mockTreeObj = Mockito.mock(TreeObject.class);
            Mockito.when(mockTreeObj.getDisplayName()).thenReturn(label);
            Mockito.when(mockTreeObj.getType()).thenReturn(TreeObject.DATA_MODEL);

            ConsistencyService mockCService = Mockito.mock(ConsistencyService.class);
            Mockito.when(mockCService.getObjectName(any(TreeObject.class))).thenReturn(label);
            Mockito.when(mockCService.queryServerDigestValue(any(MDMServerDef.class), anySetOf(TreeObject.class)))
                    .thenCallRealMethod();
            Map<TreeObject, WSDigest> serverDigestValue = mockCService.queryServerDigestValue(serverDef,
                    Collections.singleton(mockTreeObj));

            mkmap.remove(new URL(url), username, passwd);

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
            Mockito.doCallRealMethod().when(mockCService).updateLocalDigestValue(mockViewObj);
            Mockito.doCallRealMethod().when(mockCService).updateLocalDigestValue(mockItem, digestValue);
            mockCService.updateLocalDigestValue(mockViewObj);

            Mockito.verify(mockCService, Mockito.atLeastOnce()).calculateDigestValue(mockItem, mockType);
            Mockito.verify(mockCService, atLeastOnce()).updateLocalDigestValue(mockItem, digestValue);
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
            Mockito.doCallRealMethod().when(mockCService).updateCurrentDigestValue(mockViewObj); // $NON-NLS-1$
            mockCService.updateCurrentDigestValue(mockViewObj);

            Mockito.verify(mockCService, Mockito.atLeastOnce()).calculateDigestValue(mockItem, mockType);
            Mockito.verify(mockCService, atLeastOnce()).updateCurrentDigestValue(mockItem, digestValue); // $NON-NLS-1$
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Test
    public void testIsWarnUserWhenConflict() {
        try {
            IPreferenceStore preferenceStore = Mockito.mock(IPreferenceStore.class);
            Mockito.when(preferenceStore.getBoolean(PreferenceConstants.P_WARN_USER_HAS_CONFLICT)).thenReturn(true);

            ConsistencyService mockCService = Mockito.mock(ConsistencyService.class);
            Mockito.when(mockCService.getPreferenceStore()).thenReturn(preferenceStore);
            Mockito.when(mockCService.isWarnUserWhenConflict()).thenCallRealMethod();
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

            ConsistencyService mockCService = Mockito.mock(ConsistencyService.class);
            Mockito.when(mockCService.isWarnUserWhenConflict()).thenCallRealMethod();
            Mockito.when(mockCService.getPreferenceStore()).thenReturn(preferenceStore); // $NON-NLS-1$
            Mockito.when(mockCService.getConflictStrategy()).thenCallRealMethod();
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
