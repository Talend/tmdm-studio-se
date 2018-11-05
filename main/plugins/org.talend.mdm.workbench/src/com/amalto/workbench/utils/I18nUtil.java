// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.Platform;

@SuppressWarnings("nls")
public class I18nUtil {

    private static final String DEFAULT_NL = "en";

    private static final String OSGI_NL = "osgi.nl";

    private static final Log LOG = LogFactory.getLog(I18nUtil.class);

    private static String curNL = null;

    public static final String getCurrentNL() {
        if (curNL == null) {
            curNL = readCurrentNL();
            if (curNL == null || curNL.isEmpty()) {
                curNL = DEFAULT_NL;
            }
        }
        return curNL;
    }

    private static String readCurrentNL() {
        FileInputStream fin = null;

        try {
            URL url = Platform.getConfigurationLocation().getURL();

            Properties p = new Properties();
            // load the file configuration/config.ini
            File iniFile = new File(url.getFile(), "config.ini");
            fin = new FileInputStream(iniFile);
            p.load(fin);
            return p.getProperty(OSGI_NL);
        } catch (IOException ex) {
            LOG.error("Fail in reading config.ini file", ex);
            return null;
        } finally {
            IOUtils.closeQuietly(fin);
        }
    }
}
