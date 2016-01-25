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
package com.amalto.workbench.views;

import java.lang.reflect.InvocationTargetException;
import java.net.Authenticator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.UIJob;
import org.talend.mdm.commmon.util.core.CommonUtil;
import org.talend.mdm.commmon.util.webapp.XSystemObjects;

import com.amalto.workbench.actions.AServerViewAction;
import com.amalto.workbench.actions.BrowseRevisionAction;
import com.amalto.workbench.actions.BrowseViewAction;
import com.amalto.workbench.actions.CopyXObjectAction;
import com.amalto.workbench.actions.DeleteJobAction;
import com.amalto.workbench.actions.DeleteXObjectAction;
import com.amalto.workbench.actions.DuplicateXObjectAction;
import com.amalto.workbench.actions.EditXObjectAction;
import com.amalto.workbench.actions.GenerateJobDefaultTransformerAction;
import com.amalto.workbench.actions.GenerateJobDefaultTriggerAction;
import com.amalto.workbench.actions.ImportTISJobAction;
import com.amalto.workbench.actions.NewCategoryAction;
import com.amalto.workbench.actions.NewXObjectAction;
import com.amalto.workbench.actions.PasteXObjectAction;
import com.amalto.workbench.actions.RefreshAllServerAction;
import com.amalto.workbench.actions.RefreshXObjectAction;
import com.amalto.workbench.actions.RenameXObjectAction;
import com.amalto.workbench.actions.ServerEditAction;
import com.amalto.workbench.actions.ServerLogoutAction;
import com.amalto.workbench.actions.ServerRefreshAction;
import com.amalto.workbench.actions.ServerRefreshCacheAction;
import com.amalto.workbench.availablemodel.AvailableModelUtil;
import com.amalto.workbench.availablemodel.IAvailableModel;
import com.amalto.workbench.dialogs.ErrorExceptionDialog;
import com.amalto.workbench.export.ExportItemsAction;
import com.amalto.workbench.export.ImportItemsAction;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.models.IXObjectModelListener;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.models.TreeObjectTransfer;
import com.amalto.workbench.models.TreeParent;
import com.amalto.workbench.providers.ServerTreeContentProvider;
import com.amalto.workbench.providers.ServerTreeLabelProvider;
import com.amalto.workbench.providers.XtentisServerObjectsRetriever;
import com.amalto.workbench.utils.LocalTreeObjectRepository;
import com.amalto.workbench.utils.MDMServerDef;
import com.amalto.workbench.utils.MDMServerHelper;
import com.amalto.workbench.utils.UserInfo;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.utils.WorkbenchClipboard;
import com.amalto.workbench.utils.XtentisException;
import com.amalto.workbench.webservices.XtentisPort;

/**
 * The view allowing administration of the "+IConstants.TALEND+" Server
 * 
 * @author Bruno Grieder
 * 
 */
public class ServerView extends ViewPart implements IXObjectModelListener {

    private static final Log log = LogFactory.getLog(ServerView.class);

    private static final String PENDING_TREE_OBJECT = "PENDING";//$NON-NLS-1$

    public static final String VIEW_ID = "org.talend.mdm.workbench.views.ServerView";//$NON-NLS-1$

    protected TreeViewer viewer;

    protected DrillDownAdapter drillDownAdapter;

    protected Action logoutAction;

    protected Action editServerAction;

    protected Action newXObjectAction;

    protected Action renameXObjectAction;

    protected Action editXObjectAction;

    protected Action deleteXObjectAction;

    protected Action serverRefreshAction;

    protected Action serverRefreshCacheAction;

    protected Action refreshAllServerAction;

    protected Action browseViewAction;

    protected Action copyAction;

    protected Action pasteAction;

    protected Action duplicateAction;

    protected Action exportAction;

    protected Action importAction;

    protected Action newCategoryAction;

    // test for NewUserAction

    private ServerTreeContentProvider contentProvider;

    private ArrayList<TreeObject> dndTreeObjs = new ArrayList<TreeObject>();

    private int dragType = -1;

    private BrowseRevisionAction browseRevisionAction;

    /**********************************************************************************
     * The VIEW
     * 
     **********************************************************************************/

    /**
     * The constructor.
     */
    public ServerView() {
    }

    public static ServerView show() {
        IViewPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(VIEW_ID);
        if (part == null) {
            try {
                part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(VIEW_ID);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return (ServerView) part;
    }

    public TreeParent getRoot() {
        return contentProvider.getInvisibleRoot();
    }

    public java.util.List<XtentisPort> getPorts() {
        java.util.List<XtentisPort> ports = new ArrayList<XtentisPort>();
        try {
            TreeObject[] servers = contentProvider.getInvisibleRoot().getChildren();
            for (TreeObject server : servers) {
                if (server instanceof TreeParent) {
                    if (!isServerPending((TreeParent) server))
                        ports.add(Util.getPort(server));
                }
            }
        } catch (XtentisException e) {
            log.error(e.getMessage(), e);
        }
        return ports;
    }

    public java.util.List<TreeParent> getServers() {
        java.util.List<TreeParent> servs = new ArrayList<TreeParent>();
        TreeObject[] servers = contentProvider.getInvisibleRoot().getChildren();
        for (TreeObject server : servers) {
            if (server instanceof TreeParent) {
                if (!isServerPending((TreeParent) server)) {
                    servs.add((TreeParent) server);
                }
            }
        }
        return servs;
    }

    public static boolean isServerPending(TreeParent server) {
        return ((server.getChildren().length == 1 && (server).getChildren()[0].getName().equals(PENDING_TREE_OBJECT)));
    }

    private DragSource createTreeDragSource() {
        DragSource dragSource = new DragSource(viewer.getTree(), DND.DROP_MOVE | DND.DROP_COPY);
        dragSource.setTransfer(new Transfer[] { TreeObjectTransfer.getInstance() });
        dragSource.addDragListener(new DragSourceListener() {

            IStructuredSelection dndSelection = null;

            public void dragStart(DragSourceEvent event) {
                dragType = -1;
                dndSelection = (IStructuredSelection) viewer.getSelection();
                event.doit = dndSelection.size() > 0;
                dndTreeObjs.clear();
                for (Iterator<TreeObject> iter = dndSelection.iterator(); iter.hasNext();) {
                    TreeObject xobject = iter.next();
                    dndTreeObjs.add(xobject);
                    if ((dragType != -1 && dragType != xobject.getType()) || xobject.getType() == TreeObject.CATEGORY_FOLDER
                            || xobject.getParent().getType() == TreeObject.RESOURCES
                            || (LocalTreeObjectRepository.getInstance().isInSystemCatalog(xobject))
                            || (xobject.getServerRoot() == xobject.getParent())) {
                        event.doit = false;
                        break;
                    } else {
                        dragType = xobject.getType();
                    }
                }
            }

            public void dragFinished(DragSourceEvent event) {
                dndSelection = null;
            }

            public void dragSetData(DragSourceEvent event) {
                if (dndSelection == null || dndSelection.size() == 0)
                    return;
                if (!TreeObjectTransfer.getInstance().isSupportedType(event.dataType))
                    return;

                TreeObject[] sourceObjs = new TreeObject[dndSelection.size()];
                int index = 0;
                for (Iterator<TreeObject> iter = dndSelection.iterator(); iter.hasNext();) {
                    TreeObject xobject = iter.next();
                    sourceObjs[index++] = xobject;
                }
                event.data = sourceObjs;
            }
        });

        return dragSource;
    }

    private DropTarget createTreeDropTarget() {
        DropTarget dropTarget = new DropTarget(viewer.getTree(), DND.DROP_MOVE | DND.DROP_COPY);
        dropTarget.setTransfer(new Transfer[] { TreeObjectTransfer.getInstance() });
        dropTarget.addDropListener(new DropTargetAdapter() {

            @Override
            public void dragEnter(DropTargetEvent event) {
            }

            @Override
            public void dragLeave(DropTargetEvent event) {
            }

            @Override
            public void dragOver(DropTargetEvent event) {
                dropTargetValidate(event);
                event.feedback |= DND.FEEDBACK_EXPAND | DND.FEEDBACK_SCROLL;
            }

            @Override
            public void drop(DropTargetEvent event) {
                resetTargetTreeObject(event);
                if (dropTargetValidate(event))
                    dropTargetHandleDrop(event);
            }

            private void resetTargetTreeObject(DropTargetEvent event) {
                // Determine the target XObject for the drop
                IStructuredSelection dndSelection = (IStructuredSelection) viewer.getSelection();
                for (Iterator<TreeObject> iter = dndSelection.iterator(); iter.hasNext();) {
                    TreeObject xobject = iter.next();
                    if (!xobject.isXObject() && xobject instanceof TreeParent) {
                        if (dndTreeObjs.indexOf(xobject) >= 0)
                            dndTreeObjs.remove(xobject);
                        TreeParent dir = (TreeParent) xobject;
                        for (TreeObject treeObj : dir.getChildren()) {
                            if (dndTreeObjs.indexOf(treeObj) == -1)
                                dndTreeObjs.add(treeObj);
                        }
                    }
                }
            }
        });

        return dropTarget;
    }

    private boolean dropTargetValidate(DropTargetEvent event) {

        if (event.item == null)
            return false;
        Object obj = event.item.getData();
        if (obj instanceof TreeObject) {
            TreeObject treeObj = (TreeObject) obj;
            if (treeObj.getParent() == null)
                log.info(treeObj.getDisplayName());
            int xtentisType = LocalTreeObjectRepository.getInstance().receiveUnCertainTreeObjectType(treeObj);
            if ((treeObj.getType() != dragType && treeObj.getType() != TreeObject.CATEGORY_FOLDER && !(dragType == TreeObject.JOB
                    || dragType == TreeObject.TIS_JOB || dragType == TreeObject.WORKFLOW_PROCESS))
                    || dragType == TreeObject.CATEGORY_FOLDER
                    || dragType == TreeObject.DATA_MODEL_RESOURCE
                    || dragType == TreeObject.DATA_MODEL_TYPES_RESOURCE
                    || dragType == TreeObject.CUSTOM_TYPES_RESOURCE
                    || dragType == TreeObject.PICTURES_RESOURCE
                    || (treeObj.getType() == TreeObject.CATEGORY_FOLDER && xtentisType != dragType && !(dragType == TreeObject.JOB
                            || dragType == TreeObject.TIS_JOB || dragType == TreeObject.WORKFLOW_PROCESS))
                    || (treeObj.getType() == TreeObject.CATEGORY_FOLDER && treeObj.getParent().getType() == dragType && treeObj
                            .getDisplayName().equals("System"))//$NON-NLS-1$
                    || (LocalTreeObjectRepository.getInstance().isInSystemCatalog(treeObj.getParent()))) {
                event.detail = DND.DROP_NONE;
            } else {
                for (TreeObject tos : dndTreeObjs) {
                    if (tos == obj) {
                        event.detail = DND.DROP_LINK;
                        break;
                    } else {
                        if (tos.getParent().getType() == TreeObject.CATEGORY_FOLDER
                                && tos.getParent().getDisplayName().equals("System")) {//$NON-NLS-1$
                            event.detail = DND.DROP_NONE;
                        }
                        event.detail = DND.DROP_MOVE;
                    }
                }
            }
        }

        return event.detail != DND.DROP_NONE;
    }

    private void dropTargetHandleDrop(DropTargetEvent event) {
        TreeObject remoteObj = (TreeObject) event.item.getData();
        TreeParent parent = null;
        if (remoteObj instanceof TreeParent)
            parent = (TreeParent) remoteObj;
        else
            parent = remoteObj.getParent();
        if (parent.getParent().getType() == TreeObject.RESOURCES)
            return;
        // only for transfer
        ArrayList<TreeObject> subDdnList = new ArrayList<TreeObject>();

        if (parent != null) {
            for (TreeObject xobj : dndTreeObjs) {
                if (xobj.getServerRoot().getDisplayName().equals(remoteObj.getServerRoot().getDisplayName())) {
                    if (xobj.getParent() != remoteObj.getParent() && remoteObj.isXObject()) {
                        subDdnList.add(xobj);
                    } else if (xobj.getParent() != remoteObj && remoteObj instanceof TreeParent) {
                        subDdnList.add(xobj);
                    }
                    if (xobj.getType() == TreeObject.JOB || xobj.getType() == TreeObject.WORKFLOW_PROCESS)
                        subDdnList.add(xobj);
                }
            }
            dndTreeObjs.removeAll(subDdnList);
        }

        transformCatalog(remoteObj, subDdnList);

        if (!dndTreeObjs.isEmpty()) {
            WorkbenchClipboard.getWorkbenchClipboard().get().clear();
            WorkbenchClipboard.getWorkbenchClipboard().get().addAll(dndTreeObjs);
            ((PasteXObjectAction) pasteAction).setXtentisPort(remoteObj);
            ((PasteXObjectAction) pasteAction).setParent(remoteObj instanceof TreeParent ? (TreeParent) remoteObj : remoteObj
                    .getParent());
            pasteAction.run();
        }
    }

    public void forceAllSiteToRefresh() {
    }

    private void transformCatalog(TreeObject remoteObj, ArrayList<TreeObject> transferList) {
        boolean transform = false;
        TreeParent catalog = remoteObj instanceof TreeParent ? (TreeParent) remoteObj : remoteObj.getParent();
        for (TreeObject theObj : transferList) {
            theObj.getParent().removeChild(theObj);
            catalog.addChild(theObj);
            theObj.setServerRoot(catalog.getServerRoot());
            transform = true;
        }

        if (transform && getTreeContentProvider().getInvisibleRoot().getChildren().length > 1) {
            forceAllSiteToRefresh();
        }
        getViewer().refresh(false);
    }

    protected class DCDragSourceListener implements DragSourceListener {

        private int selected;

        public void dragFinished(DragSourceEvent event) {
            Control control = ((DragSource) event.widget).getControl();
            if ((control instanceof List) && ((event.detail & DND.DROP_MOVE) == DND.DROP_MOVE)) {
                ((List) control).remove(selected);
            }
        }

        public void dragSetData(DragSourceEvent event) {
            Control control = ((DragSource) event.widget).getControl();
            if ((control instanceof List))
                if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
                    this.selected = ((List) control).getSelectionIndex();
                    event.data = ((List) control).getSelection()[0];
                }
        }

        public void dragStart(DragSourceEvent event) {
            Control control = ((DragSource) event.widget).getControl();
            if (control instanceof Tree) {
                IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
                if (selection.size() == 0) {
                    event.doit = false;
                }
            }
        }

    }

    protected TreeViewer createTreeViewer(Composite parent) {
        viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL) {

            @Override
            public Object[] getSortedChildren(Object obj) {
                Object[] objs = super.getSortedChildren(obj);
                Arrays.sort(objs);
                return objs;
            }
        };

        viewer.addTreeListener(new ITreeViewerListener() {

            public void treeCollapsed(TreeExpansionEvent event) {
                setTreeNodeImage(event, false);
            }

            public void treeExpanded(TreeExpansionEvent event) {
                setTreeNodeImage(event, true);
            }

            private void setTreeNodeImage(TreeExpansionEvent event, boolean expand) {
                Object elem = event.getElement();
                if (!(elem instanceof TreeParent))
                    return;
                TreeParent parent = (TreeParent) elem;
                if (parent.getType() == TreeObject.BUILT_IN_CATEGORY_FOLDER) {
                    Image imgOpen = ImageCache.getCreatedImage("icons/folder_open_deployed-jobs.png");//$NON-NLS-1$
                    Image imgClose = ImageCache.getCreatedImage("icons/folder_deployed-jobs.png");//$NON-NLS-1$
                    if (parent.getDisplayName().equals("Source Jobs")) {//$NON-NLS-1$
                        imgOpen = ImageCache.getCreatedImage("icons/folder_open_source-jobs.png");//$NON-NLS-1$
                        imgClose = ImageCache.getCreatedImage("icons/folder_source-jobs.png");//$NON-NLS-1$
                    }
                    Widget widget = ServerView.this.getViewer().testFindItem(event.getElement());
                    TreeItem item = (TreeItem) widget;
                    item.setImage(expand ? imgOpen : imgClose);
                }
            }
        });
        ColumnViewerToolTipSupport.enableFor(viewer);
        return viewer;
    }

    /**
     * This is a callback that will allow us to create the viewer and initialize it.
     */
    @Override
    public void createPartControl(Composite parent) {

        viewer = createTreeViewer(parent);
        drillDownAdapter = new DrillDownAdapter(viewer);
        contentProvider = new ServerTreeContentProvider(this.getSite(), new TreeParent("INVISIBLE ROOT", null, TreeObject._ROOT_,//$NON-NLS-1$
                null, null));
        setTreeContentProvider(contentProvider);
        viewer.setLabelProvider(new ServerTreeLabelProvider());
        viewer.setSorter(new ViewerSorter() {

            @Override
            public int category(Object element) {
                if (element instanceof TreeParent) {
                    TreeParent category = (TreeParent) element;
                    if (category.getType() == TreeObject.CATEGORY_FOLDER)
                        return -1;
                }
                return 0;
            }
        });
        viewer.setInput(getViewSite());
        if (getSite().getWorkbenchWindow().getActivePage() != null) {
            getSite().getWorkbenchWindow().getActivePage().addPartListener(new IPartListener2() {

                public void partVisible(IWorkbenchPartReference partRef) {
                }

                public void partOpened(IWorkbenchPartReference partRef) {
                }

                public void partInputChanged(IWorkbenchPartReference partRef) {
                }

                public void partHidden(IWorkbenchPartReference partRef) {
                }

                public void partDeactivated(IWorkbenchPartReference partRef) {
                }

                public void partClosed(IWorkbenchPartReference partRef) {
                    System.gc();
                }

                public void partBroughtToTop(IWorkbenchPartReference partRef) {
                }

                public void partActivated(IWorkbenchPartReference partRef) {
                }
            });
        }
        ;
        viewer.getTree().addTreeListener(new TreeListener() {

            public void treeExpanded(TreeEvent event) {
                if (event.item.getData() instanceof TreeParent
                        && ((TreeParent) event.item.getData()).getType() == TreeObject._SERVER_
                        && ((TreeParent) event.item.getData()).getChildrenList().size() == 1) {
                    // ((TreeParent) event.item.getData()).getChildrenList().clear();
                    initServer((TreeParent) event.item.getData());
                }

            }

            public void treeCollapsed(TreeEvent e) {

            }
        });

        createTreeDragSource();
        createTreeDropTarget();
        makeActions();
        hookContextMenu();
        hookDoubleClickAction();
        hookKeyPressAction();
        contributeToActionBars();
        // hookKeyboard();
        initView();
    }

    protected void initServer(TreeParent server) {

        // add by ymli; fix the bug:0012600.made the refresh as a job run underground
        refreshServerRoot(this, server);

    }

    /**
     * @author ymli; fix the bug:0012600. made the refresh as a job run underground
     * @param view
     * @param serverRoot
     * @return
     */
    public boolean refreshServerRoot(final ServerView view, final TreeParent serverRoot) {
        UIJob job = new UIJob(Messages.ServerView_) {

            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
                try {

                    Cursor wait = new Cursor(getViewSite().getShell().getDisplay(), SWT.CURSOR_WAIT);
                    viewer.getControl().setCursor(wait);
                    new ServerRefreshAction(view, serverRoot).doRun();
                    viewer.expandToLevel(serverRoot, 1);

                    return Status.OK_STATUS;
                } catch (Exception e) {
                    MessageDialog.openError(view.getSite().getShell(), Messages._Error, e.getLocalizedMessage());
                    viewer.collapseToLevel(serverRoot, 1);
                    return Status.CANCEL_STATUS;
                } finally {
                    viewer.getControl().setCursor(new Cursor(getViewSite().getShell().getDisplay(), SWT.CURSOR_ARROW));
                }
            }
        };
        job.setPriority(Job.LONG);
        job.schedule();

        return true;

    }

    public void initView() {
        java.util.List<MDMServerDef> servers = MDMServerHelper.getServers();
        for (MDMServerDef server : servers) {
            initServerTreeParent(server);
        }
    }

    private void hookContextMenu() {
        MenuManager menuMgr = new MenuManager("#PopupMenu");//$NON-NLS-1$
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {

            public void menuAboutToShow(IMenuManager manager) {
                ServerView.this.fillContextMenu(manager);
            }
        });
        Menu menu = menuMgr.createContextMenu(viewer.getControl());
        viewer.getControl().setMenu(menu);
        getSite().registerContextMenu(menuMgr, viewer);
    }

    private void contributeToActionBars() {
        IActionBars bars = getViewSite().getActionBars();
        fillLocalToolBar(bars.getToolBarManager());
    }

    protected void fillContextMenu(IMenuManager manager) {
        TreeObject xobject = (TreeObject) ((IStructuredSelection) viewer.getSelection()).getFirstElement();
        try {
            XtentisPort port = Util.getPort(xobject);
            if (port == null)
                return;

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        if (xobject == null) {
            // manager.add(loginAction);
        } else {
            // available models

            switch (xobject.getType()) {
            case TreeObject._SERVER_:
                manager.add(serverRefreshAction);
                manager.add(serverRefreshCacheAction);
                manager.add(new Separator());
                manager.add(importAction);
                manager.add(exportAction);
                manager.add(new Separator());
                manager.add(logoutAction);
                manager.add(editServerAction);

                if (!WorkbenchClipboard.getWorkbenchClipboard().isEmpty())
                    manager.add(pasteAction);

                break;

            case TreeObject._ACTION_:
                manager.add((Action) xobject.getWsObject());
                break;
            case TreeObject.SUBSCRIPTION_ENGINE:
                manager.add(browseViewAction);
                break;
            case TreeObject.CUSTOM_TYPE:
            case TreeObject.CUSTOM_TYPES_RESOURCE:
                // manager.add(uploadCustomTypeAction);
                break;
            case TreeObject.SERVICE_CONFIGURATION:
            case TreeObject.RESOURCES:
            case TreeObject.DATA_MODEL_RESOURCE:
            case TreeObject.DATA_MODEL_TYPES_RESOURCE:
                break;
            case TreeObject.PICTURES_RESOURCE:
                manager.add(exportAction);
                manager.add(importAction);
                break;
            case TreeObject.DATA_CLUSTER:
                if (xobject.isXObject()) {
                    manager.add(browseViewAction);
                }

            case TreeObject.ROLE:
            case TreeObject.VIEW:
            default:
                if (xobject.getType() != TreeObject.CATEGORY_FOLDER && xobject.getType() != TreeObject.BUILT_IN_CATEGORY_FOLDER) {
                    manager.add(exportAction);
                    manager.add(importAction);
                }
                if (xobject.getType() == TreeObject.VIEW && xobject.isXObject()) {
                    manager.add(browseViewAction);
                }
                if (xobject.getType() == TreeObject.JOB_REGISTRY) {
                    manager.add(new ImportTISJobAction());
                    manager.add(new RefreshXObjectAction(ServerView.show(), xobject));
                }
                if (xobject.getType() == TreeObject.JOB) {
                    manager.add(new DeleteJobAction());
                    manager.add(new GenerateJobDefaultTransformerAction());
                    manager.add(new GenerateJobDefaultTriggerAction());
                }
                int type = LocalTreeObjectRepository.getInstance().receiveUnCertainTreeObjectType(xobject);
                if (!LocalTreeObjectRepository.getInstance().isInSystemCatalog(xobject)
                        && xobject.getType() != TreeObject.WORKFLOW_PROCESS && xobject.getType() != TreeObject.JOB
                        && xobject.getType() != TreeObject.WORKFLOW && xobject.getType() != TreeObject.JOB_REGISTRY) {
                    if (type != TreeObject.ROLE && xobject.getType() != TreeObject.RESOURCES
                            && xobject.getType() != TreeObject.DATA_MODEL_RESOURCE
                            && xobject.getType() != TreeObject.DATA_MODEL_TYPES_RESOURCE
                            && xobject.getType() != TreeObject.CUSTOM_TYPES_RESOURCE
                            && xobject.getType() != TreeObject.PICTURES_RESOURCE
                            && xobject.getType() != TreeObject.BUILT_IN_CATEGORY_FOLDER
                            && xobject.getType() != TreeObject.EVENT_MANAGEMENT)
                        manager.add(newXObjectAction);
                    // edit by ymli; fix the bug:0012191; if the object is DATA_CLUSTER, refused to rename.
                    // if(!(xobject instanceof TreeParent))
                    if (!(xobject instanceof TreeParent) && xobject.getType() != TreeObject.DATA_CLUSTER)
                        manager.add(renameXObjectAction);
                }

                if (Util.IsEnterPrise() && Util.hasUniverse(xobject))
                    manager.add(browseRevisionAction);

                // if(xobject.getType()!=TreeObject.WORKFLOW_PROCESS && xobject.getType()!=TreeObject.JOB &&
                // Util.hasTags(xobject))
                // manager.add(versionAction);

                if (xobject.getType() != TreeObject.WORKFLOW_PROCESS && xobject.getType() != TreeObject.JOB
                        && xobject.isXObject() && !XSystemObjects.isExist(xobject.getType(), xobject.getDisplayName())) {
                    manager.add(editXObjectAction);
                    manager.add(deleteXObjectAction);
                    manager.add(copyAction);
                    manager.add(duplicateAction);
                } else if (xobject.getType() != TreeObject.EVENT_MANAGEMENT && xobject.getType() != TreeObject.JOB_REGISTRY
                        && xobject.getType() != TreeObject.JOB && xobject.getType() != TreeObject.BUILT_IN_CATEGORY_FOLDER
                        && LocalTreeObjectRepository.getInstance().isInSystemCatalog(xobject) == false) {
                    manager.add(newCategoryAction);
                }

                if (xobject.getType() == TreeObject.CATEGORY_FOLDER
                        && LocalTreeObjectRepository.getInstance().isInSystemCatalog(xobject) == false) {
                    manager.add(deleteXObjectAction);
                }
                if (!WorkbenchClipboard.getWorkbenchClipboard().isEmpty()) {
                    // modifier:fiu see bug 0008905
                    TreeObject remoteObj = (TreeObject) ((IStructuredSelection) viewer.getSelection()).getFirstElement();
                    ((PasteXObjectAction) pasteAction).setXtentisPort(remoteObj);
                    ((PasteXObjectAction) pasteAction).setParent(remoteObj instanceof TreeParent ? (TreeParent) remoteObj
                            : remoteObj.getParent());
                    manager.add(pasteAction);
                }

            }

            java.util.List<IAvailableModel> availablemodels = AvailableModelUtil.getAvailableModels();
            cutAllAvailableModelFromRepository(availablemodels);
            for (IAvailableModel model : availablemodels) {
                model.fillContextMenu(xobject, manager);
            }
        }
        manager.add(new Separator());
        drillDownAdapter.addNavigationActions(manager);
        // Other plug-ins can contribute there actions here
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }

    private void cutAllAvailableModelFromRepository(java.util.List<IAvailableModel> availablemodels) {
        java.util.List<IAvailableModel> cutAvailableModels = new java.util.ArrayList<IAvailableModel>();
        for (IAvailableModel model : availablemodels) {
            if (model.getClass().getName().contains("ExtraAccessAvailableModelR") //$NON-NLS-1$
                    || model.getClass().getName().contains("WorkflowAvailableModelR") //$NON-NLS-1$
                    || model.getClass().getName().contains("SchematronAvailableModelR") //$NON-NLS-1$
                    || model.getClass().getName().contains("ResourceAvailableModelR") //$NON-NLS-1$
                    || model.getClass().getName().contains("SyncAvailableModelR") //$NON-NLS-1$
                    || model.getClass().getName().contains("RoleAvailableModelR") //$NON-NLS-1$
                    || model.getClass().getName().contains("UniverseAvailableModelR")) { //$NON-NLS-1$
                cutAvailableModels.add(model);
            }
        }
        for (IAvailableModel model : cutAvailableModels) {
            availablemodels.remove(model);
        }
    }

    private void fillLocalToolBar(IToolBarManager manager) {

        manager.add(refreshAllServerAction);
        manager.add(new Separator());
        manager.add(new Separator());
        drillDownAdapter.addNavigationActions(manager);
    }

    private void makeActions() {

        logoutAction = new ServerLogoutAction(this, false);
        editServerAction = new ServerEditAction(this);

        editXObjectAction = new EditXObjectAction(this);
        newXObjectAction = new NewXObjectAction(this);
        renameXObjectAction = new RenameXObjectAction(this);
        browseRevisionAction = new BrowseRevisionAction(this);
        deleteXObjectAction = new DeleteXObjectAction(this);
        serverRefreshAction = new ServerRefreshAction(this);
        serverRefreshCacheAction = new ServerRefreshCacheAction(this);
        refreshAllServerAction = new RefreshAllServerAction(this);

        browseViewAction = new BrowseViewAction(this);
        copyAction = new CopyXObjectAction(this);
        pasteAction = new PasteXObjectAction(this);
        duplicateAction = new DuplicateXObjectAction(this);

        exportAction = new ExportItemsAction(this);
        importAction = new ImportItemsAction(this);
        newCategoryAction = new NewCategoryAction(this);
    }

    private void hookDoubleClickAction() {
        viewer.addDoubleClickListener(new IDoubleClickListener() {

            public void doubleClick(DoubleClickEvent event) {
                ISelection selection = ServerView.this.getViewer().getSelection();
                TreeObject xo = (TreeObject) ((IStructuredSelection) selection).getFirstElement();
                // add by jsxie to fix the bug 22575
                if (xo == null)
                    return;

                if (xo.getType() == TreeObject._ACTION_) {
                    Class<?> actionClass = (Class<?>) xo.getWsKey();
                    try {
                        AServerViewAction action = (AServerViewAction) actionClass.newInstance();
                        action.setServerView(ServerView.this);
                        action.run();
                    } catch (Exception ex) {
                        MessageDialog.openError(viewer.getControl().getShell(), Messages._Error, Messages.ServerView_ErrorMsg);
                    }
                    return;
                }// if action
                if (xo.getType() == TreeObject._SERVER_) {
                    if (((TreeParent) xo).getChildrenList().size() == 1)
                        initServer((TreeParent) xo);
                }
                if (xo.getType() == TreeObject.WORKFLOW)
                    return;
                if (xo.getWsObject() instanceof Action) {
                    ((Action) xo.getWsObject()).run();
                    return;
                }
                if (xo.getType() == TreeObject.SUBSCRIPTION_ENGINE || (xo.getType() == TreeObject.DATA_CLUSTER && xo.isXObject())
                        || xo.getType() == TreeObject.WORKFLOW_PROCESS)
                    browseViewAction.run();
                else
                    editXObjectAction.run();
            }
        });
    }

    private void hookKeyPressAction() {
        viewer.getTree().addKeyListener(new KeyListener() {

            public void keyPressed(KeyEvent e) {

                ISelection selection = ServerView.this.getViewer().getSelection();
                TreeObject xo = (TreeObject) ((IStructuredSelection) selection).getFirstElement();

                // add by jsxie to fix the bug 22814
                if (xo == null)
                    return;

                // delete
                if ((e.stateMask == 0) && (e.keyCode == SWT.DEL)) {

                    switch (xo.getType()) {

                    case TreeObject.JOB:
                        new DeleteJobAction().run();
                        break;
                    default:
                        // MessageDialog.openError(getSite().getShell(),
                        // "Error", "Unknown " + IConstants.TALEND
                        // + " Object Type: " + xo.getType());
                        deleteXObjectAction.run();
                        return;
                    }// switch

                }
            }

            public void keyReleased(KeyEvent e) {

                if (e.keyCode == 'c' && e.stateMask == SWT.CTRL) {
                    copyAction.run();
                } else if (e.keyCode == 'v' && e.stateMask == SWT.CTRL) {
                    // modifier:fiu see bug 0008905
                    TreeObject xobject = (TreeObject) ((IStructuredSelection) viewer.getSelection()).getFirstElement();

                    // add by jsxie to fix the bug 22814
                    if (xobject == null)
                        return;

                    ((PasteXObjectAction) pasteAction).setXtentisPort(xobject);
                    ((PasteXObjectAction) pasteAction).setParent(xobject instanceof TreeParent ? (TreeParent) xobject : xobject
                            .getParent());
                    pasteAction.run();
                    // } else if (e.keyCode == SWT.DEL) {
                    // deleteXObjectAction.run();
                }

            }

        });

    }

    /**
     * Passing the focus request to the viewer's control.
     */
    @Override
    public void setFocus() {
        viewer.getControl().setFocus();
    }

    /**
     * Access to the Tree Model
     * 
     */
    public ServerTreeContentProvider getTreeContentProvider() {
        return (ServerTreeContentProvider) viewer.getContentProvider();
    }

    public void setTreeContentProvider(ServerTreeContentProvider treeContentProvider) {
        ServerTreeContentProvider oldProvider = (ServerTreeContentProvider) this.viewer.getContentProvider();
        if (oldProvider != null)
            oldProvider.removeListener(this);

        viewer.setContentProvider(treeContentProvider);
        treeContentProvider.addListener(this);
    }

    public void handleEvent(int type, TreeObject parent, TreeObject child) {
        switch (type) {
        case IXObjectModelListener.NEED_REFRESH:
            new ServerRefreshAction(this, (TreeParent) child).run();
            break;
        }

        this.viewer.refresh(false);
    }

    public TreeViewer getViewer() {
        return viewer;
    }

    public void setViewer(TreeViewer viewer) {
        this.viewer = viewer;
    }

    @Override
    public void dispose() {
        ServerTreeContentProvider oldProvider = (ServerTreeContentProvider) this.viewer.getContentProvider();
        if (oldProvider != null)
            oldProvider.removeListener(this);

        super.dispose();
    }

    public void initServerTreeParent(MDMServerDef server) {
        String url = server.getUrl();
        String username = server.getUser();
        String password = server.getPasswd();
        String universe = server.getUniverse();
        TreeParent serverRoot = new TreeParent(server.getName(), null, TreeObject._SERVER_, url,
                ("".equals(universe) ? "" : universe + "/") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        + username + ":" + (password == null ? "" : password)); //$NON-NLS-1$ //$NON-NLS-2$
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername(username);
        userInfo.setPassword(password);
        userInfo.setServerUrl(url);
        userInfo.setUniverse(universe);

        serverRoot.setUser(userInfo);
        if (Util.IsEnterPrise()) {
            if (universe == null || universe.length() == 0)
                universe = "HEAD";//$NON-NLS-1$
        }
        StringBuilder displayName = new StringBuilder();
        displayName.append(serverRoot.getDisplayName());
        if (universe != null && universe.length() != 0) {
            displayName.append(" ["); //$NON-NLS-1$
            displayName.append(universe);
            displayName.append("] "); //$NON-NLS-1$
        } else {
            displayName.append(" "); //$NON-NLS-1$
        }

        displayName.append(username);
        serverRoot.setDisplayName(displayName.toString());
        initServerTreeChildren(serverRoot);

        TreeParent invisibleRoot = getTreeContentProvider().getInvisibleRoot();
        invisibleRoot.addChild(serverRoot);
    }

    public void initServerTreeChildren(TreeParent serverRoot) {
        TreeObject obj = new TreeObject(PENDING_TREE_OBJECT, serverRoot, TreeObject._INVISIBLE, null, null);
        obj.setDisplayName("Pending..."); //$NON-NLS-1$
        ArrayList list = new ArrayList();
        list.add(obj);
        serverRoot.setChildren(list);
    }

    public boolean addServerTree(String name, String url, String username, String password, String universe) {

        // Remove authenticator dialog
        Authenticator.setDefault(null);

        try {
            XtentisServerObjectsRetriever retriever = new XtentisServerObjectsRetriever(name, url, username, password, universe,
                    this);
            new ProgressMonitorDialog(this.getSite().getShell()).run(true, true, retriever);

            TreeParent serverRoot = retriever.getServerRoot();
            TreeParent invisibleRoot = getTreeContentProvider().getInvisibleRoot();
            TreeObject[] serverRoots = invisibleRoot.getChildren();

            boolean found = false;
            for (int i = 0; i < serverRoots.length; i++) {
                // aiming add root displayName as unique ID of each server
                if (serverRoots[i].getDisplayName().equals(serverRoot.getDisplayName())) {
                    if (serverRoots[i].getWsKey().equals(serverRoot.getWsKey())) {
                        // server & universe already exists --> synchronize
                        if (serverRoots[i].getUser().getUniverse().equalsIgnoreCase(serverRoot.getUser().getUniverse())) {
                            found = true;
                            ((TreeParent) serverRoots[i]).synchronizeWith(serverRoot);
                        }
                    }
                }
            }
            if (!found)
                invisibleRoot.addChild(serverRoot);

            LocalTreeObjectRepository.getInstance().setLazySaveStrategy(false, serverRoot);
            getViewer().refresh();
            getViewer().expandToLevel(serverRoot, 1);

            // store server definition
            MDMServerDef serverDef = MDMServerDef.parse(url, username, password, universe, name);
            boolean saved = MDMServerHelper.saveServer(serverDef);
            if (!saved) {
                MessageDialog.openError(null, Messages._Error, Messages.ServerView_ErrorMsg1);
                return false;
            }
            return true;
        } catch (InterruptedException ie) {
            return false;
        } catch (InvocationTargetException e) {
            log.error(e.getMessage(), e);
            if(!Util.handleConnectionException(this, e, null)){
            	ErrorExceptionDialog.openError(this.getSite().getShell(), Messages._Error, CommonUtil.getErrMsgFromException(e.getCause()));
            }
            return false;
        }
    }

    private boolean isRename(MDMServerDef oldServerDef, String name, String url, String username, String password, String universe) {
        return (!oldServerDef.getName().equals(name)) && oldServerDef.getUrl().equals(url)
                && oldServerDef.getUser().equals(username) && oldServerDef.getPasswd().equals(password)
                && oldServerDef.getUniverse().equals(universe);
    }

    private boolean isSame(MDMServerDef oldServerDef, String name, String url, String username, String password, String universe) {
        return (oldServerDef.getName().equals(name)) && oldServerDef.getUrl().equals(url)
                && oldServerDef.getUser().equals(username) && oldServerDef.getPasswd().equals(password)
                && oldServerDef.getUniverse().equals(universe);
    }

    public void updateServerTree(MDMServerDef oldServerDef, String name, String url, String username, String password,
            String universe) {
        if (isSame(oldServerDef, name, url, username, password, universe))
            return;
        // Remove authenticator dialog
        Authenticator.setDefault(null);

        TreeParent serverRoot = null;
        for (TreeObject treeObj : getTreeContentProvider().getInvisibleRoot().getChildren()) {
            if (treeObj.getName().equals(oldServerDef.getName()) && treeObj.getType() == TreeObject._SERVER_) {
                serverRoot = (TreeParent) treeObj;
                break;
            }
        }
        if (isRename(oldServerDef, name, url, username, password, universe)) {

            serverRoot.setName(name);
            StringBuilder displayName = new StringBuilder();
            displayName.append(serverRoot.getName());
            String universeStr = universe;
            if (Util.IsEnterPrise()) {
                if (universe == null || universe.length() == 0)
                    universeStr = "HEAD";//$NON-NLS-1$
            }
            if (universeStr != null && universeStr.length() != 0) {
                displayName.append(" ["); //$NON-NLS-1$
                displayName.append(universeStr);
                displayName.append("] "); //$NON-NLS-1$
            } else {
                displayName.append(" "); //$NON-NLS-1$
            }

            displayName.append(username);
            serverRoot.setDisplayName(displayName.toString());
            getViewer().refresh();
            // // store server definition
            MDMServerDef newServerDef = MDMServerDef.parse(url, username, password, universe, name);
            boolean updated = MDMServerHelper.updateServer(oldServerDef, newServerDef);
            if (!updated) {
                MessageDialog.openError(null, Messages._Error, Messages.ServerView_ErrorMsg1);
                return;
            }
        } else {
            String tmp = String.valueOf(System.currentTimeMillis());
            // to prevent to delete current server when the new connection is refused
            boolean success = addServerTree(tmp, url, username, password, universe);
            if (success) {
                new ServerLogoutAction(this).run();
                MDMServerDef tmpServerDef = MDMServerDef.parse(url, username, password, universe, tmp);

                updateServerTree(tmpServerDef, name, url, username, password, universe);
            }

        }

    }

}
