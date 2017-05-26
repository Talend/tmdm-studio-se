// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
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

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

import org.eclipse.ui.internal.wizards.datatransfer.TarEntry;
import org.eclipse.ui.internal.wizards.datatransfer.TarException;
import org.eclipse.ui.internal.wizards.datatransfer.TarFile;

/**
 * created by liusongbo on Jan 3, 2017
 *
 */
public class IOUtilExt {

    /**
     * Determine whether the file with the given filename is in .tar.gz or .tar format.
     * 
     * @param fileName file to test
     * @return true if the file is in tar format
     */
    public static boolean isTarFile(String fileName) {
        assertNotNull(fileName);

        if (fileName.trim().length() == 0) {
            return false;
        }

        TarFile tarFile = null;
        try {
            tarFile = new TarFile(fileName.trim());
        } catch (TarException tarException) {
            return false;
        } catch (IOException ioException) {
            return false;
        } finally {
            if (tarFile != null) {
                try {
                    tarFile.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }

        return true;
    }

    public static void extractTarFile(String tarFileName, String targetFolder) throws Exception {
        Exception exception = null;
        byte[] buf = new byte[8192];
        TarFile tarFile = null;
        try {
            tarFile = new TarFile(tarFileName);
            Enumeration<TarEntry> enumeration = tarFile.entries();
            while (enumeration.hasMoreElements()) {
                TarEntry entry = enumeration.nextElement();

                File file = new File(targetFolder, entry.getName());

                if (entry.getFileType() == TarEntry.DIRECTORY) {
                    if (!file.exists()) {
                        file.mkdir();
                    }
                } else {

                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                    }

                    InputStream zin = tarFile.getInputStream(entry);
                    OutputStream fout = new FileOutputStream(file);
                    // check if parent folder exists
                    File dir = file.getParentFile();
                    if (dir.isDirectory() && !dir.exists()) {
                        dir.mkdirs();
                    }

                    try {
                        while (true) {
                            int bytesRead = zin.read(buf);
                            if (bytesRead == -1) { // end of file
                                break;
                            }
                            fout.write(buf, 0, bytesRead);

                        }
                        fout.flush();
                    } catch (Exception e) {
                        exception = e;
                        // stop looping
                        return;
                    } finally {
                        if (zin != null) {
                            try {
                                zin.close();
                            } catch (Exception e) {
                                // ignore
                            }
                        }
                        if (fout != null) {
                            try {
                                fout.close();
                            } catch (Exception e) {
                                // ignore
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            exception = e;
        } finally {
            if (tarFile != null) {
                try {
                    tarFile.close();
                } catch (IOException e) {
                    // ignore
                }
            }

            if (exception != null) {
                // notify caller with exception
                throw exception;
            }
        }
    }
}
