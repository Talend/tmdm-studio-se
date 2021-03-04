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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.DefaultTextHover;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWhitespaceDetector;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.jface.text.source.DefaultAnnotationHover;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

import com.amalto.workbench.MDMWorbenchPlugin;

public class ElementFKInfoConfiguration extends SourceViewerConfiguration {

    public static final String PREF_COLOR_DEFAULT = "colorDefault"; //$NON-NLS-1$

    public static final String PREF_COLOR_STRING = "colorString"; //$NON-NLS-1$

    public static final String PREF_COLOR_KEYWORD = "colorKeyword"; //$NON-NLS-1$

    private static ElementFKInfoColorProvider ecolorProvider;

    private List<String> KEYWORDS = new ArrayList<String>();

    private KeywordPartitionScanner keywordPartitionScanner;

    @Override
    public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
        return new String[] { IDocument.DEFAULT_CONTENT_TYPE, ElementFKInfotPartitionScanner.COMMON_STRING };
    }

    @Override
    public IAnnotationHover getAnnotationHover(ISourceViewer sourceViewer) {
        return new DefaultAnnotationHover(true);
    }

    @Override
    public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType) {
        return new DefaultTextHover(sourceViewer);
    }

    @Override
    public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {

        PresentationReconciler reconciler = new PresentationReconciler();

        DefaultDamagerRepairer stringDR = new DefaultDamagerRepairer(getStringScanner());
        reconciler.setDamager(stringDR, ElementFKInfotPartitionScanner.COMMON_STRING);
        reconciler.setRepairer(stringDR, ElementFKInfotPartitionScanner.COMMON_STRING);

        DefaultDamagerRepairer keywordDR = new DefaultDamagerRepairer(getKeywordScanner());
        reconciler.setDamager(keywordDR, IDocument.DEFAULT_CONTENT_TYPE);
        reconciler.setRepairer(keywordDR, IDocument.DEFAULT_CONTENT_TYPE);

        return reconciler;
    }

    private RuleBasedScanner getStringScanner() {
        RuleBasedScanner scanner = new RuleBasedScanner();
        ElementFKInfoColorProvider colorProvider = getEditorColorProvider();
        scanner.setDefaultReturnToken(colorProvider.getToken(PREF_COLOR_STRING));
        return scanner;
    }

    private KeywordPartitionScanner getKeywordScanner() {
        if (keywordPartitionScanner == null) {
            keywordPartitionScanner = new KeywordPartitionScanner();
        }
        return keywordPartitionScanner;
    }

    public void setFKInfos(List<String> xpaths) {
        this.KEYWORDS = xpaths;
        getKeywordScanner().initRules();
    }

    private static ElementFKInfoColorProvider getEditorColorProvider() {
        if (ecolorProvider == null) {
            ecolorProvider = new ElementFKInfoColorProvider(MDMWorbenchPlugin.getDefault().getPreferenceStore());
        }

        return ecolorProvider;
    }

    public class KeywordPartitionScanner extends RuleBasedScanner {

        public KeywordPartitionScanner() {
            initRules();
        }

        public void initRules() {
            IToken keyword = getEditorColorProvider().getToken(PREF_COLOR_KEYWORD);
            IToken other = getEditorColorProvider().getToken(PREF_COLOR_DEFAULT);

            WordRule wordRule = new WordRule(new IWordDetector() {

                public boolean isWordPart(char c) {
                    if (c == '/') {
                        return true;
                    }
                    return Character.isJavaIdentifierPart(c);
                }

                public boolean isWordStart(char c) {
                    if (c == '/') {
                        return true;
                    }
                    return Character.isJavaIdentifierStart(c);
                }
            }, other);
            for (int i = 0; i < KEYWORDS.size(); i++) {
                wordRule.addWord(KEYWORDS.get(i), keyword);
                wordRule.addWord(KEYWORDS.get(i).toUpperCase(), keyword);
            }
            IRule[] rules = new IRule[2];
            rules[0] = wordRule;
            rules[1] = new WhitespaceRule(new IWhitespaceDetector() {

                public boolean isWhitespace(char character) {
                    return Character.isWhitespace(character);
                }
            });

            setRules(rules);
        }

    }
}