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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PerspectiveAdapter;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.internal.PartSite;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.talend.commons.exception.LoginException;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.runtime.model.repository.ERepositoryStatus;
import org.talend.commons.ui.runtime.image.ECoreImage;
import org.talend.commons.ui.runtime.image.ImageProvider;
import org.talend.core.model.general.Project;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.IRepositoryFactory;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.designer.core.ui.editor.ProcessEditorInput;
import org.talend.mdm.repository.core.AbstractRepositoryAction;
import org.talend.mdm.repository.core.IServerObjectRepositoryType;
import org.talend.mdm.repository.core.command.CommandManager;
import org.talend.mdm.repository.core.service.ContainerCacheService;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.models.FolderRepositoryObject;
import org.talend.mdm.repository.plugin.RepositoryPlugin;
import org.talend.mdm.repository.ui.actions.DeployAllAction;
import org.talend.mdm.repository.ui.actions.ExportObjectAction;
import org.talend.mdm.repository.ui.actions.ImportObjectAction;
import org.talend.mdm.repository.ui.actions.ImportServerObjectAction;
import org.talend.mdm.repository.ui.actions.RefreshAction;
import org.talend.mdm.repository.ui.dialogs.SwitchPerspectiveDialog;
import org.talend.mdm.repository.ui.editors.IRepositoryViewEditorInput;
import org.talend.mdm.repository.ui.preferences.PreferenceConstants;
import org.talend.mdm.repository.ui.starting.ShowWelcomeEditor;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;
import org.talend.repository.ProjectManager;
import org.talend.repository.model.IProxyRepositoryFactory;

import com.amalto.workbench.exadapter.ExAdapterManager;
import com.amalto.workbench.views.MDMPerspective;

/**
 * DOC hbhong class global comment. Detailled comment <br/>
 *
 */
public class MDMRepositoryView extends CommonNavigator implements ITabbedPropertySheetPageContributor {

    /**
     *
     */
    private static final String JOB_EDITOR_ID = "org.talend.designer.core.ui.MultiPageTalendEditor"; //$NON-NLS-1$

    /**
     *
     */
    //    private static final String BONITA_PERSPECTIVE_ID = "org.bonitasoft.studio.perspective.process"; //$NON-NLS-1$

    public static final String CONTRIBUTER_ID = "org.talend.mdm.repository.propertycontributer"; //$NON-NLS-1$

    private static final String VIEW_CONTEXT_ID = "org.talend.mdm.repository.context"; //$NON-NLS-1$

    private static final Log log = LogFactory.getLog(MDMRepositoryView.class);

    public static final String VIEW_ID = "org.talend.mdm.repository.ui.navigator.MDMRepositoryView"; //$NON-NLS-1$

    @Override
    public void createPartControl(Composite aParent) {
        Composite mainPane = new Composite(aParent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.horizontalSpacing = 0;
        layout.marginWidth = 0;
        mainPane.setLayout(layout);
        mainPane.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

        if (exAdapter != null) {
            exAdapter.createBranchPartControl(mainPane);
        }

        Composite repositoryComp = new Composite(mainPane, SWT.NONE);
        repositoryComp.setLayout(new FillLayout());
        repositoryComp.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
        super.createPartControl(repositoryComp);

        initInput();
        registerEditorListener();
        contributeToActionBars();
        activateContext();

        if (exAdapter != null) {
            exAdapter.refreshBranchPartControl();
        }
        // new added
        regisitPerspectiveBarSelectListener();
    }

    @Override
    protected CommonViewer createCommonViewerObject(Composite aParent) {
        CommonViewer viewer = super.createCommonViewerObject(aParent);
        viewer.setLabelProvider(new MDMNavigatorDecoratingLabelProvider(viewer.getNavigatorContentService()
                .createCommonLabelProvider()));
        viewer.addTreeListener(new TreeViewerListener(viewer));
        ColumnViewerToolTipSupport.enableFor(viewer);
        return viewer;

    }

    /**
     * Activate a context that this view uses. It will be tied to this view activation events and will be removed when
     * the view is disposed.
     */
    private void activateContext() {
        IContextService contextService = (IContextService) getSite().getService(IContextService.class);
        contextService.activateContext(VIEW_CONTEXT_ID);
    }

    @Override
    public void dispose() {
        unRegisterEditorListener();
        unRegisitPerspectiveBarSelectListener();
        super.dispose();
    }

    private void contributeToActionBars() {
        IActionBars bars = getViewSite().getActionBars();
        fillLocalToolBar(bars.getToolBarManager());
    }

    private void fillLocalToolBar(IToolBarManager manager) {
        RefreshAction refreshViewAction = new RefreshAction(true);
        refreshViewAction.initCommonViewer(((CommonNavigator) this).getCommonViewer());
        manager.add(new Separator());
        manager.add(refreshViewAction);
        manager.add(new Separator());
        deployAll = new DeployAllAction(true);
        deployAll.initCommonViewer(((CommonNavigator) this).getCommonViewer());
        manager.add(deployAll);
        manager.add(new Separator());
        AbstractRepositoryAction importObject = ImportObjectAction.createImportAction();
        importObject.initCommonViewer(((CommonNavigator) this).getCommonViewer());
        manager.add(importObject);
        // manager.add(new Separator());
        ExportObjectAction exportObject = new ExportObjectAction();
        exportObject.initCommonViewer(((CommonNavigator) this).getCommonViewer());
        manager.add(exportObject);
        // manager.add(new Separator());
        ImportServerObjectAction importServerObject = new ImportServerObjectAction();
        importServerObject.initCommonViewer(((CommonNavigator) this).getCommonViewer());
        manager.add(importServerObject);
        manager.add(new Separator());

    }

    public DeployAllAction getDeployAllAction() {
        return deployAll;
    }

    /**
     * DOC hbhong Comment method "initInput".
     */
    private void initInput() {
        copyDataModelFiles();

        IRepositoryViewObject[] categoryViewObjects = RepositoryResourceUtil.getCategoryViewObjectsWithRecycle();

        getCommonViewer().setInput(categoryViewObjects);
        // getCommonViewer().addFilter(filter);
        getCommonViewer().expandToLevel(2);
    }

    /**
     * Just copy the default DataModel Data to current workspace
     */
    private void copyDataModelFiles() {
        final String resourceFolder = "resources/system/datamodel";//$NON-NLS-1$
        ERepositoryObjectType type = IServerObjectRepositoryType.TYPE_DATAMODEL;

        File resourceFile = getResourceFolder(resourceFolder);
        copyToFolder(resourceFile, type);
    }

    private File getResourceFolder(final String resourceFolder) {
        File file = null;
        try {
            File bundleFile = FileLocator.getBundleFile(RepositoryPlugin.getDefault().getBundle());
            file = new File(bundleFile, resourceFolder);
        } catch (IOException e) {
            log.error("resolve bundle file error.", e);//$NON-NLS-1$
        }

        return file;
    }

    private void copyToFolder(File resourceFolder, ERepositoryObjectType dataType) {
        if (resourceFolder != null && resourceFolder.exists()) {
            IFolder targetFolder = createTargetSystemFolder(dataType);

            File file = resourceFolder;
            File[] files = file.listFiles();
            if (files != null) {

                for (File file2 : files) {
                    if (file2.getName().equals(".svn")) { //$NON-NLS-1$
                        continue;
                    }

                    IFile ifile = targetFolder.getFile(file2.getName());
                    String fileExtension = ifile.getFileExtension();

                    if ("item".equals(fileExtension) || "properties".equals(fileExtension) || "xsd".equals(fileExtension) || "mapinfo".equals(fileExtension)) //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$ //$NON-NLS-4$
                    {
                        FileInputStream fileInputStream = null;
                        File sunfile = ifile.getLocation().toFile();
                        if (!sunfile.exists()) {
                            try {
                                fileInputStream = new FileInputStream(file2);
                                ifile.create(fileInputStream, IFile.FORCE, new NullProgressMonitor());
                            } catch (FileNotFoundException e) {
                                log.error("file not found.", e);//$NON-NLS-1$
                            } catch (CoreException e) {
                                log.error("create model file failed.", e);//$NON-NLS-1$
                            } finally {
                                if (fileInputStream != null) {
                                    try {
                                        fileInputStream.close();
                                    } catch (IOException e) {
                                        log.error(e.getMessage(), e);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private IFolder createTargetSystemFolder(ERepositoryObjectType type) {
        IFolder typeFolder = RepositoryResourceUtil.getFolder(type);

        IFolder systemFolder = typeFolder.getFolder("System");//$NON-NLS-1$
        File folderFile = systemFolder.getLocation().toFile();
        if (!folderFile.exists()) {
            try {
                systemFolder.create(0, true, new NullProgressMonitor());
            } catch (CoreException e) {
                log.error("create System folder error", e);//$NON-NLS-1$
            }
        }
        return systemFolder;
    }

    public static MDMRepositoryView show() {
        IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (activeWorkbenchWindow != null) {
            return findRepositoryView(activeWorkbenchWindow);
        } else {
            IWorkbenchWindow[] workbenchWindows = PlatformUI.getWorkbench().getWorkbenchWindows();
            if (workbenchWindows != null) {
                for (IWorkbenchWindow window : workbenchWindows) {
                    MDMRepositoryView foundView = findRepositoryView(window);
                    if (foundView != null) {
                        return foundView;
                    }
                }
            }
        }
        return null;
    }

    private static MDMRepositoryView findRepositoryView(IWorkbenchWindow workbenchWindow) {
        if (workbenchWindow != null) {
            IWorkbenchPage activePage = workbenchWindow.getActivePage();
            if (activePage != null) {
                IViewPart part = activePage.findView(VIEW_ID);
                if (part == null) {
                    try {
                        part = workbenchWindow.getActivePage().showView(VIEW_ID);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
                if (part != null && part instanceof MDMRepositoryView) {
                    return (MDMRepositoryView) part;
                }
            }
        }
        return null;
    }

    private IPartListener2 partListener = new IPartListener2() {

        IProxyRepositoryFactory factory = CoreRuntimePlugin.getInstance().getProxyRepositoryFactory();

        @Override
        public void partVisible(IWorkbenchPartReference partRef) {

        }

        @Override
        public void partOpened(IWorkbenchPartReference partRef) {

        }

        @Override
        public void partInputChanged(IWorkbenchPartReference partRef) {

        }

        @Override
        public void partHidden(IWorkbenchPartReference partRef) {

        }

        @Override
        public void partDeactivated(IWorkbenchPartReference partRef) {

        }

        @Override
        public void partClosed(IWorkbenchPartReference partRef) {
            if (activedJobEditorRefs.contains(partRef)) {
                activedJobEditorRefs.remove(partRef);
            }
            // unlock
            Item item = getItem(partRef.getPart(false));
            if (item != null) {
                try {
                    if (ERepositoryStatus.LOCK_BY_USER != factory.getStatus(item)) {
                        removeReadOnlyStatus(partRef.getPart(false));
                        return;
                    }
                    factory.unlock(item);

                } catch (PersistenceException e) {
                    log.error(e.getMessage(), e);
                } catch (LoginException e) {
                    log.error(e.getMessage(), e);
                } finally {
                    if (item != null) {
                        Property property = item.getProperty();
                        final String id = property.getId();

                        Display.getDefault().asyncExec(new Runnable() {

                            @Override
                            public void run() {
                                if (!getCommonViewer().getTree().isDisposed()) {
                                    final IRepositoryViewObject viewObject = ContainerCacheService.get(id);
                                    if (viewObject != null) {
                                        getCommonViewer().refresh(viewObject);
                                    }
                                }
                            }
                        });
                    }
                }
            }

        }

        private void removeReadOnlyStatus(IWorkbenchPart part) {
            if (part != null && part instanceof IEditorPart) {
                IEditorInput input = ((IEditorPart) part).getEditorInput();
                if (input != null && input instanceof IRepositoryViewEditorInput) {
                    ((IRepositoryViewEditorInput) input).setReadOnly(false);
                }
            }
        }

        private Item getItem(IWorkbenchPart part) {
            if (part != null && part instanceof IEditorPart) {
                IEditorInput input = ((IEditorPart) part).getEditorInput();
                if (input != null) {
                    Item item = null;
                    if (input instanceof IRepositoryViewEditorInput) {
                        item = ((IRepositoryViewEditorInput) input).getInputItem();
                    } else if (input instanceof ProcessEditorInput) {
                        item = ((ProcessEditorInput) input).getItem();
                    }

                    if (item == null && exAdapter != null) {
                        item = exAdapter.getItem(part);
                    }
                    return item;

                }
            }
            return null;

        }

        @Override
        public void partBroughtToTop(IWorkbenchPartReference partRef) {
        }

        private Set<IWorkbenchPartReference> activedJobEditorRefs = new HashSet<IWorkbenchPartReference>();

        private boolean needConfirm(String curPerspectiveId, IWorkbenchPartReference partRef) {
            String curPartId = partRef.getId();
            return MDMPerspective.PERPECTIVE_ID.equals(curPerspectiveId) && JOB_EDITOR_ID.equals(curPartId)
                    && !activedJobEditorRefs.contains(partRef);

        }

        @Override
        public void partActivated(IWorkbenchPartReference partRef) {

            String curPerspectiveId = (currentPerspective == null) ? null : currentPerspective.getId();

            // if editor is talend job editor, switch to org.talend.rcp.perspective
            if (needConfirm(curPerspectiveId, partRef)) {
                activedJobEditorRefs.add(partRef);
                Display.getDefault().asyncExec(new Runnable() {

                    @Override
                    public void run() {
                        SwitchPerspectiveDialog dialog = new SwitchPerspectiveDialog(getSite().getShell(),
                                Messages.MDMRepositoryView_integration, IPerspectiveConstants.PERSPECTIVE_ID_DI,
                                PreferenceConstants.P_AUTO_SWITCH_TO_DI, PreferenceConstants.P_NOT_ASK_AUTO_SWITCH_TO_DI);

                        dialog.run();
                    }
                });
            }

            if (partRef.getId().equals(MDMRepositoryView.VIEW_ID)) {
                if (exAdapter != null) {
                    exAdapter.refreshBranchPartControl();
                }
            }
        }

    };

    private IPerspectiveDescriptor currentPerspective;

    public String getCurrentPerspectiveId() {
        if (currentPerspective != null) {
            return currentPerspective.getId();
        }
        return null;
    }

    PerspectiveAdapter perspectiveListener = new PerspectiveAdapter() {

        @Override
        public void perspectiveDeactivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
            String pId = perspective.getId();
            if (pId.equals(MDMPerspective.PERPECTIVE_ID)) {
                IWorkbench workbench = page.getWorkbenchWindow().getWorkbench();
                IEclipseContext activeContext = ((IEclipseContext) workbench.getService(IEclipseContext.class)).getActiveLeaf();
                IWorkbenchPartSite site = MDMRepositoryView.this.getSite();
                if (site instanceof PartSite) {
                    MPart part = ((PartSite) site).getModel();
                    activeContext.set("e4ActivePart", part); //$NON-NLS-1$
                }
            }
        }

        @Override
        public void perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {

            currentPerspective = perspective;
            if (first) {
                first = false;
                if (MDMPerspective.PERPECTIVE_ID.equals(perspective.getId())) {
                    ShowWelcomeEditor.showWelcomeEditor();
                }
            }

            if (MDMPerspective.PERPECTIVE_ID.equals(perspective.getId())) {

                getCommonViewer().refresh();
            }
        }
    };

    /**
     * Register one perspective selection listener
     */
    private void regisitPerspectiveBarSelectListener() {

        PlatformUI.getWorkbench().getActiveWorkbenchWindow().addPerspectiveListener(perspectiveListener);
    }

    private void unRegisitPerspectiveBarSelectListener() {

        PlatformUI.getWorkbench().getActiveWorkbenchWindow().removePerspectiveListener(perspectiveListener);
    }

    private static boolean first = true;

    public void switchToPerspective(final String perspectiveId) {
        // Display.getDefault().asyncExec(new Runnable() {
        //
        // public void run() {
        final IWorkbench workbench = PlatformUI.getWorkbench();
        final IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
        if (window != null) {
            final IWorkbenchPage activePage = window.getActivePage();
            if (activePage != null) {
                final IPerspectiveDescriptor activePerspective = activePage.getPerspective();
                if (activePerspective == null || !activePerspective.getId().equals(perspectiveId)) {
                    final IPerspectiveRegistry registry = workbench.getPerspectiveRegistry();
                    final IWorkbenchPage page = window.getActivePage();
                    final IPerspectiveDescriptor desc = registry.findPerspectiveWithId(perspectiveId);
                    page.setPerspective(desc);
                }
            }
        }

    }

    private DeployAllAction deployAll;

    private IMDMRepositoryViewExAdapter exAdapter;

    private void registerEditorListener() {
        if (exAdapter != null) {
            exAdapter.registerWFEditorListener();
        }
        this.getSite().getPage().addPartListener(partListener);
    }

    private void unRegisterEditorListener() {
        if (exAdapter != null) {
            exAdapter.unregisterWFEditorListener();
        }
        this.getSite().getPage().removePartListener(partListener);
    }

    @Override
    public void init(IViewSite aSite, IMemento aMemento) throws PartInitException {
        super.init(aSite, aMemento);
        CommandManager.getInstance().restoreState(aMemento);
        this.exAdapter = ExAdapterManager.getAdapter(this, IMDMRepositoryViewExAdapter.class);
    }

    @Override
    public void saveState(IMemento aMemento) {
        super.saveState(aMemento);
        Project currentProject = ProjectManager.getInstance().getCurrentProject();
        org.talend.core.model.properties.Project emfProject = currentProject.getEmfProject();
        if (emfProject != null && emfProject.eResource() == null) {

            IRepositoryFactory rfactory = ProxyRepositoryFactory.getInstance().getRepositoryFactoryFromProvider();
            if (rfactory != null) {
                try {
                    rfactory.reloadProject(currentProject);
                } catch (PersistenceException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        CommandManager.getInstance().saveState(aMemento);
    }

    @Override
    public Object getAdapter(Class adapter) {
        if (adapter == IPropertySheetPage.class) {
            return new TabbedPropertySheetPage(this);
        }
        return super.getAdapter(adapter);
    }

    @Override
    public String getContributorId() {
        return CONTRIBUTER_ID;
    }

    public void refreshRootNode(ERepositoryObjectType type) {
        String rootPath = ""; //$NON-NLS-1$
        if (type.equals(IServerObjectRepositoryType.TYPE_TRANSFORMERV2)) {
            IRepositoryViewObject processCategoryObject = ContainerCacheService.get(
                    IServerObjectRepositoryType.TYPE_TRANSFORMERV2, rootPath);
            getCommonViewer().refresh(processCategoryObject);
        } else if (type.equals(IServerObjectRepositoryType.TYPE_ROUTINGRULE)) {
            IRepositoryViewObject triggerCategoryObject = ContainerCacheService.get(IServerObjectRepositoryType.TYPE_ROUTINGRULE,
                    rootPath);
            getCommonViewer().refresh(triggerCategoryObject);
        }
        IRepositoryViewObject[] rootObjects = RepositoryResourceUtil.getCategoryViewObjectsWithRecycle();

        List<IRepositoryViewObject> categoryViewObjects = rootObjects[0].getChildren();
        for (IRepositoryViewObject viewObj : categoryViewObjects) {
            if (viewObj.getRepositoryObjectType().equals(type)) {
                getCommonViewer().refresh(viewObj);
                break;
            }
        }
    }
}

class TreeViewerListener implements ITreeViewerListener {

    private TreeViewer viewer;

    public TreeViewerListener(TreeViewer viewer) {
        this.viewer = viewer;
    }

    @Override
    public void treeCollapsed(TreeExpansionEvent event) {
        Object element = event.getElement();
        if (element instanceof FolderRepositoryObject) {
            FolderRepositoryObject fro = (FolderRepositoryObject) element;
            String path = fro.getPath();
            if (!path.isEmpty()) {
                TreeItem item = getObject(viewer.getTree(), event.getElement());
                if (item != null) {
                    item.setImage(ImageProvider.getImage(ECoreImage.FOLDER_CLOSE_ICON));
                }
            }
        }
    }

    @Override
    public void treeExpanded(TreeExpansionEvent event) {
        Object element = event.getElement();
        if (element instanceof FolderRepositoryObject) {
            FolderRepositoryObject fro = (FolderRepositoryObject) element;
            String path = fro.getPath();
            if (!path.isEmpty()) {
                TreeItem item = getObject(viewer.getTree(), event.getElement());
                if (item != null) {
                    item.setImage(ImageProvider.getImage(ECoreImage.FOLDER_OPEN_ICON));
                }
            }
        }
    }

    private TreeItem getObject(Tree tree, Object objectToFind) {
        for (TreeItem item : tree.getItems()) {
            TreeItem toReturn = getObject(item, objectToFind);
            if (toReturn != null) {
                return toReturn;
            }
        }
        return null;
    }

    private TreeItem getObject(TreeItem parent, Object objectToFind) {
        for (TreeItem currentChild : parent.getItems()) {
            if (objectToFind.equals(currentChild.getData())) {
                return currentChild;
            }
            TreeItem toReturn = getObject(currentChild, objectToFind);
            if (toReturn != null) {
                return toReturn;
            }
        }
        return null;
    }

}
