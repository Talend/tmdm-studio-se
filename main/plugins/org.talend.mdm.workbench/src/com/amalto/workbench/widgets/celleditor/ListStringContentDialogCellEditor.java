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

import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.amalto.workbench.dialogs.SimpleListStringContentDialog;

public class ListStringContentDialogCellEditor extends EditableDialogCellEditor {

    public static final String DEFAULT_INFODELIMITER = "<;>";//$NON-NLS-1$

    private String dialogTitle = "";//$NON-NLS-1$

    private String infoLabel = "";//$NON-NLS-1$

    private String infoDelimiter = DEFAULT_INFODELIMITER;

    public ListStringContentDialogCellEditor(Composite parent, String dialogTitle, String infoLabel) {
        this(parent, dialogTitle, infoLabel, DEFAULT_INFODELIMITER);
    }

    public ListStringContentDialogCellEditor(Composite parent, String dialogTitle, String infoLabel, String infoDelimiter) {
        super(parent);

        if (dialogTitle != null)
            this.dialogTitle = dialogTitle;

        if (infoLabel != null)
            this.infoLabel = infoLabel;

        if (infoDelimiter != null)
            this.infoDelimiter = infoDelimiter;
    }

    @Override
    protected Object openDialogBox(Control cellEditorWindow) {

        SimpleListStringContentDialog dialog = new SimpleListStringContentDialog(cellEditorWindow.getShell(), dialogTitle,
                infoLabel, getCurListInfos());

        if (dialog.open() != Window.OK)
            return null;

        return translateInfosToStr(dialog.getInfos());
    }

    private String translateInfosToStr(String[] infos) {

        String result = "";//$NON-NLS-1$

        if (infos.length == 0)
            return "";//$NON-NLS-1$

        for (String eachInfo : infos) {

            if ("".equals(eachInfo.trim()))//$NON-NLS-1$
                continue;

            result += (eachInfo + infoDelimiter);
        }

        if ("".equals(result.trim()))//$NON-NLS-1$
            return "";//$NON-NLS-1$

        return result.substring(0, result.length() - infoDelimiter.length());
    }

    private String[] getCurListInfos() {

        if (!(getValue() instanceof String))
            return new String[0];

        String curValue = (String) getValue();

        if ("".equals(curValue.trim()))//$NON-NLS-1$
            return new String[0];

        String[] infos = curValue.split(infoDelimiter);

        if (infos.length == 0)
            return new String[] { curValue };

        List<String> results = new ArrayList<String>();
        for (String eachInfo : infos) {

            if ("".equals(eachInfo.trim()))//$NON-NLS-1$
                continue;

            results.add(eachInfo);
        }

        return results.toArray(new String[0]);
    }
}
