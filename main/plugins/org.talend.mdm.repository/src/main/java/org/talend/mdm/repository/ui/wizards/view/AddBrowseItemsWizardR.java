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
package org.talend.mdm.repository.ui.wizards.view;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.ui.IEditorReference;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDParticle;
import org.talend.commons.exception.LoginException;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.mdm.repository.core.IServerObjectRepositoryType;
import org.talend.mdm.repository.core.command.CommandManager;
import org.talend.mdm.repository.core.command.ICommand;
import org.talend.mdm.repository.core.service.RepositoryQueryService;
import org.talend.mdm.repository.model.mdmproperties.MDMServerObjectItem;
import org.talend.mdm.repository.model.mdmproperties.WSRoleItem;
import org.talend.mdm.repository.model.mdmserverobject.MdmserverobjectFactory;
import org.talend.mdm.repository.model.mdmserverobject.WSBooleanE;
import org.talend.mdm.repository.model.mdmserverobject.WSRoleE;
import org.talend.mdm.repository.model.mdmserverobject.WSRoleSpecificationE;
import org.talend.mdm.repository.model.mdmserverobject.WSRoleSpecificationInstanceE;
import org.talend.mdm.repository.model.mdmserverobject.WSViewE;
import org.talend.mdm.repository.ui.actions.view.NewViewAction;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;
import org.talend.repository.model.IProxyRepositoryFactory;

import com.amalto.workbench.dialogs.AddBrowseItemsWizard;
import com.amalto.workbench.editors.DataModelMainPage;
import com.amalto.workbench.models.KeyValue;
import com.amalto.workbench.models.Line;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.service.IValidateService;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.utils.XSDAnnotationsStructure;
import com.amalto.workbench.webservices.WSConceptKey;
import com.amalto.workbench.webservices.WSDataModelPK;
import com.amalto.workbench.webservices.WSGetBusinessConceptKey;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class AddBrowseItemsWizardR extends AddBrowseItemsWizard {

    static Logger log = Logger.getLogger(AddBrowseItemsWizardR.class);

    /**
     * DOC hbhong AddBrowseItemsWizardR constructor comment.
     *
     * @param launchPage
     * @param list
     */
    public AddBrowseItemsWizardR(DataModelMainPage launchPage) {
        super(launchPage);
    }

    @Override
    public List<String> getAllRoleNames() {
        return RepositoryQueryService.findAllRoleNames();
    }

    @Override
    protected void modifyRolesWithAttachedBrowseItem(String browseItem, List<Line> roles) {
        for (Line line : roles) {
            List<KeyValue> keyValues = line.keyValues;
            String roleName = keyValues.get(0).value;
            MDMServerObjectItem roleItem = RepositoryQueryService.findServerObjectItemByName(
                    IServerObjectRepositoryType.TYPE_ROLE, roleName);
            if (roleItem != null) {
                CommandManager.getInstance().pushCommand(ICommand.CMD_MODIFY, roleItem.getProperty().getId(),
                        roleItem.getMDMServerObject().getName());
            }
            if (roleItem != null) {
                IProxyRepositoryFactory factory = CoreRuntimePlugin.getInstance().getProxyRepositoryFactory();
                if (factory.isEditableAndLockIfPossible(roleItem)) {
                    WSRoleE role = ((WSRoleItem) roleItem).getWsRole();
                    for (WSRoleSpecificationE spec : role.getSpecification()) {
                        if (spec.getObjectType().equals("View")) {//$NON-NLS-1$
                            EList<WSRoleSpecificationInstanceE> specInstance = spec.getInstance();
                            //
                            WSRoleSpecificationInstanceE newInstance = MdmserverobjectFactory.eINSTANCE
                                    .createWSRoleSpecificationInstanceE();
                            newInstance.setInstanceName(browseItem);
                            newInstance.setWritable(keyValues.get(1).value.equals("Read Only") ? false : true);//$NON-NLS-1$
                            //
                            specInstance.add(newInstance);
                            //
                            break;
                        }
                    }
                    RepositoryResourceUtil.saveItem(roleItem);

                }
                try {
                    factory.unlock(roleItem);
                } catch (PersistenceException e) {
                    log.error(e.getMessage(), e);
                } catch (LoginException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    NewViewAction newViewAction = new NewViewAction() {

        @Override
        protected WSViewE newView(String viewName) {
            WSBooleanE wsBool = MdmserverobjectFactory.eINSTANCE.createWSBooleanE();
            wsBool.set_true(false);

            WSViewE view = MdmserverobjectFactory.eINSTANCE.createWSViewE();
            view.setName(viewName);
            // description
            StringBuffer desc = new StringBuffer();
            Map<String, String> labels = new LinkedHashMap<String, String>();
            XSDElementDeclaration decl = getXSDElementDeclaration();

            if (decl.getAnnotation() != null) {
                labels = new XSDAnnotationsStructure(decl.getAnnotation()).getLabels();
            }
            if (labels.size() == 0) {
                labels.put("EN", decl.getName());//$NON-NLS-1$
            }
            for (String lan : labels.keySet()) {
                desc.append("[" + lan.toUpperCase() + ":" + labels.get(lan) + "]");//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
            }
            view.setDescription(desc.toString());

            //
            view.setTransformerPK(""); //$NON-NLS-1$
            view.setIsTransformerActive(wsBool);
            // SearchableBusinessElements & ViewableBusinessElements
            List<String> idList = getKeyElements(page.getDataModel().getName(), decl.getName());

            List<String> fields = getFieldsForViewElements(decl, idList);
            fields.addAll(0, idList);
            view.getSearchableBusinessElements().addAll(fields);
            view.getViewableBusinessElements().addAll(fields);
            return view;
        }

    };

    private static List<String> getKeyElements(String datamodel, String concept) {
        java.util.List<String> idList = new ArrayList<String>();
        WSGetBusinessConceptKey wsGetBusinessConceptKey = new WSGetBusinessConceptKey(concept, new WSDataModelPK(datamodel));
        WSConceptKey wsConceptKey;
        try {
            wsConceptKey = RepositoryResourceUtil.getBusinessConceptKey(wsGetBusinessConceptKey);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return idList;
        }
        List<String> ids = wsConceptKey.getFields();
        for (String id : ids) {
            // need to care about more case
            if (id.startsWith("/")) {//$NON-NLS-1$
                id = id.substring(1);
            } else if (id.startsWith("//")) {//$NON-NLS-1$
                id = id.substring(2);
            }
            idList.add(concept + '/' + id);
        }
        return idList;
    }

    public static List<String> getFieldsForViewElements(XSDElementDeclaration decl, List<String> idList) {
        List<String> fields = new ArrayList<String>();
        if (decl == null || idList.size() >= 5) {
            return fields;
        }
        if (decl.getTypeDefinition() instanceof XSDComplexTypeDefinition) {
            String labelValue = null;
            List childrenList = Util.getComplexTypeDefinitionChildren((XSDComplexTypeDefinition) decl.getTypeDefinition(), true);
            if (childrenList == null) {
                return fields;
            }
            for (int j = 0; j < childrenList.size(); j++) {
                List<XSDParticle> particles = new ArrayList<XSDParticle>();
                if (childrenList.get(j) instanceof XSDModelGroup) {
                    particles = ((XSDModelGroup) childrenList.get(j)).getParticles();
                }
                if (particles != null) {
                    for (int k = 0; k < particles.size(); k++) {
                        // Only the top 5 attributes will be searchable and viewable when generating the default view

                        XSDParticle xSDCom = particles.get(k);
                        if ((xSDCom != null && xSDCom.getContent() != null)
                                && xSDCom.getContent() instanceof XSDElementDeclaration) {
                            labelValue = getFieldName(xSDCom);
                            String field = decl.getName();
                            field = field.replaceFirst("#.*", "");//$NON-NLS-1$//$NON-NLS-2$
                            field += "/" + labelValue;//$NON-NLS-1$
                            if (!idList.contains(field)) {
                                fields.add(field);
                            }
                        }
                        if (idList.size() + fields.size() == 5) {
                            break;
                        }
                    }
                }
            }
        }
        return fields;

    }

    private static String getFieldName(XSDParticle xSDCom) {
        XSDElementDeclaration xsdElementDeclaration = (XSDElementDeclaration) xSDCom.getContent();
        String labelValue = xsdElementDeclaration.getName();
        if (xsdElementDeclaration.getTypeDefinition() == null) {
            if (xSDCom.getTerm() instanceof XSDElementDeclaration) {
                labelValue = ((XSDElementDeclaration) xSDCom.getTerm()).getName();
            }
        }
        return labelValue;
    }

    @Override
    protected void newBrowseItemView(String browseItem) {
        if (toRecreateBrowserView(browseItem)) {
            IRepositoryViewObject viewObject = RepositoryResourceUtil.findViewObjectByName(IServerObjectRepositoryType.TYPE_VIEW,
                    browseItem);
            if (viewObject != null) {

                IEditorReference ref = RepositoryResourceUtil.isOpenedInEditor(viewObject);
                if (ref != null) {
                    RepositoryResourceUtil.closeEditor(ref, false);
                }

                try {
                    ProxyRepositoryFactory.getInstance().deleteObjectPhysical(viewObject);
                } catch (PersistenceException e) {
                    log.error(e.getMessage(), e);
                    return;
                }
            }

            for (XSDElementDeclaration decl : declList) {
                String fullName = BROWSE_ITEMS + decl.getName();
                if (browseItem.equals(fullName) || browseItem.startsWith(fullName + "#")) { //$NON-NLS-1$
                    try {
                        newViewAction.setXSDElementDeclaration(decl);
                        newViewAction.createNewView(browseItem);
                    } catch (Exception e) {
                        log.error("Error occurred when generating default view " + browseItem, e); //$NON-NLS-1$
                    }
                }
            }
        }
    }

    private boolean toRecreateBrowserView(String viewName) {
        IValidateService validateService = (IValidateService) GlobalServiceRegister.getDefault().getService(
                IValidateService.class);
        boolean result = validateService.validateAndAlertObjectExistence(TreeObject.VIEW, viewName);
        return result;
    }
}
