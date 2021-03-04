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
package org.talend.mdm.repository.ui.actions.job;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.ui.IEditorPart;
import org.talend.commons.ui.runtime.image.ECoreImage;
import org.talend.commons.ui.runtime.image.ImageProvider;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.model.properties.ItemState;
import org.talend.core.model.properties.PropertiesFactory;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.AbstractRepositoryAction;
import org.talend.mdm.repository.core.IServerObjectRepositoryType;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.model.mdmproperties.MdmpropertiesFactory;
import org.talend.mdm.repository.model.mdmproperties.WSRoutingRuleItem;
import org.talend.mdm.repository.model.mdmserverobject.MdmserverobjectFactory;
import org.talend.mdm.repository.model.mdmserverobject.WSRoutingRuleE;
import org.talend.mdm.repository.model.mdmserverobject.WSRoutingRuleExpressionE;
import org.talend.mdm.repository.model.mdmserverobject.WSRoutingRuleOperatorE;
import org.talend.mdm.repository.ui.dialogs.job.JobOptionsDialog;
import org.talend.mdm.repository.ui.dialogs.job.JobOptionsDialog.Execution;
import org.talend.mdm.repository.ui.dialogs.job.JobOptionsDialog.Parameter;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;
import org.talend.mdm.repository.utils.UIUtil;

import com.amalto.workbench.service.IValidateService;

/**
 * DOC jsxie class global comment. Detailled comment
 */
public class GenerateJobTriggerAction extends AbstractRepositoryAction {

    private static final String PREFIX = "CallJob_"; //$NON-NLS-1$

    protected Object selectObj;

    static Logger log = Logger.getLogger(GenerateJobTriggerAction.class);

    public GenerateJobTriggerAction() {
        super(Messages.GenerateJobXX_GenTJobTrigger);
        setImageDescriptor(ImageProvider.getImageDesc(ECoreImage.PROCESS_ICON));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.talend.mdm.repository.core.AbstractRepositoryAction#getGroupName()
     */
    @Override
    public String getGroupName() {
        return GROUP_EDIT;
    }

    @Override
    protected void doRun() {
        selectObj = getSelectedObject().get(0);

        JobOptionsDialog dialog = new JobOptionsDialog(getShell(), Messages.JobProcesssDialogTiggerTitle_title,
                Execution.EMBEDDED);
        dialog.setBlockOnOpen(true);
        int ret = dialog.open();
        if (ret == Dialog.CANCEL) {
            return;
        }

        String jobName = ""; //$NON-NLS-1$
        String jobVersion = ""; //$NON-NLS-1$

        if (selectObj instanceof IRepositoryViewObject) {
            jobName = ((IRepositoryViewObject) selectObj).getProperty().getLabel();
            jobVersion = ((IRepositoryViewObject) selectObj).getProperty().getVersion();

        }
        // check exist
        IValidateService validateService = (IValidateService) GlobalServiceRegister.getDefault().getService(
                IValidateService.class);
        if (validateService != null) {
            boolean result = validateService.validateAndAlertObjectExistence(IServerObjectRepositoryType.TYPE_ROUTINGRULE,
                    getNewTriggerName(jobName), null);
            if (!result) {
                return;
            }
        }
        //
        WSRoutingRuleE routingRule = createTrigger(jobName, jobVersion, dialog);
        // if the new objectect is opened ,than close it before regenerating
        IRepositoryViewObject toDelete = RepositoryResourceUtil.findViewObjectByName(
                IServerObjectRepositoryType.TYPE_ROUTINGRULE, PREFIX + jobName);
        if (toDelete != null) {
            IEditorPart openedEditor = UIUtil.findOpenedEditor(toDelete);
            if (openedEditor != null) {
                UIUtil.closeEditor(openedEditor, false);
            }
            // delete directly
            RepositoryResourceUtil.removeViewObjectPhysically(toDelete, jobVersion);
        }
        AttachToTriggerView(jobName, routingRule);

    }

    /**
     * DOC jsxie Comment method "createTrigger".
     *
     * @param dialog
     * @param jobVersion2
     */
    private WSRoutingRuleE createTrigger(String jobName, String jobVersion, JobOptionsDialog dialog) {
        WSRoutingRuleE routingRule = MdmserverobjectFactory.eINSTANCE.createWSRoutingRuleE();

        try {
            String server = "http://localhost:8180"; //$NON-NLS-1$

            Execution execution = dialog.getExecution();
            String url = ""; //$NON-NLS-1$
            switch (execution) {
            case EMBEDDED:
                url = "ltj://" + jobName + "/" + jobVersion; //$NON-NLS-1$ //$NON-NLS-2$
                break;
            case WEB_SERVICE:
                url = Messages.bind(Messages.GenerateJobXX_UrlString, server, jobName, jobVersion, jobName);
                break;
            }

            Parameter executionParameter = dialog.getParameter();
            String parameter = ""; //$NON-NLS-1$
            switch (executionParameter) {
            case CONTEXT_VARIABLE:
                parameter = "<configuration>\n" + "<url>" + url + "</url><contextParam>\n"//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        + "<name>xmlInput</name>\n" + "<value>{exchange_data}</value>\n" + "</contextParam>\n"//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        + "</configuration>\n";//$NON-NLS-1$
                break;
            case INTEGRATED:
                parameter = "<configuration>\n" + "<url>" + url + "</url>" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        + "</configuration>\n";//$NON-NLS-1$
                break;
            }

            // Generate the job call
            // create default CREATE operation express
            WSRoutingRuleExpressionE expression1 = MdmserverobjectFactory.eINSTANCE.createWSRoutingRuleExpressionE();

            expression1.setName("C1"); //$NON-NLS-1$
            expression1.setXpath("Update/OperationType"); //$NON-NLS-1$
            expression1.setWsOperator(newContainRoutingRuleOperator());
            expression1.setValue("CREATE"); //$NON-NLS-1$

            WSRoutingRuleExpressionE expression2 = MdmserverobjectFactory.eINSTANCE.createWSRoutingRuleExpressionE();
            expression2.setName("C2"); //$NON-NLS-1$
            expression2.setXpath("Update/OperationType"); //$NON-NLS-1$
            expression2.setWsOperator(newContainRoutingRuleOperator());
            expression2.setValue("UPDATE"); //$NON-NLS-1$

            WSRoutingRuleExpressionE expression3 = MdmserverobjectFactory.eINSTANCE.createWSRoutingRuleExpressionE();
            expression3.setName("C3"); //$NON-NLS-1$
            expression3.setXpath("Update/OperationType"); //$NON-NLS-1$
            expression3.setWsOperator(newContainRoutingRuleOperator());
            expression3.setValue("DELETE"); //$NON-NLS-1$

            routingRule.setName(getNewTriggerName(jobName));
            routingRule.setDescription("Trigger that calls the Talend Job: " + jobName); //$NON-NLS-1$
            routingRule.setSynchronous(false);
            routingRule.setConcept("Update"); //$NON-NLS-1$
            routingRule.setServiceJNDI("amalto/local/service/callJob"); //$NON-NLS-1$
            routingRule.setParameters(parameter);

            routingRule.setCondition(null);
            routingRule.setDeactive(false);

            routingRule.getWsRoutingRuleExpressions().add(expression1);
            routingRule.getWsRoutingRuleExpressions().add(expression2);
            routingRule.getWsRoutingRuleExpressions().add(expression3);

            routingRule.setCondition("C1 Or C2 Or C3"); //$NON-NLS-1$
            routingRule.setDeactive(false);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return routingRule;
    }

    private String getNewTriggerName(String name) {
        return "CallJob_" + name; //$NON-NLS-1$
    }

    private WSRoutingRuleOperatorE newContainRoutingRuleOperator() {
        WSRoutingRuleOperatorE operator = MdmserverobjectFactory.eINSTANCE.createWSRoutingRuleOperatorE();
        operator.setValue("CONTAINS");//$NON-NLS-1$
        return operator;
    }

    /**
     * DOC jsxie Comment method "AttachToTriggerView".
     *
     * @param filename
     * @param trigger
     */
    private void AttachToTriggerView(String filename, WSRoutingRuleE trigger) {

        WSRoutingRuleItem item = MdmpropertiesFactory.eINSTANCE.createWSRoutingRuleItem();
        ItemState itemState = PropertiesFactory.eINSTANCE.createItemState();
        item.setState(itemState);
        item.setWsRoutingRule(trigger);
        item.getState().setPath(""); //$NON-NLS-1$
        RepositoryResourceUtil.createItem(item, PREFIX + filename);
        getCommonViewer().refresh();
    }

    @Override
    public boolean isVisible(IRepositoryViewObject viewObj) {
        if (getSelectedObject().size() > 1) {
            return false;
        }

        return super.isVisible(viewObj);
    }
}
