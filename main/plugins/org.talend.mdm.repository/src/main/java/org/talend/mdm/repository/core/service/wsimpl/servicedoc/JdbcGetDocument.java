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
package org.talend.mdm.repository.core.service.wsimpl.servicedoc;

import org.talend.mdm.repository.core.service.ws.AbstractGetDocument;
import org.talend.mdm.repository.i18n.Messages;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class JdbcGetDocument extends AbstractGetDocument {

    /**
     * DOC hbhong CopyOfCallJobGetDocument constructor comment.
     *
     * @param twoLettersLanguageCode
     */
    public JdbcGetDocument(String twoLettersLanguageCode) {
        super(twoLettersLanguageCode);
    }

    @Override
    public String getJNDIName() {
        return "jdbc"; //$NON-NLS-1$
    }

    @Override
    public String getDescription() {
        if ("fr".matches(twoLettersLanguageCode.toLowerCase())) //$NON-NLS-1$
            return Messages.JdbcGetDocument_ServiceHandleJdbcDesc;
        return Messages.JdbcGetDocument_ServiceHandleJdbcDesc;
    }

    @Override
    public String getDocument() {
        return "There are two type parameters\n\n" //$NON-NLS-1$
                + "One(Example) :\n" //$NON-NLS-1$
                + "driverClassName=com.mysql.jdbc.Driver&url=jdbc:mysql://localhost:3306/brick&username=root&password=null&transformer=item2actionform" //$NON-NLS-1$
                + "\n\n\n" //$NON-NLS-1$
                + "Two(Example) :\n" //$NON-NLS-1$
                + "<parameters>\n" //$NON-NLS-1$
                + "   <driverClassName>com.mysql.jdbc.Driver</driverClassName>\n" //$NON-NLS-1$
                + "   <url>jdbc:mysql://localhost:3306/brick</url>\n" //$NON-NLS-1$
                + "   <username>root</username>\n" //$NON-NLS-1$
                + "   <password>null</password>\n" //$NON-NLS-1$
                + "   <transformer>item2actionform</transformer>\n" //$NON-NLS-1$
                + "</parameters>\n\n\n" //$NON-NLS-1$
                + "Notice :\n" //$NON-NLS-1$
                + "The transformer should expect to receive the content of the Item sent to the transformer in the DEFAULT variable \n" //$NON-NLS-1$
                + "with a content-type of text/xml.\nThe transformer convert Item to an ActionContent Xml Form of JDBC Adpter.\n"; //$NON-NLS-1$

    }

}
