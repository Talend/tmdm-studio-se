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
package org.talend.mdm.repository.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.support.membermodification.MemberMatcher.method;
import static org.powermock.api.support.membermodification.MemberModifier.stub;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.ui.IEditorReference;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.i18n.internal.DefaultMessagesImpl;
import org.talend.commons.runtime.model.repository.ERepositoryStatus;
import org.talend.commons.utils.VersionUtils;
import org.talend.commons.utils.workbench.resources.ResourceUtils;
import org.talend.core.model.general.Project;
import org.talend.core.model.properties.FolderType;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ItemState;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.Folder;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.model.repository.RepositoryNodeProviderRegistryReader;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.mdm.repository.core.service.ContainerCacheService;
import org.talend.mdm.repository.core.service.InteractiveService;
import org.talend.mdm.repository.core.service.RepositoryQueryService;
import org.talend.mdm.repository.extension.RepositoryNodeConfigurationManager;
import org.talend.mdm.workbench.serverexplorer.core.ServerDefService;
import org.talend.repository.ProjectManager;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.ResourceModelUtils;

import com.amalto.workbench.exadapter.ExAdapterManager;
import com.amalto.workbench.image.ImageCache;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ RepositoryResourceUtil.class, ImageDescriptor.class, JFaceResources.class, DefaultMessagesImpl.class,
        ImageCache.class, ItemState.class, ProjectManager.class, CoreRuntimePlugin.class, InteractiveService.class,
        ResourceModelUtils.class, FolderType.class, RepositoryNodeConfigurationManager.class, ResourceUtils.class,
        ContainerCacheService.class, RepositoryQueryService.class, RepositoryNodeProviderRegistryReader.class,
        ServerDefService.class, ERepositoryStatus.class, ERepositoryObjectType.class, ExAdapterManager.class,
        ProxyRepositoryFactory.class, })
public class RepositoryResourceUtilMockTest {

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
        PowerMockito.doCallRealMethod().when(RepositoryResourceUtil.class, "isLockedAndEdited", any(IRepositoryViewObject.class));
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
        PowerMockito.verifyStatic(ProjectManager.class, Mockito.atLeastOnce());
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
        when(proxyRepositoryFactory.createFolder(any(Project.class), any(ERepositoryObjectType.class), any(IPath.class),
                anyString())).thenReturn(mockTalendFolder);

        IRepositoryViewObject folderViewObject = RepositoryResourceUtil.createFolderViewObject(mockType, folderName,
                mockParentItem, isSystem);

        assertNotNull(folderViewObject);
        verify(proxyRepositoryFactory, times(1)).createFolder(any(Project.class), any(ERepositoryObjectType.class),
                any(IPath.class), anyString());
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

}
