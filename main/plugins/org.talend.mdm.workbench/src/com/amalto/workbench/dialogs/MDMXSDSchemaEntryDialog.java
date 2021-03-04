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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class MDMXSDSchemaEntryDialog extends Dialog {

    private String title;

    private ListViewer wcListViewer;

    private List<String> urls = new ArrayList<String>();

    private List<String> importedUrls = new ArrayList<String>();

    public MDMXSDSchemaEntryDialog(Shell parentShell, String title) {
        super(parentShell);
        setShellStyle(this.getShellStyle() | SWT.RESIZE);
        this.title = title;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        parent.getShell().setText(this.title);

        Composite composite = (Composite) super.createDialogArea(parent);
        GridLayout layout = (GridLayout) composite.getLayout();
        layout.numColumns = 1;

        wcListViewer = new ListViewer(composite, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
        wcListViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
        ((GridData) wcListViewer.getControl().getLayoutData()).minimumHeight = 200;

        wcListViewer.setContentProvider(new IStructuredContentProvider() {

            public void dispose() {
            }

            public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            }

            public Object[] getElements(Object inputElement) {
                return ((ArrayList) inputElement).toArray(new String[] {});
            }
        });

        wcListViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            public void selectionChanged(SelectionChangedEvent event) {
                importedUrls.clear();
                IStructuredSelection selection = (IStructuredSelection) event.getSelection();
                Iterator iter = selection.iterator();
                while (iter.hasNext()) {
                    String url = (String) iter.next();
                    importedUrls.add(url);
                }
                getButton(IDialogConstants.OK_ID).setEnabled(!selection.isEmpty());
            }
        });
        wcListViewer.setLabelProvider(new ILabelProvider() {

            public void addListener(ILabelProviderListener listener) {
            }

            public void dispose() {
            }

            public boolean isLabelProperty(Object element, String property) {
                return false;
            }

            public void removeListener(ILabelProviderListener listener) {
            }

            public Image getImage(Object element) {
                return null;
            }

            public String getText(Object element) {
                return element.toString();
            }
        });

        wcListViewer.setSorter(new ViewerSorter());
        wcListViewer.setInput(urls);
        return composite;
    }

    @Override
    protected void okPressed() {
        setReturnCode(OK);
        getButton(IDialogConstants.OK_ID).setData("dialog", MDMXSDSchemaEntryDialog.this);//$NON-NLS-1$
        // no close let Action Handler handle it
        super.okPressed();
    }

    @Override
    protected void cancelPressed() {
        setReturnCode(CANCEL);
        getButton(IDialogConstants.CANCEL_ID).setData("dialog", MDMXSDSchemaEntryDialog.this);//$NON-NLS-1$
        // no close let Action Handler handle it
        super.cancelPressed();
    }

    @Override
    protected Control createButtonBar(Composite parent) {
        Control control = super.createButtonBar(parent);
        getButton(IDialogConstants.OK_ID).setEnabled(false);

        return control;
    }

    public List<String> getMDMDataModelUrls() {
        return importedUrls;
    }

    public void setOKButton(boolean enabled) {
        getButton(IDialogConstants.OK_ID).setEnabled(enabled);
    }

    public void retrieveDataModels(List<String> objs, boolean selectAll) {
        urls.addAll(objs);
        wcListViewer.refresh();
        if (selectAll) {
            wcListViewer.getList().selectAll();
            importedUrls.addAll(objs);
        }
    }
}
