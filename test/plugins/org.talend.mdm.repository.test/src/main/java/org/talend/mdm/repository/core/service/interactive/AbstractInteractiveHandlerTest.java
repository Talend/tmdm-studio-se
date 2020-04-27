package org.talend.mdm.repository.core.service.interactive;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.mockito.Mockito;
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
        MDMServerObject mockServerObject = Mockito.mock(MDMServerObject.class);
        MDMServerObjectItem mockServerObjectItem = Mockito.mock(MDMServerObjectItem.class);
        Mockito.when(mockServerObjectItem.getMDMServerObject()).thenReturn(mockServerObject);
        Property mockProperty = Mockito.mock(Property.class);
        Mockito.when(mockProperty.getItem()).thenReturn(mockServerObjectItem);
        IRepositoryViewObject mockViewObj = Mockito.mock(IRepositoryViewObject.class);
        Mockito.when(mockViewObj.getProperty()).thenReturn(mockProperty);
        AbstractDeployCommand mockDeployCommand = Mockito.mock(AbstractDeployCommand.class);
        Mockito.when(mockDeployCommand.getViewObject()).thenReturn(mockViewObj);
        Mockito.when(mockDeployCommand.getServerDef()).thenReturn(Mockito.mock(MDMServerDef.class));

        AbstractInteractiveHandler mockInteractiveHandler = Mockito.mock(AbstractInteractiveHandler.class);
        try {
            Mockito.when(mockInteractiveHandler.deploy(any(AbstractDeployCommand.class))).thenCallRealMethod();
            Mockito.when(mockInteractiveHandler.doDeployWSObject(any(TMDMService.class), any())).thenReturn(true);
            TMDMService mockService = Mockito.mock(TMDMService.class);
            Mockito.when(mockInteractiveHandler.getService(any(MDMServerDef.class))).thenReturn(mockService); // $NON-NLS-1$
            Object mockWsObj = new Object();
            Mockito.when(mockInteractiveHandler.convert(any(Item.class), any(MDMServerObject.class))).thenReturn(mockWsObj);
            boolean deployed = mockInteractiveHandler.deploy(mockDeployCommand);
            assertTrue(deployed);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
