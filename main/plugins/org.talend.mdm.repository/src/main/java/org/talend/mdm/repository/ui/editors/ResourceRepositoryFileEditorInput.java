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

import org.eclipse.core.resources.IFile;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.IServerObjectRepositoryType;
import org.talend.mdm.repository.model.mdmproperties.MDMServerObjectItem;
import org.talend.mdm.repository.model.mdmproperties.WSResourceItem;
import org.talend.mdm.repository.model.mdmserverobject.MDMServerObject;
import org.talend.mdm.repository.model.mdmserverobject.WSResourceE;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;

import com.amalto.workbench.models.TreeObject;

/**
 * DOC hbhong class global comment. Detailled comment
 */
// public class ResourceRepositoryFileEditorInput extends FileEditorInput implements IRepositoryViewEditorInput {
public class ResourceRepositoryFileEditorInput extends XObjectEditorInput2 {

    private IFile file;

    public ResourceRepositoryFileEditorInput(IRepositoryViewObject viewObject) {
        super(viewObject);
    }

    @Override
    protected void init(Item item) {

        MDMServerObject serverObject = ((MDMServerObjectItem) item).getMDMServerObject();
        String name = null;
        if (serverObject instanceof WSResourceE) {
            name = serverObject.getName() + "." + ((WSResourceE) serverObject).getFileExtension(); //$NON-NLS-1$
        }
        Object treeObject = new TreeObject(name, null, TreeObject.RESOURCES, null, null, null);

        setModel(treeObject);
        setName(name);
    }

    public IFile getReferenceFile() {
        if (file == null) {
            Item item = getInputItem();
            String fileExtension = ((WSResourceItem) item).getResource().getFileExtension();
            file = RepositoryResourceUtil.findReferenceFile(IServerObjectRepositoryType.TYPE_RESOURCE, item, fileExtension);
        }
        return file;
    }

    @Override
    public String getName() {
        return super.getName();
    }
}
