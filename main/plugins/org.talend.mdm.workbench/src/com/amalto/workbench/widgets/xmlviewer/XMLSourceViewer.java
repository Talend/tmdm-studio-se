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
package com.amalto.workbench.widgets.xmlviewer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionSupport;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerActivation;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.AnnotationPreference;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.texteditor.MarkerAnnotationPreferences;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;

import com.amalto.workbench.MDMWorbenchPlugin;
import com.amalto.workbench.widgets.xmlviewer.action.IXMLActionDefinitionIds;
import com.amalto.workbench.widgets.xmlviewer.action.TextViewerOperationHandler;
import com.amalto.workbench.widgets.xmlviewer.annotation.TagPairMatcher;
import com.amalto.workbench.widgets.xmlviewer.model.XMLTreeModel;
import com.amalto.workbench.widgets.xmlviewer.model.tags.XMLTagDefinitionProvider;
import com.amalto.workbench.widgets.xmlviewer.model.tags.nodef.NoDefTagDefinitionProvider;
import com.amalto.workbench.widgets.xmlviewer.model.visitor.FoldingNodesVisitor;
import com.amalto.workbench.widgets.xmlviewer.partition.XMLDocumentPartitioner;
import com.amalto.workbench.widgets.xmlviewer.resources.DocumentStructureDeclaration;
import com.amalto.workbench.widgets.xmlviewer.scanner.XMLPartitionScanner;
import com.amalto.workbench.widgets.xmlviewer.validator.XMLValidator;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class XMLSourceViewer extends ProjectionViewer {

    /**
     * Preference key for showing the line number ruler.
     */
    private final static String LINE_NUMBER_RULER = AbstractDecoratedTextEditorPreferenceConstants.EDITOR_LINE_NUMBER_RULER;

    /**
     * Preference key for showing the overview ruler.
     */
    private final static String OVERVIEW_RULER = AbstractDecoratedTextEditorPreferenceConstants.EDITOR_OVERVIEW_RULER;

    /**
     * Preference key for highlighting current line.
     */
    private final static String CURRENT_LINE = AbstractDecoratedTextEditorPreferenceConstants.EDITOR_CURRENT_LINE;

    /**
     * Preference key for highlight color of current line.
     */
    private final static String CURRENT_LINE_COLOR = AbstractDecoratedTextEditorPreferenceConstants.EDITOR_CURRENT_LINE_COLOR;

    /**
     * Preference key for showing print margin ruler.
     */
    private final static String PRINT_MARGIN = AbstractDecoratedTextEditorPreferenceConstants.EDITOR_PRINT_MARGIN;

    /**
     * Preference key for print margin ruler color.
     */
    private final static String PRINT_MARGIN_COLOR = AbstractDecoratedTextEditorPreferenceConstants.EDITOR_PRINT_MARGIN_COLOR;

    /**
     * Preference key for print margin ruler column.
     */
    private final static String PRINT_MARGIN_COLUMN = AbstractDecoratedTextEditorPreferenceConstants.EDITOR_PRINT_MARGIN_COLUMN;

    public static final String ID = "testxmleditor.XMLView"; //$NON-NLS-1$

    private IOverviewRuler fOverviewRuler;

    // private IVerticalRuler fVerticalRuler;

    private MarkerAnnotationPreferences fAnnotationPreferences;

    private SourceViewerDecorationSupport fSourceViewerDecorationSupport;

    private ProjectionAnnotationModel annotationModel;

    private XMLTreeModel treeModel;

    private ProjectionSupport projectionSupport;

    // shared command handlers
    static Map<String, IHandler> handlers = new HashMap<String, IHandler>();

    // handler service
    IHandlerService handlerService = (IHandlerService) PlatformUI.getWorkbench().getService(IHandlerService.class);

    // command handler activations
    Map<IHandler, IHandlerActivation> handlerActivations = new HashMap<IHandler, IHandlerActivation>();

    /**
     * DOC hbhong XMLSourceViewer constructor comment.
     *
     * @param parent
     * @param ruler
     * @param overviewRuler
     * @param showsAnnotationOverview
     * @param styles
     */
    public XMLSourceViewer(Composite parent, IVerticalRuler ruler, IOverviewRuler overviewRuler, boolean showsAnnotationOverview,
            int styles) {
        super(parent, ruler, overviewRuler, showsAnnotationOverview, styles);
        this.fOverviewRuler = overviewRuler;

    }

    private MarkerAnnotationPreferences getAnnotationPreferences() {
        if (fAnnotationPreferences == null) {
            fAnnotationPreferences = new MarkerAnnotationPreferences();
            // force init
            fAnnotationPreferences.getAnnotationPreferences();
        }
        return fAnnotationPreferences;
    }

    public void initilize() {
        document = getNewDocument();
        initAnnotationModel();
        intallDocument(document);
        installProjectSupport();
        getSourceViewerDecorationSupport(this);
        // IColumnSupport columnSupport = (IColumnSupport) getAdapter(IColumnSupport.class);
        // RulerColumnDescriptor lineNumberColumnDescriptor = RulerColumnRegistry.getDefault().getColumnDescriptor(
        // LineNumberColumn.ID);
        // if (lineNumberColumnDescriptor != null)
        // columnSupport.setColumnVisible(lineNumberColumnDescriptor, true);
        installValidator();
        createHandlers();
        updateFont();

        //
        setEditable(true);
    }

    /**
     * DOC hbhong Comment method "updateFont".
     */
    private void updateFont() {
        getTextWidget().setFont(JFaceResources.getFont(JFaceResources.TEXT_FONT));

    }

    /**
     * DOC hbhong Comment method "installProjectSupport".
     */
    private void installProjectSupport() {
        XMLSourceViewerHelper helper = XMLSourceViewerHelper.getInstance();

        projectionSupport = new ProjectionSupport(this, helper.getAnnotationAccess(), helper.getSharedColors());
        projectionSupport.addSummarizableAnnotationType("org.eclipse.ui.workbench.texteditor.error"); //$NON-NLS-1$

        projectionSupport.install();

        // turn projection mode on
        this.doOperation(ProjectionViewer.TOGGLE);
    }

    private void installValidator() {
        if (document != null) {
            final XMLValidator validator = new XMLValidator(document, annotationModel);
            IDocumentListener validateListener = new IDocumentListener() {

                public void documentChanged(DocumentEvent event) {
                    validator.validate(document);

                }

                public void documentAboutToBeChanged(DocumentEvent event) {

                }
            };

            document.addDocumentListener(validateListener);
        }
    }

    private void initAnnotationModel() {
        annotationModel = new ProjectionAnnotationModel();
    }

    private IDocument getNewDocument() {
        final IDocument document = new Document();

        return document;
    }

    private void intallDocument(IDocument document) {
        if (document != null) {
            IDocumentPartitioner partitioner = new XMLDocumentPartitioner(new XMLPartitionScanner(),
                    XMLPartitionScanner.CONTENT_TYPES);
            partitioner.connect(document);
            document.setDocumentPartitioner(partitioner);
            treeModel = new XMLTreeModel(this);
            treeModel.createTree(document);
            document.addDocumentListener(treeModel);
        }
        setDocument(document, annotationModel);
    }

    public XMLTreeModel getTreeModel() {
        return this.treeModel;
    }

    protected IOverviewRuler getOverviewRuler() {

        return fOverviewRuler;
    }

    protected SourceViewerDecorationSupport getSourceViewerDecorationSupport(ISourceViewer viewer) {
        if (fSourceViewerDecorationSupport == null) {
            fSourceViewerDecorationSupport = new SourceViewerDecorationSupport(viewer, getOverviewRuler(), XMLSourceViewerHelper
                    .getInstance().getAnnotationAccess(), XMLSourceViewerHelper.getInstance().getSharedColors());
            // patch see SourceViewerDecorationSupport
            AnnotationPreference info = new AnnotationPreference();
            info.setAnnotationType(XMLValidator.TYPE);
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
            // support.setAnnotationPainterPreferenceKeys(DefaultMarkerAnnotationAccess.UNKNOWN,
            // UNKNOWN_INDICATION_COLOR,
            // UNKNOWN_INDICATION, UNKNOWN_INDICATION_IN_OVERVIEW_RULER, 0);
        }

        support.setCursorLinePainterPreferenceKeys(CURRENT_LINE, CURRENT_LINE_COLOR);
        support.setMarginPainterPreferenceKeys(PRINT_MARGIN, PRINT_MARGIN_COLOR, PRINT_MARGIN_COLUMN);
        support.setSymbolicFontName(getFontPropertyPreferenceKey());
        support.setCharacterPairMatcher(new TagPairMatcher());
        support.install(getPreferenceStore());
    }

    private IPreferenceStore getPreferenceStore() {
        return MDMWorbenchPlugin.getDefault().getPreferenceStore();
    }

    /*
     * Create command handlers
     */
    protected void createHandlers() {
        // content assist
        IHandler handler = new TextViewerOperationHandler(this, ISourceViewer.CONTENTASSIST_PROPOSALS);
        handlers.put(ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS, handler);

        // quick assist
        handler = new TextViewerOperationHandler(this, ISourceViewer.QUICK_ASSIST);
        handlers.put(ITextEditorActionDefinitionIds.QUICK_ASSIST, handler);

        // text formatting
        handler = new TextViewerOperationHandler(this, ISourceViewer.FORMAT);
        handlers.put(IXMLActionDefinitionIds.FORMAT, handler);

        // activate handlers
        activateHandlers();
    }

    /**
     * Activate all handlers
     */
    protected void activateHandlers() {
        // if handler service is null, return
        if (handlerService == null) {
            return;
        }

        // activate handlers if it is not active
        Iterator<String> i = handlers.keySet().iterator();
        while (i.hasNext()) {
            String id = i.next();
            IHandler handler = handlers.get(id);
            IHandlerActivation activation = handlerActivations.get(handler);
            if (activation == null) {
                activation = handlerService.activateHandler(id, handler);
                handlerActivations.put(handler, activation);
            }
        }
    }

    @Override
    protected void handleDispose() {
        if (handlerService != null) {
            for (IHandlerActivation activation : handlerActivations.values()) {
                handlerService.deactivateHandler(activation);
            }
        }

        super.handleDispose();
    }

    public XMLTagDefinitionProvider getTagContainersRegistry() {
        try {

            Collection<DocumentStructureDeclaration> schemaDefinitions = treeModel.getSchemaDefinitions();
            DocumentStructureDeclaration structureDeclaration = treeModel.getDTDDefinition();

            XMLTagDefinitionProvider registry = this.containersRegistry;

            return registry;
        } catch (Exception exception) {
            return this.containersRegistry;
        }
    }

    private NoDefTagDefinitionProvider containersRegistry = new NoDefTagDefinitionProvider();

    private Annotation[] oldAnnotations;

    private IDocument document;

    private IConfigurationElement configElement;

    public NoDefTagDefinitionProvider getCodeTagContainersRegistry() {
        return this.containersRegistry;
    }

    public void updateFoldingStructure() {
        FoldingNodesVisitor visitor = new FoldingNodesVisitor(document);
        treeModel.getTree().accept(visitor);

        HashMap<ProjectionAnnotation, Position> newAnnotations = visitor.getAnnotationsMap();
        Annotation[] annotations = visitor.getAnnotations();

        if (annotationModel != null) {
            annotationModel.modifyAnnotations(oldAnnotations, newAnnotations, null);
        }
        oldAnnotations = annotations;
    }

    //
    protected final String getFontPropertyPreferenceKey() {
        String symbolicFontName = getSymbolicFontName();
        if (symbolicFontName != null) {
            return symbolicFontName;
        }
        return JFaceResources.TEXT_FONT;
    }

    protected IConfigurationElement getConfigurationElement() {
        return configElement;
    }

    private String getSymbolicFontName() {
        if (getConfigurationElement() != null) {
            return getConfigurationElement().getAttribute("symbolicFontName"); //$NON-NLS-1$
        }
        return null;
    }

    public void setText(String text) {
        document.set(text);
    }

    public String getText() {
        return document.get();
    }
}
