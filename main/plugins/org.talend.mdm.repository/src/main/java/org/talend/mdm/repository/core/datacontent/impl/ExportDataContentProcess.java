// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.repository.core.datacontent.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.WebServiceException;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.xml.Marshaller;
import org.talend.mdm.repository.core.datacontent.DataProcessRule;
import org.talend.mdm.repository.core.datacontent.DataProcessRuleFactory;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.plugin.RepositoryPlugin;
import org.talend.mdm.repository.utils.IOUtil;
import org.talend.repository.utils.ZipFileUtils;

import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.webservices.TMDMService;
import com.amalto.workbench.webservices.WSDataClusterPK;
import com.amalto.workbench.webservices.WSGetItem;
import com.amalto.workbench.webservices.WSGetItemPKsByCriteria;
import com.amalto.workbench.webservices.WSItem;
import com.amalto.workbench.webservices.WSItemPKsByCriteriaResponseResults;

/**
 * created by HHB on 2012-10-9 Detailled comment
 * 
 */
public class ExportDataContentProcess extends AbstractDataContentProcess {

    private static Logger log = Logger.getLogger(ExportDataContentProcess.class);

    private TMDMService service;

    private String tempFolderPath;

    private String dName;

    private String outputPath;

    public ExportDataContentProcess(TMDMService service, String tempFolderPath, String dName, String outputPath) {
        this.service = service;
        this.tempFolderPath = tempFolderPath;
        this.dName = dName;
        this.outputPath = outputPath;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.mdm.repository.core.datacontent.IDataContentProcess#processDatas(org.eclipse.core.runtime.IProgressMonitor
     * )
     */
    public void processDatas(DataProcessRule rule) throws InterruptedException, InvocationTargetException {
        IProgressService progressService = getProcessService();
        ExportProcess process = new ExportProcess(rule);
        progressService.run(true, true, process);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.mdm.repository.core.datacontent.IDataContentProcess#buildRule()
     */
    public DataProcessRule buildRule() throws InvocationTargetException {
        try {
            DataProcessRule rule = DataProcessRuleFactory.createProcessRouterFromRemote(service, dName);
            return rule;
        } catch (WebServiceException e) {
            throw new InvocationTargetException(e, Messages.ExportDataContentProcess_CanNotConnectServer);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.mdm.repository.core.datacontent.IDataContentProcess#tuneRule(org.talend.mdm.repository.core.datacontent
     * .DataProcessRule)
     */
    public void tuneRule(final DataProcessRule rule) throws InterruptedException {
        if (rule.getEntityUnits().size() > 0) {
            saveRuleFile(rule);
        }
    }

    protected MultiStatus processResult;

    private boolean saveRuleFile(DataProcessRule rule) {
        if (rule != null) {
            File file = new File(tempFolderPath + File.separator + RULE_FILE_NAME);
            try {
                OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");//$NON-NLS-1$
                Marshaller.marshal(rule, writer);
                return true;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.mdm.repository.core.datacontent.IDataContentProcess#getResult()
     */
    public MultiStatus getResult() {
        if (processResult == null) {
            processResult = new MultiStatus(RepositoryPlugin.PLUGIN_ID, IStatus.ERROR, Messages.ExportObjectAction_error, null);
        }
        return processResult;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.mdm.repository.core.datacontent.impl.AbstractDataContentProcess#getProcessService()
     */
    @Override
    protected IProgressService getProcessService() {
        return PlatformUI.getWorkbench().getProgressService();
    }

    public class ExportProcess implements IRunnableWithProgress {

        private DataProcessRule rule;

        /**
         * DOC hbhong Comment method "zipFile".
         * 
         * @param tempFolderPath
         * @param fPath
         */
        private void zipFile(String tempFolderPath, String fPath, IProgressMonitor monitor) {
            monitor.subTask(Messages.AbstractDataClusterAction_zipContentFile);
            ZipFileUtils.zip(tempFolderPath, fPath, false);
            monitor.worked(5);
        }

        /**
         * DOC hbhong ExprocessContentProcess constructor comment.
         * 
         * @param service
         * @param tempFolderPath
         * @param dName
         */
        public ExportProcess(DataProcessRule rule) {
            this.rule = rule;

        }

        protected int exportCluster(TMDMService service, String tempFolderPath, String dName, IProgressMonitor monitor) {

            String encodedID = null;
            WSDataClusterPK pk = new WSDataClusterPK(dName);
            try {
                List<String> items = new ArrayList<String>();
                List<WSItemPKsByCriteriaResponseResults> results = service.getItemPKsByCriteria(
                        new WSGetItemPKsByCriteria(null, null, (long) -1, null, null, MAX_EXPORT_COUNT, 0, (long) -1, pk))
                        .getResults();

                if (results == null) {
                    return -1;
                }
                monitor.beginTask(Messages.ExportDataClusterAction_exportContent, results.size() + 10);
                monitor.subTask(Messages.ExportDataClusterAction_exporting);
                int totalSize = 0;
                int maxSize = results.size() - 1;
                if (maxSize > 0) {
                    totalSize = Integer.parseInt(Util.parse(results.get(0).getWsItemPK().getConceptName()).getDocumentElement()
                            .getTextContent());
                    if (maxSize > MAX_EXPORT_COUNT) {
                        maxSize = MAX_EXPORT_COUNT;
                    }
                }
                // Marshaller.marshal(wsitem, sw);
                // replace with following to resolve serialize List type problem
                Mapping mapping = getWSItemMapping();

                for (int i = 1; i <= maxSize; i++) {
                    WSItemPKsByCriteriaResponseResults item = results.get(i);
                    WSItem wsitem = service.getItem(new WSGetItem(item.getWsItemPK()));

                    // Marshal
                    StringWriter sw = new StringWriter();
                    Marshaller marshaller = new Marshaller(sw);
                    marshaller.setMapping(mapping);
                    marshaller.marshal(wsitem);
                    //
                    String uniqueId = pk.getPk() + "." + wsitem.getConceptName();//$NON-NLS-1$
                    for (String id : wsitem.getIds()) {
                        uniqueId = uniqueId + "." + id;//$NON-NLS-1$
                    }
                    encodedID = URLEncoder.encode(uniqueId, "UTF-8");//$NON-NLS-1$
                    writeString(tempFolderPath, sw.toString(), pk.getPk() + "/" + encodedID);//$NON-NLS-1$
                    items.add(TreeObject.DATACONTAINER_COTENTS + "/" + pk.getPk() + "/" + encodedID);//$NON-NLS-1$//$NON-NLS-2$
                    monitor.worked(1);
                }
                return totalSize;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            return -1;
        }

        public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
            final int totalSize = exportCluster(service, tempFolderPath, dName, monitor);
            if (totalSize == -1) {
                throw new InvocationTargetException(new RuntimeException(Messages.ExportDataContentProcess_exportContentError));
            }

            zipFile(tempFolderPath, outputPath, monitor);
            IOUtil.cleanFolder(new File(tempFolderPath));
            monitor.done();
            if (totalSize >= MAX_EXPORT_COUNT) {
                Display.getDefault().asyncExec(new Runnable() {

                    public void run() {
                        MessageDialog.openWarning(Display.getDefault().getActiveShell(), Messages.Common_Warning, Messages.bind(
                                Messages.ExportDataContentProcess_greaterThanMaxExportLimit, totalSize, MAX_EXPORT_COUNT));
                    }
                });

            }
        }

        protected void writeString(String exportFolder, String outputStr, String filename) {

            File f = new File(exportFolder + "/" + filename + ".txt");//$NON-NLS-1$

            if (!f.getParentFile().exists()) {
                f.getParentFile().mkdir();
            }
            FileWriter fo = null;
            try {
                fo = new FileWriter(f);
                //            IOUtils.write(outputStr, fo, "UTF-8");//$NON-NLS-1$
                fo.write(outputStr);
                fo.flush();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            } finally {
                if (fo != null) {
                    try {
                        fo.close();
                    } catch (IOException e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }

        }

    }
}
