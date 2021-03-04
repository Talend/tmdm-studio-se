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
package org.talend.mdm.repository.core.command.common;

import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ProcessItem;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.repository.ui.editor.RepositoryEditorInput;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.designer.core.ui.editor.ProcessEditorInput;
import org.talend.mdm.repository.core.command.AbstractCommand;
import org.talend.mdm.repository.core.command.CommandManager;
import org.talend.mdm.repository.core.command.ICommand;
import org.talend.mdm.repository.core.impl.RepositoryViewObjectResourceChangeManager;
import org.talend.mdm.repository.core.service.ContainerCacheService;
import org.talend.mdm.repository.model.mdmmetadata.MDMServerDef;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.RepositoryNode;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class UpdateLastServerCommand extends AbstractCommand {

    private static Logger log = Logger.getLogger(UpdateLastServerCommand.class);

    private static IProxyRepositoryFactory factory = CoreRuntimePlugin.getInstance().getProxyRepositoryFactory();

    private Item item;

    public void setItem(Item item) {
        this.item = item;
    }

    public void setServerDef(MDMServerDef serverDef) {
        this.serverDef = serverDef;
    }

    private MDMServerDef serverDef;

    public UpdateLastServerCommand() {

    }

    public UpdateLastServerCommand(Item item, MDMServerDef serverDef) {
        this.item = item;
        this.serverDef = serverDef;

    }

    /*
     * (non-Javadoc)
     *
     * @see org.talend.mdm.repository.core.command.AbstractCommand#getCommandType()
     */
    @Override
    public int getCommandType() {
        return ICommand.CMD_UPDATE_SERVER;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.talend.mdm.repository.core.command.AbstractCommand#execute(java.lang.Object,
     * org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public IStatus execute(Object params, IProgressMonitor monitor) {
        if (item == null) {
            if (viewObject == null) {
                CommandManager.getInstance().fillViewObjectToCommand(this);
            }
            item = viewObject.getProperty().getItem();
        }
        if (item != null) {
            saveLastServer(item, serverDef);
        }
        return Status.OK_STATUS;
    }

    private void saveLastServer(Item item, MDMServerDef serverDef) {
        if (item.eResource() == null) {
            try {
                IRepositoryViewObject viewObj = factory.getLastVersion(item.getProperty().getId());
                if (viewObj == null) {
                    // when object is match rule map info object,it must not exist and return null
                    return;
                }
                Property property = viewObj.getProperty();
                item = property.getItem();
                ContainerCacheService.put(property, viewObj);
            } catch (PersistenceException e) {
                log.error(e.getMessage(), e);
            }
        }

        RepositoryResourceUtil.setLastServerDef(item, serverDef);

        if (!(item instanceof ProcessItem)) {
            // for common object except job
            try {
                factory.save(item);
            } catch (PersistenceException e) {
                log.error(e.getMessage(), e);
            }
        } else {
            if (isWorkInUI()) {
                // for job object
                try {
                    RepositoryViewObjectResourceChangeManager.stopListening();
                    IEditorReference editorRef = getJobEditor(item);
                    if (editorRef != null) {
                        IEditorPart editor = editorRef.getEditor(false);
                        if (editor != null && editor.isDirty()) {

                            // when save job editor, it only keep the additional properties that Process have
                            ProcessEditorInput processEditorInput = (ProcessEditorInput) editor.getEditorInput();
                            EMap additionalProperties = item.getProperty().getAdditionalProperties();
                            Iterator iterator = additionalProperties.keySet().iterator();
                            Map<Object, Object> processAdditionalProperties = processEditorInput.getLoadedProcess()
                                    .getAdditionalProperties();
                            processAdditionalProperties.putAll(additionalProperties.map());

                            editor.doSave(new NullProgressMonitor());
                            return;
                        }
                    }

                    factory.save(item);
                } catch (PersistenceException e) {
                    log.error(e.getMessage(), e);
                } finally {
                    RepositoryViewObjectResourceChangeManager.startListening();
                }
            } else {// save under command line
                try {
                    factory.save(item);
                } catch (PersistenceException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    private boolean isWorkInUI() {
        try {
            return PlatformUI.getWorkbench() != null;
        } catch (Exception e) {
            return false;
        }
    }

    private static final String JOB_EDITOR_ID = "org.talend.designer.core.ui.MultiPageTalendEditor"; //$NON-NLS-1$

    private IEditorReference getJobEditor(Item item) {
        if (!ProxyRepositoryFactory.getInstance().isFullLogonFinished()) {
            return null;
        }
        if (PlatformUI.getWorkbench() == null || PlatformUI.getWorkbench().getActiveWorkbenchWindow() == null
                || PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage() == null) {
            return null;
        }
        IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        IEditorReference[] editorReferences = activePage.getEditorReferences();
        for (IEditorReference ref : editorReferences) {
            String editorId = ref.getId();
            if (ref != null && JOB_EDITOR_ID.equals(editorId)) {
                try {
                    IEditorInput editorInput = ref.getEditorInput();
                    if (editorInput instanceof RepositoryEditorInput) {
                        RepositoryEditorInput input = (RepositoryEditorInput) editorInput;
                        String id = input.getId();
                        if (id == null) {
                            RepositoryNode repositoryNode = input.getRepositoryNode();
                            if (repositoryNode != null) {
                                id = repositoryNode.getId();
                            }
                        }

                        if (id != null && id.equals(item.getProperty().getId())) {
                            return ref;
                        }
                    }
                } catch (PartInitException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        return null;
    }
}
