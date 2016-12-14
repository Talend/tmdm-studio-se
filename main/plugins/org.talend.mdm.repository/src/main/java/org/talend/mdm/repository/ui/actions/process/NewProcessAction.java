// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.repository.ui.actions.process;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PlatformUI;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ItemState;
import org.talend.core.model.properties.ProcessItem;
import org.talend.core.model.properties.PropertiesFactory;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.designer.core.ui.MultiPageTalendEditor;
import org.talend.designer.core.ui.editor.ProcessEditorInput;
import org.talend.mdm.repository.core.impl.transformerV2.ITransformerV2NodeConsDef;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.model.mdmproperties.ContainerItem;
import org.talend.mdm.repository.model.mdmproperties.MdmpropertiesFactory;
import org.talend.mdm.repository.model.mdmproperties.WSTransformerV2Item;
import org.talend.mdm.repository.model.mdmserverobject.MdmserverobjectFactory;
import org.talend.mdm.repository.model.mdmserverobject.WSTransformerProcessStepE;
import org.talend.mdm.repository.model.mdmserverobject.WSTransformerV2E;
import org.talend.mdm.repository.model.mdmserverobject.WSTransformerVariablesMappingE;
import org.talend.mdm.repository.ui.actions.AbstractSimpleAddAction;
import org.talend.mdm.repository.ui.wizards.process.IMDMJobTemplate;
import org.talend.mdm.repository.ui.wizards.process.NewProcessWizard;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;
import org.talend.mdm.repository.utils.RepositoryTransformUtil;
import org.talend.repository.model.IProxyRepositoryFactory;

import com.amalto.workbench.service.IValidateService;

/**
 * DOC hbhong class global comment. Detailled comment <br/>
 * 
 */
public class NewProcessAction extends AbstractSimpleAddAction implements ITransformerV2NodeConsDef {

    private static Logger log = Logger.getLogger(NewProcessAction.class);
    /**
     * DOC AddProcess constructor comment.
     * 
     * @param text
     */
    public NewProcessAction() {
        super();
    }

    @Override
    protected String getDialogTitle() {
        return Messages.NewProcessAction_newProcess;
    }

    @Override
    protected void doRun() {
        parentItem = null;
        selectObj = getSelectedObject().get(0);

        int type = getType();

        if (selectObj instanceof IRepositoryViewObject) {
            Item pItem = ((IRepositoryViewObject) selectObj).getProperty().getItem();
            if (pItem instanceof ContainerItem) {
                parentItem = (ContainerItem) pItem;
            }
        }
        IWorkbenchPartSite site = commonViewer.getCommonNavigator().getSite();
        // wizard
        NewProcessWizard newProcessWizard = new NewProcessWizard(site, type);
        WizardDialog wizardDialog = new WizardDialog(getShell(), newProcessWizard);
        wizardDialog.setPageSize(500, 260);
        if (wizardDialog.open() == IDialogConstants.OK_ID) {
            WSTransformerV2E newProcess = newProcessWizard.getNewProcess();
            final Item item = createServerObject(newProcess);
            commonViewer.refresh(selectObj);
            commonViewer.expandToLevel(selectObj, 1);
            openEditor(item);

            if (newProcessWizard.isCreateJob()) {
                generateJobTemplate(newProcessWizard.getType(), newProcessWizard.getProcessName(),
                        newProcessWizard.getReturnMessages(), newProcessWizard.isEnableRedirect(),
                        newProcessWizard.getRedirectUrl(), newProcessWizard.getJobTemplates());
                refreshJobEditorTitle(item);
            }
        }
    }

    private void refreshJobEditorTitle(Item item) {
        String label = item.getProperty().getLabel();
        // job name can not contains '#' and '$', see job's create procedure
        label = label.replaceAll("#|\\$", ""); //$NON-NLS-1$ //$NON-NLS-2$

        IWorkbenchPage activePage = getActivePage();
        IEditorReference[] editorReferences = activePage.getEditorReferences();
        if (editorReferences != null) {
            for (IEditorReference editorPart : editorReferences) {
                IEditorInput editorInput = editorPart.getEditor(false).getEditorInput();
                if (editorInput instanceof ProcessEditorInput) {
                    ProcessEditorInput processInput = (ProcessEditorInput) editorInput;
                    ProcessItem jobItem = (ProcessItem) processInput.getItem();
                    MultiPageTalendEditor jobEditor = (MultiPageTalendEditor) editorPart.getEditor(false);
                    String jobLabel = jobItem.getProperty().getLabel();
                    if (jobLabel.equals(label)) {
                        jobEditor.refreshName();
                        break;
                    }
                }
            }
        }
    }

    private IWorkbenchPage getActivePage() {
        return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
    }

    private int getType() {
        int type = 0;

        IRepositoryViewObject repositoryViewObject = (IRepositoryViewObject) selectObj;

        ContainerItem containerItem = (ContainerItem) repositoryViewObject.getProperty().getItem();
        String path = containerItem.getState().getPath();
        if (path.isEmpty()) {
            type = TYPE_PROCESS;
        } else if (path.startsWith(IPath.SEPARATOR + ITransformerV2NodeConsDef.PATH_BEFORESAVE)) {
            type = TYPE_BEFORESAVE;
        } else if (path.startsWith(IPath.SEPARATOR + ITransformerV2NodeConsDef.PATH_BEFOREDEL)) {
            type = TYPE_BEFOREDEL;
        } else if (path.startsWith(IPath.SEPARATOR + ITransformerV2NodeConsDef.PATH_ENTITYACTION)) {
            type = TYPE_ENTITYACTION;
        } else if (path.startsWith(IPath.SEPARATOR + ITransformerV2NodeConsDef.PATH_WELCOMEACTION)) {
            type = TYPE_WELCOMEACTION;
        } else if (path.startsWith(IPath.SEPARATOR + ITransformerV2NodeConsDef.PATH_SMARTVIEW)) {
            type = TYPE_SMARTVIEW;
        } else if (path.startsWith(IPath.SEPARATOR + ITransformerV2NodeConsDef.PATH_OTHER)) {
            type = TYPE_OTHER;
        }

        return type;
    }

    private WSTransformerV2E newProcess(String key) {

        WSTransformerV2E transformer = MdmserverobjectFactory.eINSTANCE.createWSTransformerV2E();
        transformer.setName(key);
        transformer.setDescription(""); //$NON-NLS-1$

        if (key.toString().startsWith("Smart_view_")) {//$NON-NLS-1$
            final String parameters = "<xsl:stylesheet xmlns:xsl='http://www.w3.org/1999/XSL/Transform' version='1.0'>\n"//$NON-NLS-1$
                    + "   <xsl:output method='html' indent='yes' omit-xml-declaration='yes'/>\n"//$NON-NLS-1$
                    + "   <xsl:template match='/'>\n" + "       <html>\n"//$NON-NLS-1$//$NON-NLS-2$
                    + "          <head><title>Smart View</title></head>\n" + "          <body>\n"//$NON-NLS-1$//$NON-NLS-2$
                    + "            <h1>This is the default Smart View for: <xsl:value-of select='./text()'/></h1>\n"//$NON-NLS-1$
                    + "            <xsl:copy-of select='.'/>\n" + "            <!-- Customize the stylesheet -->\n"//$NON-NLS-1$//$NON-NLS-2$
                    + "          </body>\n" + "       </html>\n" + "    </xsl:template>" + "</xsl:stylesheet>";//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$

            final String TRANSFORMER_PLUGIN = "amalto/local/transformer/plugin/xslt";//$NON-NLS-1$

            List<WSTransformerVariablesMappingE> inItems = new ArrayList<WSTransformerVariablesMappingE>();
            WSTransformerVariablesMappingE inputLine = MdmserverobjectFactory.eINSTANCE.createWSTransformerVariablesMappingE();
            inputLine.setPipelineVariable("_DEFAULT_");//$NON-NLS-1$
            inputLine.setPluginVariable("xml");//$NON-NLS-1$
            inItems.add(inputLine);

            List<WSTransformerVariablesMappingE> outItems = new ArrayList<WSTransformerVariablesMappingE>();
            WSTransformerVariablesMappingE outputLine = MdmserverobjectFactory.eINSTANCE.createWSTransformerVariablesMappingE();
            outputLine.setPipelineVariable("html");//$NON-NLS-1$
            outputLine.setPluginVariable("text");//$NON-NLS-1$
            outItems.add(outputLine);

            ArrayList<WSTransformerProcessStepE> list = new ArrayList<WSTransformerProcessStepE>();
            WSTransformerProcessStepE step = MdmserverobjectFactory.eINSTANCE.createWSTransformerProcessStepE();
            step.setPluginJNDI(TRANSFORMER_PLUGIN);
            step.setDescription("Stylesheet"); //$NON-NLS-1$
            step.setParameters(parameters);
            step.getInputMappings().addAll(inItems);
            step.getOutputMappings().addAll(outItems);
            step.setDisabled(false);

            list.add(step);
            transformer.getProcessSteps().addAll(list);

        }

        return transformer;
    }

    @Override
    protected Item createServerObject(String key) {

        WSTransformerV2Item item = MdmpropertiesFactory.eINSTANCE.createWSTransformerV2Item();
        ItemState itemState = PropertiesFactory.eINSTANCE.createItemState();
        item.setState(itemState);
        //
        WSTransformerV2E process = newProcess(key);
        item.setWsTransformerV2(process);

        if (parentItem != null) {
            item.getState().setPath(parentItem.getState().getPath());
            RepositoryResourceUtil.createItem(item, key);
        }
        return item;
    }

    protected Item createServerObject(WSTransformerV2E process) {

        WSTransformerV2Item item = MdmpropertiesFactory.eINSTANCE.createWSTransformerV2Item();
        ItemState itemState = PropertiesFactory.eINSTANCE.createItemState();
        item.setState(itemState);
        //
        item.setWsTransformerV2(process);

        if (parentItem != null) {
            String path = rebuildItemPath(process.getName());

            item.getState().setPath(path);
            RepositoryResourceUtil.createItem(item, process.getName());
        }
        return item;
    }

    private String rebuildItemPath(String processName) {
        String path = parentItem.getState().getPath();
        if (path.isEmpty()) {
            path = RepositoryTransformUtil.getInstance().getProcessPath(processName, true);
        }
        return path;
    }

    public void generateJobTemplate(int type, String processName, String[] returnMessages, boolean enableRedirect,
            String redirectUrl, List<IMDMJobTemplate> jobTemplates) {
        if (type == ITransformerV2NodeConsDef.TYPE_SMARTVIEW) {// don't create job if smartview
            return;
        }

        if (returnMessages == null || returnMessages.length < 2) {
            throw new IllegalArgumentException();
        }

        String infoType = null;
        String pMessage = null;
        if (type == ITransformerV2NodeConsDef.TYPE_BEFOREDEL || type == ITransformerV2NodeConsDef.TYPE_BEFORESAVE) {
            infoType = returnMessages[0];
            pMessage = returnMessages[1];
        }
        if (type == ITransformerV2NodeConsDef.TYPE_ENTITYACTION || type == ITransformerV2NodeConsDef.TYPE_WELCOMEACTION) {
            if (enableRedirect) {
                pMessage = "<results><item><attr>" + redirectUrl + //$NON-NLS-1$
                        "</attr></item></results>"; //$NON-NLS-1$
            } else {
                pMessage = ""; //$NON-NLS-1$
            }
        }
        IValidateService validateService = (IValidateService) GlobalServiceRegister.getDefault().getService(
                IValidateService.class);
        for (IMDMJobTemplate job : jobTemplates) {
            boolean result = true;

            processName = processName.replaceAll("#|\\$", ""); //$NON-NLS-1$//$NON-NLS-2$
            if (validateService != null) {
                result = validateService.validateAndAlertObjectExistence(ERepositoryObjectType.PROCESS, processName, "Job"); //$NON-NLS-1$
            }
            if (result) {
                closeEditor(processName);
                job.generateJobTemplate(type, processName, infoType, pMessage);
            }
        }
    }

    private void closeEditor(String processName) {
        IWorkbenchPage activePage = getActivePage();
        IEditorReference[] editorRef = activePage.getEditorReferences();
        for (IEditorReference ref : editorRef) {
            IEditorInput editorInput = ref.getEditor(false).getEditorInput();
            if (editorInput instanceof ProcessEditorInput) {
                ProcessEditorInput processInput = (ProcessEditorInput) editorInput;
                ProcessItem jobItem = (ProcessItem) processInput.getItem();
                if (jobItem.getProperty().getLabel().equals(processName)) {
                    activePage.closeEditors(new IEditorReference[] { ref }, false);

                    IRepositoryViewObject jobViewObject = RepositoryResourceUtil.findViewObjectByName(
                            ERepositoryObjectType.PROCESS, processName);
                    if (jobViewObject != null) {
                        IProxyRepositoryFactory factory = CoreRuntimePlugin.getInstance().getProxyRepositoryFactory();
                        try {
                            factory.deleteObjectPhysical(jobViewObject);
                        } catch (PersistenceException e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                    break;
                }
            }
        }
    }
}
