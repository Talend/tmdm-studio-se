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

public class CellEditorValueModifier<T> {

    private String infoSetMethod;

    private String infoGetMethod;

    private Class<?> setMethodParaType;

    public CellEditorValueModifier(String infoSetMethod, String infoGetMethod) {
        this(infoSetMethod, infoGetMethod, String.class);
    }

    public CellEditorValueModifier(String infoSetMethod, String infoGetMethod, Class<?> setMethodParaType) {
        this.infoSetMethod = infoSetMethod;
        this.infoGetMethod = infoGetMethod;
        this.setMethodParaType = setMethodParaType;
    }

    public boolean modify(T info, Object newValue) {

        Object newValueForModel = translateNewValueToModelAcceptable(newValue);

        if (newValueForModel == null)
            return false;

        if (!hasChange(info, newValueForModel))
            return false;

        return doModify(info, newValueForModel);

    }

    protected boolean doModify(T info, Object newValue) {

        try {
            info.getClass().getMethod(infoSetMethod, setMethodParaType).invoke(info, newValue);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    protected boolean hasChange(T info, Object newValue) {

        try {
            return !info.getClass().getMethod(infoGetMethod).invoke(info).equals(newValue);
        } catch (Exception e) {
            return false;
        }
    }

    protected Object translateNewValueToModelAcceptable(Object newValue) {

        if (newValue == null)
            return "";//$NON-NLS-1$

        return newValue.toString();
    }
}
