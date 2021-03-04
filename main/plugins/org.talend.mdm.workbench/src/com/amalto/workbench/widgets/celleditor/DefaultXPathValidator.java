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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * created by liusongbo on Nov 26, 2015
 *
 */
public class DefaultXPathValidator implements IXPathValidator {

    public boolean validate(String xpath) {
        if (xpath == null) {
            return false;
        }

        if (xpath.length() == 0) {
            return true;
        }
        Pattern pattern = Pattern.compile("\\.|([a-zA-Z]|\\d|\\/|@|:)+"); //$NON-NLS-1$
        Matcher matcher = pattern.matcher(xpath);
        boolean matches = matcher.matches();

        return matches;
    }

}
