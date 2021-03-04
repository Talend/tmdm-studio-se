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
package org.talend.mdm.repository.core.service.wsimpl.transformplugin;

import org.talend.mdm.repository.core.service.ws.AbstractPluginDetail;
import org.talend.mdm.repository.i18n.Messages;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class GroovyPluginDetail extends AbstractPluginDetail {

    /**
     * DOC hbhong ProjectPluginDetail constructor comment.
     *
     * @param twoLetterLanguageCode
     */
    public GroovyPluginDetail(String twoLetterLanguageCode) {
        super(twoLetterLanguageCode);
    }

    private static final String VARIABLE_INPUT = "variable_input"; //$NON-NLS-1$

    private static final String SCRIPT_OUTPUT = "script_output"; //$NON-NLS-1$

    protected String[] getInputVarNames() {
        return new String[] { VARIABLE_INPUT };
    }

    protected String[] getOutputVarNames() {
        return new String[] { SCRIPT_OUTPUT };
    }

    public String getDescription() {
        String description = ""; //$NON-NLS-1$
        if (twoLettersLanguageCode.equalsIgnoreCase("en")) { //$NON-NLS-1$
            description = Messages.GroovyPluginDetail_PluginCallScript;
        } else {
            description = Messages.BatchProjectXX_UNSupportedLan;
        }
        return description;
    }

    public String getDocumentation() {
        return "The groovy plugin can take the advantages of the groovy script to transformer data. \n" //$NON-NLS-1$
                + "\n" //$NON-NLS-1$
                + "\n" //$NON-NLS-1$
                + "Parameters\n" //$NON-NLS-1$
                + "   autoParseXml [optional]: parsed the default variable 'variableInput' to a xml object automatically. default: 'false'" //$NON-NLS-1$
                + "\n" + "   script [mandatory]: the content of the groovy script" + "\n" + "\n" + "\n" + "Example1" + "\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
                + "   <parameters>" + "\n" + "       <autoParseXml>false</autoParseXml>" + "\n" + "       <script><![CDATA[" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
                + "\n" + "           def records = new XmlParser().parseText(variableInput);" + "\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                + "           println records;//print parsedXmlObject" + "\n" + "           return records.depthFirst().size();" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                + "\n" + "       ]]></script>" + "\n" + "   </parameters>" + "\n" + "\n" + "Example2" + "\n" + "   <parameters>" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
                + "\n" + "       <autoParseXml>true</autoParseXml>" + "\n" + "       <script><![CDATA[" + "\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
                + "           def updateReport = variableInput;" + "\n" //$NON-NLS-1$ //$NON-NLS-2$
                + "           def itemProjection = MdmGroovyExtension.getItemProjection(" + "\n" //$NON-NLS-1$ //$NON-NLS-2$
                + "                 updateReport.RevisionID.text()," + "\n" + "                 updateReport.DataCluster.text()," //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                + "\n" + "                 updateReport.Concept.text()," + "\n" + "                 updateReport.Key.text()" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                + "\n" + "           );" + "\n" + "           //TODO: filter itemProjection" + "\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
                + "           return itemProjection;" + "\n" + "       ]]></script>" + "\n" + "   </parameters>" + "\n" + "\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
                + "\n" + "Notes for Plugin Developers: " + "\n" + "       empty"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    }

    public String getParametersSchema() {
        // Is this feature in use now?
        return "<xsd:schema" //$NON-NLS-1$
                + "       elementFormDefault='unqualified'" //$NON-NLS-1$
                + "       xmlns:xsd='http://www.w3.org/2001/XMLSchema'" //$NON-NLS-1$
                + ">" //$NON-NLS-1$
                + "<xsd:element name='parameters'>" //$NON-NLS-1$
                + "           <xsd:complexType >" //$NON-NLS-1$
                + "               <xsd:sequence>" //$NON-NLS-1$
                + "                   <xsd:element minOccurs='0' maxOccurs='1' nillable='false' name='autoParseXml' type='xsd:string'/>" //$NON-NLS-1$
                + "                   <xsd:element minOccurs='1' maxOccurs='1' nillable='false' name='script' type='xsd:string'/>" //$NON-NLS-1$
                + "               </xsd:sequence>" + "           </xsd:complexType>" + "</xsd:element>" + "</xsd:schema>"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

    }

    public String getJNDIName() {
        return "groovy"; //$NON-NLS-1$
    }
}
