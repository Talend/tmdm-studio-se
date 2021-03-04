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
package org.talend.mdm.repository.core.migrate;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.commons.utils.workbench.resources.ResourceUtils;
import org.talend.core.model.general.Project;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.login.AbstractLoginTask;
import org.talend.mdm.repository.core.migrate.impl.ProcessMigrateObjectPathRule;
import org.talend.mdm.repository.core.migrate.impl.ViewMigrateObjectPathRule;
import org.talend.repository.ProjectManager;

/**
 * DOC HHB class global comment. Detailled comment
 */
public class AssertFolderLogonTask extends AbstractLoginTask implements IRunnableWithProgress {

    MigrateObjectPathHandler[] handlers;

    public AssertFolderLogonTask() {
        MigrateObjectPathHandler viewMOPH = new MigrateObjectPathHandler(new ViewMigrateObjectPathRule());
        MigrateObjectPathHandler processMOPH = new MigrateObjectPathHandler(new ProcessMigrateObjectPathRule());

        handlers = new MigrateObjectPathHandler[] { viewMOPH, processMOPH };
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
     */
    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        Project project = ProjectManager.getInstance().getCurrentProject();
        try {
            IProject prj = ResourceUtils.getProject(project);
            for (MigrateObjectPathHandler handler : handlers) {
                IFolder folder = ResourceUtils.getFolder(prj,
                        ERepositoryObjectType.getFolderName(handler.getRule().getRepositoryObjectType()), true);
                if (folder != null) {
                    handler.assertFolder(folder);
                }
            }
        } catch (PersistenceException e) {
            ExceptionHandler.process(e);
        }

    }

}
