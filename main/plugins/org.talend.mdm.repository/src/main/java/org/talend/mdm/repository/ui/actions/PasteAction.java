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
package org.talend.mdm.repository.ui.actions;

import org.apache.log4j.Logger;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.DND;
import org.eclipse.ui.navigator.CommonDropAdapterAssistant;
import org.eclipse.ui.navigator.INavigatorDnDService;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.AbstractRepositoryAction;
import org.talend.mdm.repository.core.IRepositoryViewGlobalActionHandler;
import org.talend.mdm.repository.core.dnd.RepositoryDropAssistant;
import org.talend.mdm.repository.i18n.Messages;

import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class PasteAction extends AbstractRepositoryAction {

    private static Logger log = Logger.getLogger(PasteAction.class);
    /**
     * DOC hbhong PasteAction constructor comment.
     *
     * @param text
     */
    public PasteAction() {
        super(Messages.PasteAction_paste);
        setImageDescriptor(ImageCache.getImage(EImage.PASTE.getPath()));
        this.setId(IRepositoryViewGlobalActionHandler.PASTE);
        this.setActionDefinitionId(IRepositoryViewGlobalActionHandler.PASTE);
    }

    public String getGroupName() {
        return GROUP_COMMON;
    }

    protected void doRun() {
        RepositoryDropAssistant dropAssistant = getDropAssistant();

        if (dropAssistant != null) {
            IRepositoryViewObject dropViewObj = getSelectedDropViewObj();
            boolean result = dropAssistant.copyViewObj(getSelectedDragViewObj(), dropViewObj);
            if (result) {
                refreshContainer(dropAssistant, dropViewObj);
            }
        }
    }

    private void refreshContainer(RepositoryDropAssistant dropAssistant, IRepositoryViewObject viewObject) {
        IRepositoryViewObject parentViewObject = dropAssistant.getParentRepositoryViewObject(viewObject);
        if (parentViewObject != null)
            commonViewer.refresh(parentViewObject);
    }

    private RepositoryDropAssistant getDropAssistant() {
        INavigatorDnDService dndService = commonViewer.getNavigatorContentService().getDnDService();
        CommonDropAdapterAssistant[] dropAssistants = dndService.findCommonDropAdapterAssistants(getSelectedDropViewObj(),
                getStructuredSelection());
        for (CommonDropAdapterAssistant assistant : dropAssistants) {
            if (assistant instanceof RepositoryDropAssistant) {
                return (RepositoryDropAssistant) assistant;
            }
        }
        return null;
    }

    private IRepositoryViewObject getSelectedDropViewObj() {
        return (IRepositoryViewObject) getSelectedObject().get(0);
    }

    private IRepositoryViewObject getSelectedDragViewObj() {
        ISelection selection = LocalSelectionTransfer.getTransfer().getSelection();
        if (selection instanceof IStructuredSelection) {
            Object object = ((IStructuredSelection) selection).getFirstElement();
            if (object instanceof IRepositoryViewObject) {
                return (IRepositoryViewObject) object;
            }
        }
        return null;
    }

    public boolean isVisible(IRepositoryViewObject viewObj) {
        RepositoryDropAssistant dropAssistant = getDropAssistant();
        if (dropAssistant != null) {
            IRepositoryViewObject dragViewObj = getSelectedDragViewObj();
            IRepositoryViewObject dropViewObj = (IRepositoryViewObject) getSelectedObject().get(0);
            return dropAssistant.validate(DND.DROP_COPY, dragViewObj, dropViewObj);
        }
        return super.isVisible(viewObj);
    }

}
