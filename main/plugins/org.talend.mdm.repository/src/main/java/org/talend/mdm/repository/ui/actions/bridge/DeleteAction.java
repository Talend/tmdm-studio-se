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
package org.talend.mdm.repository.ui.actions.bridge;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.talend.commons.runtime.model.repository.ERepositoryStatus;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.ItemReferenceBean;
import org.talend.core.repository.ui.actions.DeleteActionCache;
import org.talend.core.repository.utils.RepositoryNodeDeleteManager;
import org.talend.mdm.repository.core.bridge.AbstractBridgeRepositoryAction;
import org.talend.mdm.repository.core.command.CommandManager;
import org.talend.mdm.repository.core.command.ICommand;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.models.FolderRepositoryObject;
import org.talend.repository.model.IRepositoryNode;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class DeleteAction extends AbstractBridgeRepositoryAction {

    public DeleteAction() {
        super(new org.talend.core.repository.ui.actions.DeleteAction());
        setText(Messages.RemoveFromRepositoryAction_removeFromRepository);
    }

    @Override
    public String getGroupName() {
        return GROUP_EDIT;
    }

    @Override
    protected void doRun() {

        super.doRun();

        List<IRepositoryNode> remainNodes = getRemainNodes();
        if (!remainNodes.isEmpty()) {
            for (IRepositoryNode node : remainNodes) {
                IRepositoryViewObject viewObj = node.getObject();
                if (viewObj instanceof FolderRepositoryObject) {
                    removeFolderObject(viewObj);
                } else {
                    removeServerObject(viewObj);
                }

            }
        }
        commonViewer.refresh();
    }

    private List<IRepositoryNode> getRemainNodes() {
        ISelection selection = getSelectionProvider().getSelection();
        List<IRepositoryNode> nodes = new LinkedList<IRepositoryNode>();
        if (!selection.isEmpty()) {

            for (Iterator il = ((IStructuredSelection) selection).iterator(); il.hasNext();) {
                IRepositoryNode node = (IRepositoryNode) il.next();
                nodes.add(node);
            }

            final DeleteActionCache deleteActionCache = DeleteActionCache.getInstance();
            deleteActionCache.setGetAlways(false);
            deleteActionCache.setDocRefresh(false);
            deleteActionCache.createRecords();
            final List<ItemReferenceBean> unDeleteItems = RepositoryNodeDeleteManager.getInstance().getUnDeleteItems(nodes,
                    deleteActionCache);

        }
        return nodes;
    }

    private void removeFolderObject(IRepositoryViewObject viewObj) {
        for (IRepositoryViewObject childObj : viewObj.getChildren()) {
            if (childObj instanceof FolderRepositoryObject) {
                removeFolderObject(childObj);
            } else {
                removeServerObject(childObj);
            }
        }

    }

    private void removeServerObject(IRepositoryViewObject viewObj) {
        ERepositoryStatus repositoryStatus = viewObj.getRepositoryStatus();
        if (repositoryStatus == ERepositoryStatus.DELETED) {
            CommandManager.getInstance().pushCommand(ICommand.CMD_DELETE, viewObj.getId(), viewObj.getLabel());
        }
    }
}
