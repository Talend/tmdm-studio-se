// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.workbench.rcp;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.internal.provisional.action.IToolBarContributionItem;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.registry.ActionSetRegistry;
import org.eclipse.ui.internal.registry.IActionSetDescriptor;
import org.eclipse.ui.internal.registry.PerspectiveDescriptor;
import org.eclipse.ui.internal.registry.PerspectiveRegistry;
import org.eclipse.ui.internal.registry.ViewDescriptor;
import org.eclipse.ui.internal.registry.ViewRegistry;
import org.eclipse.ui.views.IViewDescriptor;

/**
 * DOC aiming class global comment. Detailled comment
 */
public class ActionBarBuildHelper {

    private static Log log = LogFactory.getLog(ActionBarBuildHelper.class);

    protected IWorkbenchAction introAction;

    protected IWorkbenchWindow window;

    protected IActionBarConfigurer actionBarConfigurer;

    protected MenuManager helpMenu;

    protected MenuManager windowMenu;

    protected MenuManager editMenu;

    protected MenuManager fileMenu;

    protected ICoolBarManager coolBar;

    protected static final String GROUP_UNDO = "group undo"; //$NON-NLS-1$

    protected static final String GROUP_COPY = "group copy"; //$NON-NLS-1$

    protected static final String GROUP_DELETE = "group delete"; //$NON-NLS-1$

    public void setActionBarConfigurer(IActionBarConfigurer actionBarConfigurer) {
        this.actionBarConfigurer = actionBarConfigurer;
    }

    public IActionBarConfigurer getActionBarConfigurer() {
        return this.actionBarConfigurer;
    }

    public IWorkbenchWindow getWindow() {
        return this.window;
    }

    public void setWindow(IWorkbenchWindow window) {
        this.window = window;
    }

    protected void removeAction(final ActionSetRegistry reg, final IActionSetDescriptor actionSet) {
        IExtension ext = actionSet.getConfigurationElement().getDeclaringExtension();
        reg.removeExtension(ext, new Object[] { actionSet });
    }

    protected static final String[] ACTIONSETID = new String[] {
            "org.eclipse.ui.edit.text.actionSet.convertLineDelimitersTo", //$NON-NLS-1$
            "org.eclipse.ui.edit.text.actionSet.annotationNavigation", "org.eclipse.ui.NavigateActionSet", //$NON-NLS-1$ //$NON-NLS-2$
            "org.eclipse.ui.WorkingSetActionSet", "org.eclipse.ui.edit.text.actionSet.navigation", //$NON-NLS-1$ //$NON-NLS-2$
            "org.eclipse.search.searchActionSet", "org.eclipse.ui.externaltools.ExternalToolsSet", "org.talend.repository.bootTalendActionSet" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    public void fillMenuBar(final IMenuManager menuBar) {

        ActionSetRegistry reg = WorkbenchPlugin.getDefault().getActionSetRegistry();
        IActionSetDescriptor[] actionSets = reg.getActionSets();

        List<String> list = new ArrayList<String>();
        for (String item : ACTIONSETID) {
            list.add(item);
        }

        for (int i = 0; i < actionSets.length; i++) {
            if (list.contains(actionSets[i].getId())) {
                removeAction(reg, actionSets[i]);
            }
        }

        fileMenu = new MenuManager("File", IWorkbenchActionConstants.M_FILE); //$NON-NLS-1$
        menuBar.add(fileMenu);
        // MenuManager subFile = new MenuManager("&New", IWorkbenchActionConstants.NEW_EXT);
        // subFile.add(ActionFactory.NEW.create(window));
        // fileMenu.add(subFile);

        IWorkbenchAction closeAction = ActionFactory.CLOSE.create(window);
        fileMenu.add(closeAction);
        // actionBarConfigurer.registerGlobalAction(closeAction);

        IWorkbenchAction closeAllAction = ActionFactory.CLOSE_ALL.create(window);
        fileMenu.add(closeAllAction);
        // actionBarConfigurer.registerGlobalAction(closeAllAction);
        fileMenu.add(new Separator());

        fileMenu.add(ActionFactory.SAVE.create(window));

        IWorkbenchAction saveAllAction = ActionFactory.SAVE_ALL.create(window);
        fileMenu.add(saveAllAction);
        // actionBarConfigurer.registerGlobalAction(saveAllAction);
        // fileMenu.add(ActionFactory.SAVE_AS.create(window));
        fileMenu.add(new Separator());
        fileMenu.add(ActionFactory.PRINT.create(window));
        fileMenu.add(new Separator());

        fileMenu.add(new Separator());

        fileMenu.add(ActionFactory.IMPORT.create(window));

        fileMenu.add(new Separator());
        fileMenu.add(ActionFactory.QUIT.create(window));

        fileMenu.add(new Separator());

        editMenu = new MenuManager("Edit", IWorkbenchActionConstants.M_EDIT); //$NON-NLS-1$
        menuBar.add(editMenu);
        editMenu.add(new Separator(GROUP_UNDO));
        editMenu.add(new Separator(GROUP_COPY));
        editMenu.add(new Separator(GROUP_DELETE));
        editMenu.appendToGroup(GROUP_UNDO, ActionFactory.UNDO.create(window));
        editMenu.appendToGroup(GROUP_UNDO, ActionFactory.REDO.create(window));
        editMenu.appendToGroup(GROUP_COPY, ActionFactory.CUT.create(window));
        editMenu.appendToGroup(GROUP_COPY, ActionFactory.COPY.create(window));
        editMenu.appendToGroup(GROUP_COPY, ActionFactory.PASTE.create(window));
        editMenu.appendToGroup(GROUP_DELETE, ActionFactory.DELETE.create(window));
        editMenu.appendToGroup(GROUP_DELETE, ActionFactory.SELECT_ALL.create(window));
        editMenu.add(new GroupMarker(IWorkbenchActionConstants.FIND_EXT));

        MenuManager navigateMenu = new MenuManager("Navigate", IWorkbenchActionConstants.M_NAVIGATE); //$NON-NLS-1$
        navigateMenu.add(new GroupMarker(IWorkbenchActionConstants.NAV_START));
        navigateMenu.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
        navigateMenu.add(new GroupMarker(IWorkbenchActionConstants.SHOW_EXT));
        // see bug 0005492: Could not read the editor (XML Editor)
        navigateMenu.add(new GroupMarker(IWorkbenchActionConstants.OPEN_EXT));

        MenuManager gotoMenu = new MenuManager("Goto", //$NON-NLS-1$
                IWorkbenchActionConstants.GO_TO);
        navigateMenu.add(gotoMenu);

        menuBar.add(navigateMenu);

        windowMenu = new MenuManager("Window", IWorkbenchActionConstants.M_WINDOW); //$NON-NLS-1$
        menuBar.add(windowMenu);

        windowMenu.add(new ShowViewAction());
        windowMenu.add(new Separator());
        // windowMenu.add(ActionFactory.SHOW_VIEW_MENU.create(window));
        windowMenu.add(ActionFactory.MAXIMIZE.create(window));

        windowMenu.add(ActionFactory.PREFERENCES.create(window));

        helpMenu = new MenuManager("Help", IWorkbenchActionConstants.M_HELP); //$NON-NLS-1$
        menuBar.add(helpMenu);

        introAction = ActionFactory.INTRO.create(window);
        // Help
        helpMenu.add(introAction);
        helpMenu.add(ActionFactory.HELP_CONTENTS.create(window));
        IWorkbenchAction create = ActionFactory.ABOUT.create(window);
        helpMenu.add(create);
    }

    public void fillCoolBar(ICoolBarManager coolBar) {
        this.coolBar = coolBar;
        IToolBarManager toolBar = new ToolBarManager(SWT.FLAT | SWT.RIGHT);
        coolBar.add(new ToolBarContributionItem(toolBar, "Save")); //$NON-NLS-1$
        toolBar.add(ActionFactory.SAVE.create(window));

    }

    public void printCoolBar() {
        log.info("coolBar-" + coolBar); //$NON-NLS-1$

        IContributionItem[] items = coolBar.getItems();
        for (IContributionItem item : items) {
            IToolBarContributionItem it = (IToolBarContributionItem) item;

            IToolBarManager manager = it.getToolBarManager();
            printItemId(manager);

        }
    }

    public void preWindowOpen(IWorkbenchWindowConfigurer configurer) {

    }

    public void postWindowOpen() {
        hideFileActions();
        hideWindowActions();
        hideHelpActions();
        hideEditActions();
        hideCoolBarActions();

        List<IPerspectiveDescriptor> perspectivesToDelete = new ArrayList<IPerspectiveDescriptor>();

        for (IPerspectiveDescriptor desc : perspectivesToDelete) {
            PerspectiveDescriptor perspDesc = (PerspectiveDescriptor) desc;
            PerspectiveRegistry registry = (PerspectiveRegistry) window.getWorkbench().getPerspectiveRegistry();
            PerspectiveDescriptor[] descriptors = { perspDesc };
            registry.removeExtension(perspDesc.getConfigElement().getDeclaringExtension(), descriptors);
        }

        String[] viewsId = { "org.eclipse.pde.runtime.RegistryBrowser", "org.eclipse.pde.ui.DependenciesView",//$NON-NLS-1$//$NON-NLS-2$
                "org.eclipse.pde.ui.PluginsView", "org.eclipse.team.sync.views.SynchronizeView",//$NON-NLS-1$//$NON-NLS-2$
                "org.eclipse.team.ui.GenericHistoryView" };//$NON-NLS-1$

        List<IViewDescriptor> viewsToDelete = new ArrayList<IViewDescriptor>();

        for (IViewDescriptor desc : window.getWorkbench().getViewRegistry().getViews()) {
            if (ArrayUtils.contains(viewsId, desc.getId())) {
                viewsToDelete.add(desc);
            }
        }

        for (IViewDescriptor desc : viewsToDelete) {
            ViewDescriptor viewDesc = (ViewDescriptor) desc;
            ViewRegistry registry = (ViewRegistry) window.getWorkbench().getViewRegistry();
            ViewDescriptor[] descriptors = { viewDesc };
            registry.removeExtension(viewDesc.getConfigurationElement().getDeclaringExtension(), descriptors);
        }

        String[] prefsId = { "org.eclipse.team.ui.TeamPreferences" };//$NON-NLS-1$
        List<IPreferenceNode> prefsToDelete = new ArrayList<IPreferenceNode>();
        for (IPreferenceNode node : window.getWorkbench().getPreferenceManager().getRootSubNodes()) {
            if (ArrayUtils.contains(prefsId, node.getId())) {
                prefsToDelete.add(node);
            }
        }
        for (IPreferenceNode node : prefsToDelete) {
            window.getWorkbench().getPreferenceManager().remove(node);
        }
    }

    protected void hideCoolBarActions() {
        String[] removeIds = { "org.eclipse.wst.xml.ui.design.DesignToolBar", "org.eclipse.debug.ui.launchActionSet" }; //$NON-NLS-1$ //$NON-NLS-2$
        for (String id : removeIds) {
            coolBar.remove(id);
        }
    }

    protected void hideFileActions() {
        String[] removeIds = { "org.eclipse.ui.openLocalFile" }; //$NON-NLS-1$
        for (String id : removeIds) {
            fileMenu.remove(id);
        }
    }

    protected void hideWindowActions() {
        String[] removeIds = {};// "perspective"
        for (String id : removeIds) {
            windowMenu.remove(id);
        }
    }

    protected void hideHelpActions() {
        String[] removeIds = { "org.eclipse.equinox.p2.ui.sdk.update", "group.assist", //$NON-NLS-1$ //$NON-NLS-2$
                "org.eclipse.ui.actions.showKeyAssistHandler", "additions", "group.tutorials", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                "org.eclipse.ui.cheatsheets.actions.CheatSheetHelpMenuAction", "subversive", "subversive.help" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        for (String id : removeIds) {
            helpMenu.remove(id);
        }
    }

    private void printItemId(IContributionManager menuBar) {
        log.info("IContributionManager-" + menuBar); //$NON-NLS-1$
        IContributionItem[] items = menuBar.getItems();
        for (IContributionItem item : items) {
            if (item.isVisible())
                log.info(" " + item.getId()); //$NON-NLS-1$
        }
    }

    protected void hideEditActions() {
        // do nothing
    }
}
