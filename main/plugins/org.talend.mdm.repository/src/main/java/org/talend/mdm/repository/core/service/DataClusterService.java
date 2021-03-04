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
package org.talend.mdm.repository.core.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.xml.ws.WebServiceException;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.talend.mdm.repository.core.datacontent.IDataContentProcess;
import org.talend.mdm.repository.core.datacontent.impl.ExportDataContentCommandProcess;
import org.talend.mdm.repository.core.datacontent.impl.ExportDataContentProcess;
import org.talend.mdm.repository.core.datacontent.impl.ImportDataContentCommandProcess;
import org.talend.mdm.repository.core.datacontent.impl.ImportDataContentProcess;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.model.mdmmetadata.MDMServerDef;
import org.talend.repository.utils.ZipFileUtils;

import com.amalto.workbench.webservices.TMDMService;
import com.amalto.workbench.webservices.WSBoolean;
import com.amalto.workbench.webservices.WSDataCluster;
import com.amalto.workbench.webservices.WSDataClusterPK;
import com.amalto.workbench.webservices.WSExistsDataCluster;
import com.amalto.workbench.webservices.WSPutDataCluster;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class DataClusterService {

    private static final String INDEX_FILE_NAME = "DataCluster.list"; //$NON-NLS-1$

    private static DataClusterService intance = new DataClusterService();

    private static Logger log = Logger.getLogger(DataClusterService.class);

    private static final String PROP_DATA_CLUSTER_NAME = "DataClusterNames"; //$NON-NLS-1$

    public static DataClusterService getIntance() {
        return intance;
    }

    private DataClusterService() {
    }

    public boolean createDataCluster(TMDMService service, String dName) {
        try {
            WSPutDataCluster wsDataCluster = new WSPutDataCluster(new WSDataCluster(dName, "", "")); //$NON-NLS-1$ //$NON-NLS-2$
            service.putDataCluster(wsDataCluster);
            return true;
        } catch (WebServiceException ex) {
            log.error(ex.getMessage(), ex);
        }
        return false;
    }

    private String getIndexFilePath(String folderPath) {
        return folderPath + File.separator + INDEX_FILE_NAME;
    }

    public IDataContentProcess getNewImportContentProcess(MDMServerDef serverDef, String dName, String path) {
        return new ImportDataContentProcess(serverDef, dName, path);
    }

    public IDataContentProcess getNewImportContentCommandProcess(MDMServerDef serverDef, String dName, String path) {
        return new ImportDataContentCommandProcess(serverDef, dName, path);
    }

    public IDataContentProcess getNewExportContentProcess(TMDMService service, String tempFolderPath, String dName, String fPath) {
        return new ExportDataContentProcess(service, tempFolderPath, dName, fPath);
    }

    public IDataContentProcess getNewExportContentCommandProcess(TMDMService service, String tempFolderPath, String dName,
            String fPath) {
        return new ExportDataContentCommandProcess(service, tempFolderPath, dName, fPath);
    }

    public boolean isExistDataCluster(TMDMService service, String dName) {
        WSExistsDataCluster wsExistsDataCluster = new WSExistsDataCluster(new WSDataClusterPK(dName));
        WSBoolean wsBoolean = service.existsDataCluster(wsExistsDataCluster);
        return wsBoolean.isTrue();
    }

    public String loadIndexFile(String folderPath) {
        Properties prop = new Properties();
        FileInputStream fi = null;
        try {
            fi = new FileInputStream(getIndexFilePath(folderPath));
            prop.load(fi);
            return prop.getProperty(PROP_DATA_CLUSTER_NAME);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return null;
        } finally {
            if (fi != null) {
                try {
                    fi.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    public void storeIndexFile(String folderPath, String dName) {
        Properties prop = new Properties();
        prop.setProperty(PROP_DATA_CLUSTER_NAME, dName);
        FileOutputStream fo = null;
        try {
            fo = new FileOutputStream(getIndexFilePath(folderPath));
            prop.store(fo, ""); //$NON-NLS-1$
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

    public void unZipFile(String fPath, String tempFolderPath) {
        ZipFileUtils.unZip(fPath, tempFolderPath);
    }

    /**
     * DOC hbhong Comment method "zipFile".
     *
     * @param tempFolderPath
     * @param fPath
     */
    public void zipFile(String tempFolderPath, String fPath, IProgressMonitor monitor) {
        monitor.subTask(Messages.AbstractDataClusterAction_zipContentFile);
        ZipFileUtils.zip(tempFolderPath, fPath, false);
        monitor.worked(5);
    }
}
