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
package org.talend.mdm.workbench.serverexplorer.ui.dialogs;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.model.mdmmetadata.MDMServerDef;
import org.talend.mdm.repository.model.mdmproperties.MDMServerDefItem;
import org.talend.mdm.workbench.serverexplorer.core.ServerDefService;
import org.talend.mdm.workbench.serverexplorer.i18n.Messages;
import org.talend.mdm.workbench.serverexplorer.ui.providers.ServerSorter;
import org.talend.mdm.workbench.serverexplorer.ui.providers.TreeContentProvider;
import org.talend.mdm.workbench.serverexplorer.ui.providers.ViewerLabelProvider;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class SelectServerDefDialog extends TitleAreaDialog {

    private TreeViewer treeViewer;

    private Button okBun;

    private MDMServerDef serverDef;



    /**
     *
     * @return a decrypted serverDef
     */
    public MDMServerDef getSelectedServerDef() {
        return this.serverDef.getDecryptedServerDef();
    }

    /**
     * Create the dialog.
     *
     * @param parentShell
     */
    public SelectServerDefDialog(Shell parentShell) {
        super(parentShell);
    }

    /**
     * Create contents of the dialog.
     *
     * @param parent
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        setTitle(Messages.SelectServerDefDialog_selectServerLocation);
        Composite area = (Composite) super.createDialogArea(parent);
        Composite container = new Composite(area, SWT.NONE);
        container.setLayout(new FillLayout(SWT.HORIZONTAL));
        container.setLayoutData(new GridData(GridData.FILL_BOTH));

        treeViewer = new TreeViewer(container, SWT.BORDER);
        treeViewer.setSorter(new ServerSorter());

        treeViewer.setContentProvider(new TreeContentProvider());
        treeViewer.setLabelProvider(new ViewerLabelProvider());
        //
        treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            public void selectionChanged(SelectionChangedEvent event) {
                IRepositoryViewObject viewObject = getCurSelectedViewObject();
                if (viewObject == null)
                    return;
                MDMServerDefItem item = (MDMServerDefItem) viewObject.getProperty().getItem();
                serverDef = item.getServerDef();
                okBun.setEnabled(serverDef != null);
            }
        });
        treeViewer.addDoubleClickListener(new IDoubleClickListener() {

            public void doubleClick(DoubleClickEvent event) {
                okPressed();
            }
        });
        //
        initServerDefList();
        return area;
    }

    public void setSelectServer(MDMServerDef server) {
        if (server == null) {
            if (treeViewer.getTree().getItemCount() > 0) {
                TreeItem item = treeViewer.getTree().getItem(0);
                IRepositoryViewObject viewObject = (IRepositoryViewObject) item.getData();
                MDMServerDefItem defitem = (MDMServerDefItem) viewObject.getProperty().getItem();
                MDMServerDef defServer = defitem.getServerDef();
                serverDef = defServer;
                treeViewer.getTree().setSelection(item);
                okBun.setEnabled(true);
                return;
            }
        }
        TreeItem[] items = treeViewer.getTree().getItems();
        for (IRepositoryViewObject viewObject : (List<IRepositoryViewObject>) treeViewer.getInput()) {
            MDMServerDefItem defitem = (MDMServerDefItem) viewObject.getProperty().getItem();
            MDMServerDef defServer = defitem.getServerDef();
            if (server.getName().equals(defServer.getName()) && server.getHost().equals(defServer.getHost())
                    && server.getPort().equals(defServer.getPort())) {
                serverDef = defServer;
                treeViewer.setSelection(new StructuredSelection(viewObject));
                okBun.setEnabled(true);
                break;
            }
        }
    }

    private IRepositoryViewObject getCurSelectedViewObject() {
        ISelection selection = treeViewer.getSelection();
        if (!selection.isEmpty()) {
            Object firstElement = ((TreeSelection) selection).getFirstElement();
            return (IRepositoryViewObject) firstElement;
        }
        return null;
    }

    /**
     * DOC hbhong Comment method "initServerDefList".
     */
    private void initServerDefList() {
        List<IRepositoryViewObject> viewObjects = ServerDefService.getAllServerDefViewObjects();
        if (viewObjects != null) {
            treeViewer.setInput(viewObjects);
        }
    }

    /**
     * Create contents of the button bar.
     *
     * @param parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        this.okBun = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
        okBun.setEnabled(false);
    }

}
