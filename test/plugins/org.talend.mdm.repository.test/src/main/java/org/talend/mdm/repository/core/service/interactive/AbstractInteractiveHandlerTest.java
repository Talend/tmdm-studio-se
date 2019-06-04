package org.talend.mdm.repository.core.service.interactive;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

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
        MDMServerObject mockServerObject = PowerMockito.mock(MDMServerObject.class);
        MDMServerObjectItem mockServerObjectItem = PowerMockito.mock(MDMServerObjectItem.class);
        PowerMockito.when(mockServerObjectItem.getMDMServerObject()).thenReturn(mockServerObject);
        Property mockProperty = PowerMockito.mock(Property.class);
        PowerMockito.when(mockProperty.getItem()).thenReturn(mockServerObjectItem);
        IRepositoryViewObject mockViewObj = PowerMockito.mock(IRepositoryViewObject.class);
        PowerMockito.when(mockViewObj.getProperty()).thenReturn(mockProperty);
        AbstractDeployCommand mockDeployCommand = PowerMockito.mock(AbstractDeployCommand.class);
        PowerMockito.when(mockDeployCommand.getViewObject()).thenReturn(mockViewObj);
        PowerMockito.when(mockDeployCommand.getServerDef()).thenReturn(PowerMockito.mock(MDMServerDef.class));

        AbstractInteractiveHandler mockInteractiveHandler = PowerMockito.mock(AbstractInteractiveHandler.class);
        try {
            PowerMockito.when(mockInteractiveHandler.deploy(any(AbstractDeployCommand.class))).thenCallRealMethod();
            PowerMockito.when(mockInteractiveHandler.doDeployWSObject(any(TMDMService.class), any())).thenReturn(true);
            TMDMService mockService = PowerMockito.mock(TMDMService.class);
            PowerMockito.when(mockInteractiveHandler, "getService", any(MDMServerDef.class)).thenReturn(mockService); //$NON-NLS-1$
            Object mockWsObj = new Object();
            PowerMockito.when(mockInteractiveHandler.convert(any(Item.class), any(MDMServerObject.class))).thenReturn(mockWsObj);
            boolean deployed = mockInteractiveHandler.deploy(mockDeployCommand);
            assertTrue(deployed);
            
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }

}
