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
public class CodeProjectPluginDetail extends AbstractPluginDetail {

    /**
     * DOC hbhong ProjectPluginDetail constructor comment.
     * 
     * @param twoLetterLanguageCode
     */
    public CodeProjectPluginDetail(String twoLetterLanguageCode) {
        super(twoLetterLanguageCode);
    }

    private static final String INPUT_TEXT = "law_text"; //$NON-NLS-1$

    private static final String OUTPUT_TEXT = "codec_text"; //$NON-NLS-1$

    protected String[] getInputVarNames() {
        return new String[] { INPUT_TEXT };
    }

    protected String[] getOutputVarNames() {
        return new String[] { OUTPUT_TEXT };
    }

    public String getDescription() {
        String description = ""; //$NON-NLS-1$
        if (twoLettersLanguageCode.toLowerCase().equals("en")) { //$NON-NLS-1$
            description = Messages.CodeProjectXX_PluginUsedText;
        } else {
            description = Messages.BatchProjectXX_UNSupportedLan;
        }
        return description;
    }

    public String getDocumentation() {
        return "The Codec plugin can encode or decode your input text. \n" + "\n" + "\n" + "Parameters\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                + "   method [mandatory]: specify whether ENCODE or DECODE input text" + "\n" //$NON-NLS-1$ //$NON-NLS-2$
                + "   algorithm [mandatory]: specify which algorithm will be used" + "\n" //$NON-NLS-1$ //$NON-NLS-2$
                + "   wrap [optional]: wrap an xml tag of an codec text 'true' or 'false'. Default: 'false'" + "\n" + "\n" + "\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                + "Example" + "\n" + "   <parameters>" + "\n" + "       <method>ENCODE</method>" + "\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
                + "       <algorithm>BASE64</algorithm>" + "\n" + "   </parameters>" + "\n" + "\n" + "\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
                + "Notes for Plugin Developers: " + "\n" + "       empty"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    public String getParametersSchema() {
        return "<xsd:schema" //$NON-NLS-1$
                + "       elementFormDefault='unqualified'" //$NON-NLS-1$
                + "       xmlns:xsd='http://www.w3.org/2001/XMLSchema'" //$NON-NLS-1$
                + ">" //$NON-NLS-1$
                + "<xsd:element name='parameters'>" //$NON-NLS-1$
                + "           <xsd:complexType >" //$NON-NLS-1$
                + "               <xsd:sequence>" //$NON-NLS-1$
                + "                   <xsd:element minOccurs='1' maxOccurs='1' nillable='false' name='method' type='xsd:string'/>" //$NON-NLS-1$
                + "                   <xsd:element minOccurs='1' maxOccurs='1' nillable='false' name='algorithm' type='xsd:string'/>" //$NON-NLS-1$
                + "                   <xsd:element minOccurs='0' maxOccurs='1' nillable='false' name='wrap' type='xsd:string'/>" //$NON-NLS-1$
                + "               </xsd:sequence>" + "           </xsd:complexType>" + "</xsd:element>" + "</xsd:schema>"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    }

    public String getJNDIName() {
        return "codec"; //$NON-NLS-1$
    }
}
