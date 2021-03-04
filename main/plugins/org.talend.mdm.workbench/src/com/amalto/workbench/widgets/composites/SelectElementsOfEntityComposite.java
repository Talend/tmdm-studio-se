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
package com.amalto.workbench.widgets.composites;

import org.eclipse.swt.widgets.Composite;

import com.amalto.workbench.detailtabs.sections.BasePropertySection;
import com.amalto.workbench.models.infoextractor.IElementsOfEntityHolder;

public class SelectElementsOfEntityComposite extends ComboListStringContentComposite {

    private IElementsOfEntityHolder elementsHolder;

    private String colTitle = "Fields";//$NON-NLS-1$s

    public SelectElementsOfEntityComposite(Composite parent, int style, String colTitle, IElementsOfEntityHolder elementsHolder,BasePropertySection section) {
        super(parent, style, new Object[] { colTitle, elementsHolder },section);
    }

    public IElementsOfEntityHolder getElementsHolder() {
        return elementsHolder;
    }

    public void setElementsHolder(IElementsOfEntityHolder elementsHolder) {
        this.elementsHolder = elementsHolder;
    }

    @Override
    protected String[] getAllInfosInCombo() {

        if (elementsHolder == null)
            return new String[0];

        return elementsHolder.getAllElements();
    }

    @Override
    protected String getInfoColTitle() {
        return colTitle;
    }

    @Override
    protected void createExtentUIArea(Composite parent) {
    }

    @Override
    protected void initParas(Object[] paras) {

        for (Object eachPara : paras) {

            if (eachPara instanceof IElementsOfEntityHolder)
                elementsHolder = (IElementsOfEntityHolder) eachPara;

            if (eachPara instanceof String)
                colTitle = (String) eachPara;
        }

    }

}
