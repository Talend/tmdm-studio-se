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
package org.talend.mdm.repository.ui.actions.datacontainer;

import java.io.File;
import java.util.List;

import javax.xml.ws.WebServiceException;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.datacontent.IDataContentProcess;
import org.talend.mdm.repository.core.service.DataClusterService;
import org.talend.mdm.repository.core.service.RepositoryWebServiceAdapter;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.model.mdmmetadata.MDMServerDef;
import org.talend.mdm.repository.model.mdmproperties.MDMServerObjectItem;
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
public class ExportDataClusterAction extends AbstractDataClusterAction {

    private static Logger log = Logger.getLogger(ExportDataClusterAction.class);

    /**
     * DOC hbhong ExportDataClusterAction constructor comment.
     *
     * @param text
     */
    public ExportDataClusterAction() {
        super(Messages.ExportDataClusterAction_title);
        setImageDescriptor(ImageCache.getImage(EImage.EXPORT.getPath()));
    }

    @Override
    protected void doRun() {
        List<Object> selectedObject = getSelectedObject();
        if (!selectedObject.isEmpty()) {
            IRepositoryViewObject viewObj = (IRepositoryViewObject) selectedObject.get(0);
            SelectServerDefDialog dialog = new SelectServerDefDialog(getShell());
            if (dialog.open() == IDialogConstants.OK_ID) {
                MDMServerDef serverDef = dialog.getSelectedServerDef();

                MDMServerObjectItem item = (MDMServerObjectItem) viewObj.getProperty().getItem();
                String dName = item.getMDMServerObject().getName();

                try {
                    FileDialog fd = new FileDialog(getShell(), SWT.SAVE);
                    fd.setFilterExtensions(new String[] { "*.zip" }); //$NON-NLS-1$
                    String fPath = fd.open();
                    if (fPath != null) {
                        TMDMService service = RepositoryWebServiceAdapter.getMDMService(serverDef);
                        service.ping(new WSPing(Messages.ExportDataClusterAction_exportContent));
                        DataClusterService dataClusterService = DataClusterService.getIntance();
                        if (dataClusterService.isExistDataCluster(service, dName)) {
                            File tempFolder = IOUtil.getTempFolder();
                            String tempFolderPath = tempFolder.getAbsolutePath();
                            dataClusterService.storeIndexFile(tempFolderPath, dName);

                            //

                            IDataContentProcess process = dataClusterService.getNewExportContentProcess(service, tempFolderPath,
                                    dName, fPath);
                            try {
                                process.run();
                            } catch (InterruptedException e) {
                                // do nothing
                                return;
                            }

                            MultiStatus status = process.getResult();
                            if (status != null && status.getChildren().length > 0) {
                                MessageDialog.openError(getShell(), Messages._Error,
                                        Messages.bind(Messages.ExportDataClusterAction_failedExportContent, dName));
                            } else {
                                MessageDialog.openInformation(getShell(), Messages.ExportDataClusterAction_exportContent,
                                        Messages.bind(Messages.ExportDataClusterAction_successExport, dName));
                            }
                        } else {
                            MessageDialog.openInformation(getShell(), Messages.Common_Warning,
                                    Messages.bind(Messages.ExportDataClusterAction_noContainerFound, dName));
                        }
                    }
                } catch (XtentisException e) {
                    log.error(e.getMessage(), e);
                } catch (WebServiceException e) {
                    MessageDialog.openError(getShell(), Messages.ExportDataClusterAction_exportContent,
                            Messages.AbstractDataClusterAction_ConnectFailed);
                }
            }
        }
    }
}
