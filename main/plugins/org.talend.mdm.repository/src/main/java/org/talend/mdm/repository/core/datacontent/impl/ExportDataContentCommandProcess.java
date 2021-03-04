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
package org.talend.mdm.repository.core.datacontent.impl;

import org.eclipse.ui.progress.IProgressService;
import org.talend.mdm.repository.core.service.ConsoleProgressService;

import com.amalto.workbench.webservices.TMDMService;

/**
 * created by HHB on 2012-10-10 Detailled comment
 *
 */
public class ExportDataContentCommandProcess extends ExportDataContentProcess {

    /**
     * DOC HHB ExportDataContentCommandProcess constructor comment.
     *
     * @param port
     * @param tempFolderPath
     * @param dName
     * @param outputPath
     */
    public ExportDataContentCommandProcess(TMDMService service, String tempFolderPath, String dName, String outputPath) {
        super(service, tempFolderPath, dName, outputPath);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.talend.mdm.repository.core.datacontent.impl.ExportDataContentProcess#getProcessService()
     */
    @Override
    protected IProgressService getProcessService() {
        return ConsoleProgressService.getInstance();
    }

}
