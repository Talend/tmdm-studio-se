package org.talend.mdm.repository.ui.wizards.imports;

import static org.junit.Assert.*;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.zip.ZipFile;

import org.apache.log4j.Logger;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarOutputStream;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import org.eclipse.ui.internal.wizards.datatransfer.TarFile;
import org.junit.Test;
import org.talend.commons.utils.io.FilesUtils;
import org.talend.mdm.repository.utils.IOUtil;


public class MDMImportItemUtilTest {

    private static Logger log = Logger.getLogger(MDMImportItemUtilTest.class);

    @Test
    public void testBuildUnzippedTempFile() {
        assertNull(MDMImportItemUtil.buildUnzippedTempFile(null));

        File unzippedTempFile = null;

        // test file
        File tempFile = createTempFile();
        if (tempFile != null) {
            unzippedTempFile = MDMImportItemUtil.buildUnzippedTempFile(tempFile);
            assertNotNull(unzippedTempFile);
            assertEquals(tempFile.getName(), unzippedTempFile.getName());
            FilesUtils.deleteFile(unzippedTempFile.getParentFile(), true);// ////
        }

        // test folder with file
        tempFile = tempFile.getParentFile();
        unzippedTempFile = MDMImportItemUtil.buildUnzippedTempFile(tempFile);
        assertNotNull(unzippedTempFile);
        assertEquals(1, unzippedTempFile.list().length);
        assertEquals(tempFile.getName(), unzippedTempFile.getName());
        assertTrue(unzippedTempFile.isDirectory());
        assertTrue(unzippedTempFile.list().length > 0);
        FilesUtils.deleteFile(tempFile, true);// ////
        FilesUtils.deleteFile(unzippedTempFile.getParentFile(), true);// ////

        // test tar file
        File tempFolder = IOUtil.getTempFolder();
        String tarfile = tempFolder.getAbsolutePath() + File.separator + "test.tar"; //$NON-NLS-1$
        createTarFile(tarfile);
        File unzippedTempTarFile;
        try {
            unzippedTempTarFile = MDMImportItemUtil.buildUnzippedTempFile(new TarFile(tarfile));
            assertNotNull(unzippedTempTarFile);
            assertEquals("test.txt", unzippedTempTarFile.getName()); //$NON-NLS-1$
            FilesUtils.deleteFile(tempFolder, true);
            FilesUtils.deleteFile(unzippedTempTarFile.getParentFile(), true);
            // test zip file
            tempFile = createTempFile();
            tempFolder = IOUtil.getTempFolder();
            String destZipfile = tempFolder.getAbsolutePath() + File.separator + "test.zip"; //$NON-NLS-1$
            createZipFile(tempFile, destZipfile);

            unzippedTempTarFile = MDMImportItemUtil.buildUnzippedTempFile(new ZipFile(new File(destZipfile)));
            assertNotNull(unzippedTempFile);
            assertEquals("test.txt", unzippedTempTarFile.getName()); //$NON-NLS-1$
            FilesUtils.deleteFile(tempFile.getParentFile(), true);
            FilesUtils.deleteFile(tempFolder, true);
            FilesUtils.deleteFile(unzippedTempTarFile.getParentFile(), true);
        } catch (Exception e) {//
            log.error(e.getMessage(), e);
        }

    }

    private File createTempFile() {
        File tempFile = new File(IOUtil.getTempFolder(), "test.txt"); //$NON-NLS-1$
        BufferedWriter writer = null;
        try {
            boolean created = tempFile.createNewFile();
            if (created) {
                writer = new BufferedWriter(new FileWriter(tempFile));
                writer.write("This is test content."); //$NON-NLS-1$
                writer.flush();
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }

        return tempFile;
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

    private void createZipFile(File sourceFile, String targetZipFile) {
        try {
            FileOutputStream target = new FileOutputStream(targetZipFile);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(target));
            int BUFFER_SIZE = 1024;
            byte buff[] = new byte[BUFFER_SIZE];
            FileInputStream fi = new FileInputStream(sourceFile);
            BufferedInputStream origin = new BufferedInputStream(fi);
            ZipEntry entry = new ZipEntry(sourceFile.getName());
            out.putNextEntry(entry);
            int count;
            while ((count = origin.read(buff)) != -1) {
                out.write(buff, 0, count);
            }
            origin.close();
            out.close();
        } catch (IOException e) {//
            log.error(e.getMessage(), e);
        }
    }
}
