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
package org.talend.mdm.repository.ui.navigator.sorter;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.talend.core.model.properties.FolderItem;
import org.talend.core.model.properties.Item;
import org.talend.mdm.repository.core.impl.view.IViewNodeConstDef;


public class ViewCategoryOrder implements ISortOrder {

    private static List<String> folders = Arrays.asList(IPath.SEPARATOR + IViewNodeConstDef.PATH_WEBFILTER, IPath.SEPARATOR + IViewNodeConstDef.PATH_SEARCHFILTER);

    public int getOrder(Item item) {
        if(item instanceof FolderItem) {
            String path = item.getState().getPath();
            if(!path.isEmpty() && path.lastIndexOf("/") == 0) {
                return folders.indexOf(path);
            }
        }

        return -1;
    }

}
