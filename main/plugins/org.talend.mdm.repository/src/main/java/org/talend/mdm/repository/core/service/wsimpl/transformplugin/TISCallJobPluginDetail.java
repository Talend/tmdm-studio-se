// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.repository.core.service.wsimpl.transformplugin;

import org.talend.mdm.repository.core.service.ws.AbstractPluginDetail;
import org.talend.mdm.repository.i18n.Messages;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class TISCallJobPluginDetail extends AbstractPluginDetail {

    /**
     * DOC hbhong ProjectPluginDetail constructor comment.
     * 
     * @param twoLetterLanguageCode
     */
    public TISCallJobPluginDetail(String twoLetterLanguageCode) {
        super(twoLetterLanguageCode);
    }

    private static final String INPUT_TEXT = "text"; //$NON-NLS-1$

    private static final String OUTPUT_TEXT = "result"; //$NON-NLS-1$

    @Override
    protected String[] getInputVarNames() {
        return new String[] { INPUT_TEXT };
    }

    @Override
    protected String[] getOutputVarNames() {
        return new String[] { OUTPUT_TEXT };
    }

    @Override
    public String getDescription() {
        if ("fr".matches(twoLettersLanguageCode.toLowerCase())) //$NON-NLS-1$
        {
            return "Execute un call de TIS un texte et retourne le résultat"; //$NON-NLS-1$
        }
        return Messages.TISCallJobPluginDetail_description;
    }

    @Override
    public String getDocumentation() {
        return Messages.TISCallJobPluginDetail_doc_line1 + Messages.TISCallJobPluginDetail_doc_line2
                + Messages.TISCallJobPluginDetail_doc_line3 + Messages.TISCallJobPluginDetail_doc_line4
                + Messages.TISCallJobPluginDetail_doc_line5 + Messages.TISCallJobPluginDetail_doc_line6
                + Messages.TISCallJobPluginDetail_doc_line7 + Messages.TISCallJobPluginDetail_doc_line8
                + Messages.TISCallJobPluginDetail_doc_line9 + Messages.TISCallJobPluginDetail_doc_line10
                + Messages.TISCallJobPluginDetail_doc_line11 + Messages.TISCallJobPluginDetail_doc_line12
                + Messages.TISCallJobPluginDetail_doc_line13 + Messages.TISCallJobPluginDetail_doc_line14
                + Messages.TISCallJobPluginDetail_doc_line15 + Messages.TISCallJobPluginDetail_doc_line16
                + Messages.TISCallJobPluginDetail_doc_line17 + Messages.TISCallJobPluginDetail_example1
                + "   <configuration>" + "\n" //$NON-NLS-1$ //$NON-NLS-2$ 
                + "       <url>http://server:port/TISService/TISPort</url>" + "\n" + "       <contextParam>" + "\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                + "           <name>firstname</name>" + "\n" + "           <value>jack</value>" + "\n" + "       </contextParam>" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
                + "\n" + "       <contextParam>" + "\n" + "           <name>lastname</name>" + "\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
                + "           <value>jones</value>" + "\n" + "       </contextParam>" + "\n" + "       <contextParam>" + "\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
                + "           <name>company</name>" + "\n" + "           <value>{pipleVariableName}</value>" + "\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                + "       </contextParam>" + "\n" + "       <username>john</username>" + "\n" + "       <password>doe</password>" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
                + "\n" + "       <conceptMapping>" + "\n" + "           <concept>User</concept>" + "\n" + "           <fields>" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
                + "\n" + "             {" + "\n" + "             p1:firstname," + "\n" + "             p2:lastname" + "\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
                + "             }" + "\n" + "           </fields>" + "\n" + "       </conceptMapping>" + "\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
                + "   </configuration>" + "\n\n" //$NON-NLS-1$ //$NON-NLS-2$
                + Messages.TISCallJobPluginDetail_example2 + "   <configuration>" + "\n" //$NON-NLS-1$ //$NON-NLS-2$ 
                + "       <url>ltj://tiscall_multi_return/0.1</url>" + "\n" + "       <contextParam>" + "\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                + "           <name>nb_line</name>" + "\n" + "           <value>5</value>" + "\n" + "       </contextParam>" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
                + "\n" + "   </configuration>" + "\n\n"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    @Override
    public String getParametersSchema() {
        return null;
    }

    @Override
    public String getJNDIName() {
        return "callJob"; //$NON-NLS-1$
    }
}
