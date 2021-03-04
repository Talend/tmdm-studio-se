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
package org.talend.mdm.repository.core.impl.jobmodel;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.ui.gmf.util.DisplayUtils;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ProcessItem;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.model.repository.RepositoryViewObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.repository.ui.editor.RepositoryEditorInput;
import org.talend.mdm.repository.core.command.CommandManager;
import org.talend.mdm.repository.core.command.ICommand;
import org.talend.mdm.repository.core.impl.AbstractRepositoryResourceChangeListener;
import org.talend.mdm.repository.core.service.ContainerCacheService;
import org.talend.mdm.repository.model.mdmmetadata.MDMServerDef;
import org.talend.mdm.repository.ui.navigator.MDMRepositoryView;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;
import org.talend.repository.documentation.ERepositoryActionName;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class JobResourceListener extends AbstractRepositoryResourceChangeListener {

    static Logger log = Logger.getLogger(JobResourceListener.class);

    private static final String JOB_EDITOR_ID = "org.talend.designer.core.ui.MultiPageTalendEditor"; //$NON-NLS-1$

    private boolean isOpenInEditor(IRepositoryViewObject vObj) {
        if (!ProxyRepositoryFactory.getInstance().isFullLogonFinished()) {
            return false;
        }
        if (PlatformUI.getWorkbench() == null || PlatformUI.getWorkbench().getActiveWorkbenchWindow() == null
                || PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage() == null) {
            return false;
        }
        IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        IEditorReference[] editorReferences = activePage.getEditorReferences();
        for (IEditorReference ref : editorReferences) {
            String editorId = ref.getId();
            if (ref != null && JOB_EDITOR_ID.equals(editorId)) {
                try {
                    IEditorInput editorInput = ref.getEditorInput();
                    if (editorInput instanceof RepositoryEditorInput) {
                        Item item = ((RepositoryEditorInput) editorInput).getItem();
                        if (item != null && item.equals(vObj.getProperty().getItem())) {
                            return true;
                        }
                    }
                } catch (PartInitException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        return false;
    }

    @Override
    protected boolean isHandleProperty(String propertyName) {
        return propertyName.equals(ERepositoryActionName.SAVE.getName())
                || propertyName.equals(ERepositoryActionName.CREATE.getName())
                || propertyName.equals(ERepositoryActionName.IMPORT.getName())
                || propertyName.equals(ERepositoryActionName.COPY.getName());
    }

    @Override
    protected boolean isHandleItem(Item item) {
        return item instanceof ProcessItem;
    }

    @Override
    protected void run(String propertyName, final Item item) throws Exception {
        boolean jobSaved = false;
        boolean jobCreated = false;

        if (propertyName.equals(ERepositoryActionName.SAVE.getName())) {
            jobSaved = true;
        }

        if (propertyName.equals(ERepositoryActionName.CREATE.getName())
                || propertyName.equals(ERepositoryActionName.IMPORT.getName())
                || propertyName.equals(ERepositoryActionName.COPY.getName())) {
            jobCreated = true;
        }
        if (!jobSaved && !jobCreated) {
            // if the operation is not job saved or created, just don't do anything.
            return;
        }

        if (jobCreated) {
            CommandManager.getInstance().pushCommand(ICommand.CMD_ADD, item.getProperty().getId(),
                    item.getProperty().getDisplayName());

            removeLastServerInfo(item);
        }

        if (jobSaved) {
            // create a new object but without GUI info since it won't be used here certainly
            final IRepositoryViewObject viewObject = new RepositoryViewObject(item.getProperty(), true);
            final MDMServerDef serverDef = RepositoryResourceUtil.getLastServerDef(viewObject);
            DisplayUtils.getDisplay().syncExec(new Runnable() {

                public void run() {
                    if (viewObject != null && serverDef != null && isOpenInEditor(viewObject)) {
                        CommandManager.getInstance().pushCommand(ICommand.CMD_MODIFY, viewObject);
                        final IRepositoryViewObject cacheViewObject = ContainerCacheService.get(item.getProperty());
                        if (cacheViewObject != null) {
                            MDMRepositoryView.show().getCommonViewer().refresh(cacheViewObject);
                        } else {
                            MDMRepositoryView.show().getCommonViewer().refresh(viewObject);
                        }
                    }

                }
            });

        }

    }

    private void removeLastServerInfo(Item item) {
        Property property = item.getProperty();
        EMap additionalProperties = property.getAdditionalProperties();
        if (additionalProperties != null && additionalProperties.containsKey(RepositoryResourceUtil.PROP_LAST_SERVER_DEF)) {
            RepositoryResourceUtil.setLastServerDef(item, null);
            ProxyRepositoryFactory factory = ProxyRepositoryFactory.getInstance();
            try {
                item = RepositoryResourceUtil.assertItem(item);
                factory.save(item, true);
            } catch (PersistenceException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

}
