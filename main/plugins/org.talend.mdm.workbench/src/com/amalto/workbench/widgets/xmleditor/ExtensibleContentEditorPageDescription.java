// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.workbench.widgets.xmleditor;

public class ExtensibleContentEditorPageDescription {

    private String label;

    private int index;

    private ExtensibleContentEditorPageCreator creator;

    private boolean isDefault;

    public ExtensibleContentEditorPageDescription(String label, int index, ExtensibleContentEditorPageCreator creator, boolean isDefault) {

        this.label = label;
        this.index = index;
        this.creator = creator;
        this.isDefault = isDefault;

    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public ExtensibleContentEditorPageCreator getCreator() {
        return creator;
    }

    public void setCreator(ExtensibleContentEditorPageCreator creator) {
        this.creator = creator;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

}
