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
package com.amalto.workbench.proposal;

import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.ParseException;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.IControlContentAdapter;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;


/**
 * Utilities for proposals. <br/>
 *
 * $Id: ProposalUtils.java 7038 2007-11-15 14:05:48Z plegall $
 *
 */
public final class ProposalUtils {

    /**
     * Constructs a new ProposalUtils.
     */
    private ProposalUtils() {
    }

    public static ContentProposalAdapterExtended getCommonProposal(Control control, IContentProposalProvider proposalProvider,char[] autoActivationCharacters) {
        IControlContentAdapter controlContentAdapter = null;
        if (control instanceof Text) {
            controlContentAdapter = new TextContentAdapterExtended();
        } else if (control instanceof StyledText) {
            controlContentAdapter = new StyledTextContentAdapterExtended();
        } else {
            throw new IllegalArgumentException("Invalid argument :" + control.getClass()); //$NON-NLS-1$
        }
        final ContentProposalAdapterExtended contentProposalAdapter = getContentProposalAdapter(control, controlContentAdapter,
                proposalProvider,autoActivationCharacters);

        // to don't write Carriage Return when accept a proposal
        if (control instanceof StyledText) {
            final VerifyKeyListener verifyKeyListener = new VerifyKeyListener() {

                public void verifyKey(VerifyEvent verifyEvent) {
                    if (verifyEvent.character == '\r' && contentProposalAdapter.isProposalOpened()) {
                        verifyEvent.doit = false;
                    } else {
                        verifyEvent.doit = true;
                    }
                }
            };
            ((StyledText) control).addVerifyKeyListener(verifyKeyListener);
        }

        return contentProposalAdapter;
    }


    private static ContentProposalAdapterExtended getContentProposalAdapter(Control control,
            IControlContentAdapter controlContentAdapter, IContentProposalProvider proposalProvider,char[] autoActivationCharacters) {
        ContentProposalAdapterExtended contentProposalAdapter = null;
        try {
            KeyStroke keyStroke = KeyStroke.getInstance("Ctrl+Space"); //$NON-NLS-1$

            contentProposalAdapter = new ContentProposalAdapterExtended(control, controlContentAdapter, proposalProvider,
                    keyStroke, autoActivationCharacters);
            contentProposalAdapter.setPropagateKeys(true);
            contentProposalAdapter.setFilterStyle(ContentProposalAdapterExtended.FILTER_CUMULATIVE_ALL_START_WORDS);
            contentProposalAdapter.setProposalAcceptanceStyle(ContentProposalAdapterExtended.PROPOSAL_INSERT);
        } catch (ParseException pe) {
            throw new RuntimeException(pe);
        }
        return contentProposalAdapter;
    }

    public static ContentProposalAdapterExtended getCommonProposal(Control control) {
        return getCommonProposal(control, null,null);
    }



}
