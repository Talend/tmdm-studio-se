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
package org.talend.mdm.repository.ui.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
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
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.service.RepositoryQueryService;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.model.mdmproperties.MDMServerObjectItem;
import org.talend.mdm.repository.model.mdmserverobject.MDMServerObject;
import org.talend.mdm.repository.model.mdmserverobject.WSResourceE;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;

import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.models.TreeParent;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.widgets.FilteredCheckboxTree;

/**
 * DOC hbhong class global comment. <BR>
 * this class is a replacement for RepositoryCheckTreeViewer in MDM server view,not named RepositoryCheckTreeViewerR
 * ,because to avoid confuse it works for repository
 */
public abstract class AbstractNodeCheckTreeViewer {

    protected static Log log = LogFactory.getLog(AbstractNodeCheckTreeViewer.class);

    private SelectionListener bunListener;

    protected List<TreeObject> checkItems = new ArrayList<TreeObject>();

    protected String defaultTagText;

    protected FilteredCheckboxTree filteredCheckboxTree;

    protected boolean isTagEditable;

    Label itemLabel = null;

    protected Button moveButton;

    Collection<TreeObject> optimizedCheckNodes = new ArrayList<TreeObject>();

    protected SashForm sash;

    protected IStructuredSelection selection;

    protected TreeParent serverRoot;

    protected TreeViewer viewer;

    public AbstractNodeCheckTreeViewer(IStructuredSelection selection) {
        this.selection = selection;
        Object firstElement = selection.getFirstElement();
        if (firstElement != null && firstElement instanceof TreeObject) {
            serverRoot = ((TreeObject) firstElement).getServerRoot();
        }
        checkItems = selection.toList();
    }

    public AbstractNodeCheckTreeViewer(IStructuredSelection selection, String defaultTagText, boolean isTagEditable) {
        this.selection = selection;
        Object firstElement = selection.getFirstElement();
        if (firstElement != null && firstElement instanceof TreeObject) {
            serverRoot = ((TreeObject) firstElement).getServerRoot();
        }
        checkItems = selection.toList();
        this.defaultTagText = defaultTagText;
        this.isTagEditable = isTagEditable;
    }

    public AbstractNodeCheckTreeViewer(TreeParent serverRoot) {
        this.serverRoot = serverRoot;
    }

    public void addButtonSelectionListener(SelectionListener listener) {
        this.bunListener = listener;

    }

    public void addCheckStateListener(ICheckStateListener listener) {
        filteredCheckboxTree.getViewer().addCheckStateListener(listener);
    }

    /**
     *
     * @param workArea
     */
    public Composite createItemList(Composite workArea) {
        Group itemComposite = new Group(workArea, 0);
        GridLayoutFactory.swtDefaults().numColumns(2).applyTo(itemComposite);
        GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).hint(400, 300).applyTo(itemComposite);

        itemLabel = new Label(itemComposite, SWT.NONE);
        itemLabel.setText(Messages.AbstractXXViewer_SelectItem);
        GridDataFactory.swtDefaults().span(2, 1).applyTo(itemLabel);

        createTreeViewer(itemComposite);

        createSelectionButton(itemComposite);

        // force loading all nodes
        setCreatedViewer();
        createOtherControl(itemComposite);
        refresh();
        return itemComposite;
    }

    protected void createOtherControl(Composite itemComposite) {
    }

    /**
     * DOC hcw Comment method "createSelectionButton".
     *
     * @param itemComposite
     */
    protected Composite createSelectionButton(Composite itemComposite) {
        Composite buttonComposite = new Composite(itemComposite, SWT.NONE);
        GridLayoutFactory.swtDefaults().margins(0, 25).applyTo(buttonComposite);
        GridDataFactory.swtDefaults().align(SWT.FILL, SWT.BEGINNING).applyTo(buttonComposite);
        buttonComposite.setLayout(new GridLayout(1, false));

        Button hide = new Button(buttonComposite, SWT.PUSH);
        hide.setVisible(false);
        Button selectAll = new Button(buttonComposite, SWT.PUSH);
        selectAll.setText(Messages.AbstractXXViewer_SelectAll);
        selectAll.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                ((CheckboxTreeViewer) viewer).setAllChecked(true);
                updateCountStatus();
            }
        });

        Button deselectAll = new Button(buttonComposite, SWT.PUSH);
        deselectAll.setText(Messages.AbstractXXViewer_DeselectAll);
        deselectAll.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                ((CheckboxTreeViewer) viewer).setAllChecked(false);
                updateCountStatus();
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
        return buttonComposite;
    }

    protected abstract void createTreeViewer(Composite itemComposite);

    public void updateCountStatus() {
        // do nothing
    }

    protected void expandParent(TreeViewer viewer, TreeObject node) {
        TreeParent parent = node.getParent();
        if (parent != null) {
            expandParent(viewer, parent);
            viewer.setExpandedState(parent, true);
        }
    }

    protected abstract void filterCheckedObjects(Object[] selected, List<Object> ret);

    protected boolean filterRepositoryNode(TreeObject node, boolean isOverWrite) {
        if (node == null) {
            return false;
        }
        // remove the filter for resource to provide the function to import and export resources.
        if (node.getType() == TreeObject.SUBSCRIPTION_ENGINE || node.getType() == TreeObject.SERVICE_CONFIGURATION
                || node.getType() == TreeObject.JOB || node.getType() == TreeObject.JOB_REGISTRY) {
            return false;
        }
        if (!Util.IsEnterPrise()) {
            if (node.getType() == TreeObject.ROLE) {
                return false;
            }
        }

        if (!isOverWrite) {
            if (isExist(node)) {
                return false;
            }
        }
        return true;
    }

    private boolean isExist(TreeObject treeObj) {
        if (treeObj instanceof TreeParent) {
            return false;
        }
        int type = treeObj.getType();
        ERepositoryObjectType rType = RepositoryQueryService.getRepositoryObjectType(type);
        if (rType == null) {
            return false;
        }
        List<IRepositoryViewObject> children = RepositoryResourceUtil.findAllViewObjectsWithDeleted(rType);
        if (children == null) {
            return false;
        }
        String treeObjName = treeObj.getName();
        if (type == TreeObject.PICTURES_RESOURCE) {
            int index = treeObjName.indexOf("-"); //$NON-NLS-1$
            if (index > 0) {
                treeObjName = treeObjName.substring(index + 1);
            }
        }
        for (IRepositoryViewObject viewObject : children) {
            Item item = viewObject.getProperty().getItem();
            if (item instanceof MDMServerObjectItem) {
                MDMServerObject serverObj = ((MDMServerObjectItem) item).getMDMServerObject();
                String name = serverObj.getName();

                if (type == TreeObject.PICTURES_RESOURCE) {
                    name = name + "_" + viewObject.getVersion() + "." + ((WSResourceE) serverObj).getFileExtension(); //$NON-NLS-1$ //$NON-NLS-2$
                }
                if (serverObj != null && treeObjName.equals(name)) {
                    return true;
                }
            }

        }
        return false;
    }

    public IRepositoryViewObject getViewObjectByType(IRepositoryViewObject[] theInput, ERepositoryObjectType type) {
        if (theInput == null || type == null) {
            return null;
        }
        for (IRepositoryViewObject viewObj : theInput) {
            if (viewObj.getRepositoryObjectType().equals(type)) {
                return viewObj;
            }
        }
        return null;
    }

    public Object[] getCheckNodes() {
        Object[] selected = null;
        List<Object> ret = new LinkedList<Object>();
        if (viewer != null && viewer instanceof CheckboxTreeViewer) {
            selected = ((CheckboxTreeViewer) viewer).getCheckedElements();
        }
        filterCheckedObjects(selected, ret);
        return ret.toArray();
    }

    public TreeViewer getViewer() {
        return viewer;
    }

    public void refresh() {
        // empty
    }

    public void removeCheckStateListener(ICheckStateListener listener) {
        filteredCheckboxTree.getViewer().removeCheckStateListener(listener);
    }

    public void setCheckItems(List<TreeObject> list) {
        checkItems = list;
        refresh();
    }

    protected void setCreatedViewer() {
        viewer = filteredCheckboxTree.getViewer();
    }

    public void setItemText(String text) {
        itemLabel.setText(text);
    }

    public void setServerRoot(TreeParent serverRoot) {
        this.serverRoot = serverRoot;
    }

    public void setViewer(TreeViewer viewer) {
        this.viewer = viewer;
    }
}
