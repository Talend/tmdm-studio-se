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
package com.amalto.workbench.compare;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareUI;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.StructuredSelection;

import com.amalto.workbench.utils.XmlUtil;

public class CompareManager {

    private static Log log = LogFactory.getLog(CompareManager.class);

    static CompareManager instance = new CompareManager();

    public static CompareManager getInstance() {
        return instance;
    }

    public IProject createProject(String label) {
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

        IProject prj = root.getProject(label);
        if (prj.exists())
            return prj;
        final IWorkspace workspace = ResourcesPlugin.getWorkspace();
        final IProjectDescription desc = workspace.newProjectDescription(label);
        desc.setNatureIds(new String[] { MdmNature.ID });
        desc.setComment("");//$NON-NLS-1$
        try {
            prj.create(desc, null);
            prj.open(IResource.BACKGROUND_REFRESH, null);
        } catch (CoreException e) {
            log.error(e.getMessage(), e);
        }
        return prj;
    }

    public void compareTwoStream(String left, String right, boolean format, CompareHeadInfo compareHeadInfo, String leftLabel,
            String rightLabel, boolean leftEditable, boolean rightEditable) throws Exception {
        if (left == null || right == null)
            return;

        if (format) {
            left = XmlUtil.formatCompletely(left, "UTF-8");//$NON-NLS-1$
            right = XmlUtil.formatCompletely(right, "UTF-8");//$NON-NLS-1$
        }

        // prepare the two resources
        IProject prj = createProject("comparewithsvn");//$NON-NLS-1$
        IFile leftF = prj.getFile("left");//$NON-NLS-1$
        IFile rightF = prj.getFile("right");//$NON-NLS-1$
        if (!leftF.exists())
            leftF.create(new ByteArrayInputStream(left.getBytes()), IFile.FORCE, null);
        if (!rightF.exists())
            rightF.create(new ByteArrayInputStream(right.getBytes()), IFile.FORCE, null);
        leftF.setContents(new ByteArrayInputStream(left.getBytes()), IFile.FORCE, null);
        rightF.setContents(new ByteArrayInputStream(right.getBytes()), IFile.FORCE, null);

        CompareConfiguration cc = new CompareConfiguration();
        cc.setLeftEditable(leftEditable);
        cc.setRightEditable(rightEditable);
        if (leftLabel == null)
            leftLabel = "";//$NON-NLS-1$
        cc.setLeftLabel(leftLabel);
        if (rightLabel == null)
            rightLabel = "";//$NON-NLS-1$
        cc.setRightLabel(rightLabel);
        ResourceCompareInput input = new ResourceCompareInput(cc);
        StructuredSelection sel = new StructuredSelection(new IFile[] { leftF, rightF });
        input.setSelection(sel, null);
        input.setCompareHeadInfo(compareHeadInfo);
        CompareUI.openCompareEditor(input);

    }

    public String getLeftContent() throws Exception {
        IProject prj = createProject("comparewithsvn");//$NON-NLS-1$
        IFile leftF = prj.getFile("left");//$NON-NLS-1$
        if (!leftF.exists())
            return null;
        byte[] buf = new byte[1024];
        StringBuffer sb = new StringBuffer();
        BufferedInputStream in = new BufferedInputStream(leftF.getContents());
        while (in.read(buf) > 0) {
            sb.append(new String(buf));
            in.read(buf);
        }
        return sb.toString().trim();
    }
}
