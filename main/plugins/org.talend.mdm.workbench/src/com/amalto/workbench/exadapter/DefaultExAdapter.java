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
 * created by HHB on 2014-1-3 Detailled comment
 *
 */
public class DefaultExAdapter<T> implements IExAdapter<T> {

    private T adaptable;

    /* (non-Javadoc)
     * @see com.amalto.workbench.extadapter.IExtAdapter#getAdaptable()
     */
    public T getAdaptable() {
        return this.adaptable;
    }

    /* (non-Javadoc)
     * @see com.amalto.workbench.extadapter.IExtAdapter#setAdaptable(T)
     */
    public void setAdaptable(T adaptable) {
        this.adaptable = adaptable;
    }
}
