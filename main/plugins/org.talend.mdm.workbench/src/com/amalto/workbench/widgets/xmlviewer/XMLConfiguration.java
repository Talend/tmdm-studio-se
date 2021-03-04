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

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.formatter.ContentFormatter;
import org.eclipse.jface.text.formatter.IContentFormatter;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.DefaultAnnotationHover;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

import com.amalto.workbench.widgets.xmlviewer.contentassist.DataModelContentAssisProcessor;
import com.amalto.workbench.widgets.xmlviewer.contentassist.IKeyWordProvider;
import com.amalto.workbench.widgets.xmlviewer.format.XMLFormattingStrategy;
import com.amalto.workbench.widgets.xmlviewer.hover.TextHover;
import com.amalto.workbench.widgets.xmlviewer.partition.IXMLPartitions;
import com.amalto.workbench.widgets.xmlviewer.scanner.XMLPartitionScanner;
import com.amalto.workbench.widgets.xmlviewer.scanner.XMLScanner;
import com.amalto.workbench.widgets.xmlviewer.scanner.XMLTagScanner;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class XMLConfiguration extends SourceViewerConfiguration implements IXMLPartitions {

    private XMLTagScanner tagScanner;

    private XMLScanner scanner;

    private ContentAssistant contentAssist;

    private final IKeyWordProvider keyWordProvider;


    /**
     * DOC hbhong XMLEditorConfiguration constructor comment.
     */
    public XMLConfiguration(IKeyWordProvider keyWordProvider) {
        this.keyWordProvider = keyWordProvider;
    }

    public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {

        PresentationReconciler reconciler = new PresentationReconciler();
        DefaultDamagerRepairer dr = new DefaultDamagerRepairer(getXMLTagScanner());
        reconciler.setDamager(dr, IXMLPartitions.XML_TAG);
        reconciler.setRepairer(dr, IXMLPartitions.XML_TAG);
        reconciler.setDamager(dr, IXMLPartitions.XML_INCOMPLETETAG);
        reconciler.setDamager(dr, IXMLPartitions.XML_ENDTAG);
        reconciler.setRepairer(dr, IXMLPartitions.XML_ENDTAG);
        reconciler.setDamager(dr, IXMLPartitions.XML_EMPTYTAG);
        reconciler.setRepairer(dr, IXMLPartitions.XML_EMPTYTAG);
        dr = new DefaultDamagerRepairer(getXMLScanner());
        reconciler.setDamager(dr, IXMLPartitions.XML_TEXT);
        reconciler.setRepairer(dr, IXMLPartitions.XML_TEXT);
        NonRuleBasedDamagerRepairer ndr = new NonRuleBasedDamagerRepairer(new TextAttribute(IXMLColorConstants.XML_COMMENT, null,
                0));
        reconciler.setDamager(ndr, IXMLPartitions.XML_COMMENT);
        reconciler.setRepairer(ndr, IXMLPartitions.XML_COMMENT);
        NonRuleBasedDamagerRepairer ndr2 = new NonRuleBasedDamagerRepairer(new TextAttribute(IXMLColorConstants.DECLARATION,
                null, 0));
        reconciler.setDamager(ndr2, IXMLPartitions.XML_DECLARATION);
        reconciler.setRepairer(ndr2, IXMLPartitions.XML_DECLARATION);
        NonRuleBasedDamagerRepairer ndr3 = new NonRuleBasedDamagerRepairer(new TextAttribute(IXMLColorConstants.PROC_INSTR, null,
                0));
        reconciler.setDamager(ndr3, IXMLPartitions.XML_PI);
        reconciler.setRepairer(ndr3, IXMLPartitions.XML_PI);
        NonRuleBasedDamagerRepairer ndr4 = new NonRuleBasedDamagerRepairer(new TextAttribute(IXMLColorConstants.CDATA, null, 0));
        reconciler.setDamager(ndr4, IXMLPartitions.XML_CDATA);
        reconciler.setRepairer(ndr4, IXMLPartitions.XML_CDATA);
        return reconciler;

    }

    protected XMLTagScanner getXMLTagScanner() {
        if (tagScanner == null) {
            tagScanner = new XMLTagScanner();
            tagScanner.setDefaultReturnToken(new Token(new TextAttribute(IXMLColorConstants.TAG)));
        }
        return tagScanner;
    }

    public IAnnotationHover getAnnotationHover(ISourceViewer sourceViewer) {
        return new DefaultAnnotationHover(true);
    }

    @Override
    public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType) {
        return new TextHover(sourceViewer);
    }

    protected XMLScanner getXMLScanner() {
        if (scanner == null) {
            scanner = new XMLScanner();
            scanner.setDefaultReturnToken(new Token(new TextAttribute(IXMLColorConstants.DEFAULT)));
        }
        return scanner;
    }

    public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
        return XMLPartitionScanner.CONTENT_TYPES;
    }

    public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
        if (this.contentAssist == null) {
            this.contentAssist = new ContentAssistant();

            this.addContentAssistProcessors(this.contentAssist);

            this.contentAssist.setProposalPopupOrientation(20);
            this.contentAssist.setContextInformationPopupOrientation(10);
            this.contentAssist.setInformationControlCreator(getInformationControlCreator(sourceViewer));
            this.contentAssist.enableAutoActivation(true);
        }
        return this.contentAssist;
    }

    private void addContentAssistProcessors(ContentAssistant assist) {
        DataModelContentAssisProcessor processor = new DataModelContentAssisProcessor(keyWordProvider);

        assist.setContentAssistProcessor(processor, IXMLPartitions.XML_TAG);
        assist.setContentAssistProcessor(processor, IXMLPartitions.XML_EMPTYTAG);
        assist.setContentAssistProcessor(processor, IXMLPartitions.XML_INCOMPLETETAG);
        assist.setContentAssistProcessor(processor, IDocument.DEFAULT_CONTENT_TYPE);
        assist.setContentAssistProcessor(processor, IXMLPartitions.XML_TEXT);
    }

    @Override
    public IContentFormatter getContentFormatter(ISourceViewer sourceViewer) {
        ContentFormatter formatter = new ContentFormatter();
        formatter.enablePartitionAwareFormatting(false);
        formatter.setFormattingStrategy(new XMLFormattingStrategy(sourceViewer), IDocument.DEFAULT_CONTENT_TYPE);
        return formatter;
    }
}
