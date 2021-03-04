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
package com.amalto.workbench.editors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.amalto.workbench.dialogs.MenuEntryDialog;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.providers.XObjectEditorInput;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.webservices.WSMenu;
import com.amalto.workbench.webservices.WSMenuEntry;
import com.amalto.workbench.webservices.WSMenuMenuEntriesDescriptions;

public class MenuMainPage extends AMainPageV2 {

    private static Log log = LogFactory.getLog(MenuMainPage.class);

    public final static int LOCATION_BEFORE = 0;

    public final static int LOCATION_AFTER = 1;

    public final static int LOCATION_UNDER = 2;

    protected Text descriptionText;

    protected TreeViewer menuTree = null;

    protected MenuManager menuTreeMgr = null;

    protected DropTarget windowTarget;

    protected boolean refreshing = false;

    protected boolean comitting = false;

    private String uripre;

    private TreeObject treeObject;

    public MenuMainPage(FormEditor editor) {

        super(editor, MenuMainPage.class.getName(), Messages.MenuMainPage_Menu
                + ((XObjectEditorInput) editor.getEditorInput()).getName()
                + Util.getRevision((TreeObject) ((XObjectEditorInput) editor.getEditorInput()).getModel()));
        treeObject = (TreeObject) ((XObjectEditorInput) editor.getEditorInput()).getModel();
        uripre = treeObject.getEndpointIpAddress();

    }

    @Override
    protected void createCharacteristicsContent(FormToolkit toolkit, Composite mainComposite) {

        try {
            // description
            Label descriptionLabel = toolkit.createLabel(mainComposite, Messages.MenuMainPage_Description, SWT.NULL);
            descriptionLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, true, 1, 1));
            descriptionText = toolkit.createText(mainComposite, "", SWT.BORDER);//$NON-NLS-1$
            descriptionText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
            ((GridData) descriptionText.getLayoutData()).minimumHeight = 30;
            descriptionText.addModifyListener(new ModifyListener() {

                public void modifyText(ModifyEvent e) {
                    if (refreshing) {
                        return;
                    }

                    markDirtyWithoutCommit();
                }
            });
            // Util.createCompDropTarget(descriptionText);
            // make the Page window a DropTarget - we need to dispose it
            windowTarget = new DropTarget(this.getPartControl(), DND.DROP_MOVE);
            windowTarget.setTransfer(new Transfer[] { TextTransfer.getInstance() });
            windowTarget.addDropListener(new DCDropTargetListener());

            Composite composite = toolkit.createComposite(mainComposite, SWT.BORDER);
            composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
            composite.setLayout(new GridLayout(1, false));

            menuTree = new TreeViewer(composite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
            menuTree.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
            ((GridData) menuTree.getControl().getLayoutData()).heightHint = 150;

            menuTree.setContentProvider(new ITreeContentProvider() {

                public void dispose() {
                }

                public Object[] getChildren(Object parentElement) {
                    if (parentElement instanceof TreeEntry) {
                        WSMenuEntry wsEntry = ((TreeEntry) parentElement).getWSMenuEntry();
                        if (wsEntry.getSubMenus() != null) {
                            TreeEntry[] children = new TreeEntry[wsEntry.getSubMenus().size()];
                            for (int i = 0; i < wsEntry.getSubMenus().size(); i++) {
                                children[i] = new TreeEntry((TreeEntry) parentElement, wsEntry.getSubMenus().get(i));
                            }
                            return children;
                        }
                        return null;
                    }

                    if (parentElement instanceof WSMenu) { // the root
                        java.util.List<WSMenuEntry> menuEntries = ((WSMenu) parentElement).getMenuEntries();
                        if (menuEntries != null) {
                            TreeEntry[] children = new TreeEntry[menuEntries.size()];
                            for (int i = 0; i < menuEntries.size(); i++) {
                                children[i] = new TreeEntry(null, menuEntries.get(i));
                            }
                            return children;
                        }
                        return null;
                    }

                    return null; // ??!!?
                }

                public Object[] getElements(Object inputElement) {
                    return getChildren(inputElement);
                }

                public Object getParent(Object element) {
                    return null;
                }

                public boolean hasChildren(Object element) {
                    return ((getChildren(element) == null) || (getChildren(element).length > 0));
                }

                public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
                }
            });
            menuTree.setLabelProvider(new LabelProvider() {

                @Override
                public String getText(Object element) {
                    WSMenuEntry wsMenuEntry = ((TreeEntry) element).getWSMenuEntry();
                    StringBuffer label = new StringBuffer(wsMenuEntry.getId() + " - ");//$NON-NLS-1$

                    for (WSMenuMenuEntriesDescriptions description : wsMenuEntry.getDescriptions()) {
                        label.append("[").append(description.getLanguage()).append(": ")//$NON-NLS-1$//$NON-NLS-2$
                                .append(description.getLabel()).append("] ");//$NON-NLS-1$
                    }
                    if (label.length() > 200) {
                        return label.substring(0, 197) + "..."; //$NON-NLS-1$
                    }
                    return label.toString();
                }

                @Override
                public Image getImage(Object element) {
                    return ImageCache.getCreatedImage(EImage.MENU.getPath());
                }
            });

            menuTreeMgr = new MenuManager();
            menuTreeMgr.setRemoveAllWhenShown(true);
            menuTreeMgr.addMenuListener(new IMenuListener() {

                public void menuAboutToShow(IMenuManager manager) {
                    IStructuredSelection selection = ((IStructuredSelection) menuTree.getSelection());
                    if ((selection == null) || (selection.getFirstElement() == null)) {
                        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
                        return;
                    }
                    // TreeEntry entry = (TreeEntry)selection.getFirstElement();
                    menuTreeMgr.add(new TreeEntryEditAction(menuTree));
                    menuTreeMgr.add(new TreeEntryAddAction(menuTree, LOCATION_BEFORE));
                    menuTreeMgr.add(new TreeEntryAddAction(menuTree, LOCATION_AFTER));
                    menuTreeMgr.add(new TreeEntryAddAction(menuTree, LOCATION_UNDER));
                    menuTreeMgr.add(new TreeEntryDeleteAction(menuTree));
                    menuTreeMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
                }
            });
            menuTree.addDoubleClickListener(new IDoubleClickListener() {

                public void doubleClick(DoubleClickEvent event) {
                    (new TreeEntryEditAction(menuTree)).run();
                }
            });
            Menu menu = menuTreeMgr.createContextMenu(menuTree.getControl());
            menuTree.getControl().setMenu(menu);
            getSite().registerContextMenu(menuTreeMgr, menuTree);

            refreshData();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }// createCharacteristicsContent

    @Override
    protected void refreshData() {
        try {
            // System.out.println("refreshData() ");
            if (this.comitting) {
                return;
            }

            this.refreshing = true;

            WSMenu wsMenu = (WSMenu) (getXObject().getWsObject());
            descriptionText.setText(wsMenu.getDescription() == null ? "" : wsMenu.getDescription()); //$NON-NLS-1$
            menuTree.setInput(wsMenu);
            this.refreshing = false;

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            MessageDialog.openError(this.getSite().getShell(), Messages.MenuMainPage_ErrorRefreshingPage,
                    Messages.bind(Messages.MenuMainPage_ErrorRefreshingPageXX, e.getLocalizedMessage()));
        }
    }

    @Override
    protected void commit() {
        try {
            // System.out.println("commit() ROLE\n"+role.toString());
            if (this.refreshing) {
                return;
            }

            this.comitting = true;

            // commit as we go
            WSMenu wsMenu = ((WSMenu) getXObject().getWsObject());
            wsMenu.setDescription(descriptionText.getText());

            this.comitting = false;

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            MessageDialog.openError(this.getSite().getShell(), Messages.MenuMainPage_ErrorCommitingPage,
                    Messages.bind(Messages.MenuMainPage_ErrorCommitingPageXX, e.getLocalizedMessage()));
        }
    }

    @Override
    protected void createActions() {
    }

    /*
     * private void hookContextMenu(TreeViewer viewer) { }
     *
     * private void fillContextMenu(IMenuManager manager) { }
     */

    @Override
    public void dispose() {
        super.dispose();
        windowTarget.dispose();
    }

    class TreeEntry {

        private TreeEntry parentTreeEntry; // null for the root

        private WSMenuEntry wsMenuEntry; // null for an attribute

        public TreeEntry(TreeEntry parentTreeEntry, WSMenuEntry wsMenuEntry) {
            super();
            this.parentTreeEntry = parentTreeEntry;
            this.wsMenuEntry = wsMenuEntry;
        }

        public TreeEntry getParentTreeEntry() {
            return parentTreeEntry;
        }

        public void setParentTreeEntry(TreeEntry parentTreeEntry) {
            this.parentTreeEntry = parentTreeEntry;
        }

        public WSMenuEntry getWSMenuEntry() {
            return wsMenuEntry;
        }

        public void setWSMenuEntry(WSMenuEntry wsMenuEntry) {
            this.wsMenuEntry = wsMenuEntry;
        }

    }

    class MenuEntryDialogSelectionListener implements SelectionListener {

        protected TreeViewer viewer = null;

        protected TreeEntry treeEntry = null;

        public MenuEntryDialogSelectionListener(TreeViewer viewer, TreeEntry treeEntry) {
            super();
            this.viewer = viewer;
            this.treeEntry = treeEntry;
        }

        public void widgetSelected(SelectionEvent e) {
            MenuEntryDialog dlg = (MenuEntryDialog) ((Widget) e.getSource()).getData("dialog");//$NON-NLS-1$
            if (dlg.getReturnCode() == Window.OK) {
                String id = dlg.getIdText().getText();
                if ("".equals(id)) {//$NON-NLS-1$
                    MessageDialog.openError(viewer.getControl().getShell(), Messages._Error,
                            Messages.MenuMainPage_IDCannotbeEmpty);
                    return;
                }
                LinkedHashMap<String, String> descriptions = ((LinkedHashMap<String, String>) dlg.getDescriptionsTableViewer()
                        .getInput());
                if (descriptions.size() == 0) {
                    MessageDialog.openError(viewer.getControl().getShell(), Messages._Error, Messages.MenuMainPage_ErrorMsg);
                    return;
                }
                treeEntry.getWSMenuEntry().setId(id);
                treeEntry.getWSMenuEntry().setContext(
                        "".equals(dlg.getContextText().getText()) ? null : dlg.getContextText().getText());//$NON-NLS-1$
                treeEntry.getWSMenuEntry().setApplication(
                        "".equals(dlg.getApplicationNameText().getText()) ? null : dlg.getApplicationNameText().getText());//$NON-NLS-1$
                treeEntry.getWSMenuEntry().setIcon(
                        "".equals(dlg.getIconPathText().getText()) ? null : dlg.getIconPathText().getText());//$NON-NLS-1$
                treeEntry.getWSMenuEntry().getDescriptions().clear();
                for (String isoCode : descriptions.keySet()) {
                    WSMenuMenuEntriesDescriptions wsDescription = new WSMenuMenuEntriesDescriptions();
                    wsDescription.setLanguage(isoCode.toUpperCase());
                    wsDescription.setLabel(descriptions.get(isoCode));
                    treeEntry.getWSMenuEntry().getDescriptions().add(wsDescription);
                }
                viewer.setExpandedState(treeEntry, true);
                viewer.refresh(treeEntry, true);
                markDirtyWithoutCommit();
            }
            dlg.close();

        }

        public void widgetDefaultSelected(SelectionEvent e) {
        };
    }

    class TreeEntryEditAction extends Action {

        protected TreeViewer viewer = null;

        protected TreeEntry treeEntry = null;

        protected MenuEntryDialog dlg = null;

        public TreeEntryEditAction(TreeViewer view) {
            super();
            this.viewer = view;
            treeEntry = (TreeEntry) ((IStructuredSelection) viewer.getSelection()).getFirstElement();
            setImageDescriptor(ImageCache.getImage("icons/edit_obj.gif"));//$NON-NLS-1$
            setText(Messages.MenuMainPage_Edit);
            setToolTipText(Messages.MenuMainPage_EditThisMenuEntry);
        }

        @Override
        public void run() {
            try {
                super.run();
                // if attribute, edit parent else edit this one
                dlg = new MenuEntryDialog(treeEntry.getWSMenuEntry(), new MenuEntryDialogSelectionListener(viewer, treeEntry),
                        this.viewer.getControl().getShell(), Messages.MenuMainPage_EditTheMenuEntry
                                + treeEntry.getWSMenuEntry().getId(), false, uripre, isLocalInput(), treeObject);
                dlg.setBlockOnOpen(true);
                dlg.open();

            } catch (Exception e) {
                log.error(e.getMessage(), e);
                MessageDialog.openError(
                        viewer.getControl().getShell(),
                        Messages._Error,
                        Messages.bind(Messages.MenuMainPage_ErrorMsg1, treeEntry.getWSMenuEntry().getId(),
                                e.getLocalizedMessage()));
            }
        }

        @Override
        public void runWithEvent(Event event) {
            super.runWithEvent(event);
        }
    }// class RoleMenuTreeEditAction

    class TreeEntryAddAction extends Action {

        protected TreeViewer viewer = null;

        protected TreeEntry treeEntry = null;

        protected MenuEntryDialog dlg = null;

        protected int location = 0;;

        protected int position = 0;

        public TreeEntryAddAction(TreeViewer view, int location) {
            super();
            this.viewer = view;
            this.location = location;
            String label = "";//$NON-NLS-1$
            TreeEntry currentEntry = (TreeEntry) ((IStructuredSelection) viewer.getSelection()).getFirstElement();

            WSMenuEntry wsMenuEntry = new WSMenuEntry("", "", null, null, "", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            switch (location) {
            case LOCATION_UNDER:
                position = currentEntry.getWSMenuEntry().getSubMenus().size();
                treeEntry = new TreeEntry(currentEntry, wsMenuEntry);
                label = Messages.MenuMainPage_LaelText + currentEntry.getWSMenuEntry().getId();
                break;
            case LOCATION_BEFORE:
                position = findSubMenuPosition(currentEntry);
                treeEntry = new TreeEntry(currentEntry.getParentTreeEntry(), wsMenuEntry);
                label = Messages.MenuMainPage_LaelText1;
                break;
            case LOCATION_AFTER:
                position = findSubMenuPosition(currentEntry);
                treeEntry = new TreeEntry(currentEntry.getParentTreeEntry(), wsMenuEntry);
                label = Messages.MenuMainPage_AddAMenuEntryAfter;
                break;
            }
            setImageDescriptor(ImageCache.getImage("icons/add_obj.gif"));//$NON-NLS-1$
            setText(label);
            setToolTipText(Messages.MenuMainPage_AddAMenuEntry);
        }

        @Override
        public void run() {
            try {
                super.run();
                // if attribute, edit parent else edit this one
                dlg = new MenuEntryDialog(new WSMenuEntry(), new MenuEntryDialogSelectionListener(viewer, treeEntry), this.viewer
                        .getControl().getShell(), Messages.MenuMainPage_NewMenuEntry, uripre, isLocalInput(), treeObject);
                dlg.setBlockOnOpen(true);
                dlg.open();
                if (dlg.getReturnCode() == Window.OK) {
                    // add the entry to the WS Parent
                    addSubMenu(treeEntry, position);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                MessageDialog.openError(viewer.getControl().getShell(), Messages._Error,
                        Messages.bind(Messages.ErrorMsg1, e.getLocalizedMessage()));
            }
        }

        @Override
        public void runWithEvent(Event event) {
            super.runWithEvent(event);
        }

        protected int findSubMenuPosition(TreeEntry entry) {
            // find the position
            if (entry.getParentTreeEntry() == null) { // top level
                WSMenu menu = ((WSMenu) viewer.getInput());
                for (int i = 0; i < menu.getMenuEntries().size(); i++) {
                    if (menu.getMenuEntries().get(i).equals(entry.getWSMenuEntry())) {
                        position = i;
                        break;
                    }
                }
                return position;
            }
            // sub menu of a menu entry
            for (int i = 0; i < entry.getParentTreeEntry().getWSMenuEntry().getSubMenus().size(); i++) {
                if (entry.getParentTreeEntry().getWSMenuEntry().getSubMenus().get(i).equals(entry.getWSMenuEntry())) {
                    position = i;
                    break;
                }
            }
            return position;
        }

        protected void addSubMenu(TreeEntry entry, int position) {
            ArrayList<WSMenuEntry> list = new ArrayList<WSMenuEntry>();
            if (entry.getParentTreeEntry() == null) { // top level
                WSMenu menu = ((WSMenu) viewer.getInput());
                for (int i = 0; i < menu.getMenuEntries().size(); i++) {
                    if ((position == i) && (location == LOCATION_BEFORE)) {
                        list.add(entry.getWSMenuEntry());
                    }
                    list.add(menu.getMenuEntries().get(i));
                    if ((position == i) && (location == LOCATION_AFTER)) {
                        list.add(entry.getWSMenuEntry());
                    }
                }
                menu.getMenuEntries().clear();
                menu.getMenuEntries().addAll(list);
                viewer.setExpandedState(menu, true);
                viewer.refresh(menu, true);
                log.info(Messages.MenuMainPage_There);
                return;
            }

            // sub menu of a menu entry
            WSMenuEntry wsParent = entry.getParentTreeEntry().getWSMenuEntry();
            if ((wsParent.getSubMenus() == null) || (wsParent.getSubMenus().size() == 0)) { // no submenus yet
                log.info(Messages.MenuMainPage_here);
                wsParent.getSubMenus().clear();
                wsParent.getSubMenus().add(entry.getWSMenuEntry());
                viewer.setExpandedState(entry.getParentTreeEntry(), true);
                viewer.refresh(entry.getParentTreeEntry(), true);
                return;
            }
            for (int i = 0; i < wsParent.getSubMenus().size(); i++) {
                if ((position == i) && (location == LOCATION_BEFORE)) {
                    list.add(entry.getWSMenuEntry());
                }
                list.add(wsParent.getSubMenus().get(i));
                if ((position == i) && (location == LOCATION_AFTER)) {
                    list.add(entry.getWSMenuEntry());
                }
            }

            if (!list.contains(entry.getWSMenuEntry())) {
                list.add(entry.getWSMenuEntry());
            }

            wsParent.getSubMenus().clear();
            wsParent.getSubMenus().addAll(list);
            viewer.setExpandedState(entry.getParentTreeEntry(), true);
            viewer.refresh(entry.getParentTreeEntry(), true);
        }
    }// class RoleMenuTreeAddAction

    class TreeEntryDeleteAction extends Action {

        protected TreeViewer viewer = null;

        protected java.util.List<TreeEntry> treeEntries = null;

        protected MenuEntryDialog dlg = null;

        public TreeEntryDeleteAction(TreeViewer view) {
            super();
            this.viewer = view;
            treeEntries = ((IStructuredSelection) viewer.getSelection()).toList();
            setImageDescriptor(ImageCache.getImage("icons/delete_obj.gif"));//$NON-NLS-1$
            setText(Messages.MenuMainPage_DelEntry);
            setToolTipText(Messages.MenuMainPage_DelMenuEntry);
        }

        @Override
        public void run() {
            TreeEntry curRemoved = treeEntries.get(0);
            boolean refreshAll = false;
            try {
                Set<TreeEntry> refreshEntries = new HashSet<MenuMainPage.TreeEntry>();
                for (TreeEntry treeEntry : treeEntries) {
                    curRemoved = treeEntry;
                    if (treeEntry.getParentTreeEntry() == null) { // top level menu
                        WSMenu menu = ((WSMenu) viewer.getInput());
                        if (menu.getMenuEntries().size() == 1) {
                            MessageDialog.openWarning(MenuMainPage.this.getSite().getShell(),
                                    Messages.MenuMainPage_MenuEntryWarning, Messages.MenuMainPage_ErrorMsg2);
                            return;
                        }
                        menu.getMenuEntries().remove(treeEntry.getWSMenuEntry());
                        refreshAll = true;
                    } else {
                        // sub Menu Entry of a sub menu
                        TreeEntry parentTreeEntry = treeEntry.getParentTreeEntry();
                        parentTreeEntry.getWSMenuEntry().getSubMenus().remove(treeEntry.getWSMenuEntry());
                        refreshEntries.add(parentTreeEntry);
                    }
                }

                // refresh the viewer
                if (refreshAll) {
                    viewer.refresh();
                } else {
                    for (TreeEntry parentTreeEntry : refreshEntries) {
                        viewer.setExpandedState(parentTreeEntry, true);
                        viewer.refresh(parentTreeEntry, false);
                    }
                }
                // mark dirty
                markDirtyWithoutCommit();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                MessageDialog.openError(
                        viewer.getControl().getShell(),
                        Messages._Error,
                        Messages.bind(Messages.MenuMainPage_ErrorMsg3, curRemoved.getWSMenuEntry().getId(),
                                e.getLocalizedMessage()));
            }
        }

        @Override
        public void runWithEvent(Event event) {
            super.runWithEvent(event);
        }
    }// class RoleMenuTreeDeleteAction

    /****************************************************************************
     * DND
     ****************************************************************************/

    class DCDragSourceListener implements DragSourceListener {

        private int selected;

        public void dragFinished(DragSourceEvent event) {
            Control control = ((DragSource) event.widget).getControl();
            if ((control instanceof List) && ((event.detail & DND.DROP_MOVE) == DND.DROP_MOVE)) {
                ((List) control).remove(selected);
                MenuMainPage.this.markDirtyWithoutCommit();
            }
        }

        public void dragSetData(DragSourceEvent event) {
            Control control = ((DragSource) event.widget).getControl();
            if ((control instanceof List)) {
                if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
                    this.selected = ((List) control).getSelectionIndex();
                    event.data = ((List) control).getSelection()[0];
                }
            }
        }

        public void dragStart(DragSourceEvent event) {
            Control control = ((DragSource) event.widget).getControl();
            if ((control instanceof List)) {
                event.doit = (((List) control).getItemCount() > 0);
            }
        }
    }

    class DCDropTargetListener implements DropTargetListener {

        public void dragEnter(DropTargetEvent event) {
            // priority to copy
            if ((event.operations & DND.DROP_COPY) == DND.DROP_COPY) {
                event.detail = DND.DROP_COPY;
            } else if ((event.operations & DND.DROP_MOVE) == DND.DROP_MOVE) {
                event.detail = DND.DROP_MOVE;
            } else {
                event.detail = DND.DROP_NONE;
            }
        }

        public void dragLeave(DropTargetEvent event) {
        }

        public void dragOperationChanged(DropTargetEvent event) {
        }

        public void dragOver(DropTargetEvent event) {
        }

        public void drop(DropTargetEvent event) {
            Control control = ((DropTarget) event.widget).getControl();
            if ((control instanceof List) && ((event.operations & DND.DROP_COPY) == DND.DROP_COPY)) {
                if (TextTransfer.getInstance().isSupportedType(event.currentDataType)) {
                    if (!Arrays.asList(((List) control).getItems()).contains(event.data)) {
                        ((List) control).add((String) event.data);
                        MenuMainPage.this.markDirtyWithoutCommit();
                    }
                }
            }
        }

        public void dropAccept(DropTargetEvent event) {
        }

    }

}
