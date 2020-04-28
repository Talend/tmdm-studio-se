package org.talend.mdm.repository.core.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.BasicEMap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.talend.core.AbstractDQModelService;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.IService;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ItemState;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.model.repository.RepositoryObject;
import org.talend.mdm.repository.core.IServerObjectRepositoryType;
import org.talend.mdm.repository.model.mdmproperties.ContainerItem;
import org.talend.mdm.repository.model.mdmproperties.MDMServerObjectItem;
import org.talend.mdm.repository.model.mdmproperties.WSDataModelItem;
import org.talend.mdm.repository.models.FolderRepositoryObject;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.IProxyRepositoryService;


public class ContainerCacheServiceTest {

    private static Logger log = Logger.getLogger(ContainerCacheServiceTest.class);

    @Before
    public void setUp() {
        ContainerCacheService.clearCache();
    }

    @Test
    public void testPutContainer() {
        String path = "/path"; //$NON-NLS-1$
        ERepositoryObjectType repositoryType = IServerObjectRepositoryType.TYPE_DATAMODEL;

        ItemState mockState = Mockito.mock(ItemState.class);
        Mockito.when(mockState.getPath()).thenReturn(path);
        MDMServerObjectItem mockItem = Mockito.mock(MDMServerObjectItem.class);
        Mockito.when(mockItem.getState()).thenReturn(mockState);
        Property mockProperty = Mockito.mock(Property.class);
        Mockito.when(mockProperty.getItem()).thenReturn(mockItem);
        IRepositoryViewObject mockViewObj = Mockito.mock(IRepositoryViewObject.class);
        Mockito.when(mockViewObj.getProperty()).thenReturn(mockProperty);
        Mockito.when(mockViewObj.getRepositoryObjectType()).thenReturn(repositoryType);

        // not container item
        ContainerCacheService.putContainer(mockViewObj);
        assertNull(ContainerCacheService.get(repositoryType, path));

        // container item
        ContainerItem mockContainerItem = Mockito.mock(ContainerItem.class);
        Mockito.when(mockContainerItem.getState()).thenReturn(mockState);
        Mockito.when(mockContainerItem.getRepObjType()).thenReturn(repositoryType);
        Mockito.when(mockProperty.getItem()).thenReturn(mockContainerItem);
        ContainerCacheService.putContainer(mockViewObj);
        assertEquals(mockViewObj, ContainerCacheService.get(repositoryType, path));
    }

    @Test
    public void testPutPropertyIRepositoryViewObject() {
        String propId = "propertyId"; //$NON-NLS-1$

        Property mockProp = Mockito.mock(Property.class);
        Mockito.when(mockProp.getId()).thenReturn(propId);
        IRepositoryViewObject mockViewObj = Mockito.mock(IRepositoryViewObject.class);
        RepositoryObject mockRepObj = Mockito.mock(RepositoryObject.class);
        Mockito.when(mockRepObj.getProperty()).thenReturn(mockProp);

        //
        try {
            ContainerCacheService.put(null, mockViewObj);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }
        try {
            ContainerCacheService.put(mockProp, null);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }
        try {
            ContainerCacheService.put(null, null);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        ContainerCacheService.put(mockProp, mockViewObj);
        assertEquals(mockViewObj, ContainerCacheService.get(propId));

        ContainerCacheService.clearCache();

        Map<Class<?>, AbstractDQModelService> dqModelServices = null;
        Map<Class, IService> services = null;
        IProxyRepositoryService originRepoService = null;
        AbstractDQModelService originDQService = null;
        try {
            Field declaredField = GlobalServiceRegister.class.getDeclaredField("dqModelServices");
            declaredField.setAccessible(true);
            dqModelServices = (Map<Class<?>, AbstractDQModelService>) declaredField.get(GlobalServiceRegister.getDefault());
            AbstractDQModelService mockDQService = Mockito.mock(AbstractDQModelService.class);
            Mockito.when(mockDQService.getTDQRepObjType(any(Item.class)))
                    .thenReturn(IServerObjectRepositoryType.TYPE_CUSTOM_FORM);
            originDQService = dqModelServices.get(AbstractDQModelService.class);
            dqModelServices.put(AbstractDQModelService.class, mockDQService);

            Field serviceField = GlobalServiceRegister.class.getDeclaredField("services");
            serviceField.setAccessible(true);
            IProxyRepositoryService mockRepoService = Mockito.mock(IProxyRepositoryService.class);
            IProxyRepositoryFactory mockFactory = Mockito.mock(IProxyRepositoryFactory.class);
            Mockito.when(mockRepoService.getProxyRepositoryFactory()).thenReturn(mockFactory);

            services = (Map<Class, IService>) serviceField.get(GlobalServiceRegister.getDefault());
            originRepoService = (IProxyRepositoryService) dqModelServices.get(IProxyRepositoryService.class);
            services.put(IProxyRepositoryService.class, mockRepoService);

            ItemState mockItemState = Mockito.mock(ItemState.class);
            Mockito.when(mockItemState.isDeleted()).thenReturn(false);
            WSDataModelItem mockModelItem = Mockito.mock(WSDataModelItem.class);
            Mockito.when(mockModelItem.getState()).thenReturn(mockItemState);
            Mockito.when(mockProp.getItem()).thenReturn(mockModelItem);
            Mockito.when(mockProp.getAdditionalProperties()).thenReturn(new BasicEMap());
            
            ContainerCacheService.put(mockProp, mockRepObj);
            assertTrue(ContainerCacheService.get(propId) != null);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (dqModelServices != null) {
                dqModelServices.put(AbstractDQModelService.class, originDQService);
            }
            if (services != null) {
                services.put(IProxyRepositoryService.class, originRepoService);
            }
        }
    }

    @Test
    public void testPutIRepositoryViewObject() {
        String propId = "propertyId"; //$NON-NLS-1$

        //
        try {
            ContainerCacheService.put((IRepositoryViewObject) null);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        //
        IRepositoryViewObject mockViewObj = Mockito.mock(IRepositoryViewObject.class);
        Mockito.when(mockViewObj.getId()).thenReturn(propId);
        ContainerCacheService.put(mockViewObj);
        assertEquals(mockViewObj, ContainerCacheService.get(propId));

        ContainerCacheService.clearCache();
        //
        Map<Class<?>, AbstractDQModelService> dqModelServices = null;
        Map<Class, IService> services = null;
        IProxyRepositoryService originRepoService = null;
        AbstractDQModelService originDQService = null;
        try {
            Field declaredField = GlobalServiceRegister.class.getDeclaredField("dqModelServices");
            declaredField.setAccessible(true);
            dqModelServices = (Map<Class<?>, AbstractDQModelService>) declaredField.get(GlobalServiceRegister.getDefault());
            AbstractDQModelService mockDQService = Mockito.mock(AbstractDQModelService.class);
            Mockito.when(mockDQService.getTDQRepObjType(any(Item.class)))
                    .thenReturn(IServerObjectRepositoryType.TYPE_CUSTOM_FORM);
            originDQService = dqModelServices.get(AbstractDQModelService.class);
            dqModelServices.put(AbstractDQModelService.class, mockDQService);

            Field serviceField = GlobalServiceRegister.class.getDeclaredField("services");
            serviceField.setAccessible(true);
            IProxyRepositoryService mockRepoService = Mockito.mock(IProxyRepositoryService.class);
            IProxyRepositoryFactory mockFactory = Mockito.mock(IProxyRepositoryFactory.class);
            Mockito.when(mockRepoService.getProxyRepositoryFactory()).thenReturn(mockFactory);

            services = (Map<Class, IService>) serviceField.get(GlobalServiceRegister.getDefault());
            originRepoService = (IProxyRepositoryService) services.get(IProxyRepositoryService.class);
            services.put(IProxyRepositoryService.class, mockRepoService);

            Property mockProp = Mockito.mock(Property.class);
            Mockito.when(mockProp.getId()).thenReturn(propId);
            Mockito.when(mockProp.getAdditionalProperties()).thenReturn(new BasicEMap());
            Item mockItem = Mockito.mock(Item.class);
            ItemState mockState = Mockito.mock(ItemState.class);
            Mockito.when(mockState.isDeleted()).thenReturn(false);
            Mockito.when(mockItem.getState()).thenReturn(mockState);
            Mockito.when(mockProp.getItem()).thenReturn(mockItem);

            RepositoryObject mockRepObj = Mockito.mock(RepositoryObject.class);
            Mockito.when(mockRepObj.getProperty()).thenReturn(mockProp);
            Mockito.when(mockRepObj.getId()).thenReturn(propId);

            ContainerCacheService.put(mockRepObj);
            assertTrue(ContainerCacheService.get(propId) != null);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (dqModelServices != null) {
                dqModelServices.put(AbstractDQModelService.class, originDQService);
            }
            if (services != null) {
                services.put(IProxyRepositoryService.class, originRepoService);
            }
        }
    }

    @Test
    public void testCorrectPath() {
        String path = null;
        try {
            Object correctedPath = ContainerCacheService.correctPath(path);
            assertNull(correctedPath);

            path = ""; //$NON-NLS-1$
            correctedPath = ContainerCacheService.correctPath(path);
            assertTrue(correctedPath.toString().isEmpty());

            path = "/pa"; //$NON-NLS-1$
            correctedPath = ContainerCacheService.correctPath(path);
            assertEquals(path, correctedPath);

            path = "pb"; //$NON-NLS-1$
            correctedPath = ContainerCacheService.correctPath(path);
            assertEquals("/" + path, correctedPath); //$NON-NLS-1$
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Test
    public void testRemoveContainer() {
        String path = "/path"; //$NON-NLS-1$
        String propId = "propertyId"; //$NON-NLS-1$
        ERepositoryObjectType repObjType = IServerObjectRepositoryType.TYPE_DATAMODEL;

        IRepositoryViewObject mockChildRpViewObj = Mockito.mock(IRepositoryViewObject.class);
        Property mockProperty = Mockito.mock(Property.class);
        Mockito.when(mockProperty.getId()).thenReturn(propId);
        Mockito.when(mockChildRpViewObj.getId()).thenReturn(propId);
        Mockito.when(mockChildRpViewObj.getProperty()).thenReturn(mockProperty);
        ContainerCacheService.put(mockChildRpViewObj);

        // container item
        ItemState mockState = Mockito.mock(ItemState.class);
        Mockito.when(mockState.getPath()).thenReturn(path);
        ContainerItem mockContainerItem = Mockito.mock(ContainerItem.class);
        Mockito.when(mockContainerItem.getState()).thenReturn(mockState);
        Mockito.when(mockContainerItem.getRepObjType()).thenReturn(repObjType);

        Property mockFolderProperty = Mockito.mock(Property.class);
        FolderRepositoryObject mockFolderRepoObj = new FolderRepositoryObject(mockFolderProperty);
        List<IRepositoryViewObject> children = new ArrayList<IRepositoryViewObject>();
        children.add(mockChildRpViewObj);
        mockFolderRepoObj.getChildren().addAll(children);

        Mockito.when(mockFolderProperty.getItem()).thenReturn(mockContainerItem);

        ContainerCacheService.putContainer(mockFolderRepoObj);

        //
        ContainerCacheService.removeContainer(repObjType, path);
        assertNull(ContainerCacheService.get(repObjType, path));
        assertNull(ContainerCacheService.get(mockProperty));
    }

    @Test
    public void testGetParent() throws Exception {
        String path = "/pathA/pathB"; //$NON-NLS-1$
        String parentPath = "/pathA";
        ERepositoryObjectType repoType = IServerObjectRepositoryType.TYPE_DATAMODEL;

        ItemState mockState = Mockito.mock(ItemState.class);
        Mockito.when(mockState.getPath()).thenReturn(path);
        ContainerItem mockContainerItem = Mockito.mock(ContainerItem.class);
        Mockito.when(mockContainerItem.getState()).thenReturn(mockState);
        Property mockProperty = Mockito.mock(Property.class);
        Mockito.when(mockProperty.getItem()).thenReturn(mockContainerItem);
        IRepositoryViewObject mockViewObject = Mockito.mock(IRepositoryViewObject.class);
        Mockito.when(mockViewObject.getProperty()).thenReturn(mockProperty);
        Mockito.when(mockViewObject.getRepositoryObjectType()).thenReturn(repoType);

        Field declaredField = ContainerCacheService.class.getDeclaredField("containerMap");
        declaredField.setAccessible(true);
        Map<ERepositoryObjectType, Map<String, IRepositoryViewObject>> containerMap = (Map<ERepositoryObjectType, Map<String, IRepositoryViewObject>>) declaredField
                .get(null);
        Map<String, IRepositoryViewObject> map = containerMap.get(repoType);
        containerMap.remove(repoType);
        assertNull(ContainerCacheService.getParent(mockViewObject));

        //
        IRepositoryViewObject mockResultParent = Mockito.mock(IRepositoryViewObject.class);
        if (map == null) {
            map = new HashMap<String, IRepositoryViewObject>();
        }
        map.put(ContainerCacheService.correctPath(parentPath), mockResultParent);
        containerMap.put(repoType, map);
        IRepositoryViewObject parent = ContainerCacheService.getParent(mockViewObject);
        assertEquals(mockResultParent, parent);

        Mockito.when(mockState.getPath()).thenReturn("/"); //$NON-NLS-1$
        parent = ContainerCacheService.getParent(mockViewObject);
        assertNull(parent);

        Mockito.when(mockState.getPath()).thenReturn(""); //$NON-NLS-1$
        parent = ContainerCacheService.getParent(mockViewObject);
        assertNull(parent);

        Mockito.when(mockState.getPath()).thenReturn("path"); //$NON-NLS-1$
        map.put(ContainerCacheService.correctPath("path"), mockResultParent);
        parent = ContainerCacheService.getParent(mockViewObject);
        assertEquals(mockResultParent, parent);

        Item mockItem = Mockito.mock(Item.class);
        Mockito.when(mockItem.getState()).thenReturn(mockState);
        Mockito.when(mockState.getPath()).thenReturn(path);
        Mockito.when(mockProperty.getItem()).thenReturn(mockItem);
        map.put(ContainerCacheService.correctPath(path), mockResultParent);
        parent = ContainerCacheService.getParent(mockViewObject);
        assertEquals(mockResultParent, parent);
    }

}
