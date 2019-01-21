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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.talend.mdm.bulkload.client.BulkloadClient;
import org.talend.mdm.bulkload.client.BulkloadOptions;
import org.talend.mdm.bulkload.client.InputStreamMerger;
import org.talend.mdm.repository.core.datacontent.DataEntityUnit;
import org.talend.mdm.repository.core.datacontent.DataProcessRule;
import org.talend.mdm.repository.core.datacontent.DataProcessRuleFactory;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.model.mdmmetadata.MDMServerDef;
import org.talend.mdm.repository.plugin.RepositoryPlugin;
import org.talend.mdm.repository.ui.dialogs.datacontent.DataProcessRuleDialog;

import com.amalto.workbench.service.IWebServiceHook;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.webservices.WSItem;

/**
 * created by HHB on 2012-10-9 Detailled comment
 * 
 */
public class ImportDataContentProcess extends AbstractDataContentProcess {

    private static Logger log = Logger.getLogger(ImportDataContentProcess.class);

    private MultiStatus processResult;

    private final MDMServerDef serverDef;

    private final String dName;

    private final String tmpPath;

    public ImportDataContentProcess(MDMServerDef serverDef, String dName, String tmpPath) {
        this.serverDef = serverDef;
        this.dName = dName;
        this.tmpPath = tmpPath;

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.mdm.repository.core.datacontent.IDataContentProcess#buildRule()
     */
    public DataProcessRule buildRule() throws InvocationTargetException {
        try {
            DataProcessRule rule = loadRuleFile();
            if (rule == null) {
                // try build it from current item files
                rule = DataProcessRuleFactory.createProcessRouterFromLocal(new File(tmpPath), dName);
            }
            return rule;
        } catch (Exception e) {
            throw new InvocationTargetException(e, Messages.ImportDataContentProcess_LoadListFileFailed);
        }

    }

    private DataProcessRule loadRuleFile() throws UnsupportedEncodingException, FileNotFoundException, MarshalException,
            ValidationException {
        DataProcessRule rule = null;
        File folder = new File(tmpPath);
        if (folder.exists()) {
            File file = new File(folder.getParent() + File.separator + RULE_FILE_NAME);
            if (file.exists()) {
                InputStreamReader reader = new InputStreamReader(new FileInputStream(file), "UTF-8");//$NON-NLS-1$
                rule = (DataProcessRule) Unmarshaller.unmarshal(DataProcessRule.class, reader);
            }
        }

        return rule;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.mdm.repository.core.datacontent.IDataContentProcess#tuneRule(org.talend.mdm.repository.core.datacontent
     * .DataProcessRule)
     */
    public void tuneRule(DataProcessRule rule) throws InterruptedException {
        Shell shell = Display.getCurrent().getActiveShell();
        DataProcessRuleDialog dialog = new DataProcessRuleDialog(shell, rule);

        if (dialog.open() == IDialogConstants.CANCEL_ID) {
            throw new InterruptedException();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.mdm.repository.core.datacontent.IDataContentProcess#getResult()
     */
    public MultiStatus getResult() {
        if (processResult == null) {
            processResult = new MultiStatus(RepositoryPlugin.PLUGIN_ID, IStatus.ERROR,
                    Messages.ImportDataClusterAction_errorTitle, null);
        }
        return processResult;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.mdm.repository.core.datacontent.IDataContentProcess#processDatas(org.talend.mdm.repository.core.
     * datacontent.DataProcessRule, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void processDatas(DataProcessRule rule) throws InterruptedException, InvocationTargetException {
        IProgressService progressService = getProcessService();
        ImportProcess process = new ImportProcess(rule);
        progressService.run(true, true, process);

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

    public class ImportProcess implements IRunnableWithProgress {

        private final DataProcessRule rule;

        /**
         * DOC hbhong ExportContentProcess constructor comment.
         * 
         * @param serverDef
         * @param dName
         * @param path
         */
        public ImportProcess(DataProcessRule rule) {
            super();
            this.rule = rule;

        }

        class RuleFilter implements FilenameFilter {

            private Pattern pattern;

            public RuleFilter(String concept) {
                String regex = dName + "\\." + concept + "\\.\\S+"; //$NON-NLS-1$ //$NON-NLS-2$
                pattern = Pattern.compile(regex);
            }

            /*
             * (non-Javadoc)
             * 
             * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
             */
            public boolean accept(File dir, String name) {
                return pattern.matcher(name).matches();

            }

        }

        private List<File> getFilesOrderByRule() {
            File folder = new File(tmpPath);
            List<File> files = new LinkedList<File>();
            if (folder.exists() && folder.isDirectory()) {
                for (DataEntityUnit unit : rule.returnSelectedEntityUnits()) {
                    RuleFilter ruleFilter = new RuleFilter(unit.getEntityName());
                    File[] filterFiles = folder.listFiles(ruleFilter);
                    for (File file : filterFiles) {
                        files.add(file);
                    }
                }
            }
            return files;
        }

        protected void importClusterContents(IProgressMonitor monitor) {

            String url = serverDef.getProtocol() + serverDef.getHost()
                    + ":" + serverDef.getPort() + Util.getContextPath(serverDef.getPath()) + "/services/bulkload"; //$NON-NLS-1$ //$NON-NLS-2$
            String userName = serverDef.getUser();
            String password = serverDef.getPasswd();
            Reader reader = null;

            List<File> files = getFilesOrderByRule();
            monitor.beginTask(Messages.ImportDataClusterAction_importProcessTitle, files.size() + 10);
            Map<String, List<String>> conceptMap = new LinkedHashMap<String, List<String>>();

            Unmarshaller unmarshaller = new Unmarshaller(WSItem.class);
            try {
                Mapping mapping = getWSItemMapping();
                unmarshaller.setWhitespacePreserve(true);
                unmarshaller.setMapping(mapping);
            } catch (Exception e) {
                log.error(e.getLocalizedMessage(), e);

                IStatus errStatus = new Status(IStatus.ERROR, RepositoryPlugin.PLUGIN_ID,
                        Messages.ImportDataClusterAction_errorTitle, e);
                getResult().add(errStatus);
                return;
            }

            //
            for (File file : files) {
                String concept = ""; //$NON-NLS-1$
                try {
                    reader = new InputStreamReader(new FileInputStream(file), "UTF-8");//$NON-NLS-1$
                    WSItem wsItem = (WSItem) unmarshaller.unmarshal(reader);
                    //
                    String key = wsItem.getWsDataClusterPK().getPk() + "##" + wsItem.getConceptName() + "##"//$NON-NLS-1$//$NON-NLS-2$
                            + wsItem.getDataModelName();
                    List<String> list = null;
                    list = conceptMap.get(key);
                    if (list == null) {
                        list = new ArrayList<String>();
                        conceptMap.put(key, list);
                    }
                    String content = wsItem.getContent();
                    list.add(content);
                } catch (Exception e) {
                    log.error(e.getLocalizedMessage(), e);
                    String msg = Messages.bind(Messages.ImportDataClusterAction_importErrorMsg, concept, dName,
                            e.getLocalizedMessage());
                    IStatus errStatus = new Status(IStatus.ERROR, RepositoryPlugin.PLUGIN_ID, msg, e);
                    getResult().add(errStatus);
                    return;
                } finally {
                    try {
                        if (reader != null) {
                            reader.close();
                        }
                    } catch (Exception e) {
                        // do nothing
                    }
                }
                monitor.worked(1);
            }
            // store the items to db using bulkloadclient
            for (Entry<String, List<String>> entry : conceptMap.entrySet()) {
                String[] keys = entry.getKey().split("##");//$NON-NLS-1$
                String cluster = keys[0];
                String concept = keys[1];
                String datamodel = keys[2];
                BulkloadClient bulkloadClient = new BulkloadClient(url, userName, password, cluster, concept, datamodel);
                bulkloadClient.setOptions(new BulkloadOptions(false, false, 500));
                IWebServiceHook webServiceHook = Util.getWebServiceHook();
                if (webServiceHook != null) {
                    String tokenKey = webServiceHook.getTokenKey();
                    String studioToken = webServiceHook.buildStudioToken(userName);
                    bulkloadClient.setToken(tokenKey, studioToken);
                }

                StringBuilder sb = new StringBuilder();
                for (String xml : entry.getValue()) {
                    sb.append(xml).append("\n"); //$NON-NLS-1$
                }
                try {
                    InputStreamMerger manager = bulkloadClient.load();
                    synchronized (manager) {
                        InputStream bin = new ByteArrayInputStream(sb.toString().getBytes("utf-8"));//$NON-NLS-1$
                        manager.push(bin);
                        manager.close();
                        while (!manager.isAlreadyProcessed()) {
                            manager.wait(100);
                        }
                        manager.notify();
                    }
                } catch (Exception e) {
                    log.error(e.getLocalizedMessage(), e);
                    String msg = Messages.bind(Messages.ImportDataClusterAction_importErrorMsg, concept, dName,
                            e.getLocalizedMessage());
                    IStatus errStatus = new Status(IStatus.ERROR, RepositoryPlugin.PLUGIN_ID, msg, e);
                    getResult().add(errStatus);
                }
                monitor.worked(1);
            }
            monitor.done();
            return;
        }

        public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
            importClusterContents(monitor);

        }
    }

}
