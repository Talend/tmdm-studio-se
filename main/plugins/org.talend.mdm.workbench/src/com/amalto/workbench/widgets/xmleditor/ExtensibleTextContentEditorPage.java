// ============================================================================
//
// Copyright (C) 2006-2019 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.workbench.widgets.xmleditor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.VerticalRuler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.models.TreeObjectTransfer;
import com.amalto.workbench.utils.WidgetUtils;
import com.amalto.workbench.widgets.xmleditor.infoholder.ExternalInfoHolder;

public class ExtensibleTextContentEditorPage extends ExtensibleContentEditorPage {

    private static Log log = LogFactory.getLog(ExtensibleTextContentEditorPage.class);

    protected TextViewer textViewer;

    public ExtensibleTextContentEditorPage(Composite parent, int style) {
        super(parent, style);
        setLayout(new FillLayout());

        textViewer = new SourceViewer(this, new VerticalRuler(10), SWT.H_SCROLL | SWT.V_SCROLL);

        initUIListeners();
    }

    @Override
    public void refresh() {

        if (getCurrentContent().equals(getContent().getMaskContent())) {
            return;
        }

        textViewer.setDocument(new Document(getContent().getMaskContent()));
    }

    @Override
    public void saveContentFromUI() throws ExtensibleContentEditorPageSaveException {
        getContent().setContent(getCurrentContent());
    }

    @Override
    public void clearExternalResources() {

        if (textViewer.getUndoManager() != null) {
            textViewer.getUndoManager().disconnect();
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        clearExternalResources();
    }

    protected void initUIListeners() {

        WidgetUtils.initRedoUndo(textViewer);

        textViewer.addTextListener(creatTextListener());
        textViewer.getTextWidget().addKeyListener(createKeyListener());
        createCompDropTarget();
    }

    protected void createCompDropTarget() {
        DropTarget dropTarget = new DropTarget(textViewer.getTextWidget(), DND.DROP_MOVE | DND.DROP_LINK);
        dropTarget.setTransfer(new TreeObjectTransfer[] { TreeObjectTransfer.getInstance() });
        dropTarget.addDropListener(new DropTargetAdapter() {

            @Override
            public void dragEnter(DropTargetEvent event) {
            }

            @Override
            public void dragLeave(DropTargetEvent event) {
            }

            @Override
            public void dragOver(DropTargetEvent event) {
                event.feedback |= DND.FEEDBACK_EXPAND | DND.FEEDBACK_SCROLL;
            }

            @Override
            public void drop(DropTargetEvent event) {

                if (event.data instanceof TreeObject[]) {
                    if (((TreeObject[]) event.data)[0].getType() == TreeObject.TRANSFORMER) {
                        textViewer.getTextWidget().setText(textViewer.getTextWidget().getText().replace("?", "")//$NON-NLS-1$//$NON-NLS-2$
                                + ((TreeObject[]) event.data)[0].getDisplayName());
                    } else {
                        textViewer.getTextWidget().setText(
                                textViewer.getTextWidget().getText() + ((TreeObject[]) event.data)[0].getDisplayName());
                    }
                }

            }
        });

    }

    protected ITextListener creatTextListener() {
        return new ITextListener() {

            public void textChanged(TextEvent event) {
                getContent().setContent(getCurrentContent());
                notifyOnXMLDocumentChanged();
            }
        };
    }

    protected KeyListener createKeyListener() {

        return new KeyListener() {

            public void keyReleased(KeyEvent event) {
                if (textViewer.getDocument() == null) {
                    return;
                }
                int start = textViewer.getSelectedRange().x;
                int end = textViewer.getSelectedRange().y;
                int length = textViewer.getDocument().get().length();

                if (event.stateMask != SWT.CTRL || event.keyCode != 17) {
                    return;
                }

                // try {
                // ResourceSelectDialog dialog = new ResourceSelectDialog(getShell(), null,
                // Messages.ExtensibleTextXX_DialogTitle, ServerView
                // .show().getSite());
                // dialog.setBlockOnOpen(true);
                // dialog.open();
                // if (dialog.getReturnCode() == Window.OK) {
                // String xpath = dialog.getXpath();
                // String textHead = textViewer.getDocument().get(0, start);
                // String textEnd = textViewer.getDocument().get(start + end, length - start - end);
                // textViewer.setDocument(new Document(textHead + xpath + textEnd));
                // textViewer.setSelectedRange(start, xpath.length());
                //
                // getContent().setContent(getCurrentContent());
                // notifyOnXMLDocumentChanged();
                // }
                // } catch (BadLocationException e) {
                // log.error(e.getMessage(), e);
                // }
            }

            public void keyPressed(KeyEvent event) {
            }

        };

    }

    private String getCurrentContent() {

        if (textViewer.getDocument() == null || textViewer.getDocument().get() == null) {
            return "";//$NON-NLS-1$
        }

        return textViewer.getDocument().get().trim();
    }

    @Override
    public void setExternalInfoHolder(ExternalInfoHolder<?> externalInfoHolder) {
    }

    @Override
    public void reloadExternalInfo() {
    }

}
