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
package org.talend.mdm.repository.ui.actions;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.powermock.api.support.membermodification.MemberMatcher.*;
import static org.powermock.api.support.membermodification.MemberModifier.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.navigator.CommonViewer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.talend.commons.i18n.internal.DefaultMessagesImpl;
import org.talend.core.context.RepositoryContext;
import org.talend.core.model.general.Project;
import org.talend.core.model.properties.FolderItem;
import org.talend.core.model.properties.FolderType;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ItemState;
import org.talend.core.model.properties.Property;
import org.talend.core.model.properties.User;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.model.repository.RepositoryNodeProviderRegistryReader;
import org.talend.core.repository.model.IRepositoryFactory;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.repository.utils.XmiResourceManager;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.mdm.repository.core.IRepositoryNodeConfiguration;
import org.talend.mdm.repository.core.IRepositoryNodeResourceProvider;
import org.talend.mdm.repository.core.impl.recyclebin.RecycleBinNodeConfiguration;
import org.talend.mdm.repository.core.service.ContainerCacheService;
import org.talend.mdm.repository.extension.RepositoryNodeConfigurationManager;
import org.talend.mdm.repository.model.mdmproperties.ContainerItem;
import org.talend.mdm.repository.model.mdmproperties.MDMServerObjectItem;
import org.talend.mdm.repository.model.mdmserverobject.MDMServerObject;
import org.talend.mdm.repository.models.FolderRepositoryObject;
import org.talend.mdm.repository.utils.IRepositoryResourceUtilExAdapter;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;
import org.talend.repository.ProjectManager;
import org.talend.repository.model.IProxyRepositoryFactory;

import com.amalto.workbench.MDMWorbenchPlugin;
import com.amalto.workbench.exadapter.ExAdapterManager;
import com.amalto.workbench.image.ImageCache;

/**
 * DOC hbhong class global comment. Detailled comment
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ RemoveFromRepositoryAction.class, ImageDescriptor.class, JFaceResources.class, DefaultMessagesImpl.class,
    ImageCache.class, ItemState.class, CoreRuntimePlugin.class, ProjectManager.class,
    RepositoryNodeConfigurationManager.class, IProxyRepositoryFactory.class, ProxyRepositoryFactory.class,
    MessageDialog.class, RepositoryResourceUtil.class, ContainerCacheService.class,
        RepositoryNodeProviderRegistryReader.class, ExAdapterManager.class, MDMWorbenchPlugin.class })
public class RemoveFromRepositoryActionTest {

    private ProxyRepositoryFactory repositoryFactory;

    private Project projectM;

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

        RepositoryContext contextMock = mock(RepositoryContext.class);

        PowerMockito.mockStatic(ProjectManager.class);
        ProjectManager pmMock = mock(ProjectManager.class);
        projectM = mock(Project.class);
        User userMock = mock(User.class);
        when(userMock.getLogin()).thenReturn("a@b.cn"); //$NON-NLS-1$
        when(projectM.getAuthor()).thenReturn(userMock);
        when(pmMock.getCurrentProject()).thenReturn(projectM);
        when(ProjectManager.getInstance()).thenReturn(pmMock);
        when(contextMock.getUser()).thenReturn(userMock);

        repositoryFactory = mock(ProxyRepositoryFactory.class);

        RecycleBinNodeConfiguration recycleBinNodeConfiguration = mock(RecycleBinNodeConfiguration.class);
        PowerMockito.whenNew(RecycleBinNodeConfiguration.class).withNoArguments().thenReturn(recycleBinNodeConfiguration);

        stub(method(ProxyRepositoryFactory.class, "getInstance")).toReturn(repositoryFactory); //$NON-NLS-1$

        when(CoreRuntimePlugin.getInstance().getProxyRepositoryFactory()).thenReturn(repositoryFactory);

        IRepositoryFactory repositoryFactoryMock = mock(IRepositoryFactory.class);
        when(repositoryFactory.getRepositoryFactoryFromProvider()).thenReturn(repositoryFactoryMock);
        XmiResourceManager xmiResourceManager = mock(XmiResourceManager.class);
        when(repositoryFactoryMock.getResourceManager()).thenReturn(xmiResourceManager);

        PowerMockito.mockStatic(RepositoryNodeConfigurationManager.class);
        IRepositoryNodeConfiguration rncMock = mock(IRepositoryNodeConfiguration.class);
        when(RepositoryNodeConfigurationManager.getConfiguration((Item) anyObject())).thenReturn(rncMock);

        IRepositoryNodeResourceProvider resourceProviderM = mock(IRepositoryNodeResourceProvider.class);
        when(rncMock.getResourceProvider()).thenReturn(resourceProviderM);

        when(resourceProviderM.needSaveReferenceFile()).thenReturn(true);
        when(repositoryFactory.getRepositoryContext()).thenReturn(contextMock);
        when(repositoryFactory.isEditableAndLockIfPossible((Item) anyObject())).thenReturn(true);
    }

    @Test
    public void doRunTest() throws Exception {
        PowerMockito.mockStatic(MDMWorbenchPlugin.class);
        when(MDMWorbenchPlugin.getImageDescriptor(anyString())).thenReturn(mock(ImageDescriptor.class));

        RemoveFromRepositoryAction removeAction = new RemoveFromRepositoryAction();
        RemoveFromRepositoryAction removeActionM = PowerMockito.spy(removeAction);
        CommonViewer commonViewerM = mock(CommonViewer.class);
        Whitebox.setInternalState(removeActionM, "commonViewer", commonViewerM); //$NON-NLS-1$

        PowerMockito.doReturn(true).when(removeActionM, "confirm", 2); //$NON-NLS-1$
        PowerMockito.doNothing().when(removeActionM, "warn"); //$NON-NLS-1$

        // mock a mdm repositoryViewObject
        IRepositoryViewObject objectRVO = mock(IRepositoryViewObject.class);
        MDMServerObjectItem mdmItemM = mock(MDMServerObjectItem.class);
        Property propertyM = mock(Property.class);

        when(objectRVO.getProperty()).thenReturn(propertyM);
        when(propertyM.getItem()).thenReturn(mdmItemM);

        // mock a FolderRepositoryObject
        FolderRepositoryObject folderRO = mock(FolderRepositoryObject.class);
        Property foldePropertyM = mock(Property.class);
        ContainerItem containerItem = mock(ContainerItem.class);
        when(folderRO.getProperty()).thenReturn(foldePropertyM);
        when(foldePropertyM.getItem()).thenReturn(containerItem);
        ItemState itemState = mock(ItemState.class);
        when(containerItem.getState()).thenReturn(itemState);
        when(itemState.getPath()).thenReturn(""); //$NON-NLS-1$

        PowerMockito.mockStatic(RepositoryNodeProviderRegistryReader.class);
        RepositoryNodeProviderRegistryReader reader = mock(RepositoryNodeProviderRegistryReader.class);
        PowerMockito.when(RepositoryNodeProviderRegistryReader.getInstance()).thenReturn(reader);

        ERepositoryObjectType typeM = ERepositoryObjectType.PROCESS;
        when(containerItem.getRepObjType()).thenReturn(typeM);

        MDMServerObject mdmServerObjectM = mock(MDMServerObject.class);
        when(mdmItemM.getMDMServerObject()).thenReturn(mdmServerObjectM);

        Shell shellM = mock(Shell.class);
        Control controlM = mock(Control.class);
        when(commonViewerM.getControl()).thenReturn(controlM);
        when(controlM.getShell()).thenReturn(shellM);

        List<Object> selectedObjects = new ArrayList<Object>();
        selectedObjects.add(objectRVO);
        selectedObjects.add(folderRO);

        when(removeActionM.getSelectedObject()).thenReturn(selectedObjects);
        Whitebox.setInternalState(removeActionM, "lockedObjs", Collections.EMPTY_LIST); //$NON-NLS-1$
        PowerMockito.mockStatic(MessageDialog.class);
        when(MessageDialog.openConfirm((Shell) anyObject(), anyString(), anyString())).thenReturn(true);

        PowerMockito.mockStatic(RepositoryResourceUtil.class);
        when(RepositoryResourceUtil.isOpenedInEditor((IRepositoryViewObject) anyObject())).thenReturn(null);
        when(RepositoryResourceUtil.hasContainerItem(anyObject(), eq(FolderType.FOLDER_LITERAL))).thenReturn(true);

        PowerMockito.mockStatic(ContainerCacheService.class);
        FolderItem folderItemM = mock(FolderItem.class);
        when(repositoryFactory.getFolderItem(eq(projectM), eq(typeM), (IPath) anyObject())).thenReturn(folderItemM);
        when(folderItemM.getState()).thenReturn(itemState);

        removeActionM.doRun();
    }
}
