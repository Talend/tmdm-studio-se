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
package org.talend.mdm.repository.ui.actions.serviceconfiguration;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.powermock.api.support.membermodification.MemberMatcher.method;
import static org.powermock.api.support.membermodification.MemberModifier.stub;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ItemState;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.mdm.repository.extension.RepositoryNodeConfigurationManager;
import org.talend.mdm.repository.model.mdmproperties.ContainerItem;
import org.talend.mdm.repository.model.mdmproperties.MdmpropertiesFactory;
import org.talend.mdm.repository.model.mdmproperties.WSServiceConfigurationItem;
import org.talend.mdm.repository.model.mdmproperties.impl.MdmpropertiesFactoryImpl;
import org.talend.mdm.repository.model.mdmproperties.impl.WSServiceConfigurationItemImpl;
import org.talend.mdm.repository.ui.actions.AbstractSimpleAddActionTest;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;
import org.talend.repository.ProjectManager;

import com.amalto.workbench.MDMWorbenchPlugin;
import com.amalto.workbench.editors.ServiceConfigrationMainPage;
import com.amalto.workbench.exadapter.ExAdapterManager;
import com.amalto.workbench.image.ImageCache;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ImageDescriptor.class, JFaceResources.class, ImageCache.class, ItemState.class, CoreRuntimePlugin.class,
        ProjectManager.class, RepositoryNodeConfigurationManager.class, ProxyRepositoryFactory.class,
        RepositoryResourceUtil.class, ExAdapterManager.class, MdmpropertiesFactoryImpl.class,
        MDMWorbenchPlugin.class })
@PowerMockIgnore({ "org.xml.sax.*", "org.xml.sax.helpers.*" })
public class NewServiceConfigurationActionTest extends AbstractSimpleAddActionTest {

    @Test
    public void testCreateServerObject() throws Exception {
        stub(method(ServiceConfigrationMainPage.class, "checkValidXML")).toReturn(null); //$NON-NLS-1$

        stub(method(ServiceConfigrationMainPage.class, "formartXml", String.class)).toReturn(""); //$NON-NLS-1$ //$NON-NLS-2$
        //
        ContainerItem newItem = MdmpropertiesFactory.eINSTANCE.createContainerItem();
        ContainerItem mockContainerItem = spy(newItem);

        PowerMockito.mockStatic(MDMWorbenchPlugin.class);
        when(MDMWorbenchPlugin.getImageDescriptor(anyString())).thenReturn(mock(ImageDescriptor.class));

        NewServiceConfigurationAction action = new NewServiceConfigurationAction();
        NewServiceConfigurationAction spyAction = spy(action);

        Whitebox.setInternalState(spyAction, "parentItem", mockContainerItem); //$NON-NLS-1$
        ItemState itemState = mock(ItemState.class);
        when(mockContainerItem.getState()).thenReturn(itemState);
        when(mockContainerItem.getState().getPath()).thenReturn(""); //$NON-NLS-1$

        WSServiceConfigurationItem item = MdmpropertiesFactory.eINSTANCE.createWSServiceConfigurationItem();
        WSServiceConfigurationItem spyServiceConfigItem = spy(item);
        Resource mockResource = mock(Resource.class);
        ResourceSet mockResourceSet = mock(ResourceSet.class);
        when(mockResource.getResourceSet()).thenReturn(mockResourceSet);
        when(spyServiceConfigItem.eResource()).thenReturn(mockResource);
        PowerMockito.whenNew(WSServiceConfigurationItemImpl.class).withNoArguments()
                .thenReturn((WSServiceConfigurationItemImpl) spyServiceConfigItem);

        // run
        Item addedItem = spyAction.createServerObject("abc"); //$NON-NLS-1$
        assertThat(addedItem, notNullValue());

    }

}
