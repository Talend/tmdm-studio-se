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
package org.talend.mdm.repository.ui.dialogs.lock;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.plugin.RepositoryPlugin;
import org.talend.mdm.repository.ui.navigator.MDMRepositoryLabelProvider;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;

import com.amalto.workbench.models.TreeObject;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class LockedObjectDialog extends Dialog {

    private static class TreeContentProvider implements ITreeContentProvider {

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }

        public void dispose() {
        }

        public Object[] getElements(Object inputElement) {
            return getChildren(inputElement);
        }

        public Object[] getChildren(Object parentElement) {
            if (parentElement instanceof Collection) {
                return ((Collection) parentElement).toArray();
            }
            return new Object[0];
        }

        public Object getParent(Object element) {
            return null;
        }

        public boolean hasChildren(Object element) {
            return getChildren(element).length > 0;
        }
    }

    private Label titleLabel;

    private TreeViewer treeViewer;

    protected List<IRepositoryViewObject> lockedObjs;

    private final String mutliObjAlertMsg;

    private boolean continueRestOperation = true;

    private String singleObjAlertMsg;

    private boolean forceContinueResetOperation;

    public boolean canContinueRestOperation() {
        return this.continueRestOperation;
    }

    public LockedObjectDialog(Shell parentShell, String multiObjAlertMsg, String singleObjAlertMsg,
            List<IRepositoryViewObject> inputObjs, boolean forceContinueResetOperation) {
        super(parentShell);
        this.mutliObjAlertMsg = multiObjAlertMsg;
        this.singleObjAlertMsg = singleObjAlertMsg;
        this.forceContinueResetOperation = forceContinueResetOperation;

        initInput(inputObjs);

    }

    /**
     * Create contents of the dialog.
     *
     * @param parent
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        GridLayout gridLayout = (GridLayout) container.getLayout();
        gridLayout.marginHeight = 5;

        titleLabel = new Label(container, SWT.WRAP);
        GridData gd_titleLabel = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
        gd_titleLabel.heightHint = 40;
        gd_titleLabel.verticalIndent = 5;
        titleLabel.setLayoutData(gd_titleLabel);
        if (mutliObjAlertMsg != null) {
            if (canContinueRestOperation()) {
                titleLabel.setText(mutliObjAlertMsg);
            } else {
                titleLabel.setText(singleObjAlertMsg);
            }
        }
        treeViewer = new TreeViewer(container, SWT.BORDER);
        Tree tree = treeViewer.getTree();
        GridData gd_tree = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        gd_tree.verticalIndent = 5;
        tree.setLayoutData(gd_tree);
        treeViewer.setContentProvider(new TreeContentProvider());
        ILabelDecorator labelDecorator = RepositoryPlugin.getDefault().getWorkbench().getDecoratorManager().getLabelDecorator();
        DecoratingLabelProvider labelProvider = new DecoratingLabelProvider(new MDMRepositoryLabelProvider(), labelDecorator);
        treeViewer.setLabelProvider(labelProvider);

        // init input
        treeViewer.setInput(lockedObjs);

        return container;
    }

    @Override
    protected void cancelPressed() {
        super.cancelPressed();
        continueRestOperation = false;
    }

    protected void initInput(List<IRepositoryViewObject> inputObjs) {
        lockedObjs = new LinkedList<IRepositoryViewObject>();
        for (IRepositoryViewObject viewObject : inputObjs) {
            if (RepositoryResourceUtil.isLockedAndEdited(viewObject)) {
                lockedObjs.add(viewObject);
            }
        }
        // can continue
        continueRestOperation = true;
        if (forceContinueResetOperation) {
            continueRestOperation = true;
        } else {
            if (inputObjs.size() > 0) {
                if (inputObjs.size() == lockedObjs.size()) {
                    continueRestOperation = false;
                }
            }
        }
    }

    public boolean needShowDialog() {
        return !lockedObjs.isEmpty();
    }

    public Object[] getUnlockedTreeObject(Object[] inputTreeObjs, Map<IRepositoryViewObject, TreeObject> objMap) {
        if (inputTreeObjs == null || objMap == null) {
            return null;
        }
        Set<Object> lockedTreeObjs = new HashSet<Object>();
        // collect
        for (IRepositoryViewObject lockedObj : lockedObjs) {
            TreeObject lockedTreeObj = objMap.get(lockedObj);
            if (lockedTreeObj != null) {
                lockedTreeObjs.add(lockedTreeObj);
            }
        }
        //
        List<Object> newUnlockedTreeObjs = new LinkedList<Object>();
        for (Object oldObj : inputTreeObjs) {
            if (!lockedTreeObjs.contains(oldObj)) {
                newUnlockedTreeObjs.add(oldObj);
            }
        }
        return newUnlockedTreeObjs.toArray();

    }

    /**
     * Create contents of the button bar.
     *
     * @param parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        if (continueRestOperation) {
            createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
        }
    }

    /**
     * Return the initial size of the dialog.
     */
    @Override
    protected Point getInitialSize() {
        return new Point(450, 300);
    }

}
