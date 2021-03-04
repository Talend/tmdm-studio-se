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
package org.talend.mdm.repository.ui.editors;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.navigator.CommonViewer;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.service.ContainerCacheService;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.model.mdmproperties.MDMServerObjectItem;
import org.talend.mdm.repository.model.mdmproperties.WSDataModelItem;
import org.talend.mdm.repository.model.mdmserverobject.WSDataModelE;
import org.talend.mdm.repository.ui.actions.xsd.XSDDeleteConceptActionR;
import org.talend.mdm.repository.ui.actions.xsd.XSDSetAnnotationFKFilterActionR;
import org.talend.mdm.repository.ui.actions.xsd.XSDSetAnnotationForeignKeyActionR;
import org.talend.mdm.repository.ui.actions.xsd.XSDSetAnnotationForeignKeyInfoActionR;
import org.talend.mdm.repository.ui.dialogs.SelectImportedModulesDialog2;
import org.talend.mdm.repository.ui.dialogs.datamodel.DataModelFilterDialogR;
import org.talend.mdm.repository.ui.navigator.MDMRepositoryView;
import org.talend.mdm.repository.ui.wizards.view.AddBrowseItemsWizardR;
import org.talend.mdm.repository.utils.Bean2EObjUtil;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;

import com.amalto.workbench.actions.XSDDeleteConceptAction;
import com.amalto.workbench.actions.XSDSetAnnotationFKFilterAction;
import com.amalto.workbench.actions.XSDSetAnnotationForeignKeyAction;
import com.amalto.workbench.actions.XSDSetAnnotationForeignKeyInfoAction;
import com.amalto.workbench.dialogs.AddBrowseItemsWizard;
import com.amalto.workbench.dialogs.DataModelFilterDialog;
import com.amalto.workbench.dialogs.SelectImportedModulesDialog;
import com.amalto.workbench.editors.DataModelMainPage;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.providers.datamodel.SchemaTreeContentProvider;
import com.amalto.workbench.webservices.WSDataModel;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class DataModelMainPage2 extends DataModelMainPage {

    private static Logger log = Logger.getLogger(DataModelMainPage2.class);

    /**
     * DOC hbhong DataModelMainPage2 constructor comment.
     *
     * @param obj
     */
    public DataModelMainPage2(TreeObject obj) {
        super(obj);
    }

    @Override
    protected void doSave(WSDataModel wsObject) throws Exception {
        XObjectEditorInput2 editorInput = (XObjectEditorInput2) getEditorInput();

        MDMServerObjectItem serverObjectItem = (MDMServerObjectItem) editorInput.getInputItem();

        updateSchemaToItem(serverObjectItem);//

        xsdSchema = ((SchemaTreeContentProvider) viewer.getContentProvider()).getXsdSchema();
        Item newItem = RepositoryResourceUtil.assertItem(serverObjectItem);
        if (newItem != serverObjectItem) {
            editorInput.updateViewObject(ContainerCacheService.get(newItem.getProperty()));
            getEditorSite();
            serverObjectItem = (MDMServerObjectItem) newItem;

        }
        EObject eObj = Bean2EObjUtil.getInstance().convertFromBean2EObj(wsObject, serverObjectItem.getMDMServerObject());
        if (eObj != null) {
            RepositoryResourceUtil.saveItem(serverObjectItem);
        }

        refreshDirtyCue();

    }

    //
    public Item updateSchemaToItem(Item item) {
        WSDataModelE wsDataModelE = ((WSDataModelItem) item).getWsDataModel();
        WSDataModel wsDataModel = (WSDataModel) xobject.getWsObject();
        wsDataModelE.setXsdSchema(wsDataModel.getXsdSchema());
        return item;
    }

    private void refreshDirtyCue() {
        IEditorInput input = getEditorInput();
        XObjectEditorInput2 theInput = null;
        if (input instanceof XObjectEditorInput2) {
            theInput = (XObjectEditorInput2) input;
        }
        IRepositoryViewObject viewObj = theInput.getViewObject();
        CommonViewer viewer = MDMRepositoryView.show().getCommonViewer();
        viewer.refresh(viewObj);
    }

    @Override
    public Object getAdapter(Class adapter) {

        if (adapter == AddBrowseItemsWizard.class) {
            return new AddBrowseItemsWizardR(this);
        }

        if (adapter == XSDSetAnnotationForeignKeyAction.class) {
            return new XSDSetAnnotationForeignKeyActionR(this, dataModelName);
        }
        if (adapter == XSDSetAnnotationForeignKeyInfoAction.class) {
            return new XSDSetAnnotationForeignKeyInfoActionR(this, dataModelName);
        }
        if (adapter == XSDSetAnnotationFKFilterAction.class) {
            return new XSDSetAnnotationFKFilterActionR(this, dataModelName);
        }
        if (adapter == XSDDeleteConceptAction.class) {
            return new XSDDeleteConceptActionR(this);
        }
        if (adapter == DataModelFilterDialog.class) {
            return new DataModelFilterDialogR(getSite().getShell(), xobject, dataModelFilter,
                    getSchemaElementNameFilterDesByTreeViewer(targetTreeViewer));
        }

        return super.getAdapter(adapter);
    }

    @Override
    protected SelectImportedModulesDialog createSelectImportedModulesDialog() {
        return new SelectImportedModulesDialog2(getSite().getShell(), xobject, Messages.ImportXSDSchema);
    }

}
