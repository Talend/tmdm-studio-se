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


public class InfoHolderComboCellEditorValueModifier<INFOTYPE, ALLINFOHOLDERTYPE> extends ComboCellEditorValueModifier<INFOTYPE> {

    private String allInfoGetMethod;

    private ALLINFOHOLDERTYPE infofHolder;

    public InfoHolderComboCellEditorValueModifier(ALLINFOHOLDERTYPE infofHolder, String allInfoGetMethod, String infoSetMethod,
            String infoGetMethod) {
        this(infofHolder, allInfoGetMethod, infoSetMethod, infoGetMethod, String.class);
    }

    public InfoHolderComboCellEditorValueModifier(ALLINFOHOLDERTYPE infofHolder, String allInfoGetMethod, String infoSetMethod,
            String infoGetMethod, Class<?> infoSetMethodParaType) {
        super(infoSetMethod, infoGetMethod, infoSetMethodParaType);

        this.infofHolder = infofHolder;
        this.allInfoGetMethod = allInfoGetMethod;
    }

    @Override
    protected List<Object> getAllInfos() {

        try {
            Object result = infofHolder.getClass().getMethod(allInfoGetMethod).invoke(infofHolder);

            if (result instanceof Object[])
                return Arrays.asList((Object[]) result);

            if (result instanceof Collection<?>)
                return Arrays.asList(((Collection<?>) result).toArray());

        } catch (Exception e) {
            return new ArrayList<Object>();
        }

        return new ArrayList<Object>();
    }

}
