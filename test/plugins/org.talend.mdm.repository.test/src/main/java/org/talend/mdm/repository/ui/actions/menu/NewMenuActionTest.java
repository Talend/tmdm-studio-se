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
package org.talend.mdm.repository.ui.actions.menu;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;

import org.junit.Test;
import org.mockito.Mockito;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ItemState;
import org.talend.mdm.repository.model.mdmproperties.ContainerItem;
import org.talend.mdm.repository.model.mdmproperties.MdmpropertiesFactory;
import org.talend.mdm.repository.model.mdmproperties.WSMenuItem;
import org.talend.mdm.repository.ui.actions.AbstractSimpleAddAction;

public class NewMenuActionTest {

    @Test
    public void testCreateServerObject() throws Exception {
        String parentPath = "parentpath";
        //
        ContainerItem newItem = MdmpropertiesFactory.eINSTANCE.createContainerItem();
        ContainerItem mockContainerItem = spy(newItem);
        ItemState itemState = mock(ItemState.class);
        when(mockContainerItem.getState()).thenReturn(itemState);
        when(mockContainerItem.getState().getPath()).thenReturn(parentPath);

        NewMenuAction mockAction = mock(NewMenuAction.class);
        Mockito.doCallRealMethod().when(mockAction).createServerObject(Mockito.anyString());
        Field declaredField = AbstractSimpleAddAction.class.getDeclaredField("parentItem");
        declaredField.setAccessible(true);
        declaredField.set(mockAction, mockContainerItem);
        //

        // run
        Item addedItem = mockAction.createServerObject("abc"); //$NON-NLS-1$
        assertThat(addedItem, notNullValue());
        assertTrue(parentPath.equals(addedItem.getState().getPath()));
        Mockito.verify(mockAction, Mockito.times(1)).createItemAndSave(Mockito.any(WSMenuItem.class), Mockito.anyString());
    }

}
