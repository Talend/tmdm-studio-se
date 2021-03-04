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
package com.amalto.workbench.actions;

/**
 * All actions need to support Undo/Redo(DataModelMainPage) must subclass this one
 */

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xsd.XSDSchema;
import org.w3c.dom.Document;

import com.amalto.workbench.editors.DataModelMainPage;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.models.TreeParent;
import com.amalto.workbench.providers.ISchemaContentProvider;
import com.amalto.workbench.utils.Util;

public class UndoAction extends Action {

    private static Log log = LogFactory.getLog(UndoAction.class);

    protected DataModelMainPage page;

    protected XSDSchema schema = null;

    private Map<Integer, String> undoActionTrack = null;

    private Map<Integer, String> redoActionTrack = null;

    private boolean omitTrack = false;

    public UndoAction(DataModelMainPage page) {
        this.page = page;
        undoActionTrack = page.getUndoActionTrack();
        redoActionTrack = page.getRedoActionTrack();
    }

    public class XsdUndoableOperation extends AbstractOperation {

        public XsdUndoableOperation(String label) {
            super(label);
        }

        @Override
        public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {

            return UndoAction.this.execute();
        }

        @Override
        public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            return UndoAction.this.redo();
        }

        @Override
        public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            return UndoAction.this.undo();
        }
    };

    @Override
    public void run() {

        operation.addContext(page.getUndoContext());
        try {
            IStatus status = getOperationHistory().execute(operation, null, null);
        } catch (ExecutionException e) {
            log.error(e.getMessage(), e);
        }

    }

    public void run(Object toDel) {
    }

    protected XsdUndoableOperation operation = new XsdUndoableOperation("");//$NON-NLS-1$

    /**
     * need override by subclass
     */
    protected IStatus doAction() {
        return Status.OK_STATUS;
    }

    protected IStatus execute() {
        log.info(getText() + " execute...."); //$NON-NLS-1$
        String oldValue = undoActionTrack.get(getActionUndoPos());

        beforeDoAction();

        if (doAction() == Status.CANCEL_STATUS) {
            cancelDoAction(oldValue);
            return Status.CANCEL_STATUS;
        }

        afterDoAction();
        return Status.OK_STATUS;
    }

    protected void beforeDoAction() {
        schema = ((ISchemaContentProvider) page.getTreeViewer().getContentProvider()).getXsdSchema();
        if (!omitTrack) {
            commitDocumentToHistory(schema.getDocument());
        }
    }

    protected void afterDoAction() {
        if (!omitTrack) {
            commitDocumentToCurrent(schema.getDocument());
        }
    }

    protected void cancelDoAction(String oldValue) {
        removeDocumentFromHistory();
        if (oldValue != null) {
            undoActionTrack.put(getActionUndoPos(), oldValue);
        }
    }

    protected void commitDocumentToHistory(Document history) {
        try {
            String value = Util.nodeToString(history);
            undoActionTrack.put(getActionUndoPos(), value);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    protected void removeDocumentFromHistory() {
        undoActionTrack.remove(getActionUndoPos());
    }

    protected void setOmitTrack(boolean omit) {
        this.omitTrack = omit;
    }

    protected void commitDocumentToCurrent(Document currnt) {
        try {
            String value = Util.nodeToString(currnt);
            redoActionTrack.keySet().size();
            redoActionTrack.put(getActionUndoPos(), value);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private int getActionUndoPos() {
        IOperationHistory history = getOperationHistory();
        return history.getUndoHistory(page.getUndoContext()).length;
    }

    private int getActionRedoPos() {
        IOperationHistory history = getOperationHistory();
        return history.getRedoHistory(page.getUndoContext()).length;
    }

    /**
     * need override by subclass
     *
     * @return
     */
    protected IStatus undo() {
        log.info(getText() + Messages.UndoAction_undo);
        String doc = undoActionTrack.get(getActionUndoPos() - 1);
        refresh(doc);
        return Status.OK_STATUS;
    }

    protected IStatus redo() {
        log.info(getText() + Messages.UndoAction_redo);
        String doc = redoActionTrack.get(getActionUndoPos());
        refresh(doc);
        return Status.OK_STATUS;
    }

    private void refresh(String content) {
        XSDSchema xsd;
        try {
            xsd = Util.createXsdSchema(content, page.getXObject());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return;
        }
        page.setXsdSchema(xsd);
        page.refresh();
        page.markDirtyWithoutCommit();
    }

    public XsdUndoableOperation getOperation() {
        return operation;
    }

    public IOperationHistory getOperationHistory() {
        return PlatformUI.getWorkbench().getOperationSupport().getOperationHistory();
    }

    // Modified by hbhong,to fix bug 21784
    protected TreeParent getTreeParent() {
        TreeParent treeParent = (TreeParent) page.getAdapter(TreeParent.class);
        return treeParent;
    }
    // The ending| bug:21784

}
