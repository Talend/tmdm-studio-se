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
import org.talend.mdm.repository.core.datacontent.DataProcessRule;
import org.talend.mdm.repository.core.service.ConsoleProgressService;
import org.talend.mdm.repository.model.mdmmetadata.MDMServerDef;

/**
 * created by HHB on 2012-10-10 Detailled comment
 *
 */
public class ImportDataContentCommandProcess extends ImportDataContentProcess {

    /**
     * DOC HHB ImportDataContentCommandProcess constructor comment.
     *
     * @param serverDef
     * @param dName
     * @param tmpPath
     */
    public ImportDataContentCommandProcess(MDMServerDef serverDef, String dName, String tmpPath) {
        super(serverDef, dName, tmpPath);
    }

    @Override
    protected IProgressService getProcessService() {
        return ConsoleProgressService.getInstance();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.talend.mdm.repository.core.datacontent.impl.ImportDataContentProcess#tuneRule(org.talend.mdm.repository.core
     * .datacontent.DataProcessRule)
     */
    @Override
    public void tuneRule(DataProcessRule rule) throws InterruptedException {
        // do nothing to skip the popup dialog
    }

}
