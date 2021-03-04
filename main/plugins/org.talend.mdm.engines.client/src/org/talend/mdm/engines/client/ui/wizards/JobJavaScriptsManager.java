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
package org.talend.mdm.engines.client.ui.wizards;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Platform;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.core.model.properties.ProcessItem;

/**
 * DOC smallet class global comment. Detailled comment <br/>
 *
 */
public class JobJavaScriptsManager extends org.talend.repository.ui.wizards.exportjob.scriptsmanager.JobJavaScriptsManager {

    public JobJavaScriptsManager(Map<ExportChoice, Object> exportChoiceMap, String contextName, String launcher,
            int statisticPort, int tracePort) {
        super(exportChoiceMap, contextName, launcher, statisticPort, tracePort);
    }

    protected List<URL> getLauncher(boolean needLauncher, ProcessItem process, String contextName, String environment,
            int statisticPort, int tracePort, String... codeOptions) {
        List<URL> list = new ArrayList<URL>();
        if (!needLauncher) {
            return list;
        }
        String processId = process.getProperty().getId();
        String windowsCmd = getCommandByTalendJob(Platform.OS_WIN32, processId, contextName, process.getProperty().getVersion(),
                statisticPort, tracePort, codeOptions);
        String unixCmd = getCommandByTalendJob(Platform.OS_LINUX, processId, contextName, process.getProperty().getVersion(),
                statisticPort, tracePort, codeOptions);
        String tmpFold = getTmpFolder();

        createLauncherFile(process, list, unixCmd, UNIX_LAUNCHER, tmpFold);
        createLauncherFile(process, list, windowsCmd, WINDOWS_LAUNCHER, tmpFold);
        return list;
    }

    /**
     * DOC Administrator Comment method "createLauncherFile".
     *
     * @param process
     * @param list
     * @param cmdPrimary
     * @param cmdSecondary
     * @param tmpFold
     */
    private void createLauncherFile(ProcessItem process, List<URL> list, String cmdPrimary, String fileName, String tmpFold) {
        PrintWriter pw = null;
        try {

            File file = new File(tmpFold, process.getProperty().getLabel() + "_" + fileName); //$NON-NLS-1$
            file.createNewFile();
            pw = new PrintWriter(new FileOutputStream(file));
            pw.print(cmdPrimary);
            pw.flush();
            list.add(file.toURL());
            pw.close();
        } catch (Exception e) {
            ExceptionHandler.process(e);
        } finally {
            try {
                if (pw != null) {
                    pw.close();
                }
            } catch (Exception e) {
                // do nothing here
            }
        }
    }

}
