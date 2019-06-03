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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.support.membermodification.MemberMatcher.method;
import static org.powermock.api.support.membermodification.MemberModifier.stub;

import java.util.ResourceBundle;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.junit.Before;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.talend.core.context.RepositoryContext;
import org.talend.core.model.general.Project;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.User;
import org.talend.core.repository.model.IRepositoryFactory;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.repository.utils.XmiResourceManager;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.mdm.repository.core.IRepositoryNodeConfiguration;
import org.talend.mdm.repository.core.IRepositoryNodeResourceProvider;
import org.talend.mdm.repository.core.impl.recyclebin.RecycleBinNodeConfiguration;
import org.talend.mdm.repository.extension.RepositoryNodeConfigurationManager;
import org.talend.mdm.repository.utils.IRepositoryResourceUtilExAdapter;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;
import org.talend.repository.ProjectManager;
import org.talend.repository.model.IProxyRepositoryFactory;

import com.amalto.workbench.exadapter.ExAdapterManager;

/**
 * DOC hbhong class global comment. Detailled comment
 */
@PrepareForTest({ AbstractSimpleAddAction.class, ResourceBundle.class, JFaceResources.class, ImageRegistry.class,
        CoreRuntimePlugin.class, IProxyRepositoryFactory.class, RepositoryContext.class, ProjectManager.class,
        IRepositoryResourceUtilExAdapter.class, RecycleBinNodeConfiguration.class, ProxyRepositoryFactory.class,
        RepositoryNodeConfigurationManager.class, IRepositoryNodeResourceProvider.class })
public class AbstractSimpleAddActionTest {

    @Before
    public void setUp() throws Exception {
        ResourceBundle rb = mock(ResourceBundle.class);
        stub(method(ResourceBundle.class, "getBundle", String.class)).toReturn(rb); //$NON-NLS-1$

        PowerMockito.mockStatic(JFaceResources.class);
        ImageRegistry registry = mock(ImageRegistry.class);
        when(JFaceResources.getImageRegistry()).thenReturn(registry);

        PowerMockito.mockStatic(CoreRuntimePlugin.class);
        CoreRuntimePlugin coreRuntimePlugin = mock(CoreRuntimePlugin.class);
        when(CoreRuntimePlugin.getInstance()).thenReturn(coreRuntimePlugin);
        IProxyRepositoryFactory repositoryFactory = mock(IProxyRepositoryFactory.class);
        when(CoreRuntimePlugin.getInstance().getProxyRepositoryFactory()).thenReturn(repositoryFactory);

        RepositoryContext ontextMock = mock(RepositoryContext.class);
        when(repositoryFactory.getRepositoryContext()).thenReturn(ontextMock);

        PowerMockito.mockStatic(ProjectManager.class);
        ProjectManager pmMock = mock(ProjectManager.class);
        Project pMock = mock(Project.class);
        User userMock = mock(User.class);
        when(userMock.getLogin()).thenReturn("a@b.cn"); //$NON-NLS-1$
        when(pMock.getAuthor()).thenReturn(userMock);
        when(pmMock.getCurrentProject()).thenReturn(pMock);
        when(ProjectManager.getInstance()).thenReturn(pmMock);
        when(ontextMock.getUser()).thenReturn(userMock);

        IRepositoryResourceUtilExAdapter mockAdapter = PowerMockito.mock(IRepositoryResourceUtilExAdapter.class);
        PowerMockito.mockStatic(ExAdapterManager.class);
        PowerMockito.when(ExAdapterManager.getAdapter(new RepositoryResourceUtil(), IRepositoryResourceUtilExAdapter.class))
                .thenReturn(mockAdapter);

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

        IRepositoryNodeResourceProvider resourceProviderM = mock(IRepositoryNodeResourceProvider.class);
        when(rncMock.getResourceProvider()).thenReturn(resourceProviderM);

        when(resourceProviderM.needSaveReferenceFile()).thenReturn(true);
        when(repositoryFactory.isEditableAndLockIfPossible(any(Item.class))).thenReturn(true);
    }
}
