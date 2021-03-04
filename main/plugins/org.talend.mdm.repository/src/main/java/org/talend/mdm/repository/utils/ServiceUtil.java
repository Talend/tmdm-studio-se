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

import org.apache.log4j.Logger;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.IService;

/**
 * created by HHB on 2013-8-21 Detailled comment
 *
 */
public class ServiceUtil {

    static Logger log = Logger.getLogger(ServiceUtil.class);

    public static <T extends IService> T getService(Class<T> t) {
        try {
            if (GlobalServiceRegister.getDefault().isServiceRegistered(t)) {
                T service = GlobalServiceRegister.getDefault().getService(t);
                return service;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }
}
