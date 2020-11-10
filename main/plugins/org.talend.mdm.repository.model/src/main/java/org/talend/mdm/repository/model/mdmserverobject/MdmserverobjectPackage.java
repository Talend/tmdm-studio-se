/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.mdm.repository.model.mdmserverobject;

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
 * @see org.talend.mdm.repository.model.mdmserverobject.MdmserverobjectFactory
 * @model kind="package"
 * @generated
 */
public interface MdmserverobjectPackage extends EPackage {
    /**
     * The package name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNAME = "mdmserverobject";

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_URI = "http://www.talend.org/mdmserverobject";

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_PREFIX = "mdmserverobject";

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    MdmserverobjectPackage eINSTANCE = org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl.init();

    /**
     * The meta object id for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.MDMServerObjectImpl <em>MDM Server Object</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.MDMServerObjectImpl
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getMDMServerObject()
     * @generated
     */
    int MDM_SERVER_OBJECT = 0;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MDM_SERVER_OBJECT__NAME = 0;

    /**
     * The feature id for the '<em><b>Description</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MDM_SERVER_OBJECT__DESCRIPTION = 1;

    /**
     * The feature id for the '<em><b>System</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MDM_SERVER_OBJECT__SYSTEM = 2;

    /**
     * The feature id for the '<em><b>Last Server Def</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MDM_SERVER_OBJECT__LAST_SERVER_DEF = 3;

    /**
     * The feature id for the '<em><b>Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MDM_SERVER_OBJECT__TYPE = 4;

    /**
     * The feature id for the '<em><b>Timestamp</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MDM_SERVER_OBJECT__TIMESTAMP = 5;

    /**
     * The feature id for the '<em><b>Digest Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MDM_SERVER_OBJECT__DIGEST_VALUE = 6;

    /**
     * The feature id for the '<em><b>Current Digest Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MDM_SERVER_OBJECT__CURRENT_DIGEST_VALUE = 7;

    /**
     * The feature id for the '<em><b>Last Server Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MDM_SERVER_OBJECT__LAST_SERVER_NAME = 8;

    /**
     * The number of structural features of the '<em>MDM Server Object</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MDM_SERVER_OBJECT_FEATURE_COUNT = 9;

    /**
     * The meta object id for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSMenuEImpl <em>WS Menu E</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.WSMenuEImpl
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getWSMenuE()
     * @generated
     */
    int WS_MENU_E = 1;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_MENU_E__NAME = MDM_SERVER_OBJECT__NAME;

    /**
     * The feature id for the '<em><b>Description</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_MENU_E__DESCRIPTION = MDM_SERVER_OBJECT__DESCRIPTION;

    /**
     * The feature id for the '<em><b>System</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_MENU_E__SYSTEM = MDM_SERVER_OBJECT__SYSTEM;

    /**
     * The feature id for the '<em><b>Last Server Def</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_MENU_E__LAST_SERVER_DEF = MDM_SERVER_OBJECT__LAST_SERVER_DEF;

    /**
     * The feature id for the '<em><b>Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_MENU_E__TYPE = MDM_SERVER_OBJECT__TYPE;

    /**
     * The feature id for the '<em><b>Timestamp</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_MENU_E__TIMESTAMP = MDM_SERVER_OBJECT__TIMESTAMP;

    /**
     * The feature id for the '<em><b>Digest Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_MENU_E__DIGEST_VALUE = MDM_SERVER_OBJECT__DIGEST_VALUE;

    /**
     * The feature id for the '<em><b>Current Digest Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_MENU_E__CURRENT_DIGEST_VALUE = MDM_SERVER_OBJECT__CURRENT_DIGEST_VALUE;

    /**
     * The feature id for the '<em><b>Last Server Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_MENU_E__LAST_SERVER_NAME = MDM_SERVER_OBJECT__LAST_SERVER_NAME;

    /**
     * The feature id for the '<em><b>Menu Entries</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_MENU_E__MENU_ENTRIES = MDM_SERVER_OBJECT_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the '<em>WS Menu E</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_MENU_E_FEATURE_COUNT = MDM_SERVER_OBJECT_FEATURE_COUNT + 1;

    /**
     * The meta object id for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSMenuEntryEImpl <em>WS Menu Entry E</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.WSMenuEntryEImpl
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getWSMenuEntryE()
     * @generated
     */
    int WS_MENU_ENTRY_E = 2;

    /**
     * The feature id for the '<em><b>Id</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_MENU_ENTRY_E__ID = 0;

    /**
     * The feature id for the '<em><b>Application</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_MENU_ENTRY_E__APPLICATION = 1;

    /**
     * The feature id for the '<em><b>Context</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_MENU_ENTRY_E__CONTEXT = 2;

    /**
     * The feature id for the '<em><b>Icon</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_MENU_ENTRY_E__ICON = 3;

    /**
     * The feature id for the '<em><b>Descriptions</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_MENU_ENTRY_E__DESCRIPTIONS = 4;

    /**
     * The feature id for the '<em><b>Sub Menus</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_MENU_ENTRY_E__SUB_MENUS = 5;

    /**
     * The number of structural features of the '<em>WS Menu Entry E</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_MENU_ENTRY_E_FEATURE_COUNT = 6;

    /**
     * The meta object id for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSMenuMenuEntriesDescriptionsEImpl <em>WS Menu Menu Entries Descriptions E</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.WSMenuMenuEntriesDescriptionsEImpl
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getWSMenuMenuEntriesDescriptionsE()
     * @generated
     */
    int WS_MENU_MENU_ENTRIES_DESCRIPTIONS_E = 3;

    /**
     * The feature id for the '<em><b>Language</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_MENU_MENU_ENTRIES_DESCRIPTIONS_E__LANGUAGE = 0;

    /**
     * The feature id for the '<em><b>Label</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_MENU_MENU_ENTRIES_DESCRIPTIONS_E__LABEL = 1;

    /**
     * The number of structural features of the '<em>WS Menu Menu Entries Descriptions E</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_MENU_MENU_ENTRIES_DESCRIPTIONS_E_FEATURE_COUNT = 2;

    /**
     * The meta object id for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSRoleEImpl <em>WS Role E</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.WSRoleEImpl
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getWSRoleE()
     * @generated
     */
    int WS_ROLE_E = 4;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_ROLE_E__NAME = MDM_SERVER_OBJECT__NAME;

    /**
     * The feature id for the '<em><b>Description</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_ROLE_E__DESCRIPTION = MDM_SERVER_OBJECT__DESCRIPTION;

    /**
     * The feature id for the '<em><b>System</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_ROLE_E__SYSTEM = MDM_SERVER_OBJECT__SYSTEM;

    /**
     * The feature id for the '<em><b>Last Server Def</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_ROLE_E__LAST_SERVER_DEF = MDM_SERVER_OBJECT__LAST_SERVER_DEF;

    /**
     * The feature id for the '<em><b>Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_ROLE_E__TYPE = MDM_SERVER_OBJECT__TYPE;

    /**
     * The feature id for the '<em><b>Timestamp</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_ROLE_E__TIMESTAMP = MDM_SERVER_OBJECT__TIMESTAMP;

    /**
     * The feature id for the '<em><b>Digest Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_ROLE_E__DIGEST_VALUE = MDM_SERVER_OBJECT__DIGEST_VALUE;

    /**
     * The feature id for the '<em><b>Current Digest Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_ROLE_E__CURRENT_DIGEST_VALUE = MDM_SERVER_OBJECT__CURRENT_DIGEST_VALUE;

    /**
     * The feature id for the '<em><b>Last Server Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_ROLE_E__LAST_SERVER_NAME = MDM_SERVER_OBJECT__LAST_SERVER_NAME;

    /**
     * The feature id for the '<em><b>Specification</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_ROLE_E__SPECIFICATION = MDM_SERVER_OBJECT_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the '<em>WS Role E</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_ROLE_E_FEATURE_COUNT = MDM_SERVER_OBJECT_FEATURE_COUNT + 1;

    /**
     * The meta object id for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSRoleSpecificationEImpl <em>WS Role Specification E</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.WSRoleSpecificationEImpl
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getWSRoleSpecificationE()
     * @generated
     */
    int WS_ROLE_SPECIFICATION_E = 5;

    /**
     * The feature id for the '<em><b>Admin</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_ROLE_SPECIFICATION_E__ADMIN = 0;

    /**
     * The feature id for the '<em><b>Object Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_ROLE_SPECIFICATION_E__OBJECT_TYPE = 1;

    /**
     * The feature id for the '<em><b>Instance</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_ROLE_SPECIFICATION_E__INSTANCE = 2;

    /**
     * The number of structural features of the '<em>WS Role Specification E</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_ROLE_SPECIFICATION_E_FEATURE_COUNT = 3;

    /**
     * The meta object id for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSRoleSpecificationInstanceEImpl <em>WS Role Specification Instance E</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.WSRoleSpecificationInstanceEImpl
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getWSRoleSpecificationInstanceE()
     * @generated
     */
    int WS_ROLE_SPECIFICATION_INSTANCE_E = 6;

    /**
     * The feature id for the '<em><b>Instance Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_ROLE_SPECIFICATION_INSTANCE_E__INSTANCE_NAME = 0;

    /**
     * The feature id for the '<em><b>Writable</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_ROLE_SPECIFICATION_INSTANCE_E__WRITABLE = 1;

    /**
     * The feature id for the '<em><b>Parameter</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_ROLE_SPECIFICATION_INSTANCE_E__PARAMETER = 2;

    /**
     * The number of structural features of the '<em>WS Role Specification Instance E</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_ROLE_SPECIFICATION_INSTANCE_E_FEATURE_COUNT = 3;

    /**
     * The meta object id for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSViewEImpl <em>WS View E</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.WSViewEImpl
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getWSViewE()
     * @generated
     */
    int WS_VIEW_E = 7;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_VIEW_E__NAME = MDM_SERVER_OBJECT__NAME;

    /**
     * The feature id for the '<em><b>Description</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_VIEW_E__DESCRIPTION = MDM_SERVER_OBJECT__DESCRIPTION;

    /**
     * The feature id for the '<em><b>System</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_VIEW_E__SYSTEM = MDM_SERVER_OBJECT__SYSTEM;

    /**
     * The feature id for the '<em><b>Last Server Def</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_VIEW_E__LAST_SERVER_DEF = MDM_SERVER_OBJECT__LAST_SERVER_DEF;

    /**
     * The feature id for the '<em><b>Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_VIEW_E__TYPE = MDM_SERVER_OBJECT__TYPE;

    /**
     * The feature id for the '<em><b>Timestamp</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_VIEW_E__TIMESTAMP = MDM_SERVER_OBJECT__TIMESTAMP;

    /**
     * The feature id for the '<em><b>Digest Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_VIEW_E__DIGEST_VALUE = MDM_SERVER_OBJECT__DIGEST_VALUE;

    /**
     * The feature id for the '<em><b>Current Digest Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_VIEW_E__CURRENT_DIGEST_VALUE = MDM_SERVER_OBJECT__CURRENT_DIGEST_VALUE;

    /**
     * The feature id for the '<em><b>Last Server Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_VIEW_E__LAST_SERVER_NAME = MDM_SERVER_OBJECT__LAST_SERVER_NAME;

    /**
     * The feature id for the '<em><b>Searchable Business Elements</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_VIEW_E__SEARCHABLE_BUSINESS_ELEMENTS = MDM_SERVER_OBJECT_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Viewable Business Elements</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_VIEW_E__VIEWABLE_BUSINESS_ELEMENTS = MDM_SERVER_OBJECT_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Transformer Active</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_VIEW_E__TRANSFORMER_ACTIVE = MDM_SERVER_OBJECT_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Where Conditions</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_VIEW_E__WHERE_CONDITIONS = MDM_SERVER_OBJECT_FEATURE_COUNT + 3;

    /**
     * The feature id for the '<em><b>Is Transformer Active</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_VIEW_E__IS_TRANSFORMER_ACTIVE = MDM_SERVER_OBJECT_FEATURE_COUNT + 4;

    /**
     * The feature id for the '<em><b>Transformer PK</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_VIEW_E__TRANSFORMER_PK = MDM_SERVER_OBJECT_FEATURE_COUNT + 5;

    /**
     * The feature id for the '<em><b>Sort Field</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_VIEW_E__SORT_FIELD = MDM_SERVER_OBJECT_FEATURE_COUNT + 6;

    /**
     * The feature id for the '<em><b>Is Asc</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_VIEW_E__IS_ASC = MDM_SERVER_OBJECT_FEATURE_COUNT + 7;

    /**
     * The feature id for the '<em><b>Custom Form</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_VIEW_E__CUSTOM_FORM = MDM_SERVER_OBJECT_FEATURE_COUNT + 8;

    /**
     * The number of structural features of the '<em>WS View E</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_VIEW_E_FEATURE_COUNT = MDM_SERVER_OBJECT_FEATURE_COUNT + 9;

    /**
     * The meta object id for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSWhereConditionEImpl <em>WS Where Condition E</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.WSWhereConditionEImpl
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getWSWhereConditionE()
     * @generated
     */
    int WS_WHERE_CONDITION_E = 8;

    /**
     * The feature id for the '<em><b>Left Path</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_WHERE_CONDITION_E__LEFT_PATH = 0;

    /**
     * The feature id for the '<em><b>Right Value Or Path</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_WHERE_CONDITION_E__RIGHT_VALUE_OR_PATH = 1;

    /**
     * The feature id for the '<em><b>String Predicate</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_WHERE_CONDITION_E__STRING_PREDICATE = 2;

    /**
     * The feature id for the '<em><b>Operator</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_WHERE_CONDITION_E__OPERATOR = 3;

    /**
     * The feature id for the '<em><b>Spell Check</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_WHERE_CONDITION_E__SPELL_CHECK = 4;

    /**
     * The number of structural features of the '<em>WS Where Condition E</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_WHERE_CONDITION_E_FEATURE_COUNT = 5;

    /**
     * The meta object id for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSWhereOperatorEImpl <em>WS Where Operator E</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.WSWhereOperatorEImpl
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getWSWhereOperatorE()
     * @generated
     */
    int WS_WHERE_OPERATOR_E = 9;

    /**
     * The feature id for the '<em><b>Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_WHERE_OPERATOR_E__VALUE = 0;

    /**
     * The number of structural features of the '<em>WS Where Operator E</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_WHERE_OPERATOR_E_FEATURE_COUNT = 1;

    /**
     * The meta object id for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSStringPredicateEImpl <em>WS String Predicate E</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.WSStringPredicateEImpl
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getWSStringPredicateE()
     * @generated
     */
    int WS_STRING_PREDICATE_E = 10;

    /**
     * The feature id for the '<em><b>Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_STRING_PREDICATE_E__VALUE = 0;

    /**
     * The number of structural features of the '<em>WS String Predicate E</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_STRING_PREDICATE_E_FEATURE_COUNT = 1;

    /**
     * The meta object id for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSDataModelEImpl <em>WS Data Model E</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.WSDataModelEImpl
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getWSDataModelE()
     * @generated
     */
    int WS_DATA_MODEL_E = 11;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_DATA_MODEL_E__NAME = MDM_SERVER_OBJECT__NAME;

    /**
     * The feature id for the '<em><b>Description</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_DATA_MODEL_E__DESCRIPTION = MDM_SERVER_OBJECT__DESCRIPTION;

    /**
     * The feature id for the '<em><b>System</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_DATA_MODEL_E__SYSTEM = MDM_SERVER_OBJECT__SYSTEM;

    /**
     * The feature id for the '<em><b>Last Server Def</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_DATA_MODEL_E__LAST_SERVER_DEF = MDM_SERVER_OBJECT__LAST_SERVER_DEF;

    /**
     * The feature id for the '<em><b>Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_DATA_MODEL_E__TYPE = MDM_SERVER_OBJECT__TYPE;

    /**
     * The feature id for the '<em><b>Timestamp</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_DATA_MODEL_E__TIMESTAMP = MDM_SERVER_OBJECT__TIMESTAMP;

    /**
     * The feature id for the '<em><b>Digest Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_DATA_MODEL_E__DIGEST_VALUE = MDM_SERVER_OBJECT__DIGEST_VALUE;

    /**
     * The feature id for the '<em><b>Current Digest Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_DATA_MODEL_E__CURRENT_DIGEST_VALUE = MDM_SERVER_OBJECT__CURRENT_DIGEST_VALUE;

    /**
     * The feature id for the '<em><b>Last Server Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_DATA_MODEL_E__LAST_SERVER_NAME = MDM_SERVER_OBJECT__LAST_SERVER_NAME;

    /**
     * The feature id for the '<em><b>Xsd Schema</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_DATA_MODEL_E__XSD_SCHEMA = MDM_SERVER_OBJECT_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the '<em>WS Data Model E</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_DATA_MODEL_E_FEATURE_COUNT = MDM_SERVER_OBJECT_FEATURE_COUNT + 1;

    /**
     * The meta object id for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSDataClusterEImpl <em>WS Data Cluster E</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.WSDataClusterEImpl
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getWSDataClusterE()
     * @generated
     */
    int WS_DATA_CLUSTER_E = 12;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_DATA_CLUSTER_E__NAME = MDM_SERVER_OBJECT__NAME;

    /**
     * The feature id for the '<em><b>Description</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_DATA_CLUSTER_E__DESCRIPTION = MDM_SERVER_OBJECT__DESCRIPTION;

    /**
     * The feature id for the '<em><b>System</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_DATA_CLUSTER_E__SYSTEM = MDM_SERVER_OBJECT__SYSTEM;

    /**
     * The feature id for the '<em><b>Last Server Def</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_DATA_CLUSTER_E__LAST_SERVER_DEF = MDM_SERVER_OBJECT__LAST_SERVER_DEF;

    /**
     * The feature id for the '<em><b>Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_DATA_CLUSTER_E__TYPE = MDM_SERVER_OBJECT__TYPE;

    /**
     * The feature id for the '<em><b>Timestamp</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_DATA_CLUSTER_E__TIMESTAMP = MDM_SERVER_OBJECT__TIMESTAMP;

    /**
     * The feature id for the '<em><b>Digest Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_DATA_CLUSTER_E__DIGEST_VALUE = MDM_SERVER_OBJECT__DIGEST_VALUE;

    /**
     * The feature id for the '<em><b>Current Digest Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_DATA_CLUSTER_E__CURRENT_DIGEST_VALUE = MDM_SERVER_OBJECT__CURRENT_DIGEST_VALUE;

    /**
     * The feature id for the '<em><b>Last Server Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_DATA_CLUSTER_E__LAST_SERVER_NAME = MDM_SERVER_OBJECT__LAST_SERVER_NAME;

    /**
     * The feature id for the '<em><b>Vocabulary</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_DATA_CLUSTER_E__VOCABULARY = MDM_SERVER_OBJECT_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the '<em>WS Data Cluster E</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_DATA_CLUSTER_E_FEATURE_COUNT = MDM_SERVER_OBJECT_FEATURE_COUNT + 1;

    /**
     * The meta object id for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSStoredProcedureEImpl <em>WS Stored Procedure E</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.WSStoredProcedureEImpl
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getWSStoredProcedureE()
     * @generated
     */
    int WS_STORED_PROCEDURE_E = 13;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_STORED_PROCEDURE_E__NAME = MDM_SERVER_OBJECT__NAME;

    /**
     * The feature id for the '<em><b>Description</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_STORED_PROCEDURE_E__DESCRIPTION = MDM_SERVER_OBJECT__DESCRIPTION;

    /**
     * The feature id for the '<em><b>System</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_STORED_PROCEDURE_E__SYSTEM = MDM_SERVER_OBJECT__SYSTEM;

    /**
     * The feature id for the '<em><b>Last Server Def</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_STORED_PROCEDURE_E__LAST_SERVER_DEF = MDM_SERVER_OBJECT__LAST_SERVER_DEF;

    /**
     * The feature id for the '<em><b>Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_STORED_PROCEDURE_E__TYPE = MDM_SERVER_OBJECT__TYPE;

    /**
     * The feature id for the '<em><b>Timestamp</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_STORED_PROCEDURE_E__TIMESTAMP = MDM_SERVER_OBJECT__TIMESTAMP;

    /**
     * The feature id for the '<em><b>Digest Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_STORED_PROCEDURE_E__DIGEST_VALUE = MDM_SERVER_OBJECT__DIGEST_VALUE;

    /**
     * The feature id for the '<em><b>Current Digest Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_STORED_PROCEDURE_E__CURRENT_DIGEST_VALUE = MDM_SERVER_OBJECT__CURRENT_DIGEST_VALUE;

    /**
     * The feature id for the '<em><b>Last Server Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_STORED_PROCEDURE_E__LAST_SERVER_NAME = MDM_SERVER_OBJECT__LAST_SERVER_NAME;

    /**
     * The feature id for the '<em><b>Procedure</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_STORED_PROCEDURE_E__PROCEDURE = MDM_SERVER_OBJECT_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Refresh Cache</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_STORED_PROCEDURE_E__REFRESH_CACHE = MDM_SERVER_OBJECT_FEATURE_COUNT + 1;

    /**
     * The number of structural features of the '<em>WS Stored Procedure E</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_STORED_PROCEDURE_E_FEATURE_COUNT = MDM_SERVER_OBJECT_FEATURE_COUNT + 2;

    /**
     * The meta object id for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSBooleanEImpl <em>WS Boolean E</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.WSBooleanEImpl
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getWSBooleanE()
     * @generated
     */
    int WS_BOOLEAN_E = 14;

    /**
     * The feature id for the '<em><b>true</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_BOOLEAN_E__TRUE = 0;

    /**
     * The number of structural features of the '<em>WS Boolean E</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_BOOLEAN_E_FEATURE_COUNT = 1;

    /**
     * The meta object id for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSWorkflowDeployEImpl <em>WS Workflow Deploy E</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.WSWorkflowDeployEImpl
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getWSWorkflowDeployE()
     * @generated
     */
    int WS_WORKFLOW_DEPLOY_E = 15;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_WORKFLOW_DEPLOY_E__NAME = MDM_SERVER_OBJECT__NAME;

    /**
     * The feature id for the '<em><b>Description</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_WORKFLOW_DEPLOY_E__DESCRIPTION = MDM_SERVER_OBJECT__DESCRIPTION;

    /**
     * The feature id for the '<em><b>System</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_WORKFLOW_DEPLOY_E__SYSTEM = MDM_SERVER_OBJECT__SYSTEM;

    /**
     * The feature id for the '<em><b>Last Server Def</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_WORKFLOW_DEPLOY_E__LAST_SERVER_DEF = MDM_SERVER_OBJECT__LAST_SERVER_DEF;

    /**
     * The feature id for the '<em><b>Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_WORKFLOW_DEPLOY_E__TYPE = MDM_SERVER_OBJECT__TYPE;

    /**
     * The feature id for the '<em><b>Timestamp</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_WORKFLOW_DEPLOY_E__TIMESTAMP = MDM_SERVER_OBJECT__TIMESTAMP;

    /**
     * The feature id for the '<em><b>Digest Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_WORKFLOW_DEPLOY_E__DIGEST_VALUE = MDM_SERVER_OBJECT__DIGEST_VALUE;

    /**
     * The feature id for the '<em><b>Current Digest Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_WORKFLOW_DEPLOY_E__CURRENT_DIGEST_VALUE = MDM_SERVER_OBJECT__CURRENT_DIGEST_VALUE;

    /**
     * The feature id for the '<em><b>Last Server Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_WORKFLOW_DEPLOY_E__LAST_SERVER_NAME = MDM_SERVER_OBJECT__LAST_SERVER_NAME;

    /**
     * The feature id for the '<em><b>Filename</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_WORKFLOW_DEPLOY_E__FILENAME = MDM_SERVER_OBJECT_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the '<em>WS Workflow Deploy E</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_WORKFLOW_DEPLOY_E_FEATURE_COUNT = MDM_SERVER_OBJECT_FEATURE_COUNT + 1;

    /**
     * The meta object id for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSTransformerV2EImpl <em>WS Transformer V2E</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.WSTransformerV2EImpl
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getWSTransformerV2E()
     * @generated
     */
    int WS_TRANSFORMER_V2E = 16;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_TRANSFORMER_V2E__NAME = MDM_SERVER_OBJECT__NAME;

    /**
     * The feature id for the '<em><b>Description</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_TRANSFORMER_V2E__DESCRIPTION = MDM_SERVER_OBJECT__DESCRIPTION;

    /**
     * The feature id for the '<em><b>System</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_TRANSFORMER_V2E__SYSTEM = MDM_SERVER_OBJECT__SYSTEM;

    /**
     * The feature id for the '<em><b>Last Server Def</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_TRANSFORMER_V2E__LAST_SERVER_DEF = MDM_SERVER_OBJECT__LAST_SERVER_DEF;

    /**
     * The feature id for the '<em><b>Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_TRANSFORMER_V2E__TYPE = MDM_SERVER_OBJECT__TYPE;

    /**
     * The feature id for the '<em><b>Timestamp</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_TRANSFORMER_V2E__TIMESTAMP = MDM_SERVER_OBJECT__TIMESTAMP;

    /**
     * The feature id for the '<em><b>Digest Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_TRANSFORMER_V2E__DIGEST_VALUE = MDM_SERVER_OBJECT__DIGEST_VALUE;

    /**
     * The feature id for the '<em><b>Current Digest Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_TRANSFORMER_V2E__CURRENT_DIGEST_VALUE = MDM_SERVER_OBJECT__CURRENT_DIGEST_VALUE;

    /**
     * The feature id for the '<em><b>Last Server Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_TRANSFORMER_V2E__LAST_SERVER_NAME = MDM_SERVER_OBJECT__LAST_SERVER_NAME;

    /**
     * The feature id for the '<em><b>Process Steps</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_TRANSFORMER_V2E__PROCESS_STEPS = MDM_SERVER_OBJECT_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>With Admin Permissions</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_TRANSFORMER_V2E__WITH_ADMIN_PERMISSIONS = MDM_SERVER_OBJECT_FEATURE_COUNT + 1;

    /**
     * The number of structural features of the '<em>WS Transformer V2E</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_TRANSFORMER_V2E_FEATURE_COUNT = MDM_SERVER_OBJECT_FEATURE_COUNT + 2;

    /**
     * The meta object id for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSTransformerProcessStepEImpl <em>WS Transformer Process Step E</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.WSTransformerProcessStepEImpl
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getWSTransformerProcessStepE()
     * @generated
     */
    int WS_TRANSFORMER_PROCESS_STEP_E = 17;

    /**
     * The feature id for the '<em><b>Plugin JNDI</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_TRANSFORMER_PROCESS_STEP_E__PLUGIN_JNDI = 0;

    /**
     * The feature id for the '<em><b>Description</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_TRANSFORMER_PROCESS_STEP_E__DESCRIPTION = 1;

    /**
     * The feature id for the '<em><b>Parameters</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_TRANSFORMER_PROCESS_STEP_E__PARAMETERS = 2;

    /**
     * The feature id for the '<em><b>Disabled</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_TRANSFORMER_PROCESS_STEP_E__DISABLED = 3;

    /**
     * The feature id for the '<em><b>Input Mappings</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_TRANSFORMER_PROCESS_STEP_E__INPUT_MAPPINGS = 4;

    /**
     * The feature id for the '<em><b>Output Mappings</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_TRANSFORMER_PROCESS_STEP_E__OUTPUT_MAPPINGS = 5;

    /**
     * The number of structural features of the '<em>WS Transformer Process Step E</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_TRANSFORMER_PROCESS_STEP_E_FEATURE_COUNT = 6;

    /**
     * The meta object id for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSTransformerVariablesMappingEImpl <em>WS Transformer Variables Mapping E</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.WSTransformerVariablesMappingEImpl
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getWSTransformerVariablesMappingE()
     * @generated
     */
    int WS_TRANSFORMER_VARIABLES_MAPPING_E = 18;

    /**
     * The feature id for the '<em><b>Pipeline Variable</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_TRANSFORMER_VARIABLES_MAPPING_E__PIPELINE_VARIABLE = 0;

    /**
     * The feature id for the '<em><b>Plugin Variable</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_TRANSFORMER_VARIABLES_MAPPING_E__PLUGIN_VARIABLE = 1;

    /**
     * The feature id for the '<em><b>Hard Coding</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_TRANSFORMER_VARIABLES_MAPPING_E__HARD_CODING = 2;

    /**
     * The number of structural features of the '<em>WS Transformer Variables Mapping E</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_TRANSFORMER_VARIABLES_MAPPING_E_FEATURE_COUNT = 3;

    /**
     * The meta object id for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSTypedContentEImpl <em>WS Typed Content E</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.WSTypedContentEImpl
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getWSTypedContentE()
     * @generated
     */
    int WS_TYPED_CONTENT_E = 19;

    /**
     * The feature id for the '<em><b>Url</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_TYPED_CONTENT_E__URL = 0;

    /**
     * The feature id for the '<em><b>Content Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_TYPED_CONTENT_E__CONTENT_TYPE = 1;

    /**
     * The feature id for the '<em><b>Ws Bytes</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_TYPED_CONTENT_E__WS_BYTES = 2;

    /**
     * The number of structural features of the '<em>WS Typed Content E</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_TYPED_CONTENT_E_FEATURE_COUNT = 3;

    /**
     * The meta object id for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSByteArrayEImpl <em>WS Byte Array E</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.WSByteArrayEImpl
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getWSByteArrayE()
     * @generated
     */
    int WS_BYTE_ARRAY_E = 20;

    /**
     * The feature id for the '<em><b>Bytes</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_BYTE_ARRAY_E__BYTES = 0;

    /**
     * The number of structural features of the '<em>WS Byte Array E</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_BYTE_ARRAY_E_FEATURE_COUNT = 1;

    /**
     * The meta object id for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSRoutingRuleEImpl <em>WS Routing Rule E</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.WSRoutingRuleEImpl
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getWSRoutingRuleE()
     * @generated
     */
    int WS_ROUTING_RULE_E = 21;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_ROUTING_RULE_E__NAME = MDM_SERVER_OBJECT__NAME;

    /**
     * The feature id for the '<em><b>Description</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_ROUTING_RULE_E__DESCRIPTION = MDM_SERVER_OBJECT__DESCRIPTION;

    /**
     * The feature id for the '<em><b>System</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_ROUTING_RULE_E__SYSTEM = MDM_SERVER_OBJECT__SYSTEM;

    /**
     * The feature id for the '<em><b>Last Server Def</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_ROUTING_RULE_E__LAST_SERVER_DEF = MDM_SERVER_OBJECT__LAST_SERVER_DEF;

    /**
     * The feature id for the '<em><b>Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_ROUTING_RULE_E__TYPE = MDM_SERVER_OBJECT__TYPE;

    /**
     * The feature id for the '<em><b>Timestamp</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_ROUTING_RULE_E__TIMESTAMP = MDM_SERVER_OBJECT__TIMESTAMP;

    /**
     * The feature id for the '<em><b>Digest Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_ROUTING_RULE_E__DIGEST_VALUE = MDM_SERVER_OBJECT__DIGEST_VALUE;

    /**
     * The feature id for the '<em><b>Current Digest Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_ROUTING_RULE_E__CURRENT_DIGEST_VALUE = MDM_SERVER_OBJECT__CURRENT_DIGEST_VALUE;

    /**
     * The feature id for the '<em><b>Last Server Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_ROUTING_RULE_E__LAST_SERVER_NAME = MDM_SERVER_OBJECT__LAST_SERVER_NAME;

    /**
     * The feature id for the '<em><b>Synchronous</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_ROUTING_RULE_E__SYNCHRONOUS = MDM_SERVER_OBJECT_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Concept</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_ROUTING_RULE_E__CONCEPT = MDM_SERVER_OBJECT_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Service JNDI</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_ROUTING_RULE_E__SERVICE_JNDI = MDM_SERVER_OBJECT_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Parameters</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_ROUTING_RULE_E__PARAMETERS = MDM_SERVER_OBJECT_FEATURE_COUNT + 3;

    /**
     * The feature id for the '<em><b>Condition</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_ROUTING_RULE_E__CONDITION = MDM_SERVER_OBJECT_FEATURE_COUNT + 4;

    /**
     * The feature id for the '<em><b>Deactive</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_ROUTING_RULE_E__DEACTIVE = MDM_SERVER_OBJECT_FEATURE_COUNT + 5;

    /**
     * The feature id for the '<em><b>Execute Order</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_ROUTING_RULE_E__EXECUTE_ORDER = MDM_SERVER_OBJECT_FEATURE_COUNT + 6;

    /**
     * The feature id for the '<em><b>Ws Routing Rule Expressions</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_ROUTING_RULE_E__WS_ROUTING_RULE_EXPRESSIONS = MDM_SERVER_OBJECT_FEATURE_COUNT + 7;

    /**
     * The number of structural features of the '<em>WS Routing Rule E</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_ROUTING_RULE_E_FEATURE_COUNT = MDM_SERVER_OBJECT_FEATURE_COUNT + 8;

    /**
     * The meta object id for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSRoutingRuleExpressionEImpl <em>WS Routing Rule Expression E</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.WSRoutingRuleExpressionEImpl
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getWSRoutingRuleExpressionE()
     * @generated
     */
    int WS_ROUTING_RULE_EXPRESSION_E = 22;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_ROUTING_RULE_EXPRESSION_E__NAME = 0;

    /**
     * The feature id for the '<em><b>Xpath</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_ROUTING_RULE_EXPRESSION_E__XPATH = 1;

    /**
     * The feature id for the '<em><b>Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_ROUTING_RULE_EXPRESSION_E__VALUE = 2;

    /**
     * The feature id for the '<em><b>Ws Operator</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_ROUTING_RULE_EXPRESSION_E__WS_OPERATOR = 3;

    /**
     * The number of structural features of the '<em>WS Routing Rule Expression E</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_ROUTING_RULE_EXPRESSION_E_FEATURE_COUNT = 4;

    /**
     * The meta object id for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSRoutingRuleOperatorEImpl <em>WS Routing Rule Operator E</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.WSRoutingRuleOperatorEImpl
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getWSRoutingRuleOperatorE()
     * @generated
     */
    int WS_ROUTING_RULE_OPERATOR_E = 23;

    /**
     * The feature id for the '<em><b>Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_ROUTING_RULE_OPERATOR_E__VALUE = 0;

    /**
     * The number of structural features of the '<em>WS Routing Rule Operator E</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_ROUTING_RULE_OPERATOR_E_FEATURE_COUNT = 1;

    /**
     * The meta object id for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSJobModelEImpl <em>WS Job Model E</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.WSJobModelEImpl
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getWSJobModelE()
     * @generated
     */
    int WS_JOB_MODEL_E = 24;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_JOB_MODEL_E__NAME = MDM_SERVER_OBJECT__NAME;

    /**
     * The feature id for the '<em><b>Description</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_JOB_MODEL_E__DESCRIPTION = MDM_SERVER_OBJECT__DESCRIPTION;

    /**
     * The feature id for the '<em><b>System</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_JOB_MODEL_E__SYSTEM = MDM_SERVER_OBJECT__SYSTEM;

    /**
     * The feature id for the '<em><b>Last Server Def</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_JOB_MODEL_E__LAST_SERVER_DEF = MDM_SERVER_OBJECT__LAST_SERVER_DEF;

    /**
     * The feature id for the '<em><b>Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_JOB_MODEL_E__TYPE = MDM_SERVER_OBJECT__TYPE;

    /**
     * The feature id for the '<em><b>Timestamp</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_JOB_MODEL_E__TIMESTAMP = MDM_SERVER_OBJECT__TIMESTAMP;

    /**
     * The feature id for the '<em><b>Digest Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_JOB_MODEL_E__DIGEST_VALUE = MDM_SERVER_OBJECT__DIGEST_VALUE;

    /**
     * The feature id for the '<em><b>Current Digest Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_JOB_MODEL_E__CURRENT_DIGEST_VALUE = MDM_SERVER_OBJECT__CURRENT_DIGEST_VALUE;

    /**
     * The feature id for the '<em><b>Last Server Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_JOB_MODEL_E__LAST_SERVER_NAME = MDM_SERVER_OBJECT__LAST_SERVER_NAME;

    /**
     * The number of structural features of the '<em>WS Job Model E</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_JOB_MODEL_E_FEATURE_COUNT = MDM_SERVER_OBJECT_FEATURE_COUNT + 0;

    /**
     * The meta object id for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSEventManagerEImpl <em>WS Event Manager E</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.WSEventManagerEImpl
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getWSEventManagerE()
     * @generated
     */
    int WS_EVENT_MANAGER_E = 25;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_EVENT_MANAGER_E__NAME = MDM_SERVER_OBJECT__NAME;

    /**
     * The feature id for the '<em><b>Description</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_EVENT_MANAGER_E__DESCRIPTION = MDM_SERVER_OBJECT__DESCRIPTION;

    /**
     * The feature id for the '<em><b>System</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_EVENT_MANAGER_E__SYSTEM = MDM_SERVER_OBJECT__SYSTEM;

    /**
     * The feature id for the '<em><b>Last Server Def</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_EVENT_MANAGER_E__LAST_SERVER_DEF = MDM_SERVER_OBJECT__LAST_SERVER_DEF;

    /**
     * The feature id for the '<em><b>Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_EVENT_MANAGER_E__TYPE = MDM_SERVER_OBJECT__TYPE;

    /**
     * The feature id for the '<em><b>Timestamp</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_EVENT_MANAGER_E__TIMESTAMP = MDM_SERVER_OBJECT__TIMESTAMP;

    /**
     * The feature id for the '<em><b>Digest Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_EVENT_MANAGER_E__DIGEST_VALUE = MDM_SERVER_OBJECT__DIGEST_VALUE;

    /**
     * The feature id for the '<em><b>Current Digest Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_EVENT_MANAGER_E__CURRENT_DIGEST_VALUE = MDM_SERVER_OBJECT__CURRENT_DIGEST_VALUE;

    /**
     * The feature id for the '<em><b>Last Server Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_EVENT_MANAGER_E__LAST_SERVER_NAME = MDM_SERVER_OBJECT__LAST_SERVER_NAME;

    /**
     * The number of structural features of the '<em>WS Event Manager E</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_EVENT_MANAGER_E_FEATURE_COUNT = MDM_SERVER_OBJECT_FEATURE_COUNT + 0;

    /**
     * The meta object id for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSServiceConfigurationEImpl <em>WS Service Configuration E</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.WSServiceConfigurationEImpl
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getWSServiceConfigurationE()
     * @generated
     */
    int WS_SERVICE_CONFIGURATION_E = 26;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_SERVICE_CONFIGURATION_E__NAME = MDM_SERVER_OBJECT__NAME;

    /**
     * The feature id for the '<em><b>Description</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_SERVICE_CONFIGURATION_E__DESCRIPTION = MDM_SERVER_OBJECT__DESCRIPTION;

    /**
     * The feature id for the '<em><b>System</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_SERVICE_CONFIGURATION_E__SYSTEM = MDM_SERVER_OBJECT__SYSTEM;

    /**
     * The feature id for the '<em><b>Last Server Def</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_SERVICE_CONFIGURATION_E__LAST_SERVER_DEF = MDM_SERVER_OBJECT__LAST_SERVER_DEF;

    /**
     * The feature id for the '<em><b>Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_SERVICE_CONFIGURATION_E__TYPE = MDM_SERVER_OBJECT__TYPE;

    /**
     * The feature id for the '<em><b>Timestamp</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_SERVICE_CONFIGURATION_E__TIMESTAMP = MDM_SERVER_OBJECT__TIMESTAMP;

    /**
     * The feature id for the '<em><b>Digest Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_SERVICE_CONFIGURATION_E__DIGEST_VALUE = MDM_SERVER_OBJECT__DIGEST_VALUE;

    /**
     * The feature id for the '<em><b>Current Digest Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_SERVICE_CONFIGURATION_E__CURRENT_DIGEST_VALUE = MDM_SERVER_OBJECT__CURRENT_DIGEST_VALUE;

    /**
     * The feature id for the '<em><b>Last Server Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_SERVICE_CONFIGURATION_E__LAST_SERVER_NAME = MDM_SERVER_OBJECT__LAST_SERVER_NAME;

    /**
     * The feature id for the '<em><b>Service Put Configurations</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_SERVICE_CONFIGURATION_E__SERVICE_PUT_CONFIGURATIONS = MDM_SERVER_OBJECT_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the '<em>WS Service Configuration E</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_SERVICE_CONFIGURATION_E_FEATURE_COUNT = MDM_SERVER_OBJECT_FEATURE_COUNT + 1;

    /**
     * The meta object id for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSServicePutConfigurationEImpl <em>WS Service Put Configuration E</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.WSServicePutConfigurationEImpl
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getWSServicePutConfigurationE()
     * @generated
     */
    int WS_SERVICE_PUT_CONFIGURATION_E = 27;

    /**
     * The feature id for the '<em><b>Jndi Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_SERVICE_PUT_CONFIGURATION_E__JNDI_NAME = 0;

    /**
     * The feature id for the '<em><b>Configuration</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_SERVICE_PUT_CONFIGURATION_E__CONFIGURATION = 1;

    /**
     * The number of structural features of the '<em>WS Service Put Configuration E</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_SERVICE_PUT_CONFIGURATION_E_FEATURE_COUNT = 2;

    /**
     * The meta object id for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSWorkflowEImpl <em>WS Workflow E</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.WSWorkflowEImpl
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getWSWorkflowE()
     * @generated
     */
    int WS_WORKFLOW_E = 28;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_WORKFLOW_E__NAME = MDM_SERVER_OBJECT__NAME;

    /**
     * The feature id for the '<em><b>Description</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_WORKFLOW_E__DESCRIPTION = MDM_SERVER_OBJECT__DESCRIPTION;

    /**
     * The feature id for the '<em><b>System</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_WORKFLOW_E__SYSTEM = MDM_SERVER_OBJECT__SYSTEM;

    /**
     * The feature id for the '<em><b>Last Server Def</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_WORKFLOW_E__LAST_SERVER_DEF = MDM_SERVER_OBJECT__LAST_SERVER_DEF;

    /**
     * The feature id for the '<em><b>Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_WORKFLOW_E__TYPE = MDM_SERVER_OBJECT__TYPE;

    /**
     * The feature id for the '<em><b>Timestamp</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_WORKFLOW_E__TIMESTAMP = MDM_SERVER_OBJECT__TIMESTAMP;

    /**
     * The feature id for the '<em><b>Digest Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_WORKFLOW_E__DIGEST_VALUE = MDM_SERVER_OBJECT__DIGEST_VALUE;

    /**
     * The feature id for the '<em><b>Current Digest Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_WORKFLOW_E__CURRENT_DIGEST_VALUE = MDM_SERVER_OBJECT__CURRENT_DIGEST_VALUE;

    /**
     * The feature id for the '<em><b>Last Server Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_WORKFLOW_E__LAST_SERVER_NAME = MDM_SERVER_OBJECT__LAST_SERVER_NAME;

    /**
     * The feature id for the '<em><b>File Content</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_WORKFLOW_E__FILE_CONTENT = MDM_SERVER_OBJECT_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the '<em>WS Workflow E</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_WORKFLOW_E_FEATURE_COUNT = MDM_SERVER_OBJECT_FEATURE_COUNT + 1;

    /**
     * The meta object id for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSResourceEImpl <em>WS Resource E</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.WSResourceEImpl
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getWSResourceE()
     * @generated
     */
    int WS_RESOURCE_E = 29;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_RESOURCE_E__NAME = MDM_SERVER_OBJECT__NAME;

    /**
     * The feature id for the '<em><b>Description</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_RESOURCE_E__DESCRIPTION = MDM_SERVER_OBJECT__DESCRIPTION;

    /**
     * The feature id for the '<em><b>System</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_RESOURCE_E__SYSTEM = MDM_SERVER_OBJECT__SYSTEM;

    /**
     * The feature id for the '<em><b>Last Server Def</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_RESOURCE_E__LAST_SERVER_DEF = MDM_SERVER_OBJECT__LAST_SERVER_DEF;

    /**
     * The feature id for the '<em><b>Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_RESOURCE_E__TYPE = MDM_SERVER_OBJECT__TYPE;

    /**
     * The feature id for the '<em><b>Timestamp</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_RESOURCE_E__TIMESTAMP = MDM_SERVER_OBJECT__TIMESTAMP;

    /**
     * The feature id for the '<em><b>Digest Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_RESOURCE_E__DIGEST_VALUE = MDM_SERVER_OBJECT__DIGEST_VALUE;

    /**
     * The feature id for the '<em><b>Current Digest Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_RESOURCE_E__CURRENT_DIGEST_VALUE = MDM_SERVER_OBJECT__CURRENT_DIGEST_VALUE;

    /**
     * The feature id for the '<em><b>Last Server Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_RESOURCE_E__LAST_SERVER_NAME = MDM_SERVER_OBJECT__LAST_SERVER_NAME;

    /**
     * The feature id for the '<em><b>File Extension</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_RESOURCE_E__FILE_EXTENSION = MDM_SERVER_OBJECT_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>File Content</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_RESOURCE_E__FILE_CONTENT = MDM_SERVER_OBJECT_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Image Catalog</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_RESOURCE_E__IMAGE_CATALOG = MDM_SERVER_OBJECT_FEATURE_COUNT + 2;

    /**
     * The number of structural features of the '<em>WS Resource E</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_RESOURCE_E_FEATURE_COUNT = MDM_SERVER_OBJECT_FEATURE_COUNT + 3;

    /**
     * The meta object id for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSCustomFormEImpl <em>WS Custom Form E</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.WSCustomFormEImpl
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getWSCustomFormE()
     * @generated
     */
    int WS_CUSTOM_FORM_E = 30;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_CUSTOM_FORM_E__NAME = MDM_SERVER_OBJECT__NAME;

    /**
     * The feature id for the '<em><b>Description</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_CUSTOM_FORM_E__DESCRIPTION = MDM_SERVER_OBJECT__DESCRIPTION;

    /**
     * The feature id for the '<em><b>System</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_CUSTOM_FORM_E__SYSTEM = MDM_SERVER_OBJECT__SYSTEM;

    /**
     * The feature id for the '<em><b>Last Server Def</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_CUSTOM_FORM_E__LAST_SERVER_DEF = MDM_SERVER_OBJECT__LAST_SERVER_DEF;

    /**
     * The feature id for the '<em><b>Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_CUSTOM_FORM_E__TYPE = MDM_SERVER_OBJECT__TYPE;

    /**
     * The feature id for the '<em><b>Timestamp</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_CUSTOM_FORM_E__TIMESTAMP = MDM_SERVER_OBJECT__TIMESTAMP;

    /**
     * The feature id for the '<em><b>Digest Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_CUSTOM_FORM_E__DIGEST_VALUE = MDM_SERVER_OBJECT__DIGEST_VALUE;

    /**
     * The feature id for the '<em><b>Current Digest Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_CUSTOM_FORM_E__CURRENT_DIGEST_VALUE = MDM_SERVER_OBJECT__CURRENT_DIGEST_VALUE;

    /**
     * The feature id for the '<em><b>Last Server Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_CUSTOM_FORM_E__LAST_SERVER_NAME = MDM_SERVER_OBJECT__LAST_SERVER_NAME;

    /**
     * The feature id for the '<em><b>Filename</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_CUSTOM_FORM_E__FILENAME = MDM_SERVER_OBJECT_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Datamodel</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_CUSTOM_FORM_E__DATAMODEL = MDM_SERVER_OBJECT_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Entity</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_CUSTOM_FORM_E__ENTITY = MDM_SERVER_OBJECT_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Xml</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_CUSTOM_FORM_E__XML = MDM_SERVER_OBJECT_FEATURE_COUNT + 3;

    /**
     * The feature id for the '<em><b>Role</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_CUSTOM_FORM_E__ROLE = MDM_SERVER_OBJECT_FEATURE_COUNT + 4;

    /**
     * The number of structural features of the '<em>WS Custom Form E</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_CUSTOM_FORM_E_FEATURE_COUNT = MDM_SERVER_OBJECT_FEATURE_COUNT + 5;

    /**
     * The meta object id for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSMatchRuleEImpl <em>WS Match Rule E</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.WSMatchRuleEImpl
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getWSMatchRuleE()
     * @generated
     */
    int WS_MATCH_RULE_E = 31;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_MATCH_RULE_E__NAME = MDM_SERVER_OBJECT__NAME;

    /**
     * The feature id for the '<em><b>Description</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_MATCH_RULE_E__DESCRIPTION = MDM_SERVER_OBJECT__DESCRIPTION;

    /**
     * The feature id for the '<em><b>System</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_MATCH_RULE_E__SYSTEM = MDM_SERVER_OBJECT__SYSTEM;

    /**
     * The feature id for the '<em><b>Last Server Def</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_MATCH_RULE_E__LAST_SERVER_DEF = MDM_SERVER_OBJECT__LAST_SERVER_DEF;

    /**
     * The feature id for the '<em><b>Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_MATCH_RULE_E__TYPE = MDM_SERVER_OBJECT__TYPE;

    /**
     * The feature id for the '<em><b>Timestamp</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_MATCH_RULE_E__TIMESTAMP = MDM_SERVER_OBJECT__TIMESTAMP;

    /**
     * The feature id for the '<em><b>Digest Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_MATCH_RULE_E__DIGEST_VALUE = MDM_SERVER_OBJECT__DIGEST_VALUE;

    /**
     * The feature id for the '<em><b>Current Digest Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_MATCH_RULE_E__CURRENT_DIGEST_VALUE = MDM_SERVER_OBJECT__CURRENT_DIGEST_VALUE;

    /**
     * The feature id for the '<em><b>Last Server Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_MATCH_RULE_E__LAST_SERVER_NAME = MDM_SERVER_OBJECT__LAST_SERVER_NAME;

    /**
     * The feature id for the '<em><b>Configuration Xml Content</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_MATCH_RULE_E__CONFIGURATION_XML_CONTENT = MDM_SERVER_OBJECT_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>PK</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_MATCH_RULE_E__PK = MDM_SERVER_OBJECT_FEATURE_COUNT + 1;

    /**
     * The number of structural features of the '<em>WS Match Rule E</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_MATCH_RULE_E_FEATURE_COUNT = MDM_SERVER_OBJECT_FEATURE_COUNT + 2;

    /**
     * The meta object id for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSMatchRulePKEImpl <em>WS Match Rule PKE</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.WSMatchRulePKEImpl
     * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getWSMatchRulePKE()
     * @generated
     */
    int WS_MATCH_RULE_PKE = 32;

    /**
     * The feature id for the '<em><b>Pk</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_MATCH_RULE_PKE__PK = 0;

    /**
     * The number of structural features of the '<em>WS Match Rule PKE</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WS_MATCH_RULE_PKE_FEATURE_COUNT = 1;


    /**
     * Returns the meta object for class '{@link org.talend.mdm.repository.model.mdmserverobject.MDMServerObject <em>MDM Server Object</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>MDM Server Object</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.MDMServerObject
     * @generated
     */
    EClass getMDMServerObject();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.MDMServerObject#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.MDMServerObject#getName()
     * @see #getMDMServerObject()
     * @generated
     */
    EAttribute getMDMServerObject_Name();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.MDMServerObject#getDescription <em>Description</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Description</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.MDMServerObject#getDescription()
     * @see #getMDMServerObject()
     * @generated
     */
    EAttribute getMDMServerObject_Description();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.MDMServerObject#isSystem <em>System</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>System</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.MDMServerObject#isSystem()
     * @see #getMDMServerObject()
     * @generated
     */
    EAttribute getMDMServerObject_System();

    /**
     * Returns the meta object for the containment reference '{@link org.talend.mdm.repository.model.mdmserverobject.MDMServerObject#getLastServerDef <em>Last Server Def</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Last Server Def</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.MDMServerObject#getLastServerDef()
     * @see #getMDMServerObject()
     * @generated
     */
    EReference getMDMServerObject_LastServerDef();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.MDMServerObject#getType <em>Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Type</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.MDMServerObject#getType()
     * @see #getMDMServerObject()
     * @generated
     */
    EAttribute getMDMServerObject_Type();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.MDMServerObject#getTimestamp <em>Timestamp</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Timestamp</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.MDMServerObject#getTimestamp()
     * @see #getMDMServerObject()
     * @generated
     */
    EAttribute getMDMServerObject_Timestamp();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.MDMServerObject#getDigestValue <em>Digest Value</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Digest Value</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.MDMServerObject#getDigestValue()
     * @see #getMDMServerObject()
     * @generated
     */
    EAttribute getMDMServerObject_DigestValue();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.MDMServerObject#getCurrentDigestValue <em>Current Digest Value</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Current Digest Value</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.MDMServerObject#getCurrentDigestValue()
     * @see #getMDMServerObject()
     * @generated
     */
    EAttribute getMDMServerObject_CurrentDigestValue();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.MDMServerObject#getLastServerName <em>Last Server Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Last Server Name</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.MDMServerObject#getLastServerName()
     * @see #getMDMServerObject()
     * @generated
     */
    EAttribute getMDMServerObject_LastServerName();

    /**
     * Returns the meta object for class '{@link org.talend.mdm.repository.model.mdmserverobject.WSMenuE <em>WS Menu E</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>WS Menu E</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSMenuE
     * @generated
     */
    EClass getWSMenuE();

    /**
     * Returns the meta object for the containment reference list '{@link org.talend.mdm.repository.model.mdmserverobject.WSMenuE#getMenuEntries <em>Menu Entries</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Menu Entries</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSMenuE#getMenuEntries()
     * @see #getWSMenuE()
     * @generated
     */
    EReference getWSMenuE_MenuEntries();

    /**
     * Returns the meta object for class '{@link org.talend.mdm.repository.model.mdmserverobject.WSMenuEntryE <em>WS Menu Entry E</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>WS Menu Entry E</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSMenuEntryE
     * @generated
     */
    EClass getWSMenuEntryE();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.WSMenuEntryE#getId <em>Id</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Id</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSMenuEntryE#getId()
     * @see #getWSMenuEntryE()
     * @generated
     */
    EAttribute getWSMenuEntryE_Id();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.WSMenuEntryE#getApplication <em>Application</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Application</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSMenuEntryE#getApplication()
     * @see #getWSMenuEntryE()
     * @generated
     */
    EAttribute getWSMenuEntryE_Application();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.WSMenuEntryE#getContext <em>Context</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Context</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSMenuEntryE#getContext()
     * @see #getWSMenuEntryE()
     * @generated
     */
    EAttribute getWSMenuEntryE_Context();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.WSMenuEntryE#getIcon <em>Icon</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Icon</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSMenuEntryE#getIcon()
     * @see #getWSMenuEntryE()
     * @generated
     */
    EAttribute getWSMenuEntryE_Icon();

    /**
     * Returns the meta object for the containment reference list '{@link org.talend.mdm.repository.model.mdmserverobject.WSMenuEntryE#getDescriptions <em>Descriptions</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Descriptions</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSMenuEntryE#getDescriptions()
     * @see #getWSMenuEntryE()
     * @generated
     */
    EReference getWSMenuEntryE_Descriptions();

    /**
     * Returns the meta object for the containment reference list '{@link org.talend.mdm.repository.model.mdmserverobject.WSMenuEntryE#getSubMenus <em>Sub Menus</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Sub Menus</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSMenuEntryE#getSubMenus()
     * @see #getWSMenuEntryE()
     * @generated
     */
    EReference getWSMenuEntryE_SubMenus();

    /**
     * Returns the meta object for class '{@link org.talend.mdm.repository.model.mdmserverobject.WSMenuMenuEntriesDescriptionsE <em>WS Menu Menu Entries Descriptions E</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>WS Menu Menu Entries Descriptions E</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSMenuMenuEntriesDescriptionsE
     * @generated
     */
    EClass getWSMenuMenuEntriesDescriptionsE();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.WSMenuMenuEntriesDescriptionsE#getLanguage <em>Language</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Language</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSMenuMenuEntriesDescriptionsE#getLanguage()
     * @see #getWSMenuMenuEntriesDescriptionsE()
     * @generated
     */
    EAttribute getWSMenuMenuEntriesDescriptionsE_Language();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.WSMenuMenuEntriesDescriptionsE#getLabel <em>Label</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Label</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSMenuMenuEntriesDescriptionsE#getLabel()
     * @see #getWSMenuMenuEntriesDescriptionsE()
     * @generated
     */
    EAttribute getWSMenuMenuEntriesDescriptionsE_Label();

    /**
     * Returns the meta object for class '{@link org.talend.mdm.repository.model.mdmserverobject.WSRoleE <em>WS Role E</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>WS Role E</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSRoleE
     * @generated
     */
    EClass getWSRoleE();

    /**
     * Returns the meta object for the containment reference list '{@link org.talend.mdm.repository.model.mdmserverobject.WSRoleE#getSpecification <em>Specification</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Specification</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSRoleE#getSpecification()
     * @see #getWSRoleE()
     * @generated
     */
    EReference getWSRoleE_Specification();

    /**
     * Returns the meta object for class '{@link org.talend.mdm.repository.model.mdmserverobject.WSRoleSpecificationE <em>WS Role Specification E</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>WS Role Specification E</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSRoleSpecificationE
     * @generated
     */
    EClass getWSRoleSpecificationE();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.WSRoleSpecificationE#isAdmin <em>Admin</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Admin</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSRoleSpecificationE#isAdmin()
     * @see #getWSRoleSpecificationE()
     * @generated
     */
    EAttribute getWSRoleSpecificationE_Admin();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.WSRoleSpecificationE#getObjectType <em>Object Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Object Type</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSRoleSpecificationE#getObjectType()
     * @see #getWSRoleSpecificationE()
     * @generated
     */
    EAttribute getWSRoleSpecificationE_ObjectType();

    /**
     * Returns the meta object for the containment reference list '{@link org.talend.mdm.repository.model.mdmserverobject.WSRoleSpecificationE#getInstance <em>Instance</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Instance</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSRoleSpecificationE#getInstance()
     * @see #getWSRoleSpecificationE()
     * @generated
     */
    EReference getWSRoleSpecificationE_Instance();

    /**
     * Returns the meta object for class '{@link org.talend.mdm.repository.model.mdmserverobject.WSRoleSpecificationInstanceE <em>WS Role Specification Instance E</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>WS Role Specification Instance E</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSRoleSpecificationInstanceE
     * @generated
     */
    EClass getWSRoleSpecificationInstanceE();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.WSRoleSpecificationInstanceE#getInstanceName <em>Instance Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Instance Name</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSRoleSpecificationInstanceE#getInstanceName()
     * @see #getWSRoleSpecificationInstanceE()
     * @generated
     */
    EAttribute getWSRoleSpecificationInstanceE_InstanceName();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.WSRoleSpecificationInstanceE#isWritable <em>Writable</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Writable</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSRoleSpecificationInstanceE#isWritable()
     * @see #getWSRoleSpecificationInstanceE()
     * @generated
     */
    EAttribute getWSRoleSpecificationInstanceE_Writable();

    /**
     * Returns the meta object for the attribute list '{@link org.talend.mdm.repository.model.mdmserverobject.WSRoleSpecificationInstanceE#getParameter <em>Parameter</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute list '<em>Parameter</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSRoleSpecificationInstanceE#getParameter()
     * @see #getWSRoleSpecificationInstanceE()
     * @generated
     */
    EAttribute getWSRoleSpecificationInstanceE_Parameter();

    /**
     * Returns the meta object for class '{@link org.talend.mdm.repository.model.mdmserverobject.WSViewE <em>WS View E</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>WS View E</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSViewE
     * @generated
     */
    EClass getWSViewE();

    /**
     * Returns the meta object for the attribute list '{@link org.talend.mdm.repository.model.mdmserverobject.WSViewE#getSearchableBusinessElements <em>Searchable Business Elements</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute list '<em>Searchable Business Elements</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSViewE#getSearchableBusinessElements()
     * @see #getWSViewE()
     * @generated
     */
    EAttribute getWSViewE_SearchableBusinessElements();

    /**
     * Returns the meta object for the attribute list '{@link org.talend.mdm.repository.model.mdmserverobject.WSViewE#getViewableBusinessElements <em>Viewable Business Elements</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute list '<em>Viewable Business Elements</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSViewE#getViewableBusinessElements()
     * @see #getWSViewE()
     * @generated
     */
    EAttribute getWSViewE_ViewableBusinessElements();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.WSViewE#isTransformerActive <em>Transformer Active</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Transformer Active</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSViewE#isTransformerActive()
     * @see #getWSViewE()
     * @generated
     */
    EAttribute getWSViewE_TransformerActive();

    /**
     * Returns the meta object for the containment reference list '{@link org.talend.mdm.repository.model.mdmserverobject.WSViewE#getWhereConditions <em>Where Conditions</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Where Conditions</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSViewE#getWhereConditions()
     * @see #getWSViewE()
     * @generated
     */
    EReference getWSViewE_WhereConditions();

    /**
     * Returns the meta object for the containment reference '{@link org.talend.mdm.repository.model.mdmserverobject.WSViewE#getIsTransformerActive <em>Is Transformer Active</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Is Transformer Active</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSViewE#getIsTransformerActive()
     * @see #getWSViewE()
     * @generated
     */
    EReference getWSViewE_IsTransformerActive();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.WSViewE#getTransformerPK <em>Transformer PK</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Transformer PK</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSViewE#getTransformerPK()
     * @see #getWSViewE()
     * @generated
     */
    EAttribute getWSViewE_TransformerPK();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.WSViewE#getSortField <em>Sort Field</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Sort Field</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSViewE#getSortField()
     * @see #getWSViewE()
     * @generated
     */
    EAttribute getWSViewE_SortField();

    /**
     * Returns the meta object for the containment reference '{@link org.talend.mdm.repository.model.mdmserverobject.WSViewE#getIsAsc <em>Is Asc</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Is Asc</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSViewE#getIsAsc()
     * @see #getWSViewE()
     * @generated
     */
    EReference getWSViewE_IsAsc();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.WSViewE#getCustomForm <em>Custom Form</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Custom Form</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSViewE#getCustomForm()
     * @see #getWSViewE()
     * @generated
     */
    EAttribute getWSViewE_CustomForm();

    /**
     * Returns the meta object for class '{@link org.talend.mdm.repository.model.mdmserverobject.WSWhereConditionE <em>WS Where Condition E</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>WS Where Condition E</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSWhereConditionE
     * @generated
     */
    EClass getWSWhereConditionE();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.WSWhereConditionE#getLeftPath <em>Left Path</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Left Path</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSWhereConditionE#getLeftPath()
     * @see #getWSWhereConditionE()
     * @generated
     */
    EAttribute getWSWhereConditionE_LeftPath();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.WSWhereConditionE#getRightValueOrPath <em>Right Value Or Path</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Right Value Or Path</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSWhereConditionE#getRightValueOrPath()
     * @see #getWSWhereConditionE()
     * @generated
     */
    EAttribute getWSWhereConditionE_RightValueOrPath();

    /**
     * Returns the meta object for the containment reference '{@link org.talend.mdm.repository.model.mdmserverobject.WSWhereConditionE#getStringPredicate <em>String Predicate</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>String Predicate</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSWhereConditionE#getStringPredicate()
     * @see #getWSWhereConditionE()
     * @generated
     */
    EReference getWSWhereConditionE_StringPredicate();

    /**
     * Returns the meta object for the containment reference '{@link org.talend.mdm.repository.model.mdmserverobject.WSWhereConditionE#getOperator <em>Operator</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Operator</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSWhereConditionE#getOperator()
     * @see #getWSWhereConditionE()
     * @generated
     */
    EReference getWSWhereConditionE_Operator();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.WSWhereConditionE#isSpellCheck <em>Spell Check</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Spell Check</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSWhereConditionE#isSpellCheck()
     * @see #getWSWhereConditionE()
     * @generated
     */
    EAttribute getWSWhereConditionE_SpellCheck();

    /**
     * Returns the meta object for class '{@link org.talend.mdm.repository.model.mdmserverobject.WSWhereOperatorE <em>WS Where Operator E</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>WS Where Operator E</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSWhereOperatorE
     * @generated
     */
    EClass getWSWhereOperatorE();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.WSWhereOperatorE#getValue <em>Value</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Value</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSWhereOperatorE#getValue()
     * @see #getWSWhereOperatorE()
     * @generated
     */
    EAttribute getWSWhereOperatorE_Value();

    /**
     * Returns the meta object for class '{@link org.talend.mdm.repository.model.mdmserverobject.WSStringPredicateE <em>WS String Predicate E</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>WS String Predicate E</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSStringPredicateE
     * @generated
     */
    EClass getWSStringPredicateE();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.WSStringPredicateE#getValue <em>Value</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Value</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSStringPredicateE#getValue()
     * @see #getWSStringPredicateE()
     * @generated
     */
    EAttribute getWSStringPredicateE_Value();

    /**
     * Returns the meta object for class '{@link org.talend.mdm.repository.model.mdmserverobject.WSDataModelE <em>WS Data Model E</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>WS Data Model E</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSDataModelE
     * @generated
     */
    EClass getWSDataModelE();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.WSDataModelE#getXsdSchema <em>Xsd Schema</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Xsd Schema</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSDataModelE#getXsdSchema()
     * @see #getWSDataModelE()
     * @generated
     */
    EAttribute getWSDataModelE_XsdSchema();

    /**
     * Returns the meta object for class '{@link org.talend.mdm.repository.model.mdmserverobject.WSDataClusterE <em>WS Data Cluster E</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>WS Data Cluster E</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSDataClusterE
     * @generated
     */
    EClass getWSDataClusterE();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.WSDataClusterE#getVocabulary <em>Vocabulary</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Vocabulary</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSDataClusterE#getVocabulary()
     * @see #getWSDataClusterE()
     * @generated
     */
    EAttribute getWSDataClusterE_Vocabulary();

    /**
     * Returns the meta object for class '{@link org.talend.mdm.repository.model.mdmserverobject.WSStoredProcedureE <em>WS Stored Procedure E</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>WS Stored Procedure E</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSStoredProcedureE
     * @generated
     */
    EClass getWSStoredProcedureE();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.WSStoredProcedureE#getProcedure <em>Procedure</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Procedure</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSStoredProcedureE#getProcedure()
     * @see #getWSStoredProcedureE()
     * @generated
     */
    EAttribute getWSStoredProcedureE_Procedure();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.WSStoredProcedureE#isRefreshCache <em>Refresh Cache</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Refresh Cache</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSStoredProcedureE#isRefreshCache()
     * @see #getWSStoredProcedureE()
     * @generated
     */
    EAttribute getWSStoredProcedureE_RefreshCache();

    /**
     * Returns the meta object for class '{@link org.talend.mdm.repository.model.mdmserverobject.WSBooleanE <em>WS Boolean E</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>WS Boolean E</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSBooleanE
     * @generated
     */
    EClass getWSBooleanE();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.WSBooleanE#is_true <em>true</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>true</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSBooleanE#is_true()
     * @see #getWSBooleanE()
     * @generated
     */
    EAttribute getWSBooleanE__true();

    /**
     * Returns the meta object for class '{@link org.talend.mdm.repository.model.mdmserverobject.WSWorkflowDeployE <em>WS Workflow Deploy E</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>WS Workflow Deploy E</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSWorkflowDeployE
     * @generated
     */
    EClass getWSWorkflowDeployE();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.WSWorkflowDeployE#getFilename <em>Filename</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Filename</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSWorkflowDeployE#getFilename()
     * @see #getWSWorkflowDeployE()
     * @generated
     */
    EAttribute getWSWorkflowDeployE_Filename();

    /**
     * Returns the meta object for class '{@link org.talend.mdm.repository.model.mdmserverobject.WSTransformerV2E <em>WS Transformer V2E</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>WS Transformer V2E</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSTransformerV2E
     * @generated
     */
    EClass getWSTransformerV2E();

    /**
     * Returns the meta object for the containment reference list '{@link org.talend.mdm.repository.model.mdmserverobject.WSTransformerV2E#getProcessSteps <em>Process Steps</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Process Steps</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSTransformerV2E#getProcessSteps()
     * @see #getWSTransformerV2E()
     * @generated
     */
    EReference getWSTransformerV2E_ProcessSteps();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.WSTransformerV2E#isWithAdminPermissions <em>With Admin Permissions</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>With Admin Permissions</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSTransformerV2E#isWithAdminPermissions()
     * @see #getWSTransformerV2E()
     * @generated
     */
    EAttribute getWSTransformerV2E_WithAdminPermissions();

    /**
     * Returns the meta object for class '{@link org.talend.mdm.repository.model.mdmserverobject.WSTransformerProcessStepE <em>WS Transformer Process Step E</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>WS Transformer Process Step E</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSTransformerProcessStepE
     * @generated
     */
    EClass getWSTransformerProcessStepE();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.WSTransformerProcessStepE#getPluginJNDI <em>Plugin JNDI</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Plugin JNDI</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSTransformerProcessStepE#getPluginJNDI()
     * @see #getWSTransformerProcessStepE()
     * @generated
     */
    EAttribute getWSTransformerProcessStepE_PluginJNDI();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.WSTransformerProcessStepE#getDescription <em>Description</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Description</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSTransformerProcessStepE#getDescription()
     * @see #getWSTransformerProcessStepE()
     * @generated
     */
    EAttribute getWSTransformerProcessStepE_Description();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.WSTransformerProcessStepE#getParameters <em>Parameters</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Parameters</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSTransformerProcessStepE#getParameters()
     * @see #getWSTransformerProcessStepE()
     * @generated
     */
    EAttribute getWSTransformerProcessStepE_Parameters();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.WSTransformerProcessStepE#isDisabled <em>Disabled</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Disabled</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSTransformerProcessStepE#isDisabled()
     * @see #getWSTransformerProcessStepE()
     * @generated
     */
    EAttribute getWSTransformerProcessStepE_Disabled();

    /**
     * Returns the meta object for the containment reference list '{@link org.talend.mdm.repository.model.mdmserverobject.WSTransformerProcessStepE#getInputMappings <em>Input Mappings</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Input Mappings</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSTransformerProcessStepE#getInputMappings()
     * @see #getWSTransformerProcessStepE()
     * @generated
     */
    EReference getWSTransformerProcessStepE_InputMappings();

    /**
     * Returns the meta object for the containment reference list '{@link org.talend.mdm.repository.model.mdmserverobject.WSTransformerProcessStepE#getOutputMappings <em>Output Mappings</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Output Mappings</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSTransformerProcessStepE#getOutputMappings()
     * @see #getWSTransformerProcessStepE()
     * @generated
     */
    EReference getWSTransformerProcessStepE_OutputMappings();

    /**
     * Returns the meta object for class '{@link org.talend.mdm.repository.model.mdmserverobject.WSTransformerVariablesMappingE <em>WS Transformer Variables Mapping E</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>WS Transformer Variables Mapping E</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSTransformerVariablesMappingE
     * @generated
     */
    EClass getWSTransformerVariablesMappingE();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.WSTransformerVariablesMappingE#getPipelineVariable <em>Pipeline Variable</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Pipeline Variable</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSTransformerVariablesMappingE#getPipelineVariable()
     * @see #getWSTransformerVariablesMappingE()
     * @generated
     */
    EAttribute getWSTransformerVariablesMappingE_PipelineVariable();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.WSTransformerVariablesMappingE#getPluginVariable <em>Plugin Variable</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Plugin Variable</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSTransformerVariablesMappingE#getPluginVariable()
     * @see #getWSTransformerVariablesMappingE()
     * @generated
     */
    EAttribute getWSTransformerVariablesMappingE_PluginVariable();

    /**
     * Returns the meta object for the containment reference '{@link org.talend.mdm.repository.model.mdmserverobject.WSTransformerVariablesMappingE#getHardCoding <em>Hard Coding</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Hard Coding</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSTransformerVariablesMappingE#getHardCoding()
     * @see #getWSTransformerVariablesMappingE()
     * @generated
     */
    EReference getWSTransformerVariablesMappingE_HardCoding();

    /**
     * Returns the meta object for class '{@link org.talend.mdm.repository.model.mdmserverobject.WSTypedContentE <em>WS Typed Content E</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>WS Typed Content E</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSTypedContentE
     * @generated
     */
    EClass getWSTypedContentE();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.WSTypedContentE#getUrl <em>Url</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Url</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSTypedContentE#getUrl()
     * @see #getWSTypedContentE()
     * @generated
     */
    EAttribute getWSTypedContentE_Url();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.WSTypedContentE#getContentType <em>Content Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Content Type</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSTypedContentE#getContentType()
     * @see #getWSTypedContentE()
     * @generated
     */
    EAttribute getWSTypedContentE_ContentType();

    /**
     * Returns the meta object for the containment reference '{@link org.talend.mdm.repository.model.mdmserverobject.WSTypedContentE#getWsBytes <em>Ws Bytes</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Ws Bytes</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSTypedContentE#getWsBytes()
     * @see #getWSTypedContentE()
     * @generated
     */
    EReference getWSTypedContentE_WsBytes();

    /**
     * Returns the meta object for class '{@link org.talend.mdm.repository.model.mdmserverobject.WSByteArrayE <em>WS Byte Array E</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>WS Byte Array E</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSByteArrayE
     * @generated
     */
    EClass getWSByteArrayE();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.WSByteArrayE#getBytes <em>Bytes</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Bytes</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSByteArrayE#getBytes()
     * @see #getWSByteArrayE()
     * @generated
     */
    EAttribute getWSByteArrayE_Bytes();

    /**
     * Returns the meta object for class '{@link org.talend.mdm.repository.model.mdmserverobject.WSRoutingRuleE <em>WS Routing Rule E</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>WS Routing Rule E</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSRoutingRuleE
     * @generated
     */
    EClass getWSRoutingRuleE();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.WSRoutingRuleE#isSynchronous <em>Synchronous</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Synchronous</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSRoutingRuleE#isSynchronous()
     * @see #getWSRoutingRuleE()
     * @generated
     */
    EAttribute getWSRoutingRuleE_Synchronous();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.WSRoutingRuleE#getConcept <em>Concept</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Concept</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSRoutingRuleE#getConcept()
     * @see #getWSRoutingRuleE()
     * @generated
     */
    EAttribute getWSRoutingRuleE_Concept();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.WSRoutingRuleE#getServiceJNDI <em>Service JNDI</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Service JNDI</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSRoutingRuleE#getServiceJNDI()
     * @see #getWSRoutingRuleE()
     * @generated
     */
    EAttribute getWSRoutingRuleE_ServiceJNDI();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.WSRoutingRuleE#getParameters <em>Parameters</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Parameters</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSRoutingRuleE#getParameters()
     * @see #getWSRoutingRuleE()
     * @generated
     */
    EAttribute getWSRoutingRuleE_Parameters();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.WSRoutingRuleE#getCondition <em>Condition</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Condition</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSRoutingRuleE#getCondition()
     * @see #getWSRoutingRuleE()
     * @generated
     */
    EAttribute getWSRoutingRuleE_Condition();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.WSRoutingRuleE#isDeactive <em>Deactive</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Deactive</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSRoutingRuleE#isDeactive()
     * @see #getWSRoutingRuleE()
     * @generated
     */
    EAttribute getWSRoutingRuleE_Deactive();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.WSRoutingRuleE#getExecuteOrder <em>Execute Order</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Execute Order</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSRoutingRuleE#getExecuteOrder()
     * @see #getWSRoutingRuleE()
     * @generated
     */
    EAttribute getWSRoutingRuleE_ExecuteOrder();

    /**
     * Returns the meta object for the containment reference list '{@link org.talend.mdm.repository.model.mdmserverobject.WSRoutingRuleE#getWsRoutingRuleExpressions <em>Ws Routing Rule Expressions</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Ws Routing Rule Expressions</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSRoutingRuleE#getWsRoutingRuleExpressions()
     * @see #getWSRoutingRuleE()
     * @generated
     */
    EReference getWSRoutingRuleE_WsRoutingRuleExpressions();

    /**
     * Returns the meta object for class '{@link org.talend.mdm.repository.model.mdmserverobject.WSRoutingRuleExpressionE <em>WS Routing Rule Expression E</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>WS Routing Rule Expression E</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSRoutingRuleExpressionE
     * @generated
     */
    EClass getWSRoutingRuleExpressionE();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.WSRoutingRuleExpressionE#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSRoutingRuleExpressionE#getName()
     * @see #getWSRoutingRuleExpressionE()
     * @generated
     */
    EAttribute getWSRoutingRuleExpressionE_Name();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.WSRoutingRuleExpressionE#getXpath <em>Xpath</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Xpath</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSRoutingRuleExpressionE#getXpath()
     * @see #getWSRoutingRuleExpressionE()
     * @generated
     */
    EAttribute getWSRoutingRuleExpressionE_Xpath();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.WSRoutingRuleExpressionE#getValue <em>Value</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Value</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSRoutingRuleExpressionE#getValue()
     * @see #getWSRoutingRuleExpressionE()
     * @generated
     */
    EAttribute getWSRoutingRuleExpressionE_Value();

    /**
     * Returns the meta object for the containment reference '{@link org.talend.mdm.repository.model.mdmserverobject.WSRoutingRuleExpressionE#getWsOperator <em>Ws Operator</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Ws Operator</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSRoutingRuleExpressionE#getWsOperator()
     * @see #getWSRoutingRuleExpressionE()
     * @generated
     */
    EReference getWSRoutingRuleExpressionE_WsOperator();

    /**
     * Returns the meta object for class '{@link org.talend.mdm.repository.model.mdmserverobject.WSRoutingRuleOperatorE <em>WS Routing Rule Operator E</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>WS Routing Rule Operator E</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSRoutingRuleOperatorE
     * @generated
     */
    EClass getWSRoutingRuleOperatorE();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.WSRoutingRuleOperatorE#getValue <em>Value</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Value</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSRoutingRuleOperatorE#getValue()
     * @see #getWSRoutingRuleOperatorE()
     * @generated
     */
    EAttribute getWSRoutingRuleOperatorE_Value();

    /**
     * Returns the meta object for class '{@link org.talend.mdm.repository.model.mdmserverobject.WSJobModelE <em>WS Job Model E</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>WS Job Model E</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSJobModelE
     * @generated
     */
    EClass getWSJobModelE();

    /**
     * Returns the meta object for class '{@link org.talend.mdm.repository.model.mdmserverobject.WSEventManagerE <em>WS Event Manager E</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>WS Event Manager E</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSEventManagerE
     * @generated
     */
    EClass getWSEventManagerE();

    /**
     * Returns the meta object for class '{@link org.talend.mdm.repository.model.mdmserverobject.WSServiceConfigurationE <em>WS Service Configuration E</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>WS Service Configuration E</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSServiceConfigurationE
     * @generated
     */
    EClass getWSServiceConfigurationE();

    /**
     * Returns the meta object for the containment reference list '{@link org.talend.mdm.repository.model.mdmserverobject.WSServiceConfigurationE#getServicePutConfigurations <em>Service Put Configurations</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Service Put Configurations</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSServiceConfigurationE#getServicePutConfigurations()
     * @see #getWSServiceConfigurationE()
     * @generated
     */
    EReference getWSServiceConfigurationE_ServicePutConfigurations();

    /**
     * Returns the meta object for class '{@link org.talend.mdm.repository.model.mdmserverobject.WSServicePutConfigurationE <em>WS Service Put Configuration E</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>WS Service Put Configuration E</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSServicePutConfigurationE
     * @generated
     */
    EClass getWSServicePutConfigurationE();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.WSServicePutConfigurationE#getJndiName <em>Jndi Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Jndi Name</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSServicePutConfigurationE#getJndiName()
     * @see #getWSServicePutConfigurationE()
     * @generated
     */
    EAttribute getWSServicePutConfigurationE_JndiName();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.WSServicePutConfigurationE#getConfiguration <em>Configuration</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Configuration</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSServicePutConfigurationE#getConfiguration()
     * @see #getWSServicePutConfigurationE()
     * @generated
     */
    EAttribute getWSServicePutConfigurationE_Configuration();

    /**
     * Returns the meta object for class '{@link org.talend.mdm.repository.model.mdmserverobject.WSWorkflowE <em>WS Workflow E</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>WS Workflow E</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSWorkflowE
     * @generated
     */
    EClass getWSWorkflowE();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.WSWorkflowE#getFileContent <em>File Content</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>File Content</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSWorkflowE#getFileContent()
     * @see #getWSWorkflowE()
     * @generated
     */
    EAttribute getWSWorkflowE_FileContent();

    /**
     * Returns the meta object for class '{@link org.talend.mdm.repository.model.mdmserverobject.WSResourceE <em>WS Resource E</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>WS Resource E</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSResourceE
     * @generated
     */
    EClass getWSResourceE();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.WSResourceE#getFileExtension <em>File Extension</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>File Extension</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSResourceE#getFileExtension()
     * @see #getWSResourceE()
     * @generated
     */
    EAttribute getWSResourceE_FileExtension();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.WSResourceE#getFileContent <em>File Content</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>File Content</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSResourceE#getFileContent()
     * @see #getWSResourceE()
     * @generated
     */
    EAttribute getWSResourceE_FileContent();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.WSResourceE#getImageCatalog <em>Image Catalog</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Image Catalog</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSResourceE#getImageCatalog()
     * @see #getWSResourceE()
     * @generated
     */
    EAttribute getWSResourceE_ImageCatalog();

    /**
     * Returns the meta object for class '{@link org.talend.mdm.repository.model.mdmserverobject.WSCustomFormE <em>WS Custom Form E</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>WS Custom Form E</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSCustomFormE
     * @generated
     */
    EClass getWSCustomFormE();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.WSCustomFormE#getFilename <em>Filename</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Filename</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSCustomFormE#getFilename()
     * @see #getWSCustomFormE()
     * @generated
     */
    EAttribute getWSCustomFormE_Filename();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.WSCustomFormE#getDatamodel <em>Datamodel</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Datamodel</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSCustomFormE#getDatamodel()
     * @see #getWSCustomFormE()
     * @generated
     */
    EAttribute getWSCustomFormE_Datamodel();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.WSCustomFormE#getEntity <em>Entity</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Entity</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSCustomFormE#getEntity()
     * @see #getWSCustomFormE()
     * @generated
     */
    EAttribute getWSCustomFormE_Entity();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.WSCustomFormE#getXml <em>Xml</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Xml</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSCustomFormE#getXml()
     * @see #getWSCustomFormE()
     * @generated
     */
    EAttribute getWSCustomFormE_Xml();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.WSCustomFormE#getRole <em>Role</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Role</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSCustomFormE#getRole()
     * @see #getWSCustomFormE()
     * @generated
     */
    EAttribute getWSCustomFormE_Role();

    /**
     * Returns the meta object for class '{@link org.talend.mdm.repository.model.mdmserverobject.WSMatchRuleE <em>WS Match Rule E</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>WS Match Rule E</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSMatchRuleE
     * @generated
     */
    EClass getWSMatchRuleE();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.WSMatchRuleE#getConfigurationXmlContent <em>Configuration Xml Content</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Configuration Xml Content</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSMatchRuleE#getConfigurationXmlContent()
     * @see #getWSMatchRuleE()
     * @generated
     */
    EAttribute getWSMatchRuleE_ConfigurationXmlContent();

    /**
     * Returns the meta object for the containment reference '{@link org.talend.mdm.repository.model.mdmserverobject.WSMatchRuleE#getPK <em>PK</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>PK</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSMatchRuleE#getPK()
     * @see #getWSMatchRuleE()
     * @generated
     */
    EReference getWSMatchRuleE_PK();

    /**
     * Returns the meta object for class '{@link org.talend.mdm.repository.model.mdmserverobject.WSMatchRulePKE <em>WS Match Rule PKE</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>WS Match Rule PKE</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSMatchRulePKE
     * @generated
     */
    EClass getWSMatchRulePKE();

    /**
     * Returns the meta object for the attribute '{@link org.talend.mdm.repository.model.mdmserverobject.WSMatchRulePKE#getPk <em>Pk</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Pk</em>'.
     * @see org.talend.mdm.repository.model.mdmserverobject.WSMatchRulePKE#getPk()
     * @see #getWSMatchRulePKE()
     * @generated
     */
    EAttribute getWSMatchRulePKE_Pk();

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
    MdmserverobjectFactory getMdmserverobjectFactory();

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
         * The meta object literal for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.MDMServerObjectImpl <em>MDM Server Object</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.MDMServerObjectImpl
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getMDMServerObject()
         * @generated
         */
        EClass MDM_SERVER_OBJECT = eINSTANCE.getMDMServerObject();

        /**
         * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute MDM_SERVER_OBJECT__NAME = eINSTANCE.getMDMServerObject_Name();

        /**
         * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute MDM_SERVER_OBJECT__DESCRIPTION = eINSTANCE.getMDMServerObject_Description();

        /**
         * The meta object literal for the '<em><b>System</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute MDM_SERVER_OBJECT__SYSTEM = eINSTANCE.getMDMServerObject_System();

        /**
         * The meta object literal for the '<em><b>Last Server Def</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference MDM_SERVER_OBJECT__LAST_SERVER_DEF = eINSTANCE.getMDMServerObject_LastServerDef();

        /**
         * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute MDM_SERVER_OBJECT__TYPE = eINSTANCE.getMDMServerObject_Type();

        /**
         * The meta object literal for the '<em><b>Timestamp</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute MDM_SERVER_OBJECT__TIMESTAMP = eINSTANCE.getMDMServerObject_Timestamp();

        /**
         * The meta object literal for the '<em><b>Digest Value</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute MDM_SERVER_OBJECT__DIGEST_VALUE = eINSTANCE.getMDMServerObject_DigestValue();

        /**
         * The meta object literal for the '<em><b>Current Digest Value</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute MDM_SERVER_OBJECT__CURRENT_DIGEST_VALUE = eINSTANCE.getMDMServerObject_CurrentDigestValue();

        /**
         * The meta object literal for the '<em><b>Last Server Name</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute MDM_SERVER_OBJECT__LAST_SERVER_NAME = eINSTANCE.getMDMServerObject_LastServerName();

        /**
         * The meta object literal for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSMenuEImpl <em>WS Menu E</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.WSMenuEImpl
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getWSMenuE()
         * @generated
         */
        EClass WS_MENU_E = eINSTANCE.getWSMenuE();

        /**
         * The meta object literal for the '<em><b>Menu Entries</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference WS_MENU_E__MENU_ENTRIES = eINSTANCE.getWSMenuE_MenuEntries();

        /**
         * The meta object literal for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSMenuEntryEImpl <em>WS Menu Entry E</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.WSMenuEntryEImpl
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getWSMenuEntryE()
         * @generated
         */
        EClass WS_MENU_ENTRY_E = eINSTANCE.getWSMenuEntryE();

        /**
         * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute WS_MENU_ENTRY_E__ID = eINSTANCE.getWSMenuEntryE_Id();

        /**
         * The meta object literal for the '<em><b>Application</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute WS_MENU_ENTRY_E__APPLICATION = eINSTANCE.getWSMenuEntryE_Application();

        /**
         * The meta object literal for the '<em><b>Context</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute WS_MENU_ENTRY_E__CONTEXT = eINSTANCE.getWSMenuEntryE_Context();

        /**
         * The meta object literal for the '<em><b>Icon</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute WS_MENU_ENTRY_E__ICON = eINSTANCE.getWSMenuEntryE_Icon();

        /**
         * The meta object literal for the '<em><b>Descriptions</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference WS_MENU_ENTRY_E__DESCRIPTIONS = eINSTANCE.getWSMenuEntryE_Descriptions();

        /**
         * The meta object literal for the '<em><b>Sub Menus</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference WS_MENU_ENTRY_E__SUB_MENUS = eINSTANCE.getWSMenuEntryE_SubMenus();

        /**
         * The meta object literal for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSMenuMenuEntriesDescriptionsEImpl <em>WS Menu Menu Entries Descriptions E</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.WSMenuMenuEntriesDescriptionsEImpl
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getWSMenuMenuEntriesDescriptionsE()
         * @generated
         */
        EClass WS_MENU_MENU_ENTRIES_DESCRIPTIONS_E = eINSTANCE.getWSMenuMenuEntriesDescriptionsE();

        /**
         * The meta object literal for the '<em><b>Language</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute WS_MENU_MENU_ENTRIES_DESCRIPTIONS_E__LANGUAGE = eINSTANCE.getWSMenuMenuEntriesDescriptionsE_Language();

        /**
         * The meta object literal for the '<em><b>Label</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute WS_MENU_MENU_ENTRIES_DESCRIPTIONS_E__LABEL = eINSTANCE.getWSMenuMenuEntriesDescriptionsE_Label();

        /**
         * The meta object literal for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSRoleEImpl <em>WS Role E</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.WSRoleEImpl
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getWSRoleE()
         * @generated
         */
        EClass WS_ROLE_E = eINSTANCE.getWSRoleE();

        /**
         * The meta object literal for the '<em><b>Specification</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference WS_ROLE_E__SPECIFICATION = eINSTANCE.getWSRoleE_Specification();

        /**
         * The meta object literal for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSRoleSpecificationEImpl <em>WS Role Specification E</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.WSRoleSpecificationEImpl
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getWSRoleSpecificationE()
         * @generated
         */
        EClass WS_ROLE_SPECIFICATION_E = eINSTANCE.getWSRoleSpecificationE();

        /**
         * The meta object literal for the '<em><b>Admin</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute WS_ROLE_SPECIFICATION_E__ADMIN = eINSTANCE.getWSRoleSpecificationE_Admin();

        /**
         * The meta object literal for the '<em><b>Object Type</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute WS_ROLE_SPECIFICATION_E__OBJECT_TYPE = eINSTANCE.getWSRoleSpecificationE_ObjectType();

        /**
         * The meta object literal for the '<em><b>Instance</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference WS_ROLE_SPECIFICATION_E__INSTANCE = eINSTANCE.getWSRoleSpecificationE_Instance();

        /**
         * The meta object literal for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSRoleSpecificationInstanceEImpl <em>WS Role Specification Instance E</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.WSRoleSpecificationInstanceEImpl
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getWSRoleSpecificationInstanceE()
         * @generated
         */
        EClass WS_ROLE_SPECIFICATION_INSTANCE_E = eINSTANCE.getWSRoleSpecificationInstanceE();

        /**
         * The meta object literal for the '<em><b>Instance Name</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute WS_ROLE_SPECIFICATION_INSTANCE_E__INSTANCE_NAME = eINSTANCE.getWSRoleSpecificationInstanceE_InstanceName();

        /**
         * The meta object literal for the '<em><b>Writable</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute WS_ROLE_SPECIFICATION_INSTANCE_E__WRITABLE = eINSTANCE.getWSRoleSpecificationInstanceE_Writable();

        /**
         * The meta object literal for the '<em><b>Parameter</b></em>' attribute list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute WS_ROLE_SPECIFICATION_INSTANCE_E__PARAMETER = eINSTANCE.getWSRoleSpecificationInstanceE_Parameter();

        /**
         * The meta object literal for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSViewEImpl <em>WS View E</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.WSViewEImpl
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getWSViewE()
         * @generated
         */
        EClass WS_VIEW_E = eINSTANCE.getWSViewE();

        /**
         * The meta object literal for the '<em><b>Searchable Business Elements</b></em>' attribute list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute WS_VIEW_E__SEARCHABLE_BUSINESS_ELEMENTS = eINSTANCE.getWSViewE_SearchableBusinessElements();

        /**
         * The meta object literal for the '<em><b>Viewable Business Elements</b></em>' attribute list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute WS_VIEW_E__VIEWABLE_BUSINESS_ELEMENTS = eINSTANCE.getWSViewE_ViewableBusinessElements();

        /**
         * The meta object literal for the '<em><b>Transformer Active</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute WS_VIEW_E__TRANSFORMER_ACTIVE = eINSTANCE.getWSViewE_TransformerActive();

        /**
         * The meta object literal for the '<em><b>Where Conditions</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference WS_VIEW_E__WHERE_CONDITIONS = eINSTANCE.getWSViewE_WhereConditions();

        /**
         * The meta object literal for the '<em><b>Is Transformer Active</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference WS_VIEW_E__IS_TRANSFORMER_ACTIVE = eINSTANCE.getWSViewE_IsTransformerActive();

        /**
         * The meta object literal for the '<em><b>Transformer PK</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute WS_VIEW_E__TRANSFORMER_PK = eINSTANCE.getWSViewE_TransformerPK();

        /**
         * The meta object literal for the '<em><b>Sort Field</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute WS_VIEW_E__SORT_FIELD = eINSTANCE.getWSViewE_SortField();

        /**
         * The meta object literal for the '<em><b>Is Asc</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference WS_VIEW_E__IS_ASC = eINSTANCE.getWSViewE_IsAsc();

        /**
         * The meta object literal for the '<em><b>Custom Form</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute WS_VIEW_E__CUSTOM_FORM = eINSTANCE.getWSViewE_CustomForm();

        /**
         * The meta object literal for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSWhereConditionEImpl <em>WS Where Condition E</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.WSWhereConditionEImpl
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getWSWhereConditionE()
         * @generated
         */
        EClass WS_WHERE_CONDITION_E = eINSTANCE.getWSWhereConditionE();

        /**
         * The meta object literal for the '<em><b>Left Path</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute WS_WHERE_CONDITION_E__LEFT_PATH = eINSTANCE.getWSWhereConditionE_LeftPath();

        /**
         * The meta object literal for the '<em><b>Right Value Or Path</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute WS_WHERE_CONDITION_E__RIGHT_VALUE_OR_PATH = eINSTANCE.getWSWhereConditionE_RightValueOrPath();

        /**
         * The meta object literal for the '<em><b>String Predicate</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference WS_WHERE_CONDITION_E__STRING_PREDICATE = eINSTANCE.getWSWhereConditionE_StringPredicate();

        /**
         * The meta object literal for the '<em><b>Operator</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference WS_WHERE_CONDITION_E__OPERATOR = eINSTANCE.getWSWhereConditionE_Operator();

        /**
         * The meta object literal for the '<em><b>Spell Check</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute WS_WHERE_CONDITION_E__SPELL_CHECK = eINSTANCE.getWSWhereConditionE_SpellCheck();

        /**
         * The meta object literal for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSWhereOperatorEImpl <em>WS Where Operator E</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.WSWhereOperatorEImpl
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getWSWhereOperatorE()
         * @generated
         */
        EClass WS_WHERE_OPERATOR_E = eINSTANCE.getWSWhereOperatorE();

        /**
         * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute WS_WHERE_OPERATOR_E__VALUE = eINSTANCE.getWSWhereOperatorE_Value();

        /**
         * The meta object literal for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSStringPredicateEImpl <em>WS String Predicate E</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.WSStringPredicateEImpl
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getWSStringPredicateE()
         * @generated
         */
        EClass WS_STRING_PREDICATE_E = eINSTANCE.getWSStringPredicateE();

        /**
         * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute WS_STRING_PREDICATE_E__VALUE = eINSTANCE.getWSStringPredicateE_Value();

        /**
         * The meta object literal for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSDataModelEImpl <em>WS Data Model E</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.WSDataModelEImpl
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getWSDataModelE()
         * @generated
         */
        EClass WS_DATA_MODEL_E = eINSTANCE.getWSDataModelE();

        /**
         * The meta object literal for the '<em><b>Xsd Schema</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute WS_DATA_MODEL_E__XSD_SCHEMA = eINSTANCE.getWSDataModelE_XsdSchema();

        /**
         * The meta object literal for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSDataClusterEImpl <em>WS Data Cluster E</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.WSDataClusterEImpl
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getWSDataClusterE()
         * @generated
         */
        EClass WS_DATA_CLUSTER_E = eINSTANCE.getWSDataClusterE();

        /**
         * The meta object literal for the '<em><b>Vocabulary</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute WS_DATA_CLUSTER_E__VOCABULARY = eINSTANCE.getWSDataClusterE_Vocabulary();

        /**
         * The meta object literal for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSStoredProcedureEImpl <em>WS Stored Procedure E</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.WSStoredProcedureEImpl
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getWSStoredProcedureE()
         * @generated
         */
        EClass WS_STORED_PROCEDURE_E = eINSTANCE.getWSStoredProcedureE();

        /**
         * The meta object literal for the '<em><b>Procedure</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute WS_STORED_PROCEDURE_E__PROCEDURE = eINSTANCE.getWSStoredProcedureE_Procedure();

        /**
         * The meta object literal for the '<em><b>Refresh Cache</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute WS_STORED_PROCEDURE_E__REFRESH_CACHE = eINSTANCE.getWSStoredProcedureE_RefreshCache();

        /**
         * The meta object literal for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSBooleanEImpl <em>WS Boolean E</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.WSBooleanEImpl
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getWSBooleanE()
         * @generated
         */
        EClass WS_BOOLEAN_E = eINSTANCE.getWSBooleanE();

        /**
         * The meta object literal for the '<em><b>true</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute WS_BOOLEAN_E__TRUE = eINSTANCE.getWSBooleanE__true();

        /**
         * The meta object literal for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSWorkflowDeployEImpl <em>WS Workflow Deploy E</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.WSWorkflowDeployEImpl
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getWSWorkflowDeployE()
         * @generated
         */
        EClass WS_WORKFLOW_DEPLOY_E = eINSTANCE.getWSWorkflowDeployE();

        /**
         * The meta object literal for the '<em><b>Filename</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute WS_WORKFLOW_DEPLOY_E__FILENAME = eINSTANCE.getWSWorkflowDeployE_Filename();

        /**
         * The meta object literal for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSTransformerV2EImpl <em>WS Transformer V2E</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.WSTransformerV2EImpl
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getWSTransformerV2E()
         * @generated
         */
        EClass WS_TRANSFORMER_V2E = eINSTANCE.getWSTransformerV2E();

        /**
         * The meta object literal for the '<em><b>Process Steps</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference WS_TRANSFORMER_V2E__PROCESS_STEPS = eINSTANCE.getWSTransformerV2E_ProcessSteps();

        /**
         * The meta object literal for the '<em><b>With Admin Permissions</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute WS_TRANSFORMER_V2E__WITH_ADMIN_PERMISSIONS = eINSTANCE.getWSTransformerV2E_WithAdminPermissions();

        /**
         * The meta object literal for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSTransformerProcessStepEImpl <em>WS Transformer Process Step E</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.WSTransformerProcessStepEImpl
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getWSTransformerProcessStepE()
         * @generated
         */
        EClass WS_TRANSFORMER_PROCESS_STEP_E = eINSTANCE.getWSTransformerProcessStepE();

        /**
         * The meta object literal for the '<em><b>Plugin JNDI</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute WS_TRANSFORMER_PROCESS_STEP_E__PLUGIN_JNDI = eINSTANCE.getWSTransformerProcessStepE_PluginJNDI();

        /**
         * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute WS_TRANSFORMER_PROCESS_STEP_E__DESCRIPTION = eINSTANCE.getWSTransformerProcessStepE_Description();

        /**
         * The meta object literal for the '<em><b>Parameters</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute WS_TRANSFORMER_PROCESS_STEP_E__PARAMETERS = eINSTANCE.getWSTransformerProcessStepE_Parameters();

        /**
         * The meta object literal for the '<em><b>Disabled</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute WS_TRANSFORMER_PROCESS_STEP_E__DISABLED = eINSTANCE.getWSTransformerProcessStepE_Disabled();

        /**
         * The meta object literal for the '<em><b>Input Mappings</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference WS_TRANSFORMER_PROCESS_STEP_E__INPUT_MAPPINGS = eINSTANCE.getWSTransformerProcessStepE_InputMappings();

        /**
         * The meta object literal for the '<em><b>Output Mappings</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference WS_TRANSFORMER_PROCESS_STEP_E__OUTPUT_MAPPINGS = eINSTANCE.getWSTransformerProcessStepE_OutputMappings();

        /**
         * The meta object literal for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSTransformerVariablesMappingEImpl <em>WS Transformer Variables Mapping E</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.WSTransformerVariablesMappingEImpl
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getWSTransformerVariablesMappingE()
         * @generated
         */
        EClass WS_TRANSFORMER_VARIABLES_MAPPING_E = eINSTANCE.getWSTransformerVariablesMappingE();

        /**
         * The meta object literal for the '<em><b>Pipeline Variable</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute WS_TRANSFORMER_VARIABLES_MAPPING_E__PIPELINE_VARIABLE = eINSTANCE.getWSTransformerVariablesMappingE_PipelineVariable();

        /**
         * The meta object literal for the '<em><b>Plugin Variable</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute WS_TRANSFORMER_VARIABLES_MAPPING_E__PLUGIN_VARIABLE = eINSTANCE.getWSTransformerVariablesMappingE_PluginVariable();

        /**
         * The meta object literal for the '<em><b>Hard Coding</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference WS_TRANSFORMER_VARIABLES_MAPPING_E__HARD_CODING = eINSTANCE.getWSTransformerVariablesMappingE_HardCoding();

        /**
         * The meta object literal for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSTypedContentEImpl <em>WS Typed Content E</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.WSTypedContentEImpl
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getWSTypedContentE()
         * @generated
         */
        EClass WS_TYPED_CONTENT_E = eINSTANCE.getWSTypedContentE();

        /**
         * The meta object literal for the '<em><b>Url</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute WS_TYPED_CONTENT_E__URL = eINSTANCE.getWSTypedContentE_Url();

        /**
         * The meta object literal for the '<em><b>Content Type</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute WS_TYPED_CONTENT_E__CONTENT_TYPE = eINSTANCE.getWSTypedContentE_ContentType();

        /**
         * The meta object literal for the '<em><b>Ws Bytes</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference WS_TYPED_CONTENT_E__WS_BYTES = eINSTANCE.getWSTypedContentE_WsBytes();

        /**
         * The meta object literal for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSByteArrayEImpl <em>WS Byte Array E</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.WSByteArrayEImpl
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getWSByteArrayE()
         * @generated
         */
        EClass WS_BYTE_ARRAY_E = eINSTANCE.getWSByteArrayE();

        /**
         * The meta object literal for the '<em><b>Bytes</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute WS_BYTE_ARRAY_E__BYTES = eINSTANCE.getWSByteArrayE_Bytes();

        /**
         * The meta object literal for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSRoutingRuleEImpl <em>WS Routing Rule E</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.WSRoutingRuleEImpl
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getWSRoutingRuleE()
         * @generated
         */
        EClass WS_ROUTING_RULE_E = eINSTANCE.getWSRoutingRuleE();

        /**
         * The meta object literal for the '<em><b>Synchronous</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute WS_ROUTING_RULE_E__SYNCHRONOUS = eINSTANCE.getWSRoutingRuleE_Synchronous();

        /**
         * The meta object literal for the '<em><b>Concept</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute WS_ROUTING_RULE_E__CONCEPT = eINSTANCE.getWSRoutingRuleE_Concept();

        /**
         * The meta object literal for the '<em><b>Service JNDI</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute WS_ROUTING_RULE_E__SERVICE_JNDI = eINSTANCE.getWSRoutingRuleE_ServiceJNDI();

        /**
         * The meta object literal for the '<em><b>Parameters</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute WS_ROUTING_RULE_E__PARAMETERS = eINSTANCE.getWSRoutingRuleE_Parameters();

        /**
         * The meta object literal for the '<em><b>Condition</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute WS_ROUTING_RULE_E__CONDITION = eINSTANCE.getWSRoutingRuleE_Condition();

        /**
         * The meta object literal for the '<em><b>Deactive</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute WS_ROUTING_RULE_E__DEACTIVE = eINSTANCE.getWSRoutingRuleE_Deactive();

        /**
         * The meta object literal for the '<em><b>Execute Order</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute WS_ROUTING_RULE_E__EXECUTE_ORDER = eINSTANCE.getWSRoutingRuleE_ExecuteOrder();

        /**
         * The meta object literal for the '<em><b>Ws Routing Rule Expressions</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference WS_ROUTING_RULE_E__WS_ROUTING_RULE_EXPRESSIONS = eINSTANCE.getWSRoutingRuleE_WsRoutingRuleExpressions();

        /**
         * The meta object literal for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSRoutingRuleExpressionEImpl <em>WS Routing Rule Expression E</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.WSRoutingRuleExpressionEImpl
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getWSRoutingRuleExpressionE()
         * @generated
         */
        EClass WS_ROUTING_RULE_EXPRESSION_E = eINSTANCE.getWSRoutingRuleExpressionE();

        /**
         * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute WS_ROUTING_RULE_EXPRESSION_E__NAME = eINSTANCE.getWSRoutingRuleExpressionE_Name();

        /**
         * The meta object literal for the '<em><b>Xpath</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute WS_ROUTING_RULE_EXPRESSION_E__XPATH = eINSTANCE.getWSRoutingRuleExpressionE_Xpath();

        /**
         * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute WS_ROUTING_RULE_EXPRESSION_E__VALUE = eINSTANCE.getWSRoutingRuleExpressionE_Value();

        /**
         * The meta object literal for the '<em><b>Ws Operator</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference WS_ROUTING_RULE_EXPRESSION_E__WS_OPERATOR = eINSTANCE.getWSRoutingRuleExpressionE_WsOperator();

        /**
         * The meta object literal for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSRoutingRuleOperatorEImpl <em>WS Routing Rule Operator E</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.WSRoutingRuleOperatorEImpl
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getWSRoutingRuleOperatorE()
         * @generated
         */
        EClass WS_ROUTING_RULE_OPERATOR_E = eINSTANCE.getWSRoutingRuleOperatorE();

        /**
         * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute WS_ROUTING_RULE_OPERATOR_E__VALUE = eINSTANCE.getWSRoutingRuleOperatorE_Value();

        /**
         * The meta object literal for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSJobModelEImpl <em>WS Job Model E</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.WSJobModelEImpl
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getWSJobModelE()
         * @generated
         */
        EClass WS_JOB_MODEL_E = eINSTANCE.getWSJobModelE();

        /**
         * The meta object literal for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSEventManagerEImpl <em>WS Event Manager E</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.WSEventManagerEImpl
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getWSEventManagerE()
         * @generated
         */
        EClass WS_EVENT_MANAGER_E = eINSTANCE.getWSEventManagerE();

        /**
         * The meta object literal for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSServiceConfigurationEImpl <em>WS Service Configuration E</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.WSServiceConfigurationEImpl
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getWSServiceConfigurationE()
         * @generated
         */
        EClass WS_SERVICE_CONFIGURATION_E = eINSTANCE.getWSServiceConfigurationE();

        /**
         * The meta object literal for the '<em><b>Service Put Configurations</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference WS_SERVICE_CONFIGURATION_E__SERVICE_PUT_CONFIGURATIONS = eINSTANCE.getWSServiceConfigurationE_ServicePutConfigurations();

        /**
         * The meta object literal for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSServicePutConfigurationEImpl <em>WS Service Put Configuration E</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.WSServicePutConfigurationEImpl
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getWSServicePutConfigurationE()
         * @generated
         */
        EClass WS_SERVICE_PUT_CONFIGURATION_E = eINSTANCE.getWSServicePutConfigurationE();

        /**
         * The meta object literal for the '<em><b>Jndi Name</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute WS_SERVICE_PUT_CONFIGURATION_E__JNDI_NAME = eINSTANCE.getWSServicePutConfigurationE_JndiName();

        /**
         * The meta object literal for the '<em><b>Configuration</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute WS_SERVICE_PUT_CONFIGURATION_E__CONFIGURATION = eINSTANCE.getWSServicePutConfigurationE_Configuration();

        /**
         * The meta object literal for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSWorkflowEImpl <em>WS Workflow E</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.WSWorkflowEImpl
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getWSWorkflowE()
         * @generated
         */
        EClass WS_WORKFLOW_E = eINSTANCE.getWSWorkflowE();

        /**
         * The meta object literal for the '<em><b>File Content</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute WS_WORKFLOW_E__FILE_CONTENT = eINSTANCE.getWSWorkflowE_FileContent();

        /**
         * The meta object literal for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSResourceEImpl <em>WS Resource E</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.WSResourceEImpl
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getWSResourceE()
         * @generated
         */
        EClass WS_RESOURCE_E = eINSTANCE.getWSResourceE();

        /**
         * The meta object literal for the '<em><b>File Extension</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute WS_RESOURCE_E__FILE_EXTENSION = eINSTANCE.getWSResourceE_FileExtension();

        /**
         * The meta object literal for the '<em><b>File Content</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute WS_RESOURCE_E__FILE_CONTENT = eINSTANCE.getWSResourceE_FileContent();

        /**
         * The meta object literal for the '<em><b>Image Catalog</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute WS_RESOURCE_E__IMAGE_CATALOG = eINSTANCE.getWSResourceE_ImageCatalog();

        /**
         * The meta object literal for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSCustomFormEImpl <em>WS Custom Form E</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.WSCustomFormEImpl
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getWSCustomFormE()
         * @generated
         */
        EClass WS_CUSTOM_FORM_E = eINSTANCE.getWSCustomFormE();

        /**
         * The meta object literal for the '<em><b>Filename</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute WS_CUSTOM_FORM_E__FILENAME = eINSTANCE.getWSCustomFormE_Filename();

        /**
         * The meta object literal for the '<em><b>Datamodel</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute WS_CUSTOM_FORM_E__DATAMODEL = eINSTANCE.getWSCustomFormE_Datamodel();

        /**
         * The meta object literal for the '<em><b>Entity</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute WS_CUSTOM_FORM_E__ENTITY = eINSTANCE.getWSCustomFormE_Entity();

        /**
         * The meta object literal for the '<em><b>Xml</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute WS_CUSTOM_FORM_E__XML = eINSTANCE.getWSCustomFormE_Xml();

        /**
         * The meta object literal for the '<em><b>Role</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute WS_CUSTOM_FORM_E__ROLE = eINSTANCE.getWSCustomFormE_Role();

        /**
         * The meta object literal for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSMatchRuleEImpl <em>WS Match Rule E</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.WSMatchRuleEImpl
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getWSMatchRuleE()
         * @generated
         */
        EClass WS_MATCH_RULE_E = eINSTANCE.getWSMatchRuleE();

        /**
         * The meta object literal for the '<em><b>Configuration Xml Content</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute WS_MATCH_RULE_E__CONFIGURATION_XML_CONTENT = eINSTANCE.getWSMatchRuleE_ConfigurationXmlContent();

        /**
         * The meta object literal for the '<em><b>PK</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference WS_MATCH_RULE_E__PK = eINSTANCE.getWSMatchRuleE_PK();

        /**
         * The meta object literal for the '{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSMatchRulePKEImpl <em>WS Match Rule PKE</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.WSMatchRulePKEImpl
         * @see org.talend.mdm.repository.model.mdmserverobject.impl.MdmserverobjectPackageImpl#getWSMatchRulePKE()
         * @generated
         */
        EClass WS_MATCH_RULE_PKE = eINSTANCE.getWSMatchRulePKE();

        /**
         * The meta object literal for the '<em><b>Pk</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute WS_MATCH_RULE_PKE__PK = eINSTANCE.getWSMatchRulePKE_Pk();

    }

} //MdmserverobjectPackage
