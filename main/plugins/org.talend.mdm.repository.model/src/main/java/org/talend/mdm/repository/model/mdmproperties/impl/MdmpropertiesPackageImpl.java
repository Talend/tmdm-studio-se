/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.mdm.repository.model.mdmproperties.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.gmf.runtime.notation.NotationPackage;
import org.talend.core.model.metadata.builder.connection.ConnectionPackage;
import org.talend.core.model.properties.PropertiesPackage;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.dataquality.analysis.AnalysisPackage;
import org.talend.dataquality.domain.DomainPackage;
import org.talend.dataquality.indicators.IndicatorsPackage;
import org.talend.dataquality.reports.ReportsPackage;
import org.talend.dataquality.rules.RulesPackage;
import org.talend.designer.business.model.business.BusinessPackage;
import org.talend.designer.core.model.utils.emf.component.ComponentPackage;
import org.talend.designer.core.model.utils.emf.talendfile.TalendFilePackage;
import org.talend.designer.joblet.model.JobletPackage;
import org.talend.mdm.repository.model.mdmmetadata.MdmmetadataPackage;
import org.talend.mdm.repository.model.mdmproperties.ContainerItem;
import org.talend.mdm.repository.model.mdmproperties.MDMItem;
import org.talend.mdm.repository.model.mdmproperties.MDMServerDefItem;
import org.talend.mdm.repository.model.mdmproperties.MDMServerObjectItem;
import org.talend.mdm.repository.model.mdmproperties.MdmpropertiesFactory;
import org.talend.mdm.repository.model.mdmproperties.MdmpropertiesPackage;
import org.talend.mdm.repository.model.mdmproperties.WSCustomFormItem;
import org.talend.mdm.repository.model.mdmproperties.WSDataClusterItem;
import org.talend.mdm.repository.model.mdmproperties.WSDataModelItem;
import org.talend.mdm.repository.model.mdmproperties.WSEventManagerItem;
import org.talend.mdm.repository.model.mdmproperties.WSJobModelItem;
import org.talend.mdm.repository.model.mdmproperties.WSMatchRuleItem;
import org.talend.mdm.repository.model.mdmproperties.WSMenuItem;
import org.talend.mdm.repository.model.mdmproperties.WSResourceItem;
import org.talend.mdm.repository.model.mdmproperties.WSRoleItem;
import org.talend.mdm.repository.model.mdmproperties.WSRoutingRuleItem;
import org.talend.mdm.repository.model.mdmproperties.WSServiceConfigurationItem;
import org.talend.mdm.repository.model.mdmproperties.WSStoredProcedureItem;
import org.talend.mdm.repository.model.mdmproperties.WSTransformerV2Item;
import org.talend.mdm.repository.model.mdmproperties.WSViewItem;
import org.talend.mdm.repository.model.mdmproperties.WorkspaceRootItem;
import org.talend.mdm.repository.model.mdmserverobject.MdmserverobjectPackage;

import orgomg.cwm.analysis.businessnomenclature.BusinessnomenclaturePackage;
import orgomg.cwm.analysis.datamining.DataminingPackage;
import orgomg.cwm.analysis.informationvisualization.InformationvisualizationPackage;
import orgomg.cwm.analysis.olap.OlapPackage;
import orgomg.cwm.analysis.transformation.TransformationPackage;
import orgomg.cwm.foundation.businessinformation.BusinessinformationPackage;
import orgomg.cwm.foundation.datatypes.DatatypesPackage;
import orgomg.cwm.foundation.expressions.ExpressionsPackage;
import orgomg.cwm.foundation.keysindexes.KeysindexesPackage;
import orgomg.cwm.foundation.softwaredeployment.SoftwaredeploymentPackage;
import orgomg.cwm.foundation.typemapping.TypemappingPackage;
import orgomg.cwm.management.warehouseoperation.WarehouseoperationPackage;
import orgomg.cwm.management.warehouseprocess.WarehouseprocessPackage;
import orgomg.cwm.objectmodel.behavioral.BehavioralPackage;
import orgomg.cwm.objectmodel.core.CorePackage;
import orgomg.cwm.objectmodel.instance.InstancePackage;
import orgomg.cwm.objectmodel.relationships.RelationshipsPackage;
import orgomg.cwm.resource.multidimensional.MultidimensionalPackage;
import orgomg.cwm.resource.record.RecordPackage;
import orgomg.cwm.resource.relational.RelationalPackage;
import orgomg.cwm.resource.xml.XmlPackage;
import orgomg.cwmmip.CwmmipPackage;
import orgomg.cwmx.analysis.informationreporting.InformationreportingPackage;
import orgomg.cwmx.analysis.informationset.InformationsetPackage;
import orgomg.cwmx.foundation.er.ErPackage;
import orgomg.cwmx.resource.coboldata.CoboldataPackage;
import orgomg.cwmx.resource.dmsii.DmsiiPackage;
import orgomg.cwmx.resource.essbase.EssbasePackage;
import orgomg.cwmx.resource.express.ExpressPackage;
import orgomg.cwmx.resource.imsdatabase.ImsdatabasePackage;
import orgomg.mof.model.ModelPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class MdmpropertiesPackageImpl extends EPackageImpl implements MdmpropertiesPackage {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass mdmItemEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass mdmServerDefItemEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass mdmServerObjectItemEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass wsMenuItemEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass wsRoleItemEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass containerItemEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass wsDataModelItemEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass wsDataClusterItemEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass wsStoredProcedureItemEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass wsViewItemEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass wsTransformerV2ItemEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass wsRoutingRuleItemEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass wsJobModelItemEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass wsEventManagerItemEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass wsServiceConfigurationItemEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass wsResourceItemEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass wsCustomFormItemEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass workspaceRootItemEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass wsMatchRuleItemEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EDataType eRepositoryObjectTypeEDataType = null;

    /**
     * Creates an instance of the model <b>Package</b>, registered with
     * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
     * package URI value.
     * <p>Note: the correct way to create the package is via the static
     * factory method {@link #init init()}, which also performs
     * initialization of the package, or returns the registered package,
     * if one already exists.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.ecore.EPackage.Registry
     * @see org.talend.mdm.repository.model.mdmproperties.MdmpropertiesPackage#eNS_URI
     * @see #init()
     * @generated
     */
    private MdmpropertiesPackageImpl() {
        super(eNS_URI, MdmpropertiesFactory.eINSTANCE);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private static boolean isInited = false;

    /**
     * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
     * 
     * <p>This method is used to initialize {@link MdmpropertiesPackage#eINSTANCE} when that field is accessed.
     * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #eNS_URI
     * @see #createPackageContents()
     * @see #initializePackageContents()
     * @generated
     */
    public static MdmpropertiesPackage init() {
        if (isInited) {
            return (MdmpropertiesPackage)EPackage.Registry.INSTANCE.getEPackage(MdmpropertiesPackage.eNS_URI);
        }

        // Obtain or create and register package
        MdmpropertiesPackageImpl theMdmpropertiesPackage = (MdmpropertiesPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof MdmpropertiesPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new MdmpropertiesPackageImpl());

        isInited = true;

        // Initialize simple dependencies
        BusinessPackage.eINSTANCE.eClass();
        ComponentPackage.eINSTANCE.eClass();
        CorePackage.eINSTANCE.eClass();
        BehavioralPackage.eINSTANCE.eClass();
        RelationshipsPackage.eINSTANCE.eClass();
        InstancePackage.eINSTANCE.eClass();
        BusinessinformationPackage.eINSTANCE.eClass();
        DatatypesPackage.eINSTANCE.eClass();
        ExpressionsPackage.eINSTANCE.eClass();
        KeysindexesPackage.eINSTANCE.eClass();
        SoftwaredeploymentPackage.eINSTANCE.eClass();
        TypemappingPackage.eINSTANCE.eClass();
        RelationalPackage.eINSTANCE.eClass();
        RecordPackage.eINSTANCE.eClass();
        MultidimensionalPackage.eINSTANCE.eClass();
        XmlPackage.eINSTANCE.eClass();
        TransformationPackage.eINSTANCE.eClass();
        OlapPackage.eINSTANCE.eClass();
        DataminingPackage.eINSTANCE.eClass();
        InformationvisualizationPackage.eINSTANCE.eClass();
        BusinessnomenclaturePackage.eINSTANCE.eClass();
        WarehouseprocessPackage.eINSTANCE.eClass();
        WarehouseoperationPackage.eINSTANCE.eClass();
        ErPackage.eINSTANCE.eClass();
        CoboldataPackage.eINSTANCE.eClass();
        DmsiiPackage.eINSTANCE.eClass();
        ImsdatabasePackage.eINSTANCE.eClass();
        EssbasePackage.eINSTANCE.eClass();
        ExpressPackage.eINSTANCE.eClass();
        InformationsetPackage.eINSTANCE.eClass();
        InformationreportingPackage.eINSTANCE.eClass();
        CwmmipPackage.eINSTANCE.eClass();
        ModelPackage.eINSTANCE.eClass();
        AnalysisPackage.eINSTANCE.eClass();
        ReportsPackage.eINSTANCE.eClass();
        IndicatorsPackage.eINSTANCE.eClass();
        org.talend.dataquality.expressions.ExpressionsPackage.eINSTANCE.eClass();
        DomainPackage.eINSTANCE.eClass();
        RulesPackage.eINSTANCE.eClass();
        org.talend.dataquality.properties.PropertiesPackage.eINSTANCE.eClass();
        EcorePackage.eINSTANCE.eClass();
        JobletPackage.eINSTANCE.eClass();
        MdmmetadataPackage.eINSTANCE.eClass();
        MdmserverobjectPackage.eINSTANCE.eClass();
        ConnectionPackage.eINSTANCE.eClass();
        NotationPackage.eINSTANCE.eClass();
        PropertiesPackage.eINSTANCE.eClass();
        TalendFilePackage.eINSTANCE.eClass();

        // Create package meta-data objects
        theMdmpropertiesPackage.createPackageContents();

        // Initialize created meta-data
        theMdmpropertiesPackage.initializePackageContents();

        // Mark meta-data to indicate it can't be changed
        theMdmpropertiesPackage.freeze();

  
        // Update the registry and return the package
        EPackage.Registry.INSTANCE.put(MdmpropertiesPackage.eNS_URI, theMdmpropertiesPackage);
        return theMdmpropertiesPackage;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getMDMItem() {
        return mdmItemEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getMDMServerDefItem() {
        return mdmServerDefItemEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getMDMServerDefItem_ServerDef() {
        return (EReference)mdmServerDefItemEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getMDMServerObjectItem() {
        return mdmServerObjectItemEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getWSMenuItem() {
        return wsMenuItemEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getWSMenuItem_WsMenu() {
        return (EReference)wsMenuItemEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getWSRoleItem() {
        return wsRoleItemEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getWSRoleItem_WsRole() {
        return (EReference)wsRoleItemEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getContainerItem() {
        return containerItemEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getContainerItem_Label() {
        return (EAttribute)containerItemEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getContainerItem_RepObjType() {
        return (EAttribute)containerItemEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getContainerItem_Data() {
        return (EAttribute)containerItemEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getWSDataModelItem() {
        return wsDataModelItemEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getWSDataModelItem_WsDataModel() {
        return (EReference)wsDataModelItemEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getWSDataClusterItem() {
        return wsDataClusterItemEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getWSDataClusterItem_WsDataCluster() {
        return (EReference)wsDataClusterItemEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getWSStoredProcedureItem() {
        return wsStoredProcedureItemEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getWSStoredProcedureItem_WsStoredProcedure() {
        return (EReference)wsStoredProcedureItemEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getWSViewItem() {
        return wsViewItemEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getWSViewItem_WsView() {
        return (EReference)wsViewItemEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getWSTransformerV2Item() {
        return wsTransformerV2ItemEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getWSTransformerV2Item_WsTransformerV2() {
        return (EReference)wsTransformerV2ItemEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getWSRoutingRuleItem() {
        return wsRoutingRuleItemEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getWSRoutingRuleItem_WsRoutingRule() {
        return (EReference)wsRoutingRuleItemEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getWSJobModelItem() {
        return wsJobModelItemEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getWSJobModelItem_WsJobModelItem() {
        return (EReference)wsJobModelItemEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getWSEventManagerItem() {
        return wsEventManagerItemEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getWSEventManagerItem_WsEventManager() {
        return (EReference)wsEventManagerItemEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getWSServiceConfigurationItem() {
        return wsServiceConfigurationItemEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getWSServiceConfigurationItem_WsServiceConfiguration() {
        return (EReference)wsServiceConfigurationItemEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getWSResourceItem() {
        return wsResourceItemEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getWSResourceItem_Resource() {
        return (EReference)wsResourceItemEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getWSCustomFormItem() {
        return wsCustomFormItemEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getWSCustomFormItem_CustomForm() {
        return (EReference)wsCustomFormItemEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getWorkspaceRootItem() {
        return workspaceRootItemEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getWorkspaceRootItem_Label() {
        return (EAttribute)workspaceRootItemEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getWSMatchRuleItem() {
        return wsMatchRuleItemEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getWSMatchRuleItem_MdmMatchRule() {
        return (EReference)wsMatchRuleItemEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EDataType getERepositoryObjectType() {
        return eRepositoryObjectTypeEDataType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public MdmpropertiesFactory getMdmpropertiesFactory() {
        return (MdmpropertiesFactory)getEFactoryInstance();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private boolean isCreated = false;

    /**
     * Creates the meta-model objects for the package.  This method is
     * guarded to have no affect on any invocation but its first.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void createPackageContents() {
        if (isCreated) {
            return;
        }
        isCreated = true;

        // Create classes and their features
        mdmItemEClass = createEClass(MDM_ITEM);

        mdmServerDefItemEClass = createEClass(MDM_SERVER_DEF_ITEM);
        createEReference(mdmServerDefItemEClass, MDM_SERVER_DEF_ITEM__SERVER_DEF);

        mdmServerObjectItemEClass = createEClass(MDM_SERVER_OBJECT_ITEM);

        wsMenuItemEClass = createEClass(WS_MENU_ITEM);
        createEReference(wsMenuItemEClass, WS_MENU_ITEM__WS_MENU);

        wsRoleItemEClass = createEClass(WS_ROLE_ITEM);
        createEReference(wsRoleItemEClass, WS_ROLE_ITEM__WS_ROLE);

        containerItemEClass = createEClass(CONTAINER_ITEM);
        createEAttribute(containerItemEClass, CONTAINER_ITEM__LABEL);
        createEAttribute(containerItemEClass, CONTAINER_ITEM__REP_OBJ_TYPE);
        createEAttribute(containerItemEClass, CONTAINER_ITEM__DATA);

        wsDataModelItemEClass = createEClass(WS_DATA_MODEL_ITEM);
        createEReference(wsDataModelItemEClass, WS_DATA_MODEL_ITEM__WS_DATA_MODEL);

        wsDataClusterItemEClass = createEClass(WS_DATA_CLUSTER_ITEM);
        createEReference(wsDataClusterItemEClass, WS_DATA_CLUSTER_ITEM__WS_DATA_CLUSTER);

        wsStoredProcedureItemEClass = createEClass(WS_STORED_PROCEDURE_ITEM);
        createEReference(wsStoredProcedureItemEClass, WS_STORED_PROCEDURE_ITEM__WS_STORED_PROCEDURE);

        wsViewItemEClass = createEClass(WS_VIEW_ITEM);
        createEReference(wsViewItemEClass, WS_VIEW_ITEM__WS_VIEW);

        wsTransformerV2ItemEClass = createEClass(WS_TRANSFORMER_V2_ITEM);
        createEReference(wsTransformerV2ItemEClass, WS_TRANSFORMER_V2_ITEM__WS_TRANSFORMER_V2);

        wsRoutingRuleItemEClass = createEClass(WS_ROUTING_RULE_ITEM);
        createEReference(wsRoutingRuleItemEClass, WS_ROUTING_RULE_ITEM__WS_ROUTING_RULE);

        wsJobModelItemEClass = createEClass(WS_JOB_MODEL_ITEM);
        createEReference(wsJobModelItemEClass, WS_JOB_MODEL_ITEM__WS_JOB_MODEL_ITEM);

        wsEventManagerItemEClass = createEClass(WS_EVENT_MANAGER_ITEM);
        createEReference(wsEventManagerItemEClass, WS_EVENT_MANAGER_ITEM__WS_EVENT_MANAGER);

        wsServiceConfigurationItemEClass = createEClass(WS_SERVICE_CONFIGURATION_ITEM);
        createEReference(wsServiceConfigurationItemEClass, WS_SERVICE_CONFIGURATION_ITEM__WS_SERVICE_CONFIGURATION);

        wsResourceItemEClass = createEClass(WS_RESOURCE_ITEM);
        createEReference(wsResourceItemEClass, WS_RESOURCE_ITEM__RESOURCE);

        wsCustomFormItemEClass = createEClass(WS_CUSTOM_FORM_ITEM);
        createEReference(wsCustomFormItemEClass, WS_CUSTOM_FORM_ITEM__CUSTOM_FORM);

        workspaceRootItemEClass = createEClass(WORKSPACE_ROOT_ITEM);
        createEAttribute(workspaceRootItemEClass, WORKSPACE_ROOT_ITEM__LABEL);

        wsMatchRuleItemEClass = createEClass(WS_MATCH_RULE_ITEM);
        createEReference(wsMatchRuleItemEClass, WS_MATCH_RULE_ITEM__MDM_MATCH_RULE);

        // Create data types
        eRepositoryObjectTypeEDataType = createEDataType(EREPOSITORY_OBJECT_TYPE);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private boolean isInitialized = false;

    /**
     * Complete the initialization of the package and its meta-model.  This
     * method is guarded to have no affect on any invocation but its first.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void initializePackageContents() {
        if (isInitialized) {
            return;
        }
        isInitialized = true;

        // Initialize package
        setName(eNAME);
        setNsPrefix(eNS_PREFIX);
        setNsURI(eNS_URI);

        // Obtain other dependent packages
        PropertiesPackage thePropertiesPackage = (PropertiesPackage)EPackage.Registry.INSTANCE.getEPackage(PropertiesPackage.eNS_URI);
        MdmmetadataPackage theMdmmetadataPackage = (MdmmetadataPackage)EPackage.Registry.INSTANCE.getEPackage(MdmmetadataPackage.eNS_URI);
        MdmserverobjectPackage theMdmserverobjectPackage = (MdmserverobjectPackage)EPackage.Registry.INSTANCE.getEPackage(MdmserverobjectPackage.eNS_URI);
        EcorePackage theEcorePackage = (EcorePackage)EPackage.Registry.INSTANCE.getEPackage(EcorePackage.eNS_URI);

        // Create type parameters

        // Set bounds for type parameters

        // Add supertypes to classes
        mdmItemEClass.getESuperTypes().add(thePropertiesPackage.getItem());
        mdmServerDefItemEClass.getESuperTypes().add(this.getMDMItem());
        mdmServerObjectItemEClass.getESuperTypes().add(this.getMDMItem());
        wsMenuItemEClass.getESuperTypes().add(this.getMDMServerObjectItem());
        wsRoleItemEClass.getESuperTypes().add(this.getMDMServerObjectItem());
        containerItemEClass.getESuperTypes().add(this.getMDMItem());
        containerItemEClass.getESuperTypes().add(thePropertiesPackage.getFolderItem());
        wsDataModelItemEClass.getESuperTypes().add(this.getMDMServerObjectItem());
        wsDataClusterItemEClass.getESuperTypes().add(this.getMDMServerObjectItem());
        wsStoredProcedureItemEClass.getESuperTypes().add(this.getMDMServerObjectItem());
        wsViewItemEClass.getESuperTypes().add(this.getMDMServerObjectItem());
        wsTransformerV2ItemEClass.getESuperTypes().add(this.getMDMServerObjectItem());
        wsRoutingRuleItemEClass.getESuperTypes().add(this.getMDMServerObjectItem());
        wsJobModelItemEClass.getESuperTypes().add(this.getMDMServerObjectItem());
        wsEventManagerItemEClass.getESuperTypes().add(this.getMDMServerObjectItem());
        wsServiceConfigurationItemEClass.getESuperTypes().add(this.getMDMServerObjectItem());
        wsResourceItemEClass.getESuperTypes().add(this.getMDMServerObjectItem());
        wsCustomFormItemEClass.getESuperTypes().add(this.getMDMServerObjectItem());
        workspaceRootItemEClass.getESuperTypes().add(this.getMDMItem());
        workspaceRootItemEClass.getESuperTypes().add(thePropertiesPackage.getFolderItem());
        wsMatchRuleItemEClass.getESuperTypes().add(this.getMDMServerObjectItem());

        // Initialize classes and features; add operations and parameters
        initEClass(mdmItemEClass, MDMItem.class, "MDMItem", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

        initEClass(mdmServerDefItemEClass, MDMServerDefItem.class, "MDMServerDefItem", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getMDMServerDefItem_ServerDef(), theMdmmetadataPackage.getMDMServerDef(), null, "serverDef", null, 0, 1, MDMServerDefItem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(mdmServerObjectItemEClass, MDMServerObjectItem.class, "MDMServerObjectItem", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

        addEOperation(mdmServerObjectItemEClass, theMdmserverobjectPackage.getMDMServerObject(), "getMDMServerObject", 0, 1, IS_UNIQUE, IS_ORDERED);

        EOperation op = addEOperation(mdmServerObjectItemEClass, null, "setMDMServerObject", 0, 1, IS_UNIQUE, IS_ORDERED);
        addEParameter(op, theMdmserverobjectPackage.getMDMServerObject(), "serverObj", 0, 1, IS_UNIQUE, IS_ORDERED);

        initEClass(wsMenuItemEClass, WSMenuItem.class, "WSMenuItem", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getWSMenuItem_WsMenu(), theMdmserverobjectPackage.getWSMenuE(), null, "wsMenu", null, 0, 1, WSMenuItem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(wsRoleItemEClass, WSRoleItem.class, "WSRoleItem", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getWSRoleItem_WsRole(), theMdmserverobjectPackage.getWSRoleE(), null, "wsRole", null, 0, 1, WSRoleItem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(containerItemEClass, ContainerItem.class, "ContainerItem", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getContainerItem_Label(), theEcorePackage.getEString(), "label", null, 0, 1, ContainerItem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getContainerItem_RepObjType(), this.getERepositoryObjectType(), "repObjType", null, 0, 1, ContainerItem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getContainerItem_Data(), theEcorePackage.getEJavaObject(), "data", null, 0, 1, ContainerItem.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(wsDataModelItemEClass, WSDataModelItem.class, "WSDataModelItem", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getWSDataModelItem_WsDataModel(), theMdmserverobjectPackage.getWSDataModelE(), null, "wsDataModel", null, 0, 1, WSDataModelItem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(wsDataClusterItemEClass, WSDataClusterItem.class, "WSDataClusterItem", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getWSDataClusterItem_WsDataCluster(), theMdmserverobjectPackage.getWSDataClusterE(), null, "wsDataCluster", null, 0, 1, WSDataClusterItem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(wsStoredProcedureItemEClass, WSStoredProcedureItem.class, "WSStoredProcedureItem", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getWSStoredProcedureItem_WsStoredProcedure(), theMdmserverobjectPackage.getWSStoredProcedureE(), null, "wsStoredProcedure", null, 0, 1, WSStoredProcedureItem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(wsViewItemEClass, WSViewItem.class, "WSViewItem", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getWSViewItem_WsView(), theMdmserverobjectPackage.getWSViewE(), null, "wsView", null, 0, 1, WSViewItem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(wsTransformerV2ItemEClass, WSTransformerV2Item.class, "WSTransformerV2Item", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getWSTransformerV2Item_WsTransformerV2(), theMdmserverobjectPackage.getWSTransformerV2E(), null, "wsTransformerV2", null, 0, 1, WSTransformerV2Item.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(wsRoutingRuleItemEClass, WSRoutingRuleItem.class, "WSRoutingRuleItem", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getWSRoutingRuleItem_WsRoutingRule(), theMdmserverobjectPackage.getWSRoutingRuleE(), null, "wsRoutingRule", null, 0, 1, WSRoutingRuleItem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(wsJobModelItemEClass, WSJobModelItem.class, "WSJobModelItem", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getWSJobModelItem_WsJobModelItem(), theMdmserverobjectPackage.getWSJobModelE(), null, "wsJobModelItem", null, 0, 1, WSJobModelItem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(wsEventManagerItemEClass, WSEventManagerItem.class, "WSEventManagerItem", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getWSEventManagerItem_WsEventManager(), theMdmserverobjectPackage.getWSEventManagerE(), null, "wsEventManager", null, 0, 1, WSEventManagerItem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(wsServiceConfigurationItemEClass, WSServiceConfigurationItem.class, "WSServiceConfigurationItem", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getWSServiceConfigurationItem_WsServiceConfiguration(), theMdmserverobjectPackage.getWSServiceConfigurationE(), null, "wsServiceConfiguration", null, 0, 1, WSServiceConfigurationItem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(wsResourceItemEClass, WSResourceItem.class, "WSResourceItem", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getWSResourceItem_Resource(), theMdmserverobjectPackage.getWSResourceE(), null, "resource", null, 0, 1, WSResourceItem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(wsCustomFormItemEClass, WSCustomFormItem.class, "WSCustomFormItem", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getWSCustomFormItem_CustomForm(), theMdmserverobjectPackage.getWSCustomFormE(), null, "customForm", null, 0, 1, WSCustomFormItem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(workspaceRootItemEClass, WorkspaceRootItem.class, "WorkspaceRootItem", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getWorkspaceRootItem_Label(), ecorePackage.getEString(), "label", null, 0, 1, WorkspaceRootItem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(wsMatchRuleItemEClass, WSMatchRuleItem.class, "WSMatchRuleItem", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getWSMatchRuleItem_MdmMatchRule(), theMdmserverobjectPackage.getWSMatchRuleE(), null, "mdmMatchRule", null, 0, 1, WSMatchRuleItem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        // Initialize data types
        initEDataType(eRepositoryObjectTypeEDataType, ERepositoryObjectType.class, "ERepositoryObjectType", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);

        // Create resource
        createResource(eNS_URI);
    }

} //MdmpropertiesPackageImpl
