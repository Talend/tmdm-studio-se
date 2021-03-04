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

public abstract class ComboCellEditorValueModifier<T> extends CellEditorValueModifier<T> {

    public ComboCellEditorValueModifier(String infoSetMethod, String infoGetMethod) {
        this(infoSetMethod, infoGetMethod, String.class);
    }

    public ComboCellEditorValueModifier(String infoSetMethod, String infoGetMethod, Class<?> setMethodParaType) {
        super(infoSetMethod, infoGetMethod, setMethodParaType);
    }

    @Override
    protected Object translateNewValueToModelAcceptable(Object newValue) {

        if (!(newValue instanceof Integer))
            return null;

        List<Object> allInfos = getAllInfos();

        if (allInfos.size() <= (Integer) newValue || (Integer) newValue < 0)
            return null;

        if (!allInfos.contains(allInfos.get((Integer) newValue)))
            return null;

        return allInfos.get((Integer) newValue);

    }

    protected abstract List<Object> getAllInfos();
}
