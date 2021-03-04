// ============================================================================
//
// Copyright (C) 2006-2021 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.repository.ui.wizards.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSchema;
import org.junit.Test;

import com.amalto.workbench.utils.Util;


/**
 * DOC Administrator  class global comment. Detailled comment
 */
public class AddBrowseItemsWizardRTest {

    /**
     * Test method for
     * {@link org.talend.mdm.repository.ui.wizards.view.AddBrowseItemsWizardR#getKeysForViewElements(org.eclipse.xsd.XSDElementDeclaration)}
     * .
     *
     * @throws Exception
     */
    @Test
    public void testGetKeysForViewElements() throws Exception {
        String xsd = null;
        XSDSchema schema = null;
        xsd = "<xsd:schema xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" + //$NON-NLS-1$
                "<xsd:import namespace=\"http://www.w3.org/2001/XMLSchema\"/>" + //$NON-NLS-1$
                "<xsd:element name=\"Entity\">" + //$NON-NLS-1$
                "<xsd:complexType>" + //$NON-NLS-1$
                "<xsd:all>" + //$NON-NLS-1$
                "<xsd:element name=\"id\" type=\"xsd:string\"/>" + //$NON-NLS-1$
                "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"fff\" type=\"xsd:string\"/>" //$NON-NLS-1$
                + "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"eee\" type=\"xsd:string\"/>"//$NON-NLS-1$
                + "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"ddd\" type=\"xsd:string\"/>"//$NON-NLS-1$
                + "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"ggg\" type=\"xsd:string\"/>"//$NON-NLS-1$
                + "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"hhh\" type=\"xsd:string\"/>"//$NON-NLS-1$
                + "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"ccc\" type=\"xsd:string\"/>" + //$NON-NLS-1$
                "</xsd:all>" + //$NON-NLS-1$
                "</xsd:complexType>" + //$NON-NLS-1$
                "<xsd:unique name=\"Entity\">" + "<xsd:selector xpath=\".\"/>" //$NON-NLS-1$ //$NON-NLS-2$
                + "<xsd:field xpath=\"id\"/>" + //$NON-NLS-1$
                "</xsd:unique>" + //$NON-NLS-1$
                "</xsd:element>" + //$NON-NLS-1$
                "</xsd:schema>";//$NON-NLS-1$


        schema = Util.getXSDSchema(xsd);
        schema.getElementDeclarations();
        XSDElementDeclaration decl = schema.getElementDeclarations().get(0);
        List<String> idList = new ArrayList<String>();
        idList.add("Entity/id");
        List<String> keys = AddBrowseItemsWizardR.getFieldsForViewElements(decl, idList);
        keys.addAll(0, idList);
        assertNotNull(keys);
        // Only the top 5 attributes restriction is test here,
        // the original elements count is 7
        assertEquals(keys.size(), 5);
        for (int i = 0; i < keys.size(); i++) {
            switch (i) {
            case 0:
                // the element name is #eee ,so it now has been replaced to empty string
                assertEquals(keys.get(i), "Entity/id"); //$NON-NLS-1$
                continue;
            case 1:
                assertEquals(keys.get(i), "Entity/fff"); //$NON-NLS-1$
                continue;
            case 2:
                assertEquals(keys.get(i), "Entity/eee"); //$NON-NLS-1$
                continue;
            case 3:
                assertEquals(keys.get(i), "Entity/ddd"); //$NON-NLS-1$
                continue;
            case 4:
                assertEquals(keys.get(i), "Entity/ggg"); //$NON-NLS-1$
                continue;
            }
        }

    }

}
