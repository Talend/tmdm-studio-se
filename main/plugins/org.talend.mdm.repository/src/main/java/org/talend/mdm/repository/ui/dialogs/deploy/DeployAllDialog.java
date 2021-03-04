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
package org.talend.mdm.repository.ui.dialogs.deploy;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.command.CommandManager;
import org.talend.mdm.repository.core.command.deploy.AbstractDeployCommand;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.model.mdmmetadata.MDMServerDef;
import org.talend.mdm.repository.ui.widgets.RepositoryViewObjectCheckedWidget;
import org.talend.mdm.workbench.serverexplorer.core.ServerDefService;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class DeployAllDialog extends Dialog {

    private MDMServerDef serverDef;

    private ERepositoryObjectType initType;

    public DeployAllDialog(Shell parentShell, ERepositoryObjectType type) {
        super(parentShell);
        this.initType = type;
        setShellStyle(getShellStyle() | SWT.RESIZE);

    }

    /**
     * Create contents of the dialog.
     *
     * @param parent
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        mainContainer = (Composite) super.createDialogArea(parent);
        GridLayout mainContainerLayout = (GridLayout) mainContainer.getLayout();
        mainContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        mainContainerLayout.numColumns = 2;

        Label lblNewLabel_selserver = new Label(mainContainer, SWT.NONE);
        lblNewLabel_selserver.setText(Messages.DeployAllDialog_label_selectserver);
        lblNewLabel_selserver.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));

        errorLabel = new Label(mainContainer, SWT.NONE);
        errorLabelLayoutData = new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1);
        errorLabelLayoutData.exclude = true;
        errorLabel.setLayoutData(errorLabelLayoutData);
        errorLabel.setForeground(getShell().getDisplay().getSystemColor(SWT.COLOR_RED));

        comboViewer = new ComboViewer(mainContainer, SWT.DROP_DOWN | SWT.READ_ONLY);

        Combo combo = comboViewer.getCombo();
        GridData data = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);

        combo.setLayoutData(data);
        comboViewer.setLabelProvider(new LabelProvider() {

            @Override
            public String getText(Object element) {
                MDMServerDef serverDef = (MDMServerDef) element;
                return serverDef.getName() + " (" + serverDef.getHost() + ")"; //$NON-NLS-1$ //$NON-NLS-2$;
            }
        });
        comboViewer.setContentProvider(new ArrayContentProvider());
        Label emptyLabel = new Label(mainContainer, SWT.NONE);
        GridData emptydata = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
        emptydata.widthHint = 98;
        emptyLabel.setLayoutData(emptydata);
        List<MDMServerDef> allServerDefs = ServerDefService.getAllServerDefs();
        comboViewer.setInput(allServerDefs);
        comboViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            public void selectionChanged(SelectionChangedEvent event) {
                serverDef = (MDMServerDef) ((IStructuredSelection) comboViewer.getSelection()).getFirstElement();
                treeViewer.updateCurrentServerDef(null, serverDef);
                updateErrorMsgArea(treeViewer.isServerOk());
            }
        });
        final Button reconciliationBun = new Button(mainContainer, SWT.CHECK);

        reconciliationBun.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                treeViewer.enableReconciliation(reconciliationBun.getSelection());
            }
        });
        reconciliationBun.setText(Messages.DeployAllDialog_reconciliation);
        reconciliationBun.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));

        Composite container = new Composite(mainContainer, SWT.BORDER);
        container.setLayout(new GridLayout(2, false));
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
        Label lblNewLabel = new Label(container, SWT.NONE);
        lblNewLabel.setText(Messages.DeployAllDialog_label);
        new Label(container, SWT.NONE);

        List<AbstractDeployCommand> commands = CommandManager.getInstance().getAllDeployCommands();
        treeViewer = new RepositoryViewObjectCheckedWidget(container, initType, commands);

        treeViewer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3));
        Button selAllButton = new Button(container, SWT.NONE);
        selAllButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                treeViewer.selectAll(true);
            }
        });

        selAllButton.setText(Messages.DeployAllDialog_selectAll);
        Button deselAllBun = new Button(container, SWT.NONE);
        deselAllBun.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        deselAllBun.setText(Messages.DeployAllDialog_deselectAll);
        deselAllBun.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                treeViewer.selectAll(false);
            }
        });

        Button skipDeployedBun = new Button(container, SWT.NONE);
        skipDeployedBun.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        skipDeployedBun.setText(Messages.DeployAllDialog_skipDeployed);
        skipDeployedBun.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                treeViewer.skipDeployedObjects();
            }
        });

        initComboSelect();
        return container;
    }

    private void initComboSelect() {
        MDMServerDef sameServerDef = treeViewer.getSameServerDef();
        List<MDMServerDef> serverDefs = (List<MDMServerDef>) comboViewer.getInput();
        MDMServerDef defaultServerDef = null;
        if (serverDefs != null) {
            if (sameServerDef != null) {
                for (MDMServerDef def : serverDefs) {
                    if (def.getName().equals(sameServerDef.getName())) {
                        defaultServerDef = def;
                        break;
                    }
                }
            }
            if (defaultServerDef == null && serverDefs.size() > 0) {
                defaultServerDef = serverDefs.get(0);
            }

        }
        if (defaultServerDef != null) {
            comboViewer.setSelection(new StructuredSelection(defaultServerDef));
        }
    }

    public IRepositoryViewObject getViewObjectByType(IRepositoryViewObject[] theInput, ERepositoryObjectType type) {
        for (IRepositoryViewObject viewObj : theInput) {
            if (viewObj.getRepositoryObjectType().equals(type)) {
                return viewObj;
            }
        }
        return null;
    }

    private void updateErrorMsgArea(boolean isServerOk) {
        errorLabelLayoutData.exclude = isServerOk;
        errorLabel.setVisible(!isServerOk);
        if (!isServerOk) {
            String url = serverDef.getProtocol() + serverDef.getHost() + ":" + serverDef.getPort() //$NON-NLS-1$
                    + serverDef.getPath();
            errorLabel.setText(Messages.bind(Messages.UnableConnectToServer, url));
        }
        mainContainer.layout();
    }

    /**
     * Create contents of the button bar.
     *
     * @param parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        okBun = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    /**
     * Return the initial size of the dialog.
     */
    @Override
    protected Point getInitialSize() {
        return new Point(800, 500);
    }

    List<AbstractDeployCommand> selectedCommands;

    private RepositoryViewObjectCheckedWidget treeViewer;

    private Button okBun;

    private ComboViewer comboViewer;

    private Label errorLabel;

    private GridData errorLabelLayoutData;

    private Composite mainContainer;

    public List<AbstractDeployCommand> getSelectedCommands() {
        return this.selectedCommands;
    }

    @Override
    protected void okPressed() {
        selectedCommands = treeViewer.getSelectedCommands();
        if (!doCheck()) {
            return;
        }
        super.okPressed();
    }

    private boolean doCheck() {
        String errorMsg = null;
        if (serverDef == null) {
            errorMsg = Messages.DeployAllDialog_chooseServerFirst;
        }
        if (selectedCommands == null || selectedCommands.isEmpty()) {
            errorMsg = Messages.DeployAllDialog_selectMoreItem;
        }
        if (errorMsg != null) {
            MessageDialog.openWarning(getShell(), Messages.DeployAllDialog_warning, errorMsg);
            return false;
        }
        return true;

    }

    private void enableOkBun() {
        okBun.setEnabled(treeViewer.getSelectededViewObjs().size() > 0);
    }

    public MDMServerDef getServerDef() {
        return this.serverDef;
    }

}
