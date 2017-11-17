// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.repository.ui.wizards.imports.handlers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.service.IModelValidationService;
import org.talend.mdm.repository.ui.wizards.imports.OperatorUpdatorProvider;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;
import org.talend.repository.items.importexport.handlers.imports.IImportResourcesHandler;
import org.talend.repository.items.importexport.handlers.model.ImportItem;
import org.talend.repository.items.importexport.manager.ResourcesManager;

import com.amalto.workbench.exadapter.ExAdapterManager;

/**
 * created by HHB on 2015-5-4 Detailled comment
 *
 */
public class DataModelImportHandler extends CommonMdmImportHandler implements IImportResourcesHandler {

    private IDataModelImportHandlerExAdapter exAdapter;

    public DataModelImportHandler() {
        this.exAdapter = ExAdapterManager.getAdapter(this, IDataModelImportHandlerExAdapter.class);
    }

    @Override
    public void afterImportingItems(IProgressMonitor monitor, ResourcesManager resManager, ImportItem selectedImportItem) {
        if (exAdapter != null) {
            Item item = selectedImportItem.getItem();
            exAdapter.handleDataModelItem(item);
        }
        super.afterImportingItems(monitor, resManager, selectedImportItem);
    }

    @Override
    protected void update(IRepositoryViewObject object, ImportItem selectedImportItem) throws PersistenceException {
        OperatorUpdatorProvider.instance().updateOperator(object.getProperty().getItem());

        super.update(object, selectedImportItem);
    }

    @Override
    public void postImport(IProgressMonitor monitor, ResourcesManager resManager, ImportItem[] importedItemRecords) {
        validateModelAsyc(importedItemRecords);
    }

    @SuppressWarnings("unused")
    private void validateModelAsyc(ImportItem[] importedItemRecords) {
        if (GlobalServiceRegister.getDefault().isServiceRegistered(IModelValidationService.class)) {
            List<IRepositoryViewObject> viewObjs = new ArrayList<IRepositoryViewObject>();
            for (ImportItem item : importedItemRecords) {
                IRepositoryViewObject viewObj = RepositoryResourceUtil.findViewObjectById(item.getProperty().getId());
                viewObjs.add(viewObj);
            }

            Display.getDefault().asyncExec(new Runnable() {

                @Override
                public void run() {
                    IModelValidationService service = (IModelValidationService) GlobalServiceRegister.getDefault()
                            .getService(IModelValidationService.class);
                    service.validate(viewObjs, IModelValidationService.VALIDATE_IMMEDIATE);
                }
            });
        }
    }

    /////////////////////////////////
    @Override
    public void prePopulate(IProgressMonitor monitor, ResourcesManager resManager) {
    }

    @Override
    public void postPopulate(IProgressMonitor monitor, ResourcesManager resManager, ImportItem[] populatedItemRecords) {
    }

    @Override
    public void preImport(IProgressMonitor monitor, ResourcesManager resManager, ImportItem[] checkedItemRecords,
            ImportItem[] allImportItemRecords) {
    }

}
