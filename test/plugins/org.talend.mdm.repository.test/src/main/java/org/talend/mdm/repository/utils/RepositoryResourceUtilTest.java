// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
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

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.powermock.api.support.membermodification.MemberMatcher.*;
import static org.powermock.api.support.membermodification.MemberModifier.stub;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang.math.RandomUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.ui.IEditorReference;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.powermock.reflect.Whitebox;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.i18n.internal.DefaultMessagesImpl;
import org.talend.commons.runtime.model.repository.ERepositoryStatus;
import org.talend.commons.utils.VersionUtils;
import org.talend.commons.utils.data.container.RootContainer;
import org.talend.commons.utils.workbench.resources.ResourceUtils;
import org.talend.core.context.RepositoryContext;
import org.talend.core.model.general.Project;
import org.talend.core.model.properties.FolderType;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ItemState;
import org.talend.core.model.properties.PropertiesFactory;
import org.talend.core.model.properties.Property;
import org.talend.core.model.properties.User;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.Folder;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.model.repository.RepositoryNodeProviderRegistryReader;
import org.talend.core.model.repository.ResourceModelUtils;
import org.talend.core.repository.model.IRepositoryFactory;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.mdm.repository.core.IRepositoryNodeConfiguration;
import org.talend.mdm.repository.core.IRepositoryNodeLabelProvider;
import org.talend.mdm.repository.core.IRepositoryNodeResourceProvider;
import org.talend.mdm.repository.core.IServerObjectRepositoryType;
import org.talend.mdm.repository.core.impl.recyclebin.RecycleBinNodeConfiguration;
import org.talend.mdm.repository.core.service.ContainerCacheService;
import org.talend.mdm.repository.core.service.IInteractiveHandler;
import org.talend.mdm.repository.core.service.InteractiveService;
import org.talend.mdm.repository.core.service.RepositoryQueryService;
import org.talend.mdm.repository.extension.RepositoryNodeConfigurationManager;
import org.talend.mdm.repository.model.mdmproperties.ContainerItem;
import org.talend.mdm.repository.models.FolderRepositoryObject;
import org.talend.mdm.workbench.serverexplorer.core.ServerDefService;
import org.talend.repository.ProjectManager;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.IRepositoryNode.EProperties;
import org.talend.repository.model.RepositoryNode;

import com.amalto.workbench.exadapter.ExAdapterManager;
import com.amalto.workbench.image.ImageCache;

// @RunWith(PowerMockRunner.class)
@PrepareForTest({ RepositoryResourceUtil.class, ImageDescriptor.class, JFaceResources.class, DefaultMessagesImpl.class,
    ImageCache.class, ItemState.class, ProjectManager.class, CoreRuntimePlugin.class, InteractiveService.class,
    ResourceModelUtils.class, FolderType.class, RepositoryNodeConfigurationManager.class, ResourceUtils.class,
    ContainerCacheService.class, RepositoryQueryService.class, RepositoryNodeProviderRegistryReader.class,
        ServerDefService.class, ERepositoryStatus.class, ERepositoryObjectType.class, ExAdapterManager.class,
        ProxyRepositoryFactory.class,
})
public class RepositoryResourceUtilTest {

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    class StubFolderRepositoryObject extends FolderRepositoryObject {

        public StubFolderRepositoryObject(Property prop) {
            super(prop);
        }

        @Override
        public List<IRepositoryViewObject> getChildren() {
            return super.getChildren();
        }
    }

    @Before
    public void setUp() throws Exception {
        ResourceBundle rb = mock(ResourceBundle.class);
        stub(method(ResourceBundle.class, "getBundle", String.class)).toReturn(rb); //$NON-NLS-1$
        PowerMockito.mockStatic(JFaceResources.class);
        ImageRegistry registry = mock(ImageRegistry.class);
        when(JFaceResources.getImageRegistry()).thenReturn(registry);
        PowerMockito.mockStatic(DefaultMessagesImpl.class);
        when(DefaultMessagesImpl.getString(anyString())).thenReturn("anyString()"); //$NON-NLS-1$

        IRepositoryResourceUtilExAdapter mockAdapter = PowerMockito.mock(IRepositoryResourceUtilExAdapter.class);
        PowerMockito.mockStatic(ExAdapterManager.class);
        PowerMockito.when(ExAdapterManager.getAdapter(new RepositoryResourceUtil(), IRepositoryResourceUtilExAdapter.class))
        .thenReturn(mockAdapter);

        PowerMockito.mockStatic(CoreRuntimePlugin.class);
        CoreRuntimePlugin coreRuntimePlugin = mock(CoreRuntimePlugin.class);
        when(CoreRuntimePlugin.getInstance()).thenReturn(coreRuntimePlugin);

        PowerMockito.mockStatic(RepositoryNodeProviderRegistryReader.class);
        RepositoryNodeProviderRegistryReader reader = mock(RepositoryNodeProviderRegistryReader.class);
        PowerMockito.when(RepositoryNodeProviderRegistryReader.getInstance()).thenReturn(reader);
    }

    @Test
    public void testIsLockedViewObject() {
        IRepositoryViewObject mockViewObject = mock(IRepositoryViewObject.class);

        IProxyRepositoryFactory repositoryFactory = mock(IProxyRepositoryFactory.class);
        when(CoreRuntimePlugin.getInstance().getProxyRepositoryFactory()).thenReturn(repositoryFactory);

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
    public void testIsLockedAndEdited() throws Exception {
        IRepositoryViewObject mockViewObject = mock(IRepositoryViewObject.class);

        IProxyRepositoryFactory repositoryFactory = mock(IProxyRepositoryFactory.class);
        when(CoreRuntimePlugin.getInstance().getProxyRepositoryFactory()).thenReturn(repositoryFactory);

        // true
        when(repositoryFactory.getStatus(mockViewObject)).thenReturn(ERepositoryStatus.LOCK_BY_OTHER);
        boolean islockedViewObject = RepositoryResourceUtil.isLockedAndEdited(mockViewObject);
        assertTrue(islockedViewObject);

        when(repositoryFactory.getStatus(mockViewObject)).thenReturn(ERepositoryStatus.LOCK_BY_USER);

        PowerMockito.mockStatic(RepositoryResourceUtil.class);
        PowerMockito.doCallRealMethod().when(RepositoryResourceUtil.class, "isLockedAndEdited",
                (IRepositoryViewObject) Mockito.anyObject());
        when(RepositoryResourceUtil.isOpenedInEditor(mockViewObject)).thenReturn(mock(IEditorReference.class));
        islockedViewObject = RepositoryResourceUtil.isLockedAndEdited(mockViewObject);
        assertTrue(islockedViewObject);
        // false
        when(RepositoryResourceUtil.isOpenedInEditor(mockViewObject)).thenReturn(null);
        islockedViewObject = RepositoryResourceUtil.isLockedAndEdited(mockViewObject);
        assertFalse(islockedViewObject);

        // false
        ERepositoryStatus[] statuss = { ERepositoryStatus.DEFAULT, ERepositoryStatus.DELETED, ERepositoryStatus.EDITABLE,
                ERepositoryStatus.ERROR, ERepositoryStatus.NEW, ERepositoryStatus.NOT_UP_TO_DATE, ERepositoryStatus.READ_ONLY,
                ERepositoryStatus.WARN };
        for (ERepositoryStatus status : statuss) {
            when(repositoryFactory.getStatus(mockViewObject)).thenReturn(status);
            islockedViewObject = RepositoryResourceUtil.isLockedAndEdited(mockViewObject);
            assertFalse(islockedViewObject);
        }
    }

    @Test
    public void testIsLockedItem() {
        Item mockItem = mock(Item.class);

        IProxyRepositoryFactory repositoryFactory = mock(IProxyRepositoryFactory.class);
        when(CoreRuntimePlugin.getInstance().getProxyRepositoryFactory()).thenReturn(repositoryFactory);

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
        // following two lines code is handling errors when mockStatic class "RepositoryNodeConfigurationManager"
        RecycleBinNodeConfiguration mockRBNConf = mock(RecycleBinNodeConfiguration.class);
        PowerMockito.whenNew(RecycleBinNodeConfiguration.class).withNoArguments().thenReturn(mockRBNConf);

        PowerMockito.mockStatic(RepositoryNodeConfigurationManager.class);
        Item mockItem = mock(Item.class);
        IRepositoryNodeConfiguration mockConf = mock(IRepositoryNodeConfiguration.class);
        when(RepositoryNodeConfigurationManager.getConfiguration(mockItem)).thenReturn(mockConf);

        IRepositoryNodeResourceProvider mockResourceProvider = mock(IRepositoryNodeResourceProvider.class);
        when(mockConf.getResourceProvider()).thenReturn(mockResourceProvider);
        when(mockResourceProvider.needSaveReferenceFile()).thenReturn(true);

        IProxyRepositoryFactory repositoryFactory = mock(IProxyRepositoryFactory.class);
        when(CoreRuntimePlugin.getInstance().getProxyRepositoryFactory()).thenReturn(repositoryFactory);

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

        IProxyRepositoryFactory repositoryFactory = mock(IProxyRepositoryFactory.class);
        when(CoreRuntimePlugin.getInstance().getProxyRepositoryFactory()).thenReturn(repositoryFactory);

        RepositoryContext mockContext = mock(RepositoryContext.class);
        when(repositoryFactory.getRepositoryContext()).thenReturn(mockContext);
        when(repositoryFactory.getNextId()).thenReturn(nextId);

        Property mockProperty = mock(Property.class);
        when(mockItem.getProperty()).thenReturn(mockProperty);

        PropertiesFactory proFactory = PropertiesFactory.eINSTANCE;
        PropertiesFactory spyProFactory = PowerMockito.spy(proFactory);
        when(spyProFactory.createProperty()).thenReturn(mockProperty);

        User user = mock(User.class);
        when(mockContext.getUser()).thenReturn(user);

        // following two lines code is handling errors when mockStatic class "RepositoryNodeConfigurationManager"
        RecycleBinNodeConfiguration mockRBNConf = mock(RecycleBinNodeConfiguration.class);
        PowerMockito.whenNew(RecycleBinNodeConfiguration.class).withNoArguments().thenReturn(mockRBNConf);

        PowerMockito.mockStatic(RepositoryNodeConfigurationManager.class);
        IRepositoryNodeConfiguration mockConfManager = mock(IRepositoryNodeConfiguration.class);
        when(RepositoryNodeConfigurationManager.getConfiguration(Mockito.any(Item.class))).thenReturn(mockConfManager);

        IRepositoryNodeResourceProvider mockResourceProvider = mock(IRepositoryNodeResourceProvider.class);
        when(mockConfManager.getResourceProvider()).thenReturn(mockResourceProvider);
        when(mockResourceProvider.needSaveReferenceFile()).thenReturn(true);
        when(repositoryFactory.isEditableAndLockIfPossible(mockItem)).thenReturn(true);

        Resource mockResource = mock(Resource.class);
        ResourceSet mockResourceSet = mock(ResourceSet.class);
        when(mockResource.getResourceSet()).thenReturn(mockResourceSet);
        when(mockItem.eResource()).thenReturn(mockResource);

        boolean createItem = RepositoryResourceUtil.createItem(mockItem, propLabel);
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
    public void testCopyOSFileTOProject() throws Exception {
        String path = "srcpath";
        IFolder desFolder = mock(IFolder.class);
        String version = VersionUtils.DEFAULT_VERSION;
        boolean overwrite = true;
        IProgressMonitor progressMonitor = mock(IProgressMonitor.class);

        IProject mockIProject = mock(IProject.class);

        // verify that if second or third parameter is null will throws exception of IllegalArgumentException type
        try {
            RepositoryResourceUtil.copyOSFileTOProject(mockIProject, null, desFolder, version, overwrite, progressMonitor);
            fail();
        } catch (Exception e) {
            if (e instanceof IllegalArgumentException) {
                assertTrue(true);
            } else {
                assertTrue(false);
            }
        }

        try {
            RepositoryResourceUtil.copyOSFileTOProject(mockIProject, path, null, version, overwrite, progressMonitor);
            fail();
        } catch (Exception e) {
            if (e instanceof IllegalArgumentException) {
                assertTrue(true);
            } else {
                assertTrue(false);
            }
        }

        // if first parameter is null, verify it will call method to create a IProject instance
        PowerMockito.mockStatic(ProjectManager.class);
        ProjectManager mockProjectManager = mock(ProjectManager.class);
        when(ProjectManager.getInstance()).thenReturn(mockProjectManager);
        Project mockProject = mock(Project.class);
        when(ProjectManager.getInstance().getCurrentProject()).thenReturn(mockProject);

        PowerMockito.mockStatic(ResourceUtils.class);
        when(ResourceUtils.getProject(mockProject)).thenReturn(mockIProject);

        String invalidPath = "mm:\\ss";
        when(desFolder.exists()).thenReturn(true);

        IFile copyOfFile = RepositoryResourceUtil.copyOSFileTOProject(null, invalidPath, desFolder, version, overwrite,
                progressMonitor);
        PowerMockito.verifyStatic(Mockito.atLeastOnce());
        ProjectManager.getInstance();
        ResourceUtils.getProject(Mockito.any(Project.class));
        assertNull(copyOfFile);

        // verify if the file that defined by second parameter does not exist, return null
        String validPath = "d:\\ss";

        File file = new File(validPath);
        File spyFile = Mockito.spy(file);
        PowerMockito.whenNew(File.class).withArguments(validPath).thenReturn(spyFile);
        when(spyFile.exists()).thenReturn(true);
        when(spyFile.getName()).thenReturn("ss");
        IFile mockIFile = mock(IFile.class);
        when(desFolder.getFile(Mockito.anyString())).thenReturn(mockIFile);
        IPath mockIPath = mock(IPath.class);
        when(mockIFile.getLocation()).thenReturn(mockIPath);
        when(mockIPath.toOSString()).thenReturn(validPath);

        FileInputStream inputStream = mock(FileInputStream.class);
        PowerMockito.whenNew(FileInputStream.class).withArguments(spyFile).thenReturn(inputStream);

        IFile copiedFile = RepositoryResourceUtil.copyOSFileTOProject(mockIProject, validPath, desFolder, version, overwrite,
                progressMonitor);
        assertNotNull(copiedFile);

        verify(inputStream, Mockito.times(1)).close();
    }

    /**
     * Test for: getFolder(ERepositoryObjectType type, Item item)
     */
    @Test
    public void testGetFolder2Args() throws PersistenceException {
        PowerMockito.mockStatic(ProjectManager.class);
        ProjectManager mockProjectManager = mock(ProjectManager.class);
        when(ProjectManager.getInstance()).thenReturn(mockProjectManager);
        Project mockProject = mock(Project.class);
        when(ProjectManager.getInstance().getCurrentProject()).thenReturn(mockProject);

        PowerMockito.mockStatic(ResourceUtils.class);
        IProject mockIProject = mock(IProject.class);
        when(ResourceUtils.getProject(mockProject)).thenReturn(mockIProject);

        Item mockItem = mock(Item.class);
        ItemState mockState = mock(ItemState.class);
        when(mockItem.getState()).thenReturn(mockState);
        when(mockState.getPath()).thenReturn("mockStatePath");

        ERepositoryObjectType mockType = mock(ERepositoryObjectType.class);
        PowerMockito.mockStatic(ERepositoryObjectType.class);
        when(ERepositoryObjectType.getFolderName(mockType)).thenReturn("mockPath");

        IFolder mockFolder = mock(IFolder.class);
        when(mockIProject.getFolder(Mockito.anyString())).thenReturn(mockFolder);

        IFolder folder = RepositoryResourceUtil.getFolder(mockType, mockItem);
        assertEquals(mockFolder, folder);

    }

    @Test
    public void testGetTextFileContent() throws Exception {
        String encode = "UTF-8";
        String filePath = "testfile.txt";//

        IFile mockFile = mock(IFile.class);
        IPath mockPath = mock(IPath.class);
        when(mockFile.getLocation()).thenReturn(mockPath);
        when(mockFile.getLocation().toOSString()).thenReturn(filePath);
        when(mockFile.exists()).thenReturn(true);

        File file = mock(File.class);
        when(file.exists()).thenReturn(true);
        PowerMockito.whenNew(File.class).withArguments(Mockito.anyString()).thenReturn(file);
        PowerMockito.doNothing().when(mockFile).refreshLocal(0, null);

        String textContent = "this is the test content!";
        byte[] buf = textContent.getBytes(encode);
        FileInputStream mockFileInputStream = PowerMockito.mock(FileInputStream.class);
        PowerMockito.whenNew(FileInputStream.class).withArguments(file).thenReturn(mockFileInputStream);
        PowerMockito.when(mockFileInputStream.available()).thenReturn(0);

        ByteArrayOutputStream spyOutputStream = Mockito.spy(new ByteArrayOutputStream());
        PowerMockito.whenNew(ByteArrayOutputStream.class).withNoArguments().thenReturn(spyOutputStream);
        spyOutputStream.write(buf);

        String content = RepositoryResourceUtil.getTextFileContent(mockFile, encode);
        assertEquals(textContent, content);

        verify(mockFileInputStream, Mockito.atLeastOnce()).read(Matchers.<byte[]> any());
        verify(mockFileInputStream, Mockito.atLeastOnce()).close();
        verify(spyOutputStream, Mockito.atLeastOnce()).close();
    }

    /**
     * Test for: getFolder(ERepositoryObjectType type)
     */
    @Test
    public void testGetFolder() throws Exception {
        PowerMockito.mockStatic(ProjectManager.class);
        ProjectManager mockProjectManager = mock(ProjectManager.class);
        when(ProjectManager.getInstance()).thenReturn(mockProjectManager);
        Project mockProject = mock(Project.class);
        when(ProjectManager.getInstance().getCurrentProject()).thenReturn(mockProject);

        PowerMockito.mockStatic(ResourceUtils.class);
        IProject mockIProject = mock(IProject.class);
        when(ResourceUtils.getProject(mockProject)).thenReturn(mockIProject);

        IFolder mockFolder = mock(IFolder.class);
        String processFolder = "process"; //$NON-NLS-1$
        when(ResourceUtils.getFolder(mockIProject, processFolder, true)).thenReturn(mockFolder);

        PowerMockito.mockStatic(ERepositoryObjectType.class);
        ERepositoryObjectType mockType = mock(ERepositoryObjectType.class);
        when(ERepositoryObjectType.getFolderName(mockType)).thenReturn(processFolder);

        IFolder folder = RepositoryResourceUtil.getFolder(mockType);
        assertEquals(mockFolder, folder);
    }

    @Test
    public void testIsSystemFolder() throws Exception {
        String folderName = "system";
        FolderType[] folderTypes = { FolderType.FOLDER_LITERAL, FolderType.STABLE_SYSTEM_FOLDER_LITERAL };

        ContainerItem mockContainerItem = mock(ContainerItem.class);

        when(mockContainerItem.getType()).thenReturn(FolderType.SYSTEM_FOLDER_LITERAL);
        boolean result = Whitebox.invokeMethod(RepositoryResourceUtil.class, "isSystemFolder", mockContainerItem, folderName);
        assertTrue(result);

        for (FolderType folderType : folderTypes) {
            when(mockContainerItem.getType()).thenReturn(folderType);
            result = Whitebox.invokeMethod(RepositoryResourceUtil.class, "isSystemFolder", mockContainerItem, folderName);
            assertFalse(result);
        }

        Item mockItem = mock(Item.class);
        result = Whitebox.invokeMethod(RepositoryResourceUtil.class, "isSystemFolder", mockItem, folderName);
        assertFalse(result);
    }

    @Test
    public void testCreateFolderViewObject() throws Exception {
        boolean isSystem = false;
        String folderName = "folderName";

        PowerMockito.mockStatic(ProjectManager.class);
        ProjectManager mockProjectManager = mock(ProjectManager.class);
        Project mockProject = mock(Project.class);
        IProject mockIProject = mock(IProject.class);
        when(ProjectManager.getInstance()).thenReturn(mockProjectManager);
        when(ProjectManager.getInstance().getCurrentProject()).thenReturn(mockProject);

        IFolder mockFolder = mock(IFolder.class);
        PowerMockito.mockStatic(ResourceUtils.class);
        when(ResourceUtils.getProject(mockProject)).thenReturn(mockIProject);
        when(mockIProject.getFolder(Mockito.anyString())).thenReturn(mockFolder);
        when(mockFolder.exists()).thenReturn(false);
        PowerMockito.doNothing().when(ResourceUtils.class, "createFolder", mockFolder);

        ItemState mockItemState = mock(ItemState.class);
        when(mockItemState.getPath()).thenReturn("mocked_path");

        Item mockParentItem = mock(Item.class);
        when(mockParentItem.getState()).thenReturn(mockItemState);
        String processFolder = "process";
        PowerMockito.mockStatic(ERepositoryObjectType.class);
        ERepositoryObjectType mockType = mock(ERepositoryObjectType.class);
        when(mockType.getType()).thenReturn("mockType");
        when(ERepositoryObjectType.getFolderName(mockType)).thenReturn(processFolder);

        PowerMockito.mockStatic(ProxyRepositoryFactory.class);
        ProxyRepositoryFactory proxyRepositoryFactory = mock(ProxyRepositoryFactory.class);
        when(ProxyRepositoryFactory.getInstance()).thenReturn(proxyRepositoryFactory);
        Folder mockTalendFolder = mock(Folder.class);
        when(
                proxyRepositoryFactory.createFolder(any(Project.class), any(ERepositoryObjectType.class), any(IPath.class),
                        anyString())).thenReturn(mockTalendFolder);

        IRepositoryViewObject folderViewObject = RepositoryResourceUtil.createFolderViewObject(mockType, folderName,
                mockParentItem, isSystem);

        assertNotNull(folderViewObject);
        verify(proxyRepositoryFactory, times(1)).createFolder(any(Project.class), any(ERepositoryObjectType.class),
                any(IPath.class), anyString());
    }

    @Test
    public void testCreateDeletedFolderViewObject() throws Exception {
        ERepositoryObjectType type = null;
        String path = "mocked_path";
        String folderName = "folderName";

        List<IRepositoryViewObject> children = new ArrayList<IRepositoryViewObject>();
        List<IRepositoryViewObject> spychildren = PowerMockito.spy(children);

        StubFolderRepositoryObject mockParentFolderObject = mock(StubFolderRepositoryObject.class);
        when(mockParentFolderObject.getChildren()).thenReturn(spychildren);

        PowerMockito.mockStatic(ContainerCacheService.class);
        PowerMockito.doNothing().when(ContainerCacheService.class, "putContainer", Mockito.any(IRepositoryViewObject.class));

        FolderRepositoryObject deletedFolderViewObject = RepositoryResourceUtil.createDeletedFolderViewObject(type, path,
                folderName, mockParentFolderObject);

        assertNotNull(deletedFolderViewObject);
    }

    /**
     * Test for: getCategoryViewObject(IRepositoryNodeConfiguration conf)
     */
    @Test
    public void testGetCategoryViewObject() throws Exception {
        IRepositoryNodeConfiguration mockConfiguration = mock(IRepositoryNodeConfiguration.class);

        IRepositoryNodeResourceProvider mockResourceProvider = mock(IRepositoryNodeResourceProvider.class);
        when(mockConfiguration.getResourceProvider()).thenReturn(mockResourceProvider);

        ERepositoryObjectType mockType = mock(ERepositoryObjectType.class);
        when(mockConfiguration.getResourceProvider().getRepositoryObjectType(Mockito.any(Item.class))).thenReturn(mockType);

        IRepositoryNodeLabelProvider mockLabelProvider = mock(IRepositoryNodeLabelProvider.class);
        when(mockConfiguration.getLabelProvider()).thenReturn(mockLabelProvider);
        when(mockLabelProvider.getCategoryLabel(Mockito.any(ERepositoryObjectType.class))).thenReturn("anystring");

        PowerMockito.mockStatic(ContainerCacheService.class);
        PowerMockito.doNothing().when(ContainerCacheService.class, "putContainer", Mockito.any(IRepositoryViewObject.class));

        IRepositoryViewObject categoryViewObject = RepositoryResourceUtil.getCategoryViewObject(mockConfiguration);
        assertNotNull(categoryViewObject);
    }

    @Test
    public void testRemoveViewObjectPhysically() throws PersistenceException {
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
        IProxyRepositoryFactory mockFactory = mock(IProxyRepositoryFactory.class);
        when(CoreRuntimePlugin.getInstance().getProxyRepositoryFactory()).thenReturn(mockFactory);

        List<IRepositoryViewObject> viewObjects = new ArrayList<IRepositoryViewObject>();

        for (int i = 0; i < 3; i++) {
            IRepositoryViewObject mockViewObject = createMockedViewObject(name, version, path);
            viewObjects.add(mockViewObject);
        }

        when(mockFactory.getAll(mockType)).thenReturn(viewObjects);

        RepositoryResourceUtil.removeViewObjectPhysically(mockType, name, version, path);

        verify(mockFactory, Mockito.atLeastOnce()).deleteObjectPhysical(Mockito.any(IRepositoryViewObject.class), eq(version));
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

        IProxyRepositoryFactory mockFactory = mock(IProxyRepositoryFactory.class);
        when(CoreRuntimePlugin.getInstance().getProxyRepositoryFactory()).thenReturn(mockFactory);

        List<IRepositoryViewObject> viewObjects = new ArrayList<IRepositoryViewObject>();
        IRepositoryViewObject mockViewObject = mock(IRepositoryViewObject.class);
        Property mockProperty = mock(Property.class);
        when(mockViewObject.getProperty()).thenReturn(mockProperty);
        when(mockViewObject.getRepositoryObjectType()).thenReturn(mockType);
        Item mockItem = mock(Item.class);
        when(mockProperty.getItem()).thenReturn(mockItem);
        ItemState mockItemState = mock(ItemState.class);
        when(mockItemState.isDeleted()).thenReturn(false);
        when(mockItem.getState()).thenReturn(mockItemState);
        viewObjects.add(mockViewObject);
        when(mockFactory.getAll(mockType, withDeleted)).thenReturn(viewObjects);

        PowerMockito.mockStatic(InteractiveService.class);
        IInteractiveHandler mockHandler = mock(IInteractiveHandler.class);
        when(InteractiveService.findHandler(mockType)).thenReturn(mockHandler);

        PowerMockito.mockStatic(RepositoryResourceUtil.class);
        IRepositoryViewObject mockResultViewObject = mock(IRepositoryViewObject.class);
        PowerMockito.when(RepositoryResourceUtil.class, "getCacheViewObject", mockProperty, mockViewObject).thenReturn(
                mockResultViewObject);
        PowerMockito.when(RepositoryResourceUtil.class, "assertViewObject", mockViewObject).thenReturn(mockViewObject);

        PowerMockito.doCallRealMethod().when(RepositoryResourceUtil.class, "findAllViewObjects", mockType,
                useRepositoryViewObject, withDeleted);
        List<IRepositoryViewObject> viewObjectss = RepositoryResourceUtil.findAllViewObjects(mockType, useRepositoryViewObject,
                withDeleted);

        assertEquals(1, viewObjectss.size());
        PowerMockito.verifyStatic(Mockito.atLeastOnce());
        InteractiveService.findHandler(mockType);
    }

    /**
     * Test for: findViewObjects(ERepositoryObjectType type, Item parentItem, boolean useRepositoryViewObject, boolean
     * withDeleted)
     */
    @Test
    public void testFindViewObjects4Args() throws Exception {
        boolean withDeleted = false;
        boolean useRepositoryViewObject = true;
        String path = "mockPath";
        String parentPath = "parent";

        Item mockParentItem = mock(Item.class);
        ItemState mockItemState = mock(ItemState.class);
        when(mockParentItem.getState()).thenReturn(mockItemState);
        when(mockItemState.getPath()).thenReturn(parentPath);

        PowerMockito.mockStatic(ProjectManager.class);
        ProjectManager mockManager = mock(ProjectManager.class);
        when(ProjectManager.getInstance()).thenReturn(mockManager);
        Project mockProject = mock(Project.class);
        when(ProjectManager.getInstance().getCurrentProject()).thenReturn(mockProject);

        IProject mockIProject = mock(IProject.class);
        PowerMockito.mockStatic(ResourceUtils.class);
        when(ResourceUtils.getProject(mockProject)).thenReturn(mockIProject);

        PowerMockito.mockStatic(ERepositoryObjectType.class);
        ERepositoryObjectType mockType = mock(ERepositoryObjectType.class);
        when(ERepositoryObjectType.getFolderName(mockType)).thenReturn(path);

        IFolder mockFolder = mock(IFolder.class);
        when(mockIProject.getFolder(any(IPath.class))).thenReturn(mockFolder);

        List<IRepositoryViewObject> viewObjects = new ArrayList<IRepositoryViewObject>();
        viewObjects.add(mock(IRepositoryViewObject.class));
        PowerMockito.mockStatic(RepositoryResourceUtil.class);
        when(RepositoryResourceUtil.findViewObjects(mockType, mockParentItem, mockFolder, useRepositoryViewObject, withDeleted))
        .thenReturn(viewObjects);

        PowerMockito.doCallRealMethod().when(RepositoryResourceUtil.class, "findViewObjects", mockType, mockParentItem,
                useRepositoryViewObject, withDeleted);
        List<IRepositoryViewObject> result = RepositoryResourceUtil.findViewObjects(mockType, mockParentItem,
                useRepositoryViewObject, withDeleted);
        assertEquals(1, result.size());
        assertSame(viewObjects, result);

        when(ERepositoryObjectType.getFolderName(mockType)).thenReturn("");
        result = RepositoryResourceUtil.findViewObjects(mockType, mockParentItem, useRepositoryViewObject, withDeleted);
        assertEquals(0, result.size());
        assertEquals(Collections.EMPTY_LIST, result);

    }

    /**
     * Test for: findViewObjects(ERepositoryObjectType type, Item parentItem, IFolder folder, boolean
     * useRepositoryViewObject, boolean withDeleted)
     */
    @Test
    public void testFindViewObjects5Args() throws Exception {
        boolean withDeleted = false;
        boolean useRepositoryViewObject = true;

        Item mockParentItem = mock(Item.class);
        String path = "mockPath"; //$NON-NLS-1$
        ItemState mockItemState = mock(ItemState.class);
        when(mockParentItem.getState()).thenReturn(mockItemState);
        when(mockItemState.getPath()).thenReturn(path);

        ERepositoryObjectType mockType = mock(ERepositoryObjectType.class);

        IFolder[] mockResources = { mock(IFolder.class), mock(IFolder.class), mock(IFolder.class) };
        for (IFolder folder : mockResources) {
            when(folder.getName()).thenReturn("folder");
        }
        IFolder mockFolder = mock(IFolder.class);
        when(mockFolder.members()).thenReturn(mockResources);
        when(mockFolder.exists()).thenReturn(true);

        PowerMockito.mockStatic(RepositoryResourceUtil.class);
        PowerMockito.doReturn(false).when(RepositoryResourceUtil.class, "isDeletedFolder", mockResources[0]);
        PowerMockito.doReturn(false).when(RepositoryResourceUtil.class, "isDeletedFolder", mockResources[1]);
        PowerMockito.doReturn(false).when(RepositoryResourceUtil.class, "isDeletedFolder", mockResources[2]);
        PowerMockito.doReturn(false).when(RepositoryResourceUtil.class, "isSVNFolder", mockResources[0]);
        PowerMockito.doReturn(false).when(RepositoryResourceUtil.class, "isSVNFolder", mockResources[1]);
        PowerMockito.doReturn(false).when(RepositoryResourceUtil.class, "isSVNFolder", mockResources[2]);
        IRepositoryViewObject mock1ViewObject = mock(IRepositoryViewObject.class);
        when(RepositoryResourceUtil.createFolderViewObject(mockType, "folder", mockParentItem, false))
        .thenReturn(mock1ViewObject);

        PowerMockito.mockStatic(ContainerCacheService.class);
        // to mock ContainerCacheService.get(type, resPath);
        IRepositoryViewObject folderObject = mock(IRepositoryViewObject.class);
        PowerMockito.doReturn(folderObject).when(ContainerCacheService.class, "get", mockType, path); //$NON-NLS-1$

        List<IRepositoryViewObject> mockViewObjects = new ArrayList<IRepositoryViewObject>();
        mockViewObjects.add(mock(IRepositoryViewObject.class));
        mockViewObjects.add(mock(IRepositoryViewObject.class));
        mockViewObjects.add(mock(IRepositoryViewObject.class));
        when(RepositoryResourceUtil.findViewObjectsInFolder(mockType, mockParentItem, useRepositoryViewObject, withDeleted))
        .thenReturn(mockViewObjects);

        PowerMockito.doCallRealMethod().when(RepositoryResourceUtil.class, "findViewObjects", mockType, mockParentItem,
                mockFolder, useRepositoryViewObject, withDeleted);
        List<IRepositoryViewObject> allViewObjects = RepositoryResourceUtil.findViewObjects(mockType, mockParentItem, mockFolder,
                useRepositoryViewObject, withDeleted);

        assertEquals(6, allViewObjects.size());
        assertTrue(allViewObjects.containsAll(mockViewObjects));
        assertTrue(allViewObjects.contains(mock1ViewObject));
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
        PowerMockito.when(mockContainer.getMembers()).thenReturn(viewObjects);

        IProxyRepositoryFactory mockFactory = mock(IProxyRepositoryFactory.class);
        when(CoreRuntimePlugin.getInstance().getProxyRepositoryFactory()).thenReturn(mockFactory);
        PowerMockito.mockStatic(ProjectManager.class);
        ProjectManager mockProjectManager = mock(ProjectManager.class);
        Project mockProject = mock(Project.class);
        PowerMockito.when(ProjectManager.getInstance()).thenReturn(mockProjectManager);
        PowerMockito.when(ProjectManager.getInstance().getCurrentProject()).thenReturn(mockProject);

        int option = IRepositoryFactory.OPTION_DYNAMIC_OBJECTS | IRepositoryFactory.OPTION_NOT_INCLUDE_CHILDRENS
                | IRepositoryFactory.OPTION_ONLY_LAST_VERSION | IRepositoryFactory.OPTION_SKIP_DELETED;
        PowerMockito.when(mockFactory.getObjectFromFolder(mockProject, mockType, path, option)).thenReturn(mockContainer);

        List<IRepositoryViewObject> allViewObjectsInFolder = RepositoryResourceUtil.findViewObjectsInFolder(mockType,
                mockParentItem, useRepositoryViewObject, withDeleted);
        assertEquals(1, allViewObjectsInFolder.size());
        verify(mockFactory, Mockito.atLeastOnce()).getObjectFromFolder(mockProject, mockType, path, option);
        verify(mockContainer, Mockito.atLeastOnce()).getMembers();
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
        FolderType fType = mock(FolderType.class);
        // when(fType.getValue()).thenReturn(folderType[randomInt]);
        PowerMockito.stub(PowerMockito.method(FolderType.class, "getValue")).toReturn(folderType[randomInt]);
        when(mockContainerItem.getType()).thenReturn(fType);

        ERepositoryObjectType mockType = mock(ERepositoryObjectType.class);
        when(mockViewObject.getRepositoryObjectType()).thenReturn(mockType);

        RepositoryNode node = RepositoryResourceUtil.convertToNode(mockViewObject);
        assertNotNull(node);
        assertEquals(enodeType[randomInt], node.getType());
        assertEquals("mockViewObjectLabel", node.getProperties(EProperties.LABEL));
        assertEquals(mockType, node.getProperties(EProperties.CONTENT_TYPE));
    }

    @SuppressWarnings("nls")
    @Test
    public void testFindViewObjectByNameVersion() throws Exception {
        ERepositoryObjectType type = IServerObjectRepositoryType.TYPE_DATAMODEL;
        String name = "Product";
        String version = "0.1";
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
        // when(originalViewObject.getProperty().getItem().getState().getPath()).thenReturn(path);

        PowerMockito.mockStatic(ProjectManager.class);
        ProjectManager mockProjectManager = mock(ProjectManager.class);
        when(ProjectManager.getInstance()).thenReturn(mockProjectManager);
        Project mockProject = mock(Project.class);
        when(ProjectManager.getInstance().getCurrentProject()).thenReturn(mockProject);

        List<IRepositoryViewObject> allVersions = new ArrayList<>();
        IRepositoryViewObject viewObject01 = mock(IRepositoryViewObject.class);
        when(viewObject01.getVersion()).thenReturn("0.1");
        IRepositoryViewObject viewObject02 = mock(IRepositoryViewObject.class);
        when(viewObject02.getVersion()).thenReturn("0.2");
        allVersions.add(viewObject01);
        allVersions.add(viewObject02);

        IProxyRepositoryFactory repositoryFactory = mock(IProxyRepositoryFactory.class);
        when(CoreRuntimePlugin.getInstance().getProxyRepositoryFactory()).thenReturn(repositoryFactory);
        when(repositoryFactory.getAllVersion(mockProject, id, path, type)).thenReturn(allVersions);

        IRepositoryViewObject findedViewObject = RepositoryResourceUtil.findViewObjectByNameVersion(type, name, "0.1",
                originalViewObject, versions);
        assertNotNull(findedViewObject);
        assertSame(viewObject01, findedViewObject);

        findedViewObject = RepositoryResourceUtil.findViewObjectByNameVersion(type, name, "0.2", originalViewObject, versions);
        assertNotNull(findedViewObject);
        assertSame(viewObject02, findedViewObject);

        findedViewObject = RepositoryResourceUtil.findViewObjectByNameVersion(type, name, null, originalViewObject, versions);
        assertNull(findedViewObject);

        findedViewObject = RepositoryResourceUtil.findViewObjectByNameVersion(type, name, " ", originalViewObject, versions);
        assertNull(findedViewObject);

    }
}
