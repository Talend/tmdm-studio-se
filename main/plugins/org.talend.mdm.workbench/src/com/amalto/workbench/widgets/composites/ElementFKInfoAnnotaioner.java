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
package com.amalto.workbench.widgets.composites;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;

import com.amalto.workbench.i18n.Messages;

/**
 * created by liusongbo on Sep 11, 2015
 *
 */
public class ElementFKInfoAnnotaioner {

    private static Log log = LogFactory.getLog(ElementFKInfoAnnotaioner.class);

    private static String separator = "+"; //$NON-NLS-1$

    private static final String TYPE = "org.eclipse.ui.workbench.texteditor.error"; //$NON-NLS-1$

    private ProjectionAnnotationModel annotationModel;

    private IDocument document;

    private List<String> xPaths;

    public ElementFKInfoAnnotaioner(ProjectionAnnotationModel annotationModel, IDocument document) {
        this.annotationModel = annotationModel;
        this.document = document;
    }

    public Set<Annotation> updateAnnotations(ProjectionAnnotationModel annoModel, DocumentEvent event) {
        if (event != null) {
            this.document = event.fDocument;
        }

        if (annoModel != null) {
            this.annotationModel = annoModel;
        }

        if (document != null) {
            try {
                List<ITypedRegion> partitions = Arrays.asList(document.computePartitioning(0, document.getLength()));
                Map<Annotation, Position> addedAnnotations = buildAnnotations(partitions);
                addAnnotationToModel(addedAnnotations);

                return addedAnnotations.keySet();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

        return new HashSet<Annotation>();
    }

    private Map<Annotation, Position> buildAnnotations(List<ITypedRegion> partitions)
            throws BadLocationException {

        Map<Annotation, Position> annotations = new HashMap<Annotation, Position>();

        // check if different partitions are connected by '+'
        int i = 0;
        for (; i < partitions.size(); i++) {
            ITypedRegion partion = partitions.get(i);

            String partionContent = document.get(partion.getOffset(), partion.getLength());

            if (partion.getType().equals(IDocument.DEFAULT_CONTENT_TYPE)) {
                if (i == 0 || i == partitions.size() - 1) {
                    if (i == 0) {
                        if (partionContent.trim().startsWith(separator)) {
                            addToAnnotations(0, partionContent.length(), Messages.ElementFKInfoAnnotaioner_shouldNotAtStart, annotations);
                        }

                        if (i != partitions.size() - 1 && !partionContent.trim().endsWith(separator)
                                && !partionContent.trim().isEmpty()) {
                            addToAnnotations(partion.getOffset(), partionContent.length(), Messages.ElementFKInfoAnnotaioner_missingAfter,
                                    annotations);
                        }
                    }
                    if (i == partitions.size() - 1) {
                        if (partionContent.trim().endsWith(separator)) {
                            addToAnnotations(partion.getOffset(), partionContent.length(), Messages.ElementFKInfoAnnotaioner_shouldNotAtEnd,
                                    annotations);
                        }

                        if (i != 0 && !partionContent.trim().startsWith(separator) && !partionContent.trim().isEmpty()) {
                            addToAnnotations(partion.getOffset(), partionContent.length(), Messages.ElementFKInfoAnnotaioner_missingBefore,
                                    annotations);
                        }
                    }
                } else {
                    if (!partionContent.trim().startsWith(separator)) {
                        addToAnnotations(partion.getOffset(), partionContent.length(), Messages.ElementFKInfoAnnotaioner_missingBefore,
                                annotations);
                    }
                    if (!partionContent.trim().endsWith(separator)) {
                        addToAnnotations(partion.getOffset(), partionContent.length(), Messages.ElementFKInfoAnnotaioner_missingAfter,
                                annotations);
                    }
                }
            }
        }

        // check if partition is valid
        for (ITypedRegion partition : partitions) {
            if (IDocument.DEFAULT_CONTENT_TYPE.equals(partition.getType())) {
                int partionOffset = partition.getOffset();
                int partionLength = partition.getLength();
                String partion = document.get(partionOffset, partionLength);

                partionLength = partion.trim().length();
                int index = partion.indexOf(partion.trim());
                partionOffset += index;

                partion = partion.trim();
                if (partion.startsWith(separator)) {
                    partion = partion.substring(1).trim();
                    partionOffset++;
                    partionLength--;
                }

                if (partion.endsWith(separator)) {
                    partion = partion.substring(0, partion.length() - 1).trim();
                    partionLength--;
                }

                if (partion.contains(separator)) {
                    handleMergedXpaths(partion, partition, xPaths, annotations);
                } else {
                    if (!xPaths.contains(partion) && !partion.isEmpty()) {
                        addToAnnotations(partionOffset, partionLength,
                                Messages.bind(Messages.ElementFKInfoAnnotaioner_invalidXpath, partion), annotations);
                    }
                }
            }
        }

        return annotations;
    }

    /**
     * @param partion
     */
    private boolean handleMergedXpaths(String partion, ITypedRegion partition, List<String> xpaths,
            Map<Annotation, Position> annotations) throws BadLocationException {
        int offset = partition.getOffset();
        int length = partition.getLength();
        String originPartion = document.get(offset, length);
        if (originPartion.startsWith(separator)) {
            offset++;
        }

        for (int i = 0; i < partion.length(); i++) {
            if (partion.charAt(i) == '+') {
                int j = i + 1;
                for (; j < partion.length(); j++) {
                    if (partion.charAt(j) != '+') {
                        break;
                    }
                }

                int len = j - i;
                if ((i == 0 || i == partion.length() - 1) || len > 1) {
                    addToAnnotations(offset + i, len,
                            Messages.bind(Messages.ElementFKInfoAnnotaioner_redundant, partion.substring(i, j)), annotations);
                }
                i = j - 1;
            } else {
                int j = i + 1;
                for (; j < partion.length(); j++) {
                    if (partion.charAt(j) == '+') {
                        break;
                    }
                }
                int len = j - i;
                if (!xpaths.contains(partion.substring(i, j).trim())) {
                    addToAnnotations(offset + i, len,
                            Messages.bind(Messages.ElementFKInfoAnnotaioner_invalidXpath, partion.substring(i, j)),
                            annotations);
                }
                i = j - 1;
            }

        }

        return true;
    }

    private void addToAnnotations(int offset, int length, String msg, Map<Annotation, Position> annotations) {
        Annotation annotation = new Annotation(TYPE, false, msg);
        Position position = new Position(offset);
        position.setLength(length);

        annotations.put(annotation, position);
    }

    private void addAnnotationToModel(Map<Annotation, Position> addedAnnotations) {
        annotationModel.removeAllAnnotations();

        Iterator<Annotation> iterator = addedAnnotations.keySet().iterator();
        for (; iterator.hasNext();) {
            Annotation anno = iterator.next();
            annotationModel.addAnnotation(anno, addedAnnotations.get(anno));
        }

    }

    public Set<Annotation> setFKInfos(List<String> xPaths) {
        this.xPaths = xPaths;
        Set<Annotation> updatedAnnotations = updateAnnotations(null, null);
        return updatedAnnotations;
    }
}
