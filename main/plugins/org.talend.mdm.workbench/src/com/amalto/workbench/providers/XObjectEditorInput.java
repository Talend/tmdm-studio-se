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
package com.amalto.workbench.providers;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.amalto.workbench.editors.XObjectBrowser;
import com.amalto.workbench.editors.XObjectEditor;
import com.amalto.workbench.models.IXObjectModelListener;
import com.amalto.workbench.models.TreeObject;

public class XObjectEditorInput implements IEditorInput, IXObjectModelListener {

    Object model;

    String name;

    ArrayList<IXObjectModelListener> listeners = new ArrayList<IXObjectModelListener>();

    public XObjectEditorInput(Object model, String name) {
        this.model = model;
        this.name = name;

        if (this.model instanceof TreeObject) {
            return;
        }
        TreeObject xobject = (TreeObject) this.model;
        xobject.addListener(this);
    }

    public Object getAdapter(Class adapter) {

        if (adapter.equals(XObjectEditor.class)) {
            return model;
        }
        if (adapter.equals(XObjectBrowser.class)) {
            return model;
        }
        if (adapter.equals(Object.class)) {
            return model;
        }

        return null;
    }

    public boolean exists() {
        return false;
    }

    public ImageDescriptor getImageDescriptor() {
        return ImageDescriptor.getMissingImageDescriptor();
        // FIXME: Must Return an image to editors for Xtentits objects
    }

    public String getName() {
        return name;
    }

    public IPersistableElement getPersistable() {
        return null;
    }

    public String getToolTipText() {
        TreeObject xobject = (TreeObject) model;
        return xobject.getServerRoot().getDisplayName() + " - " + TreeObject.getTypeName(xobject.getType()); //$NON-NLS-1$
    }

    /**
     * Used to determine whether a new Editor should be opened or an existing one reused
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof XObjectEditorInput)) {
            return false;
        }
        return this.getToolTipText().equals(((XObjectEditorInput) obj).getToolTipText());
    }

    public Object getModel() {
        return model;
    }

    public void setModel(Object model) {
        this.model = model;
    }

    public void setName(String name) {
        this.name = name;
    }

    /******
     * Listeners interface It basically transmits event to other registered IXObjectModelListeners
     *
     */

    public void addListener(IXObjectModelListener listener) {
        listeners.add(listener);
    }

    public void removeListener(IXObjectModelListener listener) {
        listeners.remove(listener);
    }

    // IXObjectModelListener Implementation
    public void handleEvent(int type, TreeObject parent, TreeObject child) {
        for (Iterator iter = listeners.iterator(); iter.hasNext();) {
            ((IXObjectModelListener) iter.next()).handleEvent(type, parent, child);
        }
    }

}
