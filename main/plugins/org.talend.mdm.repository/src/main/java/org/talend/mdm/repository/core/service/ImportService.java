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
package org.talend.mdm.repository.core.service;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class ImportService {

    private static boolean importing = false;

    private static Lock lock = new ReentrantLock();

    private static Condition condition = lock.newCondition();

    private static List<String> importedIds = new LinkedList<String>();

    public static boolean isImporting() {
        lock.lock();

        try {
            return importing;
        } finally {
            lock.unlock();
        }

    }

    public static void blockUntilFinish() throws InterruptedException {
        lock.lock();

        try {
            if (importing) {
                condition.await();
            }
        } finally {
            lock.unlock();
        }
    }

    public static void setImporting(boolean importing) {
        lock.lock();
        try {
            if (importing) {
                importedIds.clear();
            }
            ImportService.importing = importing;
            if (!importing) {
                condition.signal();
            }
        } finally {
            lock.unlock();
        }
    }

    public static void importedId(String id) {
        importedIds.add(id);
    }

    public static List<String> getImportedIds() {
        return importedIds;
    }

}
