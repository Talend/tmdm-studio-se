package org.talend.mdm.repository.core.service;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.command.CommandManager;
import org.talend.mdm.repository.core.command.ICommand;
import org.talend.mdm.repository.core.command.deploy.AbstractDeployCommand;
import org.talend.mdm.repository.core.service.ConsistencyService.ConsistencyCheckResult;
import org.talend.mdm.repository.core.service.IModelValidationService.IModelValidateResult;
import org.talend.mdm.repository.core.service.ModelImpactAnalyseService.ImpactOperation;
import org.talend.mdm.repository.model.mdmmetadata.MDMServerDef;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;
import org.talend.mdm.repository.utils.UIUtil;

import com.amalto.workbench.exadapter.ExAdapterManager;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ModelImpactAnalyseService.class, CommandManager.class, ReflectionProvider.class, ExAdapterManager.class,
        RepositoryResourceUtil.class, ConsistencyService.class, UIUtil.class, ModelImpactAnalyseService.class })
@PowerMockIgnore({ "org.talend.core.runtime.CoreRuntimePlugin", "org.eclipse.core.runtime.*", "org.eclipse.osgi.util.*" })
public class DeployServiceTest {

    private static Logger log = Logger.getLogger(DeployServiceTest.class);

    @Test
    public void testDeployAnotherVersion() {

        try {
            MDMServerDef mockServerDef = PowerMockito.mock(MDMServerDef.class);
            IRepositoryViewObject mockViewObj = PowerMockito.mock(IRepositoryViewObject.class);
            Property mockProperty = PowerMockito.mock(Property.class);
            PowerMockito.when(mockViewObj.getProperty()).thenReturn(mockProperty);
            List<IRepositoryViewObject> repositoryViewObjs = new ArrayList<IRepositoryViewObject>(Arrays.asList(mockViewObj));

            PowerMockito.mockStatic(CommandManager.class);
            CommandManager mockCommandManager = PowerMockito.mock(CommandManager.class);
            PowerMockito.when(CommandManager.getInstance()).thenReturn(mockCommandManager);
            List<AbstractDeployCommand> deployCommands = new ArrayList<AbstractDeployCommand>();
            deployCommands.add(PowerMockito.mock(AbstractDeployCommand.class));
            PowerMockito.when(mockCommandManager.getDeployCommandsWithoutHistory(anyListOf(IRepositoryViewObject.class)))
                    .thenReturn(deployCommands);

            PowerMockito.mockStatic(ModelImpactAnalyseService.class);
            DeployService mockDeployService = PowerMockito.mock(DeployService.class);
            PowerMockito.when(
                    mockDeployService.deployAnotherVersion(any(MDMServerDef.class), anyListOf(IRepositoryViewObject.class)))
                    .thenCallRealMethod();

            mockDeployService.deployAnotherVersion(mockServerDef, repositoryViewObjs);// //
            Mockito.verify(mockDeployService).runCommands(deployCommands, mockServerDef);// //
            PowerMockito.verifyStatic(null, null);
            ModelImpactAnalyseService.analyzeCommandImpact(any(MDMServerDef.class), anyListOf(AbstractDeployCommand.class));

            // ---------
            Map<IRepositoryViewObject, ImpactOperation> analyzeModelImpact = new HashMap<IRepositoryViewObject, ModelImpactAnalyseService.ImpactOperation>();
            ImpactOperation mockImpactOperation = PowerMockito.mock(ImpactOperation.class);
            analyzeModelImpact.put(mockViewObj, mockImpactOperation);
            PowerMockito.when(
                    ModelImpactAnalyseService.analyzeCommandImpact(any(MDMServerDef.class),
                            anyListOf(AbstractDeployCommand.class))).thenReturn(analyzeModelImpact);

            mockDeployService.deployAnotherVersion(mockServerDef, repositoryViewObjs);// //
            Mockito.verify(mockDeployService, times(2)).runCommands(deployCommands, mockServerDef);// //
            PowerMockito.verifyStatic(null, times(2));
            ModelImpactAnalyseService.analyzeCommandImpact(any(MDMServerDef.class), anyListOf(AbstractDeployCommand.class));

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Test
    public void testDeploy4params() {
        MDMServerDef mockServerDef = PowerMockito.mock(MDMServerDef.class);
        List<IRepositoryViewObject> viewObjs = new ArrayList<IRepositoryViewObject>();
        int defaultCmdType = ICommand.CMD_MODIFY;
        boolean removeLocked = true;
        int selectedButton = IModelValidationService.BUTTON_CANCEL;

        IModelValidateResult mockValidateResult = PowerMockito.mock(IModelValidateResult.class);
        DeployService mockDeployService = PowerMockito.mock(DeployService.class);
        PowerMockito.when(
                mockDeployService.deploy(any(MDMServerDef.class), anyListOf(IRepositoryViewObject.class), eq(defaultCmdType),
                        eq(removeLocked))).thenCallRealMethod();
        IRepositoryViewObject mockViewObj = PowerMockito.mock(IRepositoryViewObject.class);
        viewObjs.add(mockViewObj);
        PowerMockito.mockStatic(RepositoryResourceUtil.class);
        PowerMockito.mockStatic(ConsistencyService.class);

        try {
            // check selection of cancel for validating model
            PowerMockito.when(mockValidateResult.getSelectedButton()).thenReturn(selectedButton);
            PowerMockito.when(mockDeployService.validateModel(anyListOf(IRepositoryViewObject.class))).thenReturn(
                    mockValidateResult);

            IStatus deployStatus = mockDeployService.deploy(mockServerDef, viewObjs, defaultCmdType, removeLocked);
            assertEquals(Status.CANCEL_STATUS, deployStatus);
            verify(mockDeployService).validateModel(viewObjs);

            // check selection of ok for validating model
            selectedButton = IModelValidationService.BUTTON_OK;
            List<IRepositoryViewObject> invalidViewObjs = new ArrayList<IRepositoryViewObject>();
            IRepositoryViewObject mockInvalidViewObj = PowerMockito.mock(IRepositoryViewObject.class);
            invalidViewObjs.add(mockInvalidViewObj);

            PowerMockito.when(mockValidateResult.getSelectedButton()).thenReturn(selectedButton);
            PowerMockito.when(mockValidateResult.getValidObjects(eq(selectedButton))).thenReturn(viewObjs);
            PowerMockito.when(mockValidateResult.getInvalidObjects(eq(selectedButton))).thenReturn(invalidViewObjs);
            PowerMockito.when(mockDeployService.validateModel(anyListOf(IRepositoryViewObject.class))).thenReturn(
                    mockValidateResult);
            // // check consistency null and cancel
            PowerMockito.mockStatic(ConsistencyService.class);
            ConsistencyService mockConsistencyService = PowerMockito.mock(ConsistencyService.class);
            PowerMockito.when(ConsistencyService.getInstance()).thenReturn(mockConsistencyService);
            ConsistencyCheckResult mockConsistencyCheckResult = null;//

            PowerMockito.when(
                    mockConsistencyService.checkConsistency(any(MDMServerDef.class), anyListOf(IRepositoryViewObject.class)))
                    .thenReturn(mockConsistencyCheckResult);
            deployStatus = mockDeployService.deploy(mockServerDef, viewObjs, defaultCmdType, removeLocked);
            assertEquals(Status.CANCEL_STATUS, deployStatus);

            mockConsistencyCheckResult = PowerMockito.mock(ConsistencyCheckResult.class);//
            PowerMockito.when(mockConsistencyCheckResult.isCanceled()).thenReturn(true);
            PowerMockito.when(
                    mockConsistencyService.checkConsistency(any(MDMServerDef.class), anyListOf(IRepositoryViewObject.class)))
                    .thenReturn(mockConsistencyCheckResult);
            deployStatus = mockDeployService.deploy(mockServerDef, viewObjs, defaultCmdType, removeLocked);
            assertEquals(Status.CANCEL_STATUS, deployStatus);

            // // check without impact dialog
            PowerMockito.when(mockConsistencyCheckResult.isCanceled()).thenReturn(false);
            PowerMockito.when(mockConsistencyCheckResult.getToDeployObjects()).thenReturn(viewObjs);
            List<AbstractDeployCommand> deployCommands = new ArrayList<AbstractDeployCommand>();
            deployCommands.add(PowerMockito.mock(AbstractDeployCommand.class));
            PowerMockito.when(mockDeployService.runCommands(anyListOf(AbstractDeployCommand.class), any(MDMServerDef.class))).thenReturn(Status.OK_STATUS);
            
            PowerMockito.mockStatic(CommandManager.class);
            CommandManager mockCommandManager = PowerMockito.mock(CommandManager.class);
            PowerMockito.when(CommandManager.getInstance()).thenReturn(mockCommandManager);
            PowerMockito.when(mockCommandManager.getDeployCommands(anyListOf(IRepositoryViewObject.class), anyInt())).thenReturn(deployCommands);
            
            PowerMockito.mockStatic(UIUtil.class);
            PowerMockito.when(UIUtil.isWorkInUI()).thenReturn(false);
            deployStatus = mockDeployService.deploy(mockServerDef, viewObjs, defaultCmdType, removeLocked);
            assertEquals(Status.OK_STATUS, deployStatus);
            verify(mockDeployService, atLeastOnce()).runCommands(deployCommands, mockServerDef);
            verify(mockDeployService, atLeastOnce()).updateServerConsistencyStatus(mockServerDef, Status.OK_STATUS);
            verify(mockDeployService, atLeastOnce()).generateValidationFailedDeployStatus(Status.OK_STATUS, invalidViewObjs);

            // // check with normal executed impact dialog
            PowerMockito.when(UIUtil.isWorkInUI()).thenReturn(true);
            PowerMockito.mockStatic(ModelImpactAnalyseService.class);
            deployStatus = mockDeployService.deploy(mockServerDef, viewObjs, defaultCmdType, removeLocked);
            assertEquals(Status.OK_STATUS, deployStatus);
            PowerMockito.verifyStatic(null, null);
            ModelImpactAnalyseService.analyzeCommandImpact(mockServerDef, deployCommands);

            // // check with interrupted impact dialog
            PowerMockito.when(
                    ModelImpactAnalyseService.analyzeCommandImpact(any(MDMServerDef.class),
                            anyListOf(AbstractDeployCommand.class))).thenThrow(new InterruptedException());
            deployStatus = mockDeployService.deploy(mockServerDef, viewObjs, defaultCmdType, removeLocked);
            assertEquals(Status.CANCEL_STATUS, deployStatus);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
