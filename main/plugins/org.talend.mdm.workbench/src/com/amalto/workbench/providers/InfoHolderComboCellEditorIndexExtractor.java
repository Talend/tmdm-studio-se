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

public class InfoHolderComboCellEditorIndexExtractor<INFOTYPE, ALLINFOHOLDERTYPE> extends ComboCellEditorIndexExtractor<INFOTYPE> {

    private ALLINFOHOLDERTYPE infoHolder;

    private String allInfoAccessMethod;

    public InfoHolderComboCellEditorIndexExtractor(ALLINFOHOLDERTYPE infoHolder, String allInfoAccessMethod,
            String inforAccessMethod) {
        super(inforAccessMethod);

        this.infoHolder = infoHolder;
        this.allInfoAccessMethod = allInfoAccessMethod;
    }

    @Override
    protected List<Object> getAllInfos() {

        if (infoHolder == null)
            return new ArrayList<Object>();

        Object infos = null;
        try {
            infos = infoHolder.getClass().getMethod(allInfoAccessMethod).invoke(infoHolder);
        } catch (Exception e) {
            return new ArrayList<Object>();
        }

        if (infos instanceof Object[])
            return Arrays.asList((Object[]) infos);

        if (infos instanceof Collection<?>)
            return Arrays.asList(((Collection<?>) infos).toArray());

        return new ArrayList<Object>();
    }

}
