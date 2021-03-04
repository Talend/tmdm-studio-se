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
package org.talend.mdm.repository.ui.dialogs.xpath;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.xsd.XSDSchema;
import org.talend.mdm.repository.core.service.RepositoryQueryService;
import org.talend.mdm.repository.model.mdmserverobject.WSDataModelE;

import com.amalto.workbench.dialogs.XpathSelectDialog;
import com.amalto.workbench.dialogs.datamodel.IXPathSelectionFilter;
import com.amalto.workbench.models.TreeParent;
import com.amalto.workbench.utils.Util;

/**
 * DOC hbhong class global comment. this class is compatible with XpathSelectDialog, but works for Repository view
 */
public class XpathSelectDialog2 extends XpathSelectDialog {

    private static Logger log = Logger.getLogger(XpathSelectDialog2.class);

    /**
     * DOC hbhong XpathSelectDialog2 constructor comment.
     *
     * @param parentShell
     * @param parent
     * @param title
     * @param site
     * @param isMulti
     * @param dataModelName
     * @param isAbsolutePath
     */
    public XpathSelectDialog2(Shell parentShell, String title, IWorkbenchPartSite site, boolean isMulti, String dataModelName,
            boolean isAbsolutePath) {
        super(parentShell, null, title, site, isMulti, dataModelName, isAbsolutePath);
    }

    public XpathSelectDialog2(Shell parentShell, String title, IWorkbenchPartSite site, boolean isMulti, String dataModelName) {
        this(parentShell, title, site, isMulti, dataModelName, false);
    }

    public XpathSelectDialog2(Shell parentShell, TreeParent parent, String title, IWorkbenchPartSite site, boolean isMulti,
            String dataModelName, boolean isAbsolutePath, IXPathSelectionFilter filter) {
        super(parentShell, null, title, site, isMulti, dataModelName, isAbsolutePath, filter);
    }

    @Override
    protected List<String> getAvailableDataModel() {
        return RepositoryQueryService.getDataModel(dataModelName, conceptName);
    }

    @Override
    protected void changeDomTree(final TreeParent pObject, String filter) {
        String modelDisplay = dataModelCombo.getText();
        if (modelDisplay.length() == 0) {
            return;
        }
        this.dataModelName = modelDisplay;
        try {
            XSDSchema xsd = Util.getXSDSchemaOfDirtyEditor(dataModelName);
            if (null == xsd) {
                WSDataModelE wsDataModel = RepositoryQueryService.findDataModelByName(dataModelName);
                if (wsDataModel != null) {
                    String schema = wsDataModel.getXsdSchema();// Util.nodeToString(xsdSchema.getDocument());
                    xsd = Util.createXsdSchema(schema, pObject);
                }
            }
            if (null != xsd) {
                provideViwerContent(xsd, filter);
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }
}
