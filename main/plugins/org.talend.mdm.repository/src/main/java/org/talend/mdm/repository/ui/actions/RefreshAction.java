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

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.mdm.repository.core.AbstractRepositoryAction;
import org.talend.mdm.repository.core.IRepositoryViewGlobalActionHandler;
import org.talend.mdm.repository.core.service.ImportService;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.model.mdmproperties.ContainerItem;

import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class RefreshAction extends AbstractRepositoryAction {

    private static Logger log = Logger.getLogger(RefreshAction.class);

    private boolean refreshAll;
    /**
     * DOC hbhong RefreshAction constructor comment.
     *
     * @param text
     */
    public RefreshAction(boolean refreshAll) {
        super(Messages.RefreshAction_Refresh);
        if (refreshAll) {
            String title = Messages.RefreshAction_Refresh_All;
            setText(title);
            setToolTipText(Messages.RefreshAction_Refresh_All_tooltip);
        }
        setImageDescriptor(ImageCache.getImage(EImage.REFRESH.getPath()));
        this.setId(IRepositoryViewGlobalActionHandler.REFRESH);
        this.setActionDefinitionId(IRepositoryViewGlobalActionHandler.REFRESH);
        this.refreshAll = refreshAll;
    }

    @Override
    public void run() {
        updateProject();

        super.run();
    }

    private void updateProject() {
        ImportService.setImporting(true);// to avoid workflow resource listener udpate workflow

        final ProxyRepositoryFactory factory = ProxyRepositoryFactory.getInstance();

        try {
            if (!factory.isLocalConnectionProvider()) {

                factory.initialize();

                ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
            }
        } catch (CoreException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        ImportService.setImporting(false);
    }

    @Override
    protected void doRun() {
        Display.getDefault().asyncExec(new Runnable() {

            @Override
            public void run() {
                if (refreshAll) {
                    refreshAll();
                } else {
                    refreshSelection();
                }
            }
        });

    }

    private void refreshAll() {
        Object input = commonViewer.getInput();
        for (IRepositoryViewObject viewObj : (IRepositoryViewObject[]) input) {
            commonViewer.refresh(viewObj);
        }
    }

    private void refreshSelection() {
        List<Object> selectedObject = getSelectedObject();
        if (!selectedObject.isEmpty()) {
            Object obj = selectedObject.get(0);


            Property property = ((IRepositoryViewObject) obj).getProperty();
            // When work in remote mode, select a deleted object the property would be null.
            if (property != null) {
                commonViewer.refresh(obj);
                Item item = property.getItem();
                if (item instanceof ContainerItem) {
                    commonViewer.expandToLevel(obj, 1);
                }
            } else {
                commonViewer.refresh();
            }
        }
    }

    @Override
    public String getGroupName() {
        return GROUP_COMMON;
    }

}
