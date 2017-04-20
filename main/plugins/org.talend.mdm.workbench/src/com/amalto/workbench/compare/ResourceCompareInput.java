// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation Matt McCutchen (hashproduct+eclipse@gmail.com) - Bug
 * 35390 Three-way compare cannot select (mis-selects) )ancestor resource
 *******************************************************************************/

package com.amalto.workbench.compare;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.ZipFileStructureCreator;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.compare.structuremergeviewer.DiffTreeViewer;
import org.eclipse.compare.structuremergeviewer.Differencer;
import org.eclipse.compare.structuremergeviewer.IDiffContainer;
import org.eclipse.compare.structuremergeviewer.IDiffElement;
import org.eclipse.compare.structuremergeviewer.IStructureComparator;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.utils.XmlUtil;
import com.amalto.workbench.webservices.WSDataClusterPK;
import com.amalto.workbench.webservices.WSDataModelPK;
import com.amalto.workbench.webservices.WSPutItem;

/**
 * A two-way or three-way compare for arbitrary IResources.
 */
public class ResourceCompareInput extends CompareEditorInput {

    private static Log log = LogFactory.getLog(ResourceCompareInput.class);

    private static final boolean NORMALIZE_CASE = true;

    private boolean fThreeWay = false;

    private Object fRoot;

    private IStructureComparator fAncestor;

    private IStructureComparator fLeft;

    private IStructureComparator fRight;

    private IResource fAncestorResource;

    private IResource fLeftResource;

    private IResource fRightResource;

    private DiffTreeViewer fDiffViewer;

    private IAction fOpenAction;

    private CompareHeadInfo compareHeadInfo;// if head info not equal null, then we need save changes

    class MyDiffNode extends DiffNode {

        private boolean fDirty = false;

        private ITypedElement fLastId;

        private String fLastName;

        public MyDiffNode(IDiffContainer parent, int description, ITypedElement ancestor, ITypedElement left, ITypedElement right) {
            super(parent, description, ancestor, left, right);
        }

        @Override
        public void fireChange() {
            super.fireChange();
            setDirty(true);
            fDirty = true;
            if (fDiffViewer != null) {
                fDiffViewer.refresh(this);
            }
        }

        void clearDirty() {
            fDirty = false;
        }

        @Override
        public String getName() {
            if (fLastName == null) {
                fLastName = super.getName();
            }
            if (fDirty) {
                return '<' + fLastName + '>';
            }
            return fLastName;
        }

        @Override
        public ITypedElement getId() {
            ITypedElement id = super.getId();
            if (id == null) {
                return fLastId;
            }
            fLastId = id;
            return id;
        }
    }

    static class FilteredBufferedResourceNode extends BufferedResourceNode {

        FilteredBufferedResourceNode(IResource resource) {
            super(resource);
        }

        @Override
        protected IStructureComparator createChild(IResource child) {
            String name = child.getName();
            // if (CompareUIPlugin.getDefault().filter(name, child instanceof IContainer, false))
            // return null;
            return new FilteredBufferedResourceNode(child);
        }
    }

    /*
     * Creates an compare editor input for the given selection.
     */
    ResourceCompareInput(CompareConfiguration config) {
        super(config);
    }

    @Override
    public Viewer createDiffViewer(Composite parent) {
        fDiffViewer = new DiffTreeViewer(parent, getCompareConfiguration()) {

            @Override
            protected void fillContextMenu(IMenuManager manager) {

                if (fOpenAction == null) {
                    fOpenAction = new Action() {

                        @Override
                        public void run() {
                            handleOpen(null);
                        }
                    };
                    Utilities.initAction(fOpenAction, getBundle(), "action.CompareContents."); //$NON-NLS-1$
                }

                boolean enable = false;
                ISelection selection = getSelection();
                if (selection instanceof IStructuredSelection) {
                    IStructuredSelection ss = (IStructuredSelection) selection;
                    if (ss.size() == 1) {
                        Object element = ss.getFirstElement();
                        if (element instanceof MyDiffNode) {
                            ITypedElement te = ((MyDiffNode) element).getId();
                            if (te != null) {
                                enable = !ITypedElement.FOLDER_TYPE.equals(te.getType());
                            }
                        } else {
                            enable = true;
                        }
                    }
                }
                fOpenAction.setEnabled(enable);

                manager.add(fOpenAction);

                super.fillContextMenu(manager);
            }
        };
        fDiffViewer.getControl();
        return fDiffViewer;
    }

    class SelectAncestorDialog extends MessageDialog {

        private IResource[] theResources;

        IResource ancestorResource;

        IResource leftResource;

        IResource rightResource;

        private Button[] buttons;

        public SelectAncestorDialog(Shell parentShell, IResource[] theResources) {
            super(parentShell, "", null, "", MessageDialog.QUESTION, new String[] { IDialogConstants.OK_LABEL,//$NON-NLS-1$//$NON-NLS-2$
                    IDialogConstants.CANCEL_LABEL }, 0);
            this.theResources = theResources;
        }

        @Override
        protected Control createCustomArea(Composite parent) {
            Composite composite = new Composite(parent, SWT.NONE);
            composite.setLayout(new GridLayout());
            buttons = new Button[3];
            for (int i = 0; i < 3; i++) {
                buttons[i] = new Button(composite, SWT.RADIO);
                buttons[i].addSelectionListener(selectionListener);
                buttons[i].setText("custom");//$NON-NLS-1$
                buttons[i].setFont(parent.getFont());
                // set initial state
                buttons[i].setSelection(i == 0);
            }
            pickAncestor(0);
            return composite;
        }

        private void pickAncestor(int i) {
            ancestorResource = theResources[i];
            leftResource = theResources[i == 0 ? 1 : 0];
            rightResource = theResources[i == 2 ? 1 : 2];
        }

        private SelectionListener selectionListener = new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                Button selectedButton = (Button) e.widget;
                if (!selectedButton.getSelection()) {
                    return;
                }
                for (int i = 0; i < 3; i++) {
                    if (selectedButton == buttons[i]) {
                        pickAncestor(i);
                    }
                }
            }
        };
    }

    public CompareHeadInfo getCompareHeadInfo() {
        return compareHeadInfo;
    }

    public void setCompareHeadInfo(CompareHeadInfo compareHeadInfo) {
        this.compareHeadInfo = compareHeadInfo;
    }

    // If the compare is three-way, this method asks the user which resource
    // to use as the ancestor. Returns false if the user cancels the prompt,
    // true otherwise.
    public boolean setSelection(ISelection s, Shell shell) {

        IResource[] selection = Utilities.getResources(s);

        fThreeWay = selection.length == 3;

        if (fThreeWay) {
            SelectAncestorDialog dialog = new SelectAncestorDialog(shell, selection);
            int code = dialog.open();
            if (code == Window.CANCEL) {
                return false;
            }

            fAncestorResource = dialog.ancestorResource;
            fAncestor = getStructure(fAncestorResource);
            fLeftResource = dialog.leftResource;
            fRightResource = dialog.rightResource;
        } else {
            fAncestorResource = null;
            fAncestor = null;
            fLeftResource = selection[0];
            fRightResource = selection[1];
        }

        fLeft = getStructure(fLeftResource);
        fRight = getStructure(fRightResource);
        return true;
    }

    /*
     * Returns true if compare can be executed for the given selection.
     */
    public boolean isEnabled(ISelection s) {

        IResource[] selection = Utilities.getResources(s);
        if (selection.length < 2 || selection.length > 3) {
            return false;
        }

        boolean threeWay = selection.length == 3;

        if (threeWay) {
            // It only makes sense if they're all mutually comparable.
            // If not, the user should compare two of them.
            return comparable(selection[0], selection[1]) && comparable(selection[0], selection[2])
                    && comparable(selection[1], selection[2]);
        }

        return comparable(selection[0], selection[1]);
    }

    /**
     * Initializes the images in the compare configuration.
     */
    void initializeCompareConfiguration() {
        CompareConfiguration cc = getCompareConfiguration();
        if (fLeftResource != null) {
            cc.setLeftLabel(buildLabel(fLeftResource));
            // cc.setLeftImage(CompareUIPlugin.getImage(fLeftResource));
        }
        if (fRightResource != null) {
            cc.setRightLabel(buildLabel(fRightResource));
            // cc.setRightImage(CompareUIPlugin.getImage(fRightResource));
        }
        if (fThreeWay && fAncestorResource != null) {
            cc.setAncestorLabel(buildLabel(fAncestorResource));
            // cc.setAncestorImage(CompareUIPlugin.getImage(fAncestorResource));
        }
    }

    /*
     * Returns true if both resources are either structured or unstructured.
     */
    private boolean comparable(IResource c1, IResource c2) {
        return hasStructure(c1) == hasStructure(c2);
    }

    /*
     * Returns true if the given argument has a structure.
     */
    private boolean hasStructure(IResource input) {

        if (input instanceof IContainer) {
            return true;
        }

        if (input instanceof IFile) {
            IFile file = (IFile) input;
            String type = file.getFileExtension();
            if (type != null) {
                type = normalizeCase(type);
                return "JAR".equals(type) || "ZIP".equals(type); //$NON-NLS-2$ //$NON-NLS-1$
            }
        }

        return false;
    }

    /*
     * Creates a <code>IStructureComparator</code> for the given input. Returns <code>null</code> if no
     * <code>IStructureComparator</code> can be found for the <code>IResource</code>.
     */
    private IStructureComparator getStructure(IResource input) {

        if (input instanceof IContainer) {
            return new FilteredBufferedResourceNode(input);
        }

        if (input instanceof IFile) {
            IStructureComparator rn = new FilteredBufferedResourceNode(input);
            IFile file = (IFile) input;
            String type = normalizeCase(file.getFileExtension());
            if ("JAR".equals(type) || "ZIP".equals(type)) {
                return new ZipFileStructureCreator().getStructure(rn);
            }
            return rn;
        }
        return null;
    }

    /*
     * Performs a two-way or three-way diff on the current selection.
     */
    @Override
    public Object prepareInput(IProgressMonitor pm) throws InvocationTargetException {

        try {
            // fix for PR 1GFMLFB: ITPUI:WIN2000 - files that are out of sync with the file system appear as empty
            fLeftResource.refreshLocal(IResource.DEPTH_INFINITE, pm);
            fRightResource.refreshLocal(IResource.DEPTH_INFINITE, pm);
            if (fThreeWay && fAncestorResource != null) {
                fAncestorResource.refreshLocal(IResource.DEPTH_INFINITE, pm);
                // end fix
            }

            //pm.beginTask(Utilities.getString("ResourceCompare.taskName"), IProgressMonitor.UNKNOWN); //$NON-NLS-1$

            String leftLabel = fLeftResource.getName();
            String rightLabel = fRightResource.getName();

            String title;
            // if (fThreeWay) {
            //				String format= Utilities.getString("ResourceCompare.threeWay.title"); //$NON-NLS-1$
            // String ancestorLabel= fAncestorResource.getName();
            // title= MessageFormat.format(format, new String[] {ancestorLabel, leftLabel, rightLabel});
            // } else {
            //				String format= Utilities.getString("ResourceCompare.twoWay.title"); //$NON-NLS-1$
            // title= MessageFormat.format(format, new String[] {leftLabel, rightLabel});
            // }
            // setTitle(title);

            Differencer d = new Differencer() {

                @Override
                protected Object visit(Object parent, int description, Object ancestor, Object left, Object right) {
                    return new MyDiffNode((IDiffContainer) parent, description, (ITypedElement) ancestor, (ITypedElement) left,
                            (ITypedElement) right);
                }
            };

            fRoot = d.findDifferences(fThreeWay, pm, null, fAncestor, fLeft, fRight);
            return fRoot;

        } catch (CoreException ex) {
            throw new InvocationTargetException(ex);
        } finally {
            pm.done();
        }
    }

    @Override
    public String getToolTipText() {
        // if (fLeftResource != null && fRightResource != null) {
        // String leftLabel= fLeftResource.getFullPath().makeRelative().toString();
        // String rightLabel= fRightResource.getFullPath().makeRelative().toString();
        // if (fThreeWay) {
        //				String format= Utilities.getString("ResourceCompare.threeWay.tooltip"); //$NON-NLS-1$
        // String ancestorLabel= fAncestorResource.getFullPath().makeRelative().toString();
        // return MessageFormat.format(format, new String[] {ancestorLabel, leftLabel, rightLabel});
        // }
        //			String format= Utilities.getString("ResourceCompare.twoWay.tooltip"); //$NON-NLS-1$
        // return MessageFormat.format(format, new String[] {leftLabel, rightLabel});
        // }
        // fall back
        return super.getToolTipText();
    }

    private String buildLabel(IResource r) {
        String n = r.getFullPath().toString();
        if (n.charAt(0) == IPath.SEPARATOR) {
            return n.substring(1);
        }
        return n;
    }

    @Override
    public boolean isSaveNeeded() {
        if (compareHeadInfo == null) {
            return false;
        }
        if (fRoot instanceof MyDiffNode) {
            return ((MyDiffNode) fRoot).fDirty;
        }
        return false;
    }

    @Override
    public void saveChanges(IProgressMonitor pm) throws CoreException {
        super.saveChanges(pm);
        if (fRoot instanceof DiffNode) {
            try {
                commit(pm, (DiffNode) fRoot);
            } finally {
                if (fDiffViewer != null) {
                    fDiffViewer.refresh();
                }
                setDirty(false);
                if (fRoot instanceof MyDiffNode) {
                    ((MyDiffNode) fRoot).fDirty = false;
                }
            }
        }
    }

    /*
     * Recursively walks the diff tree and commits all changes.
     */
    private void commit(IProgressMonitor pm, DiffNode node) throws CoreException {

        if (node instanceof MyDiffNode) {
            ((MyDiffNode) node).clearDirty();
        }

        ITypedElement left = node.getLeft();
        if (left instanceof BufferedResourceNode) {
            ((BufferedResourceNode) left).commit(pm);
        }

        ITypedElement right = node.getRight();
        if (right instanceof BufferedResourceNode) {
            ((BufferedResourceNode) right).commit(pm);
        }

        IDiffElement[] children = node.getChildren();
        if (children != null) {
            for (IDiffElement element : children) {
                if (element instanceof DiffNode) {
                    commit(pm, (DiffNode) element);
                }
            }
        }

        if (this.compareHeadInfo != null) {
            commitToDB();
        }
    }

    private void commitToDB() {

        try {
            String toCommitContent = CompareManager.getInstance().getLeftContent();
            toCommitContent = XmlUtil.formatCompact(toCommitContent, "UTF-8");//$NON-NLS-1$
            if (this.compareHeadInfo.isItem()) {
                Util.getMDMService(compareHeadInfo.getXobject()).putItem(
                        new WSPutItem(false, (WSDataClusterPK) compareHeadInfo.getXobject().getWsKey(), ""//$NON-NLS-1$
                                .equals(compareHeadInfo.getDataModelName()) ? null : new WSDataModelPK(compareHeadInfo
                                        .getDataModelName()), toCommitContent));
            } else {
                // TODO add support for Object(s)

            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (!Util.handleConnectionException((Shell) null, e, null)) {
                MessageDialog.openError(null, Messages._Error,
                        Messages.bind(Messages.ResourceCompareInput_ErrorMsg, e.getLocalizedMessage()));
            }
        }

    }

    /*
     * (non Javadoc) see IAdaptable.getAdapter
     */
    @Override
    public Object getAdapter(Class adapter) {
        if (IFile.class.equals(adapter)) {
            IProgressMonitor pm = new NullProgressMonitor();
            // flush changes in any dirty viewer
            flushViewers(pm);
            IFile[] files = (IFile[]) getAdapter(IFile[].class);
            if (files != null && files.length > 0) {
                return files[0]; // can only return one: limitation on IDE.saveAllEditors; see #64617
            }
            return null;
        }
        if (IFile[].class.equals(adapter)) {
            HashSet collector = new HashSet();
            collectDirtyResources(fRoot, collector);
            return collector.toArray(new IFile[collector.size()]);
        }
        return super.getAdapter(adapter);
    }

    private void collectDirtyResources(Object o, Set collector) {
        if (o instanceof DiffNode) {
            DiffNode node = (DiffNode) o;

            ITypedElement left = node.getLeft();
            if (left instanceof BufferedResourceNode) {
                BufferedResourceNode bn = (BufferedResourceNode) left;
                if (bn.isDirty()) {
                    IResource resource = bn.getResource();
                    if (resource instanceof IFile) {
                        collector.add(resource);
                    }
                }
            }

            ITypedElement right = node.getRight();
            if (right instanceof BufferedResourceNode) {
                BufferedResourceNode bn = (BufferedResourceNode) right;
                if (bn.isDirty()) {
                    IResource resource = bn.getResource();
                    if (resource instanceof IFile) {
                        collector.add(resource);
                    }
                }
            }

            IDiffElement[] children = node.getChildren();
            if (children != null) {
                for (IDiffElement element : children) {
                    if (element instanceof DiffNode) {
                        collectDirtyResources(element, collector);
                    }
                }
            }
        }
    }

    private static String normalizeCase(String s) {
        if (NORMALIZE_CASE && s != null) {
            return s.toUpperCase();
        }
        return s;
    }

    @Override
    public boolean canRunAsJob() {
        return true;
    }
}
