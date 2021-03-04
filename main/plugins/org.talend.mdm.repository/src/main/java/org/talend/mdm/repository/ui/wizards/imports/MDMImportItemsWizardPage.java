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
package org.talend.mdm.repository.ui.wizards.imports;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.progress.UIJob;
import org.talend.core.model.properties.ItemState;
import org.talend.core.model.properties.PropertiesFactory;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.mdm.repository.core.IServerObjectRepositoryType;
import org.talend.mdm.repository.core.service.ImportService;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.model.mdmproperties.MdmpropertiesFactory;
import org.talend.mdm.repository.model.mdmproperties.WSDataModelItem;
import org.talend.mdm.repository.model.mdmserverobject.MdmserverobjectFactory;
import org.talend.mdm.repository.model.mdmserverobject.WSDataModelE;
import org.talend.mdm.repository.ui.dialogs.importexchange.ImportExchangeOptionsDialogR;
import org.talend.mdm.repository.ui.navigator.MDMRepositoryView;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;
import org.talend.mdm.repository.utils.RepositoryTransformUtil;
import org.talend.repository.items.importexport.handlers.model.ImportItem;
import org.talend.repository.items.importexport.ui.wizard.imports.ImportItemsWizardPage;
import org.talend.repository.items.importexport.ui.wizard.imports.providers.ImportItemsViewerLabelProvider;
import org.talend.repository.items.importexport.wizard.models.ImportNode;
import org.talend.repository.items.importexport.wizard.models.ItemImportNode;

import com.amalto.workbench.dialogs.ImportExchangeOptionsDialog;
import com.amalto.workbench.exadapter.ExAdapterManager;
import com.amalto.workbench.widgets.WidgetFactory;

/**
 * created by HHB on 2015年5月4日 Detailled comment
 *
 */
public class MDMImportItemsWizardPage extends ImportItemsWizardPage {

    private static Logger log = Logger.getLogger(MDMImportItemsWizardPage.class);

    IMDMImportItemsWizardPageExAdapter exAdapter = null;

    protected StringBuffer zipFileRepository = new StringBuffer();;

    public MDMImportItemsWizardPage(String pageName, IStructuredSelection s) {
        super(pageName, s);
        exAdapter = ExAdapterManager.getAdapter(this, IMDMImportItemsWizardPageExAdapter.class);
    }

    @Override
    protected boolean isEnableForExchange() {
        return true;
    }

    @Override
    protected void createArchiveSelectionArea(Composite selectionArea) {
        super.createArchiveSelectionArea(selectionArea);
        Listener[] listeners = fromExchangeButton.getListeners(SWT.Selection);
        // remove
        fromExchangeButton.removeListener(SWT.Selection, listeners[0]);
        fromExchangeButton.removeListener(SWT.DefaultSelection, listeners[0]);
        fromExchangeButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                exchangeImport();
            }
        });
    }

    @Override
    protected TreeViewer createItemsTreeViewer(Composite parent) {
        TreeViewer treeViewer = super.createItemsTreeViewer(parent);
        treeViewer.setLabelProvider(new MDMImportItemsViewerLabelProvider());
        return treeViewer;
    }

    class MDMImportItemsViewerLabelProvider extends ImportItemsViewerLabelProvider {

        @Override
        public String getText(Object element) {
            if (element instanceof ImportNode) {
                String label = ((ImportNode) element).getDisplayLabel();
                if (element instanceof ItemImportNode) {
                    ImportItem itemRecord = ((ItemImportNode) element).getItemRecord();
                    ERepositoryObjectType repositoryType = itemRecord.getRepositoryType();
                    return filterViewProcessName(label, repositoryType);
                }
                return label;

            }
            return ""; //$NON-NLS-1$
        }

        private String filterViewProcessName(String itemRecordLabel, ERepositoryObjectType type) {
            String filteredName = itemRecordLabel;
            if (filteredName != null && !filteredName.isEmpty()) {

                if (type != null) {
                    if (type == IServerObjectRepositoryType.TYPE_VIEW) {
                        filteredName = RepositoryTransformUtil.getInstance().transformToSilyViewName(filteredName, true);
                    }

                    if (type == IServerObjectRepositoryType.TYPE_TRANSFORMERV2) {
                        filteredName = RepositoryTransformUtil.getInstance().transformToSilyProcessName(filteredName, true);
                    }
                }
            }

            return filteredName;
        }
    }

    @Override
    public boolean performFinish() {
        ImportService.setImporting(true);
        boolean finish = super.performFinish();
        ImportService.setImporting(false);
        refreshRepositoryViewJob();
        rebuildRelations();
        return finish;
    }

    private void rebuildRelations() {
        if (exAdapter != null) {
            exAdapter.rebuildRelations(ImportService.getImportedIds());
        }
    }

    private void refreshRepositoryViewJob() {
        new UIJob(Messages.MDMImportItemsWizardPage_refreshServer) {

            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
                MDMRepositoryView.show().getCommonViewer().refresh();
                return Status.OK_STATUS;
            }

        }.schedule();
    }

    protected ImportExchangeOptionsDialog getExchangeOptionsDialog() {
        FormToolkit toolkit = WidgetFactory.getWidgetFactory();
        ImportExchangeOptionsDialog dlg = new ImportExchangeOptionsDialogR(getShell(), toolkit, true, zipFileRepository);
        dlg.setRadioEnable(true);
        dlg.create();

        return dlg;
    }

    protected void exchangeImport() {
        ImportExchangeOptionsDialog dlg = getExchangeOptionsDialog();
        dlg.setBlockOnOpen(true);
        if (dlg.open() == Window.OK) {

            File directory = new File(zipFileRepository.toString());
            File[] files = directory.listFiles(new FileFilter() {

                public boolean accept(File pathname) {
                    return pathname.getName().endsWith(".xsd"); //$NON-NLS-1$
                }
            });
            if (null != files && files.length > 0) {
                try {
                    MDMRepositoryView view = MDMRepositoryView.show();
                    for (File file : files) {
                        final String label = file.getName().substring(0, file.getName().lastIndexOf('.'));
                        final WSDataModelItem item = MdmpropertiesFactory.eINSTANCE.createWSDataModelItem();
                        ItemState itemState = PropertiesFactory.eINSTANCE.createItemState();
                        item.setState(itemState);
                        WSDataModelE dataModel = MdmserverobjectFactory.eINSTANCE.createWSDataModelE();
                        dataModel.setName(label);
                        InputStream stream = null;
                        try {
                            stream = new FileInputStream(file);
                            dataModel.setXsdSchema(IOUtils.toString(stream));
                            item.setWsDataModel(dataModel);
                        } catch (Exception e) {
                            log.error(e.getMessage());
                        }
                        IOUtils.closeQuietly(stream);
                        item.getState().setPath(""); //$NON-NLS-1$
                        RepositoryResourceUtil.createItem(item, label);
                        view.refreshRootNode(IServerObjectRepositoryType.TYPE_DATAMODEL);
                    }
                } catch (Exception ex) {
                    log.error(ex.getMessage());
                }
                WizardDialog dialog = (WizardDialog) getWizard().getContainer();
                dialog.close();
            } else {
                MessageDialog.openWarning(getShell(), null, Messages.NO_XSD_RESOURCE);
            }
        }
    }
}
