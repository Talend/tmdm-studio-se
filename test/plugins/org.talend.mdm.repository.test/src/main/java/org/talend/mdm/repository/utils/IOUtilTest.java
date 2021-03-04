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

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;
import org.junit.Test;

public class IOUtilTest {

    private static Logger log = Logger.getLogger(IOUtilTest.class);

    /**
     * Test method for {@link org.talend.mdm.repository.utils.IOUtil#getTempFolder()}
     *
     */
    @Test
    public void testGetTempFolder() {
        File theFile = IOUtil.getTempFolder();
        assertNotNull(theFile);
        assertTrue(theFile.exists());
    }

    /**
     * Test method for {@link org.talend.mdm.repository.utils.IOUtil#cleanFolder(java.io.File folder)}
     *
     */
    @Test
    public void testCleanFolder() {
        File file = IOUtil.getTempFolder();
        String tempFolderPath = file.getAbsolutePath();
        File newfile = new File(tempFolderPath + File.separator + "testtemp"); //$NON-NLS-1$
        if (!newfile.exists()) {
            newfile.mkdirs();
        }
        IOUtil.cleanFolder(newfile);
        assertFalse(newfile.exists());
    }

    @Test
    public void testIsZipFile() {
        File dir = IOUtil.getTempFolder();
        String file = new File(dir, "test.zip").getAbsolutePath(); //$NON-NLS-1$
        assertFalse(IOUtil.isZipFile("")); //$NON-NLS-1$
        assertFalse(IOUtil.isZipFile("   ")); //$NON-NLS-1$

        createZipFile(file);
        assertTrue(IOUtil.isZipFile(file));
        new File(file).deleteOnExit();

        file = new File(dir, "test.jar").getAbsolutePath(); //$NON-NLS-1$
        createZipFile(file);
        assertTrue(IOUtil.isZipFile(file));
        new File(file).deleteOnExit();
    }

    private void createZipFile(String zipfile) {
        StringBuilder sb = new StringBuilder();
        sb.append("Test String"); //$NON-NLS-1$

        ZipOutputStream out = null;
        try {
            File f = new File(zipfile);
            out = new ZipOutputStream(new FileOutputStream(f));
            ZipEntry e = new ZipEntry("mytext.txt"); //$NON-NLS-1$
            out.putNextEntry(e);

            byte[] data = sb.toString().getBytes();
            out.write(data, 0, data.length);
        } catch (Exception e) {//
            log.error(e.getMessage(), e);
        } finally {
            if (out != null) {
                try {
                    out.closeEntry();
                    out.close();
                } catch (IOException e) {//
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

}
