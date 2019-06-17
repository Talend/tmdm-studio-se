// ============================================================================
//
// Copyright (C) 2006-2019 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.repository.ui.views;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.util.OpenStrategy;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.OpenAndLinkWithEditorHelper;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.internal.PartSite;
import org.eclipse.ui.internal.PopupMenuExtender;
import org.eclipse.ui.internal.views.markers.ExtendedMarkersView;
import org.eclipse.ui.internal.views.markers.MarkerContentGenerator;
import org.eclipse.ui.menus.IMenuService;
import org.eclipse.ui.views.markers.MarkerSupportView;
import org.eclipse.ui.views.markers.internal.ContentGeneratorDescriptor;
import org.eclipse.ui.views.markers.internal.MarkerGroup;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.utils.workbench.resources.ResourceUtils;
import org.talend.core.model.general.Project;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.marker.IValidationMarker;
import org.talend.mdm.repository.ui.markers.IOpenMarkerHandler;
import org.talend.mdm.repository.ui.markers.OpenMarkerHandlerRegister;
import org.talend.mdm.repository.ui.markers.datamodel.ModelNameMarkerGroup;
import org.talend.mdm.repository.utils.EclipseResourceManager;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;
import org.talend.mdm.repository.validate.plugin.ValidatePlugin;
import org.talend.repository.ProjectManager;

/**
 * created by HHB on 2013-1-5 Detailled comment
 *
 */
public class MDMProblemView extends MarkerSupportView implements IValidationMarker {

    static Logger log = Logger.getLogger(MDMProblemView.class);

    private static final String GENERATOR_ID = "org.talend.mdm.problemsGenerator"; //$NON-NLS-1$

    public static final String VIEW_ID = "org.talend.mdm.repository.ui.views.MDMProblemView"; //$NON-NLS-1$

    private ISelectionListener pageSelectionListener;

    /**
     * DOC HHB MDMProblemView constructor comment.
     *
     * @param contentGeneratorId
     */
    public MDMProblemView() {
        super(GENERATOR_ID);
    }

    private class ViewerPageSelectionListener implements ISelectionListener {

        private ExtendedMarkersView view;

        ViewerPageSelectionListener(ExtendedMarkersView view) {
            this.view = view;
        }

        @Override
        public void selectionChanged(IWorkbenchPart part, ISelection selection) {

            // Do not respond to our own selections or if we are not
            // visible
            if (part == MDMProblemView.this || !(getSite().getPage().isPartVisible(part))) {
                return;
            }

            // get Objects to adapt
            List objectsToAdapt = new ArrayList();
            if (part instanceof IEditorPart) {
                IEditorPart editor = (IEditorPart) part;
                objectsToAdapt.add(editor.getEditorInput());
            } else {
                if (selection instanceof IStructuredSelection) {
                    for (Iterator iterator = ((IStructuredSelection) selection).iterator(); iterator.hasNext();) {
                        Object object = iterator.next();
                        objectsToAdapt.add(object);
                    }
                }
            }

            // try to adapt them in resources and add it to the
            // selectedElements
            List selectedElements = new ArrayList();
            for (Iterator iterator = objectsToAdapt.iterator(); iterator.hasNext();) {
                Object object = iterator.next();
                if (object instanceof IRepositoryViewObject) {
                    Object[] resElements = RepositoryResourceUtil.adapt2ResourceElement((IRepositoryViewObject) object);
                    if (resElements != null) {
                        for (Object obj : resElements) {
                            selectedElements.add(obj);
                        }
                    }
                }
            }

            MarkerContentGenerator generator = getGenerator();
            updateSelectedResource(generator, selectedElements.toArray(), part == null);
        }
    }

    private void addPageAndPartSelectionListener() {
        // Initialise any selection based filtering
        pageSelectionListener = new ViewerPageSelectionListener(this);
        getSite().getPage().addPostSelectionListener(pageSelectionListener);

        pageSelectionListener.selectionChanged(getSite().getPage().getActivePart(), getSite().getPage().getSelection());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.internal.views.markers.ExtendedMarkersView#init(org.eclipse.ui.IViewSite,
     * org.eclipse.ui.IMemento)
     */
    @Override
    public void init(IViewSite site, IMemento memento) throws PartInitException {
        super.init(site, memento);
        IMenuService menuService = site.getService(IMenuService.class);

        hookModelGroup();
    }

    /**
     * DOC HHB Comment method "hookModelGroup".
     */
    private void hookModelGroup() {

        try {
            Field generatorField = ExtendedMarkersView.class.getDeclaredField("generator"); //$NON-NLS-1$
            Field descField = MarkerContentGenerator.class.getDeclaredField("generatorDescriptor"); //$NON-NLS-1$
            if (generatorField != null && descField != null) {
                generatorField.setAccessible(true);
                descField.setAccessible(true);
                Object object = generatorField.get(this);
                if (object != null) {
                    Object descObj = descField.get(object);
                    if (descObj != null) {
                        Collection groups = ((ContentGeneratorDescriptor) descObj).getMarkerGroups();
                        if (!hasMarkerGroup(groups, ModelNameMarkerGroup.ID)) {
                            groups.add(new ModelNameMarkerGroup());
                        }
                    }
                }
            }

        } catch (SecurityException e) {
            log.error(e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage(), e);
        } catch (NoSuchFieldException e) {
            log.error(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            log.error(e.getMessage(), e);
        }

    }

    private boolean hasMarkerGroup(Collection groups, String id) {
        for (Object groupObj : groups) {
            if (((MarkerGroup) groupObj).getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    private TreeViewer getTreeViewer() {
        try {
            Field viewerField = ExtendedMarkersView.class.getDeclaredField("viewer"); //$NON-NLS-1$
            if (viewerField != null) {
                viewerField.setAccessible(true);
                return (TreeViewer) viewerField.get(this);
            }
        } catch (SecurityException e) {
            log.error(e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage(), e);
        } catch (NoSuchFieldException e) {
            log.error(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    private void updateSelectedResource(MarkerContentGenerator generator, Object[] selectedElements, boolean forceUpdate) {
        if (selectedElements == null) {
            return;
        }
        try {
            Method method = MarkerContentGenerator.class.getDeclaredMethod(
                    "updateSelectedResource", Object[].class, boolean.class); //$NON-NLS-1$
            if (method != null) {
                method.setAccessible(true);
                Object param = Arrays.asList(selectedElements).stream().filter(e -> e != null)
                        .collect(Collectors.toList()).toArray();
                method.invoke(generator, param, forceUpdate);
            }
        } catch (SecurityException e) {
            log.error(e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            log.error(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            log.error(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            log.error(e.getMessage(), e);
        }
    }

    private MarkerContentGenerator getGenerator() {
        try {
            Field generatorField = ExtendedMarkersView.class.getDeclaredField("generator"); //$NON-NLS-1$
            if (generatorField != null) {
                generatorField.setAccessible(true);
                return (MarkerContentGenerator) generatorField.get(this);
            }
        } catch (SecurityException e) {
            log.error(e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage(), e);
        } catch (NoSuchFieldException e) {
            log.error(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    private void uninstallViewListener(TreeViewer treeViewer) {

        if (treeViewer != null) {
            Object listener = findListener(treeViewer);
            if (listener != null) {
                treeViewer.removePostSelectionChangedListener((ISelectionChangedListener) listener);
                treeViewer.removeDoubleClickListener((IDoubleClickListener) listener);
                treeViewer.removeOpenListener((IOpenListener) listener);
            }
            //

        }

    }

    private void removeGotoCmd() {
        PopupMenuExtender popupMenuExtender = getPopupMenuExtender();
        if (popupMenuExtender != null) {
            final MenuManager manager = popupMenuExtender.getManager();
            manager.addMenuListener(new IMenuListener() {

                @Override
                public void menuAboutToShow(IMenuManager manager) {
                    for (IContributionItem item : manager.getItems()) {
                        if (item.getId().equals("org.eclipse.ui.navigate.goToResource")) { //$NON-NLS-1$
                            manager.remove(item);
                            break;
                        }
                    }

                }
            });

        }
    }

    private PopupMenuExtender getPopupMenuExtender() {
        try {
            Field menuListField = PartSite.class.getDeclaredField("menuExtenders"); //$NON-NLS-1$
            if (menuListField != null) {
                menuListField.setAccessible(true);
                List list = (List) menuListField.get(getSite());
                for (Object obj : list) {
                    if (obj instanceof PopupMenuExtender) {
                        return (PopupMenuExtender) obj;
                    }

                }
            }
        } catch (SecurityException e) {
            log.error(e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage(), e);
        } catch (NoSuchFieldException e) {
            log.error(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            log.error(e.getMessage(), e);
        }
        return null;

    }

    private void addLinkWithEditorSupport(TreeViewer viewer) {
        new OpenAndLinkWithEditorHelper(viewer) {

            @Override
            protected void activate(ISelection selection) {
                final int currentMode = OpenStrategy.getOpenMethod();
                try {
                    OpenStrategy.setOpenMethod(OpenStrategy.DOUBLE_CLICK);
                    openSelectedMarkers(null);
                } finally {
                    OpenStrategy.setOpenMethod(currentMode);
                }
            }

            @Override
            protected void linkToEditor(ISelection selection) {
                // Not supported by this part
            }

            @Override
            protected void open(ISelection selection, boolean activate) {
                openSelectedMarkers(null);
            }
        };
    }

    public void openSelectedMarkers(Object param) {
        IMarker[] markers = getSelectedMarkers();
        IWorkbenchPage page = getSite().getPage();
        List<IOpenMarkerHandler> handlers = OpenMarkerHandlerRegister.getHandlers();

        for (IMarker marker : markers) {
            boolean canOpen = false;
            if (handlers != null) {
                for (IOpenMarkerHandler handler : handlers) {
                    canOpen = handler.canOpen(marker);
                    if (canOpen) {
                        handler.open(page, marker, param);
                        break;
                    }
                }
            }
            if (!canOpen) {
                openMarkerInEditor(marker, page);
            }
        }
    }

    private Object findListener(TreeViewer treeViewer) {
        try {
            Field doubleListenersField = StructuredViewer.class.getDeclaredField("doubleClickListeners"); //$NON-NLS-1$
            if (doubleListenersField != null) {
                doubleListenersField.setAccessible(true);
                ListenerList list = (ListenerList) doubleListenersField.get(treeViewer);
                for (Object listenerObj : list.getListeners()) {
                    if (listenerObj.getClass().getEnclosingClass() == OpenAndLinkWithEditorHelper.class) {
                        return listenerObj;
                    }
                }
            }
        } catch (SecurityException e) {
            log.error(e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage(), e);
        } catch (NoSuchFieldException e) {
            log.error(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.ui.internal.views.markers.ExtendedMarkersView#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);
        TreeViewer treeViewer = getTreeViewer();
        uninstallViewListener(treeViewer);
        removeGotoCmd();
        addLinkWithEditorSupport(treeViewer);
        addPageAndPartSelectionListener();
    }

    private static final Image IMG_PROBLEM = EclipseResourceManager.getImage(ValidatePlugin.PLUGIN_ID, "icons/problems_view.gif");

    private static final Image IMG_PROBLEM_ERR = EclipseResourceManager.getImage(ValidatePlugin.PLUGIN_ID,
            "icons/problems_view_error.gif");

    private static final Image IMG_PROBLEM_WARN = EclipseResourceManager.getImage(ValidatePlugin.PLUGIN_ID,
            "icons/problems_view_warning.gif");

    private void updateTitleImage(int severity) {
        switch (severity) {
        case IMarker.SEVERITY_ERROR:
            setTitleImage(IMG_PROBLEM_ERR);
            break;
        case IMarker.SEVERITY_WARNING:
            setTitleImage(IMG_PROBLEM_WARN);
            break;
        default:
            setTitleImage(IMG_PROBLEM);
            break;
        }
    }

    public void updateViewTitle() {
        try {
            Project project = ProjectManager.getInstance().getCurrentProject();
            IProject prj = ResourceUtils.getProject(project);
            int severity = prj.findMaxProblemSeverity(IValidationMarker.MARKER_MDM, true, IResource.DEPTH_INFINITE);
            updateTitleImage(severity);
        } catch (PersistenceException e) {
            log.error(e.getMessage(), e);
        } catch (CoreException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void dispose() {
        getSite().getPage().removePostSelectionListener(pageSelectionListener);
        super.dispose();
    }

}
