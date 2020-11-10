/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.mdm.repository.model.mdmserverobject.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.talend.mdm.repository.model.mdmserverobject.MdmserverobjectPackage;
import org.talend.mdm.repository.model.mdmserverobject.WSTransformerProcessStepE;
import org.talend.mdm.repository.model.mdmserverobject.WSTransformerV2E;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>WS Transformer V2E</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSTransformerV2EImpl#getProcessSteps <em>Process Steps</em>}</li>
 *   <li>{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSTransformerV2EImpl#isWithAdminPermissions <em>With Admin Permissions</em>}</li>
 * </ul>
 *
 * @generated
 */
public class WSTransformerV2EImpl extends MDMServerObjectImpl implements WSTransformerV2E {
    /**
     * The cached value of the '{@link #getProcessSteps() <em>Process Steps</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getProcessSteps()
     * @generated
     * @ordered
     */
    protected EList<WSTransformerProcessStepE> processSteps;

    /**
     * The default value of the '{@link #isWithAdminPermissions() <em>With Admin Permissions</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isWithAdminPermissions()
     * @generated
     * @ordered
     */
    protected static final boolean WITH_ADMIN_PERMISSIONS_EDEFAULT = false;
    /**
     * The cached value of the '{@link #isWithAdminPermissions() <em>With Admin Permissions</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isWithAdminPermissions()
     * @generated
     * @ordered
     */
    protected boolean withAdminPermissions = WITH_ADMIN_PERMISSIONS_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected WSTransformerV2EImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return MdmserverobjectPackage.Literals.WS_TRANSFORMER_V2E;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EList<WSTransformerProcessStepE> getProcessSteps() {
        if (processSteps == null) {
            processSteps = new EObjectContainmentEList<WSTransformerProcessStepE>(WSTransformerProcessStepE.class, this, MdmserverobjectPackage.WS_TRANSFORMER_V2E__PROCESS_STEPS);
        }
        return processSteps;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public boolean isWithAdminPermissions() {
        return withAdminPermissions;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setWithAdminPermissions(boolean newWithAdminPermissions) {
        boolean oldWithAdminPermissions = withAdminPermissions;
        withAdminPermissions = newWithAdminPermissions;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, MdmserverobjectPackage.WS_TRANSFORMER_V2E__WITH_ADMIN_PERMISSIONS, oldWithAdminPermissions, withAdminPermissions));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case MdmserverobjectPackage.WS_TRANSFORMER_V2E__PROCESS_STEPS:
                return ((InternalEList<?>)getProcessSteps()).basicRemove(otherEnd, msgs);
        }
        return super.eInverseRemove(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case MdmserverobjectPackage.WS_TRANSFORMER_V2E__PROCESS_STEPS:
                return getProcessSteps();
            case MdmserverobjectPackage.WS_TRANSFORMER_V2E__WITH_ADMIN_PERMISSIONS:
                return isWithAdminPermissions();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
            case MdmserverobjectPackage.WS_TRANSFORMER_V2E__PROCESS_STEPS:
                getProcessSteps().clear();
                getProcessSteps().addAll((Collection<? extends WSTransformerProcessStepE>)newValue);
                return;
            case MdmserverobjectPackage.WS_TRANSFORMER_V2E__WITH_ADMIN_PERMISSIONS:
                setWithAdminPermissions((Boolean)newValue);
                return;
        }
        super.eSet(featureID, newValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eUnset(int featureID) {
        switch (featureID) {
            case MdmserverobjectPackage.WS_TRANSFORMER_V2E__PROCESS_STEPS:
                getProcessSteps().clear();
                return;
            case MdmserverobjectPackage.WS_TRANSFORMER_V2E__WITH_ADMIN_PERMISSIONS:
                setWithAdminPermissions(WITH_ADMIN_PERMISSIONS_EDEFAULT);
                return;
        }
        super.eUnset(featureID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public boolean eIsSet(int featureID) {
        switch (featureID) {
            case MdmserverobjectPackage.WS_TRANSFORMER_V2E__PROCESS_STEPS:
                return processSteps != null && !processSteps.isEmpty();
            case MdmserverobjectPackage.WS_TRANSFORMER_V2E__WITH_ADMIN_PERMISSIONS:
                return withAdminPermissions != WITH_ADMIN_PERMISSIONS_EDEFAULT;
        }
        return super.eIsSet(featureID);
    }

    @Override
    public int getType() {
        return 16;
    }
} // WsTransformerV2EImpl
