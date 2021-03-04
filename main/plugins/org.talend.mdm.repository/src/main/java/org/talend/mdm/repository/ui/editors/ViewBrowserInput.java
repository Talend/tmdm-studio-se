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
package org.talend.mdm.repository.ui.editors;

import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.model.mdmmetadata.MDMServerDef;
import org.talend.mdm.repository.model.mdmproperties.MDMServerObjectItem;
import org.talend.mdm.repository.model.mdmserverobject.MDMServerObject;
import org.talend.mdm.repository.utils.Bean2EObjUtil;

import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.providers.XObjectBrowserInput;


/**
 * created by liusongbo on 2014-2-27
 */
public class ViewBrowserInput extends XObjectBrowserInput {
    private final IRepositoryViewObject viewObject;

    private String version;

    private MDMServerDef serverDef;

    private boolean readOnly;

    public ViewBrowserInput(IRepositoryViewObject viewObject) {
        super(new TreeObject(), null);
        this.viewObject = viewObject;
        version = viewObject.getVersion();
        init(getInputItem());
    }

    protected void init(Item item) {
        MDMServerObject serverObject = ((MDMServerObjectItem) item).getMDMServerObject();
        Object treeObject = Bean2EObjUtil.getInstance().wrapEObjWithTreeObject(serverObject);
        setModel(treeObject);
        setName(serverObject.getName());
    }

    @Override
    public String getToolTipText() {
        TreeObject xobject = (TreeObject) getModel();
        return TreeObject.getTypeName(xobject.getType()) + " - "//$NON-NLS-1$
                + xobject.getDisplayName();
    }

    public Item getInputItem() {
        if (viewObject != null) {
            return viewObject.getProperty().getItem();
        }
        return null;
    }

    public IRepositoryViewObject getViewObject() {
        return viewObject;
    }

    public String getEditorId() {
        return XObjectBrowser2.EDITOR_ID;
    }

    public boolean isReadOnly() {
        return this.readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    @Override
    public String getName() {
        if (getVersion() != null) {
            return super.getName() + " " + getVersion(); //$NON-NLS-1$
        }
        return super.getName();
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public MDMServerDef getServerDef() {
        return this.serverDef;
    }

    public void setServerDef(MDMServerDef serverDef) {
        this.serverDef = serverDef;
    }
}
