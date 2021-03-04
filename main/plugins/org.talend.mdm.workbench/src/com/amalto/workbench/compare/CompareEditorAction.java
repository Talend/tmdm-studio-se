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
package com.amalto.workbench.compare;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.compare.BufferedContent;
import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.CompareUI;
import org.eclipse.compare.IModificationDate;
import org.eclipse.compare.IStreamContentAccessor;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.compare.structuremergeviewer.Differencer;
import org.eclipse.compare.structuremergeviewer.IDiffContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * just for test
 *
 * @author achen
 *
 */
public class CompareEditorAction implements IWorkbenchWindowActionDelegate {

    private static Log log = LogFactory.getLog(CompareEditorAction.class);

    CompareConfiguration cc = new CompareConfiguration();

    public void run(IAction action) {
        cc.setLeftEditable(true);
        cc.setRightEditable(false);
        // cc.setAncestorLabel("ancestor");
        IProject prj = CompareManager.getInstance().createProject("test1111");//$NON-NLS-1$
        IFile leftF = prj.getFile("left");//$NON-NLS-1$
        IFile rightF = prj.getFile("right");//$NON-NLS-1$
        String left = "aaaaaaaaa\n\nee";//$NON-NLS-1$
        String right = ";dfeadf\n";//$NON-NLS-1$
        try {
            leftF.create(new ByteArrayInputStream(left.getBytes()), IFile.FORCE, null);
            rightF.create(new ByteArrayInputStream(right.getBytes()), IFile.FORCE, null);
        } catch (CoreException e) {
            log.error(e.getMessage(), e);
        }

        ResourceCompareInput input = new ResourceCompareInput(cc);
        StructuredSelection sel = new StructuredSelection(new IFile[] { leftF, rightF });
        input.setSelection(sel, null);
        CompareUI.openCompareEditor(input);
        // CompareUI.openCompareEditor(new MyCompareInput(cc));
    }

    class CompareInput extends CompareEditorInput {

        public CompareInput() {
            super(cc);
        }

        protected Object prepareInput(IProgressMonitor pm) {
            CompareItem ancestor = new CompareItem("Common", "contents");//$NON-NLS-1$//$NON-NLS-2$
            CompareItem left = new CompareItem("Left", "new contents\nadd\n\ndddddd");//$NON-NLS-1$//$NON-NLS-2$
            CompareItem right = new CompareItem("Right", "old contents");//$NON-NLS-1$//$NON-NLS-2$
            return new DiffNode(null, Differencer.CONFLICTING, ancestor, left, right);
        }

        @Override
        public void saveChanges(IProgressMonitor monitor) throws CoreException {

            super.saveChanges(monitor);
        }

        @Override
        public boolean isSaveNeeded() {
            return true;
        }
    }

    class CompareItem implements IStreamContentAccessor, ITypedElement, IModificationDate {

        private String contents, name;

        private long time;

        CompareItem(String name, String contents, long time) {
            this.name = name;
            this.contents = contents;
            this.time = time;
        }

        CompareItem(String name, String contents) {
            this(name, contents, System.currentTimeMillis());
        }

        public InputStream getContents() throws CoreException {
            return new ByteArrayInputStream(contents.getBytes());
        }

        public Image getImage() {
            return null;
        }

        public long getModificationDate() {
            return time;
        }

        public String getName() {
            return name;
        }

        public String getString() {
            return contents;
        }

        public String getType() {
            return ITypedElement.TEXT_TYPE;
        }
    }

    public void dispose() {


    }

    public void init(IWorkbenchWindow window) {


    }

    public void selectionChanged(IAction action, ISelection selection) {


    }

    class MyCompareInput extends CompareEditorInput {

        public MyCompareInput(CompareConfiguration configuration) {
            super(configuration);
            // TODO Auto-generated constructor stub
        }

        @Override
        protected Object prepareInput(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
            Differencer d = new Differencer() {

                protected Object visit(Object parent, int description, Object ancestor, Object left, Object right) {
                    return new MyDiffNode((IDiffContainer) parent, description, (ITypedElement) ancestor, (ITypedElement) left,
                            (ITypedElement) right);
                }
            };
            MyCompareNode ancester = new MyCompareNode("aaaaaaaaa");//$NON-NLS-1$
            MyCompareNode left = new MyCompareNode("aaaaaaaaa");//$NON-NLS-1$
            MyCompareNode right = new MyCompareNode("aaaaaaaaa\nnd\neefaa");//$NON-NLS-1$
            return d.findDifferences(true, null, null, ancester, left, right);
        }

        @Override
        public void saveChanges(IProgressMonitor monitor) throws CoreException {

            super.saveChanges(monitor);
        }

        @Override
        public boolean isDirty() {

            return super.isDirty();
        }

        class MyDiffNode extends DiffNode {

            private boolean fDirty = false;

            private ITypedElement fLastId;

            private String fLastName;

            public MyDiffNode(IDiffContainer parent, int description, ITypedElement ancestor, ITypedElement left,
                    ITypedElement right) {
                super(parent, description, ancestor, left, right);
            }

            public void fireChange() {
                super.fireChange();
                setDirty(true);
                fDirty = true;

            }

            void clearDirty() {
                fDirty = false;
            }

            public String getName() {
                if (fLastName == null)
                    fLastName = super.getName();
                if (fDirty)
                    return '<' + fLastName + '>';
                return fLastName;
            }

            public ITypedElement getId() {
                ITypedElement id = super.getId();
                if (id == null)
                    return fLastId;
                fLastId = id;
                return id;
            }
        }

        class MyCompareNode extends BufferedContent implements ITypedElement {

            private String fResource;

            MyCompareNode(String resource) {
                fResource = resource;
            }

            protected InputStream createStream() throws CoreException {
                InputStream is = null;
                is = new ByteArrayInputStream(fResource.getBytes());
                return is;
            }

            public Image getImage() {
                return null;
            }

            public String getName() {
                return null;
            }

            public String getType() {
                return ITypedElement.TEXT_TYPE;
            }
        }

    }

}
