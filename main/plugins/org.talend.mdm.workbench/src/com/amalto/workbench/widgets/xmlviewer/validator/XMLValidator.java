package com.amalto.workbench.widgets.xmlviewer.validator;

/*****************************************************************************
 * This file is part of Rinzo
 * 
 * Author: Claudio Cancinos WWW: https://sourceforge.net/projects/editorxml Copyright (C): 2008, Claudio Cancinos
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this program; If not, see
 * <http://www.gnu.org/licenses/>
 ****************************************************************************/
import java.io.StringReader;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.swt.widgets.Display;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.amalto.workbench.utils.IXMLConstants;
import com.amalto.workbench.widgets.xmlviewer.model.XMLNode;
import com.amalto.workbench.widgets.xmlviewer.utils.XMLTreeModelUtilities;

public class XMLValidator {

    private static final Log LOG = LogFactory.getLog(XMLValidator.class);

    CheckThread checkthread = new CheckThread();

    private final ProjectionAnnotationModel annotationModel;

    public static final String TYPE = "org.eclipse.ui.workbench.texteditor.error"; //$NON-NLS-1$

    private final IDocument document;

    class CheckThread implements Runnable {

        private ErrorHandler errorHandler;

        public CheckThread() {
            factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setValidating(true);
            try {
                factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
                factory.setFeature(IXMLConstants.DISALLOW_DOCTYPE_DECL, true);
            } catch (ParserConfigurationException e) {
                LOG.error(e.getMessage(), e);
            }

            this.errorHandler = new ErrorHandler() {

                private int getCharStart(int lineNumber, int columnNumber) {
                    try {

                        int lineStartChar = document.getLineOffset(lineNumber - 1);
                        Integer charEnd = getCharEnd(lineNumber, columnNumber);
                        if (charEnd != null) {
                            ITypedRegion typedRegion = document.getPartition(charEnd.intValue() - 2);
                            int partitionStartChar = typedRegion.getOffset();
                            return partitionStartChar;
                        } else {
                            return lineStartChar;
                        }
                    } catch (BadLocationException e) {
                        return 0;
                    }
                }

                private Integer getCharEnd(int lineNumber, int columnNumber) {
                    try {
                        return Integer.valueOf(document.getLineOffset(lineNumber - 1) + columnNumber);
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                private Annotation createNewAnnotaion(SAXParseException exception) {
                    String message = exception.getMessage();
                    message = message.substring(message.indexOf(":") + 1).trim(); //$NON-NLS-1$
                    // REVISARME para ver si hay otra forma de hacer esto
                    if (!"no grammar found.".equals(message) && !message.endsWith("must match DOCTYPE root \"null\".")) { //$NON-NLS-1$ //$NON-NLS-2$
                        return new Annotation(TYPE, false, message);
                    }
                    return null;
                }

                private void updateAnnotation(SAXParseException exception) {
                    Annotation annotation = createNewAnnotaion(exception);

                    if (annotation != null) {
                        int offset = getCharStart(exception.getLineNumber(), exception.getColumnNumber());
                        XMLNode activeNode = XMLTreeModelUtilities.getActiveNode(document, offset);

                        if (annotationModel != null) {
                            lastAnnotation = annotation;
                            Position position = new Position(offset);
                            if (activeNode != null) {
                                position.setLength(activeNode.getLength());
                            }

                            annotationModel.addAnnotation(annotation, position);
                        }
                    }

                }

                @Override
                public void warning(SAXParseException exception) throws SAXException {
                    exception.printStackTrace();
                }

                @Override
                public void fatalError(SAXParseException exception) throws SAXException {
                    updateAnnotation(exception);
                }

                @Override
                public void error(SAXParseException exception) throws SAXException {
                    updateAnnotation(exception);
                }
            };
        }

        private String xml;

        private DocumentBuilderFactory factory;

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
            if (xml == null || xml.trim().length() == 0) {
                return;
            }
            try {
                // clear
                clearLastAnnotaion();
                //
                DocumentBuilder builder = factory.newDocumentBuilder();
                builder.setErrorHandler(errorHandler);
                builder.parse(new InputSource(new StringReader(xml)));
            } catch (Exception e) {
                // do nothing because already has errHandler to handle it
            }
        }

        public void setXML(String xml) {
            this.xml = xml;

        }
    }

    /**
     * DOC hbhong XMLValidator constructor comment.
     * 
     * @param document
     * 
     * @param annotationModel
     */
    public XMLValidator(IDocument document, ProjectionAnnotationModel annotationModel) {
        this.document = document;
        this.annotationModel = annotationModel;
    }

    Annotation lastAnnotation;

    private void clearLastAnnotaion() {
        if (annotationModel != null) {
            if (lastAnnotation != null) {
                annotationModel.removeAnnotation(lastAnnotation);
            }
        }
    }

    /**
     * DOC hbhong Comment method "validate".
     * 
     * @param document
     */
    public void validate(IDocument document) {
        checkthread.setXML(document.get());
        Display.getDefault().asyncExec(checkthread);
    }

}
