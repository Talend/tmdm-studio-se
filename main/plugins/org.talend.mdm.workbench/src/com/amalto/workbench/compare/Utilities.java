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

/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: IBM Corporation - initial API and implementation
 *******************************************************************************/

package com.amalto.workbench.compare;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareUI;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.patch.IHunk;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.resources.mapping.ResourceMapping;
import org.eclipse.core.resources.mapping.ResourceMappingContext;
import org.eclipse.core.resources.mapping.ResourceTraversal;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;

/**
 * Convenience and utility methods.
 */
public class Utilities {

    private static Log log = LogFactory.getLog(Utilities.class);

    private static final IPath ICONS_PATH = new Path("$nl$/icons/full/"); //$NON-NLS-1$

    public static IWorkbenchPartSite findSite(Control c) {
        while (c != null && !c.isDisposed()) {
            Object data = c.getData();
            if (data instanceof IWorkbenchPart)
                return ((IWorkbenchPart) data).getSite();
            c = c.getParent();
        }
        return null;
    }

    public static void setEnableComposite(Composite composite, boolean enable) {
        Control[] children = composite.getChildren();
        for (int i = 0; i < children.length; i++)
            children[i].setEnabled(enable);
    }

    public static boolean getBoolean(CompareConfiguration cc, String key, boolean dflt) {
        if (cc != null) {
            Object value = cc.getProperty(key);
            if (value instanceof Boolean)
                return ((Boolean) value).booleanValue();
        }
        return dflt;
    }

    public static void firePropertyChange(ListenerList listenerList, Object source, String property, Object old, Object newValue) {
        PropertyChangeEvent event = new PropertyChangeEvent(source, property, old, newValue);
        firePropertyChange(listenerList, event);
    }

    public static void firePropertyChange(final ListenerList listenerList, final PropertyChangeEvent event) {
        if (listenerList == null || listenerList.isEmpty())
            return;
        // Legacy listeners may expect to get notified in the UI thread
        Runnable runnable = new Runnable() {

            public void run() {
                if (listenerList != null) {
                    Object[] listeners = listenerList.getListeners();
                    for (int i = 0; i < listeners.length; i++) {
                        final IPropertyChangeListener listener = (IPropertyChangeListener) listeners[i];
                        SafeRunner.run(new ISafeRunnable() {

                            public void run() throws Exception {
                                listener.propertyChange(event);
                            }

                            public void handleException(Throwable exception) {
                                // Logged by SafeRunner
                            }
                        });
                    }
                }
            }
        };
        if (Display.getCurrent() == null) {
            Display.getDefault().syncExec(runnable);
        } else {
            runnable.run();
        }
    }

    public static boolean okToUse(Widget widget) {
        return widget != null && !widget.isDisposed();
    }

    private static ArrayList internalGetResources(ISelection selection, Class type) {
        ArrayList tmp = new ArrayList();
        if (selection instanceof IStructuredSelection) {
            Object[] s = ((IStructuredSelection) selection).toArray();

            for (int i = 0; i < s.length; i++) {
                IResource resource = null;
                Object o = s[i];
                if (type.isInstance(o)) {
                    resource = (IResource) o;

                } else if (o instanceof ResourceMapping) {
                    try {
                        ResourceTraversal[] travs = ((ResourceMapping) o).getTraversals(ResourceMappingContext.LOCAL_CONTEXT,
                                null);
                        if (travs != null) {
                            for (int k = 0; k < travs.length; k++) {
                                IResource[] resources = travs[k].getResources();
                                for (int j = 0; j < resources.length; j++) {
                                    if (type.isInstance(resources[j]) && resources[j].isAccessible())
                                        tmp.add(resources[j]);
                                }
                            }
                        }
                    } catch (CoreException ex) {
                        log.error(ex.getMessage(), ex);
                    }
                } else if (o instanceof IAdaptable) {
                    IAdaptable a = (IAdaptable) o;
                    Object adapter = a.getAdapter(IResource.class);
                    if (type.isInstance(adapter))
                        resource = (IResource) adapter;
                }

                if (resource != null && resource.isAccessible())
                    tmp.add(resource);
            }
        }
        return tmp;
    }

    /*
     * Convenience method: extract all accessible <code>IResources</code> from given selection. Never returns null.
     */
    public static IResource[] getResources(ISelection selection) {
        ArrayList tmp = internalGetResources(selection, IResource.class);
        return (IResource[]) tmp.toArray(new IResource[tmp.size()]);
    }

    /*
     * Convenience method: extract all accessible <code>IFiles</code> from given selection. Never returns null.
     */
    public static IFile[] getFiles(ISelection selection) {
        ArrayList tmp = internalGetResources(selection, IFile.class);
        return (IFile[]) tmp.toArray(new IFile[tmp.size()]);
    }

    public static byte[] readBytes(InputStream in) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            while (true) {
                int c = in.read();
                if (c == -1)
                    break;
                bos.write(c);
            }

        } catch (IOException ex) {
            return null;

        } finally {
            Utilities.close(in);
            try {
                bos.close();
            } catch (IOException x) {
                // silently ignored
            }
        }

        return bos.toByteArray();
    }

    public static IPath getIconPath(Display display) {
        return ICONS_PATH;
    }

    /*
     * Initialize the given Action from a ResourceBundle.
     */
    public static void initAction(IAction a, ResourceBundle bundle, String prefix) {

        String labelKey = "label"; //$NON-NLS-1$
        String tooltipKey = "tooltip"; //$NON-NLS-1$
        String imageKey = "image"; //$NON-NLS-1$
        String descriptionKey = "description"; //$NON-NLS-1$

        if (prefix != null && prefix.length() > 0) {
            labelKey = prefix + labelKey;
            tooltipKey = prefix + tooltipKey;
            imageKey = prefix + imageKey;
            descriptionKey = prefix + descriptionKey;
        }

        a.setText(getString(bundle, labelKey, labelKey));
        a.setToolTipText(getString(bundle, tooltipKey, null));
        a.setDescription(getString(bundle, descriptionKey, null));

        String relPath = getString(bundle, imageKey, null);
        if (relPath != null && relPath.trim().length() > 0) {

            String dPath;
            String ePath;

            if (relPath.indexOf("/") >= 0) { //$NON-NLS-1$
                String path = relPath.substring(1);
                dPath = 'd' + path;
                ePath = 'e' + path;
            } else {
                dPath = "dlcl16/" + relPath; //$NON-NLS-1$
                ePath = "elcl16/" + relPath; //$NON-NLS-1$
            }

            // ImageDescriptor id= CompareUIPlugin.getImageDescriptor(dPath); // we set the disabled image first (see PR
            // 1GDDE87)
            // if (id != null)
            // a.setDisabledImageDescriptor(id);
            // id= CompareUIPlugin.getImageDescriptor(ePath);
            // if (id != null) {
            // a.setImageDescriptor(id);
            // a.setHoverImageDescriptor(id);
            // }
        }
    }

    public static void initToggleAction(IAction a, ResourceBundle bundle, String prefix, boolean checked) {

        String tooltip = null;
        if (checked)
            tooltip = getString(bundle, prefix + "tooltip.checked", null); //$NON-NLS-1$
        else
            tooltip = getString(bundle, prefix + "tooltip.unchecked", null); //$NON-NLS-1$
        if (tooltip == null)
            tooltip = getString(bundle, prefix + "tooltip", null); //$NON-NLS-1$

        if (tooltip != null)
            a.setToolTipText(tooltip);

        String description = null;
        if (checked)
            description = getString(bundle, prefix + "description.checked", null); //$NON-NLS-1$
        else
            description = getString(bundle, prefix + "description.unchecked", null); //$NON-NLS-1$
        if (description == null)
            description = getString(bundle, prefix + "description", null); //$NON-NLS-1$

        if (description != null)
            a.setDescription(description);

    }

    public static String getString(ResourceBundle bundle, String key, String dfltValue) {

        if (bundle != null) {
            try {
                return bundle.getString(key);
            } catch (MissingResourceException x) {
                // fall through
            }
        }
        return dfltValue;
    }

    public static String getString(String key) {
        try {
            return CompareUI.getResourceBundle().getString(key);
        } catch (MissingResourceException e) {
            return "!" + key + "!"; //$NON-NLS-2$ //$NON-NLS-1$
        }
    }

    public static String getFormattedString(String key, String arg) {
        try {
            return MessageFormat.format(CompareUI.getResourceBundle().getString(key), new String[] { arg });
        } catch (MissingResourceException e) {
            return "!" + key + "!"; //$NON-NLS-2$ //$NON-NLS-1$
        }
    }

    public static String getFormattedString(String key, String arg0, String arg1) {
        try {
            return MessageFormat.format(CompareUI.getResourceBundle().getString(key), new String[] { arg0, arg1 });
        } catch (MissingResourceException e) {
            return "!" + key + "!";//$NON-NLS-2$ //$NON-NLS-1$
        }
    }

    public static String getString(ResourceBundle bundle, String key) {
        return getString(bundle, key, key);
    }

    /**
     * Answers <code>true</code> if the given selection contains resources that don't have overlapping paths and
     * <code>false</code> otherwise.
     */
    /*
     * public static boolean isSelectionNonOverlapping() throws TeamException { IResource[] resources =
     * getSelectedResources(); // allow operation for non-overlapping resource selections if(resources.length>0) { List
     * validPaths = new ArrayList(2); for (int i = 0; i < resources.length; i++) { IResource resource = resources[i];
     *
     * // only allow cvs resources to be selected if(RepositoryProvider.getProvider(resource.getProject(),
     * CVSProviderPlugin.getTypeId()) == null) { return false; }
     *
     * // check if this resource overlaps other selections IPath resourceFullPath = resource.getFullPath();
     * if(!validPaths.isEmpty()) { for (Iterator it = validPaths.iterator(); it.hasNext();) { IPath path = (IPath)
     * it.next(); if(path.isPrefixOf(resourceFullPath) || resourceFullPath.isPrefixOf(path)) { return false; } } }
     * validPaths.add(resourceFullPath);
     *
     * // ensure that resources are managed ICVSResource cvsResource = CVSWorkspaceRoot.getCVSResourceFor(resource);
     * if(cvsResource.isFolder()) { if( ! ((ICVSFolder)cvsResource).isCVSFolder()) return false; } else { if( !
     * cvsResource.isManaged()) return false; } } return true; } return false; }
     */

    /* validate edit utilities */

    /**
     * Status constant indicating that an validateEdit call has changed the content of a file on disk.
     */
    private static final int VALIDATE_EDIT_PROBLEM = 10004;

    /**
     * Constant used to indicate that tests are being run.
     */
    public static boolean RUNNING_TESTS = false;

    /**
     * Constant used while testing the indicate that changes should be flushed when the compare input changes and a
     * viewer is dirty.
     */
    public static boolean TESTING_FLUSH_ON_COMPARE_INPUT_CHANGE = false;

    private static void displayError(final Shell shell, final String title, final IStatus status, final String message) {
        if (Display.getCurrent() != null)
            ErrorDialog.openError(shell, title, message, status);
        else {
            Display.getDefault().syncExec(new Runnable() {

                public void run() {
                    ErrorDialog.openError(shell, title, message, status);
                }
            });
        }
    }

    private static List getReadonlyFiles(IResource[] resources) {
        List readOnlyFiles = new ArrayList();
        for (int i = 0; i < resources.length; i++) {
            IResource resource = resources[i];
            ResourceAttributes resourceAttributes = resource.getResourceAttributes();
            if (resource.getType() == IResource.FILE && resourceAttributes != null && resourceAttributes.isReadOnly())
                readOnlyFiles.add(resource);
        }
        return readOnlyFiles;
    }

    private static Map createModificationStampMap(List files) {
        Map map = new HashMap();
        for (Iterator iter = files.iterator(); iter.hasNext();) {
            IFile file = (IFile) iter.next();
            map.put(file, new Long(file.getModificationStamp()));
        }
        return map;
    }

    /*
     * Returns null if an error occurred.
     */
    public static String readString(InputStream is, String encoding) throws IOException {
        if (is == null)
            return null;
        BufferedReader reader = null;
        try {
            StringBuffer buffer = new StringBuffer();
            char[] part = new char[2048];
            int read = 0;
            reader = new BufferedReader(new InputStreamReader(is, encoding));
            while ((read = reader.read(part)) != -1)
                buffer.append(part, 0, read);

            return buffer.toString();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    // silently ignored
                }
            }
        }
    }

    public static byte[] getBytes(String s, String encoding) {
        byte[] bytes = null;
        if (s != null) {
            try {
                bytes = s.getBytes(encoding);
            } catch (UnsupportedEncodingException e) {
                bytes = s.getBytes();
            }
        }
        return bytes;
    }

    public static void close(InputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException ex) {
                // silently ignored
            }
        }
    }

    public static IResource getFirstResource(ISelection selection) {
        IResource[] resources = getResources(selection);
        if (resources.length > 0)
            return resources[0];
        return null;
    }

    public static Object getAdapter(Object element, Class adapterType, boolean load) {
        if (adapterType.isInstance(element))
            return element;
        if (element instanceof IAdaptable) {
            Object adapted = ((IAdaptable) element).getAdapter(adapterType);
            if (adapterType.isInstance(adapted))
                return adapted;
        }
        if (load) {
            Object adapted = Platform.getAdapterManager().loadAdapter(element, adapterType.getName());
            if (adapterType.isInstance(adapted))
                return adapted;
        } else {
            Object adapted = Platform.getAdapterManager().getAdapter(element, adapterType);
            if (adapterType.isInstance(adapted))
                return adapted;
        }
        return null;
    }

    public static Object getAdapter(Object element, Class adapterType) {
        return getAdapter(element, adapterType, false);
    }

    /**
     * Return whether either the left or right sides of the given input represents a hunk. A hunk is a portion of a
     * file.
     *
     * @param input the compare input
     * @return whether the left or right side of the input represents a hunk
     */
    public static boolean isHunk(Object input) {
        if (input != null && input instanceof DiffNode) {
            ITypedElement right = ((DiffNode) input).getRight();
            if (right != null) {
                Object element = Utilities.getAdapter(right, IHunk.class);
                if (element instanceof IHunk)
                    return true;
            }
            ITypedElement left = ((DiffNode) input).getLeft();
            if (left != null) {
                Object element = Utilities.getAdapter(left, IHunk.class);
                if (element instanceof IHunk)
                    return true;
            }
        }
        return false;
    }

    public static void schedule(Job job, IWorkbenchSite site) {
        if (site != null) {
            IWorkbenchSiteProgressService siteProgress = (IWorkbenchSiteProgressService) site
                    .getAdapter(IWorkbenchSiteProgressService.class);
            if (siteProgress != null) {
                siteProgress.schedule(job, 0, true /* use half-busy cursor */);
                return;
            }
        }
        job.schedule();
    }

    public static void runInUIThread(final Runnable runnable) {
        if (Display.getCurrent() != null) {
            BusyIndicator.showWhile(Display.getCurrent(), runnable);
        } else {
            Display.getDefault().syncExec(new Runnable() {

                public void run() {
                    BusyIndicator.showWhile(Display.getCurrent(), runnable);
                }
            });
        }
    }
}
