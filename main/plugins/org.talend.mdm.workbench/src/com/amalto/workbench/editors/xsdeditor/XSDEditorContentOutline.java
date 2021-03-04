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
package com.amalto.workbench.editors.xsdeditor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.IPageBookViewPage;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

/**
 * created by HHB on 2015-10-27 Detailled comment
 *
 */
public class XSDEditorContentOutline extends Page implements IContentOutlinePage {

    private static Log log = LogFactory.getLog(XSDEditorContentOutline.class);

    private XSDEditor editor;

    private PageBook pagebook;

    private IContentOutlinePage activePage;

    private EmptyOutlinePage emptyOutlinePage;

    public XSDEditorContentOutline(XSDEditor editor) {
        this.editor = editor;

    }

    public void setSelection(ISelection selection) {

    }

    public ISelection getSelection() {
        return null;
    }

    public void addSelectionChangedListener(ISelectionChangedListener listener) {

    }

    public void removeSelectionChangedListener(ISelectionChangedListener listener) {

    }

    @Override
    public void createControl(Composite parent) {
        pagebook = new PageBook(parent, SWT.NONE);
        setActiveOutlinePage(activePage);

    }

    @Override
    public void dispose() {
        if (pagebook != null && !pagebook.isDisposed()) {
            pagebook.dispose();
        }

        if (emptyOutlinePage != null) {
            emptyOutlinePage.dispose();
            emptyOutlinePage = null;
        }
        pagebook = null;
        editor = null;
    }

    @Override
    public Control getControl() {
        return pagebook;
    }

    @Override
    public void setFocus() {
        if (activePage != null) {
            activePage.setFocus();
        }

    }

    public void setActiveOutlinePage(IContentOutlinePage page) {
        activePage = page;
        if (page == null) {
            activePage = getEmptyOutlinePage();
        }
        Control control = activePage.getControl();
        if (getSite() != null) {
            if (control == null || control.isDisposed()) {
                if (activePage instanceof IPageBookViewPage) {
                    try {
                        ((IPageBookViewPage) activePage).init(getSite());
                    } catch (PartInitException e) {
                        log.error(e.getMessage(), e);
                        return;
                    }
                }
                activePage.createControl(pagebook);
                control = activePage.getControl();
            }

            pagebook.showPage(control);
        }
    }

    private IContentOutlinePage getEmptyOutlinePage() {
        if (emptyOutlinePage == null) {
            this.emptyOutlinePage = new EmptyOutlinePage();
        }
        return emptyOutlinePage;
    }

    class EmptyOutlinePage extends ContentOutlinePage implements IPageBookViewPage {

        public EmptyOutlinePage() {
            super();
        }
    }

}
