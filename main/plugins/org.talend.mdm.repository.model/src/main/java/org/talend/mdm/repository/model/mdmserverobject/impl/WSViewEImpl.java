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

import org.eclipse.emf.ecore.util.EDataTypeUniqueEList;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.talend.mdm.repository.model.mdmserverobject.MdmserverobjectPackage;
import org.talend.mdm.repository.model.mdmserverobject.WSBooleanE;
import org.talend.mdm.repository.model.mdmserverobject.WSViewE;
import org.talend.mdm.repository.model.mdmserverobject.WSWhereConditionE;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>WS View E</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSViewEImpl#getSearchableBusinessElements <em>Searchable Business Elements</em>}</li>
 *   <li>{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSViewEImpl#getViewableBusinessElements <em>Viewable Business Elements</em>}</li>
 *   <li>{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSViewEImpl#isTransformerActive <em>Transformer Active</em>}</li>
 *   <li>{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSViewEImpl#getWhereConditions <em>Where Conditions</em>}</li>
 *   <li>{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSViewEImpl#getIsTransformerActive <em>Is Transformer Active</em>}</li>
 *   <li>{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSViewEImpl#getTransformerPK <em>Transformer PK</em>}</li>
 *   <li>{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSViewEImpl#getSortField <em>Sort Field</em>}</li>
 *   <li>{@link org.talend.mdm.repository.model.mdmserverobject.impl.WSViewEImpl#getIsAsc <em>Is Asc</em>}</li>
 * </ul>
 *
 * @generated
 */
public class WSViewEImpl extends MDMServerObjectImpl implements WSViewE {
    /**
     * The cached value of the '{@link #getSearchableBusinessElements() <em>Searchable Business Elements</em>}' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getSearchableBusinessElements()
     * @generated
     * @ordered
     */
    protected EList<String> searchableBusinessElements;

    /**
     * The cached value of the '{@link #getViewableBusinessElements() <em>Viewable Business Elements</em>}' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getViewableBusinessElements()
     * @generated
     * @ordered
     */
    protected EList<String> viewableBusinessElements;

    /**
     * The default value of the '{@link #isTransformerActive() <em>Transformer Active</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isTransformerActive()
     * @generated
     * @ordered
     */
    protected static final boolean TRANSFORMER_ACTIVE_EDEFAULT = false;

    /**
     * The cached value of the '{@link #isTransformerActive() <em>Transformer Active</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isTransformerActive()
     * @generated
     * @ordered
     */
    protected boolean transformerActive = TRANSFORMER_ACTIVE_EDEFAULT;

    /**
     * The cached value of the '{@link #getWhereConditions() <em>Where Conditions</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getWhereConditions()
     * @generated
     * @ordered
     */
    protected EList<WSWhereConditionE> whereConditions;

    /**
     * The cached value of the '{@link #getIsTransformerActive() <em>Is Transformer Active</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getIsTransformerActive()
     * @generated
     * @ordered
     */
    protected WSBooleanE isTransformerActive;

    /**
     * The default value of the '{@link #getTransformerPK() <em>Transformer PK</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTransformerPK()
     * @generated
     * @ordered
     */
    protected static final String TRANSFORMER_PK_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getTransformerPK() <em>Transformer PK</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTransformerPK()
     * @generated
     * @ordered
     */
    protected String transformerPK = TRANSFORMER_PK_EDEFAULT;

    /**
     * The default value of the '{@link #getSortField() <em>Sort Field</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getSortField()
     * @generated
     * @ordered
     */
    protected static final String SORT_FIELD_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getSortField() <em>Sort Field</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getSortField()
     * @generated
     * @ordered
     */
    protected String sortField = SORT_FIELD_EDEFAULT;

    /**
     * The cached value of the '{@link #getIsAsc() <em>Is Asc</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getIsAsc()
     * @generated
     * @ordered
     */
    protected WSBooleanE isAsc;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected WSViewEImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return MdmserverobjectPackage.Literals.WS_VIEW_E;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList<String> getSearchableBusinessElements() {
        if (searchableBusinessElements == null) {
            searchableBusinessElements = new EDataTypeUniqueEList<String>(String.class, this, MdmserverobjectPackage.WS_VIEW_E__SEARCHABLE_BUSINESS_ELEMENTS);
        }
        return searchableBusinessElements;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList<String> getViewableBusinessElements() {
        if (viewableBusinessElements == null) {
            viewableBusinessElements = new EDataTypeUniqueEList<String>(String.class, this, MdmserverobjectPackage.WS_VIEW_E__VIEWABLE_BUSINESS_ELEMENTS);
        }
        return viewableBusinessElements;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isTransformerActive() {
        return transformerActive;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setTransformerActive(boolean newTransformerActive) {
        boolean oldTransformerActive = transformerActive;
        transformerActive = newTransformerActive;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, MdmserverobjectPackage.WS_VIEW_E__TRANSFORMER_ACTIVE, oldTransformerActive, transformerActive));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList<WSWhereConditionE> getWhereConditions() {
        if (whereConditions == null) {
            whereConditions = new EObjectContainmentEList<WSWhereConditionE>(WSWhereConditionE.class, this, MdmserverobjectPackage.WS_VIEW_E__WHERE_CONDITIONS);
        }
        return whereConditions;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public WSBooleanE getIsTransformerActive() {
        return isTransformerActive;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetIsTransformerActive(WSBooleanE newIsTransformerActive, NotificationChain msgs) {
        WSBooleanE oldIsTransformerActive = isTransformerActive;
        isTransformerActive = newIsTransformerActive;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, MdmserverobjectPackage.WS_VIEW_E__IS_TRANSFORMER_ACTIVE, oldIsTransformerActive, newIsTransformerActive);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setIsTransformerActive(WSBooleanE newIsTransformerActive) {
        if (newIsTransformerActive != isTransformerActive) {
            NotificationChain msgs = null;
            if (isTransformerActive != null)
                msgs = ((InternalEObject)isTransformerActive).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - MdmserverobjectPackage.WS_VIEW_E__IS_TRANSFORMER_ACTIVE, null, msgs);
            if (newIsTransformerActive != null)
                msgs = ((InternalEObject)newIsTransformerActive).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - MdmserverobjectPackage.WS_VIEW_E__IS_TRANSFORMER_ACTIVE, null, msgs);
            msgs = basicSetIsTransformerActive(newIsTransformerActive, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, MdmserverobjectPackage.WS_VIEW_E__IS_TRANSFORMER_ACTIVE, newIsTransformerActive, newIsTransformerActive));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getTransformerPK() {
        return transformerPK;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setTransformerPK(String newTransformerPK) {
        String oldTransformerPK = transformerPK;
        transformerPK = newTransformerPK;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, MdmserverobjectPackage.WS_VIEW_E__TRANSFORMER_PK, oldTransformerPK, transformerPK));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getSortField() {
        return sortField;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setSortField(String newSortField) {
        String oldSortField = sortField;
        sortField = newSortField;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, MdmserverobjectPackage.WS_VIEW_E__SORT_FIELD, oldSortField, sortField));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public WSBooleanE getIsAsc() {
        return isAsc;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetIsAsc(WSBooleanE newIsAsc, NotificationChain msgs) {
        WSBooleanE oldIsAsc = isAsc;
        isAsc = newIsAsc;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, MdmserverobjectPackage.WS_VIEW_E__IS_ASC, oldIsAsc, newIsAsc);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setIsAsc(WSBooleanE newIsAsc) {
        if (newIsAsc != isAsc) {
            NotificationChain msgs = null;
            if (isAsc != null)
                msgs = ((InternalEObject)isAsc).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - MdmserverobjectPackage.WS_VIEW_E__IS_ASC, null, msgs);
            if (newIsAsc != null)
                msgs = ((InternalEObject)newIsAsc).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - MdmserverobjectPackage.WS_VIEW_E__IS_ASC, null, msgs);
            msgs = basicSetIsAsc(newIsAsc, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, MdmserverobjectPackage.WS_VIEW_E__IS_ASC, newIsAsc, newIsAsc));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case MdmserverobjectPackage.WS_VIEW_E__WHERE_CONDITIONS:
                return ((InternalEList<?>)getWhereConditions()).basicRemove(otherEnd, msgs);
            case MdmserverobjectPackage.WS_VIEW_E__IS_TRANSFORMER_ACTIVE:
                return basicSetIsTransformerActive(null, msgs);
            case MdmserverobjectPackage.WS_VIEW_E__IS_ASC:
                return basicSetIsAsc(null, msgs);
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
            case MdmserverobjectPackage.WS_VIEW_E__SEARCHABLE_BUSINESS_ELEMENTS:
                return getSearchableBusinessElements();
            case MdmserverobjectPackage.WS_VIEW_E__VIEWABLE_BUSINESS_ELEMENTS:
                return getViewableBusinessElements();
            case MdmserverobjectPackage.WS_VIEW_E__TRANSFORMER_ACTIVE:
                return isTransformerActive();
            case MdmserverobjectPackage.WS_VIEW_E__WHERE_CONDITIONS:
                return getWhereConditions();
            case MdmserverobjectPackage.WS_VIEW_E__IS_TRANSFORMER_ACTIVE:
                return getIsTransformerActive();
            case MdmserverobjectPackage.WS_VIEW_E__TRANSFORMER_PK:
                return getTransformerPK();
            case MdmserverobjectPackage.WS_VIEW_E__SORT_FIELD:
                return getSortField();
            case MdmserverobjectPackage.WS_VIEW_E__IS_ASC:
                return getIsAsc();
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
            case MdmserverobjectPackage.WS_VIEW_E__SEARCHABLE_BUSINESS_ELEMENTS:
                getSearchableBusinessElements().clear();
                getSearchableBusinessElements().addAll((Collection<? extends String>)newValue);
                return;
            case MdmserverobjectPackage.WS_VIEW_E__VIEWABLE_BUSINESS_ELEMENTS:
                getViewableBusinessElements().clear();
                getViewableBusinessElements().addAll((Collection<? extends String>)newValue);
                return;
            case MdmserverobjectPackage.WS_VIEW_E__TRANSFORMER_ACTIVE:
                setTransformerActive((Boolean)newValue);
                return;
            case MdmserverobjectPackage.WS_VIEW_E__WHERE_CONDITIONS:
                getWhereConditions().clear();
                getWhereConditions().addAll((Collection<? extends WSWhereConditionE>)newValue);
                return;
            case MdmserverobjectPackage.WS_VIEW_E__IS_TRANSFORMER_ACTIVE:
                setIsTransformerActive((WSBooleanE)newValue);
                return;
            case MdmserverobjectPackage.WS_VIEW_E__TRANSFORMER_PK:
                setTransformerPK((String)newValue);
                return;
            case MdmserverobjectPackage.WS_VIEW_E__SORT_FIELD:
                setSortField((String)newValue);
                return;
            case MdmserverobjectPackage.WS_VIEW_E__IS_ASC:
                setIsAsc((WSBooleanE)newValue);
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
            case MdmserverobjectPackage.WS_VIEW_E__SEARCHABLE_BUSINESS_ELEMENTS:
                getSearchableBusinessElements().clear();
                return;
            case MdmserverobjectPackage.WS_VIEW_E__VIEWABLE_BUSINESS_ELEMENTS:
                getViewableBusinessElements().clear();
                return;
            case MdmserverobjectPackage.WS_VIEW_E__TRANSFORMER_ACTIVE:
                setTransformerActive(TRANSFORMER_ACTIVE_EDEFAULT);
                return;
            case MdmserverobjectPackage.WS_VIEW_E__WHERE_CONDITIONS:
                getWhereConditions().clear();
                return;
            case MdmserverobjectPackage.WS_VIEW_E__IS_TRANSFORMER_ACTIVE:
                setIsTransformerActive((WSBooleanE)null);
                return;
            case MdmserverobjectPackage.WS_VIEW_E__TRANSFORMER_PK:
                setTransformerPK(TRANSFORMER_PK_EDEFAULT);
                return;
            case MdmserverobjectPackage.WS_VIEW_E__SORT_FIELD:
                setSortField(SORT_FIELD_EDEFAULT);
                return;
            case MdmserverobjectPackage.WS_VIEW_E__IS_ASC:
                setIsAsc((WSBooleanE)null);
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
            case MdmserverobjectPackage.WS_VIEW_E__SEARCHABLE_BUSINESS_ELEMENTS:
                return searchableBusinessElements != null && !searchableBusinessElements.isEmpty();
            case MdmserverobjectPackage.WS_VIEW_E__VIEWABLE_BUSINESS_ELEMENTS:
                return viewableBusinessElements != null && !viewableBusinessElements.isEmpty();
            case MdmserverobjectPackage.WS_VIEW_E__TRANSFORMER_ACTIVE:
                return transformerActive != TRANSFORMER_ACTIVE_EDEFAULT;
            case MdmserverobjectPackage.WS_VIEW_E__WHERE_CONDITIONS:
                return whereConditions != null && !whereConditions.isEmpty();
            case MdmserverobjectPackage.WS_VIEW_E__IS_TRANSFORMER_ACTIVE:
                return isTransformerActive != null;
            case MdmserverobjectPackage.WS_VIEW_E__TRANSFORMER_PK:
                return TRANSFORMER_PK_EDEFAULT == null ? transformerPK != null : !TRANSFORMER_PK_EDEFAULT.equals(transformerPK);
            case MdmserverobjectPackage.WS_VIEW_E__SORT_FIELD:
                return SORT_FIELD_EDEFAULT == null ? sortField != null : !SORT_FIELD_EDEFAULT.equals(sortField);
            case MdmserverobjectPackage.WS_VIEW_E__IS_ASC:
                return isAsc != null;
        }
        return super.eIsSet(featureID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String toString() {
        if (eIsProxy()) return super.toString();

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (searchableBusinessElements: ");
        result.append(searchableBusinessElements);
        result.append(", viewableBusinessElements: ");
        result.append(viewableBusinessElements);
        result.append(", transformerActive: ");
        result.append(transformerActive);
        result.append(", transformerPK: ");
        result.append(transformerPK);
        result.append(", sortField: ");
        result.append(sortField);
        result.append(')');
        return result.toString();
    }

    @Override
    public int getType() {
        return 8;
    }
} // WsViewEImpl
