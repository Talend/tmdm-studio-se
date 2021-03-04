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
package com.amalto.workbench.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;
import org.talend.commons.utils.workbench.resources.ResourceUtils;
import org.talend.core.model.general.Project;
import org.talend.repository.ProjectManager;

/**
 * created by liusongbo on 2013-1-6
 */
public class FileProvider {

    private static Log log = LogFactory.getLog(FileProvider.class);

    public static IFile createdTempFile(String templateString, String fileNameWithExtension, String charSet) {
        IFile file = null;
        if (templateString != null) {
            try {
                Project project = ProjectManager.getInstance().getCurrentProject();
                IProject prj = ResourceUtils.getProject(project);
                file = prj.getFile(new Path("temp/" + fileNameWithExtension)); //$NON-NLS-1$

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                OutputStreamWriter outputStreamWriter = null;
                if ((charSet == null) || (charSet.trim().isEmpty())) {
                    outputStreamWriter = new OutputStreamWriter(outputStream);
                } else {
                    outputStreamWriter = new OutputStreamWriter(outputStream, charSet);
                }

                outputStreamWriter.write(templateString);
                outputStreamWriter.flush();
                outputStreamWriter.close();

                ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
                if (file.exists()) {
                    file.setContents(inputStream, true, false, null);
                } else {
                    file.create(inputStream, true, null);
                }

                inputStream.close();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

        return file;
    }
}
