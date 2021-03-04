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
package com.amalto.workbench.editors.xslteditor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IEditorInput;
import org.eclipse.wst.xsl.ui.internal.editor.XSLEditor;

import com.amalto.workbench.i18n.Messages;

public class XSLTEditor extends XSLEditor {

    public static final String ID = "com.amalto.workbench.editors.xslteditor.XSLTEditor"; //$NON-NLS-1$

    public static Log log = LogFactory.getLog(XSLTEditor.class);

    private PageRefresher refresher;

    public XSLTEditor() {
    }

    @Override
    protected void doSetInput(IEditorInput input) throws CoreException {
        super.doSetInput(input);

        XSLTFileEditorInput editorInput = (XSLTFileEditorInput) input;
        this.refresher = editorInput.refresher;
    }

    @Override
    public void createPartControl(Composite parent) {
        Composite mainPart = new Composite(parent, SWT.NONE);
        mainPart.setLayout(new GridLayout());

        createToolBar(mainPart);

        Composite editPart = new Composite(mainPart, SWT.NONE);
        editPart.setLayout(new FillLayout());
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        editPart.setLayoutData(gd);
        super.createPartControl(editPart);

        getSourceViewer().getTextWidget().addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                if (refresher != null) {
                    refresher.makeDirty();
                }
            }
        });
    }

    private void createToolBar(Composite parent) {
        Composite toolBarComp = new Composite(parent, SWT.BORDER_DOT);
        GridData gd = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
        gd.heightHint = 25;
        toolBarComp.setLayoutData(gd);
        final GridLayout glToolBarBackground = new GridLayout();
        glToolBarBackground.verticalSpacing = 0;
        glToolBarBackground.marginWidth = 0;
        glToolBarBackground.marginHeight = 0;
        glToolBarBackground.horizontalSpacing = 0;
        toolBarComp.setLayout(glToolBarBackground);

        gd = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1);
        Button importToolItem = new Button(toolBarComp, SWT.PUSH);
        importToolItem.setLayoutData(gd);

        String btnSaveBackText = Messages.XSLTEditor_saveback;
        importToolItem.setText(btnSaveBackText);
        importToolItem.setToolTipText(btnSaveBackText);
        importToolItem.setEnabled(true);

        importToolItem.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                doSave(new NullProgressMonitor());
                Display.getCurrent().asyncExec(new Runnable() {

                    public void run() {
                        if (refresher != null) {
                            refresher.refreshPageUIEnabled();
                        }
                        close(false);
                    }
                });
            }
        });
    }

    @Override
    public void editorContextMenuAboutToShow(IMenuManager menu) {
        if (menu instanceof MenuManager) {
            MenuManager menuManager = (MenuManager) menu;
            Menu menu3 = menuManager.getMenu();
            menu3.setVisible(false);
        }
    }

    @Override
    public void doSave(IProgressMonitor progressMonitor) {
        super.doSave(progressMonitor);

        if (refresher != null) {
            String text = getSourceViewer().getTextWidget().getText();
            refresher.refreshPageContent(text);
        }
    }

    @Override
    public void dispose() {
        if (refresher != null) {
            refresher.refreshPageUIEnabled();
        }

        removeTmpFile();
        super.dispose();
    }

    private void removeTmpFile() {
        IEditorInput editorInput = getEditorInput();
        if (editorInput instanceof XSLTFileEditorInput) {
            XSLTFileEditorInput fileEditorInput = (XSLTFileEditorInput) editorInput;
            if (fileEditorInput.isTempFile) {
                try {
                    IFile file = fileEditorInput.getFile();
                    file.delete(true, new NullProgressMonitor());
                } catch (CoreException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

}
