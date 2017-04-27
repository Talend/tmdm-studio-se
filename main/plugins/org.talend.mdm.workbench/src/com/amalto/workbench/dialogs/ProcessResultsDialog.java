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
package com.amalto.workbench.dialogs;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.VerticalRuler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;
import org.eclipse.ui.internal.browser.WebBrowserPreference;
import org.talend.mdm.webservice.WSExtractedContent;

import com.amalto.workbench.editors.TransformerMainPage;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.utils.FileProvider;

public class ProcessResultsDialog extends Dialog {

    private static Log log = LogFactory.getLog(ProcessResultsDialog.class);

    private final static int BUTTON_CLOSE = 10;

    private static final String DEFAULT_DISPLAY_TEXT = Messages.ProcessResultsDialog_DefaultDisplayText;

    protected Combo variablesCombo;

    protected SourceViewer variablesViewer;

    protected String title;

    protected TreeMap<String, WSExtractedContent> resultsMap = null;

    /**
     * @param parentShell
     */
    public ProcessResultsDialog(Shell parentShell, String title, TreeMap<String, WSExtractedContent> map) {
        super(parentShell);
        this.title = title;
        this.resultsMap = map;
        setShellStyle(getShellStyle() | SWT.RESIZE | SWT.MAX);
    }

    @Override
    protected Control createDialogArea(Composite parent) {

        try {

            parent.getShell().setText(title);

            Composite composite = (Composite) super.createDialogArea(parent);
            GridLayout layout = (GridLayout) composite.getLayout();
            layout.numColumns = 2;
            ((GridData) composite.getLayoutData()).widthHint = 800;

            Label variableLabel = new Label(composite, SWT.NONE);
            variableLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1));
            variableLabel.setText(Messages.ProcessResultsDialog_PipelineVariables);

            variablesCombo = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
            variablesCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));

            /*
             * variablesCombo.addKeyListener( new KeyListener() { public void keyPressed(KeyEvent e) {} public void
             * keyReleased(KeyEvent e) { if ((e.stateMask==0) && (e.character == SWT.CR)) {
             * ProcessResultsPage.this.variablesViewer.setDocument(new Document(getText(variablesCombo.getText()))); }
             * }//keyReleased }//keyListener );
             */

            variablesViewer = new SourceViewer(composite, new VerticalRuler(10), SWT.V_SCROLL | SWT.H_SCROLL);
            variablesViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 2, 1));
            variablesViewer.configure(new TextSourceViewerConfiguration());
            ((GridData) variablesViewer.getControl().getLayoutData()).minimumHeight = 500;

            final Button seeInBrowser = new Button(composite, SWT.PUSH);
            seeInBrowser.setText(Messages.ProcessResultsDialog_display);
            seeInBrowser.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    String htmlContent = variablesViewer.getTextWidget().getText();
                    IFile file = FileProvider.createdTempFile(htmlContent, Messages.ProcessResultsDialog_temphtml, null);

                    File htmlFile = file.getLocation().toFile();
                    String SHARED_ID = "org.eclipse.ui.browser"; //$NON-NLS-1$
                    try {
                        IWorkbenchBrowserSupport support = PlatformUI.getWorkbench().getBrowserSupport();

                        if (WebBrowserPreference.getBrowserChoice() == WebBrowserPreference.INTERNAL) {
                            support.createBrowser(IWorkbenchBrowserSupport.AS_EDITOR, file.getLocation().toPortableString(),
                                    null, null).openURL(htmlFile.toURL());
                        } else {
                            support.createBrowser(IWorkbenchBrowserSupport.AS_EXTERNAL, SHARED_ID, null, null).openURL(
                                    htmlFile.toURL());
                        }
                    } catch (Exception e1) {
                        log.error(e1.getMessage(), e1);
                    }
                }
            });

            variablesCombo.addModifyListener(new ModifyListener() {

                public void modifyText(ModifyEvent e) {
                    String output = variablesCombo.getText();
                    if (output.startsWith(TransformerMainPage.DEFAULT_DISPLAY)) {
                        output = DEFAULT_DISPLAY_TEXT;// TransformerMainPage.DEFAULT_VAR+output.substring(TransformerMainPage.DEFAULT_DISPLAY.length());
                    }
                    String text = variablesCombo.getText();
                    if (text.equals(DEFAULT_DISPLAY_TEXT)) {
                        text = TransformerMainPage.DEFAULT_DISPLAY;
                    }
                    variablesViewer.setDocument(new Document(getText(text)));

                    seeInBrowser.setEnabled("html".equals(text)); //$NON-NLS-1$
                }
            });

            variablesCombo.setFocus();

            refreshData();

            return composite;

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            MessageDialog.openError(this.getShell(), Messages._Error,
                    Messages.bind(Messages.ProcessResultsDialog_ErrorMsg, e.getLocalizedMessage()));
            return null;
        }

    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, BUTTON_CLOSE, Messages.ProcessResultsDialog_Close, false);
    }

    @Override
    protected void buttonPressed(int buttonId) {
        switch (buttonId) {
        case BUTTON_CLOSE:
            this.close();
        }
    }

    private static Pattern p = Pattern.compile(".*charset\\s*=[\"|']?(.+)[\"|']([\\s|;].*)?");//$NON-NLS-1$

    protected String getText(String output) {
        WSExtractedContent ct = resultsMap.get(output);
        if (ct == null) {
            return "";//$NON-NLS-1$
        }
        String contentType = ct.getContentType();
        byte[] bytes = ct.getWsByteArray().getBytes();
        if (bytes == null) {
            return "";//$NON-NLS-1$
        }
        // extract charset
        String charset = "UTF8";//$NON-NLS-1$
        Matcher m = p.matcher(contentType);
        if (m.matches()) {
            charset = m.group(1).trim().toUpperCase();
        }
        if ("UTF-8".equals(charset)) {
            charset = "UTF8";//$NON-NLS-1$
        }
        // display
        try {
            return new String(bytes, charset);
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            return Messages.bind(Messages.ProcessResultsDialog_ErrorContent, contentType, sw.toString());
        }
    }

    protected void refreshData() {
        try {

            Set<String> outputs = resultsMap.keySet();
            for (Object element : outputs) {
                String output = (String) element;
                if (output.startsWith(TransformerMainPage.DEFAULT_VAR)) {
                    output = DEFAULT_DISPLAY_TEXT;// TransformerMainPage.DEFAULT_DISPLAY+output.substring(TransformerMainPage.DEFAULT_VAR.length());
                }
                variablesCombo.add(output);
            }
            variablesCombo.select(0);
            String text = variablesCombo.getText();
            if (text.equals(DEFAULT_DISPLAY_TEXT)) {
                text = TransformerMainPage.DEFAULT_DISPLAY;
            }
            variablesViewer.setDocument(new Document(getText(text)));

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            MessageDialog.openError(this.getShell(), Messages.ProcessResultsDialog_ErrorTitle,
                    Messages.bind(Messages.ProcessResultsDialog_ErrorMsg1, e.getLocalizedMessage()));
        }
    }

}
