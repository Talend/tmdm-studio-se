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

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.impl.transformerV2.ITransformerV2NodeConsDef;
import org.talend.mdm.repository.core.impl.view.IViewNodeConstDef;
import org.talend.mdm.repository.core.service.IModelValidationService;
import org.talend.mdm.repository.model.mdmproperties.WSTransformerV2Item;
import org.talend.mdm.repository.model.mdmproperties.WSViewItem;
import org.talend.mdm.repository.ui.wizards.imports.OperatorUpdatorProvider;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;
import org.talend.mdm.repository.utils.RepositoryTransformUtil;
import org.talend.repository.items.importexport.handlers.imports.IImportResourcesHandler;
import org.talend.repository.items.importexport.handlers.model.ImportItem;
import org.talend.repository.items.importexport.manager.ResourcesManager;

/**
 * created by HHB on 2015年5月4日 Detailled comment
 *
 */
public class ViewProcessImportHandler extends CommonMdmImportHandler implements IImportResourcesHandler {

    @Override
    protected void beforeCreatingItem(ImportItem selectedImportItem) {

        Item item = selectedImportItem.getItem();
        String statePath = item.getState().getPath();
        if (item instanceof WSViewItem) {
            if (RepositoryTransformUtil.getInstance().getViewType(item.getProperty().getLabel()) == IViewNodeConstDef.TYPE_WEBFILTER) {
                if (!statePath.startsWith(IPath.SEPARATOR + IViewNodeConstDef.PATH_WEBFILTER)) {
                    item.getState().setPath(IPath.SEPARATOR + IViewNodeConstDef.PATH_WEBFILTER + statePath);
                }
            } else {
                if (!statePath.startsWith(IPath.SEPARATOR + IViewNodeConstDef.PATH_SEARCHFILTER)) {
                    item.getState().setPath(IPath.SEPARATOR + IViewNodeConstDef.PATH_SEARCHFILTER + statePath);
                }
            }
            OperatorUpdatorProvider.instance().updateOperator(item);
        }

        if (item instanceof WSTransformerV2Item) {
            String processName = item.getProperty().getLabel();
            int processType = RepositoryTransformUtil.getInstance().getProcessType(processName);
            switch (processType) {
            case ITransformerV2NodeConsDef.TYPE_BEFORESAVE:
                if (!statePath.startsWith(IPath.SEPARATOR + ITransformerV2NodeConsDef.PATH_BEFORESAVE)) {
                    item.getState().setPath(IPath.SEPARATOR + ITransformerV2NodeConsDef.PATH_BEFORESAVE + statePath);
                }
                break;
            case ITransformerV2NodeConsDef.TYPE_BEFOREDEL:
                if (!statePath.startsWith(IPath.SEPARATOR + ITransformerV2NodeConsDef.PATH_BEFOREDEL)) {
                    item.getState().setPath(IPath.SEPARATOR + ITransformerV2NodeConsDef.PATH_BEFOREDEL + statePath);
                }
                break;
            case ITransformerV2NodeConsDef.TYPE_ENTITYACTION:
                if (!statePath.startsWith(IPath.SEPARATOR + ITransformerV2NodeConsDef.PATH_ENTITYACTION)) {
                    item.getState().setPath(IPath.SEPARATOR + ITransformerV2NodeConsDef.PATH_ENTITYACTION + statePath);
                }
                break;
            case ITransformerV2NodeConsDef.TYPE_WELCOMEACTION:
                if (!statePath.startsWith(IPath.SEPARATOR + ITransformerV2NodeConsDef.PATH_WELCOMEACTION)) {
                    item.getState().setPath(IPath.SEPARATOR + ITransformerV2NodeConsDef.PATH_WELCOMEACTION + statePath);
                }
                break;
            case ITransformerV2NodeConsDef.TYPE_SMARTVIEW:
                if (!statePath.startsWith(IPath.SEPARATOR + ITransformerV2NodeConsDef.PATH_SMARTVIEW)) {
                    item.getState().setPath(IPath.SEPARATOR + ITransformerV2NodeConsDef.PATH_SMARTVIEW + statePath);
                }
                break;
            case ITransformerV2NodeConsDef.TYPE_OTHER:
                if (!statePath.startsWith(IPath.SEPARATOR + ITransformerV2NodeConsDef.PATH_OTHER)) {
                    item.getState().setPath(IPath.SEPARATOR + ITransformerV2NodeConsDef.PATH_OTHER + statePath);
                }
                break;
            default:
                break;
            }
        }

    }

    @Override
    public void postImport(IProgressMonitor monitor, ResourcesManager resManager, ImportItem[] importedItemRecords) {
        if(importedItemRecords.length > 0 && importedItemRecords[0].getItem() instanceof WSViewItem) {
            validateModelAsyc(importedItemRecords);
        }
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
                    service.validate(viewObjs, IModelValidationService.VALIDATE_IMMEDIATE, true);
                }
            });
        }
    }

    /////////////////////////
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
