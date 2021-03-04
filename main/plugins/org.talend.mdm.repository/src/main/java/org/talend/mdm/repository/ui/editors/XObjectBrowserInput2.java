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
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.model.mdmmetadata.MDMServerDef;
import org.talend.mdm.repository.model.mdmproperties.MDMServerObjectItem;
import org.talend.mdm.repository.model.mdmserverobject.MDMServerObject;
import org.talend.mdm.repository.utils.Bean2EObjUtil;

import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.providers.XObjectBrowserInput;

/**
 * DOC achen class global comment. Detailled comment
 */
public class XObjectBrowserInput2 extends XObjectBrowserInput implements IRepositoryViewEditorInput {

    private final IRepositoryViewObject viewObject;

    private String version;

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    private MDMServerDef serverDef;

    public MDMServerDef getServerDef() {
        return this.serverDef;
    }

    public void setServerDef(MDMServerDef serverDef) {
        this.serverDef = serverDef;
    }

    public XObjectBrowserInput2(IRepositoryViewObject viewObject) {
        super(new TreeObject(), null);
        this.viewObject = viewObject;
        version = viewObject.getVersion();
        init(getInputItem());
    }

    // just now for event manager
    public XObjectBrowserInput2() {
        super(new TreeObject(), null);
        this.viewObject = null;
        TreeObject treeObj = new TreeObject("treeObject", null, TreeObject.SUBSCRIPTION_ENGINE, null, null); //$NON-NLS-1$
        setModel(treeObj);
        setName(Messages.EventManager_text);
    }

    protected void init(Item item) {
        MDMServerObject serverObject = ((MDMServerObjectItem) item).getMDMServerObject();
        Object treeObject = Bean2EObjUtil.getInstance().wrapEObjWithTreeObject(serverObject);
        setModel(treeObject);
        setName(serverObject.getName());
    }

    public XObjectBrowserInput2(IRepositoryViewObject viewObject, Object model, String name) {
        super(model, name);
        this.viewObject = viewObject;
        this.version = viewObject.getVersion();
    }

    @Override
    public String getToolTipText() {
        TreeObject xobject = (TreeObject) getModel();
        String sep = " - "; //$NON-NLS-1$
        String serverName = null;
        if (serverDef != null) {
            serverName = serverDef.getName();
        } else {
            serverName = xobject.getServerRoot().getName();
        }

        if (xobject.getType() == TreeObject.SUBSCRIPTION_ENGINE) {
            return serverName + sep + Messages.XObjectBrowserInput2_eventmanager;
        } else if (xobject.getType() == TreeObject.DATA_CLUSTER) {
            return serverName + sep + TreeObject.getTypeName(xobject.getType()) + sep + xobject.getDisplayName();
        } else {
            return TreeObject.getTypeName(xobject.getType()) + sep + xobject.getDisplayName();
        }
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

    private boolean readOnly;

    public boolean isReadOnly() {
        return this.readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    @Override
    public String getName() {
        if (getVersion() != null)
         {
            return super.getName() + " " + getVersion(); //$NON-NLS-1$
        }
        return super.getName();
    }
}
