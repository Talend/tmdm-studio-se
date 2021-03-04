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
package org.talend.mdm.engines.client.ui.actions;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonNavigator;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.ui.runtime.image.EImage;
import org.talend.commons.ui.runtime.image.ImageProvider;
import org.talend.core.CorePlugin;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.context.Context;
import org.talend.core.context.RepositoryContext;
import org.talend.core.language.ECodeLanguage;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.core.model.properties.SpagoBiServer;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.mdm.engines.client.ui.wizards.DeployOnMDMExportWizard;
import org.talend.mdm.repository.model.mdmmetadata.MDMServerDef;
import org.talend.mdm.repository.model.mdmmetadata.MdmmetadataFactory;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.IRepositoryNode.EProperties;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.ui.actions.AContextualAction;

import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.service.bridge.IRemoveCommandService;
import com.amalto.workbench.utils.MDMServerHelper;

/**
 * Action used to export job scripts. <br/>
 *
 * $Id: PublishOnSpagoAction.java 1 2007-04-26 11:25:00 cantoine
 *
 */
public final class DeployOnMDMAction extends AContextualAction {
    private static Logger log = Logger.getLogger(DeployOnMDMAction.class);

    private static final String EXPORTJOBSCRIPTS = Messages.DeployOnMDMAction_DeployToMDM;
    private static final String PROP_LAST_SERVER_DEF = "lastServerDef"; //$NON-NLS-1$

    /*
     * (non-Javadoc)
     *
     * @see org.talend.repository.ui.actions.ITreeContextualAction#init(org.eclipse.jface.viewers.TreeViewer,
     * org.eclipse.jface.viewers.IStructuredSelection)
     */
    public void init(TreeViewer viewer, IStructuredSelection selection) {
        boolean canWork = true;
        ECodeLanguage language = ((RepositoryContext) CorePlugin.getContext().getProperty(Context.REPOSITORY_CONTEXT_KEY))
                .getProject().getLanguage();
        if (selection.isEmpty() || language == ECodeLanguage.PERL) {// only Java project can see the action
            setEnabled(false);
            return;
        }
        if (MDMServerHelper.getServers().size() == 0) {
            setEnabled(false);
            return;
        }
        List<RepositoryNode> nodes = selection.toList();
        for (RepositoryNode node : nodes) {
            if (node.getType() == ENodeType.REPOSITORY_ELEMENT
                    && node.getProperties(EProperties.CONTENT_TYPE) == ERepositoryObjectType.PROCESS) {
                continue;
            }
            canWork = false;
            break;
        }
        setEnabled(canWork);
    }

    @Override
    public boolean isVisible() {
        // return isEnabled() &&
        // Activator.getDefault().getPreferenceStore().getBoolean(MDMPreferenceInitializer.MDM_STATUS);
        return isEnabled();
    }

    public DeployOnMDMAction() {
        super();

        this.setText(EXPORTJOBSCRIPTS);
        this.setToolTipText(EXPORTJOBSCRIPTS);
        this.setImageDescriptor(ImageProvider.getImageDesc(EImage.EXPORT_ICON));
    }

    @Override
    protected void doRun() {
        List<IRepositoryViewObject> viewObjs = getSelectedViewObject();

        DeployOnMDMExportWizard publishWizard = new DeployOnMDMExportWizard();
        publishWizard.setWindowTitle(EXPORTJOBSCRIPTS);
        publishWizard.init(getWorkbench(), (IStructuredSelection) this.getSelection());

        Shell activeShell = Display.getCurrent().getActiveShell();
        WizardDialog dialog = new WizardDialog(activeShell, publishWizard);
        dialog.open();

        boolean deploySucceed = publishWizard.isDeploySucceed();

        if (deploySucceed) {
            IRemoveCommandService service = (IRemoveCommandService) GlobalServiceRegister.getDefault().getService(
                    IRemoveCommandService.class);

            SpagoBiServer spagoBiServer = publishWizard.getMdmServer();
            MDMServerDef mdmServer = getMdmServer(spagoBiServer);


            IProxyRepositoryFactory factory = CoreRuntimePlugin.getInstance().getProxyRepositoryFactory();
            try {
                for (IRepositoryViewObject viewObj : viewObjs) {
                    Item item = viewObj.getProperty().getItem();
                    Property property = item.getProperty();

                    if (property != null) {
                        boolean eDeliver = property.eDeliver();
                        property.eSetDeliver(false);
                        property.getAdditionalProperties().put(PROP_LAST_SERVER_DEF, mdmServer.getName());
                        factory.save(item);
                        property.eSetDeliver(eDeliver);
                    }
                    refreshMdmRepositoryViewTree();
                    service.removeDeployPhaseCommandOf(ERepositoryObjectType.PROCESS, item);
                }
            } catch (PersistenceException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    private List<IRepositoryViewObject> getSelectedViewObject() {
        IStructuredSelection sel = (IStructuredSelection) getSelection();
        List<IRepositoryViewObject> viewObjs = new ArrayList<IRepositoryViewObject>();
        for (Object obj : sel.toList()) {

            if (obj instanceof IRepositoryNode) {
                IRepositoryNode node = (IRepositoryNode) obj;
                viewObjs.add(node.getObject());
            }
        }

        return viewObjs;
    }

    private MDMServerDef getMdmServer(SpagoBiServer spagoBiServer) {
        MDMServerDef mdmServerDef = MdmmetadataFactory.eINSTANCE.createMDMServerDef();

        mdmServerDef.setName(spagoBiServer.getShortDescription());
        mdmServerDef.setHost(spagoBiServer.getHost());
        mdmServerDef.setPort(spagoBiServer.getPort());
        mdmServerDef.setUser(spagoBiServer.getLogin());
        mdmServerDef.setPasswd(spagoBiServer.getPassword());

        return mdmServerDef;
    }


    /**
     * If the MDM Repository View is showing,refresh the navigator tree in it.
     */
    private void refreshMdmRepositoryViewTree() {
        IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        IViewPart mdmRepositoryView = activePage.findView("org.talend.mdm.repository.ui.navigator.MDMRepositoryView");//$NON-NLS-1$
        if(mdmRepositoryView instanceof CommonNavigator) {
            CommonNavigator cNavigator = (CommonNavigator) mdmRepositoryView;
            cNavigator.getCommonViewer().refresh();
        }
    }
}
