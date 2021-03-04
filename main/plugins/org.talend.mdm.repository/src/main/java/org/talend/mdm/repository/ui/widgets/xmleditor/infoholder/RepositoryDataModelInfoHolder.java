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
package org.talend.mdm.repository.ui.widgets.xmleditor.infoholder;

import org.apache.log4j.Logger;
import org.eclipse.xsd.XSDSchema;
import org.talend.mdm.repository.core.service.RepositoryQueryService;
import org.talend.mdm.repository.model.mdmserverobject.WSDataModelE;

import com.amalto.workbench.models.infoextractor.IAllDataModelHolder;
import com.amalto.workbench.utils.Util;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class RepositoryDataModelInfoHolder extends RepositoryExternalInfoHolder<IAllDataModelHolder> {

    static Logger log = Logger.getLogger(RepositoryDataModelInfoHolder.class);

    @Override
    public IAllDataModelHolder getExternalInfo() {
        return holder;
    }

    @Override
    public String getId() {
        return INFOID_ALLDATAMODELHOLDER;
    }

    IAllDataModelHolder holder = new IAllDataModelHolder() {

        public String[] getAllDataModelNames() {
            return RepositoryQueryService.findAllDataModelNames().toArray(new String[0]);
        }

        public boolean hasDataModel() {
            return getAllDataModelNames().length > 0;
        }

        public XSDSchema getDataModel(String dataModelName) {
            WSDataModelE dataModel = RepositoryQueryService.findDataModelByName(dataModelName);
            try {
                if (dataModel != null) {
                    XSDSchema xsdSchema = Util.getXSDSchema(dataModel.getXsdSchema());
                    return xsdSchema;
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            return null;
        }

        public String getDefaultDataModel() {
            return null;
        }

        public String getDefaultEntity() {
            return null;
        }
    };
}
