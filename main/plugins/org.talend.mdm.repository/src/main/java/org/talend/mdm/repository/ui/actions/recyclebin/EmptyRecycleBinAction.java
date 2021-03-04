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
package org.talend.mdm.repository.ui.actions.recyclebin;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.models.FolderRepositoryObject;
import org.talend.mdm.repository.plugin.RepositoryPlugin;
import org.talend.mdm.repository.utils.EclipseResourceManager;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class EmptyRecycleBinAction extends AbstractRemoveCommandStackAction {

    static Logger log = Logger.getLogger(EmptyRecycleBinAction.class);

    private static final ImageDescriptor EMPTY_IMG = EclipseResourceManager.getImageDescriptor(RepositoryPlugin.PLUGIN_ID,
            "icons/recycle-bin.png"); //$NON-NLS-1$;

    /**
     * DOC hbhong EmptyRecycleBinAction constructor comment.
     *
     * @param text
     */
    public EmptyRecycleBinAction() {
        super(Messages.EmptyRecycleBinAction_title);
        setImageDescriptor(EMPTY_IMG);
    }

    @Override
    public String getGroupName() {
        return GROUP_EDIT;
    }

    @Override
    protected void doRun() {
        Object selObj = getSelectedObject().get(0);
        FolderRepositoryObject conRepObj = (FolderRepositoryObject) selObj;
        List<IRepositoryViewObject> children = conRepObj.getChildren();
        int size = children.size();

        if (size > 0) {
            if (!(MessageDialog.openQuestion(getShell(), Messages.EmptyRecycleBinAction_title,
                    Messages.EmptyRecycleBinAction_confirmEmpty))) {
                return;
            }
            removeViewObjects(children);
        }
    }

    @Override
    public boolean isVisible(IRepositoryViewObject viewObj) {
        Object selObj = getSelectedObject().get(0);
        FolderRepositoryObject conRepObj = (FolderRepositoryObject) selObj;
        return !conRepObj.getChildren().isEmpty();
    }

}
