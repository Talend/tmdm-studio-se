// ============================================================================
//
// Copyright (C) 2006-2019 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package com.amalto.workbench.utils.inputvalidator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSchema;
import org.junit.Test;

import com.amalto.workbench.i18n.Messages;

public class EditXSDEleDecNameValidatorTest {

    @Test
    public void testIsValid() {
        EList<XSDElementDeclaration> decls = new BasicEList<XSDElementDeclaration>();
        XSDElementDeclaration mockDecl = mock(XSDElementDeclaration.class);
        when(mockDecl.getName()).thenReturn("Product"); //$NON-NLS-1$
        decls.add(mockDecl);

        XSDSchema mockSchema = mock(XSDSchema.class);
        when(mockSchema.getElementDeclarations()).thenReturn(decls);
        EditXSDEleDecNameValidator nameValidaor = new EditXSDEleDecNameValidator(mockSchema);

        String decname = ""; //$NON-NLS-1$
        String msg = nameValidaor.isValid(decname);
        assertEquals(Messages.EditXSDEleDecNameValidator_EntityNameCannotbeEmpty, msg);

        decname = null;
        msg = nameValidaor.isValid(decname);
        assertEquals(Messages.EditXSDEleDecNameValidator_EntityNameCannotbeEmpty, msg);

        /////
        decname = " Prod_897"; //$NON-NLS-1$ blank string on the head
        msg = nameValidaor.isValid(decname);
        assertEquals(Messages.EditXSDEleDecNameValidator_EntityNameCannotContainEmpty, msg);

        decname = "Prod 897"; //$NON-NLS-1$ blank string among it
        msg = nameValidaor.isValid(decname);
        assertEquals(Messages.EditXSDEleDecNameValidator_EntityNameCannotContainEmpty, msg);

        decname = "777PP"; //$NON-NLS-1$ digit on the head
        msg = nameValidaor.isValid(decname);
        assertEquals(Messages.InvalidName_Message, msg);

        decname = "Product-"; //$NON-NLS-1$ '-' on the end
        msg = nameValidaor.isValid(decname);
        assertEquals(Messages.InvalidName_Message, msg);

        decname = "Product."; //$NON-NLS-1$ '.' on the end
        msg = nameValidaor.isValid(decname);
        assertEquals(Messages.InvalidName_Message, msg);

        decname = "Product"; //$NON-NLS-1$
        msg = nameValidaor.isValid(decname);
        assertEquals(Messages.EditXSDEleDecNameValidator_EntityAlreadyExist, msg);

        decname = "aabb"; //$NON-NLS-1$
        msg = nameValidaor.isValid(decname);
        assertNull(msg);

        decname = "PP777"; //$NON-NLS-1$ digit on the tail
        msg = nameValidaor.isValid(decname);
        assertNull(msg);
    }

}
