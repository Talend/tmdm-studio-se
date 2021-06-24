/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.mdm.repository.model.mdmproperties.util;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.ecore.EObject;

import org.talend.core.model.properties.FolderItem;
import org.talend.core.model.properties.Item;

import org.talend.mdm.repository.model.mdmproperties.*;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see org.talend.mdm.repository.model.mdmproperties.MdmpropertiesPackage
 * @generated
 */
public class MdmpropertiesAdapterFactory extends AdapterFactoryImpl {
    /**
     * The cached model package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected static MdmpropertiesPackage modelPackage;

    /**
     * Creates an instance of the adapter factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public MdmpropertiesAdapterFactory() {
        if (modelPackage == null) {
            modelPackage = MdmpropertiesPackage.eINSTANCE;
        }
    }

    /**
     * Returns whether this factory is applicable for the type of the object.
     * <!-- begin-user-doc -->
     * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
     * <!-- end-user-doc -->
     * @return whether this factory is applicable for the type of the object.
     * @generated
     */
    @Override
    public boolean isFactoryForType(Object object) {
        if (object == modelPackage) {
            return true;
        }
        if (object instanceof EObject) {
            return ((EObject)object).eClass().getEPackage() == modelPackage;
        }
        return false;
    }

    /**
     * The switch that delegates to the <code>createXXX</code> methods.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected MdmpropertiesSwitch<Adapter> modelSwitch =
        new MdmpropertiesSwitch<Adapter>() {
            @Override
            public Adapter caseMDMItem(MDMItem object) {
                return createMDMItemAdapter();
            }
            @Override
            public Adapter caseMDMServerDefItem(MDMServerDefItem object) {
                return createMDMServerDefItemAdapter();
            }
            @Override
            public Adapter caseMDMServerObjectItem(MDMServerObjectItem object) {
                return createMDMServerObjectItemAdapter();
            }
            @Override
            public Adapter caseWSMenuItem(WSMenuItem object) {
                return createWSMenuItemAdapter();
            }
            @Override
            public Adapter caseWSRoleItem(WSRoleItem object) {
                return createWSRoleItemAdapter();
            }
            @Override
            public Adapter caseContainerItem(ContainerItem object) {
                return createContainerItemAdapter();
            }
            @Override
            public Adapter caseWSDataModelItem(WSDataModelItem object) {
                return createWSDataModelItemAdapter();
            }
            @Override
            public Adapter caseWSDataClusterItem(WSDataClusterItem object) {
                return createWSDataClusterItemAdapter();
            }
            @Override
            public Adapter caseWSStoredProcedureItem(WSStoredProcedureItem object) {
                return createWSStoredProcedureItemAdapter();
            }
            @Override
            public Adapter caseWSViewItem(WSViewItem object) {
                return createWSViewItemAdapter();
            }
            @Override
            public Adapter caseWSTransformerV2Item(WSTransformerV2Item object) {
                return createWSTransformerV2ItemAdapter();
            }
            @Override
            public Adapter caseWSRoutingRuleItem(WSRoutingRuleItem object) {
                return createWSRoutingRuleItemAdapter();
            }
            @Override
            public Adapter caseWSJobModelItem(WSJobModelItem object) {
                return createWSJobModelItemAdapter();
            }
            @Override
            public Adapter caseWSEventManagerItem(WSEventManagerItem object) {
                return createWSEventManagerItemAdapter();
            }
            @Override
            public Adapter caseWSServiceConfigurationItem(WSServiceConfigurationItem object) {
                return createWSServiceConfigurationItemAdapter();
            }
            @Override
            public Adapter caseWSResourceItem(WSResourceItem object) {
                return createWSResourceItemAdapter();
            }
            @Override
            public Adapter caseWSCustomFormItem(WSCustomFormItem object) {
                return createWSCustomFormItemAdapter();
            }
            @Override
            public Adapter caseWorkspaceRootItem(WorkspaceRootItem object) {
                return createWorkspaceRootItemAdapter();
            }
            @Override
            public Adapter caseWSMatchRuleItem(WSMatchRuleItem object) {
                return createWSMatchRuleItemAdapter();
            }
            @Override
            public Adapter caseItem(Item object) {
                return createItemAdapter();
            }
            @Override
            public Adapter caseFolderItem(FolderItem object) {
                return createFolderItemAdapter();
            }
            @Override
            public Adapter defaultCase(EObject object) {
                return createEObjectAdapter();
            }
        };

    /**
     * Creates an adapter for the <code>target</code>.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param target the object to adapt.
     * @return the adapter for the <code>target</code>.
     * @generated
     */
    @Override
    public Adapter createAdapter(Notifier target) {
        return modelSwitch.doSwitch((EObject)target);
    }


    /**
     * Creates a new adapter for an object of class '{@link org.talend.mdm.repository.model.mdmproperties.MDMItem <em>MDM Item</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.talend.mdm.repository.model.mdmproperties.MDMItem
     * @generated
     */
    public Adapter createMDMItemAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.talend.mdm.repository.model.mdmproperties.MDMServerDefItem <em>MDM Server Def Item</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.talend.mdm.repository.model.mdmproperties.MDMServerDefItem
     * @generated
     */
    public Adapter createMDMServerDefItemAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.talend.mdm.repository.model.mdmproperties.MDMServerObjectItem <em>MDM Server Object Item</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.talend.mdm.repository.model.mdmproperties.MDMServerObjectItem
     * @generated
     */
    public Adapter createMDMServerObjectItemAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.talend.mdm.repository.model.mdmproperties.WSMenuItem <em>WS Menu Item</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.talend.mdm.repository.model.mdmproperties.WSMenuItem
     * @generated
     */
    public Adapter createWSMenuItemAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.talend.mdm.repository.model.mdmproperties.WSRoleItem <em>WS Role Item</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.talend.mdm.repository.model.mdmproperties.WSRoleItem
     * @generated
     */
    public Adapter createWSRoleItemAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.talend.mdm.repository.model.mdmproperties.ContainerItem <em>Container Item</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.talend.mdm.repository.model.mdmproperties.ContainerItem
     * @generated
     */
    public Adapter createContainerItemAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.talend.mdm.repository.model.mdmproperties.WSDataModelItem <em>WS Data Model Item</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.talend.mdm.repository.model.mdmproperties.WSDataModelItem
     * @generated
     */
    public Adapter createWSDataModelItemAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.talend.mdm.repository.model.mdmproperties.WSDataClusterItem <em>WS Data Cluster Item</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.talend.mdm.repository.model.mdmproperties.WSDataClusterItem
     * @generated
     */
    public Adapter createWSDataClusterItemAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.talend.mdm.repository.model.mdmproperties.WSStoredProcedureItem <em>WS Stored Procedure Item</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.talend.mdm.repository.model.mdmproperties.WSStoredProcedureItem
     * @generated
     */
    public Adapter createWSStoredProcedureItemAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.talend.mdm.repository.model.mdmproperties.WSViewItem <em>WS View Item</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.talend.mdm.repository.model.mdmproperties.WSViewItem
     * @generated
     */
    public Adapter createWSViewItemAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.talend.mdm.repository.model.mdmproperties.WSTransformerV2Item <em>WS Transformer V2 Item</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.talend.mdm.repository.model.mdmproperties.WSTransformerV2Item
     * @generated
     */
    public Adapter createWSTransformerV2ItemAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.talend.mdm.repository.model.mdmproperties.WSRoutingRuleItem <em>WS Routing Rule Item</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.talend.mdm.repository.model.mdmproperties.WSRoutingRuleItem
     * @generated
     */
    public Adapter createWSRoutingRuleItemAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.talend.mdm.repository.model.mdmproperties.WSJobModelItem <em>WS Job Model Item</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.talend.mdm.repository.model.mdmproperties.WSJobModelItem
     * @generated
     */
    public Adapter createWSJobModelItemAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.talend.mdm.repository.model.mdmproperties.WSEventManagerItem <em>WS Event Manager Item</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.talend.mdm.repository.model.mdmproperties.WSEventManagerItem
     * @generated
     */
    public Adapter createWSEventManagerItemAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.talend.mdm.repository.model.mdmproperties.WSServiceConfigurationItem <em>WS Service Configuration Item</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.talend.mdm.repository.model.mdmproperties.WSServiceConfigurationItem
     * @generated
     */
    public Adapter createWSServiceConfigurationItemAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.talend.mdm.repository.model.mdmproperties.WSResourceItem <em>WS Resource Item</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.talend.mdm.repository.model.mdmproperties.WSResourceItem
     * @generated
     */
    public Adapter createWSResourceItemAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.talend.mdm.repository.model.mdmproperties.WSCustomFormItem <em>WS Custom Form Item</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.talend.mdm.repository.model.mdmproperties.WSCustomFormItem
     * @generated
     */
    public Adapter createWSCustomFormItemAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.talend.mdm.repository.model.mdmproperties.WorkspaceRootItem <em>Workspace Root Item</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.talend.mdm.repository.model.mdmproperties.WorkspaceRootItem
     * @generated
     */
    public Adapter createWorkspaceRootItemAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.talend.mdm.repository.model.mdmproperties.WSMatchRuleItem <em>WS Match Rule Item</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.talend.mdm.repository.model.mdmproperties.WSMatchRuleItem
     * @generated
     */
    public Adapter createWSMatchRuleItemAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.talend.core.model.properties.Item <em>Item</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.talend.core.model.properties.Item
     * @generated
     */
    public Adapter createItemAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.talend.core.model.properties.FolderItem <em>Folder Item</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.talend.core.model.properties.FolderItem
     * @generated
     */
    public Adapter createFolderItemAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for the default case.
     * <!-- begin-user-doc -->
     * This default implementation returns null.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @generated
     */
    public Adapter createEObjectAdapter() {
        return null;
    }

} //MdmpropertiesAdapterFactory
