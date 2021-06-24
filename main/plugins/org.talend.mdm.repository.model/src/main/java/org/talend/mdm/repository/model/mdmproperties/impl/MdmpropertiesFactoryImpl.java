/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.mdm.repository.model.mdmproperties.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import org.talend.core.model.repository.ERepositoryObjectType;

import org.talend.mdm.repository.model.mdmproperties.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class MdmpropertiesFactoryImpl extends EFactoryImpl implements MdmpropertiesFactory {
    /**
     * Creates the default factory implementation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static MdmpropertiesFactory init() {
        try {
            MdmpropertiesFactory theMdmpropertiesFactory = (MdmpropertiesFactory)EPackage.Registry.INSTANCE.getEFactory(MdmpropertiesPackage.eNS_URI);
            if (theMdmpropertiesFactory != null) {
                return theMdmpropertiesFactory;
            }
        }
        catch (Exception exception) {
            EcorePlugin.INSTANCE.log(exception);
        }
        return new MdmpropertiesFactoryImpl();
    }

    /**
     * Creates an instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public MdmpropertiesFactoryImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EObject create(EClass eClass) {
        switch (eClass.getClassifierID()) {
            case MdmpropertiesPackage.MDM_ITEM: return createMDMItem();
            case MdmpropertiesPackage.MDM_SERVER_DEF_ITEM: return createMDMServerDefItem();
            case MdmpropertiesPackage.MDM_SERVER_OBJECT_ITEM: return createMDMServerObjectItem();
            case MdmpropertiesPackage.WS_MENU_ITEM: return createWSMenuItem();
            case MdmpropertiesPackage.WS_ROLE_ITEM: return createWSRoleItem();
            case MdmpropertiesPackage.CONTAINER_ITEM: return createContainerItem();
            case MdmpropertiesPackage.WS_DATA_MODEL_ITEM: return createWSDataModelItem();
            case MdmpropertiesPackage.WS_DATA_CLUSTER_ITEM: return createWSDataClusterItem();
            case MdmpropertiesPackage.WS_STORED_PROCEDURE_ITEM: return createWSStoredProcedureItem();
            case MdmpropertiesPackage.WS_VIEW_ITEM: return createWSViewItem();
            case MdmpropertiesPackage.WS_TRANSFORMER_V2_ITEM: return createWSTransformerV2Item();
            case MdmpropertiesPackage.WS_ROUTING_RULE_ITEM: return createWSRoutingRuleItem();
            case MdmpropertiesPackage.WS_JOB_MODEL_ITEM: return createWSJobModelItem();
            case MdmpropertiesPackage.WS_EVENT_MANAGER_ITEM: return createWSEventManagerItem();
            case MdmpropertiesPackage.WS_SERVICE_CONFIGURATION_ITEM: return createWSServiceConfigurationItem();
            case MdmpropertiesPackage.WS_RESOURCE_ITEM: return createWSResourceItem();
            case MdmpropertiesPackage.WS_CUSTOM_FORM_ITEM: return createWSCustomFormItem();
            case MdmpropertiesPackage.WORKSPACE_ROOT_ITEM: return createWorkspaceRootItem();
            case MdmpropertiesPackage.WS_MATCH_RULE_ITEM: return createWSMatchRuleItem();
            default:
                throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object createFromString(EDataType eDataType, String initialValue) {
        switch (eDataType.getClassifierID()) {
            case MdmpropertiesPackage.EREPOSITORY_OBJECT_TYPE:
                return createERepositoryObjectTypeFromString(eDataType, initialValue);
            default:
                throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String convertToString(EDataType eDataType, Object instanceValue) {
        switch (eDataType.getClassifierID()) {
            case MdmpropertiesPackage.EREPOSITORY_OBJECT_TYPE:
                return convertERepositoryObjectTypeToString(eDataType, instanceValue);
            default:
                throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public MDMItem createMDMItem() {
        MDMItemImpl mdmItem = new MDMItemImpl();
        return mdmItem;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public MDMServerDefItem createMDMServerDefItem() {
        MDMServerDefItemImpl mdmServerDefItem = new MDMServerDefItemImpl();
        return mdmServerDefItem;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public MDMServerObjectItem createMDMServerObjectItem() {
        MDMServerObjectItemImpl mdmServerObjectItem = new MDMServerObjectItemImpl();
        return mdmServerObjectItem;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public WSMenuItem createWSMenuItem() {
        WSMenuItemImpl wsMenuItem = new WSMenuItemImpl();
        return wsMenuItem;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public WSRoleItem createWSRoleItem() {
        WSRoleItemImpl wsRoleItem = new WSRoleItemImpl();
        return wsRoleItem;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public ContainerItem createContainerItem() {
        ContainerItemImpl containerItem = new ContainerItemImpl();
        return containerItem;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public WSDataModelItem createWSDataModelItem() {
        WSDataModelItemImpl wsDataModelItem = new WSDataModelItemImpl();
        return wsDataModelItem;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public WSDataClusterItem createWSDataClusterItem() {
        WSDataClusterItemImpl wsDataClusterItem = new WSDataClusterItemImpl();
        return wsDataClusterItem;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public WSStoredProcedureItem createWSStoredProcedureItem() {
        WSStoredProcedureItemImpl wsStoredProcedureItem = new WSStoredProcedureItemImpl();
        return wsStoredProcedureItem;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public WSViewItem createWSViewItem() {
        WSViewItemImpl wsViewItem = new WSViewItemImpl();
        return wsViewItem;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public WSTransformerV2Item createWSTransformerV2Item() {
        WSTransformerV2ItemImpl wsTransformerV2Item = new WSTransformerV2ItemImpl();
        return wsTransformerV2Item;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public WSRoutingRuleItem createWSRoutingRuleItem() {
        WSRoutingRuleItemImpl wsRoutingRuleItem = new WSRoutingRuleItemImpl();
        return wsRoutingRuleItem;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public WSJobModelItem createWSJobModelItem() {
        WSJobModelItemImpl wsJobModelItem = new WSJobModelItemImpl();
        return wsJobModelItem;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public WSEventManagerItem createWSEventManagerItem() {
        WSEventManagerItemImpl wsEventManagerItem = new WSEventManagerItemImpl();
        return wsEventManagerItem;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public WSServiceConfigurationItem createWSServiceConfigurationItem() {
        WSServiceConfigurationItemImpl wsServiceConfigurationItem = new WSServiceConfigurationItemImpl();
        return wsServiceConfigurationItem;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public WSResourceItem createWSResourceItem() {
        WSResourceItemImpl wsResourceItem = new WSResourceItemImpl();
        return wsResourceItem;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public WSCustomFormItem createWSCustomFormItem() {
        WSCustomFormItemImpl wsCustomFormItem = new WSCustomFormItemImpl();
        return wsCustomFormItem;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public WorkspaceRootItem createWorkspaceRootItem() {
        WorkspaceRootItemImpl workspaceRootItem = new WorkspaceRootItemImpl();
        return workspaceRootItem;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public WSMatchRuleItem createWSMatchRuleItem() {
        WSMatchRuleItemImpl wsMatchRuleItem = new WSMatchRuleItemImpl();
        return wsMatchRuleItem;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ERepositoryObjectType createERepositoryObjectTypeFromString(EDataType eDataType, String initialValue) {
        return (ERepositoryObjectType)super.createFromString(eDataType, initialValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String convertERepositoryObjectTypeToString(EDataType eDataType, Object instanceValue) {
        return super.convertToString(eDataType, instanceValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public MdmpropertiesPackage getMdmpropertiesPackage() {
        return (MdmpropertiesPackage)getEPackage();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @deprecated
     * @generated
     */
    @Deprecated
    public static MdmpropertiesPackage getPackage() {
        return MdmpropertiesPackage.eINSTANCE;
    }

} //MdmpropertiesFactoryImpl
