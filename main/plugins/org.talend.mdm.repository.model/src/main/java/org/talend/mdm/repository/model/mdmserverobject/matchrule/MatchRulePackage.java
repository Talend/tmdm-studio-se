/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.mdm.repository.model.mdmserverobject.matchrule;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.talend.mdm.repository.model.mdmserverobject.matchrule.MatchRuleFactory
 * @model kind="package"
 * @generated
 */
public interface MatchRulePackage extends EPackage {
    /**
     * The package name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNAME = "matchrule";

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_URI = "http://www.talend.org/mdm/server/matchrule";

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_PREFIX = "matchrule";

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    MatchRulePackage eINSTANCE = org.talend.mdm.repository.model.mdmserverobject.matchrule.impl.MatchRulePackageImpl.init();

    /**
     * The meta object id for the '{@link org.talend.mdm.repository.model.mdmserverobject.matchrule.impl.MatchRuleMapInfoImpl <em>Map Info</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.mdm.repository.model.mdmserverobject.matchrule.impl.MatchRuleMapInfoImpl
     * @see org.talend.mdm.repository.model.mdmserverobject.matchrule.impl.MatchRulePackageImpl#getMatchRuleMapInfo()
     * @generated
     */
    int MATCH_RULE_MAP_INFO = 0;

    /**
     * The feature id for the '<em><b>Entity Map Infos</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MATCH_RULE_MAP_INFO__ENTITY_MAP_INFOS = 0;

    /**
     * The feature id for the '<em><b>Model Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MATCH_RULE_MAP_INFO__MODEL_NAME = 1;

    /**
     * The number of structural features of the '<em>Map Info</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MATCH_RULE_MAP_INFO_FEATURE_COUNT = 2;

    /**
     * The meta object id for the '{@link org.talend.mdm.repository.model.mdmserverobject.matchrule.impl.EntityMapInfoImpl <em>Entity Map Info</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.mdm.repository.model.mdmserverobject.matchrule.impl.EntityMapInfoImpl
     * @see org.talend.mdm.repository.model.mdmserverobject.matchrule.impl.MatchRulePackageImpl#getEntityMapInfo()
     * @generated
     */
    int ENTITY_MAP_INFO = 1;

    /**
     * The feature id for the '<em><b>Entity Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ENTITY_MAP_INFO__ENTITY_NAME = 0;

    /**
     * The feature id for the '<em><b>Survivorship Key Map</b></em>' map.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ENTITY_MAP_INFO__SURVIVORSHIP_KEY_MAP = 1;

    /**
     * The feature id for the '<em><b>Blocking Key Definition</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ENTITY_MAP_INFO__BLOCKING_KEY_DEFINITION = 2;

    /**
     * The feature id for the '<em><b>Match Rule Def Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ENTITY_MAP_INFO__MATCH_RULE_DEF_NAME = 3;

    /**
     * The feature id for the '<em><b>Parent</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ENTITY_MAP_INFO__PARENT = 4;

    /**
     * The feature id for the '<em><b>Match Rule Map Info Pages</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ENTITY_MAP_INFO__MATCH_RULE_MAP_INFO_PAGES = 5;

    /**
     * The number of structural features of the '<em>Entity Map Info</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ENTITY_MAP_INFO_FEATURE_COUNT = 6;

    /**
     * The meta object id for the '{@link org.talend.mdm.repository.model.mdmserverobject.matchrule.impl.MatchRuleMapInfoPageImpl <em>Map Info Page</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.mdm.repository.model.mdmserverobject.matchrule.impl.MatchRuleMapInfoPageImpl
     * @see org.talend.mdm.repository.model.mdmserverobject.matchrule.impl.MatchRulePackageImpl#getMatchRuleMapInfoPage()
     * @generated
     */
    int MATCH_RULE_MAP_INFO_PAGE = 2;

    /**
     * The feature id for the '<em><b>Match Key Map</b></em>' map.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MATCH_RULE_MAP_INFO_PAGE__MATCH_KEY_MAP = 0;

    /**
     * The feature id for the '<em><b>Parent</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MATCH_RULE_MAP_INFO_PAGE__PARENT = 1;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MATCH_RULE_MAP_INFO_PAGE__NAME = 2;

    /**
     * The number of structural features of the '<em>Map Info Page</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MATCH_RULE_MAP_INFO_PAGE_FEATURE_COUNT = 3;

    /**
     * The meta object id for the '{@link org.talend.mdm.repository.model.mdmserverobject.matchrule.impl.KeyXPathMapImpl <em>Key XPath Map</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.mdm.repository.model.mdmserverobject.matchrule.impl.KeyXPathMapImpl
     * @see org.talend.mdm.repository.model.mdmserverobject.matchrule.impl.MatchRulePackageImpl#getKeyXPathMap()
     * @generated
     */
    int KEY_XPATH_MAP = 3;

    /**
     * The feature id for the '<em><b>Key</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KEY_XPATH_MAP__KEY = 0;

    /**
     * The feature id for the '<em><b>Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KEY_XPATH_MAP__VALUE = 1;

    /**
     * The number of structural features of the '<em>Key XPath Map</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int KEY_XPATH_MAP_FEATURE_COUNT = 2;

    /**
     * The meta object id for the '{@link org.talend.mdm.repository.model.mdmserverobject.matchrule.impl.BlockingKeyDefinitionImpl <em>Blocking Key Definition</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.mdm.repository.model.mdmserverobject.matchrule.impl.BlockingKeyDefinitionImpl
     * @see org.talend.mdm.repository.model.mdmserverobject.matchrule.impl.MatchRulePackageImpl#getBlockingKeyDefinition()
     * @generated
     */
    int BLOCKING_KEY_DEFINITION = 4;

    /**
     * The feature id for the '<em><b>Blocking Keys</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BLOCKING_KEY_DEFINITION__BLOCKING_KEYS = 0;

    /**
     * The feature id for the '<em><b>Use Built In Column</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BLOCKING_KEY_DEFINITION__USE_BUILT_IN_COLUMN = 1;

    /**
     * The number of structural features of the '<em>Blocking Key Definition</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BLOCKING_KEY_DEFINITION_FEATURE_COUNT = 2;

    /**
     * The meta object id for the '{@link org.talend.mdm.repository.model.mdmserverobject.matchrule.impl.BlockingKeyImpl <em>Blocking Key</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.mdm.repository.model.mdmserverobject.matchrule.impl.BlockingKeyImpl
     * @see org.talend.mdm.repository.model.mdmserverobject.matchrule.impl.MatchRulePackageImpl#getBlockingKey()
     * @generated
     */
    int BLOCKING_KEY = 5;

    /**
     * The feature id for the '<em><b>Key Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BLOCKING_KEY__KEY_NAME = 0;

    /**
     * The number of structural features of the '<em>Blocking Key</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BLOCKING_KEY_FEATURE_COUNT = 1;

    /**
     * The meta object id for the '{@link org.talend.mdm.repository.model.mdmserverobject.matchrule.impl.MatchRuleMapInfoContainerImpl <em>Map Info Container</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.mdm.repository.model.mdmserverobject.matchrule.impl.MatchRuleMapInfoContainerImpl
     * @see org.talend.mdm.repository.model.mdmserverobject.matchrule.impl.MatchRulePackageImpl#getMatchRuleMapInfoContainer()
     * @generated
     */
    int MATCH_RULE_MAP_INFO_CONTAINER = 6;

    /**
     * The feature id for the '<em><b>Map Infos</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MATCH_RULE_MAP_INFO_CONTAINER__MAP_INFOS = 0;

    /**
     * The feature id for the '<em><b>Match Rule Definitions</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MATCH_RULE_MAP_INFO_CONTAINER__MATCH_RULE_DEFINITIONS = 1;

    /**
     * The number of structural features of the '<em>Map Info Container</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MATCH_RULE_MAP_INFO_CONTAINER_FEATURE_COUNT = 2;


    /**
     * Returns the meta object for class '{@link org.talend.mdm.repository.model.mdmserverobject.matchrule.MatchRuleMapInfo <em>Map Info</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Map Info</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.matchrule.MatchRuleMapInfo
     * @generated
     */
    EClass getMatchRuleMapInfo();

    /**
     * Returns the meta object for the containment reference list '{@link org.talend.mdm.repository.model.mdmserverobject.matchrule.MatchRuleMapInfo#getEntityMapInfos <em>Entity Map Infos</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Entity Map Infos</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.matchrule.MatchRuleMapInfo#getEntityMapInfos()
     * @see #getMatchRuleMapInfo()
     * @generated
     */
    EReference getMatchRuleMapInfo_EntityMapInfos();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.matchrule.MatchRuleMapInfo#getModelName <em>Model Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Model Name</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.matchrule.MatchRuleMapInfo#getModelName()
     * @see #getMatchRuleMapInfo()
     * @generated
     */
    EAttribute getMatchRuleMapInfo_ModelName();

    /**
     * Returns the meta object for class '{@link org.talend.mdm.repository.model.mdmserverobject.matchrule.EntityMapInfo <em>Entity Map Info</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Entity Map Info</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.matchrule.EntityMapInfo
     * @generated
     */
    EClass getEntityMapInfo();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.matchrule.EntityMapInfo#getEntityName <em>Entity Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Entity Name</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.matchrule.EntityMapInfo#getEntityName()
     * @see #getEntityMapInfo()
     * @generated
     */
    EAttribute getEntityMapInfo_EntityName();

    /**
     * Returns the meta object for the map '{@link org.talend.mdm.repository.model.mdmserverobject.matchrule.EntityMapInfo#getSurvivorshipKeyMap <em>Survivorship Key Map</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the map '<em>Survivorship Key Map</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.matchrule.EntityMapInfo#getSurvivorshipKeyMap()
     * @see #getEntityMapInfo()
     * @generated
     */
    EReference getEntityMapInfo_SurvivorshipKeyMap();

    /**
     * Returns the meta object for the containment reference '{@link org.talend.mdm.repository.model.mdmserverobject.matchrule.EntityMapInfo#getBlockingKeyDefinition <em>Blocking Key Definition</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Blocking Key Definition</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.matchrule.EntityMapInfo#getBlockingKeyDefinition()
     * @see #getEntityMapInfo()
     * @generated
     */
    EReference getEntityMapInfo_BlockingKeyDefinition();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.matchrule.EntityMapInfo#getMatchRuleDefName <em>Match Rule Def Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Match Rule Def Name</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.matchrule.EntityMapInfo#getMatchRuleDefName()
     * @see #getEntityMapInfo()
     * @generated
     */
    EAttribute getEntityMapInfo_MatchRuleDefName();

    /**
     * Returns the meta object for the container reference '{@link org.talend.mdm.repository.model.mdmserverobject.matchrule.EntityMapInfo#getParent <em>Parent</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Parent</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.matchrule.EntityMapInfo#getParent()
     * @see #getEntityMapInfo()
     * @generated
     */
    EReference getEntityMapInfo_Parent();

    /**
     * Returns the meta object for the containment reference list '{@link org.talend.mdm.repository.model.mdmserverobject.matchrule.EntityMapInfo#getMatchRuleMapInfoPages <em>Match Rule Map Info Pages</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Match Rule Map Info Pages</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.matchrule.EntityMapInfo#getMatchRuleMapInfoPages()
     * @see #getEntityMapInfo()
     * @generated
     */
    EReference getEntityMapInfo_MatchRuleMapInfoPages();

    /**
     * Returns the meta object for class '{@link org.talend.mdm.repository.model.mdmserverobject.matchrule.MatchRuleMapInfoPage <em>Map Info Page</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Map Info Page</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.matchrule.MatchRuleMapInfoPage
     * @generated
     */
    EClass getMatchRuleMapInfoPage();

    /**
     * Returns the meta object for the map '{@link org.talend.mdm.repository.model.mdmserverobject.matchrule.MatchRuleMapInfoPage#getMatchKeyMap <em>Match Key Map</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the map '<em>Match Key Map</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.matchrule.MatchRuleMapInfoPage#getMatchKeyMap()
     * @see #getMatchRuleMapInfoPage()
     * @generated
     */
    EReference getMatchRuleMapInfoPage_MatchKeyMap();

    /**
     * Returns the meta object for the container reference '{@link org.talend.mdm.repository.model.mdmserverobject.matchrule.MatchRuleMapInfoPage#getParent <em>Parent</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Parent</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.matchrule.MatchRuleMapInfoPage#getParent()
     * @see #getMatchRuleMapInfoPage()
     * @generated
     */
    EReference getMatchRuleMapInfoPage_Parent();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.matchrule.MatchRuleMapInfoPage#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.matchrule.MatchRuleMapInfoPage#getName()
     * @see #getMatchRuleMapInfoPage()
     * @generated
     */
    EAttribute getMatchRuleMapInfoPage_Name();

    /**
     * Returns the meta object for class '{@link java.util.Map.Entry <em>Key XPath Map</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Key XPath Map</em>'.
     * @see java.util.Map.Entry
     * @model keyDataType="org.eclipse.emf.ecore.EString"
     *        valueDataType="org.eclipse.emf.ecore.EString"
     * @generated
     */
    EClass getKeyXPathMap();

    /**
     * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Key</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Key</em>'.
     * @see java.util.Map.Entry
     * @see #getKeyXPathMap()
     * @generated
     */
    EAttribute getKeyXPathMap_Key();

    /**
     * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Value</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Value</em>'.
     * @see java.util.Map.Entry
     * @see #getKeyXPathMap()
     * @generated
     */
    EAttribute getKeyXPathMap_Value();

    /**
     * Returns the meta object for class '{@link org.talend.mdm.repository.model.mdmserverobject.matchrule.BlockingKeyDefinition <em>Blocking Key Definition</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Blocking Key Definition</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.matchrule.BlockingKeyDefinition
     * @generated
     */
    EClass getBlockingKeyDefinition();

    /**
     * Returns the meta object for the containment reference list '{@link org.talend.mdm.repository.model.mdmserverobject.matchrule.BlockingKeyDefinition#getBlockingKeys <em>Blocking Keys</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Blocking Keys</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.matchrule.BlockingKeyDefinition#getBlockingKeys()
     * @see #getBlockingKeyDefinition()
     * @generated
     */
    EReference getBlockingKeyDefinition_BlockingKeys();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.matchrule.BlockingKeyDefinition#isUseBuiltInColumn <em>Use Built In Column</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Use Built In Column</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.matchrule.BlockingKeyDefinition#isUseBuiltInColumn()
     * @see #getBlockingKeyDefinition()
     * @generated
     */
    EAttribute getBlockingKeyDefinition_UseBuiltInColumn();

    /**
     * Returns the meta object for class '{@link org.talend.mdm.repository.model.mdmserverobject.matchrule.BlockingKey <em>Blocking Key</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Blocking Key</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.matchrule.BlockingKey
     * @generated
     */
    EClass getBlockingKey();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.matchrule.BlockingKey#getKeyName <em>Key Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Key Name</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.matchrule.BlockingKey#getKeyName()
     * @see #getBlockingKey()
     * @generated
     */
    EAttribute getBlockingKey_KeyName();

    /**
     * Returns the meta object for class '{@link org.talend.mdm.repository.model.mdmserverobject.matchrule.MatchRuleMapInfoContainer <em>Map Info Container</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Map Info Container</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.matchrule.MatchRuleMapInfoContainer
     * @generated
     */
    EClass getMatchRuleMapInfoContainer();

    /**
     * Returns the meta object for the containment reference '{@link org.talend.mdm.repository.model.mdmserverobject.matchrule.MatchRuleMapInfoContainer#getMapInfos <em>Map Infos</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Map Infos</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.matchrule.MatchRuleMapInfoContainer#getMapInfos()
     * @see #getMatchRuleMapInfoContainer()
     * @generated
     */
    EReference getMatchRuleMapInfoContainer_MapInfos();

    /**
     * Returns the meta object for the containment reference list '{@link org.talend.mdm.repository.model.mdmserverobject.matchrule.MatchRuleMapInfoContainer#getMatchRuleDefinitions <em>Match Rule Definitions</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Match Rule Definitions</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.matchrule.MatchRuleMapInfoContainer#getMatchRuleDefinitions()
     * @see #getMatchRuleMapInfoContainer()
     * @generated
     */
    EReference getMatchRuleMapInfoContainer_MatchRuleDefinitions();

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
    MatchRuleFactory getMatchRuleFactory();

    /**
     * <!-- begin-user-doc -->
     * Defines literals for the meta objects that represent
     * <ul>
     *   <li>each class,</li>
     *   <li>each feature of each class,</li>
     *   <li>each enum,</li>
     *   <li>and each data type</li>
     * </ul>
     * <!-- end-user-doc -->
     * @generated
     */
    interface Literals {
        /**
         * The meta object literal for the '{@link org.talend.mdm.repository.model.mdmserverobject.matchrule.impl.MatchRuleMapInfoImpl <em>Map Info</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.mdm.repository.model.mdmserverobject.matchrule.impl.MatchRuleMapInfoImpl
         * @see org.talend.mdm.repository.model.mdmserverobject.matchrule.impl.MatchRulePackageImpl#getMatchRuleMapInfo()
         * @generated
         */
        EClass MATCH_RULE_MAP_INFO = eINSTANCE.getMatchRuleMapInfo();

        /**
         * The meta object literal for the '<em><b>Entity Map Infos</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference MATCH_RULE_MAP_INFO__ENTITY_MAP_INFOS = eINSTANCE.getMatchRuleMapInfo_EntityMapInfos();

        /**
         * The meta object literal for the '<em><b>Model Name</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute MATCH_RULE_MAP_INFO__MODEL_NAME = eINSTANCE.getMatchRuleMapInfo_ModelName();

        /**
         * The meta object literal for the '{@link org.talend.mdm.repository.model.mdmserverobject.matchrule.impl.EntityMapInfoImpl <em>Entity Map Info</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.mdm.repository.model.mdmserverobject.matchrule.impl.EntityMapInfoImpl
         * @see org.talend.mdm.repository.model.mdmserverobject.matchrule.impl.MatchRulePackageImpl#getEntityMapInfo()
         * @generated
         */
        EClass ENTITY_MAP_INFO = eINSTANCE.getEntityMapInfo();

        /**
         * The meta object literal for the '<em><b>Entity Name</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute ENTITY_MAP_INFO__ENTITY_NAME = eINSTANCE.getEntityMapInfo_EntityName();

        /**
         * The meta object literal for the '<em><b>Survivorship Key Map</b></em>' map feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference ENTITY_MAP_INFO__SURVIVORSHIP_KEY_MAP = eINSTANCE.getEntityMapInfo_SurvivorshipKeyMap();

        /**
         * The meta object literal for the '<em><b>Blocking Key Definition</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference ENTITY_MAP_INFO__BLOCKING_KEY_DEFINITION = eINSTANCE.getEntityMapInfo_BlockingKeyDefinition();

        /**
         * The meta object literal for the '<em><b>Match Rule Def Name</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute ENTITY_MAP_INFO__MATCH_RULE_DEF_NAME = eINSTANCE.getEntityMapInfo_MatchRuleDefName();

        /**
         * The meta object literal for the '<em><b>Parent</b></em>' container reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference ENTITY_MAP_INFO__PARENT = eINSTANCE.getEntityMapInfo_Parent();

        /**
         * The meta object literal for the '<em><b>Match Rule Map Info Pages</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference ENTITY_MAP_INFO__MATCH_RULE_MAP_INFO_PAGES = eINSTANCE.getEntityMapInfo_MatchRuleMapInfoPages();

        /**
         * The meta object literal for the '{@link org.talend.mdm.repository.model.mdmserverobject.matchrule.impl.MatchRuleMapInfoPageImpl <em>Map Info Page</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.mdm.repository.model.mdmserverobject.matchrule.impl.MatchRuleMapInfoPageImpl
         * @see org.talend.mdm.repository.model.mdmserverobject.matchrule.impl.MatchRulePackageImpl#getMatchRuleMapInfoPage()
         * @generated
         */
        EClass MATCH_RULE_MAP_INFO_PAGE = eINSTANCE.getMatchRuleMapInfoPage();

        /**
         * The meta object literal for the '<em><b>Match Key Map</b></em>' map feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference MATCH_RULE_MAP_INFO_PAGE__MATCH_KEY_MAP = eINSTANCE.getMatchRuleMapInfoPage_MatchKeyMap();

        /**
         * The meta object literal for the '<em><b>Parent</b></em>' container reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference MATCH_RULE_MAP_INFO_PAGE__PARENT = eINSTANCE.getMatchRuleMapInfoPage_Parent();

        /**
         * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute MATCH_RULE_MAP_INFO_PAGE__NAME = eINSTANCE.getMatchRuleMapInfoPage_Name();

        /**
         * The meta object literal for the '{@link org.talend.mdm.repository.model.mdmserverobject.matchrule.impl.KeyXPathMapImpl <em>Key XPath Map</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.mdm.repository.model.mdmserverobject.matchrule.impl.KeyXPathMapImpl
         * @see org.talend.mdm.repository.model.mdmserverobject.matchrule.impl.MatchRulePackageImpl#getKeyXPathMap()
         * @generated
         */
        EClass KEY_XPATH_MAP = eINSTANCE.getKeyXPathMap();

        /**
         * The meta object literal for the '<em><b>Key</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute KEY_XPATH_MAP__KEY = eINSTANCE.getKeyXPathMap_Key();

        /**
         * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute KEY_XPATH_MAP__VALUE = eINSTANCE.getKeyXPathMap_Value();

        /**
         * The meta object literal for the '{@link org.talend.mdm.repository.model.mdmserverobject.matchrule.impl.BlockingKeyDefinitionImpl <em>Blocking Key Definition</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.mdm.repository.model.mdmserverobject.matchrule.impl.BlockingKeyDefinitionImpl
         * @see org.talend.mdm.repository.model.mdmserverobject.matchrule.impl.MatchRulePackageImpl#getBlockingKeyDefinition()
         * @generated
         */
        EClass BLOCKING_KEY_DEFINITION = eINSTANCE.getBlockingKeyDefinition();

        /**
         * The meta object literal for the '<em><b>Blocking Keys</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference BLOCKING_KEY_DEFINITION__BLOCKING_KEYS = eINSTANCE.getBlockingKeyDefinition_BlockingKeys();

        /**
         * The meta object literal for the '<em><b>Use Built In Column</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute BLOCKING_KEY_DEFINITION__USE_BUILT_IN_COLUMN = eINSTANCE.getBlockingKeyDefinition_UseBuiltInColumn();

        /**
         * The meta object literal for the '{@link org.talend.mdm.repository.model.mdmserverobject.matchrule.impl.BlockingKeyImpl <em>Blocking Key</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.mdm.repository.model.mdmserverobject.matchrule.impl.BlockingKeyImpl
         * @see org.talend.mdm.repository.model.mdmserverobject.matchrule.impl.MatchRulePackageImpl#getBlockingKey()
         * @generated
         */
        EClass BLOCKING_KEY = eINSTANCE.getBlockingKey();

        /**
         * The meta object literal for the '<em><b>Key Name</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute BLOCKING_KEY__KEY_NAME = eINSTANCE.getBlockingKey_KeyName();

        /**
         * The meta object literal for the '{@link org.talend.mdm.repository.model.mdmserverobject.matchrule.impl.MatchRuleMapInfoContainerImpl <em>Map Info Container</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.mdm.repository.model.mdmserverobject.matchrule.impl.MatchRuleMapInfoContainerImpl
         * @see org.talend.mdm.repository.model.mdmserverobject.matchrule.impl.MatchRulePackageImpl#getMatchRuleMapInfoContainer()
         * @generated
         */
        EClass MATCH_RULE_MAP_INFO_CONTAINER = eINSTANCE.getMatchRuleMapInfoContainer();

        /**
         * The meta object literal for the '<em><b>Map Infos</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference MATCH_RULE_MAP_INFO_CONTAINER__MAP_INFOS = eINSTANCE.getMatchRuleMapInfoContainer_MapInfos();

        /**
         * The meta object literal for the '<em><b>Match Rule Definitions</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference MATCH_RULE_MAP_INFO_CONTAINER__MATCH_RULE_DEFINITIONS = eINSTANCE.getMatchRuleMapInfoContainer_MatchRuleDefinitions();

    }

} //MatchRulePackage
