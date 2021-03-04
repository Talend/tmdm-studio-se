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
package org.talend.mdm.repository.ui.wizards.process;

import java.util.ArrayList;
import java.util.List;

import org.talend.mdm.repository.model.mdmserverobject.MdmserverobjectFactory;
import org.talend.mdm.repository.model.mdmserverobject.WSTransformerProcessStepE;
import org.talend.mdm.repository.model.mdmserverobject.WSTransformerVariablesMappingE;

import com.amalto.workbench.utils.Util;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class ProcessStepFactory {

    private static final String VAR_LAW_TEXT = "law_text"; //$NON-NLS-1$

    private static final String VAR_DECODE_XML = "decode_xml"; //$NON-NLS-1$

    private static final String VAR_CODEC_TEXT = "codec_text"; //$NON-NLS-1$

    public static final String VAR_OUTPUT_URL = "output_url"; //$NON-NLS-1$

    public static final String VAR_OUTPUT = "output"; //$NON-NLS-1$

    public static final String VAR_OUTPUT_REPORT = "output_report"; //$NON-NLS-1$

    private static final String VAR_ITEM_XML = "item_xml"; //$NON-NLS-1$

    private static final String VAR_XML = "xml"; //$NON-NLS-1$

    public static final String VAR_DEFAULT = "_DEFAULT_"; //$NON-NLS-1$

    private static final String VAR_TEXT = "text"; //$NON-NLS-1$

    private static final String VAR_RESULT = "result"; //$NON-NLS-1$

    private static final String CODEC_PLUGIN = "amalto/local/transformer/plugin/codec"; //$NON-NLS-1$

    private static final String XSLT_PLUGIN = "amalto/local/transformer/plugin/xslt";//$NON-NLS-1$

    private static final String CALLJOB_PLUGIN = "amalto/local/transformer/plugin/callJob";//$NON-NLS-1$

    // step
    public static final int STEP_UPDATE_REPORT = 1;

    public static final int STEP_ESCAPE = 2;

    public static final int STEP_REDIRECT = 3;

    public static final int STEP_RETURN_MESSAGE = 4;

    public static WSTransformerProcessStepE createProcessStep(int type, Object param, String processName, boolean createJob) {
        boolean isEnterprise = Util.IsEnterPrise();
        switch (type) {
        case STEP_UPDATE_REPORT:
            return createUpdateReportStep(param);
        case STEP_ESCAPE:
            return createEscapeStep(param);
        case STEP_REDIRECT:
            if (isEnterprise && createJob) {
                if (param == null || param.toString().length() == 0) {
                    return createCallJobStep(processName, VAR_OUTPUT, VAR_DECODE_XML); // Runnable
                } else {
                    return createCallJobStep(processName, VAR_OUTPUT_URL, VAR_DECODE_XML); // Runnable
                }
            } else {
                if (param == null || param.toString().length() == 0) {
                    return createRedirectStep("", VAR_OUTPUT); //$NON-NLS-1$
                } else {
                    return createRedirectStep(param, VAR_OUTPUT_URL);
                }
            }
        case STEP_RETURN_MESSAGE:
            if (isEnterprise && createJob) {
                return createCallJobStep(processName, VAR_OUTPUT_REPORT, VAR_DEFAULT); // Before
            } else {
                return createReturnMessageStep(param);
            }
        }
        return null;
    }

    public static WSTransformerProcessStepE createSmartViewStep() {
        final String parameters = "<xsl:stylesheet xmlns:xsl='http://www.w3.org/1999/XSL/Transform' version='1.0'>\n"//$NON-NLS-1$
                + "   <xsl:output method='html' indent='yes' omit-xml-declaration='yes'/>\n"//$NON-NLS-1$
                + "   <xsl:template match='/'>\n" + "       <html>\n"//$NON-NLS-1$//$NON-NLS-2$
                + "          <head><title>Smart View</title></head>\n" + "          <body>\n"//$NON-NLS-1$//$NON-NLS-2$
                + "            <h1>This is the default Smart View for: <xsl:value-of select='./text()'/></h1>\n"//$NON-NLS-1$
                + "            <xsl:copy-of select='.'/>\n" + "            <!-- Customize the stylesheet -->\n"//$NON-NLS-1$//$NON-NLS-2$
                + "          </body>\n" + "       </html>\n" + "    </xsl:template>" + "</xsl:stylesheet>";//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$
        List<WSTransformerVariablesMappingE> inItems;
        List<WSTransformerVariablesMappingE> outItems;
        WSTransformerProcessStepE step = MdmserverobjectFactory.eINSTANCE.createWSTransformerProcessStepE();
        inItems = new ArrayList<WSTransformerVariablesMappingE>();
        WSTransformerVariablesMappingE inputLine = MdmserverobjectFactory.eINSTANCE.createWSTransformerVariablesMappingE();
        inputLine.setPipelineVariable(VAR_DEFAULT);
        inputLine.setPluginVariable(VAR_XML);
        inItems.add(inputLine);

        outItems = new ArrayList<WSTransformerVariablesMappingE>();
        WSTransformerVariablesMappingE outputLine = MdmserverobjectFactory.eINSTANCE.createWSTransformerVariablesMappingE();
        outputLine.setPipelineVariable("html");//$NON-NLS-1$
        outputLine.setPluginVariable(VAR_TEXT);
        outItems.add(outputLine);
        step.setPluginJNDI(XSLT_PLUGIN);
        step.setDescription("Stylesheet"); //$NON-NLS-1$
        step.setParameters(parameters);
        step.getInputMappings().addAll(inItems);
        step.getOutputMappings().addAll(outItems);
        step.setDisabled(false);
        return step;
    }

    public static WSTransformerProcessStepE createRedirectStep(Object param, String outputPipleVariable) {
        if (param == null || !(param instanceof String)) {
            throw new IllegalArgumentException();
        }
        String prefixParam = "<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" xmlns:mdm=\"java:com.amalto.core.plugin.base.xslt.MdmExtension\" version=\"1.0\">\n" //$NON-NLS-1$
                + "<xsl:output method=\"xml\" indent=\"yes\" omit-xml-declaration=\"yes\"/>\n" //$NON-NLS-1$
                + "<xsl:template match=\"/\" priority=\"1\">\n" + "<results>\n<item>\n<attr>"; //$NON-NLS-1$//$NON-NLS-2$
        String suffixParam = "</attr>\n</item>\n</results>\n</xsl:template>\n</xsl:stylesheet>"; //$NON-NLS-1$
        List<WSTransformerVariablesMappingE> inItems;
        List<WSTransformerVariablesMappingE> outItems;
        WSTransformerProcessStepE step = MdmserverobjectFactory.eINSTANCE.createWSTransformerProcessStepE();
        inItems = new ArrayList<WSTransformerVariablesMappingE>();
        WSTransformerVariablesMappingE inputLine = MdmserverobjectFactory.eINSTANCE.createWSTransformerVariablesMappingE();
        inputLine.setPipelineVariable(VAR_DEFAULT);
        inputLine.setPluginVariable(VAR_XML);
        inItems.add(inputLine);

        outItems = new ArrayList<WSTransformerVariablesMappingE>();
        WSTransformerVariablesMappingE outputLine = MdmserverobjectFactory.eINSTANCE.createWSTransformerVariablesMappingE();
        outputLine.setPipelineVariable(outputPipleVariable);
        outputLine.setPluginVariable(VAR_TEXT);
        outItems.add(outputLine);
        step.setPluginJNDI(XSLT_PLUGIN);
        step.setDescription("Redirect"); //$NON-NLS-1$
        step.setParameters(prefixParam + param + suffixParam);
        step.getInputMappings().addAll(inItems);
        step.getOutputMappings().addAll(outItems);
        step.setDisabled(false);
        return step;
    }

    private static WSTransformerProcessStepE createEscapeStep(Object param) {
        // Generate the XSLT step to retrieve the item from an update report
        // step
        WSTransformerProcessStepE step = MdmserverobjectFactory.eINSTANCE.createWSTransformerProcessStepE();
        List<WSTransformerVariablesMappingE> inItems = new ArrayList<WSTransformerVariablesMappingE>();
        WSTransformerVariablesMappingE inputLine = MdmserverobjectFactory.eINSTANCE.createWSTransformerVariablesMappingE();
        inputLine.setPipelineVariable(VAR_ITEM_XML);
        inputLine.setPluginVariable(VAR_LAW_TEXT);
        inItems.add(inputLine);

        List<WSTransformerVariablesMappingE> outItems = new ArrayList<WSTransformerVariablesMappingE>();
        WSTransformerVariablesMappingE outputLine = MdmserverobjectFactory.eINSTANCE.createWSTransformerVariablesMappingE();
        outputLine.setPipelineVariable(VAR_DECODE_XML);
        outputLine.setPluginVariable(VAR_CODEC_TEXT);
        outItems.add(outputLine);
        step.setPluginJNDI(CODEC_PLUGIN);
        step.setDescription("Escape the item XML"); //$NON-NLS-1$
        String parameter = "<parameters>\n" + "<method>DECODE</method>\n" + "<algorithm>XMLESCAPE</algorithm>\n"//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                + "</parameters>\n";//$NON-NLS-1$

        step.setParameters(parameter);
        step.getInputMappings().addAll(inItems);
        step.getOutputMappings().addAll(outItems);
        step.setDisabled(false);
        return step;
    }

    private static WSTransformerProcessStepE createUpdateReportStep(Object param) {
        String itemstr = "<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"  xmlns:mdm=\"java:com.amalto.core.plugin.base.xslt.MdmExtension\" version=\"1.0\"> <xsl:output method=\"xml\" indent=\"yes\" omit-xml-declaration=\"yes\" /> <xsl:template match=\"/\" priority=\"1\">\n" //$NON-NLS-1$
                + "<exchange> <report>\n <xsl:copy-of select=\"Update\"/> </report>  <item><xsl:copy-of select='mdm:getItemProjection(Update/RevisionID,Update/DataCluster,Update/Concept,Update/Key)'/></item></exchange> "//$NON-NLS-1$
                + "</xsl:template> </xsl:stylesheet>\n";//$NON-NLS-1$
        List<WSTransformerVariablesMappingE> inItems;
        List<WSTransformerVariablesMappingE> outItems;
        WSTransformerProcessStepE step = MdmserverobjectFactory.eINSTANCE.createWSTransformerProcessStepE();
        inItems = new ArrayList<WSTransformerVariablesMappingE>();
        WSTransformerVariablesMappingE inputLine = MdmserverobjectFactory.eINSTANCE.createWSTransformerVariablesMappingE();
        inputLine.setPipelineVariable(VAR_DEFAULT);
        inputLine.setPluginVariable(VAR_XML);
        inItems.add(inputLine);

        outItems = new ArrayList<WSTransformerVariablesMappingE>();
        WSTransformerVariablesMappingE outputLine = MdmserverobjectFactory.eINSTANCE.createWSTransformerVariablesMappingE();
        outputLine.setPipelineVariable(VAR_ITEM_XML);
        outputLine.setPluginVariable(VAR_TEXT);
        outItems.add(outputLine);
        step.setPluginJNDI(XSLT_PLUGIN);
        step.setDescription("UpdateReport"); //$NON-NLS-1$
        step.setParameters(itemstr);
        step.getInputMappings().addAll(inItems);
        step.getOutputMappings().addAll(outItems);
        step.setDisabled(false);
        return step;
    }

    private static WSTransformerProcessStepE createReturnMessageStep(Object param) {
        if (param == null || !(param instanceof String[]) || ((String[]) param).length != 2) {
            throw new IllegalArgumentException();
        }
        String type = ((String[]) param)[0];
        String mulMessage = ((String[]) param)[1];
        String prefixParam = "<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" xmlns:mdm=\"java:com.amalto.core.plugin.base.xslt.MdmExtension\" version=\"1.0\">\n" //$NON-NLS-1$
                + "<xsl:output method=\"xml\" indent=\"yes\" omit-xml-declaration=\"yes\"/>\n" //$NON-NLS-1$
                + "<xsl:template match=\"/\" priority=\"1\">\n" + "<report>\n <message type=\"" + type + "\">"; //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
        String suffixParam = "</message>\n</report>\n</xsl:template>\n</xsl:stylesheet>"; //$NON-NLS-1$
        List<WSTransformerVariablesMappingE> inItems;
        List<WSTransformerVariablesMappingE> outItems;
        WSTransformerProcessStepE step = MdmserverobjectFactory.eINSTANCE.createWSTransformerProcessStepE();
        inItems = new ArrayList<WSTransformerVariablesMappingE>();
        WSTransformerVariablesMappingE inputLine = MdmserverobjectFactory.eINSTANCE.createWSTransformerVariablesMappingE();
        inputLine.setPipelineVariable(VAR_DEFAULT);
        inputLine.setPluginVariable(VAR_XML);
        inItems.add(inputLine);

        outItems = new ArrayList<WSTransformerVariablesMappingE>();
        WSTransformerVariablesMappingE outputLine = MdmserverobjectFactory.eINSTANCE.createWSTransformerVariablesMappingE();
        outputLine.setPipelineVariable(VAR_OUTPUT_REPORT);
        outputLine.setPluginVariable(VAR_TEXT);
        outItems.add(outputLine);
        step.setPluginJNDI(XSLT_PLUGIN);
        step.setDescription("Retrieve Message"); //$NON-NLS-1$
        step.setParameters(prefixParam + mulMessage + suffixParam);
        step.getInputMappings().addAll(inItems);
        step.getOutputMappings().addAll(outItems);
        step.setDisabled(false);
        return step;
    }

    public static WSTransformerProcessStepE createCallJobStep(String jobName, String outputPipelineVariable, String pipleName) {

        String parameters = "<configuration><url>ltj://" + jobName + "/0.1</url>" + //$NON-NLS-1$ //$NON-NLS-2$
                "<contextParam><name>xmlInput</name><value>{" + pipleName + "}</value></contextParam></configuration>"; //$NON-NLS-1$ //$NON-NLS-2$

        List<WSTransformerVariablesMappingE> inItems;
        List<WSTransformerVariablesMappingE> outItems;
        WSTransformerProcessStepE step = MdmserverobjectFactory.eINSTANCE.createWSTransformerProcessStepE();
        inItems = new ArrayList<WSTransformerVariablesMappingE>();
        WSTransformerVariablesMappingE inputLine = MdmserverobjectFactory.eINSTANCE.createWSTransformerVariablesMappingE();
        inputLine.setPipelineVariable(pipleName);
        inputLine.setPluginVariable(VAR_TEXT);
        inItems.add(inputLine);

        outItems = new ArrayList<WSTransformerVariablesMappingE>();
        WSTransformerVariablesMappingE outputLine = MdmserverobjectFactory.eINSTANCE.createWSTransformerVariablesMappingE();
        outputLine.setPipelineVariable(outputPipelineVariable);
        outputLine.setPluginVariable(VAR_RESULT);
        outItems.add(outputLine);
        step.setPluginJNDI(CALLJOB_PLUGIN);
        step.setDescription("Call Job"); //$NON-NLS-1$
        step.setParameters(parameters);
        step.getInputMappings().addAll(inItems);
        step.getOutputMappings().addAll(outItems);
        step.setDisabled(false);

        return step;
    }

}
