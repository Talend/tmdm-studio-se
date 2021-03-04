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
package org.talend.mdm.workbench.serverexplorer.console;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.internal.console.IOConsolePage;
import org.eclipse.ui.part.IPageBookViewPage;
import org.talend.mdm.repository.model.mdmmetadata.MDMServerDef;
import org.talend.mdm.workbench.serverexplorer.core.ServerDefService;
import org.talend.mdm.workbench.serverexplorer.i18n.Messages;
import org.talend.mdm.workbench.serverexplorer.plugin.MDMServerExplorerPlugin;
import org.talend.mdm.workbench.serverexplorer.ui.dialogs.DownloadLogDialog;

import com.amalto.workbench.utils.HttpClientUtil;
import com.amalto.workbench.utils.Util;

/**
 * created by Karelun Huang on Mar 19, 2013 Detailled comment
 *
 */
public abstract class MDMServerMessageConsole extends MessageConsole implements IPropertyChangeListener {

    private static final Log LOG = LogFactory.getLog(MDMServerMessageConsole.class);

    private static final int START_FROM_TAIL = -1;

    private static final int HTTP_STATUS_OK = 200;

    private static final int HTTP_STATUS_NO_ACCESS = 401;

    private static final int HTTP_STSTUS_FORBIDDEN = 403;

    private static final int HTTP_STATUS_NOT_FOUND = 404;

    private static final ImageDescriptor DOWNLOAD_IMG = MDMServerExplorerPlugin.imageDescriptorFromPlugin(
            MDMServerExplorerPlugin.PLUGIN_ID, "icons/download.png"); //$NON-NLS-1$;

    private static final ImageDescriptor PAUSED_IMG = MDMServerExplorerPlugin.imageDescriptorFromPlugin(
            MDMServerExplorerPlugin.PLUGIN_ID, "icons/pause.gif"); //$NON-NLS-1$;

    private static final ImageDescriptor RESUMED_IMG = MDMServerExplorerPlugin.imageDescriptorFromPlugin(
            MDMServerExplorerPlugin.PLUGIN_ID, "icons/resume.gif"); //$NON-NLS-1$;

    private static final ImageDescriptor RELOAD_IMG = MDMServerExplorerPlugin.imageDescriptorFromPlugin(
            MDMServerExplorerPlugin.PLUGIN_ID, "icons/refresh.gif"); //$NON-NLS-1$;

    private static final ImageDescriptor CLOSE_IMG = MDMServerExplorerPlugin.imageDescriptorFromPlugin(
            MDMServerExplorerPlugin.PLUGIN_ID, "icons/rem_co.gif"); //$NON-NLS-1$;

    public class TerminateConsoleAction extends Action {

        public TerminateConsoleAction() {
            super(Messages.MDMServerMessageConsole_closeActionLabel);
            setImageDescriptor(CLOSE_IMG);
        }

        @Override
        public void run() {
            ConsolePlugin.getDefault().getConsoleManager().removeConsoles(new IConsole[] { MDMServerMessageConsole.this });
        }

    }

    public class DownloadAction extends Action {

        public DownloadAction() {
            super(Messages.MDMServerMessageConsole_DownloadAction_Text);
            setImageDescriptor(DOWNLOAD_IMG);
        }

        @Override
        public void run() {
            DownloadLogDialog d = new DownloadLogDialog(getShell());
            int ret = d.open();
            if (ret != IDialogConstants.OK_ID) {
                return;
            }
            final String filePath = d.getDirectoryPath();
            final boolean needOpen = d.needOpen();
            ProgressMonitorDialog pmd = new ProgressMonitorDialog(getShell());
            try {
                pmd.run(false, true, new IRunnableWithProgress() {

                    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                        download(filePath, needOpen, monitor);
                        monitor.done();
                    }
                });
            } catch (InvocationTargetException e) {
                LOG.error(e.getMessage(), e);
            } catch (InterruptedException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    public class MonitorAction extends Action {

        /**
         * false: Puased. true: Resumed.
         */
        private boolean isPaused = false;

        public MonitorAction() {
            update();
        }

        @Override
        public void run() {
            isPaused = !isPaused;
            pauseOrResume(isPaused);
            update();
        }

        public void initStatus() {
            isPaused = false;
            update();
        }

        private void update() {
            if (isPaused) {
                setText(Messages.MDMServerMessageConsole_MonitorAction_ResumeText);
                setImageDescriptor(RESUMED_IMG);
            } else {
                setText(Messages.MDMServerMessageConsole_MonitorAction_PauseText);
                setImageDescriptor(PAUSED_IMG);
            }
        }
    }

    public class ReloadAction extends Action {

        public ReloadAction() {
            super(Messages.MDMServerMessageConsole_ReloadAction_Text);
            setImageDescriptor(RELOAD_IMG);
        }

        @Override
        public void run() {
            reload();
            if (monitorAction != null) {
                monitorAction.initStatus();
            }
        }

        private void reload() {
            refreshServerDef();
            disposeTimer();
            display();
        }

        private void refreshServerDef() {
            MDMServerDef newServerDef = ServerDefService.findServerDefByName(getServerDef().getName());
            setServerDef(newServerDef);
        }
    }

    private MonitorAction monitorAction = null;

    private DownloadAction downloadAction = null;

    private ReloadAction reloadAction = null;

    private MDMServerDef serverDef = null;

    /**
     * Getter for serverDef.
     *
     * @return the serverDef
     */
    public MDMServerDef getServerDef() {
        return this.serverDef;
    }

    private Timer timer = null;

    private int position = START_FROM_TAIL;

    private TerminateConsoleAction terminateConsoleAction;

    /**
     * Getter for terminateConsoleAction.
     *
     * @return the terminateConsoleAction
     */
    public TerminateConsoleAction getTerminateConsoleAction() {
        return this.terminateConsoleAction;
    }

    public MDMServerMessageConsole(MDMServerDef serverDef) {
        this(Messages.MDMServerMessageConsole_Name, null);
        this.serverDef = serverDef;
        initMessageConsole();
        initActions();
        PlatformUI.getPreferenceStore().addPropertyChangeListener(this);
    }

    public MDMServerMessageConsole(String name, ImageDescriptor imageDescriptor) {
        super(name, imageDescriptor);
    }

    public void setServerDef(MDMServerDef serverDef) {
        this.serverDef = serverDef;
        initMessageConsole();
    }

    private void initMessageConsole() {
        String name = getConsoleTitle();
        setName(name);
        initWaterMarks();
    }

    private void initActions() {
        reloadAction = new ReloadAction();
        monitorAction = new MonitorAction();
        downloadAction = new DownloadAction();
        terminateConsoleAction = new TerminateConsoleAction();
    }

    private void initWaterMarks() {
        int buffer = MDMServerPreferenceService.getDisplayedBuffer();
        setWaterMarks(buffer, buffer + 100);
    }

    @Override
    public IPageBookViewPage createPage(IConsoleView view) {
        IOConsolePage consolePage = new IOConsolePage(this, view) {

            @Override
            protected void contextMenuAboutToShow(IMenuManager menuManager) {
                super.contextMenuAboutToShow(menuManager);
                menuManager.add(new Separator());
                menuManager.add(reloadAction);
                menuManager.add(monitorAction);
                menuManager.add(downloadAction);
                menuManager.add(terminateConsoleAction);
            }

            @Override
            public void dispose() {
                disposeTimer();
                PlatformUI.getPreferenceStore().removePropertyChangeListener(MDMServerMessageConsole.this);
                removeFromCache(serverDef.getName());
                ConsolePlugin.getDefault().getConsoleManager().removeConsoles(new IConsole[] { MDMServerMessageConsole.this });
                super.dispose();
            }
        };
        consolePage.setReadOnly();
        return consolePage;
    }

    public void reload() {
        if (reloadAction != null) {
            reloadAction.run();
        }
    }

    @Override
    public void activate() {
        super.activate();
        display();
    }

    private void display() {
        position = START_FROM_TAIL;
        clearConsole();

        monitor();
    }

    private synchronized void monitor() {
        if (timer == null) {
            timer = new Timer(true);
        }
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                doMonitor();
            }
        }, 0, getRefrehFrequency());
    }

    private HttpResponse executeByHttpget(String url, String userName, String password) {
        HttpGet httpGet = new HttpGet(url);
        HttpClientUtil.wrapHttpRequest(httpGet, userName, password);
        HttpClientUtil.addStudioToken(httpGet, serverDef.getUser());
        DefaultHttpClient httpClient = createHttpClient();

        HttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
        } catch (ClientProtocolException e) {
            LOG.error(e.getMessage(), e);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }

        return response;
    }

    private void doMonitor() {

        String monitorURL = buildMonitorURL(position);

        MessageConsoleStream errorMsgStream = newErrorMessageStream();
        InputStream is = null;
        BufferedReader br = null;
        MessageConsoleStream msgStream = null;
        InputStreamReader isr = null;
        try {
            HttpResponse response = executeByHttpget(monitorURL, serverDef.getUser(), serverDef.getPasswd());
            int code = response.getStatusLine().getStatusCode();
            if (HTTP_STATUS_OK == code) {
                modifyChunkedPosition(response);
                if (isEndOfChunk(response)) {
                    return;
                }
                is = response.getEntity().getContent();
                msgStream = newMessageStream();

                isr = new InputStreamReader(is);
                br = new BufferedReader(isr);
                String line = null;
                while ((line = br.readLine()) != null) {
                    msgStream.println(line);
                }
            } else {

                if (HTTP_STATUS_NO_ACCESS == code) {
                    errorMsgStream.println(Messages.MDMServerMessageConsole_No_Acess_Message);
                    disposeTimer();
                } else if (HTTP_STSTUS_FORBIDDEN == code) {
                    errorMsgStream.println(Messages.MDMServerMessageConsole_Forbidden_Message);
                    disposeTimer();
                } else if (HTTP_STATUS_NOT_FOUND == code) {
                    errorMsgStream.println(Messages.MDMServerMessageConsole_NotConnected_Message);
                    disposeTimer();
                }
            }
        } catch (HttpHostConnectException ex) {
            errorMsgStream.println(ex.getMessage());
            disposeTimer();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            errorMsgStream.println(e.getMessage());
            disposeTimer();
        } catch (SecurityException e) {
            errorMsgStream.println(e.getMessage());
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
            if (msgStream != null) {
                try {
                    msgStream.close();
                } catch (IOException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
            if (errorMsgStream != null) {
                try {
                    errorMsgStream.close();
                } catch (IOException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        }
    }

    private int getRefrehFrequency() {
        return MDMServerPreferenceService.getRefrehFrequency() * 1000;
    }

    private void modifyChunkedPosition(HttpResponse response) {
        Header[] headers = response.getHeaders("X-Log-Position"); //$NON-NLS-1$
        Assert.isTrue(headers.length > 0);
        String value = headers[0].getValue();
        position = Integer.parseInt(value);
    }

    private boolean isEndOfChunk(HttpResponse response) {
        Header[] headers = response.getHeaders("Content-Length"); //$NON-NLS-1$
        if (headers == null || headers.length == 0) {
            return false;
        }
        String value = headers[0].getValue();
        return "0".equals(value); //$NON-NLS-1$
    }

    private DefaultHttpClient createHttpClient() {
        DefaultHttpClient httpclient = new DefaultHttpClient();

        AuthScope authScope = new AuthScope(serverDef.getHost(), Integer.parseInt(serverDef.getPort()));
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(serverDef.getUser(), serverDef.getPasswd());
        if (httpclient != null) {
            httpclient.getCredentialsProvider().setCredentials(authScope, credentials);
        }
        return httpclient;
    }

    private String buildMonitorURL(int pos) {
        String buildURL = buildURL();
        buildURL += "?position=" + pos; //$NON-NLS-1$
        return buildURL;
    }

    private String buildURL() {
        StringBuilder sb = new StringBuilder();
        sb.append(serverDef.getProtocol());
        sb.append(serverDef.getHost());
        sb.append(":"); //$NON-NLS-1$
        sb.append(serverDef.getPort());
        sb.append(Util.getContextPath(serverDef.getPath()));
        sb.append(getLogPath());

        return sb.toString();
    }

    private void pauseOrResume(boolean isPaused) {
        if (isPaused) {
            disposeTimer();
        } else {
            monitor();
        }
    }

    private Shell getShell() {
        return Display.getDefault().getActiveShell();
    }

    private void download(String dirPath, boolean needOpen, IProgressMonitor monitor) {
        monitor.beginTask(Messages.MDMServerMessageConsole_DownloadTask_Name, 100);
        InputStream is = null;
        OutputStream os = null;
        try {
            String monitorURL = buildURL();

            HttpResponse response = executeByHttpget(monitorURL, serverDef.getUser(), serverDef.getPasswd());
            monitor.worked(40);
            int code = response.getStatusLine().getStatusCode();

            if (HTTP_STATUS_OK == code) {

                String fileName = getFileName(response);
                is = response.getEntity().getContent();
                monitor.worked(60);
                File file = new File(dirPath + File.separator + fileName);
                if (!file.getCanonicalPath().startsWith(dirPath)) {
                    LOG.warn("Get invalid log filename '" + fileName + "' from url '" + monitorURL
                            + "' when downloading server log");
                    fileName = getLogFlag() + ".log";
                    file = new File(dirPath + File.separator + fileName);
                }

                os = new FileOutputStream(file);
                IOUtils.copy(is, os);
                monitor.worked(85);

                if (needOpen) {
                    Program.launch(file.getAbsolutePath());
                }
                monitor.worked(90);
            } else {
                newErrorMessageStream().println(Messages.MDMServerMessageConsole_DownloadFailed_Message);
                if (HTTP_STATUS_NO_ACCESS == code) {
                    newErrorMessageStream().println(Messages.MDMServerMessageConsole_No_Acess_Message);
                } else if (HTTP_STSTUS_FORBIDDEN == code) {
                    newErrorMessageStream().println(Messages.MDMServerMessageConsole_Forbidden_Message);
                } else if (HTTP_STATUS_NOT_FOUND == code) {
                    newErrorMessageStream().println(Messages.MDMServerMessageConsole_NotConnected_Message);
                }
                monitor.worked(90);
            }
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            newErrorMessageStream().println(Messages.MDMServerMessageConsole_DownloadFailed_Message);
            newErrorMessageStream().println(e.getMessage());
            monitor.worked(90);
        } catch (SecurityException e) {
            MessageDialog.openError(getShell(), Messages.MDMServerMessageConsole_error, e.getMessage());
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        }
    }

    private String getFileName(HttpResponse response) {
        Header[] headers = response.getHeaders("Content-Disposition"); //$NON-NLS-1$
        Assert.isTrue(headers.length > 0);
        String value = headers[0].getValue();
        int begin = value.indexOf("\""); //$NON-NLS-1$
        int end = value.lastIndexOf("\""); //$NON-NLS-1$
        return value.substring(begin + 1, end);
    }

    private void disposeTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private MessageConsoleStream newErrorMessageStream() {
        final MessageConsoleStream msgStream = newMessageStream();
        ConsolePlugin.getStandardDisplay().asyncExec(new Runnable() {

            public void run() {
                msgStream.setColor(Display.getDefault().getSystemColor(SWT.COLOR_RED));
            }
        });
        return msgStream;
    }

    public void propertyChange(PropertyChangeEvent event) {
        if (event.getProperty().equals(MDMServerPreferenceService.REFRESH_FREQ)) {
            disposeTimer();
            monitor();
        } else if (event.getProperty().equals(MDMServerPreferenceService.DISPLAY_BUFFER_SIZE)) {
            initWaterMarks();
        }
    }

    public ReloadAction getReloadAction() {
        return this.reloadAction;
    }

    public MonitorAction getMonitorAction() {
        return this.monitorAction;
    }

    public DownloadAction getDownloadAction() {
        return this.downloadAction;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof MDMServerMessageConsole)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        MDMServerMessageConsole otherConsole = (MDMServerMessageConsole) obj;
        return serverDef == otherConsole.getServerDef();
    }

    /*
     * return a string that does not contains double dot, also can be used as a file name
     */
    protected abstract String getLogFlag();

    protected abstract String getLogPath();

    protected abstract String getConsoleTitle();

    protected abstract void removeFromCache(String serverName);
}
