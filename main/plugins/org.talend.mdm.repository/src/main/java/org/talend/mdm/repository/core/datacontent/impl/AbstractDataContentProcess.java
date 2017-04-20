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
package org.talend.mdm.repository.core.datacontent.impl;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.progress.IProgressService;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.talend.mdm.repository.core.datacontent.DataProcessRule;
import org.talend.mdm.repository.core.datacontent.IDataContentProcess;
import org.talend.mdm.repository.plugin.RepositoryPlugin;

import com.amalto.workbench.webservices.WSItem;

/**
 * created by HHB on 2012-10-9 Detailled comment
 * 
 */
public abstract class AbstractDataContentProcess implements IDataContentProcess {

    protected abstract IProgressService getProcessService();

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.mdm.repository.core.datacontent.IDataContentProcess#run(org.eclipse.core.runtime.IProgressMonitor)
     */
    public void run() throws InterruptedException {
        DataProcessRule exportRule;
        try {
            exportRule = buildRule();
            tuneRule(exportRule);
            processDatas(exportRule);
        }

        catch (InvocationTargetException ex) {
            IStatus errStatus = new Status(IStatus.ERROR, RepositoryPlugin.PLUGIN_ID, ex.getMessage(), ex.getCause());
            getResult().add(errStatus);
            return;
        }

    }

    protected Mapping getWSItemMapping() throws IOException, MappingException {

        URL mappingUrl = this.getClass().getResource("mapping.xml"); //$NON-NLS-1$
        Mapping mapping = new Mapping(WSItem.class.getClassLoader());
        mapping.loadMapping(mappingUrl);
        return mapping;

    }
}
