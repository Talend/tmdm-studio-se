package org.talend.mdm.repository.core.service.interactive;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.command.deploy.AbstractDeployCommand;
import org.talend.mdm.repository.model.mdmmetadata.MDMServerDef;
import org.talend.mdm.repository.model.mdmproperties.MDMServerObjectItem;
import org.talend.mdm.repository.model.mdmserverobject.MDMServerObject;

import com.amalto.workbench.webservices.TMDMService;


public class AbstractInteractiveHandlerTest {

    private static Logger log = Logger.getLogger(AbstractInteractiveHandlerTest.class);

    @Test
    public void testDeploy() {
        MDMServerObject mockServerObject = mock(MDMServerObject.class);
        MDMServerObjectItem mockServerObjectItem = mock(MDMServerObjectItem.class);
        when(mockServerObjectItem.getMDMServerObject()).thenReturn(mockServerObject);
        Property mockProperty = mock(Property.class);
        when(mockProperty.getItem()).thenReturn(mockServerObjectItem);
        IRepositoryViewObject mockViewObj = mock(IRepositoryViewObject.class);
        when(mockViewObj.getProperty()).thenReturn(mockProperty);
        AbstractDeployCommand mockDeployCommand = mock(AbstractDeployCommand.class);
        when(mockDeployCommand.getViewObject()).thenReturn(mockViewObj);

        AbstractInteractiveHandler mockInteractiveHandler = mock(AbstractInteractiveHandler.class);
        try {
            when(mockInteractiveHandler.deploy(any(AbstractDeployCommand.class))).thenCallRealMethod();
            TMDMService mockService = mock(TMDMService.class);

            MDMServerDef mockServerDef = mock(MDMServerDef.class);
            when(mockDeployCommand.getServerDef()).thenReturn(mockServerDef);
            when(mockInteractiveHandler.getService(mockServerDef)).thenReturn(mockService); // $NON-NLS-1$
            Object mockWsObj = new Object();
            when(mockInteractiveHandler.doDeployWSObject(mockService, mockWsObj)).thenReturn(true);
            when(mockInteractiveHandler.convert(any(Item.class), any(MDMServerObject.class))).thenReturn(mockWsObj);
            boolean deployed = mockInteractiveHandler.deploy(mockDeployCommand);
            assertTrue(deployed);
            
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }

}
