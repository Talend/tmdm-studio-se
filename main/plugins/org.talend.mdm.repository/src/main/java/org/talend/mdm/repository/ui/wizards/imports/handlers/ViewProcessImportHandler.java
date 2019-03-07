// ============================================================================
//
// Copyright (C) 2006-2019 Talend Inc. - www.talend.com
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

import org.eclipse.core.runtime.IPath;
import org.talend.core.model.properties.Item;
import org.talend.mdm.repository.core.impl.transformerV2.ITransformerV2NodeConsDef;
import org.talend.mdm.repository.core.impl.view.IViewNodeConstDef;
import org.talend.mdm.repository.model.mdmproperties.WSTransformerV2Item;
import org.talend.mdm.repository.model.mdmproperties.WSViewItem;
import org.talend.mdm.repository.ui.wizards.imports.UpdatorProvider;
import org.talend.mdm.repository.utils.RepositoryTransformUtil;
import org.talend.repository.items.importexport.handlers.model.ImportItem;

/**
 * created by HHB on 2015年5月4日 Detailled comment
 *
 */
public class ViewProcessImportHandler extends CommonMdmImportHandler {

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
            UpdatorProvider.instance().updateOperator(item);
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
    public boolean todoValidate(ImportItem item) {
        return item.getItem() instanceof WSViewItem;
    }

}
