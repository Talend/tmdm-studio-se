/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.mdm.repository.model.mdmserverobject;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>WS Transformer V2E</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.talend.mdm.repository.model.mdmserverobject.WSTransformerV2E#getProcessSteps <em>Process Steps</em>}</li>
 *   <li>{@link org.talend.mdm.repository.model.mdmserverobject.WSTransformerV2E#isWithAdminPermissions <em>With Admin Permissions</em>}</li>
 * </ul>
 *
 * @see org.talend.mdm.repository.model.mdmserverobject.MdmserverobjectPackage#getWSTransformerV2E()
 * @model
 * @generated
 */
public interface WSTransformerV2E extends MDMServerObject {
    /**
     * Returns the value of the '<em><b>Process Steps</b></em>' containment reference list.
     * The list contents are of type {@link org.talend.mdm.repository.model.mdmserverobject.WSTransformerProcessStepE}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Process Steps</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Process Steps</em>' containment reference list.
     * @see org.talend.mdm.repository.model.mdmserverobject.MdmserverobjectPackage#getWSTransformerV2E_ProcessSteps()
     * @model containment="true"
     * @generated
     */
    EList<WSTransformerProcessStepE> getProcessSteps();

    /**
     * Returns the value of the '<em><b>With Admin Permissions</b></em>' attribute.
     * The default value is <code>"false"</code>.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>With Admin Permissions</em>' attribute.
     * @see #setWithAdminPermissions(boolean)
     * @see org.talend.mdm.repository.model.mdmserverobject.MdmserverobjectPackage#getWSTransformerV2E_WithAdminPermissions()
     * @model default="false"
     * @generated
     */
    boolean isWithAdminPermissions();

    /**
     * Sets the value of the '{@link org.talend.mdm.repository.model.mdmserverobject.WSTransformerV2E#isWithAdminPermissions <em>With Admin Permissions</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>With Admin Permissions</em>' attribute.
     * @see #isWithAdminPermissions()
     * @generated
     */
    void setWithAdminPermissions(boolean value);

} // WSTransformerV2E
