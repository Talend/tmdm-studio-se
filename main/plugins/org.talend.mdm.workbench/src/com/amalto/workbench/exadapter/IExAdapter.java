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
package com.amalto.workbench.exadapter;

/**
 * created by HHB on 2014-1-3
 * Detailled comment
 *
 * @param <T>
 */
public interface IExAdapter<T> {

    public abstract T getAdaptable();

    public abstract void setAdaptable(T adaptable);

}
