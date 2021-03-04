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

import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.TextViewerUndoManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import com.amalto.workbench.proposal.ContentProposalAdapterExtended;
import com.amalto.workbench.proposal.ProposalUtils;

/**
 *
 * @author aiming
 *
 */
public class WidgetUtils {

    public static void initRedoUndo(final TextViewer viewer) {
        TextViewerUndoManager undoManager = new TextViewerUndoManager(20);
        undoManager.connect(viewer);
        viewer.setUndoManager(undoManager);
        StyledText styledText = viewer.getTextWidget();
        styledText.addKeyListener(new KeyListener() {

            public void keyPressed(KeyEvent e) {
            }

            private boolean isUndoKeyPress(KeyEvent e) {
                // CTRL + z
                return (e.keyCode == 'z' && e.stateMask == SWT.CTRL);
            }

            private boolean isRedoKeyPress(KeyEvent e) {
                // CTRL + y
                return (e.keyCode == 'y' && e.stateMask == SWT.CTRL);
            }

            public void keyReleased(KeyEvent e) {
                if (isUndoKeyPress(e)) {
                    viewer.doOperation(ITextOperationTarget.UNDO);
                } else if (isRedoKeyPress(e)) {
                    viewer.doOperation(ITextOperationTarget.REDO);
                }
            }
        });
        // undoManager.getUndoContext();

    }

    public static void enable(Composite parent, boolean enabled) {
        parent.setEnabled(enabled);
        for (Control control : parent.getChildren()) {
            if (control instanceof Text || control instanceof CCombo || control instanceof Combo || control instanceof Button
                    || control instanceof StyledText) {
                control.setEnabled(enabled);
                if (control instanceof StyledText) {
                    if (!enabled)
                        control.setBackground(control.getParent().getBackground());
                    else
                        control.setBackground(control.getDisplay().getSystemColor(SWT.COLOR_WHITE));
                }
            }
            if (control instanceof Composite) {
                enable((Composite) control, enabled);
            }
        }

    }

    public static void changeWidgetColor(Control cotrl, Color color, boolean[] backForeGround) {
        if (backForeGround == null || backForeGround.length != 2)
            return;

        if (backForeGround[0])
            cotrl.setBackground(color);
        if (backForeGround[1])
            cotrl.setForeground(color);

        if (cotrl instanceof Composite) {
            Composite comp = (Composite) cotrl;
            Control[] children = comp.getChildren();
            for (Control ch : children) {
                changeWidgetColor(ch, color, backForeGround);
            }
        }
    }

    public static ContentProposalAdapterExtended addContentProposal(Control control, String[] proposals,
            char[] autoActivationCharacters) {
        // add content proposal

        ContentProposalAdapterExtended adapter = ProposalUtils.getCommonProposal(control, new SimpleContentProposalProvider(
                proposals), autoActivationCharacters);
        // ContentProposalAdapter adapter = new ContentProposalAdapter(control,new TextContentAdapter(), new
        // SimpleContentProposalProvider(proposals), keyStroke,
        // autoActivationCharacters);
        return adapter;
    }
}
