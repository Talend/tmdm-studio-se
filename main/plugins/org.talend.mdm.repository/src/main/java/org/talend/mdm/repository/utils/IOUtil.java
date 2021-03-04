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

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipFile;

import org.eclipse.core.resources.ResourcesPlugin;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class IOUtil {

    /**
     *
     */
    private static final String TEMP_Folder_NAME = "temp"; //$NON-NLS-1$

    public static File getTempFolder() {
        String usrDir = System.getProperty("java.io.tmpdir");//$NON-NLS-1$

        File tmpFolder = new File(usrDir + File.separator + System.currentTimeMillis());
        if (!tmpFolder.exists()) {
            tmpFolder.mkdirs();
        }

        return tmpFolder;

    }

    public static File getWorkspaceTempFolder() {
        String usrDir = ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString();
        File tempFolder = new File(usrDir + File.separator + TEMP_Folder_NAME + File.separator + System.currentTimeMillis());
        if (!tempFolder.exists()) {
            tempFolder.mkdirs();
        }
        return tempFolder;
    }

    public static void cleanFolder(File folder) {
        if (folder != null && folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    cleanFolder(file);
                }
                file.delete();
            }
            folder.delete();
        }

    }

    public static boolean isExist(String path) {
        if (path == null) {
            return false;
        }

        return new File(path).exists();
    }


    /**
     * Determine whether the file with the given filename is in .zip or .jar format.
     *
     * @param fileName file to test
     * @return true if the file is in tar format
     */
    public static boolean isZipFile(String fileName) {
        if (fileName == null || fileName.trim().length() == 0) {
            return false;
        }

        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(fileName.trim());
        } catch (IOException ioException) {
            return false;
        } finally {
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }

        return true;
    }

}
