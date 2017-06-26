package com.amalto.workbench.utils.inputvalidator;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSchema;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.utils.XSDUtil;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ XSDUtil.class })
@PowerMockIgnore({ "com.amalto.workbench.i18n.Messages" })
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

        PowerMockito.mockStatic(XSDUtil.class);
        PowerMockito.when(XSDUtil.isValidatedXSDName(anyString())).thenReturn(false);

        decname = "PP"; //$NON-NLS-1$
        msg = nameValidaor.isValid(decname);
        assertEquals(Messages.InvalidName_Message, msg);

        PowerMockito.when(XSDUtil.isValidatedXSDName(anyString())).thenReturn(true);
        decname = "Product"; //$NON-NLS-1$
        msg = nameValidaor.isValid(decname);
        assertEquals(Messages.EditXSDEleDecNameValidator_EntityAlreadyExist, msg);

        decname = "aabb"; //$NON-NLS-1$
        msg = nameValidaor.isValid(decname);
        assertNull(msg);
    }

}
