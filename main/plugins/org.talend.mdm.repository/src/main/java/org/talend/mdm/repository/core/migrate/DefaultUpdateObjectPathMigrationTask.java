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

import java.util.Date;
import java.util.GregorianCalendar;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.commons.utils.workbench.resources.ResourceUtils;
import org.talend.core.model.general.Project;
import org.talend.core.model.migration.AbstractProjectMigrationTask;
import org.talend.core.model.repository.ERepositoryObjectType;

/**
 * DOC HHB class global comment. Detailled comment
 */
public class DefaultUpdateObjectPathMigrationTask extends AbstractProjectMigrationTask {

    private MigrateObjectPathHandler migrateObjectPathHandler;

    public DefaultUpdateObjectPathMigrationTask(MigrateObjectPathHandler handler) {
        this.migrateObjectPathHandler = handler;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.talend.migration.IProjectMigrationTask#execute(org.talend.core.model.general.Project)
     */
    public ExecutionResult execute(Project project) {
        try {
            IProject fsProject = ResourceUtils.getProject(project.getTechnicalLabel());
            IFolder folder = ResourceUtils.getFolder(fsProject,
                    ERepositoryObjectType.getFolderName(migrateObjectPathHandler.getRule().getRepositoryObjectType()), false);
            if (folder != null && folder.getLocation().toFile().exists()) {
                migrateObjectPathHandler.migrate(folder);
            }
            return ExecutionResult.SUCCESS_NO_ALERT;
        } catch (PersistenceException e) {
            ExceptionHandler.process(e);
            return ExecutionResult.FAILURE;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.talend.migration.IMigrationTask#getOrder()
     */
    public Date getOrder() {
        GregorianCalendar gc = new GregorianCalendar(2012, 8, 16, 2, 0, 0);
        return gc.getTime();
    }

}
