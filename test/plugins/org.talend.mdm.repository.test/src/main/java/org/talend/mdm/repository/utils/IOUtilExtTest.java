package org.talend.mdm.repository.utils;

import static org.junit.Assert.*;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import org.apache.log4j.Logger;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarOutputStream;
import org.junit.Test;


public class IOUtilExtTest {

    private static Logger log = Logger.getLogger(IOUtilExtTest.class);

    @Test
    public void testIsTarFile() {
        File tempFolder = IOUtil.getTempFolder();
        String tarfile = tempFolder.getAbsolutePath() + File.separator + "test.tar"; //$NON-NLS-1$

        assertFalse(IOUtilExt.isTarFile("")); //$NON-NLS-1$
        assertFalse(IOUtilExt.isTarFile(" ")); //$NON-NLS-1$
        assertFalse(IOUtilExt.isTarFile(tarfile));

        createTarFile(tarfile);
        assertTrue(IOUtilExt.isTarFile(tarfile));

        String gzfile = tempFolder.getAbsolutePath() + File.separator + "test.tar.gz"; //$NON-NLS-1$
        createTarGZFile(tarfile, gzfile);
        assertTrue(IOUtilExt.isTarFile(gzfile));

        new File(tarfile).deleteOnExit();
        new File(gzfile).deleteOnExit();
    }

    @Test
    public void testExtractTarFile() {
        File tempFolder = IOUtil.getTempFolder();
        String tarfile = tempFolder.getAbsolutePath() + File.separator + "test.tar"; //$NON-NLS-1$
        String gzfile = tempFolder.getAbsolutePath() + File.separator + "test.tar.gz"; //$NON-NLS-1$
        String destTarPath = tempFolder.getAbsolutePath() + File.separator + "extractedTar"; //$NON-NLS-1$
        String destGZPath = tempFolder.getAbsolutePath() + File.separator + "extractedGZ"; //$NON-NLS-1$

        try {
            File destTarDir = new File(destTarPath);
            createTarFile(tarfile);
            IOUtilExt.extractTarFile(tarfile, destTarPath);
            assertTrue(destTarDir.exists());
            String[] files = destTarDir.list();
            assertTrue(files.length == 1);
            assertEquals("test.txt", files[0]); //$NON-NLS-1$

            destTarDir.listFiles()[0].deleteOnExit();
            destTarDir.deleteOnExit();

            //
            File destGZDir = new File(destGZPath);
            createTarGZFile(tarfile, gzfile);
            IOUtilExt.extractTarFile(tarfile, destGZPath);
            assertTrue(destGZDir.exists());
            files = destGZDir.list();
            assertTrue(files.length == 1);
            assertEquals("test.txt", files[0]); //$NON-NLS-1$

            new File(tarfile).deleteOnExit();
            new File(gzfile).deleteOnExit();
            destGZDir.listFiles()[0].deleteOnExit();
            destGZDir.deleteOnExit();
        } catch (Exception e) {//
            log.error(e.getMessage(), e);
        }
    }

    private void createTarFile(String file) {
        TarOutputStream tos = null;
        try {
            File tarfile = new File(file);
            if (!tarfile.exists()) {
                tarfile.createNewFile();
            }
            tos = new TarOutputStream(new FileOutputStream(tarfile));
            TarEntry te = new TarEntry("test.txt"); //$NON-NLS-1$
            byte[] data = "some-content".getBytes("UTF-8"); //$NON-NLS-1$ //$NON-NLS-2$
            te.setSize(data.length);
            tos.putNextEntry(te);
            tos.write(data);
            tos.closeEntry();
            tos.flush();
            tos.finish();
        } catch (Exception e) {//
            log.error(e.getMessage(), e);
        } finally {
            if (tos != null) {
                try {
                    tos.close();
                } catch (IOException e) {//
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    private void createTarGZFile(String tarfile, String destfile) {
        File gzfile = new File(destfile);
        GZIPOutputStream gos = null;
        BufferedInputStream in = null;
        try {
            if (!gzfile.exists()) {
                gzfile.createNewFile();
            }
            in = new BufferedInputStream(new FileInputStream(tarfile));
            gos = new GZIPOutputStream(new FileOutputStream(gzfile));
            int count;
            byte[] data = new byte[1024];
            while ((count = in.read(data, 0, 1024)) != -1) {
                gos.write(data, 0, count);
            }

            gos.flush();
            gos.finish();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (gos != null) {
                try {
                    gos.close();
                } catch (IOException e) {//
                    log.error(e.getMessage(), e);
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {//
                    log.error(e.getMessage(), e);
                }
            }
        }
    }
}
