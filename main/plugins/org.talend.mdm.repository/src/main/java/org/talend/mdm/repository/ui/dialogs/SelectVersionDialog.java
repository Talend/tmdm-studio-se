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
package org.talend.mdm.repository.ui.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.model.repository.RepositoryObject;
import org.talend.designer.core.ui.views.jobsettings.tabs.ProcessVersionComposite;
import org.talend.repository.model.RepositoryNode;

/**
 * DOC Administrator class global comment. Detailled comment
 */
public class SelectVersionDialog extends Dialog {

    private IRepositoryViewObject viewObj;

    private String title;

    private ProcessVersionComposite versionListComposite;

    /**
     * DOC Administrator SelectVersionDialog constructor comment.
     *
     * @param parentShell
     */
    public SelectVersionDialog(Shell parentShell, String title, IRepositoryViewObject viewObject) {
        super(parentShell);
        this.title = title;
        this.viewObj = viewObject;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        parent.getShell().setText(title);
        Composite composite = (Composite) super.createDialogArea(parent);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        composite.setLayout(new GridLayout());
        versionListComposite = new ProcessVersionComposite(composite, SWT.NULL, null, viewObj);
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.heightHint = 150;
        gd.widthHint = 450;
        versionListComposite.setLayoutData(gd);
        versionListComposite.getTableViewer().addSelectionChangedListener(new ISelectionChangedListener() {

            public void selectionChanged(SelectionChangedEvent event) {
                getButton(IDialogConstants.OK_ID).setEnabled(true);
            }

        });
        versionListComposite.refresh();

        return composite;
    }

    public IRepositoryViewObject getSelection() {
        StructuredSelection selection = (StructuredSelection) versionListComposite.getSelection();
        if (selection.getFirstElement() instanceof RepositoryNode) {
            RepositoryNode node = (RepositoryNode) selection.getFirstElement();
            RepositoryObject repositoryObj = new RepositoryObject(node.getObject().getProperty());
            return repositoryObj;
        }
        return null;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);
        getButton(IDialogConstants.OK_ID).setEnabled(false);
    }

}
