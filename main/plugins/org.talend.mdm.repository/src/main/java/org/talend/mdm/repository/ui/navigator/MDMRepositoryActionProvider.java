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
package org.talend.mdm.repository.ui.navigator;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.mdm.repository.core.AbstractRepositoryAction;
import org.talend.mdm.repository.core.IRepositoryNodeActionProvider;
import org.talend.mdm.repository.core.IRepositoryNodeConfiguration;
import org.talend.mdm.repository.core.IRepositoryViewGlobalActionHandler;
import org.talend.mdm.repository.extension.RepositoryNodeConfigurationManager;
import org.talend.mdm.repository.ui.actions.CopyAction;
import org.talend.mdm.repository.ui.actions.OpenObjectAction;
import org.talend.mdm.repository.ui.actions.PasteAction;
import org.talend.mdm.repository.ui.actions.RefreshAction;

public class MDMRepositoryActionProvider extends CommonActionProvider implements IRepositoryViewGlobalActionHandler {

    public MDMRepositoryActionProvider() {
        initActions();
    }

    private Map<String, AbstractRepositoryAction> commonActionMap = new LinkedHashMap<String, AbstractRepositoryAction>();

    private void registerCommonAction(AbstractRepositoryAction action) {
        if (action != null) {
            String id = action.getActionDefinitionId();
            if (id == null) {
                id = action.getId();
            }
            commonActionMap.put(id, action);
        }
    }

    private void initActions() {
        registerCommonAction(new OpenObjectAction());
        registerCommonAction(new RefreshAction(false));
        registerCommonAction(new CopyAction());
        registerCommonAction(new PasteAction());
    }

    private void initCommonViewerForActions(CommonViewer commonViewer) {
        for (AbstractRepositoryAction action : commonActionMap.values()) {
            action.initCommonViewer(commonViewer);
        }
    }

    private void fillActionBarsForActions(IActionBars actionBars) {
        for (AbstractRepositoryAction action : commonActionMap.values()) {
            String definitionId = action.getActionDefinitionId();
            if (definitionId != null) {
                actionBars.setGlobalActionHandler(definitionId, action);
            }
        }
    }

    private void updateActionBarsForActions(IStructuredSelection selection) {
        for (AbstractRepositoryAction action : commonActionMap.values()) {
            action.selectionChanged(selection);
        }
    }

    @Override
    public void init(ICommonActionExtensionSite aSite) {
        CommonViewer commonViewer = (CommonViewer) aSite.getStructuredViewer();
        for (IRepositoryNodeConfiguration conf : RepositoryNodeConfigurationManager.getConfigurations()) {
            IRepositoryNodeActionProvider actionProvider = conf.getActionProvider();
            if (actionProvider != null) {
                actionProvider.setRepositoryViewGlobalActionHandler(this);
                actionProvider.initCommonViewer(commonViewer);
            }
        }
        initCommonViewerForActions(commonViewer);
    }

    @Override
    public void fillActionBars(IActionBars actionBars) {
        fillActionBarsForActions(actionBars);
    }

    @Override
    public void fillContextMenu(IMenuManager menu) {
        if (ProxyRepositoryFactory.getInstance().isUserReadOnlyOnCurrentProject()) {
            return;
        }

        IStructuredSelection selection = (IStructuredSelection) getContext().getSelection();
        List<AbstractRepositoryAction> finalActions = null;
        for (Iterator il = selection.iterator(); il.hasNext();) {
            Object obj = il.next();
            if (obj instanceof IRepositoryViewObject) {
                IRepositoryViewObject viewObj = (IRepositoryViewObject) obj;
                IRepositoryNodeConfiguration conf = RepositoryNodeConfigurationManager.getConfiguration(viewObj);
                IRepositoryNodeActionProvider actionProvider = conf.getActionProvider();
                if (conf != null && actionProvider != null) {
                    actionProvider.updateSelection(selection);
                    List<AbstractRepositoryAction> actions = actionProvider.getActions(viewObj);
                    if (actions != null) {
                        if (finalActions == null) {
                            finalActions = actions;
                        }
                        finalActions = combineActions(finalActions, actions);
                        if (finalActions.size() == 0) {
                            return;
                        }
                    }
                }
            }
        }
        // hook action to menu
        for (IAction action : finalActions) {
            if (action instanceof AbstractRepositoryAction) {
                AbstractRepositoryAction groupAction = (AbstractRepositoryAction) action;
                groupAction.selectionChanged(selection);
                if (groupAction.getGroupName() != null) {
                    menu.appendToGroup(groupAction.getGroupName(), action);
                } else {
                    menu.add(action);
                }
            }
        }

    }

    @Override
    public void updateActionBars() {
        IStructuredSelection selection = (IStructuredSelection) getContext().getSelection();
        updateActionBarsForActions(selection);
    }

    private List<AbstractRepositoryAction> combineActions(List<AbstractRepositoryAction> actionsA,
            List<AbstractRepositoryAction> actionsB) {
        List<AbstractRepositoryAction> result = new LinkedList<AbstractRepositoryAction>();
        for (Iterator<AbstractRepositoryAction> il = actionsA.iterator(); il.hasNext();) {
            AbstractRepositoryAction action = il.next();
            if (actionsB.contains(action)) {
                result.add(action);
            }
        }
        return result;
    }

    public AbstractRepositoryAction getGlobalAction(String actionId) {
        return commonActionMap.get(actionId);
    }

}
