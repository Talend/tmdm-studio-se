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
package org.talend.mdm.repository.ui.contributor;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.ui.editors.IRepositoryViewEditorInput;


/**
 * DOC achen  class global comment. Detailled comment
 */
public class SvnHistorySelectionProvider implements ISelectionProvider {

    IRepositoryViewEditorInput input;

    public SvnHistorySelectionProvider(IRepositoryViewEditorInput input) {
        this.input = input;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.
     * ISelectionChangedListener)
     */
    public void addSelectionChangedListener(ISelectionChangedListener listener) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
     */
    public ISelection getSelection() {
        // TODO
        IRepositoryViewObject obj = input.getViewObject();// ContainerCacheService.get(input.getInputItem().getProperty());
        // RepositoryNode node = RepositoryResourceUtil.convertToNode(obj);
        if (obj != null)
            return new StructuredSelection(obj);
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.
     * ISelectionChangedListener)
     */
    public void removeSelectionChangedListener(ISelectionChangedListener listener) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
     */
    public void setSelection(ISelection selection) {
        // TODO Auto-generated method stub

    }

}
