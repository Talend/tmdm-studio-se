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
package com.amalto.workbench.widgets.xmlviewer.hover;

import java.util.Iterator;

import org.eclipse.jface.text.DefaultTextHover;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.ISourceViewerExtension2;


/**
 * DOC hbhong  class global comment. Detailled comment
 */
public class TextHover extends DefaultTextHover {

    private final ISourceViewer sourceViewer;
    /**
     * DOC hbhong TextHover constructor comment.
     *
     * @param sourceViewer
     */
    public TextHover(ISourceViewer sourceViewer) {
        super(sourceViewer);
        this.sourceViewer = sourceViewer;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.text.ITextHover#getHoverInfo(org.eclipse.jface.text.ITextViewer, org.eclipse.jface.text.IRegion)
     */
    @Override
    public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
        String annotationMessage = getAnnotationMessage(textViewer, hoverRegion);
        if (annotationMessage != null) {
            return annotationMessage;
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.text.ITextHover#getHoverRegion(org.eclipse.jface.text.ITextViewer, int)
     */
    @Override
    public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
        return super.getHoverRegion(textViewer, offset);
    }

    private String getAnnotationMessage(ITextViewer textViewer, IRegion hoverRegion) {
        IAnnotationModel model = getAnnotationModel(sourceViewer);
        if (model == null) {
            return null;
        }

        Iterator e = model.getAnnotationIterator();
        while (e.hasNext()) {
            Annotation a = (Annotation) e.next();
            if (isIncluded(a)) {
                Position p = model.getPosition(a);
                if (p != null && p.overlapsWith(hoverRegion.getOffset(), hoverRegion.getLength())) {
                    String msg = a.getText();
                    return msg;
                    }
                }

        }

        return null;
    }

    private IAnnotationModel getAnnotationModel(ISourceViewer viewer) {
        if (viewer instanceof ISourceViewerExtension2) {
            ISourceViewerExtension2 extension = (ISourceViewerExtension2) viewer;
            return extension.getVisualAnnotationModel();
        }
        return viewer.getAnnotationModel();
    }
}
