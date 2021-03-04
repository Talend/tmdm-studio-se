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
package org.talend.mdm.repository.ui.starting.editor;

import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.talend.commons.ui.html.BrowserDynamicPartLocationListener;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.mdm.repository.plugin.RepositoryPlugin;
import org.talend.mdm.repository.ui.starting.helper.MDMStartingHelper;
import org.talend.mdm.repository.utils.EclipseResourceManager;

public class MDMStartingEditor extends EditorPart {

    public static final String ID = "org.talend.mdm.repository.ui.starting.editor.MDMStartingEditor";//$NON-NLS-1$

    protected static final String ICON_WHITE_PATH = "/icons/appli_white_16x16.png"; //$NON-NLS-1$

    protected Image titleImage;

    private Browser browser;

    public MDMStartingEditor() {
        titleImage = EclipseResourceManager.getImage(RepositoryPlugin.class, ICON_WHITE_PATH);
    }

    @Override
    public void createPartControl(Composite parent) {
        try {
            browser = new Browser(parent, SWT.NONE);
            browser.setText(MDMStartingHelper.getHelper().getHtmlContent());
            browser.addLocationListener(new BrowserDynamicPartLocationListener());

            return;
        } catch (IOException e) {
            ExceptionHandler.process(e);
        } catch (Throwable t) {

            Exception ex = new Exception("The internal web browser can not be access,the starting page won't be displayed");//$NON-NLS-1$
            ExceptionHandler.process(ex);
        }
    }

    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        setSite(site);
        setInput(input);
        setPartName(input.getName());
    }

    @Override
    public void setFocus() {
        if (browser != null) {
            browser.setFocus();
        }
    }

    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

    @Override
    public void doSave(IProgressMonitor arg0) {
        // do nothing
    }

    @Override
    public void doSaveAs() {
        // do nothing
    }

    @Override
    public Image getTitleImage() {
        if (titleImage != null) {
            return titleImage;
        }
        return super.getTitleImage();
    }

}
