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
package org.talend.mdm.repository.ui.dialogs.message;

import java.util.Collection;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.talend.mdm.repository.core.command.ICommand;
import org.talend.mdm.repository.core.service.DeployService.DeployCategoryStatus;
import org.talend.mdm.repository.core.service.DeployService.DeployStatus;
import org.talend.mdm.repository.plugin.RepositoryPlugin;
import org.talend.mdm.repository.utils.EclipseResourceManager;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class MultiStatusDialog extends Dialog {

    private class TreeContentProvider implements ITreeContentProvider {

        public void dispose() {
        }

        public Object[] getChildren(Object parentElement) {
            if (parentElement instanceof Object[]) {
                return (Object[]) parentElement;
            }
            if (parentElement instanceof Collection) {
                return ((Collection) parentElement).toArray();
            }
            if (parentElement instanceof MultiStatus) {
                return ((MultiStatus) parentElement).getChildren();
            }
            return new Object[0];
        }

        public Object[] getElements(Object inputElement) {
            return getChildren(inputElement);
        }

        public Object getParent(Object element) {
            return null;
        }

        public boolean hasChildren(Object element) {
            return getChildren(element).length > 0;
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }

    private class ViewerLabelProvider extends LabelProvider {

        @Override
        public Image getImage(Object element) {
            if (element instanceof DeployStatus) {
                DeployStatus status = (DeployStatus) element;
                int severity = status.getSeverity();
                if (severity == IStatus.OK) {
                    ICommand command = status.getCommand();
                    switch (command.getCommandType()) {
                    case ICommand.CMD_ADD:
                        return IMG_ADD;
                    case ICommand.CMD_DELETE:
                        return IMG_DELETE;
                    case ICommand.CMD_MODIFY:
                        return IMG_OK;
                    }
                } else if (severity == IStatus.INFO) {
                    return IMG_INFO;
                } else if (severity == IStatus.ERROR) {
                    return IMG_ERR;
                }
            } else if (element instanceof DeployCategoryStatus) {
                return null;
            } else if (element instanceof Status || element instanceof MultiStatus) {
                Status status = (Status) element;
                int severity = status.getSeverity();
                if (severity == IStatus.OK) {
                    return IMG_OK;
                } else if (severity == IStatus.INFO) {
                    return IMG_INFO;
                } else if (severity == IStatus.ERROR) {
                    return IMG_ERR;
                }
            }

            return null;
        }

        @Override
        public String getText(Object element) {
            if (element instanceof IStatus) {
                IStatus status = (IStatus) element;
                if (status.isMultiStatus()) {
                    return getStatusLabel(status);
                } else {
                    return status.getMessage();
                }
            }
            return ""; //$NON-NLS-1$
        }
    }

    private static final Image IMG_ADD = EclipseResourceManager.getImage(RepositoryPlugin.PLUGIN_ID, "/icons/add.gif"); //$NON-NLS-1$

    private static final Image IMG_DELETE = EclipseResourceManager.getImage(RepositoryPlugin.PLUGIN_ID,
            "/icons/recycle_bin_empty.png"); //$NON-NLS-1$

    private static final Image IMG_ERR = EclipseResourceManager.getImage(RepositoryPlugin.PLUGIN_ID, "/icons/forbidden.png"); //$NON-NLS-1$

    private static final Image IMG_INFO = EclipseResourceManager.getImage(RepositoryPlugin.PLUGIN_ID, "/icons/info_obj.gif"); //$NON-NLS-1$

    private static final Image IMG_OK = EclipseResourceManager.getImage(RepositoryPlugin.PLUGIN_ID, "/icons/check.png"); //$NON-NLS-1$

    private final String message;

    private Label msgLabel;

    protected final IStatus multiStatus;

    private Tree tree;

    private TreeViewer treeViewer;

    /**
     * Create the dialog.
     *
     * @param parentShell
     */
    public MultiStatusDialog(Shell parentShell, String message, IStatus mutliStatus) {
        super(parentShell);
        //
        this.message = message;
        this.multiStatus = initMultiStatus(mutliStatus);
        //
        this.setShellStyle(getShellStyle() | SWT.RESIZE);
    }

    protected String getStatusLabel(IStatus status) {
        return status.getMessage();

    }

    protected boolean needShowBottomMessage() {
        return false;
    }

    protected String getBottomMessage() {
        return null;
    }

    /**
     * DOC talend-mdm Comment method "initMultiStatus".
     *
     * @param mutliStatus2
     * @return
     */
    protected IStatus initMultiStatus(IStatus multiStatus) {
        return multiStatus;
    }

    /**
     * Create contents of the button bar.
     *
     * @param parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
    }

    /**
     * Create contents of the dialog.
     *
     * @param parent
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        container.setLayout(new GridLayout(1, false));

        msgLabel = new Label(container, SWT.WRAP);
        GridData gd_msgLabel = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
        gd_msgLabel.horizontalIndent = 10;
        gd_msgLabel.verticalIndent = 10;
        gd_msgLabel.heightHint = 40;
        msgLabel.setLayoutData(gd_msgLabel);

        treeViewer = new TreeViewer(container, SWT.BORDER | SWT.FULL_SELECTION);
        tree = treeViewer.getTree();
        tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        treeViewer.setContentProvider(new TreeContentProvider());
        treeViewer.setLabelProvider(new ViewerLabelProvider());
        buildBottomArea(container);
        initInput();

        return container;
    }

    protected void buildBottomArea(Composite container) {
        // do nothing
    }

    /**
     * Return the initial size of the dialog.
     */
    @Override
    protected Point getInitialSize() {
        return new Point(450, 300);
    }

    protected String getMessage() {
        if (message != null) {
            return message;
        }
        return ""; //$NON-NLS-1$
    }

    /**
     * DOC hbhong Comment method "initInput".
     */
    protected void initInput() {
        msgLabel.setText(getMessage());
        //
        if (multiStatus != null && multiStatus.isMultiStatus()) {
            treeViewer.setInput(multiStatus.getChildren());
        }

    }

}
