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
package com.amalto.workbench.dialogs;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.amalto.workbench.i18n.Messages;

/**
 * created by HHB on 2013-9-23 Detailled comment
 *
 */
public class MissingJarDialog extends Dialog {

    static Logger log = Logger.getLogger(MissingJarDialog.class);

    private Text jarsTxt;

    private final List<String> missingJars;

    /**
     * Create the dialog.
     *
     * @param parentShell
     */
    public MissingJarDialog(Shell parentShell, List<String> missingJars) {
        super(parentShell);
        this.missingJars = missingJars;
        setShellStyle(SWT.CLOSE | SWT.RESIZE | SWT.TITLE);
    }

    private String getMissingJarsString() {
        StringBuffer buf = new StringBuffer();
        if (missingJars != null) {
            for (String jarName : missingJars) {
                buf.append(jarName).append("\n"); //$NON-NLS-1$
            }

        }
        return buf.toString();
    }

    /**
     * Create contents of the dialog.
     *
     * @param parent
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        container.setLayout(new GridLayout(1, false));

        Label titleLabel = new Label(container, SWT.NONE);
        titleLabel.setText(Messages.MissingJarDialog_title);

        jarsTxt = new Text(container, SWT.BORDER | SWT.READ_ONLY | SWT.V_SCROLL | SWT.MULTI);
        jarsTxt.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        Composite composite = new Composite(container, SWT.NONE);
        composite.setLayout(new RowLayout(SWT.HORIZONTAL));
        composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label moduleLabel1 = new Label(composite, SWT.NONE);
        moduleLabel1.setText(Messages.MissingJarDialog_goto);

        Link activeModuleViewLink = new Link(composite, SWT.NONE);
        activeModuleViewLink.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                activeModulesView();
            }
        });
        activeModuleViewLink.setText("<a>" + Messages.MissingJarDialog_modulesView + "</a>"); //$NON-NLS-1$ //$NON-NLS-3$

        Label moduleLabel2 = new Label(composite, SWT.NONE);
        moduleLabel2.setText(Messages.MissingJarDialog_downloadJar);

        initMissingJarList();
        return container;
    }

    private static final String MODULES_VIEW_ID = "org.talend.designer.codegen.perlmodule.ModulesView"; //$NON-NLS-1$

    protected void activeModulesView() {
        Display.getDefault().asyncExec(new Runnable() {

            public void run() {
                try {
                    IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                    activePage.showView(MODULES_VIEW_ID);

                    MissingJarDialog.this.close();
                } catch (PartInitException e) {
                    log.error(e.getMessage(), e);
                }
            }
        });

    }

    /**
     * DOC HHB Comment method "initMissingJarList".
     */
    private void initMissingJarList() {
        String missing = getMissingJarsString();
        jarsTxt.setText(missing);
    }

    /**
     * Create contents of the button bar.
     *
     * @param parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        // createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    /**
     * Return the initial size of the dialog.
     */
    @Override
    protected Point getInitialSize() {
        return new Point(450, 300);
    }

}
