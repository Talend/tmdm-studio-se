/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.mdm.repository.model.mdmserverobject.matchrule;

import org.eclipse.emf.common.util.EMap;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Map Info Page</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.talend.mdm.repository.model.mdmserverobject.matchrule.MatchRuleMapInfoPage#getMatchKeyMap <em>Match Key Map</em>}</li>
 *   <li>{@link org.talend.mdm.repository.model.mdmserverobject.matchrule.MatchRuleMapInfoPage#getParent <em>Parent</em>}</li>
 *   <li>{@link org.talend.mdm.repository.model.mdmserverobject.matchrule.MatchRuleMapInfoPage#getName <em>Name</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.talend.mdm.repository.model.mdmserverobject.matchrule.MatchRulePackage#getMatchRuleMapInfoPage()
 * @model
 * @generated
 */
public interface MatchRuleMapInfoPage extends EObject {
    /**
     * Returns the value of the '<em><b>Match Key Map</b></em>' map.
     * The key is of type {@link java.lang.String},
     * and the value is of type {@link java.lang.String},
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Match Key Map</em>' map isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Match Key Map</em>' map.
     * @see org.talend.mdm.repository.model.mdmserverobject.matchrule.MatchRulePackage#getMatchRuleMapInfoPage_MatchKeyMap()
     * @model mapType="org.talend.mdm.repository.model.mdmserverobject.matchrule.KeyXPathMap<org.eclipse.emf.ecore.EString, org.eclipse.emf.ecore.EString>"
     * @generated
     */
    EMap<String, String> getMatchKeyMap();

    /**
     * Returns the value of the '<em><b>Parent</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link org.talend.mdm.repository.model.mdmserverobject.matchrule.EntityMapInfo#getMatchRuleMapInfoPages <em>Match Rule Map Info Pages</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Parent</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Parent</em>' container reference.
     * @see #setParent(EntityMapInfo)
     * @see org.talend.mdm.repository.model.mdmserverobject.matchrule.MatchRulePackage#getMatchRuleMapInfoPage_Parent()
     * @see org.talend.mdm.repository.model.mdmserverobject.matchrule.EntityMapInfo#getMatchRuleMapInfoPages
     * @model opposite="matchRuleMapInfoPages" transient="false"
     * @generated
     */
    EntityMapInfo getParent();

    /**
     * Sets the value of the '{@link org.talend.mdm.repository.model.mdmserverobject.matchrule.MatchRuleMapInfoPage#getParent <em>Parent</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Parent</em>' container reference.
     * @see #getParent()
     * @generated
     */
    void setParent(EntityMapInfo value);

    /**
     * Returns the value of the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Name</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Name</em>' attribute.
     * @see #setName(String)
     * @see org.talend.mdm.repository.model.mdmserverobject.matchrule.MatchRulePackage#getMatchRuleMapInfoPage_Name()
     * @model
     * @generated
     */
    String getName();

    /**
     * Sets the value of the '{@link org.talend.mdm.repository.model.mdmserverobject.matchrule.MatchRuleMapInfoPage#getName <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Name</em>' attribute.
     * @see #getName()
     * @generated
     */
    void setName(String value);

} // MatchRuleMapInfoPage
