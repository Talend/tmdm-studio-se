// ============================================================================
//
// Copyright (C) 2006-2021 Talend Inc. - www.talend.com
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

import org.eclipse.ui.navigator.CommonViewer;
import org.junit.Test;
import org.mockito.Mockito;
import org.talend.core.model.general.Project;
import org.talend.core.model.properties.FolderType;
import org.talend.core.model.properties.ItemState;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.AbstractRepositoryAction;
import org.talend.mdm.repository.model.mdmproperties.ContainerItem;
import org.talend.mdm.repository.model.mdmproperties.MDMServerObjectItem;
import org.talend.mdm.repository.model.mdmserverobject.MDMServerObject;
import org.talend.mdm.repository.models.FolderRepositoryObject;
import org.talend.repository.model.IProxyRepositoryFactory;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class RemoveFromRepositoryActionTest {

    @Test
    public void doRunTest() throws Exception {
        RemoveFromRepositoryAction removeAction = mock(RemoveFromRepositoryAction.class);
        Mockito.doCallRealMethod().when(removeAction).doRun();
        Mockito.when(removeAction.hasOpenedObject(Mockito.anyList())).thenReturn(false);
        Mockito.when(removeAction.confirm(Mockito.anyInt())).thenReturn(true);
        Field factoryField = RemoveFromRepositoryAction.class.getDeclaredField("factory");
        factoryField.setAccessible(true);
        IProxyRepositoryFactory mockFactory = mock(IProxyRepositoryFactory.class);
        factoryField.set(removeAction, mockFactory);

        Field lockedField = RemoveFromRepositoryAction.class.getDeclaredField("lockedObjs");
        lockedField.setAccessible(true);
        lockedField.set(removeAction, new ArrayList<>(1));

        Field removedField = RemoveFromRepositoryAction.class.getDeclaredField("removed");
        removedField.setAccessible(true);
        removedField.set(removeAction, new ArrayList<>(1));

        Field commonViewField = AbstractRepositoryAction.class.getDeclaredField("commonViewer");
        commonViewField.setAccessible(true);
        commonViewField.set(removeAction, Mockito.mock(CommonViewer.class));

        // mock a mdm repositoryViewObject
        IRepositoryViewObject objectRVO = mock(IRepositoryViewObject.class);
        MDMServerObjectItem mdmItemM = mock(MDMServerObjectItem.class);
        Property propertyM = mock(Property.class);

        when(objectRVO.getProperty()).thenReturn(propertyM);
        when(propertyM.getItem()).thenReturn(mdmItemM);
        when(objectRVO.getId()).thenReturn("id_serverObject");
        // mock a FolderRepositoryObject
        Property foldePropertyM = mock(Property.class);
        ContainerItem containerItem = mock(ContainerItem.class);
        FolderRepositoryObject folderRO = new FolderRepositoryObject(foldePropertyM);
        folderRO.setId("id_folderObject");
        when(foldePropertyM.getItem()).thenReturn(containerItem);
        ItemState itemState = mock(ItemState.class);
        when(containerItem.getState()).thenReturn(itemState);
        when(containerItem.getType()).thenReturn(FolderType.FOLDER_LITERAL);
        when(itemState.getPath()).thenReturn(""); //$NON-NLS-1$
        //

        ERepositoryObjectType typeM = ERepositoryObjectType.PROCESS;
        when(containerItem.getRepObjType()).thenReturn(typeM);

        MDMServerObject mdmServerObjectM = mock(MDMServerObject.class);
        when(mdmItemM.getMDMServerObject()).thenReturn(mdmServerObjectM);

        List<Object> selectedObjects = new ArrayList<Object>();
        selectedObjects.add(objectRVO);
        selectedObjects.add(folderRO);
        when(removeAction.getSelectedObject()).thenReturn(selectedObjects);

        removeAction.doRun();
        Mockito.verify(removeAction).hasOpenedObject(Mockito.anyList());
        Mockito.verify(removeAction).removeServerObject(Mockito.any(IRepositoryViewObject.class));
        Mockito.verify(removeAction).removeFolderObject(Mockito.any(FolderRepositoryObject.class));
        Mockito.verify(mockFactory).saveProject(Mockito.any(Project.class));
    }
}
