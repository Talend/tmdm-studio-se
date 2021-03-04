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
package com.amalto.workbench.widgets.xmlviewer.contentassist;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class DataModelContentAssisProcessor implements IContentAssistProcessor {

    /**
     *
     */
    private static final char START = '<';

    private final IKeyWordProvider keyWordProvider;

    public DataModelContentAssisProcessor(IKeyWordProvider keyWordProvider) {
        this.keyWordProvider = keyWordProvider;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.jface.text.contentassist.IContentAssistProcessor#computeCompletionProposals(org.eclipse.jface.text
     * .ITextViewer, int)
     */
    public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
        List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
        if (keyWordProvider != null) {

            IDocument document = viewer.getDocument();
            int start = getInputPos(document, offset);
            String input = null;
            if (start > 0) {
            try {
                input = document.get(start, offset - start);
            } catch (BadLocationException e) {
                // Nothing need report
                }
            }
            if (input != null) {
                String[] keyWords = keyWordProvider.getCurrentKeyWords();
                int len = offset - start;
                if (keyWords != null) {
                    boolean showAll = input.length() == 0;
                    for (int i = 0; i < keyWords.length; i++) {
                        String entityName = keyWords[i];
                        if (showAll || entityName.toLowerCase().startsWith(input)) {
                            String replacementString = START + entityName + "></" + entityName + ">"; //$NON-NLS-1$ //$NON-NLS-2$
                            CompletionProposal proposal = new CompletionProposal(replacementString, offset - len - 1, len + 1,
                                    entityName.length() + 2);
                            proposals.add(proposal);
                        }
                    }
                }
            }
        }
        return proposals.toArray(new ICompletionProposal[0]);
    }

    private int getInputPos(IDocument doc, int offset) {
        int cur = offset;
        try {
            do {
                char c = doc.getChar(cur);
                if (c == START) {
                    break;
                }
                cur--;
            } while (cur > 0);
            if (offset > cur) {
                return cur + 1;
            }
        } catch (BadLocationException e) {
        }
        return -1;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.jface.text.contentassist.IContentAssistProcessor#computeContextInformation(org.eclipse.jface.text
     * .ITextViewer, int)
     */
    public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset) {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getCompletionProposalAutoActivationCharacters()
     */
    public char[] getCompletionProposalAutoActivationCharacters() {
        return new char[] { START };
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getContextInformationAutoActivationCharacters()
     */
    public char[] getContextInformationAutoActivationCharacters() {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getErrorMessage()
     */
    public String getErrorMessage() {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getContextInformationValidator()
     */
    public IContextInformationValidator getContextInformationValidator() {
        return null;
    }

}
