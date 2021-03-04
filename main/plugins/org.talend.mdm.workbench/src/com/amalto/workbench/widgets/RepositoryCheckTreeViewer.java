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
package com.amalto.workbench.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.models.TreeParent;
import com.amalto.workbench.utils.Util;

/**
 * @author achen DOC achen class global comment. Detailled comment
 */
public class RepositoryCheckTreeViewer {

    protected static Log log = LogFactory.getLog(RepositoryCheckTreeViewer.class);

    protected FilteredCheckboxTree filteredCheckboxTree;

    // protected CheckboxRepositoryView exportItemsTreeView;

    Collection<TreeObject> repositoryNodes = new ArrayList<TreeObject>();

    Collection<TreeObject> optimizedCheckNodes = new ArrayList<TreeObject>();

    protected IStructuredSelection selection;

    protected SashForm sash;

    protected Button moveButton;

    protected TreeParent serverRoot;

    protected List<TreeObject> checkItems = new ArrayList<TreeObject>();

    public RepositoryCheckTreeViewer(IStructuredSelection selection) {
        this.selection = selection;
        Object firstElement = selection.getFirstElement();
        if (firstElement != null && firstElement instanceof TreeObject) {
            serverRoot = ((TreeObject) firstElement).getServerRoot();
        }
        checkItems = selection.toList();
    }

    public RepositoryCheckTreeViewer(TreeParent serverRoot) {
        this.serverRoot = serverRoot;
    }

    public void setCheckItems(List<TreeObject> list) {
        checkItems = list;
        refresh();
    }

    public void setServerRoot(TreeParent serverRoot) {
        this.serverRoot = serverRoot;
    }

    Label itemLabel = null;

    protected TreeViewer viewer;

    /**
     *
     * @param workArea
     */
    public Composite createItemList(Composite workArea) {
        Group itemComposite = new Group(workArea, 0);
        GridLayoutFactory.swtDefaults().numColumns(2).applyTo(itemComposite);
        GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).hint(400, 300).applyTo(itemComposite);

        itemLabel = new Label(itemComposite, SWT.NONE);
        itemLabel.setText(Messages.RepositoryCheckTreeViewer_ItemLabel);
        GridDataFactory.swtDefaults().span(2, 1).applyTo(itemLabel);

        createTreeViewer(itemComposite);

        createSelectionButton(itemComposite);

        // force loading all nodes
        setCreatedViewer();

        refresh();
        return itemComposite;
    }

    protected void setCreatedViewer() {
        // viewer = exportItemsTreeView.getViewer();
    }

    public void refresh() {

        // if user has select some items in repository view, mark them as checked
        for (TreeObject obj : checkItems) {
            if (obj instanceof TreeParent) {

                repositoryNodes.addAll(Util.getChildrenObj((TreeParent) obj));

            } else {
                repositoryNodes.add(obj);

            }
        }

        ((CheckboxTreeViewer) viewer).setCheckedElements(repositoryNodes.toArray());

    }

    public void setItemText(String text) {
        itemLabel.setText(text);
    }

    public TreeViewer getViewer() {
        return viewer;
    }

    public void setViewer(TreeViewer viewer) {
        this.viewer = viewer;
    }

    private void expandParent(TreeViewer viewer, TreeObject node) {
        TreeParent parent = node.getParent();
        if (parent != null) {
            expandParent(viewer, parent);
            viewer.setExpandedState(parent, true);
        }
    }

    public Object[] getCheckNodes() {
        Object[] selected = null;
        List ret = new ArrayList();
        if (viewer != null && viewer instanceof CheckboxTreeViewer) {
            selected = ((CheckboxTreeViewer) viewer).getCheckedElements();
        }
        filterCheckedObjects(selected, ret);
        return ret.toArray();
    }

    protected void filterCheckedObjects(Object[] selected, List ret) {

        for (Object element : selected) {
            if (element instanceof TreeObject) {
                TreeObject node = (TreeObject) element;
                if (node.isXObject()) {
                    ret.add(node);
                }
            }
        }

    }

    private SelectionListener bunListener;

    public void setRoot(TreeParent root) {
    }

    protected void createTreeViewer(Composite itemComposite) {

    }

    public void addCheckStateListener(ICheckStateListener listener) {
        // ((CheckboxTreeViewer) exportItemsTreeView.getViewer()).addCheckStateListener(listener);
    }

    public void removeCheckStateListener(ICheckStateListener listener) {
        // ((CheckboxTreeViewer) exportItemsTreeView.getViewer()).removeCheckStateListener(listener);
    }

    protected boolean filterRepositoryNode(TreeObject node) {
        if (node == null) {
            return false;
        }
        // remove the filter for resource to provide the function to import and export resources.
        if (node.getType() == TreeObject.SUBSCRIPTION_ENGINE || node.getType() == TreeObject.SERVICE_CONFIGURATION
                || !Util.IsEnterPrise() && node.getType() == TreeObject.WORKFLOW || !Util.IsEnterPrise()
                && node.getType() == TreeObject.WORKFLOW_PROCESS || node.getType() == TreeObject.JOB
                || node.getType() == TreeObject.JOB_REGISTRY) {
            return false;
        }
        if (!Util.IsEnterPrise()) {
            if (node.getType() == TreeObject.ROLE) {
                return false;
            }
        }
        return true;
    }

    /**
     * DOC hcw Comment method "createSelectionButton".
     *
     * @param itemComposite
     */
    private void createSelectionButton(Composite itemComposite) {
        Composite buttonComposite = new Composite(itemComposite, SWT.NONE);
        GridLayoutFactory.swtDefaults().margins(0, 25).applyTo(buttonComposite);
        GridDataFactory.swtDefaults().align(SWT.FILL, SWT.BEGINNING).applyTo(buttonComposite);
        buttonComposite.setLayout(new RowLayout(SWT.VERTICAL));

        Button hide = new Button(buttonComposite, SWT.PUSH);
        hide.setVisible(false);
        Button selectAll = new Button(buttonComposite, SWT.PUSH);
        selectAll.setText(Messages.RepositoryCheckTreeViewer_SelectAll);
        selectAll.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                ((CheckboxTreeViewer) viewer).setAllChecked(true);
            }
        });

        Button deselectAll = new Button(buttonComposite, SWT.PUSH);
        deselectAll.setText(Messages.RepositoryCheckTreeViewer_DeselectAll);
        deselectAll.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                ((CheckboxTreeViewer) viewer).setAllChecked(false);
            }
        });
        if (bunListener != null) {
            selectAll.addSelectionListener(bunListener);
            deselectAll.addSelectionListener(bunListener);
        }

        // setButtonLayoutData(deselectAll);

        Button expandBtn = new Button(buttonComposite, SWT.PUSH);
        expandBtn.setText("Expand All"); //$NON-NLS-1$
        expandBtn.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                viewer.expandAll();
            }
        });
        // setButtonLayoutData(expandBtn);

        Button collapseBtn = new Button(buttonComposite, SWT.PUSH);
        collapseBtn.setText("Collapse All"); //$NON-NLS-1$
        collapseBtn.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                viewer.collapseAll();
            }
        });
        // setButtonLayoutData(collapseBtn);
    }

    public void addButtonSelectionListener(SelectionListener listener) {
        this.bunListener = listener;

    }
}
