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

public class EditXSDIdentityConstraintNameValidatorTest {

    @Test
    public void testIsValid() {
        String entityName = "Product"; //$NON-NLS-1$
        XSDElementDeclaration mockEntity = mock(XSDElementDeclaration.class);
        when(mockEntity.getName()).thenReturn(entityName);

        XSDSchema mockSchema = mock(XSDSchema.class);
        XSDIdentityConstraintDefinition mockConstraintDef0 = mock(XSDIdentityConstraintDefinition.class);
        when(mockConstraintDef0.getName()).thenReturn(entityName);
        when(mockConstraintDef0.getSchema()).thenReturn(mockSchema);
        when(mockConstraintDef0.getIdentityConstraintCategory()).thenReturn(XSDIdentityConstraintCategory.UNIQUE_LITERAL);
        when(mockConstraintDef0.getContainer()).thenReturn(mockEntity);

        EList<XSDIdentityConstraintDefinition> constraintDefs = new BasicEList<XSDIdentityConstraintDefinition>();
        XSDIdentityConstraintDefinition mockConstraintDef1 = mock(XSDIdentityConstraintDefinition.class);
        when(mockConstraintDef1.getName()).thenReturn("Family"); //$NON-NLS-1$
        constraintDefs.add(mockConstraintDef0);
        constraintDefs.add(mockConstraintDef1);
        when(mockSchema.getIdentityConstraintDefinitions()).thenReturn(constraintDefs);

        EditXSDIdentityConstraintNameValidator validator = new EditXSDIdentityConstraintNameValidator(mockConstraintDef0);

        ///////////
        String constraintName = null;
        String msg = validator.isValid(constraintName);
        assertEquals(Messages.EditXSDIdXXValidator_UniqueKeyNameCannotbeEmpty, msg);

        constraintName = ""; //$NON-NLS-1$
        msg = validator.isValid(constraintName);
        assertEquals(Messages.EditXSDIdXXValidator_UniqueKeyNameCannotbeEmpty, msg);

        /////
        constraintName = " Prod_897"; //$NON-NLS-1$ blank string on the head
        msg = validator.isValid(constraintName);
        assertEquals(Messages.EditXSDIdXXValidator_UniqueKeyNameCannotContainEmpty, msg);

        constraintName = "Prod 897"; //$NON-NLS-1$ blank string among it
        msg = validator.isValid(constraintName);
        assertEquals(Messages.EditXSDIdXXValidator_UniqueKeyNameCannotContainEmpty, msg);

        /////
        constraintName = entityName + "_tail"; //$NON-NLS-1$ not equal to entity name
        msg = validator.isValid(constraintName);
        assertEquals(Messages.EditXSDIdXXValidator_UniqueKeyNameMustbeEqualXX, msg);

        constraintName = entityName; //
        msg = validator.isValid(constraintName);
        assertEquals(Messages.EditXSDIdXXValidator_KeyAlreadyExist, msg);

    }

}
