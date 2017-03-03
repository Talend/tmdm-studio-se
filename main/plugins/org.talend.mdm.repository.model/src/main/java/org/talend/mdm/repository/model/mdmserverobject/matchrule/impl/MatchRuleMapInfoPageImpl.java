/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.mdm.repository.model.mdmserverobject.matchrule.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EMap;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.EcoreEMap;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;

import org.talend.mdm.repository.model.mdmserverobject.matchrule.EntityMapInfo;
import org.talend.mdm.repository.model.mdmserverobject.matchrule.MatchRuleMapInfoPage;
import org.talend.mdm.repository.model.mdmserverobject.matchrule.MatchRulePackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Map Info Page</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.talend.mdm.repository.model.mdmserverobject.matchrule.impl.MatchRuleMapInfoPageImpl#getMatchKeyMap <em>Match Key Map</em>}</li>
 *   <li>{@link org.talend.mdm.repository.model.mdmserverobject.matchrule.impl.MatchRuleMapInfoPageImpl#getParent <em>Parent</em>}</li>
 *   <li>{@link org.talend.mdm.repository.model.mdmserverobject.matchrule.impl.MatchRuleMapInfoPageImpl#getName <em>Name</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class MatchRuleMapInfoPageImpl extends EObjectImpl implements MatchRuleMapInfoPage {
    /**
     * The cached value of the '{@link #getMatchKeyMap() <em>Match Key Map</em>}' map.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getMatchKeyMap()
     * @generated
     * @ordered
     */
    protected EMap<String, String> matchKeyMap;

    /**
     * The default value of the '{@link #getName() <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getName()
     * @generated
     * @ordered
     */
    protected static final String NAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getName()
     * @generated
     * @ordered
     */
    protected String name = NAME_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected MatchRuleMapInfoPageImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return MatchRulePackage.Literals.MATCH_RULE_MAP_INFO_PAGE;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EMap<String, String> getMatchKeyMap() {
        if (matchKeyMap == null) {
            matchKeyMap = new EcoreEMap<String,String>(MatchRulePackage.Literals.KEY_XPATH_MAP, KeyXPathMapImpl.class, this, MatchRulePackage.MATCH_RULE_MAP_INFO_PAGE__MATCH_KEY_MAP);
        }
        return matchKeyMap;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EntityMapInfo getParent() {
        if (eContainerFeatureID() != MatchRulePackage.MATCH_RULE_MAP_INFO_PAGE__PARENT) return null;
        return (EntityMapInfo)eContainer();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetParent(EntityMapInfo newParent, NotificationChain msgs) {
        msgs = eBasicSetContainer((InternalEObject)newParent, MatchRulePackage.MATCH_RULE_MAP_INFO_PAGE__PARENT, msgs);
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setParent(EntityMapInfo newParent) {
        if (newParent != eInternalContainer() || (eContainerFeatureID() != MatchRulePackage.MATCH_RULE_MAP_INFO_PAGE__PARENT && newParent != null)) {
            if (EcoreUtil.isAncestor(this, newParent))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
            NotificationChain msgs = null;
            if (eInternalContainer() != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newParent != null)
                msgs = ((InternalEObject)newParent).eInverseAdd(this, MatchRulePackage.ENTITY_MAP_INFO__MATCH_RULE_MAP_INFO_PAGES, EntityMapInfo.class, msgs);
            msgs = basicSetParent(newParent, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, MatchRulePackage.MATCH_RULE_MAP_INFO_PAGE__PARENT, newParent, newParent));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getName() {
        return name;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setName(String newName) {
        String oldName = name;
        name = newName;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, MatchRulePackage.MATCH_RULE_MAP_INFO_PAGE__NAME, oldName, name));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case MatchRulePackage.MATCH_RULE_MAP_INFO_PAGE__PARENT:
                if (eInternalContainer() != null)
                    msgs = eBasicRemoveFromContainer(msgs);
                return basicSetParent((EntityMapInfo)otherEnd, msgs);
        }
        return super.eInverseAdd(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case MatchRulePackage.MATCH_RULE_MAP_INFO_PAGE__MATCH_KEY_MAP:
                return ((InternalEList<?>)getMatchKeyMap()).basicRemove(otherEnd, msgs);
            case MatchRulePackage.MATCH_RULE_MAP_INFO_PAGE__PARENT:
                return basicSetParent(null, msgs);
        }
        return super.eInverseRemove(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs) {
        switch (eContainerFeatureID()) {
            case MatchRulePackage.MATCH_RULE_MAP_INFO_PAGE__PARENT:
                return eInternalContainer().eInverseRemove(this, MatchRulePackage.ENTITY_MAP_INFO__MATCH_RULE_MAP_INFO_PAGES, EntityMapInfo.class, msgs);
        }
        return super.eBasicRemoveFromContainerFeature(msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case MatchRulePackage.MATCH_RULE_MAP_INFO_PAGE__MATCH_KEY_MAP:
                if (coreType) return getMatchKeyMap();
                else return getMatchKeyMap().map();
            case MatchRulePackage.MATCH_RULE_MAP_INFO_PAGE__PARENT:
                return getParent();
            case MatchRulePackage.MATCH_RULE_MAP_INFO_PAGE__NAME:
                return getName();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
            case MatchRulePackage.MATCH_RULE_MAP_INFO_PAGE__MATCH_KEY_MAP:
                ((EStructuralFeature.Setting)getMatchKeyMap()).set(newValue);
                return;
            case MatchRulePackage.MATCH_RULE_MAP_INFO_PAGE__PARENT:
                setParent((EntityMapInfo)newValue);
                return;
            case MatchRulePackage.MATCH_RULE_MAP_INFO_PAGE__NAME:
                setName((String)newValue);
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
            case MatchRulePackage.MATCH_RULE_MAP_INFO_PAGE__MATCH_KEY_MAP:
                getMatchKeyMap().clear();
                return;
            case MatchRulePackage.MATCH_RULE_MAP_INFO_PAGE__PARENT:
                setParent((EntityMapInfo)null);
                return;
            case MatchRulePackage.MATCH_RULE_MAP_INFO_PAGE__NAME:
                setName(NAME_EDEFAULT);
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
            case MatchRulePackage.MATCH_RULE_MAP_INFO_PAGE__MATCH_KEY_MAP:
                return matchKeyMap != null && !matchKeyMap.isEmpty();
            case MatchRulePackage.MATCH_RULE_MAP_INFO_PAGE__PARENT:
                return getParent() != null;
            case MatchRulePackage.MATCH_RULE_MAP_INFO_PAGE__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
        }
        return super.eIsSet(featureID);
    }

} //MatchRuleMapInfoPageImpl
