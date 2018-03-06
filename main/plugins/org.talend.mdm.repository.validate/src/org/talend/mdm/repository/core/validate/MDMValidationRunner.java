// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.repository.core.validate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.validation.internal.ValOperation;
import org.eclipse.wst.validation.internal.ValType;
import org.eclipse.wst.validation.internal.ValidationResultSummary;
import org.eclipse.wst.validation.internal.ValidationRunner;
import org.talend.core.model.general.Project;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.IServerObjectRepositoryType;
import org.talend.mdm.repository.core.service.IModelValidationService;
import org.talend.mdm.repository.core.service.IModelValidationService.IModelValidateResult;
import org.talend.mdm.repository.core.validate.i18n.Messages;
import org.talend.mdm.repository.ui.dialogs.ValidationResultDialog;
import org.talend.mdm.repository.ui.dialogs.lock.LockedDirtyObjectDialog;
import org.talend.mdm.repository.ui.views.MDMProblemView;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;
import org.talend.mdm.repository.utils.UIUtil;
import org.talend.repository.ProjectManager;

/**
 * created by HHB on 2013-1-23 Detailled comment
 * 
 */
public class MDMValidationRunner extends WorkspaceJob {

    static Logger log = Logger.getLogger(MDMValidationRunner.class);

    private static boolean running = false;

    private final IValidationPreference validationPref;

    Map<IProject, Set<IResource>> toValidate = new HashMap<IProject, Set<IResource>>();

    Map<IRepositoryViewObject, IFile> viewObjMap = new HashMap<IRepositoryViewObject, IFile>();

    private int returnCode = IDialogConstants.OK_ID;

    private IModelValidateResult validateResult = null;

    /**
     * Sets the validateResult.
     * 
     * @param validateResult the validateResult to set
     */
    private void setValidateResult(IModelValidateResult validateResult) {
        this.validateResult = validateResult;
    }

    private LockedDirtyObjectDialog lockDirtyDialog;

    private boolean forbidShowResultDialog;

    /**
     * Getter for returnCode.
     * 
     * @return the returnCode
     */
    public int getReturnCode() {
        return this.returnCode;
    }

    /**
     * Sets the returnCode.
     * 
     * @param returnCode the returnCode to set
     */
    private void setReturnCode(int returnCode) {
        this.returnCode = returnCode;
    }

    /**
     * DOC HHB ValidationRunner constructor comment.
     * @param allowShowResultDialog
     * 
     * @param name
     */
    public MDMValidationRunner(List<IRepositoryViewObject> viewObjs, IValidationPreference validationPref,
            Boolean forbidShowResultDialog) {
        super("MDM Validation"); //$NON-NLS-1$
        this.validationPref = validationPref;
        this.forbidShowResultDialog = forbidShowResultDialog;
        init(viewObjs);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.runtime.jobs.Job#shouldSchedule()
     */
    @Override
    public boolean shouldSchedule() {
        return !running;

    }

    public static IModelValidateResult validate(List<IRepositoryViewObject> viewObjs, IValidationPreference validationPref,
            boolean forbidShowResultDialog) {
        MDMValidationRunner runner = new MDMValidationRunner(viewObjs, validationPref, forbidShowResultDialog);
        IJobChangeListener listener = new JobChangeAdapter() {

            @Override
            public void aboutToRun(IJobChangeEvent event) {
                running = true;
            }

            @Override
            public void done(IJobChangeEvent event) {
                running = false;
            }

        };
        runner.addJobChangeListener(listener);
        runner.schedule();
        try {
            runner.join();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
        return runner.validateResult;
    }

    private void init(List<IRepositoryViewObject> viewObjs) {
        viewObjMap.clear();
        Project talendProject = ProjectManager.getInstance().getCurrentProject();
        if (talendProject != null) {
            IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(talendProject.getTechnicalLabel());
            Set<IResource> files = new HashSet<IResource>();

            for (IRepositoryViewObject viewObj : viewObjs) {
                IFile file = null;
                ERepositoryObjectType type = viewObj.getRepositoryObjectType();
                if (type == IServerObjectRepositoryType.TYPE_DATAMODEL) {
                    Item item = viewObj.getProperty().getItem();
                    file = RepositoryResourceUtil.findReferenceFile(type, item, "xsd"); //$NON-NLS-1$
                    if (file != null) {
                        files.add(file);
                    }
                    //
                    //                    file = RepositoryResourceUtil.findReferenceFile(type, item, "mapinfo"); //$NON-NLS-1$
                    // if (file != null) {
                    // files.add(file);
                    // }

                }
                if (type == IServerObjectRepositoryType.TYPE_VIEW) {
                    Item item = viewObj.getProperty().getItem();
                    file = RepositoryResourceUtil.findReferenceFile(type, item, "item"); //$NON-NLS-1$
                    if (file != null) {
                        files.add(file);
                    }

                }
                if (type == IServerObjectRepositoryType.TYPE_WORKFLOW) {
                    Item item = viewObj.getProperty().getItem();
                    file = RepositoryResourceUtil.findReferenceFile(type, item, "conf"); //$NON-NLS-1$
                    if (file != null) {
                        files.add(file);
                    }
                    
                }
                viewObjMap.put(viewObj, file);
            }

            toValidate.put(project, files);
            if (UIUtil.isWorkInUI()) {
                lockDirtyDialog = new LockedDirtyObjectDialog(null, Messages.MDMValidationRunner_promptToSaveEditors, viewObjs);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.resources.WorkspaceJob#runInWorkspace(org.eclipse.core.runtime.IProgressMonitor)
     */
    @SuppressWarnings({ "restriction", "hiding" })
    @Override
    public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
        if (UIUtil.isWorkInUI() && lockDirtyDialog.needShowDialog()) {
            Display.getDefault().syncExec(new Runnable() {

                @Override
                public void run() {
                    if (lockDirtyDialog.open() == IDialogConstants.CANCEL_ID) {
                        setReturnCode(IDialogConstants.CANCEL_ID);
                    } else {
                        lockDirtyDialog.saveDirtyObjects();
                    }

                }
            });
            if (getReturnCode() == IDialogConstants.CANCEL_ID) {
                setValidateResult(new MDMValidationService.ModelValidateResult());
                return Status.CANCEL_STATUS;
            }
        }

        final ValOperation vo = ValidationRunner.validate(toValidate, ValType.Manual, monitor, false);
        if (vo.isCanceled()) {
            setValidateResult(new MDMValidationService.ModelValidateResult());
            return Status.CANCEL_STATUS;
        }
        final ValidationResultSummary result = vo.getResult();
        final IModelValidateResult validateResult = new MDMValidationService.ModelValidateResult(viewObjMap);
        if (needShowValidationResults(result)) {
            final Set<IResource> resources = toValidate.values().iterator().next();
            Display.getDefault().syncExec(new Runnable() {

                @Override
                public void run() {

                    ValidationResultDialog d = new ValidationResultDialog(new Shell(), result, validationPref, viewObjMap);
                    int code = d.open();
                    validateResult.setSelectedButton(code);
                    setValidateResult(validateResult);
                }
            });
        } else {
            setValidateResult(validateResult);
            if (validateResult.hasErrOrWarning()) {
                int code = ValidationPreferenceService.getInstance().getDeployActionWhenValidateFail();
                validateResult.setSelectedButton(code);
            } else {
                validateResult.setSelectedButton(IModelValidationService.BUTTON_OK);
            }
        }
        activeProblemView(result);
        return Status.OK_STATUS;
    }

    private boolean needShowValidationResults(final ValidationResultSummary result) {
        return !forbidShowResultDialog && UIUtil.isWorkInUI() && validationPref.shouldShowResults(result);
    }

    private void activeProblemView(ValidationResultSummary result) {
        if (UIUtil.isWorkInUI()) {
            if (result.getSeverityError() > 0 || result.getSeverityWarning() > 0 || result.getSeverityInfo() > 0) {
                Display.getDefault().asyncExec(new Runnable() {

                    @Override
                    public void run() {
                        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

                        if (page != null && UIUtil.isMDMPerspective(page)) {
                            IWorkbenchPart activepart = page.getActivePart();
                            try {
                                page.showView(MDMProblemView.VIEW_ID);
                                if (activepart != null) {
                                    page.activate(activepart);
                                }
                            } catch (PartInitException e) {
                                log.error(e.getMessage(), e);
                            }
                        }

                    }
                });
            }
        }
    }
}
