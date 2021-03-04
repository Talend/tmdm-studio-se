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
package com.amalto.workbench.widgets.celleditor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.amalto.workbench.detailtabs.sections.model.annotationinfo.langinfo.LanguageInfo;
import com.amalto.workbench.dialogs.AnnotationLangInfoDialog;
import com.amalto.workbench.utils.Util;

public class LangInfoCellEditor extends EditableDialogCellEditor {

    private static final String REGEXP_METACHARACTERS = "\\[([\\w]+)[\\s]*:[\\s]*([^]]*)\\]";//$NON-NLS-1$

    public LangInfoCellEditor(Composite parent) {
        super(parent);
    }

    @Override
    protected Object openDialogBox(Control cellEditorWindow) {
        AnnotationLangInfoDialog dialog = new AnnotationLangInfoDialog(cellEditorWindow.getShell(), parseInput());

        if (dialog.open() != Window.OK)
            return null;

        return translateLangInfo(dialog.getLangInfos());
    }

    private String translateLangInfo(LanguageInfo[] langInfos) {

        String result = "";//$NON-NLS-1$

        for (LanguageInfo eachLangInfo : langInfos)
            result += "[" + eachLangInfo.getLanguageISOCode().toUpperCase() + ":" + eachLangInfo.getLabel() + "]";//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$

        return result;
    }

    private LanguageInfo[] parseInput() {

        if (getValue() == null || !(getValue() instanceof String))
            return new LanguageInfo[0];

        List<LanguageInfo> results = new ArrayList<LanguageInfo>();
        String expression = (String) getValue();

        boolean find = false;
        Matcher match = Pattern.compile(REGEXP_METACHARACTERS).matcher(expression);
        while (match.find()) {
            find = true;
            String countryISOCode = match.group(1).trim().toLowerCase();
            String desc = match.group(2).trim();
            String country = Util.iso2lang.get(countryISOCode);
            if (country != null)
                results.add(new LanguageInfo(country, countryISOCode, desc));
        }

        if (!find && !expression.equals(""))//$NON-NLS-1$
            results.add(new LanguageInfo("English", "en", expression));//$NON-NLS-1$//$NON-NLS-2$

        return results.toArray(new LanguageInfo[0]);
    }
}
