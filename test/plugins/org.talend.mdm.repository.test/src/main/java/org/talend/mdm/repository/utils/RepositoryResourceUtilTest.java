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
package org.talend.mdm.repository.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.RandomUtils;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.EClassImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.junit.Test;
import org.mockito.Mockito;
import org.talend.commons.runtime.model.repository.ERepositoryStatus;
import org.talend.commons.utils.VersionUtils;
import org.talend.commons.utils.data.container.RootContainer;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.IService;
import org.talend.core.context.RepositoryContext;
import org.talend.core.model.properties.FolderType;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ItemState;
import org.talend.core.model.properties.PropertiesFactory;
import org.talend.core.model.properties.Property;
import org.talend.core.model.properties.User;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.IRepositoryFactory;
import org.talend.mdm.repository.core.IRepositoryNodeConfiguration;
import org.talend.mdm.repository.core.IRepositoryNodeLabelProvider;
import org.talend.mdm.repository.core.IRepositoryNodeResourceProvider;
import org.talend.mdm.repository.core.IServerObjectRepositoryType;
import org.talend.mdm.repository.core.service.ContainerCacheService;
import org.talend.mdm.repository.core.service.IInteractiveHandler;
import org.talend.mdm.repository.core.service.InteractiveService;
import org.talend.mdm.repository.extension.RepositoryNodeConfigurationManager;
import org.talend.mdm.repository.model.mdmproperties.ContainerItem;
import org.talend.mdm.repository.models.FolderRepositoryObject;
import org.talend.repository.ProjectManager;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.IProxyRepositoryService;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.IRepositoryNode.EProperties;
import org.talend.repository.model.RepositoryNode;

public class RepositoryResourceUtilTest {

    public class StubFolderRepositoryObject extends FolderRepositoryObject {

        public StubFolderRepositoryObject(Property prop) {
            super(prop);
        }

        @Override
        public List<IRepositoryViewObject> getChildren() {
            return super.getChildren();
        }
    }

    @Test
    public void testIsLockedViewObject() throws Exception {
        IRepositoryViewObject mockViewObject = mock(IRepositoryViewObject.class);

        IProxyRepositoryService mockRepoService = Mockito.mock(IProxyRepositoryService.class);
        IProxyRepositoryFactory repositoryFactory = Mockito.mock(IProxyRepositoryFactory.class);
        Mockito.when(mockRepoService.getProxyRepositoryFactory()).thenReturn(repositoryFactory);
        Field serviceField = GlobalServiceRegister.class.getDeclaredField("services");
        serviceField.setAccessible(true);
        Map<Class, IService> services = (Map<Class, IService>) serviceField.get(GlobalServiceRegister.getDefault());
        IService iService = services.get(IProxyRepositoryService.class);
        services.put(IProxyRepositoryService.class, mockRepoService);

        // true
        when(repositoryFactory.getStatus(mockViewObject)).thenReturn(ERepositoryStatus.LOCK_BY_OTHER);
        boolean islockedViewObject = RepositoryResourceUtil.isLockedViewObject(mockViewObject);
        assertTrue(islockedViewObject);

        when(repositoryFactory.getStatus(mockViewObject)).thenReturn(ERepositoryStatus.LOCK_BY_USER);
        islockedViewObject = RepositoryResourceUtil.isLockedViewObject(mockViewObject);
        assertTrue(islockedViewObject);

        // false
        ERepositoryStatus[] statuss = { ERepositoryStatus.DEFAULT, ERepositoryStatus.DELETED, ERepositoryStatus.EDITABLE,
                ERepositoryStatus.ERROR, ERepositoryStatus.NEW, ERepositoryStatus.NOT_UP_TO_DATE, ERepositoryStatus.READ_ONLY,
                ERepositoryStatus.WARN };
        for (ERepositoryStatus status : statuss) {
            when(repositoryFactory.getStatus(mockViewObject)).thenReturn(status);
            islockedViewObject = RepositoryResourceUtil.isLockedViewObject(mockViewObject);
            assertFalse(islockedViewObject);
        }
    }

    @Test
    public void testIsLockedItem() throws Exception {
        Item mockItem = mock(Item.class);

        IProxyRepositoryService mockRepoService = Mockito.mock(IProxyRepositoryService.class);
        IProxyRepositoryFactory repositoryFactory = Mockito.mock(IProxyRepositoryFactory.class);
        Mockito.when(mockRepoService.getProxyRepositoryFactory()).thenReturn(repositoryFactory);
        Field serviceField = GlobalServiceRegister.class.getDeclaredField("services");
        serviceField.setAccessible(true);
        Map<Class, IService> services = (Map<Class, IService>) serviceField.get(GlobalServiceRegister.getDefault());
        IService iService = services.get(IProxyRepositoryService.class);
        services.put(IProxyRepositoryService.class, mockRepoService);

        when(repositoryFactory.getStatus(mockItem)).thenReturn(ERepositoryStatus.LOCK_BY_OTHER);
        boolean islockedViewObject = RepositoryResourceUtil.isLockedItem(mockItem);
        assertTrue(islockedViewObject);

        when(repositoryFactory.getStatus(mockItem)).thenReturn(ERepositoryStatus.LOCK_BY_USER);
        islockedViewObject = RepositoryResourceUtil.isLockedItem(mockItem);
        assertTrue(islockedViewObject);

        // false
        ERepositoryStatus[] statuss = { ERepositoryStatus.DEFAULT, ERepositoryStatus.DELETED, ERepositoryStatus.EDITABLE,
                ERepositoryStatus.ERROR, ERepositoryStatus.NEW, ERepositoryStatus.NOT_UP_TO_DATE, ERepositoryStatus.READ_ONLY,
                ERepositoryStatus.WARN };
        for (ERepositoryStatus status : statuss) {
            when(repositoryFactory.getStatus(mockItem)).thenReturn(status);
            islockedViewObject = RepositoryResourceUtil.isLockedItem(mockItem);
            assertFalse(islockedViewObject);
        }
    }

    @Test
    public void testSaveItem() throws Exception {
        class ItemEClass extends EClassImpl {

            public ItemEClass() {
            }
        }
        ItemEClass itemEClass = new ItemEClass();

        Item mockItem = mock(Item.class);
        when(mockItem.eClass()).thenReturn(itemEClass);

        IRepositoryNodeResourceProvider mockResourceProvider = mock(IRepositoryNodeResourceProvider.class);
        when(mockResourceProvider.needSaveReferenceFile()).thenReturn(true);

        IRepositoryNodeConfiguration configuration = mock(IRepositoryNodeConfiguration.class);
        when(configuration.getResourceProvider()).thenReturn(mockResourceProvider);
        Field itemConfMapField = RepositoryNodeConfigurationManager.class.getDeclaredField("itemConfMap");
        itemConfMapField.setAccessible(true);
        Map<EClass, IRepositoryNodeConfiguration> itemConfMap = (Map<EClass, IRepositoryNodeConfiguration>) itemConfMapField
                .get(null);
        itemConfMap.put(itemEClass, configuration);

        IProxyRepositoryService mockRepoService = Mockito.mock(IProxyRepositoryService.class);
        IProxyRepositoryFactory repositoryFactory = Mockito.mock(IProxyRepositoryFactory.class);
        Mockito.when(mockRepoService.getProxyRepositoryFactory()).thenReturn(repositoryFactory);
        Field serviceField = GlobalServiceRegister.class.getDeclaredField("services");
        serviceField.setAccessible(true);
        Map<Class, IService> services = (Map<Class, IService>) serviceField.get(GlobalServiceRegister.getDefault());
        IService iService = services.get(IProxyRepositoryService.class);
        services.put(IProxyRepositoryService.class, mockRepoService);

        RepositoryResourceUtil.saveItem(mockItem);
        verify(mockResourceProvider, Mockito.times(1)).handleReferenceFile(mockItem);
        verify(repositoryFactory, Mockito.times(1)).save(mockItem, false);
    }

    @Test
    public void testCreateItemWith2Args() throws Exception {
        String propLabel = "this is propLabel";
        String nextId = "UUID_NEXT";

        Item mockItem = mock(Item.class);
        ItemState mockItemState = mock(ItemState.class);
        when(mockItem.getState()).thenReturn(mockItemState);
        when(mockItem.getState().getPath()).thenReturn("");

        IProxyRepositoryService mockRepoService = Mockito.mock(IProxyRepositoryService.class);
        IProxyRepositoryFactory repositoryFactory = Mockito.mock(IProxyRepositoryFactory.class);
        Mockito.when(mockRepoService.getProxyRepositoryFactory()).thenReturn(repositoryFactory);
        Field serviceField = GlobalServiceRegister.class.getDeclaredField("services");
        serviceField.setAccessible(true);
        Map<Class, IService> services = (Map<Class, IService>) serviceField.get(GlobalServiceRegister.getDefault());
        IService iService = services.get(IProxyRepositoryService.class);
        services.put(IProxyRepositoryService.class, mockRepoService);

        RepositoryContext mockContext = mock(RepositoryContext.class);
        when(repositoryFactory.getRepositoryContext()).thenReturn(mockContext);
        when(repositoryFactory.getNextId()).thenReturn(nextId);

        Property mockProperty = mock(Property.class);
        when(mockItem.getProperty()).thenReturn(mockProperty);

        PropertiesFactory proFactory = PropertiesFactory.eINSTANCE;
        PropertiesFactory spyProFactory = spy(proFactory);
        when(spyProFactory.createProperty()).thenReturn(mockProperty);

        User user = mock(User.class);
        when(mockContext.getUser()).thenReturn(user);

        class ItemEClass extends EClassImpl {

            public ItemEClass() {
            }
        }

        ItemEClass itemEClass = new ItemEClass();
        IRepositoryNodeResourceProvider mockResourceProvider = mock(IRepositoryNodeResourceProvider.class);
        when(mockResourceProvider.needSaveReferenceFile()).thenReturn(true);
        when(repositoryFactory.isEditableAndLockIfPossible(mockItem)).thenReturn(true);
        IRepositoryNodeConfiguration configuration = mock(IRepositoryNodeConfiguration.class);
        when(configuration.getResourceProvider()).thenReturn(mockResourceProvider);
        Field itemConfMapField = RepositoryNodeConfigurationManager.class.getDeclaredField("itemConfMap");
        itemConfMapField.setAccessible(true);
        Map<EClass, IRepositoryNodeConfiguration> itemConfMap = (Map<EClass, IRepositoryNodeConfiguration>) itemConfMapField
                .get(null);
        itemConfMap.put(itemEClass, configuration);

        URI uri = URI.createPlatformResourceURI("/localProject/MDM/datamodel/model_1.0.xsd", true);
        ResourceSet resourceSet = new ResourceSetImpl();
        Resource emfResource = resourceSet.createResource(uri);
        Resource mockResource = Mockito.spy(emfResource);
        Mockito.when(mockItem.eResource()).thenReturn(mockResource);
        Mockito.when(mockItem.eClass()).thenReturn(itemEClass);
        Mockito.when(mockResource.getResourceSet()).thenReturn(resourceSet);

        boolean createItem = RepositoryResourceUtil.createItem(mockItem, propLabel);
        services.put(IProxyRepositoryService.class, iService);

        verify(mockProperty, times(1)).setId(nextId);
        verify(mockProperty, times(1)).setVersion(Mockito.anyString());
        verify(mockProperty, times(1)).setAuthor(user);
        verify(mockProperty, times(1)).setLabel(propLabel);
        verify(repositoryFactory, times(1)).create(Mockito.any(Item.class), Mockito.any(IPath.class));
        verify(mockResourceProvider, times(1)).handleReferenceFile(mockItem);

        verify(repositoryFactory, times(1)).unlock(mockItem);

        assertTrue(createItem);
    }

    @Test
    public void testGetVersionFileName() {
        String version = "0.1";
        String firstF = "datamodel";
        String endF = "xsd";
        File file = new File(firstF + "." + endF);

        String versionFileName = RepositoryResourceUtil.getVersionFileName(file, version);
        assertEquals(firstF + "_" + version + "." + endF, versionFileName);

        versionFileName = RepositoryResourceUtil.getVersionFileName(file, null);
        assertEquals(firstF + "_" + VersionUtils.DEFAULT_VERSION + "." + endF, versionFileName);

        versionFileName = RepositoryResourceUtil.getVersionFileName(new File(firstF), null);
        assertEquals(firstF, versionFileName);
    }

    @Test
    public void testIsSystemFolder() throws Exception {
        String folderName = "system";
        FolderType[] folderTypes = { FolderType.FOLDER_LITERAL, FolderType.STABLE_SYSTEM_FOLDER_LITERAL };

        Method declaredMethod = RepositoryResourceUtil.class.getDeclaredMethod("isSystemFolder", Item.class, String.class);
        declaredMethod.setAccessible(true);

        ContainerItem mockContainerItem = mock(ContainerItem.class);

        when(mockContainerItem.getType()).thenReturn(FolderType.SYSTEM_FOLDER_LITERAL);
        boolean result = (boolean) declaredMethod.invoke(null, mockContainerItem, folderName);
        assertTrue(result);

        for (FolderType folderType : folderTypes) {
            when(mockContainerItem.getType()).thenReturn(folderType);
            result = (boolean) declaredMethod.invoke(null, mockContainerItem, folderName);
            assertFalse(result);
        }

        Item mockItem = mock(Item.class);
        result = (boolean) declaredMethod.invoke(null, mockItem, folderName);
        assertFalse(result);
    }

    @Test
    public void testCreateDeletedFolderViewObject() throws Exception {
        ERepositoryObjectType type = null;
        String path = "mocked_path";
        String folderName = "folderName";

        List<IRepositoryViewObject> children = new ArrayList<IRepositoryViewObject>();
        List<IRepositoryViewObject> spychildren = Mockito.spy(children);

        StubFolderRepositoryObject mockParentFolderObject = mock(StubFolderRepositoryObject.class);
        when(mockParentFolderObject.getChildren()).thenReturn(spychildren);

        FolderRepositoryObject deletedFolderViewObject = RepositoryResourceUtil.createDeletedFolderViewObject(type, path,
                folderName, mockParentFolderObject);
        IRepositoryViewObject iRepositoryViewObject = ContainerCacheService.get(type, path);
        ContainerCacheService.removeContainer(type, path);

        assertNotNull(deletedFolderViewObject);
        assertSame(deletedFolderViewObject, iRepositoryViewObject);
        assertTrue(spychildren.contains(deletedFolderViewObject));
    }

    /**
     * Test for: getCategoryViewObject(IRepositoryNodeConfiguration conf)
     */
    @Test
    public void testGetCategoryViewObject() throws Exception {
        ERepositoryObjectType type = IServerObjectRepositoryType.TYPE_CUSTOM_FORM;
        String label = "anystring";

        IRepositoryNodeConfiguration mockConfiguration = mock(IRepositoryNodeConfiguration.class);

        IRepositoryNodeResourceProvider mockResourceProvider = mock(IRepositoryNodeResourceProvider.class);
        when(mockResourceProvider.getRepositoryObjectType(Mockito.any(Item.class))).thenReturn(type);
        when(mockConfiguration.getResourceProvider()).thenReturn(mockResourceProvider);

        IRepositoryNodeLabelProvider mockLabelProvider = mock(IRepositoryNodeLabelProvider.class);
        when(mockConfiguration.getLabelProvider()).thenReturn(mockLabelProvider);
        when(mockLabelProvider.getCategoryLabel(Mockito.any(ERepositoryObjectType.class))).thenReturn(label);

        IRepositoryViewObject categoryViewObject = RepositoryResourceUtil.getCategoryViewObject(mockConfiguration);
        IRepositoryViewObject iRepositoryViewObject = ContainerCacheService.get(type, "");
        ContainerCacheService.removeContainer(type, "");
        assertNotNull(categoryViewObject);
        assertSame(categoryViewObject, iRepositoryViewObject);
        assertTrue(categoryViewObject.getProperty().getItem() instanceof ContainerItem);
        assertTrue(label.equals(((ContainerItem) categoryViewObject.getProperty().getItem()).getLabel()));
    }

    @Test
    public void testRemoveViewObjectPhysically() throws Exception {
        String name = "mockname";
        String version = VersionUtils.DEFAULT_VERSION;
        String path = "mockpath";
        ERepositoryObjectType mockType = mock(ERepositoryObjectType.class);

        // test the first and second parameter should not be null,and should throw exception of the expected type
        try {
            RepositoryResourceUtil.removeViewObjectPhysically(null, name, version, path);
            fail("First parameter should not be null.");
        } catch (Exception e) {
            if (e instanceof IllegalArgumentException) {
                assertTrue(true);
            } else {
                assertTrue("the throwed exception does not match", false);
            }
        }

        try {
            RepositoryResourceUtil.removeViewObjectPhysically(mockType, null, version, path);
            fail("Second parameter should not be null.");
        } catch (Exception e) {
            if (e instanceof IllegalArgumentException) {
                assertTrue(true);
            } else {
                assertTrue("the throwed exception does not match", false);
            }
        }

        // test method function
        IProxyRepositoryService mockRepoService = Mockito.mock(IProxyRepositoryService.class);
        IProxyRepositoryFactory repositoryFactory = Mockito.mock(IProxyRepositoryFactory.class);
        Mockito.when(mockRepoService.getProxyRepositoryFactory()).thenReturn(repositoryFactory);
        Field serviceField = GlobalServiceRegister.class.getDeclaredField("services");
        serviceField.setAccessible(true);
        Map<Class, IService> services = (Map<Class, IService>) serviceField.get(GlobalServiceRegister.getDefault());
        IService iService = services.get(IProxyRepositoryService.class);
        services.put(IProxyRepositoryService.class, mockRepoService);

        List<IRepositoryViewObject> viewObjects = new ArrayList<IRepositoryViewObject>();

        for (int i = 0; i < 3; i++) {
            IRepositoryViewObject mockViewObject = createMockedViewObject(name, version, path);
            viewObjects.add(mockViewObject);
        }

        when(repositoryFactory.getAll(mockType)).thenReturn(viewObjects);

        RepositoryResourceUtil.removeViewObjectPhysically(mockType, name, version, path);

        verify(repositoryFactory, Mockito.atLeastOnce()).deleteObjectPhysical(Mockito.any(IRepositoryViewObject.class),
                eq(version));
    }

    private IRepositoryViewObject createMockedViewObject(String name, String version, String path) {
        IRepositoryViewObject mockViewObject = mock(IRepositoryViewObject.class);
        Property mockProperty = mock(Property.class);
        when(mockViewObject.getProperty()).thenReturn(mockProperty);
        Item mockItem = mock(Item.class);
        when(mockProperty.getItem()).thenReturn(mockItem);
        when(mockProperty.getLabel()).thenReturn(name);
        when(mockProperty.getVersion()).thenReturn(version);
        ItemState mockItemState = mock(ItemState.class);
        when(mockItem.getState()).thenReturn(mockItemState);
        when(mockItemState.getPath()).thenReturn(path);

        return mockViewObject;
    }

    /**
     * Test for: findAllViewObjects(ERepositoryObjectType type, boolean useRepositoryViewObject, boolean withDeleted)
     */
    @Test
    public void testFindAllViewObjects() throws Exception {
        boolean withDeleted = false;
        boolean useRepositoryViewObject = true;

        ERepositoryObjectType mockType = mock(ERepositoryObjectType.class);

        IProxyRepositoryService mockRepoService = Mockito.mock(IProxyRepositoryService.class);
        IProxyRepositoryFactory mockFactory = Mockito.mock(IProxyRepositoryFactory.class);
        Mockito.when(mockRepoService.getProxyRepositoryFactory()).thenReturn(mockFactory);
        Field serviceField = GlobalServiceRegister.class.getDeclaredField("services");
        serviceField.setAccessible(true);
        Map<Class, IService> services = (Map<Class, IService>) serviceField.get(GlobalServiceRegister.getDefault());
        IService iService = services.get(IProxyRepositoryService.class);
        services.put(IProxyRepositoryService.class, mockRepoService);

        String propID = "propertyId";
        List<IRepositoryViewObject> viewObjects = new ArrayList<IRepositoryViewObject>();
        IRepositoryViewObject mockViewObject = mock(IRepositoryViewObject.class);
        Property mockProperty = mock(Property.class);
        when(mockViewObject.getProperty()).thenReturn(mockProperty);
        when(mockViewObject.getRepositoryObjectType()).thenReturn(mockType);
        Item mockItem = mock(Item.class);
        when(mockProperty.getItem()).thenReturn(mockItem);
        when(mockProperty.getId()).thenReturn(propID);
        ItemState mockItemState = mock(ItemState.class);
        when(mockItemState.isDeleted()).thenReturn(false);
        when(mockItem.getState()).thenReturn(mockItemState);

        String propID2 = "propertyId2";
        IRepositoryViewObject mockViewObject2 = mock(IRepositoryViewObject.class);
        Property mockProperty2 = mock(Property.class);
        when(mockViewObject2.getProperty()).thenReturn(mockProperty2);
        when(mockViewObject2.getRepositoryObjectType()).thenReturn(mockType);
        Item mockItem2 = mock(Item.class);
        when(mockProperty2.getItem()).thenReturn(mockItem2);
        when(mockProperty2.getId()).thenReturn(propID2);
        ItemState mockItemState2 = mock(ItemState.class);
        when(mockItemState2.isDeleted()).thenReturn(true);
        when(mockItem2.getState()).thenReturn(mockItemState2);

        viewObjects.add(mockViewObject);
        viewObjects.add(mockViewObject2);
        when(mockFactory.getAll(mockType, withDeleted)).thenReturn(viewObjects);

        IInteractiveHandler mockHandler = mock(IInteractiveHandler.class);
        when(mockHandler.getRepositoryObjectType()).thenReturn(mockType);
        Field handlersField = InteractiveService.class.getDeclaredField("handlers");
        handlersField.setAccessible(true);

        List<IInteractiveHandler> handlers = (List<IInteractiveHandler>) handlersField.get(null);
        List<IInteractiveHandler> backups = new ArrayList<>(handlers.size());
        backups.addAll(handlers);
        handlers.clear();
        handlers.add(mockHandler);

        IRepositoryViewObject mockViewObject3 = mock(IRepositoryViewObject.class);
        when(mockViewObject3.getProperty()).thenReturn(Mockito.mock(Property.class));
        ContainerCacheService.put(mockProperty, mockViewObject3);
        IRepositoryViewObject mockViewObject4 = mock(IRepositoryViewObject.class);
        when(mockViewObject4.getProperty()).thenReturn(Mockito.mock(Property.class));
        ContainerCacheService.put(mockProperty2, mockViewObject4);

        // withDeleted=false,useRepositoryViewObject=true
        List<IRepositoryViewObject> allViewObjects = RepositoryResourceUtil.findAllViewObjects(mockType, useRepositoryViewObject,
                withDeleted);
        assertEquals(1, allViewObjects.size());
        assertSame(ContainerCacheService.get(mockProperty), allViewObjects.get(0));
        assertSame(mockHandler, InteractiveService.findHandler(mockType));

        // withDeleted=false,useRepositoryViewObject=false
        useRepositoryViewObject = false;
        allViewObjects = RepositoryResourceUtil.findAllViewObjects(mockType, useRepositoryViewObject, withDeleted);
        assertEquals(1, allViewObjects.size());
        assertSame(mockViewObject, allViewObjects.get(0));

        // withDeleted=true,useRepositoryViewObject=true
        withDeleted = true;
        useRepositoryViewObject = true;
        when(mockFactory.getAll(mockType, withDeleted)).thenReturn(viewObjects);
        allViewObjects = RepositoryResourceUtil.findAllViewObjects(mockType, useRepositoryViewObject, withDeleted);
        assertEquals(2, allViewObjects.size());
        assertTrue(allViewObjects.contains(mockViewObject3));
        assertTrue(allViewObjects.contains(mockViewObject4));

        // withDeleted=true,useRepositoryViewObject=false
        useRepositoryViewObject = false;
        allViewObjects = RepositoryResourceUtil.findAllViewObjects(mockType, useRepositoryViewObject, withDeleted);
        assertEquals(2, allViewObjects.size());
        assertTrue(allViewObjects.containsAll(viewObjects));

        //
        ContainerCacheService.remove(mockProperty.getId());
        handlers.addAll(backups);
    }

    @Test
    public void testConvertToNode() throws Exception {
        IRepositoryViewObject mockViewObject = mock(IRepositoryViewObject.class);
        Property mockProperty = mock(Property.class);
        ContainerItem mockContainerItem = mock(ContainerItem.class);
        when(mockViewObject.getProperty()).thenReturn(mockProperty);
        when(mockViewObject.getLabel()).thenReturn("mockViewObjectLabel");
        when(mockProperty.getItem()).thenReturn(mockContainerItem);

        int[] folderType = { FolderType.SYSTEM_FOLDER, FolderType.STABLE_SYSTEM_FOLDER, FolderType.FOLDER };
        ENodeType[] enodeType = { ENodeType.SYSTEM_FOLDER, ENodeType.STABLE_SYSTEM_FOLDER, ENodeType.SIMPLE_FOLDER };

        int randomInt = RandomUtils.nextInt() % 3;
        FolderType fType = FolderType.get(folderType[randomInt]);
        when(mockContainerItem.getType()).thenReturn(fType);

        ERepositoryObjectType mockType = mock(ERepositoryObjectType.class);
        when(mockViewObject.getRepositoryObjectType()).thenReturn(mockType);

        RepositoryNode node = RepositoryResourceUtil.convertToNode(mockViewObject);
        assertNotNull(node);
        assertEquals(enodeType[randomInt], node.getType());
        assertEquals("mockViewObjectLabel", node.getProperties(EProperties.LABEL));
        assertEquals(mockType, node.getProperties(EProperties.CONTENT_TYPE));
    }

    @Test
    public void testFindViewObjectByNameVersion() throws Exception {
        ERepositoryObjectType type = IServerObjectRepositoryType.TYPE_DATAMODEL;
        String id = "id";
        String path = "path";
        List<String> versions = new ArrayList<>();

        IRepositoryViewObject originalViewObject = mock(IRepositoryViewObject.class);
        when(originalViewObject.getId()).thenReturn(id);

        ItemState mockState = mock(ItemState.class);
        when(mockState.getPath()).thenReturn(path);
        Item mockItem = mock(Item.class);
        when(mockItem.getState()).thenReturn(mockState);
        Property mockProperty = mock(Property.class);
        when(mockProperty.getItem()).thenReturn(mockItem);
        when(originalViewObject.getProperty()).thenReturn(mockProperty);

        List<IRepositoryViewObject> allVersions = new ArrayList<>();
        IRepositoryViewObject viewObject01 = mock(IRepositoryViewObject.class);
        when(viewObject01.getVersion()).thenReturn("0.1");
        IRepositoryViewObject viewObject02 = mock(IRepositoryViewObject.class);
        when(viewObject02.getVersion()).thenReturn("0.2");
        allVersions.add(viewObject01);
        allVersions.add(viewObject02);

        IProxyRepositoryService mockRepoService = Mockito.mock(IProxyRepositoryService.class);
        IProxyRepositoryFactory repositoryFactory = Mockito.mock(IProxyRepositoryFactory.class);
        Mockito.when(mockRepoService.getProxyRepositoryFactory()).thenReturn(repositoryFactory);
        Field serviceField = GlobalServiceRegister.class.getDeclaredField("services");
        serviceField.setAccessible(true);
        Map<Class, IService> services = (Map<Class, IService>) serviceField.get(GlobalServiceRegister.getDefault());
        IService iService = services.get(IProxyRepositoryService.class);
        services.put(IProxyRepositoryService.class, mockRepoService);

        when(repositoryFactory.getAllVersion(ProjectManager.getInstance().getCurrentProject(), id, path, type))
                .thenReturn(allVersions);

        IRepositoryViewObject findedViewObject = RepositoryResourceUtil.findViewObjectByVersion(type, "0.1",
                originalViewObject, versions);
        assertNotNull(findedViewObject);
        assertSame(viewObject01, findedViewObject);

        findedViewObject = RepositoryResourceUtil.findViewObjectByVersion(type, "0.2", originalViewObject, versions);
        assertNotNull(findedViewObject);
        assertSame(viewObject02, findedViewObject);

        findedViewObject = RepositoryResourceUtil.findViewObjectByVersion(type, null, originalViewObject, versions);
        assertNull(findedViewObject);

        findedViewObject = RepositoryResourceUtil.findViewObjectByVersion(type, " ", originalViewObject, versions);
        assertNull(findedViewObject);
    }

    /**
     * Test for: findViewObjectsInFolder(ERepositoryObjectType type, Item parentItem, boolean useRepositoryViewObject,
     * boolean withDeleted)
     */
    @Test
    public void testFindViewObjectsInFolder() throws Exception {
        boolean withDeleted = false;
        boolean useRepositoryViewObject = true;
        String path = "mockPath";

        ERepositoryObjectType mockType = mock(ERepositoryObjectType.class);

        Item mockParentItem = mock(Item.class);
        ItemState mockItemState = mock(ItemState.class);
        when(mockParentItem.getState()).thenReturn(mockItemState);
        when(mockItemState.getPath()).thenReturn(path);

        List<IRepositoryViewObject> viewObjects = new ArrayList<IRepositoryViewObject>();

        IRepositoryViewObject mockViewObject = mock(IRepositoryViewObject.class);
        Property mockProperty = mock(Property.class);
        when(mockViewObject.getProperty()).thenReturn(mockProperty);
        Item mockItem = mock(Item.class);
        when(mockProperty.getItem()).thenReturn(mockItem);
        ItemState mockState = mock(ItemState.class);
        when(mockState.isDeleted()).thenReturn(false);
        when(mockItem.getState()).thenReturn(mockState);
        when(mockState.getPath()).thenReturn(path);
        viewObjects.add(mockViewObject);

        RootContainer<String, IRepositoryViewObject> mockContainer = mock(RootContainer.class);
        Mockito.when(mockContainer.getMembers()).thenReturn(viewObjects);

        IProxyRepositoryService mockRepoService = Mockito.mock(IProxyRepositoryService.class);
        IProxyRepositoryFactory mockFactory = Mockito.mock(IProxyRepositoryFactory.class);
        Mockito.when(mockRepoService.getProxyRepositoryFactory()).thenReturn(mockFactory);
        Field serviceField = GlobalServiceRegister.class.getDeclaredField("services");
        serviceField.setAccessible(true);
        Map<Class, IService> services = (Map<Class, IService>) serviceField.get(GlobalServiceRegister.getDefault());
        IService iService = services.get(IProxyRepositoryService.class);
        services.put(IProxyRepositoryService.class, mockRepoService);

        int option = IRepositoryFactory.OPTION_DYNAMIC_OBJECTS | IRepositoryFactory.OPTION_NOT_INCLUDE_CHILDRENS
                | IRepositoryFactory.OPTION_ONLY_LAST_VERSION | IRepositoryFactory.OPTION_SKIP_DELETED;
        Mockito.when(mockFactory.getObjectFromFolder(ProjectManager.getInstance().getCurrentProject(), mockType, path, option))
                .thenReturn(mockContainer);

        List<IRepositoryViewObject> allViewObjectsInFolder = RepositoryResourceUtil.findViewObjectsInFolder(mockType,
                mockParentItem, useRepositoryViewObject, withDeleted);
        assertEquals(1, allViewObjectsInFolder.size());
        verify(mockFactory, Mockito.atLeastOnce()).getObjectFromFolder(ProjectManager.getInstance().getCurrentProject(), mockType,
                path, option);
        verify(mockContainer, Mockito.atLeastOnce()).getMembers();
    }

}
