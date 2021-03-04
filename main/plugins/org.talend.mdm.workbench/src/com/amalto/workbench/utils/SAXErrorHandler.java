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
package com.amalto.workbench.utils;

import java.util.regex.Pattern;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author bgrieder
 *
 */
public class SAXErrorHandler extends DefaultHandler {

    private Pattern namespacesP;

    private Pattern bracketsP;

    private Pattern commaP;

    private String errors;

    /**
     * ParserBean.java Constructor
     *
     */
    public SAXErrorHandler() {
        super();
        namespacesP = Pattern.compile("(\"publicid:org.*?.xsd\":)|(\"rrn:org.*?.xsd\":)");//$NON-NLS-1$
        bracketsP = Pattern.compile("(\\()|(\\))");//$NON-NLS-1$
        commaP = Pattern.compile(",");//$NON-NLS-1$
    }

    public void warning(SAXParseException ex) throws SAXException {
        setErrors(formatError("Warning", ex));//$NON-NLS-1$
        setErrors("\n");//$NON-NLS-1$
    }

    public void error(SAXParseException ex) throws SAXException {
        setErrors(formatError("Error", ex));//$NON-NLS-1$
        setErrors("\n");//$NON-NLS-1$
    }

    public void fatalError(SAXParseException ex) throws SAXException {
        setErrors(formatError("Fatal Error", ex));//$NON-NLS-1$
        setErrors("\n");//$NON-NLS-1$
        throw ex;
    }

    private String formatError(String type, SAXParseException ex) {
        String error = "";//$NON-NLS-1$
        error = "[" + type + "] ";//$NON-NLS-1$//$NON-NLS-2$
        String systemId = ex.getSystemId();
        if (systemId != null) {
            int index = systemId.lastIndexOf('/');
            if (index != -1)
                systemId = systemId.substring(index + 1);
            error = error + systemId;
        }
        String msg = ex.getMessage();
        msg = namespacesP.matcher(msg).replaceAll("");//$NON-NLS-1$
        msg = bracketsP.matcher(msg).replaceAll("");//$NON-NLS-1$
        msg = commaP.matcher(msg).replaceAll("");//$NON-NLS-1$
        error = error + ":" + ex.getLineNumber() + ":" + ex.getColumnNumber() + ": " + msg;//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
        return error;
    }

    public String getErrors() {
        return (errors == null) ? "" : errors;//$NON-NLS-1$
    }

    public void setErrors(String error) {
        if (errors == null)
            errors = "";//$NON-NLS-1$
        errors += error;
    }

}
