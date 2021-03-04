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

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionSupport;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.AnnotationPreference;
import org.eclipse.ui.texteditor.MarkerAnnotationPreferences;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;

import com.amalto.workbench.MDMWorbenchPlugin;
import com.amalto.workbench.widgets.xmlviewer.XMLSourceViewerHelper;
import com.amalto.workbench.widgets.xmlviewer.annotation.TagPairMatcher;

/**
 * created by liusongbo on Sep 9, 2015
 *
 */
public class ElementFKInfoFormatViewer extends ProjectionViewer {

    public static final String TYPE = "org.eclipse.ui.workbench.texteditor.error"; //$NON-NLS-1$

    private final static String CURRENT_LINE = AbstractDecoratedTextEditorPreferenceConstants.EDITOR_CURRENT_LINE;

    private final static String CURRENT_LINE_COLOR = AbstractDecoratedTextEditorPreferenceConstants.EDITOR_CURRENT_LINE_COLOR;

    private final static String PRINT_MARGIN = AbstractDecoratedTextEditorPreferenceConstants.EDITOR_PRINT_MARGIN;

    private final static String PRINT_MARGIN_COLOR = AbstractDecoratedTextEditorPreferenceConstants.EDITOR_PRINT_MARGIN_COLOR;

    private final static String PRINT_MARGIN_COLUMN = AbstractDecoratedTextEditorPreferenceConstants.EDITOR_PRINT_MARGIN_COLUMN;

    private IOverviewRuler overviewRuler;

    private IDocument document;

    private ElementFKInfoAnnotaioner annotationer;

    private ProjectionAnnotationModel annotationModel;

    private ProjectionSupport projectionSupport;

    private SourceViewerDecorationSupport fSourceViewerDecorationSupport;

    private MarkerAnnotationPreferences fAnnotationPreferences;

    private ElementFKInfoConfiguration config;

    private IPropertyChangeListener propertyChangeListener;

    public ElementFKInfoFormatViewer(Composite parent, IVerticalRuler ruler, IOverviewRuler overviewRuler, boolean showsAnnotationOverview,
            int styles) {
        super(parent, ruler, overviewRuler, showsAnnotationOverview, styles);
        this.overviewRuler = overviewRuler;
    }

    public void initilize() {
        document = new Document();
        annotationModel = new ProjectionAnnotationModel();
        intallDocument(document);
        installProjectSupport();
        getSourceViewerDecorationSupport(this);
        installValidator();

        getTextWidget().setWordWrap(true);
    }

    private void intallDocument(IDocument doc) {
        IDocumentPartitioner partitioner = new FastPartitioner(new ElementFKInfotPartitionScanner(),
                new String[] { ElementFKInfotPartitionScanner.COMMON_STRING });
        partitioner.connect(doc);
        doc.setDocumentPartitioner(partitioner);

        setDocument(doc, annotationModel);
    }

    private void installProjectSupport() {
        projectionSupport = new ProjectionSupport(this, ElementFKInfoFormatHelper.getAnnotationAccess(),
                ElementFKInfoFormatHelper.getSharedColors());
        projectionSupport.addSummarizableAnnotationType("org.eclipse.ui.workbench.texteditor.error"); //$NON-NLS-1$

        projectionSupport.install();

        // turn projection mode on
        this.doOperation(ProjectionViewer.TOGGLE);
    }

    protected SourceViewerDecorationSupport getSourceViewerDecorationSupport(ISourceViewer viewer) {
        if (fSourceViewerDecorationSupport == null) {
            fSourceViewerDecorationSupport = new SourceViewerDecorationSupport(viewer, overviewRuler, XMLSourceViewerHelper
                    .getInstance().getAnnotationAccess(), XMLSourceViewerHelper.getInstance().getSharedColors());
            // patch see SourceViewerDecorationSupport
            AnnotationPreference info = new AnnotationPreference();
            info.setAnnotationType(TYPE);
            info.setTextPreferenceKey("errorIndication"); //$NON-NLS-1$
            info.setTextStylePreferenceKey("errorTextStyle"); //$NON-NLS-1$
            info.setColorPreferenceKey("errorIndicationColor"); //$NON-NLS-1$

            getPreferenceStore().setValue(info.getTextPreferenceKey(), true);
            getPreferenceStore().setValue(info.getTextStylePreferenceKey(), "PROBLEM_UNDERLINE"); //$NON-NLS-1$
            getPreferenceStore().setValue(info.getColorPreferenceKey(), "255, 0, 0"); //$NON-NLS-1$
            configureSourceViewerDecorationSupport(fSourceViewerDecorationSupport);

        }
        return fSourceViewerDecorationSupport;
    }

    protected void configureSourceViewerDecorationSupport(SourceViewerDecorationSupport support) {

        Iterator e = getAnnotationPreferences().getAnnotationPreferences().iterator();
        while (e.hasNext()) {
            support.setAnnotationPreference((AnnotationPreference) e.next());
        }

        support.setCursorLinePainterPreferenceKeys(CURRENT_LINE, CURRENT_LINE_COLOR);
        support.setMarginPainterPreferenceKeys(PRINT_MARGIN, PRINT_MARGIN_COLOR, PRINT_MARGIN_COLUMN);
        support.setSymbolicFontName(JFaceResources.TEXT_FONT);
        support.setCharacterPairMatcher(new TagPairMatcher());
        support.install(getPreferenceStore());
    }

    private MarkerAnnotationPreferences getAnnotationPreferences() {
        if (fAnnotationPreferences == null) {
            fAnnotationPreferences = new MarkerAnnotationPreferences();
            // force init
            fAnnotationPreferences.getAnnotationPreferences();
        }
        return fAnnotationPreferences;
    }

    private IPreferenceStore getPreferenceStore() {
        return MDMWorbenchPlugin.getDefault().getPreferenceStore();
    }

    private void installValidator() {
        IDocumentListener validateListener = new IDocumentListener() {

            public void documentChanged(DocumentEvent event) {
                Set<Annotation> updatedAnnotations = getAnnotationer().updateAnnotations(annotationModel, event);
                if (propertyChangeListener != null) {
                    propertyChangeListener.propertyChange(new PropertyChangeEvent("", "", null, updatedAnnotations.size() == 0)); //$NON-NLS-1$ //$NON-NLS-2$
                }
            }

            public void documentAboutToBeChanged(DocumentEvent event) {

            }
        };

        document.addDocumentListener(validateListener);
    }

    private ElementFKInfoAnnotaioner getAnnotationer() {
        if (annotationer == null) {
            annotationer = new ElementFKInfoAnnotaioner(annotationModel, document);
        }

        return annotationer;
    }

    @Override
    public void configure(SourceViewerConfiguration configuration) {
        super.configure(configuration);
        this.config = (ElementFKInfoConfiguration) configuration;
    }

    public Set<Annotation> setFkinfos(List<String> xPaths) {
        Set<Annotation> updatedAnnotations = getAnnotationer().setFKInfos(xPaths);
        config.setFKInfos(xPaths);

        return updatedAnnotations;
    }

    public void setFormatFKInfo(String formatFKInfo) {
        document.set(formatFKInfo);
    }

    public String getFormatFKInfo() {

        return document.get();
    }

    public void addPropertyChangeListener(IPropertyChangeListener propChangeListener) {
        this.propertyChangeListener = propChangeListener;
    }
}
