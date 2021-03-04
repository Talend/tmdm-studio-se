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
package org.talend.mdm.repository.ui.dialogs.lock;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.designer.core.ui.editor.ProcessEditorInput;
import org.talend.mdm.repository.ui.editors.IRepositoryViewEditorInput;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class LockedDirtyObjectDialog extends LockedObjectDialog {

    private static Logger log = Logger.getLogger(LockedDirtyObjectDialog.class);

    /**
     * DOC hbhong LockedDirtyObjectDialog constructor comment.
     *
     * @param parentShell
     * @param multiObjAlertMsg
     * @param inputObjs
     */
    public LockedDirtyObjectDialog(Shell parentShell, String multiObjAlertMsg, List<IRepositoryViewObject> inputObjs) {
        super(parentShell, multiObjAlertMsg, multiObjAlertMsg, inputObjs, false);
    }

    /**
     * DOC hbhong LockedDirtyObjectDialog constructor comment.
     *
     * @param parentShell
     * @param multiObjAlertMsg
     * @param singleObjAlertMsg
     * @param inputObjs
     */
    public LockedDirtyObjectDialog(Shell parentShell, String multiObjAlertMsg, String singleObjAlertMsg,
            List<IRepositoryViewObject> inputObjs) {
        super(parentShell, multiObjAlertMsg, singleObjAlertMsg, inputObjs, false);
    }

    @Override
    public boolean canContinueRestOperation() {
        return true;
    }

    @Override
    protected void initInput(List<IRepositoryViewObject> inputObjs) {
        lockedObjs = new LinkedList<IRepositoryViewObject>();
        for (IRepositoryViewObject viewObject : inputObjs) {
            if (isDirtyMdmViewObj(inputObjs, viewObject)) {
                lockedObjs.add(viewObject);
            }
        }

    }

    private Map<String, IEditorPart> editorRefMap;

    private boolean isDirtyMdmViewObj(List<IRepositoryViewObject> inputObjs, IRepositoryViewObject viewObj) {
        if (editorRefMap == null) {
            editorRefMap = new HashMap<String, IEditorPart>();
            // collect all ids
            Set<String> ids = new HashSet<String>();
            for (IRepositoryViewObject viewObject : inputObjs) {
                String id = viewObject.getId();
                if (id != null) {
                    ids.add(id);
                }
            }

            //
            if (PlatformUI.getWorkbench().getActiveWorkbenchWindow() != null) {
                IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                for (IEditorReference ref : activePage.getEditorReferences()) {
                    try {
                        String id = null;
                        IEditorInput editorInput = ref.getEditorInput();
                        if (editorInput instanceof IRepositoryViewEditorInput) {
                            IRepositoryViewObject viewObject = ((IRepositoryViewEditorInput) editorInput).getViewObject();
                            if (viewObject != null) {
                                id = viewObject.getId();
                            }
                        } else if (editorInput instanceof ProcessEditorInput) {
                            id = ((ProcessEditorInput) editorInput).getId();
                        }

                        if (id != null && ids.contains(id)) {
                            IEditorPart editor = ref.getEditor(false);
                            if (editor != null && editor.isDirty()) {
                                editorRefMap.put(id, editor);
                            }
                        }
                    } catch (PartInitException e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
        }
        return editorRefMap.containsKey(viewObj.getId());

    }

    public void saveDirtyObjects() {
        if (editorRefMap != null) {
            for (IEditorPart editorPart : editorRefMap.values()) {
                editorPart.doSave(new NullProgressMonitor());
            }
        }
    }
}
