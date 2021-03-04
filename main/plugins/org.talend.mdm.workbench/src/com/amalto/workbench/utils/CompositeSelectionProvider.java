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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;

public class CompositeSelectionProvider implements ISelectionProvider, ISelectionChangedListener {

    private ISelection selection;

    private Object repositoryViewObj;

    private List<ISelectionChangedListener> listeners = new ArrayList<ISelectionChangedListener>();

    public void addSelectionChangedListener(ISelectionChangedListener listener) {
        listeners.add(listener);
    }

    public ISelection getSelection() {
        if (selection == null && repositoryViewObj != null) {
            StructuredSelection sel = new StructuredSelection(repositoryViewObj);
            selection = sel;
        }
        return selection;
    }

    public void removeSelectionChangedListener(ISelectionChangedListener listener) {
        listeners.remove(listener);
    }

    public void setSelection(ISelection selection) {

        this.selection = selection;
        final SelectionChangedEvent e = new SelectionChangedEvent(this, selection);

        for (final ISelectionChangedListener eachListener : listeners.toArray(new ISelectionChangedListener[0])) {
            SafeRunner.run(new SafeRunnable() {

                public void run() {
                    eachListener.selectionChanged(e);
                }
            });
        }
    }

    public void selectionChanged(SelectionChangedEvent event) {
        setSelection(event.getSelection());
    }

    public Object getRepositoryViewObj() {
        return repositoryViewObj;
    }

    public void setRepositoryViewObj(Object repositoryViewObj) {
        this.repositoryViewObj = repositoryViewObj;
    }

}
