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
package org.talend.mdm.repository.ui.dialogs;

import java.net.URL;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Shell;
import org.talend.core.model.properties.ItemState;
import org.talend.core.model.properties.Project;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.IServerObjectRepositoryType;
import org.talend.mdm.repository.core.service.RepositoryQueryService;
import org.talend.mdm.repository.core.service.RepositoryWebServiceAdapter;
import org.talend.mdm.repository.model.mdmmetadata.MDMServerDef;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;
import org.talend.mdm.workbench.serverexplorer.ui.dialogs.SelectServerDefDialog;
import org.talend.repository.ProjectManager;

import com.amalto.workbench.dialogs.SelectImportedModulesDialog;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.utils.XtentisException;
import com.amalto.workbench.webservices.TMDMService;

/**
 * DOC Administrator class global comment. Detailled comment
 */
public class SelectImportedModulesDialog2 extends SelectImportedModulesDialog {

    MDMServerDef serverDef;

    public SelectImportedModulesDialog2(Shell parentShell, TreeObject treeObj, String title) {
        super(parentShell, treeObj, title);

    }

    @Override
    protected TMDMService getPort() throws XtentisException {
        SelectServerDefDialog dialog = new SelectServerDefDialog(getShell());
        if (dialog.open() == IDialogConstants.OK_ID) {
            MDMServerDef serverDef = dialog.getSelectedServerDef();
            this.serverDef = serverDef;
            return RepositoryWebServiceAdapter.getMDMService(serverDef);
        }
        return null;
    }

    @Override
    protected boolean resolveSchemaList(List<String> schemaList) throws XtentisException {
        List<String> newList = RepositoryQueryService.findAllDataModelNames();
        for (String string : newList) {
            schemaList.add(string);
        }

        return true;
    }

    @Override
    public URL getSourceURL(String path) {
        if (null == path) {
            return null;
        }
        if (path.startsWith("type:")) { //$NON-NLS-1$
            String modelName = path.substring(5);
            IRepositoryViewObject rvobject = RepositoryResourceUtil.findViewObjectByName(
                    IServerObjectRepositoryType.TYPE_DATAMODEL, modelName);
            if (null == rvobject) {
                return null;
            }
            String prjLabel = rvobject.getProjectLabel();

            String parentPath = buildParentPath(rvobject);
            if (!parentPath.startsWith("/")) { //$NON-NLS-1$
                parentPath = "/" + parentPath; //$NON-NLS-1$
            }
            String sep = "" + IPath.SEPARATOR; //$NON-NLS-1$

            IPath plocation = getProjectLocation(rvobject);

            String fileName = "file:///" + plocation.toOSString() + sep //$NON-NLS-1$
                    + prjLabel.toUpperCase() + sep
                    + "MDM" + sep + "datamodel" + parentPath + rvobject.getLabel() + "_" + rvobject.getVersion() + ".xsd"; //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$ //$NON-NLS-4$
            return super.getSourceURL(fileName);
        } else {
            return super.getSourceURL(path);
        }
    }

    private String buildParentPath(IRepositoryViewObject viewObj) {
        String path = "" + IPath.SEPARATOR; //$NON-NLS-1$

        ItemState state = viewObj.getProperty().getItem().getState();
        String itemPath = state.getPath();
        if (!itemPath.isEmpty()) {
            path = itemPath + path;
        }

        return path;
    }

    private IPath getProjectLocation(IRepositoryViewObject viewObj) {
        Property property = viewObj.getProperty();
        Project project = ProjectManager.getInstance().getProject(property);
        IProject eclipseProject = ResourcesPlugin.getWorkspace().getRoot().getProject(project.getTechnicalLabel());
        IPath location = eclipseProject.getLocation().removeLastSegments(1);
        return location;
    }
}
