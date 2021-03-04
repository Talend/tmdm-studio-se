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
package org.talend.mdm.repository.ui.dialogs.recycle;

import java.util.Collection;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.IRepositoryNodeConfiguration;
import org.talend.mdm.repository.core.IRepositoryNodeLabelProvider;
import org.talend.mdm.repository.core.command.CommandManager;
import org.talend.mdm.repository.core.command.CommandStack;
import org.talend.mdm.repository.core.command.ICommand;
import org.talend.mdm.repository.core.command.deploy.AbstractDeployCommand;
import org.talend.mdm.repository.extension.RepositoryNodeConfigurationManager;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.models.FolderRepositoryObject;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class WaitToDeployDialog extends Dialog {

    private static class ViewerLabelProvider extends LabelProvider {

        public Image getImage(Object element) {
            if (element instanceof IRepositoryViewObject) {

                IRepositoryViewObject viewObj = (IRepositoryViewObject) element;
                ERepositoryObjectType type = viewObj.getRepositoryObjectType();

                IRepositoryNodeConfiguration configuration = RepositoryNodeConfigurationManager.getConfiguration(type);
                if (configuration != null) {
                    IRepositoryNodeLabelProvider labelProvider = configuration.getLabelProvider();
                    return labelProvider.getImage(element);
                }
            }
            return null;
        }

        public String getText(Object element) {
            if (element instanceof IRepositoryViewObject) {
                return ((IRepositoryViewObject) element).getLabel();
            }
            return super.getText(element);
        }
    }

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
            if (parentElement instanceof FolderRepositoryObject) {
                return ((FolderRepositoryObject) parentElement).getChildren().toArray();
            }
            if (parentElement instanceof Object[]) {
                return (Object[]) parentElement;
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

    private final List<IRepositoryViewObject> viewObjs;

    private TreeViewer treeViewer;

    /**
     * Create the dialog.
     *
     * @param parentShell
     */
    public WaitToDeployDialog(Shell parentShell, List<IRepositoryViewObject> viewObjs) {
        super(parentShell);
        this.viewObjs = viewObjs;
    }

    public boolean needShowDialog() {
        return needShowDialog(viewObjs);
    }

    private boolean needShowDialog(List<IRepositoryViewObject> objs) {
        for (IRepositoryViewObject viewObj : objs) {
            if (viewObj instanceof FolderRepositoryObject) {
                boolean result = needShowDialog(((FolderRepositoryObject) viewObj).getChildren());
                if (result)
                    return true;
            } else {
                boolean result = needShowObject(viewObj);
                if (result)
                    return true;
            }
        }
        return false;
    }

    private boolean needShowObject(IRepositoryViewObject viewObj) {
        String id = viewObj.getId();
        CommandStack commandStack = CommandManager.getInstance().findCommandStack(id);
        if (commandStack != null) {
            ICommand validCommand = commandStack.getValidDeployCommand();
            if (validCommand instanceof AbstractDeployCommand) {
                if (validCommand.getCommandType() == ICommand.CMD_DELETE) {
                    return RepositoryResourceUtil.getLastServerDef(viewObj) != null;
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Create contents of the dialog.
     *
     * @param parent
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);

        Label lblNewLabel = new Label(container, SWT.WRAP);
        lblNewLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        lblNewLabel.setText(Messages.WaitToDeployDialog_message);

        treeViewer = new TreeViewer(container, SWT.BORDER);
        Tree tree = treeViewer.getTree();
        tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        treeViewer.setLabelProvider(new ViewerLabelProvider());
        treeViewer.setContentProvider(new TreeContentProvider());
        treeViewer.addFilter(new ViewerFilter() {

            @Override
            public boolean select(Viewer viewer, Object parentElement, Object element) {
                if (element instanceof IRepositoryViewObject) {
                    if (element instanceof FolderRepositoryObject) {
                        return needShowDialog(((FolderRepositoryObject) element).getChildren());
                    } else {
                        return needShowObject((IRepositoryViewObject) element);
                    }
                }
                return false;
            }
        });
        treeViewer.setInput(viewObjs);
        treeViewer.expandAll();
        return container;
    }

    /**
     * Create contents of the button bar.
     *
     * @param parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    /**
     * Return the initial size of the dialog.
     */
    @Override
    protected Point getInitialSize() {
        return new Point(450, 300);
    }

}
