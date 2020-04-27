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
package org.talend.mdm.repository.ui.actions.datacontainer;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;

import org.junit.Test;
import org.mockito.Mockito;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ItemState;
import org.talend.mdm.repository.model.mdmproperties.ContainerItem;
import org.talend.mdm.repository.model.mdmproperties.MdmpropertiesFactory;
import org.talend.mdm.repository.model.mdmproperties.WSDataClusterItem;
import org.talend.mdm.repository.ui.actions.AbstractSimpleAddAction;

public class NewDataContainerActionTest {

    @Test
    public void testCreateServerObject() throws Exception {
        String pathItemPath = "parentPath";
        //
        ContainerItem newItem = MdmpropertiesFactory.eINSTANCE.createContainerItem();

        ContainerItem mockContainerItem = spy(newItem);
        ItemState itemState = mock(ItemState.class);
        when(mockContainerItem.getState()).thenReturn(itemState);
        when(mockContainerItem.getState().getPath()).thenReturn(pathItemPath); // $NON-NLS-1$

        NewDataContainerAction mockAction = mock(NewDataContainerAction.class);
        Field declaredField = AbstractSimpleAddAction.class.getDeclaredField("parentItem");
        declaredField.setAccessible(true);
        declaredField.set(mockAction, mockContainerItem);

        doCallRealMethod().when(mockAction).createServerObject(anyString());

        // run
        Item addedItem = mockAction.createServerObject("abc"); //$NON-NLS-1$
        assertThat(addedItem, notNullValue());
        assertTrue(pathItemPath.equals(addedItem.getState().getPath()));
        Mockito.verify(mockAction, times(1)).createItemAndSave(Mockito.any(WSDataClusterItem.class), anyString());
    }

}
