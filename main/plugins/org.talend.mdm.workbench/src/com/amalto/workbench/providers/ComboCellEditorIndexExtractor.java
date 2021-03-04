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
package com.amalto.workbench.providers;
import java.util.List;


public abstract class ComboCellEditorIndexExtractor<T> extends CellEditorValueExtractor<T> {

    protected ComboCellEditorIndexExtractor(String inforAccessMethod) {
        super(inforAccessMethod);
    }

    public Object getValue(T info) {

        if (info == null)
            return -1;

        return getAllInfos().indexOf(getInfoContent(info));
    }

    protected abstract List<Object> getAllInfos();
}
