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
package org.talend.mdm.engines.client.ui.wizards;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.wizards.datatransfer.WizardFileSystemResourceExportPage1;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.commons.ui.runtime.exception.MessageBoxExceptionHandler;
import org.talend.commons.ui.swt.formtools.LabelledCombo;
import org.talend.core.CorePlugin;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.language.ECodeLanguage;
import org.talend.core.language.LanguageManager;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ProcessItem;
import org.talend.core.model.properties.PropertiesFactory;
import org.talend.core.model.properties.Property;
import org.talend.core.model.properties.SpagoBiServer;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.prefs.ITalendCorePrefConstants;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.core.ui.export.ArchiveFileExportOperationFullPath;
import org.talend.designer.core.model.utils.emf.talendfile.ProcessType;
import org.talend.designer.runprocess.IProcessor;
import org.talend.designer.runprocess.JobErrorsChecker;
import org.talend.designer.runprocess.ProcessorException;
import org.talend.designer.runprocess.ProcessorUtilities;
import org.talend.mdm.engines.client.i18n.Messages;
import org.talend.repository.documentation.ExportFileResource;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.IRepositoryNode.EProperties;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.ui.utils.ZipToFile;
import org.talend.repository.ui.wizards.exportjob.JavaJobExportReArchieveCreator;
import org.talend.repository.ui.wizards.exportjob.scriptsmanager.JobScriptsManager;
import org.talend.repository.ui.wizards.exportjob.scriptsmanager.JobScriptsManager.ExportChoice;
import org.talend.repository.ui.wizards.exportjob.util.ExportJobUtil;

import com.amalto.workbench.service.ILegendServerDefService;
import com.amalto.workbench.utils.HttpClientUtil;
import com.amalto.workbench.utils.MDMServerDef;
import com.amalto.workbench.utils.Util;

/**
 * Page of the Job Scripts Export Wizard. <br/>
 *
 * $Id: DeployOnMDMExportWizardPage.java 1 2007-04-26 11:29:00 cantoine
 *
 */
public abstract class DeployOnMDMExportWizardPage extends WizardFileSystemResourceExportPage1 {

    /**
     *
     */
    private static final String UNDERLINE = "_"; //$NON-NLS-1$

    /**
     *
     */
    private static final String SEPERATOR = "/"; //$NON-NLS-1$

    /**
     *
     */
    private static final String BLANK = ""; //$NON-NLS-1$

    private static final String EXT_ZIP = ".zip"; //$NON-NLS-1$

    private static final Log log = LogFactory.getLog(DeployOnMDMExportWizardPage.class);

    // widgets
    protected Button contextButton;

    protected ExportFileResource[] process;

    protected LabelledCombo serverSpagoBi;

    protected Combo contextCombo;

    protected JobScriptsManager manager;

    protected String jobPurposeDescription;

    private SpagoBiServer mdmServer = null;

    private RuntimeException deployException;

    private boolean isDeploySucceed = false;

    protected List<JobDeploymentInfo> jobInfoList = new ArrayList<JobDeploymentInfo>();

    protected Map<ExportFileResource, RepositoryNode> processMap = new HashMap<ExportFileResource, RepositoryNode>();

    protected String selectedServer;

    private IStructuredSelection selection;

    private boolean saveflag = true;

    /**
     * Create an instance of this class.
     *
     * @param name java.lang.String
     */
    protected DeployOnMDMExportWizardPage(String name, IStructuredSelection selection, SpagoBiServer mdmserver) {
        super(name, null);
        this.selection = selection;
        this.mdmServer = mdmserver;
        RepositoryNode[] nodes = (RepositoryNode[]) selection.toList().toArray(new RepositoryNode[selection.size()]);

        List<ExportFileResource> list = new ArrayList<ExportFileResource>();
        for (RepositoryNode node : nodes) {
            if (node.getType() == ENodeType.SYSTEM_FOLDER || node.getType() == ENodeType.SIMPLE_FOLDER) {
                addTreeNode(node, node.getProperties(EProperties.LABEL).toString(), list);
            }
            if (node.getType() == ENodeType.REPOSITORY_ELEMENT) {
                IRepositoryViewObject repositoryObject = node.getObject();
                if (repositoryObject.getProperty().getItem() instanceof ProcessItem) {
                    ProcessItem processItem = (ProcessItem) repositoryObject.getProperty().getItem();
                    ProcessType processType = processItem.getProcess();
                    ExportFileResource resource = new ExportFileResource(processItem, processItem.getProperty().getLabel());
                    if (mdmserver == null) {
                        resource.setNode(node);
                    }
                    jobPurposeDescription = processItem.getProperty().getPurpose();
                    list.add(resource);
                    processMap.put(resource, node);
                    JobDeploymentInfo jobInfo = new JobDeploymentInfo();
                    jobInfo.setJobLabelName(processItem.getProperty().getLabel());
                    jobInfo.setJobPath(processItem.getState().getPath());
                    jobInfo.setJobVersion(processItem.getProperty().getVersion());
                    jobInfo.setJobPurposeDescription(jobPurposeDescription);

                    jobInfoList.add(jobInfo);
                }
            }
        }

        process = list.toArray(new ExportFileResource[list.size()]);
    }

    private void addTreeNode(RepositoryNode node, String path, List<ExportFileResource> list) {
        if (node != null && node.getType() == ENodeType.REPOSITORY_ELEMENT) {
            IRepositoryViewObject repositoryObject = node.getObject();
            if (repositoryObject.getProperty().getItem() instanceof ProcessItem) {
                ProcessItem processItem = (ProcessItem) repositoryObject.getProperty().getItem();
                ExportFileResource resource = new ExportFileResource(processItem, path);
                resource.setNode(node);
                list.add(resource);
                processMap.put(resource, node);
            }
        }
        Object[] nodes = node.getChildren().toArray();
        if (nodes.length <= 0) {
            return;
        }
        for (Object node2 : nodes) {
            addTreeNode((RepositoryNode) node2, path + SEPERATOR
                    + ((RepositoryNode) node2).getProperties(EProperties.LABEL).toString(), list);
        }
    }

    /**
     * Create an instance of this class.
     *
     * @param selection the selection
     */
    public DeployOnMDMExportWizardPage(IStructuredSelection selection) {
        this("publishOnSpagoExportPage1", selection, null); //$NON-NLS-1$
        setDescription(Messages.DeployOnMDMExportWizard_publishJob);
        setTitle(Messages.DeployOnMDMExportWizardPage_exportTitle);

    }

    /**
     * (non-Javadoc) Method declared on IDialogPage.
     */
    @Override
    public void createControl(Composite parent) {

        initializeDialogUnits(parent);

        Composite composite = new Composite(parent, SWT.NULL);
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
        composite.setFont(parent.getFont());

        createOptionsGroup(composite);

        restoreResourceSpecificationWidgetValues(); // ie.- local
        restoreWidgetValues(); // ie.- subclass hook

        updateWidgetEnablements();
        setPageComplete(determinePageCompletion());
        setErrorMessage(null); // should not initially have error message

        setControl(composite);
    }

    /*
     * @see org.eclipse.ui.internal.wizards.datatransfer.WizardFileSystemResourceExportPage1#validateSourceGroup()
     */
    @Override
    protected boolean validateSourceGroup() {
        return true;
    }

    /**
     * Create the export options specification widgets.
     *
     */
    @Override
    protected void createOptionsGroupButtons(Group optionsGroup) {
        Font font = optionsGroup.getFont();
        optionsGroup.setLayout(new GridLayout(1, true));

        Composite left = new Composite(optionsGroup, SWT.NONE);
        left.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false));
        left.setLayout(new GridLayout(2, true));

        createOptions(left, font);

    }

    /**
     * Create the buttons for the group that determine if the entire or selected directory structure should be created.
     *
     * @param optionsGroup
     * @param font
     */
    protected void createOptions(Composite optionsGroup, Font font) {
        // create directory structure radios
        List<MDMServerDef> listServerSapgo = com.amalto.workbench.utils.MDMServerHelper.getServers();
        List<String> listEngine = new ArrayList<String>();

        if (mdmServer == null) {

            for (MDMServerDef serv : listServerSapgo) {
                listEngine.add(serv.getName());
            }
            serverSpagoBi = new LabelledCombo(optionsGroup, Messages.DeployOnMDMExportWizardPage_SpagoBI_Server,
                    Messages.DeployOnMDMExportWizardPage_SpecifyServer_PublishJob, listEngine);
            serverSpagoBi.addModifyListener(new ModifyListener() {

                public void modifyText(ModifyEvent e) {
                    updateSelectedServer();

                }
            });
            serverSpagoBi.select(0);
        }

        Composite typeComp = new Composite(optionsGroup, SWT.NONE);
        typeComp.setLayout(new FillLayout(SWT.HORIZONTAL));
        typeComp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        Label typeLabel = new Label(typeComp, SWT.NONE);
        typeLabel.setText(Messages.DeployOnMDMExportWizardPage_ExportType);

        Label contentLabel = new Label(typeComp, SWT.NONE);
        contentLabel.setText(Messages.DeployOnMDMExportWizardPage_hostedZip);

        contextButton = new Button(optionsGroup, SWT.CHECK | SWT.LEFT);
        contextButton.setText(Messages.JobScriptsExportWizardPage_contextPerlScripts);
        contextButton.setSelection(true);
        contextButton.setFont(font);

        contextCombo = new Combo(optionsGroup, SWT.PUSH);

    }

    /**
     * DOC HHB Comment method "updateSelectedServer".
     */
    protected void updateSelectedServer() {
        selectedServer = serverSpagoBi.getItem(serverSpagoBi.getSelectionIndex());
    }

    /**
     * Returns a boolean indicating whether the directory portion of the passed pathname is valid and available for use.
     */
    protected boolean ensureTargetDirectoryIsValid(final String fullPathname) {
        final int separatorIndex = fullPathname.lastIndexOf(File.separator);

        if (separatorIndex == -1) {
            return true;
        }
        final List<Boolean> result = new ArrayList<Boolean>();
        Display.getDefault().syncExec(new Runnable() {

            public void run() {
                result.add(ensureTargetIsValid(new File(fullPathname.substring(0, separatorIndex))));
            }
        });
        return result.isEmpty() ? false : result.get(0);
    }

    /**
     * Returns a boolean indicating whether the passed File handle is is valid and available for use.
     */
    protected boolean ensureTargetFileIsValid(File targetFile) {
        if (targetFile.exists() && targetFile.isDirectory()) {
            displayErrorDialog(Messages.DeployOnMDMExportWizardPage_mustBeFile);
            giveFocusToDestination();
            return false;
        }

        if (targetFile.exists()) {
            if (!targetFile.canWrite()) {
                displayErrorDialog(Messages.DeployOnMDMExportWizardPage_alreadyExistsError);
                giveFocusToDestination();
                return false;
            }
        }

        return true;
    }

    /**
     * Ensures that the target output file and its containing directory are both valid and able to be used. Answer a
     * boolean indicating validity.
     */
    protected boolean ensureTargetIsValid(ExportFileResource p) {
        String targetPath = getDestinationValue(p);

        if (!ensureTargetDirectoryIsValid(targetPath)) {
            return false;
        }

        if (!ensureTargetFileIsValid(new File(targetPath))) {
            return false;
        }

        return true;
    }

    /**
     * Export the passed resource and recursively export all of its child resources (iff it's a container). Answer a
     * boolean indicating success.
     */
    protected boolean executeExportOperation(final ArchiveFileExportOperationFullPath op) {
        op.setCreateLeadupStructure(true);
        op.setUseCompression(true);

        Display.getDefault().syncExec(new Runnable() {

            public void run() {
                try {

                    PlatformUI.getWorkbench().getProgressService().run(true, true, op);

                } catch (InterruptedException e) {

                } catch (InvocationTargetException e) {
                    displayErrorDialog(e.getTargetException());
                }
                IStatus status = op.getStatus();
                if (!status.isOK()) {
                    ErrorDialog.openError(getContainer().getShell(), BLANK, null, // no
                            // special
                            // message
                            status);

                }
            }
        });

        IStatus status = op.getStatus();
        if (!status.isOK()) {
            return false;
        }

        return true;
    }

    final int W_PREPARE_PROCESS = 10;

    final int W_GENERATE_PROCESS = 2;

    final int W_PACKAGE_PROCESS = 2;

    final int W_DEPLOY_PROCESS = 10;

    final int W_EXPORT_PROCESS = 2;

    class DeployJobProcess implements IRunnableWithProgress {

        private final boolean needContextScript;

        private final String context;

        public DeployJobProcess(boolean needContextScript, String context) {
            this.needContextScript = needContextScript;
            this.context = context;
        }

        public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
            int size = process.length;
            int total = W_PREPARE_PROCESS + W_PREPARE_PROCESS + W_EXPORT_PROCESS * size + W_GENERATE_PROCESS * size
                    + W_PACKAGE_PROCESS + W_DEPLOY_PROCESS * size;
            monitor.beginTask(Messages.DeployOnMDMExportWizardPage_DeployingJobObjects, total);

            if (prepareProcess(needContextScript, context, monitor)) {
                boolean isOK = true;
                for (ExportFileResource p : process) {
                    if (exportJob(p, needContextScript, monitor)) {
                        if (checkError(p)) {
                            isOK = false;
                            break;
                        }
                        if (generateProcess(p, monitor)) {
                            if (!packageProcess(p, monitor)) {
                                isOK = false;
                                break;
                            }
                        }
                    } else {
                        isOK = false;
                    }
                }

                if (isOK && deployJobProcess(context, monitor)) {
                    Display.getDefault().asyncExec(new Runnable() {

                        public void run() {
                            MessageDialog.openInformation(getContainer().getShell(),
                                    Messages.DeployOnMDMExportWizardPage_publishJob,
                                    Messages.DeployOnMDMExportWizardPage_publishJobSuccess);
                        }
                    });

                }
            }

            monitor.done();

        }
    };

    private boolean checkError(ExportFileResource resource) {
        RepositoryNode node = processMap.get(resource);
        IStructuredSelection selection = new StructuredSelection(node);
        boolean hasError = JobErrorsChecker.checkExportErrors(selection, true);
        if (hasError) {
            recordDeployException(new RuntimeException(Messages.bind(Messages.DeployOnMDMExportWizardPage_jobCompileError,
                    resource.getItem().getProperty().getLabel())));
            setDeploySucceed(false);
        }
        return hasError;
    }

    private boolean prepareProcess(boolean needContextScript, String context, IProgressMonitor monitor) {
        monitor.setTaskName(Messages.DeployOnMDMExportWizardPage_prepareDeployJob);
        monitor.worked(W_PREPARE_PROCESS);
        Map<ExportChoice, Object> exportChoiceMap = getExportChoiceMap(needContextScript);
        boolean canExport = false;
        for (ExportChoice choice : ExportChoice.values()) {

            if (exportChoiceMap.get(choice) != null && exportChoiceMap.get(choice) instanceof Boolean
                    && (Boolean) exportChoiceMap.get(choice)) {
                canExport = true;
                break;
            }
        }
        if (!canExport) {
            Display.getDefault().asyncExec(new Runnable() {

                public void run() {
                    MessageDialog.openInformation(getContainer().getShell(),
                            Messages.DeployOnMDMExportWizardPage_publishResourceError,
                            Messages.DeployOnMDMExportWizardPage_chooseResource);
                }
            });
            setDeploySucceed(false);
            return false;
        }

        // reset the manager，now only support zip type
        manager = new JobJavaScriptsManager(exportChoiceMap, context, JobScriptsManager.ALL_ENVIRONMENTS,
                IProcessor.NO_STATISTICS, IProcessor.NO_TRACES);

        if (!saveflag) {
            saveflag = true;
            return false;
        }
        // about to invoke the operation so save our state
        doSaveWidgetValues();

        monitor.worked(W_PREPARE_PROCESS);
        return true;
    }

    private boolean exportJob(ExportFileResource p, boolean needContextScript, IProgressMonitor monitor) {
        String label = getProcessName(p);
        monitor.setTaskName(Messages.bind(Messages.DeployOnMDMExportWizardPage_exporting, label));
        if (!ensureTargetIsValid(p)) {
            setDeploySucceed(false);
            return false;
        }
        List<ExportFileResource> resourcesToExport = null;
        try {
            resourcesToExport = getExportResources(p);
        } catch (ProcessorException e) {
            MessageBoxExceptionHandler.process(e);

            setDeploySucceed(false);
            return false;
        }
        String topFolder = getRootFolderName(p);
        setTopFolder(resourcesToExport, topFolder);
        ArchiveFileExportOperationFullPath exporterOperation = getExporterOperation(p, resourcesToExport);
        boolean ok = executeExportOperation(exporterOperation);
        // TODO What if not ok ????

        // path can like name/name
        manager.deleteTempFiles();

        ProcessorUtilities.resetExportConfig();
        monitor.worked(W_EXPORT_PROCESS);
        return true;
    }

    protected void doSaveWidgetValues() {
        Display.getDefault().syncExec(new Runnable() {

            public void run() {
                saveWidgetValues();
            }
        });

    }

    private String getProcessName(ExportFileResource p) {
        ProcessItem processItem = (ProcessItem) p.getItem();
        String label = processItem.getProperty().getLabel();
        return label;
    }

    private String getProcessVersion(ExportFileResource p) {
        ProcessItem processItem = (ProcessItem) p.getItem();
        String label = processItem.getProperty().getVersion();
        return label;
    }

    private boolean generateProcess(ExportFileResource p, IProgressMonitor monitor) {
        String label = getProcessName(p);
        monitor.setTaskName(Messages.bind(Messages.DeployOnMDMExportWizardPage_Generating, label));
        ProcessItem processItem = (ProcessItem) p.getItem();

        try {
            ProcessorUtilities.generateCode(processItem, processItem.getProcess().getDefaultContext(), false, false);
            monitor.worked(W_GENERATE_PROCESS);
        } catch (ProcessorException e) {
            MessageBoxExceptionHandler.process(e);

            setDeploySucceed(false);
            return false;
        }

        return true;
    }

    private boolean packageProcess(ExportFileResource p, IProgressMonitor monitor) {
        String label = getProcessName(p);
        monitor.setTaskName(Messages.bind(Messages.DeployOnMDMExportWizardPage_Pacakging, label));
        ECodeLanguage curLanguage = LanguageManager.getCurrentLanguage();
        if (curLanguage == ECodeLanguage.JAVA) {
            reBuildJobZipFile(p);
        }
        setDesValueForJob(p);
        processForEachJob(p);
        monitor.worked(W_PACKAGE_PROCESS);

        return true;
    }

    public MDMServerDef getServerDefFromList(String desc) {
        List<MDMServerDef> listServerSapgo = com.amalto.workbench.utils.MDMServerHelper.getServers();
        if (null != listServerSapgo) {
            for (MDMServerDef def : listServerSapgo) {
                if (desc.equals(def.getName())) {
                    return def;
                }
            }
        }
        return null;

    }

    private boolean deployJobProcess(String context, IProgressMonitor monitor) {
        // retrieve user, password, host, port from selected server
        MDMServerDef server = null;
        if (mdmServer == null) {
            String selectedSpagoBiEngineName = selectedServer;

            List<MDMServerDef> listServerSapgo = com.amalto.workbench.utils.MDMServerHelper.getServers();
            for (MDMServerDef serv : listServerSapgo) {
                if (selectedSpagoBiEngineName.equals(serv.getName())) {
                    server = serv;
                    break;
                }
            }
        } else {
            server = getServerDefFromList(mdmServer.getShortDescription());
        }
        if (null == server) {
            setDeploySucceed(false);
            return false;
        }
        String user = server.getUser();
        String password = server.getPasswd();
        String host = server.getHost();
        String port = server.getPort();

        // check connection before sending data
        monitor.setTaskName(Messages.DeployOnMDMExportWizardPage_checkConnection);
        boolean success = checkConnection(server.getUrl(), user, password);

        if (!success) {
            Display.getDefault().syncExec(new Runnable() {

                public void run() {
                    String msg = Messages.DeployOnMDMExportWizardPage_UnableConnect;
                    MessageDialog.openError(getContainer().getShell(), Messages.DeployOnMDMExportWizardPage_publishResourceError,
                            msg);
                }
            });

            setDeploySucceed(false);
            return false;
        }

        // modified by xie to fix bug 20084 ,do the multi jobs deployment in one time.
        for (JobDeploymentInfo jobInfo : jobInfoList) {
            monitor.setTaskName(Messages.bind(Messages.DeployOnMDMExportWizardPage_DeployJobOnServer, jobInfo.getJobLabelName(),
                    server.getName()));
            String filename = jobInfo.getDescValue();
            String contextPath = Util.getContextPath(server.getPath());
            String mdmServerUploadURL = server.getProtocol() + host
                    + ":" + port + contextPath + "/services/uploadFile?deployjob="//$NON-NLS-1$ //$NON-NLS-2$
                    + new File(filename).getName() + "&jobpath=" + jobInfo.getJobPath() + "&contextStr=" + context;//$NON-NLS-1$//$NON-NLS-2$
            try {
                HttpClientUtil.uploadFileToAppServer(mdmServerUploadURL, filename, user, password);
                monitor.worked(W_DEPLOY_PROCESS);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                Display.getDefault().syncExec(new Runnable() {

                    public void run() {
                        MessageDialog.openError(getContainer().getShell(),
                                Messages.DeployOnMDMExportWizardPage_publishResourceError,
                                Messages.DeployOnMDMExportWizardPage_ErrorDeployMsg);
                    }
                });

                recordDeployException(new RuntimeException(e.getMessage(), e));

                setDeploySucceed(false);
                return false;
            }

        }

        mdmServer = toSpagoBiServer(server);
        setDeploySucceed(true);
        return true;
    }

    private boolean needContextScript() {
        return (contextButton != null) ? contextButton.getSelection() : true;
    }

    /**
     * The Finish button was pressed. Try to do the required work now and answer a boolean indicating success. If false
     * is returned then the wizard will not close.
     *
     * @returns boolean
     */
    @Override
    public boolean finish() {
        try {
            boolean needContextScript = needContextScript();
            String context = contextCombo.getText();
            DeployJobProcess deployJobProcess = new DeployJobProcess(needContextScript, context);
            getContainer().run(true, false, deployJobProcess);
        } catch (InvocationTargetException ex) {
            log.error(ex.getMessage(), ex);
        } catch (InterruptedException ex) {
            log.error(ex.getMessage(), ex);
        }

        return true;
    }

    private boolean checkConnection(String endpointaddress, String username, String password) {
        try {
            ILegendServerDefService serverDefService = (ILegendServerDefService) GlobalServiceRegister.getDefault().getService(
                    ILegendServerDefService.class);
            if (serverDefService != null) {
                return serverDefService.checkServerDefConnection(endpointaddress, username, password);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return true;
    }

    /**
     *
     * DOC aiming Comment method "reBuildJobZipFile".
     *
     */
    private void reBuildJobZipFile(ExportFileResource p) {
        JavaJobExportReArchieveCreator creator = null;
        String zipFile = getDestinationValue(p);

        String tmpFolder = ExportJobUtil.getTmpFolder();
        try {
            // unzip to tmpFolder
            ZipToFile.unZipFile(zipFile, tmpFolder);
            // remove jaxrpc.jar from lib , see 0021706
            String javxRpc = tmpFolder + File.separator + "WEB-INF" + File.separator + "lib" + File.separator + "jaxrpc.jar";//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
            File rpcfile = new File(javxRpc);
            if (rpcfile.exists()) {
                rpcfile.delete();
            }
            // build new jar

            if (p != null) {
                String jobFolderName = p.getDirectoryName();
                int pos = jobFolderName.indexOf(SEPERATOR);
                if (pos != -1) {
                    jobFolderName = jobFolderName.substring(pos + 1);
                }
                if (creator == null) {
                    creator = new JavaJobExportReArchieveCreator(zipFile, jobFolderName);
                } else {
                    creator.setJobFolerName(jobFolderName);
                }
                creator.buildNewJar();
            }

            // rezip the tmpFolder to zipFile
            ZipToFile.zipFile(tmpFolder, zipFile);
        } catch (Exception e) {
            ExceptionHandler.process(e);
        } finally {
            ExportJobUtil.deleteTempFiles();
        }
    }

    /**
     * process each job,unzip to a temp folder , copy the necessary folder for each job , and zip to the MDM temp folder
     * for uploading.
     */
    protected void processForEachJob(ExportFileResource p) {

        String zipFile = getDestinationValue(p);

        String tmpFolder = ExportJobUtil.getTmpFolder() + File.separator + "forJob"; //$NON-NLS-1$

        try {
            // unzip to tmpFolder
            ZipToFile.unZipFile(zipFile, tmpFolder);
            String jobLabelName = getProcessName(p);
            String jobVersion = getProcessVersion(p);
            File desFile = new File(tmpFolder + File.separator + jobLabelName + UNDERLINE + jobVersion);

            if (desFile.isDirectory()) {
                JobDeploymentInfo jobInfo = getJobDeploymentInfo(p);
                if (jobInfo != null) {
                    File sourFile = new File(tmpFolder + File.separator + jobLabelName + UNDERLINE + jobVersion + File.separator
                            + jobInfo.getJobLabelName());
                    File sourLibFile = new File(tmpFolder + File.separator + jobLabelName + UNDERLINE + jobVersion
                            + File.separator + "lib"); //$NON-NLS-1$

                    String jobDescPath = tmpFolder + File.separator + jobInfo.getJobLabelName() + UNDERLINE
                            + jobInfo.getJobVersion() + "job"; //$NON-NLS-1$

                    File jobDescDir = new File(jobDescPath);
                    if (!jobDescDir.exists()) {
                        jobDescDir.mkdir();
                    }
                    File jobDescDir_in = new File(jobDescPath + File.separator + jobInfo.getJobLabelName() + UNDERLINE
                            + jobInfo.getJobVersion());
                    if (!jobDescDir_in.exists()) {
                        jobDescDir_in.mkdir();
                    }
                    File jobDescDir_main = new File(jobDescPath + File.separator + jobInfo.getJobLabelName() + UNDERLINE
                            + jobInfo.getJobVersion() + File.separator + jobInfo.getJobLabelName());
                    if (!jobDescDir_main.exists()) {
                        jobDescDir_main.mkdir();
                    }

                    File jobDescDirLib = new File(jobDescPath + File.separator + jobInfo.getJobLabelName() + UNDERLINE
                            + jobInfo.getJobVersion() + File.separator + "lib"); //$NON-NLS-1$
                    if (!jobDescDirLib.exists()) {
                        jobDescDirLib.mkdir();
                    }

                    copy(sourFile.listFiles(), jobDescDir_main);
                    copy(sourLibFile.listFiles(), jobDescDirLib);

                    ZipToFile.zipFile(jobDescPath, jobInfo.getDescValue());
                }
            }
        } catch (Exception e) {
            ExceptionHandler.process(e);
        } finally {
            ExportJobUtil.deleteTempFiles();
        }

    }

    /**
     * copy all the files of this folder to another
     */

    private void copy(File[] s, File d) {
        if (!d.exists()) {
            d.mkdir();
        }
        for (File element : s) {
            if (element.isFile()) {
                try {
                    FileInputStream fis = new FileInputStream(element);
                    FileOutputStream out = new FileOutputStream(new File(d.getPath() + File.separator + element.getName()));
                    int count = fis.available();
                    byte[] data = new byte[count];
                    if ((fis.read(data)) != -1) {
                        out.write(data);
                    }
                    out.close();
                    fis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (element.isDirectory()) {
                File des = new File(d.getPath() + File.separator + element.getName());
                des.mkdir();
                copy(element.listFiles(), des);
            }
        }
    }

    /**
     * Get the export operation.
     *
     * @param resourcesToExport
     * @return
     */
    private ArchiveFileExportOperationFullPath getExporterOperation(ExportFileResource p,
            List<ExportFileResource> resourcesToExport) {
        ArchiveFileExportOperationFullPath exporterOperation = new ArchiveFileExportOperationFullPath(resourcesToExport,
                getDestinationValue(p));
        return exporterOperation;
    }

    private ArchiveFileExportOperationFullPath getExporterOperationForOneJob(List<ExportFileResource> resourcesToExport,
            String descValue) {
        ArchiveFileExportOperationFullPath exporterOperation = new ArchiveFileExportOperationFullPath(resourcesToExport,
                descValue);
        return exporterOperation;
    }

    /**
     * Returns the root folder name.
     *
     * @return
     */
    private String getRootFolderName(ExportFileResource p) {
        IPath path = new Path(this.getDestinationValue(p));
        String subjectString = path.lastSegment();
        Pattern regex = Pattern.compile("(.*)(?=(\\.(tar|zip))\\b)", Pattern.CANON_EQ | Pattern.CASE_INSENSITIVE //$NON-NLS-1$
                | Pattern.UNICODE_CASE);
        Matcher regexMatcher = regex.matcher(subjectString);
        if (regexMatcher.find()) {
            subjectString = regexMatcher.group(0);
        }

        return subjectString.trim();
    }

    private void setTopFolder(List<ExportFileResource> resourcesToExport, String topFolder) {
        if (resourcesToExport != null) {
            for (ExportFileResource fileResource : resourcesToExport) {
                String directory = fileResource.getDirectoryName();
                fileResource.setDirectoryName(topFolder + SEPERATOR + directory);
            }
        }
    }

    /**
     * Answer the string to display in self as the destination type.
     *
     * @return java.lang.String
     */
    @Override
    protected String getDestinationLabel() {
        return ""; //$NON-NLS-1$
    }

    /**
     * Returns resources to be exported. This returns file - for just the files use getSelectedResources.
     *
     * @return a collection of resources currently selected for export (element type: <code>IResource</code>)
     * @throws ProcessorException
     */
    protected List<ExportFileResource> getExportResources(ExportFileResource p) throws ProcessorException {
        Item item = p.getItem();
        Resource eResource = item.eResource();
        Property property = item.getProperty();
        if (eResource == null && property != null) {
            IProxyRepositoryFactory factory = CoreRuntimePlugin.getInstance().getProxyRepositoryFactory();

            try {
                IRepositoryViewObject newViewObj = factory.getLastVersion(property.getId());
                Item newItem = newViewObj.getProperty().getItem();
                p.setProcess((ProcessItem) newItem);

            } catch (PersistenceException e) {
                log.error(e.getMessage(), e);
            }
        }
        return manager.getExportResources(new ExportFileResource[] { p });
    }

    protected List<ExportFileResource> getExportResources() throws ProcessorException {
        return manager.getExportResources(process);
    }

    /**
     * set the destinationValue for each job
     */

    protected void setDesValueForJob(ExportFileResource p) {
        JobDeploymentInfo jobInfo = getJobDeploymentInfo(p);
        if (jobInfo != null) {
            String path = getDestinationValue(p);
            jobInfo.setDescValue(path);
        }

    }

    private JobDeploymentInfo getJobDeploymentInfo(ExportFileResource p) {
        for (JobDeploymentInfo jobInfo : jobInfoList) {
            Item item = p.getItem();
            if (item instanceof ProcessItem) {
                String name = getProcessName(p);
                if (jobInfo.getJobLabelName().equals(name)) {
                    return jobInfo;
                }
            }
        }
        return null;
    }

    private Map<ExportChoice, Object> getExportChoiceMap(boolean needContextScript) {
        Map<ExportChoice, Object> exportChoiceMap = new EnumMap<ExportChoice, Object>(ExportChoice.class);

        // zip
        exportChoiceMap.put(ExportChoice.needLauncher, true);
        exportChoiceMap.put(ExportChoice.needSystemRoutine, true);
        exportChoiceMap.put(ExportChoice.needUserRoutine, true);
        exportChoiceMap.put(ExportChoice.needTalendLibraries, true);
        exportChoiceMap.put(ExportChoice.needJobItem, true);
        // exportChoiceMap.put(ExportChoice.needSourceCode, true);
        exportChoiceMap.put(ExportChoice.needDependencies, true);
        exportChoiceMap.put(ExportChoice.needJobScript, true);
        exportChoiceMap.put(ExportChoice.needContext, true);
        exportChoiceMap.put(ExportChoice.applyToChildren, true);
        exportChoiceMap.put(ExportChoice.needDependencies, true);

        exportChoiceMap.put(ExportChoice.needContext, needContextScript);

        return exportChoiceMap;
    }

    /**
     * Answer the contents of self's destination specification widget. If this value does not have a suffix then add it
     * first.
     */
    @Override
    protected String getDestinationValue() {

        IPath tempPath;
        tempPath = Path.fromOSString(CorePlugin.getDefault().getPreferenceStore()
                .getString(ITalendCorePrefConstants.FILE_PATH_TEMP));
        return tempPath.toOSString();
    }

    protected String getDestinationValue(ExportFileResource p) {
        String filename = getProcessName(p) + UNDERLINE + getProcessVersion(p) + EXT_ZIP;
        IPath tempPath;
        tempPath = Path.fromOSString(CorePlugin.getDefault().getPreferenceStore()
                .getString(ITalendCorePrefConstants.FILE_PATH_TEMP));
        tempPath = tempPath.append(filename);
        return tempPath.toOSString();
    }

    /**
     * Open an appropriate destination browser so that the user can specify a source to import from.
     */
    @Override
    protected void handleDestinationBrowseButtonPressed() {
        FileDialog dialog = new FileDialog(getContainer().getShell(), SWT.SAVE);
        dialog.setFilterExtensions(new String[] { "*.zip", "*.*" }); //$NON-NLS-1$ //$NON-NLS-2$
        dialog.setText(BLANK);
        String currentSourceString = getDestinationValue();
        int lastSeparatorIndex = currentSourceString.lastIndexOf(File.separator);
        if (lastSeparatorIndex != -1) {
            dialog.setFilterPath(currentSourceString.substring(0, lastSeparatorIndex));
        }
        String selectedFileName = dialog.open();

        if (selectedFileName != null) {
            setErrorMessage(null);
            setDestinationValue(selectedFileName);
        }
    }

    /**
     * Hook method for saving widget values for restoration by the next instance of this class.
     */

    @Override
    protected void internalSaveWidgetValues() {
    }

    /**
     * Hook method for restoring widget values to the values that they held last time this wizard was used to
     * completion.
     */
    @Override
    protected void restoreWidgetValues() {
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.wizards.datatransfer.WizardFileSystemResourceExportPage1#destinationEmptyMessage()
     */
    @Override
    protected String destinationEmptyMessage() {
        return BLANK;
    }

    @Override
    protected void createOptionsGroup(Composite parent) {
        // options group
        Group optionsGroup = new Group(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        optionsGroup.setLayout(layout);
        optionsGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
        optionsGroup.setText(Messages.DeployOnMDMExportWizardPage_Settings);
        optionsGroup.setFont(parent.getFont());
        createOptionsGroupButtons(optionsGroup);

    }

    private SpagoBiServer toSpagoBiServer(MDMServerDef serverDef) {
        SpagoBiServer spagoBiServer = PropertiesFactory.eINSTANCE.createSpagoBiServer();
        spagoBiServer.setShortDescription(serverDef.getName());
        spagoBiServer.setHost(serverDef.getHost());
        spagoBiServer.setPort(serverDef.getPort());
        spagoBiServer.setLogin(serverDef.getUser());
        spagoBiServer.setPassword(serverDef.getPasswd());

        return spagoBiServer;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.internal.wizards.datatransfer.WizardFileSystemResourceExportPage1#validateDestinationGroup()
     */
    @Override
    protected boolean validateDestinationGroup() {
        return true;
    }

    public SpagoBiServer getMdmServer() {
        return this.mdmServer;
    }

    public RuntimeException getDeployException() {
        return deployException;
    }

    private void recordDeployException(RuntimeException e) {
        this.deployException = e;
    }

    private void setDeploySucceed(boolean succeed) {
        this.isDeploySucceed = succeed;
    }

    public boolean isDeploySucceed() {
        return isDeploySucceed;
    }

}
