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
package com.amalto.workbench.utils;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Font;


public class FontUtils {

    static FontRegistry fontRegistry = JFaceResources.getFontRegistry();

    public static Font getBoldFont(Font font) {

        return fontRegistry.getBold(font.getFontData()[0].getName());
    }

    public static Font getItalicFont(Font font) {

        return fontRegistry.getItalic(font.getFontData()[0].getName());
    }
}
