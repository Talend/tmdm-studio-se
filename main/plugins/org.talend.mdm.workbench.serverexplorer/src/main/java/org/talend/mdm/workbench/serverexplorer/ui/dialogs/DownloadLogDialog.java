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
package org.talend.mdm.workbench.serverexplorer.ui.dialogs;

import java.io.File;
import java.io.FileFilter;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.talend.mdm.workbench.serverexplorer.i18n.Messages;

/**
 * created by Karelun Huang on Mar 26, 2013 Detailled comment
 *
 */
public class DownloadLogDialog extends TitleAreaDialog {

    private Text pathText = null;

    private Button browseButton = null;

    private Button openButton = null;

    private String dirPath = null;

    private boolean needOpen = true;

    public DownloadLogDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        setTitle(Messages.DownloadLogDialog_Title);
        setMessage(Messages.DownloadLogDialog_Message);
        Composite control = (Composite) super.createDialogArea(parent);
        Composite composite = new Composite(control, SWT.NONE);
        composite.setLayout(new GridLayout(3, false));
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        new Label(composite, SWT.NONE).setText(Messages.DownloadLogDialog_PathLabel_Text);
        pathText = new Text(composite, SWT.BORDER);
        pathText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        dirPath = fillDefaultPath();
        pathText.setText(dirPath);
        pathText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                String path = pathText.getText().trim();
                boolean isDir = new File(path).isDirectory();
                setErrorMessage(isDir ? null : Messages.DownloadLogDialog_InputError_Message);
                getButton(IDialogConstants.OK_ID).setEnabled(isDir);
                dirPath = path;
            }
        });
        browseButton = new Button(composite, SWT.NONE);
        browseButton.setText(Messages.DownloadLogDialog_BroswerButton_Text);
        browseButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                DirectoryDialog fd = new DirectoryDialog(getShell(), SWT.SAVE);
                String path = fd.open();
                if (path != null) {
                    pathText.setText(path + File.separator);
                }
            }
        });

        openButton = new Button(composite, SWT.CHECK);
        GridDataFactory.fillDefaults().span(3, 1).applyTo(openButton);
        openButton.setText(Messages.DownloadLogDialog_OpenButton_Text);
        openButton.setSelection(needOpen);
        openButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                needOpen = openButton.getSelection();
            }
        });

        Label separator = new Label(control, SWT.HORIZONTAL | SWT.SEPARATOR);
        separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        return control;
    }

    private String fillDefaultPath() {
        String path = System.getProperty("user.home"); //$NON-NLS-1$
        File[] listFiles = new File(path).listFiles(new FileFilter() {

            public boolean accept(File subfile) {
                return subfile.isDirectory() && "Downloads".equalsIgnoreCase(subfile.getName()); //$NON-NLS-1$
            }
        });
        if (listFiles.length == 0) {
            return path + File.separator;
        }
        return listFiles[0].getAbsolutePath() + File.separator;
    }

    public String getDirectoryPath() {
        return this.dirPath;
    }

    public boolean needOpen() {
        return this.needOpen;
    }
}
