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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.support.membermodification.MemberMatcher.method;
import static org.powermock.api.support.membermodification.MemberModifier.stub;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
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
@PrepareForTest({ RenameObjectAction.class, ImageDescriptor.class, JFaceResources.class, DefaultMessagesImpl.class,
    ImageCache.class, ItemState.class, CoreRuntimePlugin.class, ProjectManager.class,
    RepositoryNodeConfigurationManager.class, IProxyRepositoryFactory.class, ProxyRepositoryFactory.class,
    MessageDialog.class, RepositoryResourceUtil.class, ContainerCacheService.class,
        RepositoryNodeProviderRegistryReader.class, RepositoryResourceUtil.class, ExAdapterManager.class, MDMWorbenchPlugin.class,
        CommonViewer.class, Shell.class })
public class RenameObjectActionTest {

    private IProxyRepositoryFactory repositoryFactory;

    private Project projectM;

    private IRepositoryNodeResourceProvider resourceProviderM;

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

        PowerMockito.mockStatic(MDMWorbenchPlugin.class);
        when(MDMWorbenchPlugin.getImageDescriptor(anyString())).thenReturn(mock(ImageDescriptor.class));

        PowerMockito.mockStatic(ImageCache.class);
        ImageDescriptor imgDesc = mock(ImageDescriptor.class);
        when(ImageCache.getImage(anyString())).thenReturn(imgDesc);
        //

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
        //

        repositoryFactory = mock(IProxyRepositoryFactory.class);
        when(CoreRuntimePlugin.getInstance().getProxyRepositoryFactory()).thenReturn(repositoryFactory);

        RecycleBinNodeConfiguration recycleBinNodeConfiguration = mock(RecycleBinNodeConfiguration.class);
        PowerMockito.whenNew(RecycleBinNodeConfiguration.class).withNoArguments().thenReturn(recycleBinNodeConfiguration);

        PowerMockito.mockStatic(ProxyRepositoryFactory.class);
        ProxyRepositoryFactory proxyRepositoryFactory = mock(ProxyRepositoryFactory.class);
        when(ProxyRepositoryFactory.getInstance()).thenReturn(proxyRepositoryFactory);
        IRepositoryFactory repositoryFactoryMock = mock(IRepositoryFactory.class);
        when(proxyRepositoryFactory.getRepositoryFactoryFromProvider()).thenReturn(repositoryFactoryMock);
        XmiResourceManager xmiResourceManager = mock(XmiResourceManager.class);
        when(repositoryFactoryMock.getResourceManager()).thenReturn(xmiResourceManager);

        PowerMockito.mockStatic(RepositoryNodeConfigurationManager.class);
        IRepositoryNodeConfiguration rncMock = mock(IRepositoryNodeConfiguration.class);
        when(RepositoryNodeConfigurationManager.getConfiguration(any(Item.class))).thenReturn(rncMock);

        resourceProviderM = mock(IRepositoryNodeResourceProvider.class);
        when(rncMock.getResourceProvider()).thenReturn(resourceProviderM);

        when(repositoryFactory.isEditableAndLockIfPossible(any(Item.class))).thenReturn(true);

    }

    @Test
    public void doRunTest() throws Exception {
        RenameObjectAction renameAction = new RenameObjectAction();
        RenameObjectAction renameActionM = PowerMockito.spy(renameAction);

        CommonViewer commonViewerM = mock(CommonViewer.class);
        Whitebox.setInternalState(renameActionM, "commonViewer", commonViewerM); //$NON-NLS-1$
        // mock a mdm repositoryViewObject
        IRepositoryViewObject objectRVO = mock(IRepositoryViewObject.class);
        IRepositoryViewObject parentRVO = mock(IRepositoryViewObject.class);
        MDMServerObjectItem mdmItemM = mock(MDMServerObjectItem.class);
        ContainerItem parentItemM = mock(ContainerItem.class);
        Property propertyM = mock(Property.class);
        Property parentPropertyM = mock(Property.class);
        String name = "mockName"; //$NON-NLS-1$
        MDMServerObject mdmServerObjectM = mock(MDMServerObject.class);
        when(mdmServerObjectM.getName()).thenReturn(name);
        ItemState itemStateM = mock(ItemState.class);

        //
        when(objectRVO.getProperty()).thenReturn(propertyM);
        when(propertyM.getItem()).thenReturn(mdmItemM);
        when(mdmItemM.getMDMServerObject()).thenReturn(mdmServerObjectM);
        when(mdmItemM.getState()).thenReturn(itemStateM);
        when(itemStateM.getPath()).thenReturn(""); //$NON-NLS-1$
        //
        when(parentRVO.getProperty()).thenReturn(parentPropertyM);
        when(parentPropertyM.getItem()).thenReturn(parentItemM);
        //

        PowerMockito.mockStatic(RepositoryNodeProviderRegistryReader.class);
        RepositoryNodeProviderRegistryReader reader = mock(RepositoryNodeProviderRegistryReader.class);
        PowerMockito.when(RepositoryNodeProviderRegistryReader.getInstance()).thenReturn(reader);

        ERepositoryObjectType typeM = ERepositoryObjectType.PROCESS;
        when(resourceProviderM.getRepositoryObjectType(mdmItemM)).thenReturn(typeM);

        PowerMockito.doReturn("NewName").when(renameActionM, "showRenameDlg", typeM, parentItemM, name); //$NON-NLS-1$ //$NON-NLS-2$
        //
        PowerMockito.mockStatic(ContainerCacheService.class);
        when(ContainerCacheService.get(eq(typeM), eq(""))).thenReturn(parentRVO); //$NON-NLS-1$
        List<Object> selectedObjects = new ArrayList<Object>();
        selectedObjects.add(objectRVO);
        //
        when(renameActionM.getSelectedObject()).thenReturn(selectedObjects);

        InputDialog inputDialogM = mock(InputDialog.class);
        PowerMockito.whenNew(InputDialog.class).withArguments(any(Shell.class), anyString(), anyString(), anyString(), any())
        .thenReturn(inputDialogM);
        when(inputDialogM.open()).thenReturn(IDialogConstants.OK_ID);
        when(inputDialogM.getValue()).thenReturn("NewName"); //$NON-NLS-1$

        renameActionM.doRun();
    }
}
