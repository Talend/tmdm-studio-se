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

import java.awt.Panel;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPartSite;

import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.models.TreeParent;
import com.amalto.workbench.providers.ServerTreeContentProvider;
import com.amalto.workbench.providers.ServerTreeLabelProvider;

public class DataModelSelectDialog extends org.eclipse.jface.dialogs.Dialog {

    // The ending| bug:21784
    private static final long serialVersionUID = 1L;

    // Modified by hbhong,to fix bug 21784|Add a TreeParent parameter to constructor
    private final TreeParent treeParent;

    private IWorkbenchPartSite site;

    private String clusterName;// cluster for default datamodel selection

    protected Label schemaLabel = null;

    private String title = Messages.DataModelSelectDialog_Title;

    private String[] xpaths;

    protected Panel panel;

    protected Button add;

    protected SelectionListener listener;

    private ServerTreeContentProvider contentProvider;

    protected TreeViewer domViewer;

    public DataModelSelectDialog(IWorkbenchPartSite iWorkbenchPartSite, TreeParent treeParent2, String clusterName) {
        super(iWorkbenchPartSite.getShell());
        this.site = iWorkbenchPartSite;
        this.treeParent = treeParent2;
        this.clusterName = clusterName;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        parent.getShell().setText(Messages.DataModelSelectDialog_SelectDataModel);
        Composite composite = (Composite) super.createDialogArea(parent);
        GridLayout layout = (GridLayout) composite.getLayout();
        layout.makeColumnsEqualWidth = false;
        schemaLabel = new Label(composite, SWT.NONE);
        schemaLabel.setText(title);
        GridData dg = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1);
        domViewer = new TreeViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.MULTI);
        domViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
        ((GridData) domViewer.getControl().getLayoutData()).heightHint = 400;
        ((GridData) domViewer.getControl().getLayoutData()).widthHint = 400;
        changeToResource();
        selectDataModel();
        return composite;
    }

    private void changeToResource() {
        TreeParent parent = null;
        // Modified by hbhong,to fix bug 21784
        TreeObject[] children = treeParent.getChildren();
        for (TreeObject element : children) {
            parent = (TreeParent)element;
            if (parent.getType() == TreeObject.DATA_MODEL) {
                break;
            }
        }
        // The ending| bug:21784
        contentProvider = new ServerTreeContentProvider(site, parent);
        setTreeContentProvider(contentProvider);
        domViewer.setLabelProvider(new ServerTreeLabelProvider());
        domViewer.setSorter(new ViewerSorter() {

            @Override
            public int category(Object element) {
                if (element instanceof TreeParent) {
                    TreeParent category = (TreeParent) element;
                    if (category.getType() == TreeObject.CATEGORY_FOLDER) {
                        return -1;
                    }
                }
                return 0;
            }
        });

        domViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            public void selectionChanged(SelectionChangedEvent e) {
                StructuredSelection sel = (StructuredSelection) e.getSelection();

                List<String> xpathList = new ArrayList<String>();
                Object[] selections = sel.toArray();
                for (Object obj : selections) {
                    TreeObject treeObj = (TreeObject) obj;
                    if (treeObj != null && treeObj.getType() == TreeObject.DATA_MODEL) {
                        xpathList.add(treeObj.getDisplayName());
                    }
                }

                xpaths = xpathList.toArray(new String[0]);

                if (getButton(IDialogConstants.OK_ID) != null) {
                    getButton(IDialogConstants.OK_ID).setEnabled(xpathList.size() > 0);
                }
            }
        });

        domViewer.setInput(site);
    }

    private void selectDataModel() {
        Object input = domViewer.getInput();
        TreeObject[] elements = (TreeObject[]) contentProvider.getElements(input);

        selectDefaultDataModel(elements);

        domViewer.addTreeListener(new ITreeViewerListener() {

            public void treeExpanded(TreeExpansionEvent event) {
                TreeParent parent = (TreeParent) event.getElement();
                TreeObject[] children = parent.getChildren();
                selectDefaultDataModel(children);
            }

            public void treeCollapsed(TreeExpansionEvent event) {//
            }
        });
    }

    private void selectDefaultDataModel(TreeObject[] children) {
        for (TreeObject treeObj : children) {
            if (treeObj.getType() == TreeObject.DATA_MODEL && clusterName.equals(treeObj.getName())) {
                domViewer.setSelection(new StructuredSelection(treeObj));
                break;
            }
        }
    }

    private void setTreeContentProvider(ServerTreeContentProvider treeContentProvider) {
        if (this.domViewer.getContentProvider() != null) {
            this.domViewer.getContentProvider().dispose();
        }
        domViewer.setContentProvider(treeContentProvider);
    }

    @Override
    protected Control createButtonBar(Composite parent) {
        Control btnBar = super.createButtonBar(parent);
        getButton(IDialogConstants.OK_ID).setText(Messages.DataModelSelectDialog_Ok);
        return btnBar;
    }

    public String[] getXpath() {
        return xpaths;
    }

}
