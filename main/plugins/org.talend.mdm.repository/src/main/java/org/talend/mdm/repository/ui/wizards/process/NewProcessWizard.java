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
package org.talend.mdm.repository.ui.wizards.process;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.ui.IWorkbenchPartSite;
import org.talend.mdm.repository.core.impl.transformerV2.ITransformerV2NodeConsDef;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.model.mdmserverobject.MdmserverobjectFactory;
import org.talend.mdm.repository.model.mdmserverobject.WSTransformerProcessStepE;
import org.talend.mdm.repository.model.mdmserverobject.WSTransformerV2E;
import org.talend.mdm.repository.utils.JobTemplateUtil;

import com.amalto.workbench.utils.Util;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class NewProcessWizard extends Wizard implements ITransformerV2NodeConsDef {

    // BEFORE
    public static final int BEFORE_TYPE = 100;

    // RUNNABLE
    public static final int RUNNABLE_TYPE = 101;

    private SelectProcessTypePage selectProcessTypePage;

    private InputProcessNamePage inputProcessNamePage;

    private final IWorkbenchPartSite site;

    private ConfigRedirectURLPage configRedirectURLPage;

    private ConfigReturnMessagePage configReturnMessagePage;

    private WSTransformerV2E transformer;

    //
    private Boolean toCreateJob;

    private String[] returnMessages;

    private String redirectUrl;

    private String processName;

    private boolean enableRedirect;

    private List<IMDMJobTemplate> jobTemplates;

    private int type;// use it to decide which type of process can be create,if 0,all type is permitted

    public WSTransformerV2E getNewProcess() {
        return this.transformer;
    }

    public NewProcessWizard(IWorkbenchPartSite site, int type) {
        this.site = site;
        this.type = type;
        setWindowTitle(Messages.NewProcessWizard_title);
    }

    @Override
    public void addPages() {
        selectProcessTypePage = new SelectProcessTypePage(type);
        inputProcessNamePage = new InputProcessNamePage(site, type);
        configRedirectURLPage = new ConfigRedirectURLPage();
        configReturnMessagePage = new ConfigReturnMessagePage();
        // add
        addPage(selectProcessTypePage);
        addPage(inputProcessNamePage);
        addPage(configRedirectURLPage);
        addPage(configReturnMessagePage);

        // add job template generate page
        jobTemplates = JobTemplateUtil.getJobTemplateGenPages();
        for (IMDMJobTemplate jobPage : jobTemplates) {
            if (jobPage instanceof WizardPage) {
                addPage((WizardPage) jobPage);
            }
        }
    }

    @Override
    public boolean performFinish() {
        String processName = inputProcessNamePage.getProcessName();
        int processType = inputProcessNamePage.getProcessType();
        String processdesc = inputProcessNamePage.getProcessDesc();
        transformer = MdmserverobjectFactory.eINSTANCE.createWSTransformerV2E();
        transformer.setName(processName);
        if (processdesc == null || processdesc.length() == 0) {
            processdesc = Messages.bind(Messages.NewProcessWizard_TemplateProcess, processName);
        }
        transformer.setDescription(processdesc);

        List<WSTransformerProcessStepE> steps = new ArrayList<WSTransformerProcessStepE>();
        createProcessStep(steps, processType);
        transformer.getProcessSteps().addAll(steps);

        saveParams();
        return true;
    }

    public Boolean isCreateJob() {
        if (toCreateJob == null) {
            toCreateJob = new Boolean(false);
            if (jobTemplates != null && jobTemplates.size() > 0) {
                IMDMJobTemplate jobtemplate = jobTemplates.get(0);
                toCreateJob = jobtemplate.createJob();
            }
        }

        return toCreateJob;
    }

    @Override
    public boolean canFinish() {
        IWizardPage currentPage = getContainer().getCurrentPage();
        if (currentPage.isPageComplete() && currentPage.getNextPage() == null) {
            return true;
        }
        return false;
    }

    /**
     * DOC hbhong Comment method "createProcessStep".
     *
     * @param steps
     * @param processType
     */
    private void createProcessStep(List<WSTransformerProcessStepE> steps, int processType) {
        String processName = inputProcessNamePage.getProcessName();
        boolean createJob = isCreateJob();

        switch (processType) {
        case TYPE_BEFOREDEL:
        case TYPE_BEFORESAVE:
            String[] messageParams = configReturnMessagePage.getMessageParams();
            WSTransformerProcessStepE messageStep = ProcessStepFactory.createProcessStep(ProcessStepFactory.STEP_RETURN_MESSAGE,
                    messageParams, processName, createJob);
            steps.add(messageStep);
            break;
        case TYPE_ENTITYACTION:
            WSTransformerProcessStepE updateStep = ProcessStepFactory.createProcessStep(ProcessStepFactory.STEP_UPDATE_REPORT,
                    null, processName, createJob);
            WSTransformerProcessStepE escapeStep = ProcessStepFactory.createProcessStep(ProcessStepFactory.STEP_ESCAPE, null,
                    processName, createJob);
            steps.add(updateStep);
            steps.add(escapeStep);

            String url = configRedirectURLPage.getUrl();
            // job name can't contains #,$ etc
            processName = processName.replaceAll("#|\\$", ""); //$NON-NLS-1$ //$NON-NLS-2$
            WSTransformerProcessStepE callJobStep = ProcessStepFactory.createProcessStep(ProcessStepFactory.STEP_REDIRECT, url,
                    processName, createJob);
            steps.add(callJobStep);

            break;
        case TYPE_WELCOMEACTION:
            // job name can't contains #,$ etc
            processName = processName.replaceAll("#|\\$", ""); //$NON-NLS-1$ //$NON-NLS-2$
            boolean isEnterprise = Util.IsEnterPrise();
            url = configRedirectURLPage.getUrl();
            if (isEnterprise && createJob) {
                if (url != null && url.length() > 0) {
                    callJobStep = ProcessStepFactory.createCallJobStep(processName, ProcessStepFactory.VAR_OUTPUT_URL,
                            ProcessStepFactory.VAR_DEFAULT);
                } else {
                    callJobStep = ProcessStepFactory.createCallJobStep(processName, ProcessStepFactory.VAR_OUTPUT,
                            ProcessStepFactory.VAR_DEFAULT);
                }
            } else {
                if (url != null && url.length() > 0) {
                    callJobStep = ProcessStepFactory.createRedirectStep(url, ProcessStepFactory.VAR_OUTPUT_URL);
                } else {
                    callJobStep = ProcessStepFactory.createRedirectStep(url, ProcessStepFactory.VAR_OUTPUT);
                }
            }
            steps.add(callJobStep);

            break;
        case TYPE_OTHER:
            if (createJob) {
                // job name can't contains #,$ etc
                processName = processName.replaceAll("#|\\$", ""); //$NON-NLS-1$ //$NON-NLS-2$
                callJobStep = ProcessStepFactory
                        .createProcessStep(ProcessStepFactory.STEP_REDIRECT, null, processName, createJob);
                steps.add(callJobStep);
            }
            break;
        case TYPE_SMARTVIEW:
            WSTransformerProcessStepE xsltStep = ProcessStepFactory.createSmartViewStep();
            steps.add(xsltStep);
            break;
        default:
            break;
        }
    }

    private void saveParams() {
        type = inputProcessNamePage.getProcessType();// here used for create job
        returnMessages = configReturnMessagePage.getMessageParams();
        enableRedirect = configRedirectURLPage.isEnableRedirect();
        redirectUrl = configRedirectURLPage.getUrl();
        processName = inputProcessNamePage.getProcessName();
    }

    public int getType() {
        return this.type;
    }

    public String[] getReturnMessages() {
        return this.returnMessages;
    }

    public String getRedirectUrl() {
        return this.redirectUrl;
    }

    public String getProcessName() {
        return this.processName;
    }

    public boolean isEnableRedirect() {
        return this.enableRedirect;
    }

    public List<IMDMJobTemplate> getJobTemplates() {
        return this.jobTemplates;
    }
}
