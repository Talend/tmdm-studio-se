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
package com.amalto.workbench.detailtabs.sections.providers;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Item;

import com.amalto.workbench.detailtabs.sections.BasePropertySection;
import com.amalto.workbench.detailtabs.sections.model.annotationinfo.langinfo.LanguageInfo;
import com.amalto.workbench.i18n.Messages;


public class LanguageInfoModifier implements ICellModifier {

    public static final String COL_PROP_LANG = Messages.LanguageInfoModifier_language;

    public static final String COL_PROP_LABLE = Messages.LanguageInfoModifier_label;

    private List<LanguageInfo> allLanguageInfos = new ArrayList<LanguageInfo>();

    private List<String> allLanguages = new ArrayList<String>();

    private TreeViewer viewer;

    protected BasePropertySection section;
    public LanguageInfoModifier(TreeViewer viewer, List<LanguageInfo> allLanguageInfos, List<String> allLanguages,BasePropertySection section) {
    	this(viewer,allLanguageInfos,allLanguages);
    	this.section=section;
    }
    public LanguageInfoModifier(TreeViewer viewer, List<LanguageInfo> allLanguageInfos, List<String> allLanguages) {
        this.allLanguageInfos = allLanguageInfos;
        this.allLanguages = allLanguages;
        this.viewer = viewer;
    }

    public boolean canModify(Object element, String property) {

        return isLanguageInfo(element) && (isColumnLanguage(property) || isColumnLabel(property));
    }

    public Object getValue(Object element, String property) {

        if (!isLanguageInfo(element))
            return null;

        if (isColumnLanguage(property)) {
            return getValueForColumnLanguage((LanguageInfo) element);
        }

        if (isColumnLabel(property)) {
            return getValueForColumnLabel((LanguageInfo) element);
        }

        return null;
    }

    public void modify(Object element, String property, Object value) {

        LanguageInfo modifiedLangInfo = getLanguageInfoFromSelection(element);

        if (!isLanguageInfo(modifiedLangInfo))
            return;

        if (isColumnLanguage(property) && isComboCellEditorValue(value)) {
            onModfiyColumnLanguage(modifiedLangInfo, (Integer) value);
        }

        if (isColumnLabel(property) && isTextCellEditorValue(value)) {
            onModfiyColumnLable(modifiedLangInfo, (String) value);
        }

        viewer.refresh();
        if(section!=null) section.autoCommit();
    }

    private void onModfiyColumnLanguage(LanguageInfo modifiedElement, Integer newSelectedIndex) {

        if (isNewLanguageExistedAlready(modifiedElement, allLanguages.get(newSelectedIndex))) {
            MessageDialog.openInformation(null, Messages.LanguageInfoModifier_Warnning, Messages.bind(Messages.LanguageInfoModifier_InfoContent, allLanguages.get(newSelectedIndex)));
            return;
        }

        modifiedElement.setLanguage(allLanguages.get(newSelectedIndex));
    }

    private void onModfiyColumnLable(LanguageInfo modifiedElement, String newLabel) {
        modifiedElement.setLabel(newLabel);
    }

    private boolean isNewLanguageExistedAlready(LanguageInfo modifiedElement, String newLanguage) {

        for (LanguageInfo eachCurLanguageInfo : allLanguageInfos) {

            if (eachCurLanguageInfo.equals(modifiedElement))
                continue;

            if (eachCurLanguageInfo.getLanguage().equals(newLanguage))
                return true;
        }

        return false;
    }

    private Integer getValueForColumnLanguage(LanguageInfo element) {
        return allLanguages.indexOf(element.getLanguage());
    }

    private String getValueForColumnLabel(LanguageInfo element) {
        return element.getLabel();
    }

    private boolean isLanguageInfo(Object element) {
        return (element instanceof LanguageInfo);
    }

    private boolean isColumnLanguage(String property) {
        return COL_PROP_LANG.equals(property);
    }

    private boolean isColumnLabel(String property) {
        return COL_PROP_LABLE.equals(property);
    }

    private boolean isComboCellEditorValue(Object value) {
        return (value instanceof Integer);
    }

    private boolean isTextCellEditorValue(Object value) {
        return (value instanceof String);
    }

    private LanguageInfo getLanguageInfoFromSelection(Object selection) {

        if (!(selection instanceof Item))
            return null;

        if (((Item) selection).getData() instanceof LanguageInfo)
            return (LanguageInfo) ((Item) selection).getData();

        return null;
    }
}
