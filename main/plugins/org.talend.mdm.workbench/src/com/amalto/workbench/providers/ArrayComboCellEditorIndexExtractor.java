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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ArrayComboCellEditorIndexExtractor<T> extends ComboCellEditorIndexExtractor<T> {

    private List<Object> allInfos = new ArrayList<Object>();

    public ArrayComboCellEditorIndexExtractor(Object[] allInfos, String inforAccessMethod) {
        super(inforAccessMethod);

        this.allInfos = Arrays.asList(allInfos);
    }

    public ArrayComboCellEditorIndexExtractor(Collection<Object> allInfos, String inforAccessMethod) {
        this(allInfos.toArray(), inforAccessMethod);
    }

    @Override
    protected List<Object> getAllInfos() {
        return allInfos;
    }

}
