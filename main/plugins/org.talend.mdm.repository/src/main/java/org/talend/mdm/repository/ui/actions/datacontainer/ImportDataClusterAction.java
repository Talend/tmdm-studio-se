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
package org.talend.mdm.repository.ui.actions.datacontainer;

import java.io.File;

import javax.xml.ws.WebServiceException;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.talend.mdm.repository.core.datacontent.IDataContentProcess;
import org.talend.mdm.repository.core.service.DataClusterService;
import org.talend.mdm.repository.core.service.RepositoryWebServiceAdapter;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.model.mdmmetadata.MDMServerDef;
import org.talend.mdm.repository.ui.dialogs.message.MultiStatusDialog;
import org.talend.mdm.repository.utils.IOUtil;
import org.talend.mdm.workbench.serverexplorer.ui.dialogs.SelectServerDefDialog;

import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.utils.XtentisException;
import com.amalto.workbench.webservices.TMDMService;
import com.amalto.workbench.webservices.WSPing;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class ImportDataClusterAction extends AbstractDataClusterAction {

    private static Logger log = Logger.getLogger(ImportDataClusterAction.class);

    /**
     * DOC hbhong ImportDataClusterAction constructor comment.
     *
     * @param text
     */
    public ImportDataClusterAction() {
        super(Messages.ImportDataClusterAction_title);
        setImageDescriptor(ImageCache.getImage(EImage.IMPORT.getPath()));
    }

    @Override
    protected void doRun() {
        FileDialog fd = new FileDialog(getShell(), SWT.OPEN);
        fd.setFilterExtensions(new String[] { "*.zip" }); //$NON-NLS-1$
        String fPath = fd.open();
        if (fPath != null) {
            DataClusterService dataClusterService = DataClusterService.getIntance();
            File tempFolder = IOUtil.getTempFolder();
            String tempFolderPath = tempFolder.getAbsolutePath();
            dataClusterService.unZipFile(fPath, tempFolderPath);
            String dName = dataClusterService.loadIndexFile(tempFolderPath);
            if (dName == null) {
                MessageDialog.openError(getShell(), Messages.Common_Error, Messages.ImportDataClusterAction_errorFormat);
                return;
            }
            tempFolderPath += File.separator + dName;
            //
            SelectServerDefDialog dialog = new SelectServerDefDialog(getShell());
            if (dialog.open() == IDialogConstants.OK_ID) {
                MDMServerDef serverDef = dialog.getSelectedServerDef();
                try {

                    TMDMService service = RepositoryWebServiceAdapter.getMDMService(serverDef);
                    service.ping(new WSPing(Messages.ImportDataClusterAction_importTitle));
                    if (!dataClusterService.isExistDataCluster(service, dName)) {
                        if (MessageDialog.openQuestion(getShell(), Messages.ImportDataClusterAction_createDataClusterTitle,
                                Messages.bind(Messages.ImportDataClusterAction_createConfirm, dName))) {
                            dataClusterService.createDataCluster(service, dName);
                        } else {
                            return;
                        }
                    }

                    IDataContentProcess process = dataClusterService.getNewImportContentProcess(serverDef, dName, tempFolderPath);
                    try {
                        process.run();

                    } catch (InterruptedException e) {
                        // do nothing
                        return;
                    }
                    MultiStatus multiStatus = process.getResult();
                    if (multiStatus != null && multiStatus.getChildren().length > 0) {
                        MultiStatusDialog statusDialog = new MultiStatusDialog(getShell(), multiStatus.getMessage(), multiStatus);
                        statusDialog.open();
                    } else {
                        // show success info
                        MessageDialog.openInformation(getShell(), Messages.ImportDataClusterAction_importTitle,
                                Messages.bind(Messages.ImportDataClusterAction_successImport, dName));
                    }
                } catch (XtentisException e) {
                    log.error(e.getMessage(), e);
                } catch (WebServiceException e) {
                    MessageDialog.openError(getShell(), Messages.ImportDataClusterAction_importTitle,
                            Messages.AbstractDataClusterAction_ConnectFailed);
                } finally {
                    IOUtil.cleanFolder(tempFolder);
                }

            }
        }
    }
}
