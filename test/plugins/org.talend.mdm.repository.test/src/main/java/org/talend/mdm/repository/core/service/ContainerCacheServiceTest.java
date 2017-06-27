package org.talend.mdm.repository.core.service;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ItemState;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.model.repository.RepositoryNodeProviderRegistryReader;
import org.talend.core.model.repository.RepositoryObject;
import org.talend.mdm.repository.core.IServerObjectRepositoryType;
import org.talend.mdm.repository.model.mdmproperties.ContainerItem;
import org.talend.mdm.repository.model.mdmproperties.MDMServerObjectItem;
import org.talend.mdm.repository.model.mdmproperties.WSDataModelItem;
import org.talend.mdm.repository.models.FolderRepositoryObject;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ ContainerCacheService.class, IServerObjectRepositoryType.class, ERepositoryObjectType.class,
        FolderRepositoryObject.class, RepositoryNodeProviderRegistryReader.class, GlobalServiceRegister.class })
@PowerMockIgnore({ "org.eclipse.core.runtime.*" })
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

        ItemState mockState = PowerMockito.mock(ItemState.class);
        PowerMockito.when(mockState.getPath()).thenReturn(path);
        MDMServerObjectItem mockItem = PowerMockito.mock(MDMServerObjectItem.class);
        PowerMockito.when(mockItem.getState()).thenReturn(mockState);
        Property mockProperty = PowerMockito.mock(Property.class);
        PowerMockito.when(mockProperty.getItem()).thenReturn(mockItem);
        IRepositoryViewObject mockViewObj = PowerMockito.mock(IRepositoryViewObject.class);
        PowerMockito.when(mockViewObj.getProperty()).thenReturn(mockProperty);
        PowerMockito.when(mockViewObj.getRepositoryObjectType()).thenReturn(repositoryType);

        // not container item
        ContainerCacheService.putContainer(mockViewObj);
        assertNull(ContainerCacheService.get(repositoryType, path));

        // container item
        ContainerItem mockContainerItem = PowerMockito.mock(ContainerItem.class);
        PowerMockito.when(mockContainerItem.getState()).thenReturn(mockState);
        PowerMockito.when(mockContainerItem.getRepObjType()).thenReturn(repositoryType);
        PowerMockito.when(mockProperty.getItem()).thenReturn(mockContainerItem);
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
        try {
            String path = "path"; //$NON-NLS-1$
            ItemState mockItemState = Mockito.mock(ItemState.class);
            Mockito.when(mockItemState.getPath()).thenReturn(path);
            Mockito.when(mockItemState.isDeleted()).thenReturn(false);
            WSDataModelItem mockModelItem = Mockito.mock(WSDataModelItem.class);
            Mockito.when(mockModelItem.getState()).thenReturn(mockItemState);
            Mockito.when(mockProp.getItem()).thenReturn(mockModelItem);
            
            IRepositoryViewObject mockRepoViewObj = PowerMockito.mock(IRepositoryViewObject.class);
            PowerMockito.mockStatic(ContainerCacheService.class);
            PowerMockito.when(ContainerCacheService.class, "put", any(Property.class), any(RepositoryObject.class)) //$NON-NLS-1$
                    .thenCallRealMethod();
            PowerMockito.when(ContainerCacheService.class, "get", anyString()).thenCallRealMethod(); //$NON-NLS-1$
            PowerMockito.when(ContainerCacheService.class, "createRepositoryViewObject", any(IRepositoryViewObject.class)) //$NON-NLS-1$
                    .thenReturn(mockRepoViewObj);

            ContainerCacheService.put(mockProp, mockRepObj);
            assertTrue(ContainerCacheService.get(propId) != null);
            assertSame(mockRepoViewObj, ContainerCacheService.get(propId));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
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
        try {
            Property mockProp = Mockito.mock(Property.class);
            Mockito.when(mockProp.getId()).thenReturn(propId);

            RepositoryObject mockRepObj = PowerMockito.mock(RepositoryObject.class);
            PowerMockito.when(mockRepObj.getProperty()).thenReturn(mockProp);
            PowerMockito.when(mockRepObj.getId()).thenReturn(propId);

            PowerMockito.mockStatic(ContainerCacheService.class);
            PowerMockito.when(ContainerCacheService.class, "put", any(IRepositoryViewObject.class)).thenCallRealMethod(); //$NON-NLS-1$
            PowerMockito.when(ContainerCacheService.class, "get", anyString()).thenCallRealMethod(); //$NON-NLS-1$
            PowerMockito.when(ContainerCacheService.class, "createRepositoryViewObject", any(IRepositoryViewObject.class)) //$NON-NLS-1$
                    .thenReturn(mockViewObj);

            ContainerCacheService.put(mockRepObj);
            if (ContainerCacheService.get(propId) != null) {
                assertSame(mockViewObj, ContainerCacheService.get(propId));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Test
    public void testCorrectPath() {
        String path = null;
        try {
            Object correctedPath = Whitebox.invokeMethod(ContainerCacheService.class, "correctPath", path); //$NON-NLS-1$
            assertNull(correctedPath);

            path = ""; //$NON-NLS-1$
            correctedPath = Whitebox.invokeMethod(ContainerCacheService.class, "correctPath", path); //$NON-NLS-1$
            assertTrue(correctedPath.toString().isEmpty());

            path = "/pa"; //$NON-NLS-1$
            correctedPath = Whitebox.invokeMethod(ContainerCacheService.class, "correctPath", path); //$NON-NLS-1$
            assertEquals(path, correctedPath);

            path = "pb"; //$NON-NLS-1$
            correctedPath = Whitebox.invokeMethod(ContainerCacheService.class, "correctPath", path); //$NON-NLS-1$
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

        IRepositoryViewObject mockChildRpViewObj = PowerMockito.mock(IRepositoryViewObject.class);
        Property mockProperty = PowerMockito.mock(Property.class);
        PowerMockito.when(mockProperty.getId()).thenReturn(propId);
        PowerMockito.when(mockChildRpViewObj.getId()).thenReturn(propId);
        PowerMockito.when(mockChildRpViewObj.getProperty()).thenReturn(mockProperty);
        ContainerCacheService.put(mockChildRpViewObj);

        // container item
        ItemState mockState = PowerMockito.mock(ItemState.class);
        PowerMockito.when(mockState.getPath()).thenReturn(path);
        ContainerItem mockContainerItem = PowerMockito.mock(ContainerItem.class);
        PowerMockito.when(mockContainerItem.getState()).thenReturn(mockState);
        PowerMockito.when(mockContainerItem.getRepObjType()).thenReturn(repObjType);

        FolderRepositoryObject mockFolderRepoObj = PowerMockito.mock(FolderRepositoryObject.class);
        List<IRepositoryViewObject> children = new ArrayList<IRepositoryViewObject>();
        children.add(mockChildRpViewObj);
        children = spy(children);
        PowerMockito.when(mockFolderRepoObj.getChildren()).thenReturn(children);
        Property mockFolderProperty = PowerMockito.mock(Property.class);
        PowerMockito.when(mockFolderProperty.getItem()).thenReturn(mockContainerItem);
        PowerMockito.when(mockFolderRepoObj.getProperty()).thenReturn(mockFolderProperty);

        ContainerCacheService.putContainer(mockFolderRepoObj);

        //
        ContainerCacheService.removeContainer(repObjType, path);
        assertNull(ContainerCacheService.get(repObjType, path));
        assertNull(ContainerCacheService.get(mockProperty));
    }

    @Test
    public void testGetParent() {
        String path = "/pathA/pathB"; //$NON-NLS-1$
        ERepositoryObjectType repoType = IServerObjectRepositoryType.TYPE_DATAMODEL;

        ItemState mockState = PowerMockito.mock(ItemState.class);
        PowerMockito.when(mockState.getPath()).thenReturn(path);
        ContainerItem mockContainerItem = PowerMockito.mock(ContainerItem.class);
        PowerMockito.when(mockContainerItem.getState()).thenReturn(mockState);
        Property mockProperty = PowerMockito.mock(Property.class);
        PowerMockito.when(mockProperty.getItem()).thenReturn(mockContainerItem);
        IRepositoryViewObject mockViewObject = PowerMockito.mock(IRepositoryViewObject.class);
        PowerMockito.when(mockViewObject.getProperty()).thenReturn(mockProperty);
        PowerMockito.when(mockViewObject.getRepositoryObjectType()).thenReturn(repoType);

        PowerMockito.mockStatic(ContainerCacheService.class);
        IRepositoryViewObject mockResultParent = PowerMockito.mock(IRepositoryViewObject.class);
        PowerMockito.when(ContainerCacheService.get(any(ERepositoryObjectType.class), anyString())).thenReturn(mockResultParent);
        PowerMockito.when(ContainerCacheService.getParent(any(IRepositoryViewObject.class))).thenCallRealMethod();

        IRepositoryViewObject parent = ContainerCacheService.getParent(mockViewObject);
        assertEquals(mockResultParent, parent);
        PowerMockito.verifyStatic();
        ContainerCacheService.get(repoType, "/pathA"); //$NON-NLS-1$

        PowerMockito.when(mockState.getPath()).thenReturn("/"); //$NON-NLS-1$
        parent = ContainerCacheService.getParent(mockViewObject);
        assertNull(parent);

        PowerMockito.when(mockState.getPath()).thenReturn(""); //$NON-NLS-1$
        parent = ContainerCacheService.getParent(mockViewObject);
        assertNull(parent);

        PowerMockito.when(mockState.getPath()).thenReturn("path"); //$NON-NLS-1$
        parent = ContainerCacheService.getParent(mockViewObject);
        assertEquals(mockResultParent, parent);
        PowerMockito.verifyStatic();
        ContainerCacheService.get(repoType, "path"); //$NON-NLS-1$

        Item mockItem = PowerMockito.mock(Item.class);
        PowerMockito.when(mockItem.getState()).thenReturn(mockState);
        PowerMockito.when(mockState.getPath()).thenReturn(path);
        PowerMockito.when(mockProperty.getItem()).thenReturn(mockItem);
        parent = ContainerCacheService.getParent(mockViewObject);
        assertEquals(mockResultParent, parent);
        PowerMockito.verifyStatic();
        ContainerCacheService.get(repoType, path);
    }

}
