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
package org.talend.mdm.repository.core.impl.datamodel;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.ui.navigator.CommonViewer;
import org.talend.core.model.properties.FolderType;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.AbstractRepositoryAction;
import org.talend.mdm.repository.core.IServerObjectRepositoryType;
import org.talend.mdm.repository.core.impl.RepositoryNodeActionProviderAdapter;
import org.talend.mdm.repository.model.mdmproperties.MDMServerObjectItem;
import org.talend.mdm.repository.ui.actions.datamodel.NewDataModelAction;
import org.talend.mdm.repository.ui.editors.IRepositoryViewEditorInput;
import org.talend.mdm.repository.ui.editors.XSDEditorInput2;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;

import com.amalto.workbench.exadapter.ExAdapterManager;

/**
 * DOC hbhong class global comment. Detailled comment <br/>
 *
 */
public class DataModelActionProvider extends RepositoryNodeActionProviderAdapter {

    static Logger log = Logger.getLogger(DataModelActionProvider.class);

    AbstractRepositoryAction addAction;

    public DataModelActionProvider() {

    }

    @Override
    public void initCommonViewer(CommonViewer commonViewer) {
        super.initCommonViewer(commonViewer);
        addAction = new NewDataModelAction();
        //
        addAction.initCommonViewer(commonViewer);
    }

    @Override
    public List<AbstractRepositoryAction> getActions(IRepositoryViewObject viewObj) {
        List<AbstractRepositoryAction> actions = super.getActions(viewObj);
        if (RepositoryResourceUtil.hasContainerItem(viewObj, FolderType.SYSTEM_FOLDER_LITERAL, FolderType.FOLDER_LITERAL)) {
            addAction(actions, addAction, viewObj);
        }

        if (viewObj.getProperty().getItem() instanceof MDMServerObjectItem) {
            addAction(actions, renameAction, viewObj);
            // validate
            if (validateAction != null) {
                addAction(actions, validateAction, viewObj);
            }
            // deploy
            addAction(actions, deployToAction, viewObj);
            addAction(actions, deployToLastServerAction, viewObj);
            addAction(actions, deployAnotherToAction, viewObj);
            addAction(actions, undeployAction, viewObj);
            IDataModelActionProviderExAdapter exAdapter = getExAdapter();
            if (exAdapter != null && exAdapter.getActions() != null) {
                for (AbstractRepositoryAction action : exAdapter.getActions()) {
                    addAction(actions, action, viewObj);
                }
            }
        }
        addAction(actions, deployAllAction, viewObj);
        return actions;
    }

    private IDataModelActionProviderExAdapter getExAdapter() {
        return ExAdapterManager.getAdapter(this, IDataModelActionProviderExAdapter.class);
    }

    @Override
    public IRepositoryViewEditorInput getOpenEditorInput(IRepositoryViewObject viewObj) {
        try {
            Item item = viewObj.getProperty().getItem();
            IFile file = RepositoryResourceUtil.findReferenceFile(IServerObjectRepositoryType.TYPE_DATAMODEL, item, "xsd"); //$NON-NLS-1$
            return new XSDEditorInput2(viewObj, file);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }
}
