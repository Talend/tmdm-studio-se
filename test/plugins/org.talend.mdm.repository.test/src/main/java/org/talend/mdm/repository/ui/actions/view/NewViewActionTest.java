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
package org.talend.mdm.repository.ui.actions.view;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;

import org.eclipse.core.runtime.IPath;
import org.junit.Test;
import org.mockito.Mockito;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ItemState;
import org.talend.mdm.repository.core.impl.view.IViewNodeConstDef;
import org.talend.mdm.repository.model.mdmproperties.ContainerItem;
import org.talend.mdm.repository.model.mdmproperties.MdmpropertiesFactory;
import org.talend.mdm.repository.model.mdmproperties.WSViewItem;
import org.talend.mdm.repository.ui.actions.AbstractSimpleAddAction;

public class NewViewActionTest {

    @Test
    public void testCreateServerObject() throws Exception {
        String parentPath = "parentPath";
        //
        ContainerItem newItem = MdmpropertiesFactory.eINSTANCE.createContainerItem();
        ContainerItem mockContainerItem = spy(newItem);
        ItemState itemState = mock(ItemState.class);
        when(mockContainerItem.getState()).thenReturn(itemState);
        when(mockContainerItem.getState().getPath()).thenReturn("");

        NewViewAction mockAction = mock(NewViewAction.class);

        Mockito.doCallRealMethod().when(mockAction).createServerObject(Mockito.anyString());
        // run
        Item addedItem = mockAction.createServerObject("abc"); //$NON-NLS-1$
        assertThat(addedItem, notNullValue());
        assertSame(IPath.SEPARATOR + IViewNodeConstDef.PATH_SEARCHFILTER, addedItem.getState().getPath());
        Mockito.verify(mockAction, times(1)).createItemAndSave(Mockito.any(WSViewItem.class), Mockito.anyString());

        addedItem = mockAction.createServerObject(IViewNodeConstDef.PREFIX_VIEW_UPPER + "abc"); //$NON-NLS-1$
        assertThat(addedItem, notNullValue());
        assertSame(IPath.SEPARATOR + IViewNodeConstDef.PATH_WEBFILTER, addedItem.getState().getPath());
        Mockito.verify(mockAction, times(2)).createItemAndSave(Mockito.any(WSViewItem.class), Mockito.anyString());

        //
        Field declaredField = AbstractSimpleAddAction.class.getDeclaredField("parentItem");
        declaredField.setAccessible(true);
        declaredField.set(mockAction, mockContainerItem);

        addedItem = mockAction.createServerObject("abc"); //$NON-NLS-1$
        assertThat(addedItem, notNullValue());
        assertSame(IPath.SEPARATOR + IViewNodeConstDef.PATH_SEARCHFILTER, addedItem.getState().getPath());
        Mockito.verify(mockAction, times(3)).createItemAndSave(Mockito.any(WSViewItem.class), Mockito.anyString());

        addedItem = mockAction.createServerObject(IViewNodeConstDef.PREFIX_VIEW_UPPER + "abc"); //$NON-NLS-1$
        assertThat(addedItem, notNullValue());
        assertSame(IPath.SEPARATOR + IViewNodeConstDef.PATH_WEBFILTER, addedItem.getState().getPath());
        Mockito.verify(mockAction, times(4)).createItemAndSave(Mockito.any(WSViewItem.class), Mockito.anyString());

        when(mockContainerItem.getState().getPath()).thenReturn(parentPath);
        addedItem = mockAction.createServerObject("abc"); //$NON-NLS-1$
        assertSame(parentPath, addedItem.getState().getPath());
        Mockito.verify(mockAction, times(5)).createItemAndSave(Mockito.any(WSViewItem.class), Mockito.anyString());
    }

}
