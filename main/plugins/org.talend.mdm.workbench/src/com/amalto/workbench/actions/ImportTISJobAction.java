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
package com.amalto.workbench.actions;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;

import com.amalto.workbench.editors.XObjectEditor;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.models.TreeParent;
import com.amalto.workbench.providers.XObjectEditorInput;
import com.amalto.workbench.utils.HttpClientUtil;
import com.amalto.workbench.utils.JobInfo;
import com.amalto.workbench.utils.LocalTreeObjectRepository;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.views.ServerView;
import com.amalto.workbench.webservices.WSMDMJobArray;
import com.amalto.workbench.webservices.WSMDMNULL;
import com.amalto.workbench.webservices.XtentisPort;

public class ImportTISJobAction extends Action {

    private ServerView server = ServerView.show();

    private TreeParent xobject;

    public ImportTISJobAction() {
        super();

        setImageDescriptor(ImageCache.getImage(EImage.WORKFLOW_PROCESS.getPath()));
        setText(Messages.ImportTISJobAction_Text);
        setToolTipText(Messages.ImportTISJobAction_Text);
    }

    public void run() {
        if (this.server != null) { // called from ServerView
            ISelection selection = server.getViewer().getSelection();
            xobject = (TreeParent) ((IStructuredSelection) selection).getFirstElement();
        }

        if (xobject.getType() != TreeObject.JOB_REGISTRY)
            return;
        try {
            // Access to server and get port
            XtentisPort port = Util.getPort(new URL(xobject.getEndpointAddress()), xobject.getUniverse(), xobject.getUsername(),
                    xobject.getPassword());
            FileDialog fileDialog = new FileDialog(server.getSite().getShell(), SWT.OPEN);
            fileDialog.setFilterExtensions(new String[] { "*.war", "*.zip" });//$NON-NLS-1$ //$NON-NLS-2$
            String name = fileDialog.open();

            boolean recover = false;
            boolean exist = false;
            if (name != null) {
                JobInfo info = getJobInfo(name);
                if (info == null)
                    return;
                WSMDMJobArray array = port.getMDMJob(new WSMDMNULL());
                exist = checkExist(array, info);
                if (exist) {
                    recover = MessageDialog.openConfirm(this.server.getSite().getShell(), Messages.ImportTISJobAction_Confirm,
                            Messages.ImportTISJobAction_ConfirmInfo + System.getProperty("line.separator") //$NON-NLS-1$
                                    + Messages.ImportTISJobAction_ConfirmInfoA);
                    if (!recover)
                        return;
                    else
                        MessageDialog.openInformation(this.server.getSite().getShell(), Messages.ImportTISJobAction_Information, Messages.ImportTISJobAction_InformationMsg);
                }

                String fileName = info.getJobname() + "_" + info.getJobversion() + info.getSuffix(); //$NON-NLS-1$
                String endpointaddress = xobject.getEndpointAddress();
                String uploadURL = new URL(endpointaddress).getProtocol() + "://" + new URL(endpointaddress).getHost() + ":"//$NON-NLS-1$ //$NON-NLS-2$
                        + new URL(endpointaddress).getPort() + "/datamanager/uploadFile?deployjob=" + fileName;//$NON-NLS-1$ 
                String remoteFile = HttpClientUtil.uploadFileToAppServer(uploadURL, name, xobject.getUsername(),
                        xobject.getPassword());

                // parse file to get jobinfo
                TreeObject jobFolder = xobject.findObject(TreeObject.BUILT_IN_CATEGORY_FOLDER, "Deployed Jobs");//$NON-NLS-1$
                if (jobFolder == null)
                    jobFolder = new TreeParent("Deployed Jobs", xobject.getServerRoot(), TreeObject.CATEGORY_FOLDER, null, null);//$NON-NLS-1$ 

                TreeObject obj = new TreeObject(
                // fileDialog.getFileName(),
                        info.getJobname() + "_" + info.getJobversion() + info.getSuffix(), xobject.getServerRoot(),//$NON-NLS-1$
                        TreeObject.JOB, info, null);
                if (!xobject.containsChild(obj)) {
                    ((TreeParent) jobFolder).addChild(obj);
                    xobject.addChild(jobFolder);
                }
                LocalTreeObjectRepository.getInstance().mergeNewTreeObject(obj);
                XObjectEditor editpart = (XObjectEditor) server
                        .getSite()
                        .getWorkbenchWindow()
                        .getActivePage()
                        .openEditor(new XObjectEditorInput(obj, obj.getDisplayName()),
                                "com.amalto.workbench.editors.XObjectEditor");//$NON-NLS-1$ 
            }

        } catch (Exception e) {

        }
    }

    /**
     * get the JobInfo:jobName and version
     * 
     * @param fileName
     * @return
     */
    public JobInfo getJobInfo(String fileName) {
        JobInfo jobInfo = null;
        try {
            ZipInputStream in = new ZipInputStream(new FileInputStream(fileName));

            ZipEntry z = null;

            try {
                String jobName = "";//$NON-NLS-1$
                String jobVersion = "";//$NON-NLS-1$
                // war
                if (fileName.endsWith(".war")) {//$NON-NLS-1$ 
                    while ((z = in.getNextEntry()) != null) {
                        String dirName = z.getName();
                        // get job version
                        if (dirName.endsWith("undeploy.wsdd")) {//$NON-NLS-1$
                            Pattern p = Pattern.compile(".*?_(\\d_\\d)/undeploy.wsdd");//$NON-NLS-1$ 
                            Matcher m = p.matcher(dirName);
                            m.groupCount();
                            if (m.matches()) {
                                jobVersion = m.group(1);
                            }
                        }
                        // get job name
                        Pattern p = Pattern.compile(".*?/(.*?)\\.wsdl");//$NON-NLS-1$ 
                        Matcher m = p.matcher(dirName);
                        if (m.matches()) {
                            jobName = m.group(1);
                        }
                    }
                    if (jobName.length() > 0) {
                        jobInfo = new JobInfo(null, null, null);
                        jobInfo.setJobname(jobName);
                        jobInfo.setJobversion(jobVersion.replaceAll("_", "."));//$NON-NLS-1$ //$NON-NLS-2$
                        jobInfo.setSuffix(".war");//$NON-NLS-1$
                        return jobInfo;
                    } else {
                        return null;
                    }
                }// war
                if (fileName.endsWith(".zip")) {//$NON-NLS-1$ 
                    while ((z = in.getNextEntry()) != null) {
                        String dirName = z.getName();
                        int pos = dirName.indexOf('/');

                        String dir = dirName.substring(0, pos);
                        pos = dir.lastIndexOf('_');
                        jobName = dir.substring(0, pos);
                        jobVersion = dir.substring(pos + 1);
                        break;

                    }
                    if (jobName.length() > 0) {
                        jobInfo = new JobInfo(null, null, null);
                        jobInfo.setJobname(jobName);
                        jobInfo.setJobversion(jobVersion);
                        jobInfo.setSuffix(".zip");//$NON-NLS-1$ 
                        return jobInfo;
                    } else {
                        return null;
                    }
                }
            } catch (FileNotFoundException e) {
            } finally {
                in.close();
            }
        } catch (Exception e) {

        }

        return jobInfo;
    }

    // if the Job exist, return true,else return false
    public boolean checkExist(WSMDMJobArray array, JobInfo jobInfo) {
        for (int i = 0; i < array.getWsMDMJob().length; i++) {
            if (array.getWsMDMJob()[i].getJobName().equals(jobInfo.getJobname())
                    && array.getWsMDMJob()[i].getJobVersion().equals(jobInfo.getJobversion())
                    && array.getWsMDMJob()[i].getSuffix().equals(jobInfo.getSuffix()))
                return true;
        }
        return false;
    }

    public void runWithEvent(Event event) {
        super.runWithEvent(event);
    }

}
