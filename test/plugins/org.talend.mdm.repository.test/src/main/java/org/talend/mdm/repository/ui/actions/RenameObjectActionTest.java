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
package org.talend.mdm.repository.ui.actions;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.EClassImpl;
import org.eclipse.ui.navigator.CommonViewer;
import org.junit.Test;
import org.mockito.Mockito;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ItemState;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.AbstractRepositoryAction;
import org.talend.mdm.repository.core.IRepositoryNodeConfiguration;
import org.talend.mdm.repository.core.IRepositoryNodeResourceProvider;
import org.talend.mdm.repository.core.service.ContainerCacheService;
import org.talend.mdm.repository.extension.RepositoryNodeConfigurationManager;
import org.talend.mdm.repository.model.mdmproperties.ContainerItem;
import org.talend.mdm.repository.model.mdmproperties.MDMServerObjectItem;
import org.talend.mdm.repository.model.mdmserverobject.MDMServerObject;
import org.talend.repository.model.IProxyRepositoryFactory;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class RenameObjectActionTest {

    @Test
    public void doRunTest() throws Exception {
        RenameObjectAction renameAction = mock(RenameObjectAction.class);
        Mockito.doCallRealMethod().when(renameAction).doRun();
        Field factoryField = RenameObjectAction.class.getDeclaredField("factory");
        factoryField.setAccessible(true);
        IProxyRepositoryFactory mockFactory = mock(IProxyRepositoryFactory.class);
        when(mockFactory.isEditableAndLockIfPossible(Mockito.any(Item.class))).thenReturn(true);
        factoryField.set(renameAction, mockFactory);

        Field commonViewField = AbstractRepositoryAction.class.getDeclaredField("commonViewer");
        commonViewField.setAccessible(true);
        commonViewField.set(renameAction, Mockito.mock(CommonViewer.class));

        //
        ERepositoryObjectType typeM = ERepositoryObjectType.PROCESS;

        // mock a mdm repositoryViewObject
        String name = "mockName"; //$NON-NLS-1$
        String parentItemPath = "pathPath";
        IRepositoryViewObject objectRVO = mock(IRepositoryViewObject.class);
        IRepositoryViewObject parentRVO = mock(IRepositoryViewObject.class);
        MDMServerObjectItem mdmItemM = mock(MDMServerObjectItem.class);
        ContainerItem parentItemM = mock(ContainerItem.class);
        when(parentItemM.getRepObjType()).thenReturn(typeM);
        Property propertyM = mock(Property.class);
        Property parentPropertyM = mock(Property.class);
        MDMServerObject mdmServerObjectM = mock(MDMServerObject.class);
        when(mdmServerObjectM.getName()).thenReturn(name);
        ItemState itemStateM = mock(ItemState.class);

        class ItemEClass extends EClassImpl {

            public ItemEClass() {
            }
        }

        ItemEClass itemEClass = new ItemEClass();
        //
        when(objectRVO.getProperty()).thenReturn(propertyM);
        when(propertyM.getItem()).thenReturn(mdmItemM);
        when(mdmItemM.getMDMServerObject()).thenReturn(mdmServerObjectM);
        when(mdmItemM.getState()).thenReturn(itemStateM);
        when(mdmItemM.eClass()).thenReturn(itemEClass);
        when(itemStateM.getPath()).thenReturn(parentItemPath);
        //
        when(parentRVO.getProperty()).thenReturn(parentPropertyM);
        when(parentPropertyM.getItem()).thenReturn(parentItemM);
        ItemState parentItemStateM = mock(ItemState.class);
        when(parentItemStateM.getPath()).thenReturn(parentItemPath);
        when(parentItemM.getState()).thenReturn(parentItemStateM);
        //
        List<Object> selectedObjects = new ArrayList<Object>();
        selectedObjects.add(objectRVO);
        when(renameAction.getSelectedObject()).thenReturn(selectedObjects);

        String newName = "NewName";
        Mockito.when(renameAction.showRenameDlg(typeM, parentItemM, name)).thenReturn(newName);

        IRepositoryNodeResourceProvider mockResourceProvider = mock(IRepositoryNodeResourceProvider.class);
        when(mockResourceProvider.getRepositoryObjectType(mdmItemM)).thenReturn(typeM);

        IRepositoryNodeConfiguration configuration = mock(IRepositoryNodeConfiguration.class);
        when(configuration.getResourceProvider()).thenReturn(mockResourceProvider);
        Field itemConfMapField = RepositoryNodeConfigurationManager.class.getDeclaredField("itemConfMap");
        itemConfMapField.setAccessible(true);
        Map<EClass, IRepositoryNodeConfiguration> itemConfMap = (Map<EClass, IRepositoryNodeConfiguration>) itemConfMapField
                .get(null);
        itemConfMap.put(itemEClass, configuration);

        ContainerCacheService.putContainer(parentRVO);

        renameAction.doRun();
        Mockito.verify(propertyM).setLabel(newName);
        Mockito.verify(mdmServerObjectM).setName(newName);
        Mockito.verify(mockFactory).isEditableAndLockIfPossible(Mockito.any(Item.class));
        Mockito.verify(mockFactory).save(Mockito.any(Item.class), Mockito.eq(false));
    }
}
