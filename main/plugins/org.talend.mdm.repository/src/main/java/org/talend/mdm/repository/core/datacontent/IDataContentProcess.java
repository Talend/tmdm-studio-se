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
package org.talend.mdm.repository.core.datacontent;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.MultiStatus;

/**
 * created by HHB on 2012-10-9 Detailled comment
 *
 */
public interface IDataContentProcess {

    int MAX_EXPORT_COUNT = 10000;

    public static final String RULE_FILE_NAME = ".list"; //$NON-NLS-1$

    public void run() throws InterruptedException;

    public DataProcessRule buildRule() throws InvocationTargetException;

    public void tuneRule(DataProcessRule rule) throws InterruptedException;

    public MultiStatus getResult();

    public void processDatas(DataProcessRule rule) throws InterruptedException, InvocationTargetException;
}
