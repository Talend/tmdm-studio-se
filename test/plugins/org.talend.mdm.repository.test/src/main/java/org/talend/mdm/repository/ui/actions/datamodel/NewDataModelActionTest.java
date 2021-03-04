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
package org.talend.mdm.repository.ui.actions.datamodel;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
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
import org.talend.mdm.repository.model.mdmproperties.WSDataModelItem;
import org.talend.mdm.repository.ui.actions.AbstractSimpleAddAction;

public class NewDataModelActionTest {

    @Test
    public void testCreateServerObject() throws Exception {
        //
        ContainerItem newItem = MdmpropertiesFactory.eINSTANCE.createContainerItem();
        ContainerItem mockContainerItem = spy(newItem);
        ItemState itemState = mock(ItemState.class);
        when(mockContainerItem.getState()).thenReturn(itemState);
        when(mockContainerItem.getState().getPath()).thenReturn(""); //$NON-NLS-1$

        // new action
        NewDataModelAction mockAction = mock(NewDataModelAction.class);

        Field declaredField = AbstractSimpleAddAction.class.getDeclaredField("parentItem");
        declaredField.setAccessible(true);
        declaredField.set(mockAction, mockContainerItem);
        //

        Mockito.doCallRealMethod().when(mockAction).createServerObject(Mockito.anyString());
        // run
        Item addedItem = mockAction.createServerObject("abc"); //$NON-NLS-1$
        assertThat(addedItem, notNullValue());
        Mockito.verify(mockAction, times(1)).createERDocument(Mockito.any(WSDataModelItem.class));
        Mockito.verify(mockAction, times(1)).createMatchRuleMapInfo(Mockito.any(WSDataModelItem.class));
        Mockito.verify(mockAction, times(1)).createItemAndSave(Mockito.anyString(), Mockito.any(WSDataModelItem.class));
    }

}
