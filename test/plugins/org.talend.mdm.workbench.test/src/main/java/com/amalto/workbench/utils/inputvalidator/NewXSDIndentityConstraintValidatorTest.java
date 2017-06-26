package com.amalto.workbench.utils.inputvalidator;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDIdentityConstraintCategory;
import org.eclipse.xsd.XSDIdentityConstraintDefinition;
import org.eclipse.xsd.XSDSchema;
import org.junit.Test;

import com.amalto.workbench.i18n.Messages;


public class NewXSDIndentityConstraintValidatorTest {

    @Test
    public void testIsValid() {
        String name = "Product"; //$NON-NLS-1$
        XSDIdentityConstraintCategory type = XSDIdentityConstraintCategory.UNIQUE_LITERAL;
        XSDElementDeclaration mockEntity = mock(XSDElementDeclaration.class);

        EList<XSDIdentityConstraintDefinition> constraintDefs = new BasicEList<XSDIdentityConstraintDefinition>();
        XSDIdentityConstraintDefinition mockConstraintDef = mock(XSDIdentityConstraintDefinition.class);
        when(mockConstraintDef.getName()).thenReturn("Product"); //$NON-NLS-1$
        when(mockConstraintDef.getContainer()).thenReturn(mockEntity);
        when(mockConstraintDef.getIdentityConstraintCategory()).thenReturn(XSDIdentityConstraintCategory.UNIQUE_LITERAL);
        constraintDefs.add(mockConstraintDef);


        XSDSchema mockSchema = mock(XSDSchema.class);
        when(mockSchema.getIdentityConstraintDefinitions()).thenReturn(constraintDefs);

        NewXSDIndentityConstraintValidator validator = new NewXSDIndentityConstraintValidator(mockSchema);

        /////
        String keyName = null;
        String msg = validator.isValid(keyName, type, mockEntity);
        assertEquals(Messages.NewXSDIndentityConstraintValidator_KeyNameCannotbeEmpty, msg);

        keyName = ""; //$NON-NLS-1$
        msg = validator.isValid(keyName, type, mockEntity);
        assertEquals(Messages.NewXSDIndentityConstraintValidator_KeyNameCannotbeEmpty, msg);

        keyName = " "; //$NON-NLS-1$
        msg = validator.isValid(keyName, type, mockEntity);
        assertEquals(Messages.NewXSDIndentityConstraintValidator_KeyNameCannotbeEmpty, msg);

        keyName = "Family"; //$NON-NLS-1$ UNIQUE_LITERAL
        msg = validator.isValid(keyName, type, mockEntity);
        assertEquals(Messages.NewXSDIndentityConstraintValidator_BElemntAlreadyHasUniqueKey, msg);

        keyName = name;
        when(mockConstraintDef.getIdentityConstraintCategory()).thenReturn(XSDIdentityConstraintCategory.KEY_LITERAL);
        msg = validator.isValid(keyName, type, mockEntity);
        assertEquals(Messages.bind(Messages.NewXSDIndentityConstraintValidator_KeyInfo, keyName), msg);

        keyName = "Family"; //$NON-NLS-1$ not UNIQUE_LITERAL
        msg = validator.isValid(keyName, type, mockEntity);
        assertNull(msg);
    }

}
