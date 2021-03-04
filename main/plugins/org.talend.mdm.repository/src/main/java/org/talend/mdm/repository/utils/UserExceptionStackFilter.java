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
package org.talend.mdm.repository.utils;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * created by liusongbo on 2012-10-17
 *
 */
public class UserExceptionStackFilter {

    /**
     *
     */
    private static final String ENTER = "\n"; //$NON-NLS-1$

    private static final Log log = LogFactory.getLog(UserExceptionStackFilter.class);

    private static final String SUFFIX = ";"; //$NON-NLS-1$

    private static final String CAUSEBY_SEP = "\\[Caused by\\]:"; //$NON-NLS-1$

    private static final String NEST_SEP = "nested exception"; //$NON-NLS-1$

    private static final String BLANK = ""; //$NON-NLS-1$

    static final String REGEX = "([[\\w]+\\.]+[\\w]+Exception:)"; //$NON-NLS-1$

    static Pattern pattern = Pattern.compile(REGEX);

    public static String[] filterExceptionMsg(String msg) {
        if (msg == null || msg.trim().isEmpty()) {
            return new String[0];
        }

        String[] splits = msg.split(CAUSEBY_SEP);
        Set<String> results = new LinkedHashSet<String>();
        for (String split : splits) {
            String result = filterException(split);
            if (result != null) {
                results.add(result);
            }
        }
        if (results.isEmpty()) {
            return new String[] { msg };
        }
        return results.toArray(new String[0]);
    }

    private static String filterException(String msg) {
        if (msg.contains(NEST_SEP)) {
            return null;
        }
        String result = msg;
        Matcher matcher = pattern.matcher(result);
        while (matcher.find()) {
            result = matcher.replaceAll(BLANK);
        }
        result = result.replaceAll(SUFFIX, BLANK);
        result = result.replaceAll(ENTER, BLANK);
        result = result.replaceAll("  ", " "); //$NON-NLS-1$ //$NON-NLS-2$
        return result.trim();
    }

}
