// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.repository.ui.navigator;

import java.text.Collator;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ViewerSorter;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.IServerObjectRepositoryType;
import org.talend.mdm.repository.model.mdmproperties.ContainerItem;
import org.talend.mdm.repository.model.mdmproperties.MDMServerObjectItem;
import org.talend.mdm.repository.ui.navigator.sorter.SortOrderFactory;

public class MDMRepositoryViewerSorter extends ViewerSorter {

    static Logger log = Logger.getLogger(MDMRepositoryViewerSorter.class);

    public MDMRepositoryViewerSorter() {
    }

    public MDMRepositoryViewerSorter(Collator collator) {
        super(collator);
    }

    @Override
    public int category(Object element) {
        if (element instanceof IRepositoryViewObject) {
            IRepositoryViewObject viewObject = (IRepositoryViewObject) element;
            ERepositoryObjectType repositoryObjectType = viewObject.getRepositoryObjectType();
            if (repositoryObjectType == null)
                return 0;
            if (repositoryObjectType == IServerObjectRepositoryType.TYPE_RECYCLE_BIN) {
                return 2;
            }
            try {
                Item item = viewObject.getProperty().getItem();
                if (item != null) {
                    if (item instanceof MDMServerObjectItem) {
                        return 1;
                    }
                    if (item instanceof ContainerItem) {
                        return SortOrderFactory.getOrder((ContainerItem)item);
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

        return 0;
    }
}
